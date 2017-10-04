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
// Module Name : com.ibm.is.sappack.gen.server.datastage
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.datastage;


import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;

import ASCLModel.ConstraintUsageEnum;
import ASCLModel.JobTypeEnum;
import ASCLModel.LinkTypeEnum;
import ASCLModel.MainObject;
import ASCLModel.ODBCTypeEnum;
import ASCLModel.ParamUsageEnum;
import ASCLModel.TypeCodeEnum;
import DataStageX.DSCanvasAnnotation;
import DataStageX.DSColumnDefinition;
import DataStageX.DSDataItemProps;
import DataStageX.DSDerivation;
import DataStageX.DSDesignView;
import DataStageX.DSExtendedParamTypeEnum;
import DataStageX.DSFilterConstraint;
import DataStageX.DSFlowVariable;
import DataStageX.DSInputPin;
import DataStageX.DSItem;
import DataStageX.DSJobDef;
import DataStageX.DSLink;
import DataStageX.DSLocalContainerDef;
import DataStageX.DSMetaBag;
import DataStageX.DSOutputPin;
import DataStageX.DSParamValEnum;
import DataStageX.DSParameterDef;
import DataStageX.DSParameterSet;
import DataStageX.DSParameterVal;
import DataStageX.DSStage;
import DataStageX.DSStageType;
import DataStageX.DataStageXFactory;

import com.ascential.xmeta.model.XMetaBasePackage.XMetaRepositoryObject;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.DBSupport.DataBaseType;
import com.ibm.is.sappack.gen.common.DSTypeDBMMapping.DSDataType;
import com.ibm.is.sappack.gen.common.DSTypeDBMMapping;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.server.common.DSStageTypeEnum;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.common.util.NextCounter;
import com.ibm.is.sappack.gen.server.common.util.StageTypeNotInstalledException;
import com.ibm.is.sappack.gen.server.util.ColumnData;
import com.ibm.is.sappack.gen.server.util.FlowVarData;
import com.ibm.is.sappack.gen.server.util.JobObject;
import com.ibm.is.sappack.gen.server.util.ObjectParamMap;
import com.ibm.is.sappack.gen.server.util.SourceColMapping;
import com.ibm.is.sappack.gen.server.util.StageData;
import com.ibm.is.sappack.gen.server.util.StageData.MetaBagData;
import com.ibm.is.sappack.gen.server.util.StageVariable;


public final class DataStageObjectFactory 
{
   // -------------------------------------------------------------------------------------
   //                                       Subclasses
   // -------------------------------------------------------------------------------------
   class FlowVariableType 
   {
      private ODBCTypeEnum odbcType;
      private TypeCodeEnum typeCode;
            
      public FlowVariableType(ODBCTypeEnum odbcType, TypeCodeEnum typeCode) 
      {
         this.odbcType = odbcType;
         this.typeCode = typeCode;
      }

      public ODBCTypeEnum getOdbcType() 
      {
         return odbcType;
      }

      public TypeCodeEnum getTypeCode() 
      {
         return typeCode;
      }     
   } // end of class FlowVariableType
   
   
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   private static final Integer INTEGER_INIT_VALUE                  = new Integer(0);
   private static final String  JOB_WINDOW_X_POS                    = "0022"; //$NON-NLS-1$
   private static final String  JOB_WINDOW_Y_POS                    = "0022"; //$NON-NLS-1$
   private static final String  JOB_WINDOW_X_SIZE                   = "1070"; //$NON-NLS-1$
   private static final String  JOB_WINDOW_Y_SIZE                   = "0850"; //$NON-NLS-1$
   private static final String  JOB_DEFAULT_DESIGN_VIEW_SIZING      = JOB_WINDOW_X_POS  + " " + JOB_WINDOW_Y_POS  + " " +   //$NON-NLS-1$ //$NON-NLS-2$
                                                                      JOB_WINDOW_X_SIZE + " " + JOB_WINDOW_Y_SIZE + " " +   //$NON-NLS-1$ //$NON-NLS-2$
                                                                      "0000 0000 0000 0000";                                //$NON-NLS-1$
   
   private static final String  DESIGN_VIEW_NAME                    = "Job";            //$NON-NLS-1$
   private static final String  PARAMETER_FILE_DD_NAME              = "DD00001";        //$NON-NLS-1$
   private static final String  PIN_TYPE_STD_PIN                    = "StdPin";         //$NON-NLS-1$
   private static final String  PIN_TYPE_STD_INPUT                  = "StdInput";       //$NON-NLS-1$
   private static final String  PIN_TYPE_STD_OUTPUT                 = "StdOutput";      //$NON-NLS-1$
   private static final String  PIN_TYPE_CUSTOM_INPUT               = "CustomInput";    //$NON-NLS-1$
   private static final String  PIN_TYPE_CUSTOM_OUTPUT              = "CustomOutput";    //$NON-NLS-1$
   private static final String  PIN_TYPE_TRANSFORMER_INPUT          = "TrxInput";       //$NON-NLS-1$
   private static final String  PIN_TYPE_TRANSFORMER_OUTPUT         = "TrxOutput";      //$NON-NLS-1$
   private static final String  LINK_STAGE_INFO_TEMPLATE            = "{0}={1}={2}";    //$NON-NLS-1$
   private static final String  PIN_ID_TEMPLATE                     = "{0}P{1}";        //$NON-NLS-1$
   private static final String  PIN_PARTNER_TEMPLATE                = "{0}|{1}";        //$NON-NLS-1$
   public  static final String  FLOW_VAR_SRC_COLUMN_ID_TEMPLATE     = "{0}.{1}";        //$NON-NLS-1$
   public  static final char    DS_VIEW_VALUE_SEPARATOR             = '|';              
   public  static final String  DS_LAZY_LI_DATA_SEPARATOR           = ",";              //$NON-NLS-1$
   
   public  static final int     OBJECT_TYPE_DEFAULT                 = 1;
   public  static final int     OBJECT_TYPE_INPUT                   = 2;
   public  static final int     OBJECT_TYPE_OUTPUT                  = 3;
   public  static final int     LINK_TYPE_DEFAULT                   = OBJECT_TYPE_DEFAULT;
   public  static final int     LINK_TYPE_INPUT                     = OBJECT_TYPE_INPUT;
   public  static final int     LINK_TYPE_OUTPUT                    = OBJECT_TYPE_OUTPUT;
   public  static final int     STAGE_TYPE_DEFAULT                  = OBJECT_TYPE_DEFAULT;
   public  static final int     STAGE_TYPE_INPUT                    = OBJECT_TYPE_INPUT;
   public  static final int     STAGE_TYPE_OUTPUT                   = OBJECT_TYPE_OUTPUT;
   
   public  static final String STAGE_TYPE_CLASS_NAME_CUSTOM_STAGE   = "CustomStage";       //$NON-NLS-1$
   public  static final String STAGE_TYPE_CLASS_NAME_TRANSFORMER    = "TransformerStage";  //$NON-NLS-1$
   public  static final String STAGE_TYPE_CLASS_NAME_CONTAINER      = "ContainerStage";    //$NON-NLS-1$

   public  static final char   DS_LIST_SEPARATOR                    = '\u00A0';
   
   // ********************************* Internal Stage IDs **********************************
   private static final String  INTERNAL_ID                         = "ROOT";                           //$NON-NLS-1$
   public  static final String  CONTAINER_STAGE_ID_PREFIX           = "C";                              //$NON-NLS-1$
   public  static final String  CONTAINER_ID_PREFIX                 = "V";                              //$NON-NLS-1$
   private static final String  CONTAINER_ID_TEMPLATE               = CONTAINER_ID_PREFIX  + "{0}";     //$NON-NLS-1$
   private static final String  DEFAULT_ANNOTATION_ID_TEMPLATE      = CONTAINER_ID_TEMPLATE + "A";      //$NON-NLS-1$
   private static final String  DEFAULT_STAGE_ID_TEMPLATE           = CONTAINER_ID_TEMPLATE + "S";      //$NON-NLS-1$
   private static final String  CONTAINER_STAGE_PARAM_VIEW          = "ContainerView";

   // ******************** MetaBag Constants (for DSJobDef/DSInputPin/DSOutput **************************
   public  static final String  METABAG_OWNER_NAME                  = "APT";                           //$NON-NLS-1$
   private static final String  METABAG_JOBDEF_NAMES                = "AdvancedRuntimeOptions";        //$NON-NLS-1$
   private static final String  METABAG_JOBDEF_VALUES               = "#DSProjectARTOptions#";         //$NON-NLS-1$
   public  static final String  METABAG_TF_EXECMODE_NAME            = "Execmode";                      //$NON-NLS-1$
   public  static final String  METABAG_TF_EXECMODE_VALUES_SEQ      = "seq";                           //$NON-NLS-1$
   public  static final String  METABAG_SCHEMA_FORMAT_NAME          = "SchemaFormat";                  //$NON-NLS-1$
   public  static final String  METABAG_LU_LOOKUP_NAME              = "LookupOperator";                //$NON-NLS-1$
   public  static final String  METABAG_LU_LOOKUP_OP_VALUES_LOOKUP  = "lookup";                        //$NON-NLS-1$

   /*
   // ******************** Physical Data Model Data Types **************************
   private static final String DBM_TYPE_BIGINT                    = "BIGINT";
   private static final String DBM_TYPE_BLOB                      = "BLOB";
   private static final String DBM_TYPE_CHAR                      = "CHAR";
   private static final String DBM_TYPE_CHAR_FOR_BIT_DATA         = "CHAR FOR BIT DATA";
   private static final String DBM_TYPE_CLOB                      = "CLOB";
   private static final String DBM_TYPE_DATALINK                  = "DATALINK";
   private static final String DBM_TYPE_DATE                      = "DATE";
   private static final String DBM_TYPE_DBCLOB                    = "DBCLOB";
   private static final String DBM_TYPE_DECFLOAT                  = "DECFLOAT";
   private static final String DBM_TYPE_DECIMAL                   = "DECIMAL";
   private static final String DBM_TYPE_DOUBLE                    = "DOUBLE";
   private static final String DBM_TYPE_FLOAT                     = "FLOAT";
   private static final String DBM_TYPE_GRAPHIC                   = "GRAPHIC";
   private static final String DBM_TYPE_INT                       = "INT";                            // oracle
   private static final String DBM_TYPE_INTEGER                   = "INTEGER";
   private static final String DBM_TYPE_LONG_VARCHAR              = "LONG VARCHAR";
   private static final String DBM_TYPE_LONG_VARCHAR_FOR_BIT_DATA = "LONG VARCHAR FOR BIT DATA";
   private static final String DBM_TYPE_LONG_VARGRAPHIC           = "LONG VARGRAPHIC";
   private static final String DBM_TYPE_NUMBER                    = "NUMBER";                        // oracle
   private static final String DBM_TYPE_NUMERIC                   = "NUMERIC";                       // oracle
   private static final String DBM_TYPE_NCHAR                     = "NCHAR";                         // oracle
   private static final String DBM_TYPE_NVARCHAR_2                = "NVARCHAR2";                     // oracle
   private static final String DBM_TYPE_RAW                       = "RAW";
   private static final String DBM_TYPE_REAL                      = "REAL";                          // oracle
   private static final String DBM_TYPE_SMALLINT                  = "SMALLINT";
   private static final String DBM_TYPE_TIME                      = "TIME";
   private static final String DBM_TYPE_TIMESTAMP                 = "TIMESTAMP";
   private static final String DBM_TYPE_VARCHAR                   = "VARCHAR";
   private static final String DBM_TYPE_VARCHAR_2                 = "VARCHAR2";                      // oracle
   private static final String DBM_TYPE_VARCHAR_FOR_BIT_DATA      = "VARCHAR FOR BIT DATA";
   private static final String DBM_TYPE_VARGRAPHIC                = "VARGRAPHIC";
   private static final String DBM_TYPE_XML                       = "XML";

   // ******************** SAP Data Types **************************
   private static final String SAP_DATA_TYPE_CLIENT               = "CLNT";
   private static final String SAP_DATA_TYPE_CHAR                 = "CHAR";
   private static final String SAP_DATA_TYPE_VARC                 = "VARC";
   private static final String SAP_DATA_TYPE_ACCP                 = "ACCP";
   private static final String SAP_DATA_TYPE_CUKY                 = "CUKY";
   private static final String SAP_DATA_TYPE_DATS                 = "DATS";
   private static final String SAP_DATA_TYPE_DEC                  = "DEC";
   private static final String SAP_DATA_TYPE_FLTP                 = "FLTP";
   private static final String SAP_DATA_TYPE_INT1                 = "INT1";
   private static final String SAP_DATA_TYPE_INT2                 = "INT2";
   private static final String SAP_DATA_TYPE_INT4                 = "INT4";
   private static final String SAP_DATA_TYPE_LANG                 = "LANG";
   private static final String SAP_DATA_TYPE_NUMC                 = "NUMC";
   private static final String SAP_DATA_TYPE_PREC                 = "PREC";
   private static final String SAP_DATA_TYPE_RAW                  = "RAW";
   private static final String SAP_DATA_TYPE_RSTR                 = "RSTR";
   private static final String SAP_DATA_TYPE_SSTR                 = "SSTR";
   private static final String SAP_DATA_TYPE_STRG                 = "STRG";
   private static final String SAP_DATA_TYPE_TIMS                 = "TIMS";
   private static final String SAP_DATA_TYPE_UNIT                 = "UNIT";
   private static final String SAP_DATA_TYPE_LRAW                 = "LRAW";
     */ 
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private static Map<ServiceToken, DataStageObjectFactory> _ObjectCache;
   
   private        DataStageXFactory      _DSFactory;
   private        ServiceToken           _srvcToken;
   private        NextCounter            _NextContainerCounter;
   private        NextCounter            _NextJobObjCounter;
   private        NextCounter            _PinLinkCounter;
   private        Map                    _ContainerIdMap;
   private        Map<String,DSDataType> _DSTypeMapping;


   // -------------------------------------------------------------------------------------
   //                                 Static Block
   // -------------------------------------------------------------------------------------
   static 
   {
      // create the object cache
      _ObjectCache = new HashMap<ServiceToken, DataStageObjectFactory>();
   } // end of static


   static String copyright()
   { 
      return Copyright.IBM_COPYRIGHT_SHORT; 
   }

