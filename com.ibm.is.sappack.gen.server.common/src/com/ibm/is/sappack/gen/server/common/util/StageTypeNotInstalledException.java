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
// Module Name : com.ibm.is.sappack.gen.server.common.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.common.util;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.util.ServerMessageCatalog;
import com.ibm.is.sappack.gen.server.common.DSStageTypeEnum;



public class StageTypeNotInstalledException extends DSAccessException 
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   protected static final long serialVersionUID = DSAccessException.serialVersionUID + 2;

   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------

   
	static String copyright() { 
	   return com.ibm.is.sappack.gen.server.common.util.Copyright.IBM_COPYRIGHT_SHORT; 
	}   

	
   public StageTypeNotInstalledException(String stageTypeName) {
      this(stageTypeName, null);
   } // end of StageTypeNotInstalledException()
   
   
   public StageTypeNotInstalledException(String stageTypeName, Throwable cause) {
      super(null, cause);

      String sapPacksVer   = null;
      String msgId         = "108000E";
      String msgParamArr[] = Constants.NO_PARAMS;

      // check if the stage type is a SAP packs stage type ...
      if (stageTypeName != null) {
         // SAPPacks v6.5 Stage type check
         if (stageTypeName.equals(DSStageTypeEnum.SAP_IDOC_EXTRACT_PX_LITERAL.getName()) || 
             stageTypeName.equals(DSStageTypeEnum.SAP_IDOC_LOAD_PX_LITERAL.getName())    || 
             stageTypeName.equals(DSStageTypeEnum.ABAP_EXTRACT_PX_LITERAL.getName())) {

            msgId       = "108100E";
            sapPacksVer = ServerMessageCatalog.getDefaultCatalog().getText("00114I");
            msgParamArr = new String[] { sapPacksVer } ;
         }
         else {
            // SAPPacks v7.x Stage type check
            if (stageTypeName.equals(DSStageTypeEnum.SAP_IDOC_EXTRACT_CONNECTOR_PX_LITERAL.getName()) || 
                stageTypeName.equals(DSStageTypeEnum.SAP_IDOC_LOAD_CONNECTOR_PX_LITERAL.getName()))   { 

               msgId       = "108100E";
               sapPacksVer = ServerMessageCatalog.getDefaultCatalog().getText("00115I");
               msgParamArr = new String[] { sapPacksVer } ;
            }
            else {
               // ==> unknown stage type
               msgParamArr = new String[] { stageTypeName } ;
            }
         }
      } // end of if (stageTypeName != null)
      
      setMessageId(msgId, msgParamArr);
   } // end of StageTypeNotInstalledException()
	
} // end of class StageTypeNotInstalledException
