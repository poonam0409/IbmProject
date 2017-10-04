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
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf;


import java.util.List;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.editors.EditorPageBase;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;


public abstract class RGConfPageBase extends EditorPageBase {
	public static final String PARAMETER_PROPERTY_NAMES = "PARAMETER_PROPERTY_NAMES"; //$NON-NLS-1$


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	
	/*
	public RGConfPageBase(String tabName, String title, String descriptionText) {
		super(tabName, title, descriptionText);
	}
	*/

	public RGConfPageBase(String tabName, String title, String descriptionText, String helpID) {
		super(tabName, title, descriptionText, helpID);
	}

	protected void configureTextForJobParameterProperty(final Text t, final String property) {
		super.configureTextForProperty(t, property);

		ModifyListener ml = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (e.widget instanceof Text) {
					Utils.addStringToListProperty(editor.getConfiguration(), PARAMETER_PROPERTY_NAMES, 
					                              Constants.JOB_PARAM_SEPARATOR, property);
					JobParamChangedEvent event = new JobParamChangedEvent(RGConfPageBase.this);
					sendEvent(event);
				}

			}
		};
		t.addModifyListener(ml);

	}

	public List<String> getPropertyNamesContainingParameters() {
		return Utils.getListProperty(editor.getConfiguration(), PARAMETER_PROPERTY_NAMES,
		                             Constants.JOB_PARAM_SEPARATOR);
	}

}
