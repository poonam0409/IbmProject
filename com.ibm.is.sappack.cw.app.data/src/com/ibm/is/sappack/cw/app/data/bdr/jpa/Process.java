package com.ibm.is.sappack.cw.app.data.bdr.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.bdr.ApprovalStatus;
import com.ibm.is.sappack.cw.app.data.bdr.ApprovalStatusDeserializer;
import com.ibm.is.sappack.cw.app.data.bdr.ApprovalStatusSerializer;
import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageStatus;
import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageStatusDeserializer;
import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageStatusSerializer;

@Entity(name = "PROCESS")
public class Process implements Serializable, Comparable<Process> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "ID", updatable = false)
	private Integer processId;

	@ManyToOne(fetch = FetchType.LAZY)
	private Process parentProcess;

	@OneToMany(mappedBy = "parentProcess", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private List<Process> childProcesses;

	@OneToMany(mappedBy = "parentProcess", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private List<ProcessStep> processSteps;

	@Column(name = "NAME", length = Resources.BDR_LENGTH_PROCESS_NAME * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String name;

	@Column(name = "DESCRIPTION", length = Resources.BDR_LENGTH_DESCRIPTION * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String description;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "UPDATED")
	private Date updated;

	@Transient
	private String type = Resources.BPH_TYPE_PROCESS;

	@Transient
	private List<Process> aclChildProcesses;

	@Transient
	private ApprovalStatus approvalStatus = ApprovalStatus.UNDEFINED;

	@Transient
	private FieldUsageStatus fieldUsageStatus = FieldUsageStatus.OK;

	// TODO make the constructor require a field
	public Process() {
	}

	// Setters / getters

	public void setType(String type) {
		this.type = type;
		this.type = Resources.BPH_TYPE_PROCESS;
	}

	public String getType() {
		return type;
	}

	@JsonProperty(value = "id")
	public void setProcessId(Integer processId) {
		this.processId = processId;
	}

	@JsonProperty(value = "id")
	public Integer getProcessId() {
		return processId;
	}

	@JsonBackReference("process-process")
	public void setParentProcess(Process parentProcess) {
		this.parentProcess = parentProcess;
	}

	@JsonBackReference("process-process")
	public Process getParentProcess() {
		return parentProcess;
	}

	@JsonManagedReference("process-process")
	public void setChildProcesses(List<Process> childProcesses) {
		this.childProcesses = childProcesses;
	}

	@JsonManagedReference("process-process")
	public List<Process> getChildProcesses() {
		if (childProcesses != null) {
			Collections.sort(childProcesses);
		} else {
			childProcesses = new ArrayList<Process>(0);
		}

		return childProcesses;
	}

	@JsonManagedReference("process-processstep")
	public void setProcessSteps(List<ProcessStep> processSteps) {
		this.processSteps = processSteps;
	}

	@JsonManagedReference("process-processstep")
	public Collection<ProcessStep> getProcessSteps() {
		if (processSteps != null) {
			Collections.sort(processSteps);
		} else {
			processSteps = new ArrayList<ProcessStep>(0);
		}

		return processSteps;
	}

	public void setName(String name) {
		if (name != null && name.length() > Resources.BDR_LENGTH_PROCESS_NAME) {
			this.name = name.substring(0, Resources.BDR_LENGTH_PROCESS_NAME);
		} else {
			this.name = name;
		}
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Collection<Process> getAclChildProcesses() {
		if (aclChildProcesses != null) {
			Collections.sort(aclChildProcesses);
		} else {
			aclChildProcesses = new ArrayList<Process>(0);
		}

		return aclChildProcesses;
	}

	public void setAclChildProcesses(List<Process> aclChildProcesses) {
		this.aclChildProcesses = aclChildProcesses;
	}

	public void addAclChildProcess(Process childProcess) {
		this.aclChildProcesses.add(childProcess);
	}

	@JsonSerialize(using = ApprovalStatusSerializer.class)
	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	@JsonDeserialize(using = ApprovalStatusDeserializer.class)
	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	
	@JsonSerialize(using = FieldUsageStatusSerializer.class)
	public FieldUsageStatus getFieldUsageStatus() {
		return fieldUsageStatus;
	}
	
	@JsonDeserialize(using = FieldUsageStatusDeserializer.class)
	public void setFieldUsageStatus(FieldUsageStatus fieldUsageStatus) {
		this.fieldUsageStatus = fieldUsageStatus;
	}

	@Override
	public int compareTo(Process p) {
		if (name != null && p.getName() != null) {
			return name.compareTo(p.getName());
		}

		return 0;
	}
}
