//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2012                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.common.request
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.request;


import org.w3c.dom.Node;

import com.ibm.is.sappack.gen.common.Constants;



final public class SupportedColumnTypesMap extends SupportedTypesBaseMap
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public  static final String XML_TAG_                                       = "ColumnType";     //$NON-NLS-1$
   
   public  final static int     COLUMN_TYPE_INT_ALL_TYPES                     = -1;
   public  final static Integer COLUMN_TYPE_ENUM_ALL_TYPES                    = new Integer(COLUMN_TYPE_INT_ALL_TYPES);
   public  final static int     COLUMN_TYPE_INT_IDOC_TYPE                     = 1;
   public  final static Integer COLUMN_TYPE_ENUM_IDOC_TYPE                    = new Integer(COLUMN_TYPE_INT_IDOC_TYPE);
   public  final static int     COLUMN_TYPE_INT_LOGICAL_TABLE                 = 2;
   public  final static Integer COLUMN_TYPE_ENUM_LOGICAL_TABLE                = new Integer(COLUMN_TYPE_INT_LOGICAL_TABLE);
   private  final static int     COLUMN_TYPE_INT_JOINED_CHECK_AND_TEXT_TABLE   = 3;
   public  final static Integer COLUMN_TYPE_ENUM_JOINED_CHECK_AND_TEXT_TABLE  = new Integer(COLUMN_TYPE_INT_JOINED_CHECK_AND_TEXT_TABLE);
   public  final static int     COLUMN_TYPE_INT_CW_TECH_FIELD                 = 5;
   public  final static Integer COLUMN_TYPE_ENUM_CW_TECH_FIELD                = new Integer(COLUMN_TYPE_INT_CW_TECH_FIELD);
   public  final static int     COLUMN_TYPE_INT_IDOC_LOAD_STATUS              = 50;
   public  final static Integer COLUMN_TYPE_ENUM_IDOC_LOAD_STATUS             = new Integer(COLUMN_TYPE_INT_IDOC_LOAD_STATUS);
   public  final static int     COLUMN_TYPE_INT_IDOC_LOAD_JOB_FIELD           = 51;
   public  final static Integer COLUMN_TYPE_ENUM_IDOC_LOAD_JOB_FIELD          = new Integer(COLUMN_TYPE_INT_IDOC_LOAD_JOB_FIELD);
   public  final static int     COLUMN_TYPE_INT_REFERENCE_CHECK_TABLE         = 60;
   public  final static Integer COLUMN_TYPE_ENUM_REFERENCE_CHECK_TABLE        = new Integer(COLUMN_TYPE_INT_REFERENCE_CHECK_TABLE);
   public  final static int     COLUMN_TYPE_INT_REFERENCE_TEXT_TABLE          = 61;
   public  final static Integer COLUMN_TYPE_ENUM_REFERENCE_TEXT_TABLE         = new Integer(COLUMN_TYPE_INT_REFERENCE_TEXT_TABLE);
   public  final static int     COLUMN_TYPE_INT_NON_REFERENCE_CHECK_TABLE     = 62;
   public  final static Integer COLUMN_TYPE_ENUM_NON_REFERENCE_CHECK_TABLE    = new Integer(COLUMN_TYPE_INT_NON_REFERENCE_CHECK_TABLE);

   /*   
   public  final static int     COLUMN_TYPE_INT_IDOC_EXTRACT_JOB_FIELD        = 52;
   public  final static Integer COLUMN_TYPE_ENUM_IDOC_EXTRACT_JOB_FIELD       = new Integer(COLUMN_TYPE_INT_IDOC_EXTRACT_JOB_FIELD);
*/
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------


   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; 
   }   


   SupportedColumnTypesMap()
   {
      super();
   } // end of SupportedColumnTypesMap()
   
   
   SupportedColumnTypesMap(Node parTypesNode)
   {
      super(parTypesNode);
   } // end of SupportedColumnTypesMap()
   
   
   public SupportedColumnTypesMap(SupportedColumnTypesMap instance)
   {
      super(instance);
   } // end of SupportedColumnTypesMap()
   
   
   void checkType(Integer parType) throws IllegalArgumentException
   {
      validateType(parType);
   } // end of checkType()
      
   
   public static void validateType(Integer parType) throws IllegalArgumentException
   {
      if (parType == null)
      {
         throw new IllegalArgumentException("Column Type must not be null");
      }
      else
      {
         switch(parType.intValue())
         {
            // Supported Column Types
            case COLUMN_TYPE_INT_ALL_TYPES: 
            case COLUMN_TYPE_INT_IDOC_TYPE: 
//            case COLUMN_TYPE_INT_IDOC_EXTRACT_JOB_FIELD: 
            case COLUMN_TYPE_INT_IDOC_LOAD_JOB_FIELD: 
            case COLUMN_TYPE_INT_JOINED_CHECK_AND_TEXT_TABLE: 
            case COLUMN_TYPE_INT_REFERENCE_CHECK_TABLE:
            case COLUMN_TYPE_INT_NON_REFERENCE_CHECK_TABLE:
            case COLUMN_TYPE_INT_REFERENCE_TEXT_TABLE:
            case COLUMN_TYPE_INT_IDOC_LOAD_STATUS: 
            case COLUMN_TYPE_INT_LOGICAL_TABLE: 
            case COLUMN_TYPE_INT_CW_TECH_FIELD:
                 break;
                      
            default:
                 throw new IllegalArgumentException("Column Type '" + parType.toString() + "' is not supported.");
         } // end of switch(parType.intValue())
      } // end of (else) if (parType == null)
   } // end of validateType()
      
   
   public String getTypeAsString(int parTypeId)
   {
      String typeString;
      
      switch(parTypeId)
      {
         case COLUMN_TYPE_INT_ALL_TYPES:
              typeString = Constants.DATA_OBJECT_SOURCE_TYPE_ALL;
              break;
              
         case COLUMN_TYPE_INT_IDOC_TYPE:
              typeString = Constants.DATA_OBJECT_SOURCE_TYPE_IDOC;
              break;
            
         case COLUMN_TYPE_INT_IDOC_LOAD_JOB_FIELD:
              typeString = Constants.DATA_OBJECT_SOURCE_TYPE_LOAD_JOB_FIELD;
              break;
         
         case COLUMN_TYPE_INT_JOINED_CHECK_AND_TEXT_TABLE:
              typeString = Constants.DATA_OBJECT_SOURCE_TYPE_JOINED_CHECK_AND_TEXT_TABLE;
              break;
       
         case COLUMN_TYPE_INT_IDOC_LOAD_STATUS:
              typeString = Constants.DATA_OBJECT_SOURCE_TYPE_LOAD_STATUS;
            break;
       
         case COLUMN_TYPE_INT_LOGICAL_TABLE:
              typeString = Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE;
              break;
       
         case COLUMN_TYPE_INT_CW_TECH_FIELD:
              typeString = Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD;
              break;
              
         case COLUMN_TYPE_INT_REFERENCE_CHECK_TABLE:
              typeString = Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_CHECK_TABLE;
              break;

         case COLUMN_TYPE_INT_REFERENCE_TEXT_TABLE:
              typeString = Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_TEXT_TABLE;
              break;

         case COLUMN_TYPE_INT_NON_REFERENCE_CHECK_TABLE:
              typeString = Constants.DATA_OBJECT_SOURCE_TYPE_NON_REFERENCE_CHECK_TABLE;
              break;

         default:
              typeString = "unknown(" + parTypeId + ")";
      } // end of switch(parTypeId)

      return(typeString);
   } // end of getTypeAsString()
   
   
   public String getXMLTag()
   {
      return(XML_TAG_);
   } // end of getXMLTag()
   
} // end of class SupportedColumnTypesMap
