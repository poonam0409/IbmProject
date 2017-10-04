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
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener.handler
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.Utilities;
import com.ibm.is.sappack.dsstages.idoc.listener.IDocTypeConfiguration;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerConstants;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerMessages;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoTable;

/**
 * InMemoryIDocFileFactory
 * 
 * Factory to create InMemoryIDocFiles
 */
public class InMemoryIDocFileFactory implements IDocFileFactory {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.listener.handler.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/* logger */
	private Logger logger = null;
	
	/* synchronized Map of IDocTypeConfigurations */
	private Map<String, IDocTypeConfiguration> idocTypeConfigurations = null;
	
	/**
	 * InMemoryIDocFileFactory
	 * 
	 * @param idocTypeConfigurations
	 */
	public InMemoryIDocFileFactory(Map<String, IDocTypeConfiguration> idocTypeConfigurations) {
		/* initialize logger */
		this.logger = StageLogger.getLogger();
		
		this.idocTypeConfigurations = idocTypeConfigurations;
	}
	
	/**
	 * getIDocType
	 * 
	 * determine the IDoc type based on the given Map
	 * that contains the control record key value pairs.
	 * 
	 * If the IDoc type is an IDoc extension, return the
	 * name of the IDoc extension type. Otherwise return
	 * the name of the IDoc base type
	 * 
	 * @param controlRecordKeyValueMap
	 * @return
	 */
	private String getIDocType(Map<String, char []> controlRecordKeyValueMap) {
		
		/* check if IDoc is extension IDoc */
		String extensionType = String.valueOf(controlRecordKeyValueMap.get(IDocServerConstants.CIMTYP)).trim();
		if(extensionType.equals(IDocServerConstants.EMPTYSTRING)) {
			/* return the base type */
			String baseType = String.valueOf(controlRecordKeyValueMap.get(IDocServerConstants.IDOCTYP)).trim();
			return baseType;
		} else {
			/* return extension type */
			return extensionType;
		}
	}
	
	
	
	/**
	 * createIDocFiles
	 * 
	 * extract IDoc data from the given controlRecordTable and
	 * segmentDataTable and create an IDocFile for each IDoc
	 * 
	 * @param controlRecordTable
	 * @param segmentDataTable
	 * @return
	 */
	@Override
	public List<IDocFile> createIDocFiles(String connectionName, JCoTable controlRecordTable, JCoTable segmentDataTable) {
		
		List<IDocFile> idocFiles = new ArrayList<IDocFile>();
		
		/* log message about incoming IDocs */
		logger.log(Level.INFO, IDocServerMessages.ProcessingIncomingIDocs, controlRecordTable.getNumRows());

		/* get a Map with all control record rows and IDoc number as key */
		Map<String, Map<String, char[]>> controlRecordMap = this.getControlRecordMap(controlRecordTable);
		
		/* get a Map with all segment data records and IDoc number as key */
		Map<String, List<StringBuffer>> segmentDataMap = this.getSegmentDataMap(segmentDataTable);
		
		/* iterate over IDoc numbers and process each IDoc separately */
		Set<Entry<String, Map<String, char[]>>> controlRecordSet = controlRecordMap.entrySet();
		Iterator<Entry<String, Map<String, char[]>>> controlRecordSetEntries = controlRecordSet.iterator();
		
		while (controlRecordSetEntries.hasNext()) {
			Entry<String, Map<String, char[]>> entry = controlRecordSetEntries.next();
			String idocNumber = entry.getKey();
			/* get control record field value pairs for this IDoc */
			Map<String, char[]> crFieldValueMap = entry.getValue();
			/* get segment data table record for this IDoc number */
			List<StringBuffer> segmentDataBuffers = segmentDataMap.get(idocNumber);
			
			/* determine the IDocType */
			String idocType = this.getIDocType(crFieldValueMap);
			
			IDocTypeConfiguration idocTypeConfig = this.idocTypeConfigurations.get(idocType);
			/* only handle registered IDoc types */
			if(idocTypeConfig != null) {
				/* get root directory for this IDocType */
				String directory = idocTypeConfig.getIDocTypDirectory();
				/* create IDocFile representing the content of one single IDoc */
				IDocFile idocFile = this.createSingleIDocFile(directory, idocType, connectionName, crFieldValueMap, segmentDataBuffers);
				idocFiles.add(idocFile);
			} else {
				/* log a warning that we received an unregistered IDoc type */
				this.logger.log(Level.WARNING, IDocServerMessages.IDocTypeNotRegistered, new Object[]{idocType, connectionName});
			}
		}
		
		return idocFiles;
	}
	
