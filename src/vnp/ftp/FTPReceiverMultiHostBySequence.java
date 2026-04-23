package vnp.ftp;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import smartlib.thread.ParameterType;
import smartlib.util.AppException;
import smartlib.util.DateUtil;
import smartlib.util.StringUtil;
import smartlib.util.WildcardFilter;

import com.enterprisedt.net.ftp.FTPFile;

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
public class FTPReceiverMultiHostBySequence extends FTPReceiverMultiHostByStamp
{
    protected int miFirstSeqPos;
    protected int miLastSeqPos;
    protected int miExpectedSeq;
    protected boolean mblnSkipMissingSeq;
    protected int miMaxSeqVal;
    protected int miMinSeqVal = 0;
    // Param Index
    protected int miFirstSeqPostIndex;
    protected int miLastSeqPostIndex;
    protected int miExpectedSeqIndex;
    protected int miSkipMissingSeqIndex;

    // ////////////////////////////////////////////////
    public Vector getFTPParameters()
    {
	Vector vtDefinition = new Vector();
	vtDefinition
		.addElement(createParameter("FTPName", "", ParameterType.PARAM_TEXTBOX_MAX, "256", "FTP Name", "0"));
	vtDefinition.addElement(createParameter("Host", "", ParameterType.PARAM_TEXTBOX_FILTER,
		ParameterType.FILTER_REGULAR, "Host Address to connect", "1"));
	vtDefinition.addElement(createParameter("Port", "", ParameterType.PARAM_TEXTBOX_MAX, "256", "FTP Port", "2"));
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
	vtDefinition.addElement(createParameter("Wildcard", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"Filler File Name", "11"));
	vtDefinition.addElement(createParameter("FirstSequencePost", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"First Sequence Post", "12"));
	vtDefinition.addElement(createParameter("ExpectedSequence", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"Expected Sequence", "13"));
	vtDefinition.addElement(createParameter("LastSequencePost", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"Last Sequence Post", "14"));

	Vector vtValue = new Vector();
	vtValue.addElement("YES");
	vtValue.addElement("NO");

	vtDefinition.addElement(createParameter("SkipMissingSequence", "", ParameterType.PARAM_COMBOBOX, vtValue,
		"Skip Missing Sequence", "15"));
	vtDefinition.addElement(createParameter("MaxTimeGetLastFile", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"Time to wait get LastFile, > 1 minutes, second unit", "16"));
	vtDefinition.addElement(createParameter("ProcessDate", "", ParameterType.PARAM_TEXTBOX_MAX, "256",
		"Start Directory to Get file, FTPStyle equal Daily or Monthly or Yearly", "17"));
	vtDefinition.addElement(createParameter("LastTimeScanFile", "", ParameterType.PARAM_READONLY, "256",
		"Last Time Scan File, default format = 'dd/MM/yyyy HH:mm:ss'", "18"));

	vtValue = new Vector();
	vtValue.addElement("Active");
	vtValue.addElement("DeActive");

	vtDefinition.addElement(createParameter("Status", "", ParameterType.PARAM_COMBOBOX, vtValue,
		"Status of Thread", "19"));

	return vtDefinition;
    }

    // Validate variables
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
	miWildcardIndex = 11;
	miFirstSeqPostIndex = 12;
	miExpectedSeqIndex = 13;
	miLastSeqPostIndex = 14;
	miSkipMissingSeqIndex = 15;
	miMaxTimeGetLastFileIndex = 16;
	miProcessDateIndex = 17;
	miLastTimeScanFileIndex = 18;
	miFTPStatus = 19;

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
	    miFirstSeqPos = loadUnsignedInteger("FTPSetting.FirstSequencePost",
		    (String) vtRow.elementAt(miFirstSeqPostIndex));
	    miLastSeqPos = loadUnsignedInteger("FTPSetting.LastSequencePost",
		    (String) vtRow.elementAt(miLastSeqPostIndex));
	    String strMaxSeq = "";
	    for (int iMaxSeq = 0; iMaxSeq < (miLastSeqPos - miFirstSeqPos); iMaxSeq++)
	    {
		strMaxSeq += "9";
	    }
	    miMaxSeqVal = Integer.parseInt(strMaxSeq);
	    miExpectedSeq = loadUnsignedInteger("FTPSetting.ExpectedSequence",
		    (String) vtRow.elementAt(miExpectedSeqIndex));
	    if (miLastSeqPos < miFirstSeqPos && miLastSeqPos != 0)
	    {
		throw new AppException("Value of 'LastSeqPos' can not be smaller than value of 'FirstSeqPos'",
			"FTPSequenceReceiver.validateParameter", "FirstSeqPos");
	    }
	    if (miMaxSeqVal < miMinSeqVal)
	    {
		throw new AppException("Value of 'MaxSeqVal' can not be smaller than value of 'MinSeqVal'",
			"FTPSequenceReceiver.validateParameter", "MinSeqVal");
	    }
	    if (miExpectedSeq < miMinSeqVal)
	    {
		throw new AppException("Value of 'ExpectedSeq' can not be smaller than value of 'MinSeqVal'",
			"FTPSequenceReceiver.validateParameter", "ExpectedSeq");
	    }
	    if (miExpectedSeq > miMaxSeqVal)
	    {
		throw new AppException("Value of 'ExpectedSeq' can not be greater than value of 'MaxSeqVal'",
			"FTPSequenceReceiver.validateParameter", "ExpectedSeq");
	    }
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

