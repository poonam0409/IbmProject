/*// IBM Confidential                                                            

package com.ibm.is.sappack.deltaextractstage.exec;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoTable;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;

import com.ibm.is.cc.javastage.api.Capabilities;
import com.ibm.is.cc.javastage.api.Configuration;
import com.ibm.is.cc.javastage.api.Logger;
import com.ibm.is.cc.javastage.api.OutputRecord;
import com.ibm.is.cc.javastage.api.Processor;
import com.ibm.is.cc.javastage.api.OutputLink;
import com.ibm.is.sappack.deltaextractstage.client.*;
import com.ibm.is.sappack.deltaextractstage.commons.*;
public class SapExtractorJavaClientStageBC extends Processor
{
	private static DsSapExtractorParam dsExtractorParam = null;
	private boolean loadDataFlag = false;
	private static boolean endofdata = false;
	private JCoTable tab;
	private int packetcount=0;
	private int _rowCount;
	private int _rowNumber;
	private Properties userStageProperties=null;
	private OutputLink m_outputLink;
	private ArrayList<JCoTable> datalist = null;
	private InitializeExecutionParamsBC intialize = null;
	private int m_nodeID = -1;


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
		String jobName = configuration.getRuntimeEnvironment().getProperty(Configuration.JOB_NAME_KEY);
		Logger.information("Inside");
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"+++++++ validateConfiguration for node:"+m_nodeID +": +++++++++++"});
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"+++++++ jobName for node:"+m_nodeID +":is "+jobName});
		m_nodeID = configuration.getNodeNumber();
		dsExtractorParam = new DsSapExtractorParam();
		dsExtractorParam.setDataStageNodeId(m_nodeID);
		dsExtractorParam.setTotalNumberofNodes(configuration.getNodeCount());
		dsExtractorParam.setJobName("DStage");
		datalist = new ArrayList<JCoTable>();
		// Specify current link configurations.
		m_outputLink = configuration.getOutputLink(0);
		userStageProperties = configuration.getUserProperties();
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
			DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"+++++++ userStageProperties for node:"+m_nodeID +": +++++++++++"+userStageProperties});
			intialize = new InitializeExecutionParamsBC(userStageProperties,dsExtractorParam);
			intialize.prepareFilterCondition();
			intialize.initilizeExecutionParameters(0);
			//DsSapExtractorLogger.information(new Object[]{"DeltaExtract_49"});
			intialize.initializeSAPConnection(true);
			//DsSapExtractorLogger.information(new Object[]{"DeltaExtract_50"});
			intialize.idocClientInitialization();
			//DsSapExtractorLogger.information(new Object[]{"DeltaExtract_51"});

		}
		catch(JCoException ex)
		{
			releaseResources();
			//DsSapExtractorLogger.information(new Object[]{"Error",m_nodeID+":time:"+System.currentTimeMillis()});
		}
	}

	public void terminate()
	{
		//DsSapExtractorLogger.information(new Object[]{"Entering Terminate node id",m_nodeID+":time:"+System.currentTimeMillis()});
		if(m_nodeID==0)
		{
			DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Inside terminating"});
			releaseResources();
			DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"done terminating"});
		}
	}

	public void process()
	{
		try{
			while(! allNodesCompletionFlag())
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
			releaseResources();
			DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Error in Process :"+e.getMessage()});
		}
		terminate();
	}

	private void releaseResources()
	{
		try {
			System.out.println("Folder to be deleted:"+dsExtractorParam.getinfoIdocFolderPath().getParentFile().getParentFile());
			FileUtils.deleteDirectory(dsExtractorParam.getinfoIdocFolderPath().getParentFile().getParentFile());

			dsExtractorParam.getInitFile().delete();
		} catch (IOException e) {
			//DsSapExtractorLogger.information(new Object[]{"DeltaExtract_61"});
			//	e.printStackTrace();
		}
	}

	public void checkIncomingPackets()
	{
		if(dsExtractorParam.getdataPacketFolderPath().list().length >0)
		{
			DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Getting data from the file for Node"+dsExtractorParam.getDataStageNodeId()});
			try
			{
				datalist = dsExtractorParam.getReadfileimpl().readDataPacketFolder();
			}
			catch(Exception e)
			{
				DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Error in Getting data from the file for Node"+dsExtractorParam.getDataStageNodeId()+ "Error: "+e.getMessage()});
			}
			DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Data fecthed from the file for Node"+dsExtractorParam.getDataStageNodeId()});
		}
		else
		{
			if(dsExtractorParam.getRsinfomonitor().checkProcessCompletion())
			{
				DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"CHecking Info Idocs in Folder for Node:"+dsExtractorParam.getDataStageNodeId()});
				//DsSapExtractorLogger.information(new Object[]{"DeltaExtract_54"});
				endofdata = true;
				createCompletionfile();
				//DsSapExtractorLogger.information(new Object[]{"DeltaExtract_55"});
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
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"------- readDataPacket --------- for Node:"+dsExtractorParam.getDataStageNodeId()});
	}

	public void writeDatatoLink()
	{
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"++++++ writeDatatoLink +++++++ for Node:"+dsExtractorParam.getDataStageNodeId()});
		OutputRecord outputRecord = m_outputLink.getOutputRecord();
		int colCount = m_outputLink.getColumnCount();
		for(int columnNumber = 0; columnNumber < colCount; columnNumber++)
		{
			try{
				int datatpye = m_outputLink.getColumn(columnNumber).getSQLType();
				outputRecord.setValue(columnNumber, convertDataType(datatpye,tab.getString(columnNumber).replaceAll(" ", "")));
			}
			catch (Exception e)
			{
				DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Error in Writing data to link: "+e.getMessage()});
				//DsSapExtractorLogger.information(new Object[]{"DeltaExtract_62"});
			}
		}
		m_outputLink.writeRecord(outputRecord);
		tab.nextRow();
		_rowNumber++;
		if(_rowNumber == _rowCount)
		{
			_rowCount = 0;
			loadDataFlag = false;
			if(packetcount % 10 == 0)
			{
				System.gc();
			}
		}
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"------- writeDatatoLink ------- for Node:"+dsExtractorParam.getDataStageNodeId()});
	}

	public Object convertDataType(int datatype, String dataValue)
	{
		dataValue = dataValue.contains("-") ? (new StringBuilder().append("-").append(dataValue.replaceAll("-", "")).toString()) : dataValue;
		Object dataValActualType = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		switch (datatype) {
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_DECIMAL:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_DOUBLE:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_FLOAT:
			dataValActualType = java.math.BigDecimal.valueOf(Double.parseDouble(dataValue));
			break;

		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_INTEGER:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_TINYINT:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_BIGINT:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_NUMERIC:
			if(!(dataValue == null))
				dataValActualType = Long.valueOf(dataValue);
			break;

		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_BINARY:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_BIT:
			dataValActualType = Integer.parseInt(dataValue);

			break;

		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_CHAR:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_WCHAR:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_VARCHAR:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_LONGVARCHAR:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_WVARCHAR:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_WLONGVARCHAR:
			dataValActualType = dataValue;

			break;

		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_DATE:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_TIME:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_TIMESTAMP:

			if (!(dataValue == null || dataValue.isEmpty() || dataValue.trim().equals("00000000"))) {
				try {
					dataValActualType = new java.sql.Date(sdf.parse(dataValue).getTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			break;

		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_UNKNOWN:
			dataValActualType = dataValue;
			break;
		}
		return dataValActualType;
	}

	public void createCompletionfile()
	{
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"++++++ createCompletionfile +++++++ for Node:"+dsExtractorParam.getDataStageNodeId()});
		File file = new File(dsExtractorParam.getinfoIdocFolderPath().getParent()+"/successFlag_"+dsExtractorParam.getDataStageNodeId());
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"------ createCompletionfile ------ for Node:"+dsExtractorParam.getDataStageNodeId()});
	}

	public boolean allNodesCompletionFlag()
	{
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"++++++ allNodesCompletionFlag ++++++ for Node:"+dsExtractorParam.getDataStageNodeId()});
		boolean flag = false;
		int totalNumofFiles1 = dsExtractorParam.getinfoIdocFolderPath().getParentFile().list().length;
		if(((totalNumofFiles1-dsExtractorParam.getTotalNumberofNodes())==2))
		{
			flag = true;
		}
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"----- allNodesCompletionFlag ----- for Node:"+dsExtractorParam.getDataStageNodeId()});
		return flag;

	}
}
*/