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
// Module Name : com.ibm.is.sappack.dsstages.idoc
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.ccf.CCFUtils;
import com.sap.conn.jco.JCoDestination;

/**
 * IDocFieldAligner
 * 
 * This class is used to convert IDoc segment field data
 * when IDocs are loaded to a non-unicode SAP system or
 * extracted from a non-unicode SAP system.
 * 
 * Usually JCo handles the conversion between unicode and non-unicode
 * systems under the hood, but for the SDATA container that is used for IDoc
 * exchange, the conversion has to be done manually.
 * 
 * In multi-byte non-unicode SAP systems, a character can be composed
 * of multiple bytes. If an IDoc field contains such characters, we 
 * have to align the IDoc field length. For IDoc load, we may also even have to 
 * truncate the IDoc field data, if the data has more bytes than
 * the field can store.
 * 
 */
public class IDocFieldAligner {
	
	/* Logger */
	private Logger logger;
	
	/* encoding of the SAP system */
	private String encoding = "";
	
	/* is target SAP system unicode */
	private boolean isUnicode = true;
	
	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	/**
	 * IDocFieldAligner
	 * 
	 * @param destination to determine the code page of the target SAP system
	 */
	public IDocFieldAligner(JCoDestination destination) {
		
		/* initialize logger */
		this.logger = StageLogger.getLogger();
		
		/* use an EncodingDetector to get the SAP platform encoding */
		EncodingDetector sapEncodingDetector = new EncodingDetector(destination);
			
		this.encoding = sapEncodingDetector.getEncoding();
		this.isUnicode = sapEncodingDetector.isUnicode();
	}
	
	
	/**
	 * alignIDocFieldForLoad
	 * 
	 * Align the IDoc field data[] and IDoc field length
	 * for an IDoc load
	 *
	 *  Example A:
	 * 
	 * Ü takes 2 bytes
	 * ß takes 2 bytes
	 * B takes 1 byte
	 * C takes 1 byte
	 * D takes 1 byte
	 * 
	 * Before Alignment:
	 * 
	 * Field Length(8) _ _ _ _ _ _ _ _
	 * IDoc Data[]     Ü ß B C D 
	 * Number of Bytes 7
	 * 
	 * The data requires 7 bytes and the field length is 8 (field can store 8 bytes),
	 * we don't have to truncate the data, but we have to align the field length,
	 * since the field would't take another 3 bytes (data length is 5).
	 * It would only take 1 more byte.
	 * 
	 * After Alignment:
	 * 
	 * Field Length(6) _ _ _ _ _ _
	 * IDoc Data[]     Ü ß B C D 
	 * Number of Bytes 7
	 * 
	 * Result: The IDoc data[] is the same, but the field length has been shortened.
	 * 
	 * 
	 * 
	 * 
	 * Example B:
	 * 
	 * Ü takes 2 bytes
	 * ß takes 2 bytes
	 * B takes 1 byte
	 * C takes 1 byte
	 * D takes 1 byte
	 * ä takes 2 bytes
	 * 
	 * Before Alignment:
	 * 
	 * Field Length(8) _ _ _ _ _ _ _ _
	 * IDoc Data[]     Ü ß B C D ä
	 * Number of Bytes 9
	 * 
	 * The data requires 9 bytes but the field can only store 8 bytes (field length is 8).
	 * We have to truncate the IDoc data[] and align the field length.
	 * 
	 * After Alignment:
	 * 
	 * Field Length(6) _ _ _ _ _ _
	 * IDoc Data[]     Ü ß B C D 
	 * Number of Bytes 7
	 * 
	 * Result: The IDoc data [] has been truncated (ä has been removed) and the field length
 	 * has been shortened.
 	 * 
	 *
	 * @param field name of the IDoc field
	 * @param docNum IDoc number
	 * @param data
	 * @param length
	 * @return IDocFieldAlignmentResult that holds the updated field length and the updated IDoc field data
	 */
	public IDocFieldAlignmentResult alignIDocFieldForLoad(String fieldName, String docNum, char [] data, int length) {
		
		/* only consider non-unicode SAP systems */
		if (!isUnicode) {

			/*
			 * get the number of bytes in the data [] in the target SAP encoding
			 */
			int numOfBytes = this.getNumberOfBytes(data);

			if (this.logger.isLoggable(Level.FINEST)) {
				this.logger.log(Level.FINEST,
				                "Field {0} data of IDoc {1} is {2} characters long and needs {3} bytes in {4} encoding. Field length is {5}.",
				                new Object[] { fieldName, docNum, data.length,
				                               numOfBytes, this.encoding, length });
			}

			int alignment = 0;

			/*
			 * check if we're dealing with (non-unicode) multi byte characters
			 * like Shift-JIS
			 */
			if (numOfBytes > data.length) {

				if (this.logger.isLoggable(Level.FINER)) {
					this.logger.log(Level.FINER,
									"Aligning field {0} of IDoc {1}. Original field length is {2}.",
									new Object[] { fieldName, docNum, length });
				}

				/*
				 * Check if the field is big enough to store all bytes. A field
				 * with the length of 10 can store 10 bytes. If the data[] has
				 * more bytes than the length of the field, we have to truncate
				 * the data[]. In this case we log a warning.
				 */
				if (numOfBytes > length) {

					/*
					 * segment data contains multi byte characters that exceed
					 * the field limits
					 */
					this.logger.log(Level.WARNING, "CC_IDOC_FieldTruncation",  //$NON-NLS-1$ 
					                new Object[] { fieldName, docNum });

					/*
					 * data [] is to big - we have to truncate characters until
					 * it fits into the field length
					 */
					while (numOfBytes > length) {

						/* remove last character in data [] */
						char[] tmp = new char[data.length - 1];
						System.arraycopy(data, 0, tmp, 0, data.length - 1);
						data = tmp;

						/* update number of bytes */
						numOfBytes = this.getNumberOfBytes(data);
					}
				}

				/*
				 * At this point with have a data[] that fits into the field
				 * length, which means the number of bytes in the data[] is not
				 * bigger than the field length (number of bytes the field can
				 * store)
				 */
				alignment = numOfBytes - data.length;

				/* update field length */
				length = length - alignment;

				if (this.logger.isLoggable(Level.FINER)) {
					this.logger
							.log(Level.FINER,
									"Field {0} of IDoc {1} has been aligned. New field length is {2}.",
									new Object[] { fieldName, docNum, length });
				}
			}

		}
		
		return new IDocFieldAlignmentResultImpl(length, data);
	}
	
