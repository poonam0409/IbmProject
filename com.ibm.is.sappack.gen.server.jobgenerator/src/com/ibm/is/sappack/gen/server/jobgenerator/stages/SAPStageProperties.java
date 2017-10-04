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


import java.util.Iterator;
import java.util.List;
import java.util.Map;

import DataStageX.DSMetaBag;
import DataStageX.DSStage;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.common.util.XMLUtils;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.util.ObjectParamMap;
import com.ibm.is.sappack.gen.server.util.StageData;
import com.ibm.is.sappack.gen.server.util.ObjectParamMap.Value;


public abstract class SAPStageProperties
{
   // -------------------------------------------------------------------------------------
   //                                           Constants
   // -------------------------------------------------------------------------------------
	// osuhre, RTC 113840: Leave single quotes for parameters since the basic routine
	//                     collecting all IDoc jobs depends on it.
   private static final String XML_PARAM_STRING_TEMPLATE          = "<{0} type='string'><![CDATA[{1}]]></{0}>";
   private static final String XML_PARAM_PROT_STRING_TEMPLATE     = "<{0} type=\"protectedstring\"><![CDATA[{1}]]></{0}>";
   private static final String XML_PARAM_STAGE_CLASSPATH_TEMPLATE = "<ConnectorClasspath type=\"string\"><![CDATA[$(DSHOME)/../DSComponents/bin/{0};$(DSHOME)/../DSComponents/bin/sapjco3.jar]]></ConnectorClasspath>";
   private static final String XML_PARAM_STAGE_CONNECTOR_OPTIONS  = "<ConnectorOtherOptions type=\"string\"><![CDATA[-Djava.ext.dirs=$(DSHOME)/../DSComponents/bin;$(DSHOME)/../../ASBNode/apps/jre/lib/ext]]></ConnectorOtherOptions>";
   private static final String JAVA_JAR_IDOC_EXTRACT              = "ccidocextractstage.jar";
   private static final String JAVA_JAR_IDOC_LOAD                 = "ccidocloadstage.jar";
   
   

   // ************************ common SAP STAGE Property Keys ******************************
   public static final String STAGE_TYPE_CLASS_NAME = StageProperties.STAGE_TYPE_CLASS_NAME_CUSTOM_STAGE;

   public static final String PROP_CONN_SAP_CONNECTION_NAME_KEY  = "CONNECTIONNAME";
   public static final String PROP_CONN_SAP_CONNECTION_PARAM_KEY = "DSSAPCONNECTIONPARAMETER";
   public static final String PROP_XML_PROPERTIES_KEY            = "XMLProperties";
   public static final String PROP_SAP_USE_DEFAULT_LOGON         = "USEDEFAULTSAPLOGON";
   public static final String PROP_USE_OFFLINE_PROCESSING        = "USEOFFLINEPROCESSING";
   public static final String CHAR_SET_KEY                       = "charset";
   public static final String PROP_SAP_SYS_USERNAME_KEY          = "SAPUSERID";
   public static final String PROP_SAP_SYS_USERNAME_KEY_2        = "USERNAME";
   public static final String PROP_SAP_SYS_PASSWORD_KEY          = "SAPPASSWORD";
   public static final String PROP_SAP_SYS_PASSWORD_KEY_2        = "PASSWORD";
   public static final String PROP_SAP_SYS_CLIENT_NUMBER_KEY     = "SAPCLIENTNUMBER";
   public static final String PROP_SAP_SYS_CLIENT_NUMBER_KEY_2   = "CLIENT";
   public static final String PROP_SAP_SYS_LANGUAGE_KEY          = "SAPLANGUAGE";
   public static final String PROP_SAP_SYS_LANGUAGE_KEY_2        = "LANGUAGE";

   
   static String copyright()
   {
      return com.ibm.is.sappack.gen.server.jobgenerator.stages.Copyright.IBM_COPYRIGHT_SHORT;
   }


   public static ObjectParamMap getXMLPropertyFromLinkMap(ObjectParamMap propertyMap)
   {
      return (getXMLPropertyFromMap(propertyMap, true));
   } // end of getXMLPropertyFromLinkMap()


   public static ObjectParamMap getXMLPropertyFromIDocExtractStageMap(ObjectParamMap propertyMap)
   {
      ObjectParamMap.Value xmlValue;
      ObjectParamMap       resultMap = getXMLPropertyFromMap(propertyMap, false);
      String               xmlString;
      
      xmlValue  = resultMap.get(PROP_XML_PROPERTIES_KEY);
      xmlString = StringUtils.replaceMessageArguments(xmlValue.getValue(), new Object[] { JAVA_JAR_IDOC_EXTRACT } );
      resultMap.put(PROP_XML_PROPERTIES_KEY, xmlString);
      
      return(resultMap);
   } // end of getXMLPropertyFromIDocExtractStageMap()

   
   public static ObjectParamMap getXMLPropertyFromIDocLoadStageMap(ObjectParamMap propertyMap)
   {
      ObjectParamMap.Value xmlValue;
      ObjectParamMap       resultMap = getXMLPropertyFromMap(propertyMap, false);
      String               xmlString;
      
      xmlValue  = resultMap.get(PROP_XML_PROPERTIES_KEY);
      xmlString = StringUtils.replaceMessageArguments(xmlValue.getValue(), new Object[] { JAVA_JAR_IDOC_LOAD } );
      resultMap.put(PROP_XML_PROPERTIES_KEY, xmlString);
      
      return(resultMap);
   } // end of getXMLPropertyFromIDocLoadStageMap()

   
   private static ObjectParamMap getXMLPropertyFromMap(ObjectParamMap propertyMap,
                                                       boolean isLinkMap)
   {
      ObjectParamMap       retParamMap;
      ObjectParamMap.Value paramValue;
      Iterator             mapIter;
      Map.Entry            mapEntry;
      String               paramName;
      String               templateName;
      String               xmlString;
      StringBuffer         xmlBuf;
      int                  paramUsage;

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("no of params: " + propertyMap.size());
      }

