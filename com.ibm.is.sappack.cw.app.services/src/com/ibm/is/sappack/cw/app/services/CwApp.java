package com.ibm.is.sappack.cw.app.services;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.ws.rs.core.Application;

import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

import com.ibm.is.sappack.cw.app.services.bdr.jaxrs.BdrExportService;
import com.ibm.is.sappack.cw.app.services.bdr.jaxrs.BphImportFileService;
import com.ibm.is.sappack.cw.app.services.bdr.jaxrs.BphImportService;
import com.ibm.is.sappack.cw.app.services.bdr.jaxrs.BphService;
import com.ibm.is.sappack.cw.app.services.bdr.jaxrs.BusinessObjectService;
import com.ibm.is.sappack.cw.app.services.bdr.jaxrs.ExportToCwdbService;
import com.ibm.is.sappack.cw.app.services.bdr.jaxrs.FieldImportService;
import com.ibm.is.sappack.cw.app.services.bdr.jaxrs.FieldService;
import com.ibm.is.sappack.cw.app.services.bdr.jaxrs.FieldUsageService;
import com.ibm.is.sappack.cw.app.services.bdr.jaxrs.ObjectAccessService;
import com.ibm.is.sappack.cw.app.services.bdr.jaxrs.TableImportService;
import com.ibm.is.sappack.cw.app.services.bdr.jaxrs.TableService;
import com.ibm.is.sappack.cw.app.services.bdr.jaxrs.TableUsageService;
import com.ibm.is.sappack.cw.app.services.config.jaxrs.LegacySystemService;
import com.ibm.is.sappack.cw.app.services.config.jaxrs.SAPConnectionsExporter;
import com.ibm.is.sappack.cw.app.services.config.jaxrs.SapConnectionTestService;
import com.ibm.is.sappack.cw.app.services.config.jaxrs.SapPasswordService;
import com.ibm.is.sappack.cw.app.services.config.jaxrs.SettingService;
import com.ibm.is.sappack.cw.app.services.config.jaxrs.TargetSapSystemService;
import com.ibm.is.sappack.cw.app.services.jaxrs.BuildNumberService;
import com.ibm.is.sappack.cw.app.services.jaxrs.ConfigCheckService;
import com.ibm.is.sappack.cw.app.services.jaxrs.SessionService;
import com.ibm.is.sappack.cw.app.services.rdm.debug.BDRTableData;
import com.ibm.is.sappack.cw.app.services.rdm.debug.BDRTreeData;
import com.ibm.is.sappack.cw.app.services.rdm.debug.RdmCleanupService;
import com.ibm.is.sappack.cw.app.services.rdm.debug.RdmMappingsCreator;
import com.ibm.is.sappack.cw.app.services.rdm.jaxrs.LoadService;
import com.ibm.is.sappack.cw.app.services.rdm.jaxrs.RdmConnectionTestService;
import com.ibm.is.sappack.cw.app.services.rdm.jaxrs.RdmExportService;
import com.ibm.is.sappack.cw.app.services.rdm.jaxrs.RdmMappingsCreationService;
import com.ibm.is.sappack.cw.app.services.rdm.jaxrs.RdmMappingsService;
import com.ibm.is.sappack.cw.app.services.rdm.jaxrs.RdmPasswordService;
import com.ibm.is.sappack.cw.app.services.rdm.jaxrs.ReferenceTableService;
import com.ibm.is.sappack.cw.app.services.rdm.jaxrs.SourceReferenceDataService;

public class CwApp extends Application {

	private static volatile Logger logger = null;
	private static String referenceTableSchema = null;
	private static String transcodingTableSchema = null;
	private static String dataTableSchema = null;
	
	public CwApp() {
		try {
			CWDBConnectionFactory.getConnection();
			getReferenceTableSchema();
			getTranscodingTableSchema();
			getDataTableSchema();
		} catch (NamingException ne) {
			ne.printStackTrace();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
	
	public static Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(Constants.LOGGER_ID);
		}
		return logger;
	}

	// Gets the schema name for reference and text tables from CWDB
	public static String getReferenceTableSchema() {
		if (referenceTableSchema == null) {
			referenceTableSchema = com.ibm.is.sappack.cw.app.services.config.DBOperations.getCwSchemaFromCWDB(Constants.CW_SCHEMAS_AREACODE_PLD);
		}
		return referenceTableSchema;
	}

	// Gets the schema name for transcoding tables from CWDB
	public static String getTranscodingTableSchema() {
		if (transcodingTableSchema == null) {
			transcodingTableSchema = com.ibm.is.sappack.cw.app.services.config.DBOperations.getCwSchemaFromCWDB(Constants.CW_SCHEMAS_AREACODE_ALG0);
		}
		return transcodingTableSchema;
	}
	
	// Gets the schema name for data tables from CWDB
	public static String getDataTableSchema() {
		if (dataTableSchema == null) {
			dataTableSchema = com.ibm.is.sappack.cw.app.services.config.DBOperations.getCwSchemaFromCWDB(Constants.CW_SCHEMAS_AREACODE_ALG0);
		}
		return dataTableSchema;
	}

	@Override
	public Set<Object> getSingletons() {
		Set<Object> s = new HashSet<Object>();
		ObjectMapper mapper = new ObjectMapper();
		AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
		AnnotationIntrospector secondary = new JaxbAnnotationIntrospector();
		AnnotationIntrospector pair = new AnnotationIntrospector.Pair(primary, secondary);
		mapper.getDeserializationConfig().setAnnotationIntrospector(pair);
		mapper.getSerializationConfig().setAnnotationIntrospector(pair);
		JacksonJaxbJsonProvider jaxbProvider = new JacksonJaxbJsonProvider();

		jaxbProvider.setMapper(mapper);
		s.add(jaxbProvider);
		return s;
	}

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		
		// Common
		classes.add(SessionService.class);
		classes.add(BuildNumberService.class);
		classes.add(ConfigCheckService.class);
		
		// Config
		classes.add(SettingService.class);
		classes.add(LegacySystemService.class);
		classes.add(TargetSapSystemService.class);
		classes.add(SapPasswordService.class);
		classes.add(SapConnectionTestService.class);
		classes.add(SAPConnectionsExporter.class);
		
		// RDM
		classes.add(RdmMappingsService.class);
		classes.add(RdmPasswordService.class);
		classes.add(RdmConnectionTestService.class);
		classes.add(ReferenceTableService.class);
		classes.add(RdmExportService.class);
		classes.add(RdmCleanupService.class);
		classes.add(RdmMappingsCreator.class);
		classes.add(SourceReferenceDataService.class);
		classes.add(RdmMappingsCreationService.class);
		classes.add(LoadService.class);
		
		// BDR
		classes.add(BphService.class);
		classes.add(TableService.class);
		classes.add(TableUsageService.class);
		classes.add(TableImportService.class);
		classes.add(FieldService.class);
		classes.add(FieldImportService.class);
		classes.add(FieldUsageService.class);
		classes.add(BusinessObjectService.class);
		classes.add(BDRTableData.class);
		classes.add(BDRTreeData.class);
		classes.add(ObjectAccessService.class);
		classes.add(BdrExportService.class);
		classes.add(BphImportFileService.class);
		classes.add(BphImportService.class);
		classes.add(ExportToCwdbService.class);
		
		return classes;
	}
}
