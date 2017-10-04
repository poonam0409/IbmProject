package com.ibm.is.sappack.cw.app.data.bdr;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class FieldUsageReport {

	private int reportId;
	private String processChain;
	private String businessObjectName;
	private int tableUsageId;
	private boolean required;
	private FieldUsageUseMode useMode;
	private FieldUsageStatus status = FieldUsageStatus.OK;
	private String globalTemplate;

	public String getGlobalTemplate() {
		return globalTemplate;
	}

	public void setGlobalTemplate(String globalTemplate) {
		this.globalTemplate = globalTemplate;
	}

	public int getReportId() {
		return reportId;
	}
	
	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	public String getProcessChain() {
		return processChain;
	}

	public void setProcessChain(String processChain) {
		this.processChain = processChain;
	}

	public String getBusinessObjectName() {
		return businessObjectName;
	}

	public void setBusinessObjectName(String businessObjectName) {
		this.businessObjectName = businessObjectName;
	}

	public int getTableUsageId() {
		return tableUsageId;
	}

	public void setTableUsageId(int tableUsageId) {
		this.tableUsageId = tableUsageId;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	@JsonSerialize(using = FieldUsageUseModeSerializer.class)
	public FieldUsageUseMode getUseMode() {
		return useMode;
	}

	@JsonDeserialize(using = FieldUsageUseModeDeserializer.class)
	public void setUseMode(FieldUsageUseMode useMode) {
		this.useMode = useMode;
	}

	@JsonSerialize(using = FieldUsageStatusSerializer.class)
	public FieldUsageStatus getStatus() {
		return status;
	}

	@JsonDeserialize(using = FieldUsageStatusDeserializer.class)
	public void setStatus(FieldUsageStatus status) {
		if (this.status != FieldUsageStatus.REQUIRED_BUT_FOLLOWUP
				&& this.status != FieldUsageStatus.REQUIRED_BUT_NOT_IN_SCOPE
				&& this.status != FieldUsageStatus.REQUIRED_BUT_BLANK) {
			this.status = status;
		}
	}
}
