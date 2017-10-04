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
// Module Name : com.ibm.is.sappack.gen.server.jobgenerator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.server.jobgenerator;


import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ASCLModel.LinkTypeEnum;
import DataStageX.DSInputPin;
import DataStageX.DSJobDef;
import DataStageX.DSLink;

import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequest;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeMIHCodeTableLoad;
import com.ibm.is.sappack.gen.common.request.SupportedColumnTypesMap;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.LookupStageProperties;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.StageFactory;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.TransformerStageProperties;
import com.ibm.is.sappack.gen.server.util.ColumnData;
import com.ibm.is.sappack.gen.server.util.GraphJobLink;
import com.ibm.is.sappack.gen.server.util.GraphJobNode;
import com.ibm.is.sappack.gen.server.util.ModelInfoBlock;
import com.ibm.is.sappack.gen.server.util.ObjectParamMap;
import com.ibm.is.sappack.gen.server.util.StageData;
import com.ibm.is.sappack.gen.server.util.TableData;


public class MIHCodeTablesLoadJob extends ABAPExtractJob
{
   // -------------------------------------------------------------------------------------
   // Constants
   // -------------------------------------------------------------------------------------
   private   static final String PRIMARY_KEY_LINK_NAME_TEMPLATE = "NextSurrogateKey() : " + MIHLoadJob.INSTANCE_ID_KEY;
   private   static final String PK_LINK_FILTER_CONSTRAINT      = "isNull({0}.PK_ID_{1}) OR ({0}.PK_ID_{1}=0)";
   private   static final String MIH_CT_TABLE_TEMPLATE          = "CD{0}";
   
   
   // -------------------------------------------------------------------------------------
   // Member Variables
   // -------------------------------------------------------------------------------------
   private Map _MIHTableDataMap;

   
   static String copyright()
   {
      return com.ibm.is.sappack.gen.server.jobgenerator.Copyright.IBM_COPYRIGHT_SHORT;
   }

   
   public MIHCodeTablesLoadJob(ServiceToken parSrvcToken,
                               RequestJobTypeMIHCodeTableLoad parJobType,
                               JobGeneratorRequest parJobReqInfo, Map<String, ModelInfoBlock> physModelID2TableMap)
          throws BaseException
   {
      super(parSrvcToken, parJobType, parJobReqInfo, physModelID2TableMap);
   } // end of MIHCodeTablesLoadJob()

   
   public List<String> create() throws BaseException 
   {
      RequestJobTypeMIHCodeTableLoad jobType;
      List<String>                   retJobsCreated;
      
      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry();
      }

      // first create the MIH table data from the ABAP table data
      jobType          = (RequestJobTypeMIHCodeTableLoad) getJobType();
      _MIHTableDataMap = createMIHTablesMap(getTargetTableArr(), jobType.getInstanceIdentifier());
      
      // add required column derivation functions ...
      jobType.addColumDerivation(MIHLoadJob.COLUMN_DERIVATION_TYPE_CHAR, MIHLoadJob.COLUMN_DERIVATION_TRIM_FN);
      jobType.addColumDerivation(MIHLoadJob.COLUMN_DERIVATION_TYPE_VARCHAR, MIHLoadJob.COLUMN_DERIVATION_TRIM_FN);
    
