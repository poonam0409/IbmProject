//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.server.jobgenerator.stages
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.jobgenerator.stages;


import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.ibm.is.sappack.gen.common.FileStageData;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.util.StageData;


public abstract class FileStageProperties
{
   // -------------------------------------------------------------------------------------
   //                                         Constants
   // -------------------------------------------------------------------------------------
   private   static final String FILENAME_REPLCMT_SCHEMA_TEMPLATE    = "{schema}";
   private   static final String FILENAME_REPLCMT_TABLE_TEMPLATE     = "{tab}";
   private   static final String METABAG_VALUE_NONE                  = "none";
   private   static final String METABAG_VALUE_SINGLE                = "single";
   private   static final String METABAG_VALUE_DOUBLE                = "double";
   private   static final String METABAG_VALUE_END                   = "end";
   private   static final String METABAG_VALUE_COMMA                 = "','";
   private   static final String METABAG_VALUE_TAB                   = "'\\t'";
   private   static final String METABAG_VALUE_NULL                  = "null";
   private   static final String METABAG_VALUE_WHITESPACE            = "ws";
   private   static final String METABAG_UDEF_VALUE_TEMPLATE         = "''{0}''";
   private   static final String METABAG_DELIM_QUOUTE_TEMPLATE       = "final_delim={0}, delim={1}, quote={2}, ";
   private   static final String METABAG_DELIM_QUOUTE_TEMPLATE_NF    = "final_delim={0}, delim={1}, null_field={2}, quote={3}, ";
   public    static final String STAGE_TYPE_CLASS_NAME               = StageProperties.STAGE_TYPE_CLASS_NAME_CUSTOM_STAGE;

   // ************************** File Stage Property Keys **********************************************
   public    static final String PROP_1ST_LINE_COLUMN_NAME_KEY       = "firstLineColumnNames";   
   public    static final String PROP_1ST_LINE_COLUMN_NAME_SET       = "firstLineColumnNames";
   // osuhre, 46979: false value is a blank
   public    static final String PROP_1ST_LINE_COLUMN_NAME_NOT_SET   = " ";   


   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private static final Map<String,String>  _BaseLinkParamsMap;


   // -------------------------------------------------------------------------------------
   //                                 Static Block
   // -------------------------------------------------------------------------------------
   static
   {
      // ------------------------------------------------------
      //            Stage Parameters
      // ------------------------------------------------------
      _BaseLinkParamsMap = new HashMap<String, String>();
      
      _BaseLinkParamsMap.put("schema", null);  
//    _BaseLinkParamsMap.put("nocleanup", "True"); // "nocleanup");
      _BaseLinkParamsMap.put("nocleanup", " ");  // equals 'True'
      _BaseLinkParamsMap.put("rejects", "continue"); // "nocleanup");
      _BaseLinkParamsMap.put(PROP_1ST_LINE_COLUMN_NAME_KEY, PROP_1ST_LINE_COLUMN_NAME_NOT_SET);
   } // end of static

   
   static String copyright()
   { 
   	return com.ibm.is.sappack.gen.server.jobgenerator.stages.Copyright.IBM_COPYRIGHT_SHORT; 
   }   
   

   protected static Map<String,String> getBaseLinkParams()
   {
      return(new HashMap<String,String>(_BaseLinkParamsMap));
   }

   
   static String determineFilename(String parSchemaName, String parTableName,
                                   FileStageData parFileData)
   {
      String retFilename;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("filename template = " + parFileData.getFilenamePrefix());
      }
      
      parTableName = StringUtils.cleanFieldName(parTableName);
      
