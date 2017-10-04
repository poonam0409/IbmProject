//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.common.ui
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui;

public class WidgetIDConstants {

	static String copyright()
	{ return com.ibm.is.sappack.gen.common.ui.Copyright.IBM_COPYRIGHT_SHORT; }

	// -------------------------------------------------------------------------------------
	// W I D G E T  I D  C O N S T A N T
	// ------------------------------------------------------------------------------------- 
	public static final String WIDGET_ID = "com.ibm.is.sappack.gen.common.widgetid"; //$NON-NLS-1$

	public static final String WIZARD_BUTTON_FINISH = Messages.WidgetIDConstants_1;
	public static final String WIZARD_BUTTON_NEXT = Messages.WidgetIDConstants_2;
	public static final String WIZARD_BUTTON_CANCEL = Messages.WidgetIDConstants_3;

	// -------------------------------------------------------------------------------------
	// C O M M O N
	// -------------------------------------------------------------------------------------
		
	// Missing SAP password dialog
	public static final String COMMON_SAPPWTEXT = "textMissingSAPPassword"; //$NON-NLS-1$
	
	// Create & Edit SAP Systems Wizard
	public static final String SAP_SYSTEMNAMETEXT = "sapSystemNameText"; //$NON-NLS-1$
	public static final String SAP_USELOADBALANCINGCHECKBOX = "useLoadBalancingCheckBox"; //$NON-NLS-1$
	public static final String SAP_HOSTNAMETEXT = "hostNameText"; //$NON-NLS-1$
	public static final String SAP_SYSTEMNUMBERTEXT = "systemNumberText"; //$NON-NLS-1$
	public static final String SAP_CLIENTIDTEXT = "clientIDText"; //$NON-NLS-1$
   public static final String SAP_USERLANGUAGETEXT = "sapUserLanguageText"; //$NON-NLS-1$
	public static final String SAP_ROUTERSTRINGTEXT = "sapRouterStringText"; //$NON-NLS-1$
	public static final String SAP_USERNAMETEXT = "sapUsernameText"; //$NON-NLS-1$
	public static final String SAP_PASSWORDTEXT = "sapPasswordText"; //$NON-NLS-1$
	public static final String SAP_ABOUTJCOBUTTON = "sapAboutJCOButton"; //$NON-NLS-1$
	public static final String SAP_TESTCONNECTIONBUTTON = "sapTestConnectionButton"; //$NON-NLS-1$
	public static final String SAP_SAVEBUTTON = "sapSaveButton"; //$NON-NLS-1$
	public static final String SAP_BUTTONDELETE = "sapButtonDelete"; //$NON-NLS-1$
	public static final String SAP_BUTTONNEW = "sapButtonNew"; //$NON-NLS-1$
	public static final String SAP_LISTOFSAPSYSTEMS = "sapListOfSAPSystems"; //$NON-NLS-1$
	
	// -------------------------------------------------------------------------------------
	// R A P I D  M O D E L E R
	// ------------------------------------------------------------------------------------- 
	
	// SapSystemSelectionWizardPage
	public static final String MOD_COMBOSAPSYSTEMS = "comboSapSystems"; //$NON-NLS-1$
	public static final String MOD_BUTTONEDITSAPSETTINGS = "buttonEditSapSystems"; //$NON-NLS-1$
	public static final String MOD_PREDEFINEDSAPMODELSOPTION = "predefinedSapModelsOption"; //$NON-NLS-1$
	public static final String MOD_BROWSESAPOPTION = "browseSapOption"; //$NON-NLS-1$
	public static final String MOD_BROWSESAPFORIDOCOPTION = "browseSAPForIDocsOption"; //$NON-NLS-1$
	public static final String MOD_SPECIFYMANUALLYOPTION = "specifyManuallyOption"; //$NON-NLS-1$
	public static final String MOD_SPECIFYIDOCSMANUALLYOPTION = "specifyIDocsManuallyOption"; //$NON-NLS-1$
	
