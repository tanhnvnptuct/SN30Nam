package vnp.thread;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import telsoft.app.object.ConstantObject;
import telsoft.app.object.SubmitMessage;
import telsoft.app.util.AppUtil;
import telsoft.app.util.SequenceManager;
import telsoft.app.util.SubmitSMEx;
import telsoft.app.util.WapOTAMessage;
import telsoft.queue.MessageQueueEx;
import telsoft.service.ParameterType;
import smartlib.thread.ThreadConstant;
import smartlib.database.Database;
import smartlib.util.AppException;
import smartlib.util.DateUtil;
import smartlib.util.StringUtil;

import com.comverse_in.prepaid.ccws.AssignBonusPlan;
import com.comverse_in.prepaid.ccws.AssignBonusPlanRequest;
import com.comverse_in.prepaid.ccws.SetAccumulatorValueRequest;
import com.comverse_in.prepaid.ccws.run.PasswordCallback;
import com.comverse_in.prepaid.ccws.run.ServiceSoapStubEx;
import com.logica.smpp.Data;
import com.logica.smpp.pdu.SubmitSM;
import com.logica.smpp.util.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

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

public class DeliverySummitKMNT2 extends PortalThread {
	private String mstrUpdate = "update cdr_message set status = '1', sent_time = sysdate where rowid = ?";

	private PreparedStatement stmtGetSQL = null;
	private PreparedStatement stmtInsert = null;
	private ResultSet rsGetSQL = null;
	private int miSubmitSize = 5000;
	private String mstrSequencePath = "";
	private String mstrMessage = "";
	private String mstrSQL = "";
	private String mstrID = "";
	private ServiceSoapStubEx stub;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private String mstrURL = "";

	////////////////////////////////////////////////////////
	public void fillParameter() throws AppException {
		super.fillParameter();
		miSubmitSize = Integer.parseInt(loadString("QueueSize"));
		mstrSequencePath = loadString("sequence-path");
		mstrID = loadMandatory("ID");
		mstrMessage = loadMandatory("message");
		mstrSQL = loadMandatory("SQL");
		mstrURL = loadMandatory("URL");
	}

