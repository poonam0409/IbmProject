package com.ibm.is.sappack.cw.app.services.bdr;

import java.util.ArrayList;
import java.util.Date;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.ProcessStep;

public class CsvProcessStep {

	private ProcessStep processStep;
	private ArrayList<String[]> content;

	public CsvProcessStep(String stepName, String stepDescription, Date creationDate) {
		this.processStep = new ProcessStep();
		this.processStep.setName(stepName);
		this.processStep.setDescription(stepDescription);
		this.processStep.setUpdated(creationDate);
		this.processStep.setType(Resources.BPH_TYPE_PROCESSSTEP);
	}

	public CsvProcessStep(ProcessStep processStep) {
		setProcessStep(processStep);
	}

	public ProcessStep getProcessStep() {
		return this.processStep;
	}

	public void setProcessStep(ProcessStep processStep) {
		this.processStep = processStep;
	}

	public ArrayList<String[]> getContent() {
		if(this.content == null) {
			this.content = new ArrayList<String[]>();
		}
		return this.content;
	}
	
}
