package vnp.thread;

import java.util.Vector;

import smartlib.util.AppException;

import com.logica.smpp.Data;
import com.logica.smpp.ServerPDUEvent;
import com.logica.smpp.ServerPDUEventListener;
import com.logica.smpp.Session;
import com.logica.smpp.SmppObject;
import com.logica.smpp.TCPIPConnection;
import com.logica.smpp.pdu.AddressRange;
import com.logica.smpp.pdu.BindReceiver;
import com.logica.smpp.pdu.BindRequest;
import com.logica.smpp.pdu.BindResponse;
import com.logica.smpp.pdu.BindTransciever;
import com.logica.smpp.pdu.BindTransmitter;
import com.logica.smpp.pdu.EnquireLink;
import com.logica.smpp.pdu.EnquireLinkResp;
import com.logica.smpp.pdu.PDU;
import com.logica.smpp.pdu.WrongLengthOfStringException;
import com.logica.smpp.util.Queue;
import vnp.util.ParameterType;

/**
 * <p>
 * Title:
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author DinhLV
 * @version 1.0
 */

public abstract class SMSThread extends PortalThread {
	protected boolean mblDebugOption = false;
	protected boolean mblBound = false;
	public Session mSession = null;
	protected String mStrBindMode = "t";
	protected String mStrIpAddress = null;
	protected int miPort = 0;
	protected String mStrSystemId = null;
	protected String mStrPassword = null;
	protected String mstrSMSCID = "";
	protected String mstrServiceCode = "";
	protected AddressRange mAddressRange = new AddressRange();
	protected boolean mblAsynchronous = false;
	protected String mStrSyncMode;
	protected long nextEnquireLink = 0;
	protected int enquireInterval = 60;
	public SMPPTestPDUEventListener mPduListener = null;

	////////////////////////////////////////////////////////
	public void fillParameter() throws AppException {
		// String strDebugOption = loadMandatory("debug-mode");
		// if (strDebugOption.toUpperCase().equals("Y")) {
		// mblDebugOption = true;
		// }
		mStrIpAddress = loadMandatory("ip-address");
		miPort = loadUnsignedInteger("port");
		mstrSMSCID = loadMandatory("SMSCID");
		mstrServiceCode = loadMandatory("servicecode");
		mStrSystemId = loadMandatory("username");
		mStrPassword = loadMandatory("password");
		mStrBindMode = loadString("bind-mode");
		byte ton = (byte) loadUnsignedIntegerSubParam("address-range.addr-ton", 0);
		byte npi = (byte) loadUnsignedIntegerSubParam("address-range.addr-npi", 1);
		String addr = loadMandatorySubParam("address-range.address-range", 2);
		mAddressRange.setTon(ton);
		mAddressRange.setNpi(npi);
		try {
			mAddressRange.setAddressRange(addr);
		} catch (WrongLengthOfStringException e) {
			throw new AppException("The length of address-range parameter is wrong.");
		}
		mStrSyncMode = loadMandatory("sync-mode");
		if (mStrSyncMode.equalsIgnoreCase("s")) {
			mblAsynchronous = false;
		} else {
			mblAsynchronous = true;
		}
		enquireInterval = loadUnsignedInteger("enquire-interval");
		super.fillParameter();
	}

	////////////////////////////////////////////////////////
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		Vector vtValue = new Vector();
		vtValue.addElement("Y");
		vtValue.addElement("N");
		vtReturn.addElement(createParameterDefinition("debug-mode", "", ParameterType.PARAM_COMBOBOX, vtValue, ""));

		vtReturn.addElement(createParameterDefinition("ip-address", "", ParameterType.PARAM_TEXTBOX_MAX, "15", ""));
		vtReturn.addElement(createParameterDefinition("port", "", ParameterType.PARAM_TEXTBOX_MASK, "99999", ""));
		vtReturn.addElement(createParameterDefinition("address-range", "", ParameterType.PARAM_SUB_TABULAR_EDITOR,
				getAddressRangeParameter(), "address-range"));
		vtValue = new Vector();
		vtValue.addElement("t");
		vtValue.addElement("r");
		vtValue.addElement("tr");
		vtReturn.addElement(createParameterDefinition("bind-mode", "", ParameterType.PARAM_COMBOBOX, vtValue, ""));
		vtReturn.addElement(createParameterDefinition("SMSCID", "", ParameterType.PARAM_TEXTBOX_MAX, "30", ""));
		vtReturn.addElement(createParameterDefinition("servicecode", "", ParameterType.PARAM_TEXTBOX_MAX, "30", ""));
		vtReturn.addElement(createParameterDefinition("username", "", ParameterType.PARAM_TEXTBOX_MAX, "30", ""));
		vtReturn.addElement(createParameterDefinition("password", "", ParameterType.PARAM_PASSWORD, "30", ""));
		vtValue = new Vector();
		vtValue.addElement("s");
		vtValue.addElement("a");

		vtReturn.addElement(createParameterDefinition("sync-mode", "", ParameterType.PARAM_COMBOBOX, vtValue, ""));
		vtReturn.addElement(
				createParameterDefinition("enquire-interval", "", ParameterType.PARAM_TEXTBOX_MASK, "999", ""));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	////////////////////////////////////////////////////////
	public Vector getAddressRangeParameter() {
		Vector vtReturn = new Vector();
		vtReturn.addElement(createParameterDefinition("addr-ton", "", ParameterType.PARAM_TEXTBOX_MASK, "9", "")); // 0
		vtReturn.addElement(createParameterDefinition("addr-npi", "", ParameterType.PARAM_TEXTBOX_MASK, "9", "")); // 1
		vtReturn.addElement(createParameterDefinition("address-range", "", ParameterType.PARAM_TEXTBOX_MAX, "15", "")); // 2
		return vtReturn;
	}

