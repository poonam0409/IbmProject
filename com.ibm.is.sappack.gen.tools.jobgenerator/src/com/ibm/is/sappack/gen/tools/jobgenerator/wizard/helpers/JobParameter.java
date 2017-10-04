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
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers;


public class JobParameter {

	private String  name;
	private String  prompt;
	private String  type;
	private String  defaultValue;
	private String  help;
   private boolean isUserCreated;


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public JobParameter(String name, String prompt, String type, String defaultValue, String help) {
		this.name = name;
		this.prompt = prompt;
		this.type = type;
		this.defaultValue = defaultValue;
		this.help = help;
		this.isUserCreated = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

   public void setUserCreated() {
      this.isUserCreated = true;
   }

   public boolean isUserCreated() {
      return(this.isUserCreated);
   }

	public String toString() {
		return "name:" + this.name + "\tprompt:" + this.prompt + "\ttype:" + this.type + "\tdefault:" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ this.defaultValue + "\thelp:" + this.help; //$NON-NLS-1$
	}

	public boolean isNameEmpty() {
		return (this.name == null) || this.name.trim().equals(""); //$NON-NLS-1$
	}

	public boolean isPromptEmpty() {
		return (this.prompt == null) || this.prompt.trim().equals(""); //$NON-NLS-1$
	}

	public boolean isTypeEmpty() {
		return (this.type == null) || this.type.trim().equals(""); //$NON-NLS-1$
	}

	public boolean isDefaultValueEmpty() {
		return (this.defaultValue == null) || this.defaultValue.trim().equals(""); //$NON-NLS-1$
	}

	public boolean isHelpEmpty() {
		return (this.help == null) || this.help.trim().equals(""); //$NON-NLS-1$
	}

	public boolean isNameEqual(JobParameter otherParameter) {
		return this.name.trim().equalsIgnoreCase(otherParameter.name.trim());
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof JobParameter) {
			JobParameter jp = (JobParameter) obj;
			if (jp.getName().equals(this.getName())                 && 
			    jp.getPrompt().equals(this.getPrompt())             && 
			    jp.getType().equals(this.getType())                 &&
			    jp.getDefaultValue().equals(this.getDefaultValue()) && 
			    jp.getHelp().equals(this.getHelp())) {

				return true;
			}

		}
		return false;
	}

}
