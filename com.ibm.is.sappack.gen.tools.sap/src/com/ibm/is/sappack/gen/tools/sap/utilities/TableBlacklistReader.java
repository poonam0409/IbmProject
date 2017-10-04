//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.utilities
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.ibm.is.sappack.gen.tools.sap.activator.Activator;

public class TableBlacklistReader {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static List<String> getCheckTableBlacklist(String blackListFileName) throws IOException {
		List<String> blackList = new ArrayList<String>();
		Activator.getLogger().fine("checktable blacklist file name: " + blackListFileName); //$NON-NLS-1$
		BufferedReader reader = null;
		InputStreamReader isr = null;
		FileInputStream fis = null;

		if ((blackListFileName != null) && !blackListFileName.trim().isEmpty()) {
			File blackListFile = new File(blackListFileName);
			Activator.getLogger().fine("checktable blacklist file exists: " + blackListFile.exists()); //$NON-NLS-1$
			try {
				if (blackListFile.exists()) {
					fis = new FileInputStream(blackListFile);
					isr = new InputStreamReader(fis);
					reader = new BufferedReader(isr);

					String tableName = reader.readLine();

					while (tableName != null) {
						if (!blackList.contains(tableName.trim()) && !tableName.equals("")) { //$NON-NLS-1$
							blackList.add(tableName.trim());
						}
						tableName = reader.readLine();
					}
				}
			}
			finally {
				try {
					if (reader != null) {
						reader.close();
					}
				}
				finally {
					try {
						if (isr != null) {
							isr.close();
						}
					}
					finally {
						try {
							if (fis != null) {
								fis.close();
							}
						}
						finally {
							// nothing to be done anymore
						}					
					}
				}
			}
		}
		
		Activator.getLogger().fine("blacklist: " + blackList); //$NON-NLS-1$
		
		return blackList;
	}

}
