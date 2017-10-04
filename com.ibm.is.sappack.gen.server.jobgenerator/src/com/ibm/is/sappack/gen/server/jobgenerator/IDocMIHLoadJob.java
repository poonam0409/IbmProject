//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2012                                            
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;

import ASCLModel.ConstraintUsageEnum;
import ASCLModel.LinkTypeEnum;
import DataStageX.DSDerivation;
import DataStageX.DSFilterConstraint;
import DataStageX.DSFlowVariable;
import DataStageX.DSInputPin;
import DataStageX.DSJobDef;
import DataStageX.DSLink;
import DataStageX.DSLocalContainerDef;

import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequest;
import com.ibm.is.sappack.gen.common.request.JobParamData;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeIDocMIHLoad;
import com.ibm.is.sappack.gen.common.request.SupportedColumnTypesMap;
import com.ibm.is.sappack.gen.common.request.SupportedTableTypesMap;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.jobgenerator.layout.DefaultContainerLayout;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.LookupStageProperties;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.SAPIDocExtractStageProperties;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.StageFactory;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.TransformerStageProperties;
import com.ibm.is.sappack.gen.server.util.ColumnData;
import com.ibm.is.sappack.gen.server.util.ContainerData;
import com.ibm.is.sappack.gen.server.util.GraphJobLink;
import com.ibm.is.sappack.gen.server.util.GraphJobNode;
import com.ibm.is.sappack.gen.server.util.ModelInfoBlock;
import com.ibm.is.sappack.gen.server.util.ObjectParamMap;
import com.ibm.is.sappack.gen.server.util.StageData;
import com.ibm.is.sappack.gen.server.util.TableData;


public class IDocMIHLoadJob extends IDocExtractJob {
   // -------------------------------------------------------------------------------------
   //                                    SubClasses
   // -------------------------------------------------------------------------------------
   private static class LookupFlowData {
      private StageData    _LookupStage;
      private DSLink       _LookupStageInputLink;
      private GraphJobNode _LookupGraphNode;
      private String       _LookupTableName;
      
      public LookupFlowData(StageData lookupStage, DSLink inputLink, 
                            GraphJobNode lookupNode, String lookupTableName) {
         _LookupGraphNode      = lookupNode;
         _LookupStage          = lookupStage;
         _LookupStageInputLink = inputLink;
         _LookupTableName      = lookupTableName;
      }
      
      public GraphJobNode getLookupGraphNode() {
         return(_LookupGraphNode);
      }
      public StageData getLookupStage() {
         return(_LookupStage);
      }
      public DSLink getLookupStageInputLink() {
         return(_LookupStageInputLink);
      }
      public String getLookupTableName() {
         return(_LookupTableName);
      }
   } // end of class LookupFlowData
   
	// -------------------------------------------------------------------------------------
	//                                    Constants
	// -------------------------------------------------------------------------------------
   private static final String CHECK_TABLE_NAME_TEMPLATE            = "JLT_{0}";
   private static final String CHECK_TABLE_DB_NAME_TEMPLATE         = "CD{0}";
   private static final String LINK_COLUMN_TEMPLATE                 = "{0}.{1}";
   private static final String CT_LOOKUP_CONDITION_COL_LINK         = " or ";
   private static final String CT_LOOKUP_COLUMN_CONDITION_TEMPLATE  = "IsNull({0}) or Len(Trim({0})) = 0";
   private static final String CT_LOOKUP_CONDITION_REVERSE_TEMPLATE = "Not({0})";
   
   
	// -------------------------------------------------------------------------------------
	//                                    Member Variables
	// -------------------------------------------------------------------------------------
   /** transformer mapping array */ 
   private TableData                               _TransformerMappingArr[];
   /** target IDoc segment array */ 
   private TableData                               _TargetTableArr[];
   /** contains the required Check Tables for a specific IDoc segment. 
       Layout: SegmentName=CheckTableMap(CTName=List<Columns>) */ 
   private Map<String, Map<String, List<String>>>  _ColumnsCheckMap;
   /** contains all check tables required by the IDoc Segment columns.
       Layout: CTName=TableData */ 
   private Map<String, TableData>                  _CheckTablesMap;
   /** contains all CLIENT ID columns (SAP type) that have been found in the check tables. 
       Layout: ColumnName=ColumnData */ 
   private Map                                     _CheckTablesClientIdMap;
   
   
	static String copyright() {
		return com.ibm.is.sappack.gen.server.jobgenerator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	
	public IDocMIHLoadJob(ServiceToken parContextToken, RequestJobTypeIDocMIHLoad parJobType,
	                      JobGeneratorRequest parJobReqInfo, Map<String, ModelInfoBlock> physModelID2TableMap) 
	       throws BaseException {
	   
		super(parContextToken, parJobType, parJobReqInfo, physModelID2TableMap);
	
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Replace Column Derivation for types: " 
                                                   + MIHLoadJob.COLUMN_DERIVATION_TYPE_CHAR + ", " 
                                                   + MIHLoadJob.COLUMN_DERIVATION_TYPE_VARCHAR);
      }
      
      // replace (existing??) column derivation functions with required column derivation functions ...
      parJobType.addColumDerivation(MIHLoadJob.COLUMN_DERIVATION_TYPE_CHAR, MIHLoadJob.COLUMN_DERIVATION_TRIM_FN);
      parJobType.addColumDerivation(MIHLoadJob.COLUMN_DERIVATION_TYPE_VARCHAR, MIHLoadJob.COLUMN_DERIVATION_TRIM_FN);

