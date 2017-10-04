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

import java.util.ArrayList;
import java.util.List;

import com.ibm.is.sappack.gen.jco.panel.internal.Messages;

/**
 * @author dsh
 *
 */
public enum JCoModelProvider {
	INSTANCE;
	
	private List<JCoSettingsPair> pair;
	
	static String copyright() {
		return com.ibm.is.sappack.gen.jco.panel.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private JCoModelProvider() {
		pair = new ArrayList<JCoSettingsPair>();

		pair.add(new JCoSettingsPair(Messages.JCoModelProvider_0, "")); //$NON-NLS-1$ //$NON-NLS-2$
		pair.add(new JCoSettingsPair(Messages.JCoModelProvider_1, "")); //$NON-NLS-1$ //$NON-NLS-2$
		pair.add(new JCoSettingsPair(Messages.JCoModelProvider_2, "")); //$NON-NLS-1$ //$NON-NLS-2$
		pair.add(new JCoSettingsPair(Messages.JCoModelProvider_3, "")); //$NON-NLS-1$ //$NON-NLS-2$
		pair.add(new JCoSettingsPair(Messages.JCoModelProvider_4, "")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void setPair(List<JCoSettingsPair> pair) {
		this.pair = pair;
	}

	public List<JCoSettingsPair> getJCoSettingsPair() {
		return pair;
	}

}
