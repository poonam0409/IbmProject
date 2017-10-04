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
// Module Name : com.ibm.is.sappack.gen.common.ui
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui;

import org.eclipse.ui.IObjectActionDelegate;

public class RMRGMode {
	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	String id;
	String name;
	String description;
	IObjectActionDelegate rmAction;
	IObjectActionDelegate replayAction;
	IObjectActionDelegate rgAction;
	ModeOptions modeOptions;

	public ModeOptions getModeOptions() {
		return modeOptions;
	}

	public void setModeOptions(ModeOptions modeOptions) {
		this.modeOptions = modeOptions;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public IObjectActionDelegate getRMAction() {
		return rmAction;
	}

	public void setRMAction(IObjectActionDelegate rmAction) {
		this.rmAction = rmAction;
	}

	public IObjectActionDelegate getReplayAction() {
		return replayAction;
	}

	public void setReplayAction(IObjectActionDelegate replayAction) {
		this.replayAction = replayAction;
	}

	public IObjectActionDelegate getRGAction() {
		return rgAction;
	}

	public void setRGAction(IObjectActionDelegate rgAction) {
		this.rgAction = rgAction;
	}

}
