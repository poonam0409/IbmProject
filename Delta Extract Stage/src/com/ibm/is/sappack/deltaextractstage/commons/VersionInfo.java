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
// Module Name : com.ibm.is.sappack.dsstages.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.deltaextractstage.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VersionInfo {


	
	@SuppressWarnings("nls")
	public static String getVersionInfo() {
		String UNKNOWN = "VERINFO=UNKNOWN";
		InputStream is = VersionInfo.class.getClassLoader().getResourceAsStream("com/ibm/is/sappack/deltaextractstage/common/build/dsstagesBuildID.properties");
		if (is == null) {
			return UNKNOWN;
		}
		Properties props = new Properties();
		try {
			props.load(is);
		} catch (IOException e) {
			return UNKNOWN;
		}

		StringBuffer versionString = new StringBuffer();

		String NL = "\n";
		versionString.append("VERINFO_PRODUCTNAME=" + props.getProperty("R3PRODUCTNAME") + NL);
		versionString.append("VERINFO_PRODUCTVERSION=" + props.getProperty("R3PRODUCTVERSION") + NL);
		versionString.append("VERINFO_BUILDNUMBER=" + props.getProperty("build.number") + NL);
		versionString.append("VERINFO_BUILDDATE=" + props.getProperty("build.date") + NL);
		versionString.append("VERINFO_PATCHLABEL=" + props.getProperty("patch.label") + NL);

		return versionString.toString();
	}

	
	@SuppressWarnings("nls")
	public static void main(String[] args) {
		String s = getVersionInfo();
		System.out.println("version info: " + s);
	}
	
}
