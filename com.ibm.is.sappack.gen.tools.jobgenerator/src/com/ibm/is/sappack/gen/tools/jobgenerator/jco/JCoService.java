//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                          
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.jco
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator.jco;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.jco.RfcDestinationDataProvider;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.SAPAccessException;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeOptionsBase;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoTable;


public class JCoService {
	private enum DSRFCSvcVersion { DSServiceV7_0, DSServiceV7_0_CTS, DSServiceV7_1, DSServiceV8_0  };

   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public  static final String SAP_JCO_FCTN_GET_VERSION            =  "0";	    //$NON-NLS-1$
   public  static final String SAP_JCO_FCTN_ABAP_CODE_LOAD         =  "1"; 	 //$NON-NLS-1$
   public  static final String SAP_JCO_FCTN_SYNTAX_CHECK           =  "2"; 	 //$NON-NLS-1$
   public  static final String SAP_JCO_FCTN_RUNTIME_EXEC           =  "3"; 	 //$NON-NLS-1$
   public  static final String SAP_JCO_FCTN_GET_TAB_REC_SIZE       =  "4"; 	 //$NON-NLS-1$
   public  static final String SAP_JCO_FCTN_GET_TAB_RECORD         =  "5"; 	 //$NON-NLS-1$
   public  static final String SAP_JCO_FCTN_SYNTAX_CHECK_R3        =  "6"; 	 //$NON-NLS-1$
   public  static final String SAP_JCO_FCTN_ABAP_CODE_DOWNLOAD     =  "7"; 	 //$NON-NLS-1$
   public  static final String SAP_JCO_FCTN_VARIANT_CREATE         =  "9"; 	 //$NON-NLS-1$
   public  static final String SAP_JCO_FCTN_VARIANT_DELETE         = "10"; 	 //$NON-NLS-1$
   public  static final String SAP_JCO_FCTN_ABAP_CODE_LOAD_CTS     = "11"; 	 //$NON-NLS-1$
   public  static final String SAP_JCO_FCTN_CREATE_RFC_DEST        = "12"; 	 //$NON-NLS-1$
   public  static final String SAP_JCO_FCTN_CLEANUP_LUW            = "13"; 	 //$NON-NLS-1$
	
   public  static final String SAP_RFC_IN_PAR_SERVICE_TYPE         = "I_SERVE_TYPE";    //$NON-NLS-1$
   public  static final String SAP_RFC_IN_PAR_DEV_CLASS            = "I_DEV_CLASS";     //$NON-NLS-1$
   public  static final String SAP_RFC_IN_PAR_REQUEST              = "I_REQUEST";       //$NON-NLS-1$
   public  static final String SAP_RFC_IN_PAR_REQUEST_DESC         = "I_REQ_DESCR";       //$NON-NLS-1$
   public  static final String SAP_RFC_IN_PAR_OVERWRITE_OVERRIDE   = "I_OVERWRITE_OVERRIDE"; //$NON-NLS-1$
   public  static final String SAP_RFC_IN_PAR_REPORT_NAME          = "I_REPORT_NAME";   //$NON-NLS-1$
   public  static final String SAP_RFC_OUT_PAR_DSS_VERSION         = "O_DSS_VER";       //$NON-NLS-1$
   public  static final String SAP_RFC_TBL_PAR_CODE                = "T_CODE";          //$NON-NLS-1$
   public  static final String SAP_RFC_TBL_PAR_DEV_CLASSES         = "T_DEVCLASSES";    //$NON-NLS-1$
   public  static final String SAP_RFC_TBL_PAR_REQUESTS            = "T_REQUESTS";      //$NON-NLS-1$
   public  static final String SAP_RFC_EX_PAR_REQ				   = "O_REQUEST";		//$NON-NLS-1$

