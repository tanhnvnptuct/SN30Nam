package vnp.vas.crs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import smartlib.util.AppException;
import vnp.thread.PortalThread;
import vnp.util.CommonVars;
import vnp.util.ParameterType;

public class CheckSubStatus extends PortalThread {

	@Override
	public String getMyConnName() {
		return "SUBSYNCVAS";
	};

	private String select_statement;
	private String service_code;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		////////////////////////////////////////////////////////
		vtReturn.addElement(createParameterDefinition("select_statement",
				"Select * from SUBSCRIBER_PACKAGE where SERVICE=? and MSISDN=? and status=1;",
				ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addElement(
				createParameterDefinition("service_code", "CONGTHEGIOIPHIM", ParameterType.PARAM_TEXTBOX_MAX, "10000"));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@SuppressWarnings({ "deprecation" })
	public void fillParameter() throws AppException {
		select_statement = loadMandatory("select_statement");
		service_code = loadMandatory("service_code");
		super.fillParameter();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void processSession() throws Exception {
		BlockingQueue<String> queue = (BlockingQueue<String>) getCommonVariable(CommonVars.DATA_SUBS);
		BlockingQueue<String> mt_queue = (BlockingQueue<String>) getCommonVariable(CommonVars.MT_SUBS);

		PreparedStatement ps = mcnMain.prepareStatement(select_statement);
		ps.setString(1, service_code);
		while (!queue.isEmpty()) {
			String msisdn = queue.take();
			try {
				ps.setString(2, msisdn);
				ResultSet rs = ps.executeQuery();
				if (!rs.next()) {
					mt_queue.put(msisdn);
				}
				Thread.sleep(1000);
			} catch (Exception ex) {
				logMonitor(ex.getMessage());
				// queue.put(item);
				Thread.sleep(1000);
			}
		}
	}

}
