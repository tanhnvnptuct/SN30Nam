package vnp.lottery;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import smartlib.database.Database;
import smartlib.thread.DBManageableThread;
import smartlib.util.AppException;
import smartlib.util.DateUtil;
import smartlib.util.FileUtil;
import smartlib.util.Global;
import smartlib.util.LogUtil;
import smartlib.util.SmartZip;
import smartlib.util.StreamUtil;
import smartlib.util.StringEscapeUtil;
import smartlib.util.StringUtil;
import smartlib.util.WildcardFilter;

public class CSVImpExt extends DBManageableThread {
	protected int[] miFieldList;
	protected int miFieldCount;
	protected String mstrDBUrl;
	protected String mstrDBUserName;
	protected String mstrDBPassword;
	protected byte[] mbtEORSymbol = "\n".getBytes();
	protected byte[] mbtEOFSymbol = "\t".getBytes();
	protected String mstrImportDir;
	protected String mstrImportStyle;
	protected String mstrBackupDir;
	protected String mstrZipFileBackupStyle;
	protected String mstrBackupStyle;
	protected String mstrRejectDir;
	protected String mstrRetryDir;
	protected String mstrRejectStyle;
	protected String mstrTempDir;
	protected String mstrWildcard;
	protected String mstrZipFileWildcard;
	protected String mstrCompressed;
	protected String mstrDateFormat;
	protected String mstrProcessDate;
	protected String mstrFieldList;
	protected String mstrSQLValidateCommand;
	protected String mstrSQLCommand;
	protected String mstrSQLQueryFileIDCommand;
	protected String mstrSQLPreCommand;
	protected String mstrSQLPstCommand;
	protected int miBatchSize;
	protected boolean mbHeader;
	protected boolean mbConnectManual;
	protected String mstrFileID;
	protected String mstrScanDir;
	protected int miTotalFile;
	protected int miCommitCount = 0;
	protected int miErrorCount = 0;
	protected File fl;
	private Vector mvtColumn = new Vector();

	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();

		Vector vtValue = new Vector();
		vtValue = new Vector();
		vtValue.addElement("Y");
		vtValue.addElement("N");
		vtReturn.addElement(createParameter("ManualConnect", "", 4, vtValue, ""));

		vtReturn.addElement(createParameter("DBUrl", "", 2, "256", "Connection url of database"));
		vtReturn.addElement(createParameter("DBUserName", "", 2, "256", "DBA user name"));
		vtReturn.addElement(createParameter("DBPassword", "", 3, "100", "Password of DBA user name"));

		vtReturn.addElement(createParameter("ImportDir", "", 2, "100", ""));

		vtValue = new Vector();
		vtValue.addElement("Directly");
		vtValue.addElement("Daily");
		vtValue.addElement("Monthly");
		vtValue.addElement("Yearly");
		vtReturn.addElement(createParameter("ImportStyle", "", 4, vtValue, ""));
		vtReturn.addElement(createParameter("BackupDir", "", 2, "100", ""));
		vtValue = new Vector();
		vtValue.addElement("Directly");
		vtValue.addElement("Daily");
		vtValue.addElement("Monthly");
		vtValue.addElement("Yearly");
		vtValue.addElement("Delete file");
		vtReturn.addElement(createParameter("BackupStyle", "", 4, vtValue, ""));
		vtValue = new Vector();
		vtValue.addElement("Directly");
		vtValue.addElement("Daily");
		vtValue.addElement("Monthly");
		vtValue.addElement("Yearly");
		vtValue.addElement("Delete file");
		vtReturn.addElement(createParameter("ZipFileBackupStyle", "", 4, vtValue, ""));
		vtReturn.addElement(createParameter("RejectDir", "", 2, "100", ""));
		vtReturn.addElement(createParameter("RetryDir", "", 2, "100", ""));
		vtValue = new Vector();
		vtValue.addElement("Directly");
		vtValue.addElement("Daily");
		vtValue.addElement("Monthly");
		vtValue.addElement("Yearly");
		vtValue.addElement("Delete file");
		vtReturn.addElement(createParameter("RejectStyle", "", 4, vtValue, ""));
		vtReturn.addElement(createParameter("TempDir", "", 2, "256", ""));
		vtReturn.addElement(createParameter("Wildcard", "", 2, "100", ""));
		vtReturn.addElement(createParameter("ZipFileWildcard", "", 2, "100", ""));
		vtValue = new Vector();
		vtValue.addElement("Not compressed");
		vtValue.addElement("GZip");
		vtValue.addElement("Zip");
		vtReturn.addElement(createParameter("Compressed", "", 4, vtValue, ""));
		vtValue = new Vector();
		vtValue.addElement("Y");
		vtValue.addElement("N");
		vtReturn.addElement(createParameter("Header", "", 4, vtValue, ""));
		vtReturn.addElement(createParameter("DateFormat", "", 2, "256", ""));
		vtReturn.addElement(createParameter("ProcessDate", "", 2, "256", ""));
		vtReturn.addElement(createParameter("LastProcessFile", "", 0, "", ""));

