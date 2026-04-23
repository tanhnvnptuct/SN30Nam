package vnp.thread;

import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import smartlib.util.AppException;
import telsoft.service.ParameterType;
import vnp.util.AppUtil;

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

public class ThreadViewQueue extends PortalThread {
	private String mstrISDNList = "";
	private String mstrSendSMS = "";
	private String mstrContent = "";
	private Vector mvtInputParams = new Vector();

	@Override
	public String getMyConnName() {
		return "DBAPP_IOS";
	};

	////////////////////////////////////////////////////////////////////////////
	// Description:
	// Auth:
	// Date:
	////////////////////////////////////////////////////////////////////////////
	public void fillParameter() throws AppException {
		Object obj = getParameter("QueueAlarmParameter");
		if (obj != null && obj instanceof Vector) {
			mvtInputParams = (Vector) obj;
		} else {
			mvtInputParams = new Vector();
		}

		if (mvtInputParams == null) {
			mvtInputParams = new Vector();
		}
		mstrISDNList = loadMandatory("ISDNList");
		mstrSendSMS = loadYesNo("SendSMS");
		mstrContent = loadMandatory("Content");
		super.fillParameter();
	}

	////////////////////////////////////////////////////////////////////////////
	// Description:
	// Auth:
	// Date:
	////////////////////////////////////////////////////////////////////////////
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		Vector vtDefinition = new Vector();

		vtDefinition.addElement(
				createParameterDefinition("QueueName", "", ParameterType.PARAM_TEXTBOX_MAX, "100", "QueueName", "0"));
		vtDefinition.addElement(createParameterDefinition("QueueAlarm", "", ParameterType.PARAM_TEXTBOX_MASK, "999999",
				"QueueAlarm", "100000"));
		vtReturn.addElement(createParameterDefinition("QueueAlarmParameter", "", ParameterType.PARAM_TABLE,
				vtDefinition, "Contain Queue Parameter"));
		vtReturn.addElement(createParameterDefinition("ISDNList", "", ParameterType.PARAM_TEXTAREA_MAX, "1000"));
		Vector vtValue = new Vector();
		vtValue.addElement("Y");
		vtValue.addElement("N");
		vtReturn.addElement(
				createParameterDefinition("SendSMS", "", ParameterType.PARAM_COMBOBOX, vtValue, "SendSMS", "0")); // 0
		vtReturn.addElement(createParameterDefinition("Content", "", ParameterType.PARAM_TEXTAREA_MAX, "1000"));

		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	////////////////////////////////////////////////////////
	public void beforeSession() throws Exception {
		super.beforeSession();
	}

	// //////////////////////////////////////////////////////
	public void afterSession() throws Exception {
		super.afterSession();
	}

	////////////////////////////////////////////////////////////////////////////
	// Description:
	// Auth:
	// Date:
	////////////////////////////////////////////////////////////////////////////
	public void processSession() throws Exception {
		for (int i = 0; i < mvtInputParams.size(); i++) {
			Vector vtRow = (Vector) mvtInputParams.elementAt(i);
			String strQueueName = vtRow.elementAt(0).toString();
			String strQueueAlarm = vtRow.elementAt(1).toString();

			int iQueueSize = ((BlockingQueue<?>) getCommonVariable(strQueueName)).size();
			// String strResult = ((BlockingQueue<?>)
			// getCommonVariable(strQueueName)).GroupSize();
			if (iQueueSize > 0) {
				String strMessage = mstrContent + " : " + strQueueName + ":" + iQueueSize + " records.";
				// strMessage = strMessage + "\n" + strResult;
				logMonitor(strMessage);
				if (iQueueSize > Integer.parseInt(strQueueAlarm)) {
					if (mstrSendSMS.equals("Y")) {
						logMonitor("Send Alarm:" + mstrISDNList);
						AppUtil.SendBrcSMS(mcnMain, "123", strMessage, mstrISDNList);
					}
				}
			}
		}
	}
}
