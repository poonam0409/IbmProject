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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.DBSupport;
import com.ibm.is.sappack.gen.common.DBSupport.DataBaseType;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.ODBCStageData;
import com.ibm.is.sappack.gen.common.PersistenceData;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequest;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeTranscoding;
import com.ibm.is.sappack.gen.common.request.SupportedTableTypesMap;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.ServerMessageCatalog;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.util.ColumnData;
import com.ibm.is.sappack.gen.server.util.ModelInfoBlock;
import com.ibm.is.sappack.gen.server.util.TableData;
import com.ibm.is.sappack.gen.common.Constants;


/**
 * TranscodingJob
 * 
 * A TranscodingJob extends a MovementJob. The only difference is the SQL select
 * statement within the source odbc stage
 * 
 * @author gaege
 * 
 */
public class TranscodingJob extends MovementJob {
	/* target system legacy ID */
	private String targetLegacyID = "";
	
	/* database vendor Type */
	private DataBaseType dbType = DataBaseType.Unknown;
	
	/* The supported transcoding types */
	private enum TranscodingType{NoTranscoding, CheckTableTranscoding, DomainTranscoding }
	
	/* List<String> of generation warnings that occurred while generating the transcoding jobs */
	private List<String> warnings = null;


	static String copyright() {
		return com.ibm.is.sappack.gen.server.jobgenerator.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	
	/**
	 * TranscodingJob
	 * 
	 * @param parSrvcToken
	 * @param parJobType
	 * @param parJobReqInfo
	 * @param physModelID2TableMapMap
	 * @throws BaseException
	 */
	public TranscodingJob(ServiceToken parSrvcToken, RequestJobTypeTranscoding parJobType,
	                      JobGeneratorRequest parJobReqInfo, Map<String, ModelInfoBlock> physModelID2TableMapMap)
	       throws BaseException {
		super(parSrvcToken, parJobType, parJobReqInfo, physModelID2TableMapMap);
	}
	
	
	@Override
	protected String getDescriptionPrefix() {
		return "Transcode tables: ";
	}


	/**
	 * create
	 * 
	 * create a Transcoding job
	 */
	public List<String> create() throws BaseException {
		
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}
		
		TableData                 srcTableArr[];
		RequestJobTypeTranscoding jtTranscoding = (RequestJobTypeTranscoding) getJobType();

		// reset generation warnings
		this.warnings = new ArrayList<String>();
		
		// detect database vendor ID
		this.dbType = ((ModelInfoBlock)_PhysModelID2TableMap.get(jtTranscoding.getSrcPhysicalModelId())).getDatabaseId();
		
		// the legacyID of the target SAP system
		this.targetLegacyID = jtTranscoding.getTargetLegacyID();
		
		// transcode reference check tables
		boolean transcodeReferenceCheckTables = jtTranscoding.isTranscodeReferenceFields();
		
		// transcode non-reference check tables
		boolean transcodeNonReferenceCheckTables = jtTranscoding.isTranscodeNonReferenceFields();
		
		// transcode domain values
//		boolean transcodeDomainValues = jtTranscoding.isTranscodeDomainValueFields();
		
		// mark unmapped values
//		boolean markUnmappedValues = jtTranscoding.isMarkUnmappedValues();
		
		
		
		// process the required source tables ...
		srcTableArr        = loadRequiredTables(jtTranscoding.getSrcPhysicalModelId());
		int numberOfTables = srcTableArr.length;

		/* trace DB vendor and transcoding job configuration details */
		if (TraceLogger.isTraceEnabled()) {
			
			String dbVendor = "unknown";
			switch(dbType) {
				case Netezza:
					  dbVendor = DBSupport.MODEL_VENDOR_NETEZZA;
					  break;
				case Oracle:
					  dbVendor = DBSupport.MODEL_VENDOR_ORACLE;
					  break;
				case DB2:
					  dbVendor = DBSupport.MODEL_VENDOR_DB2;
					  break;
			}
			TraceLogger.trace(TraceLogger.LEVEL_FINE, "Generating "+dbVendor+" specific transcoding job for "+numberOfTables+" tables for target SAP system "+targetLegacyID+"."); //$NON-NLS-1$ //$NON-NLS-2$
			
			if(transcodeNonReferenceCheckTables && transcodeReferenceCheckTables) {
				TraceLogger.trace(TraceLogger.LEVEL_FINER, "Transcoding both, reference and non-reference check tables"); //$NON-NLS-1$
			} else if(transcodeReferenceCheckTables) {
				TraceLogger.trace(TraceLogger.LEVEL_FINER, "Transcoding reference check tables only"); //$NON-NLS-1$
			} else if(transcodeNonReferenceCheckTables) {
				TraceLogger.trace(TraceLogger.LEVEL_FINER, "Transcoding non-reference check tables only"); //$NON-NLS-1$
			}
		}
		
		
		/* modify SQl select statements for each source table */
		for (int i = 0; i < numberOfTables; i++) {
			TableData srcTable = srcTableArr[i];
		
			String sqlStatement = this.generateTranscodingSqlXmlSnippet(srcTable, jtTranscoding);
			srcTableArr[i].setSQLStatement(sqlStatement);
		}
		
		/* call method in superclass to create the job */
		List<String> jobs = super.processSrcTables(srcTableArr);

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("n jobs created = " + jobs.size());
		}

