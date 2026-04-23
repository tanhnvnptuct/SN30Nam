package vnp.lottery;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import com.fasterxml.jackson.databind.ObjectMapper;

import smartlib.util.AppException;
import vnp.bean.CodeResponse;
import vnp.bean.SmsMt;
import vnp.bean.WinConfig;
import vnp.bean.WinMtTemplate;
import vnp.thread.PortalThread;
import vnp.util.CommonVars;
import vnp.util.DateTimeUtils;
import vnp.util.ParameterType;
public class SMSMTSync extends PortalThread{
	@Override
	public String getMyConnName() {
		return "PORTAL_63_promotion";
	};


		private ObjectMapper mapper;
		private Map<String, WinConfig> configs;
		private Map<String, WinMtTemplate> templates;
		private String cfg_sms_key = "SMS_NUMBER";
		private String cfg_sms_code1 = "SMS_MDT1";
		private String cfg_sms_coden = "SMS_MDTN";
//		private final static String SQL_SELECT = "select CAMPAIGN_ID, MSISDN, MT_CONTENT, STATUS, SENT_TIME from winner.WIN_MT_LOG where campaign_id = 449 ORDER BY SENT_TIME";
		private final static String SQL_SELECT = "select CAMPAIGN_ID, MSISDN, MT_CONTENT, STATUS, SENT_TIME from winner.WIN_MT_LOG where campaign_id = 448 ORDER BY SENT_TIME";
		private final static String SQL_DELETE = "Delete from winner.WIN_MT_LOG where MSISDN=? and CAMPAIGN_ID=? and SENT_TIME<=?";
		// private final static String SQL_DELETE = "Delete from WIN_MT_LOG where
		// MSISDN=? and CAMPAIGN_ID=? and SENT_TIME<=?";
		private final static String SQL_SELECT_CFG = "select CAMPAIGN_ID, CONFIG_KEY, CONFIG_VALUE, CONFIG_TYPE from winner.WIN_CONFIG where CAMPAIGN_ID = ? and CONFIG_KEY = ?";
		private final static String SQL_SELECT_MTTPL = "select * from winner.WIN_MT_TEMPLATE where CAMPAIGN_ID = ? and MT_CODE = ?";

		/*
		 * (non-Javadoc)
		 * 
		 * @see vnp.thread.PortalThread#beforeSession()
		 */
		@Override
		public void beforeSession() throws Exception {
			configs = new HashMap<String, WinConfig>();
			templates = new HashMap<String, WinMtTemplate>();
			mapper = new ObjectMapper();
			super.beforeSession();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void processSession() throws Exception {
			// CallableStatement stm = mcnMain.prepareCall(SQL_SELECT);
			// stm.registerOutParameter(1, Types.INTEGER);
			BlockingQueue<SmsMt> queue = (BlockingQueue<SmsMt>) getCommonVariable(CommonVars.QUEUE_MDT);
			int count = 0;
			int qcount = 0;
			Date lastSentTime = null;
			try {
				PreparedStatement ps = mcnMain.prepareStatement(SQL_SELECT);
				CallableStatement ds = mcnMain.prepareCall(SQL_DELETE);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					count++;
					Long campaignId = rs.getLong("CAMPAIGN_ID");
					String msisdn = rs.getString("MSISDN");
					ds.setString(1, msisdn);
					ds.setLong(2, campaignId);
					WinConfig cfg = getWinConfig(campaignId);
					if (cfg != null) {
						SmsMt mt = new SmsMt();
						String codeInfoStr = rs.getString("MT_CONTENT");
						// logMonitor("Parsing: " + codeInfoStr);
						CodeResponse codeInfo = mapper.readValue(codeInfoStr, CodeResponse.class);
						if (codeInfo != null && codeInfo.getCodes() != null) {
							if (codeInfo.getCodes().size() == 1) {
								WinMtTemplate mtTpl = getMtTemplate(campaignId, cfg_sms_code1);
								if (mtTpl == null) {
									logMonitor("No MT Template: " + campaignId + "|" + cfg_sms_code1);
									continue;
								}
								mt.setSmsContent(mtTpl.getMtContent()
										.replace("$LOTDATE$", DateTimeUtils.FormatDate(codeInfo.getLotDate()))
										.replace("$MDT$",String.format("%011d", codeInfo.getCodes().get(0).longValue())));
								mt.setMoId(1);
							} else {
								WinMtTemplate mtTpl = getMtTemplate(campaignId, cfg_sms_coden);
								if (mtTpl == null) {
									logMonitor("No MT Template: " + campaignId + "|" + cfg_sms_coden);
									continue;
								}
								
								
								//String.format("%010d", Integer.parseInt(mystring));
								
								mt.setSmsContent(mtTpl.getMtContent()
										.replace("$LOTDATE$", DateTimeUtils.FormatDate(codeInfo.getLotDate()))
										//.replace("$SOMDT$", String.valueOf(codeInfo.getCodes().size())));
										.replace("$SOMDT$", String.format("%02d", codeInfo.getCodes().size())));
								mt.setMoId(2);
							}
							mt.setMsisdn(msisdn);
							mt.setStatus(new BigDecimal(0));
							mt.setShortCode(cfg.getConfigValue());
							queue.add(mt);
							qcount++;
						} else {
							logMonitor("No code err: " + codeInfoStr);
						}
						Date mtTime = rs.getTimestamp("SENT_TIME");
						if (lastSentTime == null || lastSentTime.before(mtTime))
							lastSentTime = mtTime;

					} else {
						logMonitor("No WinConfig from campaign: " + campaignId);
					}
					ds.setTimestamp(3, new Timestamp(lastSentTime.getTime()));
					ds.addBatch();
				}
				rs.close();
				ps.close();
				// start_time = new Date();
				if (count > 0) {
					logMonitor("Items tried|queued: " + count + "|" + qcount);
					// CallableStatement ds = mcnMain.prepareCall(SQL_DELETE);
					// ds.executeUpdate();
					ds.executeBatch();
					ds.close();

				}
			} catch (Exception e) {
				logMonitor("Process Session Exception: " + e.getMessage());
			} finally {

			}
			// if (count > 0) {
			// stm.executeBatch();
			// }
		}