    // ////////////////////////////////////////////////////////
    public void beforeListFile() throws Exception
    {
	// mbMaxSeqExist = false;
	// mbMinSeqExist = false;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Get sequence from file name
    // Author: ThangPV
    // Modify DateTime: 19/02/2003
    // /////////////////////////////////////////////////////////////////////////
    protected int getFileSequence(FTPFile ffl) throws Exception
    {
	try
	{
	    String strSeq = "";
	    if (miLastSeqPos == 0)
	    {
		int iLastSeqPos = miFirstSeqPos;
		while (iLastSeqPos < ffl.getName().length() && ffl.getName().charAt(iLastSeqPos) >= '0'
			&& ffl.getName().charAt(iLastSeqPos) <= '9')
		{
		    iLastSeqPos++;
		}
		strSeq = ffl.getName().substring(miFirstSeqPos, iLastSeqPos);
	    }
	    else
	    {
		int iLastSeqPos = miLastSeqPos;
		if (iLastSeqPos > ffl.getName().length())
		{
		    iLastSeqPos = ffl.getName().length();
		}
		strSeq = ffl.getName().substring(miFirstSeqPos, iLastSeqPos);
	    }
	    return Integer.parseInt(strSeq);
	}
	catch (Exception e)
	{
	    throw new Exception(mstrFTPName + " :Can not get sequence of file '" + ffl.getName()
		    + "', please ensure parameter 'FirstSeqPos' and 'LastSeqPos' and 'Wildcard' are correct");
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
		int iFileSeq = getFileSequence(ffl);
		if (iFileSeq >= miExpectedSeq && iFileSeq >= miMinSeqVal && iFileSeq <= miMaxSeqVal)
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
	    // Check Style Date
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
            try {
               FTPFile[] listFile = mftpMain.dirDetails(mstrFTPDir +
                       mstrNextProcessDate);
               if (listFile != null && listFile.length > 0) {
                   mbLastDir = false;
               }
           } catch (Exception ex) {
               if (ex.getMessage().contains("Directory not found")){
                   mbLastDir = true;
               } else {
                   throw ex;
               }
           }
	}
	if (mbLastDir)
	{
	    java.util.Date dtCurrent = new java.util.Date();
	    if (mvtFileList.size() > 0)
	    {
		java.util.Date dtLastScan;
		if (!mstrLastTimeScanFile.equals(""))
		{
		    dtLastScan = DateUtil.toDate(mstrLastTimeScanFile, "dd/MM/yyyy HH:mm:ss");
		}
		else
		{
		    dtLastScan = dtCurrent;
		}
		if ((dtCurrent.getTime() - dtLastScan.getTime()) / 1000 < Integer.parseInt(StringUtil.nvl(
			mstrMaxTimeGetLastFile, "0")) || mvtFileList.size() > 1)
		{
		    mvtFileList.remove(mvtFileList.lastElement());
		    if (mvtFileList.size() > 0)
		    {
			mvtCurrentParams.setElementAt(StringUtil.format(dtCurrent, "dd/MM/yyyy HH:mm:ss"),
				miLastTimeScanFileIndex); // LastTimeScanFile
			setParameter("FTPSetting", mvtFTPSetting);
			// storeConfig();
		    }
		}
		else
		{
		    mvtCurrentParams.setElementAt(StringUtil.format(dtCurrent, "dd/MM/yyyy HH:mm:ss"),
			    miLastTimeScanFileIndex); // LastTimeScanFile
		    setParameter("FTPSetting", mvtFTPSetting);
		}
	    }
	    else
	    {
		mblnChangeProcessDate = true;
		mvtCurrentParams.setElementAt(StringUtil.format(dtCurrent, "dd/MM/yyyy HH:mm:ss"),
			miLastTimeScanFileIndex); // LastTimeScanFile
		setParameter("FTPSetting", mvtFTPSetting);
		// storeConfig();
	    }
	}
	else
	// Not LastDir
	{
	    java.util.Date dtCurrent = new java.util.Date();
	    if (mvtFileList.size() > 0)
	    {
		mvtCurrentParams.setElementAt(StringUtil.format(dtCurrent, "dd/MM/yyyy HH:mm:ss"),
			miLastTimeScanFileIndex); // LastTimeScanFile
	    }
	    setParameter("FTPSetting", mvtFTPSetting);
	    // storeConfig();
	}
    }


    // //////////////////////////////////////////////////////
    protected void sortFileList() throws Exception
    {
	Collections.sort(mvtFileList, new Comparator() {
	    public int compare(Object obj1, Object obj2)
	    {
		try
		{
		    int intFirstSeq = getFileSequence(((FTPFile) obj1));
		    int intSecondSeq = getFileSequence(((FTPFile) obj2));
		    if (intFirstSeq > intSecondSeq)
		    {
			return 1;
		    }
		    else if (intFirstSeq == intSecondSeq)
		    {
			return 0;
		    }
		    else
		    {
			return -1;
		    }
		}
		catch (Exception e)
		{
		    return -1;
		}
	    }
	});
    }

    // ////////////////////////////////////////////////////////////
    public void getParam() throws Exception
    {
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
	    mstrMaxTimeGetLastFile = (String) mvtCurrentParams.elementAt(miMaxTimeGetLastFileIndex); // >
												     // 1
												     // minute,
												     // sau
												     // 1
												     // phut
												     // server
												     // moi
												     // update
												     // timestamp
												     // cua
												     // file
	    mstrProcessDate = (String) mvtCurrentParams.elementAt(miProcessDateIndex);
	    mstrLastTimeScanFile = StringUtil.nvl(mvtCurrentParams.elementAt(miLastTimeScanFileIndex), "");
	    miFirstSeqPos = Integer.parseInt((String) mvtCurrentParams.elementAt(miFirstSeqPostIndex));
	    miLastSeqPos = Integer.parseInt((String) mvtCurrentParams.elementAt(miLastSeqPostIndex));
	    String strMaxSeq = "";
	    for (int iMaxSeq = 0; iMaxSeq < (miLastSeqPos - miFirstSeqPos); iMaxSeq++)
	    {
		strMaxSeq += "9";
	    }
	    miMaxSeqVal = Integer.parseInt(strMaxSeq);
	    miExpectedSeq = Integer.parseInt((String) mvtCurrentParams.elementAt(miExpectedSeqIndex));
	    if (mvtCurrentParams.elementAt(miSkipMissingSeqIndex).equals("YES"))
	    {
		mblnSkipMissingSeq = true;
	    }
	    else
	    {
		mblnSkipMissingSeq = false;
	    }

	    miPort = Integer.parseInt((String) mvtCurrentParams.elementAt(miPortIndex));

	}
	catch (Exception e)
	{
	    throw e;
	}
    }

