//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.common.ui.editors
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.editors;


import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;


public interface IControlFactory {
	
  	static String copyright = Copyright.IBM_COPYRIGHT_SHORT; 
	
	public Label createLabel(Composite parent, int style);
	
	public Link createLink(Composite parent, int style);

	public Composite createComposite(Composite parent, int style);

	public Text createText(Composite parent, int style);

	public Composite createGroup(Composite parent, String name, int style);
	
	public void expandGroup(Composite group, boolean expand);

	public Button createButton(Composite parent, int style);

	public Combo createCombo(Composite parent, int style);
}
