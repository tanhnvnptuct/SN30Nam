package vnp.lottery;

import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import smartlib.thread.ThreadParameter;
import smartlib.util.AppException;
import vnp.bean.CardItem;
import vnp.thread.PortalThread;
import vnp.util.AppUtil;
import vnp.util.ParameterType;
import vnp.util.ResponseUtil;

public class LotteryCode_ver2 extends PortalThread {
  BlockingQueue<CardItem> queue = null;
  
  private int campaignId = 184;
  
  private int add_days = 0;
  
  private int batch_count = 1000;
  
  private String api_gencode_url = "http://10.149.248.19:8080/api/Lottery/GenCodesOff";
  
  private int sub_type = 0;
  
  public String getMyConnName() {
    return "PORTAL_63_promotion";
  }
  
  public void beforeSession() throws Exception {
    super.beforeSession();
  }
  
  protected void processSession() throws Exception {
    this.queue = (BlockingQueue<CardItem>)getCommonVariable("QueueCardItem");
    ResponseUtil client = new ResponseUtil();
    int count = 0;
    while (!this.queue.isEmpty() && count < this.batch_count) {
      CardItem item = this.queue.take();
      try {
        count++;
        String msisdn = AppUtil.formatFullISDN(item.get_msisdn());
        String json_data = "{\"campaign_id\":" + this.campaignId + "," + 
          "    \"msisdn\":\"" + msisdn + "\"," + 
          "    \"numofcode\":" + item.get_nofCodes() + "," + 
          "    \"substype\":" + item.get_subtype() + "," + 
          "    \"backdays\":" + this.add_days + "}";
        String response = client.getResponse(json_data, this.api_gencode_url);
        if (!response.contains("sucess")) {
          logMonitor("err:" + response);
          logMonitor(this.api_gencode_url);
        } 
      } catch (Exception ex) {
        logMonitor("ProcessSession exception: " + ex.getMessage());
        this.queue.put(item);
        Thread.sleep(1000L);
      } 
    } 
    logMonitor("Processed items: " + count);
  }
  
  public Vector getParameterDefinition() {
    Vector<ThreadParameter> vtReturn = new Vector();
    vtReturn.addElement(createParameterDefinition("api_gencode_url", this.api_gencode_url, 
          ParameterType.PARAM_TEXTBOX_MAX, "10000"));
    vtReturn.addElement(
        createParameterDefinition("campaignId", Integer.valueOf(this.campaignId), ParameterType.PARAM_TEXTBOX_MAX, "10000"));
    vtReturn.addElement(
        createParameterDefinition("batch_count", Integer.valueOf(this.batch_count), ParameterType.PARAM_TEXTBOX_MAX, "10000"));
    vtReturn.addElement(createParameterDefinition("add_days", Integer.valueOf(this.add_days), ParameterType.PARAM_TEXTBOX_MAX, "10000"));
    vtReturn.addAll(super.getParameterDefinition());
    return vtReturn;
  }
  
  public void fillParameter() throws AppException {
    this.api_gencode_url = loadString("api_gencode_url");
    this.campaignId = loadInteger("campaignId");
    this.batch_count = loadInteger("batch_count");
    this.add_days = loadInteger("add_days");
    super.fillParameter();
  }
}