   /**
    * This method creates the Singleton instance.
    * 
    * @param srvcToken  ServiceToken instance to be saved in the object factory
    * 
    * @throws Exception
    */
   private DataStageObjectFactory(ServiceToken srvcToken)
           throws DSAccessException
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }

      // initialize the instance
      init(srvcToken);

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of DataStageObjectFactory()
   
   
   public static synchronized void createInstance()
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }

      return;
   } // end of createInstance()
   
   
   public static synchronized void deleteInstance()
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }

      _ObjectCache.clear();

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of deleteInstance()
   
   
   public static DataStageObjectFactory getInstance(ServiceToken srvcToken)
          throws DSAccessException
   {
      DataStageObjectFactory factoryInstance = null;

      if (srvcToken != null)
      {
         // check the object cache if there is a factory for the passed service token
         factoryInstance = (DataStageObjectFactory) _ObjectCache.get(srvcToken);

         if (factoryInstance == null)
         {
            factoryInstance = new DataStageObjectFactory(srvcToken);
            _ObjectCache.put(srvcToken, factoryInstance);

            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.trace(TraceLogger.LEVEL_FINER, "Service Token '" + srvcToken + "' added to object cache.");
            }
         } // end of if (factoryInstance == null)
      } // end of if (srvcToken != null)

      return(factoryInstance);
   } // end of getInstance()

   
   private void init(ServiceToken srvcToken) throws DSAccessException
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry(srvcToken);
      }

      // create NextCounter instances
      _NextContainerCounter = new NextCounter(-1);
      _NextJobObjCounter    = new NextCounter();
      _PinLinkCounter       = new NextCounter();
      _DSTypeMapping        = DSTypeDBMMapping.getDBMMapping(DataBaseType.DB2);

      // the ContainerId map ...
      _ContainerIdMap = new HashMap();

      // save passed service token ...
      _srvcToken = srvcToken;

      // create the DataStage factory 
      try 
      {
         _DSFactory = _srvcToken.getDSXFactory();
      } 
      catch (Exception pExcpt) 
      {
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.traceException(pExcpt);
         }
         
         throw new DSAccessException("120800E", Constants.NO_PARAMS, pExcpt);
      }
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of init()

   
   private void addMetaBagData(DSMetaBag dsMetaBag, List metaBagList)
   {
      MetaBagData   curMetaBagData;
      StringBuffer  mbNamesBuf;
      StringBuffer  mbOwnersBuf;
      StringBuffer  mbValuesBuf;
      StringBuffer  mbConditionBuf;
      Iterator      listIter;
      
      if (dsMetaBag != null && metaBagList !=  null)
      {
         // process all MetaBagData instances (if available)
         if (metaBagList.size() > 0)
         {
            // read existing names, owners, values ....
            if (dsMetaBag.getNames() == null)
            {
               // it's empty ...
               mbConditionBuf = new StringBuffer();
               mbNamesBuf     = new StringBuffer();
               mbOwnersBuf    = new StringBuffer();
               mbValuesBuf    = new StringBuffer();
            }
            else
            {
               mbConditionBuf = new StringBuffer(dsMetaBag.getConditions());
               mbNamesBuf     = new StringBuffer(dsMetaBag.getNames());
               mbOwnersBuf    = new StringBuffer(dsMetaBag.getOwners());
               mbValuesBuf    = new StringBuffer(dsMetaBag.getValues());
            } // end of (else) if (dsMetaBag.getNames() == null)
            
            listIter = metaBagList.iterator();
            while(listIter.hasNext())
            {
               curMetaBagData = (MetaBagData) listIter.next();

               // if it's not the first name ...
               if (mbNamesBuf.length() > 0)
               {
                  // ==> add list separator char before the next name, owner, value
                  mbConditionBuf.append(DS_LIST_SEPARATOR);
                  mbNamesBuf.append(DS_LIST_SEPARATOR);
                  mbOwnersBuf.append(DS_LIST_SEPARATOR);
                  mbValuesBuf.append(DS_LIST_SEPARATOR);
               } // end of if (mbNamesBuf.length() > 0)
               
               // ... then add name, owner, value, condition
               mbNamesBuf.append(curMetaBagData.getName());
               mbOwnersBuf.append(curMetaBagData.getOwner());
               mbValuesBuf.append(curMetaBagData.getValue());
               if (curMetaBagData.getCondition() != null)
               {
                  mbConditionBuf.append(curMetaBagData.getCondition());
               }
            } // end of while(listIter.hasNext())
            
            // copy new buffers to MetaBag instance
            dsMetaBag.setConditions(mbConditionBuf.toString());
            dsMetaBag.setNames(mbNamesBuf.toString());
            dsMetaBag.setOwners(mbOwnersBuf.toString());
            dsMetaBag.setValues(mbValuesBuf.toString());
         } // end of if (metaBagList.size() > 0)
      } // end of if (dsMetaBag != null && metaBagList !=  null)
      
   } // end of addMetaBagData()
   
   
   public void addParamsToLink(DSLink dsLink, ObjectParamMap linkParams)
   {
      if (TraceLogger.isTraceEnabled())
      {
         if (linkParams == null)
         {
            TraceLogger.entry("null");
         }
         else
         {
            TraceLogger.entry("number of params = " + linkParams.size());
         }
      }
      
      // add passed parameters to link parameters
      if (dsLink != null && linkParams != null)
      {
         addParamsToObject(dsLink.getHas_ParameterVal(), linkParams);
      } // end of if (dsLink != null && linkParams != null)
         
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of addParamsToLink()

   
   private void addParamsToObject(EList objParamList, ObjectParamMap objectParams)
   {
      DSParameterVal        newParam;
      ParamUsageEnum        usageEnum;
      String                key;
      ObjectParamMap.Value  value;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "map = " + objectParams);
      }
      
      // process all map entries
      for (Iterator keyValIt = objectParams.iterator(); keyValIt.hasNext();) 
      {
         Map.Entry paramEntry = (Map.Entry) keyValIt.next();
         
         key   = (String) paramEntry.getKey();
         value = (ObjectParamMap.Value) paramEntry.getValue();
         
         switch(value.getUsageType())
         {
            case OBJECT_TYPE_INPUT:
                 usageEnum = ParamUsageEnum.IN_LITERAL;
                 break;
            case OBJECT_TYPE_OUTPUT:
                 usageEnum = ParamUsageEnum.OUT_LITERAL;
                 break;
            default:
                 usageEnum = ParamUsageEnum.DEFAULT_LITERAL;
                 break;
         } // end of switch(value.getUsageType())
         
         newParam = createDSParameterVal(key, value.getValue());
         newParam.setUsage(usageEnum);
         objParamList.add(newParam);
      } // end of for (Iterator keyValIt = objectParams.iterator(); keyValIt.hasNext();)
         
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of addParamsToObject()


   /**
    * This method assigns the Flow Variables (columns) of a Lookup stage.
    * <p>
    * For each Lookup Stage column :
    * <ul>
    * <li>check if the variable can be found in one of the input links</li>
    * <p>If found, create a new LookupDerivation
    * <li>And if the column could not be found on a input link ...<p>
    * --> check if the variable can be found in one of the output links</li>
    *     <p>If found, set Source Column Id and remove an existing'hasValue' derivation 
    * <li>If the column could not be found on any link and it has the SAP CLIENT data type ...<p>
    * --> create a Lookup Derivation (DataStage variable) for thet column</li>
    * </ul>
    * 
    * @param lookupStageData  Lookup Stage data
    * @param lookupLink       Lookup Link
    * @param columnsMappings  source column mapping 
    */
   private void assignLookupStageColumns(StageData lookupStageData, DSLink lookupLink, 
                                         SourceColMapping columnsMappings)
   {
      FlowVarData        mappingColumnData;
      ColumnData         tmpColumnData; 
      DSDerivation       dsDerivation;
      DSFlowVariable     srcFlowVar;
      DSFlowVariable     trgFlowVar;
      DSLink             stageInputLink;
      DSLink             stageOutputLink;
      DSInputPin         stageInputPin;
      DSOutputPin        stageOutputPin;
      DSStage            actlookupStage;
      String             srcFlowVarName;
      String             actualDerivation;
      String             linkName;
      String             lookupLinkName;
      EList              lookupFlowVarsList;
      EList              trgFlowVarsList;
      EList              inputPinList;
      EList              outputPinList;
      Iterator           srcListIter;
      Iterator           pinIter;
      boolean            isOutLinkFlowVar;
      boolean            isSrcColumnResolved;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("lookup stage = " + lookupStageData.getName() + "lookup link = " + lookupLink.getName());
      }

      actlookupStage     = (DSStage) lookupStageData.getJobComponent();
      lookupLinkName     = lookupLink.getName();
      lookupFlowVarsList = lookupLink.getContains_FlowVariable();
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "lookup flow vars = " + lookupFlowVarsList.size());
      }

      // get the input pin of the target (Lookup) stage
      inputPinList = actlookupStage.getHas_InputPin(); 
      if (inputPinList.size() < 1)
      {
         throw new IllegalArgumentException("Reference link of lookup stage must be defined after Lookup stage incoming data link definition.");
      }
      
      // get the output pin of the target (Lookup) stage
      outputPinList = actlookupStage.getHas_OutputPin(); 
      if (outputPinList.size() < 1)
      {
         throw new IllegalArgumentException("Reference link of lookup stage must be defined after Lookup stage outgoing data link definition.");
      }

      srcListIter = lookupFlowVarsList.iterator();
      while(srcListIter.hasNext())
      {
         isSrcColumnResolved = false;
         srcFlowVar          = (DSFlowVariable) srcListIter.next();
         srcFlowVarName      = srcFlowVar.getName();

         // check if there is a particular mapping for that column
         // --> get the 'mapping' column name from the 'column mapping'
         mappingColumnData = columnsMappings.getMapping(srcFlowVarName);

         tmpColumnData = null;
         if (mappingColumnData != null && mappingColumnData instanceof ColumnData)
         {
            tmpColumnData = (ColumnData) mappingColumnData;
         }
         else
         {
            throw new IllegalArgumentException("'" + srcFlowVarName + "' cannot be mapped. Wrong mapping type detected.");
         } // end of if (mappingColumnData != null && mappingColumnData instanceof ColumnData)

         trgFlowVar = null;
         linkName   = null;
         // if the columns is a key column ...
         if (tmpColumnData.isKeyColumn())
         {
            // KEY column ==> search on the input links
            isOutLinkFlowVar = false;
            pinIter          = inputPinList.iterator();
            while(pinIter.hasNext() && trgFlowVar == null)
            {
               // get the input pin's link ...
               stageInputPin   = (DSInputPin) pinIter.next(); 
               stageInputLink  = (DSLink) stageInputPin.getIsTargetOf_Link();
            
               // ... and the corresponding flow variables
               trgFlowVarsList = stageInputLink.getContains_FlowVariable();
               linkName        = stageInputLink.getName();
            
               if (TraceLogger.isTraceEnabled())
               {
                  TraceLogger.trace(TraceLogger.LEVEL_FINEST, "input link name = " + linkName);
                  TraceLogger.trace(TraceLogger.LEVEL_FINEST, "input flow vars = " + trgFlowVarsList.size());
               }

               // check all input links but not the link have the 'new' lookup name
               if (!linkName.equals(lookupLinkName))
               {
                  // get the link (link must be a PRIMARY link)
                  DSLink linkSrc = (DSLink) stageInputPin.getIsTargetOf_Link();
                  
//                  if (linkSrcStage.getStageType().equals(DSStageTypeEnum.CTRANSFORMER_STAGE_LITERAL.getName()))
                  if (linkSrc.getLinkType().getValue() == LinkTypeEnum.PRIMARY)
                  {
                     // search the source flow variable in the Flow Variable list of current in link
                     trgFlowVar = getCorrespondingTargetFlowVar(trgFlowVarsList, srcFlowVarName, true, false);
                  }
               }
            } // end of while(pinIter.hasNext() && trgFlowVar == null)
            
            if (TraceLogger.isTraceEnabled() && trgFlowVar != null)
            {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, srcFlowVarName + " resolved (Input).");
            }
         }
         else
         {
            // NOT a key column ==> search on the input links
            // ==> search on the output links
            isOutLinkFlowVar = true;
            pinIter          = outputPinList.iterator();
            while(pinIter.hasNext() && trgFlowVar == null)
            {
               // get the input pin's link ...
               stageOutputPin  = (DSOutputPin) pinIter.next(); 
               stageOutputLink = (DSLink) stageOutputPin.getIsSourceOf_Link();
               
               // ... and the corresponding flow variables
               trgFlowVarsList = stageOutputLink.getContains_FlowVariable();
               linkName        = stageOutputLink.getName();
               
               if (TraceLogger.isTraceEnabled())
               {
                  TraceLogger.trace(TraceLogger.LEVEL_FINEST, "output link name = " + linkName);
                  TraceLogger.trace(TraceLogger.LEVEL_FINEST, "output flow vars = " + trgFlowVarsList.size());
               }
               
               // search the source flow variable in the Flow Variable list of current out link
               trgFlowVar = getCorrespondingTargetFlowVar(trgFlowVarsList, srcFlowVarName, false, true);
            } // end of while(pinIter.hasNext() && trgFlowVar == null)
            if (TraceLogger.isTraceEnabled() && trgFlowVar != null)
            {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, srcFlowVarName + " resolved (Output).");
            }
         } // end of (else) if (tmpColumnData.isKeyColumn())
         
         if (TraceLogger.isTraceEnabled() && trgFlowVar == null)
         {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, srcFlowVarName + " NOT resolved: is key = " + tmpColumnData.isKeyColumn());
         }

         // if the source variable was found ...
         if (trgFlowVar != null)
         {
            // there can be a value derivation on the target flow variable ...
            if (isOutLinkFlowVar)
            {
               String srcColumnId = MessageFormat.format(FLOW_VAR_SRC_COLUMN_ID_TEMPLATE,
                                                         new Object[] { lookupLinkName, srcFlowVarName });
//                                                       new Object[] { lookupLinkName, trgFlowVar.getName() });
               trgFlowVar.setSourceColumnID(srcColumnId);
            
               // ==> assign target variable to the source variable and vice versa
               srcFlowVar.getIsSourceOf_FlowVariable().add(trgFlowVar);
               trgFlowVar.getHasSource_FlowVariable().add(srcFlowVar);
            
               // target Flow Variable has been found on an OUTGOING Link
               // -------------------------------------------------------
               if (trgFlowVar.getHasValue_Derivation().size() == 1)
               {
                  // ==> remove it !!
                  trgFlowVar.getHasValue_Derivation().remove(0);
               
                  if (TraceLogger.isTraceEnabled())
                  {
                     TraceLogger.trace(TraceLogger.LEVEL_FINEST, "'HasValue Derivation' for Flow Variable '" + linkName + 
                                                                 "." + srcFlowVarName + "' removed."); 
                  }
               }
            }
            else
            {
               // target Flow Variable has been found on an INCOMING Link
               // -------------------------------------------------------
               // ==> assign target variable to the source variable and vice versa
               srcFlowVar.getHasSource_FlowVariable().add(trgFlowVar);
               trgFlowVar.getIsSourceOf_FlowVariable().add(srcFlowVar);
            
               // ==> add a derivation for the source column
               actualDerivation = MessageFormat.format(FLOW_VAR_SRC_COLUMN_ID_TEMPLATE, 
                                                       new Object[] { linkName, trgFlowVar.getName() });
//                                                        new Object[] { linkName, srcFlowVarName });
            
               if (TraceLogger.isTraceEnabled())
               {
                  TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Derivation = " + actualDerivation); 
               }
            
               dsDerivation = createDSDerivation(actualDerivation, actualDerivation, actualDerivation);
               srcFlowVar.getHasLookup_Derivation().add(dsDerivation);
            } // end of (else) if (isOutLinkFlowVar)
            
            isSrcColumnResolved = true;
         } // end of if (trgFlowVar != null)
         
         // if source column could not been resolved yet ...
         if (!isSrcColumnResolved)
         {
            // ==> check if there is Source Name matches with the mapping name
            if (tmpColumnData.getTransformerSrcMapping().equals(srcFlowVarName))
            {
               // ==> let's derive CLIENT ID type from a Job Parameter 
               if (tmpColumnData.getSAPDataType() != null && 
                   tmpColumnData.getSAPDataType().equals(Constants.SAP_DATA_TYPE_CLIENT_ID ))
               {
                  actualDerivation = Constants.SAP_CLIENT_ID_JOB_PARAM_NAME;
                  
                  if (TraceLogger.isTraceEnabled()) 
                  {
                     TraceLogger.trace(TraceLogger.LEVEL_FINEST, "New Lookup Derivation to Job Parameter '" + 
                                                                 actualDerivation + "' created.");
                  }

                  dsDerivation = createDSDerivation("", actualDerivation, actualDerivation);
                  srcFlowVar.getHasLookup_Derivation().add(dsDerivation);
                  
                  isSrcColumnResolved = true;
               } // end of if (tmpColumnData.getSAPDataType() != null && ... .SAP_DATA_TYPE_CLIENT_ID))
            } // end of if (tmpColumnData.getTransformerSrcMapping().equals(srcFlowVarName))
         } // end of if (!isSrcColumnResolved)
      } // end of while(srcListIter.hasNext())
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of assignLookupStageColumns()


   private void assignTargetColsToSourceCols(JobObject sourceStage, EList trgFlowVarsList, 
                                             SourceColMapping columnsMappings, Map columnDerivations, Map derivationExceptions)
   {
      ColumnData     curColumnData;
      FlowVarData    mappingColumnData;
      StageVariable  curStageVar;
      DSDerivation   dsDerivation;
      DSFlowVariable srcFlowVar;
      DSFlowVariable trgFlowVar;
      DSFlowVariable newStageVariable;
      DSLink         srcStageInputLink;
      DSInputPin     srcStageInputPin;
      EList          srcFlowVarsList;
      String         srcColumnId;
      String         srcColumnName;
      String         colDerivTemplate;
      String         actualDerivation;
      String         mappingColumnName;
      String         srcLinkName;
      Iterator       srcListIter;
      Iterator       trgListIter;
      boolean        hasVarFound;

   	 if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.entry("src stage = " + sourceStage.getName() + 
                           " - trg vars = " + trgFlowVarsList.size());
      }

      // get the source stage FlowVariables
      srcStageInputPin = (DSInputPin) sourceStage.getHas_InputPin().get(0); // 0 = because our source stage
                                                                            // has only one input link

      if (srcStageInputPin == null) 
      {
         if (TraceLogger.isTraceEnabled()) 
         {
            TraceLogger.trace(TraceLogger.LEVEL_FINE, "Warning: No DSInputPin found for source stage '"
                                                    + sourceStage.getName() + "'.");
         }

         srcLinkName     = null;
         srcFlowVarsList = null;
      } 
      else 
      {
         // get the input link of the stage
         srcStageInputLink = (DSLink) srcStageInputPin.getIsTargetOf_Link();
         srcFlowVarsList   = srcStageInputLink.getContains_FlowVariable();
         srcLinkName       = srcStageInputLink.getName();
         if (srcFlowVarsList.size() == 0)
         {
            // check here if there exists another link with same name on the link'sparent
            // (maybe it's a container and the Flow Variables exist on the container'sparent)
            srcFlowVarsList = getFlowVarsFromLinkParentWithSameLinkName(srcStageInputLink);
         }

         if (TraceLogger.isTraceEnabled()) 
         {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "src link name          = " + srcLinkName);
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "src flow vars          = " + srcFlowVarsList.size());
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "trg flow vars          = " + trgFlowVarsList.size());
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "column derivations     = " + columnDerivations);
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "derivations exceptions = " + derivationExceptions);
         }
      } // end of (else) if (srcStageInputPin == null)

      // check all target flow variables if they exist as source flow variables
      trgListIter = trgFlowVarsList.iterator();
      while (trgListIter.hasNext()) 
      {
         trgFlowVar = (DSFlowVariable) trgListIter.next();

         // get the 'mapping' column name from the 'column mapping'
         mappingColumnData = columnsMappings.getMapping(trgFlowVar.getName());

         if (TraceLogger.isTraceEnabled()) 
         {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "mapping Column class = "
                                                      + mappingColumnData.getClass().getName());
         }

         if (mappingColumnData instanceof StageVariable) 
         {
            // create a Stage variable ...
            curStageVar      = (StageVariable) mappingColumnData;
            newStageVariable = createStageVariable(curStageVar, trgFlowVar);

            // ... and set it on the passed transformer stage
            sourceStage.getContains_FlowVariable().add(newStageVariable);
         } 
         else 
         {
            if (srcStageInputPin != null) 
            {
               curColumnData     = (ColumnData) mappingColumnData;
               mappingColumnName = curColumnData.getTransformerSrcMapping();

               if (TraceLogger.isTraceEnabled()) 
               {
                  TraceLogger.trace(TraceLogger.LEVEL_FINEST, "mapping Column = " + mappingColumnName);
               }

               // check if there is a source flow variable for current target
               hasVarFound = false;
               srcListIter = srcFlowVarsList.iterator();
               while (srcListIter.hasNext() && !hasVarFound) 
               {
                  srcFlowVar = (DSFlowVariable) srcListIter.next();
                  if (mappingColumnName.equals(srcFlowVar.getName())) 
                  {
                     trgFlowVar.getHasSource_FlowVariable().add(srcFlowVar);
                     srcColumnName = srcFlowVar.getName();
                     srcColumnId   = MessageFormat.format(FLOW_VAR_SRC_COLUMN_ID_TEMPLATE,
                                                          new Object[] { srcLinkName, srcColumnName });
                     trgFlowVar.setSourceColumnID(srcColumnId);
                     srcFlowVar.getIsSourceOf_FlowVariable().add(trgFlowVar);

                     // setup column description with 'Related Check Table Column' 
                     trgFlowVar.setShortDescription(curColumnData.getRelatedCheckTableColumn());

                     if (sourceStage.getType().getValue() == DSStageTypeEnum.PX_REMOVE_DUPLICATES_STAGE) {
            			 String columnName = curColumnData.getDerivation();
                    	 if (columnName.indexOf("{0}") > -1) {
                    		 columnName = MessageFormat.format(columnName, new Object[] { srcLinkName });
                    		 DSDerivation derivation = (DSDerivation)trgFlowVar.getHasValue_Derivation().get(0);
                    		 derivation.setExpression(columnName);
		                 }
                     }
                     
                     // for Transformer stages ONLY !!!!
                     if (sourceStage.isTransformerStage()) 
                     {
                     	actualDerivation = null;
                     	// check first if there is a derivation exception
                     	if (derivationExceptions != null) {
                     		String sapTableName = curColumnData.getTableData().getSAPTableName();
                     		if (sapTableName == null) {
                     			sapTableName = curColumnData.getTableData().getSegmentDefinition();
                     		}

                     		if (sapTableName != null) {
                     			String columnName     = curColumnData.getDerivation();
                     			String fullColumnName = sapTableName + Constants.DERIVATION_EXCEPTION_TABLE_COLUMN_DELIMITER + columnName;
                     			actualDerivation      = (String) derivationExceptions.get(fullColumnName);
                     			if (actualDerivation == null) {
                     				// ==> try column name without table name
                     				actualDerivation = (String) derivationExceptions.get(columnName);
                     			}
                     			if (TraceLogger.isTraceEnabled()) {
                     				if (actualDerivation != null) {
                     					TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
                     						               MessageFormat.format("Determining derivation exception for column ''{0}'': ''{1}''", new Object[]{sapTableName, actualDerivation})); //$NON-NLS-1$
                     				}
                     			}
                     		} // end of if (sapTableName != null)
                    	 	} // end of if (derivationExceptions != null)

                     	// check if there is a derivation for the column (ODBC)
                     	// type to be established
                     	if (actualDerivation == null && columnDerivations != null) {
                     		colDerivTemplate = (String) columnDerivations.get(srcFlowVar.getODBCType().getName().toUpperCase());
                     		if (colDerivTemplate != null) 
                     		{
                     			// ==> column derivation is available ==> set it
                     			actualDerivation = colDerivTemplate; //MessageFormat.format(colDerivTemplate,
                     			                                     //                     new Object[] { srcColumnId });

                     		}
                     	} 
                    	 
                     	if (actualDerivation != null) {
                     		actualDerivation = StringUtils.replaceString(actualDerivation, 
                     		                                             Constants.DERIVATION_REPLACEMENT, srcColumnId);
//                     		actualDerivation = MessageFormat.format(actualDerivation, new Object[] { srcColumnId });

                     		// create and add a derivation instance ...
                     		dsDerivation = createDSDerivation(srcColumnName, actualDerivation, actualDerivation);
                     		trgFlowVar.getHasValue_Derivation().add(dsDerivation);
                     	}
                     } // end of if (sourceStage.isTransformerStage())
                     

                     hasVarFound = true;

                     if (TraceLogger.isTraceEnabled()) 
                     {
                        TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Var '" + srcFlowVar.getName()
                                                                  + "' adapted and source id = '" + srcColumnId
                                                                  + "' set.");
                     }
                  } // end of if (mappingColumnName.equals(srcFlowVar.getName()))
               } // end of while(srcListIter.hasNext() && !hasVarFound)

               if (!hasVarFound) 
               {
                  // ATTENTION: column name could not be mapped with columns
                  // on the passed source link ...
                  // ==> create a derivation for that column data
                  if (TraceLogger.isTraceEnabled()) 
                  {
                     TraceLogger.trace(TraceLogger.LEVEL_FINEST, "No mapping found for column '" + mappingColumnName
                                                               + "' ==> create a derivation.");
                  }

                  // check if there is a substitution tag for LINK NAME inside the 'derivation' string
                  if (curColumnData.getTransformerSrcMapping().indexOf("{0}") > -1) 
                  {
                     actualDerivation = MessageFormat.format(curColumnData.getTransformerSrcMapping(),
                                                             new Object[] { srcLinkName });
                  } 
                  else 
                  {
                     actualDerivation = curColumnData.getTransformerSrcMapping();
                  } // end of if (curColumnData.getTransformerSrcMapping().indexOf("{0}") > -1)
                  
                  if (TraceLogger.isTraceEnabled()) 
                  {
                     TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Value Derivation = " + actualDerivation);
                  }

                  dsDerivation = createDSDerivation("", actualDerivation, actualDerivation);
                  trgFlowVar.getHasValue_Derivation().add(dsDerivation);
               } // end of if (!hasVarFound)
            } // end of if (srcStageInputPin != null)
         } // end of (else) if (mappingColumnData instanceof StageVariable)
      } // end of while(trgListIter.hasNext())

      if (TraceLogger.isTraceEnabled()) 
      {
         TraceLogger.exit();
      }
   } // end of assignTargetColsToSourceCols()


   public static void cleanupServiceToken(ServiceToken srvcToken)
   {
      DataStageObjectFactory dsFactory;
      boolean                hasTokenRemoved = false;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("Service Token = " + srvcToken);
      }
      
      if (srvcToken != null)
      {
         dsFactory = (DataStageObjectFactory) _ObjectCache.get(srvcToken);
         
         if (dsFactory != null)
         {
            // close the open Universe DB connection
            srvcToken.uvDBDisconnect();
            
            // and remove the instance from object cache
            _ObjectCache.remove(srvcToken);
            hasTokenRemoved = true;
         } // end of if (dsFactory != null)
      } // end of if (srvcToken != null)
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("Service Token removed: " + hasTokenRemoved);
      }
   } // end of cleanupServiceToken()
   
   
   /**
    * This method creates a DSLOcalContainerDef instance and the corresponding 
    * Container Stage instance.
    * <p>
    * Instance attributes are set to default values and can be overwritten after 
    * instance creation. 
    * 
    * @param parentContainerObj  container object to which the new container definition 
    *                            is to be associated
    * @param stageName           name of the stage (van be null)
    * @param stageParams         optional map containing link parameters
    * 
    * @return new DSStage instance
    */
   public DSStage createContainerStage(MainObject parentContainerObj, String stageName, 
                                       ObjectParamMap stageParams)
          throws DSAccessException
   {
      DSLocalContainerDef localContainerDef;
      DSStage             containerStage;
      String              tmpInternalId;
      String              curContainerCounter;
      String              containerId;
      
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("container stage name = " + stageName);
      }
      
      // first create the Local Container ...
      localContainerDef = createDSLocalContainerDef(parentContainerObj, stageName);
      
      // create a new container id and store the id in ContainerID map
      curContainerCounter = _NextContainerCounter.getNextValue().toString();
      _ContainerIdMap.put(localContainerDef, curContainerCounter);

      // update stage parameters with new container id
      if (stageParams == null)
      {
         // now stage parameters yet --> create new map
         stageParams = new ObjectParamMap(OBJECT_TYPE_DEFAULT); 
      }
      containerId = getContainerId(localContainerDef, CONTAINER_ID_TEMPLATE);
      stageParams.put(CONTAINER_STAGE_PARAM_VIEW, containerId);

      // DO NOT MODIFY or REMOVE !!!!!!!!!!!!!!!!!!!!!!!!!
      // set the container id to the SHORT DESCRIPTION field
      localContainerDef.setShortDescription(containerId);
      
      // create the associated stage
      containerStage = createDSStage(parentContainerObj, stageName, null, stageParams);

      // set the "InternalId" of container stage
      tmpInternalId = DataStageObjectFactory.CONTAINER_STAGE_ID_PREFIX + curContainerCounter; 
      containerStage.setInternalID(tmpInternalId);
      
      // set links between stage and container  
      localContainerDef.getDefines_Stage().add(containerStage);
      
      // ... and also associate container stage with LocalContainer object
      containerStage.setHas_ContainerDef(localContainerDef);
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("container stage = " + containerStage);
      }
      
      return(containerStage);
   } // end of createContainerStage()

   
   /**
    * This method creates a 'default' DSDesignView instance.
    * <p>
    * Instance attributes are set to default values and can be overwritten after instance creation.
    *  
    * @param parentContainerObj  container object to which the view definition is to be associated
    * 
    * @return initialized DSDesignView instance
    */
   public DSDesignView createDSDesignViewDefinition(MainObject parentContainerObj)
          throws DSAccessException
   {
      String containerId;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }
      
      DSDesignView vDesignView = _DSFactory.createDSDesignView();
      setXMetaRepObjectBaseInfo(vDesignView);
      
      // get the id of the container ...
      containerId = getContainerId(parentContainerObj, CONTAINER_ID_TEMPLATE);

      // set default values
      vDesignView.setName(DESIGN_VIEW_NAME);
      vDesignView.setInternalID(containerId);
      vDesignView.setNextID(_NextJobObjCounter.getNextValue());
      vDesignView.setIsTopLevel(new Boolean(false));
      vDesignView.setZoomValue(new Integer(100));
      vDesignView.setGridLines(new Integer(0));
      vDesignView.setSnapToGrid(new Integer(1));
      vDesignView.setIsSystem(new Boolean(false));
      vDesignView.setContainerViewSizing(JOB_DEFAULT_DESIGN_VIEW_SIZING);
