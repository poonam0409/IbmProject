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


import com.ibm.is.sappack.gen.common.util.XMLUtils;



public final class ServerData
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public  static final String XML_TAG_SERVER                 = "Server";
   public  static final String XML_ATTRIB_SERVER_NAME         = "name";
   public  static final String XML_ATTRIB_SERVER_PORT         = "port";
   public  static final String XML_ATTRIB_SERVER_USER         = "user";
   public  static final String XML_ATTRIB_SERVER_PASSWORD     = "pw";
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private String  _Name;
   private String  _Password;
   private int     _Port;
   private String  _UserId;

   static String copyright()
   { return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; }   
   
   public ServerData(String parName, int parPort, 
                     String parUserId, String parPassword)
   {
      _Name         = parName;
      _Password     = parPassword;
      _Port         = parPort;
      _UserId       = parUserId;
   } // end of ServerData()
   
   
   public String getName()
   {
      return(_Name);
   }
   
   public String getPassword()
   {
      return(_Password);
   }
   
   public int getPort()
   {
      return(_Port);
   }
   
   public String getPortAsString()
   {
      return(String.valueOf(_Port));
   }
   
   public String toXML()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append("<");
      xmlBuf.append(XML_TAG_SERVER);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_SERVER_NAME, _Name));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_SERVER_PORT, String.valueOf(_Port)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_SERVER_USER, _UserId));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_SERVER_PASSWORD, _Password));
      xmlBuf.append(" />");
      
      return(xmlBuf.toString());
   }
   
   public String getUserId()
   {
      return(_UserId);
   }
   
} // end of class ServerData