		vtReturn.addElement(createParameter("EOFSymbol", "", 2, "100"));
		vtReturn.addElement(createParameter("EORSymbol", "", 2, "100"));
		vtReturn.addElement(createParameter("FieldList", "", 2, "4000"));
		vtReturn.addElement(createParameter("SQLValidateCommand", "", 2, "4000"));
		vtReturn.addElement(createParameter("SQLCommand", "", 2, "4000"));
		vtReturn.addElement(createParameter("SQLQueryFileIDCommand", "", 2, "4000"));
		vtReturn.addElement(createParameter("SQLPreCommand", "", 2, "4000"));
		vtReturn.addElement(createParameter("SQLPstCommand", "", 2, "4000"));
		vtReturn.addElement(createParameter("BatchSize", "", 1, "99990"));

		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	public void fillParameter() throws AppException {
		super.fillParameter();
		this.mbConnectManual = loadString("ManualConnect").equals("Y");
		
		this.mstrImportDir = loadDirectory("ImportDir",true,false);
		this.mstrImportStyle = StringUtil.nvl(getParameter("ImportStyle"), "");
		this.mstrBackupDir = loadDirectory("BackupDir", true, false);
		this.mstrBackupStyle = StringUtil.nvl(getParameter("BackupStyle"), "");
		this.mstrZipFileBackupStyle = StringUtil.nvl(getParameter("ZipFileBackupStyle"), "");
		this.mstrRejectDir = loadDirectory("RejectDir", true, false);
		this.mstrRejectStyle = StringUtil.nvl(getParameter("RejectStyle"), "");
		this.mstrRetryDir = loadDirectory("RetryDir", true, false);
		this.mstrTempDir = loadDirectory("TempDir", true, false);
		this.mstrWildcard = loadString("Wildcard");
		this.mstrZipFileWildcard = loadString("ZipFileWildcard");
		this.mstrCompressed = ((String) getParameter("Compressed"));
		this.mbHeader = loadString("Header").equals("Y");
		this.mstrDateFormat = StringUtil.nvl(getParameter("DateFormat"), "");
		this.mstrProcessDate = StringUtil.nvl(getParameter("ProcessDate"), "");

		this.mbtEOFSymbol = StringEscapeUtil.unescapeJava(loadString("EOFSymbol")).getBytes();
		this.mbtEORSymbol = StringEscapeUtil.unescapeJava(loadString("EORSymbol")).getBytes();
		this.mstrFieldList = loadString("FieldList");
		this.mstrSQLValidateCommand = StringUtil.nvl(getParameter("SQLValidateCommand"), "");
		this.mstrSQLCommand = loadString("SQLCommand");
		this.mstrSQLQueryFileIDCommand = StringUtil.nvl(getParameter("SQLQueryFileIDCommand"), "");
		this.mstrSQLPreCommand = StringUtil.nvl(getParameter("SQLPreCommand"), "");
		this.mstrSQLPstCommand = StringUtil.nvl(getParameter("SQLPstCommand"), "");
		this.miBatchSize = loadUnsignedInteger("BatchSize");

		this.mstrDBUrl = loadString("DBUrl");
		this.mstrDBUserName = loadString("DBUserName");
		this.mstrDBPassword = loadString("DBPassword");
		try {
			FileUtil.forceFolderExist(mstrImportDir);
			FileUtil.forceFolderExist(mstrBackupDir);
			FileUtil.forceFolderExist(mstrRejectDir);
			FileUtil.forceFolderExist(mstrRetryDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logMonitor(e.getMessage());
		}
	}

	public void validateParameter() throws Exception {
		super.validateParameter();
		if ((this.mbConnectManual)
				&& (this.mstrDBUrl.equals("") || this.mstrDBUserName.equals("") || this.mstrDBPassword.equals(""))) {
			throw new AppException("Connection Config cannot be null when ConnectionManual = Y",
					"TransactionImporter.validateParameter", "ConnectManual");
		}
		if ((this.mstrImportStyle != null) && (this.mstrImportStyle.length() > 0)
				&& (!this.mstrImportStyle.equals("Directly"))) {
			if ((this.mstrProcessDate == null) || (this.mstrProcessDate.length() == 0)) {
				throw new AppException("ProcessDate cannot be null when ImportStyle='" + this.mstrImportStyle + "'",
						"TransactionImporter.validateParameter", "ProcessDate");
			}

			if ((this.mstrDateFormat == null) || (this.mstrDateFormat.length() == 0)) {
				throw new AppException("DateFormat cannot be null when ImportStyle='" + this.mstrImportStyle + "'",
						"TransactionImporter.validateParameter", "DateFormat");
			}

			if (!DateUtil.isDate(this.mstrProcessDate, this.mstrDateFormat)) {
				throw new AppException("ProcessDate does not match DateFormat", "TransactionImporter.validateParameter",
						"ProcessDate");
			}
			if ((this.mstrImportStyle.equals("Daily")) && (this.mstrDateFormat.indexOf("dd") < 0)) {
				throw new AppException("DateFormat must contain 'dd' when ImportStyle='" + this.mstrImportStyle + "'",
						"TransactionImporter.validateParameter", "DateFormat");
			}
			if ((this.mstrImportStyle.equals("Monthly")) && (this.mstrDateFormat.indexOf("MM") < 0)) {
				throw new AppException("DateFormat must contain 'MM' when ImportStyle='" + this.mstrImportStyle + "'",
						"TransactionImporter.validateParameter", "DateFormat");
			}
			if ((this.mstrImportStyle.equals("Yearly")) && (this.mstrDateFormat.indexOf("yyyy") < 0)) {
				throw new AppException("DateFormat must contain 'yyyy' when ImportStyle='" + this.mstrImportStyle + "'",
						"TransactionImporter.validateParameter", "DateFormat");
			}
		}
	}

	protected void changeProcessDate() throws Exception {
		if ((this.mstrImportStyle != null) && (this.mstrImportStyle.length() > 0)
				&& (!this.mstrImportStyle.equals("Directly"))) {
			Date dt = DateUtil.toDate(this.mstrProcessDate, this.mstrDateFormat);
			do {
				if (this.mstrImportStyle.equals("Daily")) {
					dt = DateUtil.addDay(dt, 1);
				} else if (this.mstrImportStyle.equals("Monthly")) {
					dt = DateUtil.addMonth(dt, 1);
				} else if (this.mstrImportStyle.equals("Yearly")) {
					dt = DateUtil.addYear(dt, 1);
				}
				String strNextProcessDate = StringUtil.format(dt, this.mstrDateFormat);

				File fl = new File(this.mstrImportDir + strNextProcessDate + "/");
				if ((!fl.exists()) || (!fl.isDirectory())) {
					continue;
				}
				this.mstrProcessDate = strNextProcessDate;
				setParameter("ProcessDate", this.mstrProcessDate);
				storeConfig();
				return;
			} while (dt.getTime() < System.currentTimeMillis());
		}
	}

	protected void processSession() throws Exception {
		if ((this.mstrImportStyle != null) && (this.mstrImportStyle.length() > 0)
				&& (!this.mstrImportStyle.equals("Directly"))) {
			this.mstrScanDir = (this.mstrImportDir + this.mstrProcessDate + "/");
		} else {
			this.mstrScanDir = this.mstrImportDir;
		}
		
		//lấy lại các file reject do lỗi kết nối
		WildcardFilter wF = new WildcardFilter(this.mstrWildcard);
		File retryfl = new File(this.mstrRetryDir);
		String[] retryFiles = retryfl.list(wF);
		if(retryFiles != null && retryFiles.length > 0){
			for(int i = 0; i < retryFiles.length; i++){
				FileUtil.renameFile(this.mstrRetryDir + "/" + retryFiles[i], this.mstrScanDir + "/" + retryFiles[i]);
			}
		}
		
		File fl = new File(this.mstrScanDir);
		
		//unzip file nén
		WildcardFilter zipF = new WildcardFilter(this.mstrZipFileWildcard);
		String[] zipFiles = fl.list(zipF);
		if(zipFiles != null && zipFiles.length > 0){
			for(int i = 0; i < zipFiles.length; i++){
				SmartZip.UnZip(this.mstrScanDir + zipFiles[i], this.mstrScanDir);
				FileUtil.backup(this.mstrScanDir, this.mstrBackupDir, zipFiles[i], zipFiles[i], this.mstrZipFileBackupStyle);
			}
		}				
				
		String[] strFileList = fl.list(wF);
		if ((strFileList != null) && (strFileList.length > 0)) {
			Arrays.sort(strFileList);
			int iFileCount = strFileList.length;
			for (int iFileIndex = 0; (iFileIndex < iFileCount) && (this.miThreadCommand != 2); iFileIndex++) {
				try{
					process(strFileList[iFileIndex]);
				}catch(Exception e){}				
			}

		} else if (this.miThreadCommand != 2) {
			changeProcessDate();
		}
	}

	protected void process(String strFileName) throws Exception {
		String strImportFile = "";
		Connection cn = null;
		FileInputStream is = null;
		FileOutputStream os = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			logMonitor("Start importing file " + strFileName);

			String strValidateResult = validateFile(cn, strFileName);

			if ((strValidateResult != null) && (strValidateResult.length() > 0)) {
				logMonitor(strValidateResult);
				return;
			}
			if (this.mstrCompressed.equals("GZip")) {
				strImportFile = strFileName + ".extracted";
				SmartZip.GUnZip(this.mstrScanDir + strFileName, this.mstrTempDir + strImportFile);
			} else if (this.mstrCompressed.equals("Zip")) {
				strImportFile = (String) SmartZip
						.UnZip(this.mstrScanDir + strFileName, this.mstrTempDir + strImportFile).elementAt(0);
			} else {
				strImportFile = strFileName;
				FileUtil.copyFile(this.mstrScanDir + strFileName, this.mstrTempDir + strImportFile);
			}

			this.fl = new File(this.mstrTempDir + strImportFile);
			is = new FileInputStream(this.mstrTempDir + strImportFile);
			os = new FileOutputStream(this.mstrTempDir + strFileName + ".error");
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(os);

			if (this.mbHeader) {
				this.mvtColumn = readLine(bis);
				if ((this.mvtColumn == null) || (this.mvtColumn.size() <= 0)) {
					throw new Exception("Field list not found in first line of csv file");
				}
			} else {
				this.mvtColumn = StringUtil.toStringVector(this.mstrFieldList, ",");
			}
			writeLine(bos, this.mvtColumn);

			this.miCommitCount = 0;
			this.miErrorCount = 0;

			buildFieldList();
			if (mbConnectManual)
				cn = Database.getConnection(this.mstrDBUrl, this.mstrDBUserName, this.mstrDBPassword);
			else {
				cn = mcnMain;
			}
			cn.setAutoCommit(false);

			executeSQLQueryFileIDCommand(cn, strFileName);

			executeSQLPreCommand(cn, strFileName);

			String strSQL = this.mstrSQLCommand;
			strSQL = StringUtil.replaceAll(strSQL, "$ThreadID", getThreadID());
			strSQL = StringUtil.replaceAll(strSQL, "$FileID", this.mstrFileID);
			strSQL = StringUtil.replaceAll(strSQL, "$FileName", strFileName);
			strSQL = StringUtil.replaceAll(strSQL, "$FileSize", String.valueOf(this.fl.length()));
			strSQL = StringUtil.replaceAll(strSQL, "$FileDate",
					Global.FORMAT_DATE_TIME().format(new Date(this.fl.lastModified())));
			strSQL = StringUtil.replaceAll(strSQL, "$RecordCount",
					String.valueOf(this.miCommitCount + this.miErrorCount));
			PreparedStatement stmtInsert = (PreparedStatement) cn.prepareStatement(strSQL);
			//stmtInsert.setExecuteBatch(this.miBatchSize);

			importData(cn, bis, bos, stmtInsert);

			FileUtil.safeClose(bos);
			FileUtil.safeClose(bis);
			FileUtil.safeClose(os);
			FileUtil.safeClose(is);

			if (this.miThreadCommand == 2) {
				throw new SQLException("Thread interrupted");
			}
			if ((this.miCommitCount == 0) && (this.miErrorCount > 0)) {
				throw new Exception("All records in file was rejected");
			}

			stmtInsert.close();

			executeSQLPstCommand(cn, strFileName);

			FileUtil.deleteFile(this.mstrTempDir + strImportFile);

			FileUtil.backup(this.mstrScanDir, this.mstrBackupDir, strFileName, strFileName, this.mstrBackupStyle);
			if (this.miErrorCount == 0) {
				FileUtil.deleteFile(this.mstrTempDir + strFileName + ".error");
			} else {
				FileUtil.backup(this.mstrTempDir, this.mstrRejectDir, strFileName + ".error", strFileName + ".error",
						this.mstrRejectStyle);
			}

			setParameter("LastProcessFile", strFileName);
			storeConfig();

			cn.commit();
			cn.setAutoCommit(true);

			logMonitor("Importing file " + strFileName + " completed" + "\n\tTotal records:\t\t"
					+ (this.miErrorCount + this.miCommitCount) + "\n\tImported records:\t" + this.miCommitCount
					+ "\n\tError records:\t\t" + this.miErrorCount);
		} catch (Exception e) {
			FileUtil.deleteFile(this.mstrTempDir + strImportFile);

			LogUtil.log.error("Error", e);
			logMonitor("Error occured\n\t" + e.getMessage());
			
			if(e.getMessage().indexOf("Broken pipe") > -1 || e.getMessage().indexOf("Closed Connection") > -1){
				FileUtil.backup(this.mstrScanDir, this.mstrRetryDir, strFileName, strFileName, this.mstrRejectStyle);
			}else{
				FileUtil.backup(this.mstrScanDir, this.mstrRejectDir, strFileName, strFileName, this.mstrRejectStyle);
			}
			
			cn.rollback();
			cn.setAutoCommit(true);
		} finally {
			FileUtil.safeClose(bos);
			FileUtil.safeClose(bis);
			FileUtil.safeClose(os);
			FileUtil.safeClose(is);
			Database.closeObject(cn);
		}
	}

