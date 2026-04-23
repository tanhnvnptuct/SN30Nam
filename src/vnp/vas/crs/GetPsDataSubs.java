package vnp.vas.crs;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import smartlib.util.AppException;
import vnp.thread.PortalThread;
import vnp.util.CommonVars;
import vnp.util.DateTimeUtils;
import vnp.util.ParameterType;
import vnp.vas.api.VasProApi;

public class GetPsDataSubs extends PortalThread {

	@Override
	public String getMyConnName() {
		return "DBAPP_BC_SMS";
	};

	private VasProApi apiUtil;
	private String select_statement;
	private String update_statement;

	private int batch_count;
	private int day_quota;
	private Date last_time;
	private int day_count;
	private int min_money;

	private String service_code;
	private String package_code;
	
	private int request_delay;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		////////////////////////////////////////////////////////
		vtReturn.addElement(
				createParameterDefinition("service_code", "CONGTHEGIOIPHIM", ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(
				createParameterDefinition("package_code", "NGAY", ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(createParameterDefinition("select_statement",
				"SELECT d.MA_TB, d.TIEN FROM PSDATA_201606 d LEFT OUTER JOIN PSDATA_LOG PARTITION (PART_201607) l ON d.MA_TB = l.MA_TB LEFT OUTER JOIN REFUSE_SUBS r on d.MA_TB=r.MSISDN Where d.MATINH!='HNI' and l.MA_TB is null and r.MSISDN is null and rownum<=?",
				ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(createParameterDefinition("update_statement", "INSERT INTO PSDATA_LOG(MA_TB) VALUES(?)",
				ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(createParameterDefinition("batch_count", "100", ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(createParameterDefinition("day_quota", "100000", ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(createParameterDefinition("min_money", "50000", ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(createParameterDefinition("request_delay", "1000", ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@SuppressWarnings({ "deprecation" })
	public void fillParameter() throws AppException {
		service_code = loadMandatory("service_code");
		package_code = loadMandatory("package_code");
		select_statement = loadMandatory("select_statement");
		update_statement = loadMandatory("update_statement");
		batch_count = Integer.valueOf(loadMandatory("batch_count"));
		day_quota = Integer.valueOf(loadMandatory("day_quota"));
		min_money = Integer.valueOf(loadMandatory("min_money"));
		request_delay=Integer.valueOf(loadMandatory("request_delay"));
		super.fillParameter();
	}

	@Override
	public void beforeSession() throws Exception {
		super.beforeSession();
		try {
			day_count = 0;
			last_time = new Date();
			apiUtil = new VasProApi();
		} catch (Exception ex) {
			logMonitor("Before Session error: " + ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void processSession() throws Exception {
		if (DateTimeUtils.getDiffDate(new Date(), last_time) >= 1) {
			day_count = 0;
			last_time = new Date();
		}
		BlockingQueue<String> queue = (BlockingQueue<String>) getCommonVariable(CommonVars.DATA_SUBS);
		try {
			PreparedStatement ps = mcnMain.prepareStatement(select_statement);
			CallableStatement updateStm = mcnMain.prepareCall(update_statement);
			ps.setInt(1, batch_count);
			ResultSet rs = ps.executeQuery();
			int count = 0;
			BigDecimal minVal = new BigDecimal(min_money);
			while (rs.next() && count < batch_count && day_count < day_quota) {
				String msisdn = rs.getString("MA_TB");
				BigDecimal soTien = rs.getBigDecimal("TIEN");
				if (soTien.compareTo(minVal) >= 0) {
					String srvStatus = "-1";
					try {
						String reqId = String.valueOf((new Date()).getTime());
						srvStatus = apiUtil.getstatus(reqId, msisdn, service_code, package_code);
						if (srvStatus.startsWith("2|") || srvStatus.startsWith("0|")) {
							queue.add(msisdn);
							day_count++;
						} else if (!srvStatus.startsWith("1|")) {
							logMonitor("Api GetStatus " + msisdn +"|"+soTien+ ": " + srvStatus);
							if(srvStatus.startsWith("-4|")) continue;
						}
					} catch (Exception ex) {
						logMonitor(ex.getMessage());
//						continue;
					}
				}
				count++;
				updateStm.setString(1, msisdn);
				updateStm.addBatch();
				Thread.sleep(request_delay);
			}
			// start_time = new Date();
			if (count > 0) {
				logMonitor("GetPsDataSubs " + DateTimeUtils.FormatDate(last_time) + ":" + count + "|" + day_count);
				updateStm.executeBatch();
			}
			rs.close();
			ps.close();
			updateStm.close();
		} catch (Exception e) {
			logMonitor(e.getMessage());
			Thread.sleep(1000);
		} finally {

		}

	}
}
