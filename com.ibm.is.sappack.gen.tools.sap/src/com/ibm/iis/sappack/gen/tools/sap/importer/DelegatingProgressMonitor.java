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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.importer
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.importer;


import org.eclipse.core.runtime.IProgressMonitor;


public class DelegatingProgressMonitor implements IProgressMonitor {
	private IProgressMonitor[] delegates;


	static String copyright() { 
  	   return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public DelegatingProgressMonitor(IProgressMonitor delegate) {
		this.delegates = new IProgressMonitor[] { delegate };
	}

	public DelegatingProgressMonitor(IProgressMonitor[] delegates) {
		this.delegates = delegates;
	}

	@Override
	public void beginTask(String name, int totalWork) {
		for (IProgressMonitor delegate : delegates) {
			delegate.beginTask(name, totalWork);
		}

	}

	@Override
	public void done() {
		for (IProgressMonitor delegate : delegates) {
			delegate.done();
		}

	}

	@Override
	public void internalWorked(double work) {

		for (IProgressMonitor delegate : delegates) {
			delegate.internalWorked(work);
		}

	}

	@Override
	public boolean isCanceled() {
		for (IProgressMonitor delegate : delegates) {
			if (delegate.isCanceled()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setCanceled(boolean value) {
		for (IProgressMonitor delegate : delegates) {
			delegate.setCanceled(value);
		}

	}

	@Override
	public void setTaskName(String name) {
		for (IProgressMonitor delegate : delegates) {
			delegate.setTaskName(name);
		}

	}

	@Override
	public void subTask(String name) {
		for (IProgressMonitor delegate : delegates) {
			delegate.subTask(name);
		}

	}

	@Override
	public void worked(int work) {
		for (IProgressMonitor delegate : delegates) {
			delegate.worked(work);
		}

	}

}
