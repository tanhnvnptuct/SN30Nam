package vnp.thread;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import oracle.jdbc.driver.OracleTypes;
import smartlib.util.AppException;
import vnp.bean.CardItem;
import vnp.util.DateTimeUtils;
import vnp.util.ParameterType;
import vnp.vas.api.VasProApi;

public class TestThread extends PortalThread {
	@Override
	public String getMyConnName() {
		return "DBAPP_IOS";
	};
private VasProApi apiUtil;
	private String SQL_STMT = "Select * From IOS.TOPUPS Where TIME_STAMP>to_date(?,'dd/mm/yyyy hh24:mi:ss')";
	private String SQL_SENTMT = "begin ?:= bc_sms.fSendMT(?, ?, ?); end;";
	// := bc_sms.fSendMT(prs_src, prs_sms_text, prs_dest);
	private Date start_time = new Date();
private String msisdn;
	@Override
	public void beforeSession() throws Exception {
		// TODO Auto-generated method stub
		super.beforeSession();
		apiUtil = new VasProApi();
	}

	@Override
	protected void processSession() throws Exception {
		// TODO Auto-generated method stub
		try {
			// logMonitor("Connection: " + mcnMain.getSchema());
			// logMonitor("Start Quering: " +
			// DateTimeUtils.Format(lastQueried));
			// logMonitor("Topup From " + DateTimeUtils.Format(start_time));
//			start_time = new Date();
//			CallableStatement ps = mcnMain.prepareCall(SQL_SENTMT);
//			ps.registerOutParameter(1, OracleTypes.INTEGER);
//			ps.setString(2, "123");
//			ps.setString(3, "Test Thread Started");
//			ps.setString(4, "841238281166");
//			ResultSet rs = ps.executeQuery();
			// int count = 0;
			// while (rs.next()) {
			// count++;
			// populateCardItem(rs);
			// }
//			logMonitor("Message sent: " + DateTimeUtils.Format(start_time));
			// start_time = new Date();
			String reqId=String.valueOf((new Date()).getTime());
			logMonitor("API Result of "+reqId+": " + apiUtil.getstatus(reqId, msisdn, "CONGTHEGIOIPHIM", "NGAY"));
//			rs.close();
//			ps.close();
		} catch (Exception e) {
			logMonitor(e.getMessage());
			Thread.sleep(1000);
		} finally {
			// conn.close();
		}
	}

	private CardItem populateCardItem(ResultSet rs) throws SQLException {
		CardItem item = new CardItem();
		item.set_msisdn(rs.getString("MSISDN"));
		item.set_amount(rs.getInt("AMOUNT"));
		item.set_subtype(0);
		Date timeStamp = rs.getTimestamp("TIME_STAMP");

		if (this.start_time.getTime() < timeStamp.getTime()) {
			logMonitor(DateTimeUtils.Format(timeStamp));
			this.start_time = timeStamp;
		}
		return item;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		////////////////////////////////////////////////////////
		// vtReturn.addElement(createParameterDefinition("start_time",
		// DateTimeUtils.FormatTime(new Date()),
		// ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(createParameterDefinition("msisdn",
				"841238281166",
				ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@SuppressWarnings("deprecation")
	public void fillParameter() throws AppException {
		// start_time = loadTime("start_time");
		// Calendar cal = Calendar.getInstance();
		// cal.setTime(new Date());
		// cal.set(Calendar.HOUR_OF_DAY, start_time.getHours());
		// cal.set(Calendar.MINUTE, start_time.getMinutes());
		// cal.set(Calendar.SECOND, start_time.getSeconds());
		// start_time = cal.getTime();
		msisdn = loadMandatory("msisdn");
		super.fillParameter();
	}

}
