//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2012                                              
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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.ccf.CCFUtils;
import com.sap.conn.jco.JCoAttributes;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

/**
 * EncodingDetector
 * 
 * Helper class to detect the SAP platform
 * encoding based on a JCo destination object.
 * EncodingDetector also provides the functionality
 * to read the SAP encoding from an environment
 * variable that has to be set by the user.
 *
 */
public class EncodingDetector {

	/* SAP platform encoding */
	private String encoding = "";

	/* environment variable to manually set SAP platform encoding */
	public static final String SAP_PLATFORM_ENCODING = "DSSAP_SAP_PLATFORM_ENCODING"; //$NON-NLS-1$
	
	/* encodings */
	public static final String SHIFT_JIS = "Shift_JIS"; //$NON-NLS-1$
	public static final String X_SJIS_0213 = "x-SJIS_0213"; //$NON-NLS-1$
	
	/* Logger */
	Logger logger = null;
	
	
	/**
	 * EncodingDetector
	 * 
	 * @param destination
	 */
	public EncodingDetector(JCoDestination destination) {
	
		/* initialize logger */
		this.logger = StageLogger.getLogger();
		
		/*
		 * check if SAP platform encoding is set in environment variable - otherwise
		 * get SAP platform encoding from JCoDestination attributes 
		 */
		String customEncoding = System.getenv(SAP_PLATFORM_ENCODING);
		if(customEncoding != null && !customEncoding.equals(Constants.EMPTY_STRING)) {
			
			/* environment variable is set */
			this.encoding = customEncoding;
			
			if (this.logger.isLoggable(Level.FINER)) {
				this.logger.log(Level.FINER, "Using custom SAP platform encoding {0} from environment variable {1}.", new Object[]{this.encoding, SAP_PLATFORM_ENCODING});
			}
			
		} else {
			
			/* get SAP platform encoding from JCoDestination attributes */
			try {
				
				JCoAttributes attributes = destination.getAttributes();
				String sapEncoding = attributes.getPartnerEncoding();
				this.encoding = this.convertSAPEncodingToJAVAEncoding(sapEncoding);
				
				if (this.logger.isLoggable(Level.FINER)) {
					this.logger.log(Level.FINER, "Using SAP platform encoding {0}.", this.encoding);
				}
				
			} catch (JCoException e) {
				this.logger.log(Level.SEVERE, "CC_IDOC_UnexpectedSAPException", e); //$NON-NLS-1$
				CCFUtils.throwCC_Exception(e);
			}
		}
				
	}
	
	/**
	 * convertSAPEncodingToJAVAEncoding
	 * 
	 * In some cases there is no equivalent Java encoding
	 * for a given SAP encoding. In this method, custom
	 * encoding mappings between SAP and Java are defined.
	 * 
	 * @param sapEncoding
	 * @return
	 */
	private String convertSAPEncodingToJAVAEncoding(String sapEncoding) {
		
		/* replace SAP Shift_JIS with JAVA x-SJIS_0213 */
		if(sapEncoding.equals(SHIFT_JIS)) {
			if (this.logger.isLoggable(Level.FINER)) {
				this.logger.log(Level.FINER, "Using Java Charset {0} for SAP encoding {1}", new Object[]{X_SJIS_0213, SHIFT_JIS});
			}
			return X_SJIS_0213;
		}
		
		return sapEncoding;
	}
	
	
	/**
	 * getEncoding
	 * 
	 * @return SAP platform encoding
	 */
	public String getEncoding() {
		return this.encoding;
	}
	
	
	/**
	 * isUnicode
	 * 
	 * check if the SAP system is unicode or non-unicode
	 * 
	 * @return true if encoding is utf-16. Otherwise return false
	 */
	public boolean isUnicode() {
		
		/* check if we have a unicode or non-unicode SAP system */
		if(!this.encoding.equalsIgnoreCase(Constants.UTF16)) {
			
			if (this.logger.isLoggable(Level.FINER)) {
				this.logger.log(Level.FINER, "Non-Unicode SAP System with {0} encoding", this.encoding);
			}
			
			return false;
			
		} else {
			
			if (this.logger.isLoggable(Level.FINER)) {
				this.logger.log(Level.FINER, "Unicode SAP System with {0} encoding", this.encoding);
			}
			return true;
		}
	}
	
	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
}