	public Vector readLine(InputStream is) throws Exception {
		byte[] btData = null;
		if ((is.available() > 0)
				&& ((btData = StreamUtil.getDataTerminatedBySymbolOrReachEOF(is, this.mbtEORSymbol)) != null)) {
			Vector vtRow = new Vector();
			ByteArrayInputStream isData = new ByteArrayInputStream(btData);
			byte[] btValue = null;
			while ((isData.available() > 0) && ((btValue = StreamUtil.getDataTerminatedBySymbolOrReachEOF(isData,
					this.mbtEOFSymbol)) != null)) {
				if (isData.available() > 0) {
					vtRow.addElement(StringEscapeUtil
							.unescapeJava(new String(btValue, 0, btValue.length - this.mbtEOFSymbol.length)));
					continue;
				}
				boolean bFound = true;
				for (int iIndex = 1; iIndex <= this.mbtEORSymbol.length; iIndex++) {
					if (this.mbtEORSymbol[(this.mbtEORSymbol.length - iIndex)] == btValue[(btValue.length - iIndex)]) {
						continue;
					}
					bFound = false;
					break;
				}

				if (bFound) {
					vtRow.addElement(StringEscapeUtil
							.unescapeJava(new String(btValue, 0, btValue.length - this.mbtEORSymbol.length)));
				} else {
					vtRow.addElement(StringEscapeUtil.unescapeJava(new String(btValue, 0, btValue.length)));
				}
			}

			return vtRow;
		}
		return null;
	}