      // and the start the Job creation
      retJobsCreated = super.create();
      
      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.exit("Number of created jobs: " + retJobsCreated.size());
      }

      return (retJobsCreated);
   } // end of create()

   
   private Map<String, TableData> createMIHTablesMap(TableData parSrcTableArr[], int parInstanceId) 
   {
      ColumnData            newCol;
      TableData             curSrcTable;
      TableData             curMIHTable;
      Map<String,TableData> retMIHTablesMap;
      String                colName;
      String                formattedInstanceId;
      String                keyLinkNameTemplate;
      int                   srcArrIdx;

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry();
      }

      // create a copy of the source table array ...
      retMIHTablesMap = new HashMap<String,TableData>();

      // get the formatted MIH Instance Id key ...
      formattedInstanceId = MIHLoadJob.getFormattedInstanceId(parInstanceId);

      // ... and add new key columns to the existing tables
      for (srcArrIdx = 0; srcArrIdx < parSrcTableArr.length; srcArrIdx++) {
         curSrcTable = parSrcTableArr[srcArrIdx];
         
         // get a copy from the table
         curMIHTable  = curSrcTable.getCopy();

         // remove the key columns
         removeKeyAttribFromKeyColumns(curMIHTable);
         
         // generate the Primary Key column
         keyLinkNameTemplate = StringUtils.replaceString(PRIMARY_KEY_LINK_NAME_TEMPLATE, 
                                                         MIHLoadJob.INSTANCE_ID_KEY, formattedInstanceId);

         colName = MessageFormat.format(MIHLoadJob.PRIMARY_KEY_NAME_TEMPLATE, 
                                        new Object[] { curMIHTable.getName() });
         newCol  = curMIHTable.addColumn(colName, MIHLoadJob.KEY_COLUMN_LENGTH, true, colName, 
                                         TableData.COLUMN_POSITION_BEGIN);
         newCol.setTransformerSrcMapping(keyLinkNameTemplate);
         newCol.setIsKeyColumn(true);

         // set some additional required column properties
         newCol.setType(MIHLoadJob.KEY_COLUMN_TYPE);
         newCol.setDataObjectSourceType(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_JOINED_CHECK_AND_TEXT_TABLE);
         newCol.setIsUnicode(curMIHTable.isUnicodeSystem());
         
         if (TraceLogger.isTraceEnabled()) 
         {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Primary Key Col = " + colName + " - transformer mapping = "
                                                      + keyLinkNameTemplate);
         }

         retMIHTablesMap.put(curMIHTable.getName(), curMIHTable);
      } // end of for(srcArrIdx = 0; srcArrIdx < parSrcTableArr.length; srcArrIdx ++)

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.exit();
      }

      return (retMIHTablesMap);
   } // end of createMIHTablesMap()
   
   
   protected void createExtractFlow(TableData curTable, String curRFCDestination, String curRFCProgramId) 
             throws BaseException
   {
      RequestJobTypeMIHCodeTableLoad    jtExtract;
      ObjectParamMap                    linkParamMap;
      GraphJobNode                      sourceNode;
      GraphJobNode                      lookupNode;
      GraphJobNode                      transformerNode;
      GraphJobLink                      transformerLink;
      GraphJobLink                      lookupLink;
      GraphJobLink                      odbcLink;
      TableData                         lookupTableData;
      TableData                         mihTableData;
      TableData                         sapStageMapping;
      ABAPExtractJob.CreateStageResult  createResult;
      DataStageObjectFactory            dsFactory;
      DSJobDef                          jobDef;
      StageData                         newLookupStage;
      StageData                         newPersStage;
      StageData                         newTransformerStage;
      DSLink                            stageLink;
      DSLink                            baseLink;
      String                            tableName;
      String                            linkFilterConstraint;
      String                            mihTableName;
      ObjectParamMap                    transformerParamMap;

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry("table = " + curTable);
      }

      jobDef       = _JobStructureData.getJobDef();
      jtExtract    = (RequestJobTypeMIHCodeTableLoad) getJobType();
      dsFactory    = getDSFactory();
      tableName    = curTable.getName();
      mihTableName = MessageFormat.format(MIH_CT_TABLE_TEMPLATE, new Object[] { tableName });

      // *********************************************
      // ABAP Extract Stage ======>> Transformer Stage
      // *********************************************
      sapStageMapping = curTable.getCopyForSAPMapping();
      
      // create ABAP Extract stage and the left Transformer stage
      createResult = createAbapStageAndTransformer(sapStageMapping, -1, curRFCDestination, 
                                                   curRFCProgramId, jtExtract);
         
      // ***************************************
      // Transformer Stage ======>> Lookup Stage
      // ***************************************
      newTransformerStage = createResult.getStageData();
      transformerNode     = createResult.getGraphJobNode();
      
      newLookupStage      = StageFactory.createLookupStage(jobDef, dsFactory);
      lookupNode          = new GraphJobNode(newLookupStage);

      // ---------------------------------------
      // create link: Transformer ------> Lookup
      // ---------------------------------------
      baseLink = createColumnMapping(newTransformerStage, newLookupStage, sapStageMapping,
                                     newLookupStage.getLinkParams(), dsFactory);

      // update JobStructureData ...
      // ===========================
      lookupLink = new GraphJobLink(baseLink, lookupNode);
      transformerNode.addOutLink(lookupLink);

