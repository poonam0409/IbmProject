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

package com.ibm.is.sappack.deltaextractstage.load;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger;
import com.ibm.is.sappack.deltaextractstage.commons.DsSapExtractorParam;
import com.ibm.is.sappack.deltaextractstage.load.hier.ParseXML;
import com.sap.conn.jco.JCoTable;

public class ReadFileImpl{
	DsSapExtractorAdapter extractoradapter;
	DsSapExtractorParam otPara=null;
	ParseXML parsexml=null;
	private HashSet<String> readPacketInfo = null;
	private final String BS = ""+(char)47;
	public final Charset CHARSET = Charset.forName("UTF-8");
	FilenameFilter datafileFilter = null;
	String[] files = null;
	
	
	public ReadFileImpl(DsSapExtractorAdapter extractoradapter,DsSapExtractorParam otPara, DsSapExtractorLogger extractorLogger) {
		readPacketInfo = new HashSet<String>();
		this.extractoradapter = extractoradapter;
		this.otPara = otPara;
		parsexml = new ParseXML(otPara);
	}

	public void deleteFile(String fileName) {
		File file = new File(fileName);
		if(file.delete());
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[] {
				"Deleted file: "+fileName+" Succesfully ..."
		});
	}

	public boolean isFolderEmpty(File dataFilefolder) {
		fileNameFilter();
		files = dataFilefolder.list(datafileFilter);
		if(files.length>0)
		return false;
		else
		return true;
	}

	public ArrayList<JCoTable> readDataPacketFolder() {
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[] {
				"++++++++++ readDataPacketFolder +++++++++  Node Id"+otPara.getDataStageNodeId()
		});
		ArrayList<JCoTable> datapacketList = new ArrayList<JCoTable>();
		File folderPath = otPara.getdataPacketFolderPath();
		if(!(isFolderEmpty(folderPath)))
		{
			for(String filename:files){
				List<String> datapacket = new ArrayList<String>();
				try {
					Path p = Paths.get(folderPath.toString(), filename);
					DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[] {
							"Reading File ..."+p.toString()
					});
					datapacket = Files.readAllLines(p, CHARSET);
					deleteFile(p.toString());
				} catch (IOException e) {
					DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[] {
							"---- Exception Occurred in reading the file---"+e.getMessage()+"\n"
					});
				}
				if(datapacket.size() > 0)
				{
				datapacketList.add(extractoradapter.getStructureFieldLengthList(otPara.getSapTransferStructureName(), datapacket));
				}
			} 
			
			}
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[] {"--------- readDataPacketFolder ---------  Node Id"+otPara.getDataStageNodeId()+ "Number of packets read="+datapacketList.size()});
		return datapacketList;
	}
	
	public ArrayList<JCoTable> readHierDataPacketFolder() {
		
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[] { "++++++++++ Read Hier DataPacketFolder +++++++++  Node Id"+otPara.getDataStageNodeId()});
		ArrayList<JCoTable> datapacketList = new ArrayList<JCoTable>();
		File folderPath = otPara.getdataPacketFolderPath();
		if(!(isFolderEmpty(folderPath)))
		{
			String[] numberOfFiles = folderPath.list();
			for(String filepath:numberOfFiles){
			try {
				saveReadPacketInfo(filepath);
				DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[] {
						"Reading File ..."+folderPath+BS+filepath
				});
				InputStream is = new FileInputStream(folderPath+BS+filepath);
				datapacketList.add(extractoradapter.setHierStructureDetails(is));
				deleteFile(folderPath+BS+filepath);
			} catch (FileNotFoundException e) {
				DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[] {"---- Exception Occurred ---"+e.getMessage()});
			}
			
			}
		}
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[] {"--------- readDataPacketFolder ---------  Node Id"+otPara.getDataStageNodeId()});
		return datapacketList;
		
	}
	
	public void saveReadPacketInfo(String filepath)
	{
		readPacketInfo.add(filepath);
	}
	
	public void fileNameFilter()
	{
		datafileFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith("_ready")) {
					return true;
				} else {
					return false;
				}
			}
		};
	}
	
}
