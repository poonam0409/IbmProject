//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.idocseglist
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.idocseglist;


import com.ibm.iis.sappack.gen.common.ui.editors.GeneralPageBase;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class IDocSegmentCollectionGeneralPage extends GeneralPageBase {
  	static String copyright() { 
 	   return Copyright.IBM_COPYRIGHT_SHORT; 
 	}	
	
	public IDocSegmentCollectionGeneralPage() {
		super(Messages.IDocSegmentCollectionGeneralPage_0, Messages.IDocSegmentCollectionGeneralPage_1, Messages.IDocSegmentCollectionGeneralPage_2, Utils.getHelpID("idocsegment_list_editor")); //$NON-NLS-1$
	}
}