      _CheckTablesMap  = new HashMap<String, TableData>();
      _ColumnsCheckMap = new HashMap<String, Map<String, List<String>>>();
	} // end of IDocMIHLoadJob()


	private void addClientIDsToMapping(TableData parMapping) {
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("ClientId Map size = " + _CheckTablesClientIdMap.size());
      }

	   // mapping modification are just necessary if there is at least one Client Id
      if (_CheckTablesClientIdMap.size() > 0) {
         ColumnData  clientIdCol;
         ColumnData  newCol;
         Iterator    mapIter;
         Map.Entry   mapEntry;

         // create a copy of the mapping
         mapIter         = _CheckTablesClientIdMap.entrySet().iterator();
         while(mapIter.hasNext()) {
            mapEntry    = (Map.Entry) mapIter.next();
            clientIdCol = (ColumnData) mapEntry.getValue();

            newCol = parMapping.addColumn(clientIdCol.getName(), clientIdCol.getLength().intValue(), 
                                          TableData.COLUMN_POSITION_END);
            newCol.setTransformerSrcMapping(Constants.SAP_CLIENT_ID_JOB_PARAM_NAME);
            newCol.setType(clientIdCol.getType());
            newCol.setDataObjectSourceType(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_LOAD_JOB_FIELD);
            newCol.setIsUnicode(clientIdCol.isUnicode());
            newCol.setIsNullable(false);
            
            if (TraceLogger.isTraceEnabled()) {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "client id '" + newCol.getName() + "' added");
            }
         } // end of while(mapIter.hasNext())
      } // end of if (_CheckTablesClientIdMap.size() > 0)
         
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit();
      }
	} // end of addClientIDsToMapping()
	
	
   private void changeColumnTypeInTargetTable(String columnName, String columnMapping, 
                                              TableData targetTable)
   {
      ColumnData curColumnArr[];
      ColumnData curCol;
      ColumnData newCol;
//      int        tblIdx;
      int        colIdx;
      boolean    hasColumnProcessed;
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("Column = " + columnName + " - mapping = " + columnMapping);
      }
      
      // process the specified table
      // search for the column having the passed name ...
      hasColumnProcessed = false;
      curColumnArr       = targetTable.getColumnData();
      colIdx             = 0;
      while(colIdx < curColumnArr.length && !hasColumnProcessed) {
         
         curCol = curColumnArr[colIdx];
         
         if (curCol.getName().equals(columnName)) {
            targetTable.removeColumn(curCol.getName());
            
            // --> Target Table
            newCol = targetTable.addColumn(curCol.getName(), MIHLoadJob.KEY_COLUMN_LENGTH, colIdx);
            newCol.setType(MIHLoadJob.KEY_COLUMN_TYPE);
            newCol.setDataObjectSourceType(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_TYPE);
            newCol.setIsNullable(curCol.isNullable());
            newCol.setTransformerSrcMapping(columnMapping);
            
            hasColumnProcessed = true;
            
            if (TraceLogger.isTraceEnabled()) {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "column '" + curCol.getName() 
                                                         + "' has replaced.");
            }
         } // end of if (curCol.getName().equals(columnName))
         
         // next column
         colIdx ++;
      } // end of for (srcColIdx = 0; srcColIdx < curColumnArr.length; srcColIdx ++)
      
