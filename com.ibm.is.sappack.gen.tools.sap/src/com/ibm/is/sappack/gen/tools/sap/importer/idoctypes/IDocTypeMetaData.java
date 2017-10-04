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
import java.util.Iterator;
import java.util.List;


public class IDocTypeMetaData {
	
	//private IDocType idocType;
	protected List<Segment> segments;
	protected List<Segment> rootSegments;
	protected List<MessageType> messageTypes;


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	public IDocTypeMetaData(List<Segment> segments, List<MessageType> messageTypes){		
		this.segments =segments;
		this.messageTypes = messageTypes;		
	}
	
	public List<Segment> getSegments(){
		
		return this.segments;
		
	}
	
	public List<MessageType> getMessageTypes(){
		
		return this.messageTypes;
	}
	
	public List<Segment> getRootSegments(){		

		if (this.rootSegments == null) {
			List<Segment> segments = this.getSegments();
			this.rootSegments = new ArrayList<Segment>();
			Iterator<Segment> iterator = segments.iterator();
			while (iterator.hasNext()) {
				Segment segment = iterator.next();
				if (segment.parent == null) {
					this.rootSegments.add(segment);
				}
			}
		}
		return this.rootSegments;
		
	}
	
	// Optional
	
	public Segment getSegment(String segmentName)  {
		
		Iterator<Segment> iterator = this.getSegments().iterator();
		while (iterator.hasNext()) {
			Segment segment = iterator.next();
			// 
			if ((segment.getType().equalsIgnoreCase(segmentName))
					|| (segment.getDefinition().equalsIgnoreCase(segmentName))) {
				return segment;
			}  
		}

		return null;
	}

}