	/**
	 * createSingleIDocFile
	 * 
	 * create a single IDocFile with the following file name pattern
	 * 
	 * $IDocType_$ConnectionName_$IDocNumber.txt
	 * 
	 * The created IDoc files are encoded in UTF-16 with platform endianness
	 * 
	 * @param idocTypesDir
	 * @param idocType
	 * @param connectionName
	 * @param crFieldValueMap
	 * @param segmentDataBuffers
	 * @return
	 */
	private IDocFile createSingleIDocFile(String idocTypesDir, String idocType, String connectionName, Map<String, char[]> crFieldValueMap, List<StringBuffer> segmentDataBuffers) {
		
		/* create IDoc file content buffer */
		StringBuffer content = new StringBuffer();
		/* append IDoc type */
		String idocTypeName = String.valueOf(crFieldValueMap.get(IDocServerConstants.IDOCTYP)).trim();
		content.append(idocTypeName).append(IDocServerConstants.NEWLINE);	
		/* check if extended type */
		String extendedType = String.valueOf(crFieldValueMap.get(IDocServerConstants.CIMTYP)).trim();
		if(extendedType.equals(IDocServerConstants.EMPTYSTRING)) {
			content.append("NOT_APPLICABLE").append(IDocServerConstants.NEWLINE);  //$NON-NLS-1$
		} else {
			content.append(extendedType).append(IDocServerConstants.NEWLINE);
		}
		/* append release version */
		String release = String.valueOf(crFieldValueMap.get(IDocServerConstants.DOCREL)).trim();
		content.append(release).append(IDocServerConstants.NEWLINE);
		/* append the entire crFieldValueMap as one row */
		Set<Entry<String, char[]>> crFieldValueSet = crFieldValueMap.entrySet();
		Iterator<Entry<String, char[]>> crFieldValueSetEntries = crFieldValueSet.iterator();
		
		while (crFieldValueSetEntries.hasNext()) {
			Entry<String, char[]> entry = crFieldValueSetEntries.next();
			content.append(entry.getValue());
		}
		
		content.append(IDocServerConstants.NEWLINE);
		/* append all IDoc segment data records */
		Iterator<StringBuffer> segmentDataBufferIterator = segmentDataBuffers.iterator();
		while(segmentDataBufferIterator.hasNext()) {
			content.append(segmentDataBufferIterator.next()).append(IDocServerConstants.NEWLINE);
		}
		
		/* assemble IDoc file path - idocTypeDir\$IDocType_$ConnectionName_$IDocNumber.txt */
		StringBuffer filePath = new StringBuffer();
		filePath.append(idocTypesDir);
		filePath.append( Utilities.idocType2FileName(idocType.toUpperCase())).append(IDocServerConstants.UNDERSCORE);
		filePath.append(connectionName.toUpperCase()).append(IDocServerConstants.UNDERSCORE);
		filePath.append(crFieldValueMap.get(IDocServerConstants.DOCNUM)).append(".txt");
		
		/* get the IDoc number */
		char[] idocNumber = crFieldValueMap.get(IDocServerConstants.DOCNUM);
		
		/* get sender partner number */
		String senderPartnerNumber = String.valueOf(crFieldValueMap.get(IDocServerConstants.SNDPRN));
		/* get receiver partner number */
		String receiverPartnerNumber = String.valueOf(crFieldValueMap.get(IDocServerConstants.RCVPRN));
		
		/* InMemoryIDocFile that holds the content of the IDoc */
		return new InMemoryIDocFile(idocNumber, idocType, senderPartnerNumber, receiverPartnerNumber, content, filePath.toString());

	}
	
	
	
