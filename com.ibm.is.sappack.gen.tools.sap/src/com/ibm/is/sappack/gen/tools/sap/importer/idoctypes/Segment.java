//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.idoctypes
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.idoctypes;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.IDocField;


public class Segment {

	protected IDocType typeOfIDoc;
	protected String segmentType;
	protected String segmentDef;
	protected int segmentNr;
	protected String segmentDescription;
	protected long segmentMaxOccurrence;
	protected long segmentMinOccurrence;
	protected Segment parent;
	protected List<Segment> children;
	protected List<IDocField> fields;
	public boolean hasChild;
	public boolean mandatory;
	protected long groupMaxOccurence;
	protected long groupMinOccurence;
	protected boolean parentFlag;
	protected boolean selected;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private Segment() {
		this.fields = new ArrayList<IDocField>();
		this.children = new ArrayList<Segment>();
		this.selected = false;
	}

	public Segment(IDocType idoctype) {
		this();
		this.typeOfIDoc = idoctype;
		this.hasChild = false;
		this.selected = false;
	}

	public long getGroupMaxOccurence() {
		return this.groupMaxOccurence;
	}

	public void setGroupMaxOccurence(long groupMaxOccurence) {
		this.groupMaxOccurence = groupMaxOccurence;
	}

	public long getGroupMinOccurence() {
		return this.groupMinOccurence;
	}

	public void setGroupMinOccurence(long groupMinOccurence) {
		this.groupMinOccurence = groupMinOccurence;
	}

	public boolean isParentFlag() {
		return this.parentFlag;
	}

	public void setParentFlag(String parentFlag) {
		this.parentFlag = parentFlag.trim().equalsIgnoreCase("X"); //$NON-NLS-1$
	}

	public IDocType getIDocType() {
		return this.typeOfIDoc;
	}

	public int getNr() {
		return this.segmentNr;
	}

	public String getType() {
		return this.segmentType;

	}

	public String getDefinition() {
		return this.segmentDef;

	}

	public String getDescription() {
		return this.segmentDescription;
	}
	
	public void setDescription(String description) {
		this.segmentDescription = description;
	}

	public Segment getParentSegment() {
		return this.parent;
	}

	public List<Segment> getChildren() {

		return this.children;

	}

	public List<IDocField> getFields() {
		return this.fields;
	}

	public long getMaxOccurrence() {
		return this.segmentMaxOccurrence;
	}

	public long getMinOccurrence() {
		return this.segmentMinOccurrence;
	}

	public void setMaxOccurrence(long segmentMaxOccurence) {
		this.segmentMaxOccurrence = segmentMaxOccurence;
	}

	public void setMinOccurrence(long segmentMinOccurence) {
		this.segmentMinOccurrence = segmentMinOccurence;
	}

	public void addField(IDocField field) {
		this.fields.add(field);
	}
	
	public String getTableName() {
		return this.getIDocType().getName() + Constants.LDM_ENTITY_NAME_SEPARATOR + this.getDefinition();
	}
	
	public String getParentTableName() {
		return this.getIDocType().getName() + Constants.LDM_ENTITY_NAME_SEPARATOR + this.getParentSegment().getDefinition();
	}

	/**
	 * getAllCheckTables
	 * 
	 * return the check table names of all fields of this idoc segment as a
	 * comma separated string
	 * 
	 * @return
	 */
	public String getAllCheckTablesAsString() {

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < this.getFields().size(); i++) {
			IDocField field = this.getFields().get(i);
			String checkTable = field.getCheckTable();
			if (checkTable != null) {
				// don't add tables twice
				if (sb.indexOf(checkTable) == -1) {
					sb.append(field.getCheckTable()).append(","); //$NON-NLS-1$
				}
			}
		}
		// remove trailing comma
		int length = sb.length();
		if(length > 0 && sb.charAt(length-1) == ',') {
			sb.deleteCharAt(length-1);
		}

		return sb.toString();
	}

	
	public Collection<String> getAllCheckTableNames() {
		
		Set<String> result = new HashSet<String>();
		for (int i = 0; i < this.getFields().size(); i++) {
			IDocField field = this.getFields().get(i);
			String checkTable = field.getCheckTable();
			if (checkTable != null) {
				result.add(checkTable);
			}
		}
		return result;
	}

}
