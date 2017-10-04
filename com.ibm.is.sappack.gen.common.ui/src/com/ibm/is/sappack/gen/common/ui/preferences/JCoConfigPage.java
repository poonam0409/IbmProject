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
// Module Name : com.ibm.is.sappack.gen.common.ui.preferences
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui.preferences;

import java.io.File;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;

public class JCoConfigPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	static String copyright() {
		return com.ibm.is.sappack.gen.common.ui.preferences.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	FileFieldEditor jcoJARFieldEditor;
	DirectoryFieldEditor jcoNativeLibFieldEditor;
	BooleanFieldEditor useInstallerConfigFieldEditor;

	/**
	 * 
	 */
	public JCoConfigPage() {
		super(GRID);
	}

	/**
	 * @param style
	 */
	public JCoConfigPage(int style) {
		super(style);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @param style
	 */
	public JCoConfigPage(String title, int style) {
		super(title, style);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public JCoConfigPage(String title, ImageDescriptor image, int style) {
		super(title, image, style);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		this.useInstallerConfigFieldEditor = new BooleanFieldEditor(PreferenceConstants.P_USE_INSTALLER_CONFIG,
				Messages.JCoConfigPage_0, getFieldEditorParent());
		this.jcoJARFieldEditor = new FileFieldEditor(PreferenceConstants.P_JCO_JAR_LOCATION, Messages.JCoConfigPage_1,
				getFieldEditorParent());
		this.jcoNativeLibFieldEditor = new DirectoryFieldEditor(PreferenceConstants.P_JCO_NATIVE_LIB_DIR, Messages.JCoConfigPage_2,
				getFieldEditorParent());
		
		addField(this.jcoJARFieldEditor);
		addField(this.jcoNativeLibFieldEditor);
		addField(this.useInstallerConfigFieldEditor);

	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Messages.JCoConfigPage_3);
	}

	@Override
	protected void checkState() {
		super.checkState();
		
		File jcoJarFile = new File(this.jcoJARFieldEditor.getStringValue());
		File jcoNativeLibDir = new File(this.jcoNativeLibFieldEditor.getStringValue());
		
		if (null != jcoJarFile && null != jcoNativeLibDir &&
		    jcoJarFile.exists() && jcoJarFile.isFile() && jcoJarFile.length() > 0 &&
		    jcoNativeLibDir.exists() && jcoNativeLibDir.isDirectory()) {
			this.setErrorMessage(null);
			this.setValid(true);
		} else {
			if (jcoJarFile.exists() == false || jcoJarFile.isFile() == false || jcoJarFile.length() == 0) {
				setErrorMessage(Messages.JCoConfigPage_4);
			} else if (jcoNativeLibDir.exists() == false || jcoNativeLibDir.isDirectory() == false) {
				setErrorMessage(Messages.JCoConfigPage_5);
			}
			this.setValid(false);
		}
		
		if (this.useInstallerConfigFieldEditor.getBooleanValue()) {
			this.jcoJARFieldEditor.setErrorMessage(null);
			this.jcoNativeLibFieldEditor.setErrorMessage(null);
			this.setErrorMessage(null);
			this.setValid(true);			
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		
        if (event.getProperty().equals(FileFieldEditor.VALUE) ||
        	event.getProperty().equals(DirectoryFieldEditor.VALUE) ||
        	event.getProperty().equals(BooleanFieldEditor.VALUE)) {
          this.checkState();
        }   
	}
}
