//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2012                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.idocload.segcollimpl
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload.segcollimpl;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.idocload.IDocNode;

public class IDocNodeList extends ArrayList<IDocNode> {
	static final String CLASSNAME = IDocNodeList.class.getName();

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.segcollimpl.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4686991947968240495L;
	Logger logger = StageLogger.getLogger();

	public IDocNodeList(int i) {
		super(i);
	}

	@Override
	public boolean add(IDocNode n) {
		final String METHODNAME = "add(IDocNode)";
		this.logger.entering(CLASSNAME, METHODNAME);
		int index = -1;
		IDocSegment seg = n.getSegment();
		long segNr = seg.getSegmentNr();
		for (int i=0;i<this.size(); i++) {
			IDocSegment otherSeg = this.get(i).getSegment();
			if (segNr < otherSeg.getSegmentNr()) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			super.add(index, n);
		} else {
			super.add(n);
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
		return true;
	}

}
