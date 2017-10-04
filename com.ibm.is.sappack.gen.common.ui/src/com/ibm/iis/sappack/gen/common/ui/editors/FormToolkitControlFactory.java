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


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;


public class FormToolkitControlFactory implements IControlFactory {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	FormToolkit formToolKit;
	
	public FormToolkitControlFactory(FormToolkit toolkit) {
		this.formToolKit = toolkit;
	}
	
	public FormToolkit getFormToolkit() {
		return this.formToolKit;
	}
	
	@Override
	public Label createLabel(Composite parent, int style) {
		return formToolKit.createLabel(parent, "", style); //$NON-NLS-1$
	}

	@Override
	public Composite createComposite(Composite parent, int style) {
		return this.formToolKit.createComposite(parent, style);
	}

	@Override
	public Text createText(Composite parent, int style) {
		return this.formToolKit.createText(parent, "", style); //$NON-NLS-1$
	}

	@Override
	public Composite createGroup(Composite parent, String name, int style) {
		
		Section section = this.formToolKit.createSection(parent, Section.TITLE_BAR | style); //|	    		  Section.TWISTIE|Section.EXPANDED);
		
		section.setText(name);
//		section.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
//		section.setExpanded(true);
		Composite sectionComp = this.formToolKit.createComposite(section);
	    sectionComp.setLayout(new GridLayout(1, false));
		section.setClient(sectionComp);
	    return sectionComp;
	}
	
	@Override
	public void expandGroup(Composite group, boolean expand) {
		Composite parent = group.getParent();
		if (parent instanceof Section) {
			Section s = (Section) parent;
			s.setExpanded(expand);
		}
		
	}

	
	@Override
	public Button createButton(Composite parent, int style) {
		return this.formToolKit.createButton(parent, "", style); //$NON-NLS-1$
	}

	@Override
	public Combo createCombo(Composite parent, int style) {
		return new Combo(parent, style);
	}

	@Override
	public Link createLink(Composite parent, int style) {
		return new Link(parent, style);
	}

	
}