	public void writeLine(OutputStream os, Vector vtRow) throws Exception {
		for (int iColIndex = 0; iColIndex < vtRow.size(); iColIndex++) {
			os.write(StringEscapeUtil.escapeJava(StringUtil.nvl(vtRow.elementAt(iColIndex), "")).getBytes());
			if (iColIndex >= vtRow.size() - 1) {
				continue;
			}
			os.write(this.mbtEOFSymbol);
		}

		os.write(this.mbtEORSymbol);
	}

	public void importData(Connection cn, InputStream is, OutputStream os, PreparedStatement stmtDst)
			throws Exception {
		Vector vtRow = null;
		int iBatchCount = 0;
		int iMarkedIndex = 0;
		is.mark(8388608);

		int i = 0;
		String log = "";
		
		while ((this.miThreadCommand != 2) && ((vtRow = readLine(is)) != null)) {		
				i++;
			try {
				log = "";
				for (int iFieldIndex = 0; iFieldIndex < this.miFieldCount; iFieldIndex++) {
					String cmt = (String) vtRow.elementAt(this.miFieldList[iFieldIndex]);
					stmtDst.setString(iFieldIndex + 1, cmt.replaceAll("\r", ""));
					log = log+","+cmt;
				}
				stmtDst.addBatch();
				this.miCommitCount ++;
//				this.miCommitCount += stmtDst.executeUpdate();
				iBatchCount++;
				if (iBatchCount >1000) {
					iBatchCount = 0;
//					this.miCommitCount += stmtDst.sendBatch();
					stmtDst.executeBatch();
					cn.commit();
					cn.setAutoCommit(false);
				}
			} catch (Exception e) {
				LogUtil.log.error("Error", e);
				//logMonitor("Error: " + e.getClass() + " |" + e.getMessage() + " |" +vtRow);

//				int iUpdateCount = stmtDst.getUpdateCount();
//				this.miCommitCount += iUpdateCount;
				this.miErrorCount += 1;
//				is.reset();
//				for (int iIndex = 0; iIndex < iUpdateCount; iIndex++) {
//					vtRow = readLine(is);
//				}
//				vtRow = readLine(is);
				writeLine(os, vtRow);
			}
//			if (this.miCommitCount + this.miErrorCount == iMarkedIndex) {
//				continue;
//			}
//			iMarkedIndex = this.miCommitCount + this.miErrorCount;
//			is.mark(8388608);
		}

//		this.miCommitCount += stmtDst.sendBatch();
		stmtDst.executeBatch();
		cn.commit();
		cn.setAutoCommit(false);

		if (this.miThreadCommand == 2) {
			throw new SQLException("Thread interrupted");
		}
	}

