package vnp.lottery;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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
		return "PORTAL_63_winner";
	};

	private long lastSeq = 0;
	private int amount_per_code = 0;
	private String SQL_STMT = "Select * From TOPUPS Where ID>?";
	private String msg_invite = "";
	private String SQL_GetNofCodes = "{?=call pkg_km_quy3_2020.fget_NofCodes_inDay(?,?)}";


	@SuppressWarnings("unchecked")
	@Override
	protected void processSession() throws Exception {
		PreparedStatement ps = null;
		CallableStatement cs = null;
		ResultSet rs = null;

		try {
			String logMsg = "From Seq: " + lastSeq;

			//BlockingQueue<SmsMt> brc_queue = (BlockingQueue<SmsMt>) getCommonVariable(CommonVars.QUEUE_BRC);
			BlockingQueue<CardItem> card_queue = (BlockingQueue<CardItem>) getCommonVariable(CommonVars.QUEUE_CARDITEM);

			ps = mcnMain.prepareStatement(SQL_STMT);
			ps.setLong(1, lastSeq);
			
			cs = mcnMain.prepareCall(SQL_GetNofCodes);
			cs.registerOutParameter(1, Types.INTEGER);
			

			rs = ps.executeQuery();
			int count = 0;

			while (rs.next()) {
				count++;
				CardItem item = populateCardItem(rs);
				
				
				if (!item.get_msisdn().substring(0,2).equals("87")&& !item.get_msisdn().substring(0,2).equals("99")
						&& !item.get_msisdn().substring(0,2).equals("59")&& !item.get_msisdn().substring(0,2).equals("55")) {
					
					cs.setString(2, item.get_msisdn());
					cs.setLong(3, item.get_amount());
					cs.execute();
					int nofCode = cs.getInt(1);
					
					if (nofCode > 0) {
						item.set_nofCodes(nofCode);
						card_queue.put(item);
					} 
				} 
				
				if (lastSeq < item.get_id())
					lastSeq = item.get_id();
			
			}
			
			
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
		vtReturn.addElement(createParameterDefinition("amount_per_code", amount_per_code, ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(
				createParameterDefinition("msg_invite", msg_invite, ParameterType.PARAM_TEXTBOX_MAX, "10000"));

		vtReturn.addAll(super.getParameterDefinition());
		
		return vtReturn;
	}

	public void fillParameter() throws AppException {
		lastSeq = loadLong("lastSeq");
		amount_per_code = loadInteger("amount_per_code");
		msg_invite = loadString("msg_invite");
		super.fillParameter();
	}

}
