package vnp.thread;

import java.io.*;
import java.util.*;

import smartlib.thread.*;
import smartlib.util.*;

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
public class SplitFile extends vnp.util.ProcessFile {
    private String mstrNumRowSplit;
    private int iNumRowSplit;
    private String mstrExportDir;


    public void fillParameter() throws AppException {
        super.fillParameter();
        mstrNumRowSplit = loadString("NumRowSplit");
        iNumRowSplit = Integer.parseInt(mstrNumRowSplit);
        mstrExportDir = loadString("ExportDir");
    }

    ////////////////////////////////////////////////////////
    // Override
    ////////////////////////////////////////////////////////
    public Vector getParameterDefinition() {
        Vector vtReturn = new Vector();
        vtReturn.addElement(createParameter("NumRowSplit", "",
                                            ParameterType.PARAM_TEXTBOX_MAX,
                                            "100"));
        vtReturn.addElement(createParameter("ExportDir", "",
                                            ParameterType.PARAM_TEXTBOX_MAX,
                                            "100"));
        vtReturn.addAll(super.getParameterDefinition());
        return vtReturn;
    }

    public void processFile(String strFileName) throws Exception {
        try {
            String inputfile = mstrImportDir + "/" + strFileName;
            File file = new File(inputfile);
            Scanner scanner = new Scanner(file);
            int count = 0;
            while (scanner.hasNextLine()) {
                scanner.nextLine();
                count++;
            }
            logMonitor("Lines in the file: " + count);
            scanner.close();

            double temp = (count / (double) iNumRowSplit);
            int temp1 = (int) temp;
            int nof = 0;
            if (temp1 == temp) {
                nof = temp1;
            } else {
                nof = temp1 + 1;
            }
            logMonitor("No. of files to be generated :" + nof);

            FileInputStream fstream = new FileInputStream(inputfile);
            DataInputStream in = new DataInputStream(fstream);

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            for (int j = 1; j <= nof; j++) {
                String strName = mstrExportDir + "/" +
                                 strFileName.replaceAll(".txt", "") + "_" + j +
                                 ".txt";
                FileWriter fstream1 = new FileWriter(strName);
                BufferedWriter out = new BufferedWriter(fstream1);
                logMonitor("Split to file " + strName);
                for (int i = 1; i <= iNumRowSplit; i++) {
                    strLine = br.readLine();
                    if (strLine != null) {
                        out.write(strLine);
                        if (i != iNumRowSplit) {
                            out.newLine();
                        }
                    }
                }
                out.close();
            }
            in.close();
        } catch (Exception e) {
            logMonitor(e.getMessage());
        }
    }
}
