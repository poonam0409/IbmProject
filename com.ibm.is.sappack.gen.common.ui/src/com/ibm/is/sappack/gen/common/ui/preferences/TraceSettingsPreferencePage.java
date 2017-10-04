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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.helper.LoggingHelper;

public class TraceSettingsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private TraceSettingsWidget traceSetWidget = null;

	static String copyright() {
		return com.ibm.is.sappack.gen.common.ui.preferences.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public TraceSettingsPreferencePage() {
		super();
	}

	public TraceSettingsPreferencePage(String title) {
		super(title);
	}

	@Override
	protected Control createContents(Composite parent) {
		traceSetWidget = new TraceSettingsWidget(parent, SWT.NONE, this, Activator.getDefault().getPreferenceStore());
		return traceSetWidget;
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	protected void performApply() {
		storeTracePreferences();
		super.performApply();
	}

	@Override
	protected void performDefaults() {
		resetTracePreferences();
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		storeTracePreferences();

		// remove all existing loggers for our components
		resetAndRemoveAllKnownLoggers();
		
		// as the preferences might have changed we reconfigure the loggers 
		reconfigureGlobalLogging();
		return super.performOk();
	}

	private void storeTracePreferences() {
		IPreferenceStore prefStore = getPreferenceStore();
		if (traceSetWidget != null) {
			if (traceSetWidget.getTraceFileLocText() != null) {
				if (prefStore != null) {
					prefStore.setValue(PreferenceConstants.P_TRACE_FILE_LOC, traceSetWidget.getTraceFileLocText().getText());
				}
			}

			if (traceSetWidget.getTraceComponentsText() != null) {
				if (prefStore != null) {
					String traceComponents =
					      traceSetWidget.getTraceComponentsText().getText().replaceAll(Text.DELIMITER,
					            LoggingHelper.TRACE_COMPONENTS_DELIMITER);
					prefStore.setValue(PreferenceConstants.P_TRACE_COMPONENTS, traceComponents);
				}
			}

			if (traceSetWidget.getEnableTrace() != null) {
				if (prefStore != null) {
					prefStore.setValue(PreferenceConstants.P_TRACE_ENABLED, traceSetWidget.getEnableTrace().getSelection());
				}
			}
		}

		Activator.getDefault().savePluginPreferences();
	}

	private void resetTracePreferences() {
		IPreferenceStore prefStore = getPreferenceStore();
		if (prefStore != null) {
			traceSetWidget.setTraceFileLocText(prefStore.getDefaultString(PreferenceConstants.P_TRACE_FILE_LOC));
		}

		traceSetWidget.setTraceComponentsText(null);
		traceSetWidget.setEnableTrace(false);
	}
	
	private void reconfigureGlobalLogging() {
		IPreferenceStore prefStore = getPreferenceStore();
		if (prefStore != null) {
			LoggingHelper.initializeLoggers(prefStore);
		}
	}
	
	private void resetAndRemoveAllKnownLoggers() {
		LoggingHelper.resetAllKnownLoggers();
		LoggingHelper.clearAllKnownLoggersList();
	}

	public static String getTraceFileLocationString() {
		return Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.P_TRACE_FILE_LOC);
	}

	public static boolean isTraceEnabled() {
		return Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.P_TRACE_ENABLED);
	}

	public static String getTraceComponents() {
		return Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.P_TRACE_COMPONENTS);
	}
}
