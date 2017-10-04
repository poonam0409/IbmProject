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
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.validator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator.validator;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ValidationResultContentProvider implements ITreeContentProvider {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.validator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	@Override
	public Object[] getChildren(Object parentElement) {

		if(parentElement instanceof IValidationResult) {
			IValidationResult result = (IValidationResult) parentElement;
			return result.getSubResults().toArray();
		}
		
		return null;
	}

	@Override
	public Object getParent(Object element) {

		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof IValidationResult) {
			IValidationResult result = (IValidationResult) element;
			if(result.getSubResults().size() > 0) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List) {
			List<IValidationResult> validationResultList = (List<IValidationResult>) inputElement;
			return validationResultList.toArray();
		}
		return null;
	}

	@Override
	public void dispose() {
		// nothing needs to be done here
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// nothing needs to be done here
		
	}

}
