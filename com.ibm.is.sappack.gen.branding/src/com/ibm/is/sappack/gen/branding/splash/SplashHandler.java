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
// Module Name : com.ibm.is.sappack.gen.branding.splash
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.branding.splash;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;

import com.ibm.is.sappack.gen.branding.BrandingUIPlugin;

public class SplashHandler extends AbstractSplashHandler {

	static String copyright() {
		return com.ibm.is.sappack.gen.branding.splash.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public SplashHandler() {
		super();
	}

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.splash.AbstractSplashHandler#init(org.eclipse.swt.widgets.Shell)
     */
    @Override
    public void init(Shell splash) {
        String nl = getNL();
        String language;
        String country;
        int index = nl.indexOf('_');
        if (index > 0) {
            language = nl.substring(0, index);
            country = nl.substring(index + 1);
        } else {
            language = nl;
            country = null;
        }
        String imageFilePath = String.format("nl/%s/%s/product_splash.bmp", language, country);
        ImageDescriptor imageDescriptor = BrandingUIPlugin.imageDescriptorFromPlugin(BrandingUIPlugin.PLUGIN_ID, imageFilePath);
        if (imageDescriptor == null) {
            imageFilePath = String.format("nl/%s/product_splash.bmp", language);
            imageDescriptor = BrandingUIPlugin.imageDescriptorFromPlugin(BrandingUIPlugin.PLUGIN_ID, imageFilePath);
            if (imageDescriptor == null) {
                imageDescriptor = BrandingUIPlugin.imageDescriptorFromPlugin(BrandingUIPlugin.PLUGIN_ID, "product_splash.bmp");
                if (imageDescriptor == null) {
                    super.init(splash);
                    return;
                }
            }
        }
        Rectangle splashBounds = splash.getBounds();
        Image image = imageDescriptor.createImage();
        Rectangle imageBounds = image.getBounds();
        Shell shell = new Shell(splash.getStyle());
        shell.setBackgroundImage(image);
        int x = splashBounds.x + ((splashBounds.width - imageBounds.width) / 2);
        int y = splashBounds.y + ((splashBounds.height - imageBounds.height) / 2);
        shell.setBounds(x, y, imageBounds.width, imageBounds.height);
        shell.open();
        super.init(shell);
        splash.close();
    }

    /**
     * Returns a guaranteed usable NL string.
     * @return the NL string.
     */
    private String getNL() {
        String nl = Platform.getNL();
        if ((nl == null) || (nl.length() == 0)) {
            String language = System.getProperty("user.language", "en");
            String country = System.getProperty("user.country", null);
            if (country == null) {
                nl = language;
            } else {
                nl = String.format("%s_%s", language, country);
            }
        }
        return nl;
    }
}
