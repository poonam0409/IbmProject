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


import java.util.List;

import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.sap.conn.jco.JCoException;


public class IDocType {

	private String idoctypeName;
	private String basicTypeName;
	private boolean isExtendedIdocType;
	private String description;
	private IDocTypeMetaData metaData;
	private SapIDocTypeBrowser sapBrowser;
	private String release;


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public IDocType(String name, boolean isExtendedIdocType, String descrp, String release, SapIDocTypeBrowser sapBrowser) {
		this.idoctypeName = name;
		this.description = descrp;
		this.sapBrowser = sapBrowser;
		this.metaData = null;
		this.isExtendedIdocType = isExtendedIdocType;
		this.release = release;
		if (this.release == null) {
			this.release = Constants.IDOC_EMPTY_RELEASE;
		}
	}

	public String getName() {

		return this.idoctypeName;
	}

	public String getBasicType() {

		return this.basicTypeName;
	}

	public String getDescription() {

		return this.description;

	}

	public boolean isExtendedIDocType() {
		return this.isExtendedIdocType;
	}

	public SapIDocTypeBrowser getSapBrowser() {

		return this.sapBrowser;
	}

	public IDocTypeMetaData getMetaData() throws JCoException {
		if (this.metaData == null) {
			this.metaData = this.sapBrowser.getIDocTypeMetaData(this);
		}
		return this.metaData;
	}

	// The following 4 Methods may be replaced by calling the methods in
	// IDocTypeMetaData

	public List<MessageType> getMessageTypes() throws JCoException {

		return this.getMetaData().getMessageTypes();
	}

	public List<Segment> getSegments() throws JCoException {

		return this.getMetaData().getSegments();
	}

	public List<Segment> getRootSegments() throws JCoException {
		return this.getMetaData().getRootSegments();
	}

	public Segment getSegment(String segmentName) throws JCoException {
		return this.getMetaData().getSegment(segmentName);
	}
	
	public boolean exists() throws JCoException {
		return this.getMetaData() != null;
	}

	public void setBasicType(String basicTypeName) {

		this.basicTypeName = basicTypeName;
	}
	
	public String getRelease() {
		return this.release;
	}

}
