package vnp.thread;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

import com.logica.smpp.pdu.SubmitSM;
import vnp.util.SequenceManager;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author DinhLV
 * @version 1.0
 */

public class SubmitMessageVAS {
	private String commandCode;
	private String content;
	private Properties mprtAttribute;
	private SubmitSM submitSM;
	private long requestID;
	private long messageSrcID;
	private long responseID;
	private String extendedMessage;
	private boolean usingSequence = true;
	private String confirmStatus;

	////////////////////////////////////////////////////////
	public SubmitMessageVAS(SubmitSM submitSM) throws Exception {
		this(submitSM, true);
	}

	////////////////////////////////////////////////////////
	public SubmitMessageVAS(SubmitSM submitSM, boolean usingSequence) throws Exception {
		extendedMessage = "";
		this.usingSequence = usingSequence;
		responseID = (int)SequenceManager.getSequence("RESPONSE_SEQ");
		mprtAttribute = new Properties();
		this.submitSM = submitSM;
		if(usingSequence)
			this.submitSM.setSequenceNumber((int)responseID);
		else
			this.submitSM.setSequenceNumber(0);
	}

	////////////////////////////////////////////////////////
	public boolean isUsingSequence() {
		return usingSequence;
	}

	////////////////////////////////////////////////////////
	public Object getAttribute(String strKey) {
		return mprtAttribute.get(strKey);
	}

	////////////////////////////////////////////////////////
	public void setAttribute(String strKey,
							 Object objValue) {
		mprtAttribute.put(strKey, objValue);
	}

	////////////////////////////////////////////////////////
	public void setAttributes(Properties prt) {
		mprtAttribute = prt;
	}

	////////////////////////////////////////////////////////
	public String getContent() {
		return content;
	}

	////////////////////////////////////////////////////////
	public String getCommandCode() {
		return commandCode;
	}

	////////////////////////////////////////////////////////
	public void setCommandCode(String commandCode) {
		this.commandCode = commandCode;
	}

	////////////////////////////////////////////////////////
	public void setConfirmStatus(String confirmStatus) {
		this.confirmStatus = confirmStatus;
	}

	////////////////////////////////////////////////////////
	public String getConfirmStatus() {
		return confirmStatus;
	}

	////////////////////////////////////////////////////////
	public SubmitSM getSubmitSM() {
		return submitSM;
	}

	////////////////////////////////////////////////////////
	public long getMessageSrcID() {
		return messageSrcID;
	}

	////////////////////////////////////////////////////////
	public void setMessageSrcID(long messageSrcID) {
		this.messageSrcID = messageSrcID;
	}

	////////////////////////////////////////////////////////
	public long getRequestID() {
		return requestID;
	}

	////////////////////////////////////////////////////////
	public void setRequestID(long requestID) {
		this.requestID = requestID;
	}

	////////////////////////////////////////////////////////
	public long getResponseID() {
		return responseID;
	}

	////////////////////////////////////////////////////////
	private void setResponseID(long responseID) {
		this.responseID = responseID;
	}

	////////////////////////////////////////////////////////
	public String getExtendedMessage() {
		return extendedMessage;
	}

	////////////////////////////////////////////////////////
	public void setExtendedMessage(String extendedMessage) {
		this.extendedMessage = extendedMessage;
	}

	////////////////////////////////////////////////////////
	public Map getAttributes() {
		return(Map)mprtAttribute;
	}

	////////////////////////////////////////////////////////
	public void setAttributes(Map map) {
		this.mprtAttribute = (Properties)map;
	}

	////////////////////////////////////////////////////////
	public void load(InputStream inputStream) throws Exception {
	}

	////////////////////////////////////////////////////////
	public void store(OutputStream outputStream) throws Exception {
	}
}
