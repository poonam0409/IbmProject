package com.ibm.is.sappack.deltaextractstage.load;

import java.io.File;
import com.ibm.is.sappack.deltaextractstage.client.Constants;
/*
 * This class is responsible for monitoring the number of data packets received. 
 */

public class DataPacketMonitor {

	File packetCountInfoFolder = null;

	public DataPacketMonitor(File folderPath) {
		
		packetCountInfoFolder = new File(folderPath.getParent()+Constants.BACKSLASH+Constants.PACKETCOUNTFOLDER);
	}
	
	public boolean allDataPacketsReceived()
	{
		int greatestFilenumber = 0;
		String[] fileNames = packetCountInfoFolder.list();

		for(String filepath:fileNames){

			int filenumber = Integer.parseInt(filepath);
			if(filenumber>greatestFilenumber)
			{
				greatestFilenumber = filenumber;
			}
		}

		if(greatestFilenumber == fileNames.length)
			return true;
		else
			return false;
	}

}
