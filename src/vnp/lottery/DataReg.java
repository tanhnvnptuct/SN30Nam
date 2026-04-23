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

public class DataReg extends PortalThread {

	@Override
	public String getMyConnName() {
		return "PORTAL_63_promotion";
	};

	private long lastSeq = 0;
	private String SQL_STMT = "Select * From data_srv_381132 Where ID>?";
	
	

	@SuppressWarnings("unchecked")
	@Override
	protected void processSession() throws Exception {
		PreparedStatement ps = null;

		ResultSet rs = null;

		try {
			String logMsg = "From Seq: " + lastSeq;

			BlockingQueue<CardItem> card_queue = (BlockingQueue<CardItem>) getCommonVariable(CommonVars.QUEUE_CARDITEM);

			ps = mcnMain.prepareStatement(SQL_STMT);
			ps.setLong(1, lastSeq);

			rs = ps.executeQuery();
			int count = 0;

			while (rs.next()) {
				count++;
				CardItem item = populateCardItem(rs);
				if (item.get_amount() >= 20000) {
					item.set_nofCodes((int)item.get_amount()/20000);
					card_queue.put(item);
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
		item.set_msisdn(rs.getString("msisdn").substring(2));
		item.set_amount(rs.getInt("price"));
		item.set_adddays(0);
		item.set_subtype(0);

		return item;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		////////////////////////////////////////////////////////
		vtReturn.addElement(createParameterDefinition("lastSeq", lastSeq, ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		
		vtReturn.addAll(super.getParameterDefinition());
		
		return vtReturn;
	}

	public void fillParameter() throws AppException {
		lastSeq = loadLong("lastSeq");
		
		super.fillParameter();
	}

}
