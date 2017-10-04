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
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.idoctypes
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.idoctypes;


import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public class IDocSegmentCollector implements IRunnableWithProgress {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private IDocType idocType;

	/**
	 * IDocSelectedSegmentCollector
	 * 
	 * @param page
	 */
	public IDocSegmentCollector(IDocType idocType) {
		this.idocType = idocType;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			String msg = MessageFormat.format(Messages.IDocBrowserWizardPage_3, idocType.getName());
			monitor.beginTask(msg, IProgressMonitor.UNKNOWN);
			// fetch idoc segments - they will be stored inside the MetaData
			// field of the IDocType class, so we don't need to return anything here
			if(!monitor.isCanceled()) {
				this.idocType.getSegments();
			}
			monitor.done();
		} catch (Exception e) {
			Activator.getLogger().log(Level.SEVERE, Messages.LogExceptionMessage, e);
			e.printStackTrace();
			throw new InvocationTargetException(e);
		}

	}
}
