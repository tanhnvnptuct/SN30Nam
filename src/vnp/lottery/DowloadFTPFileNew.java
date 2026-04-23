package vnp.lottery;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

import vnp.ftp.FTPReceiverProcess;
import vnp.util.ParameterType;
import smartlib.util.AppException;
import smartlib.util.FileUtil;
import smartlib.util.WildcardFilter;

public class DowloadFTPFileNew extends FTPReceiverProcess{
	protected String mstrBackupType;
	protected String mstrWildcard;
//	protected String mstrDateTimeFormat;
//	protected int mstrNumberOfDataDateLeft;
//	private String latestWildcard;
	private SimpleDateFormat fmt1;
	
	@SuppressWarnings("deprecation")
	public void fillParameter() throws AppException {
		super.fillParameter();
		mstrBackupType = loadMandatory("BackupType");
		mstrWildcard = loadMandatory("Wildcard");
//		mstrDateTimeFormat = loadMandatory("DateTimeFormat");
//		mstrNumberOfDataDateLeft = loadInteger("NumberOfDataDateLeft");
		try {			
			FileUtil.forceFolderExist(ftpOutputDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void beforeSession() throws Exception {
		super.beforeSession();
//		fmt1 = new SimpleDateFormat(mstrDateTimeFormat);		
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.DAY_OF_YEAR, (-1) * mstrNumberOfDataDateLeft);
//		latestWildcard = mstrWildcard.replace("%DATE_TIME%", fmt1.format(calendar.getTime()));
	}
	
	@Override
	protected void processSession() throws Exception {
		try{
			String backupDir = ftpBackUpDir;
			java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyyMMdd");
			String strCurrentDate = fmt.format(new java.util.Date());
			if (mstrBackupType.equals("DAILY")){
				backupDir = backupDir + "/" + strCurrentDate;
				ftpClient.makeDirectory(backupDir);			
			}
			
			downloadAllFileFromDirectory(ftpFolder, ftpOutputDir, backupDir);
			logMonitor("send ftp file complete");								
		}catch(Exception e){
			logMonitor("error when download FTP file: " + e.getMessage());
		}
	}
	
	private void downloadAllFileFromDirectory(String inputDir, String ftpOutput, String backupDir){
		try{
			FTPFileFilter filter = new FTPFileFilter() {
			    @Override
			    public boolean accept(FTPFile ftpFile) {
			    	String strFileName = ftpFile.getName();
					boolean bl = false;
					if (ftpFile.isFile()) {
						//bl = WildcardFilter.match(latestWildcard, strFileName, true);
						bl = WildcardFilter.match(mstrWildcard, strFileName, true);
					}
					return bl || ftpFile.isDirectory();
			    }
			};

	        //FTPFile[] listFiles = ftpClient.listFiles(inputDir, filter);
			FTPFile[] listFiles = ftpClient.listFiles(inputDir);
			for (FTPFile ftpFile : listFiles) {
				if(ftpFile.isFile()){
					File localfile = new File(ftpOutputDir + "/" + ftpFile.getName());
					OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localfile));					
		            boolean success = ftpClient.retrieveFile(inputDir + "/" + ftpFile.getName(), outputStream);
		            outputStream.flush();
					outputStream.close();
		            if (success){
		            	logMonitor("download success filename : " + ftpFile.getName());
		            	if (mstrBackupType.equals("DELETE")){
		            		boolean del = ftpClient.deleteFile(inputDir + "/" + ftpFile.getName());
		            		logMonitor("delete file:" + ftpFile.getName() + " res:" + del);
		    			}else if(mstrBackupType.equals("NO_ACTION")){
		    				
		    			}else{
		    				backUpFile(ftpFile.getName(), inputDir, backupDir);
		    			}
		            	
		            }
				}
				if(ftpFile.isDirectory()){
					downloadAllFileFromDirectory(inputDir + "/" + ftpFile.getName(), ftpOutput, backupDir);
				}
			}
		}catch(Exception e){
			logMonitor("error when send file from " + inputDir);
		}finally{
		}		 
	}
	
	private void backUpFile(String filename,String from, String to){
		try{
			boolean success = ftpClient.rename(from + "/" + filename, to + "/" + filename);
			if(success){
				logMonitor("move file "+ filename + " from  " + from + " to " + to + " success");
			}else{
				logMonitor("move file "+ filename + " from  " + from + " to " + to + " fail");
			}
		}catch(Exception e){
			logMonitor("cannot move file " + filename + " from  " + from + " to " + to);
		}
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();		
		Vector vtValue1 = new Vector();
		vtValue1.addElement("NO_ACTION");
		vtValue1.addElement("DELETE");
		vtValue1.addElement("DIRECT");
		vtValue1.addElement("DAILY");
		vtReturn.addElement(createParameterDefinition("BackupType", "", ParameterType.PARAM_COMBOBOX, vtValue1, ""));	
		vtReturn.addElement(createParameterDefinition("Wildcard", "", ParameterType.PARAM_TEXTBOX_MAX, "100"));
//		vtReturn.addElement(createParameterDefinition("DateTimeFormat", "", ParameterType.PARAM_TEXTBOX_MAX, "100"));
//		vtReturn.addElement(createParameterDefinition("NumberOfDataDateLeft", "", ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}
}
