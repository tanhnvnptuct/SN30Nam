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

public class ReadFromSMS extends PortalThread {

	@Override
	public String getMyConnName() {
		return "PORTAL_63_winner";
	};
	private long lastSeq = 0;
	private int step = 0;
	private String SQL_STMT = "select * from promotion.sms_quay_thuong where ID>?";
	private String SQL_Update = "Update promotion.sms_quay_thuong set status = 1 where ID=?";
	private String SQL_Lottery = "{?=call pkg_instantlot.GET_PRIZE_30NAM(?,?,?)}";
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void processSession() throws Exception {
		PreparedStatement ps = null;
		PreparedStatement ps_update = null;
		CallableStatement cs = null;
		ResultSet rs = null;

		try {
			String logMsg = "From Seq: " + lastSeq;

			ps = mcnMain.prepareStatement(SQL_STMT);
			ps.setLong(1, lastSeq);
			rs = ps.executeQuery();
			ps_update = mcnMain.prepareStatement(SQL_Update);
			cs = mcnMain.prepareCall(SQL_Lottery);
			cs.registerOutParameter(1, Types.INTEGER);
			int count = 0;
			
			//doc list cac sms quay thuong
			while (rs.next()) {
				count++;
				String msisdn=rs.getString("msisdn");
				int id=rs.getInt("id");

				cs.setInt(2,id);
				cs.setString(3,msisdn);
				cs.setInt(4,step);
				//voi tung sms cua tung thue bao thi thuc hien quay thuong
				cs.execute();
				logMonitor("thue bao "+msisdn + " da quay giai: "+cs.getInt(1));
				ps_update.setInt(1, id);
				ps_update.addBatch();
				
				if (lastSeq < id)
					lastSeq = id;
				
			}
			ps_update.executeBatch();
			
			
			
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


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		////////////////////////////////////////////////////////
		vtReturn.addElement(createParameterDefinition("lastSeq", lastSeq, ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(createParameterDefinition("step", step, ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addAll(super.getParameterDefinition());
		
		return vtReturn;
	}

	public void fillParameter() throws AppException {
		lastSeq = loadLong("lastSeq");
		step = loadInteger ("step");
		super.fillParameter();
	}

}
