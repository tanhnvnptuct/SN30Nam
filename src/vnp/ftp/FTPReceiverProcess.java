package vnp.ftp;

import java.io.IOException;
import java.util.Vector;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.fss.thread.ParameterType;

import vnp.thread.PortalThread;
import smartlib.util.AppException;

public class FTPReceiverProcess extends PortalThread{
	protected String ftpServerURL = "";
	protected String ftpUsername = "";
	protected String ftpPassword = "";
	protected int ftpPort;
	protected String ftpFolder = "";
	protected String ftpOutputDir = "";
	protected String ftpBackUpDir = "";
	protected FTPClient ftpClient = null;
	
	@Override
	public void beforeSession() throws Exception {
		super.beforeSession();
		try{
			ftpClient = new FTPClient();
			ftpClient.connect(ftpServerURL, ftpPort);
            boolean res = ftpClient.login(ftpUsername,ftpPassword);
            if(!res){
            	logMonitor("log in ftp server false");
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//            ftpClient.setBufferSize(1);
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
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
	
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
