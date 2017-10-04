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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;

public class ValidationResultLabelProvider implements ITableLabelProvider{

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.validator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	@Override
	public Image getColumnImage(Object obj, int i) {
		if(i == ValidationResultPage.COLUMN_STATUS) {
			IValidationResult validationResult = (IValidationResult) obj;
			if(validationResult.getStatus() == Status.SUCCESS) {
				return Activator.getDefault().getImageRegistry().get(Constants.ICON_ID_VALIDATE_SUCCESS);
			}
			
			if(validationResult.getStatus() == Status.FAILURE) {
				return Activator.getDefault().getImageRegistry().get(Constants.ICON_ID_VALIDATE_FAILURE);
			}
			
			if(validationResult.getStatus() == Status.WARNING) {
				return Activator.getDefault().getImageRegistry().get(Constants.ICON_ID_VALIDATE_WARNING);
			}
			
		}
		
		return null;
	}

	@Override
	public String getColumnText(Object obj, int i) {
		if (obj instanceof IValidationResult) {
			IValidationResult validationResult = (IValidationResult) obj;
			switch (i) {
			case ValidationResultPage.COLUMN_NAME:
				return validationResult.getName();
			case ValidationResultPage.COLUMN_VALUE:
				return validationResult.getValue();
			case ValidationResultPage.COLUMN_MESSAGE:
				return validationResult.getMessage();
			case ValidationResultPage.COLUMN_STATUS:
			default:
				return Constants.EMPTY_STRING;
			}
		}
		return null;
	}

	@Override
	public void addListener(ILabelProviderListener ilabelproviderlistener) {
		// nothing needs to be done here
		
	}

	@Override
	public void dispose() {
		// nothing needs to be done here
		
	}

	@Override
	public boolean isLabelProperty(Object obj, String s) {
		// nothing needs to be done here
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener ilabelproviderlistener) {
		// nothing needs to be done here
		
	}

}