//      vDesignView.setContainerViewSizing("0022 0029 1040 0850 0000 0000 0000 0000"); //$NON-NLS-1$
      vDesignView.setNextStageID(_NextJobObjCounter.getNextValue());
      
      vDesignView.setStageList("");
      vDesignView.setStageYPos("");
      vDesignView.setStageXPos("");
      vDesignView.setStageXSize("");
      vDesignView.setStageYSize("");
      vDesignView.setStageTypes("");

      vDesignView.setLazyLoadInfo("");


      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("DesignViewDefinition = " + vDesignView);
      }
      
      return(vDesignView);
   } // end of createDSDesignViewDefinition()

   
   /**
    * This method creates a 'default' DSCanvasAnnotation instance.
    * <p>
    * Instance attributes are set to default values and can be overwritten after instance creation. 
    * 
    * @param text  annotation text
    * 
    * @return initialized DSCanvasAnnotation instance
    */
   public DSCanvasAnnotation createDSCanvasAnnotation(DSJobDef jobDef, String text)
          throws DSAccessException
   {
      DSCanvasAnnotation vAnnotation;
      String             vContainerId;
      Integer            vNextIdInt;
   
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }
      
      vNextIdInt  = _NextJobObjCounter.getNextValue();
      vAnnotation = _DSFactory.createDSCanvasAnnotation();
      setXMetaRepObjectBaseInfo(vAnnotation);
      
      // get the id of the container ...
      vContainerId = getContainerId(jobDef, DEFAULT_ANNOTATION_ID_TEMPLATE) + vNextIdInt;

      // ... setup and build the annotation id
      vAnnotation.setInternalID(vContainerId);
      vAnnotation.setAnnotationText(text);
      vAnnotation.setName(vAnnotation.getInternalID());
      vAnnotation.setAnnotationType(new Integer(0));
      vAnnotation.setTextColor(new Integer(0));
      vAnnotation.setTextHorizontalJustification(new Integer(0));
      vAnnotation.setTextVerticalJustification(new Integer(1));
      vAnnotation.setBackgroundTransparent(new Boolean(false));
      vAnnotation.setBorderVisible(new Boolean(true));

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("annotation = " + vAnnotation);
      }
      
      return(vAnnotation);
   } // end of createDSCanvasAnnotation()
   
   
   /**
    * This method creates a 'default' DSColumnDefinition instance.
    * <p>
    * Instance attributes are set to default values and can be overwritten after 
    * instance creation. 
    * 
    * @return initialized DSColumnDefinition instance
    */
   public DSColumnDefinition createDSColumnDefinition()
   {
      DSColumnDefinition vNewColDefintion;
      
      if (TraceLogger.isTraceEnabled())
      {
//         TraceLogger.entry();
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "ENTRY");
      }
      
      vNewColDefintion = _DSFactory.createDSColumnDefinition();
      setXMetaRepObjectBaseInfo(vNewColDefintion);
      
      if (TraceLogger.isTraceEnabled())
      {
//         TraceLogger.exit("RETURN ColumnDefintion = " + vNewColDefintion);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "RETURN ColumnDefintion = " + vNewColDefintion);
      }
      
      return(vNewColDefintion);
   } // end of createDSColumnDefinition()
   
   
   /**
    * This method creates a 'default' DSDataItemProps instance.
    * <p>
    * Instance attributes are set to default values and can be overwritten after 
    * instance creation. 
    * 
    * @param isUnicode true if the 'unicode' attribute is to be set otherwise false
    * 
    * @return initialized DSDataItemProps instance
    */
   public DSDataItemProps createDSDataItemProps(Boolean isUnicode)
   {
      DSDataItemProps vNewDataItemProps;
      
      if (TraceLogger.isTraceEnabled())
      {
//         TraceLogger.entry("is unicode = " + isUnicode);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "ENTRY is unicode = " + isUnicode);
      }
      
      vNewDataItemProps = _DSFactory.createDSDataItemProps();
      setXMetaRepObjectBaseInfo(vNewDataItemProps);
      
      // setup passed and default values
      vNewDataItemProps.setIsUString(isUnicode);
      
      if (TraceLogger.isTraceEnabled())
      {
//         TraceLogger.exit("DataItemProps = " + vNewDataItemProps);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "RETURN DataItemProps = " + vNewDataItemProps);
      }
      
      return(vNewDataItemProps);
   } // end of createDSDataItemProps()
   
   
   /**
    * This method creates a 'default' DSDerivation instance.
    * <p>
    * Instance attributes are set to default values and can be overwritten after 
    * instance creation. 
    * 
    * @param sourceColumn  derivation source column
    * @param expr          expression (value)
    * @param parsedExpr    parsed expression (value)
    * 
    * @return initialized DSDerivation instance
    */
   public DSDerivation createDSDerivation(String sourceColumn, String expr, 
                                          String parsedExpr)
   {
      DSDerivation vNewDerivation;
      
      if (TraceLogger.isTraceEnabled())
      {
//         TraceLogger.entry("src col = " + sourceColumn);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "ENTRY src col = " + sourceColumn);
      }
      
      vNewDerivation = _DSFactory.createDSDerivation();
      setXMetaRepObjectBaseInfo(vNewDerivation);
      
      // setup passed and default values
      vNewDerivation.setSourceColumn(sourceColumn);
      vNewDerivation.setExpression(expr);
      vNewDerivation.setParsedExpression(parsedExpr);
      
      if (TraceLogger.isTraceEnabled())
      {
//         TraceLogger.exit("Derivation = " + vNewDerivation);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "RETURN Derivation = " + vNewDerivation);
      }
      
      return(vNewDerivation);
   } // end of createDSDerivation()
   
   
   /**
    * This method creates a 'default' DSFilterConstraint instance. 
    * <p>
    * Instance attributes are set to default values and can be overwritten after 
    * instance creation. 
    * 
    * @param filterExpr       filter expression (value)
    * @param parsedContraint  parsed constraint (value)
    * @param constraintUsage  usage of constraint
    * 
    * @return initialized DSFilterConstraint instance
    */
   public DSFilterConstraint createDSFilterConstraint(String filterExpr, String parsedConstraint, 
                                                      ConstraintUsageEnum constraintUsage)
   {
      DSFilterConstraint  newFilterConstraint;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }
      
      newFilterConstraint = _DSFactory.createDSFilterConstraint();
      setXMetaRepObjectBaseInfo(newFilterConstraint);
      
      // setup passed and default values
      newFilterConstraint.setSourceColumns(null);
      newFilterConstraint.setFilterExpression(filterExpr);
      newFilterConstraint.setParsedConstraint(parsedConstraint);
      newFilterConstraint.setShortDescription(null);
      newFilterConstraint.setStageVars(null);
      newFilterConstraint.setUsage(constraintUsage);
            
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("FilterConstraint = " + newFilterConstraint);
      }
      
      return(newFilterConstraint);
   } // end of createDSFilterConstraint()
   
   
   /**
    * This method creates a 'default' DSFlowVariable instance. Flow variable values, 
    * e.g. name, isKey, description, etc.,  are retrieved from passed ColumnData instance.
    * <p>
    * Instance attributes are set to default values and can be overwritten after 
    * instance creation. 
    * 
    * @param colData       column data
    * 
    * @return initialized DSFlowVariable instance
    */
   public DSFlowVariable createDSFlowVariable(FlowVarData flowVarData)
   {
      FlowVariableType flowVariableType;
      DSFlowVariable   newFlowVar;
      DSDataItemProps  curDataItemProps;
      ODBCTypeEnum     colODBCType;
      TypeCodeEnum     colTypeCode;
      Integer          maximumLength;

      if (TraceLogger.isTraceEnabled())
      {
//         TraceLogger.entry("name = " + flowVarData);
         if (flowVarData != null)
         {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "ENTRY name = " + flowVarData +
                                                        " - type = " + flowVarData.getType() +
                                                        " - length = " + flowVarData.getLength() +
                                                        " - scale = " + flowVarData.getScale());
         }
      }

      // what ODBC type and type code do we use ???
      flowVariableType = resolveDBMType(flowVarData);
      colODBCType      = flowVariableType.getOdbcType();
      colTypeCode      = flowVariableType.getTypeCode();

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "ODBCType = " + colODBCType +
                                                     " - ODBCType value " + colODBCType +
                                                     " - Type code  " + colTypeCode);
      }
      
      // create the DSFlowVariable instance
      newFlowVar = createDSFlowVariable(flowVarData.getName(), colODBCType, colTypeCode);

      // set length for specific types
      maximumLength = flowVarData.getLength();

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "col max len = " + maximumLength);
      }
      
      // set length for specific types
      switch(colODBCType.getValue())
      {
         case ODBCTypeEnum.BIGINT:
         case ODBCTypeEnum.BINARY:
         case ODBCTypeEnum.INTEGER:
         case ODBCTypeEnum.SMALLINT:
         case ODBCTypeEnum.TINYINT:
         case ODBCTypeEnum.NUMERIC:
              newFlowVar.setMaximumLength(maximumLength);
              newFlowVar.setIsSigned(Boolean.TRUE);
              break;
              
         case ODBCTypeEnum.CHAR:
         case ODBCTypeEnum.WCHAR:
              if (maximumLength == null)
              {
                 maximumLength = new Integer(Constants.CHAR_DEFAULT_LENGTH);
              }
              newFlowVar.setMaximumLength(maximumLength);
              newFlowVar.setMinimumLength(newFlowVar.getMaximumLength());

              // if current FlowVar is 'unicode' ==> set Unicode flag
              if (flowVarData.isUnicode())
              {
                 // create a DataItemProps instance ...
                 curDataItemProps = createDSDataItemProps(Boolean.TRUE);
                 newFlowVar.setHas_DSDataItemProps(curDataItemProps);
              } // end of if (flowVarData.isUnicode())
              break;
              
         case ODBCTypeEnum.DATE:
         case ODBCTypeEnum.TIME:
         case ODBCTypeEnum.TIMESTAMP:
         case ODBCTypeEnum.GUID:
              break;
              
         case ODBCTypeEnum.DECIMAL:
         case ODBCTypeEnum.REAL:
              newFlowVar.setMaximumLength(maximumLength);
              newFlowVar.setFractionDigits(new Integer(flowVarData.getScale()));
//              newFlowVar.setIsSigned(Boolean.TRUE);
              break;

         case ODBCTypeEnum.DOUBLE:
         case ODBCTypeEnum.FLOAT:
              // we need a DSDataItem Property if there is a 'scale'
              if (flowVarData.getScale() > 0)
              {
                 curDataItemProps = newFlowVar.getHas_DSDataItemProps();
                 if (curDataItemProps == null)
                 {
                    curDataItemProps = createDSDataItemProps(null);
                    newFlowVar.setHas_DSDataItemProps(curDataItemProps);
                 }
                 curDataItemProps.setScale(new Integer(flowVarData.getScale()));
                 curDataItemProps.setSCDPurpose(INTEGER_INIT_VALUE);
                 curDataItemProps.setSignOption(INTEGER_INIT_VALUE);
                 curDataItemProps.setSyncIndicator(Boolean.FALSE);
              } // end of if (flowVarData.getScale() > 0)
              newFlowVar.setMaximumLength(maximumLength);
              break;
              
         case ODBCTypeEnum.VARBINARY:
         case ODBCTypeEnum.LONGVARBINARY:
         case ODBCTypeEnum.LONGVARCHAR:
         case ODBCTypeEnum.VARCHAR:
         case ODBCTypeEnum.WLONGVARCHAR:
         case ODBCTypeEnum.WVARCHAR:
              if (maximumLength == null)
              {
                 maximumLength = new Integer(Constants.CHAR_DEFAULT_LENGTH);
              }
              newFlowVar.setMaximumLength(maximumLength);
              
              // if current FlowVar is 'unicode' ==> set Unicode flag
              if (flowVarData.isUnicode())
              {
                 // create a DataItemProps instance ...
                 curDataItemProps = createDSDataItemProps(Boolean.TRUE);
                 newFlowVar.setHas_DSDataItemProps(curDataItemProps);
              } // end of if (flowVarData.isUnicode())
              break;
              
         case ODBCTypeEnum.BIT:
         case ODBCTypeEnum.UNKNOWN:
         default:
              newFlowVar.setMaximumLength(maximumLength);
      } // end of switch(colODBCType.getValue())

      newFlowVar.setLongDescription(flowVarData.getDescription());
      
      if (TraceLogger.isTraceEnabled())
      {
//         TraceLogger.exit("FlowVar = " + newFlowVar);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "RETURN FlowVar = " + newFlowVar);
      }

      return(newFlowVar);
   } // end of createDSFlowVariable()
   
   
   /**
    * This method creates a 'default' DSFlowVariable instance.
    * <p>
    * Instance attributes are set to default values and can be overwritten after 
    * instance creation. 
    * 
    * @param varName       DS variable name
    * @param odbcTypeEnum  ODBC type (enumeration value)
    * @param typeCodeEnum  type code (enumeration value)
    * 
    * @return initialized DSFlowVariable instance
    */
   public DSFlowVariable createDSFlowVariable(String varName, ODBCTypeEnum odbcTypeEnum, 
                                              TypeCodeEnum typeCodeEnum)
   {
      DSFlowVariable newFlowVar;

      if (TraceLogger.isTraceEnabled())
      {
//         TraceLogger.entry("name = " + varName);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "ENTRY name = " + varName);
      }

      newFlowVar = _DSFactory.createDSFlowVariable();
      setXMetaRepObjectBaseInfo(newFlowVar);

      // setup passed and default values
      newFlowVar.setName(varName);
      newFlowVar.setODBCType(odbcTypeEnum);
      newFlowVar.setTypeCode(typeCodeEnum);
      newFlowVar.setIsKey(new Boolean(false));
      newFlowVar.setAllowCRLF(new Integer(0));
      newFlowVar.setArrayHandling(new Integer(0));
      newFlowVar.setDisplaySize(new Integer(0));
      newFlowVar.setFractionDigits(new Integer(0));
      newFlowVar.setGroup(new Integer(0));
      newFlowVar.setIsNullable(new Boolean(false));
      newFlowVar.setIsSigned(new Boolean(false));
      newFlowVar.setLevelNumber(new Integer(0));
      newFlowVar.setPadNulls(new Integer(0));
      newFlowVar.setPKeyIsCaseless(new Integer(0));
      newFlowVar.setSortingOrder(new Integer(0));
      newFlowVar.setSortKey(new Integer(0));
      newFlowVar.setSortType(new Integer(0));

      if (TraceLogger.isTraceEnabled())
      {
//         TraceLogger.exit("FlowVar = " + newFlowVar);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "RETURN FlowVar = " + newFlowVar);
      }

      return(newFlowVar);
   } // end of createDSFlowVariable()
   
   
   /**
    * This method creates a 'default' DSItem instance. It will be added to the 
    * passed DS Job Folder ('jobFolderRid') and contains the reference to the 
    * passed DSJobDef instance. 
    * <p>
    * @param jobDef        job definition to be added
    * @param jobFolderRid  DS Job folder RID
    * 
    * @return created DSItem instance
    * 
    */
   public DSItem createDSJobItem(DSJobDef jobDef, String JobFolderRid) 
          throws DSAccessException
   {
      DSItem vJobItem;
   
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("JobDef = " + jobDef.getName());
      }
      
      vJobItem = _DSFactory.createDSItem();
      setXMetaRepObjectBaseInfo(vJobItem);
      
      // set the project info
      vJobItem.setProjectNameSpace(_srvcToken.getProjectNameSpace());
      vJobItem.setName(jobDef.getName());
      vJobItem.setReposId(jobDef.get_xmeta_repos_object_id());
      vJobItem.setClassName("JobDefn");                        //$NON-NLS-1$
      vJobItem.setIsReference(new Boolean(false));
      vJobItem.setParentRID(JobFolderRid);
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("JobItem = " + vJobItem);
      }
      
      return(vJobItem);
   } // end of createDSJobItem()
   
   
   /**
    * This method creates a 'default' DSJobDef instance.
    * <p>
    * Instance attributes are set to default values and can be overwritten after instance creation. 
    * 
    * @param jobName        DS job name
    * @param directoryPath  DS directory path
    * @param description    job description
    * 
    * @return initialized DSJobDef instance
    */
   public DSJobDef createDSJobDef(String jobName, String directoryPath, String description)
   {
      DSJobDef vJobDef;
      String   curContainerCounter;
      String   vContainerId;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("name= " + jobName);
      }
      
      vJobDef = _DSFactory.createDSJobDef();
   
      setXMetaRepObjectBaseInfo(vJobDef);
      vJobDef.setName(jobName);
      vJobDef.setInternalID(INTERNAL_ID);
      vJobDef.setShortDescription(""); //$NON-NLS-1$
      vJobDef.setLongDescription(description);
      vJobDef.setDSNameSpace(_srvcToken.getProjectNameSpace());
      vJobDef.setAllowMultipleInvocations(new Boolean(false));
      vJobDef.setAct2ActOverideDefaults(new Boolean(false));
      vJobDef.setExpressionSemanticCheckFlag(new Integer(0));
      vJobDef.setJobReportFlag(new Boolean(false));
      vJobDef.setMFProcessMetaDataXMLFileExchangeMethod(new Integer(0));
      vJobDef.setReservedWordCheck(new Integer(1));
      vJobDef.setControlAfterSubr(new Boolean(false));
      vJobDef.setDSJobType(new Integer(3));
      vJobDef.setTransactionSize(new Integer(0));
      vJobDef.setNULLIndicatorPosition(new Integer(0));
      vJobDef.setParameterFileDDName(PARAMETER_FILE_DD_NAME);
      vJobDef.setAct2ActIPCTimeout(new Integer(0));
      vJobDef.setTraceOption(new Integer(0));
      vJobDef.setWebServiceEnabled(new Boolean(false));
      vJobDef.setEnableCacheSharing(new Boolean(false));
      vJobDef.setCopyLibPrefix("ARDT"); //$NON-NLS-1$
      vJobDef.setMFProcessMetaData(new Integer(0));
      vJobDef.setAct2ActEnableRowBuffer(new Boolean(false));
      vJobDef.setCenturyBreakYear(new Integer(30));
      vJobDef.setPgmCustomizationFlag(new Boolean(false));
      vJobDef.setVersion("50.0.0"); //$NON-NLS-1$
      vJobDef.setAct2ActUseIPC(new Boolean(false));
      vJobDef.setNextAliasID(new Integer(2));
      vJobDef.setNextID(new Integer(0));
      vJobDef.setJobType(JobTypeEnum.PARALLEL_LITERAL);
      vJobDef.setRelStagesInJobStatus(new Integer(-1));
      vJobDef.setRecordPerformanceResults(new Integer(0));
      vJobDef.setCategory(directoryPath);

      // create a new container id and store it in the container id map
      curContainerCounter = _NextContainerCounter.getNextValue().toString();
      vContainerId        = MessageFormat.format(CONTAINER_ID_TEMPLATE, new Object[] { curContainerCounter } ); 
      _ContainerIdMap.put(vJobDef, curContainerCounter);
      
      vJobDef.setContainer(vContainerId);
      vJobDef.setRuntimeColumnPropagation(new Boolean(false));
      vJobDef.setAct2ActBufferSize(new Integer(0));
      vJobDef.setUploadable(new Boolean(false));
      vJobDef.setIsSystem(new Boolean(false));
      vJobDef.setIMSProgType(new Integer(0));
      vJobDef.setIsTemplate(new Boolean(false));

      vJobDef.setCobolProgramName(null);
      vJobDef.setCodeGenLocation(null);
      vJobDef.setCompileJCLName(null);
      vJobDef.setDateFormat(null);
      vJobDef.setDBMSPassword(null);
      vJobDef.setDBMSSystemName(null);
      vJobDef.setDBMSUserName(null);
      vJobDef.setFromTemplate(null);
      vJobDef.setPassword(null);
      vJobDef.setJobControlCode(null);
      vJobDef.setJobSeqCodeGenOpts(null);
      vJobDef.setMFProcessMetaDataIP(null);
      vJobDef.setMFProcessMetaDataMachineProfile(null);
      vJobDef.setMFProcessMetaDataPassword(null);
      vJobDef.setMFProcessMetaDataUserName(null);
      vJobDef.setMFProcessMetaDataXMLFilename(null);
      vJobDef.setMFProcessMetaDataXMLLocation(null);
      vJobDef.setNLSLocale(null);
      vJobDef.setNLSMapName(null);
      vJobDef.setNULLIndicatorValue(null);

      vJobDef.setOrchestrateCode(null);
      vJobDef.setOSHCollatingSequence(null);
      vJobDef.setOSHPrecompileDirectives(null);
      vJobDef.setOSHPreRunDirectives(null);
      vJobDef.setParameterFileName(null);
      vJobDef.setPlatformType(null);
      vJobDef.setRunJCLName(null);
      vJobDef.setTeradataAccountId(null);
      vJobDef.setTeradataTDPid(null);
      vJobDef.setUserName(null);
      vJobDef.setValidationStatus(new Integer(0));
      vJobDef.setPerformanceResultsFile(null);

      // create the DSMetaBag
      createDSJobMetaBag(vJobDef);

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("jobdef = " + vJobDef);
      }
      
      return(vJobDef);
   } // end of createDSJobDef()

   
   /**
    * creates the DSMetaBag for a DSJobDef instance
    * 
    * @param jobDef     JobDef instance
    */
   private void createDSJobMetaBag(DSJobDef jobDef) 
   {
      DSMetaBag metaBag = createDSMetaBag(); // _DSFactory.createDSMetaBag();
      
      // setup 'XMetaRepositoryObject'
      setXMetaRepObjectBaseInfo(metaBag);
      
      metaBag.setOf_DSJobDef(jobDef);
      metaBag.setOwners(METABAG_OWNER_NAME);
      metaBag.setNames(METABAG_JOBDEF_NAMES);
      metaBag.setValues(METABAG_JOBDEF_VALUES);
      jobDef.setHas_DSMetaBag(metaBag);
      
   } // end of createDSJobMetaBag() 

   
   /**
    * creates a DSMetaBag for a DSInputPin instance
    * 
    * @param inputPin  DSInputPin instance to be set
    * 
    * @return new DSMetaBag instance
    */
   private DSMetaBag createDSMetaBagForInputPin(List metaBagDataList, DSInputPin inputPin) 
   {
      DSMetaBag metaBag;
      
      metaBag = null;
      if (metaBagDataList.size() > 0)
      {
         metaBag = inputPin.getHas_DSMetaBag();
         
         // create a DSMetaBag instance if there isn't one
         if (metaBag == null)
         {
            metaBag = createDSMetaBag();
            metaBag.setOf_DSInputPin(inputPin);
         } // end of if (metaBag == null)
         
         addMetaBagData(metaBag, metaBagDataList);
      } // end of if (metaBagDataList.size() > 0)

      return(metaBag);
   } // end of createDSMetaBagForPin() 

   
   /**
    * creates a DSMetaBag for a DSOutputPin instance
    *
    * @param outputPin  DSOututPin instance to be set
    * 
    * @return new DSMetaBag instance
    */
   private DSMetaBag createDSMetaBagForOutputPin(List metaBagDataList, DSOutputPin outputPin) 
   {
      DSMetaBag metaBag;
      
      metaBag = null;
      if (metaBagDataList.size() > 0)
      {
         metaBag = outputPin.getHas_DSMetaBag();
         
         // create a DSMetaBag instance if there isn't one
         if (metaBag == null)
         {
            metaBag = createDSMetaBag();
            metaBag.setOf_DSOutputPin(outputPin);
         } // end of if (metaBag == null)
         
         addMetaBagData(metaBag, metaBagDataList);
      } // end of if (metaBagDataList.size() > 0)

      return(metaBag);
   } // end of createDSMetaBagForPin() 

   
   /**
    * This method creates a generic DSMetaBag instance.
    * <p>
    * Instance attributes are set to default values and can be overwritten after 
    * instance creation. 
    *
    * @return DSMetaBag instance
    */
   public DSMetaBag createDSMetaBag() 
   {
      DSMetaBag metaBag = _DSFactory.createDSMetaBag();
      
      // setup 'XMetaRepositoryObject'
      setXMetaRepObjectBaseInfo(metaBag);
      
      metaBag.setOf_DSTableDefinition(null);
      metaBag.setOf_DSDataElement(null);
      metaBag.setOf_DSTransform(null);
      metaBag.setOf_DSStageType(null);
      metaBag.setOf_DSJobDef(null);
      metaBag.setOf_DSDesignView(null);
      metaBag.setOf_DSStage(null);
      metaBag.setOf_DSRoutine(null);
      metaBag.setOf_DSOutputPin(null);
      metaBag.setOf_DSDataConnection(null);
      metaBag.setOf_DSDataQualitySpec(null);
      metaBag.setConditions(null);
      metaBag.setOwners(null);
      metaBag.setNames(null);
      metaBag.setValues(null);

      return(metaBag);
   } // end of createDSMetaBag() 

   
   /**
    * This method creates a 'default' DSLink instance.
    * <p>
    * Instance attributes are set to default values and can be overwritten after instance creation. 
    * 
    * @param parentContainerObj  container object to which the new link will be associated with
    * @param linkName            link name
    * @param useCounterForName   if true, a unique number is appended to the passed name
    * @param linkType            link type (for example LinkTypeEnum.PRIMARY_LITERAL)
    * @param linkParams          optional map containing link parameters
    * 
    * @return initialized DSLink instance
    * 
    * @throws DSAccessException if an unrecoverable error occurs
    */
   public DSLink createDSLink(MainObject parentContainerObj, String linkName, boolean useCounterForName, 
                              ObjectParamMap linkParams)
          throws DSAccessException
   {
      return(createDSLink(parentContainerObj, linkName, useCounterForName, 
                          LinkTypeEnum.PRIMARY_LITERAL, linkParams));
   } // end of createDSLink()
   
   
   /**
    * This method creates a 'default' DSLink instance.
    * <p>
    * Instance attributes are set to default values and can be overwritten after instance creation. 
    * 
    * @param parentContainerObj container object to which the new link will be associated with
    * @param linkName           link name
    * @param linkType           link type (LINK_TYPE_INPUT or LINK_TYPE_OUTPUT)
    * @param useCounterForName  if true, a unique number is appended to the passed name
    * @param linkParams         optional map containing link parameters
    * 
    * @return initialized DSLink instance
    * 
    * @throws DSAccessException if an unrecoverable error occurs
    */
   public DSLink createDSLink(MainObject parentContainerObj, String linkName, boolean useCounterForName, 
                              LinkTypeEnum linkType, ObjectParamMap linkParams)
          throws DSAccessException
   {
      DSLink   newLink;
      String   realName;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("name = " + linkName);
      }
      
      newLink = _DSFactory.createDSLink();
      setXMetaRepObjectBaseInfo(newLink);

      // use DSLink class as link name if none has been specified
      if (linkName == null)
      {
         linkName = DSLink.class.getName().substring(DSLink.class.getName().indexOf('.')+1); // DSLink
      }
      
      if (useCounterForName)
      {
         realName = linkName + _NextJobObjCounter.getNextValue().toString();
      }
      else
      {
         realName = linkName;  
      }
      
      newLink.setName(realName);
      newLink.setLinkType(linkType);
      newLink.setIsSystem(new Boolean(false));

      // associate with passed Container Job Def
      if (parentContainerObj instanceof DSJobDef)
      {
         DSJobDef jobDef = (DSJobDef) parentContainerObj;
         
         newLink.setOf_JobDef(jobDef);
         jobDef.getContains_JobObject().add(newLink);
      }
      else
      {
         if (parentContainerObj instanceof DSLocalContainerDef)
         {
            DSLocalContainerDef localContainer = (DSLocalContainerDef) parentContainerObj;
            
            newLink.setOf_ContainerDef(localContainer);
            localContainer.getContains_JobObject().add(newLink);
         }
         else
         {
            throw new DSAccessException("120500E", 
                                        new String[] { parentContainerObj.getClass().getName() });
         } // end of (else) if (parentContainerObj instanceof DSLocalContainerDef)
      } // end of (else) if (parentContainerObj instanceof DSJobDef)

      // set some additional default values ...
      
      // add passed parameters as link parameters
      addParamsToLink(newLink, linkParams);

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("link = " + newLink);
      }
      
      return(newLink);
   } // end of createDSLink()
   
   
   /**
    * This method creates a 'default' DSContainerDef instance.
    * <p>
    * Instance attributes are set to default values and can be overwritten 
    * after instance creation. 
    * 
    * @param parentContainerObj  container object to which the new container definition 
    *                            is to be associated
    * @param containerName       container name
    * 
    * @return initialized DSLocalContainerDef instance
    */
   private DSLocalContainerDef createDSLocalContainerDef(MainObject parentContainerObj, 
                                                         String containerName)
           throws DSAccessException
   {
      DSLocalContainerDef newDSContainerDef;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("name = " + containerName);
      }
      
      newDSContainerDef = _DSFactory.createDSLocalContainerDef();
      setXMetaRepObjectBaseInfo(newDSContainerDef);
      
      // associate with passed parent container definition
      if (parentContainerObj instanceof DSJobDef)
      {
         DSJobDef jobDef = (DSJobDef) parentContainerObj;
         
         newDSContainerDef.setOf_JobDef(jobDef);
         jobDef.getContains_JobObject().add(newDSContainerDef);
      }
      else
      {
         if (parentContainerObj instanceof DSLocalContainerDef)
         {
            DSLocalContainerDef localContainerDef = (DSLocalContainerDef) parentContainerObj;
            
            newDSContainerDef.setOf_ContainerDef(localContainerDef);
            localContainerDef.getContains_JobObject().add(newDSContainerDef);
         }
         else
         {
            throw new DSAccessException("120500E", 
                                        new String[] { parentContainerObj.getClass().getName() });
         } // end of (else) if (dsContainerObj instanceof DSLocalContainerDef)
      } // end of (else) if (dsContainerObj instanceof DSJobDef)
      
      newDSContainerDef.setName(containerName);
      newDSContainerDef.setJobType(JobTypeEnum.SERVER_LITERAL);
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("local container def = " + newDSContainerDef.getName());
      }
      
      return(newDSContainerDef);
   } // end of createDSLocalContainerDef()
   
   
   /**
    * This method creates a 'default' DSParameterDef instance.
    * <p>
    * Instance attributes are set to default values and can be overwritten after instance creation. 
    * 
    * @param name           parameter name
    * @param typeCode       parameter type code
    * @param extendedType   extended parameter type
    * @param value          parameter value
    * @param displayPrompt  parameter prompt (if null, the name values is set for the prompt) 
    * 
    * @return initialized DSParameterVal instance
    */
   public DSParameterDef createDSParameterDef(String name, TypeCodeEnum typeCode,
                                              DSExtendedParamTypeEnum extendedType,
                                              String value, String displayPrompt)
          throws DSAccessException
   {
      DSParameterDef newDSParamDef;
      DSParameterSet dsParameterSet;
      String         promptString;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("name = " + name);
      }
      
      newDSParamDef = _DSFactory.createDSParameterDef();
      setXMetaRepObjectBaseInfo(newDSParamDef);
      
      if (displayPrompt == null)
      {
         promptString = name;
      }
      else
      {
         promptString = displayPrompt;
      }
      newDSParamDef.setUsage(ParamUsageEnum.DEFAULT_LITERAL);
      newDSParamDef.setDisplayCaption(promptString);
      newDSParamDef.setName(name);
      if(extendedType == DSExtendedParamTypeEnum.ENCRYPTED_LITERAL) 
      {
         newDSParamDef.setDefaultValue(_srvcToken.encrypt(value));
      } 
      else 
      {
         newDSParamDef.setDefaultValue(value);
      }
      newDSParamDef.setTypeCode(typeCode);
      newDSParamDef.setExtendedType(extendedType);
      
      // extended handling for parameter sets
      if (extendedType == DSExtendedParamTypeEnum.PARAMETERSET_LITERAL)
      {
         dsParameterSet = null;
         
         try
         {
            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.trace(TraceLogger.LEVEL_FINER, "search for parameter set '" + name + "'.");
            }
            
            // get the ParamterSet object
            dsParameterSet = DataStageAccessManager.getInstance().getDSParameterSet(name, _srvcToken);
            
            if (TraceLogger.isTraceEnabled())
            {
               if (dsParameterSet == null)
               {
                  TraceLogger.trace(TraceLogger.LEVEL_FINE, "param set = null");
               }
               else
               {
                  TraceLogger.trace(TraceLogger.LEVEL_FINE, "param set = " + dsParameterSet.getName());
               }
            }
         } // end of try
         catch(DSAccessException dsAccessExcpt)
         {
            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.traceException(dsAccessExcpt);
            }
         }
         
         if (dsParameterSet == null)
         {
            throw new DSAccessException("120600E", new String[]  { name }); 
         }
         
         newDSParamDef.setIs_ParameterSet(dsParameterSet);
      } // end of if (extendedType == DSExtendedParamTypeEnum.PARAMETERSET_LITERAL)
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("param def = " + newDSParamDef.getName());
      }
      
      return(newDSParamDef);
   } // end of createDSParameterDef()
   
   
   /**
    * This method creates a 'default' DSParameterVal instance.
    * <p>
    * Instance attributes are set to default values and can be overwritten after 
    * instance creation. 
    * 
    * @param name   parameter name
    * @param value  parameter value
    * 
    * @return initialized DSParameterVal instance
    */
   private DSParameterVal createDSParameterVal(String name, String value) 
   {
      DSParameterVal newDSParamVal;
      
      if (TraceLogger.isTraceEnabled())
      {
//         TraceLogger.entry("name = " + name);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "ENTRY name = " + name);
      }
      
      newDSParamVal = _DSFactory.createDSParameterVal();
      setXMetaRepObjectBaseInfo(newDSParamVal);
      
      newDSParamVal.setUsage(ParamUsageEnum.DEFAULT_LITERAL);
      newDSParamVal.setParamType(DSParamValEnum.DIRECT_LITERAL);
      newDSParamVal.setParameterName(name);
      newDSParamVal.setValueExpression(value);
      
      if (TraceLogger.isTraceEnabled())
      {
//         TraceLogger.exit("param val = " + newDSParamVal);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "RETURN param val = " + newDSParamVal);
      }
      
      return(newDSParamVal);
   } // end of createDSParameterVal()
   
   
   /**
    * This method creates a 'default' DSStage instance.
    * <p>
    * Instance attributes are set to default values and can be overwritten after instance creation. 
    * 
    * @param parentContainerObj container object to which the new stage will be associated with
    * @param stageName          name of the stage (van be null)
    * @param stageType          type of the stage to be created
    * @param stageParams        optional map containing link parameters
    * 
    * @return new DSStage instance
    */
   public DSStage createDSStage(MainObject parentContainerObj, String stageName, 
                                DSStageTypeEnum stageType, ObjectParamMap stageParams)
          throws DSAccessException
   {
      DSStage     vNewStage;
      DSStageType vCurStageType;
      String      vStageTypeName;
      String      actStageName;
      String      curContainerId;
      Integer     vNextIdInt;
      int         vStageTypeValue;
      
      if (TraceLogger.isTraceEnabled())
      {
         if (stageType == null)
         {
            TraceLogger.entry("stage type = null - stage name = " + stageName);
         }
         else
         {
            TraceLogger.entry("stage type = " + stageType.getName());
         }
      }
      
      vNewStage = _DSFactory.createDSStage();
      setXMetaRepObjectBaseInfo(vNewStage);
      
      // get stage type name and value
      vCurStageType = null;
      vNextIdInt    = _NextJobObjCounter.getNextValue();
      
      if (stageType == null)
      {
         vCurStageType   = null;
         vStageTypeName  = null;
         vStageTypeValue = -1;
      }
      else
      {
         vStageTypeName  = stageType.getName();
         vStageTypeValue = stageType.getValue();

         if (vStageTypeName != null)
         {
            vCurStageType = DataStageAccessManager.getInstance().getDSStageTypeDefinition(stageType, _srvcToken);

            // check if current Stage type (SAP Packs) is been installed ...
            if (vCurStageType == null)
            {
               throw new StageTypeNotInstalledException(vStageTypeName);
            }
         }
         
         // if stageName is not set ...
         if (stageName == null)
         {
            // ==> use stage type name as stage Name
            stageName = vCurStageType.getStageName();
         } // end of if (stageName == null)
         
      } // end of (else)if (stageType == null)
      
      actStageName = StringUtils.cleanFieldName(stageName);
      vNewStage.setName(actStageName + "_" + vNextIdInt.toString());
      vNewStage.setStageType(vStageTypeName);
      vNewStage.setOf_StageType(vCurStageType);
      
      // get id from parent container
      curContainerId = getContainerId(parentContainerObj, DEFAULT_STAGE_ID_TEMPLATE) + vNextIdInt;
      vNewStage.setInternalID(curContainerId);
      vNewStage.setNextID(new Integer(vNextIdInt.intValue() + 1));
      vNewStage.setIsSystem(new Boolean(false));
      vNewStage.setNextRecordID(new Integer(0));
      
      // associate with passed DSJobDef or DSLocalContainerDef respectively 
      if (parentContainerObj instanceof DSJobDef)
      {
         DSJobDef jobDef = (DSJobDef) parentContainerObj;
         
         vNewStage.setOf_JobDef(jobDef);
         jobDef.getContains_JobObject().add(vNewStage);
      }
      else
      {
         if (parentContainerObj instanceof DSLocalContainerDef)
         {
            DSLocalContainerDef containerDef = (DSLocalContainerDef) parentContainerObj;
            
            vNewStage.setOf_ContainerDef(containerDef);
            containerDef.getContains_JobObject().add(vNewStage);
         }
         else
         {
            throw new DSAccessException("120500E", 
                                        new String[] { parentContainerObj.getClass().getName() });
         }
      }
         
      
      if (vStageTypeValue == DSStageTypeEnum.CTRANSFORMER_STAGE)
      {
         vNewStage.setStageTypeIsActive(new Boolean(true));
         vNewStage.setAllowColumnMapping(null);
         vNewStage.setNextRecordID(null); 
      } 
      else
      {
         if (vStageTypeValue == DSStageTypeEnum.PX_LOOKUP_STAGE)
         {
            vNewStage.setStageTypeIsActive(new Boolean(true));
            vNewStage.setAllowColumnMapping(new Boolean(false));
         } 
         else
         {
            if (vStageTypeValue == DSStageTypeEnum.PX_COLUMN_GENERATOR_STAGE)
            {
               vNewStage.setStageTypeIsActive(new Boolean(true));
               vNewStage.setAllowColumnMapping(new Boolean(false));
            }
            else 
            {
               vNewStage.setStageTypeIsActive(new Boolean(false));
               vNewStage.setAllowColumnMapping(new Boolean(false));
            }
         }
      }

      // add passed parameters to stage parameters
      if (stageParams != null)
      {
         addParamsToObject(vNewStage.getHas_ParameterVal(), stageParams);
         
      } // end of if (stageParams != null)
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("stage = " + vNewStage);
      }
      
      return(vNewStage);
   } // end of createDSStage()

   
   private DSFlowVariable createStageVariable(StageVariable stageVarData, DSFlowVariable targetFlowVar) 
   {
      DSDerivation   dsDerivation;
      DSFlowVariable newStageVariable;
      String         stageVarName;
      String         expression;

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("stage var name = " + stageVarData.getName() + 
                           "trg flow var name = " + targetFlowVar.getName());
      }
      
      stageVarName = stageVarData.getName();
      expression   = stageVarData.getDerivation();

      // create a DSFlowVariable ...
      newStageVariable = createDSFlowVariable(stageVarData);
      newStageVariable.setName(stageVarName);

      // set the linking
      newStageVariable.getIsSourceOf_FlowVariable().add(newStageVariable);
      newStageVariable.getIsSourceOf_FlowVariable().add(targetFlowVar);
      newStageVariable.getHasSource_FlowVariable().add(newStageVariable);
      targetFlowVar.getHasSource_FlowVariable().add(newStageVariable);

      // create and set a DSDerivation instance ...
      dsDerivation = createDSDerivation("", expression, expression);
      dsDerivation.setStageVars(stageVarName);
      newStageVariable.getHasValue_Derivation().add(dsDerivation);

      // add a derivation instance on the target FlowVariable ...
      dsDerivation = createDSDerivation("", stageVarName, stageVarName);
      dsDerivation.setStageVars(stageVarName);
      targetFlowVar.getHasValue_Derivation().add(dsDerivation);
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("stage variable = " + newStageVariable);
      }
      
      return(newStageVariable);
   } // end of createStageVariable()


   /**
    * This method determines the pin type dependent on the passed stage.
    * 
    * @param stageData  JobObject instance
    * @param pinType    input pin type or output pin type
    * 
    * @return Pin type (String)
    */
   private String determinePinType(JobObject stageData, int pinType)
   {
      String retPinType;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("stage = " + stageData.getName()); 
      }
      
      if (stageData.isTransformerStage()) 
      {
         if (pinType == OBJECT_TYPE_INPUT)
         {
            retPinType = PIN_TYPE_TRANSFORMER_INPUT;
         }
         else
         {
            retPinType = PIN_TYPE_TRANSFORMER_OUTPUT;
         }
      }
      else
         if (stageData.isCustomStage())
         {
            if (pinType == OBJECT_TYPE_INPUT)
            {
               retPinType = PIN_TYPE_CUSTOM_INPUT;
            }
            else
            {
               retPinType = PIN_TYPE_CUSTOM_OUTPUT;
            }
         }
      else
         if (stageData.isContainer() || stageData.isContainerStage())
         {
            if (pinType == OBJECT_TYPE_INPUT)
            {
               retPinType = PIN_TYPE_STD_PIN;
            }
            else
            {
               retPinType = PIN_TYPE_STD_OUTPUT;
            }
         }
      else
         if (pinType == OBJECT_TYPE_INPUT)
         {
            retPinType = PIN_TYPE_STD_INPUT;
         }
         else
         {
            retPinType = PIN_TYPE_STD_OUTPUT;
         }
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("Pin type = " + retPinType);
      }
      
      return(retPinType);
   } // end of determinePinType()


   private String getContainerId(MainObject dsContainerObj, String idTemplate)
           throws DSAccessException
   {
      String idCounter;
      String retContainerId;
      
      // get the id of the specified container ...
      idCounter = (String) _ContainerIdMap.get(dsContainerObj);
      
      if (idCounter == null)
      {
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.trace(TraceLogger.LEVEL_FINE, 
                              "Internal error: Cannot find container id for '" + dsContainerObj.getName() +"'.");
         }

         throw new DSAccessException("120700E", new String[] { dsContainerObj.getName() }); 
      } // end of if (idCounter == null)

      if (idTemplate == null)
      {
         retContainerId = idCounter;
      }
      else
      {
         retContainerId = MessageFormat.format(idTemplate, new Object[] { idCounter } );
      }
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, retContainerId);
      }

      return(retContainerId);
   } // end of getContainerId()
   
   
   private DSFlowVariable getCorrespondingTargetFlowVar(EList trgFlowVarsList, 
                                                        String srcFlowVarName, 
                                                        boolean lookUpOnShortDesc,
                                                        boolean lookUpOnDerivations)
   {
      DSFlowVariable   trgFlowVar;
      DSFlowVariable   retFlowVar;
      DSDerivation     curDerivation;
      String           shortDescription;
      EList            derivationList;
      Iterator         trgListIter;
      int              listIdx;
      boolean          trgVarFound;

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("src flow var name = " + srcFlowVarName + 
                           " - luShortDesc = " + lookUpOnShortDesc + 
                           " - luDerivation = " + lookUpOnDerivations);
      }
      
      // search if the source flow variable is found in the current link
      trgVarFound = false;
      retFlowVar  = null;
      trgListIter = trgFlowVarsList.iterator();
      while(trgListIter.hasNext() && !trgVarFound)
      {
         trgFlowVar = (DSFlowVariable) trgListIter.next();

         // check if the lookup is to be continued on the 'Short Description'
         if (lookUpOnShortDesc)
         {
            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
                                 "Target FlowVariable '" + srcFlowVarName + 
                                 "' has not been found. Look if a Related Check Table column is set on the Short Description.");
            }
            
            // The FlowVariable name and the source name do not match
            // ==> check if there is a Shot Description having that source column name
            shortDescription = trgFlowVar.getShortDescription();
            
            if (shortDescription != null && shortDescription.equals(srcFlowVarName))
            {
               retFlowVar  = trgFlowVar;
               trgVarFound = true;
               
               if (TraceLogger.isTraceEnabled())
               {
                  TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Related Check Table Column found.");
               }
            } // end of if (shortDescription != null && shortDescription.equals(srcFlowVarName))
         } // end of if (lookUpOnShortDesc)
         
         // check if the lookup is to be continued on the derivation
         if (!trgVarFound && lookUpOnDerivations)
         {
            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
                                 "Target FlowVariable '" + srcFlowVarName + 
                                 "' has not been found. Look if there is a Derivation having the taht name.");
            }
            
            // The FlowVariable name and the source name do not match
            // ==> check if there is a derivation having that source column name
            derivationList = trgFlowVar.getHasValue_Derivation();
            listIdx        = 0;
            while(listIdx < derivationList.size() && !trgVarFound)
            {
               curDerivation = (DSDerivation) derivationList.get(listIdx);
               if (curDerivation.getParsedExpression().equals(srcFlowVarName))
               {
                  retFlowVar  = trgFlowVar;
                  trgVarFound = true;
                  
                  // remove the current derivation since it's not used anymore
                  derivationList.remove(listIdx);
                  
                  if (TraceLogger.isTraceEnabled())
                  {
                     TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Derivation found. Derivation instance is removed.");
                  }
               }
               else
               {
                  listIdx ++;
               }
            } // end of while(listIdx < derivationList.size() && !trgVarFound)
         } // end of if (!trgVarFound && lookUpOnDerivations)

         if (!trgVarFound)
         {
            // check if the target flowVar's name matches with source column name
            if (trgFlowVar.getName().equals(srcFlowVarName))
            {
               retFlowVar  = trgFlowVar;
               trgVarFound = true;
            } // end of if (trgFlowVar.getName().equals(srcFlowVarName))
         } // end of if (!trgVarFound)
      } // end of while(trgListIter.hasNext() & &!trgVarFound)