/*      
      // ******************************************
      // Reference (Persistence) stage (for lookup) 
      // ******************************************
      newPersStage = StageFactory.createPersistenceTargetStage(jobDef, curTable.getDBSchema(), 
                                                               curTable, jtExtract.getPersistenceData(),
                                                               dsFactory);
      sourceNode   = new GraphJobNode(newPersStage.getStage());
      _JobStructureData.addSourceNode(sourceNode);

      // -------------------------------------------------
      // create reference link: Persistence ------> Lookup
      // -------------------------------------------------
      lookupTableData = curTable.getCopyWithKeyCols();
      insertMIHColumn(lookupTableData);
      linkParamMap = ObjectParamMap.concat(newPersStage.getLinkParams(), newLookupStage.getLinkParams());
      stageLink    = createColumnMapping(newPersStage, newLookupStage, lookupTableData,
                                         linkParamMap, LinkTypeEnum.REFERENCE_LITERAL, dsFactory);
      odbcLink     = new GraphJobLink(stageLink, lookupNode);

      
      // update JobStructureData ...
      // ===========================
      lookupLink = new GraphJobLink(stageLink, new GraphJobNode(newLookupStage.getStage()));
      sourceNode.addOutLink(odbcLink);
*/
      
      
      // ***************************************
      // Lookup Stage ======>> Transformer Stage
      // ***************************************
      transformerParamMap = TransformerStageProperties.getDefaultStageParams();
      transformerParamMap.put(TransformerStageProperties.PROP_SURROGATE_KEY_SOURCE_TYPE_KEY,
                              TransformerStageProperties.PROP_SURROGATE_KEY_SOURCE_TYPE_FILE);
      transformerParamMap.put(TransformerStageProperties.PROP_SURROGATE_KEY_STATE_FILE_KEY, 
                              jtExtract.getSurrogateKeyFile());
      newTransformerStage = StageFactory.createTransformerStage(jobDef, dsFactory, transformerParamMap);
      transformerNode     = new GraphJobNode(newTransformerStage);


      // ---------------------------------------
      // create link: Lookup ------> Transformer
      // ---------------------------------------
      mihTableData = sapStageMapping.getCopy();
      insertMIHColumn(mihTableData);
      stageLink    = createColumnMapping(newLookupStage, newTransformerStage, mihTableData,
                                         newLookupStage.getLinkParams(), dsFactory);
      
      // update JobStructureData ...
      // ===========================
      transformerLink = new GraphJobLink(stageLink, transformerNode);
      lookupNode.addOutLink(transformerLink);
      
      // setup the filter constraint for the table (data) 
      mihTableData         = (TableData) _MIHTableDataMap.get(tableName);
      linkFilterConstraint = MessageFormat.format(PK_LINK_FILTER_CONSTRAINT, 
                                                 new Object[] { stageLink.getName(), mihTableData.getName() });
      mihTableData.setFilterConstraint(linkFilterConstraint);
      

      // ********************************************
      // Transformer Stage ======>> Persistence stage
      // ********************************************
      TableData tmpLookupMapping = curTable.getCopy(mihTableName);
      newPersStage = StageFactory.createPersistenceTargetStage(jobDef, curTable, 
                                                               jtExtract.getPersistenceData(),
                                                               getServiceToken());
      
      // --------------------------------------------
      // create link: Transformer ------> Persistence
      // --------------------------------------------
      mihTableData = (TableData) _MIHTableDataMap.get(tableName);
      stageLink    = createColumnMapping(newTransformerStage, newPersStage, mihTableData,
                                         newPersStage.getLinkParams(), dsFactory);


      // update JobStructureData ...
      // ===========================
      odbcLink = new GraphJobLink(stageLink, new GraphJobNode(newPersStage));
      transformerNode.addOutLink(odbcLink);

      // ================================
      // LAST NOT LEAST create the LOOKUP
      // ================================
      lookupTableData = curTable.getCopyWithKeyCols();
      insertMIHColumn(lookupTableData);
      
      // ******************************************
      // Reference (Persistence) stage (for lookup) 
      // ******************************************
      tmpLookupMapping = lookupTableData.getCopy(mihTableName);
      newPersStage = StageFactory.createPersistenceSourceStage(jobDef, tmpLookupMapping, 
                                                               jtExtract.getPersistenceData(), 
                                                               null, dsFactory);
      sourceNode   = new GraphJobNode(newPersStage);
      _JobStructureData.getLayoutData().addSourceNode(sourceNode);

      // get a modified mapping for succeeding stages ...
      lookupTableData = lookupTableData.getCopyForSAPMapping();
      

      // -------------------------------------------------
      // create reference link: Persistence ------> Lookup
      // -------------------------------------------------
      linkParamMap = ObjectParamMap.concat(newPersStage.getLinkParams(), newLookupStage.getLinkParams());
      stageLink    = createColumnMapping(newPersStage, newLookupStage, lookupTableData,
                                         linkParamMap, LinkTypeEnum.REFERENCE_LITERAL, dsFactory);
      odbcLink     = new GraphJobLink(stageLink, lookupNode);
      
      // set 'continue' for 'Lookup Failure'
      ((DSInputPin) stageLink.getTo_InputPin()).setLookupFail(LookupStageProperties.LOOKUP_FAIL_CONTINUE);

      
      // update JobStructureData ...
      // ===========================