	public void executeSQLQueryFileIDCommand(Connection cn, String strFileName) throws SQLException {
		if ((this.mstrSQLQueryFileIDCommand != null) && (this.mstrSQLQueryFileIDCommand.length() > 0)) {
			String strSQL = this.mstrSQLQueryFileIDCommand;
			strSQL = StringUtil.replaceAll(strSQL, "$ThreadID", getThreadID());
			strSQL = StringUtil.replaceAll(strSQL, "$FileID", this.mstrFileID);
			strSQL = StringUtil.replaceAll(strSQL, "$FileName", strFileName);
			strSQL = StringUtil.replaceAll(strSQL, "$FileSize", String.valueOf(this.fl.length()));
			strSQL = StringUtil.replaceAll(strSQL, "$FileDate",
					Global.FORMAT_DATE_TIME().format(new Date(this.fl.lastModified())));
			strSQL = StringUtil.replaceAll(strSQL, "$RecordCount",
					String.valueOf(this.miCommitCount + this.miErrorCount));
			strSQL = StringUtil.replaceAll(strSQL, "$CommitCount", String.valueOf(this.miCommitCount));
			strSQL = StringUtil.replaceAll(strSQL, "$ErrorCount", String.valueOf(this.miErrorCount));

			Statement stmt = cn.createStatement();
			ResultSet rs = stmt.executeQuery(strSQL);
			if (rs.next()) {
				this.mstrFileID = rs.getString(1);
			}
			stmt.close();
		}
	}

