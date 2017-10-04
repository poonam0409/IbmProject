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
// Module Name : com.ibm.is.sappack.gen.common.ui
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui;

import org.eclipse.swt.widgets.Widget;

public class WidgetIDUtils {
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.common.ui.Copyright.IBM_COPYRIGHT_SHORT; }

	/**
	 * Call this function on widgets to ensure that the corresponding widget
	 * can be tested properly by the RFT test cases
	 */
	public static void assignID(Widget w, String id) {
		w.setData(WidgetIDConstants.WIDGET_ID, id);
	}
}