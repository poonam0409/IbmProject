//---------------------------------------------------------------------------
// IBM Confidential
//
// OCO Source Materials
//
// 5724-Q55
//
// (C) Copyright IBM Corporation 2015
//
// The source code for this program is not published or otherwise
// divested of its trade secrets, irrespective of what has been
// deposited with the U.S. Copyright Office.
//---------------------------------------------------------------------------
//-*-************************************************************************
//
// IBM InfoSphere Information Server Packs for SAP Applications
//
// Module Name : com.ibm.is.sappack.dsstages.common
//
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.dsstages.idoc.listener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.logging.Level;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerConstants;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerMessages;
import com.sap.conn.jco.JCoTable;

public class DsSapCreateStagingFiles {

	public static HashMap<String,String> initfileInfo = null;
	public final Charset CHARSET = Charset.forName("UTF-8");


	public DsSapCreateStagingFiles() {
		initfileInfo = new HashMap<String,String>();
	}

	public void initializeDataPacketFileCreation(String filepath, JCoTable tab, String dataPacket, String reqNumber, int packetId) {
		
		boolean isInitialised = initfileInfo.containsKey(reqNumber);
		if(!isInitialised) // initfileInfo.containsKey(reqNumber))
		{
			readinitfile(filepath);
			isInitialised = initfileInfo.containsKey(reqNumber);
		}
		
		if(isInitialised)
		{
			try{
				StageLogger.getLogger().log(Level.INFO, "Request Number: {0}",reqNumber);
				String jobdetail = initfileInfo.get(reqNumber.trim());
				String[] detail = jobdetail.split(IDocServerConstants.TILDA);
				String jobName = detail[0];
				int noofNodes = Integer.parseInt(detail[1]);
				String datasourcetype = detail[2];
				StageLogger.getLogger().log(Level.INFO, "jobName, noofNodes, datasourcetype: {0}, {1}, {2}",new Object[]{jobName, noofNodes, datasourcetype});
				if(datasourcetype.equals("H"))
					dataPacket = tab.toXML();
				String folderpath = filepath+IDocServerConstants.BACKSLASH+jobName+IDocServerConstants.BACKSLASH+reqNumber+IDocServerConstants.DATAPACKETFOLDERNAME;
				if(validateFolder(folderpath))
				{
					String filename = reqNumber+IDocServerConstants.UNDERSCORE+packetId;
					int nodenumber = packetId%noofNodes;
					File file = new File(folderpath+IDocServerConstants.DATAPACKETCOMPLETEFOLDER+nodenumber+IDocServerConstants.BACKSLASH+filename);
					if (!file.exists()) {
						StageLogger.getLogger().log(Level.INFO, "File does not Exists. Creating it.");
						writeContentToFile(dataPacket,file,datasourcetype);
						StageLogger.getLogger().log(Level.INFO, "Content Written to file.");
						try {
							renameFile(file,packetId);
							StageLogger.getLogger().log(Level.INFO, "Value of DS_DELTA_DELETE_INTERMEDIATE_FILE: {0}",System.getenv("DS_DELTA_DELETE_INTERMEDIATE_FILE"));
						} catch (IOException e) {
							throw new RuntimeException(e.getMessage());
						} catch (Exception e) {
							throw new RuntimeException(e.getMessage());
						}
					}
				}
				else
				{
					//StageLogger.getLogger().log(Level.INFO, "Folder does not exists: {0}",folderpath);
					//	throw new RuntimeException("Folder does not exists: "+folderpath);
				}
			}
			catch(Exception e){
				throw new RuntimeException(IDocServerMessages.ExceptionRaisedinDataPacket+e.getMessage());
			}
		}
		else
		{
			throw new RuntimeException(IDocServerMessages.ExceptionRaisedinDataPacket);
		}
	}

	private void renameFile(File dataPacketFilename, int packetId) throws IOException {
		try{
			File dataFilenameforClient = new File(dataPacketFilename.toString()+IDocServerConstants.FILEREADYFLAG);
			dataPacketFilename.renameTo(dataFilenameforClient);
			StageLogger.getLogger().log(Level.INFO, "File renamed");
			File file = new File(dataFilenameforClient.getParentFile().getParentFile().getParent()+IDocServerConstants.DATAPACKETCOUNTINFOFOLDER+packetId);
			file.createNewFile();
		}catch (Exception e){
			StageLogger.getLogger().log(Level.INFO, "error in copyFile() method: {0}",e.getMessage());	
		}
	}

	public void writeContentToFile(String dataPacket, File file, String dataSourcetype) throws IOException
	{
		BufferedWriter writer = Files.newBufferedWriter(file.toPath(), CHARSET,java.nio.file.StandardOpenOption.CREATE);
		try {
			writer.write(dataPacket);
		} catch (IOException e) {
			StageLogger.getLogger().log(Level.INFO, "Error in Processing datapacket in Datastage. Check Log file");
		throw new RuntimeException(IDocServerMessages.ExceptionRaisedinDataPacket+e.getMessage());
		}
		finally{
			writer.close();
		}
	}

	public boolean validateFolder(String folderpath) {
		File file  = new File(folderpath);
		return file.isDirectory();
	}

	public void readinitfile(String folderPath)
	{
		File folder = new File(folderPath);
		String[] numberOfFiles = folder.list();
		for(String fileName:numberOfFiles){
			try
			{
				String reqId = fileName.substring(0,fileName.indexOf(IDocServerConstants.JOBNAMETAG));
				int nodeofnodes = Integer.parseInt(fileName.substring(fileName.indexOf(IDocServerConstants.NOOFNODESTAG)+3,fileName.indexOf(IDocServerConstants.PERCENT)));
				String jobName = fileName.substring(fileName.indexOf(IDocServerConstants.JOBNAMETAG)+3,fileName.indexOf(IDocServerConstants.NOOFNODESTAG));
				String dataSourceType = fileName.substring(fileName.length()-1);
				initfileInfo.put(reqId, jobName+IDocServerConstants.TILDA+nodeofnodes+IDocServerConstants.TILDA+dataSourceType);
			}
			catch(Exception e)
			{
				//	//StageLogger.getLogger().log(Level.INFO, "Correct file not found. Ignoring parsing.");
			}
		}
	}
}
