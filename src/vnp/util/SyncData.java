package vnp.util;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

import smartlib.database.Database;
import smartlib.util.StringUtil;
import vnp.util.TextFileWriter;

/**
 * <p>Title: He thong doi soat so lieu</p>
 *
 * <p>Description: He thong doi soat so lieu thue bao tra truoc</p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: Billing Center - Vinaphone</p>
 *
 * @author Nguyen Ngoc Tuan
 * @version 1.0
 */

public class SyncData {
    public String mstrImportDir = "";
    public String mstrErrorDir = "";
    public Connection mcon_Target;
    public int mintBatchMode = 0;
    public String mstrInsertSQL = "";
    public String mstrInsertFields = "";
    public String mstrUpdateSQL = "";
    public String mstrUpdateFields = "";

    public boolean mblnFirst = true;

    public int miBatchSize = 5000;
    public int miOrgBatchSize = 5000;
    public int miInsertRecords;
    public int miUpdateRecords;
    public int miErrorRecords;
    public int miTotalRecord;
    public CSVFile csvSource = new CSVFile();
    public String mstrDelimiter = ";";
    protected int[] miInsertFields;
    protected int[] miUpdateFields;
    protected PreparedStatement mstmt_Insert;
    protected PreparedStatement mstmt_Update;
    private Vector vtData = new Vector();

    protected TextFileWriter mErrorFile = new TextFileWriter();

    public String mstrFileIDField = "";
    public String mstrFileID = "";
    public String mstrInputHeader = "";

    ////////////////////////////////////////////////////////
    public void prepareDataSource(String strFileName) throws Exception {
        this.csvSource = new CSVFile();
        this.csvSource.setDelimited(this.mstrDelimiter);
        if ((this.mstrInputHeader != null) && (!(this.mstrInputHeader.equals("")))) {
            this.csvSource.setHeader(this.mstrInputHeader);
        }
        this.csvSource.openCSVFile(strFileName, 1048576);
        if ((this.csvSource.marrHeaders != null) &&
            (this.csvSource.marrHeaders.size() != 0)) {
            if (this.mintBatchMode == 0) {
                Vector vtInsertField = StringUtil.toStringVector(this.
                        mstrInsertFields);
                this.miInsertFields = new int[vtInsertField.size()];
                for (int i = 0; i < this.miInsertFields.length; ++i) {
                    this.miInsertFields[i] = this.csvSource.findColumn((String)
                            vtInsertField.elementAt(i));
                    if (this.miInsertFields[i] < 0) {
                        throw new Exception("Field " +
                                            ((String) vtInsertField.elementAt(i)) +
                                            " not found in select statement");
                    }
                }
            }

            Vector vtUpdateField = StringUtil.toStringVector(this.
                    mstrUpdateFields);
            this.miUpdateFields = new int[vtUpdateField.size()];
            for (int i = 0; i < this.miUpdateFields.length; ++i) {
                this.miUpdateFields[i] = this.csvSource.findColumn((String)
                        vtUpdateField.elementAt(i));

                if (this.miUpdateFields[i] < 0) {
                    throw new Exception("Field " +
                                        ((String) vtUpdateField.elementAt(i)) +
                                        " not found in insert statement");
                }
            }
        }
    }

    ////////////////////////////////////////////////////////
    public void releaseDataSource() throws Exception {
        this.csvSource.safeCloseCSVFile();
    }

    ////////////////////////////////////////////////////////
    public void prepareDataTarget() throws Exception {
        this.mstmt_Insert = this.mcon_Target.prepareStatement(this.
                mstrInsertSQL);
        this.mstmt_Update = this.mcon_Target.prepareStatement(this.
                mstrUpdateSQL);
    }

    ////////////////////////////////////////////////////////
    public void releaseDataTarget() {
        Database.closeObject(this.mstmt_Update);
        Database.closeObject(this.mstmt_Insert);
    }

