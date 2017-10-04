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



public final class JobParamData
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public  static final int    JOB_PARAM_TYPE_STRING           = 0;
   public  static final int    JOB_PARAM_TYPE_ENCYRYPTED       = 1;
   public  static final int    JOB_PARAM_TYPE_INTEGER          = 2;
   public  static final int    JOB_PARAM_TYPE_FLOAT            = 3;
   public  static final int    JOB_PARAM_TYPE_PATHNAME         = 4;
   public  static final int    JOB_PARAM_TYPE_LIST             = 5;
   public  static final int    JOB_PARAM_TYPE_DATE             = 6;
   public  static final int    JOB_PARAM_TYPE_TIME             = 7;
   public  static final int    JOB_PARAM_TYPE_PARAM_SET        = 8;
   
   public  static final String XML_TAG_JOB_PARAM               = "JobParam";
   public  static final String XML_ATTRIB_NAME                 = "name";
   public  static final String XML_ATTRIB_PROMPT               = "prompt";
   public  static final String XML_ATTRIB_TYPE                 = "type";
   public  static final String XML_ATTRIB_DEFAULT              = "default";
   public  static final String XML_ATTRIB_DESCRIPTION          = "desc";
   

   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private String _DefaultValue;
   private String _HelpText;
   private String _Prompt;
   private String _Name;
   private int    _Type;

   
   static String copyright() 
   { 
      return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; 
   }
   
   public JobParamData(String parName, String parPrompt, int parType, 
                       String parDefaultValue, String parHelpText)
   {
      
      _Name         = parName;
      _DefaultValue = parDefaultValue;
      _Prompt       = parPrompt;
      _HelpText     = parHelpText;
      
      // check passed type
      checkParamType(parType);
      _Type = parType;
   } // end of JobParamData()


   public JobParamData(Node jobParamNode)
   {
      int paramType;
      
      // get job parameter attributes from DOM node
      if (jobParamNode.getNodeName().equals(XML_TAG_JOB_PARAM))
      {
         _Name         = XMLUtils.getNodeAttributeValue(jobParamNode, XML_ATTRIB_NAME);
         _DefaultValue = XMLUtils.getNodeAttributeValue(jobParamNode, XML_ATTRIB_DEFAULT);
         _Prompt       = XMLUtils.getNodeAttributeValue(jobParamNode, XML_ATTRIB_PROMPT);
         _HelpText     = XMLUtils.getNodeAttributeValue(jobParamNode, XML_ATTRIB_DESCRIPTION);
         
         // check passed type
         paramType = Integer.parseInt(XMLUtils.getNodeAttributeValue(jobParamNode, XML_ATTRIB_TYPE));
         checkParamType(paramType);
         _Type = paramType;
      } // end of if (folderNode.getNodeName().equals(XML_TAG_FOLDER))
      
   } // end of JobParamData()

   
   private void checkParamType(int pTypeVal)
   {
      // check passed type first
      switch(pTypeVal)
      {
         case JOB_PARAM_TYPE_DATE:
         case JOB_PARAM_TYPE_ENCYRYPTED:
         case JOB_PARAM_TYPE_FLOAT:
         case JOB_PARAM_TYPE_INTEGER:
         case JOB_PARAM_TYPE_LIST:
         case JOB_PARAM_TYPE_PARAM_SET:
         case JOB_PARAM_TYPE_PATHNAME:
         case JOB_PARAM_TYPE_STRING:
         case JOB_PARAM_TYPE_TIME:
              break;
         
         default:
              throw new IllegalArgumentException("Unknown Job Param Type '" + pTypeVal + "' !!!");
      } // end of switch(pTypeVal)
 
   } // end of JobParamData()

   
   public String getDefaultValue()
   {
      return(_DefaultValue);
   }
   
   
   public String getHelpText()
   {
      return(_HelpText);
   }
   
   
   public String getName()
   {
      return(_Name);
   }
   
   
   public String getPrompt()
   {
      return(_Prompt);
   }
   
   
   public int getType()
   {
      return(_Type);
   }
   
   
   public String getTypeAsString()
   {
      return(String.valueOf(_Type));
   }

   public boolean equals(JobParamData other) {
      boolean isEqual;
      
      if (_DefaultValue != null && other._DefaultValue != null  &&
          _DefaultValue.equals(other._DefaultValue)             &&
          _HelpText != null &&other._HelpText != null           &&
          _HelpText.equals(other._HelpText)                     &&
          _Name != null && other._Name != null                  &&
          _Name.equals(other._Name)                             &&
          _Prompt != null && other._Prompt != null              &&
          _Prompt.equals(other._Prompt)                         &&
          _Type == other._Type) {
         
         isEqual = true;
      }
      else {
         isEqual = false;
      }

      return(isEqual);
   }


   public String toString()
   {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      
      traceStringBuf.append("<START>name = ");
      traceStringBuf.append(_Name);
      traceStringBuf.append(" - type = ");
      traceStringBuf.append(_Type);
      traceStringBuf.append(" - prompt = ");
      traceStringBuf.append(_Prompt);
      traceStringBuf.append(" - default value = ");
      if (_Type == JOB_PARAM_TYPE_ENCYRYPTED)
      {
         traceStringBuf.append("******");
      }
      else
      {
         traceStringBuf.append(_DefaultValue);
      }
      traceStringBuf.append(" - help text = ");
      traceStringBuf.append(_HelpText);
      traceStringBuf.append("<END>");
      
      return(traceStringBuf.toString());
   }
   
   
   public String toXML()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append("<");
      xmlBuf.append(XML_TAG_JOB_PARAM);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_NAME,_Name));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_PROMPT, _Prompt)); 
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_TYPE, getTypeAsString())); 
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_DEFAULT, _DefaultValue)); 
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_DESCRIPTION, _HelpText)); 
      xmlBuf.append(" />");
      
      return(xmlBuf.toString());
   }
   
} // end of class JobParamData
