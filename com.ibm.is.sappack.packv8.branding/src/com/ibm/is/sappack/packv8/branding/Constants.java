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
// Module Name : com.ibm.is.sappack.gen.branding
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.packv8.branding;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

public final class Constants {

	static String copyright()
	{ return com.ibm.is.sappack.packv8.branding.Copyright.IBM_COPYRIGHT_SHORT; }

	private static final String ABOUT_MAPPINGS = "$nl$/about.mappings";

	private static final int APP_VERSION_INDEX = 0;

	private static final int APP_VERSION_QUALIFIER_INDEX = 1;
	
	private static final int APP_BUILD_NUMBER_INDEX = 2;
	
	public static final String getVersionQualifier() {
		return Constants.parseAboutMappings()[Constants.APP_VERSION_QUALIFIER_INDEX];
	}
	
	public static final String getBuildNumber() {
		return Constants.parseAboutMappings()[Constants.APP_BUILD_NUMBER_INDEX];
	}
	
	public static final String getVersionNumber() {
		return Constants.parseAboutMappings()[Constants.APP_VERSION_INDEX];
	}

	private static final String[] parseAboutMappings() {
		IProduct product = Platform.getProduct();
		if (product == null)
			return null;
		URL location = FileLocator.find(product.getDefiningBundle(), new Path(ABOUT_MAPPINGS), null);
		PropertyResourceBundle bundle = null;
		if (location != null) {
			InputStream is = null;
			try {
				is = location.openStream();
				bundle = new PropertyResourceBundle(is);
			} catch (IOException e) {
				// Nothing we can do here
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (IOException e) {
					// do nothing if we fail to close
				}
			}
		}

		ArrayList<String> mappingsList = new ArrayList<String>(3);
		if (bundle != null) {
			for (int i = 0;; ++i) {
				try {
					mappingsList.add(bundle.getString(Integer.toString(i)));
				} catch (MissingResourceException e) {
					break; // No more keys!
				}
			}
		}

		String[] result = new String[mappingsList.size()];
		mappingsList.toArray(result);
		return result;
	}
}