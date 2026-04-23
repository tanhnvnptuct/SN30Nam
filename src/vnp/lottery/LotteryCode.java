package vnp.lottery;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import com.fasterxml.jackson.databind.ObjectMapper;

import smartlib.util.AppException;
import vnp.bean.CardItem;
import vnp.thread.PortalThread;
import vnp.util.AppUtil;
import vnp.util.CommonVars;
import vnp.util.ParameterType;
import vnp.util.ResponseUtil;

public class LotteryCode extends PortalThread {
	BlockingQueue<CardItem> queue = null;

	private int campaignId = 184;
	private int add_days = 0;
	private int batch_count = 1000;
	private String api_gencode_url = "http://10.149.248.64:8002/CommonLotteryV2/api/GenCodesOff/";

	private int sub_type = 0; // 0: tra truoc |||||||| 1: tra sau

	@Override
	public String getMyConnName() {
		return "DBAPP_IOS";
	};

	@Override
	public void beforeSession() throws Exception {
		super.beforeSession();

	}

	@SuppressWarnings({ "unchecked" })
	@Override
	protected void processSession() throws Exception {

		//queue = (BlockingQueue<CardItem>) getCommonVariable(CommonVars.QUEUE_MDT);
		queue = (BlockingQueue<CardItem>) getCommonVariable(CommonVars.QUEUE_CARDITEM);

		ResponseUtil client = new ResponseUtil();
		int count = 0;
		while (!queue.isEmpty() && count < batch_count) {
			CardItem item = queue.take();
			try {

				count++;

				String msisdn = AppUtil.formatFullISDN(item.get_msisdn());

//				String getUrl = api_gencode_url + new Date().getTime() + "/" + campaignId + "/" + msisdn + "/"
//						+ item.get_nofCodes() + "/" + item.get_subtype() + "/" + add_days;
				
				String getUrl = api_gencode_url + new Date().getTime() + "/" + campaignId + "/" + msisdn + "/"
						+ item.get_nofCodes() + "/" + item.get_subtype() + "/" + item.get_addday();
				
				//logMonitor(getUrl);
				

//				String response = client.getJson(getUrl);
//
//				HashMap<String, Object> result = new ObjectMapper().readValue(response, HashMap.class);
//
//				if (!"sucess".equalsIgnoreCase(result.get("status").toString())) {
//					logMonitor(response);
//				}
				
				String response = client.getHttpsJson(getUrl);
				
				if (!response.contains("sucess")) {
					logMonitor("err:"+response);
					logMonitor(getUrl);
				}
				
			} catch (Exception ex) {
				logMonitor("ProcessSession exception: " + ex.getMessage());
				queue.put(item);
				Thread.sleep(1000);
			} 
		}
		logMonitor("Processed items: " + count);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		////////////////////////////////////////////////////////
		vtReturn.addElement(createParameterDefinition("api_gencode_url", api_gencode_url,
				ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(
				createParameterDefinition("campaignId", campaignId, ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(
				createParameterDefinition("batch_count", batch_count, ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(createParameterDefinition("add_days", add_days, ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	public void fillParameter() throws AppException {
		api_gencode_url = loadString("api_gencode_url");
		campaignId = loadInteger("campaignId");
		batch_count = loadInteger("batch_count");
		add_days = loadInteger("add_days");
		super.fillParameter();
	}
}
