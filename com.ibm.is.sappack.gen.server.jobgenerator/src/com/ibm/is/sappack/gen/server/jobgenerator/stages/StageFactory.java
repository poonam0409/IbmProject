//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
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

import ASCLModel.MainObject;
import DataStageX.DSJobDef;
import DataStageX.DSMetaBag;
import DataStageX.DSStage;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.FileStageData;
import com.ibm.is.sappack.gen.common.NetezzaStageData;
import com.ibm.is.sappack.gen.common.ODBCStageData;
import com.ibm.is.sappack.gen.common.PersistenceData;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeIDocExtract;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeIDocLoad;
import com.ibm.is.sappack.gen.common.request.SAPSystemData;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.server.common.DSStageTypeEnum;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.util.ObjectParamMap;
import com.ibm.is.sappack.gen.server.util.StageData;
import com.ibm.is.sappack.gen.server.util.TableData;


public final class StageFactory
{
   // -------------------------------------------------------------------------------------
   // Constants
   // -------------------------------------------------------------------------------------
   private static final String ABAP_EXTRACT_STAGE_NAME_TEMPLATE  = "ABAP_Extract_{0}";
   private static final String IDOC_LOAD_STAGE_NAME_TEMPLATE     = "{0}_{1}";
   private static final String IDOC_EXTRACT_STAGE_NAME_TEMPLATE  = IDOC_LOAD_STAGE_NAME_TEMPLATE;
   private static final String DB_TABLE_NAME_TEMPLATE            = "{0}.{1}";
   private static final String DB_SQL_WHERE_REPLACEMENT          = "#WHERE#";
   private static final String DB_SQL_NO_WHERE_REPLACEMENT       = "WHERE #WHERE#";
   private static final String DB_SQL_SCHEMA_REPLACEMENT         = "#SCHEMA#";
   private static final String WHERE_CONDITION_TABLE_PLACEHOLDER = "<TABLE>";

   // -------------------------------------------------------------------------------------
   // Member Variables
   // -------------------------------------------------------------------------------------

   
   static String copyright()
   { 
   	return com.ibm.is.sappack.gen.server.jobgenerator.stages.Copyright.IBM_COPYRIGHT_SHORT; 
   }   

   
   public static StageData createABAPExtractStage(DSJobDef jobDef, String tableName, boolean isUnicodeCharSet,
                                                  DataStageObjectFactory dsFactory) 
                 throws DSAccessException
   {
      DSStage          newABAPStage;
      String           stageName;
      ObjectParamMap   abapExtractParamMap;

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry("table = " + tableName);
      }

      // create the parameter map for the ABAP stage
      abapExtractParamMap = ABAPExtractStageProperties.getDefaultStageParams();
      if (isUnicodeCharSet)
      {
         // UNICODE charset is used ...
         abapExtractParamMap.put(ABAPExtractStageProperties.CHAR_SET_KEY, Constants.DEFAULT_UNICODE_CHARSET);
      }

      // create the stage
      stageName    = MessageFormat.format(ABAP_EXTRACT_STAGE_NAME_TEMPLATE,
                                          new Object[] { tableName });
      newABAPStage = dsFactory.createDSStage(jobDef, stageName, DSStageTypeEnum.ABAP_EXTRACT_PX_LITERAL, 
                                             abapExtractParamMap);
      newABAPStage.setStageTypeClassName(ABAPExtractStageProperties.STAGE_TYPE_CLASS_NAME);
      newABAPStage.setShortDescription(Constants.JOB_GEN_SAP_STAGE_ID);

