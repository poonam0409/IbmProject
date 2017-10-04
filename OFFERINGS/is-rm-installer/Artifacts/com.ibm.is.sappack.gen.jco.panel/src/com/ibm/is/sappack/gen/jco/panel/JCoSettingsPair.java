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
package com.ibm.is.sappack.gen.jco.panel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.is.sappack.gen.jco.panel.internal.Messages;

/**
 * @author dsh
 *
 */
public class JCoSettingsPair {
	private final ILogger log = IMLogger.getLogger(com.ibm.is.sappack.gen.jco.panel.JCoSettingsPair.class);
	
	private String jcoSettingsName;
	private String jcoSettingsValue;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	static String copyright() {
		return com.ibm.is.sappack.gen.jco.panel.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/**
	 * 
	 */
	public JCoSettingsPair() {
		super();
	}

	public JCoSettingsPair(String name, String value) {
		super();
		this.jcoSettingsName = name;
		this.jcoSettingsValue = value;
	}

	public String getJcoSettingsName() {
		return jcoSettingsName;
	}

	public void setJcoSettingsName(String jcoSettingsName) {
		propertyChangeSupport.firePropertyChange("jcoSettingsName", this.jcoSettingsName, //$NON-NLS-1$
				this.jcoSettingsName = jcoSettingsName);

	}

	public String getJcoSettingsValue() {
		return jcoSettingsValue;
	}

	public void setJcoSettingsValue(String jcoSettingsValue) {
		propertyChangeSupport.firePropertyChange("jcoSettingsValue", this.jcoSettingsValue, //$NON-NLS-1$
				this.jcoSettingsValue = jcoSettingsValue);
	}
	
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	@Override
	public String toString() {
		return this.jcoSettingsName + ": " + this.jcoSettingsValue; //$NON-NLS-1$
	}
}
