package telsoft.app.util;

import com.logica.smpp.pdu.SubmitSM;
import com.logica.smpp.util.ByteBuffer;

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

public class SubmitSMEx extends SubmitSM {
	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		try {
			buffer.appendCString(getServiceType());
			buffer.appendBuffer(getSourceAddr().getData());
			buffer.appendBuffer(getDestAddr().getData());
			buffer.appendByte(getEsmClass());
			buffer.appendByte(getProtocolId());
			buffer.appendByte(getPriorityFlag());
			buffer.appendCString(getScheduleDeliveryTime());
			buffer.appendCString(getValidityPeriod());
			buffer.appendByte(getRegisteredDelivery());
			buffer.appendByte(getReplaceIfPresentFlag());
			buffer.appendByte(getDataCoding());
			buffer.appendBuffer(shortMessage.getData());
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return buffer;
	}
}
