package com.ibm.is.sappack.deltaextractstage.load;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger;
import com.ibm.is.sappack.deltaextractstage.commons.DsSapExtractorParam;

public class ProcessCompletionHandler {
	
	private DsSapExtractorParam dsExtractorParam = null;
	
	public ProcessCompletionHandler(DsSapExtractorParam dsExtractorParam) {
		this.dsExtractorParam = dsExtractorParam;
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
	//	DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"++++++ allNodesCompletionFlag ++++++ for Node:"+dsExtractorParam.getDataStageNodeId()});
		boolean flag = false;
		int totalNumofFiles1 = dsExtractorParam.getinfoIdocFolderPath().getParentFile().list().length;
		if(((totalNumofFiles1-dsExtractorParam.getTotalNumberofNodes())==2))
		{
			flag = true;
			DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"++++++ allNodesCompletionFlag ++++++ for Node:"+dsExtractorParam.getDataStageNodeId()});
		}
	//	DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"----- allNodesCompletionFlag ----- for Node:"+dsExtractorParam.getDataStageNodeId()});
		return flag;
	}

}
