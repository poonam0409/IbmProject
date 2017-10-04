package com.ibm.is.sappack.cw.app.data;

@SuppressWarnings("nls")
public final class Resources {
	
	public static enum DATABASE_TYPE {
		DB2,
		ORACLE,
		UNKNOWN
	}

	public static final String INITIAL_CONTEXT_ID = "java:comp/env";
	public static final String PERSISTENCE_CONTEXT_ID = "persistence/cwapp";
	public static final String USERTRANSACTION_CONTEXT_ID = "java:comp/UserTransaction";
	public static final String USERREGISTRY_CONTEXT_ID = "UserRegistry";

	public static final String EMPTY_STRING = "";
	public static final String STAR_STRING = "*";
	
	public static final String CW_DATABASE_ID = "jdbc/CWDatabase";
	public static final String CWAPP_DATABASE_ID = "jdbc/CWAppDatabase";
	
	public static final String ORACLE = "Oracle";
	public static final String DB2 = "DB2";
	
	public static final String MANDT_DOMAIN = "MANDT";
	
	public static final String BPH_TYPE_PROCESS = "Process";
	public static final String BPH_TYPE_BUSINESSOBJECT = "BusinessObject";
	public static final String BPH_TYPE_PROCESSSTEP = "ProcessStep";
	public static final String BPH_TYPE_TABLE = "Table";
	public static final String BPH_TYPE_TABLE_USAGE = "TableUsage";
	
	public static final String BDR_TABLE_USAGE_NAME_SEPARATOR = " > ";
	public static final int BDR_TABLE_USAGE_NAME_SEPARATOR_LENGTH = 3; // IMPORTANT: This must be kept current!
	
	// Field length limits, based on the CW tables where applicable
	// Need to match the ones in Constants.js
	
	public static final int BDR_LENGTH_PROCESS_NAME = 255;
	public static final int BDR_LENGTH_DESCRIPTION = 255;
	public static final int BDR_LENGTH_PROCESS_STEP_NAME = 255;
	public static final int BDR_LENGTH_BO_NAME = 255;
	public static final int BDR_LENGTH_BO_SHORTNAME = 10; // Workaround: We write BO names into the LOB field when exporting to CWDB. The LOB name length is 10. 
	public static final int BDR_LENGTH_TABLE_NAME = 30;
	public static final int BDR_LENGTH_FIELD_NAME = 30;
	public static final int BDR_LENGTH_CW_LEGACY_ID = 20;
	public static final int BDR_LENGTH_ROLLOUT = 30;
	public static final int BDR_LENGTH_SAP_VIEW = 100;
	public static final int BDR_LENGTH_CHECK_TABLE = 30;
	public static final int BDR_LENGTH_COMMENT = 255;
	public static final int BDR_LENGTH_TRANSACTION_NAME = 100;
	public static final int BDR_LENGTH_BYTE_MULTIPLIER = 4;
}
