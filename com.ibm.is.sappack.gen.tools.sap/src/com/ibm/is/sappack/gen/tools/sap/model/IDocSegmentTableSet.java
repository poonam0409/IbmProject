//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2012                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.model
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.tools.sap.model;

import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Segment;

public class IDocSegmentTableSet {
	
	private Segment     idocSegment;
	private SapTableSet sapTableSet;
	
	
   static String copyright() { 
      return com.ibm.is.sappack.gen.tools.sap.model.Copyright.IBM_COPYRIGHT_SHORT; 
   }

   
	/**
	 * IDocTableSet
	 * @param idocSegment
	 */
	public IDocSegmentTableSet(Segment idocSegment) {
		this.idocSegment = idocSegment;
		this.sapTableSet = new SapTableSet();
	}
	
	/**
	 * add
	 * @param table
	 */
	public void add(SapTable table) {
		if(!this.sapTableSet.contains(table)) {
			this.sapTableSet.add(table);
		}
	}

	/**
	 * getIdocSegment
	 * @return
	 */
	public Segment getIdocSegment() {
		return idocSegment;
	}

	/**
	 * getSapTableSet
	 * @return
	 */
	public SapTableSet getSapTableSet() {
		return sapTableSet;
	}
	
}
