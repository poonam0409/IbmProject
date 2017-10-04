package com.ibm.is.sappack.cw.app.data.bdr.jpa;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageStatus;
import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageStatusDeserializer;
import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageStatusSerializer;
import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageUseMode;
import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageUseModeDeserializer;
import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageUseModeSerializer;

@Entity(name = "FIELDUSAGE")
@JsonIgnoreProperties(ignoreUnknown=true)
public class FieldUsage implements Serializable, Comparable<FieldUsage> {

	private static final long serialVersionUID = 1L;

	// Sortable columns
	public static final List<String> BDR_FIELD_USAGE_SORTABLE_COLUMNS
			= Arrays.asList("required", "useMode", "comment");
	public static final List<String> BDR_FIELD_USAGE_SORTABLE_COLUMNS_FROM_FIELD
			= Arrays.asList("name", "recommended", "sapView", "description", "checkTable");

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "ID", updatable = false)
	private Integer fieldUsageId;

	@ManyToOne(fetch = FetchType.LAZY)
	private Field field;

	@ManyToOne(fetch = FetchType.LAZY)
	private TableUsage tableUsage;

	@Basic
	@Column(name = "REQ")
	private boolean required = false;
	
	@Column(name = "USE")
	@Enumerated(EnumType.STRING)
	private FieldUsageUseMode useMode = FieldUsageUseMode.BLANK;

	@Column(name = "COMMENT", length = Resources.BDR_LENGTH_COMMENT * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String comment;
	
	@Column(name = "GT")
	private String globalTemplate;

	public String getGlobalTemplate() {
		return globalTemplate;
	}

	public void setGlobalTemplate(String globalTemplate) {
		this.globalTemplate = globalTemplate;
	}

	@Transient
	private String name;

	@Transient
	private String description;

	@Transient
	private boolean recommended = false;
	
	@Transient
	private String sapView;
	
	@Transient
	private FieldUsageStatus status = FieldUsageStatus.OK;
	
	@Column(name = "CHECKTABLE", length = Resources.BDR_LENGTH_CHECK_TABLE * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String checkTable;


	// TODO make the constructor require a field and a table usage
	public FieldUsage() {
		this.useMode = FieldUsageUseMode.BLANK;
	}
	
	public void setFieldUsageId(Integer fieldUsageId) {
		this.fieldUsageId = fieldUsageId;
	}

	public Integer getFieldUsageId() {
		return fieldUsageId;
	}

	@JsonBackReference("field-fieldusage")
	public void setField(Field field) {
		this.field = field;
	}

	@JsonBackReference("field-fieldusage")
	public Field getField() {
		return field;
	}

	@JsonBackReference("fieldusage-tableusage")
	public void setTableUsage(TableUsage tableUsage) {
		this.tableUsage = tableUsage;
	}

	@JsonBackReference("fieldusage-tableusage")
	public TableUsage getTableUsage() {
		return tableUsage;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		if (field != null) {
			return field.getName();
		}

		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		if (field != null) {
			return field.getDescription();
		}

		return description;
	}

	public void setRecommended(boolean recommended) {
		this.recommended = recommended;
	}

	public boolean getRecommended() {
		if (field != null) {
			return field.getRecommended();
		}
		
		return recommended;
	}
	
	public void setSapView(String sapView) {
		this.sapView = sapView;
	}

	public String getSapView() {
		if (field != null) {
			return field.getSapView();
		}

		return sapView;
	}

	public void setCheckTable(String checkTable) {
		this.checkTable = checkTable;
	}

	public String getCheckTable() {
		if (field != null) {
			return field.getCheckTable();
		}

		return checkTable;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean getRequired() {
		return required;
	}

	@JsonDeserialize(using = FieldUsageUseModeDeserializer.class)
	public void setUseMode(FieldUsageUseMode useMode) {
		this.useMode = useMode;
	}

	@JsonSerialize(using = FieldUsageUseModeSerializer.class)
	public FieldUsageUseMode getUseMode() {
		//change usage mode for deprecated values to new values
		
		if (useMode != null) {
			if (useMode ==	FieldUsageUseMode.READ || useMode == FieldUsageUseMode.WRITE) {
				useMode = FieldUsageUseMode.INSCOPE;
			} else if (useMode == FieldUsageUseMode.UNUSED) {
				useMode = FieldUsageUseMode.NOTINSCOPE;
			}
		}
		return useMode;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment == null ? "" : comment;
	}

	@JsonDeserialize(using = FieldUsageStatusDeserializer.class)
	public void setStatus(FieldUsageStatus status) {
		this.status = status;
	}

	@JsonSerialize(using = FieldUsageStatusSerializer.class)
	public FieldUsageStatus getStatus() {
		if (required) {
			switch (useMode) {
				case FOLLOWUP :
					status = FieldUsageStatus.REQUIRED_BUT_FOLLOWUP;
					break;
				case NOTINSCOPE :
					status = FieldUsageStatus.REQUIRED_BUT_NOT_IN_SCOPE;
					break;
				case BLANK :
					status = FieldUsageStatus.REQUIRED_BUT_BLANK;
					break;
				default :
					break;
			}
		}
		
		return status;
	}

	public void updateFrom(FieldUsage fieldUsage) {
		this.required = fieldUsage.getRequired();
		this.useMode = fieldUsage.getUseMode();
		this.comment = fieldUsage.getComment();
		this.status = fieldUsage.getStatus();
		this.globalTemplate = fieldUsage.getGlobalTemplate();
	}

	@Override
	public int compareTo(FieldUsage fu) {
		if (name != null && fu.getName() != null) {
			return name.compareTo(fu.getName());
		}

		return 0;
	}
}
