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


/*
 * This class is the driving class. It is called first from the Datastage and then the further process 
 * completes.
 * 
 * 
 *      Maintenance log - insert most recent change description at top
 *      Date    		Defect/APAR WI        WHO     	Description.........................................
 *      
 *      27-01-2017		262275				Amit Kumar	Error occurred while running Delta Extract full load for 0FI_GL_14 and COPA data source
 *      
 *       
 */

package com.ibm.is.sappack.deltaextractstage.exec;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoTable;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import com.ibm.is.cc.javastage.api.Capabilities;
import com.ibm.is.cc.javastage.api.Configuration;
import com.ibm.is.cc.javastage.api.OutputRecord;
import com.ibm.is.cc.javastage.api.Processor;
import com.ibm.is.cc.javastage.api.OutputLink;
import com.ibm.is.sappack.deltaextractstage.client.*;
import com.ibm.is.sappack.deltaextractstage.commons.*;
import com.ibm.is.sappack.deltaextractstage.load.*;
public class SapExtractorJavaClientStage extends Processor
{
	private DsSapExtractorParam dsExtractorParam = null;
	private boolean loadDataFlag = false;
	private static boolean endofdata = false;
	private JCoTable tab;
	private int _rowCount;
	private int _rowNumber;
	private Properties userStageProperties=null;
	private OutputLink m_outputLink;
	private ArrayList<JCoTable> datalist = null;
	private InitializeExecutionParams intialize = null;
	private int m_nodeID = -1;
	DataType datatype= null;
	ProcessCompletionHandler completionhandler = null;


	public Capabilities getCapabilities()
	{
	Capabilities capabilities = new Capabilities();
	capabilities.setMinimumInputLinkCount(0);
	capabilities.setMaximumInputLinkCount(0);
	capabilities.setMaximumOutputStreamLinkCount(1);
	capabilities.setMaximumRejectLinkCount(0);
	return capabilities;
	}
	
