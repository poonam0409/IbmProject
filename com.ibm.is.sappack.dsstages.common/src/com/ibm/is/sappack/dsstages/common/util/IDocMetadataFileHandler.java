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
// Module Name : com.ibm.is.sappack.dsstages.common.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.ibm.is.sappack.dsstages.common.CCFResource;
import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.IDocField;
import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.common.RuntimeConfiguration;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.Utilities;
import com.ibm.is.sappack.dsstages.common.impl.ControlRecord;
import com.ibm.is.sappack.dsstages.common.impl.IDocFieldImpl;
import com.ibm.is.sappack.dsstages.common.impl.IDocSegmentImpl;
import com.ibm.is.sappack.dsstages.common.impl.IDocTypeImpl;

/**
 * IDocMetadataFileHandler class that wraps the reading and writing of IDoc metadata files.
 */
public class IDocMetadataFileHandler {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.util.Copyright.IBM_COPYRIGHT_SHORT;
	}

	// constants
	private static final String CLASSNAME = IDocMetadataFileHandler.class.getName();
	private static final String FILE_SEPARATOR = System.getProperty("file.separator"); //$NON-NLS-1$
	private static final String FILE_UNDERSCORE = "_"; //$NON-NLS-1$
	private static final String FILE_ENCODING = "UTF8"; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final char COLON = ':';
	private static final String NEWLINE = "\n"; //$NON-NLS-1$
	private static final String IDOC_BEGIN = "<IDOC_METADATA_FILE_IDOC_BEGIN>"; //$NON-NLS-1$
	private static final String IDOC_END = "<IDOC_METADATA_FILE_IDOC_END>"; //$NON-NLS-1$
	private static final String CR_BEGIN = "<IDOC_METADATA_FILE_CR_BEGIN>"; //$NON-NLS-1$
	private static final String CR_END = "<IDOC_METADATA_FILE_CR_END>"; //$NON-NLS-1$
	private static final String SEGMENT_BEGIN = "<IDOC_METADATA_FILE_SEGMENT_BEGIN>"; //$NON-NLS-1$
	private static final String SEGMENT_END = "<IDOC_METADATA_FILE_SEGMENT_END>"; //$NON-NLS-1$
	private static final String FIELD_BEGIN = "<IDOC_METADATA_FILE_FIELD_BEGIN>"; //$NON-NLS-1$
	private static final String FIELD_END = "<IDOC_METADATA_FILE_FIELD_END>"; //$NON-NLS-1$

	// members
	private Logger logger;
	private RuntimeConfiguration runtimeConfig;
	private String idocTypeName;
	private String fileName;
	private boolean compressed = true;

	/**
	 * Default constructor
	 */
	public IDocMetadataFileHandler() {
		logger = StageLogger.getLogger();
		runtimeConfig = RuntimeConfiguration.getRuntimeConfiguration();
	}
	