/*      
      // search if the source flow variable is found in the current link
      trgVarFound = false;
      retFlowVar  = null;
      trgListIter = trgFlowVarsList.iterator();
      while(trgListIter.hasNext() && !trgVarFound)
      {
         trgFlowVar = (DSFlowVariable) trgListIter.next();

         if (trgFlowVar.getName().equals(srcFlowVarName))
         {
            retFlowVar  = trgFlowVar;
            trgVarFound = true;
         }
         else
         {
            // check if the lookup is to be continued on the 'Short Description'
            if (lookUpOnShortDesc)
            {
               if (TraceLogger.isTraceEnabled())
               {
                  TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
                                    "Target FlowVariable '" + srcFlowVarName + 
                                    "' has not been found. Look if a Related Check Table column is set on the Short Description.");
               }
               
               // The FlowVariable name and the source name do not match
               // ==> check if there is a Shot Description having that source column name
               shortDescription = trgFlowVar.getShortDescription();
               
               if (shortDescription != null && shortDescription.equals(srcFlowVarName))
               {
                  retFlowVar  = trgFlowVar;
                  trgVarFound = true;
                  
                  if (TraceLogger.isTraceEnabled())
                  {
                     TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Related Check Table Column found.");
                  }
               } // end of if (shortDescription != null && shortDescription.equals(srcFlowVarName))
            } // end of if (lookUpOnShortDesc)
            
            // check if the lookup is to be continued on the derivation
            if (!trgVarFound && lookUpOnDerivations)
            {
               if (TraceLogger.isTraceEnabled())
               {
                  TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
                                    "Target FlowVariable '" + srcFlowVarName + 
                                    "' has not been found. Look if there is a Derivation having the taht name.");
               }
               
               // The FlowVariable name and the source name do not match
               // ==> check if there is a derivation having that source column name
               derivationList = trgFlowVar.getHasValue_Derivation();
               listIdx        = 0;
               while(listIdx < derivationList.size() && !trgVarFound)
               {
                  curDerivation = (DSDerivation) derivationList.get(listIdx);
                  if (curDerivation.getParsedExpression().equals(srcFlowVarName))
                  {
                     retFlowVar  = trgFlowVar;
                     trgVarFound = true;
                     
                     // remove the current derivation since it's not used anymore
                     derivationList.remove(listIdx);
                     
                     if (TraceLogger.isTraceEnabled())
                     {
                        TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Derivation found. Derivation instance is removed.");
                     }
                  }
                  else
                  {
                     listIdx ++;
                  }
               } // end of while(listIdx < derivationList.size() && !trgVarFound)
            } // end of if (!trgVarFound && lookUpOnDerivations)
         } // end of (else) if (trgFlowVar.getName().equals(srcFlowVarName))
      } // end of while(trgListIter.hasNext() & &!trgVarFound)
*/
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("target flow var found = " + trgVarFound);
      }
      
      return(retFlowVar);
   } // end of getCorrespondingTargetFlowVar()

   
   private EList getFlowVarsFromLinkParentWithSameLinkName(DSLink dsLink)
   {
      DSLocalContainerDef containerDef;
      DSLocalContainerDef parentContainerDef;
      DSJobDef            parentJobDef;
      Object              curJobObject;
      String              linkName;
      EList               retFlowVarList;
      EList               jobObjectList;
      Iterator            jobObjIter;
      boolean             hasFound;
      
      retFlowVarList = dsLink.getContains_FlowVariable();
      linkName       = dsLink.getName();
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "link name = " + linkName);
      }
      
      // check if there the link's parent is container
      containerDef   = (DSLocalContainerDef) dsLink.getOf_ContainerDef();
      if (containerDef != null)
      {
         // get the container's parent ...
         parentContainerDef = (DSLocalContainerDef) containerDef.getOf_ContainerDef();
         
         if (parentContainerDef == null)
         {
            parentJobDef  = (DSJobDef) containerDef.getOf_JobDef();
            jobObjectList = parentJobDef.getContains_JobObject();
         }
         else
         {
            jobObjectList = parentContainerDef.getContains_JobObject();
         }
         
         // search for a link with same name
         jobObjIter = jobObjectList.iterator();
         hasFound   = false;
         while(jobObjIter.hasNext() && !hasFound)
         {
            curJobObject = jobObjIter.next();
            
            // is it a link and has it the needed name ????
            if (curJobObject instanceof DSLink                      && 
                ((DSLink) curJobObject).getName().equals(linkName))
            {
               retFlowVarList = ((DSLink) curJobObject).getContains_FlowVariable();
               hasFound       = true;
               
               if (TraceLogger.isTraceEnabled())
               {
                  TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Found: nFlowVars = " + retFlowVarList.size());
               }
            }
         } // end of while(jobObjIter.hasNext())
      } // end of if (containerDef != null)
      
      return(retFlowVarList);
   } // end of getFlowVarsFromLinkParentWithSameLinkName()

