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
// Module Name : com.ibm.is.sappack.gen.server.jobgenerator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.server.jobgenerator;


import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;

import ASCLModel.ConstraintUsageEnum;
import ASCLModel.LinkTypeEnum;
import ASCLModel.MainObject;
import ASCLModel.TypeCodeEnum;
import DataStageX.DSCanvasAnnotation;
import DataStageX.DSDerivation;
import DataStageX.DSDesignView;
import DataStageX.DSExtendedParamTypeEnum;
import DataStageX.DSFilterConstraint;
import DataStageX.DSFlowVariable;
import DataStageX.DSInputPin;
import DataStageX.DSJobDef;
import DataStageX.DSLink;
import DataStageX.DSOutputPin;
import DataStageX.DSParameterDef;

import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.DBSupport.DataBaseType;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequest;
import com.ibm.is.sappack.gen.common.request.JobParamData;
import com.ibm.is.sappack.gen.common.request.RequestJobType;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.ServerMessageCatalog;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.datastage.DataStageAccessManager;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.jobgenerator.layout.BaseLayout;
import com.ibm.is.sappack.gen.server.jobgenerator.util.JobStructureData;
import com.ibm.is.sappack.gen.server.jobgenerator.util.JobStructureData.LayoutData;
import com.ibm.is.sappack.gen.server.util.ColumnData;
import com.ibm.is.sappack.gen.server.util.ContainerData;
import com.ibm.is.sappack.gen.server.util.JobObject;
import com.ibm.is.sappack.gen.server.util.ModelInfoBlock;
import com.ibm.is.sappack.gen.server.util.ObjectParamMap;
import com.ibm.is.sappack.gen.server.util.SourceColMapping;
import com.ibm.is.sappack.gen.server.util.StageData;
import com.ibm.is.sappack.gen.server.util.TableData;


public abstract class BaseJob {
   
	// -------------------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------------------
	public  static final int    SHORT_DESCRIPT_MAX_LEN  = 254;

	// *************************** Annotation values ***************************
   private static final String ANNOTATION_TEXT_FONT    = "Microsoft Sans Serif\\11\\0\\0\\0\\400\\0"; //$NON-NLS-1$
   private static final String ANNOTATION_TYPE_ID      = "ID_PALETTEANNOTATION";                      //$NON-NLS-1$
   private static final String ANNOTATION_X_POS        = "11";                                        //$NON-NLS-1$
   private static final String ANNOTATION_Y_POS        = "30";                                        //$NON-NLS-1$
   private static final String ANNOTATION_X_SIZE       = "700";                                       //$NON-NLS-1$
   private static final String ANNOTATION_Y_SIZE       = "80";                                        //$NON-NLS-1$
   private static final String DATE_FORMAT_TEMPLATE    = "MM/dd/yyyy HH:mm";                          //$NON-NLS-1$
      

	// -------------------------------------------------------------------------------------
	// Member Variables
	// -------------------------------------------------------------------------------------
   private   static String                        _AnnotationTextTemplate;
	private          ServiceToken                  _SrvcToken;
	protected        RequestJobType                _JobType;
	protected        JobGeneratorRequest           _JobRequestInfo;
	protected        JobStructureData              _JobStructureData;
	protected        Map<String, ModelInfoBlock>   _PhysModelID2TableMap;
	private          DataBaseType                  _DatabaseType;


	static {
	   // load ANNOTATION text template from message catalog 
      _AnnotationTextTemplate = ServerMessageCatalog.getDefaultCatalog().getText("00110I"); 
	} // end of static { }


	// -------------------------------------------------------------------------------------
	// Abstract Methods
	// -------------------------------------------------------------------------------------
	abstract public    List<String> create() throws BaseException;
	abstract protected void         validate() throws BaseException;


   static String copyright() {
      return com.ibm.is.sappack.gen.server.jobgenerator.Copyright.IBM_COPYRIGHT_SHORT;
   }

	
	public BaseJob(ServiceToken parSrvcToken, RequestJobType parJobType, JobGeneratorRequest parJobReqInfo,
	               Map<String, ModelInfoBlock> physModelID2TableMap) throws BaseException {
	   
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		_SrvcToken            = parSrvcToken;
		_JobType              = parJobType;
		_JobRequestInfo       = parJobReqInfo;
		_PhysModelID2TableMap = physModelID2TableMap;
		_DatabaseType         = DataBaseType.DB2;

		// validate the job ...
		validate();

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of BaseJob()

	
	private void addAvailableJobParameters(DSJobDef jobDef) throws DSAccessException {
		DSParameterDef newJobParamDef;
		DSExtendedParamTypeEnum extendedType;
		TypeCodeEnum typeCode;
		DataStageObjectFactory dsFactory;
		Iterator listIter;
		JobParamData jobParam;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		dsFactory = DataStageObjectFactory.getInstance(_SrvcToken);

		listIter = _JobRequestInfo.getJobParamsList().iterator();
		while (listIter.hasNext()) {
			jobParam = (JobParamData) listIter.next();

			// determine the type code to be set
			extendedType = null;
			switch (jobParam.getType()) {
            case JobParamData.JOB_PARAM_TYPE_DATE:
                 typeCode = TypeCodeEnum.DATE_LITERAL;
                 break;

            case JobParamData.JOB_PARAM_TYPE_ENCYRYPTED:
                 typeCode     = TypeCodeEnum.STRING_LITERAL;
                 extendedType = DSExtendedParamTypeEnum.ENCRYPTED_LITERAL;
                 break;

            case JobParamData.JOB_PARAM_TYPE_FLOAT:
                 typeCode = TypeCodeEnum.SFLOAT_LITERAL;
                 break;

            case JobParamData.JOB_PARAM_TYPE_INTEGER:
                 typeCode = TypeCodeEnum.INT64_LITERAL;
                 break;

            case JobParamData.JOB_PARAM_TYPE_LIST:
                 typeCode = TypeCodeEnum.STRING_LITERAL;
                 extendedType = DSExtendedParamTypeEnum.STRINGLIST_LITERAL;
                 break;

            case JobParamData.JOB_PARAM_TYPE_PARAM_SET:
                 typeCode     = TypeCodeEnum.STRING_LITERAL;
                 extendedType = DSExtendedParamTypeEnum.PARAMETERSET_LITERAL;
                 break;

            case JobParamData.JOB_PARAM_TYPE_PATHNAME:
                 typeCode     = TypeCodeEnum.STRING_LITERAL;
                 extendedType = DSExtendedParamTypeEnum.PATHNAME_LITERAL;
                 break;

            case JobParamData.JOB_PARAM_TYPE_TIME:
                 typeCode = TypeCodeEnum.TIME_LITERAL;
                 break;

            case JobParamData.JOB_PARAM_TYPE_STRING:
            default:
                 typeCode = TypeCodeEnum.STRING_LITERAL;
                 break;
			} // end of switch (jobParam.getType())

			// and now create the parameter definition
			newJobParamDef = dsFactory.createDSParameterDef(jobParam.getName(), typeCode, extendedType, 
			                                                jobParam.getDefaultValue(), jobParam.getPrompt());
			newJobParamDef.setLongDescription(jobParam.getHelpText());

			jobDef.getHas_ParameterDef().add(newJobParamDef);
		} // end of while(listIter.hasNext())

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit(jobDef.getHas_ParameterDef().size() + " job params added");
		}
	} // end of addAvailableJobParameters()

	
   private void createJobLayout() throws BaseException {
      
      DataStageObjectFactory  dsFactory;
      LayoutData              curLayoutData;
      BaseLayout              containerLayout;
      DSCanvasAnnotation      jobAnnotation;
      DSDesignView            newDesignView;
      DSJobDef                jobDef;
      MainObject              curContainerDef;
      String                  tmpDesignViewInfo;
      StringBuffer            descriptionBuf;
      List                    containerDefList;
      Iterator                containerIter; 
      boolean                 doCreateAnnotation;
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry();
      }
      