	public boolean validateConfiguration(Configuration configuration, 
			boolean       isRuntime) throws Exception
			{
		m_nodeID = configuration.getNodeNumber();
		String jobName = configuration.getRuntimeEnvironment().getProperty(Configuration.JOB_NAME_KEY);
		String projectName = configuration.getRuntimeEnvironment().getProperty(Configuration.PROJECT_NAME_KEY);
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"+++++++ jobName for node:"+m_nodeID +":is "+jobName});
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"+++++++ validateConfiguration for node:"+m_nodeID +": +++++++++++"});
		dsExtractorParam = new DsSapExtractorParam();
		dsExtractorParam.setDataStageNodeId(m_nodeID);
		dsExtractorParam.setTotalNumberofNodes(configuration.getNodeCount());
        DsSapExtractorLogger.information("DeltaExtract_76",new Object[]{"DeltaExtract_76", VersionInfo.getVersionInfo()});
		DsSapExtractorLogger.information("DeltaExtract_63",new Object[]{"DeltaExtract_63",m_nodeID });
		DsSapExtractorLogger.information("DeltaExtract_64",new Object[]{"DeltaExtract_64",configuration.getNodeCount() });
		dsExtractorParam.setJobName(projectName+"#"+jobName);
		datalist = new ArrayList<JCoTable>();
		// Specify current link configurations.
		m_outputLink = configuration.getOutputLink(0);
		userStageProperties = configuration.getUserProperties();
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"------- userStageProperties for node:"+m_nodeID +": ---------"+userStageProperties});
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"------- validateConfiguration for node:"+m_nodeID +": ---------"});
		if(!(userStageProperties==null))
			return true;
		else 
			return false;
			}

	public void initialize()
	{
		try
		{
			initializeClasses();
			intialize.initilizeExecutionParameters(0);
			DsSapExtractorLogger.information("DeltaExtract_49",new Object[]{"DeltaExtract_49"});
			intialize.initializeSAPConnection(true);
			DsSapExtractorLogger.information("DeltaExtract_50",new Object[]{"DeltaExtract_50"});
			intialize.idocClientInitialization();
			DsSapExtractorLogger.information("DeltaExtract_51",new Object[]{"DeltaExtract_51"});

		}
		catch(JCoException ex)
		{
			releaseResources();
			DsSapExtractorLogger.fatal("DeltaExtract_FatalError_09",new Object[]{"DeltaExtract_FatalError_09",m_nodeID});
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void initializeClasses()
	{
		datatype = new DataType();
		completionhandler = new ProcessCompletionHandler(dsExtractorParam);
		intialize = new InitializeExecutionParams(userStageProperties,dsExtractorParam,datatype);
	}

	public void terminate()
	{
		DsSapExtractorLogger.information("DeltaExtract_66",new Object[]{"DeltaExtract_66"});
		if(m_nodeID==0)
		{
			DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Inside terminating"});
			releaseResources();
			DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"done terminating"});
			DsSapExtractorLogger.information("DeltaExtract_67",new Object[]{"DeltaExtract_67"});
		}
	}

	public void process()
	{
		try{
			while(! completionhandler.allNodesCompletionFlag())
			{
				while(!endofdata)
				{
					while(!(loadDataFlag || endofdata)) 
					{
						if( datalist.size() > 0)
						{
							readDataPacket();
						} else
						{
							checkIncomingPackets();
						}
					}
					if(loadDataFlag && _rowNumber < _rowCount)
					{
						writeDatatoLink();
					}
					else
						loadDataFlag = false;
				}
			}
		}
		catch(Exception e)
		{
			terminate();
			DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Error in Process :"+e.getMessage()});
			DsSapExtractorLogger.fatal("DeltaExtract_FatalError_10",new Object[]{"DeltaExtract_FatalError_10",e.getMessage()});
		}
		terminate();
	}

	private void releaseResources()
	{
		try {
		//	FileUtils.deleteDirectory(dsExtractorParam.getinfoIdocFolderPath().getParentFile().getParentFile());
			intialize.deleteDirectory(dsExtractorParam.getinfoIdocFolderPath().getParentFile().getParentFile());
			dsExtractorParam.getInitFile().delete();
		} catch (IOException e) {
			DsSapExtractorLogger.warning("DeltaExtract_61",new Object[]{"DeltaExtract_61"});
		}
	}

	public void checkIncomingPackets()
	{
		if(dsExtractorParam.getdataPacketFolderPath().list().length >0)
		{
			DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Getting data from the file for node ID:"+dsExtractorParam.getDataStageNodeId()});
			try
			{
				if(dsExtractorParam.getDataSrcType().equals("HIER"))
					datalist = dsExtractorParam.getReadfileimpl().readHierDataPacketFolder();
				else
				datalist = dsExtractorParam.getReadfileimpl().readDataPacketFolder();
				dsExtractorParam.getRsinfomonitor().jobstartTimestamp = System.currentTimeMillis();
			}
			catch(Exception e)
			{
				DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Error in Getting data from the file for node ID:"+dsExtractorParam.getDataStageNodeId()+ "Error: "+e.getMessage()});
			}
			DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Data fecthed from the file for node ID:"+dsExtractorParam.getDataStageNodeId()});
		}
		else
		{
			if(dsExtractorParam.getRsinfomonitor().checkProcessCompletion())
			{
				DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Checking Info IDocs in folder for node ID:"+dsExtractorParam.getDataStageNodeId()});
				endofdata = true;
				completionhandler.createCompletionfile();
				DsSapExtractorLogger.information("DeltaExtract_55",new Object[]{"DeltaExtract_55"});
			}
		}
	}

	public void readDataPacket()
	{
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"+++++++ readDataPacket ++++++++++ for Node:"+dsExtractorParam.getDataStageNodeId()});
		tab = (JCoTable)datalist.remove(0);
		this._rowCount = tab.getNumRows();
		tab.firstRow();
		_rowNumber = 0;
		loadDataFlag = true;
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Total number of records in this packet for Node:"+dsExtractorParam.getDataStageNodeId()+"is:"+_rowCount +"Setting Load Flag to:"+loadDataFlag});
		DsSapExtractorLogger.information("DeltaExtract_71",new Object[]{"DeltaExtract_71",_rowCount});
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"------- readDataPacket --------- for Node:"+dsExtractorParam.getDataStageNodeId()});
	}

	public void writeDatatoLink()
	{
		OutputRecord outputRecord = m_outputLink.getOutputRecord();
		int colCount = m_outputLink.getColumnCount();
		for(int columnNumber = 0; columnNumber < colCount; columnNumber++)
		{
			try{
				int datatpye = m_outputLink.getColumn(columnNumber).getSQLType();
				String colName = m_outputLink.getColumn(columnNumber).getName();
				outputRecord.setValue(columnNumber, datatype.convertDataType(datatpye,tab.getString(colName)));
			}
			catch (Exception e)
			{
				DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Error in Writing data to link: "+e.getMessage()+" Erroneous Record fields are as follows:"});
				/*262275: Below try-catch block is added for errorenous data to be printed in job log.
				First line will be comma separated <column name>~<data type> values
				Next line will be comma separated column values.  e.g.:
				ANLN1~Char,ANLN2~Char,AUFNR~Char,AUGBL~Char,AUGDT~Date,BELNR~Char,BLART~Char,BLDAT~Date,BSCHL~Char,BSTAT~Char,BUDAT~Date...
				,,,,00000000,0100011788,AB,19990610,50,,19990329,1000,006,            0.00,            0.00...
				We can paste these two lines in a .csv file and open it with ms excel to see column-wise record.*/
				try{
					String record="";
					for(int columnNumberlog = 0; columnNumberlog < colCount; columnNumberlog++){
						record+=m_outputLink.getColumn(columnNumberlog).getName()+"~"+m_outputLink.getColumn(columnNumberlog).getSQLTypeName()+",";
					}
					DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{record});
					record="";
					for(int columnNumberlog = 0; columnNumberlog < colCount; columnNumberlog++){
						record+=tab.getString(m_outputLink.getColumn(columnNumberlog).getName())+",";
					}
					DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{record});
				}catch(Exception er){
					DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"error while generating errorenous data: "+er.getMessage()});
				}
				DsSapExtractorLogger.fatal("DeltaExtract_62",new Object[]{"DeltaExtract_62",e.getMessage()});
			}
		}
		m_outputLink.writeRecord(outputRecord);
		tab.nextRow();
		_rowNumber++;
		if(_rowNumber == _rowCount)
		{
			_rowCount = 0;
			loadDataFlag = false;
		}
	}
	
}
