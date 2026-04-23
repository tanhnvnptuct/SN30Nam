package vnp.thread;

import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.PropertyConfigurator;

import smartlib.admin.server.AppAuthenticator;
import smartlib.admin.server.DataHistoryUtil;
import smartlib.database.ConnectionFactory;
import smartlib.dictionary.Dictionary;
import smartlib.dictionary.DictionaryNode;
import smartlib.thread.FileThreadLister2;
import smartlib.thread.ProcessorListener;
import smartlib.thread.ThreadManager;
import smartlib.thread.ThreadProcessor;
import smartlib.util.Global;
import smartlib.util.LogOutputStream;
import vnp.bean.CardItem;
import vnp.bean.Maduthuong;
import vnp.bean.PrizeRewardBuffer;
import vnp.bean.SmsMt;
import vnp.util.CommonVars;

/**
 *
 * @author HuongNV
 *
 */
public class AppManager implements ProcessorListener {
	private ConnectionFactory pool = null;
	private String DefaultConnName = null;
	private Dictionary dic = null;
	private Hashtable<String, ConnectionFactory> ConnFactories = new Hashtable<String, ConnectionFactory>();
	private Hashtable<Object, Object> mVariables = new Hashtable<Object, Object>();
	public static String SQL_SOURCE_PATH = "resources/";
	public static String REPORT_EXPORT_PATH = "mail/";