	// TableBrowserWizardPage
	public static final String MOD_TEXTTABLESEARCHTERM = "textTableSearchTerm"; //$NON-NLS-1$
	public static final String MOD_BUTTONSEARCH = "buttonSearch"; //$NON-NLS-1$
	public static final String MOD_TABLEBROWSEDTABLES = "tableBrowsedTables"; //$NON-NLS-1$
	public static final String MOD_TABLESELECTEDTABLES = "tableSelectedTables"; //$NON-NLS-1$
	public static final String MOD_BUTTONADDTABLES = "buttonAddTables"; //$NON-NLS-1$
	public static final String MOD_BUTTONREMOVETABLES = "buttonRemoveTables"; //$NON-NLS-1$
	
	// IDocBrowserWizardPage
	public static final String MOD_TEXTIDOCSEARCHTERM = "textIDocSearchTerm"; //$NON-NLS-1$
	public static final String MOD_BASICTYPEBUTTON = "basicTypeButton"; //$NON-NLS-1$
	public static final String MOD_EXTENSIONTYPEBUTTON = "extensionTypeButton"; //$NON-NLS-1$
	public static final String MOD_IDOCTREE = "iDocTree"; //$NON-NLS-1$
	
	// ImportOptionsWizardPage
	public static final String MOD_CHECKBOXCHECKTABLES = "checkBoxCheckTables"; //$NON-NLS-1$
	public static final String MOD_RECURSIVESEARCH = "recursiveSearch"; //$NON-NLS-1$
	public static final String MOD_CHECKBOXTEXTTABLES = "checkboxTextTables"; //$NON-NLS-1$
   public static final String MOD_EXTRACT_RELATIONS  = "extractRelations"; //$NON-NLS-1$
   public static final String MOD_ENFORCE_RELATIONS  = "enforceRelations"; //$NON-NLS-1$
	public static final String MOD_BLACKLISTFILECOMBO = "blacklistFileCombo"; //$NON-NLS-1$
	
	// IDocTypeImportOptionsWizardPage
	public static final String MOD_IDOCEXTRACTMODELOPTION = "idocExtractModelOption"; //$NON-NLS-1$
	public static final String MOD_IDOCLOADMODELOPTION = "idocLoadModelOption"; //$NON-NLS-1$
	public static final String MOD_SEGMENTFIELDSNULLABLEOPTION = "segmentFieldsNullableOption"; //$NON-NLS-1$
	public static final String MOD_USEALLTYPESOPTION = "useAllTypesOption"; //$NON-NLS-1$
	public static final String MOD_USEVARCHARTYPEOPTION = "useVarcharTypeOption"; //$NON-NLS-1$
	public static final String MOD_LENGTHFACTORCOMBO = "lengthFactorCombo"; //$NON-NLS-1$
	public static final String MOD_CHECKTABLESOPTION = "checkTablesOption"; //$NON-NLS-1$
	public static final String MOD_TEXTTABLESOPTION = "textTablesOption"; //$NON-NLS-1$
	
	// TableListWizardPage
	public static final String MOD_TABLELISTTABLE = "tableListTable"; //$NON-NLS-1$
	public static final String MOD_SAVESELECTEDTABLESBUTTON = "saveSelectedTablesButton"; //$NON-NLS-1$
	
	// IDocSegmentListWizardPage
	public static final String MOD_SEGMENTLISTTABLE = "segmentListTable"; //$NON-NLS-1$
	
	// TechnicalFieldsWizardPage
	public static final String MOD_CONFIGURETECHNICALFIELDSBUTTON = "configureTechnicalFieldsButton"; //$NON-NLS-1$
	public static final String MOD_TECHNICALFIELDSTABLE = "technicalFieldsTable"; //$NON-NLS-1$
	public static final String MOD_ADDBUTTON = "addButton"; //$NON-NLS-1$
	public static final String MOD_REMOVEBUTTON = "removeButton"; //$NON-NLS-1$
	
