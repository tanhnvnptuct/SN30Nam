package vnp.lottery;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

public class PromotionUsage extends PortalThread {

	@Override
	public String getMyConnName() {
		return "PORTAL_63_promotion";
	};

	private String SQL_STMT = "select * from usagemtr_srv_381132 where log_date=to_date(?,'yyyymmdd') and "
			+ "subscriber_id not in (select msisdn from data_srv_381132 where date_time>=to_date(?,'yyyymmdd') and date_time <to_date(?,'yyyymmdd') and price>='20000')";
	private String SQL_lichchuongtrinh = "select * from lich_chuongtrinh where ngay = trunc(sysdate) and trangthai = 1";
	private String SQL_updatelichchuongtrinh = "update lich_chuongtrinh set trangthai = 2 where ngay = trunc(sysdate) and trangthai = 1";
	

	@SuppressWarnings("unchecked")
	@Override
	protected void processSession() throws Exception {
		
		Calendar cal = Calendar.getInstance();
		
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String currentdate = df.format(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        
        
        
        PreparedStatement ps_lich = mcnMain.prepareStatement(SQL_lichchuongtrinh);
		ResultSet rs_lich = null;
		rs_lich = ps_lich.executeQuery();
		CallableStatement cs_update = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;
		
		try {
			

			BlockingQueue<CardItem> card_queue = (BlockingQueue<CardItem>) getCommonVariable(CommonVars.QUEUE_CARDITEM);

			if (rs_lich.next()){
				ps = mcnMain.prepareStatement(SQL_STMT);
				ps.setString(1, df.format(cal.getTime()));
				ps.setString(2, df.format(cal.getTime()));
				ps.setString(3, currentdate);

				rs = ps.executeQuery();
				

				while (rs.next()) {
					count++;
					CardItem item = populateCardItem(rs);
					item.set_nofCodes((int)item.get_amount()/20000);
					card_queue.put(item);
				}
			}
			
			if (count>0){
				cs_update = mcnMain.prepareCall(SQL_updatelichchuongtrinh);
				cs_update.execute();
			}
			
			logMonitor("Process: "+count+" items");
			
		} catch (Exception e) {
			e.printStackTrace();
			logMonitor(e.getMessage());
			Thread.sleep(1000);
		} finally {
			Database.closeObject(rs);
			Database.closeObject(ps);
			Database.closeObject(rs_lich);
			Database.closeObject(ps_lich);
			Database.closeObject(cs_update);
		}

	}


	private CardItem populateCardItem(ResultSet rs) throws SQLException {
		CardItem item = new CardItem();
		//item.set_id(rs.getLong("ID"));
		item.set_msisdn(rs.getString("subscriber_id").substring(2));
		item.set_amount(rs.getInt("tkc"));
		item.set_adddays(1);
		item.set_subtype(2);

		return item;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		////////////////////////////////////////////////////////
		vtReturn.addAll(super.getParameterDefinition());
		
		return vtReturn;
	}

	public void fillParameter() throws AppException {
		super.fillParameter();
	}

}