/*   
   public static TypeCodeEnum resolveType(String code) {
      
      if(code.equals("CHAR")) {
         return TypeCodeEnum.STRING_LITERAL;
      }
      if(code.equals("DECIMAL")) {
         return TypeCodeEnum.DECIMAL_LITERAL;
      }
      if(code.equals("DATE")) {
         return TypeCodeEnum.DATE_LITERAL;
      }
      if(code.equals("DOUBLE")) {
         return TypeCodeEnum.DECIMAL_LITERAL;
      }
      if(code.equals("FLOAT")) {
         return TypeCodeEnum.DFLOAT_LITERAL;
      }
      if(code.equals("INTEGER")) {
         return TypeCodeEnum.INT32_LITERAL;
      }
      if(code.equals("VARCHAR")) {
         return TypeCodeEnum.STRING_LITERAL;
      }
      if(code.equals("NUMERIC")) {
         return TypeCodeEnum.DFLOAT_LITERAL;
      }
      if(code.equals("SMALLINT")) {
         return TypeCodeEnum.INT8_LITERAL;
      }
      if(code.equals("BIGINT")) {
         return TypeCodeEnum.INT64_LITERAL;
      }
      if(code.equals("TIME")) {
         return TypeCodeEnum.TIME_LITERAL;
      }
      if(code.equals("TIMESTAMP")) {
         return TypeCodeEnum.DATETIME_LITERAL;
      }
      if(code.equals("VARGRAPHIC")) {
            return TypeCodeEnum.STRING_LITERAL;
      }
      
      return TypeCodeEnum.UNKNOWN_LITERAL;
   }
	   
	   
	public static ODBCTypeEnum resolveODBCType(String code) {
	      
      if(code.equals("CHAR")) {
         return ODBCTypeEnum.CHAR_LITERAL;
      }
      if(code.equals("DECIMAL")) {
         return ODBCTypeEnum.DECIMAL_LITERAL;
      }
      if(code.equals("DATE")) {
         return ODBCTypeEnum.DATE_LITERAL;
      }
      if(code.equals("DOUBLE")) {
         return ODBCTypeEnum.DOUBLE_LITERAL;
      }
      if(code.equals("FLOAT")) {
         return ODBCTypeEnum.FLOAT_LITERAL;
      }
      if(code.equals("INTEGER")) {
         return ODBCTypeEnum.INTEGER_LITERAL;
      }
      if(code.equals("VARCHAR")) {
         return ODBCTypeEnum.VARCHAR_LITERAL;
      }
      if(code.equals("NUMERIC")) {
         return ODBCTypeEnum.NUMERIC_LITERAL;
      }
      if(code.equals("SMALLINT")) {
         return ODBCTypeEnum.SMALLINT_LITERAL;
      }
      if(code.equals("BIGINT")) {
         return ODBCTypeEnum.BIGINT_LITERAL;
      }
      if(code.equals("TIME")) {
         return ODBCTypeEnum.TIME_LITERAL;
      }
      if(code.equals("TIMESTAMP")) {
         return ODBCTypeEnum.TIMESTAMP_LITERAL;
      }
      if(code.equals("VARGRAPHIC")) {
            return ODBCTypeEnum.VARCHAR_LITERAL;
     }
      
      return ODBCTypeEnum.UNKNOWN_LITERAL;
	}  
*/
   private FlowVariableType resolveDBMType(FlowVarData flowVarData) {
      FlowVariableType            retFlowVarType;
      DSTypeDBMMapping.DSDataType dsType;
      String                      curDBMType;
      Map<String,DSDataType>      typeMapping;
      boolean                     isSAPCol;
      
      typeMapping = _DSTypeMapping;
      
      if (flowVarData instanceof ColumnData && 
          ((ColumnData) flowVarData).isSAPColumn())
      {
         isSAPCol = true;
         
         if (typeMapping == null) {
            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.trace(TraceLogger.LEVEL_FINE, "DS data type mapping has not been set. Use SAP mapping as default mapping."); 
            }
            
            typeMapping = DSTypeDBMMapping.getSAPMapping();
         }
         
      }
      else
      {
         isSAPCol = false;
         
         if (typeMapping == null) {
            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.trace(TraceLogger.LEVEL_FINE, "DS data type mapping has not been set. Use DB2 mapping as default mapping."); 
            }
            
            typeMapping = DSTypeDBMMapping.getDBMMapping(DataBaseType.DB2);
         }
         
      }
      curDBMType = flowVarData.getType();
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
                           "ENTRY  Type = " + curDBMType + " - SAP Column = " + isSAPCol);
      }

      // search for the DS Data type ...
      dsType = (DSTypeDBMMapping.DSDataType) typeMapping.get(curDBMType);
      if (dsType == null)
      {
         // not found ==> set to UNKNOWN if type could not be determined
         retFlowVarType = new FlowVariableType(ODBCTypeEnum.UNKNOWN_LITERAL, TypeCodeEnum.UNKNOWN_LITERAL);
      }
      else
      {
         retFlowVarType = new FlowVariableType(ODBCTypeEnum.get(dsType.getTypeName().toUpperCase()), 
                                               TypeCodeEnum.get(dsType.getTypeCode().toUpperCase()));
      }
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
                           "RETURN TypeEnum = " + retFlowVarType.odbcType.getName() + 
                           " - Type Code = " + retFlowVarType.getTypeCode().getName());
      }
      
      return(retFlowVarType);
   } // end of resolveType()
   
   
