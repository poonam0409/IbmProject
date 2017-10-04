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

import com.ibm.is.sappack.gen.common.ODBCStageData;
import com.ibm.is.sappack.gen.common.PersistenceData;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.util.ObjectParamMap;


public abstract class ODBCStageProperties extends StageProperties
{
   // -------------------------------------------------------------------------------------
   //                                         Constants
   // -------------------------------------------------------------------------------------
   // ************************** ODBC Stage Property Keys **********************************************
   private static final String FEATURE_DISABLED            = "0"; 
   private static final String FEATURE_ENABLED             = "1"; 
   private static final String GEN_SQL_NO                  = FEATURE_DISABLED;	
   private static final String GEN_SQL_YES                 = FEATURE_ENABLED; 
   private static final String FAIL_ON_MIMATCH_NO          = FEATURE_DISABLED; 
   private static final String FAIL_ON_MIMATCH_YES         = FEATURE_ENABLED; 
   public  static final String PROP_XML_PROPERTIES_KEY     = "XMLProperties";
   public  static final String STAGE_TYPE_CLASS_NAME       = StageProperties.STAGE_TYPE_CLASS_NAME_CUSTOM_STAGE;
   private static final String SIZE_INSERT_THEN_UPDATE     = "1";
   private static final String CP_DEFAULT                  = "0"; 
   private static final String CP_UNICODE                  = "1"; 
   private static final String CP_USER_SPECIFIED           = "2"; 
   