	/**
	 * getSegmentDataMap
	 * 
	 * create a Map with IDoc number as key and a List of StringBuffers containing
	 * the segment data records of this IDoc as value
	 * 
	 * @param segmentDataTable
	 * @return
	 */
	private Map<String, List<StringBuffer>> getSegmentDataMap(JCoTable segmentDataTable) {
		
		/* IDoc number */
		String docNum = "";
		
		/* LinkedHashMap that stores the segments in original order */
		Map<String, List<StringBuffer>> segmentDataMap = new LinkedHashMap<String, List<StringBuffer>>();
		
		/* iterate over segment data table process each record */
		for(int i=0; i<segmentDataTable.getNumRows(); i++) {
			segmentDataTable.setRow(i);
			/* buffer for this row */
			StringBuffer rowBuffer = new StringBuffer();
			JCoFieldIterator fieldIterator = segmentDataTable.getFieldIterator();
			while (fieldIterator.hasNextField()) {
				
				JCoField field = fieldIterator.nextField();
				char [] rawValue = field.getCharArray();
				/* get the IDoc number */
				if(field.getName().equals(IDocServerConstants.DOCNUM)) {
					String fieldValue = field.getValue().toString();
					docNum = fieldValue;
				}
				/* append fieldValue to row buffer */
				rowBuffer.append(rawValue);
			}
			
			/* check if we already have a List of StringBuffers for this IDocNumber in segmentDataMap */
			List<StringBuffer> segmentDataBuffers = segmentDataMap.get(docNum);
			if(segmentDataBuffers != null) {
				/* add a new entry in segmentDataBuffers for this IDoc number */
				segmentDataBuffers.add(rowBuffer);
			} else {
				/* create a new segment data List of StringBuffers for this IDoc number */
				segmentDataBuffers = new ArrayList<StringBuffer>();
				segmentDataBuffers.add(rowBuffer);
				segmentDataMap.put(docNum, segmentDataBuffers);
			}
			
			/* reset IDoc number */
			docNum = "";
		}
		
		return segmentDataMap;
	}
	
	
	/**
	 * getControlRecordMap
	 * 
	 * iterate over the control record table and store
	 * the fields of each row in a map with the field name as key.
	 * After that store each row in an additional map with the IDoc number as key
	 * 
	 * @param controlRecordTable
	 * @return
	 */
	private Map<String, Map<String, char[]>> getControlRecordMap(JCoTable controlRecordTable) {
		
		/* Nested map to store IDoc number - IDoc control record data pairs */
		Map<String, Map<String, char[]>> controlRecordMap = new LinkedHashMap<String, Map<String, char[]>>();
		String docNum = "";
		
		/* iterate over control record table to determine number of IDocs received */
		for(int i=0; i<controlRecordTable.getNumRows(); i++) {
			/* LinkedHashMap to store field name - field value pairs in original order */
			Map<String, char []> rowMap = new LinkedHashMap<String, char[]>(); 
			controlRecordTable.setRow(i);
			JCoFieldIterator fieldIterator = controlRecordTable.getFieldIterator();
			
			while (fieldIterator.hasNextField()) {
				/* get the IDoc number */
				JCoField field = fieldIterator.nextField();
				String fieldName = field.getName();
				char [] rawValue = field.getCharArray();
				if(fieldName.equals(IDocServerConstants.DOCNUM)) {
					String fieldValue = field.getValue().toString();
					docNum = fieldValue;
				}
				/* store field name - field value pairs in rowMap */
				rowMap.put(fieldName, rawValue);
				
			}
			/* store the row with IDoc number as key in a Map */
			controlRecordMap.put(docNum, rowMap);
			
			/* reset IDoc number before processing the next row */
			docNum = "";
		}
		
		return controlRecordMap;
	}
	
	/**
	 * InMemoryIDocFile
	 * 
	 * In memory implementation of an IDocFile
	 *
	 */
	public class InMemoryIDocFile implements IDocFile {
		
		/* class name for logging purposes */
		private final String CLASSNAME = InMemoryIDocFile.class.getName();
		