      dsFactory        = DataStageObjectFactory.getInstance(_SrvcToken);
      jobDef           = _JobStructureData.getJobDef();
      containerDefList = _JobStructureData.getParentContainerDefs();
      containerIter    = containerDefList.iterator();
      while(containerIter.hasNext()) {
         curContainerDef = (MainObject) containerIter.next();
         
         // upper level container (DSJobDef) creates an annotation
         doCreateAnnotation = (curContainerDef == jobDef);
         
         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Container = " + curContainerDef.getName() 
                                                      + " - create annotation = " + doCreateAnnotation);
         }
         
         // build the layout for that container
         curLayoutData   = _JobStructureData.getLayoutData(curContainerDef);
         containerLayout = curLayoutData.getContainerLayout();
         newDesignView   = containerLayout.createLayout(curContainerDef, 
                                                        curLayoutData.getSourceNodesList(), 
                                                        dsFactory);
         
         // create the JobAnnotation if required
         if (doCreateAnnotation) {
            jobAnnotation = createJobAnnotation(jobDef);
            
            newDesignView.getHas_DSCanvasAnnotation().add(jobAnnotation);

            StringBuffer stageListBuf  = new StringBuffer(newDesignView.getStageList());
            StringBuffer stageTypesBuf = new StringBuffer(newDesignView.getStageTypes());
            StringBuffer stageXPosBuf  = new StringBuffer(newDesignView.getStageXPos());
            StringBuffer stageYPosBuf  = new StringBuffer(newDesignView.getStageYPos());
            StringBuffer stageXSizeBuf = new StringBuffer(newDesignView.getStageXSize());
            StringBuffer stageYSizeBuf = new StringBuffer(newDesignView.getStageYSize());
            
            // check if there is any job object ... 
            tmpDesignViewInfo = newDesignView.getStageList();
            if (tmpDesignViewInfo != null && tmpDesignViewInfo.length() > 0) {
               // yes ==> append a separator to each single buffer
               stageListBuf.append(DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR);
               stageTypesBuf.append(DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR);
               stageXPosBuf.append(DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR);
               stageYPosBuf.append(DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR);
               stageXSizeBuf.append(DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR);
               stageYSizeBuf.append(DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR);
            }

            // set location and size of the annotation ...
            stageListBuf.append(jobAnnotation.getInternalID());
            stageTypesBuf.append(ANNOTATION_TYPE_ID);
            stageXPosBuf.append(ANNOTATION_X_POS);
            stageYPosBuf.append(ANNOTATION_Y_POS);
            stageXSizeBuf.append(ANNOTATION_X_SIZE);
            stageYSizeBuf.append(ANNOTATION_Y_SIZE);
            
            // ... and update the DesignView instance
            newDesignView.setStageList(stageListBuf.toString());
            newDesignView.setStageTypes(stageTypesBuf.toString());
            newDesignView.setStageXPos(stageXPosBuf.toString());
            newDesignView.setStageYPos(stageYPosBuf.toString());
            newDesignView.setStageXSize(stageXSizeBuf.toString());
            newDesignView.setStageYSize(stageYSizeBuf.toString());
            
            if (TraceLogger.isTraceEnabled()) {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Stages       = " + newDesignView.getStageList());
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Stage types  = " + newDesignView.getStageTypes());
            }
         } // end of if (doCreateAnnotation)
         
         // add the (long) Job description ...
         descriptionBuf = new StringBuffer();
         if (jobDef.getLongDescription() != null) {
            descriptionBuf.append(jobDef.getLongDescription());
         }
         if (_JobRequestInfo.getJobDescription() != null) {
            if (descriptionBuf.length() > 0) {
            	descriptionBuf.append(Constants.NEWLINE_WINDOWS);  // DSDesigner (Windows) needs a CRLF !!
            }
            descriptionBuf.append(_JobRequestInfo.getJobDescription());
         }
         if (descriptionBuf.length() > 0) {
            jobDef.setLongDescription(descriptionBuf.toString());
         }
      } // end of while(containerIter.hasNext())
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit();
      }
   } // end of createJobLayout()

   /**
    * This method creates a link between the specified source container and 
    * the target stage.
    * <p>
    * If parameter 'linkParamMap' is specified, the map data is applied to 
    * the new created DSLink instance.
    * 
    * @param sourceStage
    *           source ContainerData instance
    * @param targetStage
    *           target StageData instance
    * @param linkParamsMap
    *           map that contains additional link parameter data
    * @param dsFactory
    *           DSFactory to be used to create DataStage instances
    */
   protected DSLink createColumnMapping(ContainerData container, StageData targetStage, 
                                        ObjectParamMap linkParams, DataStageObjectFactory dsFactory) 
             throws BaseException {
      
      StageData  containerStage;
      DSLink     newStageLink;
      DSLink     tmpLink;
      DSInputPin tmpInputPin;
      String     linkName;

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("source container = " + container.getName());
      }

      try {
         // determine the link's name ...
         // (from Container Stage's InputPin)
         containerStage = container.getContainerStage();
         tmpInputPin    = (DSInputPin) containerStage.getHas_InputPin().get(0);
         tmpLink        = (DSLink) tmpInputPin.getIsTargetOf_Link();
         linkName       = tmpLink.getName();
            
         // create a link between source and target stages
         newStageLink = dsFactory.createDSLink(targetStage.getParent(), linkName, false, 
                                               LinkTypeEnum.PRIMARY_LITERAL, linkParams);
      }
      catch(DSAccessException dsAccessExcpt) {
         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.traceException(dsAccessExcpt);
         }
         
         throw new JobGeneratorException("104500E", new String[] { dsAccessExcpt.getMessage() }, 
                                         dsAccessExcpt);
      }
      
      // update the link (associations with stages and flow variables, etc ...)
      dsFactory.updateLink(newStageLink, container, targetStage, null, null, null); 

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit("Stage link = " + newStageLink);
      }

      return (newStageLink);
   } // end of createColumnMapping()
   
   
	/**
	 * This method creates the column mapping between the passed source and 
	 * target stage using the passed column data.
	 * <p>
	 * If parameter 'linkParamMap' is specified, the map data is applied to 
	 * the new created DSLink instance.
	 * 
	 * @param sourceStage
    *           source StageData instance
	 * @param targetStage
    *           target StageData instance
	 * @param pMappingSchema
	 *           mapping schema
	 * @param linkParamsMap
	 *           map that contains additional link parameter data
	 * @param dsFactory
	 *           DSFactory to be used to create DataStage instances
	 */
	protected DSLink createColumnMapping(StageData sourceStage, StageData targetStage, TableData pMappingSchema,
	                                     ObjectParamMap linkParams, DataStageObjectFactory dsFactory) 
	          throws BaseException {
	   
	   return(createColumnMapping(sourceStage, targetStage, pMappingSchema, linkParams, 
	                              LinkTypeEnum.PRIMARY_LITERAL, dsFactory));
	} // end of createColumnMapping()

	
   /**
    * This method creates the column mapping between the passed source and 
    * target stage using the passed column data.
    * <p>
    * If parameter 'linkParamMap' is specified, the map data is applied to 
    * the new created DSLink instance.
    * 
    * @param sourceStage
    *           source StageData instance
    * @param targetStage
    *           target StageData instance
    * @param pMappingSchema
    *           mapping schema
    * @param linkParamsMap
    *           map that contains additional link parameter data
    * @param linkType
    *           link type (for example LinkTypeEnum.PRIMARY_LITERAL)
    * @param dsFactory
    *           DSFactory to be used to create DataStage instances
    */
   protected DSLink createColumnMapping(StageData sourceStage, StageData targetStage, 
                                        TableData pMappingSchema, ObjectParamMap linkParams, 
                                        LinkTypeEnum linkType, DataStageObjectFactory dsFactory) 
             throws BaseException {
      
      SourceColMapping columnMapping;
      DSLink           newStageLink;
      MainObject       linkParent;

      if (TraceLogger.isTraceEnabled()) {
         if (pMappingSchema == null) {
            TraceLogger.entry("No columns to be processed. Schema missing");
         }
         else {
            TraceLogger.entry("Columns to be processed: " + pMappingSchema.getColumnData().length);
         }
      }

      try {
         // determine the parent of the link ...
         if (sourceStage.isContainer()) {
            linkParent = targetStage.getParent();
         }
         else {
            linkParent = sourceStage.getParent();
         }
            
         // create a link between source and target stages
         newStageLink = dsFactory.createDSLink(linkParent, null, true, linkType, linkParams);
      }
      catch(DSAccessException dsAccessExcpt) {
         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.traceException(dsAccessExcpt);
         }
         
         throw new JobGeneratorException("104500E" , new String[] { dsAccessExcpt.getMessage() }, 
                                         dsAccessExcpt);
      }

      // create the set of Flow Variables and the appropriate Column Mapping 
      columnMapping = createFlowVarSetAndColumnMapping(newStageLink, sourceStage, 
                                                       targetStage, pMappingSchema, dsFactory);
      
      // update the link (associations with stages and flow variables, etc ...)
      dsFactory.updateLink(newStageLink, sourceStage, targetStage, columnMapping, 
                           _JobType.getColumnDerivationMap(), _JobType.getDerivationExceptionsMap());

      // set a filter constraint if it exists for that link ...
      // (for OUTGOING transformer stage links ONLY !!!!!!)
      if (sourceStage.isTransformerStage()) {
         setFilterContraint(pMappingSchema.getFilterConstraint(), newStageLink, dsFactory);
      } // end of if (sourceStage.isTransformerStage())

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit("Stage link = " + newStageLink);
      }

      return (newStageLink);
   } // end of createColumnMapping()


   /**
    * This method creates the column mapping between the passed source and 
    * target stage using the passed column data.
    * <p>
    * If parameter 'linkParamMap' is specified, the map data is applied to 
    * the new created DSLink instance.
    * 
    * @param sourceStage
    *           source StageData instance
    * @param targetStage
    *           target ContainerData instance
    * @param pMappingSchema
    *           mapping schema
    * @param linkParamsMap
    *           map that contains additional link parameter data
    * @param dsFactory
    *           DSFactory to be used to create DataStage instances
    */
   protected DSLink createColumnMapping(StageData sourceStage, ContainerData container, 
                                        TableData pMappingSchema, ObjectParamMap linkParams, 
                                        DataStageObjectFactory dsFactory) 
             throws BaseException {
      
      StageData        containerStage;
      SourceColMapping columnMapping;
      DSLink           newStageLink;
      DSLink           tmpLink;
      DSOutputPin      tmpOutputPin;
      String           linkName;

      if (TraceLogger.isTraceEnabled()) {
         if (pMappingSchema == null) {
            TraceLogger.entry("No columns to be processed. Mapping schema missing");
         }
         else {
            TraceLogger.entry("Columns to be processed: " + pMappingSchema.getColumnData().length +
                              " - target container = " + container.getName());
         }
      }

      try {
         // determine the link's name ...
         // (from Container Stage's OutputPin)
         containerStage = container.getContainerStage();
         tmpOutputPin   = (DSOutputPin) containerStage.getHas_OutputPin().get(0);
         tmpLink        = (DSLink) tmpOutputPin.getIsSourceOf_Link();
         linkName       = tmpLink.getName();
            
         // create a link between source and target stages
         newStageLink = dsFactory.createDSLink(sourceStage.getParent(), linkName, false,
                                               LinkTypeEnum.PRIMARY_LITERAL, linkParams);
      }
      catch(DSAccessException dsAccessExcpt) {
         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.traceException(dsAccessExcpt);
         }
         
         throw new JobGeneratorException("104500E", new String[] { dsAccessExcpt.getMessage() }, 
                                         dsAccessExcpt);
      }

      // create the set of Flow Variables and the appropriate Column Mapping 
      columnMapping = createFlowVarSetAndColumnMapping(newStageLink, sourceStage, 
                                                       container, pMappingSchema, dsFactory);
      
      // update the link (associations with stages and flow variables, etc ...)
      dsFactory.updateLink(newStageLink, sourceStage, container, columnMapping, 
                           _JobType.getColumnDerivationMap(), _JobType.getDerivationExceptionsMap());

      // set a filter constraint if it exists for that link ...
      // (for OUTGOING transformer links ONLY !!!!!!)
      if (sourceStage.isTransformerStage()) {
         setFilterContraint(pMappingSchema.getFilterConstraint(), newStageLink, dsFactory);
      } // end of if (sourceStage.isTransformerStage())

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit("Stage link = " + newStageLink);
      }

      return (newStageLink);
   } // end of createColumnMapping()
   
   
	protected String createCounterString(int maxNumber, int counter) {
		int digits = Integer.toString(maxNumber).length();
		StringBuffer result = new StringBuffer(Integer.toString(counter));
		
		int n = digits - result.length();
		for (int i = 0; i < n; i++) {
			result.insert(0, "0");
		}

		return result.toString();
	} // end of createCounterString()

	
   /**
    * This method creates the a DSFlowVariable and sets up some basic column 
    * data. 
    * 
    * @param colData
    *           column data
    * @param isUnicode 
    *           true if the column can contain unicode data otherwise false
    * @param dsFactory
    *           DSFactory to be used to create DataStage instances
    */
   private DSFlowVariable createFlowVariable(ColumnData colData, boolean isUnicode, 
                                             DataStageObjectFactory dsFactory) { 

      DSFlowVariable retFlowVariable;

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Column: " + colData + " - unicode = " + isUnicode);
      }

      // create a FlowVariable and setup basic colum information ...
      retFlowVariable = dsFactory.createDSFlowVariable(colData);
      retFlowVariable.setIsKey(new Boolean(colData.isKeyColumn()));
      retFlowVariable.setIsNullable(new Boolean(colData.isNullable()));

      // set display size
      retFlowVariable.setDisplaySize(colData.getDisplayLength(isUnicode));

      return (retFlowVariable);
   } // end of createFlowVariable()


   /**
    * This method creates the a DSFlowVariable and sets up some basic column 
    * data. 
    * 
    * @param stageLink
    *           stage link instance
    * @param sourceStage 
    *           source JobObject instance
    * @param targetStage 
    *           target JobObject instance
    * @param pMappingSchema
    *           mapping schema
    * @param dsFactory
    *           DSFactory to be used to create DataStage instances
    */
   private SourceColMapping createFlowVarSetAndColumnMapping(DSLink stageLink,
                                                             JobObject sourceStage, 
                                                             JobObject targetStage, 
                                                             TableData pMappingSchema, 
                                                             DataStageObjectFactory dsFactory) {
      RequestJobType   jobType;
      ColumnData       curColumnData;
      ColumnData       columnsArr[];
      SourceColMapping retColumnMapping;
      DSFlowVariable   dsFlowVariable;
      String           columnName;
      boolean          createFlowVarDerivation;

      if (TraceLogger.isTraceEnabled()) {
         if (pMappingSchema == null) {
            TraceLogger.entry("src stage = " + sourceStage.getName());
         }
         else {
            TraceLogger.entry("src stage = " + sourceStage.getName() + 
                              " - columns to be processed: " + pMappingSchema.getColumnData().length);
         }
      }

      // for non-container stages only
      retColumnMapping = null;
      if (!sourceStage.isContainerStage()) {
         StageData srcStageData =  (StageData) sourceStage;
         StageData trgStageData =  (StageData) targetStage;
         
         // determine if a derivation is to be created for each Flow Variable
         // (DO NOT for Lookup stages !!!!!!)
         createFlowVarDerivation = false;
         if (srcStageData.isCustomStage()   &&
             !srcStageData.isLookupStage()  && 
             !trgStageData.isLookupStage()) {
            
            createFlowVarDerivation = true;
         }
//         else {
//            if (!srcStageData.isDBStage()  && 
//                !trgStageData.isDBStage()) {
//            }
//         }
         
         // process all columns if there has a mapping (table) been specified ...
         if (pMappingSchema != null) {
            
            jobType     = getJobType();
            columnsArr  = pMappingSchema.getColumnData();
            
            for (int colIdx = 0; colIdx < columnsArr.length; colIdx++) {
               
               curColumnData = columnsArr[colIdx];
               columnName    = curColumnData.getName();

               // check what kind of columns is to be processed
               if (jobType.doProcessColumn(curColumnData.getDataObjectSourceType())) {

                  
                  dsFlowVariable = createFlowVariable(curColumnData, pMappingSchema.isUnicodeSystem(), 
                                                      dsFactory); 
                  
                  stageLink.getContains_FlowVariable().add(dsFlowVariable);

                  // add column derivation for source custom stages
                  if (createFlowVarDerivation) 
                  {
                     DSDerivation dsColDerivation;
                     String actDerivation = curColumnData.getDerivation();

                     dsColDerivation = dsFactory.createDSDerivation("", actDerivation, "");
                     dsColDerivation.setName(columnName);

                     dsFlowVariable.getHasValue_Derivation().add(dsColDerivation);
                  } // end of if (createFlowVarDerivation)
               } // end of if (jobType.doProcessColumn(curColumnData.getDataObjectSourceType()))
            } // end of for (int colIdx = 0; colIdx < columnsArr.length; colIdx++)

            // create the column mapping object ...
            retColumnMapping = TableData.createSourceColumnMapping(pMappingSchema);
         } // end of if (pMappingSchema != null)
      } // end of if (!sourceStage.isContainerStage())

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit("SrcColumnMapping = " + retColumnMapping);
      }

      return (retColumnMapping);
   } // end of createFlowVarSetAndColumnMapping()
   
   
	private DSCanvasAnnotation createJobAnnotation(DSJobDef jobDef) throws DSAccessException {
		DSCanvasAnnotation jobAnnotation;
		String             jobInfo;
		String             curDate;
		String             curDisplayModelId;
		SimpleDateFormat   dateFormatter;

		// check if passed model id is valid
		curDisplayModelId = _JobType.getDisplayModelId();
		if (curDisplayModelId == null) {
			curDisplayModelId = "";
		}

		// dateFormatter = new SimpleDateFormat("E MM/dd/yyyy hh:mm:ss");
		dateFormatter = new SimpleDateFormat(DATE_FORMAT_TEMPLATE);
		curDate       = dateFormatter.format(Calendar.getInstance().getTime());
		jobInfo       = MessageFormat.format(_AnnotationTextTemplate, 
		                                     new Object[] { curDate, 
		                                                    _JobRequestInfo.getISUsername(),
		                                                    _JobRequestInfo.getDomainServerName(), 
		                                                    curDisplayModelId,
		                                                    Constants.MODEL_VERSION, 
                                                          Constants.BUILD_NUMBER });

		jobAnnotation = DataStageObjectFactory.getInstance(_SrvcToken).createDSCanvasAnnotation(jobDef, 
		                                                                                           jobInfo);
		jobAnnotation.setBorderVisible(Boolean.TRUE);
		jobAnnotation.setBackgroundTransparent(Boolean.TRUE);
		jobAnnotation.setBackgroundColor(new Integer(0));
		// jobAnnotation.setBackgroundTransparent(Boolean.FALSE);
		jobAnnotation.setTextFont(ANNOTATION_TEXT_FONT);

		// jobAnnotation.setTextColor(new Integer(255));
		jobAnnotation.setTextColor(new Integer(0));

		return (jobAnnotation);
	} // end of createJobAnnotation()

	
	protected DSJobDef createJobDef(String jobName) throws DSAccessException {
		DataStageObjectFactory dsof;
		DSJobDef               newJobDef;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("job name = " + jobName);
		}

		_SrvcToken.startTransaction();

		if (DataStageAccessManager.getInstance().doesJobExist(jobName, _SrvcToken)) {
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Job exists. Overwrite = " + _JobType.doOverwriteJob());
			}

			if (_JobType.doOverwriteJob()) {
				// remove the job if it exists ...
				DataStageAccessManager.getInstance().deleteDSJob(jobName, _SrvcToken);
			}
			else {
				throw new DSAccessException("104400E", new String[] { jobName });
			}
		}

		// create a new Job Definition
		dsof      = DataStageObjectFactory.getInstance(_SrvcToken);
		newJobDef = dsof.createDSJobDef(jobName, _SrvcToken.getDSFolder().getDirectoryPath(), 
		                                _JobType.getJobName());

		// set the type mapping to be used in the new job
		dsof.setDSTypeMapping(_DatabaseType);
		
		// create a new JobStructureData ...
		_JobStructureData = new JobStructureData(newJobDef);

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("jobDef = " + newJobDef);
		}

		return (newJobDef);
	} // end of createJobDef()

	
   /**
    * This method creates the column mapping between the passed source and 
    * target stage using the passed column data.
    * <p>
    * If parameter 'linkParamMap' is specified, the map data is applied to 
    * the new created DSLink instance.
    * 
    * @param sourceStage
    *           source DSStage instance
    * @param targetStage
    *           target DSStage instance
    * @param pMappingSchema
    *           mapping schema
    * @param linkParamsMap
    *           map that contains additional link parameter data
    * @param baseLink
    *           link to which the new link is to be referenced
    * @param dsFactory
    *           DSFactory to be used to create DataStage instances
    */
   protected DSLink createLookupColumnMapping(StageData sourceStage, StageData targetStage, 
                                              TableData pMappingSchema, ObjectParamMap linkParams,
                                              DSLink baseLink, DataStageObjectFactory dsFactory) 
             throws BaseException {
      RequestJobType   jobType;
      ColumnData       curColumnData;
      ColumnData       columnsArr[];
      SourceColMapping columnMapping;
      DSLink           newStageLink;
      DSFlowVariable   dsFlowVariable;

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("Columns to be processed: " + pMappingSchema.getColumnData().length);
      }

      try {
         // create a link between source and target stages
         newStageLink = dsFactory.createDSLink(_JobStructureData.getJobDef(), null, true, 
                                               LinkTypeEnum.REFERENCE_LITERAL, linkParams);
      }
      catch(DSAccessException dsAccessExcpt) {
         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.traceException(dsAccessExcpt);
         }
         
         throw new JobGeneratorException("104500E", new String[] { dsAccessExcpt.getMessage() }, 
                                         dsAccessExcpt);
      }

      // add a new DSFlowVariable for each column
      jobType     = getJobType();
      columnsArr  = pMappingSchema.getColumnData();
      for (int colIdx = 0; colIdx < columnsArr.length; colIdx++) {
         curColumnData = columnsArr[colIdx];

         // check if the current column kind is to be processed
         if (jobType.doProcessColumn(curColumnData.getDataObjectSourceType())) {
            // create a FlowVariable for each column
            dsFlowVariable = createFlowVariable(curColumnData, pMappingSchema.isUnicodeSystem(), 
                                                dsFactory); 
            newStageLink.getContains_FlowVariable().add(dsFlowVariable);
         } // end of if (jobType.doProcessColumn(curColumnData.getDataObjectSourceType()))
      } // end of for (int colIdx = 0; colIdx < columnsArr.length; colIdx++)

      // create the column mapping object ...
      columnMapping = TableData.createSourceColumnMapping(pMappingSchema);

      // update the link (associations with stages and flow variables, etc ...)
      dsFactory.updateLink(newStageLink, sourceStage, targetStage, columnMapping, null, null);

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit("Stage link = " + newStageLink);
      }

      return (newStageLink);
   } // end of createLookupColumnMapping()

   
	private static void createSelectStatementFromColumnList(TableData tableArr[], DataBaseType dbType) {
		ColumnData    columnArr[];
		ColumnData    curCol;
		StringBuffer  sqlBuf;
		int           columnIdx;

		sqlBuf = new StringBuffer();
		for(TableData curTable : tableArr) {
			columnArr = curTable.getColumnData();

			sqlBuf.setLength(0);
			sqlBuf.append("<SQL> <SelectStatement collapsed='1' modified='1' type='string'><![CDATA[SELECT \n"); //$NON-NLS-1$
			for(columnIdx = 0; columnIdx < columnArr.length; columnIdx ++) {
				curCol = columnArr[columnIdx];

				if (columnIdx > 0) {
					sqlBuf.append(",\n"); //$NON-NLS-1$
				} // end of if (columnIdx > 0)

	         switch(dbType)
	         {
	            // required for oracle (defect 107833)
	            case Oracle:
	                 sqlBuf.append(curTable.getName() + ".\"" + curCol.getName() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
	                 break;
	                 
	            default:
	                 sqlBuf.append(curTable.getName() + "." + curCol.getName()); //$NON-NLS-1$
	         }
			} // end of for(columnIdx = 0; columnIdx < columnArr.length; columnIdx ++)

	      // add FROM and WHERE clause
	      switch(dbType)
	      {
	         case DB2:
	              sqlBuf.append(" \nFROM \"#SCHEMA#\"." + curTable.getName() + " " + curTable.getName() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	              break;
	            
	         // required for oracle (defect 107833)
	         case Oracle:
	         default:
	              // Oracle support: AS is not supported by Oracle
	              sqlBuf.append(" \nFROM #SCHEMA#." + curTable.getName() + " " + curTable.getName() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	              break;
	      }
			sqlBuf.append(" WHERE #WHERE#]]>\n"); //$NON-NLS-1$
			sqlBuf.append("<Tables collapsed='1'>	<Table type='string'><![CDATA[#SCHEMA#." + curTable.getName() + "]]></Table></Tables>\n"); //$NON-NLS-1$ //$NON-NLS-2$
			sqlBuf.append("<Parameters collapsed='1'></Parameters> <Columns collapsed='1'>\n"); //$NON-NLS-1$
			
			for(columnIdx = 0; columnIdx < columnArr.length; columnIdx ++) {
				curCol = columnArr[columnIdx];
				sqlBuf.append("<Column type='string'><![CDATA[" + curCol.getName() + "," + curCol.getName() + "," + curTable.getName() + "]]></Column>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			} // end of for(columnIdx = 0; columnIdx < columnArr.length; columnIdx ++)
			
			sqlBuf.append("</Columns><WhereClause type='string'><![CDATA[" + curTable.getName() + ".#WHERE#]]></WhereClause></SelectStatement>\n"); //$NON-NLS-1$ //$NON-NLS-2$
			sqlBuf.append("<EnablePartitioning modified='1' type='bool'><![CDATA[0]]></EnablePartitioning\n>" + "</SQL>\n"); //$NON-NLS-1$ //$NON-NLS-2$

			curTable.setSQLStatement(sqlBuf.toString());
		} // end of for(TableData curTable : tableArr)
	} // end of createSelectStatementFromColumnList()


	protected RequestJobType getJobType() {
		return (_JobType);
	} // end of getJobType()


	protected DataStageObjectFactory getDSFactory() throws DSAccessException {
		return (DataStageObjectFactory.getInstance(_SrvcToken));
	} // end of getDSFactory()


	protected TableData[] loadRequiredTables(String parPhysicalModelId) throws BaseException {
		TableData      retTableArr[];
		TableData      tmpTableArr[];
		TableData      lastTableArr[];
      ModelInfoBlock modelInfoBlk;
		Map            tableSetMap;
		Iterator       supportedTypesIter;
		Integer        curSupportedType;
		int            usedTablesCounter;
		int            numberTableArrsProcessed;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("Physical Model Id = " + parPhysicalModelId);
		}

		// get the table set for the passed Physical Model Id ...
		modelInfoBlk  = (ModelInfoBlock) _PhysModelID2TableMap.get(parPhysicalModelId);
		tableSetMap   = modelInfoBlk.getTableMap();
		_DatabaseType = modelInfoBlk.getDatabaseId();

		// get all tables that types matches with the stored supported types
		lastTableArr             = null;
		numberTableArrsProcessed = 0;
		usedTablesCounter        = 0;
		supportedTypesIter       = _JobType.getSupportedTableTypes().iterator();
		while (supportedTypesIter.hasNext()) {
			curSupportedType = (Integer) supportedTypesIter.next();
			tmpTableArr      = (TableData[]) tableSetMap.get(curSupportedType);

			if (tmpTableArr != null && tmpTableArr.length > 0) {
				usedTablesCounter        += tmpTableArr.length;
				numberTableArrsProcessed ++;
				lastTableArr              = tmpTableArr;
			} // end of if (tmpTableArr != null && tmpTableArr.length > 0)
		} // end of while(supportedTypesIter.hasNext())

		// create the result table array to hold all tables
		retTableArr = new TableData[usedTablesCounter];

		// evaluate the number of tables to be processed
		switch (numberTableArrsProcessed) {
		case 0:
			  // nothing to do
			  break;

		case 1:
			  // there is only one table array ==> it's the last
			  retTableArr = lastTableArr;
			  break;

		default:
			  // more than one table ==> copy the tables into the result array
			  usedTablesCounter = 0;
			  supportedTypesIter = _JobType.getSupportedTableTypes().iterator();
			  while (supportedTypesIter.hasNext()) {
				  curSupportedType = (Integer) supportedTypesIter.next();
				  tmpTableArr      = (TableData[]) tableSetMap.get(curSupportedType);

				  if (tmpTableArr != null && tmpTableArr.length > 0) {
				     System.arraycopy(tmpTableArr, 0, retTableArr, usedTablesCounter, tmpTableArr.length);

				     usedTablesCounter += tmpTableArr.length;
				  } // end of if (tmpTableArr != null && tmpTableArr.length > 0)
			  } // end of while(supportedTypesIter.hasNext())
		} // end of switch(numberTableArrsProcessed)

		if (TraceLogger.isTraceEnabled()) {
			if (retTableArr == null) {
				TraceLogger.exit("Number of tables: -");
			}
			else {
				TraceLogger.exit("Number of tables: " + retTableArr.length);
			}
		}

		// for IDocLoad and Movement Jobs ...
		if (this instanceof IDocLoadJob || 
			 this instanceof MovementJob) {
			// ==> create SQL Select statements for all tables result tables
			createSelectStatementFromColumnList(retTableArr, modelInfoBlk.getDatabaseId());
		}

		return (retTableArr);
	} // end of loadRequiredTables()


	protected TableData[] mergeTables_DeleteNotUsedAnymore(String parPhysicalModelId, TableData orgTableArr[]) throws BaseException {
		TableData        retTableArr[];
		TableData        tmpTableArr[];
      ModelInfoBlock   modelInfoBlk;
		Map              tableSetMap;
		HashSet<String>  uniqueTabSet;
		List<TableData>  fullTableList;
		Iterator         supportedTypesIter;
		Integer          curSupportedType;

		if (TraceLogger.isTraceEnabled()) {
			int orgTableCount = 0;
			if (orgTableArr != null) {
				orgTableCount = orgTableArr.length;
			}
			TraceLogger.entry("Physical Model Id = " + parPhysicalModelId + " - n org tables = " + orgTableCount);
		}

		fullTableList = new ArrayList<TableData>();
		
		// create a hash set to ensure that a table exists only once
		uniqueTabSet = new HashSet<String>();
	
		// if available setup 'Full table list' with existing (i.e. passed) tables
		if (orgTableArr != null) {
			for (TableData curTable: orgTableArr) {
				fullTableList.add(curTable);
				uniqueTabSet.add(curTable.getName());
			}
		}

		// get the table set for the passed Physical Model Id ...
		modelInfoBlk  = (ModelInfoBlock) _PhysModelID2TableMap.get(parPhysicalModelId);
		tableSetMap   = modelInfoBlk.getTableMap();
		_DatabaseType = modelInfoBlk.getDatabaseId();

		// get all tables that types matches with the stored supported types
		supportedTypesIter = _JobType.getSupportedTableTypes().iterator();
		while (supportedTypesIter.hasNext()) {
			curSupportedType = (Integer) supportedTypesIter.next();
			tmpTableArr      = (TableData[]) tableSetMap.get(curSupportedType);

			if (tmpTableArr != null) {
				// before adding a table to 'Full table list' check if it already exist
				for(TableData curTable : tmpTableArr) {
					if (!uniqueTabSet.contains(curTable.getName())) {
						fullTableList.add(curTable);
						uniqueTabSet.add(curTable.getName());
					}
				} // end of for(TableData curTableData : tmpTableArr)
			} // end of if (tmpTableArr != null && tmpTableArr.length > 0)
		} // end of while(supportedTypesIter.hasNext())

		retTableArr = fullTableList.toArray(new TableData[0]);

		// for IDocLoad and Movement Jobs ...
		if (this instanceof IDocLoadJob || this instanceof MovementJob) {
			// ==> create SQL Select statements for all tables result tables
			createSelectStatementFromColumnList(retTableArr, modelInfoBlk.getDatabaseId());
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("Number of tables: " + retTableArr.length);
		}

		return (retTableArr);
	} // end of mergeTables()


	protected void prepareAndSaveJob() throws BaseException {
		DSJobDef   jobDef;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		jobDef = _JobStructureData.getJobDef();

		// add available Job Parameters ...
		addAvailableJobParameters(jobDef);

		// build the job layout for all container definitions
      createJobLayout();

      
		// save the job
		DataStageAccessManager.getInstance().saveDSJob(jobDef, _SrvcToken);

		// cleanup service token from object factory
		DataStageObjectFactory.cleanupServiceToken(_SrvcToken);

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of prepareAndSaveJob()


	/**
	 * This method removes the last character of the passes string.
	 * 
	 * @param str
	 *           string of that the last character is removed
	 * 
	 * @return modified string (without the last character)
	 */
	protected String removeLastChar(String str) {
		String retString;

		if (str.length() > 1) {
			retString = str.substring(0, str.length() - 1);
		}
		else {
			retString = str;
		}

		return (retString);
	} // end of removeLastChar()


   private void setFilterContraint(String filterConstraint, DSLink dsLink, 
                                   DataStageObjectFactory dsFactory) {
      DSFilterConstraint  dsFilterConstraint;
      DSFlowVariable      curFlowVar;
      DSLink              curDSLink;
      String              curLinkName;
      String              fullColumnName;
      Map<String,String>  filterColumnMap;
      Map<String,String>  linkNameMap;
      EList               jobObjectList;
      Iterator            jobObjectListIter;
      Iterator            flowVarIter;
      Object              jobObject;
      int                 separatorIdx;
      int                 endIdx;
      int                 startIdx;
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("link = " + dsLink.getName() + " - filter constraint = " + filterConstraint);
      }
      
      if (filterConstraint != null) {
         // for all cases we need OUT constraint
         dsFilterConstraint = dsFactory.createDSFilterConstraint(filterConstraint, filterConstraint, 
                                                                 ConstraintUsageEnum.OUT_LITERAL);

         dsLink.getHas_FilterConstraint().add(dsFilterConstraint);

         // ------------------------------------------------------------------------
         // SAMPLE CONSTRAINT LAYOUT: "isNull(Link1.Column_0) OR (Link2.Column_0=0)"
         // ------------------------------------------------------------------------
         
         // determine the column names used in the filter constraint
         filterColumnMap = new HashMap<String,String>();
         linkNameMap     = new HashMap<String,String>();
         startIdx    = filterConstraint.indexOf('(');
         while(startIdx > -1) {
            separatorIdx = filterConstraint.indexOf('.', startIdx);
            if (separatorIdx > -1) {
               endIdx = filterConstraint.indexOf(')', startIdx);
               if(endIdx < 0) {
                  endIdx   = filterConstraint.length();
                  startIdx = endIdx - 1;
               }
               curLinkName    = filterConstraint.substring(startIdx+1, separatorIdx);
               fullColumnName = filterConstraint.substring(startIdx+1, endIdx);
               
               // search of terminating operator characters, for example =,>,<,...
               endIdx = fullColumnName.indexOf('<');
               if(endIdx < 0) {
                  endIdx = fullColumnName.indexOf('>');
                  if(endIdx < 0) {
                     endIdx = fullColumnName.indexOf('=');
                  }
               }
               if(endIdx > -1) {
                  fullColumnName = fullColumnName.substring(0, endIdx); 
               }
               fullColumnName = fullColumnName.trim();
               filterColumnMap.put(fullColumnName, fullColumnName);
               
               // add link name for performance reasons only
               linkNameMap.put(curLinkName, curLinkName);
               
               // next column (name) 
               startIdx = filterConstraint.indexOf('(', startIdx + 1);
            } // end of if (separatorIdx > -1)
         } // end of while(startIdx > -1)
         
         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "'" + filterColumnMap.size() + "' columns need to be updated.");
         }
         
         // go on if there is at least one column affected
         if (filterColumnMap.size() > 0) {
            
            // update all columns that are affected by this constraint
            jobObjectList = dsLink.getOf_JobDef().getContains_JobObject();
            
            if (TraceLogger.isTraceEnabled()) {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "'" + jobObjectList.size() + "' links are processed.");
            }
            
            // iterate through all DSLinks
            jobObjectListIter = jobObjectList.iterator();
            while(jobObjectListIter.hasNext()) {
               
               jobObject = jobObjectListIter.next();
               
               // only DSLink instances may be processed 
               if (jobObject instanceof DSLink) {
                  curDSLink   = (DSLink) jobObject;
                  curLinkName = curDSLink.getName();
                  
                  // process current link only there is one required column on this link
                  if (linkNameMap.containsKey(curLinkName)) {
                     
                     flowVarIter = curDSLink.getContains_FlowVariable().iterator();
                     while(flowVarIter.hasNext()) {
                        curFlowVar     = (DSFlowVariable) flowVarIter.next();
                        fullColumnName = MessageFormat.format(DataStageObjectFactory.FLOW_VAR_SRC_COLUMN_ID_TEMPLATE,
                                                              new Object[] { curLinkName, curFlowVar.getName() });

                        // add filer constraint if flow variable (name) exists in the filter columns map
                        if (filterColumnMap.containsKey(fullColumnName)) {
                           curFlowVar.getUsedIn_FilterConstraint().add(dsFilterConstraint);
                           
                           if (TraceLogger.isTraceEnabled()) {
                              TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Flow Variable '" + fullColumnName + "' has been updated with fuler constraint.");
                           }
                        } // end of if (filterColumnMap.containsKey(curFlowVar.getName()))
                     } // end of while(flowVarIter.hasNext())
                  } // end of if (linkNameMap.containsKey(curLinkName))
               } // end of if (jobObject instanceof DSLink)
            } // end of while(jobObjectListIter.hasNext())
         } // end of if (filterColumnMap.size() > 0)
      } // end of if (filterConstraint != null)
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit();
      }
   } // end of setFilterContraint()

   public ServiceToken getServiceToken() {
       return _SrvcToken;
   }
   
	public void setShortAndLongJobDescription(String desc) {
		// osuhre: short description can only have up to SHORT_DESCRIPT_MAX_LEN characters
		int shortDescLen = desc.length();
		if (shortDescLen > BaseJob.SHORT_DESCRIPT_MAX_LEN) {
			shortDescLen = BaseJob.SHORT_DESCRIPT_MAX_LEN;
		}
		this._JobStructureData.getJobDef().setShortDescription(desc.substring(0, shortDescLen));
		this._JobStructureData.getJobDef().setLongDescription(desc);

	}
   
} // end of class BaseJob