	private static final String PROP_SRC_XML_PROP_TEMPLATE  = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" + //$NON-NLS-1$
                                                             "<Properties version=\"1.1\">" +  //$NON-NLS-1$
                                                             "<Common>" +  //$NON-NLS-1$
                                                             "<Context type=\"int\">1</Context>"+  //$NON-NLS-1$
                                                             "<Variant type=\"string\">3.5</Variant>" +  //$NON-NLS-1$
                                                             "<DescriptorVersion type=\"string\">1.0</DescriptorVersion>"+  //$NON-NLS-1$
                                                             "<PartitionType type=\"int\">-1</PartitionType>" +  //$NON-NLS-1$
                                                             "</Common>" +  //$NON-NLS-1$
                                                             "<Connection>" +  //$NON-NLS-1$
            /* param  0 = ODBC Data Source     */            "<DataSource modified=\"1\" type=\"string\"><![CDATA[{0}]]></DataSource>" +  //$NON-NLS-1$
            /* param  1 = user id              */            "<Username modified=\"1\" type=\"string\"><![CDATA[{1}]]></Username>"+  //$NON-NLS-1$
            /* param  2 = (encrypted) password */            "<Password modified=\"1\" type=\"string\"><![CDATA[{2}]]></Password>" +  //$NON-NLS-1$
                                                             "</Connection>" +  //$NON-NLS-1$
                                                             "<Usage>"+  //$NON-NLS-1$
            /* param  3 = generate SQL flag    */            "<GenerateSQL modified=\"1\" type=\"bool\"><![CDATA[{3}]]></GenerateSQL>" + //$NON-NLS-1$ 
            /* param  4 = table name           */            "<TableName modified=\"1\" type=\"string\"><![CDATA[{4}]]></TableName>" + //$NON-NLS-1$ 
            /* param  5 = quoted Ids           */            "<EnableQuotedIDs type=\"bool\"><![CDATA[{5}]]></EnableQuotedIDs>" +          //$NON-NLS-1$
            /* param  6 = SQL string           */            "{6}" + //$NON-NLS-1$            												 	
                                                             "<Transaction>"+  //$NON-NLS-1$
            /* param  7 = Record count         */            "<RecordCount type=\"int\"><![CDATA[{7}]]></RecordCount>" +  //$NON-NLS-1$
                                                             "<EndOfWave collapsed=\"1\" type=\"int\"><![CDATA[0]]></EndOfWave>"+  //$NON-NLS-1$
                                                             "</Transaction>" +  //$NON-NLS-1$
                                                             "<Session>" +  //$NON-NLS-1$
                                                             "<IsolationLevel type=\"int\"><![CDATA[1]]></IsolationLevel>"+  //$NON-NLS-1$
            /* param  8 = Auto commit          */            "<AutocommitMode type=\"int\"><![CDATA[{8}]]></AutocommitMode>" +  //$NON-NLS-1$
            /* param  9 = Array size           */            "<ArraySize type=\"int\"><![CDATA[{9}]]></ArraySize>" +  //$NON-NLS-1$
                                                             "<SchemaReconciliation>" +  //$NON-NLS-1$
            /* param 10 = FailOnSizeMismatch   */            "<FailOnSizeMismatch type=\"bool\"><![CDATA[{10}]]></FailOnSizeMismatch>"+  //$NON-NLS-1$
            /* param 11 = FailOnTypeMismatch   */            "<FailOnTypeMismatch type=\"bool\"><![CDATA[{11}]]></FailOnTypeMismatch>" +  //$NON-NLS-1$
                                                             "</SchemaReconciliation>"+  //$NON-NLS-1$
                                                             "<PassLobLocator collapsed=\"1\" type=\"bool\"><![CDATA[0]]></PassLobLocator>"+  //$NON-NLS-1$
            /* param 12 = CodePage             */            "{12}" + //$NON-NLS-1$
                                                             "</Session>"+  //$NON-NLS-1$
                                                             "<BeforeAfter collapsed=\"1\" type=\"bool\"><![CDATA[0]]></BeforeAfter>" +  //$NON-NLS-1$
                                                             "</Usage>" +  //$NON-NLS-1$
                                                             "</Properties>"; //$NON-NLS-1$
   private static final String PROP_SRC_T_XML_PROP_DEF_SQL  = "<SQL>"+  //$NON-NLS-1$                
                                                              "<EnablePartitioning collapsed=\"1\" type=\"bool\"><![CDATA[0]]></EnablePartitioning>" +  //$NON-NLS-1$
                                                              "</SQL>";
   private static final String PROP_TRG_1_XML_PROP_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-16\"?><Properties version=\"1.1\">" + //$NON-NLS-1$
   		                                                     "<Common>" +                                                                //$NON-NLS-1$
   		                                                     "<Context type=\"int\">2</Context>" +                                       //$NON-NLS-1$
   		                                                     "<Variant type=\"string\">3.5</Variant>" +                                  //$NON-NLS-1$
   		                                                     "<DescriptorVersion type=\"string\">1.0</DescriptorVersion>" +              //$NON-NLS-1$
   		                                                     "<PartitionType type=\"int\">-1</PartitionType>" +                          //$NON-NLS-1$
   		                                                     "</Common>" +      	                                                         //$NON-NLS-1$
   		                                                     "<Connection>" +                                                            //$NON-NLS-1$
             /* param 0 = ODBC Data Source     */             "<DataSource modified=\"1\" type=\"string\"><![CDATA[{0}]]></DataSource>" + //$NON-NLS-1$
             /* param 1 = user id              */             "<Username modified=\"1\" type=\"string\"><![CDATA[{1}]]></Username>"+      //$NON-NLS-1$
             /* param 2 = (encrypted) password */             "<Password modified=\"1\" type=\"string\"><![CDATA[{2}]]></Password>" +     //$NON-NLS-1$
   		                                                     "</Connection>" +                                                           //$NON-NLS-1$
   		                                                     "<Usage>" +                                                                 //$NON-NLS-1$
             /* param 3 = Write Mode           */             "{3}" +                                                                     //$NON-NLS-1$
   		                                                     "<GenerateSQL modified=\"1\" type=\"bool\"><![CDATA[1]]></GenerateSQL>" +   //$NON-NLS-1$
             /* param 4 = table name           */             "<TableName modified=\"1\" type=\"string\"><![CDATA[{4}]]></TableName>" +   //$NON-NLS-1$
             /* param 5 = quoted Ids           */             "<EnableQuotedIDs type=\"bool\"><![CDATA[{5}]]></EnableQuotedIDs>" +          //$NON-NLS-1$
   		                                                     "<SQL></SQL>";                                                              //$NON-NLS-1$
   private static final String PROP_TRG_T_XML_TACT_DEFAULT  = "<TableAction collapsed=\"1\" type=\"int\"><![CDATA[0]]></TableAction>";
   private static final String PROP_TRG_T_XML_TACT_CREATE   = "<TableAction modified=\"1\" type=\"int\"><![CDATA[1]]><GenerateCreateStatement type=\"bool\"><![CDATA[1]]><FailOnError type=\"bool\"><![CDATA[1]]></FailOnError></GenerateCreateStatement></TableAction>";   
   private static final String PROP_TRG_T_XML_TACT_REPLACE  = "<TableAction modified=\"1\" type=\"int\"><![CDATA[2]]><GenerateCreateStatement type=\"bool\"><![CDATA[1]]><FailOnError type=\"bool\"><![CDATA[1]]></FailOnError></GenerateCreateStatement><GenerateDropStatement type=\"bool\"><![CDATA[1]]><FailOnError type=\"bool\"><![CDATA[0]]></FailOnError></GenerateDropStatement></TableAction>";   
   private static final String PROP_TRG_T_XML_TACT_TRUNCATE = "<TableAction modified=\"1\" type=\"int\"><![CDATA[3]]><GenerateTruncateStatement type=\"bool\"><![CDATA[1]]><FailOnError type=\"bool\"><![CDATA[1]]></FailOnError></GenerateTruncateStatement></TableAction>";
   private static final String PROP_TRG_W0_XML_PROP_TEMPLATE = "<WriteMode type=\"int\"><![CDATA[{0}]]></WriteMode>";//$NON-NLS-1$;
   private static final String PROP_TRG_W1_XML_PROP_TEMPLATE = "<WriteMode modified=\"1\" type=\"int\"><![CDATA[{0}]]></WriteMode>"; 
   private static final String PROP_TRG_2_XML_PROP_TEMPLATE = "<Transaction>" + //$NON-NLS-1$
            /* param 0 = Record Count         */              "<RecordCount type=\"int\"><![CDATA[{0}]]></RecordCount>" + //$NON-NLS-1$
                                                              "</Transaction>" + //$NON-NLS-1$
                                                              "<Session>" + //$NON-NLS-1$
                                                              "<IsolationLevel type=\"int\"><![CDATA[1]]></IsolationLevel>" + //$NON-NLS-1$
            /* param 1 = AutoCommit           */              "<AutocommitMode type=\"int\"><![CDATA[{1}]]></AutocommitMode>" + //$NON-NLS-1$
            /* param 2 = Array Size           */              "<ArraySize type=\"int\"><![CDATA[{2}]]></ArraySize>" + //$NON-NLS-1$
                                                              "<SchemaReconciliation>" + //$NON-NLS-1$
            /* param 3 = FailOnTypeMismatch   */              "<FailOnSizeMismatch type=\"bool\"><![CDATA[{3}]]></FailOnSizeMismatch>" + //$NON-NLS-1$
            /* param 4 = FailOnTypeMismatch   */              "<FailOnTypeMismatch type=\"bool\"><![CDATA[{4}]]></FailOnTypeMismatch>" + //$NON-NLS-1$
                                                              "<FailOnCodePageMismatch type=\"bool\"><![CDATA[0]]></FailOnCodePageMismatch>" + //$NON-NLS-1$
                                                              "<DropUnmatchedFields type=\"bool\"><![CDATA[1]]></DropUnmatchedFields>" + //$NON-NLS-1$
                                                              "</SchemaReconciliation>" + //$NON-NLS-1$
            /* param 5 = CodePage             */              "{5}" + //$NON-NLS-1$
                                                              "</Session>" + //$NON-NLS-1$
                                                              "<BeforeAfter collapsed=\"1\" type=\"bool\"><![CDATA[0]]></BeforeAfter>" + //$NON-NLS-1$
                                                              "</Usage></Properties>"; //$NON-NLS-1$
   private static final String PROP_XML_CODEPAGE            = "<CodePage type=\"int\"><![CDATA[{0}]]>{1}</CodePage>"; //$NON-NLS-1$
   private static final String PROP_XML_CODEPAGE_NAME       = "<CodePageName type=\"string\"><![CDATA[{0}]]></CodePageName>"; //$NON-NLS-1$                
                                                              

   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private static final Map<String,String>  _DefaultStageParamsMap;
   
   
   static String copyright()
   { return com.ibm.is.sappack.gen.server.jobgenerator.stages.Copyright.IBM_COPYRIGHT_SHORT; }   
   
