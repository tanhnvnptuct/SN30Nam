package vnp.vas.crs;

import java.sql.CallableStatement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import smartlib.util.AppException;
import vnp.thread.PortalThread;
import vnp.util.CommonVars;
import vnp.util.ParameterType;

public class SendCrsMT extends PortalThread {

	@Override
	public String getMyConnName() {
		return "DBAPP_BC_SMS";
	};
	private int request_delay;
	private String short_code;
	private String crs_msg;
	private String SQL_STMT = "begin ?:= fSendMT(?, ?, ?); end;";// prs_shortcode_mt,
																	// prs_message,
																	// r_data.msisdn

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		////////////////////////////////////////////////////////
		vtReturn.addElement(createParameterDefinition("short_code", "1352", ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(createParameterDefinition("crs_msg",
				"(QC) MIEN PHI 100% cuoc 3G tai http://tgphim.vn. Soan DK8 gui 1352 de xem nhung bo phim HOT nhat, HAY nhat va co hoi Trung TIVI SONY 55 INCH FULL HD tri gia 30 TRIEU DONG. Mien phi ngay dau, cuoc sau KM 2.000d/ngay. Tu choi QC, soan TC gui 18001091.",
				ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(createParameterDefinition("request_delay", "1000", ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@SuppressWarnings({ "deprecation" })
	public void fillParameter() throws AppException {
		short_code = loadMandatory("short_code");
		crs_msg = loadMandatory("crs_msg");
		request_delay=Integer.valueOf(loadMandatory("request_delay"));
		super.fillParameter();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void processSession() throws Exception {
		try {
		BlockingQueue<String> queue = (BlockingQueue<String>) getCommonVariable(CommonVars.DATA_SUBS);
		Calendar calendar = GregorianCalendar.getInstance(); // creates a new
																// calendar
																// instance
		int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in
																// 24h format
		// calendar.get(Calendar.HOUR); // gets hour in 12h format
		// calendar.get(Calendar.MONTH);
		if (((hour_of_day >= 8 && hour_of_day < 12) || (hour_of_day >= 14 && hour_of_day < 20)) && queue.size() > 0) {
			CallableStatement cstmt = mcnMain.prepareCall(SQL_STMT);
			cstmt.registerOutParameter(1, oracle.jdbc.driver.OracleTypes.NUMBER);
			cstmt.setString(2, short_code);
			cstmt.setString(3, crs_msg);
			int count=0;
			while (!queue.isEmpty()) {
				String msisdn = queue.take();
				try {
					cstmt.setString(4, msisdn);
					if (IsTestingMode()) {
						logMonitor("fSendMT:" + short_code + "|" + msisdn + "|" + crs_msg);
					} else {
						count++;
						cstmt.executeQuery();
						Thread.sleep(request_delay);
					}
					// cstmt.getInt(1);
				} catch (Exception e) {
					logMonitor(e.getMessage());
					queue.put(msisdn);
					Thread.sleep(1000);
				}

			}
			cstmt.close();
			if(count>0){logMonitor("SMS Sent: "+ count);}
		}
		} catch (Exception e) {
			logMonitor(e.getMessage());			
			Thread.sleep(1000);
		}

	}

}
