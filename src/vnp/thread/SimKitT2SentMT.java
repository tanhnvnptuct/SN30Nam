/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vnp.thread;

import java.sql.CallableStatement;
import java.util.Vector;
import smartlib.util.AppException;
import vnp.util.ParameterType;

/**
 *
 * @author vovan
 */
public class SimKitT2SentMT extends PortalThread {

    private int campaignid;
    private String message;
    private String shortcode;
    private Integer count;

    //int interval;
    @Override
    protected void processSession() throws Exception {

        int count = 0;
        //logMonitor("Init data T - " + interval);
        CallableStatement stmt = null;

//        while (miThreadCommand != ThreadConstant.THREAD_STOP) {
        try {

            System.out.println("Get connection " + mcnMain);
            String sql = "begin ?:=MCA_RBT_CAMPAIGN.fSendSMSbyCampaign(?,?,?); end;";
            stmt = mcnMain.prepareCall(sql);
            stmt.registerOutParameter(1, java.sql.Types.INTEGER);
            stmt.setInt(2, campaignid);
            stmt.setString(3, message);
            stmt.setString(4, shortcode);
            stmt.executeQuery();
            count = stmt.getInt(1);
            logMonitor(count + " SMS sent");
        } catch (Exception e) {
            logMonitor("Exception: " + e.getMessage());
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public Vector getParameterDefinition() {
        Vector vtReturn = new Vector();
        ////////////////////////////////////////////////////////
        vtReturn.addElement(createParameterDefinition("campaignid", "", ParameterType.PARAM_TEXTBOX_MAX, "10000"));
        vtReturn.addElement(createParameterDefinition("message", "", ParameterType.PARAM_TEXTBOX_MAX, "10000"));
        vtReturn.addElement(createParameterDefinition("shortcode", "", ParameterType.PARAM_TEXTBOX_MAX, "10000"));
//        vtReturn.addElement(createParameterDefinition("interval", "", ParameterType.PARAM_TEXTBOX_MASK, "9999999"));
        vtReturn.addAll(super.getParameterDefinition());
        return vtReturn;
    }

    public void fillParameter() throws AppException {
        campaignid = loadInteger("campaignid");
        message = loadString("message");
        shortcode = loadString("shortcode");
        //  interval = loadInteger("interval");
    }

}