      // if UNICODE character set is used ...
      if (isUnicodeCharSet)
      {
         newABAPStage.setNLSMapName(Constants.DEFAULT_UNICODE_CHARSET);
      }

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.exit();
      }

      return (new StageData(newABAPStage));
   } // end of createABAPExtractStage()

   
   private static StageData createFileSourceStage(MainObject parDSContainerDef, String parSchemaName, 
                                                  String parTableName, FileStageData parFileData, 
                                                  DataStageObjectFactory parDSFactory) 
                  throws DSAccessException
   {
      StageData       retStageData;
      DSStage         newStage;
      ObjectParamMap  newLinkParamMap;
      ObjectParamMap  fileStageParamMap;
      String          stageName;
      String          filePropertyValue;
      String          firstLineIsColName;
      String          fileName;
      String          nlsMapName;

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry("schema = " + parSchemaName + " - table = " + parTableName);
      }

      // setup the parameter values for the stage ...
      stageName = MessageFormat.format(DB_TABLE_NAME_TEMPLATE, new Object[] { parSchemaName, 
                                                                              parTableName });

      // setup NLS mapping for new file stage ...
      fileStageParamMap = null;
      switch(parFileData.getCodePageId())
      {
         case FileStageData.CODE_PAGE_ISO8859_1:
              nlsMapName = Constants.CODE_PAGE_ISO_8859_1;
              break;

         case FileStageData.CODE_PAGE_UTF8:
              nlsMapName = Constants.CODE_PAGE_UTF_8;
              break;

         case FileStageData.CODE_PAGE_UTF16:
              nlsMapName = Constants.CODE_PAGE_UTF_16;
              break;
              
         default:
              nlsMapName = null;
      } // end of switch(parFileData.getCodePageSetting())

      if (nlsMapName != null) {
         fileStageParamMap = new ObjectParamMap(DataStageObjectFactory.STAGE_TYPE_DEFAULT);
         fileStageParamMap.put(SAPStageProperties.CHAR_SET_KEY, nlsMapName);
      }

      // ... and then create the stage
      newStage = parDSFactory.createDSStage(parDSContainerDef, stageName, 
                                            DSStageTypeEnum.PX_SEQUENTIAL_FILE_LITERAL, 
                                            fileStageParamMap);
      newStage.setStageTypeClassName(FileStageProperties.STAGE_TYPE_CLASS_NAME);
      newStage.setNLSMapName(nlsMapName);

      // complete and set the filename ....
      fileName          = FileStageProperties.determineFilename(parSchemaName, 
                                                                parTableName, 
                                                                parFileData);
      filePropertyValue = MessageFormat.format(FileSourceStageProperties.PROP_FILE_NAME_TEMPLATE, 
                                               new Object[] { fileName } );
      
      // setup the link parameters ...
      newLinkParamMap = FileSourceStageProperties.getDefaultLinkParams();
      newLinkParamMap.put(FileSourceStageProperties.PROP_FILE_NAME_KEY, filePropertyValue);

      if (parFileData.is1stLineColumnNames())
      {
         firstLineIsColName = FileStageProperties.PROP_1ST_LINE_COLUMN_NAME_SET; 
      }
      else
      {
    	  // osuhre: for source stages, the false value is different
         firstLineIsColName = FileStageProperties.PROP_1ST_LINE_COLUMN_NAME_NOT_SET; 
      }
      newLinkParamMap.put(FileStageProperties.PROP_1ST_LINE_COLUMN_NAME_KEY, firstLineIsColName);

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.exit();
      }

      retStageData = new StageData(newStage, newLinkParamMap, DataStageObjectFactory.LINK_TYPE_OUTPUT);
      
      // set MetaBag information (e.g. delimiters, quote, ...
      FileStageProperties.setDelimsAndQuote(retStageData, parFileData);
      
      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.exit();
      }

      return (retStageData);
   } // end of createFileSourceStage()


   public static StageData createFileTargetStage(MainObject parDSContainerDef, String parSchemaName, 
                                                 String parTableName, FileStageData parFileData, 
                                                 DataStageObjectFactory parDSFactory)
                  throws DSAccessException
   {
      StageData       retStageData;
      DSStage         newStage;
      ObjectParamMap  newLinkParamMap;
      ObjectParamMap  fileStageParamMap;
      String          stageName;
      String          filePropertyValue;
      String          fileName;
      String          tmpValue;
      String          nlsMapName;

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry("schema = " + parSchemaName + " - table = " + parTableName);
      }

      // setup the parameter values for the stage ...
      stageName = MessageFormat.format(DB_TABLE_NAME_TEMPLATE, new Object[] { parSchemaName, 
                                                                              parTableName });

      // setup NLS mapping for new file stage ...
      fileStageParamMap = null;
      switch(parFileData.getCodePageId())
      {
         case FileStageData.CODE_PAGE_ISO8859_1:
              nlsMapName = Constants.CODE_PAGE_ISO_8859_1;
              break;

         case FileStageData.CODE_PAGE_UTF8:
              nlsMapName = Constants.CODE_PAGE_UTF_8;
              break;

         case FileStageData.CODE_PAGE_UTF16:
              nlsMapName = Constants.CODE_PAGE_UTF_16;
              break;
              
         default:
              nlsMapName = null;
      } // end of switch(parFileData.getCodePageSetting())

      if (nlsMapName != null) {
         fileStageParamMap = new ObjectParamMap(DataStageObjectFactory.STAGE_TYPE_DEFAULT);
         fileStageParamMap.put(SAPStageProperties.CHAR_SET_KEY, nlsMapName);
      }
      
      // ... and then create the stage
      newStage = parDSFactory.createDSStage(parDSContainerDef, stageName, 
                                            DSStageTypeEnum.PX_SEQUENTIAL_FILE_LITERAL, 
                                            fileStageParamMap);
      newStage.setStageTypeClassName(FileStageProperties.STAGE_TYPE_CLASS_NAME);
      newStage.setNLSMapName(nlsMapName);

      // complete and set the filename ....
      fileName          = FileStageProperties.determineFilename(parSchemaName, 
                                                                parTableName, 
                                                                parFileData);
      filePropertyValue = MessageFormat.format(FileTargetStageProperties.PROP_FILE_NAME_TEMPLATE, 
                                               new Object[] { fileName } );
      // setup the link parameters ...
      newLinkParamMap = FileTargetStageProperties.getDefaultLinkParams();
      newLinkParamMap.put(FileTargetStageProperties.PROP_FILE_NAME_KEY, filePropertyValue);
      
      if (parFileData.is1stLineColumnNames())
      {
         tmpValue = FileStageProperties.PROP_1ST_LINE_COLUMN_NAME_SET; 
      }
      else
      {
         tmpValue = FileStageProperties.PROP_1ST_LINE_COLUMN_NAME_NOT_SET; 
      }
      newLinkParamMap.put(FileStageProperties.PROP_1ST_LINE_COLUMN_NAME_KEY, tmpValue);
      
      switch(parFileData.getUpdateMode())
      {
         case FileStageData.UPDATE_MODE_APPEND:
              tmpValue = FileTargetStageProperties.PROP_FILEMODE_APPEND;
              break;
         case FileStageData.UPDATE_MODE_CREATE:
              tmpValue = FileTargetStageProperties.PROP_FILEMODE_CREATE;
              break;
         
         case FileStageData.UPDATE_MODE_OVERWRITE:
         default:
              tmpValue = FileTargetStageProperties.PROP_FILEMODE_OVERWRITE; 
      } // end of switch(parFileData.getUpdateMode())
      newLinkParamMap.put(FileTargetStageProperties.PROP_FILEMODE_KEY, tmpValue);
      
      retStageData = new StageData(newStage, newLinkParamMap, DataStageObjectFactory.LINK_TYPE_INPUT);
      
      // set MetaBag information (e.g. delimiters, quote, ...
      FileStageProperties.setDelimsAndQuote(retStageData, parFileData);
      
      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.exit();
      }

      return (retStageData);
   } // end of createFileTargetStage()


	public static StageData createFunnelStage(MainObject dsContainerDef,
                                             DataStageObjectFactory dsFactory, ObjectParamMap paramMap)  
	       throws DSAccessException {
		DSStage newFunnelStage;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		// create the stage
		newFunnelStage = dsFactory.createDSStage(dsContainerDef, null, DSStageTypeEnum.PX_FUNNEL_LITERAL, null);
		newFunnelStage.setStageTypeClassName(StageProperties.STAGE_TYPE_CLASS_NAME_CUSTOM_STAGE);

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}

		return (new StageData(newFunnelStage));
	} // end of createFunnelStage()


   public static StageData createIDOCExtractStage(DSJobDef jobDef, String idocTypeName, 
		                                            boolean isExtendedIDocType, String basicTypeName, 
		                                            String idocSegmentType, String jobName, 
		                                            RequestJobTypeIDocExtract jtIDOCExtract,
                                                  boolean isUnicodeCharSet, boolean doCreateV7Stage,
                                                  ServiceToken srvcToken) 
                 throws DSAccessException
   {
      DSStage                newIDOCStage;
      DSStageTypeEnum        stageTypeEnum;
      DataStageObjectFactory dsFactory;
      String                 stageName;
      ObjectParamMap         idocExtractParamMap;

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry("IDoc type = " + idocTypeName + 
                           " - is extended IDoc type = " + isExtendedIDocType +
                           " - basic IDoc type = " + basicTypeName +
                           " - segment type = " + idocSegmentType +
                           " - job name = " + jobName +
                           " - createV7Stage = " + doCreateV7Stage);
      }
      dsFactory = DataStageObjectFactory.getInstance(srvcToken);

      // setup stage parameter map
      idocExtractParamMap = SAPIDocExtractStageProperties.getDefaultStageParams();
      idocExtractParamMap.put(SAPIDocExtractStageProperties.PROP_IDOC_TYPE_KEY, idocTypeName);
      if (isExtendedIDocType) {
         idocExtractParamMap.put(SAPIDocExtractStageProperties.PROP_IDOC_BASIC_TYPE_KEY, basicTypeName);
      }
      idocExtractParamMap.put(SAPIDocExtractStageProperties.PROP_CONN_SAP_CONNECTION_NAME_KEY,
                              jtIDOCExtract.getSAPSystem().getSAPSystemName());
      idocExtractParamMap.put(SAPIDocExtractStageProperties.PROP_JOBNAME_KEY,
                              jobName);
      
      if (jtIDOCExtract.doUseOfflineProcessing())
      {
         idocExtractParamMap.put(SAPIDocExtractStageProperties.PROP_USE_OFFLINE_PROCESSING, "1");
      }

      // set current SAPSystemData for SAP System Logon  ...
      setSAPSystemData(idocExtractParamMap, jtIDOCExtract.getSAPSystem(), false, srvcToken);

      // check for connection Job Parameter
      if (StringUtils.isJobParamVariable(jtIDOCExtract.getSAPSystem().getSAPSystemName())) 
      {
         // it's available ==> add the Job Parameter name to the stage params
         idocExtractParamMap.put(SAPIDocExtractStageProperties.PROP_CONN_SAP_CONNECTION_PARAM_KEY,
                                 getJobParamName(jtIDOCExtract.getSAPSystem().getSAPSystemName()));
      }
      
      if (doCreateV7Stage) 
      {
         stageTypeEnum       = DSStageTypeEnum.SAP_IDOC_EXTRACT_CONNECTOR_PX_LITERAL;
         idocExtractParamMap = SAPStageProperties.getXMLPropertyFromIDocExtractStageMap(idocExtractParamMap);
      }
      else
      {
         stageTypeEnum = DSStageTypeEnum.SAP_IDOC_EXTRACT_PX_LITERAL;
      } // end of (else) if (doCreateV7Stage)
      
      // A T T E N T I O N: These properties must not be part of the XML !!!
      // -------------------------------------------------------------------
      if (isUnicodeCharSet)
      {
         // UNICODE character set is used ...
         idocExtractParamMap.put(SAPIDocExtractStageProperties.CHAR_SET_KEY, Constants.DEFAULT_UNICODE_CHARSET);
      }
      
      // ... and then create the stage
      stageName = MessageFormat.format(IDOC_EXTRACT_STAGE_NAME_TEMPLATE, 
                                       new Object[] { idocTypeName, idocSegmentType });
      newIDOCStage = dsFactory.createDSStage(jobDef, stageName, stageTypeEnum, 
                                             idocExtractParamMap);
      
      newIDOCStage.setStageTypeClassName(SAPIDocExtractStageProperties.STAGE_TYPE_CLASS_NAME);
      String shortDesc = Constants.JOB_GEN_SAP_STAGE_ID;
      shortDesc += "\n" + (isExtendedIDocType ? Constants.JOB_GEN_EXTENDED_IDOC_TYPE : Constants.JOB_GEN_BASIC_IDOC_TYPE);
      newIDOCStage.setShortDescription(shortDesc);

      // if UNICODE charset is used ...
      if (isUnicodeCharSet)
      {
         newIDOCStage.setNLSMapName(Constants.DEFAULT_UNICODE_CHARSET);
      }

      // for V7 stages ...
      if (doCreateV7Stage) 
      {
         // ==> set partitioning data on the incoming link
         SAPStageProperties.setSequentialExecutionMode(newIDOCStage, dsFactory);
      } // end of if (doCreateV7Stage)
      
      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.exit();
      }

      return(new StageData(newIDOCStage));
   } // end of createIDOCExtractStage()

   
   public static StageData createIDOCLoadStage(DSJobDef jobDef, String idocTypeName, boolean isExtendedIDocType, 
                                               String basicTypeName, String idocSegmentType, String jobName, 
                                               RequestJobTypeIDocLoad jtIDOCLoad,
                                               boolean isUnicodeCharSet, boolean doCreateV7Stage,
                                               ServiceToken srvcToken) 
                 throws DSAccessException
   {
      ObjectParamMap         idocLoadParamMap;
      DSStage                newIDOCStage;
      StageData              retStageData;
      DSStageTypeEnum        stageTypeEnum;
      DataStageObjectFactory dsFactory;
      String                 stageName;

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry("IDoc type = " + idocTypeName + 
                           " - is extended IDoc type = " + isExtendedIDocType +
                           " - basic IDoc type = " + basicTypeName +
                           " - segment type = " + idocSegmentType +
                           " - job name = " + jobName +
                           " - createV7Stage = " + doCreateV7Stage);
      }
      dsFactory = DataStageObjectFactory.getInstance(srvcToken);

      // setup stage parameter map
      idocLoadParamMap = SAPIDocLoadStageProperties.getDefaultStageParams();
      idocLoadParamMap.put(SAPIDocLoadStageProperties.PROP_IDOC_TYPE_KEY, idocTypeName);
      if (isExtendedIDocType) {
         idocLoadParamMap.put(SAPIDocLoadStageProperties.PROP_IDOC_BASIC_TYPE_KEY, basicTypeName);
      }
      idocLoadParamMap.put(SAPIDocLoadStageProperties.PROP_MES_TYPE_KEY, jtIDOCLoad.getSAPMessageType());
      idocLoadParamMap.put(SAPIDocLoadStageProperties.PROP_CONN_SAP_CONNECTION_NAME_KEY,
                           jtIDOCLoad.getSAPSystem().getSAPSystemName());
      idocLoadParamMap.put(SAPIDocLoadStageProperties.PROP_JOBNAME_KEY, jobName);
      idocLoadParamMap.put(SAPIDocLoadStageProperties.PROP_SAP_IDOC_PER_TRANS, 
                           String.valueOf(jtIDOCLoad.getIDOCsPerTrans()));

      // set current SAPSystemData for SAP System Logon  ...
      setSAPSystemData(idocLoadParamMap, jtIDOCLoad.getSAPSystem(), false, srvcToken);
      
      // check for connection Job Parameter
      if (StringUtils.isJobParamVariable(jtIDOCLoad.getSAPSystem().getSAPSystemName())) 
      {
         // it's available ==> add the Job Parameter name to the stage params
         idocLoadParamMap.put(SAPIDocLoadStageProperties.PROP_CONN_SAP_CONNECTION_PARAM_KEY,
                              getJobParamName(jtIDOCLoad.getSAPSystem().getSAPSystemName()));
      }
      else {
    	  // osuhre: fill in Design time (...DT) parameters only if no job parameter is given 
    	  idocLoadParamMap.put(SAPIDocLoadStageProperties.PROP_SAP_CONNECTION_NAME_DT_KEY,
    			  	              jtIDOCLoad.getSAPSystem().getSAPSystemName());
    	  idocLoadParamMap.put(SAPIDocLoadStageProperties.PROP_SAP_CONNECTION_DT_KEY,
    			                 jtIDOCLoad.getSAPSystem().getSAPSystemName());
      }

      if (doCreateV7Stage) 
      {
         stageTypeEnum    = DSStageTypeEnum.SAP_IDOC_LOAD_CONNECTOR_PX_LITERAL;
         idocLoadParamMap = SAPStageProperties.getXMLPropertyFromIDocLoadStageMap(idocLoadParamMap);
      }
      else
      {
         stageTypeEnum = DSStageTypeEnum.SAP_IDOC_LOAD_PX_LITERAL;
      } // end of (else) if (doCreateV7Stage)

      // A T T E N T I O N: These properties must not be part of the XML !!!
      // -------------------------------------------------------------------
      if (isUnicodeCharSet)
      {
         // UNICODE character set is used ...
         idocLoadParamMap.put(SAPIDocLoadStageProperties.CHAR_SET_KEY, Constants.DEFAULT_UNICODE_CHARSET);
      }
      
      // ... and then create the stage
      stageName    = MessageFormat.format(IDOC_LOAD_STAGE_NAME_TEMPLATE, new Object[] { idocTypeName, 
                                                                                        idocSegmentType });
      newIDOCStage = dsFactory.createDSStage(jobDef, stageName, stageTypeEnum, 
                                             idocLoadParamMap);
      newIDOCStage.setStageTypeClassName(SAPIDocExtractStageProperties.STAGE_TYPE_CLASS_NAME);
      String shortDesc = Constants.JOB_GEN_SAP_STAGE_ID;
      shortDesc += "\n" + (isExtendedIDocType ? Constants.JOB_GEN_EXTENDED_IDOC_TYPE : Constants.JOB_GEN_BASIC_IDOC_TYPE);
      newIDOCStage.setShortDescription(shortDesc);

      // if UNICODE charset is used ...
      if (isUnicodeCharSet)
      {
         newIDOCStage.setNLSMapName(Constants.DEFAULT_UNICODE_CHARSET);
      }

      retStageData = new StageData(newIDOCStage);
      
      // for V7 stages ...
      if (doCreateV7Stage) 
      {
         // ==> set partitioning data on the incoming link
         SAPStageProperties.setPartitioningStageInfoHashKey(retStageData.getMetaBagList());
      } // end of if (doCreateV7Stage)
      

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.exit();
      }

      return (retStageData);
   } // end of createIDOCLoadStage()

   
   public static StageData createContainerStage(DSJobDef jobDef, String tableName,
                                                DataStageObjectFactory dsFactory)
                 throws DSAccessException
   {
      DSStage             containerStage;
      StageData           stageData;
      
      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry();
      }

      // create the Container stage (and implicitly the LocalContainerDef) ...
      containerStage = dsFactory.createContainerStage(jobDef, tableName, 
                                                      ContainerStageProperties.getDefaultStageParams());
      containerStage.setStageTypeClassName(StageProperties.STAGE_TYPE_CLASS_NAME_CONTAINER);

      
      stageData = new StageData(containerStage);
      stageData.setType(DSStageTypeEnum.CONTAINER_STAGE_LITERAL);
      
      return(stageData);
   } // end of createContainerStage()
   
   
   public static StageData createLookupStage(MainObject parDSContainerDef, DataStageObjectFactory parDSFactory) 
                 throws DSAccessException
   {
      StageData retStageData;
      DSStage   newLookupStage;
      DSMetaBag metaBag;
      
      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry();
      }

      // create the stage
      newLookupStage = parDSFactory.createDSStage(parDSContainerDef, null, 
                                                  DSStageTypeEnum.PX_LOOKUP_LITERAL,
                                                  null);
      newLookupStage.setStageTypeClassName(StageProperties.STAGE_TYPE_CLASS_NAME_CUSTOM_STAGE);

      // set MetaBag for 'Lookup'
      metaBag = parDSFactory.createDSMetaBag();
      metaBag.setOf_DSStage(newLookupStage);
      metaBag.setOwners(DataStageObjectFactory.METABAG_OWNER_NAME);
      metaBag.setNames(DataStageObjectFactory.METABAG_LU_LOOKUP_NAME);
      metaBag.setValues(DataStageObjectFactory.METABAG_LU_LOOKUP_OP_VALUES_LOOKUP);
      newLookupStage.setOf_DSMetaBag(metaBag);
      
      
      retStageData = new StageData(newLookupStage, 
                                   LookupStageProperties.getDefaultLinkParams(), 
                                   DataStageObjectFactory.LINK_TYPE_INPUT);

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.exit("Lookup Stage Data = " + retStageData);
      }

      return(retStageData);
   } // end of createLookupStage()
   
   
   private static StageData createODBCSourceStage(MainObject parDSContainerDef, TableData parTableData, 
                                                  ODBCStageData parODBCData, String parSqlStatement, 
                                                  DataStageObjectFactory parDSFactory) 
                  throws DSAccessException
   {
      ObjectParamMap odbcParamMap;
      DSStage        newStage;
      String         fullTableName;
      String         xmlProperties;
      String         schemaName;
      String         tableName;
      String         stageName;
      

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry("table = " + parTableData.getName());
      }

      tableName  = parTableData.getName();
      schemaName = parODBCData.getAlternameSchemaName();
      if (schemaName == null)
      {
         schemaName = parTableData.getDBSchema();
      }
      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINER, "Schema name = " + schemaName);
      }
      
      // if there is an SQL statement - use it 
      if(parSqlStatement != null) 
      {
         // ==> determine the SQL statement to be applied
         if (TraceLogger.isTraceEnabled()) 
         {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "sql (entry) = " + parSqlStatement);
         }

         parSqlStatement = parSqlStatement.replaceAll(DB_SQL_SCHEMA_REPLACEMENT, schemaName);
         
         // check if we need to apply a SQL where condition   
         if (parODBCData.getSQLWhereCondition() != null) 
         {
        	 String sql = parODBCData.getSQLWhereCondition();
        	 if (TraceLogger.isTraceEnabled()) {
        		 TraceLogger.trace(TraceLogger.LEVEL_FINE, MessageFormat.format("Replacing " + WHERE_CONDITION_TABLE_PLACEHOLDER + " in WHERE condition ''{0}'' with ''{1}''", sql, tableName));
        	 }
        	 String condition = sql.replaceAll(WHERE_CONDITION_TABLE_PLACEHOLDER, tableName);
        	 if (TraceLogger.isTraceEnabled()) {
        		 TraceLogger.trace(TraceLogger.LEVEL_FINE, MessageFormat.format("New WHERE condition: ''{0}''", condition)); 
        	 }
        	 parSqlStatement = parSqlStatement.replaceAll(DB_SQL_WHERE_REPLACEMENT, condition);
         }
         else 
         {
        	 parSqlStatement = parSqlStatement.replaceAll(DB_SQL_NO_WHERE_REPLACEMENT, "");
         }
      } 
      else 
      {
         parSqlStatement = null;
      } // end of if (parSqlStatement != null)

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "sql (applied) = " + parSqlStatement);
      }

      // setup the parameter values for the stage ...
      fullTableName = MessageFormat.format(DB_TABLE_NAME_TEMPLATE, new Object[] { schemaName, 
                                                                                  tableName });
      odbcParamMap  = ODBCStageProperties.getDefaultStageParams();
      xmlProperties = ODBCStageProperties.getSourceStageXMLProp(parODBCData, 
                                                                parSqlStatement, 
                                                                fullTableName);
      odbcParamMap.put(ODBCStageProperties.PROP_XML_PROPERTIES_KEY, xmlProperties);

      // build the stage name ...
      stageName = MessageFormat.format(DB_TABLE_NAME_TEMPLATE, new Object[] { schemaName, 
                                                                              parTableData.getDescriptiveTableName() });

      // ... and then create the stage
      newStage = parDSFactory.createDSStage(parDSContainerDef, stageName, 
                                            DSStageTypeEnum.ODBC_CONNECTOR_PX_LITERAL, 
                                            odbcParamMap);
      newStage.setStageTypeClassName(ODBCStageProperties.STAGE_TYPE_CLASS_NAME);

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.exit();
      }

      return (new StageData(newStage));
   } // end of createODBCSourceStage()

   
   private static StageData createODBCTargetStage(MainObject parDSContainerDef, TableData parTableData, 
                                                  ODBCStageData parODBCData, DataStageObjectFactory parDSFactory)
                 throws DSAccessException
   {
      ObjectParamMap odbcParamMap;
      DSStage        newODBCStage;
      String         fullTableName;
      String         xmlProperties;
      String         schemaName;
      String         tableName;
      String         stageName;

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry("table = " + parTableData.getName());
      }

      tableName  = parTableData.getName();
      schemaName = parODBCData.getAlternameSchemaName();
      if (schemaName == null)
      {
         schemaName = parTableData.getDBSchema();
      }
      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINER, "Schema name = " + schemaName);
      }

      // setup the parameter values for the stage ...
      fullTableName = MessageFormat.format(DB_TABLE_NAME_TEMPLATE, new Object[] { schemaName, 
                                                                                  tableName });
      odbcParamMap  = ODBCStageProperties.getDefaultStageParams();
      xmlProperties = ODBCStageProperties.getTargetStageXMLProp(parODBCData, fullTableName);
      odbcParamMap.put(ODBCStageProperties.PROP_XML_PROPERTIES_KEY, xmlProperties);

      // build the stage name ...
      stageName = MessageFormat.format(DB_TABLE_NAME_TEMPLATE, new Object[] { schemaName, 
                                                                              parTableData.getDescriptiveTableName() });

      // ... and then create the stage
      newODBCStage = parDSFactory.createDSStage(parDSContainerDef, stageName, 
                                                DSStageTypeEnum.ODBC_CONNECTOR_PX_LITERAL, 
                                                odbcParamMap);
      newODBCStage.setStageTypeClassName(ODBCStageProperties.STAGE_TYPE_CLASS_NAME);

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.exit();
      }

      return (new StageData(newODBCStage));
   } // end of createODBCTargetStage()

   
   public static StageData createPersistenceSourceStage(MainObject dsContainerDef, TableData tableData,
                                                        PersistenceData persistenceData, 
                                                        String sqlStatement, DataStageObjectFactory dsFactory) 
                 throws DSAccessException
   {
      StageData newPersistenceStage;

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry("schema = " + tableData.getDBSchema() + " - table = " + tableData.getName());
      }

      // determine the real persistence data and call the appropriate 'create stage' method
      if (persistenceData instanceof ODBCStageData) 
      {
         newPersistenceStage = createODBCSourceStage(dsContainerDef, tableData,
                                                     (ODBCStageData) persistenceData, 
                                                     sqlStatement, dsFactory);
      }
      else
      {
         if (persistenceData instanceof FileStageData) 
         {
            newPersistenceStage = createFileSourceStage(dsContainerDef, tableData.getDBSchema(),
                                                        tableData.getDescriptiveTableName(), 
                                                        (FileStageData) persistenceData, 
                                                        dsFactory);
         }
         else
         {
            newPersistenceStage = null;
            
            if (TraceLogger.isTraceEnabled()) 
            {
               TraceLogger.trace(TraceLogger.LEVEL_FINE, "Unknown Persistence Stage data: " + persistenceData);
            }
         } // end of (else) if (persistenceData instanceof FileStageData)
      } // end of (else) if (persistenceData instanceof ODBCStageData)

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.exit();
      }

      return (newPersistenceStage);
   } // end of createPersitenceSourceStage()


   public static StageData createPersistenceTargetStage(MainObject parDSContainerDef, TableData parTableData, 
                                                        PersistenceData parPersistenceData, 
                                                        ServiceToken srvcToken)
                 throws DSAccessException
   {
      StageData              newPersistenceStage;
      DataStageObjectFactory dsFactory;

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry("schema = " + parTableData.getDBSchema() + " - table = " + parTableData.getName());
      }
      dsFactory = DataStageObjectFactory.getInstance(srvcToken);

      // determine the real persistence data and call the appropriate 'create stage' method
      if (parPersistenceData instanceof ODBCStageData) 
      {
         newPersistenceStage = createODBCTargetStage(parDSContainerDef, parTableData, 
                                                     (ODBCStageData) parPersistenceData, 
                                                     dsFactory);
      }
      else
      {
         if (parPersistenceData instanceof FileStageData) 
         {
            newPersistenceStage = createFileTargetStage(parDSContainerDef, parTableData.getDBSchema(), 
                                                        parTableData.getDescriptiveTableName(), 
                                                        (FileStageData) parPersistenceData, 
                                                        dsFactory);
         }
         else
         {
            if (parPersistenceData instanceof NetezzaStageData) 
            {
               newPersistenceStage = createNetezzaStage(parDSContainerDef, parTableData,
                                                        (NetezzaStageData) parPersistenceData, srvcToken);
            }
            else
            {
               newPersistenceStage = null;
               
               if (TraceLogger.isTraceEnabled()) 
               {
                  TraceLogger.trace(TraceLogger.LEVEL_FINE, "Unknown Persistence Stage data: " + parPersistenceData);
               }
            }
         } // end of (else) if (parPersistenceData instanceof FileStageData)
      } // end of (else) if (parPersistenceData instanceof ODBCStageData)
      
      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.exit();
      }

      return (newPersistenceStage);
   } // end of createPersistenceTargetStage()

   
	private static StageData createNetezzaStage(MainObject parDSContainerDef, TableData parTableData, 
                                               NetezzaStageData parNetezzaData, ServiceToken srvcToken)
	        throws DSAccessException {

		DataStageObjectFactory dsFactory;
		ObjectParamMap         paramMap;
		DSStage                newNetezzaStage;
      String                 xmlProperties;
      String                 tableName;
      String                 stageName;

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry("table = " + parTableData.getName());
      }

      dsFactory  = DataStageObjectFactory.getInstance(srvcToken);
      tableName  = parTableData.getName();

      // setup the parameter values for the stage ...
      paramMap      = NetezzaStageProperties.getDefaultStageParams();
      xmlProperties = NetezzaStageProperties.getStageXMLProp(parNetezzaData, tableName, srvcToken);
      paramMap.put(ODBCStageProperties.PROP_XML_PROPERTIES_KEY, xmlProperties);

      
      // build the stage name ...
      stageName = parTableData.getDescriptiveTableName();

      // ... and then create the stage
      newNetezzaStage = dsFactory.createDSStage(parDSContainerDef, stageName, 
                                                DSStageTypeEnum.NETEZZA_CONNECTOR_PX_LITERAL, 
                                                paramMap);
      newNetezzaStage.setStageTypeClassName(NetezzaStageProperties.STAGE_TYPE_CLASS_NAME);

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.exit();
      }

      return (new StageData(newNetezzaStage, NetezzaStageProperties.getLinkParams(parNetezzaData), 
                            DataStageObjectFactory.LINK_TYPE_INPUT));
   } // end of createNetezzaStage()
	

   public static StageData createRemoveDuplicatesStage(MainObject dsContainerDef,
	                                                    DataStageObjectFactory dsFactory, ObjectParamMap paramMap)
	       throws DSAccessException {
		ObjectParamMap curRemoveDuplicatesParamMap;
		DSStage        newRemoveDuplicatesStage;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		if (paramMap == null) {
			curRemoveDuplicatesParamMap = StageProperties.getEmptyDefaultStageParams();
		} 
		else {
			curRemoveDuplicatesParamMap = paramMap;
		}
		
		//set the key property of the remove duplicates stage
		curRemoveDuplicatesParamMap.put(StageProperties.REMOVEDUPLICATES_KEY_NAME, 
		                                StageProperties.REMOVEDUPLICATES_KEY_VALUE.replaceFirst("\\{0\\}", Constants.IDOC_TECH_FLD_ADM_DOCNUM_NAME));
		
		// create the stage
		newRemoveDuplicatesStage = dsFactory.createDSStage(dsContainerDef, null,
		                                                   DSStageTypeEnum.PX_REMOVE_DUPLICATES_LITERAL,
		                                                   curRemoveDuplicatesParamMap);
		                                                   newRemoveDuplicatesStage.setStageTypeClassName(StageProperties.STAGE_TYPE_CLASS_NAME_CUSTOM_STAGE);

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}

		return (new StageData(newRemoveDuplicatesStage));
	} // end of createRemoveDuplicatesStage()

   
   public static StageData createTransformerStage(MainObject dsContainerDef, DataStageObjectFactory dsFactory, 
                                                  ObjectParamMap paramMap) 
                 throws DSAccessException
   {
      ObjectParamMap curTransformerParamMap;
      DSStage        newTransformerStage;

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry();
      }

      if (paramMap == null)
      {
         curTransformerParamMap = TransformerStageProperties.getDefaultStageParams();
      }
      else
      {
         curTransformerParamMap = paramMap;
      }

      // create the stage
      newTransformerStage = dsFactory.createDSStage(dsContainerDef, null, 
                                                    DSStageTypeEnum.CTRANSFORMER_STAGE_LITERAL,
                                                    curTransformerParamMap);
      newTransformerStage.setStageTypeClassName(StageProperties.STAGE_TYPE_CLASS_NAME_TRANSFORMER);

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.exit();
      }

      return (new StageData(newTransformerStage));
   } // end of createTransformerStage()

   
   /**
    * This method sets the SAPSystemData in the passed Stage Object Parameter map 
    * if a userId is found.  
    * 
    * <p><b>Note: </b>If the password exists it will be encrypted before setting in the map.
    * <p><b>Note: </b>There are different constants for ABAP Extract stage and 
    * other SAP stages. See parameter 'isABAPExtractStage'. 
    * 
    * @param parObjectParamMap   ObjectParameter map
    * @param parSAPSystemData    SAP System Data
    * @param isABAPExtractStage  true if it's called from ABAPExtractStage creator otherwise false
   **/
   public static void setSAPSystemData(ObjectParamMap parObjectParamMap, 
                                       SAPSystemData parSAPSystemData, 
                                       boolean isABAPExtractStage,
                                       ServiceToken srvcToken)
   {
      String sapPassword;

      if (parSAPSystemData.getSAPUserName() != null)
      {
         // different password handling and different constants for ABAP and IDoc stages 
         if (isABAPExtractStage)
         {
            // ABAPExtract stage
            sapPassword = srvcToken.encrypt(parSAPSystemData.getSAPPassword());

            parObjectParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_USERNAME_KEY, parSAPSystemData.getSAPUserName());
            parObjectParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_PASSWORD_KEY, sapPassword);
            parObjectParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_CLIENT_NUMBER_KEY, parSAPSystemData.getSAPClientNumber());
            parObjectParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_LANGUAGE_KEY, parSAPSystemData.getSAPUserLanguage());
            parObjectParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_USE_DEFAULT_LOGON, "0");
         }
         else
         {
            // IDOC (Extract/Load) stage
            if (StringUtils.isJobParamVariable(parSAPSystemData.getSAPPassword())) {
               sapPassword = parSAPSystemData.getSAPPassword();
            }
            else {
               sapPassword = srvcToken.encrypt(parSAPSystemData.getSAPPassword());
            }

            parObjectParamMap.put(SAPStageProperties.PROP_SAP_SYS_USERNAME_KEY, 
                                  parSAPSystemData.getSAPUserName());
            parObjectParamMap.put(SAPStageProperties.PROP_SAP_SYS_USERNAME_KEY_2, 
                                  parSAPSystemData.getSAPUserName());
            parObjectParamMap.put(SAPStageProperties.PROP_SAP_SYS_PASSWORD_KEY, 
                                  sapPassword);
            parObjectParamMap.put(SAPStageProperties.PROP_SAP_SYS_PASSWORD_KEY_2, 
                                  sapPassword);
            parObjectParamMap.put(SAPStageProperties.PROP_SAP_SYS_CLIENT_NUMBER_KEY, 
                                  parSAPSystemData.getSAPClientNumber());
            parObjectParamMap.put(SAPStageProperties.PROP_SAP_SYS_CLIENT_NUMBER_KEY_2, 
                                  parSAPSystemData.getSAPClientNumber());
            parObjectParamMap.put(SAPStageProperties.PROP_SAP_SYS_LANGUAGE_KEY,   
                                  parSAPSystemData.getSAPUserLanguage());
            parObjectParamMap.put(SAPStageProperties.PROP_SAP_SYS_LANGUAGE_KEY_2, 
                                  parSAPSystemData.getSAPUserLanguage());
            parObjectParamMap.put(SAPStageProperties.PROP_SAP_USE_DEFAULT_LOGON, "0");
         } // end of (else) if (parImplementingClass instanceof ABAPExtractJob)
      } // end of if (isABAPExtractStage)
   } // end of setSAPSystemData()


	public static String getJobParamName(String param) {
		if (!StringUtils.isJobParamVariable(param)) {
			return null;
		}
		String result = param.substring(1, param.length() - 1);
		return result;
	}
	
	public static void main(String[] args) {
		try {
			String p = "#xyz#aa";
			String n = getJobParamName(p);
			System.out.println("param: " + n);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

} // end of class StageFactory