    // //////////////////////////////////////////////////////
    protected String validateFile(FTPFile ffl) throws Exception
    {
	int iFileSeq = getFileSequence(ffl);
	if (iFileSeq != miExpectedSeq)
	{
	    if (mblnSkipMissingSeq)
	    {
		// Log file missingmiExpectedSeq
		String strLog = mstrFTPName + " :Missing sequence from " + String.valueOf(miExpectedSeq) + " to "
			+ String.valueOf(iFileSeq - 1);

		// Skip get these file
		miExpectedSeq = iFileSeq;
		mvtCurrentParams.setElementAt(String.valueOf(miExpectedSeq), miExpectedSeqIndex);
		setParameter("FTPSetting", mvtFTPSetting);
		storeConfig();

		// Change next session
		throw new Exception(strLog);
	    }
	    mvtCurrentParams.setElementAt(mstrProcessDate, miProcessDateIndex); // RollBack
										// ProcessDate
	    setParameter("FTPSetting", mvtFTPSetting);
	    storeConfig();
	    throw new Exception(mstrFTPName + " :Matching sequence " + iFileSeq + " when expected sequence is "
		    + miExpectedSeq);
	}
	return super.validateFile(ffl);
    }

    // /////////////////////////////////////////////////////////////////////////
    /**
     * Event raised when file missing
     *
     * @param fl
     *            File
     * @param iFileSeq
     *            int
     * @throws Exception
     */
    // /////////////////////////////////////////////////////////////////////////

    // //////////////////////////////////////////////////////
    protected void afterGetFile(FTPFile ffl, String strLocalDir) throws Exception
    {
	if (miExpectedSeq == miMaxSeqVal)
	{
	    miExpectedSeq = miMinSeqVal;
	}
	else
	{
	    miExpectedSeq++;
	}
	mvtCurrentParams.setElementAt(String.valueOf(miExpectedSeq), miExpectedSeqIndex);
	setParameter("FTPSetting", mvtFTPSetting);
	storeConfig();
	// Log completed(String strTotalRecords,String strSuccessRecords,String
	// strErrorRecords,String strStatus)
	// String strTotalRecords,String strSuccessRecords,String
	// strErrorRecords,String strStatus,java.util.Date dtFileStamp,long
	// lngFileSize
	// Status 1: Success, 0: fail, 2 Missing
	// logComplete("1", "1", "0", "1", ffl.lastModified(), ffl.size(),
	// strLocalDir);
	logMonitor("Getting file " + ffl.getName() + " completed (Size: " + ffl.size() + " bytes, TimeStamp: "
		+ StringUtil.format(ffl.lastModified(), "yyyy/MM/dd HH:mm:ss") + ")");
    }
}
