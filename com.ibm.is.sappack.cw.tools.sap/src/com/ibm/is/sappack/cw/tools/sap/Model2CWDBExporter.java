package com.ibm.is.sappack.cw.tools.sap;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.ibm.db.models.logical.AtomicDomain;
import com.ibm.db.models.logical.Attribute;
import com.ibm.db.models.logical.Entity;
import com.ibm.iis.sappack.gen.common.ui.connections.CWDBConnection;
import com.ibm.iis.sappack.gen.common.ui.util.Pair;
import com.ibm.iis.sappack.gen.tools.sap.views.dependency.DependencyTreeFactory;
import com.ibm.is.sappack.cw.tools.sap.Model2CWDBExporter.Message.MSGTYPE;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.importer.LogicalTable2LdmImporter;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;

public class Model2CWDBExporter {
	static String copyright() {
		return com.ibm.is.sappack.cw.tools.sap.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static class Message { //implements Comparable<Message>{
		public static enum MSGTYPE {
			ERROR, WARNING, INFO 
		}

		private MSGTYPE type;
		private String message;

		public Message(MSGTYPE type, String message) {
			super();
			this.type = type;
			this.message = message;
		}

		public MSGTYPE getType() {
			return type;
		}

		public String getMessage() {
			return message;
		}

		public String toString() {
			String msg = null;
			switch (this.type) {
			case ERROR:
				msg = Messages.Model2CWDBExporter_0;
				break;
			case WARNING:
				msg = Messages.Model2CWDBExporter_1;
				break;
			default:
				msg = Messages.Model2CWDBExporter_2;
			}
			msg = MessageFormat.format(msg, this.message);
			return msg;
		}

		/*
		@Override
		public int compareTo(Message o) {
			int thisType = this.type.ordinal();
			int otherType = o.type.ordinal();
			if (thisType != otherType) {
				return thisType < otherType ? -1 : 1;
			}
			return message.compareTo(o.message);
		}
*/
	}

	public static class MessageList extends ArrayList<Message> {

		private static final long serialVersionUID = -990644122033137874L;

		public List<Message> getMessages(MSGTYPE filterType) {
			List<Message> result = new ArrayList<Model2CWDBExporter.Message>();
			for (Message m : this) {
				if (m.getType() == filterType) {
					result.add(m);
				}
			}
			return result;
		}

		public List<Message> getErrorMessages() {
			return getMessages(MSGTYPE.ERROR);
		}

		public List<Message> getWarningMessages() {
			return getMessages(MSGTYPE.WARNING);
		}

		public String getErrorMessagesAsString() {
			List<Message> messages = getErrorMessages();
			if (messages.isEmpty()) {
				return null;
			}
			String NL = "\n"; //$NON-NLS-1$
			StringBuffer buf = new StringBuffer();
			for (Message m : messages) {
				buf.append(m.toString() + NL);
			}
			return buf.toString();
		}

		public String getAllMessagesAsString() {
			String NL = "\n"; //$NON-NLS-1$
			StringBuffer buf = new StringBuffer();
			
			for (Message m : this.getErrorMessages()) {
				buf.append(m.toString() + NL);
			}
			buf.append(NL);
			for (Message m : this.getWarningMessages()) {
				buf.append(m.toString() + NL);
			}
			buf.append(NL);
			for (Message m : this.getMessages(MSGTYPE.INFO)) {
				buf.append(m.toString() + NL);
			}
					
			return buf.toString();
		}
	}

	boolean enableUserAnnotations = true;
	
	String legacyID;
	String rlout;
	String objectName;
	CWDBConnection cwdbConnection;
	Connection connection;
	MessageList messages;

	List<String> dataTablesAsDefinedInConfig;

	List<LdmAccessor> ldmAccessors;
	Entity rootEntity = null;
	List<Entity> dataTables;
	List<Entity> checkTables;
	List<Entity> textTables;
	List<Entity> domainTables;
	Map<Entity, LdmAccessor> entity2LDMMap;
	
	private static final String INSERT_RELATION_STAT = //
	"INSERT INTO AUX.SAP_FOREIGN_KEYS (" + // //$NON-NLS-1$
			"           CW_LEGACY_ID" + // 1 //$NON-NLS-1$
			"         , CW_LOB" + // 2 //$NON-NLS-1$
			"         , CW_RLOUT" + // 3 //$NON-NLS-1$
			"         , SAP_TABNAME" + // 4 //$NON-NLS-1$
			"         , CW_TABNAME" + // 5 //$NON-NLS-1$
			"         , SAP_FIELDNAME" + // 6 //$NON-NLS-1$
			"         , CW_FIELDNAME" + // 7 //$NON-NLS-1$
			"         , FORTABLE" + // 8 //$NON-NLS-1$
			"         , CHECKTABLE" + // 9 //$NON-NLS-1$
			"         , FORKEY" + // 10 //$NON-NLS-1$
			"         , CHECKFIELD" + // 11 //$NON-NLS-1$
			"         , PRIMPOS" + // 12 //$NON-NLS-1$
			"         , SAP_FORTABLE" + // 13 //$NON-NLS-1$
			"         , SAP_CHECKTABLE" + // 14 //$NON-NLS-1$
			"         , SAP_FORKEY" + // 15 //$NON-NLS-1$
			"         , SAP_CHECKFIELD" + // 16 //$NON-NLS-1$
			"         )" + // //$NON-NLS-1$
			" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; //$NON-NLS-1$

	private Entity getDataTableEntityFromSAPName(String sapName) {
		for (Entity e : this.dataTables) {
			if (sapName.equals(getSAPTableName(e))) {
				return e;
			}
		}
		return null;
	}

	private Entity getTextTableEntityFromSAPName(String sapName) {
		for (Entity e : this.textTables) {
			if (sapName.equals(getSAPTextTableName(e))) {
				return e;
			}
		}
		return null;
	}

	private Entity getCheckTableEntityFromSAPName(String sapName) {
		for (Entity e : this.checkTables) {
			if (sapName.equals(getSAPCheckTableName(e))) {
				return e;
			}
		}
		return null;
	}

	public Model2CWDBExporter(List<IFile> ldmFiles, CWDBConnection cwdbConnection, String legacyID, String rlout, String objectName) throws IOException {
		this.ldmAccessors = new ArrayList<LdmAccessor>();
		for (IFile f : ldmFiles) {
			this.ldmAccessors.add(new LdmAccessor(f, null));
		}
		this.cwdbConnection = cwdbConnection;
		this.legacyID = legacyID;
		this.rlout = rlout;
		this.objectName = objectName;
	}

	static Entity findEntity(Entity entity, Collection<Entity> entities) {
		for (Entity e : entities) {
			if (e.getName().equals(entity.getName())) {
				return e;
			}
		}
		return null;
	}

	private boolean checkIfRMImportsExistinLDMs(IProgressMonitor monitor) {
		boolean result = true;
		for (LdmAccessor acc : this.ldmAccessors) {
			IFile ldmFile = acc.getModelFile();
			String id = DependencyTreeFactory.getLDMID(ldmFile);
			if (id == null) {
				String msg = MessageFormat.format(Messages.Model2CWDBExporter_23, ldmFile.getName());
				this.messages.add(new Message(MSGTYPE.WARNING, msg));
			}
		}
		return result;
	}
	
	public MessageList initialize(IProgressMonitor monitor) throws SQLException {
		monitor = SubMonitor.convert(monitor);
		this.entity2LDMMap = new HashMap<Entity, LdmAccessor>();
		this.dataTablesAsDefinedInConfig = new ArrayList<String>();
		messages = new MessageList();
		this.connection = this.cwdbConnection.getJDBCConnection();
		dataTables = new ArrayList<Entity>();
		checkTables = new ArrayList<Entity>();
		this.textTables = new ArrayList<Entity>();
		this.domainTables = new ArrayList<Entity>();

		if (!checkIfRMImportsExistinLDMs(monitor)) {
			return messages;
		}
		
		if (!readAndCheckModels(monitor)) {
			return messages;
		}
		if (monitor.isCanceled()) {
			return messages;
		}

		if (!readConfigTables(monitor)) {
			return messages;
		}
		if (monitor.isCanceled()) {
			return messages;
		}

		if (!checkConfiguredTablesExistInModel(monitor)) {
			return messages;
		}

		return messages;
	}

	private boolean checkConfiguredTablesExistInModel(IProgressMonitor monitor) {
		Set<String> bdrTablesNotInModel = new HashSet<String>(this.dataTablesAsDefinedInConfig);
		for (Entity e : this.dataTables) {
			for (String t : this.dataTablesAsDefinedInConfig) {
				if (getSAPTableName(e).equals(t)) {
					bdrTablesNotInModel.remove(t);
				}
			}
		}
		for (String t : bdrTablesNotInModel) {
			messages.add(new Message(MSGTYPE.WARNING, MessageFormat.format(Messages.Model2CWDBExporter_3, t)));
		}

		Set<String> modelTablesNotInBDR = new HashSet<String>();
		for (Entity e : this.dataTables) {
			modelTablesNotInBDR.add(getSAPTableName(e));
		}
		for (String t : this.dataTablesAsDefinedInConfig) {
			modelTablesNotInBDR.remove(t);
		}
		for (String t : modelTablesNotInBDR) {
			messages.add(new Message(MSGTYPE.WARNING, MessageFormat.format(Messages.Model2CWDBExporter_4, t)));
		}
		return true;
	}

	private boolean isMarkedAsRoot(Entity e) {
		if (!enableUserAnnotations) {
			return false;
		}
		String isRootAnnot = LdmAccessor.getAnnotationValue(e, Constants.ANNOT_CW_IS_ROOT_TABLE);
		boolean isRoot = false;
		if (isRootAnnot != null && isRootAnnot.equals(Constants.ANNOT_VALUE_TRUE)) {
			isRoot = true;
		}
		return isRoot;
	}
	
	private String getMarkedParentTable(Entity e) {
		if (!enableUserAnnotations) {
			return null;
		}
		String markedParent = LdmAccessor.getAnnotationValue(e, Constants.ANNOT_CW_PARENT_TABLE);
		return markedParent;
	}
	
	private boolean readAndCheckModels(IProgressMonitor monitor) {
		boolean result = true;
		for (LdmAccessor acc : this.ldmAccessors) {
			List<Entity> entities = acc.getAllEntities();
			for (Entity e : entities) {
				
				this.entity2LDMMap.put(e, acc);
				String entityType = LdmAccessor.getAnnotationValue(e, Constants.ANNOT_DATA_OBJECT_SOURCE);
				if (entityType.equals(Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE)) {
					// check root
					if (enableUserAnnotations) {
						if (isMarkedAsRoot(e)) {
							if (this.rootEntity != null) {
								String msg = MessageFormat.format(Messages.Model2CWDBExporter_18, new Object[] { e.getName(), this.rootEntity.getName(), Constants.ANNOT_CW_IS_ROOT_TABLE });
								messages.add(new Message(MSGTYPE.ERROR, msg));
								result = false;
							} else {
								this.rootEntity = e;
								String msg = MessageFormat.format(Messages.Model2CWDBExporter_19, new Object[] { e.getName(), acc.getModelFile().getName(), Constants.ANNOT_CW_IS_ROOT_TABLE });
								messages.add(new Message(MSGTYPE.INFO, msg));
							}
						}
					}
					Entity duplicateEntity = findEntity(e, dataTables);
					if (duplicateEntity != null) {
						String msg = MessageFormat.format(Messages.Model2CWDBExporter_5, e.getName(), entity2LDMMap.get(duplicateEntity).getModelFile().getName(), acc
								.getModelFile().getName());
						messages.add(new Message(MSGTYPE.ERROR, msg));
						result = false;
					} else {
						dataTables.add(e);
					}
				} else if (entityType.equals(Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_CHECK_TABLE) // 
						|| entityType.equals(Constants.DATA_OBJECT_SOURCE_TYPE_NON_REFERENCE_CHECK_TABLE) //
				) {
					Entity duplicateCheckTableEntity = findEntity(e, checkTables);
					if (duplicateCheckTableEntity != null) {
						String msg = MessageFormat.format(Messages.Model2CWDBExporter_6, e.getName(), entity2LDMMap.get(duplicateCheckTableEntity).getModelFile()
								.getName(), acc.getModelFile().getName());
						messages.add(new Message(MSGTYPE.ERROR, msg));
						result = false;
					} else {
						checkTables.add(e);
					}
				} else if (entityType.equals(Constants.DATA_OBJECT_SOURCE_TYPE_DOMAIN_TABLE)) {
					Entity duplicateDomainTableEntity = findEntity(e, domainTables);
					if (duplicateDomainTableEntity != null) {
						String msg = MessageFormat.format(Messages.Model2CWDBExporter_7, e.getName(), entity2LDMMap.get(duplicateDomainTableEntity).getModelFile()
								.getName(), acc.getModelFile().getName());
						messages.add(new Message(MSGTYPE.ERROR, msg));
						result = false;
					} else {
						domainTables.add(e);
					}
				} else if (entityType.equals(Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_TEXT_TABLE)) {
					Entity duplicateTextTableEntity = findEntity(e, textTables);
					if (duplicateTextTableEntity != null) {
						String msg = MessageFormat.format(Messages.Model2CWDBExporter_8, e.getName(), entity2LDMMap.get(duplicateTextTableEntity).getModelFile()
								.getName(), acc.getModelFile().getName());
						messages.add(new Message(MSGTYPE.ERROR, msg));
						result = false;
					} else {
						textTables.add(e);
					}
				}
			}
		}
		
		
		boolean noDataTables = this.dataTables.isEmpty();
		boolean missingAdditionalTables = this.checkTables.isEmpty() || this.textTables.isEmpty() || this.domainTables.isEmpty();
		if (noDataTables) {
			String msg = Messages.Model2CWDBExporter_24;
			messages.add(new Message(MSGTYPE.ERROR, msg));
			result = false;
		} else {
			if (missingAdditionalTables) {
				String msg = Messages.Model2CWDBExporter_25;
				messages.add(new Message(MSGTYPE.WARNING, msg));
			} 
		}

		
		if (enableUserAnnotations) {
			if (this.rootEntity == null) {
				String msg = MessageFormat.format(Messages.Model2CWDBExporter_20, Constants.ANNOT_CW_IS_ROOT_TABLE, Constants.ANNOT_VALUE_TRUE);
				messages.add(new Message(MSGTYPE.ERROR, msg));
				result = false;
			}
		}
		if (enableUserAnnotations) {
			// now check all parenttable relationships
			for (Entity e : this.dataTables) {
				String markedParent = getMarkedParentTable(e);
				if (markedParent != null) {
					String modelName = this.entity2LDMMap.get(e).getModelFile().getName();
					Entity parent = getDataTableEntityFromSAPName(markedParent);
					if (parent == null) {
						String msg = MessageFormat.format(Messages.Model2CWDBExporter_21, new Object[] { e.getName(),
								Constants.ANNOT_CW_PARENT_TABLE, markedParent, modelName });
						messages.add(new Message(MSGTYPE.ERROR, msg));
						result = false;
					} else {
						String msg = MessageFormat.format(Messages.Model2CWDBExporter_22, new Object[] { e.getName(), Constants.ANNOT_CW_PARENT_TABLE,
								markedParent, modelName});
						messages.add(new Message(MSGTYPE.INFO, msg));
					}
				}
			}
		}
	
		return result;
	}

	private String getSAPTableName(Entity e) {
		String sapTableName = LdmAccessor.getAnnotationValue(e, Constants.ANNOT_SAP_TABLE_NAME);
		return sapTableName;
	}

	private String getSAPCheckTableName(Entity e) {
		String sapTableName = LdmAccessor.getAnnotationValue(e, Constants.ANNOT_CHECK_TABLE_NAME);
		return sapTableName;
	}

	private String getSAPTextTableName(Entity e) {
		String sapTableName = LdmAccessor.getAnnotationValue(e, Constants.ANNOT_TEXT_TABLE_NAME);
		return sapTableName;
	}

	private String getSAPColumnName(Attribute attr) {
		String sapAttrName = LdmAccessor.getAnnotationValue(attr, Constants.ANNOT_SAP_COLUMN_NAME);
		return sapAttrName;
	}

	private boolean readConfigTables(IProgressMonitor monitor) throws SQLException {
		ResultSet rs = null;
		PreparedStatement prep = null;
		try {
			prep = this.connection.prepareStatement("SELECT T.SAP_TABNAME FROM AUX.SAP_DATATABLES_CONFIG T WHERE T.CW_RLOUT = ? AND T.CW_LOB = ? AND T.CW_LEGACY_ID = ?"); //$NON-NLS-1$
			prep.setString(1, this.rlout);
			prep.setString(2, this.objectName);
			prep.setString(3, this.legacyID);
			rs = prep.executeQuery();
			while (rs.next()) {
				String tabName = rs.getString(1);
				this.dataTablesAsDefinedInConfig.add(tabName);
			}
		} finally {
			if (prep != null) {
				prep.close();
				prep = null;
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}
		}
		return true;
	}

	public MessageList exportModel2CWDB(IProgressMonitor progressMonitor) throws SQLException {
		// set auto commit to false to enable rollback
		boolean ac = this.connection.getAutoCommit();
		this.connection.setAutoCommit(false);
		boolean success = false;
		Savepoint sp = this.connection.setSavepoint();
		try {
			SubMonitor monitor = SubMonitor.convert(progressMonitor, Messages.Model2CWDBExporter_9, 100);

			messages = new MessageList();

			if (!deleteMetadataTables(monitor.newChild(5))) {
				return messages;
			}
			if (monitor.isCanceled()) {
				return messages;
			}

			if (!publishDatatables(monitor.newChild(20))) {
				return messages;
			}
			if (monitor.isCanceled()) {
				return messages;
			}

			if (!publishChecktables(monitor.newChild(25))) {
				return messages;
			}
			if (monitor.isCanceled()) {
				return messages;
			}
			
			if (!publishTextTables(monitor.newChild(25))) {
				return messages;
			}
			if (monitor.isCanceled()) {
				return messages;
			}
			
			if (!publishDomainTables(monitor.newChild(20))) {
				return messages;
			}

			success = true;
		} catch (SQLException exc) {
			Activator.logException(exc);
			success = false;
			throw exc;
		} finally {
			try {
				if (success) {
					this.connection.commit();
				} else {
					this.connection.rollback(sp);
					this.connection.releaseSavepoint(sp);
				}
			} finally {
				if (progressMonitor != null) {
					progressMonitor.done();
				}
				this.connection.setAutoCommit(ac);
			}
		}
		return messages;
	}

	private boolean publishDomainTables(IProgressMonitor monitor) throws SQLException {
		String domainTableInsertStat = "INSERT INTO AUX.SAP_DOMAINTABLES_METADATA (" + // //$NON-NLS-1$
			"  CW_LEGACY_ID" + // 1 //$NON-NLS-1$
			", CW_LOB" + // 2 //$NON-NLS-1$
			", CW_RLOUT" + // 3 //$NON-NLS-1$
			", DOMNAME" + // 4 //$NON-NLS-1$
			", CW_DOMAIN_TABLE" + // 5 //$NON-NLS-1$
			", CW_DOMAIN_TRANSCODING_TABLE" + // 6 //$NON-NLS-1$
			", DDTEXT"+ // 7 //$NON-NLS-1$
			") VALUES (?, ?, ?, ?, ?, ?, ?)"; //$NON-NLS-1$
		PreparedStatement insertIntoDomainTableStat = null;
		try {
			insertIntoDomainTableStat = this.connection.prepareStatement(domainTableInsertStat);
			PrepStatementBatch batch = new PrepStatementBatch(insertIntoDomainTableStat);
			insertIntoDomainTableStat.setString(1, this.legacyID);
			insertIntoDomainTableStat.setString(2, this.objectName);
			insertIntoDomainTableStat.setString(3, this.rlout);
			for (Entity dtt : this.domainTables) {
				Map<String, String> annotations = LdmAccessor.getAnnotations(dtt);
				setSQLParameter(insertIntoDomainTableStat, 4, Constants.ANNOT_DATATYPE_DOMAIN, annotations);
				
				String dttName = dtt.getName();
				insertIntoDomainTableStat.setString(5, dttName);
				
				setSQLParameter(insertIntoDomainTableStat, 6, Constants.ANNOT_DOMAIN_TRANSLATION_TABLE, annotations);
				
				insertIntoDomainTableStat.setString(7, dtt.getLabel());
				batch.execute();
			}
			batch.finish();
		} finally {
			if (insertIntoDomainTableStat != null) {
				insertIntoDomainTableStat.close();
				insertIntoDomainTableStat = null;
			}
		}
		return true;
	}
	
	private boolean publishTextTables(IProgressMonitor monitor) throws SQLException {
		for (Entity e : this.textTables) {
			publishFields(e, monitor);
			if (monitor.isCanceled()) {
				return false;
			}
		}
		return true;
	}
	
	private boolean publishChecktables(IProgressMonitor monitor) throws SQLException {
		String insertStat = "INSERT INTO AUX.SAP_CHECKTABLES_METADATA (" + // //$NON-NLS-1$
				"CW_LEGACY_ID" + // 1 //$NON-NLS-1$
				", SAP_CHECKTABLE" + // 2 //$NON-NLS-1$
				", CW_CHECKTABLE" + // 3 //$NON-NLS-1$
				", TEXTTABLE" + // 4 //$NON-NLS-1$
				", CW_LOB" + // 5 //$NON-NLS-1$
				", CW_RLOUT" + // 6 //$NON-NLS-1$
				", CW_TEXTTABLE" + // 7 //$NON-NLS-1$
				", TABLE_TYPE" + // 8 //$NON-NLS-1$
				", TRANSCODING_TABLE" + // 9 //$NON-NLS-1$
				", CT_CHECKTABLE" + // 10 //$NON-NLS-1$
				", DDTEXT" + // 11 //$NON-NLS-1$
				") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; //$NON-NLS-1$

		PreparedStatement insertIntoCheckTablesStat = null;
		PreparedStatement insertCheckTextTableRelationStat = null;
		try {
			insertIntoCheckTablesStat = this.connection.prepareStatement(insertStat);
		
			insertCheckTextTableRelationStat = this.connection.prepareStatement(INSERT_RELATION_STAT);
			PrepStatementBatch batch = new PrepStatementBatch(insertIntoCheckTablesStat);
			for (Entity e : this.checkTables) {
				if (monitor.isCanceled()) {
					return false;
				}
				
				// If the check table also appears as a data table, skip the check table metadata creation
				if (!shouldCheckTableBePublished(e)) {
					continue;
				}
				
				Map<String, String> annotations = LdmAccessor.getAnnotations(e);

				insertIntoCheckTablesStat.setString(1, legacyID);
				insertCheckTextTableRelationStat.setString(1, legacyID);
				String traceData = "Check table - text table FK data: " + legacyID;
				
				String sapName = getSAPCheckTableName(e);
				insertIntoCheckTablesStat.setString(2, sapName);
				insertCheckTextTableRelationStat.setString(14, sapName);
				
				String cwTabName = getCWSpecificHalfConvertedTabName(sapName);
				insertIntoCheckTablesStat.setString(3, cwTabName);
				insertCheckTextTableRelationStat.setString(9, cwTabName);
				String traceData89 = cwTabName;                                               // 9
				
				String textTableName = annotations.get(Constants.ANNOT_TEXT_TABLE_NAME);
				insertIntoCheckTablesStat.setString(4, textTableName);
				insertCheckTextTableRelationStat.setString(4, textTableName);
				traceData += "," + this.objectName + "," + this.rlout + "," + textTableName; // 1 - 4
				insertCheckTextTableRelationStat.setString(13, textTableName);
				String traceData1314 = textTableName + "," + sapName;                        // 13 - 14
				
				insertIntoCheckTablesStat.setString(5, this.objectName);
				insertCheckTextTableRelationStat.setString(2, this.objectName);
				
				insertIntoCheckTablesStat.setString(6, this.rlout);
				insertCheckTextTableRelationStat.setString(3, this.rlout);
				
				String modelTextTableName = annotations.get(Constants.ANNOT_MODEL_TEXT_TABLE_NAME);
				insertIntoCheckTablesStat.setString(7, modelTextTableName);
				insertCheckTextTableRelationStat.setString(5, modelTextTableName);
				traceData += "," + modelTextTableName;                                       // 1 - 5
				insertCheckTextTableRelationStat.setString(8, modelTextTableName);
				traceData89 = modelTextTableName + "," + traceData89;                        // 8 + 9
				
				setSQLParameter(insertIntoCheckTablesStat, 8, Constants.ANNOT_DATA_OBJECT_SOURCE, annotations);
				setSQLParameter(insertIntoCheckTablesStat, 9, Constants.ANNOT_TRANSLATION_TABLE_NAME, annotations);
				insertIntoCheckTablesStat.setString(10, e.getName());
				insertIntoCheckTablesStat.setString(11, e.getLabel());
				
				batch.execute();
				publishFields(e, monitor); 
				// publish check / textTableRelation
				if (textTableName != null) {
					Entity textTable = getTextTableEntityFromSAPName(textTableName);
					publishCheckTextTableRelation(e, textTable, insertCheckTextTableRelationStat, monitor, traceData, traceData89, traceData1314);
				}
			}
			batch.finish();
		} finally {
			if (insertIntoCheckTablesStat != null) {
				insertIntoCheckTablesStat.close();
				insertIntoCheckTablesStat = null;
			}
			if (insertCheckTextTableRelationStat != null) {
				insertCheckTextTableRelationStat.close();
				insertCheckTextTableRelationStat = null;
			}
		}
		return true;
	}
	
	private boolean publishCheckTextTableRelation(Entity checkTable, Entity textTable, PreparedStatement insertCheckTextTableRelation, IProgressMonitor monitor,
			String traceData15, String traceData89, String traceData1314) throws SQLException {
		Activator.getLogger().log(Level.FINEST, "publishCheckTextTableRelation enter");
		String joinCond = LdmAccessor.getAnnotationValue(checkTable, Constants.ANNOT_CHECKTABLE_TEXTTABLE_JOINCONDITION);
		if (joinCond == null) {
			return true;
		}
		List<Pair<String, String>> ctTTJoinCond = LogicalTable2LdmImporter.parseCTJoinCondition(joinCond);
		SubMonitor progress = SubMonitor.convert(monitor, Messages.Model2CWDBExporter_10, 100);
		SubMonitor loop = progress.newChild(100).setWorkRemaining(ctTTJoinCond.size());
		PrepStatementBatch batch = new PrepStatementBatch(insertCheckTextTableRelation);
		int pos = 1;
		LdmAccessor ttAcc = this.entity2LDMMap.get(textTable);
		LdmAccessor ctAcc = this.entity2LDMMap.get(checkTable);
		for (Pair<String, String> join : ctTTJoinCond) {
			if (monitor.isCanceled()) {
				return false;
			}
			insertCheckTextTableRelation.setInt(12, pos);
			String traceData1216 = "" + pos + "," + traceData1314;
			pos++;
			
			String ctFieldName = join.getFirst();
			String ttFieldName = join.getSecond();
			insertCheckTextTableRelation.setString(6, ttFieldName);
			String traceData = traceData15 + "," + ttFieldName;                                                   // 1 - 6
			insertCheckTextTableRelation.setString(15, ttFieldName);
			insertCheckTextTableRelation.setString(16, ctFieldName);
			traceData1216 += "," + ttFieldName + "," + ctFieldName;                           // 12 - 16
			
			// leave some parameters empty for now
			Attribute ttAttr = ttAcc.findAttribute(textTable, ttFieldName);
			if (ttAttr != null) {
				String modelFieldName = ttAttr.getName();
				insertCheckTextTableRelation.setString(7, modelFieldName);
				insertCheckTextTableRelation.setString(10, modelFieldName);
				traceData += "," + modelFieldName + "," + traceData89 + "," + modelFieldName; // 1 - 10

				Attribute ctAttr = ctAcc.findAttribute(checkTable, ctFieldName);
				if (ctAttr != null) {
					String modelCTFieldName = ctAttr.getName();
					insertCheckTextTableRelation.setString(11, modelCTFieldName);
					traceData += "," + modelCTFieldName + "," + traceData1216;
					Activator.getLogger().log(Level.FINEST, traceData);
					Activator.getLogger().log(Level.FINEST, "Executing in-between...");
					batch.execute();
				} else {
					String msg = MessageFormat.format(Messages.Model2CWDBExporter_11, ctFieldName);
					Activator.getLogger().log(Level.INFO, msg);
					System.out.println(this.getClass().getName() + ": " + msg); //$NON-NLS-1$
					traceData += "," + null + "," + traceData1216;
					Activator.getLogger().log(Level.FINEST, traceData);
				}
				
			} else {
				traceData += "," + null + "," + traceData89 + "," + null; // 1 - 10
				traceData += "," + null + "," + traceData1216;
				String msg = MessageFormat.format(Messages.Model2CWDBExporter_12, ttFieldName);
				Activator.getLogger().log(Level.INFO, msg);
				System.out.println(this.getClass().getName() + ": " +msg); //$NON-NLS-1$
			}
			loop.worked(1);
		}
		batch.finish();
		Activator.getLogger().log(Level.FINEST, "publishCheckTextTableRelation exit");
		return true;
	}

	private boolean publishDatatables(IProgressMonitor monitor) throws SQLException {
		String insertStat = "INSERT INTO AUX.SAP_DATATABLES_METADATA (" + // //$NON-NLS-1$
				"  CW_LEGACY_ID" + // 1 //$NON-NLS-1$
				", SAP_TABNAME" + // 2 //$NON-NLS-1$
				", CW_TABNAME" + // 3 //$NON-NLS-1$
				", OBJECT_NAME" + // 4 //$NON-NLS-1$
				", KEYFIELD" + // 5 //$NON-NLS-1$
				", FOREIGNKEYFIELD" + // 6 //$NON-NLS-1$
				", CW_LOB" + // 7 //$NON-NLS-1$
				", CW_RLOUT" + // 8 //$NON-NLS-1$
				", DDTEXT" + // 9 //$NON-NLS-1$
				", ISROOT" + // 10 //$NON-NLS-1$
				", PARENTTABLE" + // 11 //$NON-NLS-1$
				", HIERARCHY_LEVEL" + // 12 //$NON-NLS-1$
				") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; //$NON-NLS-1$

		PreparedStatement prep = null;
		try {
			SubMonitor progress = SubMonitor.convert(monitor, Messages.Model2CWDBExporter_13, 100);
			SubMonitor loop = progress.newChild(100).setWorkRemaining(this.dataTablesAsDefinedInConfig.size());
			prep = this.connection.prepareStatement(insertStat);
			for (String t : this.dataTablesAsDefinedInConfig) {
				Entity e = getDataTableEntityFromSAPName(t);
				if (e == null) {
					messages.add(new Message(MSGTYPE.WARNING, MessageFormat.format(Messages.Model2CWDBExporter_14, t)));
					continue;
				}

				prep.setString(1, this.legacyID);
				prep.setString(2, t);
				prep.setString(3, e.getName());
				prep.setString(4, this.objectName);
				prep.setString(5, LogicalTable2LdmImporter.createTechnicalPKName(t));
				prep.setString(6, null); // filled by CW for now
				prep.setString(7, this.objectName);
				prep.setString(8, this.rlout);
				prep.setString(9, e.getLabel());
				String isRoot = null;
				Short rootLevel = null;
				if (this.isMarkedAsRoot(e)) {
					isRoot = "X"; //$NON-NLS-1$
					rootLevel = Short.valueOf((short) 0);
				}
				prep.setString(10, isRoot);
				prep.setObject(12, rootLevel);
				
				String markedParent = this.getMarkedParentTable(e);
				prep.setString(11, markedParent);
				prep.execute();

				//				System.out.println("publish fields for table "+t.name+"...");
				publishFields(e, loop.newChild(1));

				if (loop.isCanceled()) {
					return false;
				}
			}
			//			System.out.println("Datatable publishing done");
		} finally {
			if (prep != null) {
				prep.close();
				prep = null;
			}
		}
		return true;
	}

	private boolean deleteMetadataTables(IProgressMonitor monitor) throws SQLException {
		String[] tablesToBeDeleted = new String[] { //
				//
						"AUX.SAP_DATATABLES_METADATA", // //$NON-NLS-1$
						"AUX.SAP_CHECKTABLES_METADATA", // //$NON-NLS-1$
						"AUX.SAP_DOMAINTABLES_METADATA", // //$NON-NLS-1$
						"AUX.SAP_FIELDS_METADATA", // //$NON-NLS-1$
						"AUX.SAP_FOREIGN_KEYS", // //$NON-NLS-1$
				//
				};

		monitor = SubMonitor.convert(monitor, Messages.Model2CWDBExporter_15, tablesToBeDeleted.length);
		PreparedStatement prep = null;
		try {
			for (String table : tablesToBeDeleted) {
				String deleteStatement = "DELETE FROM " + table + " T WHERE T.CW_RLOUT = ? AND T.CW_LOB = ? AND T.CW_LEGACY_ID = ?"; //$NON-NLS-1$ //$NON-NLS-2$
				prep = connection.prepareStatement(deleteStatement);
				prep.setString(1, this.rlout);
				prep.setString(2, this.objectName);
				prep.setString(3, this.legacyID);
				prep.execute();
				prep.close();
				prep = null;
				monitor.worked(1);
			}
		} finally {
			if (prep != null) {
				prep.close();
				prep = null;
			}
		}
		return true;
	}

	String[] getForTableAndForKey(String leftSideOfJoinCond, String defaultTableName) {
		String[] result = new String[2];
		String forTab = null;
		String forKey = null;
		String cond = leftSideOfJoinCond.trim();
		if (cond.equals("*")) { //$NON-NLS-1$
			forTab = "*"; //$NON-NLS-1$
			forKey = null;
		} else {
			if (cond.startsWith("'") && cond.endsWith("'")) { //$NON-NLS-1$ //$NON-NLS-2$
				forTab = cond;
				forKey = null;
			} else {
				int ix = cond.indexOf("."); //$NON-NLS-1$
				if (ix >= 0) {
					forTab = cond.substring(0, ix);
					forKey = cond.substring(ix + 1);
				} else {
					forTab = defaultTableName;
					forKey = cond;
				}
			}
		}
		result[0] = forTab;
		result[1] = forKey;
		return result;
	}

	String getSAPColumnName(Entity e, String ldmColumnName) {
		LdmAccessor acc = this.entity2LDMMap.get(e);
		Attribute attr = acc.findAttribute(e, ldmColumnName);
		if (attr == null) {
			return null;
		}
		return getSAPColumnName(attr);
	}
	
	List<Pair<String, String>> getSAPJoinCondition(Entity forTable, Entity checkTable, List<Pair<String, String>> modelJoinCond) {
		List<Pair<String, String>> result = new ArrayList<Pair<String,String>>();
		for (Pair<String, String> modelJoin : modelJoinCond) {
			String forTableColumnName = getSAPColumnName(forTable, modelJoin.getFirst());
			if (forTableColumnName == null) {
				forTableColumnName = modelJoin.getFirst();
			}
			String checkTableColumnName = getSAPColumnName(checkTable, modelJoin.getSecond());
			if (checkTableColumnName == null) {
				checkTableColumnName = modelJoin.getSecond();
			}
			result.add(new Pair<String, String>(forTableColumnName, checkTableColumnName));
		}
		return result;
	}


	private void publishRelation(Entity e, Attribute attr, PreparedStatement insertRelationStat, IProgressMonitor monitor) throws SQLException {
		String checkTable = LdmAccessor.getAnnotationValue(attr, Constants.ANNOT_RELATED_CHECKTABLE);
		if (checkTable != null) {
			Entity checkTableEntity = this.getCheckTableEntityFromSAPName(checkTable);
			if (checkTableEntity == null) {
				checkTableEntity = this.getDataTableEntityFromSAPName(checkTable);
			}
			if (checkTableEntity == null) {
				// skip table
				return;
			}
			String ldmJoinCond = LdmAccessor.getAnnotationValue(attr, com.ibm.is.sappack.gen.common.Constants.ANNOT_RELATED_CT_JOIN);
			if (ldmJoinCond != null) {
				Map<String, String> annotations = LdmAccessor.getAnnotations(attr);
				String traceDataBuf = "FK data: ";
				String traceDataBuf9 = "";
				String traceDataBuf14 = "";
				
				String sapcol = setSQLParameter(insertRelationStat, 6, Constants.ANNOT_SAP_COLUMN_NAME, annotations);
				traceDataBuf += sapcol;
				insertRelationStat.setString(7, attr.getName());
				traceDataBuf += "," + attr.getName();
				String sapTableName = getSAPTableName(e);
				insertRelationStat.setString(9, getCWSpecificHalfConvertedTabName(checkTable));
				traceDataBuf9 += "," + getCWSpecificHalfConvertedTabName(checkTable);
				insertRelationStat.setString(14, checkTable);
				traceDataBuf14 += "," + checkTable;

				int primpos = 1;
				List<Pair<String, String>> ldmRelations = LogicalTable2LdmImporter.parseCTJoinCondition(ldmJoinCond);
				List<Pair<String, String>> sapRelations = getSAPJoinCondition(e, checkTableEntity, ldmRelations); 
				for (int i = 0; i < ldmRelations.size(); i++) {
					String traceData = "";
					String traceDataBuf1316 = "";
					traceData += traceDataBuf;
					
					Pair<String, String> sapRelation = sapRelations.get(i);
					String[] sapForTabAndKey = getForTableAndForKey(sapRelation.getFirst(), sapTableName);
					String sapForTab = sapForTabAndKey[0];
					String sapForKey = sapForTabAndKey[1];
					insertRelationStat.setString(13, sapForTab);
					insertRelationStat.setString(15, sapForKey);
					insertRelationStat.setString(16, sapRelation.getSecond());
					traceDataBuf1316 += "," + sapForTab + traceDataBuf14 + "," + sapForKey + "," + sapRelation.getSecond();

					Pair<String, String> ldmRelation = ldmRelations.get(i);
					String[] ldmForTabAndKey = getForTableAndForKey(ldmRelation.getFirst(), sapTableName);
					String ldmForTab = ldmForTabAndKey[0];
					String ldmForKey = ldmForTabAndKey[1];
					insertRelationStat.setString(8, ldmForTab);
					traceData += "," + ldmForTab;
					traceData += traceDataBuf9;
					insertRelationStat.setString(10, ldmForKey);
					traceData += "," + ldmForKey;
					boolean useFullyConvertedNames = true;
					if (useFullyConvertedNames) {
						insertRelationStat.setString(11, ldmRelation.getSecond());
						traceData += "," + ldmRelation.getSecond();
					} else {
						insertRelationStat.setString(11, getCWSpecificHalfConvertedTabName(sapRelation.getSecond()));
						traceData += "," + getCWSpecificHalfConvertedTabName(sapRelation.getSecond());
					}

					insertRelationStat.setInt(12, primpos);
					traceData += "," + primpos;
					primpos++;

					traceData += traceDataBuf1316;
					Activator.getLogger().log(Level.FINEST, traceData);
					//DEBUG
					Activator.getLogger().log(Level.FINEST, traceData);
					
					insertRelationStat.execute();
				}
			}
		}
	}

	private void clearStatement(PreparedStatement stat, int startIndex, int endIndex) throws SQLException {
		for (int i = startIndex; i <= endIndex; i++) {
			stat.setObject(i, null);
		}
	}

	private String getCWSpecificHalfConvertedTabName(String sapTabName) {
		sapTabName = sapTabName.replaceAll("/", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		return sapTabName;
	}

	static int parseLengthFromDatatypeString(String s) {
		int length = -1;
		String baseType = s.toLowerCase();
		String pfx = "varchar("; //$NON-NLS-1$
		if (baseType.startsWith(pfx)) {
			int ix = baseType.indexOf(")"); //$NON-NLS-1$
			if (ix > 0) {
				String lengthStr = baseType.substring(pfx.length(), ix);
				try {
					length = Integer.parseInt(lengthStr);
				} catch(NumberFormatException exc ) {
					exc.printStackTrace();
					// do nothing else
				}
			}
		}
		return length;
		
	}
	
	/**
	 * publish all fields and add relation from each field to check table if there is one.
	 * @param e
	 * @param monitor
	 * @throws SQLException
	 */
	private void publishFields(Entity e, IProgressMonitor monitor) throws SQLException {

		ResultSet selectTechFieldsRS = null;
		PreparedStatement insertFieldStat = null;
		PreparedStatement insertRelationStat = null;
		try {
			String sapTableName = getSAPTableName(e);
			if (sapTableName == null) {
				sapTableName = getSAPCheckTableName(e);
			} 
			if (sapTableName == null) {
				sapTableName = getSAPTextTableName(e);
			}
			String cwTabname = e.getName();

			String insertFieldStatString = // 					
			"INSERT INTO AUX.SAP_FIELDS_METADATA ( " + // //$NON-NLS-1$
					"             CW_LEGACY_ID " + // 1 //$NON-NLS-1$
					"           , SAP_TABNAME " + // 2 //$NON-NLS-1$
					"           , CW_LOB " + // 3 //$NON-NLS-1$
					"           , CW_RLOUT " + // 4 //$NON-NLS-1$
					"           , CW_TABNAME" + // 5 //$NON-NLS-1$
					"           , SAP_FIELDNAME" + // 6 //$NON-NLS-1$
					"           , CW_FIELDNAME" + // 7 //$NON-NLS-1$
					"           , DOMNAME" + // 8 //$NON-NLS-1$
					"           , SAP_CHECKTABLE" + // 9 //$NON-NLS-1$
					"           , CW_CHECKTABLE" + // 10 //$NON-NLS-1$
					"           , DATATYPE" + // 11 //$NON-NLS-1$
					"           , LENG" + // 12 //$NON-NLS-1$
					"           , DECIMALS" + // 13 //$NON-NLS-1$
					"           , DDTEXT" + // 14 //$NON-NLS-1$
					"           , VALEXI" + // 15 //$NON-NLS-1$
					"           , POSITION" + // 16 //$NON-NLS-1$
					"           , TRANSCODING_TABLE_SOURCE_FIELD" + // 17 //$NON-NLS-1$
					"           , TRANSCODING_TABLE_TARGET_FIELD" + // 18 //$NON-NLS-1$
					"           , CW_LENG" + // 19 //$NON-NLS-1$
					"           , KEYFLAG" + // 20 //$NON-NLS-1$
					"           )" + // //$NON-NLS-1$
					"  VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )"; //$NON-NLS-1$

			insertFieldStat = connection.prepareStatement(insertFieldStatString);
			insertFieldStat.setString(1, this.legacyID);
			insertFieldStat.setString(2, sapTableName);
			insertFieldStat.setString(3, this.objectName);
			insertFieldStat.setString(4, this.rlout);
			insertFieldStat.setString(5, cwTabname);
			
			Activator.getLogger().log(Level.FINEST, "Fields for table: " + sapTableName);
			Activator.getLogger().log(Level.FINEST, insertFieldStatString);
			Activator.getLogger().log(Level.FINEST, this.legacyID + "," + sapTableName + "," + this.objectName + ","
					+ this.rlout + "," + cwTabname);

			insertRelationStat = this.connection.prepareStatement(INSERT_RELATION_STAT);
			insertRelationStat.setString(1, this.legacyID);
			insertRelationStat.setString(2, this.objectName);
			insertRelationStat.setString(3, this.rlout);
			insertRelationStat.setString(4, sapTableName);
			insertRelationStat.setString(5, cwTabname);
			
			Activator.getLogger().log(Level.FINEST, "FK relation: ");
			Activator.getLogger().log(Level.FINEST, INSERT_RELATION_STAT);
			Activator.getLogger().log(Level.FINEST, this.legacyID + "," + this.objectName + ","
					+ this.rlout + "," + sapTableName + "," + cwTabname);

			int pos = 1;
			List<?> attrs = e.getAttributes();
			SubMonitor progress = SubMonitor.convert(monitor, Messages.Model2CWDBExporter_16, 100);
			SubMonitor loopProgress = progress.newChild(100).setWorkRemaining(attrs.size());

			// build map from domainName to lengths
			Map<String, Integer> domainName2Length = new HashMap<String, Integer>();
			LdmAccessor acc = this.entity2LDMMap.get(e);
			List<?> domainList = acc.getRootPackage().getDomainsRecursively();
			for (Object o : domainList) {
				AtomicDomain ad = (AtomicDomain) o;
				int length = parseLengthFromDatatypeString(ad.getBaseType());
				domainName2Length.put(ad.getName(), length);
			}

			PrepStatementBatch insertFieldBatch = new PrepStatementBatch(insertFieldStat);
			Iterator<?> it = attrs.iterator();
			while (it.hasNext()) {
				Object o = it.next();
				Attribute attr = (Attribute) o;
				boolean publishField = shouldFieldBePublished(attr);
				if (publishField) {
					clearStatement(insertFieldStat, 6, 19);
					String traceData = "Field data: ";

					Map<String, String> annotations = LdmAccessor.getAnnotations(attr);
					String cwFieldName = attr.getName();
					String sapColumnName = annotations.get(Constants.ANNOT_SAP_COLUMN_NAME);
					if (sapColumnName == null) {
						messages.add(new Message(MSGTYPE.WARNING, MessageFormat.format(Messages.Model2CWDBExporter_17, cwFieldName)));
						continue;
					}
					// 6 SAP_FIELDNAME
					traceData = sapColumnName;
					insertFieldStat.setString(6, sapColumnName);
					// 7 CW_FIELDNAME
					traceData += "," + cwFieldName;
					insertFieldStat.setString(7, cwFieldName);

					// 8 DOMNAME
					String domain = setSQLParameter(insertFieldStat, 8, Constants.ANNOT_DATATYPE_DOMAIN, annotations);
					traceData += "," + domain;
					String checkTable = annotations.get(Constants.ANNOT_RELATED_CHECKTABLE);
					traceData += "," + checkTable;
					if (checkTable != null) {
						// 9
						insertFieldStat.setString(9, checkTable);
						// 10
						String cwChecktableName = getCWSpecificHalfConvertedTabName(checkTable);
						insertFieldStat.setString(10, cwChecktableName);
						traceData += "," + cwChecktableName;
						this.publishRelation(e, attr, insertRelationStat, loopProgress);
					} else {
						insertFieldStat.setString(9, null);
						traceData += "," + null;
						insertFieldStat.setString(10, null);
					}
					// 11
					String datatype = setSQLParameter(insertFieldStat, 11, Constants.ANNOT_DATATYPE_DATATYPE, annotations);
					traceData += "," + datatype;
					// 12
					int sapLength = Integer.parseInt(annotations.get(Constants.ANNOT_DATATYPE_LENGTH));
					insertFieldStat.setInt(12, sapLength);
					traceData += "," + sapLength;
					// 13
					String decimals = setIntSQLParameter(insertFieldStat, 13, Constants.ANNOT_DATATYPE_DECIMALS, annotations);
					traceData += "," + decimals;
					// 14 DDTEXT
					insertFieldStat.setString(14, attr.getLabel());
					traceData += "," + attr.getLabel();
					// 15
					String valexi = ""; //$NON-NLS-1$
					String dtName = LdmAccessor.getAnnotationValue(attr, Constants.ANNOT_DOMAIN_TABLE);
					if (dtName != null) {
						valexi = "X"; //$NON-NLS-1$
					}
					insertFieldStat.setString(15, valexi);
					traceData += "," + valexi;
					// 16
					insertFieldStat.setInt(16, pos); // TODO read position from SAP
					traceData += "," + pos;
					pos++;

					// 17
					String srcfld = setSQLParameter(insertFieldStat, 17, Constants.ANNOT_TRANSCODING_TBL_SRC_FLD, annotations);
					traceData += "," + srcfld;
					// 18
					String trgfld = setSQLParameter(insertFieldStat, 18, Constants.ANNOT_TRANSCODING_TBL_TRG_FLD, annotations);
					traceData += "," + trgfld;
					// 19
					String dt = attr.getDataType();
					int cwLength = -1;
					Integer cwLengthObj = domainName2Length.get(dt);
					if (cwLengthObj != null) {
						cwLength = cwLengthObj;
					}
					if (cwLength == -1) {
						cwLength = parseLengthFromDatatypeString(dt);
						if (cwLength == -1) {
							cwLength = sapLength;
						}
					}
					
					insertFieldStat.setInt(19, cwLength);
					traceData += "," + cwLength;
					
					String isKeyInSAP = annotations.get(Constants.ANNOT_IS_KEY_IN_SAP);
					if (isKeyInSAP != null) {
						boolean b = Boolean.parseBoolean(isKeyInSAP);
						insertFieldStat.setString(20, b ? "X" : null); //$NON-NLS-1$
						traceData += "," + (b ? "X" : null);
					}
					Activator.getLogger().log(Level.FINEST, traceData);
					insertFieldBatch.execute();
				}
				loopProgress.newChild(1); // worked(1);
				if (loopProgress.isCanceled()) {
					return;
				}
			}
			insertFieldBatch.finish();
		} finally {
			if (selectTechFieldsRS != null) {
				selectTechFieldsRS.close();
				selectTechFieldsRS = null;
			}
			if (insertFieldStat != null) {
				insertFieldStat.close();
				insertFieldStat = null;
			}
			if (insertRelationStat != null) {
				insertRelationStat.close();
				insertRelationStat = null;
			}
		}
	}

	private String setSQLParameter(PreparedStatement stat, int index, String annotationName, Map<String, String> annotations) throws SQLException {
		String s = annotations.get(annotationName);
		stat.setString(index, s);
		return s;
	}

	private String setIntSQLParameter(PreparedStatement stat, int index, String annotationName, Map<String, String> annotations) throws SQLException {
		String s = annotations.get(annotationName);
		if (s == null) {
		} else {
			stat.setInt(index, Integer.parseInt(s));
		}
		return s;
	}

	private boolean shouldFieldBePublished(Attribute attr) {
		String s = LdmAccessor.getAnnotationValue(attr, Constants.ANNOT_DATA_OBJECT_SOURCE);
		return Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE.equals(s) || Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_CHECK_TABLE.equals(s) || Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_TEXT_TABLE.equals(s);
	}
	
	private boolean shouldCheckTableBePublished(Entity e) {
		for (Entity dt : this.dataTables) {
			if (getSAPCheckTableName(e).equals(getSAPTableName(dt))) {
				Activator.getLogger().log(Level.FINEST, "Check table exists as data table, skipping");
				return false;
			}
		}
		return true;
	}

}