   public  static final String SAP_RFC_PARAM_BOOL_ENABLE           = "X";               //$NON-NLS-1$
   private static final String SAP_RFC_PARAM_SPECIAL_CHAR          = "\u00c4";          //$NON-NLS-1$
   public  static final String SAP_RFC_VAL_KEY_LINE                = "LINE";            //$NON-NLS-1$
   public  static final String SAP_RFC_VAL_KEY_DEV_CLASS           = "DEVCLASS";        //$NON-NLS-1$
   public  static final String SAP_RFC_VAL_KEY_TRKORR              = "TRKORR";          //$NON-NLS-1$
   
   private static final String SAP_RFC_DS_SERVICE_NAME_V7_0        = "Z_RFC_DS_SERVICE";      //$NON-NLS-1$
   private static final String SAP_RFC_DS_SERVICE_NAME_V7_0_CTS    = "Z_RFC_DS_SERVICE_CTS";  //$NON-NLS-1$
   private static final String SAP_RFC_DS_SERVICE_VERSION_V7_0     = "200";                   //$NON-NLS-1$
   private static final String SAP_RFC_DS_SERVICE_NAME_V7_1        = "Z_RFC_DS_SERVICE_V7_1"; //$NON-NLS-1$
   private static final String SAP_RFC_DS_SERVICE_VERSION_V7_1     = "7.1";                   //$NON-NLS-1$
   private static final String SAP_RFC_DS_SERVICE_NAME_V8_0        = "Z_RFC_DS_SERVICE_V8_0"; //$NON-NLS-1$
   private static final String SAP_RFC_DS_SERVICE_VERSION_V8_0     = "8.0";                   //$NON-NLS-1$
   public  static final String SAP_RFC_TEMP_DEV_CLASS              = "$TMP";                  //$NON-NLS-1$

