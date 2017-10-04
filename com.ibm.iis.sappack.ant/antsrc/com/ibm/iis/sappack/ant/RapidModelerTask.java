package com.ibm.iis.sappack.ant;

import java.text.MessageFormat;
import java.util.Date;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.ibm.iis.sappack.gen.tools.sap.api.RapidModeler;
import com.ibm.iis.sappack.gen.tools.sap.api.RapidModelerFactory;

public class RapidModelerTask extends Task {

	private String sapSystemName;
	private String sapUser;
	private String sapPassword;
	private String sapObjectCollectionFile;
	private String rapidModelerConfigurationFile;
	private String ldmFile;
	private String checkTableLDMFile = null;
	
	public RapidModelerTask() {
		super();
	}

	public void setSapSystemName(String sapSystemName) {
		this.sapSystemName = sapSystemName;
	}

	public void setSapObjectListFile(String sapObjectListFile) {
		this.sapObjectCollectionFile = sapObjectListFile;
	}

	public void setRapidModelerConfigurationFile(String rapidModelerConfigurationFile) {
		this.rapidModelerConfigurationFile = rapidModelerConfigurationFile;
	}

	public void setLdmFile(String ldmFile) {
		this.ldmFile = ldmFile;
	}
	
	public void setCheckTableLDMFile(String checkTableLDMFile) {
		this.checkTableLDMFile = checkTableLDMFile;
	}
	
	public void setSapUser(String sapUser) {
		this.sapUser = sapUser;
	}
	
	public void setSapPassword(String sapPassword) {
		this.sapPassword = sapPassword;
	}

	@SuppressWarnings("nls")
	@Override
	public void execute() throws BuildException {
		try {
			log("=============================================", Project.MSG_INFO);
			log(MessageFormat.format("Starting Rapid Modeler for SAP on ''{0}''", new Date().toString()), Project.MSG_INFO); 
			log(MessageFormat.format("SAP system name: ''{0}''", this.sapSystemName), Project.MSG_INFO);
			log(MessageFormat.format("SAP object list: ''{0}''", this.sapObjectCollectionFile), Project.MSG_INFO);
			log(MessageFormat.format("Rapid Modeler configuration: ''{0}''", this.rapidModelerConfigurationFile), Project.MSG_INFO);
			log(MessageFormat.format("Logical data model: ''{0}''", this.ldmFile), Project.MSG_INFO);
			log(MessageFormat.format("Check table logical data model: ''{0}''", this.checkTableLDMFile == null ? "none" : this.ldmFile), Project.MSG_INFO);
			RapidModeler modeler = RapidModelerFactory.createRapidModeler();
			modeler.importMetadata(sapSystemName, sapUser, sapPassword, 
					sapObjectCollectionFile, rapidModelerConfigurationFile, ldmFile, checkTableLDMFile, false, null);
			log(MessageFormat.format("Rapid modeler finished successully on ''{0}''", new Date().toString()), Project.MSG_INFO);
			log("=============================================", Project.MSG_INFO);
		} catch (Exception exc) {
			exc.printStackTrace();
			exc.printStackTrace(System.out);
			log(exc, Project.MSG_ERR);
			throw new BuildException(exc);
		}
	}

}
