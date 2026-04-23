package vnp.util;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import smartlib.database.Database;
import smartlib.util.AppException;
import smartlib.util.StringUtil;
import smartlib.util.WildcardFilter;

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

public class AppUtil {
	public static int randInt() {
		Random generator = new Random();
		return generator.nextInt(255);
	}

	////////////////////////////////////////////////////////
	public static void waiting(int n) {
		try {
			Thread.sleep(n * 1000);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	////////////////////////////////////////////////////////
	public static String nvlEx(Object objInput, String strNullValue) {
		if (objInput == null || objInput.equals("")) {
			return strNullValue;
		}
		return objInput.toString();
	}

	////////////////////////////////////////////////////////////////////////////
	// Description: chia text
	// Auth:
	// Date:
	// Out: String[]
	////////////////////////////////////////////////////////////////////////////
	public static String[] splitByWidth(String strContent, int width) throws Exception {
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

		} catch (Exception e) {
			return new String[0];
		}
	}

	/////////////////////////////////////////////////////////////////////
	public static String[] getFileList(String strPath, String strWildcard) {
		File Dir = new File(strPath);
		return Dir.list(new WildcardFilter(strWildcard));
	}

	/////////////////////////////////////////////////////////////////////////
	public static String[] splitString(String pstrString, String pWilcard) {
		StringTokenizer tokenizer = null;
		if (!pWilcard.equals("")) {
			tokenizer = new StringTokenizer(pstrString, pWilcard);
		} else {
			tokenizer = new StringTokenizer(pstrString);
		}
		String rowValue[] = new String[tokenizer.countTokens()];
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			rowValue[i] = token;
			i++;
		}
		return rowValue;
	}

	////////////////////////////////////////////////////////
	public static String getInfo(String strInfo, String strParam, String strSplit) {
		String strReturn = "";
		String[] strInfoArray = StringUtil.toStringArray(strInfo, strSplit);
		for (int i = 0; i < strInfoArray.length; i++) {
			String[] strCheck = strInfoArray[i].split("=");
			if (strCheck[0].equals(strParam)) {
				if (strCheck.length > 1) {
					strReturn = strCheck[1];
				} else {
					strReturn = "";
				}
				break;
			}
		}

		return strReturn;
	}