    ////////////////////////////////////////////////////////
    public void beforeImport(String strFileName) throws Exception {
        this.vtData.clear();
        this.miTotalRecord = 0;
        this.miInsertRecords = 0;
        this.miErrorRecords = 0;
        this.miUpdateRecords = 0;
        this.miOrgBatchSize = this.miBatchSize;

        prepareDataSource(strFileName);
        prepareDataTarget();

        this.mErrorFile.openFile(this.mstrErrorDir + getFileName(strFileName),
                                 102400);
        this.mErrorFile.addText(this.csvSource.mstrHeader + ";ERROR");
        this.mblnFirst = true;
    }

    ////////////////////////////////////////////////////////
    private String getFileName(String strFullFileName) throws Exception {
        String strReturn = strFullFileName;
        int iIndex = strFullFileName.lastIndexOf("/");
        if (iIndex < 0) {
            iIndex = strFullFileName.lastIndexOf("\\");
        }
        if (iIndex > 0) {
            return strFullFileName.substring(iIndex + 1);
        }
        return strReturn;
    }

    ////////////////////////////////////////////////////////
    public void afterImport(String strFileName) throws Exception {
        try {
            if (this.miErrorRecords > 0) {
                this.mErrorFile.closeFile();
            } else {
                this.mErrorFile.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();

            this.mErrorFile.clear();
            throw e;
        }
    }

    ////////////////////////////////////////////////////////
    public void logErrorRecord(String strError,
                               Vector vtError) throws Exception {
        StringBuffer strResult = new StringBuffer();
        for (int iIndex = 0; iIndex < vtError.size(); ++iIndex) {
            strResult.append((String) vtError.elementAt(iIndex));
            strResult.append(";");
        }
        strResult.append(strError);
        this.mErrorFile.addText(strResult.toString());
        this.miErrorRecords += 1;
    }

    ////////////////////////////////////////////////////////
    public String bindValueInsertSQL(String strInsertSQL,
                                     String strValue) {
        String strSQL = strInsertSQL;
        strSQL = StringUtil.replaceAll(strSQL, "?", "'" + strValue + "'", 1);
        return strSQL;
    }

    ////////////////////////////////////////////////////////
    public void runInsertBatch() throws Exception {
        String strError = "";
        int intFirst = 0;
        int iUpdateCount = 0;

        int intDataSize = this.vtData.size();
        String strInsertSQL = this.mstrInsertSQL;
        while (intFirst < intDataSize) {
            for (int intIndex = intFirst; intIndex < intDataSize; ++intIndex) {
                Vector vtRowTem = (Vector)this.vtData.elementAt(intIndex);
                for (int i = 0; i < this.miInsertFields.length; ++i) {
                    this.mstmt_Insert.setString(i + 1,
                                                (String) vtRowTem.
                                                elementAt(this.miInsertFields[i]));
                    strInsertSQL = bindValueInsertSQL(strInsertSQL,
                            (String) vtRowTem.elementAt(this.miInsertFields[i]));
                }
                this.mstmt_Insert.addBatch();
            }
            try {
                this.mstmt_Insert.executeBatch();
                this.miBatchSize = this.miOrgBatchSize;
            } catch (BatchUpdateException e) {
                strError = StringUtil.nvl(e.getMessage().trim(), "");
            } catch (SQLException e) {
                strError = StringUtil.nvl(e.getMessage().trim(), "");
            }
            if ((strError != null) && (strError.startsWith("ORA-0168"))) {
                throw new Exception(strError);
            }
            iUpdateCount = this.mstmt_Insert.getUpdateCount();
            this.miInsertRecords += iUpdateCount;
            intFirst += iUpdateCount + 1;
            if (!strError.equals("")) {
                processErrorRecord(strError,
                                   (Vector)this.vtData.elementAt(intFirst - 1));
            }
            strError = "";
        }

        this.vtData.clear();
    }

    ////////////////////////////////////////////////////////
    public void runUpdateBatch() throws Exception {
        String strError = "";
        int intFirst = 0;
        int iUpdateCount = 0;

        int intDataSize = this.vtData.size();
        while (intFirst < intDataSize) {
            for (int intIndex = intFirst; intIndex < intDataSize; ++intIndex) {
                Vector vtRowTem = (Vector)this.vtData.elementAt(intIndex);
                for (int i = 0; i < this.miUpdateFields.length; ++i) {
                    this.mstmt_Update.setString(i + 1,
                                                (String) vtRowTem.
                                                elementAt(this.miUpdateFields[i]));
                }
                this.mstmt_Update.addBatch();
            }
            try {
                this.mstmt_Update.executeBatch();
            } catch (BatchUpdateException e) {
                strError = StringUtil.nvl(e.getMessage().trim(), "");
            } catch (SQLException e) {
                strError = StringUtil.nvl(e.getMessage().trim(), "");
            }
            iUpdateCount = this.mstmt_Update.getUpdateCount();
            this.miUpdateRecords += iUpdateCount;
            intFirst += iUpdateCount + 1;

            if (!strError.equals("")) {
                processErrorRecord(strError,
                                   (Vector)this.vtData.elementAt(intFirst - 1));
            }
            strError = "";
        }

        this.vtData.clear();
    }

    ////////////////////////////////////////////////////////
    public void importData(String strFileName) throws Exception {
        int intCount = 0;
        try {
            beforeImport(strFileName);
            while (this.csvSource.next()) {
                this.miTotalRecord += 1;

                bindData();
                ++intCount;

                if (intCount >= this.miBatchSize) {
                    ;
                }
                if (this.mintBatchMode == 0) {
                    runInsertBatch();
                } else {
                    runUpdateBatch();
                }
                intCount = 0;
            }

            if (this.mintBatchMode == 0) {
                runInsertBatch();
            } else {
                runUpdateBatch();
            }
        } finally {
            releaseDataTarget();
            releaseDataSource();
            afterImport(strFileName);
        }
    }

    ////////////////////////////////////////////////////////
    public void bindData() throws Exception {
        this.vtData.addElement(this.csvSource.marrValues);
    }

    ////////////////////////////////////////////////////////
    public void processErrorRecord(String strError,
                                   Vector vtError) throws Exception {
        if ((strError.startsWith("ORA-00001")) && (this.mintBatchMode == 0)) {
            try {
                processDuplicateRecord(vtError);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                logErrorRecord(e.getMessage().trim(), vtError);
            }
        } else {
            logErrorRecord(strError, vtError);
        }
    }

    ////////////////////////////////////////////////////////
    public void processDuplicateRecord(Vector vtDuplicate) throws Exception {
        String strValue = "";
        try {
            for (int iIndex = 0; iIndex < this.miUpdateFields.length; ++iIndex) {
                strValue = (String) vtDuplicate.elementAt(this.miUpdateFields[
                        iIndex]);
                this.mstmt_Update.setString(iIndex + 1, strValue);
            }
            this.mstmt_Update.executeUpdate();
            this.miUpdateRecords += 1;

            this.miBatchSize = 1;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
//
//	public static void main(String[] args) {
//		try {
//			long lngStart = System.currentTimeMillis();
//			SyncData syncImportText = new SyncData();
//			Connection mcnMain = Database.getConnection("jdbc:oracle:thin:@10.151.59.22:1521:crc", "interconnect", "doisoat");
//
//			syncImportText.mstrErrorDir = "C://Temp//ErrDir//";
//			syncImportText.mstrImportDir = "C://Temp//";
//			syncImportText.mcon_Target = mcnMain;
//			syncImportText.mstrInsertSQL = "INSERT /*+ APPEND */ INTO Tmp_Og_Call (FILE_ID,LOCATION_ID,TYPE,STA_DATETIME,DURATION,CALLING_NUMBER,CALLED_NUMBER,IMSI,CELL_ID,OG_ROUTE,IC_ROUTE,MOB_TYPE_ID,TIME_ZONE_ID,PO_CODE,SUB_CLASS1,SUB_CLASS2,SUB_CLASS3,DIR_CLASS1,DIR_CLASS2,DIR_CLASS3,FREE,DOM_NUM_BLOCKS,DOM_NUM_DURATION,DOM_CHARGE,SER_CHARGE,INT_NUM_BLOCKS,INT_NUM_DURATION,INT_CHARGE,CALL_TYPE_ID,ROB_ID) VALUES (?,?,?,TO_DATE(?,'YYYY/MM/DD HH24:MI:SS'),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//			syncImportText.mstrUpdateSQL = "UPDATE \tTmp_OG_CALL SET \t \tFILE_ID = ?, LOCATION_ID = ?, DURATION = ?, CALLING_NUMBER = ?, CALLED_NUMBER = ? \t, CELL_ID = ?, OG_ROUTE = ?, IC_ROUTE = ? \t, MOB_TYPE_ID = ?, TIME_ZONE_ID = ?, PO_CODE = ? \t, SUB_CLASS1 = ?, SUB_CLASS2 = ?, SUB_CLASS3 = ? \t, DIR_CLASS1 = ?, DIR_CLASS2 = ?, DIR_CLASS3 = ? \t, FREE = ?, DOM_NUM_BLOCKS = ?, DOM_NUM_DURATION = ? \t, DOM_CHARGE = ?, SER_CHARGE = ?, INT_NUM_BLOCKS = ? \t, INT_NUM_DURATION = ?, INT_CHARGE = ?,CALL_TYPE_ID=?,ROB_ID=? WHERE TYPE = ? AND STA_DATETIME = TO_DATE(?,'YYYY/MM/DD HH24:MI:SS') AND IMSI =?";
//			syncImportText.mstrInsertFields = "FILE_ID,CEN_CODE,TYPE,STA_DATETIME,DURATION,CALLING_NUMBER,CALLED_NUMBER,IMSI,CELL_ID,OG_ROUTE,IC_ROUTE,MOB_TYPE_ID,TIME_ZONE_ID,PO_CODE,SUB_CLASS1,SUB_CLASS2,SUB_CLASS3,DIR_CLASS1,DIR_CLASS2,DIR_CLASS3,FREE,DOM_NUM_BLOCKS,DOM_NUM_DURATION,DOM_CHARGE,SER_CHARGE,INT_NUM_BLOCKS,INT_NUM_DURATION,INT_CHARGE,CALL_TYPE_ID,ROB_ID";
//			syncImportText.mstrUpdateFields = "FILE_ID,CEN_CODE,DURATION,CALLING_NUMBER,CALLED_NUMBER,CELL_ID,OG_ROUTE,IC_ROUTE,MOB_TYPE_ID,TIME_ZONE_ID,PO_CODE,SUB_CLASS1,SUB_CLASS2,SUB_CLASS3,DIR_CLASS1,DIR_CLASS2,DIR_CLASS3,FREE,DOM_NUM_BLOCKS,DOM_NUM_DURATION,DOM_CHARGE,SER_CHARGE,INT_NUM_BLOCKS,INT_NUM_DURATION,INT_CHARGE,CALL_TYPE_ID,ROB_ID,TYPE,STA_DATETIME,IMSI";
//			syncImportText.importData("OG-CDR25-20050502_5257.---.txt");
//			mcnMain.commit();
//			System.out.println("Total of record : " + syncImportText.miTotalRecord);
//
//			System.out.println("Inserted record : " + syncImportText.miInsertRecords);
//
//			System.out.println("Updated record  : " + syncImportText.miUpdateRecords);
//
//			System.out.println("Error record    : " + syncImportText.miErrorRecords);
//
//			long lngFinish = System.currentTimeMillis();
//			System.out.print("Time: " + (lngFinish - lngStart));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