//	// Debug constructor
//    public IDocMetadataFileHandler(String debug) {
//        logger = StageLogger.getLogger();
//        runtimeConfig = RuntimeConfiguration.getRuntimeConfiguration();
//    }
    

	/**
	 * This function initializes the IDocMetadataFileHandler object by means of generating a filename for metadata
	 * storage.
	 * 
	 * @param sapSystemName
	 *           - the name of the SAP system the IDoc metadata is bound to
	 * @param idocTypeName
	 *           - the IDoc type name
	 * @param basicTypeName
	 *           - the name of the basic type for this IDoc type
	 * @param idocTypeRelease
	 *           - the IDoc release version
	 */
	public void initialize(String sapSystemName, String idocTypeName, String basicTypeName, String idocTypeRelease) {
		final String METHODNAME =
		      "initialize(String sapSystemName, String idocTypeName, String basicTypeName, String idocTypeRelease)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		this.idocTypeName = idocTypeName;

		fileName = getMetadataFileName(idocTypeName, basicTypeName, idocTypeRelease, sapSystemName);

		logger.exiting(CLASSNAME, METHODNAME);
	}

	/**
	 * This function returns the name of the IDoc metadata file that has been generated for this IDocMetadataFileHandler
	 * instance.
	 * 
	 * @return the name of the IDoc metadata file or an empty string if the name has not been generated (yet).
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * This function checks if the IDoc metadata file that has been allocated for this IDocMetadataFileHandler instance
	 * exists on the filesystem.
	 * 
	 * @return <code>true</code> if the file exists, <code>false</code> otherwise
	 */
	public boolean hasMetadataFile() {
		final String METHODNAME = "hasMetadataFile()"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		File metadataFile = new File(fileName);

		logger.exiting(CLASSNAME, METHODNAME);

		return metadataFile.exists();
	}

	/**
	 * This function reads the IDoc metadata from the IDoc metadata file. At first, the IDoc metadata file gets read into
	 * a byte buffer, afterwards this buffer is parsed for the metadata information.
	 * 
	 * @return a fully constructed IDocTypeImpl object
	 * @throws IOException
	 *            in case something went wrong while reading the IDoc metadata file
	 */
	public IDocTypeImpl readMetadataFromFile() throws IOException {
		final String METHODNAME = "readMetadataFromFile()"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		logger.log(Level.FINE, "Reading IDoc metadata file: {0}", new Object[] { fileName }); //$NON-NLS-1$

		IDocTypeImpl idocType = null;
		File metadataFile = new File(fileName);

		// if the file doesn't exist at this point we'll log a severe message
		// which will abort the current job
		if (!metadataFile.exists()) {
			logger.log(Level.SEVERE, "CC_IDOC_TypeMetadataFileNotFound", new Object[] { idocTypeName } ); //$NON-NLS-1$
			throw new FileNotFoundException(fileName);
		}

		String buffer;

		// uncompress the metadata file in GZIP format
		if (compressed) {
			FileInputStream fis = null;
			GZIPInputStream gzis = null;
			ByteArrayOutputStream baos = null;
			BufferedOutputStream bos = null;
			FileLock readLock = null;

			try {
				fis = new FileInputStream(fileName);
				gzis = new GZIPInputStream(fis);
				baos = new ByteArrayOutputStream();
				bos = new BufferedOutputStream(baos);

				// try to acquire a lock for reading (shared lock) for the whole file
				FileChannel fc = fis.getChannel();
				readLock = fc.tryLock(0L, Long.MAX_VALUE, true);

				if (readLock != null && readLock.isValid()) {
					int bytesRead;
					byte[] tmpBuf = new byte[4096];

					// we read the contents of the compressed file
					// into an output stream which later gives us the opportunity
					// to convert it into a String object
					while ((bytesRead = gzis.read(tmpBuf)) != -1) {
						bos.write(tmpBuf, 0, bytesRead);

						// making sure that the whole stream contents gets written
						// to the underlying ByteArrayOutputStream
						bos.flush();
					}

					// making sure that the whole stream contents gets written
					// to the underlying ByteArrayOutputStream
					bos.flush();
				}
				else {

					// we can't get a read (shared) lock on the metadata file which means
					// that retrieving metadata from this file is not possible at this point
					// that's why we just return the NULL IDocTypeImpl object
					// and handle it in the calling class
					logger.exiting(CLASSNAME, METHODNAME);

					return idocType;
				}

				buffer = baos.toString(FILE_ENCODING);
			}
			catch (IOException ioe) {
				throw new IOException(CCFResource.getCCFMessage("CC_IDOC_TypeMetadataFileReadError", fileName), ioe); //$NON-NLS-1$
			}
			finally {
				try {
					if (bos != null) {
						bos.close();
					}
				}
				catch (IOException ioe) {
					logger.log(Level.FINER, "Buffered output stream for {0} could not be closed", fileName); //$NON-NLS-1$
				}
				finally {
					try {
						if (baos != null) {
							baos.close();
						}
					}
					catch (IOException ioe) {
						logger.log(Level.FINER, "Byte array output stream for {0} could not be closed", fileName); //$NON-NLS-1$
					}
					finally {
						try {
							if (gzis != null) {
								gzis.close();
							}
						}
						catch (IOException ioe) {
							logger
							      .log(Level.FINER, "GZIP input stream for {0} could not be closed", new Object[] { fileName }); //$NON-NLS-1$
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
		else {
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			FileLock readLock = null;
			int bytesToRead = (int) new File(fileName).length();
			byte[] buf = new byte[bytesToRead];

			try {
				fis = new FileInputStream(fileName);
				bis = new BufferedInputStream(fis);

				// try to acquire a lock for reading (shared lock) for the whole file
				FileChannel fc = fis.getChannel();
				readLock = fc.tryLock(0L, Long.MAX_VALUE, true);

				if (readLock != null && readLock.isValid()) {
					int bytesRead = bis.read(buf);

					// we have to check if the number of bytes actually read from the stream
					// is identical to what we've expected
					if (bytesRead != bytesToRead) {
						throw new IOException(CCFResource.getCCFMessage("CC_IDOC_TypeMetadataFileReadError", fileName)); //$NON-NLS-1$
					}
				}
				else {

					// we can't get a read (shared) lock on the metadata file which means
					// that retrieving metadata from this file is not possible at this point
					// that's why we just return the NULL IDocTypeImpl object
					// and handle it in the calling class
					logger.exiting(CLASSNAME, METHODNAME);

					return idocType;
				}

				buffer = new String(buf);
			}
			catch (IOException ioe) {
				throw new IOException(CCFResource.getCCFMessage("CC_IDOC_TypeMetadataFileReadError", fileName), ioe); //$NON-NLS-1$
			}
			finally {
				try {
					if (bis != null) {
						bis.close();
					}
				}
				catch (IOException ioe) {
					logger.log(Level.FINER, "Buffered input stream for {0} could not be closed", fileName); //$NON-NLS-1$					
				}
				finally {
					try {
						if (readLock != null && readLock.isValid()) {
							readLock.release();
						}
					}
					catch (ClosedChannelException cce) {
						logger.log(Level.FINER, "Read lock for {0} could not be released", fileName); //$NON-NLS-1$
					}
					catch (IOException ioe) {
						logger.log(Level.FINER, "Read lock for {0} could not be released", fileName); //$NON-NLS-1$
					}
					finally {
						try {
							if (fis != null) {
								fis.close();
							}
						}
						catch (IOException ioe) {
							logger
							      .log(Level.FINER, "File input stream for {0} could not be closed", new Object[] { fileName }); //$NON-NLS-1$
						}
					}
				}
			}
		}

		idocType = parseMetadataFile(new StringBuffer(buffer));

		logger.exiting(CLASSNAME, METHODNAME);

		return idocType;
	}

    /**
     * This function converts the IDoc object into a string.
     * It is public because it is re-used in the design-time metadata caching component.
     * 
     * @return string representation of the IDoc type
     */
	public String serializeMetadata(IDocTypeImpl idocType) {
        final String METHODNAME = "writeMetadataToFile(IDocTypeImpl idocType)"; //$NON-NLS-1$
        logger.entering(CLASSNAME, METHODNAME);

        if (idocType == null) {
            logger.exiting(CLASSNAME, METHODNAME);
            return null;
        } else {
            final StringBuffer sb = new StringBuffer();

            // open idoc
            sb.append(IDOC_BEGIN).append(NEWLINE);

            // add idoc metadata
            sb.append(Constants.IDOC_METADATA_IDOC_FIELD_IDOCTYP).append(COLON).append(idocType.getIDocTypeName()).append(NEWLINE);
            sb.append(Constants.IDOC_METADATA_IDOC_FIELD_DESCRP).append(COLON).append(idocType.getIDocTypeDescription()).append(NEWLINE);
            sb.append(Constants.IDOC_METADATA_IDOC_FIELD_RELEASED).append(COLON).append(idocType.getRelease()).append(NEWLINE);

            if (idocType.getBasicTypeName() != null) {
                sb.append(Constants.IDOC_METADATA_IDOC_FIELD_CIMTYP).append(COLON).append(idocType.getBasicTypeName()).append(NEWLINE);
            } else {
                sb.append(Constants.IDOC_METADATA_IDOC_FIELD_CIMTYP).append(COLON).append(EMPTY).append(NEWLINE);
            }

            // get the metadata for the control record segment
            ControlRecord cr = idocType.getControlRecord();

            if (cr != null) {

                // open control record
                sb.append(CR_BEGIN).append(NEWLINE);

                // add control record metadata
                sb.append(Constants.IDOC_METADATA_SEGM_FIELD_NUMFIELDS).append(COLON).append(cr.getFields().size()).append(NEWLINE);

                Iterator<IDocField> crFields = cr.getFields().iterator();

                while (crFields.hasNext()) {
                    IDocField crField = crFields.next();

                    // open field
                    sb.append(FIELD_BEGIN).append(NEWLINE);

                    // add field metadata
                    sb.append(Constants.IDOC_METADATA_FLDS_FIELD_FIELDNAME).append(COLON).append(crField.getFieldName()).append(NEWLINE);
                    sb.append(Constants.IDOC_METADATA_FLDS_FIELD_DESCRP).append(COLON).append(crField.getFieldDescription()).append(NEWLINE);
                    sb.append(Constants.IDOC_METADATA_FLDS_FIELD_INTLEN).append(COLON).append(crField.getLength()).append(NEWLINE);
                    sb.append(Constants.IDOC_METADATA_FLDS_FIELD_EXTLEN).append(COLON).append(crField.getLengthAsString()).append(NEWLINE);
                    sb.append(Constants.IDOC_METADATA_FLDS_FIELD_DATATYPE).append(COLON).append(crField.getSAPType()).append(NEWLINE);

                    // close field
                    sb.append(FIELD_END).append(NEWLINE);
                }

                // close control record
                sb.append(CR_END).append(NEWLINE);
            }

            // go through all available segments
            IDocSegmentTraversal.traverseIDoc(idocType, new IDocSegmentTraversal.Visitor() {

                public void visit(IDocSegment segment) {

                    // open segment
                    sb.append(SEGMENT_BEGIN).append(NEWLINE);

                    // add segment metadata
                    sb.append(Constants.IDOC_METADATA_SEGM_FIELD_SEGMENTDEF).append(COLON).append(segment.getSegmentDefinitionName()).append(NEWLINE);
                    sb.append(Constants.IDOC_METADATA_SEGM_FIELD_SEGMENTTYP).append(COLON).append(segment.getSegmentTypeName()).append(NEWLINE);
                    sb.append(Constants.IDOC_METADATA_SEGM_FIELD_DESCRP).append(COLON).append(segment.getSegmentDescription()).append(NEWLINE);
                    sb.append(Constants.IDOC_METADATA_SEGM_FIELD_NR).append(COLON).append(segment.getSegmentNr()).append(NEWLINE);

                    if (segment.getParent() != null) {
                        sb.append(Constants.IDOC_METADATA_SEGM_FIELD_PARSEG).append(COLON).append(segment.getParent().getSegmentTypeName()).append(
                                NEWLINE);
                    } else {
                        sb.append(Constants.IDOC_METADATA_SEGM_FIELD_PARSEG).append(COLON).append(EMPTY).append(NEWLINE);
                    }

                    sb.append(Constants.IDOC_METADATA_SEGM_FIELD_PARFLG).append(COLON).append(segment.isParentFlag()).append(NEWLINE);
                    sb.append(Constants.IDOC_METADATA_SEGM_FIELD_MUSTFL).append(COLON).append(segment.isMandatory()).append(NEWLINE);
                    sb.append(Constants.IDOC_METADATA_SEGM_FIELD_OCCMIN).append(COLON).append(segment.getMinOccurrence()).append(NEWLINE);
                    sb.append(Constants.IDOC_METADATA_SEGM_FIELD_OCCMAX).append(COLON).append(segment.getMaxOccurrence()).append(NEWLINE);
                    sb.append(Constants.IDOC_METADATA_SEGM_FIELD_HLEVEL).append(COLON).append(segment.getHierarchyLevel()).append(NEWLINE);
                    sb.append(Constants.IDOC_METADATA_SEGM_FIELD_NUMFIELDS).append(COLON).append(segment.getFields().size()).append(NEWLINE);

                    Iterator<IDocField> it = segment.getFields().iterator();

                    while (it.hasNext()) {
                        IDocField field = it.next();

                        // open field
                        sb.append(FIELD_BEGIN).append(NEWLINE);

                        // add field metadata
                        sb.append(Constants.IDOC_METADATA_FLDS_FIELD_FIELDNAME).append(COLON).append(field.getFieldName()).append(NEWLINE);
                        sb.append(Constants.IDOC_METADATA_FLDS_FIELD_DESCRP).append(COLON).append(field.getFieldDescription()).append(NEWLINE);
                        sb.append(Constants.IDOC_METADATA_FLDS_FIELD_INTLEN).append(COLON).append(field.getLength()).append(NEWLINE);
                        sb.append(Constants.IDOC_METADATA_FLDS_FIELD_EXTLEN).append(COLON).append(field.getLengthAsString()).append(NEWLINE);
                        sb.append(Constants.IDOC_METADATA_FLDS_FIELD_DATATYPE).append(COLON).append(field.getSAPType()).append(NEWLINE);

                        // close field
                        sb.append(FIELD_END).append(NEWLINE);
                    }

                    // close segment
                    sb.append(SEGMENT_END).append(NEWLINE);
                }
            });

            // close idoc
            sb.append(IDOC_END).append(NEWLINE);
            logger.exiting(CLASSNAME, METHODNAME);

            return sb.toString();
        }
    }
	
	/**
	 * This function writes IDoc metadata information to an IDoc metadata file in concise, human readable form. However,
	 * per default the metadata file itself gets saved in compressed (GZIP) format in order to preserve disk space.
	 * 
	 * @param idocType
	 *           - the IDoc type for which the metadata information shall be persisted to file
	 * @return <code>true</code> if writing to the file has been successful, <code>false</code> otherwise
	 */
	public boolean writeMetadataToFile(IDocTypeImpl idocType) {
		final String METHODNAME = "writeMetadataToFile(IDocTypeImpl idocType)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		boolean written = false;

		if (idocType != null) {
		    final String idocString = serializeMetadata(idocType);

			// do the actual file writing
			if (fileName != null) {
				
				// if the parent directory does not exist try to create it
				// before writing any actual file content
				boolean parentDirCreated = false;
				File file = new File(fileName);
				String dirName = file.getParent();
				
				if (dirName != null) {
					File dir = new File(dirName);
					
					// check if the parent directory exists
					if (!dir.exists()) {
						parentDirCreated = dir.mkdir();
					}
					else {
						
						// if the parent directory already exists there's nothing we need to do
						// but to set the boolean variable parentDirCreated to true
						parentDirCreated = true;
					}
				}
				
				if (!parentDirCreated) {
					
					// issue a warning if the parent directory could not be created and exit
					logger.log(Level.WARNING, "CC_IDOC_TypeMetadataDirectoryError", new Object[] { fileName });  //$NON-NLS-1$
					
					logger.exiting(CLASSNAME, METHODNAME);

					return written;
				}

				// compress the metadata file in GZIP format to save space
				if (compressed) {
					FileOutputStream fos = null;
					GZIPOutputStream gzos = null;
					FileLock writeLock = null;

					try {
						fos = new FileOutputStream(fileName);
						gzos = new GZIPOutputStream(fos);

						// try to acquire a lock for writing (exclusive lock) for the whole file
						FileChannel fc = fos.getChannel();
						writeLock = fc.tryLock();

						if (writeLock != null && writeLock.isValid()) {
							if (gzos != null) {
								byte[] buffer = idocString.getBytes(FILE_ENCODING);
								gzos.write(buffer, 0, buffer.length);
								written = true;
							}
						}
					}
					catch (FileNotFoundException fnfe) {
						logger.log(Level.WARNING, "CC_IDOC_TypeMetadataFileWriteError", new Object[] { fileName });  //$NON-NLS-1$
					}
					catch (IOException ioe) {
						logger.log(Level.WARNING, "CC_IDOC_TypeMetadataFileWriteError", new Object[] { fileName });  //$NON-NLS-1$
					}
					finally {
						try {
							if (gzos != null) {
								gzos.close();
							}
						}
						catch (IOException ioe) {
							logger.log(Level.FINER,
							      "GZIP output stream for {0} could not be closed", new Object[] { fileName }); //$NON-NLS-1$
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
				else {
					FileOutputStream fos = null;
					OutputStreamWriter osw = null;
					BufferedWriter bw = null;
					FileLock writeLock = null;

					try {
						fos = new FileOutputStream(fileName);
						osw = new OutputStreamWriter(fos, FILE_ENCODING);

						// try to acquire a lock for writing (exclusive lock) for the whole file
						FileChannel fc = fos.getChannel();
						writeLock = fc.tryLock();

						if (writeLock != null && writeLock.isValid()) {
							if (osw != null) {
								bw = new BufferedWriter(osw);

								if (bw != null) {
									bw.append(idocString);
									written = true;
								}
							}
						}
					}
					catch (FileNotFoundException fnfe) {
						logger.log(Level.WARNING, "CC_IDOC_TypeMetadataFileWriteError", new Object[] { fileName });  //$NON-NLS-1$
					}
					catch (IOException ioe) {
						logger.log(Level.WARNING, "CC_IDOC_TypeMetadataFileWriteError", new Object[] { fileName });  //$NON-NLS-1$
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
								logger.log(Level.FINER,
								      "Output stream writer for {0} could not be closed", new Object[] { fileName }); //$NON-NLS-1$
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
				}
			}
		}

		logger.exiting(CLASSNAME, METHODNAME);

		return written;
	}

	/**
	 * This function generates a filename for the IDoc metadata file storage. The file will be stored in the
	 * host-specific SAP connection folder so that metadata files for the same IDoc type name can coexist.
	 * 
	 * @param idocTypeName
	 *           - the IDoc type name
	 * @param basicTypeName
	 *           - the name of the basic type for this IDoc type
	 * @param idocTypeRelease
	 *           - the IDoc release version
	 * @param sapSystemName
	 *           - the name of the SAP system the IDoc metadata is bound to
	 * 
	 * @return the generated filename or <code>null</code> if no filename could be generated
	 */
	private String getMetadataFileName(String idocTypeName, String basicTypeName, String idocTypeRelease,
	      String sapSystemName) {
		final String METHODNAME =
		      "getMetadataFileName(String idocTypeName, String basicTypeName, String idocTypeRelease, String sapSystemName)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		// check if basicTypeName is NULL and if so, change it to empty string
		if (basicTypeName == null) {
			basicTypeName = EMPTY;
		}

		String filename = null;
		StringBuffer sb = new StringBuffer();

		idocTypeName = Utilities.idocType2FileName(idocTypeName);
		basicTypeName = Utilities.idocType2FileName(basicTypeName);
		
		sb.append(runtimeConfig.getDSSAPHOME());
		sb.append(FILE_SEPARATOR + Constants.CONFIG_FOLDER_DSSAPCONNECTIONS);
		sb.append(FILE_SEPARATOR + sapSystemName);
		sb.append(FILE_SEPARATOR + Constants.CONFIG_FOLDER_IDOC_TYPES);
		sb.append(FILE_SEPARATOR + idocTypeName);
		sb.append(FILE_SEPARATOR + idocTypeName + FILE_UNDERSCORE + basicTypeName + FILE_UNDERSCORE + idocTypeRelease
		      + Constants.IDOC_METADATA_FILE_ENDING);
		filename = sb.toString();

		logger.exiting(CLASSNAME, METHODNAME);

		return filename;
	}

	/**
	 * This function parses the IDoc metadata and constructs an IDocTypeImpl object from it.
	 * 
	 * @param metadataFile
	 *           - the IDoc metadata to be parsed
	 * 
	 * @return a fully constructed IDocTypeImpl object
	 */
	public IDocTypeImpl parseMetadataFile(StringBuffer metadataFile) {
		final String METHODNAME = "parseMetadataFile(StringBuffer metadataFile)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		IDocTypeImpl idocType = new IDocTypeImpl();
		ControlRecord cr = null;
		HashMap<String, IDocSegmentImpl> typeSegmentMap = new HashMap<String, IDocSegmentImpl>();

		// idoc start marker
		String idocStart = fetchNextLine(metadataFile);

		try {
            // the first line of the file should be the idoc start marker
            if (idocStart.equalsIgnoreCase(IDOC_BEGIN)) {

                // retrieve the idoc properties
                idocType.setIDocTypeName(fetchNextLine(metadataFile)); // IDOCTYP
                idocType.setIDocTypeDescription(fetchNextLine(metadataFile)); // DESCRP
                idocType.setRelease(fetchNextLine(metadataFile)); // RELEASED
                idocType.setBasicTypeName(fetchNextLine(metadataFile)); // CIMTYP

                // retrieve the control record properties
                // control record start marker
                String crStart = fetchNextLine(metadataFile);

                if (crStart.equalsIgnoreCase(CR_BEGIN)) {
                    cr = new ControlRecord(idocType);
                    int numFields = Integer.parseInt(fetchNextLine(metadataFile)); // NUMFIELDS

                    logger.log(Level.FINEST, "Number of control record fields to process: {0}", new Object[] { numFields }); //$NON-NLS-1$

                    // go through all fields of the segment
                    for (int fieldNr = 0; fieldNr < numFields; fieldNr++) {

                        // field start marker
                        String fieldStart = fetchNextLine(metadataFile);

                        // the first line should be the field start marker
                        if (fieldStart.equalsIgnoreCase(FIELD_BEGIN)) {
                            IDocFieldImpl field = new IDocFieldImpl(cr);
                            field.setFieldName(fetchNextLine(metadataFile)); // FIELDNAME
                            field.setFieldDescription(fetchNextLine(metadataFile)); // DESCRP

                            String intLength = fetchNextLine(metadataFile); // INTLEN
                            field.setLength(Integer.parseInt(intLength));

                            String extLength = fetchNextLine(metadataFile); // EXTLEN
                            field.setLengthAsString(Integer.parseInt(extLength));

                            field.setSAPType(fetchNextLine(metadataFile)); // DATATYPE

                            // field end marker
                            String fieldEnd = fetchNextLine(metadataFile);

                            // the last line should be a field end marker
                            if (!fieldEnd.equalsIgnoreCase(FIELD_END)) {
                                logger.log(Level.FINE, "IDoc metadata file format error in control record field {0}", new Object[] { field //$NON-NLS-1$
                                        .getFieldName() });
                                throw new IDocMetadataFormatException(new Object[] { fileName });
                            }

                            // add the field to the segment
                            cr.getFields().add(field);
                        } else {
                            logger.log(Level.FINE, "IDoc metadata file format error in control record field section"); //$NON-NLS-1$
                            throw new IDocMetadataFormatException(new Object[] { fileName });
                        }
                    }

                    // control record end marker
                    String crEnd = fetchNextLine(metadataFile);

                    // the last line should be a control record end marker
                    if (!crEnd.equalsIgnoreCase(CR_END)) {
                        logger.log(Level.FINE, "IDoc metadata file format error in control record section"); //$NON-NLS-1$
                        throw new IDocMetadataFormatException(new Object[] { fileName });
                    }
                } else {
                    logger.log(Level.FINE, "IDoc metadata file format error in control record section"); //$NON-NLS-1$
                    throw new IDocMetadataFormatException(new Object[] { fileName });
                }

                // attach the control record to the idocType object
                idocType.setControlRecord(cr);

                // now grab the rest of the idoc metadata file
                // which should contain one or more segments
                while (metadataFile.length() > 0) {
                    IDocSegmentImpl segment;

                    // segment start marker
                    String segmentStart = fetchNextLine(metadataFile);

                    // check for end of idoc which is the end of the idoc
                    // metadata file as well
                    if (segmentStart.equalsIgnoreCase(IDOC_END)) {
                        break;
                    } else if (segmentStart.equalsIgnoreCase(SEGMENT_BEGIN)) {
                        segment = new IDocSegmentImpl(idocType);
                        segment.setSegmentDefinitionName(fetchNextLine(metadataFile)); // SEGMENTDEF
                        segment.setSegmentTypeName(fetchNextLine(metadataFile)); // SEGMENTTYP
                        logger.log(Level.FINE, "DEBUG: Processing segment: " + segment.getSegmentTypeName()); //$NON-NLS-1$
                        segment.setSegmentDescription(fetchNextLine(metadataFile)); // DESCRP

                        String segmentNr = fetchNextLine(metadataFile); // NR
                        try {
                            segment.setSegmentNr(Long.valueOf(segmentNr));
                        } catch (NumberFormatException e) {
                            // This can happen if an old IMF file is used that
                            // didn't contain segment descriptions.
                            // In this case, the parentType value will be parsed
                            // instead of the segmentNr one.
                            // This is fine since we'll just get the metadata
                            // from SAP in this case.
                            throw new IDocMetadataFormatException(new Object[] { fileName });
                        }

                        String parentType = fetchNextLine(metadataFile); // PARSEG
                        String parentFlag = fetchNextLine(metadataFile); // PARFLG
                        segment.setParentFlag(false);

                        if (parentFlag.equals(Boolean.toString(true))) {
                            segment.setParentFlag(true);
                        }

                        String mandatoryFlag = fetchNextLine(metadataFile); // MUSTFL
                        segment.setMandatory(false);

                        if (mandatoryFlag.equals(Boolean.toString(true))) {
                            segment.setMandatory(true);
                        }

                        String minOccurrence = fetchNextLine(metadataFile); // OCCMIN
                        segment.setMinOccurrence(Long.parseLong(minOccurrence));

                        String maxOccurrence = fetchNextLine(metadataFile); // OCCMAX
                        segment.setMaxOccurrence(Long.parseLong(maxOccurrence));

                        String hierarchyLevel = fetchNextLine(metadataFile); // HLEVEL
                        segment.setHierarchyLevel(Long.parseLong(hierarchyLevel));

                        int numFields = Integer.parseInt(fetchNextLine(metadataFile)); // NUMFIELDS

                        logger.log(Level.FINEST, "Number of segment fields to process: {0}", new Object[] { numFields }); //$NON-NLS-1$

                        // go through all fields of the segment
                        for (int fieldNr = 0; fieldNr < numFields; fieldNr++) {

                            // field start marker
                            String fieldStart = fetchNextLine(metadataFile);

                            // the first line should be the field start marker
                            if (fieldStart.equalsIgnoreCase(FIELD_BEGIN)) {
                                IDocFieldImpl field = new IDocFieldImpl(segment);
                                field.setFieldName(fetchNextLine(metadataFile)); // FIELDNAME
                                logger.log(Level.FINER, "DEBUG: Processing field: " + field.getFieldName()); //$NON-NLS-1$
                                field.setFieldDescription(fetchNextLine(metadataFile)); // DESCRP

                                String intLength = fetchNextLine(metadataFile); // INTLEN
                                field.setLength(Integer.parseInt(intLength));

                                String extLength = fetchNextLine(metadataFile); // EXTLEN
                                field.setLengthAsString(Integer.parseInt(extLength));

                                field.setSAPType(fetchNextLine(metadataFile)); // DATATYPE

                                // field end marker
                                String fieldEnd = fetchNextLine(metadataFile);

                                // the last line should be a field end marker
                                if (!fieldEnd.equalsIgnoreCase(FIELD_END)) {
                                    logger.log(Level.FINE, "IDoc metadata file format error in field {0}", new Object[] { field //$NON-NLS-1$
                                            .getFieldName() });
                                    throw new IDocMetadataFormatException(new Object[] { fileName });
                                }

                                // add the field to the segment
                                segment.getFields().add(field);
                            } else {
                                logger.log(Level.FINE, "IDoc metadata file format error in field section"); //$NON-NLS-1$
                                throw new IDocMetadataFormatException(new Object[] { fileName });
                            }
                        }

                        // segment end marker
                        String segmentEnd = fetchNextLine(metadataFile);

                        // the last line should be a segment end marker
                        if (!segmentEnd.equalsIgnoreCase(SEGMENT_END)) {
                            logger.log(Level.FINE, "IDoc metadata file format error in segment {0}", new Object[] { segment //$NON-NLS-1$
                                    .getSegmentTypeName() });
                            throw new IDocMetadataFormatException(new Object[] { fileName });
                        }

                        typeSegmentMap.put(segment.getSegmentTypeName(), segment);

                        // set parent-child relationship
                        if (!parentType.equals(EMPTY)) {
                            IDocSegmentImpl parentSegment = typeSegmentMap.get(parentType);
                            parentSegment.getChildSegments().add(segment);
                            segment.setParent(parentSegment);
                        } else {
                            idocType.getRootSegments().add(segment);
                        }
                    } else {
                        logger.log(Level.FINE, "IDoc metadata file format error in segment section"); //$NON-NLS-1$
                        throw new IDocMetadataFormatException(new Object[] { fileName });
                    }
                }
            }
        } catch (IDocMetadataFormatException x) {
            logger.log(Level.WARNING, "CC_IDOC_TypeMetadataFileFormatError", x.getObjects());  //$NON-NLS-1$
            logger.exiting(CLASSNAME, METHODNAME);
            return null;
        }

		logger.exiting(CLASSNAME, METHODNAME);
		return idocType;
	}

	/**
	 * This function reads the next line from the IDoc metadata buffer and deletes it upon successful retrieval.
	 * 
	 * @param fileContents
	 *           - the current IDoc metadata buffer
	 * 
	 * @return the line read from the IDoc metadata buffer
	 */
	private String fetchNextLine(StringBuffer fileContents) {
		final String METHODNAME = "fetchNextLine(StringBuffer fileContents)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		String result;
		String line;

		int newlineIndex = fileContents.indexOf(NEWLINE);

		// handle the line ending of the last line of the idoc metadata file
		if (newlineIndex == -1) {
			line = fileContents.substring(0);
			fileContents.delete(0, fileContents.length());
		}
		else {
			line = fileContents.substring(0, newlineIndex);
			fileContents.delete(0, newlineIndex + NEWLINE.length());
		}

		int colonIndex = line.indexOf(COLON);

		// check if the line contains a COLON separator character
		if (colonIndex == -1) {
			result = line;
		}
		else {
			result = line.substring(colonIndex + 1, line.length());
		}

		logger.exiting(CLASSNAME, METHODNAME);

		return result;
	}
}
