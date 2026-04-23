package vnp.lottery;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import com.fss.sql.Database;

import smartlib.database.ConnectionFactory;
import smartlib.util.AppException;
import vnp.bean.CardItem;
import vnp.thread.PortalThread;
import vnp.util.CommonVars;
import vnp.util.ParameterType;

public class UsageMtr extends PortalThread{
	@Override
	public String getMyConnName() {
		return "PIMA";
	};
	
	private ConnectionFactory pool_cnn = null;
	private java.sql.Connection ora_cnn = null;
	private long batch_count;

	private String sql_insert = "insert into usagemtr_srv_381132 values (?,?,?,?,?,?,?)";
	
	private String SQL_flag = "select * from pps_subs.ketqua_xuly_dulieu a where loai_file like 'CREATE_MTR_LIST' and date_time=trunc(sysdate-?)";
	private String SQL_lichchuongtrinh = "select * from lich_chuongtrinh where ngay = trunc(sysdate) and trangthai = 0";
	private String SQL_updatelichchuongtrinh = "update lich_chuongtrinh set trangthai = 1 where ngay = trunc(sysdate) and trangthai = 0";
	
	//private String SQL_STMT = "select log_date,subscriber_id, tkc from pps_subs.usage_mtr where log_date=to_date(?,'yyyymmdd') and tkc>=50000";
	
	private String SQL_STMT = "select  subscriber_id,a.log_date,originaltimestamp,tkc, replace( REPLACE(REPLACE( a.mtr_comment,'Tru KM ',''),'FROM 3G_',''),'FEE/','') as goi, "
			+ " mtr_comment,DECODE( a.prod_ln_cd,1,'Tra sau','Tra truoc') as loai_tb from pps_subs.usage_mtr a "
			+ " where a.log_date=to_date(?,'yyyymmdd')  and ( a.mtr_comment like 'Tru KM %' or  a.mtr_comment like 'FROM 3G%' or a.mtr_comment like 'FEE/%') and tkc>=20000";
	
	private int the_day_before;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void processSession() throws Exception {
		logMonitor("Start get sub_bts_daily: ");
		
		pool_cnn = new ConnectionFactory("oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@10.156.3.252:1521/drportal", "promotion", "ZuNmDEPw9WwgxrAqlRJm7A==", 100);

		ora_cnn = pool_cnn.getConnection();
		
		CallableStatement stm = ora_cnn.prepareCall(sql_insert);
		
		
		PreparedStatement ps_flag = mcnMain.prepareStatement(SQL_flag);
		ps_flag.setInt(1, the_day_before);
		//ps_flag.setInt(1, 2);
		ResultSet rs_flag = null;
		
		PreparedStatement ps_lich = ora_cnn.prepareStatement(SQL_lichchuongtrinh);
		ResultSet rs_lich = null;
		
		rs_flag = ps_flag.executeQuery();
		rs_lich = ps_lich.executeQuery();
		
		//lich moi ngay chay 1 lan/ chay du lieu sub_bts_daily cua ngay T - 
		//neu lich chua thuc hien va da co du lieu trong bang sub_bts_daily thi thuc hien
		if (rs_flag.next() && rs_lich.next()){
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -1*the_day_before);
	        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
	     
			PreparedStatement ps = null;
			CallableStatement cs_update = null;
			ResultSet rs = null;

			try {
				ps = mcnMain.prepareStatement(SQL_STMT);
				ps.setString(1,df.format(cal.getTime()));
				rs = ps.executeQuery();

				int total_count = 0, count = 0;

				while (rs.next()) {
					count++;
					total_count++;
					stm.setString(1,"84"+ rs.getString("subscriber_id"));
					stm.setTimestamp(2, rs.getTimestamp("log_date"));
					stm.setTimestamp(3, rs.getTimestamp("originaltimestamp"));
					stm.setLong(4, rs.getLong("tkc"));
					stm.setString(5, rs.getString("goi"));
					stm.setString(6, rs.getString("mtr_comment"));
					stm.setString(7, rs.getString("loai_tb"));
					
					if (count<batch_count){
						
						stm.addBatch();
						count++;
					}
					else{
						
						stm.addBatch();
						stm.executeBatch();
						count = 0;
					}
	
				}
				
				
				stm.executeBatch();
				if (total_count > 0) {
					
					logMonitor("insert: " + total_count);
				}
				
				
				cs_update = ora_cnn.prepareCall(SQL_updatelichchuongtrinh);
				cs_update.execute();

				
			} catch (Exception e) {
				e.printStackTrace();
				logMonitor(e.getMessage());
				Thread.sleep(1000);
			} finally {
				Database.closeObject(rs);
				Database.closeObject(ps);
				Database.closeObject(cs_update);
			}
		}
		Database.closeObject(ps_flag);
		Database.closeObject(rs_flag);
		Database.closeObject(ps_lich);
		Database.closeObject(rs_lich);
		Database.closeObject(stm);
		Database.closeObject(ora_cnn);

	

	}

	
	private CardItem populateCardItem(ResultSet rs) throws SQLException {
		CardItem item = new CardItem();
		//item.set_id(rs.getLong("ID"));
		item.set_msisdn(rs.getString("subscriber_id"));
		item.set_amount(rs.getInt("tkc"));
		item.set_nofCodes(rs.getInt("tkc")/50000);
		item.set_subtype(5);

		return item;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		////////////////////////////////////////////////////////
		vtReturn.addElement(
				createParameterDefinition("batch_count", batch_count, ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(
				createParameterDefinition("the_day_before", the_day_before, ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addAll(super.getParameterDefinition());
		
		return vtReturn;
	}

	public void fillParameter() throws AppException {
		batch_count = loadInteger("batch_count");
		the_day_before = loadInteger("the_day_before");
		super.fillParameter();
	}
}
