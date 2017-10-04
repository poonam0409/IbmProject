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
// Module Name : com.ibm.is.sappack.gen.tools.sap.utilities
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.utilities;


import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.tools.sap.PreferencePageRMSettings;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmNameConverter;


public class LogicalTableNameConverter extends DefaultNameConverter {
   // -------------------------------------------------------------------------------------
   // Constants
   // -------------------------------------------------------------------------------------
      

   // -------------------------------------------------------------------------------------
   // Member Variables
   // -------------------------------------------------------------------------------------
   private int _MaxNameLen;
   
   
   static String copyright() {
      return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT;
   }
   

   protected LogicalTableNameConverter() {
      // get maximum entity name length from Preference page
      _MaxNameLen = PreferencePageRMSettings.getMaxDBIdentifierLength();
      
   } // end of LogicalTableNameConverter()
   
   
   @Override
   public String convertAttributeName(String attributeName) {
      return(convertEntityName(attributeName));
   }


   @Override
   public String convertEntityName(String entityName)
   {
      String retConvertedName; 
      
      entityName       = StringUtils.cleanFieldName(entityName);
      retConvertedName = entityName;
      
      if (entityName != null) {
         if (entityName.length() > this._MaxNameLen) {
            retConvertedName = convertName(null, entityName, this._MaxNameLen);
         }
      } // end of if (entityName != null)

      return(retConvertedName);
   } // end of convertEntityName()


   @Override
   public String convertRelationShipName(String relationShipName) {
      return(convertEntityName(relationShipName));
   }
   
   @Override
   public String convertRelationShipName(String relationShipName, int suffixLen) {
      String retConvertedName;
      int    maxLen;
      
      relationShipName = StringUtils.cleanFieldName(relationShipName);
      retConvertedName = relationShipName;
      
      if (relationShipName != null) {
         if (suffixLen < 0) {
            suffixLen = 0;
         }
         else {
            // suffix length is larger than the maximum length --> just take a third 
            if (suffixLen > this._MaxNameLen) {
               suffixLen = (int) (suffixLen / 3);
            }
            
            // suffix length is still larger than the maximum length --> ignore it !!! 
            if (suffixLen > this._MaxNameLen) {
                  suffixLen = 0;
            }
         } // end of if (suffixLen < 0)
         maxLen = this._MaxNameLen - suffixLen;
         
         if (relationShipName.length() > maxLen) {
            retConvertedName = convertName(null, relationShipName, maxLen);
         }
      } // end of if (relationShipName != null)

      return(retConvertedName);
   }

   
   protected String convertName(String prefix, String identifier, int maxLen) {
      String       retConvertedId;
      StringBuffer idBuf;
      int          remainingLen;
      
      // the maximum length must be positive
      if (maxLen > 0) {
         idBuf = new StringBuffer();
         
         if (prefix == null) {
            int sepIdx;
            
            // the separator is the 'LDM_ENTITY_NAME_SEPARATOR'
            sepIdx = identifier.indexOf(Constants.LDM_ENTITY_NAME_SEPARATOR);
            if (sepIdx > -1) {
               prefix     = identifier.substring(0, sepIdx);
               identifier = identifier.substring(sepIdx + 1);
            }
         } // end of if (prefix == null)
       
         if (prefix != null) {
            if (prefix.length() <= HASHCODE_LEN) {
               idBuf.append(prefix);
            }
            else {
               idBuf.append(prefix.charAt(0));
               idBuf.append(getHashPartAsString(prefix.hashCode()));
            }
            
            idBuf.append(Constants.LDM_ENTITY_NAME_SEPARATOR);
         } // end of if (prefix != null) {


         remainingLen = maxLen - idBuf.length();
         
         if (remainingLen < 1) {
            throw new IllegalArgumentException(Messages.LogicalTableNameConverter_0);
         }

         if (remainingLen >= identifier.length()) {
            idBuf.append(identifier);
         }
         else {
            if (remainingLen <= HASHCODE_LEN) {
               idBuf.append(identifier.substring(0, remainingLen));
            }
            else {
               
               idBuf.append(identifier.substring(0, remainingLen - HASHCODE_LEN));
               idBuf.append(getHashPartAsString(identifier.hashCode()));
            }
         } // end of (else) if (remainingLen >= identifier.length())
         
         retConvertedId = idBuf.toString();
      }
      else {
         retConvertedId = identifier; 
      } // end of if (maxLen > 0)
      
      return(retConvertedId);
   } // end of convertName()


   private String getHashPartAsString(int hashCode) {
      String hashAsString;
      
      hashAsString = String.valueOf(Math.abs(hashCode));
//      hashAsString = hashAsString.substring(0, HASHCODE_LEN);                             // first HASHCODE_LEN digits 
      hashAsString = hashAsString.substring(hashAsString.length() - HASHCODE_LEN);       // last HASHCODE_LEN digits
      
      return(hashAsString);
   } // end of getHashPartAsString()


   public int getMaxNameLen() {
      return(this._MaxNameLen);
   } // end of getMaxNameLen()

   public static LdmNameConverter createLogicalTableNameConverter() {
	   if (ModeManager.isModellingEnabled()) {
		   return new ModellingNameConverter();
	   }
	   return new LogicalTableNameConverter();
   }
   
} // end of class LogicalTableNameConverter
