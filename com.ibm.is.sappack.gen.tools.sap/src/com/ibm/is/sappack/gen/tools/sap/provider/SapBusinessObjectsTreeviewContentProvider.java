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
// Module Name : com.ibm.is.sappack.gen.tools.sap.provider
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.provider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.ibm.is.sappack.gen.tools.sap.model.AbstractBusinessObject;
import com.ibm.is.sappack.gen.tools.sap.model.SapBusinessObject;
import com.ibm.is.sappack.gen.tools.sap.model.SapTableBusinessObject;

public class SapBusinessObjectsTreeviewContentProvider implements ITreeContentProvider {

	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.sap.provider.Copyright.IBM_COPYRIGHT_SHORT; }
	
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof SapBusinessObject) {
			SapBusinessObject businessObject = (SapBusinessObject) parentElement;
			if (businessObject.hasChildren()) {
				SapBusinessObject[] children = businessObject.getChildren();
				return children;
			}
			return businessObject.getTables();
		}
		return null;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof SapBusinessObject) {
			return true;
		}
		if (element instanceof SapTableBusinessObject) {
			return false;
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof AbstractBusinessObject[]) {
			AbstractBusinessObject[] businessObjects = (AbstractBusinessObject[]) inputElement;
			return businessObjects;
		}
		return null;
	}

	public void dispose() {
		// Nothing to be done here
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// Nothing to be done here
	}

}
