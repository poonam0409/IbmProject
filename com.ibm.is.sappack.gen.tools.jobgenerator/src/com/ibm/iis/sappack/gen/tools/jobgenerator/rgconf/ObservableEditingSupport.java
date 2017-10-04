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


import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;


public abstract class ObservableEditingSupport extends EditingSupport {
	public ObservableEditingSupport(ColumnViewer viewer) {
		super(viewer);
	}


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public class EditingObservable extends Observable {

		@Override
		protected void clearChanged() {
			super.clearChanged();
		}

		@Override
		protected void setChanged() {
			super.setChanged();
		}

	};

	EditingObservable observable = new EditingObservable();

	public void addObserver(Observer o) {
		observable.addObserver(o);
	}

	public int countObservers() {
		return observable.countObservers();
	}

	public void deleteObserver(Observer o) {
		observable.deleteObserver(o);
	}

	public void deleteObservers() {
		observable.deleteObservers();
	}

	public boolean hasChanged() {
		return observable.hasChanged();
	}

	public void notifyObservers() {
		observable.notifyObservers();
	}

	public void notifyObservers(Object arg) {
		observable.notifyObservers(arg);
	}

}
