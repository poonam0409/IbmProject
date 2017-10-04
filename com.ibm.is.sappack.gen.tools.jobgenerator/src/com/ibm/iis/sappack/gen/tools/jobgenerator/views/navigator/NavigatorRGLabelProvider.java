package com.ibm.iis.sappack.gen.tools.jobgenerator.views.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.graphics.Image;

import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.RGConfiguration;
import com.ibm.iis.sappack.gen.tools.sap.views.navigator.NavigatorRMLabelProvider;

public class NavigatorRGLabelProvider extends NavigatorRMLabelProvider {

  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

  	@Override
	public Image getImage(Object element) {
		if (element instanceof IFile) {
			IFile f = (IFile) element;
			String name = f.getName();
			if (name.endsWith(RGConfiguration.RGCONF_FILE_EXTENSION)) {
				return ImageProvider.getImage(ImageProvider.IMAGE_RGCONF_16);
			}
			if (name.endsWith(NavigatorRGContentProvider.ABAP_ARCHIVE_EXTENSION)) {
				return ImageProvider.getImage(ImageProvider.IMAGE_ABAP_ARCHIVE_16);
			}
		}
		return super.getImage(element);
	}


}