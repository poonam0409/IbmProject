//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2012                                              
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

import java.util.ArrayList;
import java.util.List;

import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocType;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Segment;


public class IDocTableSet {

	private IDocType                  idocType;
	private List<IDocSegmentTableSet> idocSegmentTableSetList = new ArrayList<IDocSegmentTableSet>();


   static String copyright() {
      return com.ibm.is.sappack.gen.tools.sap.model.Copyright.IBM_COPYRIGHT_SHORT;
   }

	/**
	 * IDocTableSet
	 * 
	 * @param idocType
	 * @param idocSegmentTableSet
	 */
	public IDocTableSet(IDocType idocType) {
		this.idocType = idocType;
	}

	/**
	 * add
	 * 
	 * @param idocSegment
	 */
	public void add(IDocSegmentTableSet idocSegmentTableSet) {

		this.idocSegmentTableSetList.add(idocSegmentTableSet);
	}

	/**
	 * getIdocType
	 * 
	 * @return
	 */
	public IDocType getIDocType() {
		return idocType;
	}

	/**
	 * getIdocSegmentTableSetList
	 * 
	 * @return
	 */
	public List<IDocSegmentTableSet> getIdocSegmentTableSetList() {
		return idocSegmentTableSetList;
	}

	/**
	 * 
	 * @return
	 */
	public Object[] toSortedArray() {
		int size = 0;

		/* determine the size of the array */
		for (int i = 0; i < this.idocSegmentTableSetList.size(); i++) {
			IDocSegmentTableSet idocSegmentTableSet = this.idocSegmentTableSetList.get(i);
			/* we need one entry for the segment */
			size++;
			/* and one entry for each table */
			size = size + idocSegmentTableSet.getSapTableSet().size();
		}

//		Object[] values = new Object[size];
		ArrayList<Object> values = new ArrayList<Object>();
		int index = 0;
		/* add Segments and SapTables to the array */
		for (int i = 0; i < this.idocSegmentTableSetList.size(); i++) {
			IDocSegmentTableSet idocSegmentTableSet = this.idocSegmentTableSetList.get(i);
			Segment segment = idocSegmentTableSet.getIdocSegment();
			SapTableSet tableSet = idocSegmentTableSet.getSapTableSet();
			if (segment != null) {
				values.add(segment);
			}
			index++;
			SapTable[] tableArray = tableSet.toSortedArray();
			for (int t = 0; t < tableArray.length; t++) {
				values.add( tableArray[t] );
				index++;
			}
		}
		return values.toArray();
	}

}