   private static boolean 			_bTransportRequestCreated=false;
   private static String            _sReqIdForSingleTRCase="";


   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
	private JCoDestination    destination;
	private JCoFunction       function;
	private String            sapPackage;
	private String            ctsRequest;
	private String			  ctsRequestDesc;
	private boolean           useCTS;
	private boolean			  createSeparateTransports;
	private DSRFCSvcVersion   rfcSvcVersion;


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.jco.Copyright.IBM_COPYRIGHT_SHORT;
	}


	private JCoService(SapSystem sapSystem, String ctsPackage, String ctsRequest, String ctsRequestDesc, boolean createSeparateTransports) throws SAPAccessException {		
		init(sapSystem, ctsPackage, ctsRequest, ctsRequestDesc, createSeparateTransports);
	} // end of JCoService()


	/**
	 * This method checks the 'SAP RFC DS Service' version.
	 * <p>
	 * If the version is valid it returns null otherwise it returns an appropriate 
	 * error message.
	 *  
	 * @return null or error message 
	 * 
	 * @throws JobGeneratorException
	 */
	public String checkVersion() throws SAPAccessException {
		String  rfcServiceName;
		String  retSAPDSVersion;
		String  supportedVersion;
		String  retCheckMsg;

		// get the supported version
		supportedVersion = getServiceVersion(this.rfcSvcVersion);

		// get the installed DS version from SAP
		retSAPDSVersion = getSAPDSServiceVersion();

		if (retSAPDSVersion.startsWith(supportedVersion)) {
			retCheckMsg = null;         // correct service version !!!
		}
		else {
			rfcServiceName = getServiceName(this.rfcSvcVersion);
			retCheckMsg    = MessageFormat.format(Messages.JCOService_2, rfcServiceName, retSAPDSVersion, supportedVersion);
		}

		return(retCheckMsg);
	} // end of checkVersion()


	private static DSRFCSvcVersion determineRFCServiceVersionToBeUsed(boolean useCTS) {
		DSRFCSvcVersion retRFCSvcVersion;

		switch(ModeOptionsBase.getDSServiceVersion()) {

		case v80:
		default:
			retRFCSvcVersion = DSRFCSvcVersion.DSServiceV8_0;
			break;
			
		case v71:
			retRFCSvcVersion = DSRFCSvcVersion.DSServiceV7_1;
			break;
			
		case v70:
			retRFCSvcVersion = DSRFCSvcVersion.DSServiceV7_0;
			// for v7.0 set CTS if required ... 
			if (useCTS) {
				retRFCSvcVersion = DSRFCSvcVersion.DSServiceV7_0_CTS;
			}
			break;
		}

		return(retRFCSvcVersion);
	} // end of determineRFCServiceVersionToBeUsed()


	public void execute()throws SAPAccessException {
		try {
			function.execute(this.destination);
		}
		catch(JCoException jcoExcpt) {
			throw new SAPAccessException("126900E", new String[] { jcoExcpt.getMessage() }, jcoExcpt); //$NON-NLS-1$
		}
	}


	private void init(SapSystem sapSystem, String ctsPackage, String ctsRequest, String ctsRequestDesc, boolean createSeparateTransports) throws SAPAccessException {	
		JCoRepository  jcoRepository;
		String         sapRFCServiceName;

		this.sapPackage = ctsPackage;
		this.ctsRequest = ctsRequest;
		this.ctsRequestDesc = ctsRequestDesc;
		this.createSeparateTransports = createSeparateTransports;
		_bTransportRequestCreated	= false;
		_sReqIdForSingleTRCase = "";
		this.useCTS     = !(ctsPackage == null && ctsRequest == null);

		this.rfcSvcVersion = determineRFCServiceVersionToBeUsed(this.useCTS);
		sapRFCServiceName  = getServiceName(this.rfcSvcVersion);

		Activator.getLogger().finer(JCoService.class.getSimpleName() + ": DS RFC Function = " + sapRFCServiceName + 
                                                                     " - Version = " + getSupportedServiceVersion()); //$NON-NLS-1$ $NON-NLS-2$

		if (sapSystem == null) {
			throw new IllegalArgumentException("SAPSystem must not be null!!"); //$NON-NLS-1$
		}
		else {
			try {
				this.destination = RfcDestinationDataProvider.getDestination(sapSystem);

				if (this.destination == null) {
					throw new IllegalArgumentException("SAP Password must not be null!!"); //$NON-NLS-1$
				}
				else {
					jcoRepository = this.destination.getRepository();
					this.function = jcoRepository.getFunction(sapRFCServiceName);
				} // end of (else) if (this.destination == null)
			} // end of try
			catch(JCoException jcoExcpt) {
				throw new SAPAccessException("126900E", new String[] { jcoExcpt.getMessage() }, jcoExcpt); //$NON-NLS-1$
			}

			if (this.function == null) {
				String errMsg = MessageFormat.format(Messages.JCOService_1, sapRFCServiceName);

				throw new SAPAccessException("126900E", new String[] { errMsg }); //$NON-NLS-1$
			} // end of if (this.function == null)
		}
	} // end of init()


	protected JCoParameterList getExportParamList() {
		return(this.function.getExportParameterList());
	} // end of getExportParamList()


	protected JCoParameterList getImportParamList() {
		return(this.function.getImportParameterList());
	} // end of getImportParamList()


	public static JCoService getJCoService(SapSystem sapSystem) throws SAPAccessException {
		return(getJCoService(sapSystem, null, null, null, true));
	}


	public static JCoService getJCoService(SapSystem sapSystem, String ctsPackage, String ctsRequest, String ctsRequestDesc, boolean createSeparateTransports)  
          throws SAPAccessException {
		JCoService newService;

		newService = new JCoService(sapSystem, ctsPackage, ctsRequest, ctsRequestDesc, createSeparateTransports);

		return(newService);
	} // end of getJcoService()


	public List<String> getRequests(String devClass) throws SAPAccessException {
		JCoTable         requestTable;
		JCoParameterList inParamList;
		JCoParameterList tableParamList;
		String           request;
		List<String>     retRequestList;

		if (devClass == null || devClass.isEmpty()) {
			throw new IllegalArgumentException("devClass must not be null or empty.");  //$NON-NLS-1$
		}

		inParamList    = this.function.getImportParameterList();
		tableParamList = this.function.getTableParameterList();
		inParamList.clear();
		if (useCTS) {
			inParamList.setValue(SAP_RFC_IN_PAR_SERVICE_TYPE, SAP_JCO_FCTN_ABAP_CODE_LOAD_CTS);
		} 
		else {
			inParamList.setValue(SAP_RFC_IN_PAR_SERVICE_TYPE, SAP_JCO_FCTN_ABAP_CODE_LOAD);
		}
		inParamList.setValue(SAP_RFC_IN_PAR_REPORT_NAME, SAP_RFC_PARAM_SPECIAL_CHAR); // a character not allowed in report names: the german A + umlaut
		inParamList.setValue(SAP_RFC_IN_PAR_DEV_CLASS, devClass);

		execute();

		requestTable   = tableParamList.getTable(SAP_RFC_TBL_PAR_REQUESTS);
		retRequestList = new ArrayList<String>();
		do {
			request = requestTable.getString(SAP_RFC_VAL_KEY_TRKORR);
			retRequestList.add(request);
		} 
		while (requestTable.nextRow());

		return(retRequestList);
	}


	public List<String> getPackages() throws SAPAccessException {
		JCoTable         devClassTable;
		JCoParameterList inParamList    = this.function.getImportParameterList();
		JCoParameterList tableParamList = this.function.getTableParameterList();
		String           devClass;
		List<String>     retDevClassList;

		inParamList.clear();
		if (useCTS) {
			inParamList.setValue(SAP_RFC_IN_PAR_SERVICE_TYPE, SAP_JCO_FCTN_ABAP_CODE_LOAD_CTS); // $NON-NLS-1$
		} 
		else {
			inParamList.setValue(SAP_RFC_IN_PAR_SERVICE_TYPE, SAP_JCO_FCTN_ABAP_CODE_LOAD); // $NON-NLS-1$
		}
		inParamList.setValue(SAP_RFC_IN_PAR_REPORT_NAME, SAP_RFC_PARAM_SPECIAL_CHAR); // a character not allowed in report names: the german A + umlaut

		execute();

		devClassTable   = tableParamList.getTable(SAP_RFC_TBL_PAR_DEV_CLASSES);
		retDevClassList = new ArrayList<String>();
		do {
			devClass = devClassTable.getString(SAP_RFC_VAL_KEY_DEV_CLASS);
			retDevClassList.add(devClass);
		} 
		while (devClassTable.nextRow());

		return(retDevClassList);
	} // end of getPackages()


	public String getSAPDSServiceVersion() throws SAPAccessException {
		JCoParameterList inParamList  = this.function.getImportParameterList();
		JCoParameterList outParamList = this.function.getExportParameterList();
		String           retServiceVersion;

		inParamList.clear();
		inParamList.setValue(SAP_RFC_IN_PAR_SERVICE_TYPE, SAP_JCO_FCTN_GET_VERSION);

		execute();

		retServiceVersion = (String) outParamList.getValue(SAP_RFC_OUT_PAR_DSS_VERSION);
		retServiceVersion = retServiceVersion.trim();

		return(retServiceVersion);
	} // end of getSAPDSServiceVersion()


	private static String getServiceName(DSRFCSvcVersion rfcSvcVersion) {
		String retServiceName;

		switch(rfcSvcVersion) {
			case DSServiceV7_0:
				  retServiceName = SAP_RFC_DS_SERVICE_NAME_V7_0;
				  break;

			case DSServiceV7_0_CTS:
				  retServiceName = SAP_RFC_DS_SERVICE_NAME_V7_0_CTS;
				  break;

			case DSServiceV7_1:
				  retServiceName = SAP_RFC_DS_SERVICE_NAME_V7_1;
				  break;  
			case DSServiceV8_0:
			default:
				  retServiceName = SAP_RFC_DS_SERVICE_NAME_V8_0;
				  break;
		}

		return(retServiceName);
	} // end of getServiceName()


	private static String getServiceVersion(DSRFCSvcVersion rfcSvcVersion) {
		String retServiceVer;

		switch(rfcSvcVersion) {
			case DSServiceV7_0:
			case DSServiceV7_0_CTS:
				  retServiceVer = SAP_RFC_DS_SERVICE_VERSION_V7_0;
				  break;

			case DSServiceV7_1:
				retServiceVer = SAP_RFC_DS_SERVICE_VERSION_V7_1;
				break;
			case DSServiceV8_0:
			default:
				  retServiceVer = SAP_RFC_DS_SERVICE_VERSION_V8_0;
		}

		return(retServiceVer);
	} // end of getServiceVersion()


	public static String getSupportedServiceName() {
		DSRFCSvcVersion curVersion;

		// assume CTS is not used
		curVersion = determineRFCServiceVersionToBeUsed(false);
		return(getServiceName(curVersion));
	} // end of getSupportedServiceName()


	public static String getSupportedServiceVersion() {
		DSRFCSvcVersion curVersion;

		// assume CTS is not used
		curVersion = determineRFCServiceVersionToBeUsed(false);
		return(getServiceVersion(curVersion));
	} // end of getSupportedServiceVersion()


	protected JCoParameterList getTableParamList() {
		return(this.function.getTableParameterList());
	} // end of getTableParamList()


	public void uploadAbapReport(String reportName, String[] codeFragments) throws SAPAccessException {
		JCoParameterList inParamList;
		JCoParameterList outParamList = this.function.getExportParameterList();
		
		if (reportName == null) {
			throw new IllegalArgumentException("'reportName' is null");
		}
		if (codeFragments == null) {
			throw new IllegalArgumentException("'codeFragments' is null");
		}

		inParamList = this.function.getImportParameterList();
		reportName  = reportName.trim().toUpperCase();

		inParamList.clear();
		inParamList.setValue(SAP_RFC_IN_PAR_REPORT_NAME, reportName);

		if (this.useCTS) {
			inParamList.setValue(SAP_RFC_IN_PAR_SERVICE_TYPE, SAP_JCO_FCTN_ABAP_CODE_LOAD_CTS);
			inParamList.setValue(SAP_RFC_IN_PAR_DEV_CLASS, this.sapPackage);
			inParamList.setValue(SAP_RFC_IN_PAR_OVERWRITE_OVERRIDE, SAP_RFC_PARAM_BOOL_ENABLE);

			if (!this.sapPackage.equals(SAP_RFC_TEMP_DEV_CLASS)) {
				
				String ctsRequestIdToBeUsed=this.ctsRequest;
				if(false == createSeparateTransports && false == _sReqIdForSingleTRCase.equalsIgnoreCase(""))
					ctsRequestIdToBeUsed=_sReqIdForSingleTRCase;
					
				inParamList.setValue(SAP_RFC_IN_PAR_REQUEST, ctsRequestIdToBeUsed);
				inParamList.setValue(SAP_RFC_IN_PAR_REQUEST_DESC, this.ctsRequestDesc);
			}
		} 
		else {
			inParamList.setValue(SAP_RFC_IN_PAR_SERVICE_TYPE, SAP_JCO_FCTN_ABAP_CODE_LOAD);
		}

		JCoTable codeTable = this.function.getTableParameterList().getTable(SAP_RFC_TBL_PAR_CODE);
		codeTable.clear();
		for (int i = 0; i < codeFragments.length; i++) {
			codeTable.appendRow();
			codeTable.setValue(SAP_RFC_VAL_KEY_LINE, codeFragments[i]);
		}

		execute();
		
		if (this.useCTS) 
		{
			if(!createSeparateTransports){ //Create Single TR case

				String ctsRequestIdRecieved = (String) outParamList.getValue(SAP_RFC_EX_PAR_REQ); //Get the TR returned by SAP

				if(_bTransportRequestCreated ==false && !ctsRequestIdRecieved.equalsIgnoreCase("")){
					_sReqIdForSingleTRCase=ctsRequestIdRecieved;
					_bTransportRequestCreated=true;
				}
			}
		}
	} // end of uploadAbapReport()

} // end of class JcoService
