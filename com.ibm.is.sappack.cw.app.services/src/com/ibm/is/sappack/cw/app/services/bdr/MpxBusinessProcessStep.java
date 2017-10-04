package com.ibm.is.sappack.cw.app.services.bdr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wink.json4j.JSONException;

import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.ProcessStep;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.ImportProcessStepProvider;

public class MpxBusinessProcessStep {
	
	private ProcessStep processStep;

	public MpxBusinessProcessStep(String name, Date creationDate, Process parent, ImportProcessStepProvider importProcessStepProvider) throws JSONException {
		this.processStep = importProcessStepProvider.provideProcessStep(parent, name, Constants.EMPTY_STRING, creationDate, Constants.EMPTY_STRING);
	}

	public ProcessStep getProcessStep() {
		return processStep;
	}

	public void setProcessStep(ProcessStep processStep) {
		this.processStep = processStep;
	}

	public void setParent(Process parent) {
		if(parent != null) {
			this.processStep.setParentProcess(parent);
			List<ProcessStep> childProcesses = (List<ProcessStep>)parent.getProcessSteps();
			if(childProcesses == null || childProcesses.isEmpty()) {
				childProcesses = new ArrayList<ProcessStep>();
				parent.setProcessSteps(childProcesses);
			}
			childProcesses.add(processStep);
		}
	}
	
}
