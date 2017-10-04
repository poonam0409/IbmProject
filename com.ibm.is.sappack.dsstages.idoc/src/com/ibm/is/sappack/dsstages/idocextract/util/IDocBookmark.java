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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.Utilities;

/**
 * IDocBookmark class that encapsulates the bookmark file actions for IDoc extract jobs.
 */
public class IDocBookmark {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocextract.util.Copyright.IBM_COPYRIGHT_SHORT;
	}

	// constants
	private static final String CLASSNAME = IDocBookmark.class.getName();
	private static final String BMK_FILE_EXT = ".ibk"; //$NON-NLS-1$
	private static final String BMK_ENTRY_DELIMITER = "|"; //$NON-NLS-1$
	private static final int BMK_NUMBER_DELIMITED_ENTRIES = 3;
	private static final String BMK_INITIAL_TIMESTAMP = "0"; //$NON-NLS-1$
	private static final String BMK_FILE_ENCODING = "UTF8"; //$NON-NLS-1$
	private static final String FILENAME_UNDERSCORE = "_"; //$NON-NLS-1$

	private static String FILE_SEPARATOR = System.getProperty("file.separator"); //$NON-NLS-1$

	private static final String IDOC_BOOKMARK_FNFE_MSG = "bookmark file {0} was not found"; //$NON-NLS-1$
	private static final String IDOC_BOOKMARK_IOE_MSG = "error accessing bookmark file {0}"; //$NON-NLS-1$
	private static final String IDOC_BOOKMARK_SE_MSG = "security violation trying to access bookmark file {0}"; //$NON-NLS-1$
	private static final String IDOC_BOOKMARK_FILEEXISTS_MSG = "bookmark file {0} already exists"; //$NON-NLS-1$

	// members
	private Logger logger = null;

	private enum BmkTimestamp {
		CURRENT, PREVIOUS, UPDATE
	};

	private String bmkFileName = null;
	private boolean bmkInitialized = false;

	/**
	 * Default constructor
	 */
	public IDocBookmark() {
		this.logger = StageLogger.getLogger();
	}
	
	/**
	 * This function initializes an object of type IDocBookmark by means of generating a filename for the bookmark
	 * handling.
	 * 
	 * @param jobName
	 *           - the name of the IDoc extract job that owns the bookmark file
	 * @param idocType
	 *           - the name of the IDoc type which is handled by the IDoc extract job
	 * @param idocExtractDir
	 *           - the directory where the bookmark file will be stored
	 */
	public void initialize(String jobName, String idocType, String idocExtractDir) {
		final String METHODNAME = "initialize(String jobName, String idocType, String idocExtractDir)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME, new Object[]{jobName, idocType, idocExtractDir});

		setBmkFileName(jobName, idocType, idocExtractDir);

		// check if the bookmark file is empty (which should not be the case at this moment
		// since the conductor node should have initialized it with an entry prior to the worker nodes
		// accessing the file)
		if (!bmkFileIsEmpty(this.bmkFileName)) {
			this.bmkInitialized = true;
		}

		logger.exiting(CLASSNAME, METHODNAME);
	}

	/**
	 * This function initializes an object of type IDocBookmark by allocating a bookmark file resource by means of naming
	 * and allocating it and creating an initial bookmark entry. This function should only be called once for an object
	 * of type IDocBookmark. The initialization status of the object can be obtained by calling {@link #isInitialized()
	 * isInitialized()} accordingly.
	 * 
	 * @param jobName
	 *           - the name of the IDoc extract job that owns the bookmark file
	 * @param idocType
	 *           - the name of the IDoc type which is handled by the IDoc extract job
	 * @param idocExtractDir
	 *           - the directory where the bookmark file will be stored
	 * 
	 * @return <code>true</code> if the bookmark file has been allocated successfully, <code>false</code> otherwise
	 * 
	 * @see com.ibm.is.sappack.dsstages.idocextract.util.IDocBookmark#isInitialized
	 */
	public boolean setup(String jobName, String idocType, String idocExtractDir) {
		final String METHODNAME = "setup(String jobName, String idocType, String idocExtractDir)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME, new Object[]{jobName, idocType, idocExtractDir});

		boolean fileAvailable = false;
		boolean initialEntryCreated = false;
		
		// make sure that bookmark file is only initialized if necessary
		// (avoiding a repeated initialization of the same object)
		if (!this.bmkInitialized) {
			setBmkFileName(jobName, idocType, idocExtractDir);
			fileAvailable = createFile(getFileName());
			if (fileAvailable){
				initialEntryCreated = createInitialBmkEntry(getFileName());
			}
			this.bmkInitialized = fileAvailable && initialEntryCreated;
		}
		logger.exiting(CLASSNAME, METHODNAME, fileAvailable && initialEntryCreated);

		return fileAvailable && initialEntryCreated;
	}

	/**
	 * This function checks if the IDocBookmark object has been properly initialized.
	 * 
	 * @return <code>true</code> if the IDocBookmark object is initialized, <code>false</code> otherwise
	 * 
	 * @see com.ibm.is.sappack.dsstages.idocextract.util.IDocBookmark#initialize
	 */
	public boolean isInitialized() {
		return this.bmkInitialized;
	}

	/**
	 * This function returns the name of the bookmark file allocated for the IDocBookmark object.
	 * 
	 * @return the name of the bookmark file allocated for the IDocBookmark object or <code>null</code> if the
	 *         IDocBookmark object has not been properly initialized
	 */
	public String getFileName() {
		return this.bmkFileName;
	}

	/**
	 * This function retrieves the value of the CURRENT field from the bookmark entry.
	 * 
	 * @return the value of the CURRENT field from the bookmark entry or <code>null</code> if the retrieval of the value
	 *         failed
	 */
	public Long getCurrent() {
		final String METHODNAME = "getCurrent()"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		Long current = null;
		String fileName = getFileName();

		String currentString = readTimestampFromBmkFile(fileName, BmkTimestamp.CURRENT);

		if (currentString != null) {
			current = Long.valueOf(currentString);
		}

		logger.exiting(CLASSNAME, METHODNAME, current);

		return current;
	}

	/**
	 * This function retrieves the value of the PREVIOUS field from the bookmark entry.
	 * 
	 * @return the value of the PREVIOUS field from the bookmark entry or <code>null</code> if the retrieval of the value
	 *         failed
	 */
	public Long getPrevious() {
		final String METHODNAME = "getPrevious()"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		Long previous = null;
		String fileName = getFileName();

		String previousString = readTimestampFromBmkFile(fileName, BmkTimestamp.PREVIOUS);

		if (previousString != null) {
			previous = Long.valueOf(previousString);
		}

		logger.exiting(CLASSNAME, METHODNAME, previous);

		return previous;
	}

	/**
	 * This function updates the bookmark entry by applying the actual time this function is called to the CURRENT
	 * timestamp field. The PREVIOUS timestamp field will be assigned the former value of the CURRENT timestamp field.
	 * 
	 * @return <code>true</code> if the bookmark file was updated successfully, <code>false</code> otherwise
	 * 
	 * @see com.ibm.is.sappack.dsstages.idocextract.util.IDocBookmark#reset
	 */
	public boolean update() {
		final String METHODNAME = "update()"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		boolean result = false;
		Long newCurrent = null;
		String fileName = getFileName();

		String currentTimestamp = readTimestampFromBmkFile(fileName, BmkTimestamp.CURRENT);

		if (currentTimestamp != null) {
			StringBuffer bmkEntry = new StringBuffer();

			// CURRENT field gets current time
			newCurrent = Long.valueOf(Calendar.getInstance().getTimeInMillis());
			bmkEntry.append(newCurrent.toString());
			bmkEntry.append(BMK_ENTRY_DELIMITER);

			// PREVIOUS field gets previous CURRENT field entry
			bmkEntry.append(currentTimestamp);
			bmkEntry.append(BMK_ENTRY_DELIMITER);

			// UPDATE field always gets current time
			bmkEntry.append(newCurrent.toString());

			result = writeToBmkFile(fileName, bmkEntry.toString());
		}else
			logger.log(Level.FINE, "Timestamp could not be retrieved"); //$NON-NLS-1$


		logger.exiting(CLASSNAME, METHODNAME, result);

		return result;
	}

	/**
	 * This function resets the bookmark entry by applying the value of the PREVIOUS timestamp to the CURRENT timestamp
	 * field. The PREVIOUS timestamp field will remain unchanged.
	 * 
	 * @return <code>true</code> if the bookmark update succeeds, <code>false</code> otherwise
	 * 
	 * @see com.ibm.is.sappack.dsstages.idocextract.bookmark#update
	 */
	public boolean reset() {
		final String METHODNAME = "reset()"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		boolean fileUpdated = false;
		String fileName = getFileName();

		if (fileExists(fileName)) {
			if (!bmkFileIsEmpty(fileName)) {
				String previousTimestamp = readTimestampFromBmkFile(fileName, BmkTimestamp.PREVIOUS);

				if (previousTimestamp != null) {
					StringBuffer bmkEntry = new StringBuffer();

					// CURRENT field gets PREVIOUS field entry
					bmkEntry.append(previousTimestamp);
					bmkEntry.append(BMK_ENTRY_DELIMITER);

					// PREVIOUS field get PREVIOUS field entry (virtually remain unchanged)
					bmkEntry.append(previousTimestamp);
					bmkEntry.append(BMK_ENTRY_DELIMITER);

					// UPDATE field always gets current time
					bmkEntry.append(Calendar.getInstance().getTimeInMillis());

					if (writeToBmkFile(fileName, bmkEntry.toString())) {
						fileUpdated = true;
					}
				}
			}
		}

		logger.exiting(CLASSNAME, METHODNAME, fileUpdated);

		return fileUpdated;
	}

	/**
	 * This function checks if the bookmark file is empty.
	 * 
	 * @param fileName
	 *           - the name of the bookmark file, fully qualified
	 * 
	 * @return <code>true</code> if the file is empty, <code>false</code> otherwise
	 */
	private boolean bmkFileIsEmpty(String fileName) {
		final String METHODNAME = "bmkFileIsEmpty(String fileName)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		boolean fileIsEmpty = false;

		if (fileExists(fileName)) {
			FileInputStream fis = null;

			try {
				fis = new FileInputStream(fileName);

				if (fis != null) {
					int fileContents = fis.read();

					// if the above read() method returns "-1" the file is empty (nothing to read)
					if (fileContents == -1) {
						fileIsEmpty = true;
					}
				}
			}
			catch (FileNotFoundException fnfe) {
				logger.log(Level.FINE, IDOC_BOOKMARK_FNFE_MSG, new Object[] { fileName });
			}
			catch (IOException ioe) {
				logger.log(Level.FINE, IDOC_BOOKMARK_IOE_MSG, new Object[] { fileName });
			}
			finally {
				try {
					if (fis != null) {
						fis.close();
					}
				}
				catch (IOException ioe) {
					logger.log(Level.FINE, IDOC_BOOKMARK_IOE_MSG, new Object[] { fileName });
				}
			}
		}

		logger.exiting(CLASSNAME, METHODNAME, fileIsEmpty);

		return fileIsEmpty;
	}

	/**
	 * This function generates the fully qualified bookmark file name from the input parameters. Every IDoc extract job
	 * is going to have its own bookmark file.
	 * 
	 * @param jobName
	 *           - the name of the IDoc extract job that owns the bookmark file
	 * @param idocType
	 *           - the name of the IDoc type which is handled by the IDoc extract job
	 * @param idocExtractDir
	 *           - the directory where the bookmark file will be stored
	 */
	private void setBmkFileName(String jobName, String idocType, String idocExtractDir) {
		final String METHODNAME = "setBmkFileName(String jobName, String idocType, String idocExtractDir)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME, new Object[]{jobName, idocType, idocExtractDir});

		idocType = Utilities.idocType2FileName(idocType);

		
		StringBuffer fileName = new StringBuffer();

		fileName.append(idocExtractDir);
		fileName.append(FILE_SEPARATOR);
		fileName.append(idocType);
		fileName.append(FILENAME_UNDERSCORE);
		fileName.append(jobName);
		fileName.append(BMK_FILE_EXT);

		this.logger.log(Level.FINE, "Writing to bookmark file: {0}", fileName.toString()); //$NON-NLS-1$
		
		this.bmkFileName = fileName.toString();

		logger.exiting(CLASSNAME, METHODNAME);
	}

	/**
	 * This function creates in initial entry in the bookmark file. Both the CURRENT timestamp and the PREVIOUS timestamp
	 * field of the entry will be set to 0 (start of the epoch).
	 * 
	 * @param fileName
	 *           - the name of the bookmark file, fully qualified
	 * 
	 * @return <code>true</code> if the initial entry was created 
	 * successfully or an entry already exists, <code>false</code> if writing the initial entry failed
	 */
	private boolean createInitialBmkEntry(String fileName) {
		final String METHODNAME = "createInitialBmkEntry(String fileName)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		boolean entryCreated = true;

		if (bmkFileIsEmpty(fileName)) {
			StringBuffer bmkEntry = new StringBuffer();

			// CURRENT field gets "0" (equals start of the epoch)
			bmkEntry.append(BMK_INITIAL_TIMESTAMP);
			bmkEntry.append(BMK_ENTRY_DELIMITER);

			// PREVIOUS field gets "0" (equals start of the epoch)
			bmkEntry.append(BMK_INITIAL_TIMESTAMP);
			bmkEntry.append(BMK_ENTRY_DELIMITER);

			// UPDATE field always gets current time
			bmkEntry.append(Calendar.getInstance().getTimeInMillis());

			logger.log(Level.FINEST, "Initial bookmark file setup timestamp: {0}", bmkEntry.toString()); //$NON-NLS-1$
			entryCreated = writeToBmkFile(fileName, bmkEntry.toString());
		}

		logger.exiting(CLASSNAME, METHODNAME, entryCreated);

		return entryCreated;
	}

	/**
	 * This function retrieves the value of a specific timestamp field (CURRENT, PREVIOUS or UPDATE) from the bookmark
	 * file entry.
	 * 
	 * @param fileName
	 *           - the name of the bookmark file, fully qualified
	 * @param bmkTs
	 *           - the timestamp field to retrieve, must be a member of the internal <code>BmkTimestamp</code> enum type
	 * 
	 * @return a String representation of the retrieved timestamp field or <code>null</code> if the retrieval failed
	 */
	private String readTimestampFromBmkFile(String fileName, BmkTimestamp bmkTs) {
		final String METHODNAME = "readTimestampFromBmkFile(String fileName, BmkTimestamp bmkTs)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME, new Object[]{fileName, bmkTs});

		String timeStamp = null;

		String bmkEntry = readFromBmkFile(fileName);

		if (bmkEntry != null) {
			String[] entries = bmkEntry.split("\\" + BMK_ENTRY_DELIMITER);  //$NON-NLS-1$
			
			// must be exactly 3 entries (CURRENT, PREVIOUS and UPDATE)
			if (entries != null && entries.length == BMK_NUMBER_DELIMITED_ENTRIES) {
				switch (bmkTs) {
				case CURRENT:
					timeStamp = entries[0];
					break;
				case PREVIOUS:
					timeStamp = entries[1];
					break;
				case UPDATE:
					timeStamp = entries[2];
					break;
				default:
					break;
				}
			}
		}
		
		logger.exiting(CLASSNAME, METHODNAME, timeStamp);

		return timeStamp;
	}

	/**
	 * This function reads the bookmark entry from the bookmark file.
	 * 
	 * @param fileName
	 *           - the name of the bookmark file, fully qualified
	 * 
	 * @return the complete bookmark entry or <code>null</code> if the retrieval of the bookmark entry failed
	 */
	private String readFromBmkFile(String fileName) {
		final String METHODNAME = "readFromBmkFile(String fileName)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		String entryRead = null;

		if (fileExists(fileName)) {
			if (!bmkFileIsEmpty(fileName)) {
				FileInputStream fis = null;
				InputStreamReader isr = null;
				BufferedReader br = null;
				FileLock readLock = null;

				try {
					fis = new FileInputStream(fileName);
					isr = new InputStreamReader(fis, BMK_FILE_ENCODING);

					// try to acquire a lock for reading (shared lock) for the whole file
					FileChannel fc = fis.getChannel();
					readLock = fc.tryLock(0L, Long.MAX_VALUE, true);

					if (readLock != null && readLock.isValid()) {
						if (isr != null) {
							br = new BufferedReader(isr);
							String bmkLine = null;
							StringBuffer fileContents = new StringBuffer();
							int lineCount = 0;

							while ((bmkLine = br.readLine()) != null) {
								lineCount++;
								fileContents.append(bmkLine);
							}

							// make sure that the bookmark file contains exactly one line
							// and that this line is not empty
							if (lineCount == 1 && fileContents.length() != 0) {
								entryRead = fileContents.toString();
							}
						}
					}
					else {

						// we can't get a read (shared) lock on the bookmark file which means
						// that retrieving timestamps from this file is not possible at this point
						// that's why we just return the NULL String object
						// and handle it in the calling class
						logger.exiting(CLASSNAME, METHODNAME);

						return entryRead;
					}
				}
				catch (FileNotFoundException fnfe) {
					logger.log(Level.WARNING, "CC_IDOC_EXTRACT_BookmarkFileReadError", fileName); //$NON-NLS-1$
				}
				catch (UnsupportedEncodingException uee) {
					logger.log(Level.WARNING, "CC_IDOC_EXTRACT_BookmarkFileReadError", fileName); //$NON-NLS-1$
				}
				catch (IOException ioe) {
					logger.log(Level.WARNING, "CC_IDOC_EXTRACT_BookmarkFileReadError", fileName); //$NON-NLS-1$
				}
				finally {
					try {
						if (br != null) {
							br.close();
						}
					}
					catch (IOException ioe) {
						logger.log(Level.FINER, "Buffered reader for {0} could not be closed", new Object[] { fileName }); //$NON-NLS-1$
					}
					finally {
						try {
							if (isr != null) {
								isr.close();
							}
						}
						catch (IOException ioe) {
							logger.log(Level.FINER, "Input stream reader for {0} could not be closed", new Object[] { fileName }); //$NON-NLS-1$
						}
						finally {
							try {
								if (readLock != null && readLock.isValid()) {
									readLock.release();
								}
							}
							catch (ClosedChannelException cce) {
								logger.log(Level.FINER, "Read lock for {0} could not be released", new Object[] { fileName }); //$NON-NLS-1$
							}
							catch (IOException ioe) {
								logger.log(Level.FINER, "Read lock for {0} could not be released", new Object[] { fileName }); //$NON-NLS-1$
							}
							finally {
								try {
									if (fis != null) {
										fis.close();
									}
								}
								catch (IOException ioe) {
									logger.log(Level.FINER,
									      "File input stream for {0} could not be closed", new Object[] { fileName }); //$NON-NLS-1$
								}
							}
						}
					}
				}
			}
		}

		logger.exiting(CLASSNAME, METHODNAME, entryRead);

		return entryRead;
	}

	/**
	 * This function writes a bookmark entry to the bookmark file.
	 * 
	 * @param fileName
	 *           - the name of the bookmark file, fully qualified
	 * 
	 * @param fileEntry
	 *           - the bookmark entry to be written to the bookmark file
	 * 
	 * @return <code>true</code> if the bookmark entry was written successfully, <code>false</code> otherwise
	 */
	private boolean writeToBmkFile(String fileName, String fileEntry) {
		final String METHODNAME = "writeToBmkFile(String fileName, String fileEntry)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME, new Object[]{fileName, fileEntry});

		boolean entryWritten = false;
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		FileLock writeLock = null;

		try {

			// opening bookmark file for writing in OVERWRITE mode to make sure
			// that whatever the contents is it will be overwritten by the new bookmark entry
			fos = new FileOutputStream(fileName);
			osw = new OutputStreamWriter(fos, BMK_FILE_ENCODING);

			// try to acquire a lock for writing (exclusive lock) for the whole file
			FileChannel fc = fos.getChannel();
			writeLock = fc.tryLock();

			if (writeLock != null && writeLock.isValid()) {
				bw = new BufferedWriter(osw);
				bw.write(fileEntry);
				entryWritten = true;
			}else
				logger.log(Level.FINE, "Could not obtain file lock {0}", fileName); //$NON-NLS-1$
			
		}
		catch (FileNotFoundException fnfe) {
			logger.log(Level.SEVERE, "CC_IDOC_EXTRACT_BookmarkFileWriteError", fileName);  //$NON-NLS-1$
			logger.log(Level.SEVERE, "CC_IDOC_CommonUnexpectedException", fnfe);           //$NON-NLS-1$
		}
		catch (IOException ioe) {
			logger.log(Level.SEVERE, "CC_IDOC_EXTRACT_BookmarkFileWriteError", fileName);  //$NON-NLS-1$
			logger.log(Level.SEVERE, "CC_IDOC_CommonUnexpectedException", ioe);            //$NON-NLS-1$
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
				finally {
					try {
						if (writeLock != null && writeLock.isValid()) {
							writeLock.release();
						}
					}
					catch (ClosedChannelException cce) {
						logger.log(Level.FINER, "Write lock for {0} could not be released", new Object[] { fileName }); //$NON-NLS-1$
					}
					catch (IOException ioe) {
						logger.log(Level.FINER, "Write lock for {0} could not be released", new Object[] { fileName }); //$NON-NLS-1$
					}
					finally {
						try {
							if (fos != null) {
								fos.close();
							}
						}
						catch (IOException ioe) {
							logger.log(Level.FINER,
							      "File output stream for {0} could not be closed", new Object[] { fileName }); //$NON-NLS-1$
						}
					}
				}
			}
		}

		logger.exiting(CLASSNAME, METHODNAME, entryWritten);

		return entryWritten;
	}

	/**
	 * This function creates a file using the given filename.
	 * 
	 * @param fileName
	 *           - the name of the file that will be created

	 * @return <code>true</code> if the bookmark file can be used, <code>false</code> otherwise
	 */
	private boolean createFile(String fileName){
		final String METHODNAME = "createFile(String fileName)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		boolean fileAvailable = false;

		File file = new File(fileName);

		try {
			if (file != null) {
				boolean fileCreated = file.createNewFile();
				if (!fileCreated) {
					logger.log(Level.FINE, IDOC_BOOKMARK_FILEEXISTS_MSG, new Object[] { fileName });
				}
				fileAvailable = true;
			}

		} catch (SecurityException se) {
			logger.log(Level.SEVERE, "CC_IDOC_EXTRACT_BookmarkFileInitError", fileName); //$NON-NLS-1$
			logger.log(Level.SEVERE, "CC_IDOC_CommonUnexpectedException", se);           //$NON-NLS-1$
		}
		catch (IOException ioe) {
			logger.log(Level.SEVERE, "CC_IDOC_EXTRACT_BookmarkFileInitError", fileName); //$NON-NLS-1$
			logger.log(Level.SEVERE, "CC_IDOC_CommonUnexpectedException", ioe);          //$NON-NLS-1$
		}

		logger.exiting(CLASSNAME, METHODNAME, fileAvailable);
		return fileAvailable;
	}

	/**
	 * This function checks if a file exists on the file system.
	 * 
	 * @param fileName
	 *           - the name of the file, fully qualified
	 * 
	 * @return <code>true</code> if the file exists, <code>false</code> otherwise
	 */
	private boolean fileExists(String fileName) {
		final String METHODNAME = "fileExists(String fileName)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		boolean fileExists = false;

		File file = new File(fileName);

		if (file != null) {
			try {
				fileExists = file.exists();
			}
			catch (SecurityException se) {
				logger.log(Level.FINER, IDOC_BOOKMARK_SE_MSG, new Object[] { fileName });
			}
		}

		logger.exiting(CLASSNAME, METHODNAME, fileExists);

		return fileExists;
	}
}
