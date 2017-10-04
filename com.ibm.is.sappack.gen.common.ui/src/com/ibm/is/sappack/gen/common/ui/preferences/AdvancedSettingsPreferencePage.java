//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
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


import java.io.LineNumberReader;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class AdvancedSettingsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static String ADVANCED_SETTINGS = AdvancedSettingsPreferencePage.class.getSimpleName() + ".settings"; //$NON-NLS-1$

	private Text settingsText;


	static String copyright() {
		return com.ibm.is.sappack.gen.common.ui.preferences.Copyright.IBM_COPYRIGHT_SHORT;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));

		Label l = new Label(comp, SWT.NONE);
		l.setText(Messages.AdvancedSettingsPreferencePage_0);
		settingsText = new Text(comp, SWT.BORDER | SWT.MULTI);
		settingsText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));

		settingsText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setErrorMessage(null);
				int[] lineNumber = new int[1];
				Map<String, String> map = parseSettings(settingsText.getText(), lineNumber);
				if (map == null) {
					String msg = MessageFormat.format(Messages.AdvancedSettingsPreferencePage_1, lineNumber[0]);
					setErrorMessage(msg);
				}

			}
		});

		String settings = Activator.getDefault().getDialogSettings().get(ADVANCED_SETTINGS);
		if (settings != null) {
			settingsText.setText(settings);
		}

		return comp;
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void performApply() {
		storePreferences();
		super.performApply();
	}

	@Override
	public boolean performOk() {
		storePreferences();
		return super.performOk();
	}

	private void storePreferences() {
		if (this.settingsText != null) {
			String s = settingsText.getText();
			Activator.getDefault().getDialogSettings().put(ADVANCED_SETTINGS, s);
		}
	}

	static Map<String, String> parseSettings(String settings, int[] lineNumber) {
		Map<String, String> result = new HashMap<String, String>();
		if (settings != null) {
			LineNumberReader lnr = new LineNumberReader(new StringReader(settings));
			String line = null;
			try {
				while ((line = lnr.readLine()) != null) {
					int ix = line.indexOf("="); //$NON-NLS-1$
					String key = line.substring(0, ix);
					String value = line.substring(ix + 1);
					result.put(key, value);
				}
			} catch (Exception e) {
				if (lineNumber != null) {
					lineNumber[0] = lnr.getLineNumber();
				}
				Activator.getLogger().log(Level.FINE, Messages.UnexpectedErrorOcurred, e);
				return null;
			}
		}
		return result;
	}

	public static Map<String, String> getAdvancedSettings() {
		String settings = Activator.getDefault().getDialogSettings().get(ADVANCED_SETTINGS);
		Map<String, String> result = parseSettings(settings, null);
		if (result == null) {
			return new HashMap<String, String>();
		}
		return result;
	}

	public static boolean isSettingEnabled(String settingKey) {
   	String  settingValue;
   	boolean isEnabled = false;

   	if (settingKey != null) {
   		settingValue = getSetting(settingKey);

   		if (settingValue != null) {
   			if (settingValue.equalsIgnoreCase(AdvancedSettingsConstants.SETTING_ENABLED)     ||
  				    settingValue.equalsIgnoreCase(AdvancedSettingsConstants.SETTING_ENABLED_STR) ||
  				    settingValue.equalsIgnoreCase(AdvancedSettingsConstants.SETTING_ENABLED_NUM)) {
   				isEnabled = true;
   			}
   		}
   	}

   	return(isEnabled);
	}
	
	public static String getSetting(String key) {
		Map<String, String> settings = getAdvancedSettings();
		return(settings.get(key));
	}

	public static int getSettingAsInt(String key, int defaultValue) {
		Map<String, String> settings = getAdvancedSettings();
		String value = settings.get(key);
		if (value == null) {
			return defaultValue;
		}
		return Integer.parseInt(value);
	}
	
}
