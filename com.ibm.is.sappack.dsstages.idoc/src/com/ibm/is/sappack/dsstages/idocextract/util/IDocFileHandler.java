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
// Module Name : com.ibm.is.sappack.dsstages.idocextract.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocextract.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.CCFResource;
import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.StageLogger;


/**
 * The IDoc file handler reads and parses the contents of the 
 * IDoc files created by the IDoc Listener.
 */

public class IDocFileHandler {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocextract.util.Copyright.IBM_COPYRIGHT_SHORT;
	}

	// constants
	static final String CLASSNAME = IDocFileHandler.class.getName();
	
	private static final String NEWLINE = "\n"; //$NON-NLS-1$
	private static final String encoding = com.ibm.is.sappack.dsstages.common.Utilities.getPlatformEncodingForIDocFiles();
	private static final int OVERALL_SEGMENT_LENGTH = 1063;


	private Logger logger = null;
	private Map<String, List<String>> segmentDataMap = null;
	private File file = null;

	/**
	 * Default constructor
	 */
	public IDocFileHandler(File file) {
		logger = StageLogger.getLogger();
		final String METHODNAME = "<init>"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);
		this.file = file;
		logger.exiting(CLASSNAME, METHODNAME);	
	}

	/**
	 * Reads and parses the IDoc file. 
	 * @return 
	 */
	public boolean initialize(){
		final String METHODNAME = "initialize"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		boolean result = false; 
		try {
			String idocContent = readIDocContentFromFile(this.file);
			if (idocContent != null){
				result = parseIdocFile(idocContent);
			}

		} catch (IOException e) {
			this.logger.log(Level.SEVERE, "CC_IDOC_FileReadException", e);
		}

		logger.exiting(CLASSNAME, METHODNAME);
		return result; 
	}

	/**
	 * Retrieve all segments of a segment type from the IDoc file 
	 * 
	 * @param segmentName name of the IDoc segment 
	 * @return list containing the segment data for all segments in the IDoc file of a specific type
	 */
	public List<String> getSegmentData(String segmentName){
		final String METHODNAME = "getSegmentData"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);
		
		List<String> segmentDataList = null;
		if (this.segmentDataMap.containsKey(segmentName)){
			segmentDataList = this.segmentDataMap.get(segmentName);
		}
		logger.exiting(CLASSNAME, METHODNAME);
		return segmentDataList;
	}
	

	/**
	 * Read the IDoc content from the specified file.  
	 * 
	 * @param idocFile a file written by the IDocListener that contains an IDoc.
	 * */
	private String readIDocContentFromFile(File idocFile) throws IOException{
		final String METHODNAME = "readIDocContentFromFile()"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		String fileName = idocFile.getName();

		logger.log(Level.FINE, "Reading File {0} in {1} encoding", new Object[]{fileName, encoding}); //$NON-NLS-1$

		FileInputStream fis = null;
		InputStreamReader isr = null;
		
		// The file is written in double-byte encoding
		int bytesToRead = (int) (idocFile.length() / 2);
		char[] buf = new char[bytesToRead];
		String buffer = null;

		try {
			fis = new FileInputStream(idocFile);
			isr = new InputStreamReader(fis, encoding);


			int bytesRead = isr.read(buf);

			// sanity check: is the number of bytes actually read from the stream
			// identical to what we've expected
			if (!(bytesRead == bytesToRead)) {
				throw new IOException(CCFResource.getCCFMessage("CC_IDOC_EXTRACT_IDocFileReadError", fileName)); //$NON-NLS-1$
			}

			buffer = new String(buf);
			
		}
		catch (IOException ioe) {
			throw new IOException(CCFResource.getCCFMessage("CC_IDOC_EXTRACT_IDocFileReadError", fileName), ioe); //$NON-NLS-1$
		}
		finally {
			try {
				if (fis != null) {
					fis.close();
				}
			}
			catch (IOException ioe) {
				logger.log(Level.FINER, "File input stream for {0} could not be closed", new Object[] { fileName }); //$NON-NLS-1$
			}
		}

		logger.exiting(CLASSNAME, METHODNAME);
		return(buffer);
	}
	
	/**  
	 * Parse the content of the IDoc file to extract the segment data. 
	 * @param idocContent IDoc content
	 */
	private boolean parseIdocFile(String idocContent){
		final String METHODNAME = "parseIdocFile"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		this.segmentDataMap = new HashMap<String, List<String>>();
		String line = ""; //$NON-NLS-1$
		int start = 0;
		int end = 0;

		logger.log(Level.FINEST, "IDoc file content: {0} ", new Object[] { idocContent  }); //$NON-NLS-1$
		
		/* Skip the first 3 lines:
		 *  - IDoc type 
		 *  - extended IDoc type
		 *  - release */
		for (int i = 1; i <= 3; i++){
			start = idocContent.indexOf(NEWLINE, start) + 1;
		}
				
		/* check that we have valid IDoc content */
		if (start <= 0 || start > idocContent.length()){
			logger.log(Level.FINE, "IDoc file content does not have the expected format"); //$NON-NLS-1$
			return false; 
		}

		/* Extract control record from file
		 * The control record always is the first segment in the IDoc file. 
		 * 
		 * TABNAM  Name of the table structure (valid values starting with EDI_DC)
		 */
		end = idocContent.indexOf(NEWLINE, start); 
		line = idocContent.substring(start, end);
		logger.log(Level.FINEST, "Control record starts at {0} ends at {1} data: {2} ", new Object[] { start, end, line }); //$NON-NLS-1$
		
		String controlrecordName = line.substring(0, 10).trim();
		if (controlrecordName.indexOf("EDI_DC") == -1){ //$NON-NLS-1$ 
			// should never happen
			logger.log(Level.FINEST, "ERROR: Invalid control record: {0}", controlrecordName ); //$NON-NLS-1$
		}
		
		logger.log(Level.FINEST, "Extracted control record data: {0} ", line ); //$NON-NLS-1$
			
		List<String> controlrecordBuffer = this.segmentDataMap.get(Constants.IDOC_CONTROL_RECORD_SEGMENT_TYPE_NAME); 
		if (controlrecordBuffer == null){
			controlrecordBuffer = new ArrayList<String>();
			controlrecordBuffer.add(line);
			segmentDataMap.put(Constants.IDOC_CONTROL_RECORD_SEGMENT_TYPE_NAME, controlrecordBuffer);
		}else{
			//should never happen
			logger.log(Level.FINE, "Invalid IDoc file: more than 1 control record"); //$NON-NLS-1$
		}
		
		/* Extract segment data from file:
		 * 
		 * Each line represents a segment with the following format: 
		 * <SEGNAM><MANDT><DOCNUM><SEGNUM><PSGNUM><HLEVEL><SDATA>
		 * 
		 * where:
		 * 
		 * SEGNAM name of the segment   CHAR(30)
		 * MANDT  client                CHAR(3)
		 * DOCNUM IDoc number           CHAR(16)
		 * SEGNUM segment number        CHAR(6)
		 * PSGNUM parent segment number CHAR(6)
		 * HLEVEL hierarchy level       CHAR(2)
		 * SDATA  actual segment data   CHAR(1000)
		 *  */		
		start = end+1; //set start to the first segment following the control record
		while (idocContent.indexOf(NEWLINE, start) != -1){
			
			end = start + OVERALL_SEGMENT_LENGTH;
			
			line = idocContent.substring(start, end);
			logger.log(Level.FINEST, "Segment starts at {0} ends at {1} data: {2} ", new Object[] { start, end, line }); //$NON-NLS-1$
			
			String segmentName = line.substring(0, 30).trim();
			logger.log(Level.FINEST, "Extracted segment name: {0} segment data: {1} ", new Object[] { segmentName, line }); //$NON-NLS-1$
				
			List<String> segmentDataBuffers = this.segmentDataMap.get(segmentName); 
			if (segmentDataBuffers != null){
				segmentDataBuffers.add(line);
			}else{
				segmentDataBuffers = new ArrayList<String>();
				segmentDataBuffers.add(line);
				segmentDataMap.put(segmentName, segmentDataBuffers);
			}
			start = end+1;
		}
		logger.exiting(CLASSNAME, METHODNAME);
		return true;
	}
}
