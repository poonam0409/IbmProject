define({

	root : {
		
		// main messages
		
		MAIN_1 : "IBM InfoSphere Conversion Workbench Application",
		MAIN_2 : "Welcome,",
		MAIN_3 : "Help",
		MAIN_4 : "Online Help",
		MAIN_5 : "IBM Support Portal",
		MAIN_7 : "Reference Data Load",
		MAIN_8 : "Configuration",
		MAIN_9 : "Business Data Roadmap",
		CWAPP_2 : "Target Reference Data",
		CWAPP_3 : "Source Reference Data",
		CWAPP_4 : "Data Mappings",
		CWAPP_5 : "Target SAP Systems",
		CWAPP_6 : "Settings",
		CWAPP_7 : "Values for one or more RDM Hub connection parameters are not specified. Specify parameter values on the Configuration page.",
		CWAPP_8 : "Configuration",
		CWAPP_9 : "Source Systems",
		CWAPP_11 : "Please save your work before the session times out.<BR> You will be logged out at {0}.",

		// generic messages
		
		OK             	: "OK", // OK buttons
		STATUS_OK      	: "OK", // OK status messages
		CANCEL        	: "Cancel",
		FINISH        	: "Finish",
		NEXT           	: "Next &gt",
		BACK           	: "&lt Back",
		SELECT_ALL     	: "Select All",
		DESELECT_ALL   	: "Deselect All",
		REFRESH        	: "Refresh",
		COLON          	: ":",
		NAME           	: "Name",
		SHORT_NAME     	: "Object Conversion ID",
		DESCRIPTION    	: "Description",
		CW_SYSTEM_ID   	: "CW system ID",
		SYSTEM_ID      	: "System ID",
		HOST            : "Host name",
		TABLE          	: "Table",
		SCHEMA         	: "Schema",
		TYPE		   	: "Type",
		SAVE		   	: "Save",
		DISCARD_CHANGES	: "Discard Changes",
		TEST_CONNECTION_BUTTON : "Test Connection",
		FILTER          : "Filter...",
		LOB             : "LOB",
		ROLLOUT         : "Rollout",
		EXPORT          : "Export",
		IMPORT		: "Import",
		ADD		: "Add",
		LOG_OUT         : "Log out",
		STATUS          : "Status",
		FIELDS_ADDED		: "Fields added",
		
		CHECK_TABLE_TYPE: "Check table",
		DOMAIN_TABLE_TYPE: "Domain table",
		GENERIC_TABLE_TYPE: "Unspecified",
		CHOOSE_OR_TYPE_PLACEHOLDER : "Choose or type...",
		PASSWORD_ONLY_SAVED_FOR_SESSION : "The password is only saved for the current session.",
		PASSWORD_NOT_SAVED : "The password will not be saved.",
		CANCELLED: "Cancelled by the user.",

		// Error messages
		
		LOGIN_DB_SETUP_ERROR_TITLE : "Configuration Error",
		LOGIN_DB_SETUP_ERROR : "The Conversion Workbench database<br>has not been set up properly.<br>This application cannot be used right now.",
		INTERNAL_ERROR 	: "The request could not be processed. Try again.<br>If the problem persists, contact IBM Support.",
		NOT_FOUND_ERROR : "No connection to the server. Please check your network connection.",
		ERROR_CODE      : "Error code: ",

		// **************************************************************************************
		
		//    Configuration

		// **************************************************************************************
		
		// SourceSystemsPage messages

		SOURCESYSTEMSPG_4 : "Delete",
		SOURCESYSTEMSPG_13 : "Use load balancing",
		SOURCESYSTEMSPG_14 : "System number:",
		SOURCESYSTEMSPG_15 : "Router string:",
		SOURCESYSTEMSPG_16 : "User:",
		SOURCESYSTEMSPG_17 : "Password:",
		SOURCESYSTEMSPG_19 : "Client:",
		SOURCESYSTEMSPG_20 : "Language:",
		SOURCESYSTEMSPG_21 : "Message server:",
		SOURCESYSTEMSPG_23 : "Group name:",
		SOURCESYSTEMSPG_24 : "Host name:",
		SOURCESYSTEMSPG_25 : "SAP connection test successful.",
		SOURCESYSTEMSPG_26 : "SAP connection test failed: incorrect user credentials.",
		SOURCESYSTEMSPG_27 : "SAP connection test failed: internal error.",
		SOURCESYSTEMSPG_28 : "SAP connection test failed: connection error.",
		SOURCESYSTEMSPG_29 : "Add SAP system",
		SOURCESYSTEMSPG_30 : "Add non-SAP system",
		SOURCESYSTEMSPG_31 : "The source system is an SAP system",
		SOURCESYSTEMSPG_32 : "The source system is not an SAP system",
		SOURCESYSTEMSPG_36 : "Export for InfoSphere DataStage",
		SOURCESYSTEMSPG_37 : "A system with the same ID exists",
		SOURCESYSTEMSPG_38 : "You have not configured any source systems.",
		SOURCESYSTEMSPG_39 : "A system with the same name exists",
		SOURCESYSTEMSPG_40 : "Connection test in progress...",
		SOURCESYSTEMSPG_41 : "SAP",

		// TargetSapSystemsPage messages

		TARGETSAPSYSTEMSPG_4 : "Delete",
		TARGETSAPSYSTEMSPG_13 : "Use load balancing",
		TARGETSAPSYSTEMSPG_14 : "System number:",
		TARGETSAPSYSTEMSPG_15 : "Router string:",
		TARGETSAPSYSTEMSPG_16 : "User:",
		TARGETSAPSYSTEMSPG_17 : "Password:",
		TARGETSAPSYSTEMSPG_19 : "Client:",
		TARGETSAPSYSTEMSPG_20 : "Language:",
		TARGETSAPSYSTEMSPG_21 : "Message server:",
		TARGETSAPSYSTEMSPG_23 : "Group name:",
		TARGETSAPSYSTEMSPG_24 : "Host name:",
		TARGETSAPSYSTEMSPG_25 : "SAP connection test successful.",
		TARGETSAPSYSTEMSPG_26 : "SAP connection test failed: incorrect user credentials.",
		TARGETSAPSYSTEMSPG_27 : "SAP connection test failed: internal error.",
		TARGETSAPSYSTEMSPG_28 : "SAP connection test failed: connection error.",
		TARGETSAPSYSTEMSPG_32 : "Export for InfoSphere DataStage",
		TARGETSAPSYSTEMSPG_33 : "A system with the same ID exists",
		TARGETSAPSYSTEMSPG_34 : "You have not configured any target SAP systems.",
		TARGETSAPSYSTEMSPG_35 : "A system with the same name already exists",
		TARGETSAPSYSTEMSPG_36 : "Connection test in progress...",
		TARGETSAPSYSTEMSPG_37 : "Not related to any SAP Views",

		// RdmSettingsPage messages

		ConfigSettings_GeneralSettings : "General Settings",
		ConfigSettings_RdmHubSettings : "RDM Hub Settings",
		RDMSETTINGSPG_5 : "Host name:",
		RDMSETTINGSPG_6 : "Application HTTP port:",
		RDMSETTINGSPG_7 : "User:",
		RDMSETTINGSPG_8 : "Password:",
		RDMSETTINGSPG_10 : "Language for descriptions:",
		RDMSETTINGSPG_11 : "Test RDM Hub Connection",
		RDMSETTINGSPG_12 : "RDM Hub connection test successful.",
		RDMSETTINGSPG_13 : "RDM Hub connection test failed: incorrect user credentials.",
		RDMSETTINGSPG_14 : "RDM Hub connection test failed: internal error.",
		RDMSETTINGSPG_15 : "RDM Hub connection test failed.",
		RDMSETTINGSPG_16 : "Test connection in progress...",
		
		// **************************************************************************************
		
		//    RDM

		// **************************************************************************************

		RDMPageEmpty : "No target reference tables have been loaded yet.",
		
		// TargetReferenceDataPage messages

		TGTREFRNCDATAPG_4 : "Load New...",
		TGTREFRNCDATAPG_5 : "Reload",
		TGTREFRNCDATAPG_6 : "Export to RDM Hub",
		TGTREFRNCDATAPG_7 : "Delete",
		TGTREFRNCDATAPG_9 : "Reference table",
		TGTREFRNCDATAPG_11 : "Rows",
		TGTREFRNCDATAPG_13 : "Loaded",
		TGTREFRNCDATAPG_14 : "Target SAP system",
		TGTREFRNCDATAPG_15 : "Target reference data set",
		TGTREFRNCDATAPG_16 : "{0} tables",
		TGTREFRNCDATAPG_17 : "{0} tables, {1} tables selected",
		TGTREFRNCDATAPG_18 : "The reference data set in RDM Hub is not current.",

		// SourceReferenceDataPage messages

		SRCREFRNCDATAPG_4 : "Export to RDM Hub",
		SRCREFRNCDATAPG_5 : "Create Initial Mappings",
		SRCREFRNCDATAPG_7 : "Source System",
		SRCREFRNCDATAPG_8 : "Target reference table",
		SRCREFRNCDATAPG_11 : "Source reference data set",
		SRCREFRNCDATAPG_12 : "Matching target reference data set",
		SRCREFRNCDATAPG_13 : "RDM Hub mapping",
		SRCREFRNCDATAPG_14 : "{0} tables",
		SRCREFRNCDATAPG_15 : "{0} tables, {1} tables selected",
		SourceReferenceDataPage_NoRulesWarning : "This table is not referenced by any data tables. The exported source reference data set will be empty.",
		
		// DataMappingsPage messages

		DATAMAPNGSPG_2 : "Import from RDM Hub",
		DATAMAPNGSPG_6 : "Transcoding table",
		DATAMAPNGSPG_7 : "Mapping",
		DATAMAPNGSPG_8 : "RDM Hub status",
		DATAMAPNGSPG_9 : "Latest version",
		DATAMAPNGSPG_10 : "Loaded version",
		DATAMAPNGSPG_11 : "Source set CW system ID",
		DATAMAPNGSPG_12 : "Loaded",
		DATAMAPNGSPG_13 : "Value mappings",
		DATAMAPNGSPG_14 : "Source values",
		DATAMAPNGSPG_15 : "No selection is made",
		DATAMAPNGSPG_16 : "One or more mappings loaded into this transcoding table are incomplete",
		DATAMAPNGSPG_17 : "The transcoding table is missing.",
		DATAMAPNGSPG_18 : "Conflict",
		DATAMAPNGSPG_19 : "New",
		DATAMAPNGSPG_20 : "Loaded",
		DATAMAPNGSPG_21 : "Invalid",
		DATAMAPNGSPG_22 : "Removed",
		DATAMAPNGSPG_23 : "{0} rows",
		DATAMAPNGSPG_24 : "{0} rows, {1} rows selected",
		DATAMAPNGSPG_28 : "Conflict with: <br>",
		DATAMAPNGSPG_29 : "\n (same data sets)",
		DATAMAPNGSPG_30 : "Potential conflict with: <br>",
		DATAMAPNGSPG_31 : "\n (only one of these mappings can be loaded)",
		DATAMAPNGSPG_32 : "The selected mappings conflict with each other and cannot be loaded together.<br>"
				+ " Select any one of the conflicting mappings.",

		// Load Reference Data Wizard

		REFDATALOADWIZ_1 : "Load Reference Data From Target SAP System",
		
		// ParametersPage

		WizardParameters_PageTitle       : "Settings",
		WizardParameters_TargetSapSystem : "Target SAP system:",
		WizardParameters_NoSapSystems    : "No SAP systems are configured.",
		WizardParameters_BusinessObject  : "Business object",
		WizardParameters_NoDataForSystem : "No data for this SAP system",
		WizardParameters_AllBOs          : "&ltAll&gt",
		
		// ReferenceDataTableSelectionPage

		REFRNCDATATBLSELPG_1 : "Reference Table Selection",
		REFRNCDATATBLSELPG_7 : "Text table",
		REFRNCDATATBLSELPG_8 : "The table exists and has been loaded",
		REFRNCDATATBLSELPG_9 : "The table exists and has not been loaded",
		REFRNCDATATBLSELPG_10 : "The table is referenced but does not exist in the CW database",
		REFRNCDATATBLSELPG_10_TextTable : "The required text table does not exist in the CW database",
		REFRNCDATATBLSELPG_11 : "{0} table(s) are OK, {1} table(s) have been previously loaded",
		REFRNCDATATBLSELPG_11_JoiningComma : ", ",
		REFRNCDATATBLSELPG_11_Errors : "{0} table(s) have errors",
		REFRNCDATATBLSELPG_12 : "All tables are OK",
		REFRNCDATATBLSELPG_13 : "Transcoding table",
		WizardTableSelection_NoTables : "No in-scope reference tables were found.",
		
		// ReferenceDataImportSummaryPage

		REFRNCDATAIMPSUMPG_1 : "Summary",
		REFRNCDATAIMPSUMPG_2 : "SAP system: {0}",
		REFRNCDATAIMPSUMPG_3 : "Rollout: {0}",
		REFRNCDATAIMPSUMPG_4 : "Business object: {0}",
		REFRNCDATAIMPSUMPG_5 : "Load the following tables:",
		REFRNCDATAIMPSUMPG_6 : "Text table",
		REFRNCDATAIMPSUMPG_7 : "The table exists and contains data",
		REFRNCDATAIMPSUMPG_8 : "The table exists and is empty",
		REFRNCDATAIMPSUMPG_9 : "The table does not exist",
		REFRNCDATAIMPSUMPG_10 : "Transcoding table",

		// ReferenceDataImportProgressDialog messages

		REFRNCDATAIMPPROGDLG_1 : "Reference Data Load",
		REFRNCDATAIMPPROGDLG_2 : "Loading reference data from SAP system: {0}...",
		REFRNCDATAIMPPROGDLG_3 : "Loading reference data from multipe SAP systems: {0}...",
		REFRNCDATAIMPPROGDLG_4 : "Finished loading reference data from SAP system: {0}.",
		REFRNCDATAIMPPROGDLG_5 : "Finished loading reference data from SAP systems: {0}.",
		REFRNCDATAIMPPROGDLG_8 : "Text table",
		REFRNCDATAIMPPROGDLG_9 : "Rows",
		REFRNCDATAIMPPROGDLG_11 : "Loading...",
		REFRNCDATAIMPPROGDLG_13 : "Failed. See log for details.",
		REFRNCDATAIMPPROGDLG_15 : "Overall progress:",
		REFRNCDATAIMPPROGDLG_16 : "Table progress:",
		REFRNCDATAIMPPROGDLG_19 : "No changes",
		
		// ReferenceDataExportProgressDialog messages
		
		REFRNCDATAEXPPROGDLG_1 : "Reference Data Export",
		REFRNCDATAEXPPROGDLG_2 : "Exporting reference data to RDM Hub...",
		REFRNCDATAEXPPROGDLG_3 : "Export finished.",
		REFRNCDATAEXPPROGDLG_6 : "Rows",
		REFRNCDATAEXPPROGDLG_10 : "Table not found",
		REFRNCDATAEXPPROGDLG_11 : "Exporting...",
		REFRNCDATAEXPPROGDLG_12 : "Overall progress:",
		REFRNCDATAEXPPROGDLG_13 : "Table progress:",
		REFRNCDATAEXPPROGDLG_16 : "No changes",
		
		// DataMappingImportProgressDialog messages

		DATAMAPNGIMPPROGDLG_1 : "Data Mapping Import",
		DATAMAPNGIMPPROGDLG_2 : "Transcoding table",
		DATAMAPNGIMPPROGDLG_3 : "Mappings",
		DATAMAPNGIMPPROGDLG_4 : "Mapped source values",
		DATAMAPNGIMPPROGDLG_5 : "Source values",
		DATAMAPNGIMPPROGDLG_7 : "Importing mappings from RDM Hub...",
		DATAMAPNGIMPPROGDLG_8 : "Import finished.",
		DATAMAPNGIMPPROGDLG_10 : "Duplicate relation",
		DATAMAPNGIMPPROGDLG_11 : "Invalid mapping",
		DATAMAPNGIMPPROGDLG_12 : "Database error",
		DATAMAPNGIMPPROGDLG_13 : "Conflict in mapping",
		DATAMAPNGIMPPROGDLG_14 : "The transcoding table doesn't exist",
		DATAMAPNGIMPPROGDLG_15 : "There are no mappings for this transcoding table",
		DATAMAPNGIMPPROGDLG_16 : "Overall progress:",
		DATAMAPNGIMPPROGDLG_17 : "Table progress:",
		DATAMAPNGIMPPROGDLG_21 : "Importing...",
		DATAMAPNGIMPPROGDLG_22 : "Cancelled",
		DATAMAPNGIMPPROGDLG_23 : "RDM Hub login error",

		// DataMappingsCreationProgressDialog messages

		DATAMAPNGSCRTPROGDLG_1 : "Data Mapping Creation",
		DATAMAPNGSCRTPROGDLG_2 : "Creating initial data mappings...",
		DATAMAPNGSCRTPROGDLG_3 : "Mapping creation finished.",
		DATAMAPNGSCRTPROGDLG_6 : "RDM Hub login error",
		DATAMAPNGSCRTPROGDLG_7 : "Overall progress:",
		DATAMAPNGSCRTPROGDLG_10 : "Mapping name",
		DATAMAPNGSCRTPROGDLG_11 : "Target set",
		DATAMAPNGSCRTPROGDLG_12 : "Source set",
		DATAMAPNGSCRTPROGDLG_14 : "Creating...",
		DATAMAPNGSCRTPROGDLG_15 : "Source or target data set is unknown",
		DATAMAPNGSCRTPROGDLG_16 : "The mapping conflicts with another mapping.",
		DATAMAPNGSCRTPROGDLG_17 : "Mapping exists",
		DATAMAPNGSCRTPROGDLG_18 : "Set is missing in RDM Hub",

		// ReferenceDataEraseDialog messages
		
		REFRNCDATAERSDLG_1 : "Delete Reference Data",
		REFRNCDATAERSDLG_2 : "Delete the content of {0} reference table(s)?<br><br>You won't be able to export or import related RDM Hub entities<br>unless you load the reference data again.",
		REFRNCDATAERSDLG_3 : "Deleting reference data...",
		REFRNCDATAERSDLG_4 : "The reference data has been deleted.",
		REFRNCDATAERSDLG_5 : "The reference data has not been deleted.",
		REFRNCDATAERSDLG_6 : "Delete",

		// SapSystemPasswordDialog messages

		SAPPWDDLG_1 : "SAP password",
		SAPPWDDLG_2 : "Enter the password.",
		SAPPWDDLG_3 : "Password:",
		SAPPWDDLG_4 : "SAP system:",
		SAPPWDDLG_5 : "User:",
		SAPPWDDLG_6 : "The password is incorrect.",
		SAPPWDDLG_11 : "The SAP system is unreachable. Check the SAP system configuration.",
		
		// RdmPasswordDialog messages

		RDMPASSWDDLG_1 : "RDM Hub Password",
		RDMPASSWDDLG_4 : "Enter the password for RDM Hub user {0}:",
		RDMPASSWDDLG_5 : "The password is incorrect.",
		RDMPASSWDDLG_6 : "The RDM Hub system is unreachable. Check the RDM Hub configuration.",
		
		// **************************************************************************************
		
		//    BDR

		// **************************************************************************************
		
		// BDR tab titles

		BDR_1 : "Process Hierarchy",
		BDR_2 : "Business Objects",
		BDR_3 : "Tables",
		
		// BPH Tab messages

		BPHTAB_2 : "Delete",
		BPHTAB_3 : "Import",
		BPHTAB_4 : "Export",
		BPHTAB_5 : "Copy",
		BPHTAB_6 : "Paste",
		BPHTAB_7 : "Move To GT...",
		BphButtonAddTopLevelProcess   : "Add top level process...",
		BphButtonAddProcess           : "Add process here...",
		BphButtonAddProcessStep       : "Add process step here...",
		BphButtonAddBusinessObject 	  : "Add business object...",
		BphButtonAttachBusinessObject : "Attach business objects...",
		BphButtonAttachTables         : "Attach tables...",
		
		BPHTAB_13 : "Export to CW database...",
		BPHTAB_24 : "Export to CSV file...",

		BPHTAB_14 : "Import from CSV file...",
		BPHTAB_15 : "Import Process Hierarchy from SAP Solution Manager file...",
		BPHTAB_22 : "Note that any existing hierarchy or data objects may be merged with the objects from the file. To avoid unexpected results, save your work to a file using the export funcionality first.",
		
		BPHTAB_16 : "Detach",
		BPHTAB_17 : "Attach tables",
		BPHTAB_18 : "Attach business objects",
		
		BphPlaceholderMessage: "Add or select an item.",
		BphPlaceholderMessageMultiselection: "Multiple items selected.",

		// BPH ImportDialog messages
		
		BPHIMPORTDIALOG_1 : "Browse...",
		BPHIMPORTDIALOG_2 : "The selected file is too large. The maximum allowed size is 80MB.",
		BPHIMPORTDIALOG_3 : "Uploading the data model...",
		BPHIMPORTDIALOG_4 : "The import file upload was cancelled.",
		BPHIMPORTDIALOG_5 : "The import file upload did not finish due to an unknown error.",
		BPHIMPORTDIALOG_6 : "No file was selected.",
		BPHIMPORTDIALOG_7 : "The selected file is not valid.",
		BPHIMPORTDIALOG_8 : "The import file upload did not finish due to an internal error.",
		BPHIMPORTDIALOG_9 : "The selected file is being uploaded and processed...",
		BPHIMPORTDIALOG_10 : "Select an MPX file to upload.",
		BPHIMPORTDIALOG_12 : "Import complete.",
		BPHIMPORTDIALOG_13 : "Import Process Hierarchy from SAP Solution Manager",
		BPHIMPORTDIALOG_14 : "Import complete Business Data Roadmap",
		BPHIMPORTDIALOG_15 : "Import Business Data Roadmap into an existing Process Hierarchy",
		BPHIMPORTDIALOG_16 : "Select a CSV file to upload.",
		BPHIMPORTDIALOG_18 : "Unknown file type. Only the MPX and CSV file types are supported.",
		BPHIMPORTDIALOG_19 : "The import has been cancelled.",
		BPHIMPORTDIALOG_20 : " process(es) and/or steps added to Process Hierarchy.",
		BPHIMPORTDIALOG_21 : " business object(s) added to Business Objects tab.",
		BPHIMPORTDIALOG_22 : " table(s) added or changed.",
		BPHIMPORTDIALOG_26 : " table usage(s) added or changed.",
		BPHIMPORTDIALOG_23 : "The import has finished, but no changes have been made.",
		BPHIMPORTDIALOG_24 : "Import Process Hierarchy from CSV file.",
		BPHIMPORTDIALOG_25 : "Import Table Metadata from CSV file.",
		BPHIMPORTDIALOG_27 : "Import SAP Views from CSV file.",
		BPHIMPORTDIALOG_28 : "Import file contains invalid data.",
		BPHIMPORTDIALOG_29 : "Data header is invalid.",
		BPHIMPORTDIALOG_30 : "Process Hierarchy is invalid.",
		// Load fields from SAP dialog
		
		FIELDSIMPORTDIALOG_1 : "Loading...",
		FIELDSIMPORTDIALOG_3 : "Table doesn\'t exist",
		FIELDSIMPORTDIALOG_4 : "Failed",
		FIELDSIMPORTDIALOG_5 : "Metadata Load",
		FIELDSIMPORTDIALOG_6 : "Loading field metadata from SAP system: {0}...",
		FIELDSIMPORTDIALOG_8 : "Finished loading field metadata from SAP system: {0}.",
		FIELDSIMPORTDIALOG_10 : "Overall progress:",
		FIELDSIMPORTDIALOG_11 : "Table progress:",
		FIELDSIMPORTBUTTON : "Import from SAP",
		FIELDSIMPORTSAPVIEWBUTTON : "Import SAP Views",
		
		// BDR export dialog
		
		ExportTypeDialog_1 : "Export to CSV file",
		ExportTypeDialog_2 : "Export tables",
		ExportTypeDialog_3 : "Export BDR",
		ExportTypeDialog_4 : "Complete BDR",
		ExportTypeDialog_5 : "Selection",
		ExportTypeDialog_6 : "All table fields",
		ExportTypeDialog_7 : "Only",
		ExportTypeDialog_8 : "In Scope",
		ExportTypeDialog_9 : "Not In Scope",
		ExportTypeDialog_10 : "Follow Up",
		ExportTypeDialog_11 : "Choose an export type:",
		ExportTypeDialog_12 : " (documentation use only)",
		
		// BDR import dialog
		
		ImportTypeDialog_1 : "Choose an import type:",
		ImportTypeDialog_2 : "Import tables (from table export file)",
		ImportTypeDialog_3 : "Import BDR",
		ImportTypeDialog_4 : "Import complete BDR",
		ImportTypeDialog_5 : "Import processes and steps",
		ImportTypeDialog_6 : "Import business objects with attached tables",
		ImportTypeDialog_7 : "Import from CSV file",
		ImportTypeDialog_8 : "Overwrite existing items",
		ImportTypeDialog_9 : "Overwrite existing SAP Views",
		ImportTypeDialog_10 : "Merge with existing SAP Views",
				
		// BDR export to file
		
		ExportToFile_Dialog : "The export is being prepared. This may take a while.<br>You will be notified as soon as the download is ready.",
		ExportToFile_Cancel : "The export has been cancelled.",
		ExportToFile_InternalError : "An internal error has occurred. The export could not be completed.",
		MovedToGT : "Move to Global Template completed. No. of updated items is: ",
		
		// BPH export To CWDB dialog
		
		ExportToCwdb_DialogTitle : "Export Business Data Roadmap to the CW Database",
		ExportToCwdb_ParametersHeader : "Export parameters:",
		ExportToCwdb_SeparateScopes : "Separate in-scope value aggregation per business object",
		ExportToCwdb_OverwriteConfirmation : "The target tables contain data for the selected rollout. Are you sure that you want to overwrite it?",
		ExportToCwdb_OverwriteButton : "Export & Overwrite",
		ExportToCwdb_InProgress : "Exporting the Business Data Roadmap...",
		ExportToCwdb_Complete : "Business Data Roadmap exported successfully.",
		
		// BPH DeleteDialog messages
		
		DeleteDialog_Title : "Delete Item",
		DeleteDialog_Process : "Are you sure that you want to delete this process?<br>All subprocesses, related process steps, and table usage objects will also be deleted.",
		DeleteDialog_ProcessStep : "Are you sure that you want to delete this process step?<br>All related table usage objects will also be deleted.",
		DeleteDialog_Detach_BO : "Are you sure that you want to detach this business object from the process step?<br>All related table usage objects will be deleted.",
		DeleteDialog_BO : "Are you sure that you want to delete this business object?<br>All related table usage objects will also be deleted.",
		DeleteDialog_Detach_Table : "Are you sure that you want to detach this table from the business object?<br>All related table usage objects will be deleted.",
		DeleteDialog_Table : "Are you sure that you want to delete this table?<br>All related table usage objects will also be deleted.",
		DeleteDialog_Multi : "Are you sure that you want to delete or detach these items?<br>All dependent items will also be deleted or detached accordingly.",
		
		// Data not saved dialog
		
		DataNotSavedDialogTitle : "Warning: unsaved changes", 
		DataNotSavedWarning : "You are about to lose all unsaved changes. Would you like to save them?",
		
		// BDR object types
		
		BphProcess: "Process",
		BphProcessStep: "Process step",
		BphBusinessObject: "Business object",
		BphTable: "Table",
		BphTableUsage: "Table usage",
		
		// BPH Header messages
		
		BphObjectLastUpdated : "Last updated: {0}",
		BphApprovalStatusUndefined : "",
		BphApprovalStatusDraft : "Draft",
		BphApprovalStatusPending : "Pending approval",
		BphApprovalStatusApproved : "Approved",
		BphApprovalStatusMixed : "Not yet approved",
		BphApprovalStatusPlaceholder : "Change status...",
		
		// BPH Details Pane title messages for new objects
		
		BphTitleAddProcess  		: "New process",
		BphTitleAddProcessStep 		: "New process step",
		BphTitleAddBusinessObject 	: "New business object",
		BphTitleAddTable 			: "New table",
		
		// BPH Attach Business Objects Dialog
		
		BphAddBODialog_Prompt : "Select business objects to attach:",
		BphAddBODialog_NoData : "No business objects found.",
		
		// BPH Attach Tables Dialog
		
		BphAddTableDialog_Prompt : "Select tables to attach:",
		BphAddTableDialog_NoData : "No tables found.",
		
		// BPH Tables
		
		BPHTABLESTAB_2 : "Loading the data...",
		BPHTABLESTAB_3 : "No tables found.",
		BPHTABLESTAB_4 : "Description",
		BPHTABLESTAB_Transactions : "Transactions",
		BPHTABLESTAB_5 : "Fields",
		BPHTABLESTAB_6 : "Used in...",
		BPHTABLESTAB_7 : "Filter by name...",
		BPHTABLESTAB_8 : "Import fields from SAP",
		BPHTABLESTAB_9 : "Importing fields from SAP. Please wait.",
		BPHTABLESTAB_10 : "No fields found. Add or import fields.",
		BPHTABLESTAB_11 : "Select a field from the table above to view usage information for that field.",
		BPHTABLESTAB_12 : "Field usage in processes:",
		BPHTABLESTAB_13 : "Import from data model...",
		BPHTABLESTAB_14 : "The selected field has no usage attributes.",
		BPHTABLESTAB_15 : "Save the pending changes to view the field usage.",
		BPHTABLESTAB_16 : "Filter by SAP view",
		BPHTABLESTAB_17 : "Apply Filter",

		// TablesFieldPage messages
		
		TABLESFIELDPAGE_1 : "Import fields from SAP",
		TABLESFIELDPAGE_3 : "Name",
		TABLESFIELDPAGE_4 : "Check table",
		TABLESFIELDPAGE_5 : "SAP view",
		TABLESFIELDPAGE_6 : "Description",
		TABLESFIELDPAGE_7 : "Comment",
		TABLESFIELDPAGE_8 : "Business process",
		TABLESFIELDPAGE_9 : "Business object",
		TABLESFIELDPAGE_10 : "Recommended",
		TABLESFIELDPAGE_11 : "Scope",
		TABLESFIELDPAGE_14 : "Required",
		TABLESFIELDPAGE_15 : "Skip TC",
		TABLESFIELDPAGE_16 : "The usage of the field is OK",
		TABLESFIELDPAGE_17 : "This field is marked as required but is in the follow up state",
		TABLESFIELDPAGE_18 : "Warning: The field is written multiple times",
		TABLESFIELDPAGE_19 : "Warning: The field is read but never written",
		TABLESFIELDPAGE_20 : "Warning: The field is marked as required but is never written",
		TABLESFIELDPAGE_21 : "Error: The field is marked as required but is not in scope",
		TABLESFIELDPAGE_26 : "Error: The field is marked as required but has no scope setting",
		TABLESFIELDPAGE_22 : "Open Table",
		TABLESFIELDPAGE_23 : "Import fields from SAP (keep existing settings)",
		TABLESFIELDPAGE_24 : "The table couldn't be found and therefore no fields were imported.",
		TABLESFIELDPAGE_25 : "The action couldn't be performed due to an internal error.",
		TABLESFIELDPAGE_27 : "GT",
		TABLESFIELDPAGE_SET_IS_REQUIRED : "Set Required",
		TABLESFIELDPAGE_SET_SCOPE : "Set Scope",
		TABLESFIELDPAGE_SET_IN_SCOPE : "In Scope",
		TABLESFIELDPAGE_SET_NOT_IN_SCOPE : "Not In Scope",
		TABLESFIELDPAGE_SET_FOLLOW_UP : "Follow Up",
		TABLESFIELDPAGE_SET_BLANK : "(blank)",
		TABLESFIELDPAGE_SET_REQUIRED : "Required",
		TABLESFIELDPAGE_SET_NOT_REQUIRED : "Not Required",
		
		// TablesFieldPage tooltips
		
		TABLESFIELDPAGE_TT_1 : "Name of the field",
		TABLESFIELDPAGE_TT_2 : "Check table for the field",
		TABLESFIELDPAGE_TT_3 : "The SAP UI component in which this field is used",
		TABLESFIELDPAGE_TT_4 : "Description for the field",
		TABLESFIELDPAGE_TT_5 : "Status of the field",
		TABLESFIELDPAGE_TT_6 : "Checked if using the field is recommended",
		TABLESFIELDPAGE_TT_12 : "This is a recommended field",
		TABLESFIELDPAGE_TT_13 : "This is not a recommended field",
		TABLESFIELDPAGE_TT_7 : "Checked if using the field is mandatory",
		TABLESFIELDPAGE_12 : "The field is mandatory",
		TABLESFIELDPAGE_13 : "The field is optional",
		TABLESFIELDPAGE_TT_8 : "How the field is used in the context of the process step.",
		TABLESFIELDPAGE_TT_9 : "Optional comment for the field",
		TABLESFIELDPAGE_TT_10 : "The process step in which the field is used.",
		TABLESFIELDPAGE_TT_11 : "Name of the business object to which the field is attached.",
		TABLESFIELDPAGE_TT_14 : "This is a Global Template.",

		BphFieldUsageStatusRead : "Read", // Deprecated
		BphFieldUsageStatusWrite : "Write", // Deprecated
		BphFieldUsageStatusUnused : "Not used", // Deprecated
		BphFieldUsageStatusFollowUp : "Follow up",
		BphFieldUsageStatusInScope : "In scope",
		BphFieldUsageStatusNotInScope : "Not in scope",
		BphFieldUsageStatusBlank : "",

		// AddFieldDialog messages
		
		ADDFIELDDIALOG_1 : "Add a field",
		ADDFIELDDIALOG_2 : "Enter the following details for the field to be added:",
		ADDFIELDDIALOG_3 : "Check table",
		ADDFIELDDIALOG_4 : "SAP view",
		
		// Used In tab
		
		BdrNoUsages: "This item is not currently used in the Business Process Hierarchy.",

		// TabAccessInformation messages

		TABACCESSINFORMATION_1 : "Access Control",
		TABACCESSINFORMATION_TitleProcess : "Set Step Access",
		TABACCESSINFORMATION_2 : "WebSphere Application Server user registry:",
		TABACCESSINFORMATION_3 : "Who can access this object:",
		TABACCESSINFORMATION_4 : "Grant",
		TABACCESSINFORMATION_5 : "Revoke",
		TABACCESSINFORMATION_6 : "Use this tab to grant or revoke access to all steps under this process at once.",

		// TabTransactions messages

		TABTRANSACTIONS_1 : "No SAP transactions found.",
		TABTRANSACTIONS_2 : "SAP transaction",
		TABTRANSACTIONS_3 : "Comment",

		// AboutDialog messages
		
		ABOUTDLG_1 : "About",
		ABOUTDLG_2 : "About IBM InfoSphere Conversion Workbench Application",
		ABOUTDLG_3 : "Licensed Materials - Property of IBM.  5725-E59  &copy; Copyright 1998, 2013 IBM Corporation. IBM, the IBM logo, and InfoSphere are trademarks of IBM Corporation, registered in many jurisdictions worldwide. Java and all Java-based trademarks and logos are trademarks or registered trademarks of Oracle and/or its affiliates. Other product and service names might be trademarks of IBM or other companies. This Program is licensed under the terms of the license agreement accompanying the Program. This license agreement may be either located in a Program directory folder or library identified as \"License\" or \"Non_IBM_License\", if applicable, or provided as a printed license agreement. Please read this agreement carefully before using the Program. By using the Program, you agree to these terms.",
		AboutDialog_BuildNumberLabel : "Build {0}",
		
		// SearchBox messages
		
		SEARCHBOX_1 : "Enter the filter condition...",
		SEARCHBOX_2 : "Clear the filter condition",

		// Remediation

		REMEDIATION_TAB_NAME : "Remediation",

		last : ""
	},

	de : true
});


