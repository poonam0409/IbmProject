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
// Module Name : com.ibm.is.sappack.gen.tools.sap.utilities
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.utilities;

public class CodePageCheck {
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private String _AllowedASCIIs;

	public CodePageCheck() {
		char[] charArr = new char[256];

		for (int idx = 0; idx < charArr.length; idx++) {
			charArr[idx] = (char) idx;
		}

		_AllowedASCIIs = new String(charArr);
	} // end of CodePageCheck()

	public boolean isASCII(String str) {
		int arrIdx;
		int checkStringLen;
		boolean retIsAscii;

		arrIdx = 0;
		checkStringLen = str.length();
		retIsAscii = true;
		while (arrIdx < checkStringLen && retIsAscii) {
			if (!_AllowedASCIIs.contains(String.valueOf(str.charAt(arrIdx)))) {
				retIsAscii = false;
			} else {
				arrIdx++;
			}
		} // end of while(arrIdx < checkStrLen && retIsAscii)

		return (retIsAscii);
	} // end of isASCII()

}