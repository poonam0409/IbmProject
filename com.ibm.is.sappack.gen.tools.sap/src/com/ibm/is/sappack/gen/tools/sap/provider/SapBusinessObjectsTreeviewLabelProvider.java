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

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.model.AbstractBusinessObject;
import com.ibm.is.sappack.gen.tools.sap.model.SapBusinessObject;
import com.ibm.is.sappack.gen.tools.sap.model.SapTableBusinessObject;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;

public class SapBusinessObjectsTreeviewLabelProvider extends ColumnLabelProvider implements ILabelProvider {

	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.sap.provider.Copyright.IBM_COPYRIGHT_SHORT; }
	
	public Image getImage(Object element) {
		ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
		if (element instanceof SapTableBusinessObject) {
			return imageRegistry.get(Constants.ICON_ID_TABLE);
		}

		if (element instanceof SapBusinessObject) {
			if (((SapBusinessObject) element).hasChildren()) {
				return imageRegistry.get(Constants.ICON_ID_FOLDER);
			}
			return imageRegistry.get(Constants.ICON_ID_TABLE_FOLDER);
		}
		return null;
	}

	public String getText(Object element) {
		if (element instanceof AbstractBusinessObject || element instanceof SapBusinessObject) {
			AbstractBusinessObject businessObject = (AbstractBusinessObject) element;

			String label = businessObject.getLabel();
			String description = businessObject.getDescription();
			// If a label exists, we will use it
			if (!Utilities.isEmpty(label)) {
				return businessObject.getName() + " (" + label + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			// If a description exists, we will use it
			if (!Utilities.isEmpty(description)) {
				return businessObject.getName() + " (" + description + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			// Neither label nor description is available
			return businessObject.getName();

		}
		return element.getClass().getName();
	}

	public void addListener(ILabelProviderListener listener) {
		// Nothing to be done here
	}

	public void dispose() {
		// Nothing to be done here
	}

	public boolean isLabelProperty(Object element, String property) {
		// Nothing to be done here
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// Nothing to be done here
	}

	@Override
	public String getToolTipText(Object element) {
		if (element instanceof AbstractBusinessObject) {
			AbstractBusinessObject businessObject = (AbstractBusinessObject) element;

			String description = businessObject.getDescription();
			if (!Utilities.isEmpty(description)) {
				return description;
			}

			String label = businessObject.getLabel();
			if (!Utilities.isEmpty(label)) {
				return label;
			}

			return businessObject.getName();
		}
		return Constants.EMPTY_STRING;
	}
}