      // first get the map usage from 1st element ...
      if (propertyMap.size() > 0) {
         mapEntry = (Map.Entry) propertyMap.iterator().next();
         paramValue = (Value) mapEntry.getValue();
         paramUsage = paramValue.getUsageType();
      } else {
         paramUsage = DataStageObjectFactory.OBJECT_TYPE_DEFAULT;
      }

      xmlBuf = new StringBuffer();
      xmlBuf.append(XMLUtils.XML_HEADER_UTF16);
      xmlBuf.append("<Properties version=\"1.1\">");
      if (isLinkMap) {
         xmlBuf.append("<Usage>");
      } else {
         xmlBuf.append("<Connection>");
      }

      retParamMap = new ObjectParamMap(paramUsage);
      mapIter = propertyMap.iterator();
      while (mapIter.hasNext()) {
         mapEntry   = (Map.Entry) mapIter.next();
         paramName  = (String) mapEntry.getKey();
         paramValue = (Value) mapEntry.getValue();

         // do not encrypt Job Parameters !!!!!!!!
         if ((paramName.equals(PROP_SAP_SYS_PASSWORD_KEY) || 
              paramName.equals(PROP_SAP_SYS_PASSWORD_KEY_2))  &&
              !StringUtils.isJobParamVariable(paramValue.getValue())) {
            templateName = XML_PARAM_PROT_STRING_TEMPLATE;
         } else {
            templateName = XML_PARAM_STRING_TEMPLATE;
         }

         xmlString = StringUtils.replaceMessageArguments(templateName, new Object[] { paramName, 
                                                                       paramValue.getValue() });
         xmlBuf.append(xmlString);
      } // end of while(mapIter.hasNext())

      if (isLinkMap) {
         xmlBuf.append("</Usage>");
      } else {
         xmlBuf.append("</Connection>");
         
         // add Java classpath and additional connection options  
         xmlBuf.append("<Usage>");
         xmlBuf.append("<Java>");
         xmlBuf.append(XML_PARAM_STAGE_CLASSPATH_TEMPLATE);
         xmlBuf.append(XML_PARAM_STAGE_CONNECTOR_OPTIONS);
         xmlBuf.append("</Java>");
         xmlBuf.append("</Usage>");
      }
      xmlBuf.append("</Properties>");

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "XML = " + xmlBuf.toString());
      }

      retParamMap.put(PROP_XML_PROPERTIES_KEY, xmlBuf.toString());

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit();
      }

      return (retParamMap);
   } // end of getXMLPropertyFromMap()


   public static void setPartitioningStageInfoHashKey(List<StageData.MetaBagData> metaBagList)
   {
      // --> create a MetaBag for the SAP stage
      metaBagList.add(new StageData.MetaBagData(DataStageObjectFactory.METABAG_OWNER_NAME, 
                                                 "RTColumnProp", "0"));
      metaBagList.add(new StageData.MetaBagData(DataStageObjectFactory.METABAG_OWNER_NAME, 
                                                "Part/Col", "hash -key "+ Constants.IDOC_TECH_FLD_ADM_DOCNUM_NAME + " -cs"));
      metaBagList.add(new StageData.MetaBagData(DataStageObjectFactory.METABAG_OWNER_NAME, "SortAdv", "-nonStable"));
      metaBagList.add(new StageData.MetaBagData(DataStageObjectFactory.METABAG_OWNER_NAME, "SeqSort", "0"));
      metaBagList.add(new StageData.MetaBagData(DataStageObjectFactory.METABAG_OWNER_NAME, "_PartColSortColumns",
                                                Constants.IDOC_TECH_FLD_ADM_DOCNUM_NAME));

   } // end of setPartitioningStageInfoHashKey()


   public static void setSequentialExecutionMode(DSStage parDSStage, DataStageObjectFactory parDSFactory)
   {
      DSMetaBag metaBag;

      // set MetaBag for 'Serial Execution'
      metaBag = parDSFactory.createDSMetaBag();
      metaBag.setOf_DSStage(parDSStage);
      metaBag.setOwners(DataStageObjectFactory.METABAG_OWNER_NAME);
      metaBag.setNames(DataStageObjectFactory.METABAG_TF_EXECMODE_NAME);
      metaBag.setValues(DataStageObjectFactory.METABAG_TF_EXECMODE_VALUES_SEQ);
      parDSStage.setOf_DSMetaBag(metaBag);

   } // end of setSequentialExecutionMode()

} // end of class SAPStageProperties
