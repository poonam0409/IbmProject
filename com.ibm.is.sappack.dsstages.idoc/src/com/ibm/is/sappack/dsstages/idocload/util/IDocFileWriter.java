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
// Module Name : com.ibm.is.sappack.dsstages.idocload.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.StageLogger;

/**
 * Class that wraps the writing of IDocs to files in EDI format in the case of file-based IDoc load.
 */
public class IDocFileWriter {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.util.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	// constants
	private static final String CLASSNAME = IDocFileWriter.class.getName();
	private static final String FILE_SEPARATOR = System.getProperty("file.separator"); //$NON-NLS-1$
	private static final String FILE_UNDERSCORE = "_"; //$NON-NLS-1$

	// members
	private Logger logger;
	private List<IDocFileEntry> listOfIDocs;
	private String fileName;
	private String fileEncoding = "";

	/**
	 * Default constructor
	 * 
	 * @param IDoc file encoding
	 */
	public IDocFileWriter(String encoding) {
		logger = StageLogger.getLogger();
		listOfIDocs = new ArrayList<IDocFileEntry>();
		fileEncoding = encoding;
	}

	/**
	 * This function initializes the IDocFileWriter object by means of generating a filename for IDoc storage.
	 * 
	 * @param idocFilePath
	 *           - the path where the IDoc file will be stored
	 * @param curTransaction
	 *           - the number of the current transaction
	 * @param curNodeNumber
	 *           - the number of the current processing node
	 * @param idocTypeName
	 *           - the name of the IDoc type
	 * @param sapSysName
	 *           - the name of the SAP system
	 * @param invocationID
	 *           - the unique invocation ID of the current job
	 */
	public void initialize(String idocFilePath, int curTransaction, int curNodeNumber, String idocTypeName,
	      String sapSysName, String invocationID) {
		final String METHODNAME =
		      "initialize(String idocFilePath, int curTransaction, int curNodeNumber, String idocTypeName, String sapSysName, String invocationID)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		fileName =
		      getIDocFileName(idocFilePath, curTransaction, curNodeNumber, idocTypeName, sapSysName,
		      		invocationID);

		logger.exiting(CLASSNAME, METHODNAME);
	}

	/**
	 * This function adds an IDocFileEntry object to the list of objects that is to be written to the filesystem later
	 * on.
	 * 
	 * @param entry
	 *           - the IDoc entry to be added
	 */
	public void add(IDocFileEntry entry) {
		final String METHODNAME = "add(IDocFileEntry entry)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		listOfIDocs.add(entry);

		logger.exiting(CLASSNAME, METHODNAME);
	}

	/**
	 * This function writes all IDoc entries to the filesystem. The number of chunks (aka package size) determines the
	 * number of IDocs contained in a single IDoc file.
	 * 
	 * @param numberOfIDocsPerFile
	 *           - the number of IDocs to be contained in a single IDoc file
	 * 
	 * @return <code>true</code> if writing to all files has been successful, <code>false</code> otherwise
	 */
	public boolean flush(int numberOfIDocsPerFile) {
		final String METHODNAME = "flush(int numberOfIDocsPerFile)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		boolean flushed = true;

		Iterator<IDocFileEntry> it = listOfIDocs.iterator();
		int count = 0;

		while (count < numberOfIDocsPerFile && it.hasNext()) {
			flushed = flushed && writeRecordToFile(it.next());
			count++;
		}

		logger.exiting(CLASSNAME, METHODNAME);

		return flushed;
	}
	
	/**
	 * This function returns the name of the IDoc file that has been
	 * generated for this IDocFileWriter instance.
	 *  
	 * @return the name of the IDoc file or an empty string if the name
	 * has not been generated (yet).
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * This function generates a filename for the IDoc file storage. As the filename must be unique it includes a
	 * timestamp (generated at the conductor node before the worker nodes start) as well as other information like IDoc
	 * type name, SAP system name, worker node number and transaction number.
	 * 
	 * @param idocFilePath
	 *           - the path where the IDoc file will be stored
	 * @param curTransaction
	 *           - the number of the current transaction
	 * @param curNodeNumber
	 *           - the number of the current processing node
	 * @param idocTypeName
	 *           - the name of the IDoc type
	 * @param sapSysName
	 *           - the name of the SAP system
	 * @param invocationID
	 *           - the unique invocation ID of the current job
	 * 
	 * @return the generated filename or <code>null</code> if no filename could be generated
	 */
	private String getIDocFileName(String idocFilePath, int curTransaction, int curNodeNumber,
	      String idocTypeName, String sapSysName, String invocationID) {
		final String METHODNAME =
		      "getIDocFileName(String idocFilePath, int curTransaction, int curNodeNumber, String idocTypeName, String sapSysName, String invocationID)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		String filename = null;

		StringBuffer sb = new StringBuffer();
		sb.append(idocFilePath);
		sb.append(FILE_SEPARATOR);
		sb.append(idocTypeName + FILE_UNDERSCORE + sapSysName + FILE_UNDERSCORE + invocationID + FILE_UNDERSCORE
		      + String.valueOf(curNodeNumber) + String.valueOf(curTransaction) + Constants.IDOC_FILE_SUFFIX);
		filename = sb.toString();

		logger.exiting(CLASSNAME, METHODNAME);

		return filename;
	}

	/**
	 * This function writes single IDoc entries to a given file. If the file exists, data will be appended.
	 * 
	 * @param entry
	 *           - the IDoc entry to be written
	 * 
	 * @return <code>true</code> if writing to the file has been successful, <code>false</code> otherwise
	 */
	private boolean writeRecordToFile(IDocFileEntry entry) {
		final String METHODNAME = "writeRecordToFile(IDocFileEntry Entry)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		boolean entryWritten = false;

		if (fileName != null) {
			OutputStreamWriter osw = null;
			BufferedWriter bw = null;

			try {

				// open IDoc file in append mode
				if (this.logger.isLoggable(Level.FINER)) {
					this.logger.log(Level.FINER, "Writing IDoc file {0} in {1} encoding.", new Object[]{fileName, fileEncoding});
				}
				osw = new OutputStreamWriter(new FileOutputStream(fileName, true), fileEncoding);

				if (osw != null) {
					bw = new BufferedWriter(osw);

					if (bw != null) {
						bw.append(entry.retrieve());
						entryWritten = true;
					}
				}
			}
			catch (FileNotFoundException fnfe) {
				logger.log(Level.WARNING, "CC_IDOC_LOAD_FileWriteError", fileName);  //$NON-NLS-1$
			}
			catch (IOException ioe) {
				logger.log(Level.WARNING, "CC_IDOC_LOAD_FileWriteError", fileName);  //$NON-NLS-1$
			}
			finally {
				try {
					if (bw != null) {
						bw.close();
					}
				}
				catch (IOException ioe) {
					logger.log(Level.FINER, "Buffered writer for {0} could not be closed", new Object[] { fileName }); //$NON-NLS-1$
				}
				finally {
					try {
						if (osw != null) {
							osw.close();
						}
					}
					catch (IOException ioe) {
						logger.log(Level.FINER, "Output stream writer for {0} could not be closed", new Object[] { fileName }); //$NON-NLS-1$
					}
				}
			}
		}

		logger.exiting(CLASSNAME, METHODNAME);

		return entryWritten;
	}
}
