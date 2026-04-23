package vnp.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

import smartlib.thread.ThreadConstant;
import smartlib.util.DateUtil;
import smartlib.util.FileUtil;
import smartlib.util.StringUtil;
import smartlib.util.WildcardFilter;

import com.enterprisedt.net.ftp.FTPFile;
import java.text.*;
import com.enterprisedt.net.ftp.*;
import java.io.*;

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
public class FTPReceiverMultiHostByStamp extends FTPThreadMultiHostByStamp {
    protected int miListItemCount;
    protected Vector mvtFileList;
    protected Hashtable mprtDirectoryList;
    protected String mstrLocalFileFormat;
    protected String mstrBackupDir;
    protected String mstrBackupFileFormat;
    protected String mstrLastProcessFileStamp;
    protected String mstrMaxTimeGetLastFile;
    protected String mstrProcessDate;
    protected String mstrCurrScanDir;
    protected String mstrLastTimeScanFile;
    protected String mstrDirectBackupDir;
    protected String mstrStorageDir;
    protected String mstrLastFileStamp;
    protected boolean mbLastDir;
    protected String mstrNextProcessDate;
    protected boolean mblnChangeProcessDate;

    public void beforeListFile() throws Exception {

    }

    // ////////////////////////////////////////////////////////
    // Create Parser
    public void listfile() throws Exception {
        try {
            beforeListFile();

            // List file
            mprtDirectoryList = new Hashtable();
            mvtFileList = new Vector();
            listFile("");

            sortFileList();
            afterListFile();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // //////////////////////////////////////////////////////
    // List file
    protected void listFile(String strAdditionPath) throws Exception {
        // Get scandir
        if (mstrFTPStyle != null && mstrFTPStyle.length() > 0 &&
            !mstrFTPStyle.equals("Directly")) {
            mstrCurrScanDir = mstrFTPDir + mstrProcessDate + "/";
        } else {
            mstrCurrScanDir = mstrFTPDir;
        }
        // Check FTP Working Dir
        try {
            mftpMain.chdir(mstrCurrScanDir + strAdditionPath);
        } catch (Exception ex) {
            throw new Exception(mstrFTPName +
                    " :Could not change working directory to remote directory ("
                                + mstrCurrScanDir + strAdditionPath + ")");
        }
        // list Files
        FTPFile[] fflFileList = mftpMain.dirDetails(mstrCurrScanDir +
                strAdditionPath);

        if (fflFileList != null) {
            for (int iFileIndex = 0; iFileIndex < fflFileList.length;
                                  iFileIndex++) {
                if (fflFileList[iFileIndex].isDir() ||
                    fflFileList[iFileIndex].isLink()) {
                    // process subDirectory
                } else {
                    FTPFile ffl = createListItem(fflFileList[iFileIndex]);
                    if (ffl != null) {
                        mvtFileList.addElement(ffl);
                        mprtDirectoryList.put(ffl, strAdditionPath);
                    }
                }
            }
        }
    }

    // //////////////////////////////////////////////////////
    public FTPFile createListItem(FTPFile ffl) throws Exception {
        try {
            // Some ftp server not support ls [wildcard] -> need to check
            if (!WildcardFilter.match(mstrWildcard, ffl.getName())) {
                return null;
            } else {
                java.util.Date dtFileStamp = ffl.lastModified();
                java.util.Date dtLastStamp = DateUtil.toDate(
                        mstrLastProcessFileStamp, "dd/MM/yyyy HH:mm:ss.SSS");
                if (dtFileStamp.after(dtLastStamp) ||
                    dtFileStamp.equals(dtLastStamp)) {
                    return ffl;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return null;
    }

    // //////////////////////////////////////////////////////
    public void afterListFile() throws Exception {
        mbLastDir = true;
        mblnChangeProcessDate = false;
        if (mstrFTPStyle != null && !mstrFTPStyle.equals("") &&
            !mstrFTPStyle.equals("Directly")) {
            java.util.Date dt = DateUtil.toDate(mstrProcessDate, mstrDateFormat);
            if (mstrFTPStyle.equals("Daily")) {
                dt = DateUtil.addDay(dt, 1);
            } else if (mstrFTPStyle.equals("Monthly")) {
                dt = DateUtil.addMonth(dt, 1);
            } else if (mstrFTPStyle.equals("Yearly")) {
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
        if (mbLastDir) {
            java.util.Date dtCurrent = new java.util.Date();
            if (mvtFileList.size() > 0) {
                java.util.Date dtStampFirstFile = ((FTPFile) mvtFileList.
                        firstElement()).lastModified();
                java.util.Date dtStampLastFile = ((FTPFile) mvtFileList.
                                                  lastElement()).lastModified();
                java.util.Date dtLastScan;
                java.util.Date dtLastFileStamp = null;
                if (!mstrLastFileStamp.equals("")) {
                    dtLastFileStamp = DateUtil.toDate(mstrLastFileStamp,
                            "dd/MM/yyyy HH:mm:ss.SSS");
                }
                if (!mstrLastTimeScanFile.equals("")) {
                    dtLastScan = DateUtil.toDate(mstrLastTimeScanFile,
                                                 "dd/MM/yyyy HH:mm:ss.SSS");
                } else {
                    dtLastScan = dtCurrent;
                }
                // first filestamp != last FileStamp
                if (dtStampFirstFile.compareTo(dtStampLastFile) != 0) {
                    for (int i = mvtFileList.size() - 1; i >= 0; i--) {
                        FTPFile fl = (FTPFile) mvtFileList.elementAt(i);
                        if (fl.lastModified().compareTo(dtStampLastFile) == 0) {
                            mvtFileList.removeElementAt(i);
                        } else {
                            dtStampFirstFile = fl.lastModified();
                            break;
                        }
                    }
                    // //LastFileStampProcess
                    mvtCurrentParams.setElementAt(StringUtil.format(dtCurrent,
                            "dd/MM/yyyy HH:mm:ss.SSS"),
                                                  miLastTimeScanFileIndex); // LastTimeScanFile
                    mvtCurrentParams.setElementAt(StringUtil.format(
                            dtStampLastFile, "dd/MM/yyyy HH:mm:ss.SSS"),
                                                  miLastFileStampIndex); // LastFileStamp
                    setParameter("FTPSetting", mvtFTPSetting);
                } else {
                    if ((dtCurrent.getTime() - dtLastScan.getTime()) / 1000 <
                        Integer.parseInt(StringUtil.nvl(
                                mstrMaxTimeGetLastFile, "0"))) {
                        mvtFileList.removeAllElements();
                    } else {
                        if (mstrLastFileStamp.equals("") ||
                            dtLastFileStamp.compareTo(dtStampLastFile) != 0) {
                            mvtFileList.removeAllElements();
                            mvtCurrentParams.setElementAt(StringUtil.format(
                                    dtStampLastFile, "dd/MM/yyyy HH:mm:ss.SSS"),
                                    miLastFileStampIndex); // LastFileStamp
                            mvtCurrentParams.setElementAt(StringUtil.format(
                                    dtCurrent, "dd/MM/yyyy HH:mm:ss.SSS"),
                                    miLastTimeScanFileIndex); // LastTimeScanFile
                        } else {
                            // //LastFileStampProcess
                            mvtCurrentParams.setElementAt(StringUtil.format(
                                    dtCurrent, "dd/MM/yyyy HH:mm:ss.SSS"),
                                    miLastTimeScanFileIndex); // LastTimeScanFile
                        }
                    }
                    setParameter("FTPSetting", mvtFTPSetting);
                }
            } else {
                if (!mstrFTPStyle.equals("") && !mstrFTPStyle.equals("Directly")) {
                    mblnChangeProcessDate = true;
                }
                mvtCurrentParams.setElementAt(StringUtil.format(dtCurrent,
                        "dd/MM/yyyy HH:mm:ss.SSS"),
                                              miLastTimeScanFileIndex); // LastTimeScanFile
                setParameter("FTPSetting", mvtFTPSetting);
            }
        } else
        // Not LastDir
        {
            java.util.Date dtCurrent = new java.util.Date();
            mvtCurrentParams.setElementAt(StringUtil.format(dtCurrent,
                    "dd/MM/yyyy HH:mm:ss.SSS"), miLastTimeScanFileIndex); // LastTimeScanFile
            if (mvtFileList.size() > 0) {
                java.util.Date dtStampLastFile = ((FTPFile) mvtFileList.
                                                  lastElement()).lastModified();
                // //LastFileStampProcess
                mvtCurrentParams.setElementAt(StringUtil.format(dtStampLastFile,
                        "dd/MM/yyyy HH:mm:ss.SSS"),
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

    protected void sortFileList() throws Exception {
        Collections.sort(mvtFileList, new Comparator() {
            public int compare(Object obj1, Object obj2) {
                return ((FTPFile) obj1).lastModified().compareTo(((FTPFile)
                        obj2).lastModified());
            }
        });
    }

    // ///////////////////////////////////////////////////////////
    public void beforeProcessFileList() throws Exception {
        logMonitor("=========================================");
        logMonitor("Start Processing " + mstrFTPName);
    }

    // ///////////////////////////////////////////////////////////
    public void afterProcessFileList() throws Exception {

    }

    // ////////////////////////////////////////////////////////////
    public void getParam() throws Exception {
        try {
            mstrFTPName = loadString("FTPSetting.FTPName",
                                     (String)
                                     mvtCurrentParams.elementAt(miFTPNameIndex));
            mstrFTPDir = loadString("FTPSetting.FTPDir",
                                    (String)
                                    mvtCurrentParams.elementAt(miFTPDirIndex));
            if (!mstrFTPDir.endsWith("/") && !mstrFTPDir.endsWith("\\") &&
                !mstrFTPDir.equals("")) {
                mstrFTPDir += "/";
            }
            mstrLocalDir = loadDirectory("FTPSetting.LocalDir",
                                         (String)
                                         mvtCurrentParams.elementAt(miLocalDirIndex),
                                         true, true);
            mstrTempDir = loadDirectory("FTPSetting.TempDir",
                                        (String)
                                        mvtCurrentParams.elementAt(miTempDirIndex),
                                        true, true);
            mstrWildcard = loadString("FTPSetting.Wildcard",
                                      (String)
                                      mvtCurrentParams.elementAt(miWildcardIndex));
            mstrLocalFileFormat = (String) mvtCurrentParams.elementAt(
                    miLocalFileFormatIndex);
            mstrBackupDir = (String) mvtCurrentParams.elementAt(
                    miBackupDirIndex);
            if (!mstrBackupDir.endsWith("/") && !mstrBackupDir.endsWith("\\") &&
                !mstrBackupDir.equals("")) {
                mstrBackupDir += "/";
            }
            mstrBackupFileFormat = (String) mvtCurrentParams.elementAt(
                    miBackupFileFormatIndex);
            mstrLastProcessFileStamp = (String) mvtCurrentParams.elementAt(
                    miLastProcessFileStampIndex);
            if (mstrLastProcessFileStamp.equals("")) {
                mstrLastProcessFileStamp = "01/01/2000 01:01:01.000";
            }
            mstrMaxTimeGetLastFile = (String) mvtCurrentParams.elementAt(
                    miMaxTimeGetLastFileIndex);
            // > 1 minute, sau 1 phut server moi update timestamp cua file
            mstrProcessDate = (String) mvtCurrentParams.elementAt(
                    miProcessDateIndex);
            mstrLastTimeScanFile = StringUtil.nvl(mvtCurrentParams.elementAt(
                    miLastTimeScanFileIndex), "");
            mstrLastFileStamp = StringUtil.nvl(mvtCurrentParams.elementAt(
                    miLastFileStampIndex), "");
        } catch (Exception e) {
            throw e;
        }
    }

    // ///////////////////////////////////////////////////////////
    public void processFTP() throws Exception {
        try {
            getParam();
            listfile();
            // Receive list of file
            miListItemCount = mvtFileList.size();
            if (miListItemCount > 0) {
                beforeProcessFileList();
                for (int iIndex = 0;
                                  iIndex < miListItemCount &&
                                  miThreadCommand != ThreadConstant.THREAD_STOP;
                                  // if Stop Thread while getting list File, => miss file at the
                                  // bottom lits.
                                  // because afterListFile updated LastFileStamp,
                                  // LastFileStampProcess
                                  iIndex++) {
                    process(iIndex);
                }

                afterProcessFileList();
            }
            if (!mbLastDir && miThreadCommand != ThreadConstant.THREAD_STOP)
            // change process date
            {
                mvtCurrentParams.setElementAt(mstrNextProcessDate,
                                              miProcessDateIndex); // ProcessDate
                setParameter("FTPSetting", mvtFTPSetting);
            }
            if (mblnChangeProcessDate &&
                miThreadCommand != ThreadConstant.THREAD_STOP) {
                java.util.Date dt = DateUtil.toDate(mstrProcessDate,
                        mstrDateFormat);
                java.util.Date dtCurrent = new java.util.Date();
                dtCurrent = DateUtil.toDate(StringUtil.format(dtCurrent,
                        mstrDateFormat), mstrDateFormat);
                while (dt.before(dtCurrent)) {
                    if (mstrFTPStyle.equals("Daily")) {
                        dt = DateUtil.addDay(dt, 1);
                    } else if (mstrFTPStyle.equals("Monthly")) {
                        dt = DateUtil.addMonth(dt, 1);
                    } else if (mstrFTPStyle.equals("Yearly")) {
                        dt = DateUtil.addYear(dt, 1);
                    }
                    mstrNextProcessDate = StringUtil.format(dt, mstrDateFormat);
                    try {
                        mftpMain.chdir(mstrFTPDir + mstrNextProcessDate);
                        mvtCurrentParams.setElementAt(mstrNextProcessDate,
                                miProcessDateIndex); // ProcessDate
                        setParameter("FTPSetting", mvtFTPSetting);
                        break;
                    } catch (Exception ex1) {
                        ex1.printStackTrace();
                    }

                }
            }
            storeConfig();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // //////////////////////////////////////////////////////
    protected String validateFile(FTPFile ffl) throws Exception {
        return "";
    }

    // ////////////////////////////////////////////////////////
    public void process(int iFileIndex) throws Exception {
        // Get file
        FTPFile ffl = (FTPFile) mvtFileList.elementAt(iFileIndex);
        String strValidateResult = validateFile(ffl);
        boolean bResult = (strValidateResult == null ||
                           strValidateResult.length() == 0);
        if (!bResult) {
            logMonitor(strValidateResult);
        } else {
            getFile(ffl);
        }
    }

    // //////////////////////////////////////////////////////
    protected void beforeGetFile(FTPFile ffl, String strRemoteDir,
                                 String strHost, String strRemoteStyle) throws
            Exception {
        // Get storage dir
        mstrStorageDir = mstrLocalDir;
        if (mstrLocalStyle.equals("Daily")) {
            mstrStorageDir +=
                    StringUtil.format(new java.util.Date(), mstrDateFormat) +
                    "/";
        } else if (mstrLocalStyle.equals("Monthly")) {
            mstrStorageDir +=
                    StringUtil.format(new java.util.Date(), mstrDateFormat) +
                    "/";
        } else if (mstrLocalStyle.equals("Yearly")) {
            mstrStorageDir +=
                    StringUtil.format(new java.util.Date(), mstrDateFormat) +
                    "/";
        }
        FileUtil.forceFolderExist(mstrStorageDir);
        // Log start
        logMonitor("Start getting file " + ffl.getName() + " from ftp server");
    }

    // //////////////////////////////////////////////////////
    protected void getFile(FTPFile ffl) throws Exception {
        try {
            String strAdditionPath = StringUtil.nvl(mprtDirectoryList.get(ffl),
                    "");
            // Before get file event
            beforeGetFile(ffl, mstrCurrScanDir + strAdditionPath, mstrHost,
                          mstrFTPStyle);

            // Get file
            String strRemoteFilePath = mstrCurrScanDir + strAdditionPath +
                                       ffl.getName();
            FileOutputStream os = null;
            try {
                os = new FileOutputStream(mstrTempDir + ffl.getName());
                mftpMain.get(os, strRemoteFilePath);

            } catch (Exception e) {
                throw new Exception(mstrFTPName +
                                    " :Download file failed:\r\n\t\t" +
                                    e.getMessage());
            } finally {
                FileUtil.safeClose(os);
            }

            // Validate file size
            File fl = new File(mstrTempDir + ffl.getName());
            if (!fl.exists()) {
                throw new Exception(mstrFTPName +
                        " :Download file failed, file does not exist");
            }
            if (fl.length() != ffl.size()) {
                throw new Exception(mstrFTPName +
                        " :Getted file size does not equals to ftp file size");
            }

            // Make local file
            String strGettedFilePath = FileUtil.backup(mstrTempDir,
                    mstrLocalDir, ffl.getName(),
                    FileUtil.formatFileName(ffl.getName(), mstrLocalFileFormat),
                    mstrLocalStyle, strAdditionPath);
            try {
                // After get file event
                afterGetFile(ffl, mstrLocalDir);
            } catch (Exception e) {
                FileUtil.deleteFile(strGettedFilePath);
                throw e;
            }
        } catch (Exception e) {
            logMonitor("Error: " + e.getMessage());
            throw e;
        }
    }

    // //////////////////////////////////////////////////////
    protected void afterGetFile(FTPFile ffl, String strLocalDir) throws
            Exception {
        java.util.Date dtLastFileStampProcess = ffl.lastModified();
        mvtCurrentParams.setElementAt(StringUtil.format(dtLastFileStampProcess,
                "dd/MM/yyyy HH:mm:ss.SSS"),
                                      miLastProcessFileStampIndex); // LastFileStampProcess
        setParameter("FTPSetting", mvtFTPSetting);
        storeConfig();
        logMonitor("Getting file " + ffl.getName() + " completed (Size: " +
                   ffl.size() + " bytes, TimeStamp: "
                   +
                   StringUtil.format(ffl.lastModified(), "yyyy/MM/dd HH:mm:ss.SSS") +
                   ")");
    }

}
