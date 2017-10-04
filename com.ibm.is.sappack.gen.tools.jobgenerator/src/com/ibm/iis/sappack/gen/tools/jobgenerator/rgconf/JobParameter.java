//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf;


import java.util.StringTokenizer;

import com.ibm.is.sappack.gen.tools.sap.constants.Constants;


public class JobParameter {
	public static final String TYPE_NAME_PARAMETER_SET = "Parameter Set"; //$NON-NLS-1$
	public static final String TYPE_NAME_TIME          = "Time";          //$NON-NLS-1$
	public static final String TYPE_NAME_DATE          = "Date";          //$NON-NLS-1$
	public static final String TYPE_NAME_LIST          = "List";          //$NON-NLS-1$
	public static final String TYPE_NAME_PATHNAME      = "Pathname";      //$NON-NLS-1$
	public static final String TYPE_NAME_FLOAT         = "Float";         //$NON-NLS-1$
	public static final String TYPE_NAME_INTEGER       = "Integer";       //$NON-NLS-1$
	public static final String TYPE_NAME_ENCRYPTED     = "Encrypted";     //$NON-NLS-1$
	public static final String TYPE_NAME_STRING        = "String";        //$NON-NLS-1$

	private String name;
	private String prompt;
	private String type;
	private String defaultValue;
	private String help;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public JobParameter(String name, String prompt, String type, String defaultValue, String help) {
		this.name = name;
		this.prompt = prompt;
		this.type = type;
		this.defaultValue = defaultValue;
		this.help = help;
	}


	public String toString() {
		return "name:" + this.name + "\tprompt:" + this.prompt + "\ttype:" + this.type + "\tdefault:" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ this.defaultValue + "\thelp:" + this.help; //$NON-NLS-1$
	}

	public String getName() {
		return this.name;
	}

	public String getPrompt() {
		return this.prompt;
	}

	public String getType() {
		return this.type;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public String getHelp() {
		return this.help;
	}

	public String convertToString() {
		return(this.name + Constants.JOB_PARAM_SEPARATOR   +
		       this.prompt + Constants.JOB_PARAM_SEPARATOR + 
		       this.type + Constants.JOB_PARAM_SEPARATOR   + 
		       this.defaultValue + Constants.JOB_PARAM_SEPARATOR +
		       this.help);
	}

	static String getNextToken(StringTokenizer tok) {
		if (!tok.hasMoreTokens()) {
			return ""; //$NON-NLS-1$
		}
		String s = tok.nextToken();
		if (Constants.JOB_PARAM_SEPARATOR.equals(s)) {
			return ""; //$NON-NLS-1$
		}
		if (tok.hasMoreTokens()) {
			tok.nextToken();
		}
		return s;
	}

	public static JobParameter createFromString(String s) {
		StringTokenizer tok = new StringTokenizer(s, Constants.JOB_PARAM_SEPARATOR, true);
		String name = getNextToken(tok);
		String prompt = getNextToken(tok);
		String type = getNextToken(tok);
		String defaultValue = getNextToken(tok);
		String help = getNextToken(tok);

		JobParameter param = new JobParameter(name, prompt, type, defaultValue, help);
		return param;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof JobParameter)) {
			return false;
		}
		JobParameter other = (JobParameter) obj;
		return other.name.equals(this.name);
	}

	@Override
	public int hashCode() {
		return (this.name + this.type).hashCode();
	}
		
	public boolean isParameterSet() {
		boolean isParamSet = false;

		if (this.type != null) {
			isParamSet = this.type.equalsIgnoreCase(TYPE_NAME_PARAMETER_SET);
		}

		return(isParamSet);
	}
}