	/**
	 * alignIDocFieldForExtract
	 * 
	 * Align the IDoc field length for an extracted IDoc. It is 
	 * not necessary to truncated data because SAP prevents an
	 * IDoc field byte overload for outbound IDocs.
	 * 
	 * The IDoc field data is stored in the data []. If the field
	 * data contains any multi byte characters and the SAP system
	 * is a non-unicode system, the data [] may contain data that
	 * actually belongs to the next IDoc field.
	 * 
	 * We have to count the number of bytes in the data [] and 
	 * truncate the overlapping characters of the next IDoc field.
	 * 
	 * Example:
	 * 
	 * Ü takes 2 bytes
	 * ß takes 2 bytes
	 * B takes 1 byte
	 * C takes 1 byte
	 * D takes 1 byte
	 * ä takes 2 bytes
	 * 
	 * Before Alignment:
	 * 
	 * Field Length(8) _ _ _ _ _ _ _ _
	 * IDoc Data[]     Ü ß B C D ä D C
	 * Number of Bytes 11
	 * 
	 * The IDoc data [] stores characters with 11 bytes but the IDoc field
	 * can only store 8 bytes. We need to truncate C, D and ä.
	 * 
	 * After Alignment:
	 * 
	 * Field Length(8) _ _ _ _ _ _ _ _
	 * IDoc Data[]     Ü ß B C D
	 * Number of Bytes 7
	 * 
	 * The field length is unchanged, but the content of the IDoc data [] has changed and
	 * the length of the IDoc data [] is smaller now, since we truncated 3 characters that 
	 * did not belong to this IDoc field.
	 *
	 * @param field name of the IDoc field
	 * @param docNum IDoc number
	 * @param data of the IDoc field
	 * @param length of the IDoc field
	 * @return IDocFieldAlignmentResult that holds the aligned length of the IDoc field data [] and the aligned IDoc field data [] itself
	 */
	public IDocFieldAlignmentResult alignIDocFieldForExtract(String fieldName, String docNum, char [] data, int length) {
		
		/* only consider non-unicode SAP systems */
		if (!isUnicode) {

			/*
			 * get the number of bytes in the data [] in the target SAP encoding
			 */
			int numOfBytes = this.getNumberOfBytes(data);

			if (this.logger.isLoggable(Level.FINEST)) {
				this.logger
						.log(Level.FINEST,
								"Field {0} data of IDoc {1} is {2} characters long and needs {3} bytes in {4} encoding. Field length is {5}.",
								new Object[] { fieldName, docNum, data.length,
										numOfBytes, this.encoding, length });
			}

			/*
			 * check if we're dealing with (non-unicode) multi byte characters
			 * like Shift-JIS
			 */
			if (numOfBytes > data.length) {

				if (this.logger.isLoggable(Level.FINER)) {
					this.logger
							.log(Level.FINER,
									"Aligning field {0} of IDoc {1}. Original field length is {2}.",
									new Object[] { fieldName, docNum, length });
				}

				if (numOfBytes > length) {

					/* truncate characters until all bytes fit into the field */
					while (numOfBytes > length) {

						/* remove last character in data [] */
						char[] tmp = new char[data.length - 1];
						System.arraycopy(data, 0, tmp, 0, data.length - 1);
						data = tmp;

						/* update number of bytes */
						numOfBytes = this.getNumberOfBytes(data);
					}
				}

				/*
				 * create a data [] with the original field length and
				 * initialize it with blanks
				 */
				char[] alignedData = new char[length];
				Arrays.fill(alignedData, ' ');
				/* remember remaining characters in data [] */
				length = data.length;
				/* copy content of data [] to alignedData [] */
				System.arraycopy(data, 0, alignedData, 0, data.length);
				data = alignedData;

				if (this.logger.isLoggable(Level.FINER)) {
					this.logger.log(Level.FINER, "Field {0} of IDoc {1} has been aligned. New field length is {2}.",
					                new Object[] { fieldName, docNum, length });
				}
			}

		}
		
		return new IDocFieldAlignmentResultImpl(length, data);
	}

