package vnp.lottery;

import vnp.lottery.ImportFile;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.Vector;

import smartlib.database.Database;
import smartlib.util.AppException;
import smartlib.util.FileUtil;
import smartlib.util.Global;
import smartlib.util.StringUtil;
import vnp.util.ParameterType;

public class ImportTextFile extends ImportFile{
	protected PreparedStatement stmt;
	protected String mstrHeader;
	protected String mstrDelimited;
	protected boolean bSkipFirstRow;
	protected String mstrInsertCommand;
	protected String mstrInsertField;
	protected String[] strHeaderArray;
	protected String[] strFieldArray;
	protected int iBatchSize = 1000;

	////////////////////////////////////////////////////////
	// Override
	////////////////////////////////////////////////////////
	public void fillParameter() throws AppException {
		super.fillParameter();
		mstrHeader = loadMandatory("Header");
		mstrDelimited = loadMandatory("Delimited");
		bSkipFirstRow = loadString("SkipFirstRow").equals("Y");
		mstrInsertCommand = loadString("InsertCommand");
		mstrInsertField = loadString("InsertField");
		iBatchSize = loadInteger("BatchSize");

	}

	////////////////////////////////////////////////////////
	// Override
	////////////////////////////////////////////////////////
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		Vector vtValue = new Vector();
		vtValue.addElement("Y");
		vtValue.addElement("N");
		vtReturn.addElement(createParameterDefinition("SkipFirstRow", "", ParameterType.PARAM_COMBOBOX, vtValue, ""));
		vtReturn.addElement(createParameterDefinition("Header", "", ParameterType.PARAM_TEXTAREA_MAX, "2000"));
		vtReturn.addElement(createParameterDefinition("Delimited", "", ParameterType.PARAM_TEXTBOX_MAX, "10"));
		vtReturn.addElement(createParameterDefinition("InsertCommand", "", ParameterType.PARAM_TEXTAREA_MAX, "2000"));
		vtReturn.addElement(createParameterDefinition("InsertField", "", ParameterType.PARAM_TEXTAREA_MAX, "2000"));
		vtReturn.addElement(createParameterDefinition("BatchSize", "", ParameterType.PARAM_TEXTBOX_MASK, "99990"));

		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	public void beforeProcessSession() throws Exception {
		super.beforeProcessSession();
		strHeaderArray = StringUtil.toStringArray(mstrHeader, mstrDelimited);
		//strFieldArray = StringUtil.toStringArray(mstrHeader, mstrDelimited);
		strFieldArray = StringUtil.toStringArray(mstrInsertField, mstrDelimited);

	}

	public boolean importFile(String strFileName) throws Exception {
		try {
			String command = StringUtil.replaceAll(mstrInsertCommand, "$FileName", "'" + strFileName + "'");
			stmt = mcn.prepareStatement(command);
			int iCount = 0;
			String strLine = "";
			String[] strLineArray;
			while (readInput.next()) {
				try{
					iTotal++;
					if (bSkipFirstRow && iTotal == 1)
						continue;
					strLine = readInput.getLine();
					strLineArray = StringUtil.toStringArray(strLine, mstrDelimited);
					if (strLineArray.length != strHeaderArray.length) {
						iErr++;
						writeError.addText(strLine + mstrDelimited + "Number of columns doesn't match header");
						continue;
					}
					for (int i = 0; i < strFieldArray.length; i++) {
						for (int j = 0; j < strHeaderArray.length; j++) {
							if (strFieldArray[i].equalsIgnoreCase(strHeaderArray[j])) {
								stmt.setString(i + 1, strLineArray[j]);//AppUtil.formatISDN()
								continue;
							}
						}
					}
					
									stmt.addBatch();
					iCount++;
					if (iCount % iBatchSize == 0) {
						stmt.executeBatch();
						logMonitor("Row imported : " + iCount);
					}
				}catch(Exception e){
					iErr++;
					writeError.addText(strLine + mstrDelimited + e.getMessage() + mstrDelimited + e.getCause());
				}				
			}
			stmt.executeBatch();
			mcn.commit();
			logMonitor("--------------------------");
			logMonitor("Error row    : " + iErr);
			logMonitor("Imported row : " + iCount);
			logMonitor("Total row    : " + iTotal);
			return true;
		} catch (Exception e) {
			mcn.rollback();
			e.printStackTrace();
			logMonitor(e.getMessage());
		} finally {
			Database.closeObject(stmt);
		}
		return false;
	}

}
