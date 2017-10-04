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



public final class NetezzaStageData extends PersistenceData
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public  static final String XML_TAG_NAME_NETEZZA_STAGE     = "NetezzaStage"; //$NON-NLS-1$
   private static final String XML_TAG_DATASOURCE             = "DataSource";   //$NON-NLS-1$
   private static final String XML_TAG_DATABASE               = "DataBase";     //$NON-NLS-1$
   private static final String XML_TAG_USERNAME               = "User";         //$NON-NLS-1$
   private static final String XML_TAG_PASSWORD               = "Password";     //$NON-NLS-1$
//   private static final String XML_TAG_TABLENAME              = "TableName";    //$NON-NLS-1$
   private static final String XML_ATTRIB_TABLE_ACTION        = "tableAction";  //$NON-NLS-1$
   
   
   public  static final int    TABLE_ACTION_APPEND            = 0;
   public  static final int    TABLE_ACTION_CREATE            = 1;
   public  static final int    TABLE_ACTION_REPLACE           = 2;
   public  static final int    TABLE_ACTION_TRUNCATE          = 3;

   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private String   _DataSourceName;
   private String   _DatabaseName;
   private String   _UserName;
   private String   _Password;
//   private String   _TableName;
   private int      _TableAction;
   
   

   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.Copyright.IBM_COPYRIGHT_SHORT; 
   }

   
   public NetezzaStageData()
   {
//   	this(null, null, null, null, null);
   	this(null, null, null, null);
   }
   
   
//   public NetezzaStageData(String dataSource, String dataBase, String user, String password, String tableName)
   public NetezzaStageData(String dataSource, String dataBase, String user, String password)
   {
   	_DataSourceName = dataSource;
   	_DatabaseName   = dataBase;
   	_UserName       = user;
   	_Password       = password;
//   	_TableName      = tableName;
   	_TableAction = TABLE_ACTION_APPEND;
   } // end of NetezzaStageData()

   
   public NetezzaStageData(Node parNetezzaStageNode)
   {
      // get CodePage data first
      super(parNetezzaStageNode); 
      
      _DatabaseName           = XMLUtils.getChildNodeText(parNetezzaStageNode, XML_TAG_DATABASE);
      _DataSourceName         = XMLUtils.getChildNodeText(parNetezzaStageNode, XML_TAG_DATASOURCE);
      _UserName               = XMLUtils.getChildNodeText(parNetezzaStageNode, XML_TAG_USERNAME);
      _Password               = XMLUtils.getChildNodeText(parNetezzaStageNode, XML_TAG_PASSWORD);
//      _TableName              = XMLUtils.getChildNodeText(parNetezzaStageNode, XML_TAG_TABLENAME);
      _DatabaseName           = XMLUtils.getChildNodeText(parNetezzaStageNode, XML_TAG_DATABASE);
      _DatabaseName           = XMLUtils.getChildNodeText(parNetezzaStageNode, XML_TAG_DATABASE);
      _TableAction            = Integer.parseInt(XMLUtils.getNodeAttributeValue(parNetezzaStageNode, 
                                                                                XML_ATTRIB_TABLE_ACTION));
   } // end of NetezzaStageData()
   
   
   public String getDatabase()
   {
      return(_DatabaseName);
   }
   
   
   public String getDataSource()
   {
      return(_DataSourceName);
   }
   
   
   public String getPassword()
   {
      return(_Password);
   }
   
   
//   public String getTable()
//   {
//      return(_TableName);
//   }
   
   
   public String getUser()
   {
      return(_UserName);
   }
   
   
   public int getTableAction()
   {
      return(_TableAction);
   }
   
   
   private boolean isTableActionValid(int parAction)
   {
      boolean isValid;
      
      switch(parAction)
      {
         case TABLE_ACTION_APPEND:
         case TABLE_ACTION_CREATE:
         case TABLE_ACTION_REPLACE:
         case TABLE_ACTION_TRUNCATE:
              isValid = true;
              break;
              
         default:
              isValid = false;
      } // end of switch(parAction)
      
      return(isValid);
   } // end of isTableActionValid()
   
   
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


   public void setTableAction(int parTableAction)
   {
   	if (isTableActionValid(parTableAction))
   	{
   		_TableAction = parTableAction;
   	}
   } // end of setTableAction()
   
   
   public void setDatabase(String parDatabaseName)
   {
   	_DatabaseName = parDatabaseName;
   } // end of setDatabase()
   
   
   public void setDataSource(String parDataSourceName)
   {
   	_DataSourceName = parDataSourceName;
   } // end of setDataSource()
   
   
   public void setPassword(String parPassword)
   {
   	_Password = parPassword;
   } // end of setPassword()
   
   
//   public void setTable(String parTableName)
//   {
//   	_TableName = parTableName;
//   } // end of setTable()
//   
//   
   public void setUser(String parUserName)
   {
   	_UserName = parUserName;
   } // end of setUser()
   
   
   public String toXML()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append("<"); //$NON-NLS-1$
      xmlBuf.append(XML_TAG_NAME_NETEZZA_STAGE);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_TABLE_ACTION, String.valueOf(_TableAction)));
      xmlBuf.append(">"); //$NON-NLS-1$
      
      xmlBuf.append(XMLUtils.createCDATAElement(XML_TAG_DATABASE,   _DatabaseName));
      xmlBuf.append(XMLUtils.createCDATAElement(XML_TAG_DATASOURCE, _DataSourceName));
      if (_UserName != null)
      {
         xmlBuf.append(XMLUtils.createCDATAElement(XML_TAG_USERNAME,   _UserName));
         xmlBuf.append(XMLUtils.createCDATAElement(XML_TAG_PASSWORD,   _Password));
      }

//      xmlBuf.append(XMLUtils.createCDATAElement(XML_TAG_TABLENAME,  _TableName));
//      xmlBuf.append(getCodePageXML());
      xmlBuf.append("</"); //$NON-NLS-1$
      xmlBuf.append(XML_TAG_NAME_NETEZZA_STAGE);
      xmlBuf.append(">"); //$NON-NLS-1$
      
      return(xmlBuf.toString());
   } // end of toXML()
   
   
   public String toString()
   {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      traceStringBuf.append("Data Source: ");                  //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_DataSourceName));
      traceStringBuf.append(" - Database: ");                  //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_DatabaseName));
      traceStringBuf.append(" - User: ");                      //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_UserName));
      traceStringBuf.append(" - Password: ");                  //$NON-NLS-1$
      if (_Password == null)
      {
         traceStringBuf.append(String.valueOf("-"));           //$NON-NLS-1$
      }
      else
      {
         traceStringBuf.append(String.valueOf("*****"));       //$NON-NLS-1$
      }
//      traceStringBuf.append(" - Table name: ");                //$NON-NLS-1$
//      traceStringBuf.append(String.valueOf(_TableName));
      traceStringBuf.append(" - Table action: ");             //$NON-NLS-1$
      traceStringBuf.append(String.valueOf(_TableAction));
      traceStringBuf.append(getTraceString());
      
      return(traceStringBuf.toString());
   } // end of toString()
   
} // end of class NetezzaStageData
