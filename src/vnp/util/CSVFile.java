package vnp.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.Vector;

import smartlib.util.StringUtil;

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

public class CSVFile {
  protected FileReader mTextFile = null;
  protected BufferedReader mTextBuffer = null;
  public boolean mblnSuppressHeaders = false;
  public boolean mblnIgnoreCheckSize = false;
  public boolean mbExitIfMatchFirstEOF = false;
  protected String mstrDelimited = ";";
  protected String mstrEndOfFile = "";
  protected Vector marrHeaders = null;
  protected Vector marrValues = null;
  protected String mstrHeader = "";
  protected String mstrCurrentLine = null;
  protected String mstrLine = null;

  ////////////////////////////////////////////////////////
  public void setDelimited(String strDelimited) {
    this.mstrDelimited = strDelimited;
  }

  ////////////////////////////////////////////////////////
  public void setHeader(String strHeader) {
    this.mstrHeader = strHeader;
  }

  ////////////////////////////////////////////////////////
  public int getColumnCount() {
    return this.marrHeaders.size();
  }

  ////////////////////////////////////////////////////////
  public int getCurrentLineColumnCount() {
    return this.marrValues.size();
  }

  ////////////////////////////////////////////////////////
  public int findColumn(String strField) {
    for (int intIndex = 0; intIndex < this.marrHeaders.size(); ++intIndex) {
      if (strField.equalsIgnoreCase( (String)this.marrHeaders.elementAt(
          intIndex))) {
        return intIndex;
      }
    }
    return -1;
  }

  ////////////////////////////////////////////////////////
  private void parseLine(String strLine) throws Exception {
    if (this.mstrDelimited.equals("")) {
      return;
    }
    this.marrValues = StringUtil.toStringVector(strLine, this.mstrDelimited);
  }

  ////////////////////////////////////////////////////////
  public void openCSVFile(String strPath,
                          String strDelimited,
                          int intIgnoreRows,
                          String strEndOfFile,
                          int intBufferSize) throws Exception {
    openCSVFile(strPath, strDelimited, intIgnoreRows, strEndOfFile,
                intBufferSize, 0L);
  }

  ////////////////////////////////////////////////////////
  public void openCSVFile(String strPath,
                          String strDelimited,
                          int intIgnoreRows,
                          String strEndOfFile,
                          int intBufferSize,
                          long lngNumCharacterSkip) throws Exception {
    try {
      this.mTextFile = new FileReader(strPath);
      this.mTextBuffer = new BufferedReader(this.mTextFile, intBufferSize);
      this.mTextBuffer.skip(lngNumCharacterSkip);
      this.mstrCurrentLine = null;
      if (this.mstrDelimited.equals("")) {
        this.mstrDelimited = strDelimited;
      }
      this.mstrEndOfFile = strEndOfFile;
      parseHeader(intIgnoreRows);
    }
    catch (Exception e) {
      safeCloseCSVFile();
      throw e;
    }
  }

  ////////////////////////////////////////////////////////
  public void openCSVFile(String strPath,
                          int intBufferSize) throws Exception {
    openCSVFile(strPath, ";", 0, "", intBufferSize);
  }

  public void openCSVFile(String strPath) throws Exception {
    openCSVFile(strPath, ";", 0, "", 1048576);
  }

  ////////////////////////////////////////////////////////
  public void closeCSVFile() throws Exception {
    try {
      this.mTextBuffer.close();
      this.mTextFile.close();
    }
    catch (Exception e) {
    }
    finally {
      safeCloseCSVFile();
    }
  }

  ////////////////////////////////////////////////////////
  public void safeCloseCSVFile() {
    if (this.marrHeaders != null) {
      this.marrHeaders.clear();
    }
    safeClose(this.mTextBuffer);
    safeClose(this.mTextFile);
  }

