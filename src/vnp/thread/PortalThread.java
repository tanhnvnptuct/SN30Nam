package vnp.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import smartlib.database.ConnectionFactory;
import smartlib.dictionary.Dictionary;
import smartlib.dictionary.DictionaryNode;
import smartlib.thread.GroupParameter;
import smartlib.thread.ManageableThread;
import smartlib.thread.ThreadParameter;
import smartlib.util.AppException;
import smartlib.util.StringUtil;
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

public abstract class PortalThread extends ManageableThread {

	private Dictionary dic = null;
	protected String testing_mode = "Y";
	protected int test = 0;
	protected List<String> testMsisdns;

	/**
	 * @return the myConnName
	 */
	public String getMyConnName() {
		return null;
	}

	protected boolean IsTestingMode() {
		return "Y".equalsIgnoreCase(testing_mode);
	}

	protected boolean IsInTestMsisdn(String msisdn) {
		return (testMsisdns != null) && testMsisdns.contains(msisdn);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see smartlib.thread.ManageableThread#loadConfig()
	 */
	@Override
	public void loadConfig() throws Exception {
		// TODO Auto-generated method stub
		super.loadConfig();
		test = 1;
		if (getMyConnName() != null && getMyConnName() != "") {
			dic = new Dictionary("configuration/server.txt");
			int iConnectPoolSize = 20;
			try {
				iConnectPoolSize = Integer.parseInt(dic.getString("ConnectionPoolSize"));
			} catch (Exception e) {
			}

			DictionaryNode ndConnection = dic.getChild("Connection." + getMyConnName());
			pool = new ConnectionFactory(ndConnection.getString("Driver"), ndConnection.getString("Url"),
					ndConnection.getString("UserName"), ndConnection.getString("Password"), iConnectPoolSize);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see smartlib.thread.ManageableThread#processSession()
	 */
	@Override
	protected void processSession() throws Exception {
		// TODO Auto-generated method stub

	}

	private ConnectionFactory pool = null;
	protected java.sql.Connection mcnMain = null;
	protected String conName = null;

	////////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		vtReturn.addAll(super.getParameterDefinition());
		removeParameterDefinition(vtReturn, "Alert");
		removeParameterDefinition(vtReturn, "Debug");

		Vector vtValue = new Vector();
		vtValue.addElement("Y");
		vtValue.addElement("N");
		vtReturn.addElement(createParameterDefinition("testing_mode", "Y", ParameterType.PARAM_COMBOBOX, vtValue,
				"testing_mode", "0")); // 0
		return vtReturn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see smartlib.thread.ManageableThread#fillParameter()
	 */
	@Override
	public void fillParameter() throws AppException {
		// TODO Auto-generated method stub
		testing_mode = loadYesNo("testing_mode");
		super.fillParameter();
	}

	////////////////////////////////////////////////////////
	public void beforeSession() throws Exception {
		super.beforeSession();
		openConnection();
		if (dic == null)
			dic = new Dictionary("configuration/server.txt");
		testMsisdns = Collections.synchronizedList(new ArrayList<String>());
		DictionaryNode testMsisdnNode = dic.getChild("TestMsisdns");
		if (testMsisdnNode != null)
			for (Object ndConn : testMsisdnNode.getChildValueList()) {
				this.testMsisdns.add(ndConn.toString());
			}
	}

	// //////////////////////////////////////////////////////
	public void afterSession() throws Exception {
		super.afterSession();
		closeConnection();
	}

	protected void openConnection() throws Exception {
		if (pool != null)
			mcnMain = pool.getConnection();
		else
			mcnMain = this.getManager().getConnection();
	}

	protected void closeConnection() {
		smartlib.database.Database.closeObject(mcnMain);
	}

	////////////////////////////////////////////////////////
	protected synchronized void debugMonitor(String message) {
		if (this.isDebug()) {
			logMonitor(message);
		}
	}

	public Object getCommonVariable(Object object) throws Exception {
		return ((AppManager) this.getManager().getProcessorListener()).getCommonVariable(object);
	}

	public void setCommonVariable(Object object, Object object1) throws Exception {
		((AppManager) this.getManager().getProcessorListener()).setCommonVariable(object, object1);
	}

	/////////////
	//
	/////////////

	@SuppressWarnings("static-access")
	public ThreadParameter createParameterDefinition(String string, Object object, String string2, Object object3) {
		return this.createParameter(string, object, Integer.parseInt(string2), object3);
	}

	@SuppressWarnings("static-access")
	public ThreadParameter createParameterDefinition(String string, Object object, String string2, Object object3,
			String string4) {
		return this.createParameter(string, object, Integer.parseInt(string2), object3, string4);
	}

	@SuppressWarnings("static-access")
	public ThreadParameter createParameterDefinition(String string, Object object, String string2, Object object3,
			String string4, String string5) {
		return this.createParameter(string, object, Integer.parseInt(string2), object3, string4, string5);
	}

	public int loadUnsignedIntegerSubParam(String string, int _int) throws AppException {
		String strArray[] = StringUtil.toStringArray(string, ".");
		GroupParameter gp = new GroupParameter(this, strArray[0]);
		return gp.loadUnsignedInteger(strArray[1]);
	}

	@SuppressWarnings("deprecation")
	protected String loadMandatorySubParam(String string, int _int) throws AppException {
		String strArray[] = StringUtil.toStringArray(string, ".");
		GroupParameter gp = new GroupParameter(this, strArray[0]);
		return gp.loadMandatory(strArray[1]);
	}

	public String loadString(String string) throws AppException {
		String strParameterValue = StringUtil.nvl(getParameter(string), "");
		return strParameterValue;
	}

	@SuppressWarnings("rawtypes")
	public Vector removeElement(Vector vtReturn, String strParamName) {
		for (int iIndex = vtReturn.size() - 1; iIndex >= 0; iIndex--) {
			ThreadParameter tp = ((ThreadParameter) vtReturn.elementAt(iIndex));
			if (tp.getName().equals(strParamName)) {
				vtReturn.removeElementAt(iIndex);
			}
		}
		return vtReturn;
	}

}