      // complete the filename ....
      // ==> check if there are some placeholders inside
      if (parFileData.getFilenamePrefix().indexOf(FILENAME_REPLCMT_SCHEMA_TEMPLATE) > -1 ||
          parFileData.getFilenamePrefix().indexOf(FILENAME_REPLCMT_TABLE_TEMPLATE)  > -1)
      {
         retFilename = StringUtils.replaceString(parFileData.getFilenamePrefix(),
                                                 FILENAME_REPLCMT_SCHEMA_TEMPLATE, parSchemaName);
         retFilename = StringUtils.replaceString(retFilename, FILENAME_REPLCMT_TABLE_TEMPLATE,
                                                 parTableName);
      }
      else
      {
         // no replacement required
         retFilename = parFileData.getFilenamePrefix() + parTableName;
      }

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("filename = " + retFilename);
      }
      
      return(retFilename);
   } // end of determineFilename()
   
   
   static void setDelimsAndQuote(StageData parFileStage, FileStageData parFileData) 
   {
      String mbValue;
      String finalDelim;
      String delim;
      String nullFieldValue;
      String quote;

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }
      
      // determine the MetaBag value to be set
      //      -- delimiter --
      switch(parFileData.getDelimiter())
      {
         case FileStageData.DELIMITER_END:
              delim = METABAG_VALUE_END;
              break;
         case FileStageData.DELIMITER_NONE:
              delim = METABAG_VALUE_NONE;
              break;
         case FileStageData.DELIMITER_NULL:
              delim = METABAG_VALUE_NULL;
              break;
         case FileStageData.DELIMITER_TAB:
              delim = METABAG_VALUE_TAB;
              break;
         case FileStageData.DELIMITER_USERFEF:
              delim = MessageFormat.format(METABAG_UDEF_VALUE_TEMPLATE, 
                                           new Object[] { String.valueOf(parFileData.getDelimiterChar()) } );
              break;
         case FileStageData.DELIMITER_WHITESPACE:
              delim = METABAG_VALUE_WHITESPACE;
              break;
         case FileStageData.DELIMITER_COMMA:
         default:
              delim = METABAG_VALUE_COMMA;
              break;
      } // end of switch(parFileData.getDelimiter())
      
      //      -- final delimiter --
      switch(parFileData.getFinalDelimiter())
      {
         case FileStageData.DELIMITER_END:
              finalDelim = METABAG_VALUE_END;
              break;
         case FileStageData.DELIMITER_NONE:
              finalDelim = METABAG_VALUE_NONE;
              break;
         case FileStageData.DELIMITER_NULL:
              finalDelim = METABAG_VALUE_NULL;
              break;
         case FileStageData.DELIMITER_TAB:
              finalDelim = METABAG_VALUE_TAB;
              break;
         case FileStageData.DELIMITER_USERFEF:
              finalDelim = MessageFormat.format(METABAG_UDEF_VALUE_TEMPLATE, 
                                                new Object[] { String.valueOf(parFileData.getDelimiterChar()) } );
              break;
         case FileStageData.DELIMITER_WHITESPACE:
              finalDelim = METABAG_VALUE_WHITESPACE;
              break;
         case FileStageData.DELIMITER_COMMA:
         default:
              finalDelim = METABAG_VALUE_COMMA;
              break;
      } // end of switch(parFileData.getDelimiter())
      
      //      -- quote --
      switch(parFileData.getQuote())
      {
         case FileStageData.QUOTE_USERFEF:
              quote = MessageFormat.format(METABAG_UDEF_VALUE_TEMPLATE, 
                                           new Object[] { parFileData.getQuoteString() } );
              break;
       
         case FileStageData.QUOTE_NONE:
              quote = METABAG_VALUE_NONE;
              break;
     
         case FileStageData.QUOTE_SINGLE:
              quote = METABAG_VALUE_SINGLE;
              break;
            
         case FileStageData.QUOTE_DOUBLE:
         default:
              quote = METABAG_VALUE_DOUBLE;
              break;
      } // end of switch(parFileData.getQuote())

      nullFieldValue =  parFileData.getNullFieldString();
      if (nullFieldValue == null)
      {
         mbValue = MessageFormat.format(METABAG_DELIM_QUOUTE_TEMPLATE, 
                                        new Object[] { finalDelim, delim, quote } );
      }
      else
      {
         nullFieldValue = MessageFormat.format(METABAG_UDEF_VALUE_TEMPLATE, 
                                               new Object[] { nullFieldValue } );
         mbValue        = MessageFormat.format(METABAG_DELIM_QUOUTE_TEMPLATE_NF, 
                                               new Object[] { finalDelim, delim, nullFieldValue, quote } );
      }
      
      parFileStage.getMetaBagList().add(new StageData.MetaBagData(DataStageObjectFactory.METABAG_OWNER_NAME,
                                                                  DataStageObjectFactory.METABAG_SCHEMA_FORMAT_NAME,
                                                                  mbValue));

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("MetaBagList = " + parFileStage.getMetaBagList());
      }
   } // end of setDelimsAndQuote()
   
} // end of class FileStageProperties 