		private WinMtTemplate getMtTemplate(Long campaignId, String cfg_sms_code2) throws SQLException {
			String key = cfg_sms_code2 + campaignId;
			if (templates.containsKey(key)) {
				return templates.get(key);
			} else {
				WinMtTemplate cfg = null;
				PreparedStatement ps = mcnMain.prepareStatement(SQL_SELECT_MTTPL);
				ps.setLong(1, campaignId);
				ps.setString(2, cfg_sms_code2);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					cfg = new WinMtTemplate();
					cfg.setCampaignId(campaignId);
					cfg.setMtCode(cfg_sms_code2);
					cfg.setMtContent(rs.getString("MT_CONTENT"));
					templates.put(key, cfg);
				}
				rs.close();
				ps.close();
				return cfg;
			}
		}

		private WinConfig getWinConfig(Long campaignId) throws SQLException {
			String key = cfg_sms_key + campaignId;
			if (configs.containsKey(key)) {
				return configs.get(key);
			} else {
				WinConfig cfg = null;
				PreparedStatement ps = mcnMain.prepareStatement(SQL_SELECT_CFG);
				ps.setLong(1, campaignId);
				ps.setString(2, cfg_sms_key);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					cfg = new WinConfig();
					cfg.setCampaignId(campaignId);
					cfg.setConfigKey(cfg_sms_key);
					cfg.setConfigValue(rs.getString("CONFIG_VALUE"));
					configs.put(key, cfg);
				}
				rs.close();
				ps.close();
				return cfg;
			}
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Vector getParameterDefinition() {
			Vector vtReturn = new Vector();
			////////////////////////////////////////////////////////

			vtReturn.addElement(
					createParameterDefinition("cfg_sms_key", cfg_sms_key, ParameterType.PARAM_TEXTBOX_MAX, "10000"));
			vtReturn.addElement(
					createParameterDefinition("cfg_sms_code1", cfg_sms_code1, ParameterType.PARAM_TEXTBOX_MAX, "10000"));
			vtReturn.addElement(
					createParameterDefinition("cfg_sms_coden", cfg_sms_coden, ParameterType.PARAM_TEXTBOX_MAX, "10000"));
			vtReturn.addAll(super.getParameterDefinition());
			return vtReturn;
		}

		public void fillParameter() throws AppException {
			cfg_sms_key = loadString("cfg_sms_key");
			cfg_sms_code1 = loadString("cfg_sms_code1");
			cfg_sms_coden = loadString("cfg_sms_coden");
			super.fillParameter();
		}
}