/*      
      // process all tables ...
      for (tblIdx = 0; tblIdx < _TargetTableArr.length; tblIdx ++) {
         
         targetTable = _TargetTableArr[tblIdx];
         
         // search for the column having the passed name ...
         hasColumnProcessed = false;
         curColumnArr       = targetTable.getColumnData();
         colIdx             = 0;
         while(colIdx < curColumnArr.length && !hasColumnProcessed) {
            
            curCol = curColumnArr[colIdx];
            
            if (curCol.getName().equals(columnName)) {
               targetTable.removeColumn(curCol.getName());
               
               // --> Target Table
               newCol = targetTable.addColumn(curCol.getName(), MIHLoadJob.KEY_COLUMN_LENGTH, colIdx);
               newCol.setType(MIHLoadJob.KEY_COLUMN_TYPE);
               newCol.setDataObjectSourceType(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_TYPE);
               newCol.setIsNullable(curCol.isNullable());
               newCol.setTransformerSrcMapping(columnMapping);
               
               hasColumnProcessed = true;
               
               if (TraceLogger.isTraceEnabled()) {
                  TraceLogger.trace(TraceLogger.LEVEL_FINEST, "column '" + curCol.getName() 
                                                            + "' has replaced.");
               }
            } // end of if (curCol.getName().equals(columnName))
            
            // next column
            colIdx ++;
         } // end of for (srcColIdx = 0; srcColIdx < curColumnArr.length; srcColIdx ++)
      } // end of for (tblIdx = 0; tblIdx < _TargetTableArr.length; tblIdx ++)
*/
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit();
      }
      
   } // end of changeColumnTypeInTargetTable()

   
	public List<String> create() throws BaseException {
	   
		RequestJobTypeIDocMIHLoad jtIDocMIHLoad;
		TableData                 srcTableArr[];
		List<String>              retJobsCreated;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		jtIDocMIHLoad  = (RequestJobTypeIDocMIHLoad) getJobType();
		retJobsCreated = new ArrayList<String>();

		// get the array of tables to be processed
		srcTableArr = loadRequiredTables(jtIDocMIHLoad.getPhysicalModelId());

		// ---------------------------
		// ==> process existing tables
		// ---------------------------
		if (srcTableArr.length > 0) {
		   // initialize the used maps
		   init(jtIDocMIHLoad, srcTableArr);
		   
			// we have to store the event of job creation failure
			boolean jobCreationError = false;

			try {
				// create a DS task container
				String curJobName = getJobType().getJobName();
				createJobDef(curJobName);

				// job gets added to the list of (successfully) created jobs
				// though job creation actually takes place later
				retJobsCreated.add(curJobName);

				// create the flow
				if (_CheckTablesMap.size() == 0)
				{
				   // no CheckTables ==> create 'regular' IDoc Extract Job 
				   super.createFlow(srcTableArr, _TargetTableArr);
				}
				else
				{
               // CheckTables exist ==> create 'extended' IDoc Extract Job
				   createFlowWithCheckTables(srcTableArr);
				} // end of (else) if (_CheckTablesMap.size() == 0)

				// save the job
				prepareAndSaveJob();
			}
			catch (DSAccessException pDSAccessExcpt) {
				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.traceException(pDSAccessExcpt);
				}

				// something went wrong during job creation
				jobCreationError = true;

            throw new JobGeneratorException("126100E", 
                                            new String[] { pDSAccessExcpt.getLocalizedMessage() }, 
                                            pDSAccessExcpt);
			}
			catch (JobGeneratorException pJobGenExcpt) {

				// something went wrong during job creation
				jobCreationError = true;

				throw pJobGenExcpt;
			}
			catch (Exception pExcpt) {
				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.traceException(pExcpt);
				}

				// something went wrong during job creation
				jobCreationError = true;

            throw new JobGeneratorException("101500E", 
                                            new String[] { pExcpt.getMessage() }, 
                                            pExcpt);
			}
			finally {

				// in case of job creation problems we simply remove
				// the last job from the list of (successfully) created jobs
				// because we cannot be sure that it indeed has been created
				if (jobCreationError) {

					// possibly an exception occurred BEFORE the job could actually
					// be added to the list of created jobs, therefore we must
					// check for the size of the list
					if (retJobsCreated.size() != 0) {
						retJobsCreated.remove(retJobsCreated.size() - 1);
					}
				}
			}
		} // end of if (srcTableArr.length > 0)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("Number of created jobs: " + retJobsCreated.size());
		}

		return (retJobsCreated);
	} // end of create()

	
   private void createColumnsCheckMapAndTransformerMapping(RequestJobTypeIDocMIHLoad jtIDocMIHLoad, 
                                                           TableData srcTableArr[]) 
           throws JobGeneratorException {
      
      TableData                 transformerMapping;
      ColumnData                curColumnArr[];
      ColumnData                curCol;
      String                    relatedCTName;
      String                    fullCTName;
      Map<String, List<String>> tblCheckMap;
      int                       tblIdx;
      int                       srcColIdx;
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("IDOC tables = " + srcTableArr.length 
                         + " - check tables = " + _CheckTablesMap.size());
      }
      
      // create the Transformer Mapping array based on the Source TableData array
      _TransformerMappingArr = new TableData[srcTableArr.length];

      // process all tables ...
      for (tblIdx = 0; tblIdx < _TransformerMappingArr.length; tblIdx ++) {
         
         // process all columns in that table ...
         transformerMapping = srcTableArr[tblIdx].getCopy();
         curColumnArr       = srcTableArr[tblIdx].getColumnData();
         tblCheckMap        = null;
         
         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "table = " + srcTableArr[tblIdx].getName());
         }
         
         tblCheckMap = new HashMap<String, List<String>>();
         for (srcColIdx = 0; srcColIdx < curColumnArr.length; srcColIdx ++) {
            
            curCol        = curColumnArr[srcColIdx];
            relatedCTName = curCol.getRelatedCheckTable();
            
            if (TraceLogger.isTraceEnabled()) {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "column = " + curCol.getName());
            }
            
            if (relatedCTName != null) {
               fullCTName = MessageFormat.format(CHECK_TABLE_NAME_TEMPLATE, 
                                                 new Object[] { relatedCTName } );

               if (_CheckTablesMap.containsKey(fullCTName)) {
                  
                  // check if there is already a column associated with current check table 
                  List<String> colList;
                  
                  if (tblCheckMap.containsKey(fullCTName)) {
                     // yes ==> add the current column to the list
                     colList = tblCheckMap.get(fullCTName);
                  }
                  else {
                     // no ==> create a new (columns) list
                     colList = new ArrayList<String>();
                     tblCheckMap.put(fullCTName, colList);
                     
//                     tblCheckMap.put(curCol.getName(), fullCTName);
                  }
                  colList.add(curCol.getName());

                  // then replace that column in the transformer mapping ... 
                  if (jtIDocMIHLoad.useOneLookupStage()) {
                     // but only we have to use one lookup stage
                     replaceTransformerForeignKeyColumn(transformerMapping, 
                                                        fullCTName, curCol);
                  }
                  
                  // change column type in the target table
                  changeColumnTypeInTargetTable(curCol.getName(), curCol.getName(), 
                                                _TargetTableArr[tblIdx]);
               }
               else {
                  if (TraceLogger.isTraceEnabled()) {
                     TraceLogger.trace(TraceLogger.LEVEL_FINE, 
                                       "Data inconsistency detected: No check table found for '" + relatedCTName + "'.");
                  }
                  
                  throw new JobGeneratorException("126600E", new String[] { relatedCTName } );
               } // end of (else) if (_CheckTablesMap.containsKey(fullCTName))
            } // end of if (relatedCTName != null)
         } // end of for (srcColIdx = 0; srcColIdx < curColumnArr.length; srcColIdx ++)
         
         // add the check map
         if (tblCheckMap.size() > 0) {
            _ColumnsCheckMap.put(transformerMapping.getName(), tblCheckMap);
         }
         
         // save the modified Target table
         _TransformerMappingArr[tblIdx] = transformerMapping;
      } // end of for (tblIdx = 0; tblIdx < _TransformerTableArr.length; tblIdx ++)
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit("Check Tables Map size: " + _ColumnsCheckMap.size());
      }
   } // end of createColumnsCheckMapAndTransformerMapping()
	
	
   private void createFlowWithCheckTables(TableData parSrcTableArr[]) 
           throws BaseException
   {
      RequestJobTypeIDocMIHLoad jobType;
      GraphJobNode              sourceNode;
      GraphJobNode              containerNode;
      GraphJobLink              transformerLink;
      GraphJobLink              odbcLink;
      TableData                 curSrcTable;
      TableData                 curTargetTable;
      TableData                 curTransformerMapping;
      TableData                 curSAPStageMapping;
      StageData                 newContainerStage;
      StageData                 newIDOCStage;
      StageData                 newPersStage;
      DataStageObjectFactory    dsFactory;
      DSJobDef                  jobDef;
      DSLink                    stageLink;
      String                    stageIDOCTypeName;
      boolean                   isExtendedIDocType;
      ObjectParamMap            linkParamMap;
      int                       tblIdx;

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("SAP IDoc Extract tables to be processed: " + parSrcTableArr.length);
      }

      // number of source and target tables must be equal ...
      if (parSrcTableArr.length != _TargetTableArr.length) {
         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.trace(TraceLogger.LEVEL_FINE, "Number of source and target tables are not equal");
         }
         
         throw new JobGeneratorException("126300E", Constants.NO_PARAMS);
      } // end of if (parSrcTableArr.length != _TargetTableArr.length)

      dsFactory          = getDSFactory();
      jobDef             = _JobStructureData.getJobDef();
      curSrcTable        = parSrcTableArr[0];
      stageIDOCTypeName  = curSrcTable.getIDocType();
      isExtendedIDocType = curSrcTable.getIsExtendedIDocType();
      jobType            = (RequestJobTypeIDocMIHLoad) getJobType();

      // *****************************
      // INPUT: SAP IDOC Extract Stage
      // *****************************
      newIDOCStage = StageFactory.createIDOCExtractStage(jobDef, stageIDOCTypeName, isExtendedIDocType,
                                                         curSrcTable.getIDocBasicType(),
                                                         curSrcTable.getSegmentType(), 
                                                         getJobType().getJobName(), jobType, 
                                                         curSrcTable.isUnicodeSystem(), 
                                                         _JobRequestInfo.doCreateV7Stage(),
                                                         getServiceToken());

      // update JobStructureData ...
      // ===========================
      sourceNode = new GraphJobNode(newIDOCStage);
      _JobStructureData.getLayoutData().addSourceNode(sourceNode);

      for (tblIdx = 0; tblIdx < parSrcTableArr.length; tblIdx++) {
         curSrcTable           = parSrcTableArr[tblIdx];
         curTargetTable        = _TargetTableArr[tblIdx];
         curTransformerMapping = _TransformerMappingArr[tblIdx].getCopyForSAPMapping();
         curSAPStageMapping    = curSrcTable.getCopyForSAPMapping();
         // tableName = curTable.getName();

         // current IDOC type must match with first IDOC type
         // and there must be any columns to be processed
         if (stageIDOCTypeName.equals(curSAPStageMapping.getIDocType()) && 
             jobType.doProcessTable(curSAPStageMapping.getTableType())  && 
             curSAPStageMapping.getColumnData().length > 0) {
            
            // different layout if table contains any columns that has related CheckTables
            if (_ColumnsCheckMap.containsKey(curSAPStageMapping.getName())) {
               // Some Check Tables exist
               // ==> 'Single Extract Flow' with IDocStage ---> Transformer ---> PersistenceStage
               // ------------------------------------------------------------------------------------
               // ***************
               // Container Stage
               // ***************
               newContainerStage = StageFactory.createContainerStage(jobDef, curSrcTable.getSegmentDefinition(), dsFactory);
               containerNode     = new GraphJobNode(newContainerStage);

               // ---------------------------------------------------------------------------
               // create links: IDOC Extract Stage <------> Container Stage (Local Container)
               // ---------------------------------------------------------------------------
               // set IDOC Extract Stage Link properties
               // --------------------------------------
               linkParamMap = SAPIDocExtractStageProperties.getDefaultLinkParams();
               linkParamMap.put(SAPIDocExtractStageProperties.LNK_PROP_SEG_TYPE_KEY, curSrcTable.getSegmentType());
               linkParamMap.put(SAPIDocExtractStageProperties.LNK_PROP_OBJECT_NAME_KEY, curSrcTable.getName());
               linkParamMap.put(SAPIDocExtractStageProperties.LNK_PROP_SEG_NAME_KEY, curSrcTable.getSegmentDefinition());
               linkParamMap.put(SAPIDocExtractStageProperties.LNK_PROP_CONNECTION_NAME_KEY, 
                                jobType.getSAPSystem().getSAPSystemName());
               linkParamMap.put(SAPIDocExtractStageProperties.LNK_PROP_IDOC_TYPE_NAME_KEY, stageIDOCTypeName);

               // and then create the column mapping (link)
               stageLink = createColumnMapping(newIDOCStage, newContainerStage, curSAPStageMapping, 
                                               linkParamMap, dsFactory);

               // update JobStructureData ...
               // ===========================
               transformerLink = new GraphJobLink(stageLink, containerNode);
               sourceNode.addOutLink(transformerLink);

               // ******************
               // OUTPUT: ODBC stage
               // ******************
               newPersStage = StageFactory.createPersistenceTargetStage(jobDef, 
                                                                        curTargetTable, 
                                                                        jobType.getPersistenceData(), 
                                                                        getServiceToken());

               // -------------------------------------------------------
               // create link: Container Stage <------> Persistence Stage 
               // -------------------------------------------------------
               //
               // "NO MAPPING ALLOWED" here --> TableData is null 
               //
               stageLink = createColumnMapping(newContainerStage, newPersStage, null, 
                                               newPersStage.getLinkParams(), dsFactory);

               // update JobStructureData ...
               // ===========================
               odbcLink = new GraphJobLink(stageLink, new GraphJobNode(newPersStage));
               containerNode.addOutLink(odbcLink);
               
               // and then create the Local Container flow
               if (jobType.useOneLookupStage()) {
                  createContainerFlowOneLookupStage(newContainerStage.getContainerData(), curSAPStageMapping, 
                                                    curTransformerMapping, curTargetTable);
               }
               else {
                  createContainerFlowManyLookupStages(newContainerStage.getContainerData(), curSAPStageMapping, 
                                                      curTransformerMapping, curTargetTable);
               }
            }
            else
            {
               // No related Check Tables exist
               // ==> 'Single Extract Flow' with IDocStage ---> Transformer ---> PersistenceStage
               // ------------------------------------------------------------------------------------
               createSingleExtractFlow(curSAPStageMapping, curTargetTable, stageIDOCTypeName, 
                                       jobType, newIDOCStage, sourceNode, _TargetTableArr);
            } // end of (else) if (_ColumnsCheckMap.containsKey(curSrcTable.getName()))
         }
         else {
            if (curSrcTable.getColumnData().length == 0) {
               if (TraceLogger.isTraceEnabled()) {
                  TraceLogger.trace(TraceLogger.LEVEL_FINE, "Table '" + curSrcTable.getName()
                                                          + "' has no colums to be processed.");
               }
            }
            else {
               if (!jobType.doProcessTable(curSrcTable.getTableType())) {
                  if (TraceLogger.isTraceEnabled()) {
                     TraceLogger.trace(TraceLogger.LEVEL_FINE, "Table '" + curSrcTable.getName()
                                                             + "' is not a required table.");
                  }
               }
               else {
                  if (TraceLogger.isTraceEnabled()) {
                     TraceLogger.trace(TraceLogger.EVENT, "IDOC Type does not match with 'first' IDOC Type !!!!");
                  }
               }
            } // end of if (pTable.getColumnData().length > 0)
         } // end of (else) if (stageIDOCTypeName.equals(curTable.getIDocType()) ... curTable.getColumnData().length >
      } // end of for (tblIdx = 0; tblIdx < pTableArr.length; tblIdx++)

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit();
      }
      
   } // end of createFlowWithCheckTables()
	
   
   private void createContainerFlowManyLookupStages(ContainerData parContainer, 
                                                    TableData parSourceMapping, 
                                                    TableData parTransformerMapping, 
                                                    TableData parTargetMapping)   
           throws BaseException {
      
      RequestJobTypeIDocMIHLoad   jobType;
      GraphJobNode                sourceNode;
      GraphJobNode                transformerNode;
      GraphJobNode                lookupNode;
      GraphJobNode                prevGraphNode;
      GraphJobLink                lookupLink;
      GraphJobLink                transformerLink;
      GraphJobLink                containerLink;
      GraphJobLink                persistenceLink;
      StageData                   newTransformerStage;
      StageData                   newLookupStage;
      StageData                   lookupPersStage;
      StageData                   prevStage;
      TableData                   transformerMapping;
      TableData                   lookupMapping;
      ObjectParamMap              linkParamMap;
      DataStageObjectFactory      dsFactory;
      DSLocalContainerDef         jobObjContainer;
      DSLink                      baseDataLink;
      DSLink                      stageLink;
      String                      lookupColName;
      String                      lookupTableName;
      String                      mihCodeTableName;
      List                        lookupColList;
      List<ColumnData>            prevLookupColDataList;
      Map                         relatedColCheckMap;
      Map<String, LookupFlowData> lookupStageMap;
      Iterator                    mapIter;
      Iterator                    listIter;
      Map.Entry                   mapEntry;
      boolean                     is1stLoopRun;


      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("SAP IDoc Extract mapping: " + parSourceMapping.getName());
      }

      dsFactory       = getDSFactory();
      jobType         = (RequestJobTypeIDocMIHLoad) getJobType();
      jobObjContainer = (DSLocalContainerDef) parContainer.getJobComponent();
      _JobStructureData.setContainerLayout(jobObjContainer, new DefaultContainerLayout());

      // update JobStructureData ...
      // ===========================
      sourceNode = new GraphJobNode(parContainer);
      _JobStructureData.getLayoutData(parContainer.getJobComponent()).addSourceNode(sourceNode);

      // ******************************************
      // Local Container ======>> Transformer Stage
      // ******************************************
      newTransformerStage = StageFactory.createTransformerStage(jobObjContainer, dsFactory, null);
      transformerNode     = new GraphJobNode(newTransformerStage);


      // --------------------------------------------------
      // create link: Container (entry) ------> Transformer
      // WITHOUT any mapping -> the mapping of the parent will be used
      // --------------------------------------------------
      stageLink = createColumnMapping(parContainer, newTransformerStage,
                                      TransformerStageProperties.getDefaultLinkParams(), 
                                      dsFactory);
      
      // update JobStructureData ...
      // ===========================
      transformerLink = new GraphJobLink(stageLink, transformerNode);
      sourceNode.addOutLink(transformerLink);

      // add existing CLIENT ID columns to the transformer mapping
      transformerMapping = parTransformerMapping.getCopy();
      addClientIDsToMapping(transformerMapping);
      
      // ******************************************
      // Transformer Stage ======>> Lookup Stage(s)
      // ******************************************
      prevGraphNode         = transformerNode;
      prevStage             = newTransformerStage;
      prevLookupColDataList = new ArrayList<ColumnData>();
      
      // create a Lookup Stage for each check table
      // ------------------------------------------
      is1stLoopRun       = true;
      lookupStageMap     = new HashMap<String, LookupFlowData>();
      relatedColCheckMap = (Map) _ColumnsCheckMap.get(parSourceMapping.getName());
      mapIter            = relatedColCheckMap.entrySet().iterator();
      while(mapIter.hasNext()) {
         
         mapEntry        = (Map.Entry) mapIter.next();
         lookupTableName = (String) mapEntry.getKey();

         // create the Lookup Stage
         // -----------------------
         newLookupStage = StageFactory.createLookupStage(jobObjContainer, dsFactory);
         lookupNode     = new GraphJobNode(newLookupStage);

         // create the mapping
         // ------------------
         // ----------------------------------------------------
         // create links: 'Previous' Stage <------> Lookup Stages
         // ----------------------------------------------------
         // then create the link between the transformer stage and lookup stage
         baseDataLink = createColumnMapping(prevStage, newLookupStage, transformerMapping,
                                            newLookupStage.getLinkParams(), dsFactory);

         // after the first mapping make sure that the CLIENT ID is mapped to a column 
         if (is1stLoopRun) {
            modifyClientIDsMapping(transformerMapping);
            is1stLoopRun = false;
         }

         // save the Lookup stage for later mapping
         lookupStageMap.put(lookupTableName, new LookupFlowData(newLookupStage, baseDataLink, 
                                                                lookupNode, lookupTableName));
         
         // 'do forget' that we have a special lookup key mapping 
         // ==> use the regular mapping 
         listIter = prevLookupColDataList.iterator();
         while(listIter.hasNext()) {
            ColumnData tmpColData = (ColumnData) listIter.next();
            tmpColData.setTransformerSrcMapping(tmpColData.getName());
         } // end of while(listIter.hasNext())
         prevLookupColDataList.clear();
         
         // one lookup link for each lookup column
         lookupColList = (List) mapEntry.getValue();
         listIter = lookupColList.iterator();
         while(listIter.hasNext()) {
            ColumnData tmpColData;
            
            lookupColName = (String) listIter.next();
            tmpColData    = replaceTransformerForeignKeyColumn(transformerMapping, 
                                                               lookupTableName, lookupColName);
            prevLookupColDataList.add(tmpColData);
         } // end of while(listIter.hasNext())
         
         // update JobStructureData ...
         // ===========================
         lookupLink = new GraphJobLink(baseDataLink, lookupNode, GraphJobLink.LINK_DIRECTION_DOWN);
         prevGraphNode.addOutLink(lookupLink);

         prevStage     = newLookupStage;
         prevGraphNode = lookupNode;
      } // end of while(mapIter.hasNext())
         
      // ***************************************
      // Lookup Stage ======>> Transformer Stage
      // ***************************************
      newTransformerStage = StageFactory.createTransformerStage(jobObjContainer, dsFactory, 
                                                                TransformerStageProperties.getDefaultStageParams());
      transformerNode     = new GraphJobNode(newTransformerStage);


      // ---------------------------------------
      // create link: Lookup ------> Transformer
      // ---------------------------------------
      removeClientIDsFromMapping(transformerMapping);
      stageLink = createColumnMapping(prevStage, newTransformerStage, transformerMapping,
                                      prevStage.getLinkParams(), dsFactory);
      
      
      // update JobStructureData ...
      // ===========================
      transformerLink = new GraphJobLink(stageLink, transformerNode);
      prevGraphNode.addOutLink(transformerLink);

      // ******************************************
      // Transformer Stage ======>> Local Container 
      // ******************************************
      // create link: Transformer ------> Container (exit)
      stageLink = createColumnMapping(newTransformerStage, parContainer, parTargetMapping, 
                                      null, dsFactory);

      // update JobStructureData ...
      // ===========================
      containerLink = new GraphJobLink(stageLink, new GraphJobNode(parContainer));
      transformerNode.addOutLink(containerLink);


      // ***********************************************
      // Add Reference (Persistence) stages (for lookup) 
      // ***********************************************
      mapIter = lookupStageMap.entrySet().iterator();
      while(mapIter.hasNext()) {
         LookupFlowData  luFlowData;
         
         mapEntry        = (Map.Entry) mapIter.next();
         luFlowData      = (LookupFlowData) mapEntry.getValue();
         lookupTableName = luFlowData.getLookupTableName();
         lookupMapping   = (TableData) _CheckTablesMap.get(lookupTableName);
         newLookupStage  = luFlowData.getLookupStage();
         
         // since MIH prefixes the check tables --> prefix the lookup table name
         mihCodeTableName = MessageFormat.format(CHECK_TABLE_DB_NAME_TEMPLATE, 
                                                 new Object[] { lookupTableName } );

         // one lookup link for each column
         lookupColList = (List) relatedColCheckMap.get(lookupTableName);
         listIter = lookupColList.iterator();
         while(listIter.hasNext()) {
            lookupColName = (String) listIter.next();
         
            if (TraceLogger.isTraceEnabled()) {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "create CT Persistence Stage for column '" + lookupColName + "'.");
            }
            
            TableData tmpMapping = lookupMapping.getCopy(mihCodeTableName);
            lookupPersStage = StageFactory.createPersistenceSourceStage(jobObjContainer, 
                                                                        tmpMapping, 
                                                                        jobType.getPersistenceData(), 
                                                                        null, dsFactory);
            sourceNode      = new GraphJobNode(lookupPersStage);
            _JobStructureData.getLayoutData(jobObjContainer).addSourceNode(sourceNode);
            
            if (TraceLogger.isTraceEnabled()) {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "create Link 'CT Stage --> LookUpStage.");
            }
            
            // -------------------------------------------------
            // create reference link: Persistence ------> Lookup
            // -------------------------------------------------
            linkParamMap    = ObjectParamMap.concat(lookupPersStage.getLinkParams(), newLookupStage.getLinkParams());
            stageLink       = createColumnMapping(lookupPersStage, newLookupStage, lookupMapping,
                                                  linkParamMap, LinkTypeEnum.REFERENCE_LITERAL, dsFactory);
            persistenceLink = new GraphJobLink(stageLink, luFlowData.getLookupGraphNode(), 
                                               GraphJobLink.LINK_DIRECTION_LEFT);

            // setup lookup condition
            setupLookupCondition(stageLink, luFlowData.getLookupStageInputLink(), 
                                 lookupMapping, dsFactory);

            // update JobStructureData ...
            // ===========================
            sourceNode.addOutLink(persistenceLink);
         } // end of while(listIter.hasNext())
      } // end of while(mapIter.hasNext())
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit();
      }
      
   } // end of createContainerFlowManyLookupStages()
   
   
   private void createContainerFlowOneLookupStage(ContainerData parContainer, 
                                                  TableData parSourceMapping, 
                                                  TableData parTransformerMapping, 
                                                  TableData parTargetMapping)   
           throws BaseException {

      RequestJobTypeIDocMIHLoad jobType;
      GraphJobNode              sourceNode;
      GraphJobNode              transformerNode;
      GraphJobNode              lookupNode;
      GraphJobLink              lookupLink;
      GraphJobLink              transformerLink;
      GraphJobLink              containerLink;
      GraphJobLink              persistenceLink;
      StageData                 newTransformerStage;
      StageData                 newLookupStage;
      StageData                 lookupPersStage;
      TableData                 transformerMapping;
      TableData                 lookupMapping;
      ObjectParamMap            linkParamMap;
      DataStageObjectFactory    dsFactory;
      DSLocalContainerDef       jobObjContainer;
      DSLink                    baseDataLink;
      DSLink                    stageLink;
      String                    lookupColName;
      String                    lookupTableName;
      String                    mihCodeTableName;
      List                      lookupColList;
      Map                       relatedColCheckMap;
      Iterator                  mapIter;
      Iterator                  listIter;
      Map.Entry                 mapEntry;


      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("SAP IDoc Extract mapping: " + parSourceMapping.getName());
      }

      dsFactory       = getDSFactory();
      jobType         = (RequestJobTypeIDocMIHLoad) getJobType();
      jobObjContainer = (DSLocalContainerDef) parContainer.getJobComponent();
      _JobStructureData.setContainerLayout(jobObjContainer, new DefaultContainerLayout());

      // update JobStructureData ...
      // ===========================
      sourceNode = new GraphJobNode(parContainer);
      _JobStructureData.getLayoutData(parContainer.getJobComponent()).addSourceNode(sourceNode);

      // ******************************************
      // Local Container ======>> Transformer Stage
      // ******************************************
      newTransformerStage = StageFactory.createTransformerStage(jobObjContainer, dsFactory, null);
      transformerNode     = new GraphJobNode(newTransformerStage);


      // --------------------------------------------------
      // create link: Container (entry) ------> Transformer
      // WITHOUT any mapping -> the mapping of the parent will be used
      // --------------------------------------------------
      stageLink = createColumnMapping(parContainer, newTransformerStage,
                                      TransformerStageProperties.getDefaultLinkParams(), 
                                      dsFactory);

      // update JobStructureData ...
      // ===========================
      transformerLink = new GraphJobLink(stageLink, transformerNode);
      sourceNode.addOutLink(transformerLink);


      // ***************************************
      // Transformer Stage ======>> Lookup Stage
      // ***************************************
      newLookupStage = StageFactory.createLookupStage(jobObjContainer, dsFactory);
      lookupNode     = new GraphJobNode(newLookupStage);

      // add existing CLIENT ID columns to the transformer mapping
      transformerMapping = parSourceMapping.getCopy();
      addClientIDsToMapping(transformerMapping);

      // ---------------------------------------------------
      // create link: TransformerStage <------> Lookup Stage
      // ---------------------------------------------------
      // create the link between the transformer stage and lookup stage 
      baseDataLink = createColumnMapping(newTransformerStage, newLookupStage, transformerMapping,
                                         newLookupStage.getLinkParams(), dsFactory);

      // update JobStructureData ...
      // ===========================
      lookupLink = new GraphJobLink(baseDataLink, lookupNode);
      transformerNode.addOutLink(lookupLink);

      // ***************************************
      // Lookup Stage ======>> Transformer Stage
      // ***************************************
      newTransformerStage = StageFactory.createTransformerStage(jobObjContainer, dsFactory, 
                                                                TransformerStageProperties.getDefaultStageParams());
      transformerNode     = new GraphJobNode(newTransformerStage);


      // ---------------------------------------
      // create link: Lookup ------> Transformer
      // ---------------------------------------
      stageLink = createColumnMapping(newLookupStage, newTransformerStage, parTransformerMapping,
                                      newLookupStage.getLinkParams(), dsFactory);


      // update JobStructureData ...
      // ===========================
      transformerLink = new GraphJobLink(stageLink, transformerNode);
      lookupNode.addOutLink(transformerLink);

      // ******************************************
      // Transformer Stage ======>> Local Container 
      // ******************************************
      // create link: Transformer ------> Container (exit)
      stageLink = createColumnMapping(newTransformerStage, parContainer, parTargetMapping, 
                                      null, dsFactory);

      // update JobStructureData ...
      // ===========================
      containerLink = new GraphJobLink(stageLink, new GraphJobNode(parContainer));
      transformerNode.addOutLink(containerLink);


      // ***********************************************
      // Add Reference (Persistence) stages (for lookup) 
      // ***********************************************
      relatedColCheckMap = (Map) _ColumnsCheckMap.get(parSourceMapping.getName());
      mapIter            = relatedColCheckMap.entrySet().iterator();
      while(mapIter.hasNext()) {
         mapEntry        = (Map.Entry) mapIter.next();
         lookupTableName = (String) mapEntry.getKey();
         lookupColList   = (List) mapEntry.getValue();
         lookupMapping   = (TableData) _CheckTablesMap.get(lookupTableName);
         
         // one run for each column ...
         listIter = lookupColList.iterator();
         while(listIter.hasNext()) {
            lookupColName = (String) listIter.next();
            
            // since MIH prefixes the check tables --> prefix the lookup table name
            mihCodeTableName = MessageFormat.format(CHECK_TABLE_DB_NAME_TEMPLATE, 
                                                    new Object[] { lookupTableName } );

            if (TraceLogger.isTraceEnabled()) {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
                                 "create CT Persistence Stage for column '" + lookupColName + "'.");
            }

            TableData tmpPersMapping = lookupMapping.getCopy(mihCodeTableName);
            lookupPersStage = StageFactory.createPersistenceSourceStage(jobObjContainer, 
                                                                        tmpPersMapping, 
                                                                        jobType.getPersistenceData(), 
                                                                        null, dsFactory);
            sourceNode      = new GraphJobNode(lookupPersStage);
            _JobStructureData.getLayoutData(jobObjContainer).addSourceNode(sourceNode);

            if (TraceLogger.isTraceEnabled()) {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "create Link 'CT Stage --> LookUpStage.");
            }

            // -------------------------------------------------
            // create reference link: Persistence ------> Lookup
            // -------------------------------------------------
            linkParamMap    = ObjectParamMap.concat(lookupPersStage.getLinkParams(), newLookupStage.getLinkParams());
            stageLink       = createColumnMapping(lookupPersStage, newLookupStage, lookupMapping,
                                                  linkParamMap, LinkTypeEnum.REFERENCE_LITERAL, dsFactory);
            persistenceLink = new GraphJobLink(stageLink, lookupNode);

            // setup lookup condition
            setupLookupCondition(stageLink, baseDataLink, lookupMapping, dsFactory);

            // update JobStructureData ...
            // ===========================
            sourceNode.addOutLink(persistenceLink);
         } // end of while(listIter.hasNext())         
      } // end of while(mapIter.hasNext())

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit();
      }

   } // end of createContainerFlowOneLookupStage()


   private String getSourceNameFromLink(EList flowVarList, String columnName) {
      DSFlowVariable dsFlowVar;
      DSDerivation   dsColDerivation;
      String         retSourceName;
      EList          derivationsList;
      Iterator       flowVarListIter;
      
      retSourceName   = null;
      flowVarListIter = flowVarList.iterator();
      while(flowVarListIter.hasNext() && retSourceName == null) {
         dsFlowVar = (DSFlowVariable) flowVarListIter.next();
         
         // is the current FlowVariable the column we are looking for ?
         if (dsFlowVar.getName().equals(columnName)) {
            // get its lookup derivation ...
            derivationsList = dsFlowVar.getHasLookup_Derivation();
            
            if (derivationsList.size() > 0) {
               dsColDerivation = (DSDerivation) derivationsList.get(0);
               
               retSourceName = dsColDerivation.getParsedExpression();
            }
            
         } // end of if (dsFlowVar.getName().equals(columnName))
      } // end of while(flowVarListIter.hasNext() && retSourceName == null)
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
                           "Source of column '" + columnName + "': " + retSourceName);
      }
      
      return(retSourceName);
   } // end of getSourceNameFromLink() 
   
   
   protected static Map getClientIdsFromCheckTables(Map checkTablesMap) {
      
      TableData               checkTable;
      ColumnData              columArr[];
      String                  sapDataType;
      Map<String, ColumnData> clientIDMap;
      Iterator                entrySetIter;
      Map.Entry               mapEntry;      
      int                     colIdx;
      boolean                 doesExist;

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry();
      }

      clientIDMap = new HashMap<String, ColumnData>();
      doesExist   = false;
      
      // process all columns of all check tables  until the first CLIENT Id is found
      entrySetIter = checkTablesMap.entrySet().iterator();
      while (entrySetIter.hasNext() && !doesExist) {
         mapEntry = (Map.Entry) entrySetIter.next();
         checkTable = (TableData) mapEntry.getValue();
         
         // check the columns ...
         columArr = checkTable.getColumnData();
         colIdx   = 0;
         while (colIdx < columArr.length && !doesExist) {
            sapDataType = columArr[colIdx].getSAPDataType(); 
            if (sapDataType != null && sapDataType.equals(Constants.SAP_DATA_TYPE_CLIENT_ID)) {
               
               // save column that has the SAP type CLIENT ID
               clientIDMap.put(columArr[colIdx].getName(), columArr[colIdx]);
               
// remove the flag if you want to have all different CLIENT ID's in the map 
               doesExist = true;
            }
            else {
               colIdx ++;
            } // end of if (sapDataType != null && sapDataType.equals(Constants.SAP_DATA_TYPE_CLIENT_ID))
         } // end of while (colIdx < columArr.length && !doesExist)
      } // end of while (entrySetIter.hasNext() && !doesExist)
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit("client id exists = " + doesExist);
      }
      
      return(clientIDMap);
   } // end of getClientIdsFromCheckTables()
   
   
   private void init(RequestJobTypeIDocMIHLoad jtIDocMIHLoad, TableData srcTableArr[]) 
           throws JobGeneratorException {

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry();
      }

      // copy the source table and add additional MIH columns
      _TargetTableArr = MIHLoadJob.createTargetTables(srcTableArr, jtIDocMIHLoad.getInstanceIdentifier());

      // get the CheckTables that are involved ...
      loadCheckTables(jtIDocMIHLoad.getPhysicalModelId());
      
      // create the mapping for columns to be checked 
      createColumnsCheckMapAndTransformerMapping(jtIDocMIHLoad, srcTableArr);
      
      // get list (map) of existing CLIENT IDs which exist in the check tables
      _CheckTablesClientIdMap = getClientIdsFromCheckTables(_CheckTablesMap); 
      if (_CheckTablesClientIdMap.size() > 0)
      {
         // ==> create an additional Job Parameter
         JobParamData clientIdJobParam = new JobParamData(Constants.SAP_CLIENT_ID_JOB_PARAM_NAME, 
                                                          Constants.SAP_CLIENT_ID_JOB_PARAM_NAME, 
                                                          JobParamData.JOB_PARAM_TYPE_STRING, 
                                                          "", "");
         _JobRequestInfo.addJobParameter(clientIdJobParam);
      } // end of if (_CheckTablesClientIdMap.size() > 0)
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit("target tables = " + _TargetTableArr.length);
      }
   } // end of init()
   
   
   private void loadCheckTables(String parPhysicalModelId) {
      
      ColumnData     newCol;
      TableData      tmpCTTableArr[];
      TableData      newCTTableWithKeys;
      ModelInfoBlock modelInfoBlk;
      String         pkIdKeyName;
      Map            tableSetMap;
      int            arrIdx;

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("Physical Model Id = " + parPhysicalModelId);
      }

      // get the table set for the passed Physical Model Id ...
      modelInfoBlk =(ModelInfoBlock) _PhysModelID2TableMap.get(parPhysicalModelId);
      tableSetMap  = modelInfoBlk.getTableMap();
      
      // get the CheckTables array from tables map
      tmpCTTableArr = (TableData[]) tableSetMap.get(SupportedTableTypesMap.TABLE_TYPE_ENUM_JOINED_CHECK_AND_TEXT_TABLE);

      // create a map from the copied Check Tables with table name as key
      for (arrIdx = 0; arrIdx < tmpCTTableArr.length; arrIdx ++) {
         // get a copy of the Check Table just containing the key columns
         newCTTableWithKeys = tmpCTTableArr[arrIdx].getCopyWithKeyCols();

         // add the PK ID column
         pkIdKeyName = MessageFormat.format(MIHLoadJob.PRIMARY_KEY_NAME_TEMPLATE, 
                                            new Object[] { newCTTableWithKeys.getName() } );
         newCol = newCTTableWithKeys.addColumn(pkIdKeyName, MIHLoadJob.KEY_COLUMN_LENGTH, 
                                               TableData.COLUMN_POSITION_BEGIN);
         newCol.setType(MIHLoadJob.KEY_COLUMN_TYPE);
         newCol.setDataObjectSourceType(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_JOINED_CHECK_AND_TEXT_TABLE);
         newCol.setIsNullable(false);
         newCol.setTransformerSrcMapping(pkIdKeyName);
         
         _CheckTablesMap.put(newCTTableWithKeys.getName(), newCTTableWithKeys);
         
      } // end of for (arrIdx = 0; arrIdx < tmpCTTableArr.length; arrIdx ++)

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit("Number of Check Tables: " + _CheckTablesMap.size());
      }
   } // end of loadCheckTables()
   
   
   private void modifyClientIDsMapping(TableData parMapping) {
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("ClientId Map size = " + _CheckTablesClientIdMap.size());
      }

      // mapping modification are just necessary if there is at least one Client Id
      if (_CheckTablesClientIdMap.size() > 0) {
         ColumnData  clientIdCol;
         ColumnData  modColumn;
         Iterator    mapIter;
         Map.Entry   mapEntry;

         mapIter = _CheckTablesClientIdMap.entrySet().iterator();
         while(mapIter.hasNext()) {
            mapEntry    = (Map.Entry) mapIter.next();
            clientIdCol = (ColumnData) mapEntry.getValue();

            // set 1:1 mapping for CLIENT IDs
            modColumn = parMapping.getColumn(clientIdCol.getName());
            modColumn.setTransformerSrcMapping(clientIdCol.getName());
            
            if (TraceLogger.isTraceEnabled()) {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "client id '" + modColumn.getName() + "' modified");
            }
         } // end of while(mapIter.hasNext())
      } // end of if (_CheckTablesClientIdMap.size() > 0)
         
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit();
      }
   } // end of modifyClientIDsMapping()
   
   
   private void removeClientIDsFromMapping(TableData parMapping) {
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("ClientId Map size = " + _CheckTablesClientIdMap.size());
      }

      // mapping modification are just necessary if there is at least one Client Id
      if (_CheckTablesClientIdMap.size() > 0) {
         ColumnData  clientIdCol;
         Iterator    mapIter;
         Map.Entry   mapEntry;

         mapIter = _CheckTablesClientIdMap.entrySet().iterator();
         while(mapIter.hasNext()) {
            mapEntry    = (Map.Entry) mapIter.next();
            clientIdCol = (ColumnData) mapEntry.getValue();

            parMapping.removeColumn(clientIdCol.getName());
            
            if (TraceLogger.isTraceEnabled()) {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "client id '" + clientIdCol.getName() + "' removed");
            }
         } // end of while(mapIter.hasNext())
      } // end of if (_CheckTablesClientIdMap.size() > 0)
         
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit();
      }
   } // end of removeClientIDsFromMapping()
   
   
   private ColumnData replaceTransformerForeignKeyColumn(TableData curTransformerMapping, String curChkTableName, 
                                                         String columnName) {

      ColumnData colData;
      ColumnData coldataArr[];
      int        colArrIdx;

      // search for the column having the specified name
      coldataArr = curTransformerMapping.getColumnData();
      colData    = null;
      colArrIdx  = 0;
      while(colArrIdx < coldataArr.length && 
            colData == null) {
         if (coldataArr[colArrIdx].getName().equals(columnName)) {
            colData = coldataArr[colArrIdx];
         }
         else {
            colArrIdx ++;
         }
      } // end of while(colArrIdx < coldataArr.length && colData == null)
      
      return(replaceTransformerForeignKeyColumn(curTransformerMapping, curChkTableName, colData));
   } // end of replaceTransformerForeignKeyColumn()


   private ColumnData replaceTransformerForeignKeyColumn(TableData curTransformerMapping, String curChkTableName, 
                                                         ColumnData curCTColumn) {

      ColumnData newCol;
      String     mappingKeyName;
      int        colIdx;

      // then remove that column from the mapping ...
      colIdx = curTransformerMapping.removeColumn(curCTColumn.getName());

      // and add a Foreign Key column instead
      // --> Transformer Table
      mappingKeyName = MessageFormat.format(MIHLoadJob.PRIMARY_KEY_NAME_TEMPLATE, 
                                            new Object[] { curChkTableName } );
      newCol = curTransformerMapping.addColumn(curCTColumn.getName(), MIHLoadJob.KEY_COLUMN_LENGTH, colIdx);
      newCol.setType(MIHLoadJob.KEY_COLUMN_TYPE);
      newCol.setDataObjectSourceType(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_TYPE);
      newCol.setIsNullable(curCTColumn.isNullable());
      newCol.setTransformerSrcMapping(mappingKeyName);

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "type of column '" + curCTColumn.getName() 
                                                   + "' has been replaced. Mapping is set to '" 
                                                   + mappingKeyName + "'.");
      }
 
      return(newCol);
   } // end of replaceTransformerForeignKeyColumn()


   private void setupLookupCondition(DSLink lookupStageLink, DSLink idocDataLink, 
                                     TableData lookupMapping, DataStageObjectFactory dsFactory) {
      
      DSFilterConstraint dsFilterCondition;
      ColumnData         keyColumn;
      ColumnData         keyColumnArr[];
      StringBuffer       conditionBuf;
      StringBuffer       sourceColBuf;
      String             conditionString;
      String             linkColumnName;
      int                arrIdx;
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("lookup link = " + lookupStageLink.getName());
      }
      
      keyColumnArr = lookupMapping.getColumnData();
      conditionBuf = new StringBuffer();
      sourceColBuf = new StringBuffer();;
      for(arrIdx = 0; arrIdx < keyColumnArr.length; arrIdx ++) {
         keyColumn = keyColumnArr[arrIdx];
         
         // process key columns only
         if (keyColumn.isKeyColumn()) {
            // but no SAP CLIENT TYPE columns
            if (keyColumn.getSAPDataType() == null || 
                !keyColumn.getSAPDataType().equals(Constants.SAP_DATA_TYPE_CLIENT_ID)) {
               
               // get the link/column name from specified lookup link
               linkColumnName = getSourceNameFromLink(lookupStageLink.getContains_FlowVariable(), 
                                                      keyColumn.getName());
               
               if (linkColumnName == null) {
                  // link/column name could not be found ==> use IDoc data link and column name  
                  linkColumnName = MessageFormat.format(LINK_COLUMN_TEMPLATE, 
                                                        new Object[] { idocDataLink.getName(), 
                                                                       keyColumn.getName() } );
               }

               // add separator from second name on
               if (conditionBuf.length() > 0) {
                  
                  conditionBuf.append(CT_LOOKUP_CONDITION_COL_LINK);
                  sourceColBuf.append(";");
               }

               conditionBuf.append(MessageFormat.format(CT_LOOKUP_COLUMN_CONDITION_TEMPLATE, 
                                                        new Object[] { linkColumnName } ));
               sourceColBuf.append(linkColumnName);
            } // end of if (keyColumn.getSAPDataType() == null || ... SAP_DATA_TYPE_CLIENT_ID))
         } // end of if (keyColumn.isKeyColumn()) {
      } // end of for(arrIdx = 0; arrIdx < keyColumnArr.length; arrIdx ++) {

      // create a new filter condition
      conditionString   = MessageFormat.format(IDocMIHLoadJob.CT_LOOKUP_CONDITION_REVERSE_TEMPLATE, 
                                               new Object[] { conditionBuf.toString() } );
      dsFilterCondition = dsFactory.createDSFilterConstraint(conditionString, conditionString, 
                                                             ConstraintUsageEnum.IN_LITERAL); 
      
      // the source columns
      dsFilterCondition.setSourceColumns(sourceColBuf.toString());
      
      lookupStageLink.getHas_FilterConstraint().add(dsFilterCondition);
      
      // and set 'continue' for 'Condition Not Met'
      ((DSInputPin) lookupStageLink.getTo_InputPin()).setConditionNotMet(LookupStageProperties.LOOKUP_FAIL_CONTINUE);
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Filter condition = " + conditionString);
         
         TraceLogger.exit();
      }
      
   } // end of setupLookupCondition()
   
} // end of class IDocMIHLoadJob