//   private FlowVariableType resolveType(FlowVarData flowVarData) 
//   {
//      FlowVariableType retFlowVarType;
//      String           curType;
//      boolean          isSAPCol;
//      
//      if (flowVarData instanceof ColumnData &&
//          ((ColumnData) flowVarData).isSAPColumn())
//      {
//         isSAPCol = true;
//      }
//      else
//      {
//         isSAPCol = false;
//      }
//      curType = flowVarData.getType();
//      
//      if (TraceLogger.isTraceEnabled())
//      {
//         TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
//                           "ENTRY  Type = " + curType + " - SAP Column = " + isSAPCol);
//      }
//
//      retFlowVarType = null;
//      if (isSAPCol)
//      {
//         // ------------------
//         // ==> SAP data types
//         // ------------------
//         if (curType.equals(SAP_DATA_TYPE_CLIENT)) 
//         {
//            retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//         }
//         else
//            if (curType.equals(SAP_DATA_TYPE_CHAR)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
////               retFlowVarType = new FlowVariableType(ODBCTypeEnum.CHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_VARC)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_ACCP)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_CUKY)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_DATS)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_DEC)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.DECIMAL_LITERAL, TypeCodeEnum.DECIMAL_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_FLTP)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.FLOAT_LITERAL, TypeCodeEnum.SFLOAT_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_INT1)) 
//            {
////               retFlowVarType = new FlowVariableType(ODBCTypeEnum.SMALLINT_LITERAL, TypeCodeEnum.INT16_LITERAL);
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.INTEGER_LITERAL, TypeCodeEnum.INT32_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_INT2)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.INTEGER_LITERAL, TypeCodeEnum.INT32_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_INT4)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.INTEGER_LITERAL, TypeCodeEnum.INT32_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_LANG)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_NUMC)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else               
//            if (curType.equals(SAP_DATA_TYPE_PREC)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.INTEGER_LITERAL, TypeCodeEnum.INT32_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_RAW)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.BINARY_LITERAL, TypeCodeEnum.BINARY_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_RSTR)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else            
//            if (curType.equals(SAP_DATA_TYPE_SSTR)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_STRG)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_TIMS)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_UNIT)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if (curType.equals(SAP_DATA_TYPE_LRAW)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.BINARY_LITERAL, TypeCodeEnum.BINARY_LITERAL);
//            }
//      }
//      else
//      {
//         // -----------------------------
//         // ==> Physical Model data types
//         // -----------------------------
//         if (curType.equals(DBM_TYPE_BIGINT)) 
//         {
//            retFlowVarType = new FlowVariableType(ODBCTypeEnum.BIGINT_LITERAL, TypeCodeEnum.INT64_LITERAL);
//         }
//         else
//            if (curType.equals(DBM_TYPE_BLOB)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.LONGVARBINARY_LITERAL, TypeCodeEnum.BINARY_LITERAL);
//            }
//         else
//            if (curType.equals(DBM_TYPE_CHAR)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.CHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if (curType.equals(DBM_TYPE_CHAR_FOR_BIT_DATA)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.BINARY_LITERAL, TypeCodeEnum.BINARY_LITERAL);
//            }
//         else
//            if (curType.equals(DBM_TYPE_CLOB)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.LONGVARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if (curType.equals(DBM_TYPE_DATALINK)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if (curType.equals(DBM_TYPE_DATE)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.DATE_LITERAL, TypeCodeEnum.DATE_LITERAL);
//            }
//         else
//            if (curType.equals(DBM_TYPE_DBCLOB)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.WLONGVARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if (curType.equals(DBM_TYPE_DECFLOAT)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.FLOAT_LITERAL, TypeCodeEnum.SFLOAT_LITERAL);
//            }
//         else
//            if (curType.equals(DBM_TYPE_DECIMAL)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.DECIMAL_LITERAL, TypeCodeEnum.DECIMAL_LITERAL);
//            }
//         else
//            if (curType.equals(DBM_TYPE_DOUBLE)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.DOUBLE_LITERAL, TypeCodeEnum.DFLOAT_LITERAL);
//            }
//         else
//            if(curType.equals(DBM_TYPE_FLOAT)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.FLOAT_LITERAL, TypeCodeEnum.SFLOAT_LITERAL);
//            }
//         else
//            if(curType.equals(DBM_TYPE_GRAPHIC)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.WCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if (curType.equals(DBM_TYPE_INT))   // oracle: is also mapped to NUMBER
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.DOUBLE_LITERAL, TypeCodeEnum.DFLOAT_LITERAL);
//            }
//         else
//            if (curType.equals(DBM_TYPE_INTEGER)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.INTEGER_LITERAL, TypeCodeEnum.INT32_LITERAL);
//            }
//         else
//            if(curType.equals(DBM_TYPE_NCHAR))  // oracle 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.WCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if (curType.equals(DBM_TYPE_NUMBER))  // oracle 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.DOUBLE_LITERAL, TypeCodeEnum.DFLOAT_LITERAL);
//            }
//         else
//            if(curType.equals(DBM_TYPE_LONG_VARCHAR)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if(curType.equals(DBM_TYPE_LONG_VARCHAR_FOR_BIT_DATA)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARBINARY_LITERAL, TypeCodeEnum.BINARY_LITERAL);
//            }
//         else
//            if(curType.equals(DBM_TYPE_LONG_VARGRAPHIC)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.WVARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if(curType.equals(DBM_TYPE_NUMERIC))  // oracle 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.NUMERIC_LITERAL, TypeCodeEnum.DECIMAL_LITERAL);
//            }
//         else
//            if (curType.equals(DBM_TYPE_RAW))  // oracle
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.BINARY_LITERAL, TypeCodeEnum.BINARY_LITERAL);
//            }
//         else
//            if(curType.equals(DBM_TYPE_REAL)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.REAL_LITERAL, TypeCodeEnum.SFLOAT_LITERAL);
//            }
//         else
//            if(curType.equals(DBM_TYPE_SMALLINT)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.SMALLINT_LITERAL, TypeCodeEnum.INT16_LITERAL);
//            }
//         else
//            if(curType.equals(DBM_TYPE_TIME)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.TIME_LITERAL, TypeCodeEnum.TIME_LITERAL);
//            }
//         else
//            if(curType.equals(DBM_TYPE_TIMESTAMP)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.TIMESTAMP_LITERAL, TypeCodeEnum.DATETIME_LITERAL);
//            }
//         else
//            if(curType.equals(DBM_TYPE_VARCHAR)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if(curType.equals(DBM_TYPE_VARCHAR_2) || curType.equals(DBM_TYPE_NVARCHAR_2)) // oracle 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
////               retFlowVarType = new FlowVariableType(ODBCTypeEnum.WVARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if(curType.equals(DBM_TYPE_VARCHAR_FOR_BIT_DATA)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARBINARY_LITERAL, TypeCodeEnum.BINARY_LITERAL);
//            }
//         else
//            if(curType.equals(DBM_TYPE_VARGRAPHIC)) 
//            {
////               retFlowVarType = new FlowVariableType(ODBCTypeEnum.WVARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.VARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//         else
//            if(curType.equals(DBM_TYPE_XML)) 
//            {
//               retFlowVarType = new FlowVariableType(ODBCTypeEnum.LONGVARCHAR_LITERAL, TypeCodeEnum.STRING_LITERAL);
//            }
//      } // end of (else) if (isSAPCol)
//      
//      // set to UNKNOWN if type could not be determined
//      if (retFlowVarType == null)
//      {
//         retFlowVarType = new FlowVariableType(ODBCTypeEnum.UNKNOWN_LITERAL, TypeCodeEnum.UNKNOWN_LITERAL);
//      }
//      
//      if (TraceLogger.isTraceEnabled())
//      {
//         TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
//                           "RETURN TypeEnum = " + retFlowVarType.odbcType.getName() + 
//                           " - Type Code = " + retFlowVarType.getTypeCode().getName());
//      }
//      
//      return(retFlowVarType);
//   }
//   
//
   public void setDSTypeMapping(DataBaseType databaseType)
   {
      _DSTypeMapping = DSTypeDBMMapping.getDBMMapping(databaseType);
   } // end of setDSTypeMapping()


   void setXMetaRepObjectBaseInfo(XMetaRepositoryObject xmetaRepositoryObj) 
   {
      DataStageAccessManager.getInstance().setXMetaRepObjectBaseInfo(xmetaRepositoryObj);
   } // end of createXMetaRepositoryObject()


   /**
    * This method updates the passed DSLink instance with Job dependent values, for 
    * example StageInfo, InputPin, OutputPin, etc ...
    * <p>
    * 
    * @param dsLink             DSJobDef instance to be updated
    * @param sourceStage        link's source stage (to be updated)
    * @param targetStage        link's target stage (to be updated)
    * @param srcColumnMapping   source column mapping
    * @param columnDerivations  column derivation map
    */
   public void updateLink(DSLink dsLink, JobObject sourceStage, JobObject targetStage, 
                          SourceColMapping columnMappings, Map columnDerivations, Map derivationExceptions) 
   {
      DSInputPin   inputPin;
      DSOutputPin  outputPin;
      String       tmpFomatBuf;
      String       curPinType;
      String       curPinString;
      String       curStageTypeName;
      Integer      curPinLinkCounter;
      boolean      linkIsConnected;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("name = " + dsLink.getName() 
                         + " - src obj = " + sourceStage 
                         + " - trg obj = " + targetStage);
      }
      
      curPinLinkCounter = _PinLinkCounter.getNextValue();
      
      // set the link's StageInfo ... (data comes from source link)
      // (NOT for Container Stages !!!!)
      if (!sourceStage.isContainerStage())
      {
         if (sourceStage.getStageTypeName() == null)
         {
            curStageTypeName = "";
         }
         else
         {
            curStageTypeName = sourceStage.getStageTypeName();
         } // end of (else) if (actSourceStage.getStageType() == null)
         
         tmpFomatBuf = MessageFormat.format(LINK_STAGE_INFO_TEMPLATE, new Object[] { sourceStage.getName(), 
                                                                                     curStageTypeName, 
                                                                                     sourceStage.getStageTypeClassName() });
         dsLink.setStageInfo(tmpFomatBuf);
      } // end of if (!sourceStage.isContainerStage())

      // create the OutputPin for the link
      // ---------------------------------
      outputPin = _DSFactory.createDSOutputPin();
      setXMetaRepObjectBaseInfo(outputPin);
      tmpFomatBuf = MessageFormat.format(PIN_ID_TEMPLATE, new Object[] { sourceStage.getInternalID(), 
                                                                         curPinLinkCounter.toString() });
      outputPin.setInternalID(tmpFomatBuf);
      outputPin.setHas_DSMetaBag(createDSMetaBagForOutputPin(sourceStage.getMetaBagList(), outputPin));
      outputPin.setLeftTextPos(new Integer(222));
      outputPin.setTopTextPos(new Integer(211));
      
      // determine and set the PIN TYPE
      curPinType = determinePinType(sourceStage, OBJECT_TYPE_OUTPUT);
      outputPin.setPinType(curPinType);
      
      // associate the link with the source stage ...
      outputPin.setIsSourceOf_Link(dsLink);
      outputPin.setOf_JobComponent(sourceStage.getJobComponent());
      sourceStage.getHas_OutputPin().add(outputPin);
      
      // update source stage's OutputPin
      curPinString = sourceStage.getOutputPins();
      if (curPinString == null)
      {
         curPinString = outputPin.getInternalID(); 
      }
      else
      {
         curPinString = curPinString + DS_VIEW_VALUE_SEPARATOR + outputPin.getInternalID(); 
      }
      sourceStage.setOutputPins(curPinString);
            
      // create the InputPin for the link
      // --------------------------------
      inputPin = _DSFactory.createDSInputPin();
      setXMetaRepObjectBaseInfo(inputPin);
      tmpFomatBuf = MessageFormat.format(PIN_ID_TEMPLATE, new Object[] { targetStage.getInternalID(), 
                                                                         curPinLinkCounter.toString() });
      inputPin.setInternalID(tmpFomatBuf);
      inputPin.setHas_DSMetaBag(createDSMetaBagForInputPin(targetStage.getMetaBagList(), inputPin));
      
      // determine and set link type
      switch(dsLink.getLinkType().getValue())
      {
         case LinkTypeEnum.CONTROL:
              inputPin.setLinkType(new Integer(0)); // ??????????????????????
              break;
         
         case LinkTypeEnum.REFERENCE:
              inputPin.setLinkType(new Integer(2));
              break;
              
         case LinkTypeEnum.REJECT:
              inputPin.setLinkType(new Integer(3));
              break;
            
         case LinkTypeEnum.PRIMARY:
         default:
              inputPin.setLinkType(new Integer(1));
      } // end of switch(dsLink.getLinkType().getValue())
      
      // determine and set the PIN TYPE
      curPinType = determinePinType(targetStage, OBJECT_TYPE_INPUT);
      inputPin.setPinType(curPinType);
      
      // associate the link with the target stage ...
      inputPin.setIsTargetOf_Link(dsLink);
      inputPin.setOf_JobComponent(targetStage.getJobComponent());
      targetStage.getHas_InputPin().add(inputPin);
      
      // update target stage's InputPin
      curPinString = targetStage.getInputPins();
      if (curPinString == null)
      {
         curPinString = inputPin.getInternalID(); 
      }
      else
      {
         curPinString = curPinString + DS_VIEW_VALUE_SEPARATOR + inputPin.getInternalID(); 
      }
      targetStage.setInputPins(curPinString);

      // set Pin Partner (output)
      tmpFomatBuf = MessageFormat.format(PIN_PARTNER_TEMPLATE, new Object[] { targetStage.getInternalID(), 
                                                                              inputPin.getInternalID() });
      outputPin.setPartner(tmpFomatBuf);
      
      // set Pin Partner (input)
      tmpFomatBuf = MessageFormat.format(PIN_PARTNER_TEMPLATE, new Object[] { sourceStage.getInternalID(), 
                                                                              outputPin.getInternalID() });
      inputPin.setPartner(tmpFomatBuf);
      
      // adapt link's flow variables for specified stage types
      linkIsConnected = false;
      switch(sourceStage.getType().getValue())
      {
         case DSStageTypeEnum.CTRANSFORMER_STAGE:
         case DSStageTypeEnum.PX_LOOKUP_STAGE:
         case DSStageTypeEnum.PX_REMOVE_DUPLICATES_STAGE:
              assignTargetColsToSourceCols((StageData) sourceStage, dsLink.getContains_FlowVariable(), 
                                           columnMappings, columnDerivations, derivationExceptions);
              linkIsConnected = true;
              break;

         default:
              ;
      } // end of switch(sourceStage.getType().getValue())
      
      // adapt link's flow variables for specified target stage types
      switch(targetStage.getType().getValue())
      {
         case DSStageTypeEnum.PX_LOOKUP_STAGE:
              if (!linkIsConnected)
              {
                 assignLookupStageColumns((StageData) targetStage, dsLink, columnMappings);
                 linkIsConnected = true;
              } // end of if (!linkIsConnected)
              break;
          
         default:
              ;
      } // end of switch(targetStage.getType().getValue())
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of updateLink()


   /**
    * This private method creates a JobItem in the Universe DB.
    * 
    * @param folderName  folder name
    * @param dsJobDef    job definition
    * 
    * @throws JobLockedException
    */
