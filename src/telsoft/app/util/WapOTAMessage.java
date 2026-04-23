package telsoft.app.util;

import java.io.ByteArrayOutputStream;
import java.util.Vector;
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;

import com.logica.smpp.util.ByteBuffer;
import vnp.thread.AppManager;

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

public class WapOTAMessage extends BytecodeMessage {
//	////////////////////////////////////////////////////////
//	public Vector createWapOta_Message_Type2(String strOperatorName) throws Exception {
//		try {
//			byte[] arrOtaWbxml = createWapOta_Wbxml_Type2(strOperatorName, 10);
//			//ByteBuffer bTemp = new ByteBuffer(arrOtaWbxml);
//			//System.out.println("type2:" + bTemp.getHexDump());
//			Vector vtReturn = new Vector();
//			Vector vtData = BytecodeMessage.splitBytes(arrOtaWbxml, 128);
//			for(int i = 0; i < vtData.size(); i++) {
//				ByteBuffer message = new ByteBuffer();
//				// UDH is needed to tell the mobile phone details
//				// how to deliver the data in the message payload
//				// first goes UDH length -- this UDH will have 6 bytes
//				if(vtData.size() > 1) {
//					message.appendByte((byte)0x0B);
//				} else {
//					message.appendByte((byte)0x06);
//					// then goes IE -- information element
//					// IE Identifier -- 5 means that the following will
//					// be destination and originator port numbers
//				}
//				//String UDHOTA = "0B0504C34FC00200";
//				//String UDHOMA = "0B05040B8423F000";
//
//				message.appendByte((byte)5);
//				// IE Data Length -- the length of the IE
//				// two ports per two bytes = 4
//				message.appendByte((byte)4);
//				// the destination port -- port where ringing tone is received
//				//message.appendShort( (short) 49999);C34F
//				// originator port (unused in fact)
//				//message.appendShort( (short) 9200);C002
//				//System.out.println("UDH:"+message.toString());
//				message.appendByte((byte)0x0B);
//				message.appendByte((byte)0x84);
//				message.appendByte((byte)0x23);
//				message.appendByte((byte)0xF0);
//				if(vtData.size() > 1) {
//					message.appendByte((byte)0x00); // UDH SAR IE
//					message.appendByte((byte)0x03); // UDH SAR IE Length
//					message.appendByte((byte)0x04); // Datagram reference number
//					message.appendByte((byte)vtData.size()); // Total number of segments in datagram
//					message.appendByte((byte)(i + 1)); // Segment count
//				}
//
//				message.appendBytes((byte[])vtData.elementAt(i));
//				vtReturn.addElement(message);
//			}
//			return vtReturn;
//		} catch(Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//
//	}
//
//	////////////////////////////////////////////////////////
//	public byte[] createWapOta_Wbxml_Type2(String operatorName,
//										   int iTransactionId) throws Exception {
//		ByteArrayOutputStream arrOtaWsp = new ByteArrayOutputStream();
//		ByteArrayOutputStream arrOtaWbxml = new ByteArrayOutputStream();
//                AppManager appManager = new AppManager();
//                appManager.loadFileConfig();
//		String strMessage1 = appManager.getParameter("GRPSConfig." + operatorName + ".MESSAGE.01");
//		String strMessage2 = appManager.getParameter("GRPSConfig." + operatorName + ".MESSAGE.02");
//		String strMessage3 = appManager.getParameter("GRPSConfig." + operatorName + ".MESSAGE.03");
//		byte[] bMessage1 = HextoByteArray.fromHexString(strMessage1);
//		byte[] bMessage2 = HextoByteArray.fromHexString(strMessage2);
//		byte[] bMessage3 = HextoByteArray.fromHexString(strMessage3);
//
//		arrOtaWbxml.write(bMessage1);
//		arrOtaWbxml.write(bMessage2);
//		arrOtaWbxml.write(bMessage3);
//
//		byte[] arrMacValues;
//		ByteArrayOutputStream result = new ByteArrayOutputStream();
//		Mac theMac = Mac.getInstance("HmacSHA1");
//		SecretKeySpec theKey = new SecretKeySpec("1111".getBytes(), "HmacSHA1");
//		theMac.init(theKey);
//		byte[] authentication = theMac.doFinal(arrOtaWbxml.toByteArray());
//
//		for(int i = 0; i < authentication.length; i++) {
//			byte temp1 = (byte)((authentication[i] & 0xf0) >> 4);
//			byte temp2 = (byte)(authentication[i] & 0x0f);
//			temp1 += 0x30;
//			if(temp1 > 0x39) {
//				temp1 = (byte)(temp1 += 7);
//			}
//			temp2 += 0x30;
//			if(temp2 > 0x39) {
//				temp2 = (byte)(temp2 += 7);
//			}
//			result.write(temp1);
//			result.write(temp2);
//		}
//
//		arrMacValues = result.toByteArray();
//		//===== Header
//		arrOtaWsp.write(iTransactionId);
//		arrOtaWsp.write(0x06); // Push PDU Type
//
//		arrOtaWsp.write(0x2F); //strMacValues.length() + 7); // Headers length
//
//		// Content type value length given as "Length-quote Length"
//
//		arrOtaWsp.write(0x1F);
//		arrOtaWsp.write(0x2D); //strMacValues.length() + 5); // 0x2d;
//
//		arrOtaWsp.write(0xB6); // The assigned number for the media type application/vnd.wap.connectivity-wbxml is 36 [WINA]. This is encoded as a short integer.
//
//		// Assigned number for the well-known parameter SEC is 11.
//		// This is encoded as a short integer. Chosen security method is USERPIN (1), encoded as a short integer.
//		arrOtaWsp.write(0x91);
//		arrOtaWsp.write(0x81);
//
//		// Assigned number for the well-known parameter MAC is 12. This is encoded as a short integer.
//		arrOtaWsp.write(0x92);
//
//		arrOtaWsp.write(arrMacValues);
//		// End-of-string for the encoded MAC value.
//		arrOtaWsp.write(WbxmlOtaType2Token.NULL_TERMINATION_OF_CONTENT_TYPE_STRING);
//
//		arrOtaWsp.write(arrOtaWbxml.toByteArray());
//		return arrOtaWsp.toByteArray();
//	}
//
//	////////////////////////////////////////////////////////
//	public Vector createStreamingMessage(String strOperatorName) throws Exception {
//		try {
//			byte[] arrOtaWbxml = createStreaming_Wbxml_Type2(strOperatorName, 10);
//			//ByteBuffer bTemp = new ByteBuffer(arrOtaWbxml);
//			//System.out.println("type2:" + bTemp.getHexDump());
//			Vector vtReturn = new Vector();
//			Vector vtData = BytecodeMessage.splitBytes(arrOtaWbxml, 128);
//			for(int i = 0; i < vtData.size(); i++) {
//				ByteBuffer message = new ByteBuffer();
//				// UDH is needed to tell the mobile phone details
//				// how to deliver the data in the message payload
//				// first goes UDH length -- this UDH will have 6 bytes
//				if(vtData.size() > 1) {
//					message.appendByte((byte)0x0B);
//				} else {
//					message.appendByte((byte)0x06);
//					// then goes IE -- information element
//					// IE Identifier -- 5 means that the following will
//					// be destination and originator port numbers
//				}
//				//String UDHOTA = "0B0504C34FC00200";
//				//String UDHOMA = "0B05040B8423F000";
//
//				message.appendByte((byte)5);
//				// IE Data Length -- the length of the IE
//				// two ports per two bytes = 4
//				message.appendByte((byte)4);
//				// the destination port -- port where ringing tone is received
//				//message.appendShort( (short) 49999);C34F
//				// originator port (unused in fact)
//				//message.appendShort( (short) 9200);C002
//				//System.out.println("UDH:"+message.toString());
//				message.appendByte((byte)0x0B);
//				message.appendByte((byte)0x84);
//				message.appendByte((byte)0x23);
//				message.appendByte((byte)0xF0);
//				if(vtData.size() > 1) {
//					message.appendByte((byte)0x00); // UDH SAR IE
//					message.appendByte((byte)0x03); // UDH SAR IE Length
//					message.appendByte((byte)0x04); // Datagram reference number
//					message.appendByte((byte)vtData.size()); // Total number of segments in datagram
//					message.appendByte((byte)(i + 1)); // Segment count
//				}
//
//				message.appendBytes((byte[])vtData.elementAt(i));
//				vtReturn.addElement(message);
//			}
//			return vtReturn;
//		} catch(Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//
//	}
//
//	////////////////////////////////////////////////////////
//	public Vector createStreamingMessageNew(String strMessage) throws Exception {
//		try {
//			byte[] arrOtaWbxml = createStreaming_Wbxml_Type3(strMessage, 10);
//			//ByteBuffer bTemp = new ByteBuffer(arrOtaWbxml);
//			//System.out.println("type2:" + bTemp.getHexDump());
//			Vector vtReturn = new Vector();
//			Vector vtData = BytecodeMessage.splitBytes(arrOtaWbxml, 128);
//			for(int i = 0; i < vtData.size(); i++) {
//				ByteBuffer message = new ByteBuffer();
//				// UDH is needed to tell the mobile phone details
//				// how to deliver the data in the message payload
//				// first goes UDH length -- this UDH will have 6 bytes
//				if(vtData.size() > 1) {
//					message.appendByte((byte)0x0B);
//				} else {
//					message.appendByte((byte)0x06);
//					// then goes IE -- information element
//					// IE Identifier -- 5 means that the following will
//					// be destination and originator port numbers
//				}
//				//String UDHOTA = "0B0504C34FC00200";
//				//String UDHOMA = "0B05040B8423F000";
//
//				message.appendByte((byte)5);
//				// IE Data Length -- the length of the IE
//				// two ports per two bytes = 4
//				message.appendByte((byte)4);
//				// the destination port -- port where ringing tone is received
//				//message.appendShort( (short) 49999);C34F
//				// originator port (unused in fact)
//				//message.appendShort( (short) 9200);C002
//				//System.out.println("UDH:"+message.toString());
//				message.appendByte((byte)0x0B);
//				message.appendByte((byte)0x84);
//				message.appendByte((byte)0x23);
//				message.appendByte((byte)0xF0);
//				if(vtData.size() > 1) {
//					message.appendByte((byte)0x00); // UDH SAR IE
//					message.appendByte((byte)0x03); // UDH SAR IE Length
//					message.appendByte((byte)0x04); // Datagram reference number
//					message.appendByte((byte)vtData.size()); // Total number of segments in datagram
//					message.appendByte((byte)(i + 1)); // Segment count
//				}
//
//				message.appendBytes((byte[])vtData.elementAt(i));
//				vtReturn.addElement(message);
//			}
//			return vtReturn;
//		} catch(Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//
//	}
//
//	////////////////////////////////////////////////////////
//	public byte[] createStreaming_Wbxml_Type2(String operatorName,
//											  int iTransactionId) throws Exception {
//		ByteArrayOutputStream arrOtaWsp = new ByteArrayOutputStream();
//		ByteArrayOutputStream arrOtaWbxml = new ByteArrayOutputStream();
//                AppManager appManager = new AppManager();
//                appManager.loadFileConfig();
//		String strMessage1 = appManager.getParameter("Streaming." + operatorName + ".MESSAGE.01");
//		String strMessage2 = appManager.getParameter("Streaming." + operatorName + ".MESSAGE.02");
//
//		byte[] bMessage1 = HextoByteArray.fromHexString(strMessage1);
//		byte[] bMessage2 = HextoByteArray.fromHexString(strMessage2);
//
//		arrOtaWbxml.write(bMessage1);
//		arrOtaWbxml.write(bMessage2);
//
//		byte[] arrMacValues;
//		ByteArrayOutputStream result = new ByteArrayOutputStream();
//		Mac theMac = Mac.getInstance("HmacSHA1");
//		SecretKeySpec theKey = new SecretKeySpec("1234".getBytes(), "HmacSHA1");
//		theMac.init(theKey);
//		byte[] authentication = theMac.doFinal(arrOtaWbxml.toByteArray());
//
//		for(int i = 0; i < authentication.length; i++) {
//			byte temp1 = (byte)((authentication[i] & 0xf0) >> 4);
//			byte temp2 = (byte)(authentication[i] & 0x0f);
//			temp1 += 0x30;
//			if(temp1 > 0x39) {
//				temp1 = (byte)(temp1 += 7);
//			}
//			temp2 += 0x30;
//			if(temp2 > 0x39) {
//				temp2 = (byte)(temp2 += 7);
//			}
//			result.write(temp1);
//			result.write(temp2);
//		}
//
//		arrMacValues = result.toByteArray();
//		//===== Header
//		arrOtaWsp.write(iTransactionId);
//		arrOtaWsp.write(0x06); // Push PDU Type
//
//		arrOtaWsp.write(0x2F); //strMacValues.length() + 7); // Headers length
//
//		// Content type value length given as "Length-quote Length"
//
//		arrOtaWsp.write(0x1F);
//		arrOtaWsp.write(0x2D); //strMacValues.length() + 5); // 0x2d;
//
//		arrOtaWsp.write(0xB6); // The assigned number for the media type application/vnd.wap.connectivity-wbxml is 36 [WINA]. This is encoded as a short integer.
//
//		// Assigned number for the well-known parameter SEC is 11.
//		// This is encoded as a short integer. Chosen security method is USERPIN (1), encoded as a short integer.
//		arrOtaWsp.write(0x91);
//		arrOtaWsp.write(0x81);
//
//		// Assigned number for the well-known parameter MAC is 12. This is encoded as a short integer.
//		arrOtaWsp.write(0x92);
//
//		arrOtaWsp.write(arrMacValues);
//		// End-of-string for the encoded MAC value.
//		arrOtaWsp.write(WbxmlOtaType2Token.NULL_TERMINATION_OF_CONTENT_TYPE_STRING);
//
//		arrOtaWsp.write(arrOtaWbxml.toByteArray());
//		return arrOtaWsp.toByteArray();
//	}
//
//	////////////////////////////////////////////////////////////////////////////
//	// Description:
//	// Auth:
//	// Date:
//	////////////////////////////////////////////////////////////////////////////
//	public byte[] createStreaming_Wbxml_Type3(String strMessage,
//											  int iTransactionId) throws Exception {
//		ByteArrayOutputStream arrOtaWsp = new ByteArrayOutputStream();
//		ByteArrayOutputStream arrOtaWbxml = new ByteArrayOutputStream();
//
//		byte[] bMessage = HextoByteArray.fromHexString(strMessage);
//
//		arrOtaWbxml.write(bMessage);
//
//		byte[] arrMacValues;
//		ByteArrayOutputStream result = new ByteArrayOutputStream();
//		Mac theMac = Mac.getInstance("HmacSHA1");
//		SecretKeySpec theKey = new SecretKeySpec("1234".getBytes(), "HmacSHA1");
//		theMac.init(theKey);
//		byte[] authentication = theMac.doFinal(arrOtaWbxml.toByteArray());
//
//		for(int i = 0; i < authentication.length; i++) {
//			byte temp1 = (byte)((authentication[i] & 0xf0) >> 4);
//			byte temp2 = (byte)(authentication[i] & 0x0f);
//			temp1 += 0x30;
//			if(temp1 > 0x39) {
//				temp1 = (byte)(temp1 += 7);
//			}
//			temp2 += 0x30;
//			if(temp2 > 0x39) {
//				temp2 = (byte)(temp2 += 7);
//			}
//			result.write(temp1);
//			result.write(temp2);
//		}
//
//		arrMacValues = result.toByteArray();
//		//===== Header
//		arrOtaWsp.write(iTransactionId);
//		arrOtaWsp.write(0x06); // Push PDU Type
//
//		arrOtaWsp.write(0x2F); //strMacValues.length() + 7); // Headers length
//
//		// Content type value length given as "Length-quote Length"
//
//		arrOtaWsp.write(0x1F);
//		arrOtaWsp.write(0x2D); //strMacValues.length() + 5); // 0x2d;
//
//		arrOtaWsp.write(0xB6); // The assigned number for the media type application/vnd.wap.connectivity-wbxml is 36 [WINA]. This is encoded as a short integer.
//
//		// Assigned number for the well-known parameter SEC is 11.
//		// This is encoded as a short integer. Chosen security method is USERPIN (1), encoded as a short integer.
//		arrOtaWsp.write(0x91);
//		arrOtaWsp.write(0x81);
//
//		// Assigned number for the well-known parameter MAC is 12. This is encoded as a short integer.
//		arrOtaWsp.write(0x92);
//
//		arrOtaWsp.write(arrMacValues);
//		// End-of-string for the encoded MAC value.
//		arrOtaWsp.write(WbxmlOtaType2Token.NULL_TERMINATION_OF_CONTENT_TYPE_STRING);
//
//		arrOtaWsp.write(arrOtaWbxml.toByteArray());
//		return arrOtaWsp.toByteArray();
//	}
}
