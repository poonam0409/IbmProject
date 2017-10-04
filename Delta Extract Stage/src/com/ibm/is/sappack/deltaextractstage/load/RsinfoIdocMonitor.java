package com.ibm.is.sappack.deltaextractstage.load;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger;
import com.ibm.is.sappack.deltaextractstage.commons.DsSapExtractorParam;

public class RsinfoIdocMonitor {
	
	private final String BS = ""+(char)47;
	private final String REQSTATE0FOLDER = BS+0+BS;
	private final String REQSTATE1FOLDER = BS+1+BS;
	private final String REQSTATE2FOLDER = BS+2+BS;
	private final String REQSTATE5FOLDER = BS+5+BS;
	private final String REQSTATE8FOLDER = BS+8+BS;
	private final String REQSTATE9FOLDER = BS+9+BS;
	private int rsInfoState_2_count;
	private boolean rsInfoState_0 = false;
	private boolean rsInfoState_1 = false;
	private boolean rsInfoState_8 = false;
	private boolean rsInfoState_9 = false;
	private int seqNumberState_9 = 0;
	private DsSapExtractorParam dsExtractorParam = null;
	private boolean raiseProcessCompletionFlag = false;
	public long jobstartTimestamp;
	private String PROCINTERRUPT="ProcessInterrupted";
	private DataPacketMonitor datapacketmonitor = null;
	
	
	public RsinfoIdocMonitor(DsSapExtractorParam dsExtractorParam, DsSapExtractorLogger extractorLogger) {
		this.dsExtractorParam = dsExtractorParam;
		jobstartTimestamp = System.currentTimeMillis();
	}
	
	public void checkRsInfoState_0() {
		
		String path = dsExtractorParam.getinfoIdocFolderPath().getAbsolutePath();
		File file = new File(path+REQSTATE0FOLDER);
		if(getFileCount(file)>0){
		jobstartTimestamp = System.currentTimeMillis();
		this.rsInfoState_0 = true;
		}
	}
	
	public void checkRsInfoState_1() {

		String path = dsExtractorParam.getinfoIdocFolderPath().getAbsolutePath();
		File file = new File(path+REQSTATE1FOLDER);
		if(getFileCount(file)>0)
		{
		jobstartTimestamp = System.currentTimeMillis();
		this.rsInfoState_1 = true;
		}
	}
	
	public void checkRsInfoState_2_count() {
		
		String path = dsExtractorParam.getinfoIdocFolderPath().getAbsolutePath();
		File file = new File(path+REQSTATE2FOLDER);
		this.rsInfoState_2_count = getFileCount(file);
	}
		
	
	public void checkRsInfoState_5() {
		String path = dsExtractorParam.getinfoIdocFolderPath().getAbsolutePath();
		File file = new File(path+REQSTATE5FOLDER);
		if(getFileCount(file)>0)
		DsSapExtractorLogger.fatal("DeltaExtract_73",new Object[]{"DeltaExtract_73"});
	}
	
	public void checkRsInfoState_8() {
		String path = dsExtractorParam.getinfoIdocFolderPath().getAbsolutePath();
		File file = new File(path+REQSTATE8FOLDER);
		if(getFileCount(file)>0)
		this.rsInfoState_8 = true;
	}
	

	public void checkRsInfoState_9() {
		String path = dsExtractorParam.getinfoIdocFolderPath().getAbsolutePath();
		File file = new File(path+REQSTATE9FOLDER);
		int fileCount = getFileCount(file);
		if(fileCount>0)
		{
		jobstartTimestamp = System.currentTimeMillis();
		this.rsInfoState_9 = true;
		String[] fileName =  file.list();
		for(String filepath:fileName)
		{
			this.seqNumberState_9 =Integer.parseInt(filepath.substring(filepath.indexOf("InfoIdocNum_")+12, filepath.indexOf("_RequestState_9")));
		}
		
		}
	}
	
public void checkRsInfoIdocStatus()
{
	if(!rsInfoState_0)checkRsInfoState_0();
	if(!rsInfoState_1)checkRsInfoState_1();
	checkRsInfoState_2_count();
	checkRsInfoState_5();
	if(!rsInfoState_8)checkRsInfoState_8();
	if(!rsInfoState_9)checkRsInfoState_9();
}

public int getFileCount(File file)
{
	int length = 0;
	if(file.isDirectory())
	{
	 length = file.list().length;
	}
	return length;
}

public boolean checkProcessCompletion()
{
	checkRsInfoIdocStatus();
	if(rsInfoState_0 && rsInfoState_1 && rsInfoState_9){
		datapacketmonitor = new DataPacketMonitor(dsExtractorParam.getinfoIdocFolderPath());
		int diff =seqNumberState_9-rsInfoState_2_count;
		if(diff == 3 && (datapacketmonitor.allDataPacketsReceived()))   // The Info Idoc number for RQState 9 is always 3 greater than the number of infoIdocs received with RQState 2.
		{
			raiseProcessCompletionFlag = true;
			DsSapExtractorLogger.information("DeltaExtract_74",new Object[]{"DeltaExtract_74"});
		}
	}
	else if(rsInfoState_0 && rsInfoState_1 && rsInfoState_8)
	{
		raiseProcessCompletionFlag = true;
		DsSapExtractorLogger.information("DeltaExtract_75",new Object[]{"DeltaExtract_75"});
	}
	else
		 { 
		 checktimeout();
		 checkProcessInterrupted();
		 }
	return raiseProcessCompletionFlag;
}

public void checktimeout()
{
	long currentTimestamp = System.currentTimeMillis();
	long diff = currentTimestamp-jobstartTimestamp;
	if(diff > dsExtractorParam.getExtractionTimeOut())
	throw new RuntimeException("Time out occured.");
}

public void checkProcessInterrupted()
{
	String filePath = dsExtractorParam.getinfoIdocFolderPath().getParent()+BS+PROCINTERRUPT+BS;
	File file =new File(filePath);
	StringBuilder errMsg=new StringBuilder();
	if(getFileCount(file)>0)
	{
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath+PROCINTERRUPT+".log"));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null)
				errMsg.append(sCurrentLine);
			if (br != null)br.close();
		} catch (Exception e) {
			throw new RuntimeException("Unable to read file: "+PROCINTERRUPT+".log");
		}
		throw new RuntimeException(errMsg.toString());
		}
}
}
