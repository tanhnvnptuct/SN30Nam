package telsoft.app.util;

import java.io.File;
import java.util.Vector;

import smartlib.util.FileUtil;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author DinhLV
 * @version 1.0
 */

public class SequenceManager {
	private static Vector vtSequence = null;
	private static String sequencePath = null;
	public static void setSequencePath(String path) throws Exception {
		sequencePath = path;
		vtSequence = new Vector();
		File flList = new File(path);
		if(!flList.exists())
			FileUtil.forceFolderExist(sequencePath);
	}

	////////////////////////////////////////////////////////
	private static void setSequencePathOld(String path) throws Exception {
		sequencePath = path;
		vtSequence = new Vector();
		File flList = new File(path);
		if(!flList.exists())
			FileUtil.forceFolderExist(sequencePath);

		String[] fileList = flList.list();
		for(int i = 0; i < fileList.length; i++) {
			String fileName = fileList[i];
			String[] flElements = fileName.split("-");
			if(flElements.length > 1) {
				try {
					Vector vtRow = new Vector();
					long lngSequence = Long.parseLong(flElements[1]);
					vtRow.addElement(flElements[0]);
					vtRow.addElement(new Long(lngSequence));
					vtRow.addElement(fileName);
					vtSequence.addElement(vtRow);
				} catch(Exception e) {
					FileUtil.deleteFile(sequencePath + "/" + fileName);
				}
			}
		}
	}

	////////////////////////////////////////////////////////
	private static synchronized long getSequenceOld(String sequenceName) throws Exception {
		if(sequencePath == null)
			FileUtil.forceFolderExist(sequencePath);
		//throw new Exception("No sequence path found");
		long lngReturn = -1;
		for(int i = 0; i < vtSequence.size(); i++) {
			Vector vtRow = (Vector)vtSequence.elementAt(i);
			String strSequenceName = vtRow.elementAt(0).toString();
			if(strSequenceName.toUpperCase().equals(sequenceName.toUpperCase())) {
				long lngSequence = Long.parseLong(vtRow.elementAt(1).toString());
				String sourceFile = sequencePath + "/" + vtRow.elementAt(2).toString();
				String newFileName = sequenceName.toUpperCase() + "-" + (lngSequence + 1);
				String destFile = sequencePath + "/" + newFileName;
				if(FileUtil.renameFile(sourceFile, destFile)) {
					lngReturn = (lngSequence + 1);
					vtRow.setElementAt(new Long(lngReturn), 1);
					vtRow.setElementAt(newFileName, 2);
					vtSequence.setElementAt(vtRow, i);
				} else
					throw new Exception("Can not get sequence value");
			}
		}
		if(lngReturn == -1)
			throw new Exception("No sequence with name " + sequenceName + " found");
		return lngReturn;
	}

	////////////////////////////////////////////////////////
	public static synchronized long getSequence(String sequenceName) throws Exception {
		if(sequencePath == null)
			FileUtil.forceFolderExist(sequencePath);
		File flList = new File(sequencePath);
		if(!flList.exists())
			throw new Exception("Sequence path does not exists");
		String[] fileList = flList.list();
		long lngSequenceVal = -1;
		for(int i = 0; i < fileList.length; i++) {
			String fileName = fileList[i];
			String[] flElements = fileName.split("-");
			if(flElements.length > 1) {
				sequenceName = sequenceName.toUpperCase();
				if(flElements[0].toUpperCase().equals(sequenceName)) {
					try {
						lngSequenceVal = Long.parseLong(flElements[1]);
						String sourceFile = sequencePath + "/" + fileName;
						lngSequenceVal++;
						String newFileName = sequenceName.toUpperCase() + "-" + lngSequenceVal;
						String destFile = sequencePath + "/" + newFileName;
						if(!FileUtil.renameFile(sourceFile, destFile))
							throw new Exception("Can not get sequence value");
					} catch(Exception e) {
						throw new Exception("Sequence with name " + sequenceName + " is invalid");
					}
				}
			} else {
				FileUtil.deleteFile(sequencePath + "/" + fileName);
			}
		}
		if(lngSequenceVal == -1)
			throw new Exception("No sequence with name " + sequenceName + " found");
		else
			return lngSequenceVal;
	}
}
