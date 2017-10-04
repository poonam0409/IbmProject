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
// Module Name : com.ibm.is.sappack.gen.tools.sap.utilities
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.tools.sap.utilities;


import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmNameConverter;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocType;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Segment;


public class IDocTypeNameConverter extends LogicalTableNameConverter {
   // -------------------------------------------------------------------------------------
   // Constants
   // -------------------------------------------------------------------------------------

   // -------------------------------------------------------------------------------------
   // Member Variables
   // -------------------------------------------------------------------------------------
   private String _IDocTypeName;
   private int    _TypeNameLen;
   
   
   static String copyright() {
      return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT;
   }
   

   private IDocTypeNameConverter(IDocType idocType) {
      // save the IDoc type name
      _IDocTypeName = StringUtils.cleanFieldName(idocType.getName());
      _TypeNameLen  = _IDocTypeName.length() + Constants.LDM_ENTITY_NAME_SEPARATOR.length();
   } // end of IDocTypeNameConverter()


   private IDocTypeNameConverter(Segment segment) {
      this(segment.getIDocType());
   } // end of IDocTypeNameConverter()


   @Override
   public String convertAttributeName(String attributeName)
   {
      String retAttribName = attributeName;
      int    maxLen        = getMaxNameLen();
      
      if (attributeName != null) {
         attributeName = StringUtils.cleanFieldName(attributeName);
         retAttribName = attributeName;
         
         if (attributeName.length() > maxLen) {
            String    prefix = null;
            String    suffix;
            String    id;
            int       idx1stSep;
            int       idx2ndSep;
            int       key1Len;
            int       key2Len;
            int       prefLen;
            int       suffLen;
            
            id        = attributeName;
            suffix    = ""; //$NON-NLS-1$

            // check if attribute name is a 'key type' name (for example, 'SEG_', 'IDOC_' 
            idx1stSep = attributeName.indexOf(Constants.LDM_ENTITY_NAME_SEPARATOR);
            if (idx1stSep > -1) {
               prefix  = attributeName.substring(0, idx1stSep + 1);  // including SEPARATOR
               prefLen = prefix.length();
               key1Len = Math.min(prefLen, Constants.IDOC_PRIMARY_KEY_TEMPLATE.length());
               key2Len = Math.min(prefLen, Constants.IDOC_PRIMARY_KEY_ROOT_TEMPLATE.length());
               
               if (prefix.equals(Constants.IDOC_PRIMARY_KEY_TEMPLATE.substring(0, key1Len))        || 
                   prefix.equals(Constants.IDOC_PRIMARY_KEY_ROOT_TEMPLATE.substring(0, key2Len))) {
                  
                  suffix  = attributeName.substring(attributeName.length() - Constants.P_KEY_SUFFIX.length());
                  suffLen = suffix.length();
                  maxLen  = maxLen - (prefLen + suffLen);
                  
                  // get the 'real' attribute name without prefix and suffix ...
                  id            = attributeName.substring(prefLen, attributeName.length() - suffLen);
                  attributeName = convertName(null, id, maxLen);
                  retAttribName = prefix + attributeName + suffix; 
               }
               else {
                  prefix = attributeName.substring(0, idx1stSep);
                  
                  idx2ndSep = attributeName.indexOf(Constants.LDM_ENTITY_NAME_SEPARATOR, idx1stSep + 1);
                  if (idx2ndSep > -1) {
                     id     = attributeName.substring(idx1stSep+1, idx2ndSep);
                     suffix = attributeName.substring(idx2ndSep);
                  }
                  else {
                     id = attributeName.substring(idx1stSep+1);
                  }
                  
                  retAttribName = convertName(prefix, id, maxLen) + suffix;
               } // end of (else) if (prefix.equals(Constants.IDOC_PRIMARY_KEY_TEMPLATE.substring(0, prefLen)) || ...
            }
            else {
               retAttribName = convertName(prefix, id, maxLen);
            } // end of (else) if (idx1stSep > -1)
         } // end of if (attributeName.length() > maxLen)
      } // end of if (attributeName != null)

// if (getMaxNameLen() < retAttribName.length())
// System.out.println("maxlen = " + getMaxNameLen() + " --> len = " + retAttribName.length());

      return(retAttribName);
   } // end of convertAttributeName()

   
   @Override
   public String convertEntityName(String entityName)
   {
      String retConvertedName = entityName;
      
      if (entityName != null) {
         entityName       = StringUtils.cleanFieldName(entityName);
         retConvertedName = entityName;
         
         if (entityName.length() > getMaxNameLen()) {
            String prefix = null;
            
            // get entity name without prefix part
            if (entityName.startsWith(_IDocTypeName)) {
               entityName = entityName.substring(_TypeNameLen);
               prefix     = _IDocTypeName;
            }
            
            retConvertedName = convertName(prefix, entityName, getMaxNameLen());
         }
      } // end of if (entityName != null)

      return(retConvertedName);
   } // end of convertEntityName()

   
   public String getIDocTypeName() {
      return(this._IDocTypeName);
   } // end of getIDocTypeName()
   
   
   public static LdmNameConverter createIDocTypeNameConverter(IDocType idocType) {
	   if (ModeManager.isModellingEnabled()) {
		   return new ModellingNameConverter();
	   }
	   return new IDocTypeNameConverter(idocType);
   }
} // end of class IDocTypeNameConverter
