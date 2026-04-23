package vnp.util;

import java.io.*;
import java.util.*;
import vnp.thread.PortalThread;
import smartlib.util.AppException;
import smartlib.util.FileUtil;
import smartlib.util.WildcardFilter;
import smartlib.thread.ThreadConstant;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: TELSOFT</p>
 * @author DUNGNV
 * @version 1.0
 * Purpose : Base class for other threads
 */

public class ProcessFile extends PortalThread {
    ////////////////////////////////////////////////////////
    // Member variables
    ////////////////////////////////////////////////////////
    // Compute variables
    protected String mstrImportDir;
    protected String mstrBackupDir;
    protected String mstrTempDir;
    protected String mstrWildcard;
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
        mstrBackupType = loadMandatory("BackupType");
        mstrTempDir = loadDirectory("TempDir", true, true);
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
        vtReturn.addElement(createParameterDefinition("ImportDir", "",
                ParameterType.PARAM_TEXTBOX_MAX, "100"));
        vtReturn.addElement(createParameterDefinition("BackupDir", "",
                ParameterType.PARAM_TEXTBOX_MAX, "100"));
        Vector vtValue1 = new Vector();
        vtValue1.addElement("DIRECT");
        vtValue1.addElement("DAILY");
        vtReturn.addElement(createParameterDefinition("BackupType", "",
                ParameterType.PARAM_COMBOBOX, vtValue1, ""));
        vtReturn.addElement(createParameterDefinition("TempDir", "",
                ParameterType.PARAM_TEXTBOX_MAX, "100"));
        vtReturn.addElement(createParameterDefinition("Wildcard", "",
                ParameterType.PARAM_TEXTBOX_MAX, "100"));
        vtReturn.addElement(createParameterDefinition("TimeSleep", "",
                ParameterType.PARAM_TEXTBOX_MASK, "9999999990"));
        vtReturn.addElement(createParameterDefinition("NumRecordSleep", "",
                ParameterType.PARAM_TEXTBOX_MASK, "9999999990"));
        vtReturn.addAll(super.getParameterDefinition());
        return vtReturn;
    }

    ////////////////////////////////////////////////////////
    // before process file
    // Author : ThangPV
    // Created Date : 16/09/2004
    ////////////////////////////////////////////////////////
    public void beforeProcessFile(String strFileName) throws Exception {
        logMonitor("Start of processing file " + strFileName);
    }

    ////////////////////////////////////////////////////////
    // after process file
    // Author : ThangPV
    // Created Date : 16/09/2004
    ////////////////////////////////////////////////////////
    public void afterProcessFile(String strFileName) throws Exception {
        try {
            if (bFileOK) { //if file OK, rename to BackupDir
                if (!mstrBackupDir.equals("")) {
                    java.text.SimpleDateFormat fmt = new java.text.
                            SimpleDateFormat("yyyyMMdd");
                    String strCurrentDate = fmt.format(new java.util.Date());
                    String strBackupFilePath;
                    if (mstrBackupType.equals("DAILY")) {
                        FileUtil.forceFolderExist(mstrBackupDir +
                                                  strCurrentDate);
                        strBackupFilePath = mstrBackupDir + strCurrentDate +
                                            "/" + strFileName;
                    } else {
                        strBackupFilePath = mstrBackupDir + "/" + strFileName;
                    }

                    if (!FileUtil.renameFile(mstrImportDir + strFileName,
                                             strBackupFilePath)) {
                        String strMsg = "Cannot rename file " + mstrImportDir +
                                        strFileName + " to " + mstrBackupDir +
                                        strFileName;
                        throw new AppException(strMsg);
                    }
                } else {
                    FileUtil.deleteFile(mstrImportDir + strFileName);
                }
                try {
                    storeConfig();
                } catch (Exception e) {
                    throw new AppException(e.getMessage());
                }
            }

        } finally {
            logMonitor("End of processing file " + strFileName);
        }
    }

    ////////////////////////////////////////////////////////
    // process file
    // Author : ThangPV
    // Created Date : 16/09/2004
    ////////////////////////////////////////////////////////
    public void processFile(String strFileName) throws Exception {

    }

    ////////////////////////////////////////////////////////
    // before process session
    // Author : ThangPV
    // Created Date : 16/09/2004
    ////////////////////////////////////////////////////////
    public void beforeProcessSession() throws Exception {

    }

    ////////////////////////////////////////////////////////
    // after process session
    // Author : ThangPV
    // Created Date : 16/09/2004
    ////////////////////////////////////////////////////////
    public void afterProcessSession() throws Exception {

    }

    ///////////////////////////////////////////////////////////////////////////
    // validate file before call convert
    // Author: HiepTH
    // Modify DateTime: 09/07/2003
    ///////////////////////////////////////////////////////////////////////////
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
                for (int iFileIndex = 0;
                                      !mmgrMain.isServerLocked() &&
                                      iFileIndex < iFileCount &&
                                      miThreadCommand !=
                                      ThreadConstant.THREAD_STOP; iFileIndex++) {
                    String strFileName = strFileList[iFileIndex];
                    beforeProcessFile(strFileName);
                    bFileOK = true;
                    processFile(strFileName);
                    afterProcessFile(strFileName);
                    miTotalFile++;
                }
            }
        } catch (Exception e) {
            //Show exception to user
            logMonitor(e.getMessage());
            e.printStackTrace();
        }

        afterProcessSession();
    }
}
