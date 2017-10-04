//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.server.jobgenerator.stages
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.jobgenerator.stages;


import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.ibm.is.sappack.gen.common.NetezzaStageData;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.util.ObjectParamMap;


public abstract class NetezzaStageProperties extends StageProperties
{
   // -------------------------------------------------------------------------------------
   //                                         Constants
   // -------------------------------------------------------------------------------------
   public  static final String STAGE_TYPE_CLASS_NAME  = StageProperties.STAGE_TYPE_CLASS_NAME_CUSTOM_STAGE;
   private static final String PROP_XML_PROP_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-16\"?><Properties version=\"1.1\">" + //$NON-NLS-1$
                                                        "<Common>" +                                                                //$NON-NLS-1$
                                                        "<Context type=\"int\">2</Context>" +                                       //$NON-NLS-1$
                                                        "<Variant type=\"string\">4.5</Variant>" +                                  //$NON-NLS-1$
                                                        "<DescriptorVersion type=\"string\">1.0</DescriptorVersion>" +              //$NON-NLS-1$
                                                        "<PartitionType type=\"int\">-1</PartitionType>" +                          //$NON-NLS-1$
                                                        "<RCP type=\"int\">0</RCP>" +                                               //$NON-NLS-1$
                                                        "</Common>" +      	                                                      //$NON-NLS-1$
                                                        "<Connection>" +                                                            //$NON-NLS-1$
            /* param 0 = Data Source     */             "<DataSource modified=\"1\" type=\"string\"><![CDATA[{0}]]></DataSource>" + //$NON-NLS-1$
            /* param 1 = Database        */             "<Database modified=\"1\" type=\"string\"><![CDATA[{1}]]></Database>" +     //$NON-NLS-1$
            /* param 2 = user            */             "<Username modified=\"1\" type=\"string\"><![CDATA[{2}]]></Username>"+      //$NON-NLS-1$
            /* param 3 = (encrypted) password */        "<Password modified=\"1\" type=\"protectedstring\"><![CDATA[{3}]]></Password>" + //$NON-NLS-1$
                                                        "<UseSeparateConnectionForTWT collapsed=\"1\" type=\"bool\"><![CDATA[0]]></UseSeparateConnectionForTWT>" + //$NON-NLS-1$ 
                                                        "</Connection>" +                                                           //$NON-NLS-1$
                                                        "<Usage>" +                                                                 //$NON-NLS-1$
                                                        "<WriteMode type=\"int\"><![CDATA[0]]></WriteMode>" +                       //$NON-NLS-1$
            /* param 4 = table name */                  "<TableName modified=\"1\" type=\"string\"><![CDATA[{4}]]></TableName>" +   //$NON-NLS-1$
                                                        "<EnableCaseSensitiveIDs type=\"bool\"><![CDATA[0]]></EnableCaseSensitiveIDs>" + //$NON-NLS-1$
                                                        "<TruncateColumnNames collapsed=\"1\" type=\"bool\"><![CDATA[0]]></TruncateColumnNames>" + //$NON-NLS-1$
                                                        "<SQL>" +                                                                   //$NON-NLS-1$
                                                        "<DirectInsert type=\"bool\"><![CDATA[0]]></DirectInsert>" +                //$NON-NLS-1$
                                                        "<EnableRecordOrdering collapsed=\"1\" type=\"bool\"><![CDATA[0]]></EnableRecordOrdering>" + //$NON-NLS-1$
                                                        "<CheckDuplicateRows collapsed=\"1\" type=\"bool\"><![CDATA[0]]></CheckDuplicateRows>" +     //$NON-NLS-1$
                                                        "</SQL>" +                                                                  //$NON-NLS-1$
            /* param 5 = table action */                "<TableAction modified=\"1\" type=\"int\"><![CDATA[{5}]]><GenerateCreateStatement" + //$NON-NLS-1$
                                                        " type=\"bool\"><![CDATA[1]]><DistributionKey collapsed=\"1\"" +            //$NON-NLS-1$
                                                        " type=\"int\"><![CDATA[1]]></DistributionKey>" +                           //$NON-NLS-1$
                                                        "<FailOnError collapsed=\"1\" type=\"bool\"><![CDATA[1]]></FailOnError>" +  //$NON-NLS-1$
                                                        "</GenerateCreateStatement>" +                                              //$NON-NLS-1$
                                                        "</TableAction>" +                                                          //$NON-NLS-1$
                                                        "<Session>" +                                                               //$NON-NLS-1$
                                                        "<SchemaReconciliation>" +                                                  //$NON-NLS-1$
                                                        "<UnmatchedLinkColumnAction type=\"int\"><![CDATA[1]]></UnmatchedLinkColumnAction>" + //$NON-NLS-1$
                                                        "<TypeMismatchAction type=\"int\"><![CDATA[1]]></TypeMismatchAction>" +     //$NON-NLS-1$
                                                        "<UnmatchedTableColumnAction type=\"int\"><![CDATA[1]]></UnmatchedTableColumnAction>" + //$NON-NLS-1$
                                                        "<MismatchReportingAction type=\"int\"><![CDATA[2]]></MismatchReportingAction>" + //$NON-NLS-1$
                                                        "</SchemaReconciliation>" +                                                 //$NON-NLS-1$
                                                        "<TemporaryWorkTable type=\"int\"><![CDATA[0]]><DropTable" +                //$NON-NLS-1$
                                                        " type=\"bool\"><![CDATA[1]]></DropTable>" +                                //$NON-NLS-1$
                                                        "</TemporaryWorkTable>" +                                                   //$NON-NLS-1$
                                                        "<LoadOptions>" +                                                           //$NON-NLS-1$
                                                        "<ValidatePKs collapsed=\"1\" type=\"bool\"><![CDATA[0]]></ValidatePKs>" +  //$NON-NLS-1$
                                                        "<GenerateStatistics collapsed=\"1\" type=\"bool\"><![CDATA[0]]></GenerateStatistics>" + //$NON-NLS-1$
                                                        "<MaxRejectCount type=\"int\"><![CDATA[1]]></MaxRejectCount>" +             //$NON-NLS-1$
                                                        "</LoadOptions>" +                                                          //$NON-NLS-1$
                                                        "</Session>" +                                                              //$NON-NLS-1$
                                                        "<BeforeAfterSQL collapsed=\"1\" type=\"bool\"><![CDATA[0]]></BeforeAfterSQL>" + //$NON-NLS-1$
                                                        "</Usage></Properties>"; //$NON-NLS-1$
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private static final Map<String,String>  _DefaultStageParamsMap;
   private static final Map<String,String>  _DefaultLinkParamsMap;
   
   
   // -------------------------------------------------------------------------------------
   //                                 Static Block
   // -------------------------------------------------------------------------------------
   static
   {
      // ------------------------------------------------------
      //            Stage Parameters
      // ------------------------------------------------------
      _DefaultStageParamsMap = new HashMap<String,String>();

      _DefaultStageParamsMap.put("VariantName", "4.5");
      _DefaultStageParamsMap.put("VariantLibrary", "ccnz");
      _DefaultStageParamsMap.put("VariantVersion", "1.0");
      _DefaultStageParamsMap.put("SupportedVariants", "V1;4.5::ccnz");
      _DefaultStageParamsMap.put("SupportedVariantsLibraries", "ccnz");
      _DefaultStageParamsMap.put("SupportedVariantsVersions", "1.0");
      _DefaultStageParamsMap.put("Orientation", "link");
      _DefaultStageParamsMap.put("RejectFromLink", "-1");
      _DefaultStageParamsMap.put("RejectThreshold", "0");
      _DefaultStageParamsMap.put("RejectNumber", "0");
      _DefaultStageParamsMap.put("RejectUsesPercentage", "false");
      _DefaultStageParamsMap.put("ConnectorName", "NetezzaConnector");
      _DefaultStageParamsMap.put("Engine", "PX"); // "EE");
      _DefaultStageParamsMap.put("Context", "target");
      _DefaultStageParamsMap.put("ConnectionString", "/Connection/DataSource");
      _DefaultStageParamsMap.put("Username", "/Connection/Username");
      _DefaultStageParamsMap.put("Password", "/Connection/Password");
      _DefaultStageParamsMap.put("Database", "/Connection/Database");
      _DefaultStageParamsMap.put("UseSeparateConnectionForTWT", "/Connection/UseSeparateConnectionForTWT");
      _DefaultStageParamsMap.put("DatabaseTWT", "/Connection/UseSeparateConnectionForTWT/Database");
      _DefaultStageParamsMap.put("UsernameTWT", "/Connection/UseSeparateConnectionForTWT/Username");
      _DefaultStageParamsMap.put("PasswordTWT", "/Connection/UseSeparateConnectionForTWT/Password");
      _DefaultStageParamsMap.put("supportedTransactionModel", "local");

      // ------------------------------------------------------
      //            Link Parameters
      // ------------------------------------------------------
      _DefaultLinkParamsMap = new HashMap<String,String>();

      _DefaultLinkParamsMap.put("VariantName", "4.5");
      _DefaultLinkParamsMap.put("VariantLibrary", "ccnz");
      _DefaultLinkParamsMap.put("VariantVersion", "1.0");
      _DefaultLinkParamsMap.put("RejectFromLink", "-1");
      _DefaultLinkParamsMap.put("RejectThreshold", "0");
      _DefaultLinkParamsMap.put("RejectNumber", "0");
      _DefaultLinkParamsMap.put("RejectUsesPercentage", "false");
      _DefaultLinkParamsMap.put("ConnectorName", "NetezzaConnector");
   } // end of static


