package vnp.thread;

import java.sql.PreparedStatement;
import java.util.Vector;

import telsoft.app.object.ConstantObject;
import telsoft.app.object.SubmitMessage;
import telsoft.app.util.AppUtil;
import telsoft.queue.MessageQueueEx;
import telsoft.service.ParameterType;
import smartlib.thread.ThreadConstant;
import smartlib.database.Database;
import smartlib.util.AppException;

import com.logica.smpp.Data;
import com.logica.smpp.pdu.Address;
import com.logica.smpp.pdu.SubmitSM;
import com.logica.smpp.pdu.SubmitSMResp;
import com.logica.smpp.pdu.WrongLengthOfStringException;
import com.logica.smpp.util.ByteBuffer;
import smartlib.thread.ThreadParameter;

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

public class SMSSender2 extends SMSThread {
	private Address mSourceAddress = new Address();
	private Address mDestAddress = new Address();
	private byte replaceIfPresentFlag = 0;
	private String mstrSplitPrrefix = "";
	private String mstrPrrefix = "";

	// private String mstrUpdate = "UPDATE respond_log " +
	// "set status = '0' " +
	// "WHERE sequence_id = ? " +
	// "and status = '1'";
	// private PreparedStatement stmtUpdate = null;

	////////////////////////////////////////////////////////
	public void fillParameter() throws AppException {
		super.fillParameter();
		mStrBindMode = "t";
		byte ton = (byte) loadUnsignedIntegerSubParam("source-address-range.addr-ton", 0);
		byte npi = (byte) loadUnsignedIntegerSubParam("source-address-range.addr-npi", 1);
		String strAddr = loadMandatorySubParam("source-address-range.address-range", 2);
		setAddressParameter("source-address-range", mSourceAddress, ton, npi, strAddr);

		ton = (byte) loadUnsignedIntegerSubParam("Dest-address-range.addr-ton", 0);
		npi = (byte) loadUnsignedIntegerSubParam("Dest-address-range.addr-npi", 1);
		strAddr = loadMandatorySubParam("Dest-address-range.address-range", 2);
		setAddressParameter("Dest-address", mDestAddress, ton, npi, strAddr);
		mstrSplitPrrefix = loadMandatory("Cut-Prefix");
		mstrPrrefix = loadMandatory("Prefix");
	}

	////////////////////////////////////////////////////////
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		vtReturn.addElement(createParameterDefinition("source-address-range", "",
				ParameterType.PARAM_SUB_TABULAR_EDITOR, getSourceAddressRangeParameter(), "source-address-range"));
		vtReturn.addElement(createParameterDefinition("Dest-address-range", "", ParameterType.PARAM_SUB_TABULAR_EDITOR,
				getSourceAddressRangeParameter(), "Dest-address-range"));
		vtReturn.addAll(super.getParameterDefinition());
		vtReturn = removeElement(vtReturn, "bind-mode");

		Vector vtValue = new Vector();
		vtValue.addElement("Y");
		vtValue.addElement("N");
		vtReturn.addElement(createParameterDefinition("Cut-Prefix", "", ParameterType.PARAM_COMBOBOX, vtValue));
		vtReturn.addElement(createParameterDefinition("Prefix", "", ParameterType.PARAM_TEXTBOX_MAX, "15", ""));

