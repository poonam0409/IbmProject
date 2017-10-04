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
// Module Name : com.ibm.is.sappack.dsstages.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common;

import java.util.List;

public interface IDocSegment {

	static final String IBM_COPYRIGHT_SHORT = Copyright.IBM_COPYRIGHT_SHORT;
	
	List<IDocSegment> getChildSegments();

	long getHierarchyLevel();

	String getSegmentTypeName();

	String getSegmentDefinitionName();

	IDocType getIDocType();
	
	long getSegmentNr();

	long getMinOccurrence();

	long getMaxOccurrence();

	String getSegmentDescription();

	IDocSegment getParent();

	List<IDocField> getFields();

	boolean isMandatory();
	
	boolean isParentFlag();
}