	// LdmPackageSelectionPage
	public static final String MOD_USESPECIFICPACKAGECHECKBOX = "useSpecificPackageCheckbox"; //$NON-NLS-1$
	public static final String MOD_PACKAGESELECTIONTABLE = "packageSelectionTable"; //$NON-NLS-1$
	public static final String MOD_CLEARSELECTIONBUTTON = "clearSelectionButton"; //$NON-NLS-1$
	
	// SummaryWizardPage
	public static final String MOD_SUMMARYTEXT = "summaryText"; //$NON-NLS-1$
	
	// -------------------------------------------------------------------------------------
	// R A P I D  G E N E R A T O R
	// ------------------------------------------------------------------------------------- 
	
	// JobTypeSelectionPage
	public static final String GEN_ABAPEXTRACTJOBOPTION = "abapExtractJobOption"; //$NON-NLS-1$
	public static final String GEN_IDOCEXTRACTJOBOPTIONS = "idocExtractJobOption"; //$NON-NLS-1$
	public static final String GEN_IDOCLOADJOBOPTION = "idocLoadJobOption"; //$NON-NLS-1$
	
	// InformationServerDetailsPage
	public static final String GEN_ISHOSTTEXT = "isHostText"; //$NON-NLS-1$
	public static final String GEN_ISPORTTEXT = "isPortText";  //$NON-NLS-1$
	public static final String GEN_ISUSERTEXT = "isUserText"; //$NON-NLS-1$
	public static final String GEN_ISPASSWORDTEXT = "isPasswordText"; //$NON-NLS-1$
	public static final String GEN_DSPROJECTTEXT = "dsProjectText"; //$NON-NLS-1$
	public static final String GEN_DSFOLDERTEXT = "dsFolderText"; //$NON-NLS-1$
   public static final String GEN_DSFOLDERSELECTBUTTON = "dsFolderSelectButton"; //$NON-NLS-1$
	public static final String GEN_DSJOBTEXT = "dsJobText"; //$NON-NLS-1$
	public static final String GEN_DSOVERWRITEJOBBUTTON = "dsOverWriteJobButton"; //$NON-NLS-1$
	
	// JobGeneratorAbapPage
	public static final String GEN_SAPGATEWAYHOSTTEXT = "sapGatewayHostText"; //$NON-NLS-1$
	public static final String GEN_SAPGATEWAYSERVICETEXT = "sapGatewayServiceText"; //$NON-NLS-1$
	public static final String GEN_SAPUSERTEXT = "sapUserText"; //$NON-NLS-1$
	public static final String GEN_SAPUSERPASSWORDTEXT = "sapUserPasswordText"; //$NON-NLS-1$
	public static final String GEN_SAPUSERCLIENTNUMBERTEXT = "sapUserClientNumberText"; //$NON-NLS-1$
	public static final String GEN_SAPUSERLANGUAGETEXT = "sapUserLanguageText"; //$NON-NLS-1$
	public static final String GEN_PERSISTENCETYPESCOMBO = "persistenceTypesCombo"; //$NON-NLS-1$
	public static final String GEN_ODBCDATASOURCETEXT = "odbcDataSourceText"; //$NON-NLS-1$
	public static final String GEN_ODBCUSERTEXT = "odbcUserText"; //$NON-NLS-1$
	public static final String GEN_ODBCPASSWORDTEXT = "odbcPasswordText"; //$NON-NLS-1$
	public static final String GEN_ODBCSQLWHERECONDTEXT = "odbcSqlWhereCondText"; //$NON-NLS-1$
	public static final String GEN_TABLEACTIONSCOMBO = "tableActionsCombo"; //$NON-NLS-1$
	public static final String GEN_FILETEXT = "fileText"; //$NON-NLS-1$
	public static final String GEN_FILEUPDATEMODECOMBO = "fileUpdateModeCombo"; //$NON-NLS-1$
	public static final String GEN_SAPRFCDESTINATIONSFILECOMBO = "sapRfcDestinationsFileCombo"; //$NON-NLS-1$
	public static final String GEN_RFCDESTINATIONSFILEBROWSEBUTTON = "rfcDestinationsFileBrowseButton"; //$NON-NLS-1$
	public static final String GEN_DSMAXFLOWSINJOBCOMBO = "dsMaxFlowsInJobCombo"; //$NON-NLS-1$
	public static final String GEN_CREATERFCDESTAUTO = "createRfcDestAutomatically"; //$NON-NLS-1$
	public static final String GEN_DELETERFCDESTIFEXIST = "deleteRfcDestIfExisting"; //$NON-NLS-1$
	public static final String GEN_VALIDATEBUTTON = "validateButton"; //$NON-NLS-1$
	
