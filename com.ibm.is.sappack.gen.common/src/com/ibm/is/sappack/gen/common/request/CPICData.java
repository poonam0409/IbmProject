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
// Module Name : com.ibm.is.sappack.gen.common.request
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.request;

import org.w3c.dom.Node;

import com.ibm.is.sappack.gen.common.util.XMLUtils;

public class CPICData {
	
	static String copyright() {
		return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	boolean isEnabled;
	boolean useSAPLogonDetails;
	String cpicUser;
	String cpicPassword;

	public static final String XML_TAG_CPIC_DATA = "cpicData";
	private static final String XML_ATTR_CPIC_ENABLED = "cpicEnabled";
	private static final String XML_ATTR_CPIC_USE_SAPLOGON_DETAILS = "cpicUseSAPLogonDetails";
	private static final String XML_ATTR_CPIC_USER = "cpicUser";
	private static final String XML_ATTR_CPIC_PASSWORD = "cpicPassword";

	public CPICData() {
		isEnabled = false;
	}

	public CPICData(Node n) {
		if (n != null) {
			isEnabled = Boolean.valueOf(XMLUtils.getNodeAttributeValue(n, XML_ATTR_CPIC_ENABLED)).booleanValue();
			if (isEnabled) {
				useSAPLogonDetails = Boolean.valueOf(XMLUtils.getNodeAttributeValue(n, XML_ATTR_CPIC_USE_SAPLOGON_DETAILS)).booleanValue();
				cpicUser = XMLUtils.getNodeAttributeValue(n, XML_ATTR_CPIC_USER);
				cpicPassword = XMLUtils.getNodeAttributeValue(n, XML_ATTR_CPIC_PASSWORD);
			}
		}
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isUseSAPLogonDetails() {
		return useSAPLogonDetails;
	}

	public void setUseSAPLogonDetails(boolean useSAPLogonDetails) {
		this.useSAPLogonDetails = useSAPLogonDetails;
	}

	public String getCpicUser() {
		return cpicUser;
	}

	public void setCpicUser(String cpicUser) {
		this.cpicUser = cpicUser;
	}

	public String getCpicPassword() {
		return cpicPassword;
	}

	public void setCpicPassword(String cpicPassword) {
		this.cpicPassword = cpicPassword;
	}

	
   public String toString() {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      traceStringBuf.append("Use SAP Logon Details: ");
      traceStringBuf.append(this.useSAPLogonDetails);
      traceStringBuf.append(" - UserId: ");
      traceStringBuf.append(this.cpicUser);
      traceStringBuf.append(" - Password: ");
      if (this.cpicPassword == null) {
         traceStringBuf.append("null");
      }
      else {
         traceStringBuf.append("******");
      }
      
      return(traceStringBuf.toString());
   }

   
	public String toXML() {
		StringBuffer xmlBuf = new StringBuffer();
		xmlBuf.append("<");
		xmlBuf.append(XML_TAG_CPIC_DATA);
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTR_CPIC_ENABLED, Boolean.valueOf(isEnabled)));
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTR_CPIC_USE_SAPLOGON_DETAILS, Boolean.valueOf(useSAPLogonDetails)));
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTR_CPIC_USER, cpicUser));
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTR_CPIC_PASSWORD, cpicPassword));
		xmlBuf.append("/>");
		return xmlBuf.toString();
	}
}
