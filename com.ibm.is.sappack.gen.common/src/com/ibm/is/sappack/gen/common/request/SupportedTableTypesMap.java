//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2013                                              
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


final public class SupportedTableTypesMap extends SupportedTypesBaseMap
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public  static final String XML_TAG                                      = "TableType";     //$NON-NLS-1$
   
   public  final static int     TABLE_TYPE_INT_ALL_TYPES                    = -100;
   public  final static Integer TABLE_TYPE_ENUM_ALL_TYPES                   = new Integer(TABLE_TYPE_INT_ALL_TYPES);
   public  final static int     TABLE_TYPE_INT_UNKOWN                       = 100;
   public  final static Integer TABLE_TYPE_ENUM_UNKNOWN                     = new Integer(TABLE_TYPE_INT_UNKOWN);
   public  final static int     TABLE_TYPE_INT_LOGICAL_TABLE                = 101;
   public  final static Integer TABLE_TYPE_ENUM_LOGICAL_TABLE               = new Integer(TABLE_TYPE_INT_LOGICAL_TABLE);
   public  final static int     TABLE_TYPE_INT_IDOC_TYPE_ALL                = 102;
   public  final static Integer TABLE_TYPE_ENUM_IDOC_TYPE_ALL               = new Integer(TABLE_TYPE_INT_IDOC_TYPE_ALL);
   public  final static int     TABLE_TYPE_INT_IDOC_EXTRACT_TYPE            = 103;
   public  final static Integer TABLE_TYPE_ENUM_IDOC_TYPE_EXTRACT           = new Integer(TABLE_TYPE_INT_IDOC_EXTRACT_TYPE);
   public  final static int     TABLE_TYPE_INT_IDOC_LOAD_TYPE               = 104;
   public  final static Integer TABLE_TYPE_ENUM_IDOC_TYPE_LOAD              = new Integer(TABLE_TYPE_INT_IDOC_LOAD_TYPE);
   public  final static int     TABLE_TYPE_INT_IDOC_LOAD_STATUS             = 105;
   public  final static Integer TABLE_TYPE_ENUM_IDOC_LOAD_STATUS            = new Integer(TABLE_TYPE_INT_IDOC_LOAD_STATUS);
   public  final static int     TABLE_TYPE_INT_JOINED_CHECK_AND_TEXT_TABLE  = 106;
   public  final static Integer TABLE_TYPE_ENUM_JOINED_CHECK_AND_TEXT_TABLE = new Integer(TABLE_TYPE_INT_JOINED_CHECK_AND_TEXT_TABLE);
   public  final static int     TABLE_TYPE_INT_REFERENCE_CHECK_TABLE  	    = 107;
   public  final static Integer TABLE_TYPE_ENUM_REFERENCE_CHECK_TABLE       = new Integer(TABLE_TYPE_INT_REFERENCE_CHECK_TABLE);
   public  final static int     TABLE_TYPE_INT_REFERENCE_TEXT_TABLE  	    = 108;
   public  final static Integer TABLE_TYPE_ENUM_REFERENCE_TEXT_TABLE        = new Integer(TABLE_TYPE_INT_REFERENCE_TEXT_TABLE);
   public  final static int     TABLE_TYPE_INT_TRANSLATION_TABLE            = 109;
   public  final static Integer TABLE_TYPE_ENUM_TRANSLATION_TABLE           = new Integer(TABLE_TYPE_INT_TRANSLATION_TABLE);
   public  final static int     TABLE_TYPE_INT_NON_REFERENCE_CHECK_TABLE  	 = 110;
   public  final static Integer TABLE_TYPE_ENUM_NON_REFERENCE_CHECK_TABLE   = new Integer(TABLE_TYPE_INT_NON_REFERENCE_CHECK_TABLE);
   public  final static int     TABLE_TYPE_INT_DOMAIN_TRANSLATION_TABLE 	 = 111;
   public  final static Integer TABLE_TYPE_ENUM_DOMAIN_TRANSLATION_TABLE    = new Integer(TABLE_TYPE_INT_DOMAIN_TRANSLATION_TABLE);
   public  final static int     TABLE_TYPE_INT_DOMAIN_TABLE 	    		    = 112;
   public  final static Integer TABLE_TYPE_ENUM_DOMAIN_TABLE       			 = new Integer(TABLE_TYPE_INT_DOMAIN_TABLE);
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------

   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; 
   }
   

   public SupportedTableTypesMap()
   {
      super();
   } // end of SupportedColumnTypesMap()
   
   
   public SupportedTableTypesMap(SupportedTableTypesMap instance)
   {
      super(instance);
   } // end of SupportedColumnTypesMap()
   
   
   SupportedTableTypesMap(Node parTypesNode)
   {
      super(parTypesNode);
   } // end of SupportedColumnTypesMap()
   
   
   void checkType(Integer parType) throws IllegalArgumentException
   {
      if (parType == null)
      {
         throw new IllegalArgumentException("Table Type must not be null");
      }
      else
      {
         switch(parType.intValue())
         {
                 // Supported Table Types
            case TABLE_TYPE_INT_ALL_TYPES: 
            case TABLE_TYPE_INT_JOINED_CHECK_AND_TEXT_TABLE: 
            case TABLE_TYPE_INT_REFERENCE_CHECK_TABLE:
            case TABLE_TYPE_INT_NON_REFERENCE_CHECK_TABLE:
            case TABLE_TYPE_INT_REFERENCE_TEXT_TABLE: 
            case TABLE_TYPE_INT_IDOC_TYPE_ALL: 
            case TABLE_TYPE_INT_IDOC_EXTRACT_TYPE: 
            case TABLE_TYPE_INT_IDOC_LOAD_TYPE: 
            case TABLE_TYPE_INT_IDOC_LOAD_STATUS: 
            case TABLE_TYPE_INT_LOGICAL_TABLE: 
                 break;
                      
            default:
                 throw new IllegalArgumentException("Table Type '" + parType.toString() + "' is not supported.");
         } // end of switch(parType.intValue())
      } // end of (else) if (parType == null)
   } // end of checkType()
      
   
   public static String getType(int parTypeId)
   {
      String typeString;
      
      switch(parTypeId)
      {
      	case TABLE_TYPE_INT_ALL_TYPES:
              typeString = Constants.DATA_OBJECT_SOURCE_TYPE_ALL;
      		  break;
         
         case TABLE_TYPE_INT_IDOC_TYPE_ALL:
              typeString = Constants.MODEL_PURPOSE_IDOC_ALL;
              break;
              
         case TABLE_TYPE_INT_IDOC_EXTRACT_TYPE:
              typeString = Constants.MODEL_PURPOSE_IDOC_EXTRACT;
              break;
            
         case TABLE_TYPE_INT_IDOC_LOAD_TYPE:
              typeString = Constants.MODEL_PURPOSE_IDOC_LOAD;
              break;
         
         case TABLE_TYPE_INT_JOINED_CHECK_AND_TEXT_TABLE:
              typeString = Constants.DATA_OBJECT_SOURCE_TYPE_JOINED_CHECK_AND_TEXT_TABLE;
              break;
         
         case TABLE_TYPE_INT_LOGICAL_TABLE:
              typeString = Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE;
              break;
         
         case TABLE_TYPE_INT_IDOC_LOAD_STATUS:
              typeString = Constants.DATA_OBJECT_SOURCE_TYPE_LOAD_STATUS;
              break;
         
         case TABLE_TYPE_INT_REFERENCE_CHECK_TABLE:
        	  typeString = Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_CHECK_TABLE;
        	  break;
        	  
         case TABLE_TYPE_INT_NON_REFERENCE_CHECK_TABLE:
        	 typeString = Constants.DATA_OBJECT_SOURCE_TYPE_NON_REFERENCE_CHECK_TABLE;
       	  	 break;
        	  
         case TABLE_TYPE_INT_REFERENCE_TEXT_TABLE:
        	 typeString = Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_TEXT_TABLE;
        	 break;
              
         default:
              typeString = "unknown";
      } // end of switch(parTypeId)
      
      return(typeString);
   } // end of getType()
   
   
   public String getTypeAsString(int parTypeId)
   {
      return(getType(parTypeId));
   } // end of getTypeAsString()
   
   
   public String getXMLTag()
   {
      return(XML_TAG);
   } // end of getXMLTag() 
   
} // end of class SupportedColumnTypesMap
