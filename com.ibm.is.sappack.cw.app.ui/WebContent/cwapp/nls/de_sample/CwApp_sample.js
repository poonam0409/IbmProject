define({

		// main messages
		
		MAIN_1 : "IBM InfoSphere Conversion Workbench Application",
		MAIN_2 : "Willkommen,",
		MAIN_3 : "Hilfe",
		MAIN_4 : "Online Hilfe",
		MAIN_5 : "IBM Support Portal",
		MAIN_6 : "Einstieg",
		MAIN_7 : "Reference Data Management",
		MAIN_8 : "Konfiguration",
		MAIN_9 : "BDR",

		// generic messages
		
		OK             : "OK", // OK buttons
		STATUS_OK      : "OK", // OK status messages
		CANCEL         : "Cancel",
		FINISH         : "Finish",
		NEXT           : "Next &gt",
		BACK           : "&lt Back",
		SELECT_ALL     : "Select All",
		DESELECT_ALL   : "Deselect All",
		REFRESH        : "Refresh",
		COLON          : ":",
		NAME           : "Name",
		DESCRIPTION    : "Description",
		CW_SYSTEM_ID   : "CW System ID",
		SYSTEM_ID      : "System ID",
		TABLE          : "Table",
		SCHEMA         : "Schema",
		INTERNAL_ERROR : "Internal error",
		
		// main CwApp messages

		CWAPP_1 : "Reference Data Management",
		CWAPP_2 : "Target Reference Data",
		CWAPP_3 : "Source Reference Data",
		CWAPP_4 : "Data Mappings",
		CWAPP_5 : "Target SAP System",
		CWAPP_6 : "RDM Settings",
		CWAPP_7 : "The RDM connection parameters are incomplete. Please use the Configuration tab to configure the RDM connection.",
		CWAPP_8 : "Configuration",
		CWAPP_9 : "Source Systems",
		CWAPP_10 : "BDR",

		// DataMappingsPage messages

		DATAMAPNGSPG_1 : "Data Mappings",
		DATAMAPNGSPG_2 : "Import",
		DATAMAPNGSPG_6 : "Transcoding Table",
		DATAMAPNGSPG_7 : "Mapping",
		DATAMAPNGSPG_8 : "RDM Status",
		DATAMAPNGSPG_9 : "Latest Version",
		DATAMAPNGSPG_10 : "Loaded Version",
		DATAMAPNGSPG_11 : "Source Set System ID",
		DATAMAPNGSPG_12 : "Loaded",
		DATAMAPNGSPG_13 : "Value Mappings",
		DATAMAPNGSPG_14 : "Source Values",
		DATAMAPNGSPG_15 : "Nothing selected",
		DATAMAPNGSPG_16 : "One or more of the mappings,<br> loaded into this transcoding table <br> are missing",
		DATAMAPNGSPG_17 : "The Transcoding Table is missing",
		DATAMAPNGSPG_18 : "Conflict",
		DATAMAPNGSPG_19 : "New",
		DATAMAPNGSPG_20 : "Loaded",
		DATAMAPNGSPG_21 : "Invalid",
		DATAMAPNGSPG_22 : "Removed",
		DATAMAPNGSPG_23 : "{0} rows",
		DATAMAPNGSPG_24 : "{0} rows, {1} rows selected",
		DATAMAPNGSPG_25 : "RDM parameter missing",
		DATAMAPNGSPG_26 : "No schema is set",
		DATAMAPNGSPG_27 : "Unauthorized access",
		DATAMAPNGSPG_28 : "Conflict with: <br>",
		DATAMAPNGSPG_29 : "\n (same data sets)",
		DATAMAPNGSPG_30 : "Potential Conflict with: <br>",
		DATAMAPNGSPG_31 : "\n (only one of these mappings can be loaded)",
		DATAMAPNGSPG_32 : "Your selection contains conflicting mappings that cannot be loaded together.<br>"
				+ " Please choose one of the conflicting mappings",
		DATAMAPNGSPG_33 : "Internal server error",
		DATAMAPNGSPG_34 : "RDM System does not respond. Please check the url.",

		// ReferenceDataImportWizard messages

		REFDATAIMPWIZ_1 : "Load Reference Data",

		// SourceSystemsPage messages

		SOURCESYSTEMSPG_1 : "Source Systems",
		SOURCESYSTEMSPG_2 : "Details",
		SOURCESYSTEMSPG_3 : "Add",
		SOURCESYSTEMSPG_4 : "Delete",
		SOURCESYSTEMSPG_7 : "Revert changes",
		SOURCESYSTEMSPG_8 : "Save",
		SOURCESYSTEMSPG_9 : "Test connection",
		SOURCESYSTEMSPG_13 : "Use load balancing",
		SOURCESYSTEMSPG_14 : "System number:",
		SOURCESYSTEMSPG_15 : "Router string:",
		SOURCESYSTEMSPG_16 : "User:",
		SOURCESYSTEMSPG_17 : "Password:",
		SOURCESYSTEMSPG_18 : "(only saved for the current session)",
		SOURCESYSTEMSPG_19 : "Client:",
		SOURCESYSTEMSPG_20 : "Logon language:",
		SOURCESYSTEMSPG_21 : "Message server:",
		SOURCESYSTEMSPG_23 : "Group name:",
		SOURCESYSTEMSPG_24 : "Hostname:",
		SOURCESYSTEMSPG_25 : "SAP connection test succeeded.",
		SOURCESYSTEMSPG_26 : "SAP connection test failed due to an incorrect login.",
		SOURCESYSTEMSPG_27 : "SAP connection test failed due to an internal error.",
		SOURCESYSTEMSPG_28 : "SAP connection test failed due to a connection error.",

		// TargetSapSystemPage messages

		TARGETSAPSYSTEMPG_1 : "Target SAP System",
		TARGETSAPSYSTEMPG_7 : "Revert changes",
		TARGETSAPSYSTEMPG_8 : "Save",
		TARGETSAPSYSTEMPG_9 : "Test connection",
		TARGETSAPSYSTEMPG_13 : "Use load balancing",
		TARGETSAPSYSTEMPG_14 : "System number:",
		TARGETSAPSYSTEMPG_15 : "Router string:",
		TARGETSAPSYSTEMPG_16 : "User:",
		TARGETSAPSYSTEMPG_17 : "Password:",
		TARGETSAPSYSTEMPG_18 : "(only saved for the current session)",
		TARGETSAPSYSTEMPG_19 : "Client:",
		TARGETSAPSYSTEMPG_20 : "Logon language:",
		TARGETSAPSYSTEMPG_21 : "Message server:",
		TARGETSAPSYSTEMPG_23 : "Group name:",
		TARGETSAPSYSTEMPG_24 : "Hostname:",
		TARGETSAPSYSTEMPG_25 : "SAP connection test succeeded.",
		TARGETSAPSYSTEMPG_26 : "SAP connection test failed due to an incorrect login.",
		TARGETSAPSYSTEMPG_27 : "SAP connection test failed due to an internal error.",
		TARGETSAPSYSTEMPG_28 : "SAP connection test failed due to a connection error.",

		// RdmSettingsPage messages

		RDMSETTINGSPG_1 : "RDM Settings",
		RDMSETTINGSPG_2 : "Revert changes",
		RDMSETTINGSPG_3 : "Save",
		RDMSETTINGSPG_4 : "Test connection",
		RDMSETTINGSPG_5 : "RDM host name:",
		RDMSETTINGSPG_6 : "RDM application HTTP port:",
		RDMSETTINGSPG_7 : "RDM user:",
		RDMSETTINGSPG_8 : "RDM password:",
		RDMSETTINGSPG_9 : "(only saved for the current session)",
		RDMSETTINGSPG_10 : "Language for descriptions:",
		RDMSETTINGSPG_11 : "Alignment area schema:",
		RDMSETTINGSPG_12 : "RDM connection test succeeded.",
		RDMSETTINGSPG_13 : "RDM connection test failed due to an incorrect login.",
		RDMSETTINGSPG_14 : "RDM connection test failed due to an internal error.",
		RDMSETTINGSPG_15 : "RDM connection test failed with status: ",

		// SourceReferenceDataPage messages

		SRCREFRNCDATAPG_1 : "Source Reference Data",
		SRCREFRNCDATAPG_4 : "Export Source Data to RDM",
		SRCREFRNCDATAPG_5 : "Create Initial Mappings",
		SRCREFRNCDATAPG_7 : "Source System ID",
		SRCREFRNCDATAPG_8 : "Target Reference Data Table",
		SRCREFRNCDATAPG_11 : "Source RDM Set",
		SRCREFRNCDATAPG_12 : "Matching Target RDM Set",
		SRCREFRNCDATAPG_13 : "RDM Mapping",
		SRCREFRNCDATAPG_14 : "{0} tables",
		SRCREFRNCDATAPG_15 : "{0} tables, {1} tables selected",

		// TargetReferenceDataPage messages

		TGTREFRNCDATAPG_1 : "Target Reference Data",
		TGTREFRNCDATAPG_4 : "Load New",
		TGTREFRNCDATAPG_5 : "Reload",
		TGTREFRNCDATAPG_6 : "Export to RDM",
		TGTREFRNCDATAPG_7 : "Erase",
		TGTREFRNCDATAPG_9 : "Reference Data Table",
		TGTREFRNCDATAPG_11 : "Rows",
		TGTREFRNCDATAPG_13 : "Loaded",
		TGTREFRNCDATAPG_14 : "From SAP System",
		TGTREFRNCDATAPG_15 : "Target RDM Set",
		TGTREFRNCDATAPG_16 : "{0} tables",
		TGTREFRNCDATAPG_17 : "{0} tables, {1} tables selected",
		TGTREFRNCDATAPG_18 : "The RDM Set is not up to date",

		// DataModelUploadPage messages

		DATAMDLUPLOADPG_1 : "Upload a Rapid Modeler physical model file for the Preload Area (.dbm)",
		DATAMDLUPLOADPG_2 : "Browse...",
		DATAMDLUPLOADPG_3 : "Uploading Data Model...",
		DATAMDLUPLOADPG_4 : "The data model upload was aborted.",
		DATAMDLUPLOADPG_5 : "The data model upload did not finish due to an unknown error.",
		DATAMDLUPLOADPG_6 : "The data model was not uploaded.",
		DATAMDLUPLOADPG_7 : "The data model is not valid.",
		DATAMDLUPLOADPG_8 : "The data model upload did not finish due to an internal error.",
		DATAMDLUPLOADPG_9 : "The selected data model is too large, the maximum allowed size is 80MB.",

		// ReferenceDataImportSummaryPage messages

		REFRNCDATAIMPSUMPG_1 : "Reference Data Import Summary",
		REFRNCDATAIMPSUMPG_2 : "SAP System selected for import: {0}",
		REFRNCDATAIMPSUMPG_6 : "Text Table",
		REFRNCDATAIMPSUMPG_7 : "The table exists and contains data",
		REFRNCDATAIMPSUMPG_8 : "The table exists and is empty",
		REFRNCDATAIMPSUMPG_9 : "The table does not exist",

		// ReferenceDataTableSelectionPage messages

		REFRNCDATATBLSELPG_1 : "Select Reference Tables",
		REFRNCDATATBLSELPG_7 : "Text Table",
		REFRNCDATATBLSELPG_8 : "The table exists and contains data",
		REFRNCDATATBLSELPG_9 : "The table exists and is empty",
		REFRNCDATATBLSELPG_10 : "The table does not exist",
		REFRNCDATATBLSELPG_11 : "{0} tables are OK, {1} tables have warnings, {2} tables have errors",
		REFRNCDATATBLSELPG_12 : "All tables are OK",

		// SapSystemSelectionPage messages

		SAPSYSSELPG_1 : "Select Source SAP System",
		SAPSYSSELPG_3 : "Host",
		SAPSYSSELPG_5 : "You have not configured any SAP systems yet.",

		// AbstractProgressDialog messages
		
		ABSTRACTPROGDLG_1 : "Internal error on server",
		ABSTRACTPROGDLG_2 : "Server timeout",
		ABSTRACTPROGDLG_3 : "No connection to the server. Please check your internet connection",
		ABSTRACTPROGDLG_4 : "Wrong password or username",

		// DataMappingImportProgressDialog messages

		DATAMAPNGIMPPROGDLG_1 : "Data Mapping Import",
		DATAMAPNGIMPPROGDLG_2 : "Transcoding Table",
		DATAMAPNGIMPPROGDLG_3 : "Mappings",
		DATAMAPNGIMPPROGDLG_4 : "Mapped Source Values",
		DATAMAPNGIMPPROGDLG_5 : "Source Values",
		DATAMAPNGIMPPROGDLG_6 : "Load Status",
		DATAMAPNGIMPPROGDLG_7 : "Import Data Mappings",
		DATAMAPNGIMPPROGDLG_8 : "Import finished",
		DATAMAPNGIMPPROGDLG_10 : "Duplicate relation",
		DATAMAPNGIMPPROGDLG_11 : "Invalid mapping",
		DATAMAPNGIMPPROGDLG_12 : "Database error",
		DATAMAPNGIMPPROGDLG_13 : "Conflict mapping",
		DATAMAPNGIMPPROGDLG_14 : "No transcoding table",
		DATAMAPNGIMPPROGDLG_15 : "No mappings for this transcoding table",
		DATAMAPNGIMPPROGDLG_16 : "Overall progress:",
		DATAMAPNGIMPPROGDLG_17 : "Table progress:",
		DATAMAPNGIMPPROGDLG_21 : "Importing...",
		DATAMAPNGIMPPROGDLG_22 : "Cancelled",
		DATAMAPNGIMPPROGDLG_23 : "RDM Login Error",

		// DataMappingsCreationProgressDialog messages

		DATAMAPNGSCRTPROGDLG_1 : "Data Mappings Creation",
		DATAMAPNGSCRTPROGDLG_2 : "Creating Initial Data Mappings",
		DATAMAPNGSCRTPROGDLG_3 : "Created Initial Data Mappings",
		DATAMAPNGSCRTPROGDLG_6 : "RDM login error",
		DATAMAPNGSCRTPROGDLG_7 : "Overall progress:",
		DATAMAPNGSCRTPROGDLG_10 : "Mapping Name",
		DATAMAPNGSCRTPROGDLG_11 : "Target Set",
		DATAMAPNGSCRTPROGDLG_12 : "Source Set",
		DATAMAPNGSCRTPROGDLG_13 : "Status",
		DATAMAPNGSCRTPROGDLG_14 : "Creating",
		DATAMAPNGSCRTPROGDLG_15 : "Set is Missing in CW",
		DATAMAPNGSCRTPROGDLG_16 : "Conflict with another Mapping",
		DATAMAPNGSCRTPROGDLG_17 : "Mapping Exists",
		DATAMAPNGSCRTPROGDLG_18 : "Set is Missing in RDM",

		// RdmPasswordDialog messages

		RDMPASSWDDLG_1 : "Password",
		RDMPASSWDDLG_4 : "Please enter the RDM password for user {0}:",
		RDMPASSWDDLG_5 : "The password you entered is incorrect. Please try again.",
		RDMPASSWDDLG_6 : "RDM does not respond. Please check the url.",
		RDMPASSWDDLG_7 : "Internal server error",

		// ReferenceDataEraseDialog messages
		
		REFRNCDATAERSDLG_1 : "Erase Reference Data",
		REFRNCDATAERSDLG_2 : "Are you sure you want to erase the content of {0} Reference Data Tables?<br><br>Export and import functionality on related entities will become unavailable until the tables are loaded again.",
		REFRNCDATAERSDLG_3 : "Erasing Reference Data Table content...",
		REFRNCDATAERSDLG_4 : "The Reference Data Table content has been erased",
		REFRNCDATAERSDLG_5 : "Erasing the Reference Data Table content failed",
		REFRNCDATAERSDLG_6 : "Erase",

		// ReferenceDataExportProgressDialog messages
		
		REFRNCDATAEXPPROGDLG_1 : "Reference Data Export",
		REFRNCDATAEXPPROGDLG_2 : "Exporting reference data to RDM...",
		REFRNCDATAEXPPROGDLG_3 : "Done.",
		REFRNCDATAEXPPROGDLG_6 : "Rows",
		REFRNCDATAEXPPROGDLG_7 : "Export Status",
		REFRNCDATAEXPPROGDLG_10 : "Table not found",
		REFRNCDATAEXPPROGDLG_11 : "Exporting...",
		REFRNCDATAEXPPROGDLG_12 : "Overall progress:",
		REFRNCDATAEXPPROGDLG_13 : "Table progress:",
		REFRNCDATAEXPPROGDLG_16 : "No changes",

		// ReferenceDataImportProgressDialog messages

		REFRNCDATAIMPPROGDLG_1 : "Reference Data Load",
		REFRNCDATAIMPPROGDLG_2 : "Loading reference data from SAP system: {0}",
		REFRNCDATAIMPPROGDLG_3 : "Loading reference data from multipe SAP systems: {0}",
		REFRNCDATAIMPPROGDLG_4 : "Loading reference data from SAP system: {0} has finished",
		REFRNCDATAIMPPROGDLG_5 : "Loading reference data from SAP systems: {0} has finished",
		REFRNCDATAIMPPROGDLG_8 : "Text Table",
		REFRNCDATAIMPPROGDLG_9 : "Rows",
		REFRNCDATAIMPPROGDLG_10 : "Load Status",
		REFRNCDATAIMPPROGDLG_11 : "Loading...",
		REFRNCDATAIMPPROGDLG_13 : "Failed",
		REFRNCDATAIMPPROGDLG_15 : "Overall progress:",
		REFRNCDATAIMPPROGDLG_16 : "Table progress:",
		REFRNCDATAIMPPROGDLG_19 : "No Changes",

		// SapSystemPasswordDialog messages

		SAPPWDDLG_1 : "Password",
		SAPPWDDLG_2 : "Please enter the password:",
		SAPPWDDLG_3 : "Password:",
		SAPPWDDLG_4 : "SAP System:",
		SAPPWDDLG_5 : "User:",
		SAPPWDDLG_6 : "The password you entered is incorrect. Please try again.",
		SAPPWDDLG_7 : "The SAP password dialog has been cancelled.",
		SAPPWDDLG_10 : "Internal error on server",
		SAPPWDDLG_11 : "SAP system unreachable. Please check the SAP system url.",
		
		// BDR messages

		BDR_1 : "BPH",
		BDR_2 : "BO",
		BDR_3 : "Tables",
		
		// BPH messages

		BPHTAB_1 : "add",
		BPHTAB_2 : "remove",
		BPHTAB_3 : "import",
		BPHTAB_4 : "export",
		BPHTAB_5 : "Filter...",

		// AboutDialog messages
		
		ABOUTDLG_1 : "About",
		ABOUTDLG_2 : "About IBM InfoSphere Conversion Workbench Application",
		ABOUTDLG_3 : "Licensed Materials - Property of IBM.  5725-E59  &copy; Copyright 1998, 2012 IBM Corporation.  IBM, the IBM logo, and InfoSphere are trademarks of IBM Corporation, registered in many jurisdictions worldwide.  Java and all Java-based trademarks and logos are trademarks or registered trademarks of Oracle and/or its affiliates.  Other product and service names might be trademarks of IBM or other companies.  This Program is licensed under the terms of the license agreement accompanying the Program.  This license agreement may be either located in a Program directory folder or library identified as \"License\" or \"Non_IBM_License\", if applicable, or provided as a printed license agreement. Please read this agreement carefully before using the Program. By using the Program, you agree to these terms.",

		last : ""
});