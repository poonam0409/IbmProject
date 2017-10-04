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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocType;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Segment;

public class SapIDocTypesTreeviewLabelProvider implements ILabelProvider {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.provider.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public Image getImage(Object element) {
		ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
		
		if (element instanceof IDocType) {
			return imageRegistry.get(Constants.ICON_ID_TABLE_FOLDER);
		}

		if (element instanceof Segment) {
			return imageRegistry.get(Constants.ICON_ID_TABLE);
		}
		
		return null;
	}

	public String getText(Object element) {
		String text = null;
		
		if (element instanceof IDocType) {
			IDocType idocType = (IDocType) element;
			text = idocType.getName().trim();
			String release = idocType.getRelease();
			if (release != null && !release.trim().isEmpty()) {
				text += " (" + release + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			String description = idocType.getDescription();
			if ((description != null) && !description.trim().equals("")) { //$NON-NLS-1$
				text += " - " + description; //$NON-NLS-1$
			}
		}
		else if (element instanceof Segment) {
			Segment segment = (Segment) element;
			text = segment.getType().trim();
			text += " (" + segment.getDefinition() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			String description = segment.getDescription();
			if ((description != null) && !description.trim().equals("")) { //$NON-NLS-1$
				text += " - " + description; //$NON-NLS-1$
			}
		}
		
		return text;
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
}
