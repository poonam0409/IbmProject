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

package com.ibm.is.sappack.deltaextractstage.client;

import java.util.Random;
import java.util.logging.Level;
import com.ibm.is.sappack.deltaextractstage.commons.DsSapExtractorParam;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

public class PreRunConfiguration {
	
	private DsSapExtractorParam dsExtractorParam = null;
	boolean status = false;
	private final String ZDS_DE_CONFIGURE_V8_0 = "ZDS_DE_CONFIGURE_V8_0";
	private final String ZDS_DE_LS_CONFIGURE_V8_0 = "ZDS_DE_LS_CONFIGURE_V8_0";
	private final String ZDS_DE_ROOSPRMSC_CREATE_V8_0 = "ZDS_DE_ROOSPRMSC_CREATE_V8_0";
	private final String ZDS_DE_UPDATE_CHGE_POINTR_V8_0 = "ZDS_DE_UPDATE_CHGE_POINTR_V8_0";
	private final String ZDS_DE_TABL_ACTIVATE_V8_0 = "ZDS_DE_TABL_ACTIVATE_V8_0";
	private JCoDestination extractorDestination = null;
	
	public PreRunConfiguration(JCoDestination extractorDestination, DsSapExtractorParam dsExtractorParam) {
		
		this.extractorDestination = extractorDestination;
		this.dsExtractorParam = dsExtractorParam;
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000",  new Object[] {
				"Inside PreRunConfig..."
		});
	}
	
	
	public boolean udpateRSBASIDOCTable()
	{
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000",  new Object[] {
				"updating RSBASIDOCTable..."
		});
		try {
			JCoFunction function = extractorDestination.getRepository().getFunction(ZDS_DE_CONFIGURE_V8_0);
			function.getImportParameterList().setValue("IV_RLOGSYS", dsExtractorParam.getSenderPort());
			function.execute(extractorDestination);
			return readETReturn(function);
			
		}
		catch (JCoException e) {
			DsSapExtractorLogger.fatal("DeltaExtract_FatalError_01",new Object[]{e.getMessage()});
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public boolean updateROOSGENTable()
	{
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000",  new Object[] {
				"updating ROOSGENTable..."
		});
		try {
			JCoFunction function = extractorDestination.getRepository().getFunction(ZDS_DE_LS_CONFIGURE_V8_0);
			function.getImportParameterList().setValue("IV_OLTPSOURCE", dsExtractorParam.getSegmentStructure());
			function.getImportParameterList().setValue("IV_RLOGSYS", dsExtractorParam.getSenderPort());
			function.getImportParameterList().setValue("IV_TRAN_TYPE", dsExtractorParam.getDataSrcType());
			function.execute(extractorDestination);
			return readETReturn(function);
			
		} catch (JCoException e) {
			DsSapExtractorLogger.fatal("DeltaExtract_FatalError_01",new Object[]{e.getMessage()});
			throw new RuntimeException(e.getMessage());
		}
	}
	
	
	public boolean udpateROOSPRMSCTable()
	{
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000",  new Object[] {
				"updating ROOSPRMSCTable..."
		});
		try {
			JCoFunction function = extractorDestination.getRepository().getFunction(ZDS_DE_ROOSPRMSC_CREATE_V8_0);
			function.getImportParameterList().setValue("IV_OLTPSOURCE", dsExtractorParam.getSegmentStructure());
			function.getImportParameterList().setValue("IV_RLOGSYS", dsExtractorParam.getSenderPort());
			String randomNumber = generateDynamicRequestNumber();
			DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000",  new Object[] {
					"randomNumber..."+randomNumber
			});
			function.getImportParameterList().setValue("IV_INITRNR", randomNumber);
			function.execute(extractorDestination);
			return readETReturn(function);
			
		} catch (JCoException e) {
			DsSapExtractorLogger.fatal("DeltaExtract_FatalError_01",new Object[]{e.getMessage()});
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public boolean updateTBDA2Table()
	{
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000",  new Object[] {
				"updatePointer..."
		});
		try {
			JCoFunction function = extractorDestination.getRepository().getFunction(ZDS_DE_UPDATE_CHGE_POINTR_V8_0);
			function.getImportParameterList().setValue("IV_OLTPSOURCE", dsExtractorParam.getSegmentStructure());
			function.getImportParameterList().setValue("IV_RLOGSYS", dsExtractorParam.getSenderPort());
			function.getImportParameterList().setValue("IV_TRAN_TYPE", dsExtractorParam.getDataSrcType());
			function.execute(extractorDestination);
			return readETReturn(function);
			
		} catch (JCoException e) {
			DsSapExtractorLogger.fatal("DeltaExtract_FatalError_01",new Object[]{e.getMessage()});
			throw new RuntimeException(e.getMessage());
		}
	}
	
	
	public boolean activateDatasource()
	{
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000",  new Object[] {
				"Activate datasource..."
		});
		try {
			JCoFunction function = extractorDestination.getRepository().getFunction(ZDS_DE_TABL_ACTIVATE_V8_0);
			function.getImportParameterList().setValue("IV_OLTPSOURCE", dsExtractorParam.getSegmentStructure());
			function.getImportParameterList().setValue("IV_RLOGSYS", dsExtractorParam.getSenderPort());
			function.execute(extractorDestination);
			return readETReturn(function);
			
		} catch (JCoException e) {
			DsSapExtractorLogger.fatal("DeltaExtract_FatalError_01",new Object[]{e.getMessage()});
			throw new RuntimeException(e.getMessage());
		}
	}
	
	
	
	
	public void updatetables()
	{
		if(udpateRSBASIDOCTable())
			if(updateROOSGENTable())
			{
				if(! dsExtractorParam.getDataSrcType().equals("HIER"))
				activateDatasource();
				if(dsExtractorParam.getDataSrcType().equals("TEXT")||dsExtractorParam.getDataSrcType().equals("ATTR"))
				{	if(updateTBDA2Table());
				    if(dsExtractorParam.getDataFetchMode().equals("D"))
					udpateROOSPRMSCTable();
				}
				else
				{
					if(dsExtractorParam.getDataFetchMode().equals("D"))
						udpateROOSPRMSCTable();
				}
			}
		
	}
	
	private boolean readETReturn(JCoFunction function) {
		JCoTable table = function.getTableParameterList().getTable("ET_RETURN");
		String type = table.getValue("TYPE").toString();
		String message = table.getValue("MESSAGE").toString();
		if("S".equals(type))
		{
			DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[] {
					"SuccesFull in updating RFC..."+function.getName() +"Message:"+message
			});
			DsSapExtractorLogger.information("DeltaExtract_10000",new Object[]{message});
			return true;
		}
		else
		{
			DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[] {
					"Error in updating RFC..."+function.getName() +"Message:"+message
			});
			DsSapExtractorLogger.fatal("DeltaExtract_FatalError_01",new Object[]{message});
			return false;
		}
	}
	
	/**
     * Generates dynamic request number Request Number Pattern :
     * DS<1712063026>
     *
     * @return
     */
    private String generateDynamicRequestNumber() {
    	StringBuilder buff = new StringBuilder();
    	buff.append("DS");
    	buff.append(randomNumberGenrator());
       return buff.toString();
    }
    
    public int randomNumberGenrator()
	{
		Random number = new Random();
		return Math.abs(number.nextInt());
	}
	
	}