	////////////////////////////////////////////////////////
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		vtReturn.addElement(createParameterDefinition("URL", "", ParameterType.PARAM_TEXTAREA_MAX, "99999", ""));
		vtReturn.addElement(createParameterDefinition("SQL", "", ParameterType.PARAM_TEXTAREA_MAX, "99999", ""));
		vtReturn.addElement(createParameterDefinition("QueueSize", "", ParameterType.PARAM_TEXTBOX_MASK, "99999", ""));
		vtReturn.addElement(
				createParameterDefinition("sequence-path", "", ParameterType.PARAM_TEXTAREA_MAX, "100", ""));
		vtReturn.addElement(createParameterDefinition("message", "", ParameterType.PARAM_TEXTAREA_MAX, "99999", ""));
		vtReturn.addElement(createParameterDefinition("system-code", "", ParameterType.PARAM_TEXTAREA_MAX, "100", ""));
		vtReturn.addElement(createParameterDefinition("ID", "", ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	////////////////////////////////////////////////////////
	public void beforeSession() throws Exception {
		super.beforeSession();
		try {
			mcnMain.setAutoCommit(true);
			stmtGetSQL = mcnMain.prepareStatement(mstrSQL);
			stmtInsert = mcnMain.prepareStatement(mstrUpdate);
			SequenceManager.setSequencePath(mstrSequencePath);
			// stub = initComvConnection();
			URL endpointURL = new URL(mstrURL);
			stub = new ServiceSoapStubEx(endpointURL, null);
			stub._setProperty("action", "UsernameToken");
			stub._setProperty("passwordType", "PasswordText");
			stub._setProperty("user", "ncpt_sub_tool");
			PasswordCallback pwCallback = new PasswordCallback("123456");
			stub._setProperty("passwordCallbackRef", pwCallback);
			if (stub == null) {
				System.out.println("Connected to IN (CCWS-IN) error Stub  is null");
				throw new Exception("Binding to IN (CCWS-IN) is null");
			}
			stub.setTimeout(1000);
		} catch (Exception ex) {
			ex.printStackTrace();
			logMonitor(ex.getMessage());
			throw ex;
		}
	}

	// public static ServiceSoapStubEx initComvConnection() throws Exception {
	//// URL endpointURL = new URL("http://10.149.227.158/ccws/ccws.asmx");
	// URL endpointURL = new URL(mstrURL);
	// ServiceSoapStubEx stub = new ServiceSoapStubEx(endpointURL, null);
	// stub._setProperty("action", "UsernameToken");
	// stub._setProperty("passwordType", "PasswordText");
	// stub._setProperty("user", "ncpt_sub_tool");
	// PasswordCallback pwCallback = new PasswordCallback("123456");
	// stub._setProperty("passwordCallbackRef", pwCallback);
	// if (stub == null) {
	// System.out.println("Connected to IN (CCWS-IN) error Stub is null");
	// throw new Exception("Binding to IN (CCWS-IN) is null");
	// }
	// stub.setTimeout(1000);
	// return stub;
	// }

	////////////////////////////////////////////////////////
	public void processSession() throws Exception {
		logMonitor("Starting get message....");
		try {
			stmtGetSQL.setString(1, mstrID);
			stmtGetSQL.setString(2, mstrID);
			rsGetSQL = stmtGetSQL.executeQuery();
			String strMaxID = mstrID;
			if (((MessageQueueEx) getCommonVariable("QueueSubmit2")).size() < miSubmitSize) {
				int iBatch = 0;
				while (rsGetSQL.next()) {
					try {
						strMaxID = rsGetSQL.getString(1);
						logMonitor("ISDN: " + rsGetSQL.getString(5));
						// Gan bonus
						logMonitor("Assign Bonus Plan");
						try {
							AssignBonusPlanRequest abpr = new AssignBonusPlanRequest(rsGetSQL.getString(5), "",
									"BUNUS_CUCBO_RULE");
							stub.assignBonusPlan(abpr);
							
						} catch (Exception ex) {
							if (!ex.getMessage().equalsIgnoreCase(
									"<ErrorCode>4070</ErrorCode><ErrorDescription>Subscriber duplicate bonus plans</ErrorDescription>")) {
								throw ex;
							}
						}

						// Reset Acc
						logMonitor("Reset Acc");
						SetAccumulatorValueRequest savr = new SetAccumulatorValueRequest(rsGetSQL.getString(5), "",
								"ACC_CUCBO_RULE", Double.parseDouble("0"));
						stub.setAccumulatorValue(savr);

						// Gui tin SMS: Neu thoi gian hien tai sau 21h thi khong gui tin nhan.
						logMonitor("Build SMS");
						int iHour = Integer.parseInt(rsGetSQL.getString(4));
						if (iHour <= 2100) {
							sendTextMessage(rsGetSQL.getString(2), mstrMessage);
						}

						// Cap nhat du lieu
						logMonitor("Update data");
						logMonitor("KQ :" + rsGetSQL.getString(1) + "|" + rsGetSQL.getString(2));
						stmtInsert.setString(1, StringUtil.nvl(rsGetSQL.getString(3), ""));
						stmtInsert.addBatch();

						iBatch++;
						if (iBatch <= 1000) {
							continue;
						} else {
							stmtInsert.executeBatch();
							mcnMain.commit();
							iBatch = 0;
						}
					} catch (Exception ex) {
						logMonitor("ISDN:" + rsGetSQL.getString(1) + "|Error:" + ex.getMessage());
						continue;
					}
				}
				if (iBatch > 0) {
					stmtInsert.executeBatch();
					mcnMain.commit();
				}
			}
			// Cap nhat MAX ID cho lan sau
			logMonitor("finish get message....");
			setParameter("ID", strMaxID);
			storeConfig();
		} catch (Exception ex) {
			ex.printStackTrace();
			logMonitor(ex.getMessage());
			throw ex;
		}
		// }
	}

	////////////////////////////////////////////////////////
	public void afterSession() throws Exception {
		super.afterSession();
		try {
			Database.closeObject(rsGetSQL);
			Database.closeObject(stmtGetSQL);
			Database.closeObject(stmtInsert);
			super.afterSession();
		} catch (Exception ex) {
			ex.printStackTrace();
			logMonitor(ex.getMessage());
			throw ex;
		}
	}

	////////////////////////////////////////////////////////////////////////////
	// Description: gui ban tin text
	// Auth:
	// Date:
	////////////////////////////////////////////////////////////////////////////
	private void sendTextMessage(String strISDN, String strMessage) throws Exception {
		// logMonitor("Send SMS");
		// if (strMessage.length() > 160) {
		// SubmitSM submitSM = new SubmitSM();
		// ByteBuffer bbMSG = new ByteBuffer();
		// String[] strSplitMSG = splitByWidth(strMessage, 153);
		// int iTotalSegment = strSplitMSG.length;
		// int iRand = AppUtil.randInt();
		// for (int i = 0; i < iTotalSegment; i++) {
		// submitSM = new SubmitSM();
		// bbMSG = new ByteBuffer();
		// bbMSG.appendByte((byte) 0x05); // UDH Length
		// bbMSG.appendByte((byte) 0x00); // IE Identifier
		// bbMSG.appendByte((byte) 0x03); // IE Data Length
		// bbMSG.appendByte((byte) iRand); //Reference Number
		// bbMSG.appendByte((byte) iTotalSegment); //Number of pieces
		// bbMSG.appendByte((byte) (i + 1)); //Sequence number
		// bbMSG.appendString(strSplitMSG[i], Data.ENC_ISO8859_1);
		// submitSM.setShortMessageData(bbMSG);
		// submitSM.getSourceAddr().setAddress("333");
		// submitSM.getDestAddr().setAddress(strISDN);
		// submitSM.setEsmClass((byte) (Data.SM_UDH_GSM));
		// SubmitMessage submitMessage = new SubmitMessage(submitSM);
		// ((MessageQueueEx) getCommonVariable("QueueSubmit")).attach(
		// "333", "333", submitMessage);
		// }
		// } else {
		SubmitSM submitSM = new SubmitSM();
		submitSM.setShortMessage(strMessage);
		submitSM.getSourceAddr().setAddress("333");
		submitSM.getDestAddr().setAddress(strISDN);
		SubmitMessage submitMessage = new SubmitMessage(submitSM);
		((MessageQueueEx) getCommonVariable("QueueSubmit2")).attach("333", "333", submitMessage);
		// }
	}

	////////////////////////////////////////////////////////////////////////////
	// Description: chia text
	// Auth:
	// Date:
	// Out: String[]
	////////////////////////////////////////////////////////////////////////////
	private String[] splitByWidth(String strContent, int width) throws Exception {
		try {
			if (width == 0) {
				String[] strResult = new String[1];
				strResult[0] = strContent;
				return strResult;
			} else {
				if (strContent.equalsIgnoreCase("")) {
					return new String[0];
				} else {

					if (strContent.length() <= width) {
						String[] strResult = new String[1];
						strResult[0] = strContent;
						return strResult;
					} else {
						int NumSeg = strContent.length() / width + 1;
						String[] strResult = new String[NumSeg];
						int startPos = 0;

						for (int i = 0; i < NumSeg - 1; i++) {
							strResult[i] = strContent.substring(startPos, ((width * (i + 1))));
							startPos = (i + 1) * width;
						}
						strResult[NumSeg - 1] = strContent.substring(startPos, strContent.length());
						return strResult;
					}
				}
			}
		} catch (Exception ex) {
			return new String[0];
		}
	}
}
