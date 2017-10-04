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
// Module Name : com.ibm.is.sappack.gen.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.common;

import org.w3c.dom.Node;

import com.ibm.is.sappack.gen.common.util.XMLUtils;



public abstract class PersistenceData
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public  static final String XML_TAG_NAME_CODEPAGE          = "CodePage"; //$NON-NLS-1$
   private static final String XML_ATTRIB_SELECTED            = "selected"; //$NON-NLS-1$
   private static final String XML_ATTRIB_NAME                = "name"; //$NON-NLS-1$
   public  static final int    CODE_PAGE_DEFAULT              = 1;
   public  static final int    CODE_PAGE_UNICODE              = 2;
   public  static final int    CODE_PAGE_UTF8                 = 3;
   public  static final int    CODE_PAGE_UTF16                = 4;
   public  static final int    CODE_PAGE_ISO8859_1            = 5;
   public  static final int    CODE_PAGE_USER_SPECIFIED       = 6;


   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private String   _CodePageName;
   private int      _CodePageId;


   // -------------------------------------------------------------------------------------
   //                                 Abstract Methods
   // -------------------------------------------------------------------------------------
   abstract public String toXML();
   abstract public String toString();
   
   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.Copyright.IBM_COPYRIGHT_SHORT; 
   }
   
   
   public PersistenceData()
   {
      _CodePageId = CODE_PAGE_DEFAULT;
      _CodePageName    = null;
   } // end of PersistenceData()
   
   
   public PersistenceData(Node parParentDataNode)
   {
      Node   codePageNode;
      String tmpValue;

      _CodePageId = CODE_PAGE_DEFAULT;
      
      // get the Codepage node
      codePageNode = XMLUtils.getChildNode(parParentDataNode, XML_TAG_NAME_CODEPAGE);
      
      if (codePageNode != null)
      {
         tmpValue = XMLUtils.getNodeAttributeValue(codePageNode, XML_ATTRIB_SELECTED);
         if (tmpValue != null)
         {
            _CodePageId = Integer.parseInt(tmpValue);
         }
      }

      switch(_CodePageId)
      {
         case CODE_PAGE_USER_SPECIFIED:
              _CodePageName = XMLUtils.getNodeAttributeValue(codePageNode, XML_ATTRIB_NAME);
              break;

         default:
              _CodePageName = null;
      } // end of switch(_CodePageSetting)
      
   } // end of PersistenceData()


   protected String getCodePageName() 
   {
      return(_CodePageName);
   }


   public int getCodePageId() 
   {
      return(_CodePageId);
   }


   protected String getTraceString()
   {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      traceStringBuf.append(" - CP selection: "); //$NON-NLS-1$
      traceStringBuf.append(_CodePageId);
      traceStringBuf.append(" - CP name: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_CodePageName));
      
      return(traceStringBuf.toString());
   }


   protected String getCodePageXML()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append("<"); //$NON-NLS-1$
      xmlBuf.append(XML_TAG_NAME_CODEPAGE);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_SELECTED, String.valueOf(_CodePageId)));
      
      switch(_CodePageId)
      {
         case CODE_PAGE_USER_SPECIFIED:
              xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_NAME, _CodePageName));
              break;

         default:
              ;
      } // end of switch(_CodePageSetting)
      xmlBuf.append(" />"); //$NON-NLS-1$
      
      return(xmlBuf.toString());
   }


   protected void setCodePageName(String parCodePageName) 
   {
      _CodePageName    = parCodePageName;
      if (_CodePageName != null)
      {
         _CodePageId = CODE_PAGE_USER_SPECIFIED;
      }
   }


   protected void setCodePageId(int parCodePageId) 
   {
      _CodePageId = parCodePageId;
   }

} // end of abstract class PersistenceData