	public void executeSQLPreCommand(Connection cn, String strFileName) throws SQLException {
		if ((this.mstrSQLPreCommand != null) && (this.mstrSQLPreCommand.length() > 0)) {
			String strSQL = this.mstrSQLPreCommand;
			strSQL = StringUtil.replaceAll(strSQL, "$ThreadID", getThreadID());
			strSQL = StringUtil.replaceAll(strSQL, "$FileID", this.mstrFileID);
			strSQL = StringUtil.replaceAll(strSQL, "$FileName", strFileName);
			strSQL = StringUtil.replaceAll(strSQL, "$FileSize", String.valueOf(this.fl.length()));
			strSQL = StringUtil.replaceAll(strSQL, "$FileDate",
					Global.FORMAT_DATE_TIME().format(new Date(this.fl.lastModified())));
			strSQL = StringUtil.replaceAll(strSQL, "$RecordCount",
					String.valueOf(this.miCommitCount + this.miErrorCount));
			strSQL = StringUtil.replaceAll(strSQL, "$CommitCount", String.valueOf(this.miCommitCount));
			strSQL = StringUtil.replaceAll(strSQL, "$ErrorCount", String.valueOf(this.miErrorCount));

			Statement stmt = cn.createStatement();
			stmt.executeUpdate(strSQL);
			stmt.close();
		}
	}

