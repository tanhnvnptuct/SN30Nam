/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vnp.thread;

import java.util.Vector;

import smartlib.util.AppException;
import vnp.util.ParameterType;

/**
 *
 * @author vovan
 */
public class SimKitT2InitData extends PortalThread {

    private String[] campaignids;

    int interval;

    @Override
    protected void processSession() throws Exception {
       
    }

    public Vector getParameterDefinition() {
        Vector vtReturn = new Vector();
        ////////////////////////////////////////////////////////
        vtReturn.addElement(createParameterDefinition("campaignids", "", ParameterType.PARAM_TEXTBOX_MAX, "10000"));
//        vtReturn.addElement(createParameterDefinition("interval", "", ParameterType.PARAM_TEXTBOX_MASK, "9999999"));
        vtReturn.addAll(super.getParameterDefinition());
        return vtReturn;
    }

    public void fillParameter() throws AppException {
//        String campaignidArray = loadString("campaignids");
//        campaignids = campaignidArray.split("\\|");
//        interval = loadInteger("interval");
    	super.fillParameter();
    }

}