		/* complete path on file system for the IDoc file */
		private String path = "";
		
		/* IDoc file content contained in StringBuffer */
		private StringBuffer content = null;
		
		/* logger */
		private Logger logger = null;
		
		/* IDoc number as char [] to preserve leading zeros */
		private char [] idocNumber = null;
		
		/* sender partner number */
		private String senderPartnerNumber = "";
		
		/* receiver partner number */
		private String receiverPartnerNumber = "";
		
		/* IDoc type */
		private String idocType = "";
		
		
		/**
		 * InMemoryIDocFile
		 * 
		 * private constructor
		 * 
		 * @param idocNumber
		 * @param idocType
		 * @param senderPartnerNumber
		 * @param receiverPartnerNumber
		 * @param content
		 * @param path
		 */
		private InMemoryIDocFile(char [] idocNumber, String idocType, String senderPartnerNumber, String receiverPartnerNumber, StringBuffer content, String path) {
			this.content = content;
			this.path = path;
			this.logger = StageLogger.getLogger();
			this.idocNumber = idocNumber;
			this.senderPartnerNumber = senderPartnerNumber;
			this.receiverPartnerNumber = receiverPartnerNumber;
			this.idocType = idocType;
		}
		
		@Override
		public void commit() {
			
			final String METHODNAME = "commit()"; //$NON-NLS-1$
			this.logger.entering(CLASSNAME, METHODNAME);
			
			/* create IDoc file in UTF16 with platform endianness*/
			String encoding = com.ibm.is.sappack.dsstages.common.Utilities.getPlatformEncodingForIDocFiles();
			OutputStreamWriter streamWriter = null;
			BufferedWriter out = null;
			
			try {
				streamWriter = new OutputStreamWriter(new FileOutputStream(this.path), encoding);
		        out = new BufferedWriter(streamWriter);
		        logger.log(Level.FINER, "Writing File {0} in {1} encoding", new Object[]{this.path, encoding});
		        out.write(content.toString());
		        out.close();
			} catch (IOException e) {
				/* IDoc file could not be written to disk */
				this.logger.log(Level.SEVERE, e.getMessage());
				throw new RuntimeException(e.getMessage());
			} finally {
				if (out != null) {
					try {
	               out.close();
               }
               catch (IOException e) {
               	this.logger.log(Level.FINER, e.getMessage());
               } finally {
               	if (streamWriter != null) {
               		try {
	                     streamWriter.close();
                     }
                     catch (IOException e) {
                     	this.logger.log(Level.FINER, e.getMessage());
                     }
               	}
               }
				}
			}
			
			/* log IDocFile summary */
			this.logger.log(Level.INFO, IDocServerMessages.IDocReceived, new Object[]{ String.valueOf(this.idocNumber),this.idocType});
	
			this.logger.exiting(CLASSNAME, METHODNAME);
		}

		@Override
		public void rollback() {
			
			final String METHODNAME = "rollback()"; //$NON-NLS-1$
			this.logger.entering(CLASSNAME, METHODNAME);
			
			logger.log(Level.INFO, IDocServerMessages.RollbackIDocFile, this.path);
			
			/* delete the file */
			File file = new File(this.path);
			boolean fileDeleted = file.delete();
			
			if (!fileDeleted) {
				logger.log(Level.FINER, "IDoc file {0} could not be deleted during rollback", new Object[] {file.getName()}); //$NON-NLS-1$
			}
			
			/* cleanup global variables */
			this.idocNumber = null;
			this.path = null;
			this.content = new StringBuffer();
			this.senderPartnerNumber = "";
			this.receiverPartnerNumber = "";
			
			this.logger.exiting(CLASSNAME, METHODNAME);
		}

		@Override
		public String getIDocNumber() {
			return String.valueOf(this.idocNumber);
		}

		@Override
		public String getReceiverPartnerNumber() {
			return this.receiverPartnerNumber;
		}

		@Override
		public String getSenderPartnerNumber() {
			return this.senderPartnerNumber;
		}

		@Override
		public String getIDocType() {
			return this.idocType;
		}

	
	}

}