//      lookupLink = new GraphJobLink(stageLink, new GraphJobNode(newLookupStage));
      sourceNode.addOutLink(odbcLink);

      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit();
      }
   } // end of createExtractFlow()

   
   private void insertMIHColumn(TableData mihTable) 
   {
      ColumnData newCol;
      String     colName;
      
      colName = MessageFormat.format(MIHLoadJob.PRIMARY_KEY_NAME_TEMPLATE, 
                                     new Object[] { mihTable.getName() });
      newCol  = mihTable.addColumn(colName, MIHLoadJob.KEY_COLUMN_LENGTH, 
                                   TableData.COLUMN_POSITION_BEGIN);
      newCol.setTransformerSrcMapping(colName);

      // set some additional required column properties
      newCol.setType(MIHLoadJob.KEY_COLUMN_TYPE);
      newCol.setDataObjectSourceType(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_JOINED_CHECK_AND_TEXT_TABLE);
      newCol.setIsUnicode(mihTable.isUnicodeSystem());
   } // end of insertMIHColumn()
   
   
   private void removeKeyAttribFromKeyColumns(TableData curTableData)
   {
      ColumnData colArr[];
      int        colIdx;
      int        nRemovedKeys;
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("table = " + curTableData);
      }
      
      nRemovedKeys = 0;
      colArr = curTableData.getColumnData();
      for(colIdx = 0; colIdx < colArr.length; colIdx ++)
      {
         if (colArr[colIdx].isKeyColumn())
         {
            colArr[colIdx].setIsKeyColumn(false);
            nRemovedKeys ++;
         }
      }
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit("Key attribute of '" + nRemovedKeys + "' columns removed.");
      }
   } // end of removeKeyAttribFromKeyColumns()

} // end of class MIHCodeTablesLoadJob
