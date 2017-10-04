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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.RMRGMode;
import com.ibm.is.sappack.gen.common.ui.util.Utilities;


public abstract class GeneralPageBase extends EditorPageBase {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	Composite pageComposite;
	Text documentationText;
	
	/*
	protected GeneralPageBase(String tabName, String title, String descriptionText) {
		super(tabName, title, descriptionText);
	}
	*/

	protected GeneralPageBase(String tabName, String title, String descriptionText, String helpID) {
		super(tabName, title, descriptionText, helpID);
	}

	@Override
	public void createControls(IControlFactory controlFactory, Composite composite) {

		composite = controlFactory.createComposite(composite, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		String name = ""; //$NON-NLS-1$
		String location = ""; //$NON-NLS-1$
		String lastModified = ""; //$NON-NLS-1$
		IFile file = this.editor.getEditedFile();
		if (file != null) {
			name = file.getName();
			location = file.getLocation().toOSString();
			Date d = new Date(file.getLocalTimeStamp());
			lastModified = Utilities.getDateString(d);
		}

		List<String> keys = new ArrayList<String>( Arrays.asList(new String[] { Messages.GeneralPageBase_2, Messages.GeneralPageBase_3, Messages.GeneralPageBase_4 }) );
		List<String> values = new ArrayList<String>(Arrays.asList(new String[] { name, location, lastModified }));
		List<Integer> styles = new ArrayList<Integer>(Arrays.asList(new Integer[] { SWT.READ_ONLY, SWT.READ_ONLY, SWT.READ_ONLY }));
		List<String> mappedKeys = new ArrayList<String>(Arrays.asList(new String[] { "NAME", "LOCATION", "LAST_MODIFIED" })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
		if (this.editor.isModeSensitive()) {
			keys.add(Messages.GeneralPageBase_10);
			RMRGMode mode = this.editor.getConfiguration().getMode();
			String modeName = mode.getName();
			values.add(modeName);
			styles.add(SWT.READ_ONLY);
			mappedKeys.add("MODE"); // dummy value, will not be written because it is read-only //$NON-NLS-1$
		}
	
		this.createKeyValueTextGroup(controlFactory, composite, 
				Messages.GeneralPageBase_0, 
				Messages.GeneralPageBase_1,
				keys.toArray(new String[0]),
				values.toArray(new String[0]),
				styles.toArray(new Integer[0]),
				mappedKeys.toArray(new String[0])
				/*
				new String[] { Messages.GeneralPageBase_2, Messages.GeneralPageBase_3, Messages.GeneralPageBase_4 }, 
				new String[] { name, location, lastModified }, 
				new int[] { SWT.READ_ONLY, SWT.READ_ONLY, SWT.READ_ONLY }, 
				new String[] { "NAME", "LOCATION", "LAST_MODIFIED" } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				*/
		);

		Composite comp2 = controlFactory.createComposite(composite, SWT.NONE);
		comp2.setLayout(new GridLayout(2, true));
		GridData gd2 = new GridData(SWT.FILL, SWT.TOP, false, false);
		gd2.horizontalSpan = 2;
		comp2.setLayoutData(gd2);
		
		Composite docGroup = this.createGroup(controlFactory, comp2, Messages.GeneralPageBase_5, null);
		this.documentationText = controlFactory.createText(docGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData textGD = new GridData(SWT.DEFAULT, 100);
		textGD.horizontalAlignment = SWT.FILL;
		textGD.verticalAlignment = SWT.FILL;
		textGD.grabExcessHorizontalSpace = true;
		textGD.grabExcessVerticalSpace = true;
		this.documentationText.setLayoutData(textGD);
		configureTextForProperty(this.documentationText, PropertiesConstants.KEY_DOCUMENTATION);
		
		this.createKeyValueTextGroup(controlFactory, comp2, 
				Messages.GeneralPageBase_6, null, 
				new String[] { Messages.GeneralPageBase_7, Messages.GeneralPageBase_8, Messages.GeneralPageBase_9 }, 
				new String[] { null, null, null }, 
				new Integer[] {	SWT.NONE, SWT.NONE, SWT.NONE }, 
				new String[] { "AUTHOR", "COMPANY", "VERSION" } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		);
		this.pageComposite = composite;
	}
	
	protected Composite getPageComposite() {
		return pageComposite;
	}

	public static class DocumentationAppendEvent extends EditorEvent {

		String appendString;
		public DocumentationAppendEvent(EditorPageBase sourcePage, String appendString) {
			super(sourcePage);
			this.appendString = appendString;
		}
		
		public String getAppendString() {
			return this.appendString;
		}
		
	}
	
	@Override
	public void update(EditorEvent event) {
		super.update(event);
		if (event instanceof DocumentationAppendEvent) {
			String s = ((DocumentationAppendEvent) event).getAppendString();
			String text = Utils.getText(this.documentationText);
			if (text != null) {
				text += "\n"; //$NON-NLS-1$
			} else {
				text = ""; //$NON-NLS-1$
			}
			text += s+"\n"; //$NON-NLS-1$ 
			this.documentationText.setText(text);
		}
	}
	
	
}
