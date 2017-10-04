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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.DBSupport.DataBaseType;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequest;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeMIHLoad;
import com.ibm.is.sappack.gen.common.request.SupportedColumnTypesMap;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.util.ColumnData;
import com.ibm.is.sappack.gen.server.util.ModelInfoBlock;
import com.ibm.is.sappack.gen.server.util.TableData;


public class MIHLoadJob extends MovementJob {
	// -------------------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------------------
   protected static final String PRIMARY_KEY_NAME_TEMPLATE      = "PK_ID_{0}";
	private   static final String FOREIGN_KEY_NAME               = "PARENT";
	protected static final String KEY_COLUMN_TYPE                = "BIGINT";
	protected static final int    KEY_COLUMN_LENGTH              = 8;
	protected static final String INSTANCE_ID_KEY                = "#INST_ID#";
	private   static final String PRIMARY_KEY_LINK_NAME_TEMPLATE = "Right({0}.ADM_DOCNUM,11) : Trim({0}.ADM_SEGNUM, '0') : " + INSTANCE_ID_KEY;
	private   static final String FOREIGN_KEY_LINK_NAME_TEMPLATE = "Right({0}.ADM_DOCNUM,11) : Trim({0}.ADM_PSGNUM, '0') : " + INSTANCE_ID_KEY;
   protected static final String COLUMN_DERIVATION_TYPE_CHAR    = "CHAR";
   protected static final String COLUMN_DERIVATION_TYPE_VARCHAR = "VARCHAR";
   protected static final String COLUMN_DERIVATION_TRIM_FN      = "trim({0})";
	
	// -------------------------------------------------------------------------------------
	// Member Variables
	// -------------------------------------------------------------------------------------
	
	
   static String copyright() {
      return com.ibm.is.sappack.gen.server.jobgenerator.Copyright.IBM_COPYRIGHT_SHORT;
   }


	public MIHLoadJob(ServiceToken parServiceToken, RequestJobTypeMIHLoad parJobType, 
	                  JobGeneratorRequest parJobReqInfo, Map<String, ModelInfoBlock> physModelID2TableMap) throws BaseException {
		super(parServiceToken, parJobType, parJobReqInfo, physModelID2TableMap);
	} // end of MIHLoadJob()


	public List<String> create() throws BaseException {
		RequestJobTypeMIHLoad     jtMIHLoad;
		TableData                 srcTableArr[];
		TableData                 trgTableArr[];
		Map<Integer, TableData[]> trgTableSetMap;
		List<String>              retJobsCreated;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		jtMIHLoad = (RequestJobTypeMIHLoad) getJobType();
		retJobsCreated = new ArrayList<String>();

		// 1. get the table array from the source model ...
		srcTableArr = loadRequiredTables(jtMIHLoad.getSrcPhysicalModelId());

		// ---------------------------
		// ==> process existing tables
		// ---------------------------
		if (srcTableArr.length > 0) {
			// 2. ... create the (modified Physical) target model ...
			trgTableSetMap = new HashMap<Integer, TableData[]>();
			trgTableArr    = createTargetTables(srcTableArr, jtMIHLoad.getInstanceIdentifier());
			trgTableSetMap.put(jtMIHLoad.getSupportedTableTypes().getFirstType(), trgTableArr);

			// 3. ... and put it into the (Physical) model map
			_PhysModelID2TableMap.put(jtMIHLoad.getTrgPhysicalModelId(),
			                          new ModelInfoBlock(trgTableSetMap, DataBaseType.DB2));

			// 4. and call the base class to complete the job
			retJobsCreated = super.create();
		} // end of if (srcTableArr.length > 0)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("Number of created jobs: " + retJobsCreated.size());
		}

