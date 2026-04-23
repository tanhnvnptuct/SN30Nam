package telsoft.app.util;

import java.util.Vector;

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

public class BytecodeMessage extends CommonMessage {
	////////////////////////////////////////////////////////
	// Purpose:
	////////////////////////////////////////////////////////
	public static Vector splitBytes(byte[] src, int len) {
		Vector vtReturn = new Vector();
		if(src == null) {
			return vtReturn;
		}
		int iStart = 0;
		int iRemain = src.length;
		while(iRemain != 0) {
			if(iRemain > len) {
				byte[] bt = new byte[len];
				iRemain = iRemain - len;
				System.arraycopy(src, iStart, bt, 0, len);
				iStart += len;
				vtReturn.addElement(bt);
			} else {
				byte[] bt = new byte[iRemain];
				System.arraycopy(src, iStart, bt, 0, iRemain);
				vtReturn.addElement(bt);
                iRemain = 0;
			}
		}
		return vtReturn;
	}
}