/*   
   void uvDBCreateJob(DSJobDef dsJobDef, String folderName) throws JobLockedException
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("DSJobDef = " + dsJobDef.getName());
      }
      
      if (_UniverseDBAccessor == null)
      {
         uvDBConnect();
      }
      
      _UniverseDBAccessor.createJob(folderName, dsJobDef.getName());
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of uvDBCreateJob()
*/
   
   /**
    * This method connects to the Universe DB.
    *  
    * @throws DSAccessException if an error occurred
    */
/*   
   void uvDBConnect()
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }
      
      if (_UniverseDBAccessor != null)
      {
         _UniverseDBAccessor.disconnect();
      }
      
      //3. drop and create job from universe DB, make sure drop of job and drop of UV instance is within same transaction
      try
      {
         DSProject curProject = _srvcToken.getDSProject();
         
         _UniverseDBAccessor = new UVAccessor(curProject);
         _UniverseDBAccessor.connect();
      }
      catch(Exception excpt)
      {
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.trace(TraceLogger.LEVEL_FINE, "UvDB Connection error: " + excpt);
            TraceLogger.traceException(excpt);
         }
         _UniverseDBAccessor = null;
      }

//      //retrieve the targeted DS job folder via its rid
//      this.serializerContext.setDsJobFolder(queryFactory.getDSFolderByRid(dsFolderRid));
//      if(dsTableDefFolderRid != null){
//         this.serializerContext.setDsTableDefFolder(queryFactory.getDSFolderByRid(dsTableDefFolderRid));
//      }
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of uvDBConnect()
   
   
   void uvDBDeleteJob(String jobName) throws JobLockedException
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("Job = " + jobName);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "UVAccessor = " + _UniverseDBAccessor);
      }

      if (_UniverseDBAccessor != null)
      {
         _UniverseDBAccessor.deleteJob(jobName);
      }
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of uvDBDeleteJob()
*/   
   
   /**
    * This methods disconnects from the Universe database.
    */
/*   
   void uvDBDisconnect()
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("UVAccessor = " + _UniverseDBAccessor);
      }
      
      if (_UniverseDBAccessor != null)
      {
         _UniverseDBAccessor.disconnect();
      }
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of uvDBDisconnect()
*/   
   
} // end of class DataStageObjectFactory
