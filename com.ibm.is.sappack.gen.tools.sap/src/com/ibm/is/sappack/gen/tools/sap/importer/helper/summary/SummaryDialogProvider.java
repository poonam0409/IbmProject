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
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.helper.summary
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.helper.summary;


import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;


public class SummaryDialogProvider implements IStructuredContentProvider, ITableLabelProvider {
	
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.helper.summary.Copyright.IBM_COPYRIGHT_SHORT;
	}


	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List) {
			List<?> contentList = (List<?>) inputElement;
			return (SummaryEntry[]) contentList.toArray(new SummaryEntry[contentList.size()]);
		}
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
			SummaryEntry entry = (SummaryEntry) element;
			if (entry.isTypeInformation()) {
				return imageRegistry.get(Constants.ICON_ID_MESSAGE_INFORMATION);
			} else if (entry.isTypeWarning()) {
				return imageRegistry.get(Constants.ICON_ID_MESSAGE_WARNING);
			} else if (entry.isTypeError()) {
				return imageRegistry.get(Constants.ICON_ID_MESSAGE_ERROR);
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		SummaryEntry entry = (SummaryEntry) element;
		if (columnIndex == 1) {
			return entry.getTableName();
		} else if (columnIndex == 2) {
			return entry.getMessageText();
		}
		return null;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

}
