//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
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

import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.common.util.XMLUtils;


public final class ODBCStageData extends PersistenceData
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public static final String  XML_TAG_NAME_ODBC_STAGE          = "ODBCStage"; //$NON-NLS-1$
   public static final String  XML_ATTRIB_DATASOURCE            = "datasource"; //$NON-NLS-1$
   public static final String  XML_ATTRIB_USERNAME              = "user"; //$NON-NLS-1$
   public static final String  XML_ATTRIB_PASSWORD              = "pw"; //$NON-NLS-1$
   public static final String  XML_ATTRIB_ALT_SCHEMA_NAME       = "alternameSchemaName"; //$NON-NLS-1$
   public static final String  XML_ATTRIB_WRITE_MOD             = "writeMode"; //$NON-NLS-1$
   public static final String  XML_ATTRIB_INSERTION_MODE        = "insertMode"; //$NON-NLS-1$
   public static final String  XML_ATTRIB_FAIL_ON_SIZE_MISMATCH = "failOnSizeMismatch"; //$NON-NLS-1$
   public static final String  XML_ATTRIB_FAIL_ON_TYPE_MISMATCH = "failOnTypeMismatch"; //$NON-NLS-1$
   public static final String  XML_ATTRIB_QUOTED_IDS_ENABLED    = "quotedIDsEnabled"; //$NON-NLS-1$
   public static final String  XML_ATTRIB_SQL_WHERE_COND        = "sqlWhereCondition"; //$NON-NLS-1$
   public static final String  XML_ATTRIB_RECORD_COUNT          = "recordCount"; //$NON-NLS-1$
   public static final String  XML_ATTRIB_AUTO_COMMIT           = "autoCommit"; //$NON-NLS-1$
   public static final String  XML_ATTRIB_ARRAY_SIZE            = "arraySize"; //$NON-NLS-1$
   public static final int     WRITE_MODE_INSERT                = 0;
   public static final int     WRITE_MODE_UPDATE                = 1;
   public static final int     WRITE_MODE_DELETE                = 2;
   public static final int     WRITE_MODE_INSERT_THEN_UPDATE    = 3;
   public static final int     WRITE_MODE_UPDATE_THEN_INSERT    = 4;
   public static final int     WRITE_MODE_DELETE_THEN_INSERT    = 5;
   public static final int     INSERT_MODE_APPEND               = 10;
   public static final int     INSERT_MODE_CREATE               = 11;
   public static final int     INSERT_MODE_TRUNCATE             = 12;
   public static final int     INSERT_MODE_REPLACE              = 13;
   public static final boolean DEFAULT_AUTO_COMMIT_MODE         = false;
   public static final int     DEFAULT_ARRAY_SIZE               = 2000;
   public static final int     DEFAULT_RECORD_COUNT             = 2000;
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private String   _DataSource;
   private String   _UserName;
   private String   _Password;
   private int      _WriteMode;
   private int      _InsertMode;
   private String   _SQLWhereCondition;
   private boolean  _FailOnSizeMismatch;
   private boolean  _FailOnTypeMismatch;
   private boolean  _QuotedIDsEnabled;
   private boolean  _AutoCommitMode;
   private String   _AlternateSchema;
   private String   _RecordCount;
   private String   _ArraySize;
   
   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.Copyright.IBM_COPYRIGHT_SHORT; 
   }
   
   
   public ODBCStageData(String parODBCDataSourceName, String parODBCUsername, 
                        String parODBCPassword)
   {
      _DataSource         = parODBCDataSourceName;
      _UserName           = parODBCUsername;
      _Password           = parODBCPassword;
      _InsertMode         = INSERT_MODE_APPEND;
      _WriteMode          = WRITE_MODE_INSERT;
      _AutoCommitMode     = DEFAULT_AUTO_COMMIT_MODE;
      _FailOnSizeMismatch = false;
      _FailOnTypeMismatch = true;
      _QuotedIDsEnabled   = true;
      _SQLWhereCondition  = null;
      _RecordCount        = String.valueOf(DEFAULT_RECORD_COUNT);
      _ArraySize          = String.valueOf(DEFAULT_RECORD_COUNT);
   } // end of ODBCStageData()

   
   public ODBCStageData(ODBCStageData parODBCStageData)
   {
      _ArraySize          = parODBCStageData._ArraySize;
      _AlternateSchema    = parODBCStageData._AlternateSchema;
      _AutoCommitMode     = parODBCStageData._AutoCommitMode;
      _DataSource         = parODBCStageData._DataSource;
      _FailOnSizeMismatch = parODBCStageData._FailOnSizeMismatch;
      _FailOnTypeMismatch = parODBCStageData._FailOnTypeMismatch;
      _InsertMode         = parODBCStageData._InsertMode;
      _Password           = parODBCStageData._Password;
      _QuotedIDsEnabled   = parODBCStageData._QuotedIDsEnabled;
      _RecordCount        = parODBCStageData._RecordCount;
      _SQLWhereCondition  = parODBCStageData._SQLWhereCondition;
      _UserName           = parODBCStageData._UserName;
      _WriteMode          = parODBCStageData._WriteMode;
   } // end of ODBCStageData()
   
   
   public ODBCStageData(Node parODBCStageNode)
   {
      // get CodePage data first
      super(parODBCStageNode); 
      
      String tmpValue;
      
      _DataSource      = XMLUtils.getNodeAttributeValue(parODBCStageNode, XML_ATTRIB_DATASOURCE);
      _UserName        = XMLUtils.getNodeAttributeValue(parODBCStageNode, XML_ATTRIB_USERNAME);
      _Password        = XMLUtils.getNodeAttributeValue(parODBCStageNode, XML_ATTRIB_PASSWORD);
      _AlternateSchema = XMLUtils.getNodeAttributeValue(parODBCStageNode, XML_ATTRIB_ALT_SCHEMA_NAME);

      tmpValue = XMLUtils.getNodeAttributeValue(parODBCStageNode, XML_ATTRIB_WRITE_MOD);
      if (tmpValue != null)
      {
         _WriteMode = Integer.parseInt(tmpValue);
      }
      tmpValue = XMLUtils.getNodeAttributeValue(parODBCStageNode, XML_ATTRIB_INSERTION_MODE);
      if (tmpValue != null)
      {
         _InsertMode = Integer.parseInt(tmpValue);
      }
      tmpValue = XMLUtils.getNodeAttributeValue(parODBCStageNode, XML_ATTRIB_FAIL_ON_SIZE_MISMATCH);
      if (tmpValue != null)
      {
         _FailOnSizeMismatch = Boolean.valueOf(tmpValue).booleanValue();
      }
      tmpValue = XMLUtils.getNodeAttributeValue(parODBCStageNode, XML_ATTRIB_FAIL_ON_TYPE_MISMATCH);
      if (tmpValue != null)
      {
         _FailOnTypeMismatch = Boolean.valueOf(tmpValue).booleanValue();
      }
      tmpValue = XMLUtils.getNodeAttributeValue(parODBCStageNode, XML_ATTRIB_QUOTED_IDS_ENABLED);
      if (tmpValue != null)
      {
         _QuotedIDsEnabled = Boolean.valueOf(tmpValue).booleanValue();
      }
      _SQLWhereCondition = XMLUtils.getNodeAttributeValue(parODBCStageNode, XML_ATTRIB_SQL_WHERE_COND);
      
      tmpValue = XMLUtils.getNodeAttributeValue(parODBCStageNode, XML_ATTRIB_AUTO_COMMIT);
      if (tmpValue != null)
      {
         _AutoCommitMode = Boolean.valueOf(tmpValue).booleanValue();
      }
      tmpValue = XMLUtils.getNodeAttributeValue(parODBCStageNode, XML_ATTRIB_ARRAY_SIZE);
      setArraySize(tmpValue);
      tmpValue = XMLUtils.getNodeAttributeValue(parODBCStageNode, XML_ATTRIB_RECORD_COUNT);
      setRecordCount(tmpValue);
   } // end of ODBCStageData()


   public ODBCStageData(String parODBCDataSourceName)
   {
      this(parODBCDataSourceName, null, null);
   } // end of ODBCStageData()
   
   
   public String getAlternameSchemaName() 
   {
      return(_AlternateSchema);
   }


   public String getArraySize() 
   {
      return(_ArraySize);
   }


   public String getCodePageName() 
   {
      return(super.getCodePageName());
   }


   public String getDataSourceName()
   {
      return(_DataSource);
   }
   
   
   public int getInsertMode()
   {
      return(_InsertMode);
   }

   
   public String getPassword()
   {
      return(_Password);
   }
   
   
   public String getRecordCount() 
   {
      return(_RecordCount);
   }


   public String getSQLWhereCondition()
   {
      return(_SQLWhereCondition);
   }
   
   
   public String getUserName()
   {
      return(_UserName);
   }

   
   public int getWriteMode()
   {
      return(_WriteMode);
   }

   
   public boolean isAutoCommit()
   {
      return(_AutoCommitMode);
   }

   
   public boolean isFailOnSizeMismatch()
   {
      return(_FailOnSizeMismatch);
   }

   
   public boolean isFailOnTypeMismatch()
   {
      return(_FailOnTypeMismatch);
   }

   
   public boolean isQuotedIDsEnabled()
   {
      return(_QuotedIDsEnabled);
   }

   
   public void setAlternateSchemaName(String parSchemaName)
   {
      _AlternateSchema = parSchemaName;

      if (_AlternateSchema != null && _AlternateSchema.length() == 0)
      {
         _AlternateSchema = null;
      }
   }


   public void setArraySize(String parArraySize)
   {
      _ArraySize = parArraySize;

      if (_ArraySize == null || _ArraySize.length() == 0)
      {
         _ArraySize = String.valueOf(DEFAULT_ARRAY_SIZE);
      }
      else {
      	if (!StringUtils.isJobParamVariable(_ArraySize)) {
      		// not a Job parameter ==> check if it's an INT value
      		try {
      			Integer.parseInt(_ArraySize);
      		}
      		catch (NumberFormatException nmberFormatExcpt) {
      			// not an INT value ==> set array size default
               _ArraySize = String.valueOf(DEFAULT_ARRAY_SIZE);
      		}
      	}
      }
   }


   public void setAutoCommit(boolean parAutoCommit)
   {
      _AutoCommitMode = parAutoCommit;
   }

   
   public void setCodePageId(int parCodePageId) 
   {
      switch(parCodePageId)
      {
         case CODE_PAGE_DEFAULT:
         case CODE_PAGE_UNICODE:
              super.setCodePageName(null);
              super.setCodePageId(parCodePageId);
              break;

         default:
            throw new IllegalArgumentException("Invalid code page Id value '" + parCodePageId + "'.");
      } // end of switch(parSetting)
   } // end of setCodePage()


   public void setCodePageName(String parCodePageName)
   {
      super.setCodePageName(parCodePageName);
   }


   public void setFailOnSizeMismatch(boolean failOnMismatch)
   {
      _FailOnSizeMismatch = failOnMismatch;
   }

   
   public void setFailOnTypeMismatch(boolean failOnMismatch)
   {
      _FailOnTypeMismatch = failOnMismatch;
   }

   
   public void setInsertMode(int parInsertMode)
   {
      switch(parInsertMode)
      {
         case INSERT_MODE_APPEND:
         case INSERT_MODE_CREATE:
         case INSERT_MODE_TRUNCATE:
         case INSERT_MODE_REPLACE:
              _InsertMode = parInsertMode; 
              break;
              
         default:
              throw new IllegalArgumentException("Unknown Insert Mode '" + parInsertMode + "' !!!"); //$NON-NLS-1$ //$NON-NLS-2$
      }
   }

   
   public void setEnableQuotedIDs(boolean quotedIDsEnabled)
   {
      _QuotedIDsEnabled = quotedIDsEnabled;
   }

   
   public void setRecordCount(String parRecordCount)
   {
      _RecordCount = parRecordCount;

      if (_RecordCount == null || _RecordCount.length() == 0)
      {
         _RecordCount = String.valueOf(DEFAULT_RECORD_COUNT);
      }
      else {
      	if (!StringUtils.isJobParamVariable(_RecordCount)) {
      		// not a Job parameter ==> check if it's an INT value
      		try {
      			Integer.parseInt(_RecordCount);
      		}
      		catch (NumberFormatException nmberFormatExcpt) {
      			// not an INT value ==> set record count default
               _RecordCount = String.valueOf(DEFAULT_RECORD_COUNT);
      		}
      	}
      }
   }
   public void setSQLWhereCondition(String parSQLWhereCond)
   {
      _SQLWhereCondition = parSQLWhereCond;
   }
   
   
   public void setWriteMode(int parWriteMode)
   {
      switch(parWriteMode)
      {
         case WRITE_MODE_DELETE:
         case WRITE_MODE_DELETE_THEN_INSERT:
         case WRITE_MODE_INSERT:
         case WRITE_MODE_INSERT_THEN_UPDATE:
         case WRITE_MODE_UPDATE:
         case WRITE_MODE_UPDATE_THEN_INSERT:
              _WriteMode = parWriteMode; 
              break;
              
         default:
              throw new IllegalArgumentException("Unknown Write Mode '" + parWriteMode + "' !!!"); //$NON-NLS-1$ //$NON-NLS-2$
      }
   }

   
   public String toString()
   {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      traceStringBuf.append("DataSource: "); //$NON-NLS-1$
      traceStringBuf.append(_DataSource);
      traceStringBuf.append(" - UserName: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_UserName));
      traceStringBuf.append(" - Password: "); //$NON-NLS-1$
      if (_Password == null)
      {
         traceStringBuf.append("null"); //$NON-NLS-1$
      }
      else
      {
         traceStringBuf.append("******"); //$NON-NLS-1$
      }
      traceStringBuf.append(" - alternate Schema: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_AlternateSchema));
      traceStringBuf.append(" - auto commit: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_AutoCommitMode));
      traceStringBuf.append(" - array size: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_ArraySize));
      traceStringBuf.append(" - record count: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_RecordCount));
      traceStringBuf.append(" - Write Mode: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_WriteMode));
      traceStringBuf.append(" - Insertion Mode: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_InsertMode));
      traceStringBuf.append(" - FailOnSizeMismatch: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_FailOnSizeMismatch));
      traceStringBuf.append(" - FailOnTypeMismatch: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_FailOnTypeMismatch));
      traceStringBuf.append(" - QuotedIDsEnabled: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_QuotedIDsEnabled));
      traceStringBuf.append(" - SQL Where Cond: "); //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_SQLWhereCondition));
      traceStringBuf.append(getTraceString());
      
      return(traceStringBuf.toString());
   }
   
   public String toXML()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append("<"); //$NON-NLS-1$
      xmlBuf.append(XML_TAG_NAME_ODBC_STAGE);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_DATASOURCE, _DataSource));
      
      if (_UserName != null)
      {
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_USERNAME, _UserName));
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_PASSWORD, _Password));
      }

      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_ALT_SCHEMA_NAME, _AlternateSchema));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_RECORD_COUNT, _RecordCount));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_ARRAY_SIZE, _ArraySize));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_AUTO_COMMIT, String.valueOf(_AutoCommitMode)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_WRITE_MOD, String.valueOf(_WriteMode)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_INSERTION_MODE, String.valueOf(_InsertMode)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_FAIL_ON_SIZE_MISMATCH, String.valueOf(_FailOnSizeMismatch)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_FAIL_ON_TYPE_MISMATCH, String.valueOf(_FailOnTypeMismatch)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_QUOTED_IDS_ENABLED, String.valueOf(_QuotedIDsEnabled)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_SQL_WHERE_COND, _SQLWhereCondition));
      
      xmlBuf.append(">"); //$NON-NLS-1$
      xmlBuf.append(getCodePageXML());
      xmlBuf.append("</"); //$NON-NLS-1$
      xmlBuf.append(XML_TAG_NAME_ODBC_STAGE);
      xmlBuf.append(">"); //$NON-NLS-1$
      
      return(xmlBuf.toString());
   }
   
} // end of class ODBCStageData
