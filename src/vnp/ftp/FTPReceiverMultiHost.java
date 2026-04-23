package vnp.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import smartlib.thread.ParameterType;
import smartlib.thread.ThreadConstant;
import smartlib.util.AppException;
import smartlib.util.DateUtil;
import smartlib.util.FileUtil;
import smartlib.util.StringUtil;
import smartlib.util.WildcardFilter;

import com.enterprisedt.net.ftp.FTPFile;

/**
 * <p>
 * Title:
 * </p>
 *
 * <p>
 * Description: FTP files, khai báo nhiều host trên cùng 1 tiến trình, lấy file
 * dựa vào timestamp của file
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: Telsoft Telecommunication Software and Services
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class FTPReceiverMultiHost extends FTPThreadMultiHostByStamp
{
    protected int miListItemCount;
    protected Vector mvtFileList;
    protected Vector mvtFileListYesterday;
    protected Hashtable mprtDirectoryList;
    protected Hashtable mprtDirectoryListYesterday;
    protected String mstrLocalFileFormat;
    protected String mstrBackupDir;
    protected String mstrBackupFileFormat;
    protected String mstrLastProcessFileStamp;
    protected String mstrMaxTimeGetLastFile;
    protected String mstrProcessDate;
    protected String mstrCurrScanDir, mstrYesterdayDir;
    protected String mstrLastTimeScanFile;
    protected String mstrDirectBackupDir;
    protected String mstrStorageDir;
    protected String mstrLastFileStamp;
    protected boolean mbLastDir;
    protected String mstrNextProcessDate;
    protected boolean mblnChangeProcessDate;
    protected Map mFileAlreadyGot, mFileAlreadyGotYesterday;
    protected int miFileAlreadyGot, miFileAlreadyGotYesterday, miTimeStopGetYesterdayFile, miHoursToAlert;
    protected String mstrFileAlreadyGot, mstrFileAlreadyGotYesterday, mstrTimeStopGetYesterdayFile, mstrHoursToAlert;
    protected boolean mbGetFileYesterday;

    public void beforeListFile() throws Exception
    {
	mFileAlreadyGot = new HashMap<String, String>();
	String strAlreadyGot[] = StringUtil.toStringArray(mstrFileAlreadyGot, ",");
	for (int i = 0; i < strAlreadyGot.length; i++)
	{
	    mFileAlreadyGot.put(strAlreadyGot[i], "1");
	}

	mFileAlreadyGotYesterday = new HashMap<String, String>();
	String strAlreadyGotYesterday[] = StringUtil.toStringArray(mstrFileAlreadyGotYesterday, ",");
	for (int i = 0; i < strAlreadyGotYesterday.length; i++)
	{
	    mFileAlreadyGotYesterday.put(strAlreadyGotYesterday[i], "1");
	}

    }

    // ////////////////////////////////////////////////////////
    // Create Parser
    public void listfile() throws Exception
    {
	try
	{
	    beforeListFile();
	    // List file
	    mprtDirectoryList = new Hashtable();
	    mprtDirectoryListYesterday = new Hashtable();
	    mvtFileList = new Vector();
	    mvtFileListYesterday = new Vector();
	    listFile("");
	    sortFileList();
	    if (mbGetFileYesterday)
	    {
		sortFileListYesterday();
	    }
	    afterListFile();
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    throw e;
	}
    }

    // //////////////////////////////////////////////////////
    // List file
    protected void listFile(String strAdditionPath) throws Exception
    {
	// Get scandir
	if (mstrFTPStyle != null && mstrFTPStyle.length() > 0 && !mstrFTPStyle.equals("Directly"))
	{
	    mstrCurrScanDir = mstrFTPDir + mstrProcessDate + "/";
	    java.util.Date dt = DateUtil.toDate(mstrProcessDate, mstrDateFormat);
	    if (mstrFTPStyle.equals("Daily"))
	    {
		dt = DateUtil.addDay(dt, -1);
	    }
	    String strYesterdayProcessDate = StringUtil.format(dt, mstrDateFormat);
	    mstrYesterdayDir = mstrFTPDir + strYesterdayProcessDate + "/";

	    java.util.Date dtCurrent = new java.util.Date();
	    java.util.Date dtProcessDate = DateUtil.toDate(mstrProcessDate, mstrDateFormat);
	    dtProcessDate = DateUtil.addHour(dtProcessDate, Integer.parseInt(mstrTimeStopGetYesterdayFile));

	    java.util.Date dtWarn = DateUtil.toDate(mstrLastProcessFileStamp, "dd/MM/yyyy HH:mm:ss");

	    dtWarn = DateUtil.addHour(dtWarn, Integer.parseInt(mstrHoursToAlert));
	    if (dtWarn.compareTo(dtCurrent) < 0)
	    {
		logMonitor("ALERT: More than " + mstrHoursToAlert
			+ " hour(s) had no new files. \n\t\t\t Last time get file is: " + mstrLastProcessFileStamp);
	    }
	    if (dtCurrent.compareTo(dtProcessDate) > 0)
	    {
		mbGetFileYesterday = false;
	    }
	    else
	    {
		mbGetFileYesterday = true;
	    }
	}
	else
	{
	    mstrCurrScanDir = mstrFTPDir;
	}

	// Check FTP Working Dir
	try
	{
	    mftpMain.chdir(mstrCurrScanDir + strAdditionPath);
	}
	catch (Exception ex)
	{
	    throw new Exception(mstrFTPName + " :Could not change working directory to remote directory ("
		    + mstrCurrScanDir + strAdditionPath + ")");
	}
	// list Files
	FTPFile[] fflFileList = mftpMain.dirDetails(mstrCurrScanDir + strAdditionPath);

	if (fflFileList != null)
	{
	    for (int iFileIndex = 0; iFileIndex < fflFileList.length; iFileIndex++)
	    {
		if (fflFileList[iFileIndex].isDir() || fflFileList[iFileIndex].isLink())
		{
		    // process subDirectory
		}
		else
		{
		    FTPFile ffl = createListItem(fflFileList[iFileIndex]);
		    if (ffl != null)
		    {
			mvtFileList.addElement(ffl);
			mprtDirectoryList.put(ffl, strAdditionPath);
		    }
		}
	    }
	}

	if (mbGetFileYesterday)
	{
	    FTPFile[] fflFileListYesterday = mftpMain.dirDetails(mstrYesterdayDir + strAdditionPath);

	    if (fflFileListYesterday != null)
	    {
		for (int iFileIndex = 0; iFileIndex < fflFileListYesterday.length; iFileIndex++)
		{
		    if (fflFileListYesterday[iFileIndex].isDir() || fflFileListYesterday[iFileIndex].isLink())
		    {
			// process subDirectory
		    }
		    else
		    {
			FTPFile ffl = createListItem(fflFileListYesterday[iFileIndex]);
			if (ffl != null)
			{
			    mvtFileListYesterday.addElement(ffl);
			    mprtDirectoryListYesterday.put(ffl, strAdditionPath);
			}
		    }
		}
	    }
	}
    }

    // //////////////////////////////////////////////////////
    public FTPFile createListItem(FTPFile ffl) throws Exception
    {
	try
	{
	    // Some ftp server not support ls [wildcard] -> need to check
	    if (!WildcardFilter.match(mstrWildcard, ffl.getName()))
	    {
		return null;
	    }
	    else
	    {
		String strFileName = ffl.getName();
		if (!StringUtil.nvl(mFileAlreadyGot.get(strFileName), "||||").equals("1"))
		{
		    return ffl;
		}
	    }
	}
	catch (Exception ex)
	{
	    ex.printStackTrace();
	    throw ex;
	}
	return null;
    }

    // //////////////////////////////////////////////////////
    public void afterListFile() throws Exception
    {
	mbLastDir = true;
	mblnChangeProcessDate = false;
	if (mstrFTPStyle != null && !mstrFTPStyle.equals("") && !mstrFTPStyle.equals("Directly"))
	{
	    java.util.Date dt = DateUtil.toDate(mstrProcessDate, mstrDateFormat);
	    if (mstrFTPStyle.equals("Daily"))
	    {
		dt = DateUtil.addDay(dt, 1);
	    }
	    else if (mstrFTPStyle.equals("Monthly"))
	    {
		dt = DateUtil.addMonth(dt, 1);
	    }
	    else if (mstrFTPStyle.equals("Yearly"))
	    {
		dt = DateUtil.addYear(dt, 1);
	    }
	    mstrNextProcessDate = StringUtil.format(dt, mstrDateFormat);
	    FTPFile[] listFile = mftpMain.dirDetails(mstrFTPDir + mstrNextProcessDate);
	    if (listFile != null && listFile.length > 0)
	    {
		mbLastDir = false;
	    }
	}
	if (mbLastDir)
	{
	    java.util.Date dtCurrent = new java.util.Date();
	    if (mvtFileList.size() > 0 || mvtFileListYesterday.size() > 0)
	    {
		java.util.Date dtStampFirstFile = null;
		java.util.Date dtStampLastFile = null;
		java.util.Date dtStampFirstFileYesterday = null;
		java.util.Date dtStampLastFileYesterday = null;
		if (mvtFileList.size() > 0)
		{
		    dtStampFirstFile = ((FTPFile) mvtFileList.firstElement()).lastModified();
		    dtStampLastFile = ((FTPFile) mvtFileList.lastElement()).lastModified();
		}
		if (mvtFileListYesterday.size() > 0)
		{
		    dtStampFirstFileYesterday = ((FTPFile) mvtFileListYesterday.firstElement()).lastModified();
		    dtStampLastFileYesterday = ((FTPFile) mvtFileListYesterday.lastElement()).lastModified();
		}
		java.util.Date dtLastScan;
		java.util.Date dtLastFileStamp = null;
		if (!mstrLastFileStamp.equals(""))
		{
		    dtLastFileStamp = DateUtil.toDate(mstrLastFileStamp, "dd/MM/yyyy HH:mm:ss");
		}
		if (!mstrLastTimeScanFile.equals(""))
		{
		    dtLastScan = DateUtil.toDate(mstrLastTimeScanFile, "dd/MM/yyyy HH:mm:ss");
		}
		else
		{
		    dtLastScan = dtCurrent;
		}
		// first filestamp != last FileStamp
		if (dtStampFirstFileYesterday != null && dtStampLastFileYesterday != null)
		    if (dtStampFirstFileYesterday.compareTo(dtStampLastFileYesterday) != 0)
		    {
			for (int i = mvtFileListYesterday.size() - 1; i >= 0; i--)
			{
			    FTPFile fl = (FTPFile) mvtFileListYesterday.elementAt(i);
			    if (StringUtil.nvl(mFileAlreadyGotYesterday.get(fl.getName()), "").equalsIgnoreCase("1"))
			    {
				mvtFileListYesterday.removeElementAt(i);
			    }
			}
		    }
		if (dtStampFirstFile != null && dtStampLastFile != null)
		    if (dtStampFirstFile.compareTo(dtStampLastFile) != 0)
		    {
			for (int i = mvtFileList.size() - 1; i >= 0; i--)
			{
			    FTPFile fl = (FTPFile) mvtFileList.elementAt(i);
			    if (StringUtil.nvl(mFileAlreadyGot.get(fl.getName()), "").equalsIgnoreCase("1"))
			    {
				mvtFileList.removeElementAt(i);
			    }
			}
			// LastFileStampProcess
			mvtCurrentParams.setElementAt(StringUtil.format(dtCurrent, "dd/MM/yyyy HH:mm:ss"),
				miLastTimeScanFileIndex); // LastTimeScanFile
			mvtCurrentParams.setElementAt(StringUtil.format(dtStampLastFile, "dd/MM/yyyy HH:mm:ss"),
				miLastFileStampIndex); // LastFileStamp
			setParameter("FTPSetting", mvtFTPSetting);
		    }
		    else
		    {
			if ((dtCurrent.getTime() - dtLastScan.getTime()) / 1000 < Integer.parseInt(StringUtil.nvl(
				mstrMaxTimeGetLastFile, "0")))
			{
			    mvtFileList.removeAllElements();
			}
			else
			{
			    if (mstrLastFileStamp.equals("")
				    || dtLastFileStamp.compareTo(dtStampLastFileYesterday) != 0)
			    {
				mvtFileListYesterday.removeAllElements();
			    }
			    if (mstrLastFileStamp.equals("") || dtLastFileStamp.compareTo(dtStampLastFile) != 0)
			    {
				mvtFileList.removeAllElements();
				mvtCurrentParams
					.setElementAt(StringUtil.format(dtStampLastFile, "dd/MM/yyyy HH:mm:ss"),
						miLastFileStampIndex); // LastFileStamp
				mvtCurrentParams.setElementAt(StringUtil.format(dtCurrent, "dd/MM/yyyy HH:mm:ss"),
					miLastTimeScanFileIndex); // LastTimeScanFile
			    }
			    else
			    {
				// //LastFileStampProcess
				mvtCurrentParams.setElementAt(StringUtil.format(dtCurrent, "dd/MM/yyyy HH:mm:ss"),
					miLastTimeScanFileIndex); // LastTimeScanFile
			    }
			}
			setParameter("FTPSetting", mvtFTPSetting);
		    }
	    }
	    else
	    {
		if (!mstrFTPStyle.equals("") && !mstrFTPStyle.equals("Directly"))
		{
		    mblnChangeProcessDate = true;
		}
	    }
	}
	else
	// Not LastDir
	{
	    java.util.Date dtCurrent = new java.util.Date();
	    mvtCurrentParams.setElementAt(StringUtil.format(dtCurrent, "dd/MM/yyyy HH:mm:ss"), miLastTimeScanFileIndex); // LastTimeScanFile
	    if (mvtFileList.size() > 0)
	    {
		java.util.Date dtStampLastFile = ((FTPFile) mvtFileList.lastElement()).lastModified();
		// //LastFileStampProcess
		mvtCurrentParams.setElementAt(StringUtil.format(dtStampLastFile, "dd/MM/yyyy HH:mm:ss"),
			miLastFileStampIndex); // LastFileStamp
	    }
	    setParameter("FTPSetting", mvtFTPSetting);
	}
    }

    /**
     * Sort file List
     *
     * @throws Exception
     */

    protected void sortFileList() throws Exception
    {
	Collections.sort(mvtFileList, new Comparator() {
	    public int compare(Object obj1, Object obj2)
	    {
		return ((FTPFile) obj1).lastModified().compareTo(((FTPFile) obj2).lastModified());
	    }
	});
    }

    /**
     * Sort file List
     *
     * @throws Exception
     */

    protected void sortFileListYesterday() throws Exception
    {
	Collections.sort(mvtFileListYesterday, new Comparator() {
	    public int compare(Object obj1, Object obj2)
	    {
		return ((FTPFile) obj1).lastModified().compareTo(((FTPFile) obj2).lastModified());
	    }
	});
    }

    // ///////////////////////////////////////////////////////////
    public void beforeProcessFileList() throws Exception
    {
	logMonitor("=========================================");
	logMonitor("Start Processing " + mstrFTPName);
    }

    // ///////////////////////////////////////////////////////////
    public void afterProcessFileList() throws Exception
    {

    }

    // ////////////////////////////////////////////////////////////
    public void getParam() throws Exception
    {
	miFTPNameIndex = 0;
	miHostIndex = 1;
	miPortIndex = 2;
	miUserIndex = 3;
	miPasswordIndex = 4;
	miFTPDirIndex = 5;
	miLocalDirIndex = 6;
	miLocalFileFormatIndex = 7;
	miBackupDirIndex = 8;
	miBackupFileFormatIndex = 9;
	miTempDirIndex = 10;
	miLastProcessFileStampIndex = 11;
	miWildcardIndex = 12;
	miMaxTimeGetLastFileIndex = 13;
	miProcessDateIndex = 14;
	miLastTimeScanFileIndex = 15;
	miLastFileStampIndex = 16;
	miFTPStatus = 17;
	miFileAlreadyGot = 18;
	miFileAlreadyGotYesterday = 19;
	miTimeStopGetYesterdayFile = 20;
	miHoursToAlert = 21;
	try
	{
	    mstrFTPName = loadString("FTPSetting.FTPName", (String) mvtCurrentParams.elementAt(miFTPNameIndex));
	    mstrFTPDir = loadString("FTPSetting.FTPDir", (String) mvtCurrentParams.elementAt(miFTPDirIndex));
	    if (!mstrFTPDir.endsWith("/") && !mstrFTPDir.endsWith("\\") && !mstrFTPDir.equals(""))
	    {
		mstrFTPDir += "/";
	    }
	    mstrLocalDir = loadDirectory("FTPSetting.LocalDir", (String) mvtCurrentParams.elementAt(miLocalDirIndex),
		    true, true);
	    mstrTempDir = loadDirectory("FTPSetting.TempDir", (String) mvtCurrentParams.elementAt(miTempDirIndex),
		    true, true);
	    mstrWildcard = loadString("FTPSetting.Wildcard", (String) mvtCurrentParams.elementAt(miWildcardIndex));
	    mstrLocalFileFormat = (String) mvtCurrentParams.elementAt(miLocalFileFormatIndex);
	    mstrBackupDir = (String) mvtCurrentParams.elementAt(miBackupDirIndex);
	    if (!mstrBackupDir.endsWith("/") && !mstrBackupDir.endsWith("\\") && !mstrBackupDir.equals(""))
	    {
		mstrBackupDir += "/";
	    }
	    mstrBackupFileFormat = (String) mvtCurrentParams.elementAt(miBackupFileFormatIndex);
	    mstrLastProcessFileStamp = (String) mvtCurrentParams.elementAt(miLastProcessFileStampIndex);
	    if (mstrLastProcessFileStamp.equals(""))
	    {
		mstrLastProcessFileStamp = "01/01/2000 01:01:01";
	    }
	    mstrMaxTimeGetLastFile = (String) mvtCurrentParams.elementAt(miMaxTimeGetLastFileIndex);
	    // > 1 minute, sau 1 phut server moi update timestamp cua file
	    mstrProcessDate = (String) mvtCurrentParams.elementAt(miProcessDateIndex);
	    mstrLastTimeScanFile = StringUtil.nvl(mvtCurrentParams.elementAt(miLastTimeScanFileIndex), "");
	    mstrLastFileStamp = StringUtil.nvl(mvtCurrentParams.elementAt(miLastFileStampIndex), "");
	    mstrFileAlreadyGot = StringUtil.nvl(mvtCurrentParams.elementAt(miFileAlreadyGot), "");
	    mstrFileAlreadyGotYesterday = StringUtil.nvl(mvtCurrentParams.elementAt(miFileAlreadyGotYesterday), "");
	    mstrTimeStopGetYesterdayFile = StringUtil.nvl(mvtCurrentParams.elementAt(miTimeStopGetYesterdayFile), "");
	    mstrHoursToAlert = StringUtil.nvl(mvtCurrentParams.elementAt(miHoursToAlert), "");
	}
	catch (Exception e)
	{
	    throw e;
	}
    }

    // ///////////////////////////////////////////////////////////
    public void processFTP() throws Exception
    {
	try
	{
	    getParam();
	    listfile();
	    // Receive list of file
	    miListItemCount = mvtFileList.size();
	    if (miListItemCount > 0)
	    {
		beforeProcessFileList();
		for (int iIndex = 0; iIndex < miListItemCount && miThreadCommand != ThreadConstant.THREAD_STOP;
		// if Stop Thread while getting list File, => miss file at the
		// bottom lits.
		// because afterListFile updated LastFileStamp,
		// LastFileStampProcess
		iIndex++)
		{
		    process(iIndex);
		}
		afterProcessFileList();
	    }

	    miListItemCount = mvtFileListYesterday.size();
	    if (miListItemCount > 0)
	    {
		beforeProcessFileList();
		for (int iIndex = 0; iIndex < miListItemCount && miThreadCommand != ThreadConstant.THREAD_STOP;
		// if Stop Thread while getting list File, => miss file at the
		// bottom lits.
		// because afterListFile updated LastFileStamp,
		// LastFileStampProcess
		iIndex++)
		{
		    processYesterday(iIndex);
		}
		afterProcessFileList();
	    }

	    if (!mbLastDir && miThreadCommand != ThreadConstant.THREAD_STOP)
	    // change process date
	    {
		mvtCurrentParams.setElementAt(mstrNextProcessDate, miProcessDateIndex); // ProcessDate
		mvtCurrentParams.setElementAt("", miFileAlreadyGot);
		mvtCurrentParams.setElementAt(mstrFileAlreadyGot, miFileAlreadyGotYesterday);
		mFileAlreadyGotYesterday = mFileAlreadyGot;
		mFileAlreadyGot = new HashMap<String, String>();
		mbGetFileYesterday = true;
		setParameter("FTPSetting", mvtFTPSetting);
	    }
	    if (mblnChangeProcessDate && miThreadCommand != ThreadConstant.THREAD_STOP)
	    {
		java.util.Date dt = DateUtil.toDate(mstrProcessDate, mstrDateFormat);
		java.util.Date dtCurrent = new java.util.Date();
		dtCurrent = DateUtil.toDate(StringUtil.format(dtCurrent, mstrDateFormat), mstrDateFormat);
		while (dt.before(dtCurrent))
		{
		    if (mstrFTPStyle.equals("Daily"))
		    {
			dt = DateUtil.addDay(dt, 1);
		    }
		    else if (mstrFTPStyle.equals("Monthly"))
		    {
			dt = DateUtil.addMonth(dt, 1);
		    }
		    else if (mstrFTPStyle.equals("Yearly"))
		    {
			dt = DateUtil.addYear(dt, 1);
		    }
		    mstrNextProcessDate = StringUtil.format(dt, mstrDateFormat);
		    try
		    {
			mftpMain.chdir(mstrFTPDir + mstrNextProcessDate);
			mvtCurrentParams.setElementAt(mstrNextProcessDate, miProcessDateIndex); // ProcessDate
			// Reset FileAlreadyGotList
			setParameter("FTPSetting", mvtFTPSetting);
			break;
		    }
		    catch (Exception ex1)
		    {
			ex1.printStackTrace();
		    }
		}
	    }
	    storeConfig();
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    throw e;
	}
    }

    // //////////////////////////////////////////////////////
    protected String validateFile(FTPFile ffl) throws Exception
    {
	return "";
    }

    // ////////////////////////////////////////////////////////
    public void processYesterday(int iFileIndex) throws Exception
    {
	// Get file
	FTPFile ffl = (FTPFile) mvtFileListYesterday.elementAt(iFileIndex);
	String strValidateResult = validateFile(ffl);
	boolean bResult = (strValidateResult == null || strValidateResult.length() == 0);
	if (!bResult)
	{
	    logMonitor(strValidateResult);
	}
	else
	{
	    if (StringUtil.nvl(mFileAlreadyGotYesterday.get(ffl.getName()), "").equalsIgnoreCase(""))
	    {
		getFileYesterday(ffl);
		mFileAlreadyGotYesterday.put(ffl.getName(), "1");
		mstrFileAlreadyGotYesterday += ffl.getName() + ",";
		mvtCurrentParams.setElementAt(mstrFileAlreadyGotYesterday, miFileAlreadyGotYesterday); // ProcessDate
		setParameter("FTPSetting", mvtFTPSetting);
	    }
	}
    }

    // ////////////////////////////////////////////////////////
    public void process(int iFileIndex) throws Exception
    {
	// Get file
	FTPFile ffl = (FTPFile) mvtFileList.elementAt(iFileIndex);
	String strValidateResult = validateFile(ffl);
	boolean bResult = (strValidateResult == null || strValidateResult.length() == 0);
	if (!bResult)
	{
	    logMonitor(strValidateResult);
	}
	else
	{
	    if (StringUtil.nvl(mFileAlreadyGot.get(ffl.getName()), "").equalsIgnoreCase(""))
	    {
		getFile(ffl);
		mFileAlreadyGot.put(ffl.getName(), "1");
		mstrFileAlreadyGot += ffl.getName() + ",";
		mvtCurrentParams.setElementAt(mstrFileAlreadyGot, miFileAlreadyGot); // ProcessDate
		setParameter("FTPSetting", mvtFTPSetting);
	    }
	}
    }

    // //////////////////////////////////////////////////////
    protected void beforeGetFile(FTPFile ffl, String strRemoteDir, String strHost, String strRemoteStyle)
	    throws Exception
    {
	// Get storage dir
	mstrStorageDir = mstrLocalDir;
	if (mstrLocalStyle.equals("Daily"))
	{
	    mstrStorageDir += StringUtil.format(new java.util.Date(), mstrDateFormat) + "/";
	}
	else if (mstrLocalStyle.equals("Monthly"))
	{
	    mstrStorageDir += StringUtil.format(new java.util.Date(), mstrDateFormat) + "/";
	}
	else if (mstrLocalStyle.equals("Yearly"))
	{
	    mstrStorageDir += StringUtil.format(new java.util.Date(), mstrDateFormat) + "/";
	}
	FileUtil.forceFolderExist(mstrStorageDir);
	// Log start
	logMonitor("Start getting file " + ffl.getName() + " from ftp server");
    }

    // //////////////////////////////////////////////////////
    protected void getFile(FTPFile ffl) throws Exception
    {
	try
	{
	    String strAdditionPath = StringUtil.nvl(mprtDirectoryList.get(ffl), "");
	    // Before get file event
	    beforeGetFile(ffl, mstrCurrScanDir + strAdditionPath, mstrHost, mstrFTPStyle);

	    // Get file
	    String strRemoteFilePath = mstrCurrScanDir + strAdditionPath + ffl.getName();
	    FileOutputStream os = null;
	    try
	    {
		os = new FileOutputStream(mstrTempDir + ffl.getName());
		mftpMain.get(os, strRemoteFilePath);

	    }
	    catch (Exception e)
	    {
		throw new Exception(mstrFTPName + " :Download file failed:\r\n\t\t" + e.getMessage());
	    }
	    finally
	    {
		FileUtil.safeClose(os);
	    }

	    // Validate file size
	    File fl = new File(mstrTempDir + ffl.getName());
	    if (!fl.exists())
	    {
		throw new Exception(mstrFTPName + " :Download file failed, file does not exist");
	    }
	    if (fl.length() != ffl.size())
	    {
		throw new Exception(mstrFTPName + " :Getted file size does not equals to ftp file size");
	    }

	    // Make local file
	    String strGettedFilePath = FileUtil.backup(mstrTempDir, mstrLocalDir, ffl.getName(),
		    FileUtil.formatFileName(ffl.getName(), mstrLocalFileFormat), mstrLocalStyle, strAdditionPath);
	    try
	    {
		// After get file event
		afterGetFile(ffl, mstrLocalDir);
	    }
	    catch (Exception e)
	    {
		FileUtil.deleteFile(strGettedFilePath);
		throw e;
	    }
	}
	catch (Exception e)
	{
	    logMonitor("Error: " + e.getMessage());
	    throw e;
	}
    }

    protected void getFileYesterday(FTPFile ffl) throws Exception
    {
	try
	{
	    String strAdditionPath = StringUtil.nvl(mprtDirectoryListYesterday.get(ffl), "");
	    // Before get file event
	    beforeGetFile(ffl, mstrYesterdayDir + strAdditionPath, mstrHost, mstrFTPStyle);

	    // Get file
	    String strRemoteFilePath = mstrYesterdayDir + strAdditionPath + ffl.getName();
	    FileOutputStream os = null;
	    try
	    {
		os = new FileOutputStream(mstrTempDir + ffl.getName());
		mftpMain.get(os, strRemoteFilePath);

	    }
	    catch (Exception e)
	    {
		throw new Exception(mstrFTPName + " :Download file failed:\r\n\t\t" + e.getMessage());
	    }
	    finally
	    {
		FileUtil.safeClose(os);
	    }

	    // Validate file size
	    File fl = new File(mstrTempDir + ffl.getName());
	    if (!fl.exists())
	    {
		throw new Exception(mstrFTPName + " :Download file failed, file does not exist");
	    }
	    if (fl.length() != ffl.size())
	    {
		throw new Exception(mstrFTPName + " :Getted file size does not equals to ftp file size");
	    }

	    // Make local file
	    String strGettedFilePath = FileUtil.backup(mstrTempDir, mstrLocalDir, ffl.getName(),
		    FileUtil.formatFileName(ffl.getName(), mstrLocalFileFormat), mstrLocalStyle, strAdditionPath);
	    try
	    {
		// After get file event
		afterGetFile(ffl, mstrLocalDir);
	    }
	    catch (Exception e)
	    {
		FileUtil.deleteFile(strGettedFilePath);
		throw e;
	    }
	}
	catch (Exception e)
	{
	    logMonitor("Error: " + e.getMessage());
	    throw e;
	}
    }

    // //////////////////////////////////////////////////////
    protected void afterGetFile(FTPFile ffl, String strLocalDir) throws Exception
    {
	java.util.Date dtLastFileStampProcess = ffl.lastModified();
	mvtCurrentParams.setElementAt(StringUtil.format(dtLastFileStampProcess, "dd/MM/yyyy HH:mm:ss"),
		miLastFileStampIndex); // LastFileStamp
	mvtCurrentParams.setElementAt(StringUtil.format(new java.util.Date(), "dd/MM/yyyy HH:mm:ss"),
		miLastProcessFileStampIndex); // LastFileStampProcess

	setParameter("FTPSetting", mvtFTPSetting);
	storeConfig();
	logMonitor("Getting file " + ffl.getName() + " completed (Size: " + ffl.size() + " bytes, TimeStamp: "
		+ StringUtil.format(ffl.lastModified(), "yyyy/MM/dd HH:mm:ss") + ")");
    }

    public Vector getFTPParameters()
    {
	Vector vtDefinition = new Vector();
	vtDefinition
		.addElement(createParameter("FTPName", "", ParameterType.PARAM_TEXTBOX_MAX, "256", "FTP Name", "0"));
	vtDefinition.addElement(createParameter("Host", "", ParameterType.PARAM_TEXTBOX_FILTER,
		ParameterType.FILTER_REGULAR, "Host Address to connect", "1"));
	vtDefinition.addElement(createParameter("Port", "", ParameterType.PARAM_TEXTBOX_MASK, "99990", "Port FTP = 21",
		"2"));
	vtDefinition.addElement(createParameter("User", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"UserName to connect", "3"));
	vtDefinition.addElement(createParameter("Password", "", ParameterType.PARAM_PASSWORD, "100",
		"Password to connect", "4"));
	vtDefinition.addElement(createParameter("FTPDir", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"Directory store file in server", "5"));
	vtDefinition.addElement(createParameter("LocalDir", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"Directory store file in Local", "6"));
	vtDefinition.addElement(createParameter("LocalFileFormat", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"Format local file name.\r\n Can use $FileName,$BaseFileName,$FileExtension as parameter", "7"));
	vtDefinition.addElement(createParameter("BackupDir", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"Directory store filebackup in server", "8"));
	vtDefinition.addElement(createParameter("BackupFileFormat", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"Format local file name.\r\n Can use $FileName,$BaseFileName,$FileExtension as parameter", "9"));
	vtDefinition.addElement(createParameter("TempDir", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"Temp Directory in Local", "10"));
	vtDefinition.addElement(createParameter("LastProcessFileStamp", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"CreateDateTime of Last File processed, default format = 'dd/MM/yyyy HH:mm:ss'", "11"));
	vtDefinition.addElement(createParameter("Wildcard", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"Filler File Name", "12"));
	vtDefinition.addElement(createParameter("MaxTimeGetLastFile", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"Time to wait get LastFile, > 1 minutes, second unit", "13"));
	vtDefinition.addElement(createParameter("ProcessDate", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"Start Directory to Get file, FTPStyle equal Daily or Monthly or Yearly", "14"));
	vtDefinition.addElement(createParameter("LastTimeScanFile", "", ParameterType.PARAM_READONLY, "256",
		"Last Time Scan File, default format = 'dd/MM/yyyy HH:mm:ss'", "15"));
	vtDefinition.addElement(createParameter("LastFileStamp", "", ParameterType.PARAM_READONLY, "256",
		"Last Time Scan File, default format = 'dd/MM/yyyy HH:mm:ss'", "16"));
	Vector vtValue = new Vector();
	vtValue.addElement("Active");
	vtValue.addElement("DeActive");
	vtDefinition.addElement(createParameter("Status", "", ParameterType.PARAM_COMBOBOX, vtValue,
		"Status of Thread", "17"));
	vtDefinition.addElement(createParameter("FileAlreadyGot", "", ParameterType.PARAM_READONLY, "999999",
		"ListFileAlreadGot'", "18"));
	vtDefinition.addElement(createParameter("FileAlreadyGotYesterday", "", ParameterType.PARAM_READONLY, "999999",
		"ListFileAlreadGotYesterday'", "19"));
	vtDefinition.addElement(createParameter("TimeStopGetYesterdayFile", "", ParameterType.PARAM_TEXTBOX_MAX, "10",
		"TimeStopGetYesterdayFile'", "20"));
	vtDefinition.addElement(createParameter("HoursToWarn", "", ParameterType.PARAM_TEXTBOX_MAX, "10",
		"Hours to warn when no new file to get'", "21"));
	return vtDefinition;
    }

    public void validateParameter() throws Exception
    {
	miFTPNameIndex = 0;
	miHostIndex = 1;
	miPortIndex = 2;
	miUserIndex = 3;
	miPasswordIndex = 4;
	miFTPDirIndex = 5;
	miLocalDirIndex = 6;
	miLocalFileFormatIndex = 7;
	miBackupDirIndex = 8;
	miBackupFileFormatIndex = 9;
	miTempDirIndex = 10;
	miLastProcessFileStampIndex = 11;
	miWildcardIndex = 12;
	miMaxTimeGetLastFileIndex = 13;
	miProcessDateIndex = 14;
	miLastTimeScanFileIndex = 15;
	miLastFileStampIndex = 16;
	miFTPStatus = 17;
	miFileAlreadyGot = 18;
	miFileAlreadyGotYesterday = 19;
	miTimeStopGetYesterdayFile = 20;
	String strProcessDate;
	for (int i = 0; i < mvtFTPSetting.size(); i++)
	{
	    Vector vtRow = (Vector) mvtFTPSetting.elementAt(i);
	    if (vtRow.size() < miFTPStatus)
	    {
		for (int j = vtRow.size(); j < miFTPStatus; j++)
		{
		    vtRow.addElement("");
		}
	    }
	    miPort = loadInteger("FTPSetting.Port", (String) vtRow.elementAt(miPortIndex));
	    mstrFTPName = loadString("FTPSetting.FTPName", (String) vtRow.elementAt(miFTPNameIndex));
	    mstrHost = loadString("FTPSetting.Host", (String) vtRow.elementAt(miHostIndex));
	    mstrUser = loadString("FTPSetting.User", (String) vtRow.elementAt(miUserIndex));
	    mstrPassword = loadString("FTPSetting.Password", (String) vtRow.elementAt(miPasswordIndex));
	    mstrFTPDir = loadString("FTPSetting.FTPDir", (String) vtRow.elementAt(miFTPDirIndex));
	    mstrLocalDir = loadDirectory("FTPSetting.LocalDir", (String) vtRow.elementAt(miLocalDirIndex), true, true);
	    mstrTempDir = loadDirectory("FTPSetting.TempDir", (String) vtRow.elementAt(miTempDirIndex), true, true);
	    mstrWildcard = loadString("FTPSetting.Wildcard", (String) vtRow.elementAt(miWildcardIndex));
	    strProcessDate = StringUtil.nvl((String) vtRow.elementAt(miProcessDateIndex), "");
	    mstrThreadStatus = loadString("FTPSetting.Status", (String) vtRow.elementAt(miFTPStatus));

	    if (mstrFTPStyle != null && mstrFTPStyle.length() > 0 && !mstrFTPStyle.equals("Directly"))
	    {
		if (mstrDateFormat == null || mstrDateFormat.length() == 0)
		{
		    throw new AppException("DateFormat cannot be null when FTPStyle='" + mstrFTPStyle + "'",
			    "FTPReceiver.validateParameter", "DateFormat");
		}
		if (mstrFTPStyle.equals("Daily") && mstrDateFormat.indexOf("dd") < 0)
		{
		    throw new AppException("DateFormat must contain 'dd' when FTPStyle='" + mstrFTPStyle + "'",
			    "FTPReceiver.validateParameter", "DateFormat");
		}
		else if (mstrFTPStyle.equals("Monthly") && mstrDateFormat.indexOf("MM") < 0)
		{
		    throw new AppException("DateFormat must contain 'MM' when FTPStyle='" + mstrFTPStyle + "'",
			    "FTPReceiver.validateParameter", "DateFormat");
		}
		else if (mstrFTPStyle.equals("Yearly") && mstrDateFormat.indexOf("yyyy") < 0)
		{
		    throw new AppException("DateFormat must contain 'yyyy' when FTPStyle='" + mstrFTPStyle + "'",
			    "FTPReceiver.validateParameter", "DateFormat");
		}

		if (strProcessDate == null || strProcessDate.length() == 0)
		{
		    throw new AppException(
			    "FTPSetting.ProcessDate cannot be null when FTPStyle='" + mstrFTPStyle + "'",
			    "FTPReceiver.validateParameter", "ProcessDate");
		}
		if (!DateUtil.isDate(strProcessDate, mstrDateFormat))
		{
		    throw new AppException("FTPSetting.ProcessDate does not match DateFormat",
			    "FTPReceiver.validateParameter", "ProcessDate");
		}
	    }
	}

    }
}