	static {
		try {
			com.sim.Global.setTemplateDirectoryAsResource(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadFileConfig() {
		try {
			dic = new Dictionary("configuration/server.txt");
			DefaultConnName = dic.getString("DefaultDatabase");
			int iConnectPoolSize = 20;
			try {
				iConnectPoolSize = Integer.parseInt(dic.getString("ConnectionPoolSize"));
			} catch (Exception e) {
			}
			for (Object ndConn : dic.getChild("Connection").getChildList()) {
				DictionaryNode ndConnection = (DictionaryNode) ndConn;
				ConnectionFactory cnnf = new ConnectionFactory(ndConnection.getString("Driver"),
						ndConnection.getString("Url"), ndConnection.getString("UserName"),
						ndConnection.getString("Password"), iConnectPoolSize);
				ConnFactories.put(ndConnection.getPath(), cnnf);
				if (ndConnection.mstrName.equalsIgnoreCase(DefaultConnName)) {
					pool = cnnf;
				}
			}
			// DictionaryNode ndConnection = dic.getChild("Connection." +
			// DefaultConnName);
			// pool = new ConnectionFactory(ndConnection.getString("Driver"),
			// ndConnection.getString("Url"),
			// ndConnection.getString("UserName"),
			// ndConnection.getString("Password"), iConnectPoolSize);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 *
	 * @param strKey
	 *            String
	 * @param strDefault
	 *            String
	 * @return String
	 */
	public String getParameter(String strKey, String strDefault) {
		String res = dic.getString(strKey);
		if (res == null || res.equals("")) {
			res = strDefault;
		}
		return res;
	}

	/**
	 *
	 * @param strKey
	 *            String
	 * @return String
	 */
	public String getParameter(String strKey) {
		return getParameter(strKey, "");
	}

	/**
	 *
	 * @return Connection
	 * @throws Exception
	 */
	public Connection getConnection() throws Exception {
		return pool.getConnection();
	}

	public Connection getConnection(String connName) throws Exception {
		ConnectionFactory connf = ConnFactories.get("Connection." + connName);
		if (connf != null)
			return connf.getConnection();
		else
			return null;
	}

	/**
	 *
	 * @param processor
	 *            ThreadProcessor
	 * @throws Exception
	 */
	public void onCreate(ThreadProcessor processor) throws Exception {
		processor.log = new DataHistoryUtil();
		processor.authenticator = new AppAuthenticator();
	}

	/**
	 *
	 * @param processor
	 *            ThreadProcessor
	 * @throws Exception
	 */
	public void onOpen(ThreadProcessor processor) throws Exception {
		processor.mcnMain = getConnection();
		((DataHistoryUtil) processor.log).setConnection(processor.mcnMain);
		((AppAuthenticator) processor.authenticator).setConnection(processor.mcnMain);
	}

	/**
	 *
	 * @param args
	 *            String[]
	 */
	public static void main(String args[]) {
		PropertyConfigurator.configure("configuration/log4j.properties");
		AppManager appManager = new AppManager();

		// BlockingQueue<PrizeList> queueAvailPrize = new
		// LinkedBlockingQueue<PrizeList>();
		// BlockingQueue<SmsMo> queueMO = new LinkedBlockingQueue<SmsMo>();
		BlockingQueue<SmsMt> queueMT = new LinkedBlockingQueue<SmsMt>();
		BlockingQueue<SmsMt> queueMDT = new LinkedBlockingQueue<SmsMt>();
		BlockingQueue<SmsMt> queueBRC = new LinkedBlockingQueue<SmsMt>();
		
		BlockingQueue<Maduthuong> qSyncMDT = new LinkedBlockingQueue<Maduthuong>();
		BlockingQueue<CardItem> queueCard = new LinkedBlockingQueue<CardItem>();
		try {
			// appManager.setCommonVariable(CommonVars.QUEUE_MO, queueMO);
			appManager.setCommonVariable(CommonVars.QUEUE_MT, queueMT);
			appManager.setCommonVariable(CommonVars.QUEUE_MDT, queueMDT);
			appManager.setCommonVariable(CommonVars.QUEUE_BRC, queueBRC);
			appManager.setCommonVariable(CommonVars.QUEUE_SYNC_LOTTERYCODE, qSyncMDT);
			appManager.setCommonVariable(CommonVars.QUEUE_CARDITEM, queueCard);
			// List<String> testMsisdns=Collections.synchronizedList(new
			// ArrayList<String>());
			// if (dic == null)
			// dic = new Dictionary("configuration/server.txt");
			// testMsisdns = Collections.synchronizedList(new
			// ArrayList<String>());
			// DictionaryNode testMsisdnNode = dic.getChild("TestMsisdns");
			// if (testMsisdnNode != null)
			// for (Object ndConn : testMsisdnNode.getChildValueList()) {
			// testMsisdns.add(ndConn.toString());
			// }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		appManager.loadFileConfig();
		appManager.initSystem();
	}

	public void initSystem() {
		try {
			System.setProperty("mail.smtp.auth", "true");
			String strLogFile = getParameter("ErrorLog", "error.log");
			String strWorkingDir = System.getProperty("user.dir");
			if (!strWorkingDir.endsWith("/") || !strWorkingDir.endsWith("\\")) {
				strWorkingDir += "/";
			}
			File fl = new File(strWorkingDir + strLogFile);
			if (fl.getParentFile() != null) {
				fl.getParentFile().mkdirs();
			}
			PrintStream ps = new PrintStream(new LogOutputStream(strWorkingDir + strLogFile));
			System.setOut(ps);
			System.setErr(ps);

			Global.APP_NAME = "Telsoft ThreadManager";

			int iPortID = 8338;
			try {
				iPortID = Integer.parseInt(getParameter("PortID"));
			} catch (NumberFormatException ex) {
			}

			ThreadManager cs = new ThreadManager(iPortID, this);

			// Set action log file
			strLogFile = getParameter("ActionLog");
			fl = new File(strLogFile);
			if (fl.getParentFile() != null) {
				fl.getParentFile().mkdirs();
			}
			if (strLogFile != null && !strLogFile.equals("")) {
				cs.setActionLogFile(strLogFile);
			}
			// Set action log file
			strLogFile = getParameter("AlertLog");
			fl = new File(strLogFile);
			if (fl.getParentFile() != null) {
				fl.getParentFile().mkdirs();
			}
			if (strLogFile != null && !strLogFile.equals("")) {
				cs.setAlertLogFile(new File(strLogFile));
			}
			// Set max logfile size
			try {
				if (!getParameter("MaxLoggingSize").equals("")) {
					int iMaxLogFileSize = Integer.parseInt(getParameter("MaxLoggingSize"));
					if (iMaxLogFileSize > 0) {
						cs.setMaxLogFileSize(iMaxLogFileSize);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Set MaxLogContentSize
			try {
				if (!getParameter("MaxLogContentSize").equals("")) {
					int iMaxLogContentSize = Integer.parseInt(getParameter("MaxLogContentSize"));
					if (iMaxLogContentSize > 0) {
						cs.setMaxLogContentSize(iMaxLogContentSize);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Set max connection
			try {
				int iMaxConnectionAllowed = Integer.parseInt(getParameter("MaxConnectionAllowed"));
				if (iMaxConnectionAllowed > 0) {
					cs.setMaxConnectionAllowed(iMaxConnectionAllowed);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			cs.getThreadListers().clear();
			cs.getThreadListers().add(new FileThreadLister2("configuration/thread/"));
			com.sim.Global.setResourceDirectory(SQL_SOURCE_PATH);
			com.sim.Global.setOutputDirectory(REPORT_EXPORT_PATH);
			// Start manager
			cs.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCommonVariable(Object name, Object value) throws Exception {
		mVariables.put(name, value);
	}

	public Object getCommonVariable(Object object) {
		return mVariables.get(object);
	}
}