	/**
	 * getNumberOfBytes
	 * 
	 * get the number of bytes of the given char array
	 * in the SAP platform encoding.
	 * 
	 * @param data
	 * @return number of bytes
	 */
	private int getNumberOfBytes(char [] data) {
		
		int numberOfBytes = 0;
		
		try {
			numberOfBytes = new String(data).getBytes(this.encoding).length;
			
		} catch (UnsupportedEncodingException e) { 
		
			/* SAP system has an encoding that Java cannot handle */
			this.logger.log(Level.SEVERE, "CC_IDOC_UnknownSAPEncoding", e); //$NON-NLS-1$
			CCFUtils.throwCC_Exception(e);
		}
		
		return numberOfBytes;
	}
	
	
	
	/**
	 * IDocFieldAlignmentResultImpl
	 * 
	 * stores the alignment result
	 */
	class IDocFieldAlignmentResultImpl implements IDocFieldAlignmentResult {

		/* alignment results */
		private int alignedLength;
		private char [] alignedData;
		
		/**
		 * IDocFieldAlignmentResultImpl
		 * 
		 * @param alignedLength
		 * @param alignedData
		 */
		protected IDocFieldAlignmentResultImpl(int alignedLength, char [] alignedData) {
			
			this.alignedLength = alignedLength;
			this.alignedData = alignedData;
		}
		
		@Override
		public int getLength() {
			return this.alignedLength;
		}

		@Override
		public char[] getData() {
			return this.alignedData;
		}
		
	}

}
