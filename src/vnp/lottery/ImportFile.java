package vnp.lottery;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Vector;

import vnp.thread.PortalThread;
import vnp.util.ParameterType;
import smartlib.database.Database;
import smartlib.thread.ThreadConstant;
import smartlib.util.AppException;
import smartlib.util.FileUtil;
import smartlib.util.SmartZip;
import smartlib.util.WildcardFilter;
import telsoft.file.util.TextFileReader;
import telsoft.file.util.TextFileWriter;

public class ImportFile extends PortalThread {
	////////////////////////////////////////////////////////
	// Member variables
	////////////////////////////////////////////////////////
	// Directory variables
	protected Connection mcn;
	protected boolean mbConnectManual;
	protected String mstrDBUrl;
	protected String mstrDBUserName;
	protected String mstrDBPassword;
	protected String mstrImportDir;
	protected String mstrBackupDir;
	protected String mstrRejectDir;
	protected String mstrErrorDir;
	protected String mstrTempDir;
	protected String mstrWildcard;
	protected String mstrCompress;
	// File Process variables
	protected TextFileReader readInput;
	protected TextFileWriter writeReject;
	protected TextFileWriter writeError;
	protected int iTotal = 0;
	protected int iSuc = 0;
	protected int iRej = 0;
	protected int iErr = 0;

	// Sequence variables
	protected int miTimeSleep;
	protected int miNumRecordSleep;
	protected int miFileSeq;

	// Common used variables
	protected int miTotalFile;
	protected boolean bFileOK = true;
	protected String mstrBackupType;

