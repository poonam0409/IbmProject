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
// Module Name : com.ibm.is.sappack.gen.common.ui
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.preferences.ModePreferencePage;


public class ModeManager {

	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static final String DEFAULT_MODE_ID           = "RMRMG_DEFAULT_MODE";  //$NON-NLS-1$
	public static final String CW_MODE_ID                = "RMRG_CW_MODE";        //$NON-NLS-1$
	public static final String MODELLING_MODE_ID         = "RMRG_MODELLING_MODE"; //$NON-NLS-1$
//	public static final String GEN_V7_SAP_STAGES_ENABLED = "P_GEN_V7_SAP_STAGES";        //$NON-NLS-1$ // no more v6.5 support: this code can be removed !!!

	static RMRGMode[] installedModes;
	static Collection<String> modellingAnnotationsToBeFiltered = null;


	static RMRGMode findMode(Collection<RMRGMode> modes, String id) {
		for (RMRGMode m : modes) {
			if (m.getID().equals(id)) {
				return m;
			}
		}
		return null;
	}

	static RMRGMode[] findInstalledModes() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint("com.ibm.is.sappack.gen.common.ui.mode"); //$NON-NLS-1$
		if (point == null) {
			return new RMRGMode[0];
		}

		List<RMRGMode> foundModes = new ArrayList<RMRGMode>();

		IExtension[] extensions = point.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] configElements = extensions[i].getConfigurationElements();
			for (int j = 0; j < configElements.length; j++) {
				if (configElements[j].getName().equals("rmrgmode")) { //$NON-NLS-1$
					String idAttr = configElements[j].getAttribute("modeID"); //$NON-NLS-1$
					RMRGMode mode = findMode(foundModes, idAttr);
					if (mode == null) {
						mode = new RMRGMode();
						mode.setID(idAttr);
						foundModes.add(mode);
					}
					String modeName = configElements[j].getAttribute("modeName"); //$NON-NLS-1$
					if (modeName != null) {
						mode.setName(modeName);
					}
					String modeDescription = configElements[j].getAttribute("modeDescription"); //$NON-NLS-1$
					if (modeDescription != null) {
						mode.setDescription(modeDescription);
					}
/*
					String attr = "rmAction"; //$NON-NLS-1$
					String rmActionAttr = configElements[j].getAttribute(attr); 
					if (rmActionAttr != null) {
						try {
							IObjectActionDelegate rmAction = (IObjectActionDelegate) configElements[j].createExecutableExtension(attr); 
							mode.setRMAction(rmAction);
						} catch (CoreException e) {
							String msg = Messages.CWExtensionConfiguration_0;
							msg = MessageFormat.format(msg, new Object[]{ rmActionAttr, attr} );
							Activator.getLogger().log(Level.WARNING, msg, e);
						}
					}
					attr = "rgAction"; //$NON-NLS-1$
					String rgActionAttr = configElements[j].getAttribute(attr);
					if (rgActionAttr != null) {
						try {
							IObjectActionDelegate rgAction = (IObjectActionDelegate) configElements[j].createExecutableExtension(attr); 
							mode.setRGAction(rgAction);
						} catch (CoreException e) {
							String msg = Messages.CWExtensionConfiguration_0;
							msg = MessageFormat.format(msg, rgActionAttr, attr);
							Activator.getLogger().log(Level.WARNING, msg, e);
						}
					}
					attr = "replayAction"; //$NON-NLS-1$
					String replayActionAttr = configElements[j].getAttribute(attr); 
					if (replayActionAttr != null) {
						try {
							IObjectActionDelegate replayAction = (IObjectActionDelegate) configElements[j].createExecutableExtension(attr); 
							mode.setReplayAction(replayAction);
						} catch (CoreException e) {
							String msg = Messages.CWExtensionConfiguration_0;
							msg = MessageFormat.format(msg, rgActionAttr, attr);
							Activator.getLogger().log(Level.WARNING, msg, e);
						}
					}
*/
					String attr = "modeOptions"; //$NON-NLS-1$
					String modeOptionsAttr = configElements[j].getAttribute(attr);
					if (modeOptionsAttr != null) {
						try {
							ModeOptions modeOptions = (ModeOptions) configElements[j].createExecutableExtension(attr); 
							mode.setModeOptions(modeOptions);
						} catch (CoreException e) {
							String msg = Messages.CWExtensionConfiguration_0;
							msg = MessageFormat.format(msg, modeName, attr);
							Activator.getLogger().log(Level.WARNING, msg, e);
						}
					}
				}
			}
		}
		return foundModes.toArray(new RMRGMode[0]);
	}

	public static RMRGMode[] getInstalledModes() {
		if (installedModes == null) {
			installedModes = findInstalledModes();
			int defaultPos = -1;
			for (int i = 0; i < installedModes.length; i++) {
				if (installedModes[i].getID().equals(DEFAULT_MODE_ID)) {
					defaultPos = i;
					break;
				}
			}
			// put default at the beginning of the list
			if (defaultPos > 0) {
				RMRGMode tmp = installedModes[0];
				installedModes[0] = installedModes[defaultPos];
				installedModes[defaultPos] = tmp;
			}
		}
		return installedModes;
	}

	public static RMRGMode getMode(String id) {
		for (RMRGMode mode : getInstalledModes()) {
			if (mode.getID().equals(id)) {
				return mode;
			}
		}
		return null;
	}

	public static RMRGMode getActiveMode() {
		String modeName = Activator.getDefault().getPreferenceStore().getString(ModePreferencePage.CURRENT_MODE_ID);

		List<RMRGMode> installedModes = Arrays.asList(getInstalledModes());
		RMRGMode mode = null;
		if (modeName == null || modeName.isEmpty()) {
			mode = findMode(installedModes, MODELLING_MODE_ID);
			if (mode == null) {
				// get CW mode if nothing is stored in pref store 
				mode = getMode(CW_MODE_ID);
			}
		}
		else {
			mode = findMode(installedModes, modeName);
		}

		if (mode == null) {
			// 'previous' mode not available ==> assume default mode
			mode = getMode(DEFAULT_MODE_ID);
//			Activator.getLogger().log(Level.WARNING, "'Previous' mode '" + modeName + "' could not be found. Use DEFAULT mode instead."); //$NON-NLS-1$ $NON-NLS-2$
		}

		return mode;
	}

	public static boolean generateV7Stages() {
/* no more v6.5 support: this code can be removed !!!		
		Map<String, String> advSettings = AdvancedSettingsPreferencePage.getAdvancedSettings();
		String s = advSettings.get(ADV_SETTING_RG_GEN_STAGES_VERSION);
		if ( s == null) {
			return true;
		}
		if (s.startsWith("6.5")) { //$NON-NLS-1$
			return false;
		}
no more v6.5 support: this code can be removed !!! */		
		// Pack v7.1 and CW does create v7.x stages only
		return true;
	}

	public static boolean isCWEnabled() {
		return CW_MODE_ID.equals(getActiveMode().getID());
	}

	public static boolean isModellingEnabled() {
		return MODELLING_MODE_ID.equals(getActiveMode().getID());
	}

	public static Collection<String> getModellingAnnotationsToBeFiltered() {
		if (modellingAnnotationsToBeFiltered == null) {
			String[] annotations = new String[] { Constants.ANNOT_ABAP_PROGRAM_NAME, Constants.ANNOT_ABAP_CODE };
			Set<String> result = new HashSet<String>();
			result.addAll(Arrays.asList(annotations));
			modellingAnnotationsToBeFiltered = result;
		}
		return modellingAnnotationsToBeFiltered;
	}

}
