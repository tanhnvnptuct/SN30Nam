package vnp.thread;

import java.net.*;
import java.io.*;

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
public class Untitled2 {
    public String get() {
        String strReturn = "";
        try {
            URL yahoo = new URL("http://10.1.10.175/ProvinceVasReceiver/Receiver?reqid=1453&eventName=ACTIVE_TS_088&msisdn=84912050050&timeActive=20160229081224&typeKitSub=TS_088&note=KM_TS_088");
            URLConnection yc = yahoo.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
//                System.out.println(inputLine);
                return inputLine;
            }
            in.close();
        } catch (Exception ex) {
            ex.getMessage();
        }
        return strReturn;
    }

    public static void main(String[] args) {
        Untitled2 un = new Untitled2();
        System.out.print(un.get());

    }
}