	////////////////////////////////////////////////////////
	public void beforeSession() throws Exception {
		super.beforeSession();
		// make sure smsc session is closed
		try {
			if (mSession != null) {
				if (mSession.isBound()) {
					if (mSession.getReceiver().isReceiver()) {
						mSession.unbind();
						mSession = null;
						mblBound = false;
						logMonitor("It can take a while to stop the receiver..", true);
						throw new Exception("It can take a while to stop the receiver..");
					}
				} else {
					mSession.unbind();
					mSession = null;
					mblBound = false;
				}
			}
		} catch (Exception e) {
			mSession = null;
			e.printStackTrace();
			throw e;
		} finally {
			mSession = null;
			mblBound = false;
		}

		// open smsc connection
		BindRequest request = null;
		BindResponse response = null;

		try {
			if (mStrBindMode.compareToIgnoreCase("t") == 0) {
				request = new BindTransmitter();
			} else if (mStrBindMode.compareToIgnoreCase("r") == 0) {
				request = new BindReceiver();
			} else if (mStrBindMode.compareToIgnoreCase("tr") == 0) {
				request = new BindTransciever();
			} else {
				throw new Exception(
						"Invalid bind mode, expected t, r or tr, got " + mStrBindMode + ". Operation canceled.");
			}
			TCPIPConnection connection = new TCPIPConnection(mStrIpAddress, miPort);
			connection.setReceiveTimeout(20 * 1000);
			mSession = new Session(connection);
			logMonitor("Session Value :" + mSession.toString());

			// set values
			request.setSystemId(mStrSystemId); // user
			request.setPassword(mStrPassword); // password
			request.setAddressRange(mAddressRange);
			logMonitor("Bind request " + getBindRequestInfor(request));
			if (mblAsynchronous) {
				mPduListener = new SMPPTestPDUEventListener(mSession);
				response = mSession.bind(request, mPduListener);
			} else {
				response = mSession.bind(request);
			}
			logMonitor("Bind response " + response.debugString());
			if (response.getCommandStatus() == Data.ESME_ROK) {
				mblBound = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			request = null;
			response = null;
			throw e;
		}
	}

	////////////////////////////////////////////////////////
	public void afterSession() throws Exception {
		super.afterSession();

		// Release ftp connection
		try {
			if (mSession != null) {
				if (mSession.getReceiver().isReceiver()) {
					mSession.unbind();
					mSession = null;
					mblBound = false;
					logMonitor("It can take a while to stop the receiver.");
				} else {
					mSession.unbind();
					mSession = null;
					mblBound = false;
				}
			}

			mSession.unbind();
			mSession = null;
			mblBound = false;
		} catch (Exception e) {
			e.printStackTrace();
			logMonitor("Error Unbind :" + e.getLocalizedMessage());
		}
	}

	////////////////////////////////////////////////////////
	private String getBindRequestInfor(BindRequest request) {
		String dbgs = "(bindreq: ";
		dbgs += request.getSystemId();
		dbgs += " ";
		dbgs += request.getSystemType();
		dbgs += " ";
		dbgs += Integer.toString(request.getInterfaceVersion());
		dbgs += " ";
		dbgs += request.getAddressRange().debugString();
		dbgs += ") ";
		return dbgs;
	}

	////////////////////////////////////////////////////////
	protected class SMPPTestPDUEventListener extends SmppObject implements ServerPDUEventListener {
		Session session;
		Queue requestEvents = new Queue();

		public SMPPTestPDUEventListener(Session session) {
			this.session = session;
		}

		public void handleEvent(ServerPDUEvent event) {
			PDU pdu = event.getPDU();
			if (pdu.isRequest() || pdu.isResponse()) {
				synchronized (requestEvents) {
					requestEvents.enqueue(event);
					requestEvents.notify();
				}
			} else {
				logMonitor("pdu of unknown class (not request nor " + "response) received, discarding "
						+ pdu.debugString());
			}
		}

		/**
		 * Returns received pdu from the queue. If the queue is empty, the
		 * method blocks for the specified timeout.
		 */
		public ServerPDUEvent getRequestEvent(long timeout) {
			ServerPDUEvent pduEvent = null;
			synchronized (requestEvents) {
				if (requestEvents.isEmpty()) {
					try {
						requestEvents.wait(timeout);
					} catch (InterruptedException e) {
						// ignoring, actually this is what we're waiting for
					}
				}
				if (!requestEvents.isEmpty()) {
					pduEvent = (ServerPDUEvent) requestEvents.dequeue();
				}
			}
			return pduEvent;
		}
	}

	////////////////////////////////////////////////////////
	protected synchronized void enquireLink() throws Exception {
		if (System.currentTimeMillis() < nextEnquireLink) {
			return;
		}
		EnquireLink request = new EnquireLink();
		EnquireLinkResp response;
		logMonitor("Sending enquire link.....");
		logMonitor("Enquire Link request " + request.debugString());
		if (mblAsynchronous) {
			synchronized (mSession) {
				mSession.enquireLink(request);
			}
		} else {
			synchronized (mSession) {
				response = mSession.enquireLink(request);
			}
			logMonitor("Enquire Link response " + response.debugString());
		}
		if (mcnMain == null || mcnMain.isClosed()) {
			try {
				logMonitor("Connection is closed");
				openConnection();
				logMonitor("Connection is opened");
			} catch (Exception ex) {
				ex.printStackTrace();
				logMonitor("Connection can not open");
			}
		}
		nextEnquireLink = System.currentTimeMillis() + enquireInterval * 1000;
	}
}
