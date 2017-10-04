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
// Module Name : com.ibm.is.sappack.gen.common.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.util;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.Constants.VersionCheckResult;

public final class StringUtils {
	// -------------------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------------------
   private static final char   VERSION_SEPARATOR_CHAR  = '.';
   public  static final String PARAM_START_CHAR        = Constants.JOB_PARAM_SEPARATOR;
   public  static final String PARAM_END_CHAR          = PARAM_START_CHAR;

	// -------------------------------------------------------------------------------------
	// Member Variables
	// -------------------------------------------------------------------------------------


	static String copyright() {
		return com.ibm.is.sappack.gen.common.util.Copyright.IBM_COPYRIGHT_SHORT;
	}


	public static void checkModelVersion(String parModelVersion, String parMajorVersion) 
	       throws JobGeneratorException {

		if (!isVersionSupported(parModelVersion, parMajorVersion)) {
			throw new JobGeneratorException("102200E", new String[] { parModelVersion, 
			                                                          parMajorVersion } );
		}
	} // end of checkModelVersion()


	public static void checkClientServerVersion(String parClientVersion, String parServerVersion) 
          throws JobGeneratorException {

		if (!isVersionSupported(parClientVersion, parServerVersion)) {
			throw new JobGeneratorException("106800E", new String[] { parClientVersion, 
			                                                          parServerVersion } );
		}
	} // end of checkClientServerVersion()


	public static VersionCheckResult checkVersion(String parVersion2check, String parRequiredVersion) 
          throws JobGeneratorException {

		VersionCheckResult chkResult;

		if (parVersion2check == null) {
			throw new JobGeneratorException("102100E", Constants.NO_PARAMS);
		}

		// check for "non-checking" character ...
		if (parVersion2check.length() > 0 && parVersion2check.charAt(0) == Constants.MODEL_VERSION_ALWAYS_ALLOWED_CHAR) {
			// model version is not to be checked
			chkResult = VersionCheckResult.CheckedVersionEqual;
		}
		else {
			String   majorVersionCheck;
			String   minorVersionCheck;
			String   majorVersionRequired;
			String   minorVersionRequired;
			int      curSepIdxCheck;
			int      curSepIdxRequired;

			// separate version into major and minor version
			// --> version to check
			curSepIdxCheck = parVersion2check.indexOf(VERSION_SEPARATOR_CHAR);
			if (curSepIdxCheck > -1) {
				majorVersionCheck = parVersion2check.substring(0, curSepIdxCheck);
				minorVersionCheck = parVersion2check.substring(curSepIdxCheck+1, parVersion2check.length());
			}
			else {
				majorVersionCheck = parVersion2check;
				minorVersionCheck = null;
			}  

			// --> required version
			curSepIdxRequired = parRequiredVersion.indexOf(VERSION_SEPARATOR_CHAR);
			if (curSepIdxRequired > -1) {
				majorVersionRequired = parRequiredVersion.substring(0, curSepIdxRequired);
				minorVersionRequired = parRequiredVersion.substring(curSepIdxRequired+1, parRequiredVersion.length());
			}
			else {
				majorVersionRequired = parRequiredVersion;
				minorVersionRequired = null;
			}  
			
			chkResult = equalVersionString(majorVersionCheck, majorVersionRequired);
			if (chkResult == VersionCheckResult.CheckedVersionEqual) {
				if (minorVersionCheck    == null || minorVersionCheck.isEmpty()   || 
				    minorVersionRequired == null || minorVersionRequired.isEmpty())  {
					// no minor version check ==> check is finished
					;
				}
				else {
					// let's check the MINOR version
					chkResult = equalVersionString(minorVersionCheck, minorVersionRequired);
				} // end of if (parRequiredMinorVersion == null || curVersionSepIdx < 0) 
			} // end of if (curVersion.equals(parMajorVersion))
		} // end of (else) if (parModelVersion.length() > 0 && ... == Constants.MODEL_VERSION_ALWAYS_ALLOWED_CHAR)

		return(chkResult);
	} // end of checkVersion()


   public static String cleanFieldName(String fieldName) {
      String retCleanedName = fieldName;
      
      if (fieldName != null && fieldName.trim().length() > 0) {
//         retCleanedName = fieldName.replace('/', '_');
         retCleanedName = fieldName.replaceAll("[ ,./]", "_");

         if (retCleanedName.charAt(0) == '_') {
            retCleanedName = 'A' + retCleanedName.substring(1);
         }

         // replace all '#' by ''
         retCleanedName = retCleanedName.replaceAll("[#]", "");
      }
      
      return(retCleanedName);
   } // end of cleanFieldName()


