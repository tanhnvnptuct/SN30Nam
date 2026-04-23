package vnp.ftp;

import java.util.Vector;

import com.fss.thread.ParameterType;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import vnp.thread.PortalThread;
import smartlib.util.AppException;


public class SFTPTranceiverProcess extends PortalThread{
	protected String ftpServerURL = "";
	protected String ftpUsername = "";
	protected String ftpPassword = "";
	protected int ftpPort;
	protected String ftpFolder = "";
	protected String ftpOutputDir = "";
	protected String ftpBackUpDir = "";
	//protected FTPClient ftpClient = null;
	protected Session session = null;
	protected Channel channel = null;
	protected ChannelSftp sftp = null;
	protected final JSch ssh = new JSch();
	
	@Override
	public void beforeSession() throws Exception {
		super.beforeSession();
		try{
			session = ssh.getSession(ftpUsername, ftpServerURL, ftpPort);
		    session.setPassword(ftpPassword);
		    java.util.Properties config = new java.util.Properties(); 
		    config.put("StrictHostKeyChecking", "no");
		    session.setConfig(config);
		    session.connect();
		    channel = session.openChannel("sftp");
		    channel.connect();
		}catch(Exception ex){
			logMonitor("error when login FTP server: " + ex.getMessage());
		}
	}

	@Override
	protected void processSession() throws Exception {
	}
	

	@Override
	public void afterSession() throws Exception {
	    super.afterSession();
	    try {
	    	if(channel != null && channel.isConnected()){
	    		channel.disconnect();
	    	}
	    	if(sftp != null && sftp.isConnected()){
	    		sftp.disconnect();
	    	}
	    	if(session != null && session.isConnected()){
	    		session.disconnect();
	    	}
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		vtReturn.addElement(createParameterDefinition("FTPServerURL", "", ParameterType.PARAM_TEXTBOX_MAX, "500", ""));
		vtReturn.addElement(createParameterDefinition("FTPUsername", "", ParameterType.PARAM_TEXTBOX_MAX, "500", ""));
		vtReturn.addElement(createParameterDefinition("FTPPassword", "", ParameterType.PARAM_TEXTBOX_MAX, "500", ""));
		vtReturn.addElement(createParameterDefinition("FTPPort", "", ParameterType.PARAM_TEXTBOX_MAX, "500", ""));
		vtReturn.addElement(createParameterDefinition("OutputDir", "", ParameterType.PARAM_TEXTBOX_MAX, "500", ""));
		vtReturn.addElement(createParameterDefinition("FTPFolder", "", ParameterType.PARAM_TEXTBOX_MAX, "500", ""));
		vtReturn.addElement(createParameterDefinition("BackUpDir", "", ParameterType.PARAM_TEXTBOX_MAX, "500", ""));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void fillParameter() throws AppException {
		ftpServerURL = loadMandatory("FTPServerURL");
		ftpUsername = loadMandatory("FTPUsername");
		ftpPassword = loadMandatory("FTPPassword");
		ftpPort = loadInteger("FTPPort");
		ftpOutputDir = loadMandatory("OutputDir");
		ftpBackUpDir = loadMandatory("BackUpDir");
		ftpFolder = loadMandatory("FTPFolder");
		super.fillParameter();
	}
}
