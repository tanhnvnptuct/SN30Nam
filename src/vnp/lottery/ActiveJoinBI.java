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

public class ActiveJoinBI extends PortalThread {

	@Override
	public String getMyConnName() {
		return "PIMA";
	};


	private String SQL_STMT = "SELECT /*+parallel(16)*/ a.mo_key,a.acct_key,a.accs_mthd_key,a.actvtn_dt,b.package_name service_code,b.package_name, b.PROD_SPEC_GRP_CD, b.ORIGINALTIMESTAMP ngay_muagoi, nvl(b.TKC, b.TK_KHAC) tien_goi, trunc (nvl(b.TKC, b.TK_KHAC)/50000) mdt\r\n"
			+ "FROM  pps_subs.bi_dwb_acct_actvtn a  INNER JOIN pps_subs.v_stg_ocs_chitiet_sub_km b ON b.day_key>=TO_NUMBER(TO_CHAR(SYSDATE-2, 'YYYYMMDD')) and b.PROD_SPEC_GRP_CD in ('DATA','KMCB','CCBS')\r\n"
			+ "AND b.acct_key = a.acct_key AND b.sub_partition_key = a.sub_partition_key AND b.ORIGINALTIMESTAMP BETWEEN a.actvtn_dt AND a.actvtn_dt + 7 AND nvl(b.TKC, b.TK_KHAC) >= 50000 \r\n"
			+ "WHERE a.mo_key in ( 202605,2026016,2026017,202608) and b.ORIGINALTIMESTAMP between trunc(sysdate-2) and trunc(sysdate-1)";
	
	
	

	@SuppressWarnings("unchecked")
	@Override
	protected void processSession() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		
		logMonitor("Start get sub_bts_daily: ");
		logMonitor(SQL_STMT);
		BlockingQueue<CardItem> card_queue = (BlockingQueue<CardItem>) getCommonVariable(CommonVars.QUEUE_CARDITEM);
		int count = 0;
		ps = mcnMain.prepareStatement(SQL_STMT);
		rs = ps.executeQuery();
		logMonitor("Start add queue!");
		while (rs.next()) {
			count++;
			CardItem item = populateCardItem(rs);
			String msisdn = item.get_msisdn().substring(0,4);
			if (!msisdn.equals("8487") && !msisdn.equals("8499") && !msisdn.equals("8459")&& !msisdn.equals("8455")) 
				card_queue.put(item);
		}


		if (count > 0) {
			//saveLastSequence(lastSeq);
			logMonitor("Tong so thue bao : " + count);
		}
		
		Database.closeObject(rs);
		Database.closeObject(ps);
		
	}
	
	
	private CardItem populateCardItem(ResultSet rs) throws SQLException {
		CardItem item = new CardItem();
		//item.set_id(rs.getLong("ID"));
		item.set_msisdn(rs.getString("accs_mthd_key").substring(2));
		int noofcode=2+rs.getInt("mdt");
		item.set_nofCodes (noofcode);
		//item.set_subtype(0);
		if (rs.getString("PROD_SPEC_GRP_CD").equalsIgnoreCase("CCBS"))
			item.set_subtype(1);
		else
			item.set_subtype(0);
		item.set_adddays(2);

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