  ////////////////////////////////////////////////////////
  public static void safeClose(Reader reader) {
    try {
      if (reader != null) {
        reader.close();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  ////////////////////////////////////////////////////////
  public void parseHeader(int intIgnoreRows) throws Exception {
    if (intIgnoreRows > 0) {
      for (int j = 0; j < intIgnoreRows; ++j) {
        this.mstrLine = this.mTextBuffer.readLine();
        if (this.mstrLine == null) {
          break;
        }
      }
    }

    if ( (this.mstrHeader == null) || (this.mstrHeader.equals(""))) {
      this.mstrLine = this.mTextBuffer.readLine();
      while (true) {
        if ( (this.mstrLine == null) || (! (this.mstrLine.trim().equals("")))) {
          break;
        }
        this.mstrLine = this.mTextBuffer.readLine();
      }
    }

    this.mstrLine = this.mstrHeader;

    if ( (this.mstrLine == null) || (this.mstrDelimited.equals("")) ||
        ( (! (this.mstrEndOfFile.equals(""))) &&
         (this.mstrLine.startsWith(this.mstrEndOfFile)))) {
      return;
    }
    this.mstrHeader = StringUtil.nvl(this.mstrLine, "");
    this.marrHeaders = StringUtil.toStringVector(this.mstrHeader,
                                                 this.mstrDelimited);
    if (this.mblnSuppressHeaders) {
      for (int intIndex = 0; intIndex < this.marrHeaders.size(); ++intIndex) {
        this.marrHeaders.setElementAt("COLUMN" + intIndex, intIndex);
      }

    }
    else {
      this.mstrLine = this.mTextBuffer.readLine();
    }
    for (int intIndex = 0; intIndex < this.marrHeaders.size(); ++intIndex) {
      this.marrHeaders.setElementAt(this.marrHeaders.elementAt(intIndex).
                                    toString().toUpperCase(), intIndex);
    }
  }

  ////////////////////////////////////////////////////////
  public void parseValues() throws Exception {
    parseLine(this.mstrLine);
    if ( (this.marrHeaders == null) || (this.marrValues == null) ||
        (this.mblnIgnoreCheckSize) ||
        (this.marrValues.size() == this.marrHeaders.size())) {
      return;
    }
    throw new Exception("Number of columns does not match header");
  }

  ////////////////////////////////////////////////////////
  public boolean first() throws Exception {
    throw new Exception("This method does not supported");
  }

  ////////////////////////////////////////////////////////
  public boolean last() throws Exception {
    throw new Exception("This method does not supported");
  }

  ////////////////////////////////////////////////////////
  public boolean prev() throws Exception {
    throw new Exception("This method does not supported");
  }

  ////////////////////////////////////////////////////////
  public boolean next() throws Exception {
    if ( (this.mstrLine == null) ||
        ( (! (this.mstrEndOfFile.equals(""))) &&
         (this.mstrLine.startsWith(this.mstrEndOfFile))) ||
        ( (this.mbExitIfMatchFirstEOF) &&
         (this.mstrLine.equals(this.mstrEndOfFile)))) {
      return false;
    }

    if (! (this.mstrLine.trim().equals(""))) {
      parseValues();
      this.mstrCurrentLine = this.mstrLine;
      this.mstrLine = this.mTextBuffer.readLine();
      return true;
    }

    this.mstrLine = this.mTextBuffer.readLine();
    return next();
  }

  ////////////////////////////////////////////////////////
  public String getString(int intIndex) {
    if (intIndex < 0) {
      return null;
    }

    return StringUtil.nvl(this.marrValues.elementAt(intIndex), "");
  }

  ////////////////////////////////////////////////////////
  public String getString(String strField) {
    return getString(findColumn(strField));
  }

  ////////////////////////////////////////////////////////
  public String getLine() {
    return this.mstrCurrentLine;
  }

  ////////////////////////////////////////////////////////
  public String getHeader() {
    return this.mstrHeader;
  }
}
