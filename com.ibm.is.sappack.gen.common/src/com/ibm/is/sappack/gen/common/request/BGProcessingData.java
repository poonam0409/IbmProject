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



final public class BGProcessingData
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public  static final String XML_TAG_BG_PROC                    = "BackgroundProcessing";       //$NON-NLS-1$ 
   private static final String XML_ATTRIB_BG_PROC_ENABLED         = "enabled";                    //$NON-NLS-1$
   private static final String XML_ATTRIB_BG_PROC_VARIANT_NAME    = "variantName";                //$NON-NLS-1$
   private static final String XML_ATTRIB_BG_PROC_START_DELAY     = "startDelay";                 //$NON-NLS-1$
   private static final String XML_ATTRIB_BG_PROC_KEEP_VARIANT    = "keepVariant";                //$NON-NLS-1$
   private static final String XML_ATTRIB_BG_PROC_USE_SAP_VARIANT = "useSAPVariant";              //$NON-NLS-1$
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private boolean   _IsEnabled;
   private String    _VariantName;
   private int       _StartDelay;
   private boolean   _DoKeepVariant;
   private boolean   _DoUseSAPVariant;
   
   static String copyright()
   { return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; }
   
   BGProcessingData()
   {
      _IsEnabled       = false;
      _VariantName     = null;
      _StartDelay      = 5;
      _DoKeepVariant   = false;
      _DoUseSAPVariant = false;
   } // end of BGProcessingData()
   
   
   BGProcessingData(Node parBGProcessingNode)
   {
      this();
      
      String   tmpBoolString;
      String   tmpIntString;

      if (parBGProcessingNode != null)
      {
         // get BACKGROUND PROCESS info 
         tmpBoolString = XMLUtils.getNodeAttributeValue(parBGProcessingNode, XML_ATTRIB_BG_PROC_ENABLED);
         if (tmpBoolString != null)
         {
            _IsEnabled = Boolean.valueOf(tmpBoolString).booleanValue();
            
            if (_IsEnabled)
            {
               _VariantName = XMLUtils.getNodeAttributeValue(parBGProcessingNode, XML_ATTRIB_BG_PROC_VARIANT_NAME);
               
               tmpIntString = XMLUtils.getNodeAttributeValue(parBGProcessingNode, XML_ATTRIB_BG_PROC_START_DELAY);
               if (tmpIntString != null)
               {
                  _StartDelay = Integer.parseInt(tmpIntString);
               }
                           
               tmpBoolString = XMLUtils.getNodeAttributeValue(parBGProcessingNode, XML_ATTRIB_BG_PROC_KEEP_VARIANT);
               if (tmpBoolString != null)
               {
                  _DoKeepVariant = Boolean.valueOf(tmpBoolString).booleanValue();
               }
               
               tmpBoolString = XMLUtils.getNodeAttributeValue(parBGProcessingNode, XML_ATTRIB_BG_PROC_USE_SAP_VARIANT);
               if (tmpBoolString != null)
               {
                  _DoUseSAPVariant = Boolean.valueOf(tmpBoolString).booleanValue();
               }
            } // end of if (_IsEnabled)
         } // end of if (tmpBoolString != null)
      } // end of if (parBGProcessingNode != null)
   } // end of BGProcessingData()
   
   
   public boolean doKeepVariant() 
   {
      return(_DoKeepVariant);
   }
   
   
   public boolean doUseSAPVariant() 
   {
      return(_DoUseSAPVariant);
   }
   
   
   public int getStartDelay() 
   {
      return(_StartDelay);
   }
   
   
   public String getVariant() 
   {
      return(_VariantName);
   }
   
   
   public boolean isEnabled() 
   {
      return(_IsEnabled);
   }
   
   
   public void setBackgroundProcessOptions(String parVariantName, int parStartDelay, 
                                           boolean doUseSAPVariant, boolean doKeepVariant) 
   {
      if (parVariantName == null)
      {
         throw new IllegalArgumentException("VariantName must be specified.");
      }

      if (parStartDelay < 1)
      {
         throw new IllegalArgumentException("StartDelay must not be less then 1.");
      }

      _IsEnabled       = true;
      _VariantName     = parVariantName;
      _StartDelay      = parStartDelay;
      _DoKeepVariant   = doKeepVariant;
      _DoUseSAPVariant = doUseSAPVariant;
   }


   public String toString()
   {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      
      if (_IsEnabled)
      {
         traceStringBuf.append("Variant: ");
         traceStringBuf.append(_VariantName);
         traceStringBuf.append(" - Start Delay: ");
         traceStringBuf.append(_StartDelay);
         traceStringBuf.append(" - Keep Variant: ");
         traceStringBuf.append(_DoKeepVariant);
         traceStringBuf.append(" - Use SAP Variant: ");
         traceStringBuf.append(_DoUseSAPVariant);
      } // end of if (_IsEnabled)
      
      return(traceStringBuf.toString());
   }

   
   public String toXML()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append("<");
      xmlBuf.append(XML_TAG_BG_PROC);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_BG_PROC_ENABLED, 
                    String.valueOf(_IsEnabled)));
      
      if (_IsEnabled)
      {
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_BG_PROC_VARIANT_NAME, 
                                                       String.valueOf(_VariantName)));
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_BG_PROC_START_DELAY, 
                                                       String.valueOf(_StartDelay)));
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_BG_PROC_KEEP_VARIANT, 
                                                       String.valueOf(_DoKeepVariant)));
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_BG_PROC_USE_SAP_VARIANT, 
                                                       String.valueOf(_DoUseSAPVariant)));
      } // end of if (_IsBackgoundProcessEnabled)
      
      xmlBuf.append("/>");
      
      return(xmlBuf.toString());
   }
   
} // end of class BGProcessingData