	////////////////////////////////////////////////////////
	// Override
	////////////////////////////////////////////////////////
	public void fillParameter() throws AppException {
		super.fillParameter();
		////////////////////////////////////////////////////////
		// Fill parameter
		////////////////////////////////////////////////////////
		mstrImportDir = loadDirectory("ImportDir", true, true);
		mstrBackupDir = loadDirectory("BackupDir", true, false);
		mstrRejectDir = loadDirectory("RejectDir", true, false);
		mstrErrorDir = loadDirectory("ErrorDir", true, true);
		mstrBackupType = loadMandatory("BackupType");
		mstrTempDir = loadDirectory("TempDir", true, true);
		mstrCompress = loadString("Compress");

		try {
			FileUtil.forceFolderExist(mstrImportDir);
			FileUtil.forceFolderExist(mstrBackupDir);
			FileUtil.forceFolderExist(mstrRejectDir);
			FileUtil.forceFolderExist(mstrErrorDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		////////////////////////////////////////////////////////
		mstrWildcard = loadMandatory("Wildcard");
		miTimeSleep = loadUnsignedInteger("TimeSleep");
		miNumRecordSleep = loadUnsignedInteger("NumRecordSleep");
	}

	////////////////////////////////////////////////////////
	// Override
	////////////////////////////////////////////////////////
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		Vector vtValue = new Vector();
		vtValue = new Vector();
		vtValue.addElement("Y");
		vtValue.addElement("N");
		vtReturn.addElement(createParameter("ManualConnect", "", 4, vtValue, ""));

		vtReturn.addElement(createParameter("Url", "", 2, "256", "Connection url of database"));
		vtReturn.addElement(createParameter("UserName", "", 2, "256", "DB user name"));
		vtReturn.addElement(createParameter("Password", "", 3, "100", "Password of DB user name"));

		vtReturn.addElement(createParameterDefinition("ImportDir", "", ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addElement(createParameterDefinition("BackupDir", "", ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addElement(createParameterDefinition("RejectDir", "", ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addElement(createParameterDefinition("ErrorDir", "", ParameterType.PARAM_TEXTBOX_MAX, "100"));
		Vector vtValue1 = new Vector();
		vtValue1.addElement("N");
		vtValue1.addElement("DIRECT");
		vtValue1.addElement("DAILY");
		vtReturn.addElement(createParameterDefinition("BackupType", "", ParameterType.PARAM_COMBOBOX, vtValue1, ""));
		vtReturn.addElement(createParameterDefinition("TempDir", "", ParameterType.PARAM_TEXTBOX_MAX, "100"));
		Vector vtValue3 = new Vector();
		vtValue3.addElement("N");
		vtValue3.addElement("z");
		vtValue3.addElement("zip");
		vtValue3.addElement("gzip");
		vtReturn.addElement(createParameterDefinition("Compress", "", ParameterType.PARAM_COMBOBOX, vtValue3, ""));
		vtReturn.addElement(createParameterDefinition("Wildcard", "", ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addElement(createParameterDefinition("TimeSleep", "", ParameterType.PARAM_TEXTBOX_MASK, "9999999990"));
		vtReturn.addElement(
				createParameterDefinition("NumRecordSleep", "", ParameterType.PARAM_TEXTBOX_MASK, "9999999990"));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	public void beforeImportFile(String strFileName) throws Exception {
		logMonitor("Start of processing file " + strFileName);
		readInput = new TextFileReader();
		readInput.openFile(mstrImportDir + "/" + strFileName, 1024 * 1024);
		writeReject = new TextFileWriter();
		writeReject.openFile(mstrRejectDir + "/" + strFileName, 1024 * 1024);
		writeError = new TextFileWriter();
		writeError.openFile(mstrErrorDir + "/" + strFileName, 1024 * 1024);
		iTotal = 0;
		iSuc = 0;
		iRej = 0;
		iErr = 0;
	}

	public void afterImportFile(String strFileName) throws Exception {
		try {
			readInput.safeCloseFile();
			if (bFileOK) { // if file OK, rename to BackupDir
				if (!mstrBackupDir.equals("")) {
					java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyyMMdd");
					String strCurrentDate = fmt.format(new java.util.Date());
					String strBackupFilePath = "";
					if (mstrBackupType.equals("DAILY")) {
						FileUtil.forceFolderExist(mstrBackupDir + strCurrentDate);
						strBackupFilePath = mstrBackupDir + strCurrentDate + "/" + strFileName;
					} else if (mstrBackupType.equals("DIRECT")) {
						strBackupFilePath = mstrBackupDir + "/" + strFileName;
					}
					if (!mstrBackupType.equals("N")) {
						if (!FileUtil.renameFile(mstrImportDir + strFileName, strBackupFilePath)) {
							String strMsg = "Cannot rename file " + mstrImportDir + strFileName + " to " + mstrBackupDir
									+ strFileName;
							throw new AppException(strMsg);
						}
					}
				} else if (!mstrBackupType.equals("N")) {
					FileUtil.deleteFile(mstrImportDir + strFileName);
				}
				try {
					storeConfig();
				} catch (Exception e) {
					throw new AppException(e.getMessage());
				}
			}
			if (iRej == 0) {
				writeReject.clear();
			} else {
				writeReject.safeCloseFile();
			}
			if (iErr == 0) {
				writeError.clear();
			} else {
				writeError.safeCloseFile();
			}
		} finally {
			// logMonitor("Total row : " + iTotal);
			// logMonitor("Succ row : " + iSuc);
			// logMonitor("Reject row : " + iRej);
			// logMonitor("Error row : " + iErr);
			logMonitor("End of processing file " + strFileName);
		}
	}

	public boolean importFile(String strFileName) throws Exception {
		return false;
	}

	@Override
	public String getMyConnName() {
		return "PORTAL_63";
	};
	
	public void beforeProcessSession() throws Exception {
		super.beforeSession();
		if (mbConnectManual) {
			mcn = Database.getConnection(this.mstrDBUrl, this.mstrDBUserName, this.mstrDBPassword);
		} else {
			mcn = mcnMain;
		}
		mcn.setAutoCommit(false);

	}

	public void afterProcessSession() throws Exception {
		Database.closeObject(mcn);
		super.afterSession();
	}

	public String[] ValidFileList(String[] strFileList) throws Exception {
		Vector vtFileList = new Vector();
		int length = strFileList.length;
		int j = 0;
		for (int i = 0; i < length; i++) {
			File fl = new File(strFileList[i]);
			if (fl.isFile()) {
				vtFileList.addElement(strFileList[i]);
			}
		}
		length = vtFileList.size();
		String[] strFileListReturn = new String[length];
		for (int i = 0; i < length; i++) {
			strFileListReturn[i] = vtFileList.elementAt(i).toString();
		}
		return strFileListReturn;
	}

	////////////////////////////////////////////////////////
	public void processSession() throws Exception {
		beforeProcessSession();
		try {
			miTotalFile = 0;
			// List file and compute
			File fl = new File(mstrImportDir);
			FileFilter fft = new FileFilter() {
				public boolean accept(File f) {
					String strFileName = f.getName();
					boolean bl = false;
					bl = f.isFile();
					if (bl) {
						bl = WildcardFilter.match(mstrWildcard, strFileName, true);
					}
					return bl;
				}
			};
			File arrFileList[] = fl.listFiles(fft);
			int length = arrFileList.length;
			String strFileList[] = new String[length];
			for (int i = 0; i < length; i++) {
				strFileList[i] = arrFileList[i].getName();
			}
			if (strFileList != null && strFileList.length > 0) {
				Arrays.sort(strFileList);
				/////////////////
				int iFileCount = strFileList.length;
				for (int iFileIndex = 0; !mmgrMain.isServerLocked() && iFileIndex < iFileCount
						&& miThreadCommand != ThreadConstant.THREAD_STOP; iFileIndex++) {
					String strFileName = strFileList[iFileIndex];
					Vector vtFileName = new Vector();
					if (!mstrCompress.equals("N")) {
						vtFileName = unCompress(mstrImportDir, strFileName, mstrCompress, mstrImportDir);
						for (int i = 0; i < vtFileName.size(); i++) {
							beforeImportFile(vtFileName.elementAt(i).toString());
							bFileOK = importFile(vtFileName.elementAt(i).toString());
							afterImportFile(vtFileName.elementAt(i).toString());
							miTotalFile++;
						}
						FileUtil.deleteFile(mstrImportDir + "/" + strFileName);
					} else {
						beforeImportFile(strFileName);
						bFileOK = importFile(strFileName);
						afterImportFile(strFileName);
						miTotalFile++;
					}
				}
			}
		} catch (Exception e) {
			// Show exception to user
			logMonitor(e.getMessage());
			e.printStackTrace();
		}

		afterProcessSession();
	}

	public static Vector unCompress(String strInputPath, String strFileName, String strFileType, String strOuputPath)
			throws Exception {
		Vector vtFilename;
		if (strFileType.equalsIgnoreCase("zip")) {
			vtFilename = SmartZip.UnZip(strInputPath + strFileName, strOuputPath);
		} else if (strFileType.equalsIgnoreCase("z")) {
			String strOutName = strFileName.toLowerCase().replace(".z", "");
			SmartZip.GUnZip(strInputPath + strFileName, strOuputPath + strOutName);
			vtFilename = new Vector();
			vtFilename.addElement(strOutName);
		} else if (strFileType.equalsIgnoreCase("gzip")) {
			String strOutName = strFileName.toLowerCase().replace(".gz", "");
			SmartZip.GUnZip(strInputPath + strFileName, strOuputPath + strOutName);
			vtFilename = new Vector();
			vtFilename.addElement(strOutName);
		} else {
			vtFilename = new Vector();
			vtFilename.addElement(strFileName);
		}
		return vtFilename;
	}
}
