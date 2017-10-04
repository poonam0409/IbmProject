//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2013                                              
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

package com.ibm.is.sappack.dsstages.common;


// public interface CCFResource {
abstract public class CCFResource {
	protected static CCFResource _CCFResource;

	// abstract methods
	// ----------------
	public abstract String getText(String textId);
	public abstract String getText(String textId, Object textParamArr[]);
	public abstract String getMessage(String msgId);
	public abstract String getMessage(String msgId, Object paramArr[]);

	
	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.Copyright.IBM_COPYRIGHT_SHORT;
	}

	protected CCFResource () {
		_CCFResource = this;
	}

	public static String getCCFText(String textId) {
		return(getCCFText(textId, (Object[]) null));
	}
	
	public static String getCCFText(String textId, String textParam) {
		Object paramArr[];
		
		if (textParam == null) {
			paramArr = null;
		}
		else {
			paramArr = new Object[] { textParam };
		}

		return(getCCFText(textId, paramArr ));
	}
	
	public static String getCCFText(String textId, Object textParamArr[]) {
		String text;

		if (_CCFResource == null) {
			text = null;
		}
		else {
			text = _CCFResource.getText(textId, textParamArr);
		}

		return(text);
	}
	
	public static String getCCFMessage(String msgId) {
		return(getCCFMessage(msgId, (Object[]) null));
	}
	
	public static String getCCFMessage(String msgId, String msgParam) {
		Object paramArr[];
		
		if (msgParam == null) {
			paramArr = null;
		}
		else {
			paramArr = new Object[] { msgParam };
		}

		return(getCCFMessage(msgId, paramArr ));
	}
	
	public static String getCCFMessage(String msgId, Object paramArr[]) {
		String msgText;

		if (_CCFResource == null) {
			msgText = null;
		}
		else {
			msgText = _CCFResource.getMessage(msgId, paramArr);
		}

		return(msgText);
	}
	
} // end of abstract class CCFResource
