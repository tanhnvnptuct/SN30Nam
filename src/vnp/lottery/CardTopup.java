package vnp.lottery;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import com.fss.sql.Database;

import smartlib.util.AppException;
import vnp.bean.CardItem;
import vnp.bean.SmsMt;
import vnp.thread.PortalThread;
import vnp.util.CommonVars;
import vnp.util.ParameterType;

public class CardTopup extends PortalThread {

	@Override
	public String getMyConnName() {
		return "PORTAL_63";
	};

	private long lastSeq = 0;
	private String SQL_STMT = "Select * From TOPUPS Where ID>?";
	private String msg_invite = "";
	

	@SuppressWarnings("unchecked")
	@Override
	protected void processSession() throws Exception {
		PreparedStatement ps = null;

		ResultSet rs = null;

		try {
			String logMsg = "From Seq: " + lastSeq;

			//BlockingQueue<SmsMt> brc_queue = (BlockingQueue<SmsMt>) getCommonVariable(CommonVars.QUEUE_BRC);
			BlockingQueue<CardItem> card_queue = (BlockingQueue<CardItem>) getCommonVariable(CommonVars.QUEUE_CARDITEM);

			ps = mcnMain.prepareStatement(SQL_STMT);
			ps.setLong(1, lastSeq);

			rs = ps.executeQuery();
			int count = 0;

			while (rs.next()) {
				count++;
				CardItem item = populateCardItem(rs);
				if (item.get_amount() >= 20000 && item.get_msisdn().substring(0,2)!="87") {
					item.set_nofCodes((int)item.get_amount()/20000);
					card_queue.put(item);
				} 
				
//				int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
//				if (hour_of_day >= 8 && hour_of_day < 19 && rs.getInt("AMOUNT") >= 50000) {
//						SmsMt mt = new SmsMt();
//						mt.setSmsContent(msg_invite);
//						mt.setMsisdn(item.get_msisdn());
//						brc_queue.add(mt);
//				} 

				if (lastSeq < item.get_id())
					lastSeq = item.get_id();
			
			}
			
//			SmsMt mt = new SmsMt();
//			//mt.setShortCode(cfg_sms_code);
//			mt.setSmsContent(msg_invite);
//			mt.setMsisdn("943863097");
////			mt.setMsisdn("941233764");
//			brc_queue.add(mt);
			
			
			if (count > 0) {
				saveLastSequence(lastSeq);
				logMonitor(logMsg + " To " + lastSeq + ": " + count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logMonitor(e.getMessage());
			saveLastSequence(lastSeq);
			Thread.sleep(1000);
		} finally {
			Database.closeObject(rs);
			Database.closeObject(ps);
		}

	}

	private void saveLastSequence(Long lastSeq) throws Exception {
		setParameter("lastSeq", lastSeq);
		storeConfig();
	}

	private CardItem populateCardItem(ResultSet rs) throws SQLException {
		CardItem item = new CardItem();
		item.set_id(rs.getLong("ID"));
		item.set_msisdn(rs.getString("subscriber"));
		item.set_amount(rs.getInt("AMOUNT"));
		item.set_adddays(0);
		item.set_subtype(0);

		return item;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		////////////////////////////////////////////////////////
		vtReturn.addElement(createParameterDefinition("lastSeq", lastSeq, ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(
				createParameterDefinition("msg_invite", msg_invite, ParameterType.PARAM_TEXTBOX_MAX, "10000"));

		vtReturn.addAll(super.getParameterDefinition());
		
		return vtReturn;
	}

	public void fillParameter() throws AppException {
		lastSeq = loadLong("lastSeq");
		msg_invite = loadString("msg_invite");
		super.fillParameter();
	}

}
