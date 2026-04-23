package vnp.thread;

import java.net.*;
import java.text.*;
import java.util.*;

import com.comverse_in.prepaid.ccws.*;
import com.comverse_in.prepaid.ccws.run.*;
import smartlib.thread.ParameterType;
import smartlib.util.*;
import vnp.util.*;

/**
 * <p>Title: He thong canh bao</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: Billing Center - Vinaphone</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ProcessResetAcc extends vnp.util.ProcessFile {
    private String mstrHeader;
    private String mstrDelimited;
    private String mstrURL;
    private String mstrAccName;
    private String mstrAccValue;
    private String mstrNumLog;
    private int iNumLog;
    private String mstrErrorDir;
    private String mstrExportDir;
    private ServiceSoapStubEx stub;
    private SimpleDateFormat sdfTime = new SimpleDateFormat(
            "dd/MM/yyyy HH:mm:ss");
    private int iTotalRow = 0;
    private int iSuccRow = 0;
    private int iErrRow = 0;
    TextFileWriter txtError = new TextFileWriter();
    TextFileWriter txtExport = new TextFileWriter();

    public void fillParameter() throws AppException {
        super.fillParameter();
        ////////////////////////////////////////////////////////
        mstrHeader = loadString("Header");
        mstrDelimited = loadString("Delimited");
        mstrURL = loadString("URL");
        mstrAccName = loadString("AccName");
        mstrAccValue = loadString("AccValue");
        mstrNumLog = loadString("NumLog");
        iNumLog = Integer.parseInt(mstrNumLog);
        mstrErrorDir = loadString("ErrorDir");
        mstrExportDir = loadString("ExportDir");
    }

    ////////////////////////////////////////////////////////
    // Override
    ////////////////////////////////////////////////////////
    public Vector getParameterDefinition() {
        Vector vtReturn = new Vector();
        Vector vtRow = new Vector();
//        vtRow.add("ACC_Recharge");
//        vtRow.add("ACC_TALK_24");
//        vtRow.add("TALK_24");
//        vtRow.add("ACC_CALL_SECS");
//        vtRow.add("ALO");
//        vtRow.add("ACC_Recharge_Credit");
//        vtRow.add("ACC_Recharge_1");
//        vtRow.add("ACC_Recharge_Rules");
//        vtRow.add("TT14");
//        vtRow.add("CK45");
//        vtRow.add("ACC_Rech_Amnt");
//        vtRow.add("DV");
//        vtRow.add("ACC_PREF_NUM");
//        vtRow.add("ACC_PREF_NUM_SMS");
//        vtRow.add("ACC_Recharge_ALCO");
//        vtRow.add("ACC_NATIONAL_SMS");
//        vtRow.add("ACC_Recharge_Rule_1");
//        vtRow.add("ACC_SMS_VNP_DAILY");
//        vtRow.add("ACC_VC_INTRANET_DAILY");
//        vtRow.add("ACC_Recharge_Rule_2");
//        vtRow.add("KM_10phut");
//        vtRow.add("ACC_Recharge_2");
//        vtRow.add("ACC_Recharge_3");
//        vtRow.add("ACC_GOLD1_RULE");
        vtRow.add("ACC_Recharge");
        vtRow.add("ACC_TALK_24");
        vtRow.add("ACC_VC_INTRANET_DAILY");
        vtRow.add("TALK_24");
        vtRow.add("ACC_CALL_SECS");
        vtRow.add("ALO");
        vtRow.add("KM_10phut");
        vtRow.add("ACC_Recharge_Credit");
        vtRow.add("ACC_Recharge_1");
        vtRow.add("ACC_Recharge_Rules");
        vtRow.add("TT14");
        vtRow.add("CK45");
        vtRow.add("ACC_Rech_Amnt");
        vtRow.add("DV");
        vtRow.add("ACC_SMS_VNP_DAILY");
        vtRow.add("ACC_PREF_NUM");
        vtRow.add("ACC_PREF_NUM_SMS");
        vtRow.add("ACC_Recharge_ALCO");
        vtRow.add("ACC_NATIONAL_SMS");
        vtRow.add("ACC_Recharge_Rule_1");
        vtRow.add("ACC_Recharge_Rule_2");
        vtRow.add("ACC_Recharge_2");
        vtRow.add("ACC_Recharge_3");
        vtRow.add("ACC_GOLD1_RULE");
        vtRow.add("ACC_CUCBO_RULE");
        vtRow.add("ACC_KETBAN");

        vtReturn.addElement(createParameter("AccName", "",
                                            ParameterType.PARAM_COMBOBOX, vtRow,
                                            "", ""));
        vtReturn.addElement(createParameter("AccValue", "",
                                            ParameterType.PARAM_TEXTBOX_MAX,
                                            "100"));

        vtReturn.addElement(createParameter("Header", "",
                                            ParameterType.PARAM_TEXTBOX_MAX,
                                            "100"));
        vtReturn.addElement(createParameter("Delimited", "",
                                            ParameterType.PARAM_TEXTBOX_MAX,
                                            "100"));
        vtReturn.addElement(createParameter("URL", "",
                                            ParameterType.PARAM_TEXTBOX_MAX,
                                            "100"));
        vtReturn.addElement(createParameter("NumLog", "",
                                            ParameterType.PARAM_TEXTBOX_MAX,
                                            "100"));
        vtReturn.addElement(createParameter("ErrorDir", "",
                                            ParameterType.PARAM_TEXTBOX_MAX,
                                            "100"));
        vtReturn.addElement(createParameter("ExportDir", "",
                                            ParameterType.PARAM_TEXTBOX_MAX,
                                            "100"));
        vtReturn.addAll(super.getParameterDefinition());
        return vtReturn;
    }

    public ServiceSoapStubEx initComvConnection() throws Exception {
        URL endpointURL = new URL(mstrURL);
        ServiceSoapStubEx stub = new ServiceSoapStubEx(endpointURL, null);
        stub._setProperty("action", "UsernameToken");
        stub._setProperty("passwordType", "PasswordText");
        stub._setProperty("user", "ncpt_sub_tool");
        PasswordCallback pwCallback = new PasswordCallback("123456");
        stub._setProperty("passwordCallbackRef", pwCallback);
        if (stub == null) {
            logMonitor("Connected to IN (CCWS-IN) error Stub  is null");
            throw new Exception("Binding to IN (CCWS-IN) is null");
        }
        stub.setTimeout(1000);
        return stub;
    }

    public void beforeProcessSession() throws Exception {
        URL endpointURL = new URL(mstrURL);
        stub = new ServiceSoapStubEx(endpointURL, null);
        stub._setProperty("action", "UsernameToken");
        stub._setProperty("passwordType", "PasswordText");
        stub._setProperty("user", "ncpt_sub_tool");
        PasswordCallback pwCallback = new PasswordCallback("123456");
        stub._setProperty("passwordCallbackRef", pwCallback);
        if (stub == null) {
            logMonitor("Connected to IN (CCWS-IN) error Stub  is null");
            throw new Exception("Binding to IN (CCWS-IN) is null");
        }
    }

    public void processFile(String strFileName) throws Exception {
        CSVFile csv = new CSVFile();
        iTotalRow = 0;
        iSuccRow = 0;
        iErrRow = 0;
        csv.setHeader(mstrHeader);
        csv.setDelimited(mstrDelimited);
        csv.openCSVFile(mstrImportDir + "/" + strFileName, 1024 * 1024);
        String mstrLineOld;
        String[] mstrLine;

        txtError = new TextFileWriter();
        txtError.openFile(mstrErrorDir + "/" + strFileName, 1024 * 1024);
        txtExport = new TextFileWriter();
        txtExport.openFile(mstrExportDir + "/" + strFileName, 1024 * 1024);

        while (csv.next()) {
            iTotalRow++;
            mstrLineOld = csv.getLine();
            mstrLine = mstrLineOld.split(mstrDelimited);
            try {
                SetAccumulatorValueRequest savr = new
                                                  SetAccumulatorValueRequest(
                        mstrLine[0], "", mstrAccName,
                        Double.parseDouble(mstrAccValue));
                stub.setAccumulatorValue(savr);
                iSuccRow++;
                txtExport.addText(mstrLineOld + mstrDelimited +
                                  sdfTime.format(new Date()));
                if ((iTotalRow % iNumLog) == 0) {
                    logMonitor("Process row " + iTotalRow);
                }
            } catch (Exception ex) {
                iErrRow++;
                txtError.addText(mstrLineOld + mstrDelimited + ex.getMessage().replaceAll(";","").replaceAll("\n",""));
                logMonitor(ex.getMessage());
            }
        }
        logMonitor("Total Row : " + iTotalRow);
        logMonitor("Succe Row : " + iSuccRow);
        logMonitor("Error Row : " + iErrRow);
        logMonitor("Process File " + strFileName + " successfully!");

        if (iErrRow > 0) {
            txtError.safeCloseFile();
        } else {
            txtError.clear();
        }
        if (iSuccRow > 0) {
            txtExport.safeCloseFile();
        } else {
            txtExport.clear();
        }
        csv.safeCloseCSVFile();

    }
}
