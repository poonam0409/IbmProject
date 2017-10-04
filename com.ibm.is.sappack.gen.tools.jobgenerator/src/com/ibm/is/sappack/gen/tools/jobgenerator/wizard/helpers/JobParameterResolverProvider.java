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
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers;


import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.JobParameter;


public class JobParameterResolverProvider implements ITableLabelProvider, IStructuredContentProvider{

	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.Copyright.IBM_COPYRIGHT_SHORT; }	


	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		
		return null;
	}
 
	
	@Override
	public String getColumnText(Object element, int columnIndex) {
		
		if(element instanceof JobParameter) {
		
			JobParameter param = (JobParameter) element;
			
			switch (columnIndex) {
			case JobParameterResolverPage.COLUMN_PARAMETER_NAME:
				return param.getPrompt();
				
			case JobParameterResolverPage.COLUMN_PARAMETER_VALUE:
				
				if(param.getType().equals(JobParameter.TYPE_NAME_ENCRYPTED)) {
					return param.getDefaultValue().replaceAll(".", "*");  //$NON-NLS-1$//$NON-NLS-2$
				} else {
					return param.getDefaultValue();	
				}
				
			default:
				return null;
			}
		}
		
		return null;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		
	}

	@Override
	public void dispose() {
	
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		
		if(inputElement instanceof List) {
			List elements = (List) inputElement;
			return elements.toArray();
		}
		
		return null;
	}

}
