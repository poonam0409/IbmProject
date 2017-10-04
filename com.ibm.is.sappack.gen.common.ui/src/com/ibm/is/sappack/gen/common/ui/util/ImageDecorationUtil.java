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
// Module Name : com.ibm.is.sappack.gen.common.ui.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui.util;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

// utility class for image decoration which will basically overlay an image
// with a second one whereas the position of the image to be overlaid can be specified
public class ImageDecorationUtil extends CompositeImageDescriptor {

	private Image baseImage;
	private Image decoratorImage;
	private int position;

	public static final int TOP_LEFT = 0;
	public static final int TOP_RIGHT = 1;
	public static final int BOTTOM_LEFT = 2;
	public static final int BOTTOM_RIGHT = 3;

	// non-default constructor
	public ImageDecorationUtil(Image baseImage, Image decoratorImage, int position) {
		this.baseImage = baseImage;
		this.decoratorImage = decoratorImage;
		this.position = position;
	}

	// static inclusion of copyright statement
	static String copyright() {
		return com.ibm.is.sappack.gen.common.ui.util.Copyright.IBM_COPYRIGHT_SHORT;
	}

	// retrieve the composite image (base image with decorator image overlay)
	public Image getImage() {
		return createImage();
	}

	@Override
	protected void drawCompositeImage(int width, int height) {
		drawImage(baseImage.getImageData(), 0, 0);
		ImageData decoratorImageData = decoratorImage.getImageData();

		// we're going to overlay the decorator image at the given position
		switch (position) {
		case TOP_LEFT:
			drawImage(decoratorImageData, 0, 0);
			break;
		case TOP_RIGHT:
			drawImage(decoratorImageData, getSize().x - decoratorImageData.width, 0);
			break;
		case BOTTOM_LEFT:
			drawImage(decoratorImageData, 0, getSize().y - decoratorImageData.height);
			break;
		case BOTTOM_RIGHT:
			drawImage(decoratorImageData, getSize().x - decoratorImageData.width, getSize().y - decoratorImageData.height);
			break;
		default:
			break;
		}
	}

	@Override
	protected Point getSize() {
		if (baseImage != null) {
			return new Point(baseImage.getBounds().width, baseImage.getBounds().height);
		}

		return null;
	}
}
