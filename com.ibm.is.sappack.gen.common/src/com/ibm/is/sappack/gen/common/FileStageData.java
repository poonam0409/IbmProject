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



public final class FileStageData extends PersistenceData
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public  static final String XML_TAG_NAME_FILE_STAGE        = "FileStage"; //$NON-NLS-1$
   private static final String XML_ATTRIB_FILENAME_PREFIX     = "filenamePrefix"; //$NON-NLS-1$
   private static final String XML_ATTRIB_FILE_UPDATE_MODE    = "fileUpdateMode"; //$NON-NLS-1$
   private static final String XML_ATTRIB_1ST_LINE_IS_COLNAME = "firstLineIsColumnNames"; //$NON-NLS-1$
   private static final String XML_ATTRIB_DELIMTER            = "delimiter"; //$NON-NLS-1$
   private static final String XML_ATTRIB_UDEF_DELIMTER       = "delimiterChar"; //$NON-NLS-1$
   private static final String XML_ATTRIB_FINAL_DELIMTER      = "finalDelimiter"; //$NON-NLS-1$
   private static final String XML_ATTRIB_UDEF_FINAL_DELIMTER = "FinalDelimiterChar"; //$NON-NLS-1$
   private static final String XML_ATTRIB_QUOTE               = "quote"; //$NON-NLS-1$
   private static final String XML_ATTRIB_UDEF_QUOTE          = "quoteChar"; //$NON-NLS-1$
   private static final String XML_ATTRIB_UDEF_NULL_FIELD     = "nullField"; //$NON-NLS-1$
   
   public  static final String NULL_FIELD_DEFAULT_VALUE       = "";
   
   private static final int    QUOTE_MIN_LEN                = 1;
   private static final int    QUOTE_MAX_LEN                = 2;
   
   public  static final int    UPDATE_MODE_APPEND           = 1;
   public  static final int    UPDATE_MODE_CREATE           = 2;
   public  static final int    UPDATE_MODE_OVERWRITE        = 3;

   public  static final int    DELIMITER_NONE               = 10;
   public  static final int    DELIMITER_USERFEF            = 11;
   public  static final int    DELIMITER_WHITESPACE         = 12;
   public  static final int    DELIMITER_TAB                = 13;
   public  static final int    DELIMITER_END                = 14;
   public  static final int    DELIMITER_NULL               = 15;
   public  static final int    DELIMITER_COMMA              = 16;
 
   public  static final int    QUOTE_NONE                   = 20;
   public  static final int    QUOTE_USERFEF                = 21;
   public  static final int    QUOTE_SINGLE                 = 22;
   public  static final int    QUOTE_DOUBLE                 = 23;
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private String   _FilenamePrefix;
   private int      _UpdateMode;
   private boolean  _IsFirstLineColumnNames;
   private int      _DelimiterSelect;
   private int      _FinalDelimiterSelect;
   private int      _QuoteSelect;
   private String   _UserDefFinalDelimiter;
   private String   _UserDefDelimiter;
   private String   _UserDefQuote;
   private String   _UserDefNullField;
   

   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.Copyright.IBM_COPYRIGHT_SHORT; 
   }

   
   public FileStageData(String filenamePrefix, boolean isFirstLineColumnLine)
   {
      this(filenamePrefix, isFirstLineColumnLine, UPDATE_MODE_OVERWRITE);
   } // end of FileStageData()

   
   public FileStageData(String filenamePrefix, boolean isFirstLineColumnLine, int updateMode)
   {
      _FilenamePrefix          = filenamePrefix;
      _IsFirstLineColumnNames  = isFirstLineColumnLine;
      _DelimiterSelect         = DELIMITER_COMMA;
      _FinalDelimiterSelect    = DELIMITER_END;
      _QuoteSelect             = QUOTE_DOUBLE;
      _UserDefDelimiter        = null;
      _UserDefFinalDelimiter   = null;
      _UserDefQuote            = null;
      _UserDefNullField        = null;
      
      setUpdateMode(updateMode); 
   } // end of FileStageData()

   
   public FileStageData(String filenamePrefix)
   {
      this(filenamePrefix, true, UPDATE_MODE_OVERWRITE);
   } // end of FileStageData()
   
   
   public FileStageData(Node parFileStageNode)
   {
      // get CodePage data first
      super(parFileStageNode); 
      
      _FilenamePrefix         = XMLUtils.getNodeAttributeValue(parFileStageNode, XML_ATTRIB_FILENAME_PREFIX);
      _UpdateMode             = Integer.parseInt(XMLUtils.getNodeAttributeValue(parFileStageNode, 
                                                                                XML_ATTRIB_FILE_UPDATE_MODE));
      _IsFirstLineColumnNames = Boolean.valueOf(XMLUtils.getNodeAttributeValue(parFileStageNode, 
                                                                               XML_ATTRIB_1ST_LINE_IS_COLNAME)).booleanValue();
      _DelimiterSelect        = Integer.parseInt(XMLUtils.getNodeAttributeValue(parFileStageNode, 
                                                                                XML_ATTRIB_DELIMTER));
      _UserDefDelimiter       = XMLUtils.getNodeAttributeValue(parFileStageNode, XML_ATTRIB_UDEF_DELIMTER);
      _FinalDelimiterSelect   = Integer.parseInt(XMLUtils.getNodeAttributeValue(parFileStageNode, 
                                                                                XML_ATTRIB_FINAL_DELIMTER));
      _UserDefFinalDelimiter  = XMLUtils.getNodeAttributeValue(parFileStageNode, XML_ATTRIB_UDEF_FINAL_DELIMTER);
      _QuoteSelect            = Integer.parseInt(XMLUtils.getNodeAttributeValue(parFileStageNode, 
                                                                                XML_ATTRIB_QUOTE));
      _UserDefQuote           = XMLUtils.getNodeAttributeValue(parFileStageNode, XML_ATTRIB_UDEF_QUOTE);
      _UserDefNullField       = XMLUtils.getNodeAttributeValue(parFileStageNode, XML_ATTRIB_UDEF_NULL_FIELD);
      
   } // end of FileStageData()
   
   
   public String getFilenamePrefix()
   {
      return(_FilenamePrefix);
   }
   
   
   public int getUpdateMode()
   {
      return(_UpdateMode);
   }
   
   
   public int getDelimiter()
   {
      return(_DelimiterSelect);
   }
   
   
   public String getDelimiterChar()
   {
      return(_UserDefDelimiter);
   }
   
   
   public int getFinalDelimiter()
   {
      return(_FinalDelimiterSelect);
   }
   
   
   public String getFinalDelimiterChar()
   {
      return(_UserDefFinalDelimiter);
   }
   
   
   public String getNullFieldString()
   {
      return(_UserDefNullField);
   }
   
   
   public int getQuote()
   {
      return(_QuoteSelect);
   }
   
   
   public String getQuoteString()
   {
      return(_UserDefQuote);
   }
   
   
   public boolean is1stLineColumnNames()
   {
      return(_IsFirstLineColumnNames);
   }

   
   private boolean isDelimiterValid(int parDelimiter)
   {
      boolean isValid;
      
      switch(parDelimiter)
      {
         case DELIMITER_COMMA:
         case DELIMITER_END:
         case DELIMITER_NONE:
         case DELIMITER_NULL:
         case DELIMITER_TAB:
         case DELIMITER_WHITESPACE:
              isValid = true;
              break;
              
         default:
              isValid = false;
      } // end of switch(parDelimiter)
      
      return(isValid);
   } // end of isDelimiterValid()
   
   
   public void setCodePageId(int parCodePageId) 
   {
      switch(parCodePageId)
      {
         case CODE_PAGE_DEFAULT:
         case CODE_PAGE_ISO8859_1:
         case CODE_PAGE_UTF8:
         case CODE_PAGE_UTF16:
              super.setCodePageId(parCodePageId);
              break;

         default:
            throw new IllegalArgumentException("Invalid code page Id value '" + parCodePageId + "'.");
      } // end of switch(parSetting)
   } // end of setCodePage()

   public void setDelimiter(char parDelimiterChar)
   {
      _DelimiterSelect  = DELIMITER_USERFEF;
      _UserDefDelimiter = String.valueOf(parDelimiterChar);
      
   } // end of setDelimiter()
   
   
   public void setDelimiter(int parDelimiter)
   {
      if (isDelimiterValid(parDelimiter))
      {
         _DelimiterSelect  = parDelimiter;
         _UserDefDelimiter = null;
      }
      else
      {
         throw new IllegalArgumentException("Delimiter value '" + parDelimiter + "' is not valid."); //$NON-NLS-1$ //$NON-NLS-2$
      } // end of if (isDelimiterValid(parDelimiter))
      
   } // end of setDelimiter()
   
   
   public void setFinalDelimiter(char parDelimiterChar)
   {
      _FinalDelimiterSelect  = DELIMITER_USERFEF;
      _UserDefFinalDelimiter = String.valueOf(parDelimiterChar);
      
   } // end of setDelimiter()
   
   
   public void setFinalDelimiter(int parDelimiter)
   {
      if (isDelimiterValid(parDelimiter))
      {
         _FinalDelimiterSelect  = parDelimiter;
         _UserDefFinalDelimiter = null;
      }
      else
      {
         throw new IllegalArgumentException("Final Delimiter value '" + parDelimiter + "' is not valid."); //$NON-NLS-1$ //$NON-NLS-2$
      } // end of if (isDelimiterValid(parDelimiter))
      
   } // end of setDelimiter()
   
   
   public void setNullFieldValue(String parNullFieldValue)
   {
      _UserDefNullField = String.valueOf(parNullFieldValue);
   } // end of setNullFieldValue()
   
   
   public void setQuote(String parQuoteString)
   {
      StringBuffer tmpBuf;
      int          idx;
      
      if (parQuoteString == null                    || 
          parQuoteString.length() < QUOTE_MIN_LEN   || 
          parQuoteString.length() > QUOTE_MAX_LEN)
      {
         throw new IllegalArgumentException("Quote must be a valid string. Must have one or two characters."); //$NON-NLS-1$
      }
      
      _QuoteSelect = QUOTE_USERFEF;
      
      // special handling of the backslash (\) character
      if (parQuoteString.indexOf('\\') > -1)
      {
         tmpBuf = new StringBuffer();
         for (idx = 0; idx < parQuoteString.length(); idx ++)
         {
            if (parQuoteString.charAt(idx) == '\\')
            {
               tmpBuf.append("\\\\"); //$NON-NLS-1$
            }
            else
            {
               tmpBuf.append(parQuoteString.charAt(idx));
            }
         }
         _UserDefQuote = tmpBuf.toString();
      }
      else
      {
         _UserDefQuote = parQuoteString;
      } // end of (else) if (parQuoteString.indexOf('\\') > -1)
      
   } // end of setQuote()
   
   
   public void setQuote(int parQuote)
   {
      switch(parQuote)
      {
         case QUOTE_DOUBLE:
         case QUOTE_NONE:
         case QUOTE_SINGLE:
              break;
              
         default:
              throw new IllegalArgumentException("Quote value '" + parQuote + "' is not valid."); //$NON-NLS-1$ //$NON-NLS-2$
      } // end of switch(parQuote)
      
      _QuoteSelect  = parQuote;
      _UserDefQuote = null;
   } // end of setQuote()
   
   
   public void setUpdateMode(int parUpdateMode)
   {
      switch(parUpdateMode)
      {
         case UPDATE_MODE_APPEND:
         case UPDATE_MODE_CREATE:
         case UPDATE_MODE_OVERWRITE:
              _UpdateMode = parUpdateMode; 
              break;
              
         default:
              throw new IllegalArgumentException("Unknown Update Mode '" + parUpdateMode + "' !!!"); //$NON-NLS-1$ //$NON-NLS-2$
      }
   }

   
   public String toXML()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append("<"); //$NON-NLS-1$
      xmlBuf.append(XML_TAG_NAME_FILE_STAGE);
      
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_FILENAME_PREFIX, _FilenamePrefix));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_FILE_UPDATE_MODE, String.valueOf(_UpdateMode)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_1ST_LINE_IS_COLNAME, String.valueOf(_IsFirstLineColumnNames)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_DELIMTER, String.valueOf(_DelimiterSelect)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_UDEF_DELIMTER, String.valueOf(_UserDefDelimiter)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_FINAL_DELIMTER, String.valueOf(_FinalDelimiterSelect)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_UDEF_FINAL_DELIMTER, String.valueOf(_UserDefFinalDelimiter)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_QUOTE, String.valueOf(_QuoteSelect)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_UDEF_QUOTE, String.valueOf(_UserDefQuote)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_UDEF_NULL_FIELD, _UserDefNullField));
      
      xmlBuf.append(">"); //$NON-NLS-1$
      xmlBuf.append(getCodePageXML());
      xmlBuf.append("</"); //$NON-NLS-1$
      xmlBuf.append(XML_TAG_NAME_FILE_STAGE);
      xmlBuf.append(">"); //$NON-NLS-1$
      
      return(xmlBuf.toString());
   }
   
   
   public String toString()
   {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      traceStringBuf.append("Filename Prfx: "); //$NON-NLS-1$
      traceStringBuf.append(_FilenamePrefix);
      traceStringBuf.append(" - fileUpdateMode: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_UpdateMode));
      traceStringBuf.append(" - Is1stLineColNames: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_IsFirstLineColumnNames));
      
      traceStringBuf.append(" - delimiter: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_DelimiterSelect));
      if (_DelimiterSelect == DELIMITER_USERFEF)
      {
         traceStringBuf.append(" - delimiter char: "); //$NON-NLS-1$
         traceStringBuf.append(String.valueOf(_UserDefDelimiter));
      }
      
      traceStringBuf.append(" - final delimiter: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_FinalDelimiterSelect));
      if (_FinalDelimiterSelect == DELIMITER_USERFEF)
      {
         traceStringBuf.append(" - final delimiter char: "); //$NON-NLS-1$
         traceStringBuf.append(String.valueOf(_UserDefFinalDelimiter));
      }

      traceStringBuf.append(" - quote: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_QuoteSelect));
      if (_QuoteSelect == QUOTE_USERFEF)
      {
         traceStringBuf.append(" - quote char: "); //$NON-NLS-1$
         traceStringBuf.append(String.valueOf(_UserDefQuote));
      }
      traceStringBuf.append(" - null field: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_UserDefNullField));
      traceStringBuf.append(getTraceString());
      
      return(traceStringBuf.toString());
   }
   
} // end of class FileStageData