		/* return List of transcodingJob names */
		return(jobs);
	}


	/**
	 * getGenerationWarnings
	 * 
	 * get a List<String> of generation warnings
	 * that occurred while generating the
	 * transcoding jobs
	 * 
	 * @return generation warnings
	 */
	public List getGenerationWarnings() {
		return this.warnings;
	}
	
	
	/**
	 * getTablesOfType
	 * 
	 * get a Map of <String tableName, TableData tableData> key value pairs
	 * of all tables of the specified type from the specified model
	 * 
	 * @param tableType like translation tables or domain translation tables
	 * @param parPhysicalModelID
	 * @return
	 */
	private Map getTablesOfType(int tableType, String parPhysicalModelID) {

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("table type = " + tableType + " - model id = " + parPhysicalModelID);
		}

		Map<String, TableData> tableDataMap = new HashMap<String,TableData>();
		ModelInfoBlock modelInfoBlk = (ModelInfoBlock) _PhysModelID2TableMap.get(parPhysicalModelID);
		Map tableSetMap = modelInfoBlk.getTableMap();

		/* get all tables of the specified table type */
		TableData[] tableData = (TableData[]) tableSetMap.get(tableType);

		/* convert array into Map<TableName, TableData> */
		for(int i=0; i<tableData.length; i++) {
			TableData td        = tableData[i];
			String    tableName = td.getName();
			tableDataMap.put(tableName, td);
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("n tables = " + tableDataMap.size());
		}

		/* return <String tableName, TableData tableData> map */
		return(tableDataMap);
	}
	
	
	/**
	 * getTranslationTables
	 * 
	 * get a Map of <String tableName, TableData tableData>
	 * key value pairs for all translation tables in the
	 * specified physical data model
	 * 
	 * @param parPhysicalModelId
	 * @return Map
	 */
	private Map getTranslationTables(String parPhysicalModelId) {
		return this.getTablesOfType(SupportedTableTypesMap.TABLE_TYPE_INT_TRANSLATION_TABLE, parPhysicalModelId);
	}


	/**
	 * getDomainTranslationTables
	 * 
	 * get a Map of <String tableName, TableData tableData>
	 * key value pairs for all domain translation tables in the
	 * specified physical data model
	 * 
	 * @param parPhysicalModelId
	 * @return Map
	 */
	private Map getDomainTranslationTables(String parPhysicalModelId) {
		return this.getTablesOfType(SupportedTableTypesMap.TABLE_TYPE_INT_DOMAIN_TRANSLATION_TABLE, parPhysicalModelId);
	}


	/**
	 * validate
	 * 
	 * validate the transcoding job request
	 */
	protected void validate() throws JobGeneratorException {
		
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}
		
		/* detect and store the database type */
		RequestJobTypeTranscoding jtTranscoding = (RequestJobTypeTranscoding) getJobType();
		
		/* check if target legacyID is specified */
		String legacyID = jtTranscoding.getTargetLegacyID();
		
		/* target legacyID is mandatory */
		if(legacyID == null || legacyID.equals(Constants.EMPTY_STRING)) {
			throw new JobGeneratorException("127100E", Constants.NO_PARAMS); //$NON-NLS-1$ 
		}
		
		/* make sure that the source dbm is an ALG model */
		
		
		/* source stage of a transcoding job must be an ODBC stage */
		PersistenceData srcPersistenceData = jtTranscoding.getPersistenceDataSrc();
		if (!(srcPersistenceData instanceof ODBCStageData)) {
			throw new JobGeneratorException("127200E", Constants.NO_PARAMS); //$NON-NLS-1$ 
		}
		
		/* check the database type of the source ODBC stage - only IBM DB2 and ORACLE are supported */
		DataBaseType dbType = ((ModelInfoBlock)_PhysModelID2TableMap.get(jtTranscoding.getSrcPhysicalModelId())).getDatabaseId();
		
		if(dbType != DataBaseType.DB2 && dbType != DataBaseType.Oracle) {
			throw new JobGeneratorException("127300E", new String[] { dbType.toString() } ); //$NON-NLS-1$ 
		}

		/* transcoding job has to be a valid movement job */
		super.validate();
		
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	}
	
	
	/**
	 * selectColumn
	 * 
	 * add a database vendor specific select statement
	 * to the given SQL StringBuffer for the given table name
	 * and column data
	 * 
	 * @param sql
	 * @param tableName
	 * @param columnData
	 */
	private void selectColumn(StringBuffer sql, String tableName, ColumnData columnData) {

		switch(dbType) {
			/* oracle - quote table names and column names */
			case Oracle:
				  sql.append("\"").append(tableName).append("\".\"").append(columnData.getName()).append("\""); //$NON-NLS-1$
              break;
               
              /* IBM DB2 */
			case DB2:
			default:
				  sql.append(tableName).append(".").append(columnData.getName()); //$NON-NLS-1$
              break;
		}
	}
	
	
	
	
	/**
	 * transcodeColumn
	 * 
	 * append a database vendor specific
	 * SQL transcoding snippet to the given
	 * StringBuffer for the given column
	 * 
	 * @param sql 
	 * @param counter to create unique translation table aliases
	 * @param ttAliases
	 * @param joinConditions
	 * @param ttName
	 * @param columnData
	 * @param joinCondition to join the translation table or domain translation table with a SAP logical table
	 * @param translation table column or domain translation table column
	 */
	private void transcodeColumn(StringBuffer sql, int counter, Map<String,String> ttAliases, Map<String,String> joinConditions, 
	                             String ttName, ColumnData columnData, String joinCondition, String translationTableColumn,
	                             boolean markUnmappedValues)
	        throws JobGeneratorException {
		
		if (TraceLogger.isTraceEnabled()) {
//			TraceLogger.entry();
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "ENTRY: ttName = " + ttName + " - col = " + columnData + 
			                                            " - JoinCond = " + joinCondition + " - TTColumn = " + translationTableColumn);
		}
		
		// consistency check: ttName and jonCondition must both exist !!!
		if (joinCondition == null || joinCondition.length() == 0) {
			throw new JobGeneratorException("127400E", new String[] { ttName, columnData.getName() } ); //$NON-NLS-1$
		}
		if (ttName == null || ttName.length() == 0) {
			throw new JobGeneratorException("127500E", new String[] { joinCondition, columnData.getName() } ); //$NON-NLS-1$
		}
			
		/* get column information */
		String colName = columnData.getName();

		/* create a new alias for the translation table */
		String ttAlias = "TT"+counter; //$NON-NLS-1$
		/* remember the translation table alias combination */
		ttAliases.put(ttAlias, ttName);
		
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Transcoding column "+ colName+ " with translation table alias "+ ttAlias); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		String dataTableAlias = columnData.getTableData().getName();
		switch(dbType) {
			     // oracle - quote table names and column names
			case Oracle:
				  String dataColName = "\"" + dataTableAlias + "\".\"" + colName + "\"";
				  sql.append("coalesce(");
				  sql.append("\"").append(ttAlias).append("\".\"").append(translationTableColumn).append("\", ");
				  if (markUnmappedValues) {
				      // * MARK UNMAPPED VALUES :
				      // * **********************
				      // * Example transcoding clause:
				      // * coalesce(TT10.TARGET_BRSCH, NVL2(D.BRSCH, CONCAT('§', D.BRSCH), D.BRSCH)) AS BRSCH
						sql.append("NVL2(").append(dataColName).append(", CONCAT('§', ").append(dataColName).append("), ");
						sql.append(dataColName).append("))");
				  }
				  else {
				      // * DO NOT MARK UNMAPPED VALUES :
				      // * *****************************
				      // * Example transcoding clause:
				      // * coalesce(TT10.TARGET_BRSCH, D.BRSCH) AS BRSCH
						sql.append(dataColName).append(")");
				  }
				  sql.append(" AS \"").append(colName).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
				  break;
           
				  // IBM DB2
			case DB2: 
				  sql.append("coalesce(");
				  sql.append(ttAlias).append(".").append(translationTableColumn);
				  if (markUnmappedValues) {
					  // * MARK UNMAPPED VALUES :
				     // * **********************
					  // * Example transcoding clause:
					  // * coalesce(TT10.TARGET_BRSCH, CONCAT('§', D.BRSCH)) AS BRSCH
					  sql.append(", CONCAT('§', ").append(dataTableAlias).append(".").append(colName).append("))");
				  }
				  else {
				      // * DO NOT MARK UNMAPPED VALUES :
				      // * *****************************
						// * Example transcoding clause:
						// * coalesce(TT10.TARGET_BRSCH, D.BRSCH) AS BRSCH
						sql.append(", ").append(dataTableAlias).append(".").append(colName).append(")");
				  }
				  sql.append(" AS ").append(colName); //$NON-NLS-1$
				  break;
		}
		joinCondition = joinCondition.replaceAll(ttName + ".", ttAlias + ".");
		
		/* remember the join condition - we will need it later */
		joinConditions.put(ttAlias, joinCondition);
		
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "RETURN"); //$NON-NLS-1$
//			TraceLogger.exit();
		}
	}


	/**
	 * generateTranscodingSqlXmlSnippet
	 * 
	 * Transcoding jobs require a non-default SQL select statement. This method
	 * is used to generate the required SQL statement.
	 * 
	 * @param tableData
	 * @param transcoding request
	 * @return
	 * @throws JobGeneratorException 
	 */
	private String generateTranscodingSqlXmlSnippet(TableData tableData, RequestJobTypeTranscoding request) throws JobGeneratorException {

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("Table = " + tableData.getName());
		}
		
		// check if different model is to be used for translation tables 
		String transcodingTableModelId = request.getCTPhysicalModelId();
		if (transcodingTableModelId == null) {
			// no different model ==> use the source model
			transcodingTableModelId = request.getSrcPhysicalModelId();
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "transcodingTableModelId = " + transcodingTableModelId);
		}
		
		/* get required translation tables */
		Map translationTableMap = this.getTranslationTables(transcodingTableModelId);
		
		/* get required domain translation tables */
		Map domainTranslationTableMap = this.getDomainTranslationTables(transcodingTableModelId);
		
		/* transcoding SQL select snippet */
		StringBuffer sql = new StringBuffer("<SQL> <SelectStatement collapsed='1' modified='1' type='string'><![CDATA[SELECT \n"); //$NON-NLS-1$


		/* remember the table name */
		String tableName = tableData.getName();
		
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINER, "Generating transcoding SQL statement for table " + tableName); //$NON-NLS-1$
		}

		/* get a list of all columns of the table */
		ColumnData[] columnDataArr = tableData.getColumnData();
		
		/* Map of join conditions to join the translation tables with translation table name as key */
		Map<String,String> joinConditions = new HashMap<String,String>();
		
		/* Map translation table aliases to translation tables */
		Map<String,String> ttAliases = new HashMap<String,String>();
		

		/*
		 * iterate over each column and check if the columns has a related translation table
		 */
		for (int counter = 0; counter < columnDataArr.length; counter++) {
			ColumnData columnData = columnDataArr[counter];
			
			/* determine transcoding type for this column - checkTable, Domain or none */
			TranscodingType transcodingType = this.determineTranscodingType(columnData, request, translationTableMap, domainTranslationTableMap, tableName);
		
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, "TT check for column '" + columnData.getName() + "': transoding type = " + transcodingType.toString());
			}
			
			switch(transcodingType) {
			
				case CheckTableTranscoding:
					String ttName = columnData.getRelatedTranslationTable();
					/* corresponding translation table column */
					String ttColumn = columnData.getRelatedTranslationTableColumn();
					/* replace translation table with alias in join condition */
					String ttJoinCondition = columnData.getRelatedTTJoinCondition();		
					/* append transcoding sql statement for this column */
					this.transcodeColumn(sql, counter, ttAliases, joinConditions, ttName, columnData,
					                     ttJoinCondition, ttColumn, request.isMarkUnmappedValues());
					break;
				
				case DomainTranscoding:
					/* domain translation table */
					String dtt = columnData.getDomainTranslationTable();
					/* target column in domain translation table */
					String dttColumn = columnData.getRelatedDTTColumn();
					/* join condition to join domain translation table with SAP logical table */
					String dttJoinCondition = columnData.getRelatedDTTJoinCondition();		
					/* transcode domain value column */
					this.transcodeColumn(sql, counter, ttAliases, joinConditions, dtt, columnData,
					                     dttJoinCondition, dttColumn, request.isMarkUnmappedValues());
					break;
					
				default:
					/* column has no translation table and no domain - just do a regular select */
					this.selectColumn(sql, tableName, columnData);
					break;
			}
					
			sql.append(",").append("\n");//$NON-NLS-1$ //$NON-NLS-2$
		}
		
		/* remove last ',' */
		sql.deleteCharAt(sql.length()-2);
		
		/* append 'from' clause */
		appendFromClause(sql, tableName);
		
		/* append 'join' statements to the sql statement */
		appendJoinStatements(sql, joinConditions, ttAliases, tableName);
		
		/* append 'where' clause */
		appendWhereClause(sql, tableName);
		
		/* append ODBC stage column type description */
		appendTableMetadata(sql, tableName, columnDataArr);
		
		
		/* return SQL statement string */
		String sqlString = sql.toString();
		
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("Generated transcoding SQL statement for table '" + tableName + "': " + sqlString);//$NON-NLS-1$ //$NON-NLS-2$);
		}

		/* return SQL XML */
		return sqlString;
	}
	
	/**
	 * appendFromClause
	 * 
	 * @param sql
	 * @param tableName
	 */
	private void appendFromClause(StringBuffer sql, String tableName) {
		
		/* add database vendor specific FROM clause */
		switch(dbType) {
		 case DB2:
			 sql.append(" FROM ").append("\"#SCHEMA#\"").append(".").append(tableName).append(" ").append(tableName); //$NON-NLS-1$ //$NON-NLS-2$
             break;
           
		 case Oracle:
			 sql.append(" FROM ").append("#SCHEMA#").append(".").append(tableName).append(" ").append(tableName); //$NON-NLS-1$ //$NON-NLS-2$
             break;
		}
	}
	
	
	
	/**
	 * appendWhereClause
	 * 
	 * @param sql
	 * @param tableName
	 */
	private void appendWhereClause(StringBuffer sql, String tableName) {
		// WHERE #WHERE#]]>
		sql.append(" WHERE #WHERE#]]>").append("\n"); //$NON-NLS-1$
	}


	/**
	 * appendJoinStatements
	 * 
	 * @param sql
	 * @param joinConditions
	 * @param ttAliases
	 * @param tableName
	 */
	private void appendJoinStatements(StringBuffer sql, Map joinConditions, Map ttAliases, String tableName ) {
		
		/* join SAP value table with LEGACY_SYSTEM table */
		sql.append(" JOIN AUX.LEGACY_SYSTEM T on (T.CW_LEGACY_ID='").append(targetLegacyID).append("')\n"); //$NON-NLS-1$ //$NON-NLS-2$
		
		/* join translation tables */
		Collection keys = joinConditions.keySet();
		Iterator joinConditionsIterator = keys.iterator();
		while(joinConditionsIterator.hasNext()) {

			
			/* generate something like this:
			 * LEFT OUTER JOIN ALG0.TT_T016 TT10 ON D.LEGACY_ID = TT10.SOURCE_LEGACY_ID   
			 * AND T.LEGACY_ID = TT10.TARGET_LEGACY_ID AND D.BRSCH = TT10.SOURCE_BRSCH
			 */
			
			String ttAlias = (String) joinConditionsIterator.next();
			String translationTable = (String) ttAliases.get(ttAlias);
			String joinCondition = (String) joinConditions.get(ttAlias);
			
			sql.append("LEFT OUTER JOIN #SCHEMA#.").append(translationTable).append(" ").append(ttAlias).append(" ").append(" ON ");  //$NON-NLS-1$
			
			/* join translation table LEGACY_ID columns */
			sql.append(tableName).append(".CW_LEGACY_ID = ").append(ttAlias).append(".SOURCE_LEGACY_ID AND T.CW_LEGACY_ID = ").append(ttAlias).append(".TARGET_LEGACY_ID AND ");
			
			/* join translation table value columns */
			/* replace '|' with 'AND' */
			joinCondition = joinCondition.replaceAll("\\|", "AND");  //$NON-NLS-1$
			sql.append(joinCondition).append("\n"); //$NON-NLS-1$
			
		}
	}


	/**
	 * appendTableMetadata
	 * 
	 * @param sql
	 * @param tableName
	 * @param columnDataArr
	 */
	private void appendTableMetadata(StringBuffer sql, String tableName, ColumnData[] columnDataArr) {
		
		sql.append("<Tables collapsed='1'>	<Table type='string'><![CDATA[").append("#SCHEMA#").append(".").append(tableName).append("]]></Table></Tables>\n"); //$NON-NLS-1$

		sql.append("<Parameters collapsed='1'></Parameters> <Columns collapsed='1'>\n"); //$NON-NLS-1$
		
		/* add <Column type='string'><![CDATA[$COLNAME,$COLNAME,$TABLENAME]]></Column> for each column */
		for (int i = 0; i < columnDataArr.length; i++) {
			
			/* get column information */
			ColumnData columnData = columnDataArr[i];
			String colName = columnData.getName();
			
			sql.append("<Column type='string'><![CDATA[").append(colName).append(",").append(colName).append(",").append(tableName).append("]]></Column>").append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		sql.append("</Columns><WhereClause type='string'><![CDATA[" + tableName + ".#WHERE#]]></WhereClause></SelectStatement>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		sql.append("<EnablePartitioning modified='1' type='bool'><![CDATA[0]]></EnablePartitioning\n>" + "</SQL>\n"); //$NON-NLS-1$ //$NON-NLS-2$
	}


	/**
	 * determineTranscodingType
	 * 
	 * determine the transcoding type of the given
	 * ColumnData object:
	 * 
	 * If the column has a reference check table, the corresponding 
	 * translation table will be used to transcode the column values, but 
	 * only if the transcoding request is configured to transcode columns
	 * that have a reference check table.
	 * 
	 * If the column has a non-reference check table, the corresponding 
	 * translation table will be used to transcode the column values, but 
	 * only if the transcoding request is configured to transcode columns
	 * that have a non-reference check table.
	 * 
	 * If the column has no check table but is assigned to a SAP domain,
	 * the corresponding domain translation table will be used to 
	 * transcode the column values, but only if the transcoding request is 
	 * configured to transcode columns that have a domain.
	 * 
	 * If the column has both, a check table and a domain, the (check table)
	 * translation table will be used for transcoding. The domain and its
	 * corresponding domain translation table will not be considered for
	 * transcoding in this case.
	 * 
	 * If the column has neither a check table nor a domain, the column
	 * will not be transcoded.
	 * 
	 * @param col
	 * @param transcoding request
	 * @param translation table map
	 * @param domain translation table map
	 * @param table name
	 * @return
	 * @throws jobGeneratorException
	 */
	private TranscodingType determineTranscodingType(ColumnData col, RequestJobTypeTranscoding request, 
		                                              Map translationTableMap, Map domainTranslationTableMap, String tableName)
	        throws JobGeneratorException {
		TranscodingType resultTranscType;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "ENTRY: table = " + tableName + " - col = " + col + " - request = " + request);
//			TraceLogger.entry();
		}

		// transcode reference check tables
		boolean transcodeReferenceCheckTables = request.isTranscodeReferenceFields();
		
		// transcode non-reference check tables
		boolean transcodeNonReferenceCheckTables = request.isTranscodeNonReferenceFields();
		
		// transcode domain values
		boolean transcodeDomainValues = request.isTranscodeDomainValueFields();
		
		// mark unmapped values
//		boolean markUnmappedValues = request.isMarkUnmappedValues();
		
		// check if column is applicable for check table transcoding
		String ct      = col.getRelatedCheckTable();
		String ttName  = col.getRelatedTranslationTable();
		String colName = col.getName();

		resultTranscType = TranscodingType.NoTranscoding;
		if ((ct     != null && !ct.equals(Constants.EMPTY_STRING))      && 
		    (ttName != null && !ttName.equals(Constants.EMPTY_STRING))) {

			// sanity test - check if translation table exists in the physical data model
			TableData ttData = (TableData) translationTableMap.get(ttName);

			if (ttData == null) {
				if(transcodeReferenceCheckTables || transcodeNonReferenceCheckTables) {

					/* Column has a translation table annotation, but the translation table does not exist in the model. This
					 * might happen if one SAP logical table (like MBEW) has a checktable reference to another SAP logical table (like MARA) */

					String warning = MessageFormat.format(ServerMessageCatalog.getDefaultCatalog().getMessage("001001W"),
					                                      new Object [] {tableName, colName, ttName} );
					this.warnings.add(warning);

					if (TraceLogger.isTraceEnabled()) {
						TraceLogger.trace(TraceLogger.LEVEL_INFO,   warning);
						TraceLogger.trace(TraceLogger.LEVEL_FINEST, warning); 
					}
				} // end of if(transcodeReferenceCheckTables || transcodeNonReferenceCheckTables)
				
				resultTranscType = TranscodingType.NoTranscoding;

				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.trace(TraceLogger.LEVEL_FINEST, "NoTranscoding"); 
			   }
			}
			else {
				/* 
				 * At this point we have to distinguish two code paths depending
				 * on the checkTableType:
				 * 
				 * If the checkTableType is null, which
				 * might happen on older model versions, we cannot differ translation
				 * tables for reference check tables and translation tables for non-reference tables.
				 * That's o.k. as long as the transcoding job is configured to transcode both, reference
				 * check tables and non-reference check tables. But if the transcoding job is configured
				 * to transcode certain types of check tables only, there's now way for us to decide whether 
				 * a particular translation table has a reference check table or non-reference check table assigned.
				 * In this case with have to abort the job generation with an exception.
				 * 
				 * If the checkTableType is not null (which should be the case on newer model version) 
				 * we can distinguish translation tables for reference check tables and translation tables 
				 * for non-reference check tables. In this case, we can generate transcoding jobs that use 
				 * translation tables for reference and non-reference check tables, or for reference or 
				 * non-reference check tables only.
				 */

				/* check if we have an annotated check table type - ReferenceCheckTable or NonReferenceCheckTable */
				String checkTableType = ttData.getCheckTableType();
				if (checkTableType == null) {

					if (transcodeReferenceCheckTables && transcodeNonReferenceCheckTables) {

						/* we don't have an annotated check table type for this translation table,
						 * but we can continue if this transcoding job is configured to transcode both,
						 * reference check tables and non-reference check tables
						 */
						resultTranscType = TranscodingType.CheckTableTranscoding;

						if (TraceLogger.isTraceEnabled()) {
							TraceLogger.trace(TraceLogger.LEVEL_FINEST, "CheckTableTranscoding"); 
					   }
					} 
					else {
						/* throw an exception if we must not transcode certain check table types but have
						 * no information about the check table type
						 */
						throw new JobGeneratorException("127000E",                      //$NON-NLS-1$
						                                new String[] { ttName, Constants.ANNOT_SAPPACK_CHECK_TBL_DATA_OBJECT_SOURCE } );
					} // end of if (transcodeReferenceCheckTables && transcodeNonReferenceCheckTables)
				}
				else {
					/* model contains the SAPPACK_CHECK_TABLE_DATA_OBJECT_SOURCE annotation
					 * for translation tables. We can distinguish reference check tables
					 * and non-reference check tables
					 */
					if (transcodeReferenceCheckTables && 
						checkTableType.equals(Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_CHECK_TABLE)) {

						resultTranscType = TranscodingType.CheckTableTranscoding;

						if (TraceLogger.isTraceEnabled()) {
							TraceLogger.trace(TraceLogger.LEVEL_FINEST, "CheckTableTranscoding"); 
					   }
					}
					else {
						if (transcodeNonReferenceCheckTables && 
							checkTableType.equals(Constants.DATA_OBJECT_SOURCE_TYPE_NON_REFERENCE_CHECK_TABLE)) {

							resultTranscType = TranscodingType.CheckTableTranscoding;

							if (TraceLogger.isTraceEnabled()) {
								TraceLogger.trace(TraceLogger.LEVEL_FINEST, "CheckTableTranscoding"); 
						   }
						}
						else {
							resultTranscType = TranscodingType.NoTranscoding;

							if (TraceLogger.isTraceEnabled()) {
								TraceLogger.trace(TraceLogger.LEVEL_FINEST, "NoTranscoding"); 
						   }
						} // end of (else) if (transcodeNonReferenceCheckTables && ... TYPE_NON_REFERENCE_CHECK_TABLE))
					} // end of (else) if (transcodeReferenceCheckTables && ... TYPE_REFERENCE_CHECK_TABLE))
				} // end of if (checkTableType == null)
			} // end of (else) if (ttData == null)
		}
		else {      // else ==> if ((ct != null && !ct.equals(Constants.EMPTY_STRING)) ...
			/* check if column is applicable for domain transcoding */
			String domain  = col.getDomain();
			String dttName = col.getDomainTranslationTable();

			if (domain  != null && !domain.equals(Constants.EMPTY_STRING)   && 
				 dttName != null && !dttName.equals(Constants.EMPTY_STRING)) {

				if (transcodeDomainValues) {
					
					/* sanity test - check if domain translation table exists in the physical data model */
					if(!domainTranslationTableMap.containsKey(dttName)) {
						
						/* log a warning if domain translation table does not exist in the physical data model */
						String warning = MessageFormat.format(ServerMessageCatalog.getDefaultCatalog().getMessage("001001W"),
						                                      new Object [] {tableName, colName, dttName} );
						this.warnings.add(warning);

						if (TraceLogger.isTraceEnabled()) {
							TraceLogger.trace(TraceLogger.LEVEL_INFO, warning); 
							TraceLogger.trace(TraceLogger.LEVEL_FINEST, warning); 
					   }

						resultTranscType = TranscodingType.NoTranscoding;
					} 
					else {
						resultTranscType = TranscodingType.DomainTranscoding;

						if (TraceLogger.isTraceEnabled()) {
							TraceLogger.trace(TraceLogger.LEVEL_FINEST, "DomainTranscoding"); 
					   }
					}
				} // end of if (transcodeDomainValues)
			} // end of if (domain  != null && ... !dttName.equals(Constants.EMPTY_STRING)) 
	   } // end of (else) if ((ct != null && !ct.equals(Constants.EMPTY_STRING)) ...

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "RETURN: rc = " + resultTranscType.toString());
//			TraceLogger.exit();
		}
		
		return resultTranscType;
	}
}
