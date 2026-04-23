package telsoft.app.util;

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

public class CommonMessage {
	public static final int VALUE_INDEX = 5;

	////////////////////////////////////////////////////////
	public String getContent(Object objResponse, 
							 int iIndex) throws Exception {
		String strContent = "";
		Object[] arrDetail;
		if(objResponse instanceof oracle.sql.STRUCT) {
			arrDetail = ((oracle.sql.STRUCT)objResponse).getAttributes();
			strContent = arrDetail[iIndex].toString();
		}
		return strContent;
	}
}
