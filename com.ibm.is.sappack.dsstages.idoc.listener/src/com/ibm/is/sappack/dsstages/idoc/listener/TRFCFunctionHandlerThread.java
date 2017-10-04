//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
/*
 * 
 *      Maintenance log - insert most recent change description at top
 *      Date    		Defect/APAR WI        WHO     	Description.........................................
 *      
 *      27-01-2017		262275				Amit Kumar	Error occurred while running Delta Extract full load for 0FI_GL_14 and COPA data source
 *      
 * 
*/
package com.ibm.is.sappack.dsstages.idoc.listener;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.idoc.listener.handler.IDocHandler;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerConstants;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerMessages;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

/**
 * TRFCFunctionHandlerThread
 * 
 * Thread to process IDocs from SAP
 * 
 */
class TRFCFunctionHandlerThread implements Callable<String>{
	String tid;
	JCoFunction jcofunction;
	Logger logger;
	String connectionName;
	IDocHandler idocHandler;
	DsSapCreateStagingFiles dssapstaging;
	static final String CLASSNAME = TRFCFunctionHandlerThread.class.getName();
	private final String BS = ""+(char)47;
	private final String AMPERSAND = ""+(char)38;
	private final String JOBNAMETAG = AMPERSAND+"J"+AMPERSAND;
	private final String NOOFNODESTAG = AMPERSAND+"N"+AMPERSAND;
	private final int RECORD_MAX_LENGTH = 1000;
	
	TRFCFunctionHandlerThread(String tid, JCoFunction jcofunction,String connectionName,IDocHandler idocHandler)
	{
		this.jcofunction=jcofunction;
		this.tid=tid;
		this.connectionName=connectionName;
		this.idocHandler=idocHandler;
		this.logger = StageLogger.getLogger();
	}
	
	public String call()
	{

		try
		{
			logger.log(Level.INFO, "A new Thread started to handle current request.");
			logger.log(Level.INFO, "TID: {0}", tid);
			logger.log(Level.INFO, "ConnectionID: {0}", connectionName); 
			StringBuffer filePath = new StringBuffer();
			if(IDocServerImpl.s_packhome == null || IDocServerImpl.s_packhome.length() == 0) {
				String msg = IDocServerMessages.PackHomeNotSet;
				logger.log(Level.SEVERE, msg);
				//throw new RuntimeException(msg);
				return msg;
			}
			filePath.append(IDocServerImpl.s_packhome).append(IDocServerConstants.DSSAPConnections).append(File.separator);
			filePath.append(IDocServerImpl.s_conName).append(File.separator).append(IDocServerConstants.DeltaExtract).append(File.separator);
			String functionName = jcofunction.getName();
			logger.log(Level.INFO, "filePath: {0}", filePath.toString());
			setProcInterruptPath(filePath.toString());
			logger.log(Level.INFO, "Incoming request {0} from SAP", functionName); //$NON-NLS-1$
			if (functionName.equals(IDocServerConstants.RSAR_TRFC_DATA_RECEIVED))
			{
				rsar_trfc_data_receivedHandler(tid,jcofunction,filePath.toString());
				
			}else if (functionName.equals(IDocServerConstants.IDOC_INBOUND_ASYNCHRONOUS))
			{
				idoc_inbound_asynchronousHandler(tid, jcofunction, filePath.toString());
			}
			else
			{
					/* log call of unsupported function */
				logger.log(Level.WARNING, IDocServerMessages.FunctionModuleNotSupported, functionName);
			}
		}catch(RuntimeException rEx)
		{
			logger.log(Level.INFO, "Exception in thread: {0}", rEx.getMessage());
			return rEx.getMessage();
		}catch(Exception ex)
		{
			logger.log(Level.INFO, "Exception in thread: {0}", ex.getMessage());
			logger.log(Level.INFO, "Returning tid: {0} to call rollback():", tid);
			return ex.getMessage();
		}
		return "success";
	}
	
	/**
     * Implements the IDoc data extraction for IDOC_INBOUND_ASYNCHRONOUS.
     *
     * @param jcoContext
     * @param function
     * @param filePath
     */
	private void idoc_inbound_asynchronousHandler(String tid, JCoFunction jcofunction, String filePath)
		throws SQLException, JCoException, RuntimeException
		{
		if(idocHandler != null) {
			logger.log(Level.INFO, "TID Manager: entering into method:idocHandler.processIDocs");
		
			/* process IDocs */
			idocHandler.processIDocs(tid,jcofunction, connectionName,filePath.toString());
			
		} else {
			/* ignore unknown transaction IDs */
			logger.log(Level.WARNING, IDocServerMessages.TIDUnknownTransation, tid);  
		}
		}
	