		return vtReturn;
	}

	////////////////////////////////////////////////////////
	public Vector getSourceAddressRangeParameter() {
		Vector vtReturn = new Vector();
		vtReturn.addElement(createParameterDefinition("addr-ton", "", ParameterType.PARAM_TEXTBOX_MASK, "9", "")); // 0
		vtReturn.addElement(createParameterDefinition("addr-npi", "", ParameterType.PARAM_TEXTBOX_MASK, "9", "")); // 1
		vtReturn.addElement(createParameterDefinition("address-range", "", ParameterType.PARAM_TEXTBOX_MAX, "15", "")); // 2
		return vtReturn;
	}

	////////////////////////////////////////////////////////
	private void setAddressParameter(String descr, Address address, byte ton, byte npi, String addr) {
		address.setTon(ton);
		address.setNpi(npi);
		try {
			address.setAddress(addr);
		} catch (WrongLengthOfStringException e) {
			logMonitor("The length of " + descr + " parameter is wrong.");
		}
	}

	////////////////////////////////////////////////////////
	public void beforeSession() throws Exception {
		super.beforeSession();
		try {
			// stmtUpdate = mcnMain.prepareStatement(mstrUpdate);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	////////////////////////////////////////////////////////
	public void afterSession() throws Exception {
		super.afterSession();
		try {
			// Database.closeObject(stmtUpdate);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	////////////////////////////////////////////////////////
	private void updateRespond(long lSequence) throws Exception {
		// update bang response
		// stmtUpdate.setString(1, Long.toString(lSequence));
		// stmtUpdate.execute();
	}

	////////////////////////////////////////////////////////
	public void processSession() throws AppException {
		try {
			// debugMonitor("Going to submit function");
			submit();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(e, "processSession");
		}
	}

	////////////////////////////////////////////////////////
	private void submit() throws AppException, Exception {
		SubmitSM submitRequest = null;
		long lSequence = 0;
		long nextLog = System.currentTimeMillis();
		try {
			int iQueueSize = ((MessageQueueEx) getCommonVariable("QueueSubmit2")).size();
			while (miThreadCommand != ThreadConstant.THREAD_STOP) {
				// Write log for alarm
				if (System.currentTimeMillis() >= nextLog) {
					logMonitor("Thread is running.");
					nextLog = System.currentTimeMillis() + ConstantObject.$TIME_WRITE_LOG * 1000;
				}
				// send enquire link
				if (System.currentTimeMillis() >= nextEnquireLink) {
					enquireLink();
				}
				// check connection first
				for (int i = 0; i <= iQueueSize; i++) {

					// SubmitSM submitSM = new SubmitSM();
					// submitSM.setShortMessage(strMessage);
					// submitSM.getSourceAddr().setAddress("333");
					// submitSM.getDestAddr().setAddress(strISDN);
					// SubmitMessage submitMessage = new
					// SubmitMessage(submitSM);
					SubmitMessage submitMessage = (SubmitMessage) ((MessageQueueEx) getCommonVariable("QueueSubmit2"))
							.detach(mstrServiceCode, mstrSMSCID);
					if (submitMessage != null) {
						submitRequest = submitMessage.getSubmitSM();
						String strCallingNumber = submitRequest.getSourceAddr().getAddress();
						String strCalledNumber = submitRequest.getDestAddr().getAddress();
						// lSequence = submitMessage.getRequestID();
						// if (mstrSplitPrrefix.equals("Y")) {
						// if (strCallingNumber.startsWith(mstrPrrefix)) {
						// strCallingNumber = strCallingNumber.substring(
						// mstrPrrefix.length());
						// }
						// }
						// setting address
						mSourceAddress.setAddress(strCallingNumber);
						mDestAddress.setAddress(strCalledNumber);
						submitRequest.setSourceAddr(mSourceAddress);
						submitRequest.setDestAddr(mDestAddress);
						submitRequest.setReplaceIfPresentFlag(replaceIfPresentFlag);
						// additional paprameters
						submitRequest.setRegisteredDelivery((byte) 0);
						debugMonitor("Submit data :" + submitRequest.debugString());
						// if (mblAsynchronous) {
						// synchronized (mSession) {
						// try {
						// mSession.submit(submitRequest);
						// } catch (Exception e) {
						// e.printStackTrace();
						//// updateRespond(lSequence);
						// logMonitor("updateRespond:" + lSequence);
						// submitMessage = null;
						// throw e;
						// }
						// }
						// } else {
						SubmitSMResp submitResponse = null;
						synchronized (mSession) {
							try {
								// logMonitor("Submitting");
								logMonitor("Sending :" + strCalledNumber);
								mSession.submit(submitRequest);
								// logMonitor("Submit Response: " +
								// submitResponse);
							} catch (Exception e) {
								e.printStackTrace();
								// updateRespond(lSequence);
								logMonitor("updateRespond:" + lSequence + "|" + e.getMessage());
								((MessageQueueEx) getCommonVariable("QueueSubmit2")).attach("333", "333",
										submitMessage);
								submitMessage = null;
								throw e;
							}
						}
						// debugMonitor("SubmitSMResp :" +
						// submitResponse.debugString());
					}
					nextEnquireLink = System.currentTimeMillis() + enquireInterval * 1000;
				}
				// }
				Thread.sleep(5);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(e, "submit");
		} finally {
			mSession = null;
			submitRequest = null;
		}
	}
}
