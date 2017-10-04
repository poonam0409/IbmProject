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



public class RequestJobTypeMIHCodeTableLoad extends RequestJobTypeABAPExtract
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
             static final String XML_ATTRIB_INSTANCE_ID       = RequestJobTypeMIHLoad.XML_ATTRIB_INSTANCE_ID;
             static final String XML_ATTRIB_ILS_SURR_KEY_FILE = RequestJobTypeIDocLoad.XML_ATTRIB_ILS_SURR_KEY_FILE;
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private int      _InstanceIdentifier;
   private String   _SurrogateKeyFile; 
   
   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; 
   }
   
   
   public RequestJobTypeMIHCodeTableLoad(String parJobName, String parPhysModelId,
                                            int parInstanceIdentifier, String parSurrogateKeyFile)
   {
      super(parJobName, parPhysModelId);
      
      // set values ...
      _InstanceIdentifier = parInstanceIdentifier;
      _SurrogateKeyFile   = parSurrogateKeyFile;
      
   } // end of RequestJobTypeMIHCodeTableLoad()
   
   
   public RequestJobTypeMIHCodeTableLoad(Node parJobTypeExtractNode)
   {
      super(parJobTypeExtractNode);

      String tmpAttribValue;
      
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      // set Instance Id 
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      tmpAttribValue = XMLUtils.getNodeAttributeValue(parJobTypeExtractNode, RequestJobTypeMIHLoad.XML_ATTRIB_INSTANCE_ID);
      if (tmpAttribValue != null)
      {
         _InstanceIdentifier = Integer.parseInt(tmpAttribValue);
      }
      
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      // Surrogate Key File 
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      _SurrogateKeyFile = XMLUtils.getNodeAttributeValue(parJobTypeExtractNode, XML_ATTRIB_ILS_SURR_KEY_FILE);
      
      // validate the member variables
      validate();
   } // end of RequestJobTypeMIHCodeTableLoad()

   
   public int getInstanceIdentifier()
   {
      return(_InstanceIdentifier);
   }

   
   public int getJobType()
   {
      return(TYPE_MIH_CODETABLE_LOAD_ID);
   }

   
   public String getJobTypeAsString()
   {
      return(TYPE_MIH_CODETABLE_LOAD);
   }
   
   
   String getJobTypeAttribs()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append(super.getJobTypeAttribs()); 
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_INSTANCE_ID, 
                                                    String.valueOf(_InstanceIdentifier)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_ILS_SURR_KEY_FILE, _SurrogateKeyFile));

      return(xmlBuf.toString());      
   }

   
   public String getSurrogateKeyFile()
   {
      return(_SurrogateKeyFile);
   }
   
   
   void setSupportedTypes(SupportedColumnTypesMap parColumTypesMap, 
                          SupportedTableTypesMap parTableTypesMap)
   {
//      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_LOGICAL_TABLE);
      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_JOINED_CHECK_AND_TEXT_TABLE);
//      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_LOGICAL_TABLE);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_JOINED_CHECK_AND_TEXT_TABLE);
   }

   
   public String toString()
   {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      traceStringBuf.append(" - instance Id: ");
      traceStringBuf.append(_InstanceIdentifier);
      traceStringBuf.append(" - SurrogateKeyFile: ");
      traceStringBuf.append(_SurrogateKeyFile);
      
      return(super.toString() + traceStringBuf.toString());
   }
   
   public void validate() throws IllegalArgumentException
   {
      super.validate();
      
      checkParamExists(_SurrogateKeyFile, "Surrogate Key File");
      
      // check the passed instance id
      RequestJobTypeMIHLoad.checkInstanceId(_InstanceIdentifier);
   }

} // end of class RequestJobTypeMIHCodeTableLoad