	public void executeSQLPstCommand(Connection cn, String strFileName) throws SQLException {
		if ((this.mstrSQLPstCommand != null) && (this.mstrSQLPstCommand.length() > 0)) {
			String strSQL = this.mstrSQLPstCommand;
			strSQL = StringUtil.replaceAll(strSQL, "$ThreadID", getThreadID());
			strSQL = StringUtil.replaceAll(strSQL, "$FileID", this.mstrFileID);
			strSQL = StringUtil.replaceAll(strSQL, "$FileName", strFileName);
			strSQL = StringUtil.replaceAll(strSQL, "$FileSize", String.valueOf(this.fl.length()));
			strSQL = StringUtil.replaceAll(strSQL, "$FileDate",
					Global.FORMAT_DATE_TIME().format(new Date(this.fl.lastModified())));
			strSQL = StringUtil.replaceAll(strSQL, "$RecordCount",
					String.valueOf(this.miCommitCount + this.miErrorCount));
			strSQL = StringUtil.replaceAll(strSQL, "$CommitCount", String.valueOf(this.miCommitCount));
			strSQL = StringUtil.replaceAll(strSQL, "$ErrorCount", String.valueOf(this.miErrorCount));

			Statement stmt = cn.createStatement();
			stmt.executeUpdate(strSQL);
			stmt.close();
		}
	}

	public String validateFile(Connection cn, String strFileName) throws SQLException {
		if ((this.mstrSQLValidateCommand != null) && (this.mstrSQLValidateCommand.length() > 0)) {
			String strSQL = this.mstrSQLValidateCommand;
			strSQL = StringUtil.replaceAll(strSQL, "$ThreadID", getThreadID());
			strSQL = StringUtil.replaceAll(strSQL, "$FileID", this.mstrFileID);
			strSQL = StringUtil.replaceAll(strSQL, "$FileName", strFileName);
			strSQL = StringUtil.replaceAll(strSQL, "$FileSize", String.valueOf(this.fl.length()));
			strSQL = StringUtil.replaceAll(strSQL, "$FileDate",
					Global.FORMAT_DATE_TIME().format(new Date(this.fl.lastModified())));
			Statement stmt = cn.createStatement();
			ResultSet rs = stmt.executeQuery(strSQL);

			String strValidationResult = null;
			if (rs.next()) {
				strValidationResult = rs.getString(1);
			}

			rs.close();
			stmt.close();
			return strValidationResult;
		}
		return null;
	}

	public void buildFieldList() throws Exception {
		Vector mvtFieldList = StringUtil.toStringVector(StringUtil.replaceAll(this.mstrFieldList, ";", ","));

		this.miFieldCount = mvtFieldList.size();
		this.miFieldList = new int[this.miFieldCount];
		for (int iFieldIndex = 0; iFieldIndex < this.miFieldList.length; iFieldIndex++) {
			this.miFieldList[iFieldIndex] = this.mvtColumn.indexOf((String) mvtFieldList.elementAt(iFieldIndex));
			if (this.miFieldList[iFieldIndex] >= 0) {
				continue;
			}
			throw new IOException(
					"Field with name " + (String) mvtFieldList.elementAt(iFieldIndex) + " does not exist in CSV file");
		}
	}
}