	/**
     * Implements the IDoc data extraction into the segment table.
     *
     * @param jcoContext
     * @param function
     */
    private void rsar_trfc_data_receivedHandler(String tid,JCoFunction function, String filePath) 
    	throws SQLException, JCoException 
    	{
    	try{
    	logger.log(Level.INFO, "+++ rsar_trfc_data_receivedHandler +++");
    	String idocReqNumber = null;
        String receivedData = "";
		logger.log(Level.INFO, "FilePath is: {0}",filePath);
		StringBuilder str = new StringBuilder();
        dssapstaging = new DsSapCreateStagingFiles();
        JCoStructure importStructure = function.getImportParameterList().getStructure("I_S_HEADER");
        idocReqNumber = importStructure.getString("REQUNR");
		if(IDocServerImpl.errorReqNum.containsKey(idocReqNumber))
			throw new RuntimeException("Request No. "+idocReqNumber+" can't be processed. Error in Listener.");
        IDocServerImpl.initfileInfo.put(tid, idocReqNumber);
        int dataPacketId = Integer.parseInt(importStructure.getString("DATAPAKID"));
        logger.log(Level.INFO, "--------HEADER DATA---------");
        logger.log(Level.INFO, "Request Number for data transfer : {0}" , idocReqNumber);
        logger.log(Level.INFO, "Type of requested data : {0}" , importStructure.getString("REQOBJTYPE"));
        JCoTable table = function.getTableParameterList().getTable("I_T_DATA");
        int numRows = table.getNumRows();
        logger.log(Level.INFO, "Number of rows to write: {0}",numRows);
        int recordLenght=0;
        for (int row = 0; row < numRows; row++) {
        	receivedData = table.getString("DATA");
        	String flag = table.getString("SEQUELFLAG");
        	if(row==0)
        	{
        		recordLenght = receivedData.length();
            	str.append(receivedData);
        	}
        	else
        	{
        		if(flag.equals("X"))
        		{
        			//262275: adding those trailing blank spaces(if any) of previous record which was trimmed by JCoTable API here, when next record comes with continuous flag
        			str.append(addBlankSpaces(recordLenght)+receivedData);
        			recordLenght = receivedData.length();
                }
        		else
        		{
        			str.append(System.lineSeparator());
        			recordLenght = receivedData.length();
                	str.append(receivedData);
        		}
        	}
        	table.nextRow();
        }
        logger.log(Level.INFO, "Before Data Packet File creation.");
        dssapstaging.initializeDataPacketFileCreation(filePath,table,str.toString(),idocReqNumber,dataPacketId);
        logger.log(Level.INFO, "After Data Packet File creation.");
    	}catch(Exception ex){
    		throw new RuntimeException(ex.getMessage());
    	}
    }
    
    public void setProcInterruptPath(String folderPath)
	{
		
    	File folder = new File(folderPath);
    	IDocServerImpl.initfileInfo=new HashMap <String,String>();
    	if(validateFolder(folderPath))
    	{
    	StageLogger.getLogger().log(Level.INFO, "inside method: setProcInterruptPath()");
		String[] numberOfFiles = folder.list();
		StageLogger.getLogger().log(Level.INFO, "numberOfFiles: {0}",numberOfFiles);
		for(String fileName:numberOfFiles){
			try
			{
				String reqId = fileName.substring(0,fileName.indexOf(JOBNAMETAG));
				StageLogger.getLogger().log(Level.INFO, "reqId: {0}",reqId);
				String jobName = fileName.substring(fileName.indexOf(JOBNAMETAG)+3,fileName.indexOf(NOOFNODESTAG));
				StageLogger.getLogger().log(Level.INFO, "jobName: {0}",jobName);
				IDocServerImpl.initfileInfo.put(reqId,folderPath+BS+jobName+BS+reqId+BS);
//				StageLogger.getLogger().log(Level.INFO, "procIntKey: {0}",procIntKey);
			}
			catch(Exception e)
			{
			//	StageLogger.getLogger().log(Level.INFO, "Error occured in setProcInterruptPath: {0}",IDocServerImpl.initfileInfo);
			}
		}
		StageLogger.getLogger().log(Level.INFO, "IDocServerImpl.initfileInfo: {0}",IDocServerImpl.initfileInfo);
    	}
	}
    
	public boolean validateFolder(String folderpath) {
		StageLogger.getLogger().log(Level.INFO, "folderpath inside validateFolder(String folderpath) method: {0}",folderpath);
		File file  = new File(folderpath);
		StageLogger.getLogger().log(Level.INFO, "file.isDirectory() inside validateFolder(String folderpath) method: {0}",file.isDirectory());
		return file.isDirectory();
	}
	
	private String addBlankSpaces(int l){
		try{
			String s="";
			if(l>RECORD_MAX_LENGTH)
				return "";
			for(int i=0;i<RECORD_MAX_LENGTH-l;i++)
				s+=" ";
			return s;
		}catch(Exception e){
			return "";
		}
	}
}