   static String copyright()
   { 
   	return com.ibm.is.sappack.gen.server.jobgenerator.stages.Copyright.IBM_COPYRIGHT_SHORT; 
   }   
   
   
   public static ObjectParamMap getDefaultStageParams()
   {
      return(new ObjectParamMap(_DefaultStageParamsMap, DataStageObjectFactory.STAGE_TYPE_DEFAULT));
   }

   
   protected static ObjectParamMap getLinkParams(NetezzaStageData parStageData)
   {
   	Map<String,String>  curLinkParamsMap = new HashMap<String,String>(_DefaultLinkParamsMap);
   	
      return(new ObjectParamMap(curLinkParamsMap, DataStageObjectFactory.LINK_TYPE_INPUT));
   }

   
   public static String getStageXMLProp(NetezzaStageData parNetezzaData, String parTableName, ServiceToken srvcToken)
   {
      StringBuffer newXMLPropsBuf = new StringBuffer();
      String       curDataSource;
      String       curUserId;
      String       curPassword;
      String       curDatabase;
      
      // we cannot cope with 'null' values ...
      // ==> if needed, convert them to empty strings
      curDataSource = convertNullToEmpty(parNetezzaData.getDataSource());
      curUserId     = convertNullToEmpty(parNetezzaData.getUser());
      curPassword   = convertNullToEmpty(parNetezzaData.getPassword());
		curPassword   = srvcToken.encrypt(curPassword);
      parTableName  = convertNullToEmpty(parTableName);
      curDatabase   = convertNullToEmpty(parNetezzaData.getDatabase());
      
      newXMLPropsBuf.append(MessageFormat.format(PROP_XML_PROP_TEMPLATE, new Object[] {curDataSource,
                                                                                       curDatabase,
                                                                                       curUserId, 
                                                                                       curPassword,
                                                                                       parTableName,
                                                                                       String.valueOf(parNetezzaData.getTableAction()) })) ;

      return(newXMLPropsBuf.toString());
   } // end of getStageXMLProp()
   
} // end of class NetezzaStageProperties 