		return (retJobsCreated);
	} // end of createJob()


	public static TableData[] createTargetTables(TableData parSrcTableArr[], int parInstanceId) {
		ColumnData newCol;
		TableData  curSrcTable;
		TableData  curTrgTable;
		TableData  retTrgTableArr[];
		String     colName;
		String     formattedInstanceId;
		String     keyLinkNameTemplate;
		int        srcArrIdx;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		// create a copy of the source table array ...
		retTrgTableArr = new TableData[parSrcTableArr.length];

		// get the formatted MIH Instance Id key ...
		formattedInstanceId = getFormattedInstanceId(parInstanceId);

		// ... and add new key columns to the existing tables
		for (srcArrIdx = 0; srcArrIdx < parSrcTableArr.length; srcArrIdx++) {
			curSrcTable = parSrcTableArr[srcArrIdx];
			curTrgTable = curSrcTable.getCopy();

			// for non-root segments ...
			if (!curTrgTable.isRootSegment()) {
				keyLinkNameTemplate = StringUtils.replaceString(FOREIGN_KEY_LINK_NAME_TEMPLATE, 
				                                                INSTANCE_ID_KEY, formattedInstanceId);

				// ==> create a foreign key
				newCol = curTrgTable.addColumn(FOREIGN_KEY_NAME, KEY_COLUMN_LENGTH, false, 
				                               FOREIGN_KEY_NAME, TableData.COLUMN_POSITION_BEGIN);
				newCol.setTransformerSrcMapping(keyLinkNameTemplate);

				// set some additional required column properties
				newCol.setType(KEY_COLUMN_TYPE);
				newCol.setDataObjectSourceType(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_LOAD_JOB_FIELD);
				newCol.setIsUnicode(curTrgTable.isUnicodeSystem());
				newCol.setIsNullable(false);

				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Foreign Key Col = " + newCol.getName()
					                                          + " - trf mapping = " + keyLinkNameTemplate);
				}
			} // end of if (!curTrgTable.isRootSegment())

			// generate the (primary key) column name
			keyLinkNameTemplate = StringUtils.replaceString(PRIMARY_KEY_LINK_NAME_TEMPLATE, 
			                                                INSTANCE_ID_KEY, formattedInstanceId);

			colName = MessageFormat.format(PRIMARY_KEY_NAME_TEMPLATE, new Object[] { curTrgTable.getName() });
			newCol  = curTrgTable.addColumn(colName, KEY_COLUMN_LENGTH, true, colName, 
			                                TableData.COLUMN_POSITION_BEGIN);
			newCol.setTransformerSrcMapping(keyLinkNameTemplate);
			newCol.setIsKeyColumn(true);

			// set some additional required column properties
			newCol.setType(KEY_COLUMN_TYPE);
			newCol.setDataObjectSourceType(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_LOAD_JOB_FIELD);
			newCol.setIsUnicode(curTrgTable.isUnicodeSystem());

			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Primary Key Col = " + colName + " - transformer mapping = "
				                                          + keyLinkNameTemplate);
			}

			retTrgTableArr[srcArrIdx] = curTrgTable;
		} // end of for(srcArrIdx = 0; srcArrIdx < parSrcTableArr.length; srcArrIdx ++)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}

		return (retTrgTableArr);
	} // end of createTargetTables()

	
	protected static String getFormattedInstanceId(int parInstanceId) {
		String retFormattedId;

		// we need a 2-digit string
		if (parInstanceId > 9) {
			retFormattedId = String.valueOf(parInstanceId);
		}
		else {
			retFormattedId = "0" + String.valueOf(parInstanceId);
		}

		return (retFormattedId);
	} // end of getFormattedInstanceId()

	
	protected void validate() throws JobGeneratorException {
      ModelInfoBlock        modelInfoBlk;
		RequestJobTypeMIHLoad jtMIHLoad;
		Map                   modelMap;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		jtMIHLoad = (RequestJobTypeMIHLoad) getJobType();

		// check if there are at least two models in the map ...
		modelInfoBlk = (ModelInfoBlock) _PhysModelID2TableMap.get(jtMIHLoad.getTrgPhysicalModelId());
      modelMap     = modelInfoBlk.getTableMap();
		if (modelMap == null) {
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, "MIH Jobs require one source model to be defined.");
			}
			throw new JobGeneratorException("126700E", Constants.NO_PARAMS);
		} // end of if (modelMap == null)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of validate()

} // end of class MIHLoadJob
