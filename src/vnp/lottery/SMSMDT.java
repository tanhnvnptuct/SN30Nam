package vnp.lottery;

import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import smartlib.util.AppException;
import vnp.bean.SmsMt;
import vnp.thread.PortalThread;
import vnp.util.AppUtil;
import vnp.util.CommonVars;
import vnp.util.ParameterType;

public class SMSMDT extends PortalThread {
	BlockingQueue<SmsMt> queue = null;
	private int substr_mdt_1 = 0;
	private int substr_mdt_n = 0;
	private String cfg_sms_code1 = "SMS_MDT1";
	@Override
	public String getMyConnName() {
		return "PORTAL_63_promotion";
	};

	@Override
	public void beforeSession() throws Exception {
		// TODO Auto-generated method stub
		super.beforeSession();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void processSession() throws Exception {
		CallableStatement stm = mcnMain
				.prepareCall("insert into sms_mt_1558 (id,msisdn,sms_content,CAMPAIGN_ID) values (seq_sms_mt.nextval,?,?,448)");
		// stm.registerOutParameter(1, Types.INTEGER);
		queue = (BlockingQueue<SmsMt>) getCommonVariable(CommonVars.QUEUE_MDT);
		int count = 0;
		List<SmsMt> sendingMts = new ArrayList<SmsMt>();
		try {
			while (!queue.isEmpty() && count <= 1000) {
				SmsMt item = queue.take();
				count++;
				// stm.setString(2, item.getShortCode());
			
				
				//lon hon 220 thi tach lam 2 tin nhan
				if (item.getSmsContent().length()>=220){
					//neu co 1 mdt
					if(item.getMoId()==1){
						
						//tin nhan dau tu 0-200 ky tu dau tien
						stm.setString(1, item.getMsisdn());
						stm.setString(2, item.getSmsContent().substring(0,substr_mdt_1));
						// stm.setInt(5, item.getStatus().intValue());
						stm.addBatch();
						
						//tin nhan hai gom cac ky tu con lai thi gui truoc
						stm.setString(1, item.getMsisdn());
						stm.setString(2, item.getSmsContent().substring(substr_mdt_1));
						// stm.setInt(5, item.getStatus().intValue());
						stm.addBatch();
						
						
						
					}
					//neu co nhieu mdt thi MoID = 2
					else{
						//tin nhan dau tu 0-200 ky tu dau tien
						stm.setString(1, item.getMsisdn());
						stm.setString(2, item.getSmsContent().substring(0,substr_mdt_n));
						// stm.setInt(5, item.getStatus().intValue());
						stm.addBatch();
						

						//tin nhan hai gom cac ky tu con lai thi gui truoc
						stm.setString(1, item.getMsisdn());
						stm.setString(2, item.getSmsContent().substring(substr_mdt_n));
						// stm.setInt(5, item.getStatus().intValue());
						stm.addBatch();
						
					
					}
					
					//sendingMts.add(item);
				}
				else{
					stm.setString(1, AppUtil.formatFullISDN(item.getMsisdn()));
					stm.setString(2, item.getSmsContent());
					// stm.setInt(5, item.getStatus().intValue());
					stm.addBatch();
					//sendingMts.add(item);
				}
				
			}
			if (count > 0) {
				logMonitor("Sending: " + count);
				stm.executeBatch();
			}
		} catch (Exception ex) {
			logMonitor(ex.getMessage());
//			for (SmsMt item : sendingMts)
//				queue.put(item);
//			Thread.sleep(1000);
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		////////////////////////////////////////////////////////
		vtReturn.addElement(createParameterDefinition("batch_count", "1000", ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(createParameterDefinition("substr_mdt_1", substr_mdt_1, ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(createParameterDefinition("substr_mdt_n", substr_mdt_n, ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	public void fillParameter() throws AppException {

		loadInteger("batch_count");
		substr_mdt_1 = loadInteger("substr_mdt_1");
		substr_mdt_n = loadInteger("substr_mdt_n");

		super.fillParameter();
	}
}