   // CWAdditionalJobSettingsPage
   public static final String GEN_DESCRIPTION_ENABLED_BUTTON   = "descriptionEnabledButton"; //$NON-NLS-1$
   public static final String GEN_DESCRIPTION_EDITOR           = "descriptionEditor"; //$NON-NLS-1$
   public static final String GEN_DESCRIPTION_FILE_LOAD_BUTTON = "descriptionFileLoadButton"; //$NON-NLS-1$

	// ABAP program upload page
	public static final String GEN_SAPCONNECTIONCOMBO = "sapConnectionCombo"; //$NON-NLS-1$
	public static final String GEN_SAVEABAPPROGRAMSOPTION = "saveAbapProgramsOption"; //$NON-NLS-1$
	public static final String GEN_UPLOADABAPPROGRAMSOPTION = "uploadAbapProgramsOption"; //$NON-NLS-1$
	public static final String GEN_SUPPRESSBACKGROUNDJOB = "suppressBackgroundJob"; //$NON-NLS-1$
	public static final String GEN_RETRIES = "numberOfRetries"; //$NON-NLS-1$
	public static final String GEN_RETRYINTERVAL = "retryInterval"; //$NON-NLS-1$
	
	// JobGeneratorIdocExtractPage
	public static final String GEN_OVERWRITECONNECTIONDEFAULTS = "overwriteConnectionDefaults"; //$NON-NLS-1$
	
	// JobGeneratorIdocLoadPage
	public static final String GEN_SAPMESSAGETYPECOMBO = "sapMessageTypeCombo"; //$NON-NLS-1$
	
	// JobGeneratorJobParametersPage
	public static final String GEN_JOBPARAMETERSTABLE = "jobParametersTable"; //$NON-NLS-1$
	public static final String GEN_JOBPARAMETERSADDBUTTON = "jobParametersAddButton"; //$NON-NLS-1$
	public static final String GEN_JOBPARAMETERSREMOVEBUTTON = "jobParametersRemoveButton"; //$NON-NLS-1$
   public static final String GEN_JOBPARAMETERSCHECKBUTTON = "jobParametersCheckButton"; //$NON-NLS-1$
	
	// JobGeneratorCustomDerivationsPage
	public static final String GEN_CUSTOMDERIVATIONSTABLE = "customDerivationsTable"; //$NON-NLS-1$
	public static final String GEN_USECUSTOMDERIVATIONSBUTTON = "useCustomDerivationsButton"; //$NON-NLS-1$
	public static final String GEN_CUSTOMDERIVATIONSADDBUTTON = "customDerivationsAddButton"; //$NON-NLS-1$
	public static final String GEN_CUSTOMDERIVATIONSREMOVEBUTTON = "customDerivationsRemoveButton"; //$NON-NLS-1$
	
	// JobGeneratorSummaryPage
	public static final String GEN_SUMMARYTEXT = "summaryText"; //$NON-NLS-1$
	public static final String GEN_COMPILEJOBSBUTTON = "compileJobsButton"; //$NON-NLS-1$
}