   private static VersionCheckResult equalVersionString(String isStr, String toBeStr) {
   	VersionCheckResult chkResult = null;
   	int                checkLen;
   	int                idx;

   	idx       = 0;
   	chkResult = VersionCheckResult.CheckedVersionEqual;
   	checkLen  = Math.min(isStr.length(), toBeStr.length());

   	while(idx < checkLen && chkResult == VersionCheckResult.CheckedVersionEqual) {
   		if (isStr.charAt(idx) > toBeStr.charAt(idx)) {
   			chkResult = VersionCheckResult.CheckedVersionAbove;
   		}
   		else {
   			if (isStr.charAt(idx) < toBeStr.charAt(idx)) {
   				chkResult = VersionCheckResult.CheckedVersionBelow;
   			}
   		}
   		idx ++;
   	} // end of while(idx < checkLen && chkResult == VersionCheckResult.CheckedVersionEqual)

   	return(chkResult);
   } // end of equalVersionString()
   
   public static String getVersionInfoString() {
      return(ServerMessageCatalog.getDefaultCatalog().getText("00100I", 
                                                              new Object[] { Constants.NEWLINE,
                                                                             Constants.BUILD_ID, 
                                                                             Constants.CLIENT_SERVER_VERSION, 
                                                                             Constants.MODEL_VERSION } ));
      
   } // end of getVersionInfoString()


   public static boolean isJobParamVariable(String paramName) {
      boolean isJobParam;
      int     paramIdxEnd;
      int     paramIdxStart;

      isJobParam = false;
      
      // job parameter must have :
      //   - at least 3 characters
      //   - one PARAM_START_CHAR and one PARAM_END_CHAR
      if (paramName != null && paramName.length() > 2) {   // at least '# #'

      	paramIdxStart = paramName.indexOf(PARAM_START_CHAR);
      	if (paramIdxStart > -1) {

         	paramIdxEnd = paramName.indexOf(PARAM_END_CHAR, paramIdxStart + 1);
         	if (paramIdxEnd > -1) {
               isJobParam = true;
         	} // end of if (paramIdxEnd > -1)
      	} // end of if (paramIdxStart > -1)

//      	// check if the passed parameter name is a variable
//      	if (paramName.charAt(0)                     == PARAM_START_CHAR && 
//      		paramName.charAt(paramName.length() - 1) == PARAM_END_CHAR) {
//      		isJobParam = true;
//      	} 
      } // end of if (paramName != null && paramName.length() > 1)

      return (isJobParam);
   } // end of isJobParamVariable()
   

	public static boolean isVersionSupported(String parVersion2check, String parVersionRequired)
          throws JobGeneratorException {

		boolean isSupported;

		switch(checkVersion(parVersion2check, parVersionRequired)) {
			case CheckedVersionAbove:
				  isSupported = false;
				  break;

			default:     
				  isSupported = true;
		} // end of switch(checkVersion(parVersion2check, parMajorVersion))

		return(isSupported);
	} // end of isVersionSupported()


	public static String replaceString(String parSource, String parExpression, String parReplacement) {
	   String       returnResult;
		StringBuffer resultBuffer;
		int endIdx;
		int startIdx;
		
		if (parSource == null) {
	      returnResult = null;
		}
		else {
		    // search for occurrence of the specified expression
	      resultBuffer = new StringBuffer();
	      endIdx       = 0;
	      startIdx     = parSource.indexOf(parExpression);
	      while (startIdx > -1) {
	         // copy data from last end position until new start position
	         resultBuffer.append(parSource.substring(endIdx, startIdx));
	         resultBuffer.append(parReplacement);

	         // calculate new end and start indices
	         endIdx   = startIdx + parExpression.length();
	         startIdx = parSource.indexOf(parExpression, endIdx);
	      } // end of while(startIdx > -1)

	      // append remaining data
	      resultBuffer.append(parSource.substring(endIdx, parSource.length()));
	      
	      returnResult = resultBuffer.toString();
		}

		return (returnResult);
	} // end of replaceString()


	public static String trim(String parData) {
		String resultData;

		if (parData == null) {
			resultData = parData;
		}
		else {
			resultData = parData.trim();
		}

		return (resultData);
	} // end of trim()
	
	/*
	 * This method works similar like MessageFormat.format() except that single quotes
	 * are left as-is.
	 */
	public static String replaceMessageArguments(String pattern, Object[] args) {
		String result = pattern;
		for (int i=0; i<args.length; i++) {
			result = replaceString(result, "{" + i + "}", args[i].toString()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return result;
	}
	
} // end of class StringUtils
