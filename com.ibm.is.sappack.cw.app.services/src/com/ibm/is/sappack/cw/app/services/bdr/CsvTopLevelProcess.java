package com.ibm.is.sappack.cw.app.services.bdr;

import java.util.ArrayList;
import java.util.Date;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;

public class CsvTopLevelProcess {

	private Process process;
	private ArrayList<String[]> content;
	
	public CsvTopLevelProcess(String name, String description, Date creationDate) {
		this.process = new Process();
		this.process.setName(name);
		this.process.setDescription(description);
		this.process.setUpdated(creationDate);
		this.process.setType(Resources.BPH_TYPE_PROCESS);
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process topLevelProcess) {
		this.process = topLevelProcess;
	}
	
	public ArrayList<String[]> getContent() {
		if(content == null) {
			content = new ArrayList<String[]>();
		}
		return content;
	}

	public void setContent(ArrayList<String[]> content) {
		this.content = content;
	}
	
}
