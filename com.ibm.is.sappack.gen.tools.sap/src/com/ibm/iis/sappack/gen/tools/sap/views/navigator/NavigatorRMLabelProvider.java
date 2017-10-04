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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.views.navigator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.views.navigator;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE.SharedImages;

import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.tools.sap.views.dependency.DependencyView;
import com.ibm.iis.sappack.gen.tools.sap.views.navigator.NavigatorRMContentProvider.NavIDocSegment;
import com.ibm.iis.sappack.gen.tools.sap.views.navigator.NavigatorRMContentProvider.NavIDocType;
import com.ibm.iis.sappack.gen.tools.sap.views.navigator.NavigatorRMContentProvider.NavTable;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public class NavigatorRMLabelProvider extends LabelProvider {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	@Override
	public Image getImage(Object element) {
		if (element instanceof IFile) {
			IFile f = (IFile) element;
			Image img = DependencyView.getFileImage(f);
			return img;
			/*
			if (img != null) {
				return img;
			}
			return img = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
			*/
		}
		if (element instanceof ProjectCategory) {
			return ((ProjectCategory) element).img;
		}
		if (element instanceof IProject) {
			IProject project = (IProject) element;
			IProjectNature nature = null;
			try {
				nature = project.getNature(Constants.COM_IBM_DATATOOLS_CORE_UI_DATABASE_DESIGN_NATURE); 
			} catch (CoreException e) {
				Activator.logException(e);
				nature = null;
			}
			if (nature != null) {
				return ImageProvider.getImage(ImageProvider.IMAGE_DATADESIGN_PROJECT);
			}
			return PlatformUI.getWorkbench().getSharedImages().getImage(SharedImages.IMG_OBJ_PROJECT);
		}
		if (element instanceof NavTable) {
			return ImageProvider.getImage(ImageProvider.IMAGE_TABLES_16);
		}
		if (element instanceof NavIDocType) {
			return ImageProvider.getImage(ImageProvider.IMAGE_IDOC_TYPE_ICON);
		}
		if (element instanceof NavIDocSegment) {
			return ImageProvider.getImage(ImageProvider.IMAGE_IDOC_SEGMENT_ICON);
		}

		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IResource) {
			return ((IResource) element).getName();
		}
		if (element instanceof ProjectCategory) {
			return ((ProjectCategory) element).name;
		}
		if (element instanceof NameWrapper) {
			return ((NameWrapper) element).getName();
		}
		return null;
	}

}