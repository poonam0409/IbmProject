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

import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.common.util.XMLUtils;



public final class SAPSystemData
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public static final String XML_TAG_NAME_SAP_SYSTEM      = "SAPSystem";
   public static final String XML_ATTRIB_NAME              = "name";
   public static final String XML_ATTRIB_USERNAME          = "user";
   public static final String XML_ATTRIB_PASSWORD          = "pw";
   public static final String XML_ATTRIB_CLIENT_NUMBER     = "clientNumber";
   public static final String XML_ATTRIB_LANGUAGE          = "language";
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private String _SystemName;
   private String _UserName;
   private String _Password;
   private String _ClientNumber;
   private String _Language;
   
   static String copyright()
   { return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; }
   
   public SAPSystemData(String parSAPSystemName)
   {
      this(parSAPSystemName, null, null, null, null);
   } // end of SAPSystemData()

   
   public SAPSystemData(String parSAPSystemName, String parSAPUserName, String parSAPPassword, 
                        String parSAPClientNumber, String parSAPLanguage)
   {
      if (parSAPSystemName == null || parSAPSystemName.length() == 0)
      {
         throw new IllegalArgumentException("SAP System name must be specified.");
      }
      
      // if a non-empty user Id has been specified ...
      if (parSAPUserName != null && parSAPUserName.trim().length() > 0)
      {
         // ==> Password, Client Number and Language must be specified too !!!
         if (parSAPPassword     == null || parSAPPassword.trim().length()     == 0 ||
             parSAPClientNumber == null || parSAPClientNumber.trim().length() == 0 || 
             parSAPLanguage     == null || parSAPLanguage.trim().length()     == 0)
         {
            throw new IllegalArgumentException("SAP UserId, Password, ClientNumber and Language must be specified.");
         }
         
         _UserName = StringUtils.trim(parSAPUserName);
      }
      else
      {
         _UserName = null;
      } // end of (else) if (parSAPUserName != null && parSAPUserName.trim().length() > 0)
      
      _SystemName   = parSAPSystemName;
      _Password     = StringUtils.trim(parSAPPassword);
      _ClientNumber = StringUtils.trim(parSAPClientNumber);
      _Language     = StringUtils.trim(parSAPLanguage);
      
   } // end of SAPSystemData()


   SAPSystemData(Node parSAPSystemNode)
   {
      _SystemName   = XMLUtils.getNodeAttributeValue(parSAPSystemNode, XML_ATTRIB_NAME);
      _UserName     = XMLUtils.getNodeAttributeValue(parSAPSystemNode, XML_ATTRIB_USERNAME);
      _Password     = XMLUtils.getNodeAttributeValue(parSAPSystemNode, XML_ATTRIB_PASSWORD);
      _ClientNumber = XMLUtils.getNodeAttributeValue(parSAPSystemNode, XML_ATTRIB_CLIENT_NUMBER);
      _Language     = XMLUtils.getNodeAttributeValue(parSAPSystemNode, XML_ATTRIB_LANGUAGE);
      
   } // end of SAPSystemData()


   public String getSAPClientNumber()
   {
      return(_ClientNumber);
   }

   
   public String getSAPSystemName()
   {
      return(_SystemName);
   }
   
   
   public String getSAPPassword()
   {
      return(_Password);
   }
   
   
   public String getSAPUserName()
   {
      return(_UserName);
   }
   
   
   public String getSAPUserLanguage()
   {
      return(_Language);
   }

   
   public String toXML()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append("<");
      xmlBuf.append(XML_TAG_NAME_SAP_SYSTEM);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_NAME,_SystemName));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_USERNAME, _UserName)); 
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_PASSWORD, _Password)); 
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_CLIENT_NUMBER, _ClientNumber)); 
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_LANGUAGE, _Language)); 
      xmlBuf.append("/>");
      
      return(xmlBuf.toString());
   }

   
   public String toString()
   {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      traceStringBuf.append("System name: ");
      traceStringBuf.append(_SystemName);
      traceStringBuf.append(" - UserName: ");
      traceStringBuf.append(String.valueOf(_UserName));
      traceStringBuf.append(" - Password: ");
      if (_Password == null)
      {
         traceStringBuf.append("null");
      }
      else
      {
         String pwTrace = "******";
         
         if (StringUtils.isJobParamVariable(_Password)) {
            pwTrace = _Password;
         }
         else {
            if (_Password.length() == 0) {
               pwTrace = "<empty>";
            }
         }
         traceStringBuf.append(pwTrace);
      }
      traceStringBuf.append(" - ClientNumber: ");
      traceStringBuf.append(String.valueOf(_ClientNumber));
      traceStringBuf.append(" - Language: ");
      traceStringBuf.append(String.valueOf(_Language));
      
      return(traceStringBuf.toString());
   }
   
} // end of class SAPSystemData