   // -------------------------------------------------------------------------------------
   //                                 Static Block
   // -------------------------------------------------------------------------------------
   static
   {
      // ------------------------------------------------------
      //            Stage Parameters
      // ------------------------------------------------------
      _DefaultStageParamsMap = new HashMap<String,String>();
      _DefaultStageParamsMap.put("VariantName", "3.5");
      _DefaultStageParamsMap.put("VariantLibrary", "ccodbc");
      _DefaultStageParamsMap.put("VariantVersion", "1.0");
      _DefaultStageParamsMap.put("SupportedVariants", "3.5");
      _DefaultStageParamsMap.put("SupportedVariantsLibraries", "ccodbc");
      _DefaultStageParamsMap.put("SupportedVariantsVersions", "1.0");
      _DefaultStageParamsMap.put("RejectThreshold", "0");
      _DefaultStageParamsMap.put("RejectNumber", "0");
      _DefaultStageParamsMap.put("RejectUsesPercentage", "false");
      _DefaultStageParamsMap.put("ConnectorName", "ODBCConnector");
      _DefaultStageParamsMap.put("Engine", "PX");
      _DefaultStageParamsMap.put("Context", "");
      _DefaultStageParamsMap.put("ConnectionString", "/Connection/DataSource");
      _DefaultStageParamsMap.put("Username", "/Connection/Username");
      _DefaultStageParamsMap.put("Password", "/Connection/Password");
   } // end of static

   
   public static ObjectParamMap getDefaultStageParams()
   {
      return(new ObjectParamMap(_DefaultStageParamsMap, DataStageObjectFactory.STAGE_TYPE_DEFAULT));
   }

   
   public static String getSourceStageXMLProp(ODBCStageData odbcData, String sqlStatement,
                                              String tableName)
   {
      String newXMLProps;
      String odbcDataSource;
      String odbcUserId;
      String odbcPassword;
      String autoCommit;
      String failOnSizeMismatch;
      String failOnTypeMismatch;
      String quotedIDsEnabled;
      String codePageXML;
      String codePageName;
      String codePageId;
      
      
      // we cannot cope with 'null' values ...
      // ==> if needed, convert them to empty strings
      odbcDataSource = convertNullToEmpty(odbcData.getDataSourceName());
      odbcUserId     = convertNullToEmpty(odbcData.getUserName());
      odbcPassword   = convertNullToEmpty(odbcData.getPassword());
      tableName      = convertNullToEmpty(tableName);

      String generateSQL = null;
      if (sqlStatement == null)
      {
         sqlStatement = PROP_SRC_T_XML_PROP_DEF_SQL;
         generateSQL = GEN_SQL_YES;
      }
      else 
      {
         generateSQL = GEN_SQL_NO;    	  
      }

      if (odbcData.isAutoCommit())
      {
         autoCommit = FEATURE_ENABLED;
      }
      else
      {
      	autoCommit = FEATURE_DISABLED;
      } // end of if (parODBCData.isAutoCommit())


      if (odbcData.isFailOnSizeMismatch())
      {
         failOnSizeMismatch = FAIL_ON_MIMATCH_YES;
      }
      else
      {
         failOnSizeMismatch = FAIL_ON_MIMATCH_NO;
      }

      if (odbcData.isFailOnTypeMismatch())
      {
         failOnTypeMismatch = FAIL_ON_MIMATCH_YES;
      }
      else
      {
         failOnTypeMismatch = FAIL_ON_MIMATCH_NO;
      }

      if (odbcData.isQuotedIDsEnabled()) {
         quotedIDsEnabled = FEATURE_ENABLED;
      }
      else {
         quotedIDsEnabled = FEATURE_DISABLED;
      }

      // determine the code page XML to be used
      switch(odbcData.getCodePageId()) {
         case PersistenceData.CODE_PAGE_UNICODE:
              codePageId   = CP_UNICODE;
              codePageName = "";
              break;

         case PersistenceData.CODE_PAGE_USER_SPECIFIED:
              codePageId   = CP_USER_SPECIFIED;
              codePageName = MessageFormat.format(PROP_XML_CODEPAGE_NAME, 
                                                  new Object[] { String.valueOf(odbcData.getCodePageName()) } );
              break;
             
         default:
              codePageId   = CP_DEFAULT;
              codePageName = "";
      }
      codePageXML = MessageFormat.format(PROP_XML_CODEPAGE, new Object[] { codePageId, codePageName } );

      newXMLProps = MessageFormat.format(PROP_SRC_XML_PROP_TEMPLATE, new Object[] { odbcDataSource, 
                                                                                    odbcUserId, 
                                                                                    odbcPassword, 
                                                                                    generateSQL,
                                                                                    tableName,
                                                                                    quotedIDsEnabled,
                                                                                    sqlStatement,
                                                                                    odbcData.getRecordCount(),
                                                                                    autoCommit,
                                                                                    odbcData.getArraySize(),
                                                                                    failOnSizeMismatch,
                                                                                    failOnTypeMismatch,
                                                                                    codePageXML} );

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "ODBC xml props = " + newXMLProps);
		}

      return(newXMLProps);
   } // end of getSourceStageXMLProp()

   
   public static String getTargetStageXMLProp(ODBCStageData parODBCData, String parTableName)
   {
      StringBuffer newXMLPropsBuf = new StringBuffer();
      String       odbcDataSource;
      String       odbcUserId;
      String       odbcPassword;
      String       arraySize;
      String       autoCommit;
      String       recordCount;
      String       failOnSizeMismatch;
      String       failOnTypeMismatch;
      String       xmlProps;
      String       quotedIDsEnabled;
      String       codePageXML;
      String       codePageName;
      String       codePageId;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "table = " + parTableName + " - write mode = " + parODBCData.getWriteMode());
		}

      // we cannot cope with 'null' values ...
      // ==> if needed, convert them to empty strings
      odbcDataSource = convertNullToEmpty(parODBCData.getDataSourceName());
      odbcUserId     = convertNullToEmpty(parODBCData.getUserName());
      odbcPassword   = convertNullToEmpty(parODBCData.getPassword());
      parTableName   = convertNullToEmpty(parTableName);
      
      if (parODBCData.isQuotedIDsEnabled()) {
         quotedIDsEnabled = FEATURE_ENABLED;
      }
      else {
         quotedIDsEnabled = FEATURE_DISABLED;
      }
      
      newXMLPropsBuf.append(MessageFormat.format(PROP_TRG_1_XML_PROP_TEMPLATE, new Object[] {odbcDataSource, 
                                                                                             odbcUserId, 
                                                                                             odbcPassword,
                                                                                             getWriteModeXML(parODBCData),
                                                                                             parTableName,
                                                                                             quotedIDsEnabled})) ;
      
      // determine the INSERT mode to be set 
      // NOTE: FOR WRITE MODE INSER ONLY
      if (parODBCData.getWriteMode() == ODBCStageData.WRITE_MODE_INSERT)
      {
         switch(parODBCData.getInsertMode())
         {
            case ODBCStageData.INSERT_MODE_CREATE:
                 newXMLPropsBuf.append(PROP_TRG_T_XML_TACT_CREATE);
                 break;
               
            case ODBCStageData.INSERT_MODE_REPLACE:
                 newXMLPropsBuf.append(PROP_TRG_T_XML_TACT_REPLACE);
                 break;
               
            case ODBCStageData.INSERT_MODE_TRUNCATE:
                 newXMLPropsBuf.append(PROP_TRG_T_XML_TACT_TRUNCATE);
                 break;
                 
            case ODBCStageData.INSERT_MODE_APPEND:
            default:
                 newXMLPropsBuf.append(PROP_TRG_T_XML_TACT_DEFAULT);
         } // end of switch(parODBCData.getInsertMode())
      } // end of if (parODBCData.getWriteMode() == ODBCStageData.WRITE_MODE_INSERT)

      if (parODBCData.isAutoCommit())
      {
         autoCommit = FEATURE_ENABLED;
      }
      else
      {
      	autoCommit = FEATURE_DISABLED;
      } // end of if (parODBCData.isAutoCommit())

      if (parODBCData.isFailOnSizeMismatch())
      {
         failOnSizeMismatch = FAIL_ON_MIMATCH_YES;
      }
      else
      {
         failOnSizeMismatch = FAIL_ON_MIMATCH_NO;
      } // end of if (parODBCData.isFailOnTypeMismatch())
      
      if (parODBCData.isFailOnTypeMismatch())
      {
         failOnTypeMismatch = FAIL_ON_MIMATCH_YES;
      }
      else
      {
         failOnTypeMismatch = FAIL_ON_MIMATCH_NO;
      } // end of if (parODBCData.isFailOnTypeMismatch())
      
      // determine the code page XML to be used
      switch(parODBCData.getCodePageId()) {
         case PersistenceData.CODE_PAGE_UNICODE:
              codePageId   = CP_UNICODE;
              codePageName = "";
              break;
              
         case PersistenceData.CODE_PAGE_USER_SPECIFIED:
              codePageId   = CP_USER_SPECIFIED;
              codePageName = MessageFormat.format(PROP_XML_CODEPAGE_NAME, 
                                                  new Object[] { String.valueOf(parODBCData.getCodePageName()) } );
              break;
              
         default:
              codePageId   = CP_DEFAULT;
              codePageName = "";
      }
      codePageXML = MessageFormat.format(PROP_XML_CODEPAGE, new Object[] { codePageId, codePageName } );
      arraySize   = parODBCData.getArraySize();
      recordCount = parODBCData.getRecordCount();

      // modify ARRAY SIZE and RECORD COUNT if it's a particular WRITE MODE
      if (parODBCData.getWriteMode() == ODBCStageData.WRITE_MODE_UPDATE_THEN_INSERT || 
          parODBCData.getWriteMode() == ODBCStageData.WRITE_MODE_INSERT_THEN_UPDATE) {
     		arraySize   = SIZE_INSERT_THEN_UPDATE;
     		recordCount = SIZE_INSERT_THEN_UPDATE;

   		if (TraceLogger.isTraceEnabled()) {
   			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Dedicated values for ARRAY SIZE and RECORD COUNT !!");
   		}
      } // end of if (parODBCData.getWriteMode() == ODBCStageData.WRITE_MODE_UPDATE_ ... INSERT_THEN_UPDATE)

      xmlProps = MessageFormat.format(PROP_TRG_2_XML_PROP_TEMPLATE, new Object[] { recordCount,
                                                                                   autoCommit,
                                                                                   arraySize,
                                                                                   failOnSizeMismatch,
                                                                                   failOnTypeMismatch,
                                                                                   codePageXML } );

      newXMLPropsBuf.append(xmlProps);

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "ODBC xml props = " + newXMLPropsBuf.toString());
		}

      return(newXMLPropsBuf.toString());
   } // end of getTargetStageXMLProp()


   private static String getWriteModeXML(ODBCStageData parODBCData)
   {
      String retWriteModeXML;

      switch(parODBCData.getWriteMode())
      {
         case ODBCStageData.WRITE_MODE_DELETE:
              retWriteModeXML = MessageFormat.format(PROP_TRG_W1_XML_PROP_TEMPLATE, new Object[] { "2" }) ;
              break;

         case ODBCStageData.WRITE_MODE_DELETE_THEN_INSERT:
              retWriteModeXML = MessageFormat.format(PROP_TRG_W1_XML_PROP_TEMPLATE, new Object[] { "5" }) ;
              break;

         case ODBCStageData.WRITE_MODE_INSERT_THEN_UPDATE:
              retWriteModeXML = MessageFormat.format(PROP_TRG_W1_XML_PROP_TEMPLATE, new Object[] { "3" }) ;
              break;

         case ODBCStageData.WRITE_MODE_UPDATE:
              retWriteModeXML = MessageFormat.format(PROP_TRG_W1_XML_PROP_TEMPLATE, new Object[] { "1" }) ;
              break;

         case ODBCStageData.WRITE_MODE_UPDATE_THEN_INSERT:
              retWriteModeXML = MessageFormat.format(PROP_TRG_W1_XML_PROP_TEMPLATE, new Object[] { "4" }) ;
              break;

         case ODBCStageData.WRITE_MODE_INSERT:
         default:
              retWriteModeXML = MessageFormat.format(PROP_TRG_W0_XML_PROP_TEMPLATE, new Object[] { "0" }) ;
      } // end of switch(parODBCData.getWriteMode())

      return(retWriteModeXML);
   } // end of getWriteModeXML()
   
} // end of class ODBCStageProperties 
