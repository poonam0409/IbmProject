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
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.swt.graphics.Image;

import com.ibm.is.sappack.gen.common.ui.util.ImageDecorationUtil;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Segment;

public class SapIDocTypesTreeviewLabelDecorator extends SapIDocTypesTreeviewLabelProvider implements ILabelDecorator {

	// default constructor
	public SapIDocTypesTreeviewLabelDecorator() {
		super();
	}

	// static inclusion of copyright statement
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.provider.Copyright.IBM_COPYRIGHT_SHORT;
	}

	@Override
	public Image decorateImage(Image image, Object element) {
		ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
		Image decoratedImage = null;

		// we only add a decorator to the default image for IDoc segments that are marked 'mandatory'
		if (element instanceof Segment) {
			if (((Segment) element).mandatory) {
				decoratedImage = drawDecoration(image, imageRegistry.get(Constants.DECORATOR_ID_MANDATORY_SEGMENT));
			}
		}

		return decoratedImage;
	}

	@Override
	public String decorateText(String text, Object element) {
		return null;
	}

	// we have to draw the decorated image ourselves so we hand over the drawing to a utility class
	// which will take care of overlaying the standard (base) image with the decoration image
	private Image drawDecoration(Image baseImage, Image decoratorImage) {
		Image finalImage;
		ImageDecorationUtil decorator =
		      new ImageDecorationUtil(baseImage, decoratorImage, ImageDecorationUtil.BOTTOM_LEFT);
		finalImage = decorator.getImage();
		return finalImage;
	}
}