	////////////////////////////////////////////////////////
	public static void SendSMS(Connection cn, String strISDNList, String strContent, Long lLastPos, Long lFileLength)
			throws Exception {
		if (strISDNList != null) {
			String[] arrISDNList = StringUtil.toStringArray(strISDNList, ";");
			int isize = arrISDNList.length;
			PreparedStatement pstmt = null;
			try {
				if ((isize > 0) && (!arrISDNList[0].equals(""))) {
					String strSQL = "INSERT INTO ALERT_SMS (SMS_ID, DATE_TIME, ISDN, CONTENT, LAST_POS, FILE_LENGTH) "
							+ "VALUES (SEQ_ALERT_SMS.NEXTVAL, sysdate, ?, ?, ?, ?)";
					pstmt = cn.prepareStatement(strSQL);
					for (int i = 0; i < isize; i++) {
						pstmt.setString(1, arrISDNList[i]);
						pstmt.setString(2, strContent);
						pstmt.setString(3, Long.toString(lLastPos));
						pstmt.setString(4, Long.toString(lFileLength));
						pstmt.executeUpdate();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				Database.closeObject(pstmt);
				cn.commit();
			}
		}
	}

	// //////////////////////////////////////////////////////
	public static void SendSMS(Connection cn, String strSystem, String strISDNList, String strContent,
			String strServiceCode, String strProcessID) throws Exception {
		if (strISDNList != null) {
			String[] arrISDNList = StringUtil.toStringArray(strISDNList, ";");
			int isize = arrISDNList.length;
			PreparedStatement pstmt = null;
			try {
				if ((isize > 0) && (!arrISDNList[0].equals(""))) {
					String strSQL = "INSERT INTO RESPOND_LOG (SEQUENCE_ID, RECORD_DATETIME, SYSTEM_CODE, ISDN, SERVICE_CODE, CONTENT, SMS_TYPE, SMSC_ID, STATUS, PROCESS_ID) "
							+ "VALUES (RESPOND_LOG_SEQ.NEXTVAL, sysdate, ?, ?, ?, ?, '0', '1', '0', ?)";
					pstmt = cn.prepareStatement(strSQL);
					for (int i = 0; i < isize; i++) {
						pstmt.setString(1, strSystem);
						pstmt.setString(2, arrISDNList[i]);
						pstmt.setString(3, strServiceCode);
						pstmt.setString(4, strContent);
						pstmt.setString(5, strProcessID);
						pstmt.executeUpdate();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				Database.closeObject(pstmt);
				cn.commit();
			}
		}
	}

	public static void SendBrcSMS(Connection cn, String prsSource, String prsContent, String prsDes) throws Exception {
		if (prsDes != null) {
			String[] arrISDNList = StringUtil.toStringArray(prsDes, ";");
			int isize = arrISDNList.length;
			CallableStatement pstmt = null;
			try {
				if ((isize > 0) && (!arrISDNList[0].equals(""))) {
					String strSQL = "begin ?:= bc_sms.fSendMT(?, ?, ?); end;";
					pstmt = cn.prepareCall(strSQL);
					pstmt.registerOutParameter(1, java.sql.Types.NUMERIC);
					for (int i = 0; i < isize; i++) {
						pstmt.setString(2, prsSource);
						pstmt.setString(3, prsContent);
						pstmt.setString(4, arrISDNList[i]);
						pstmt.executeUpdate();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				Database.closeObject(pstmt);
				cn.commit();
			}
		}
	}

	////////////////////////////////////////////////////////
	public static String validBarePhone(String strPhone) throws Exception {
		String strBarePhone = "";
		if (strPhone.startsWith("00")) {
			strBarePhone = strPhone.substring(2);
		} else if (!strPhone.startsWith("00")) {
			if (!strPhone.startsWith("84")) {
				String strTmp = strPhone;
				if (strPhone.substring(0, 1).equals("0")) {
					strTmp = strPhone.substring(1);
				}
				if (strTmp.length() == 9 || strTmp.length() == 10) {
					strBarePhone = "84" + strTmp;
				} else {
					strBarePhone = strTmp;
				}
			} else {
				if (strPhone.substring(2, 3).equals("0")) {
					strBarePhone = "84" + strPhone.substring(3);
				} else {
					strBarePhone = strPhone;
				}
			}
		}

		return strBarePhone;
	}

	////////////////////////////////////////////////////////
	public static int getNumber(String strArray, String strSplit) throws Exception {
		String[] strInfoArray = StringUtil.toStringArray(strArray, strSplit);
		return strInfoArray.length;
	}

	////////////////////////////////////////////////////////
	public static int getStringPos(String strInfo, String strArray, String strSplit) throws Exception {
		String[] strInfoArray = StringUtil.toStringArray(strArray, strSplit);
		for (int i = 0; i < strInfoArray.length; i++) {
			if (strInfo.equals(strInfoArray[i])) {
				return i;
			}
		}

		return -1;
	}

	////////////////////////////////////////////////////////
	public static String getStringPos(int iPos, String strArray, String strSplit) throws Exception {
		String[] strInfoArray = StringUtil.toStringArray(strArray, strSplit);

		return strInfoArray[iPos];
	}

	////////////////////////////////////////////////////////
	public static String getStringFirst(String strArray, String strSplit) throws Exception {
		String[] strInfoArray = StringUtil.toStringArray(strArray, strSplit);
		for (int i = 1; i < strInfoArray.length - 1; i++) {
			if (!strInfoArray[i].equals("")) {
				return strInfoArray[i];
			}
		}

		return "";
	}

	////////////////////////////////////////////////////////
	public static int getStringFirstPos(String strArray, String strSplit) throws Exception {
		String[] strInfoArray = StringUtil.toStringArray(strArray, strSplit);
		for (int i = 1; i < strInfoArray.length; i++) {
			if (strInfoArray[i].equals("")) {
				return i;
			}
		}

		return -1;
	}

	////////////////////////////////////////////////////////
	public static String setStringPos(int iPos, String strArray, String strReplace, String strSplit) throws Exception {
		String strReturn = "";
		String[] strInfoArray = StringUtil.toStringArray(strArray, strSplit);
		for (int i = 0; i < strInfoArray.length; i++) {
			if (i == iPos) {
				strReturn += strReplace + strSplit;
			} else {
				strReturn += strInfoArray[i] + strSplit;
			}
		}

		return strReturn.substring(0, strReturn.length() - 1);
	}

	////////////////////////////////////////////////////////
	public static String buildCallBarringCMD(Connection cn, String strISDN, String strCB_BL_Action,
			String strCB_WL_Action, String strCB_Other_Action, String strCB_Other_Cli_Status,
			String strCB_Other_Act_Type, String strInCBAction, String strInCBCli00, String strInCBTreef,
			String strInCBOtherCli, String strInCBActionType) throws Exception {
		String strCB_Command = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String strSQL = " Select phone, type, bare_phone, tree_ref " + " From callbarring_info " + " where Isdn = ?"
				+ " and status = '1'";

		try {
			stmt = cn.prepareStatement(strSQL);
			stmt.setString(1, strISDN);
			rs = stmt.executeQuery();
			while (rs.next()) {
				String strBarePhone = StringUtil.nvl(rs.getString(3), "");
				String strBareType = StringUtil.nvl(rs.getString(2), "");

				int iPos = getStringPos(strBarePhone, strInCBCli00, "&");
				if (iPos > 0) {
					System.out.println(strBarePhone + ":" + iPos);
					if (strBareType.equals("2")) {
						strInCBAction = setStringPos(iPos, strInCBAction, strCB_BL_Action, "&");
					} else {
						strInCBAction = setStringPos(iPos, strInCBAction, strCB_WL_Action, "&");
					}
				}
			}
			System.out.println(strInCBAction);
			if (!strCB_Other_Action.equals("")) {
				int iTotal = getNumber(strInCBAction, "&");
				strInCBAction = setStringPos(iTotal - 1, strInCBAction, strCB_Other_Action, "&");
			}

			if (!strCB_Other_Act_Type.equals("")) {
				int iTotal = getNumber(strInCBActionType, "&");
				strInCBActionType = setStringPos(iTotal - 1, strInCBActionType, strCB_Other_Act_Type, "&");
			}

			if (!strCB_Other_Cli_Status.equals("")) {
				strInCBOtherCli = strCB_Other_Cli_Status;
			}

			System.out.println(strInCBAction);
			System.out.println(strInCBActionType);
			System.out.println(strInCBOtherCli);
			System.out.println(strInCBTreef);
		} catch (Exception ex) {
			throw new AppException(ex, "loadForm", strSQL);
		} finally {
			Database.closeObject(rs);
			Database.closeObject(stmt);

			return strCB_Command;
		}
	}

	////////////////////////////////////////////////////////
	public static Calendar convertUTC(Date dtData) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		sdf.format(dtData);
		return sdf.getCalendar();
	}

	////////////////////////////////////////////////////////
	public static boolean indexOf(String strOrg, String strDes, String strSplit) {
		boolean bCheck = false;
		StringTokenizer st1 = new StringTokenizer(strOrg, strSplit, false);
		while (st1.hasMoreTokens()) {
			String strTmp = st1.nextToken();
			try {
				if (strTmp.equals(strDes)) {
					bCheck = true;
					break;
				}
			} catch (Exception ex) {
				continue;
			}
		}
		return bCheck;
	}

	//////////////////////////////////////////////////////
	public static String formatISDN(String strISDN) {
		String strISDNReturn = "";
		if (!strISDN.startsWith("84")) {
			String strTmp = strISDN;
			if (strISDN.substring(0, 1).equals("0")) {
				strTmp = strISDN.substring(1);
			}
			if (strTmp.length() == 9 || strTmp.length() == 10) {
				strISDNReturn = "84" + strTmp;
			} else {
				strISDNReturn = strTmp;
			}
		} else {
			if (strISDN.substring(2, 3).equals("0")) {
				strISDNReturn = "84" + strISDN.substring(3);
			} else {
				strISDNReturn = strISDN;
			}
		}
		return strISDNReturn;
	}

	public static String shortenISDN(String strISDN) {
		return strISDN.replaceFirst("^(0|84){1}", "");
	}

	public static String formatFullISDN(String strISDN) {
		//return "84" + shortenISDN(strISDN);
		return "84" + strISDN;
	}

	////////////////////////////////////////////////////////
	public static String formatOppositeISDN(String strISDN) {
		String strISDNReturn = "";
		if (strISDN.startsWith("84")) {
			String tmp = strISDN.substring(2);
			if (tmp.startsWith("0")) {
				strISDNReturn = tmp.substring(1);
			} else {
				strISDNReturn = tmp;
			}
		} else {
			if (strISDN.startsWith("0")) {
				strISDNReturn = strISDN.substring(1);
			} else {
				strISDNReturn = strISDN;
			}
		}
		return strISDNReturn;
	}

	////////////////////////////////////////////////////////
	public static long subMiliSecondDate(Date dtStart, Date dtEnd) {
		Calendar calStart = convertUTC(dtStart);
		Calendar calEnd = convertUTC(dtEnd);
		return (long) ((calEnd.getTimeInMillis() - calStart.getTimeInMillis()));
	}

	// //////////////////////////////////////////////////////
	public static long subSecondDate(Date dtStart, Date dtEnd) {
		return (long) (subMiliSecondDate(dtStart, dtEnd) / (1000));
	}

	// //////////////////////////////////////////////////////
	public static long subMinuteDate(Date dtStart, Date dtEnd) {
		return (long) (subMiliSecondDate(dtStart, dtEnd) / (1000 * 60));
	}

	// //////////////////////////////////////////////////////
	public static long subHourDate(Date dtStart, Date dtEnd) {
		return (long) (subMiliSecondDate(dtStart, dtEnd) / (1000 * 60 * 60));
	}

	// //////////////////////////////////////////////////////
	public static long subDayDate(Date dtStart, Date dtEnd) {
		return (long) (subMiliSecondDate(dtStart, dtEnd) / (1000 * 60 * 60 * 24));
	}

	// //////////////////////////////////////////////////////
	public static String[] sortArrayString(String[] strDATAs) {
		Arrays.sort(strDATAs, Collections.reverseOrder());

		return strDATAs;
	}

	// //////////////////////////////////////////////////////
	public static Vector getSubTwoVectorString(Vector vtSrc, Vector vtDes) {
		Vector vtResult = new Vector();
		for (int i = 0; i < vtDes.size(); i++) {
			String strDes = StringUtil.nvl(vtDes.elementAt(i), "");
			if (!vtSrc.contains(strDes)) {
				vtResult.addElement(strDes);
			}
		}

		return vtResult;
	}

	////////////////////////////////////////////////////////
	public static boolean checkConnection(Connection cnn) {
		PreparedStatement stmt = null;
		String strSQL = "select 1 from dual";
		try {
			stmt = cnn.prepareStatement(strSQL);
			stmt.executeQuery();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			Database.closeObject(stmt);
		}
	}

	////////////////////////////////////////////////////////
	public static Map processModule(Map mData, Vector vtData, Map mProcessResult, Connection cConnection,
			String strClass, String strMethod) throws Exception {
		Method method = null;
		Object object = null;
		try {
			Class[] paramType = { Map.class, Vector.class, Map.class, Connection.class };
			Object[] objList = { mData, vtData, mProcessResult, cConnection };
			Map mClassLoader = (Map) mData.get(ConstantObject.$M_CLASS_LOADER);

			Class c = (Class) mClassLoader.get(strClass);
			method = c.getDeclaredMethod(strMethod, paramType);
			object = c.newInstance();
			mData = (Map) method.invoke(object, objList);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			method = null;
			object = null;
		}
		return mData;
	}

	public static boolean compareDate(String strOperator, java.util.Date dtValue, java.util.Date dtCompare) {
		if (strOperator.equals("1") && dtValue.after(dtCompare)) { // ">"
			return true;
		} else if (strOperator.equals("2") && dtValue.before(dtCompare)) { // "<"
			return true;
		} else if (strOperator.equals("3") && (dtValue.after(dtCompare) || dtValue.equals(dtCompare))) { // ">="
			return true;
		} else if (strOperator.equals("4") && (dtValue.before(dtCompare) || dtValue.equals(dtCompare))) { // "<="
			return true;
		} else if (strOperator.equals("5") && dtValue.equals(dtCompare)) { // "="
			return true;
		} else if (strOperator.equals("6") && !dtValue.equals(dtCompare)) { // "<>"
			return true;
		}
		return false;
	}

	public static boolean compareString(String strOperator, String strValue, String strCompare) {
		if (strOperator.equalsIgnoreCase("7") && strValue.equalsIgnoreCase(strCompare)) { // "EQUALS"
			return true;
		} else if (strOperator.equals("8") && strValue.contains(strCompare)) { // "IN"
			return true;
		} else if (strOperator.equals("9") && !strValue.contains(strCompare)) { // "NOT
																				// IN"
			return true;
		}
		return false;
	}

	public static boolean compareInteger(String strOperator, int iValue, int iCompare) {
		if (strOperator.equals("1") && iValue > iCompare) {
			return true;
		} else if (strOperator.equals("2") && iValue < iCompare) {
			return true;
		} else if (strOperator.equals("3") && iValue >= iCompare) {
			return true;
		} else if (strOperator.equals("4") && iValue <= iCompare) {
			return true;
		} else if (strOperator.equals("5") && iValue == iCompare) {
			return true;
		} else if (strOperator.equals("6") && iValue != iCompare) {
			return true;
		}
		return false;
	}

	public static boolean compareLong(String strOperator, long lValue, long lCompare) {
		if (strOperator.equals("1") && lValue > lCompare) {
			return true;
		} else if (strOperator.equals("2") && lValue < lCompare) {
			return true;
		} else if (strOperator.equals("3") && lValue >= lCompare) {
			return true;
		} else if (strOperator.equals("4") && lValue <= lCompare) {
			return true;
		} else if (strOperator.equals("5") && lValue == lCompare) {
			return true;
		} else if (strOperator.equals("6") && lValue != lCompare) {
			return true;
		}
		return false;
	}

}
