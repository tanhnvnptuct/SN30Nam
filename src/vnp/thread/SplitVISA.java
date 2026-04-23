package vnp.thread;

import java.io.*;

import telsoft.util.*;

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
public class SplitVISA {
    public SplitVISA() {
    }

    public void processFile() {
        try {
//            Vector vtData = new Vector();
//            vtData.add("AGG");
//            vtData.add("BDG");
//            vtData.add("BDH");
//            vtData.add("BGG");
//            vtData.add("BKN");
//            vtData.add("BLU");
//            vtData.add("BNH");
//            vtData.add("BPC");
//            vtData.add("BTN");
//            vtData.add("CBG");
//            vtData.add("CMU");
//            vtData.add("CTO");
//            vtData.add("DAN");
//            vtData.add("DBN");
//            vtData.add("DLK");
//            vtData.add("DNG");
//            vtData.add("DNI");
//            vtData.add("DTP");
//            vtData.add("GLI");
//            vtData.add("HBH");
//            vtData.add("HCM");
//            vtData.add("HDG");
//            vtData.add("HGG");
//            vtData.add("HGG2");
//            vtData.add("HNI");
//            vtData.add("HNM");
//            vtData.add("HPG");
//            vtData.add("HTH");
//            vtData.add("HUE");
//            vtData.add("HYN");
//            vtData.add("KHA");
//            vtData.add("KTM");
//            vtData.add("LAN");
//            vtData.add("LCI");
//            vtData.add("LCU");
//            vtData.add("LDG");
//            vtData.add("LSN");
//            vtData.add("NAN");
//            vtData.add("NBH");
//            vtData.add("NDH");
//            vtData.add("NTN");
//            vtData.add("PTO");
//            vtData.add("PYN");
//            vtData.add("QBH");
//            vtData.add("QNH");
//            vtData.add("QNI");
//            vtData.add("QNM");
//            vtData.add("QTI");
//            vtData.add("SLA");
//            vtData.add("SST");
//            vtData.add("STG");
//            vtData.add("TBH");
//            vtData.add("TGG");
//            vtData.add("THA");
//            vtData.add("TNH");
//            vtData.add("TNN");
//            vtData.add("TQG");
//            vtData.add("TVH");
//            vtData.add("VLG");
//            vtData.add("VPC");
//            vtData.add("VTU");
//            vtData.add("YBI");

            FileInputStream fis = new FileInputStream(new File(
                    "olddata/visa25022016.csv"));
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader in = new BufferedReader(isr);

//            for (int j = 0; j < vtData.size(); j++) {
////                System.out.println("FileOutputStream ois" +
////                                   vtData.elementAt(j).toString() +
////                                   " = new FileOutputStream(\"olddata/visa_" +
////                                   vtData.elementAt(j).toString() + ".csv\");");
////                System.out.println("OutputStreamWriter osr" +
////                                   vtData.elementAt(j).toString() +
////                                   " = new OutputStreamWriter(ois" +
////                                   vtData.elementAt(j).toString() +
////                                   ", \"UTF-8\");");
////                System.out.println("BufferedWriter out" +
////                                   vtData.elementAt(j).toString() +
////                                   " = new BufferedWriter(osr" +
////                                   vtData.elementAt(j).toString() + ");");
////
////                System.out.println("else if (str[10].equalsIgnoreCase(\"" +
////                                   vtData.elementAt(j).toString() + "\")) {out" +
////                                   vtData.elementAt(j).toString() +
////                                   ".write(line);}");
//                System.out.println("out" + vtData.elementAt(j).toString() +
//                                   ".close;");
//            }

            FileOutputStream oisAGG = new FileOutputStream(
                    "olddata/visa_AGG.csv");
            OutputStreamWriter osrAGG = new OutputStreamWriter(oisAGG, "UTF-8");
            BufferedWriter outAGG = new BufferedWriter(osrAGG);
            FileOutputStream oisBDG = new FileOutputStream(
                    "olddata/visa_BDG.csv");
            OutputStreamWriter osrBDG = new OutputStreamWriter(oisBDG, "UTF-8");
            BufferedWriter outBDG = new BufferedWriter(osrBDG);
            FileOutputStream oisBDH = new FileOutputStream(
                    "olddata/visa_BDH.csv");
            OutputStreamWriter osrBDH = new OutputStreamWriter(oisBDH, "UTF-8");
            BufferedWriter outBDH = new BufferedWriter(osrBDH);
            FileOutputStream oisBGG = new FileOutputStream(
                    "olddata/visa_BGG.csv");
            OutputStreamWriter osrBGG = new OutputStreamWriter(oisBGG, "UTF-8");
            BufferedWriter outBGG = new BufferedWriter(osrBGG);
            FileOutputStream oisBKN = new FileOutputStream(
                    "olddata/visa_BKN.csv");
            OutputStreamWriter osrBKN = new OutputStreamWriter(oisBKN, "UTF-8");
            BufferedWriter outBKN = new BufferedWriter(osrBKN);
            FileOutputStream oisBLU = new FileOutputStream(
                    "olddata/visa_BLU.csv");
            OutputStreamWriter osrBLU = new OutputStreamWriter(oisBLU, "UTF-8");
            BufferedWriter outBLU = new BufferedWriter(osrBLU);
            FileOutputStream oisBNH = new FileOutputStream(
                    "olddata/visa_BNH.csv");
            OutputStreamWriter osrBNH = new OutputStreamWriter(oisBNH, "UTF-8");
            BufferedWriter outBNH = new BufferedWriter(osrBNH);
            FileOutputStream oisBPC = new FileOutputStream(
                    "olddata/visa_BPC.csv");
            OutputStreamWriter osrBPC = new OutputStreamWriter(oisBPC, "UTF-8");
            BufferedWriter outBPC = new BufferedWriter(osrBPC);
            FileOutputStream oisBTN = new FileOutputStream(
                    "olddata/visa_BTN.csv");
            OutputStreamWriter osrBTN = new OutputStreamWriter(oisBTN, "UTF-8");
            BufferedWriter outBTN = new BufferedWriter(osrBTN);
            FileOutputStream oisCBG = new FileOutputStream(
                    "olddata/visa_CBG.csv");
            OutputStreamWriter osrCBG = new OutputStreamWriter(oisCBG, "UTF-8");
            BufferedWriter outCBG = new BufferedWriter(osrCBG);
            FileOutputStream oisCMU = new FileOutputStream(
                    "olddata/visa_CMU.csv");
            OutputStreamWriter osrCMU = new OutputStreamWriter(oisCMU, "UTF-8");
            BufferedWriter outCMU = new BufferedWriter(osrCMU);
            FileOutputStream oisCTO = new FileOutputStream(
                    "olddata/visa_CTO.csv");
            OutputStreamWriter osrCTO = new OutputStreamWriter(oisCTO, "UTF-8");
            BufferedWriter outCTO = new BufferedWriter(osrCTO);
            FileOutputStream oisDAN = new FileOutputStream(
                    "olddata/visa_DAN.csv");
            OutputStreamWriter osrDAN = new OutputStreamWriter(oisDAN, "UTF-8");
            BufferedWriter outDAN = new BufferedWriter(osrDAN);
            FileOutputStream oisDBN = new FileOutputStream(
                    "olddata/visa_DBN.csv");
            OutputStreamWriter osrDBN = new OutputStreamWriter(oisDBN, "UTF-8");
            BufferedWriter outDBN = new BufferedWriter(osrDBN);
            FileOutputStream oisDLK = new FileOutputStream(
                    "olddata/visa_DLK.csv");
            OutputStreamWriter osrDLK = new OutputStreamWriter(oisDLK, "UTF-8");
            BufferedWriter outDLK = new BufferedWriter(osrDLK);
            FileOutputStream oisDNG = new FileOutputStream(
                    "olddata/visa_DNG.csv");
            OutputStreamWriter osrDNG = new OutputStreamWriter(oisDNG, "UTF-8");
            BufferedWriter outDNG = new BufferedWriter(osrDNG);
            FileOutputStream oisDNI = new FileOutputStream(
                    "olddata/visa_DNI.csv");
            OutputStreamWriter osrDNI = new OutputStreamWriter(oisDNI, "UTF-8");
            BufferedWriter outDNI = new BufferedWriter(osrDNI);
            FileOutputStream oisDTP = new FileOutputStream(
                    "olddata/visa_DTP.csv");
            OutputStreamWriter osrDTP = new OutputStreamWriter(oisDTP, "UTF-8");
            BufferedWriter outDTP = new BufferedWriter(osrDTP);
            FileOutputStream oisGLI = new FileOutputStream(
                    "olddata/visa_GLI.csv");
            OutputStreamWriter osrGLI = new OutputStreamWriter(oisGLI, "UTF-8");
            BufferedWriter outGLI = new BufferedWriter(osrGLI);
            FileOutputStream oisHBH = new FileOutputStream(
                    "olddata/visa_HBH.csv");
            OutputStreamWriter osrHBH = new OutputStreamWriter(oisHBH, "UTF-8");
            BufferedWriter outHBH = new BufferedWriter(osrHBH);
            FileOutputStream oisHCM = new FileOutputStream(
                    "olddata/visa_HCM.csv");
            OutputStreamWriter osrHCM = new OutputStreamWriter(oisHCM, "UTF-8");
            BufferedWriter outHCM = new BufferedWriter(osrHCM);
            FileOutputStream oisHDG = new FileOutputStream(
                    "olddata/visa_HDG.csv");
            OutputStreamWriter osrHDG = new OutputStreamWriter(oisHDG, "UTF-8");
            BufferedWriter outHDG = new BufferedWriter(osrHDG);
            FileOutputStream oisHGG = new FileOutputStream(
                    "olddata/visa_HGG.csv");
            OutputStreamWriter osrHGG = new OutputStreamWriter(oisHGG, "UTF-8");
            BufferedWriter outHGG = new BufferedWriter(osrHGG);
            FileOutputStream oisHGG2 = new FileOutputStream(
                    "olddata/visa_HGG2.csv");
            OutputStreamWriter osrHGG2 = new OutputStreamWriter(oisHGG2,
                    "UTF-8");
            BufferedWriter outHGG2 = new BufferedWriter(osrHGG2);
            FileOutputStream oisHNI = new FileOutputStream(
                    "olddata/visa_HNI.csv");
            OutputStreamWriter osrHNI = new OutputStreamWriter(oisHNI, "UTF-8");
            BufferedWriter outHNI = new BufferedWriter(osrHNI);
            FileOutputStream oisHNM = new FileOutputStream(
                    "olddata/visa_HNM.csv");
            OutputStreamWriter osrHNM = new OutputStreamWriter(oisHNM, "UTF-8");
            BufferedWriter outHNM = new BufferedWriter(osrHNM);
            FileOutputStream oisHPG = new FileOutputStream(
                    "olddata/visa_HPG.csv");
            OutputStreamWriter osrHPG = new OutputStreamWriter(oisHPG, "UTF-8");
            BufferedWriter outHPG = new BufferedWriter(osrHPG);
            FileOutputStream oisHTH = new FileOutputStream(
                    "olddata/visa_HTH.csv");
            OutputStreamWriter osrHTH = new OutputStreamWriter(oisHTH, "UTF-8");
            BufferedWriter outHTH = new BufferedWriter(osrHTH);
            FileOutputStream oisHUE = new FileOutputStream(
                    "olddata/visa_HUE.csv");
            OutputStreamWriter osrHUE = new OutputStreamWriter(oisHUE, "UTF-8");
            BufferedWriter outHUE = new BufferedWriter(osrHUE);
            FileOutputStream oisHYN = new FileOutputStream(
                    "olddata/visa_HYN.csv");
            OutputStreamWriter osrHYN = new OutputStreamWriter(oisHYN, "UTF-8");
            BufferedWriter outHYN = new BufferedWriter(osrHYN);
            FileOutputStream oisKHA = new FileOutputStream(
                    "olddata/visa_KHA.csv");
            OutputStreamWriter osrKHA = new OutputStreamWriter(oisKHA, "UTF-8");
            BufferedWriter outKHA = new BufferedWriter(osrKHA);
            FileOutputStream oisKTM = new FileOutputStream(
                    "olddata/visa_KTM.csv");
            OutputStreamWriter osrKTM = new OutputStreamWriter(oisKTM, "UTF-8");
            BufferedWriter outKTM = new BufferedWriter(osrKTM);
            FileOutputStream oisLAN = new FileOutputStream(
                    "olddata/visa_LAN.csv");
            OutputStreamWriter osrLAN = new OutputStreamWriter(oisLAN, "UTF-8");
            BufferedWriter outLAN = new BufferedWriter(osrLAN);
            FileOutputStream oisLCI = new FileOutputStream(
                    "olddata/visa_LCI.csv");
            OutputStreamWriter osrLCI = new OutputStreamWriter(oisLCI, "UTF-8");
            BufferedWriter outLCI = new BufferedWriter(osrLCI);
            FileOutputStream oisLCU = new FileOutputStream(
                    "olddata/visa_LCU.csv");
            OutputStreamWriter osrLCU = new OutputStreamWriter(oisLCU, "UTF-8");
            BufferedWriter outLCU = new BufferedWriter(osrLCU);
            FileOutputStream oisLDG = new FileOutputStream(
                    "olddata/visa_LDG.csv");
            OutputStreamWriter osrLDG = new OutputStreamWriter(oisLDG, "UTF-8");
            BufferedWriter outLDG = new BufferedWriter(osrLDG);
            FileOutputStream oisLSN = new FileOutputStream(
                    "olddata/visa_LSN.csv");
            OutputStreamWriter osrLSN = new OutputStreamWriter(oisLSN, "UTF-8");
            BufferedWriter outLSN = new BufferedWriter(osrLSN);
            FileOutputStream oisNAN = new FileOutputStream(
                    "olddata/visa_NAN.csv");
            OutputStreamWriter osrNAN = new OutputStreamWriter(oisNAN, "UTF-8");
            BufferedWriter outNAN = new BufferedWriter(osrNAN);
            FileOutputStream oisNBH = new FileOutputStream(
                    "olddata/visa_NBH.csv");
            OutputStreamWriter osrNBH = new OutputStreamWriter(oisNBH, "UTF-8");
            BufferedWriter outNBH = new BufferedWriter(osrNBH);
            FileOutputStream oisNDH = new FileOutputStream(
                    "olddata/visa_NDH.csv");
            OutputStreamWriter osrNDH = new OutputStreamWriter(oisNDH, "UTF-8");
            BufferedWriter outNDH = new BufferedWriter(osrNDH);
            FileOutputStream oisNTN = new FileOutputStream(
                    "olddata/visa_NTN.csv");
            OutputStreamWriter osrNTN = new OutputStreamWriter(oisNTN, "UTF-8");
            BufferedWriter outNTN = new BufferedWriter(osrNTN);
            FileOutputStream oisPTO = new FileOutputStream(
                    "olddata/visa_PTO.csv");
            OutputStreamWriter osrPTO = new OutputStreamWriter(oisPTO, "UTF-8");
            BufferedWriter outPTO = new BufferedWriter(osrPTO);
            FileOutputStream oisPYN = new FileOutputStream(
                    "olddata/visa_PYN.csv");
            OutputStreamWriter osrPYN = new OutputStreamWriter(oisPYN, "UTF-8");
            BufferedWriter outPYN = new BufferedWriter(osrPYN);
            FileOutputStream oisQBH = new FileOutputStream(
                    "olddata/visa_QBH.csv");
            OutputStreamWriter osrQBH = new OutputStreamWriter(oisQBH, "UTF-8");
            BufferedWriter outQBH = new BufferedWriter(osrQBH);
            FileOutputStream oisQNH = new FileOutputStream(
                    "olddata/visa_QNH.csv");
            OutputStreamWriter osrQNH = new OutputStreamWriter(oisQNH, "UTF-8");
            BufferedWriter outQNH = new BufferedWriter(osrQNH);
            FileOutputStream oisQNI = new FileOutputStream(
                    "olddata/visa_QNI.csv");
            OutputStreamWriter osrQNI = new OutputStreamWriter(oisQNI, "UTF-8");
            BufferedWriter outQNI = new BufferedWriter(osrQNI);
            FileOutputStream oisQNM = new FileOutputStream(
                    "olddata/visa_QNM.csv");
            OutputStreamWriter osrQNM = new OutputStreamWriter(oisQNM, "UTF-8");
            BufferedWriter outQNM = new BufferedWriter(osrQNM);
            FileOutputStream oisQTI = new FileOutputStream(
                    "olddata/visa_QTI.csv");
            OutputStreamWriter osrQTI = new OutputStreamWriter(oisQTI, "UTF-8");
            BufferedWriter outQTI = new BufferedWriter(osrQTI);
            FileOutputStream oisSLA = new FileOutputStream(
                    "olddata/visa_SLA.csv");
            OutputStreamWriter osrSLA = new OutputStreamWriter(oisSLA, "UTF-8");
            BufferedWriter outSLA = new BufferedWriter(osrSLA);
            FileOutputStream oisSST = new FileOutputStream(
                    "olddata/visa_SST.csv");
            OutputStreamWriter osrSST = new OutputStreamWriter(oisSST, "UTF-8");
            BufferedWriter outSST = new BufferedWriter(osrSST);
            FileOutputStream oisSTG = new FileOutputStream(
                    "olddata/visa_STG.csv");
            OutputStreamWriter osrSTG = new OutputStreamWriter(oisSTG, "UTF-8");
            BufferedWriter outSTG = new BufferedWriter(osrSTG);
            FileOutputStream oisTBH = new FileOutputStream(
                    "olddata/visa_TBH.csv");
            OutputStreamWriter osrTBH = new OutputStreamWriter(oisTBH, "UTF-8");
            BufferedWriter outTBH = new BufferedWriter(osrTBH);
            FileOutputStream oisTGG = new FileOutputStream(
                    "olddata/visa_TGG.csv");
            OutputStreamWriter osrTGG = new OutputStreamWriter(oisTGG, "UTF-8");
            BufferedWriter outTGG = new BufferedWriter(osrTGG);
            FileOutputStream oisTHA = new FileOutputStream(
                    "olddata/visa_THA.csv");
            OutputStreamWriter osrTHA = new OutputStreamWriter(oisTHA, "UTF-8");
            BufferedWriter outTHA = new BufferedWriter(osrTHA);
            FileOutputStream oisTNH = new FileOutputStream(
                    "olddata/visa_TNH.csv");
            OutputStreamWriter osrTNH = new OutputStreamWriter(oisTNH, "UTF-8");
            BufferedWriter outTNH = new BufferedWriter(osrTNH);
            FileOutputStream oisTNN = new FileOutputStream(
                    "olddata/visa_TNN.csv");
            OutputStreamWriter osrTNN = new OutputStreamWriter(oisTNN, "UTF-8");
            BufferedWriter outTNN = new BufferedWriter(osrTNN);
            FileOutputStream oisTQG = new FileOutputStream(
                    "olddata/visa_TQG.csv");
            OutputStreamWriter osrTQG = new OutputStreamWriter(oisTQG, "UTF-8");
            BufferedWriter outTQG = new BufferedWriter(osrTQG);
            FileOutputStream oisTVH = new FileOutputStream(
                    "olddata/visa_TVH.csv");
            OutputStreamWriter osrTVH = new OutputStreamWriter(oisTVH, "UTF-8");
            BufferedWriter outTVH = new BufferedWriter(osrTVH);
            FileOutputStream oisVLG = new FileOutputStream(
                    "olddata/visa_VLG.csv");
            OutputStreamWriter osrVLG = new OutputStreamWriter(oisVLG, "UTF-8");
            BufferedWriter outVLG = new BufferedWriter(osrVLG);
            FileOutputStream oisVPC = new FileOutputStream(
                    "olddata/visa_VPC.csv");
            OutputStreamWriter osrVPC = new OutputStreamWriter(oisVPC, "UTF-8");
            BufferedWriter outVPC = new BufferedWriter(osrVPC);
            FileOutputStream oisVTU = new FileOutputStream(
                    "olddata/visa_VTU.csv");
            OutputStreamWriter osrVTU = new OutputStreamWriter(oisVTU, "UTF-8");
            BufferedWriter outVTU = new BufferedWriter(osrVTU);
            FileOutputStream oisYBI = new FileOutputStream(
                    "olddata/visa_YBI.csv");
            OutputStreamWriter osrYBI = new OutputStreamWriter(oisYBI, "UTF-8");
            BufferedWriter outYBI = new BufferedWriter(osrYBI);

            FileOutputStream ois = new FileOutputStream("olddata/visa.csv");
            OutputStreamWriter osr = new OutputStreamWriter(ois, "UTF-8");
            BufferedWriter out = new BufferedWriter(osr);

            StringUtil strU = new StringUtil();
            String line;
            int i = 0;
            while ((line = in.readLine()) != null) {
                i++;
                String[] str = strU.toStringArray(line, "|");
                if (str.length != 13) {
                    System.out.println("Row " + i + " :" + line);
                } else {
                    if (str[10].equalsIgnoreCase("AGG")) {
                        outAGG.write(line);
                    } else if (str[10].equalsIgnoreCase("BDG")) {
                        outBDG.write(line);
                    } else if (str[10].equalsIgnoreCase("BDH")) {
                        outBDH.write(line);
                    } else if (str[10].equalsIgnoreCase("BGG")) {
                        outBGG.write(line);
                    } else if (str[10].equalsIgnoreCase("BKN")) {
                        outBKN.write(line);
                    } else if (str[10].equalsIgnoreCase("BLU")) {
                        outBLU.write(line);
                    } else if (str[10].equalsIgnoreCase("BNH")) {
                        outBNH.write(line);
                    } else if (str[10].equalsIgnoreCase("BPC")) {
                        outBPC.write(line);
                    } else if (str[10].equalsIgnoreCase("BTN")) {
                        outBTN.write(line);
                    } else if (str[10].equalsIgnoreCase("CBG")) {
                        outCBG.write(line);
                    } else if (str[10].equalsIgnoreCase("CMU")) {
                        outCMU.write(line);
                    } else if (str[10].equalsIgnoreCase("CTO")) {
                        outCTO.write(line);
                    } else if (str[10].equalsIgnoreCase("DAN")) {
                        outDAN.write(line);
                    } else if (str[10].equalsIgnoreCase("DBN")) {
                        outDBN.write(line);
                    } else if (str[10].equalsIgnoreCase("DLK")) {
                        outDLK.write(line);
                    } else if (str[10].equalsIgnoreCase("DNG")) {
                        outDNG.write(line);
                    } else if (str[10].equalsIgnoreCase("DNI")) {
                        outDNI.write(line);
                    } else if (str[10].equalsIgnoreCase("DTP")) {
                        outDTP.write(line);
                    } else if (str[10].equalsIgnoreCase("GLI")) {
                        outGLI.write(line);
                    } else if (str[10].equalsIgnoreCase("HBH")) {
                        outHBH.write(line);
                    } else if (str[10].equalsIgnoreCase("HCM")) {
                        outHCM.write(line);
                    } else if (str[10].equalsIgnoreCase("HDG")) {
                        outHDG.write(line);
                    } else if (str[10].equalsIgnoreCase("HGG")) {
                        outHGG.write(line);
                    } else if (str[10].equalsIgnoreCase("HGG2")) {
                        outHGG2.write(line);
                    } else if (str[10].equalsIgnoreCase("HNI")) {
                        outHNI.write(line);
                    } else if (str[10].equalsIgnoreCase("HNM")) {
                        outHNM.write(line);
                    } else if (str[10].equalsIgnoreCase("HPG")) {
                        outHPG.write(line);
                    } else if (str[10].equalsIgnoreCase("HTH")) {
                        outHTH.write(line);
                    } else if (str[10].equalsIgnoreCase("HUE")) {
                        outHUE.write(line);
                    } else if (str[10].equalsIgnoreCase("HYN")) {
                        outHYN.write(line);
                    } else if (str[10].equalsIgnoreCase("KHA")) {
                        outKHA.write(line);
                    } else if (str[10].equalsIgnoreCase("KTM")) {
                        outKTM.write(line);
                    } else if (str[10].equalsIgnoreCase("LAN")) {
                        outLAN.write(line);
                    } else if (str[10].equalsIgnoreCase("LCI")) {
                        outLCI.write(line);
                    } else if (str[10].equalsIgnoreCase("LCU")) {
                        outLCU.write(line);
                    } else if (str[10].equalsIgnoreCase("LDG")) {
                        outLDG.write(line);
                    } else if (str[10].equalsIgnoreCase("LSN")) {
                        outLSN.write(line);
                    } else if (str[10].equalsIgnoreCase("NAN")) {
                        outNAN.write(line);
                    } else if (str[10].equalsIgnoreCase("NBH")) {
                        outNBH.write(line);
                    } else if (str[10].equalsIgnoreCase("NDH")) {
                        outNDH.write(line);
                    } else if (str[10].equalsIgnoreCase("NTN")) {
                        outNTN.write(line);
                    } else if (str[10].equalsIgnoreCase("PTO")) {
                        outPTO.write(line);
                    } else if (str[10].equalsIgnoreCase("PYN")) {
                        outPYN.write(line);
                    } else if (str[10].equalsIgnoreCase("QBH")) {
                        outQBH.write(line);
                    } else if (str[10].equalsIgnoreCase("QNH")) {
                        outQNH.write(line);
                    } else if (str[10].equalsIgnoreCase("QNI")) {
                        outQNI.write(line);
                    } else if (str[10].equalsIgnoreCase("QNM")) {
                        outQNM.write(line);
                    } else if (str[10].equalsIgnoreCase("QTI")) {
                        outQTI.write(line);
                    } else if (str[10].equalsIgnoreCase("SLA")) {
                        outSLA.write(line);
                    } else if (str[10].equalsIgnoreCase("SST")) {
                        outSST.write(line);
                    } else if (str[10].equalsIgnoreCase("STG")) {
                        outSTG.write(line);
                    } else if (str[10].equalsIgnoreCase("TBH")) {
                        outTBH.write(line);
                    } else if (str[10].equalsIgnoreCase("TGG")) {
                        outTGG.write(line);
                    } else if (str[10].equalsIgnoreCase("THA")) {
                        outTHA.write(line);
                    } else if (str[10].equalsIgnoreCase("TNH")) {
                        outTNH.write(line);
                    } else if (str[10].equalsIgnoreCase("TNN")) {
                        outTNN.write(line);
                    } else if (str[10].equalsIgnoreCase("TQG")) {
                        outTQG.write(line);
                    } else if (str[10].equalsIgnoreCase("TVH")) {
                        outTVH.write(line);
                    } else if (str[10].equalsIgnoreCase("VLG")) {
                        outVLG.write(line);
                    } else if (str[10].equalsIgnoreCase("VPC")) {
                        outVPC.write(line);
                    } else if (str[10].equalsIgnoreCase("VTU")) {
                        outVTU.write(line);
                    } else if (str[10].equalsIgnoreCase("YBI")) {
                        outYBI.write(line);
                    } else {
                        out.write(line);
                    }
                }
            }
            in.close();
            out.close();
            outAGG.close();
            outBDG.close();
            outBDH.close();
            outBGG.close();
            outBKN.close();
            outBLU.close();
            outBNH.close();
            outBPC.close();
            outBTN.close();
            outCBG.close();
            outCMU.close();
            outCTO.close();
            outDAN.close();
            outDBN.close();
            outDLK.close();
            outDNG.close();
            outDNI.close();
            outDTP.close();
            outGLI.close();
            outHBH.close();
            outHCM.close();
            outHDG.close();
            outHGG.close();
            outHGG2.close();
            outHNI.close();
            outHNM.close();
            outHPG.close();
            outHTH.close();
            outHUE.close();
            outHYN.close();
            outKHA.close();
            outKTM.close();
            outLAN.close();
            outLCI.close();
            outLCU.close();
            outLDG.close();
            outLSN.close();
            outNAN.close();
            outNBH.close();
            outNDH.close();
            outNTN.close();
            outPTO.close();
            outPYN.close();
            outQBH.close();
            outQNH.close();
            outQNI.close();
            outQNM.close();
            outQTI.close();
            outSLA.close();
            outSST.close();
            outSTG.close();
            outTBH.close();
            outTGG.close();
            outTHA.close();
            outTNH.close();
            outTNN.close();
            outTQG.close();
            outTVH.close();
            outVLG.close();
            outVPC.close();
            outVTU.close();
            outYBI.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SplitVISA splitvisa = new SplitVISA();
        splitvisa.processFile();

    }
}
