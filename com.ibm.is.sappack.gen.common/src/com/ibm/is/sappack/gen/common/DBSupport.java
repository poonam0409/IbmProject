//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2013                                              
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

import java.io.File;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.XMLUtils;


public final class DBSupport {
	
	static String copyright()	{ 
	   return com.ibm.is.sappack.gen.common.Copyright.IBM_COPYRIGHT_SHORT; 
	}
	
   // -------------------------------------------------------------------------------------
   //                            S u b   c l a s s e s
   // -------------------------------------------------------------------------------------
	public static class DBInstance {
	   
	   // -------------------------------------------------------------------------------------
	   //                                       Constants
	   // -------------------------------------------------------------------------------------
	   
	   // -------------------------------------------------------------------------------------
	   //                    X P A T H   E x p r e s s i o n   S t r i n g s
	   // -------------------------------------------------------------------------------------
	   private   static final String XPATH_STRING_DBM_ANNOTATION_MODEL_PURPOSE    = "/xmi:XMI/{0}[1]/eAnnotations/details[@key=''" + Constants.ANNOT_MODEL_PURPOSE + "'']/@value"; //$NON-NLS-1$ //$NON-NLS-2$
      private   static final String XPATH_STRING_DBM_ANNOTATION_MODEL_VERSION    = "/xmi:XMI/{0}[1]/eAnnotations/details[@key=''" + Constants.ANNOT_GENERATED_MODEL_VERSION + "'']/@value"; //$NON-NLS-1$ //$NON-NLS-2$
	   private   static final String XPATH_STRING_DBM_ALL_ANNOTATIONS_IDOCTYPE    = "/xmi:XMI/{0}/eAnnotations/details[@key=''" + Constants.ANNOT_IDOC_TYPE + "'']/@value"; //$NON-NLS-1$ //$NON-NLS-2$
	   private   static final String XPATH_STRING_DBM_ANNOTATION_MESSAGETYPES     = "/xmi:XMI/{0}[1]/eAnnotations/details[@key=''" + Constants.ANNOT_SAP_MESSAGE_TYPES + "'']/@value";    //$NON-NLS-1$ //$NON-NLS-2$
      private   static final String XPATH_LIST_DBM_ANNOTATION_DATA_OBJECT_SOURCE_IDOC = "/xmi:XMI/{0}[1]/eAnnotations/details[@key=''" + Constants.ANNOT_DATA_OBJECT_SOURCE + "'' and @value=''" + Constants.DATA_OBJECT_SOURCE_TYPE_IDOC + "'']"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      private   static final String XPATH_LIST_DBM_ANNOTATION_DATA_OBJECT_SOURCE_LOGICALTABLE = "/xmi:XMI/{0}/eAnnotations/details[@key=''" + Constants.ANNOT_DATA_OBJECT_SOURCE + "'' and @value=''" + Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE + "'']"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      private   static final String XPATH_LIST_DBM_ANNOTATION_DATA_OBJECT_SOURCE_JOINEDCHECKTABLE = "/xmi:XMI/{0}/eAnnotations/details[@key=''" + Constants.ANNOT_DATA_OBJECT_SOURCE + "'' and @value=''" + Constants.DATA_OBJECT_SOURCE_TYPE_JOINED_CHECK_AND_TEXT_TABLE + "'']"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      private   static final String XPATH_LIST_DBM_ANNOTATION_DATA_OBJECT_SOURCE_REFERENCE_CHECK_TABLE = "/xmi:XMI/{0}/eAnnotations/details[@key=''" + Constants.ANNOT_DATA_OBJECT_SOURCE + "'' and @value=''" + Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_CHECK_TABLE + "'']"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      private   static final String XPATH_LIST_DBM_ANNOTATION_DATA_OBJECT_SOURCE_NON_REFERENCE_CHECK_TABLE = "/xmi:XMI/{0}/eAnnotations/details[@key=''" + Constants.ANNOT_DATA_OBJECT_SOURCE + "'' and @value=''" + Constants.DATA_OBJECT_SOURCE_TYPE_NON_REFERENCE_CHECK_TABLE + "'']"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      private   static final String XPATH_LIST_DBM_ANNOTATION_DATA_OBJECT_SOURCE_TRANSLATIONTABLE = "/xmi:XMI/{0}/eAnnotations/details[@key=''" + Constants.ANNOT_DATA_OBJECT_SOURCE + "'' and @value=''" + Constants.DATA_OBJECT_SOURCE_TYPE_TRANSLATION_TABLE + "'']"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      private   static final String XPATH_LIST_DBM_ANNOTATION_ABAP_CPIC_PROG_NAME                 = "/xmi:XMI/{0}/eAnnotations/details[@key=''" + Constants.ANNOT_ABAP_PROGRAM_NAME_CPIC + "'']"; //$NON-NLS-1$ //$NON-NLS-2$
      private   static final String XPATH_LIST_DBM_ANNOTATION_SEG_TYPE_IS_CONTROL_RECORD = "/xmi:XMI/{0}[1]/eAnnotations/details[@key=''" + Constants.ANNOT_SEGMENT_TYPE + "'' and @value=''CONTROL_RECORD'']"; //$NON-NLS-1$ //$NON-NLS-2$ 
      private   static final String XPATH_LIST_DBM_ANNOTATION_V7_TABLES          = "/xmi:XMI/{0}/eAnnotations/details[@key=''" + Constants.ANNOT_PACKS_V7_MODE + "'' and @value=''" + Boolean.toString(true) + "'']"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      private	static final String XPATH_LIST_DBM_ANNOTATION_CHECK_TBL_DATA_OBJECT_SOURCE = "/xmi:XMI/{0}[1]/eAnnotations/details[@key=''" + Constants.ANNOT_SAPPACK_CHECK_TBL_DATA_OBJECT_SOURCE + "'']/@value"; //$NON-NLS-1$ //$NON-NLS-2$
      
      private   static final String XPATH_STRING_DBM_LDM_ID     = "/xmi:XMI/{0}[1]/eAnnotations/details[@key=''" + "SAPPACK_SAP_LDM_ID" + "'']/@value";    
      
	   // -------------------------------------------------------------------------------------
	   //                                 Member Variables
	   // -------------------------------------------------------------------------------------
	   private DataBaseType _DBType;
	   private Document    _XMLDocument;
	   private String      _SchemaName;


      private DBInstance(File pModelFile) throws JobGeneratorException {
         
         Document modelDoc = null;

         try {
            DocumentBuilderFactory domfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = domfactory.newDocumentBuilder();
            modelDoc = builder.parse(pModelFile);
         }
         catch(Exception pExcpt) {
            throw new JobGeneratorException("105400E", new String[] { pExcpt.toString() } );  //$NON-NLS-1$
         }
         init(modelDoc);
      } // end of DBInstance()


      private DBInstance(Document pModelDoc) throws JobGeneratorException {
         init(pModelDoc);
      } // end of DBInstance()


      public boolean containsAbapCPIC() {
         return(containsNodes(XPATH_LIST_DBM_ANNOTATION_ABAP_CPIC_PROG_NAME));
      } // end of containsAbapCPIC()


      public boolean containsIDocs() {
         return(containsNodes(XPATH_LIST_DBM_ANNOTATION_DATA_OBJECT_SOURCE_IDOC));
      } // end of containsIDocs()

      public boolean containsControlRecord() {
    	  return containsNodes(XPATH_LIST_DBM_ANNOTATION_SEG_TYPE_IS_CONTROL_RECORD);
      }

      private boolean containsJoinedChecktables() {
         return(containsNodes(XPATH_LIST_DBM_ANNOTATION_DATA_OBJECT_SOURCE_JOINEDCHECKTABLE));
      } // end of containsJoinedChecktables()

      public boolean containsReferenceChecktables() {
          return(containsNodes(XPATH_LIST_DBM_ANNOTATION_DATA_OBJECT_SOURCE_REFERENCE_CHECK_TABLE));
       } // end of containsJoinedChecktables()

      public boolean containsNonReferenceChecktables() {
          return(containsNodes(XPATH_LIST_DBM_ANNOTATION_DATA_OBJECT_SOURCE_NON_REFERENCE_CHECK_TABLE));
       } // end of containsJoinedChecktables()

      public boolean containsLogicalTables() {
         return(containsNodes(XPATH_LIST_DBM_ANNOTATION_DATA_OBJECT_SOURCE_LOGICALTABLE));
      } // end of containsLogicalTables()


      public boolean containsTranslationTables() {
         return(containsNodes(XPATH_LIST_DBM_ANNOTATION_DATA_OBJECT_SOURCE_TRANSLATIONTABLE));
      } // end of containsTranslationTables()

      public boolean containsCheckTableTypesForTranslationTables() {
    	  return(containsNodes(XPATH_LIST_DBM_ANNOTATION_CHECK_TBL_DATA_OBJECT_SOURCE));
      }
      
      private boolean containsNodes(String key) {
         List    nodeList;
         boolean containsNodes;

         nodeList = (List) evaluateXPathExpression(key, XMLUtils.XPATH_RESULT_TYPE_LIST);
         
         if (nodeList == null || nodeList.size() == 0) {
            containsNodes = false; 
         }
         else {
            containsNodes = true; 
         }

         return(containsNodes);
      } // end of containsNodes()
      

      public boolean containsV7Tables() {
         return(containsNodes(XPATH_LIST_DBM_ANNOTATION_V7_TABLES));
      } // end of containsV7Tables()


      private Object evaluateXPathExpression(String pXPathExprTemplate, int pXPathResultType) {
         String resolvedXPath;
         Object retObject;

         // get XPATH expression to evaluate
         switch(_DBType) {
            case Netezza:
                 resolvedXPath = MessageFormat.format(pXPathExprTemplate, new Object[] { DBNAME_TYPE_NETEZZA } );
                 break;
           
            case Oracle:
                 resolvedXPath = MessageFormat.format(pXPathExprTemplate, new Object[] { DBNAME_TYPE_ORACLE } );
                 break;
         
            case DB2:
            default:
                 resolvedXPath = MessageFormat.format(pXPathExprTemplate, new Object[] { DBNAME_TYPE_DB2 } );
         } // end of switch(_DBId) 

         retObject = XMLUtils.evaluate(_XMLDocument, resolvedXPath, pXPathResultType);         

         return(retObject);
      } // end of evaluateXPathExpression()


      public DataBaseType getDatabaseType() {
         return(_DBType);
      } // end of getDatabaseType()
      

      public String[] getIDocTypes() {
         List     idocTypeNodeList;
         Set      resultSet;
         Iterator listIter;

         // get all IDOC types ('detail') nodes 
         idocTypeNodeList = (List) evaluateXPathExpression(XPATH_STRING_DBM_ALL_ANNOTATIONS_IDOCTYPE, 
                                                           XMLUtils.XPATH_RESULT_TYPE_LIST); 

         resultSet = new HashSet();
         listIter = idocTypeNodeList.iterator();
         while(listIter.hasNext()) {
            String s = ((Node) listIter.next()).getNodeValue();
            resultSet.add(s);
         }

         return((String[]) resultSet.toArray(new String[0]));
      } // end of getIDocTypes()


      public String getMessageTypeAnnotation() {
         return((String) evaluateXPathExpression(XPATH_STRING_DBM_ANNOTATION_MESSAGETYPES, 
                                                 XMLUtils.XPATH_RESULT_TYPE_STRING));
      } // end of getMessageTypeAnnotation

      public String getLDMID() {
          return((String) evaluateXPathExpression(XPATH_STRING_DBM_LDM_ID, 
                  XMLUtils.XPATH_RESULT_TYPE_STRING));    	  
      }

      public String getModelPurposeAnnotation() {
         return((String) evaluateXPathExpression(XPATH_STRING_DBM_ANNOTATION_MODEL_PURPOSE, 
                                                 XMLUtils.XPATH_RESULT_TYPE_STRING));
      } // end of getModelPurposeAnnotation()


      public String getModelVersionAnnotation() {
         return((String) evaluateXPathExpression(XPATH_STRING_DBM_ANNOTATION_MODEL_VERSION, 
                                                 XMLUtils.XPATH_RESULT_TYPE_STRING));
      } // end of getModelVersionAnnotation()


      public String getSchemaName() {
         return(_SchemaName);
      } // end of getSchemaName()


      private void init(Document pModelDoc) throws JobGeneratorException {
         // get DB id ...
         _DBType = DBSupport.getDatabaseType(pModelDoc);

         // ... the DB Schema name ...
         _SchemaName = DBSupport.getDBSchemaName(pModelDoc, _DBType);

         // and save passed XML document
         _XMLDocument = pModelDoc;
      } // end of init()
      
	} // end of class DBInstance
	


   
	// -------------------------------------------------------------------------------------
	//                 D a t a b a s e   d e p e n d e n t   I d s
	// -------------------------------------------------------------------------------------
   private  static final String  XPATH_SCHEMA_NAMES_DEFINITION_TEMPLATE = "xmi:XMI/{0}";                   //$NON-NLS-1$
   private  static final String  MODEL_DATABASE_NODE_NAME               = "Database";                      //$NON-NLS-1$
   private  static final String  MODEL_ATTRIB_NAME_VENDOR               = "vendor";                        //$NON-NLS-1$
   public   static final String  MODEL_VENDOR_DB2                       = "DB2";                           //$NON-NLS-1$
   public   static final String  MODEL_VENDOR_INFORMIX                  = "Informix";                      //$NON-NLS-1$
   public   static final String  MODEL_VENDOR_NETEZZA                   = "Netezza";                       //$NON-NLS-1$
   public   static final String  MODEL_VENDOR_ORACLE                    = "Oracle";                        //$NON-NLS-1$
   public   static final String  MODEL_VENDOR_SQL_SERVERR               = "SQL Server";                    //$NON-NLS-1$

   public enum DataBaseType { Unknown, DB2, Oracle, Netezza };
   
   public   static final String  DBNAME_TYPE_DB2                        = "LUW:LUWTable";                  //$NON-NLS-1$
   public   static final String  DBSCHEMA_TYPE_DB2                      = "DB2Model:DB2Schema";            //$NON-NLS-1$
   public   static final String  DBNAME_TYPE_ORACLE                     = "OracleModel:OracleTable";       //$NON-NLS-1$
   public   static final String  DBSCHEMA_TYPE_ORACLE                   = "SQLSchema:Schema";              //$NON-NLS-1$
   public   static final String  DBNAME_TYPE_NETEZZA                    = "NetezzaModel:NetezzaTable";    //$NON-NLS-1$
   public   static final String  DBSCHEMA_TYPE_NETEZZA                  = "NetezzaModel:NetezzaSchema";   //$NON-NLS-1$

   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   
   
   
   /**
    * This support method creates a DBInstance object.
    * 
    * @param pModelFile  DB model file
    * 
    * @return DBInstance object
    * 
    * @throws JobGeneratorException if DB model id could not be recognized
    */
   public static DBInstance createDBInstance(File pModelFile)
           throws JobGeneratorException
   {
      return(new DBInstance(pModelFile));
   } // end of createDBInstance()
   
   
   /**
    * This support method creates a DBInstance object.
    * 
    * @param pModelDoc  DB model document
    * 
    * @return DBInstance object
    * 
    * @throws JobGeneratorException if DB model id could not be recognized
    */
   public static DBInstance createDBInstance(Document pModelDoc)
           throws JobGeneratorException
   {
      return(new DBInstance(pModelDoc));
   } // end of createDBInstance()
   
   
   /**
    * This support method checks if the passed DB is supported.
    * 
    * @param pDBId  database Id
    * 
    * @throws IllegalArgumentException if the DB Id is not known
    */
   public static void checkDatabaseType(DataBaseType pDBType)
   {
      // check passed DB id if it's supported
      switch(pDBType)
      {
         case DB2:
         case Oracle:
         case Netezza:
              break;
              
         default:
            throw new IllegalArgumentException();
      } // end of switch(pDBId)
      
   } // end of checkDatabaseId()


   /**
    * This support method determines the Id of the passed 
    * database model document and returns it.
    * 
    * @param pModelDoc  DB model document
    * 
    * @return Database Id (for example ID_DB2, ID_ORACLE, ID_NETEZZA, ...)
    * 
    * @throws JobGeneratorException if DB model id could not be recognized
    */
   public static DataBaseType getDatabaseType(Document pModelDoc)
          throws JobGeneratorException
   {
      NamedNodeMap vAttribList;
      NodeList     vChildNodeList;
      String       vVendorName;
      DataBaseType retDBType;
      int          idxChild;
      int          idxAttrib;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }
      
      // get the root's children ...
      vChildNodeList = pModelDoc.getDocumentElement().getChildNodes();
      
      // search for the '%var%DataBase' node name
      idxChild    = 0;
      retDBType   = DataBaseType.Unknown;
      vVendorName = null;
      while(idxChild < vChildNodeList.getLength() && vVendorName == null)     
      {
         Node child = vChildNodeList.item(idxChild);
         
         // search for the node that ends with MODEL_DATABASE_NODE_NAME
         if (child.getNodeName().endsWith(MODEL_DATABASE_NODE_NAME))
         {
            // found ==> now search for the 
            // get the root's attributes ...
            vAttribList = child.getAttributes();
            idxAttrib  = 0;
            while(idxAttrib < vAttribList.getLength() && vVendorName == null)     
            {
               Node attrib = vAttribList.item(idxAttrib);
               
               if (attrib.getNodeName().equalsIgnoreCase(MODEL_ATTRIB_NAME_VENDOR))
               {
                  vVendorName = attrib.getNodeValue();
               }
               
               idxAttrib ++;
            } // end of while(idxAttrib < vAttribList.getLength() && vVendorName == null)
         } // end of if (child.getNodeName().endsWith(MODEL_DATABASE_NODE_NAME))
         
         idxChild ++;
      } // end of while(idx < vChildNodeList.getLength() && vVendorName == null)

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Vendor = " + vVendorName); //$NON-NLS-1$
      }

      if (vVendorName == null)
      {
         throw new JobGeneratorException("105200E", Constants.NO_PARAMS);  //$NON-NLS-1$
      }
      else
      {
         if (vVendorName.startsWith(MODEL_VENDOR_DB2)) 
         {
            retDBType = DataBaseType.DB2;
         }
         else
         {
            if (vVendorName.equals(MODEL_VENDOR_ORACLE)) 
            {
               retDBType = DataBaseType.Oracle;
            }
            else
            {
               if (vVendorName.equals(MODEL_VENDOR_NETEZZA)) 
               {
                  retDBType = DataBaseType.Netezza;
               }
               else
               {
                  throw new JobGeneratorException("105800E", new String[] { vVendorName });  //$NON-NLS-1$
               }
            } // end of (else) if (vVendorName.equals(MODEL_VENDOR_ORACLE))
         } // end of (else) if (modelName.equals(DB_MODEL_VALUE_DB2))
      }
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("DB Type = " + retDBType.toString()); //$NON-NLS-1$
      }
      
      return(retDBType);
   } // end of getDBType()
   
   
   /**
    * This support method returns the schema name of the DB model document.
    * 
    * @param pModelDoc  model document
    * @param pDBId      Database Id (for example ID_DB2, ID_ORACLE, ID_NETEZZA, ...)
    * 
    * @return DB Schema name
    * 
    * @throws IllegalArgumentException if 'pModelDoc' is null 
    */
   public static String getDBSchemaName(Document pModelDoc, DataBaseType pDBType) 
          throws IllegalArgumentException
   {
      List    vSchemaList;
      Node    curSchemaNode;
      String  vSchemaName;
      String  vXPATHSchemaDefinition;

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }
      
      // passed model document must exist
      if (pModelDoc == null)
      {
         throw new IllegalArgumentException("No (model) Document."); //$NON-NLS-1$
      } // end of if (pModelDoc == null)

      // determine some XPATH schema definition ...
      switch(pDBType)
      {
         case Netezza:
              vXPATHSchemaDefinition = MessageFormat.format(XPATH_SCHEMA_NAMES_DEFINITION_TEMPLATE, 
                                                            new Object[] { DBSCHEMA_TYPE_NETEZZA } );
              break;
              
         case Oracle:
              vXPATHSchemaDefinition = MessageFormat.format(XPATH_SCHEMA_NAMES_DEFINITION_TEMPLATE, 
                                                            new Object[] { DBSCHEMA_TYPE_ORACLE } );
              break;
            
         default:
              vXPATHSchemaDefinition = MessageFormat.format(XPATH_SCHEMA_NAMES_DEFINITION_TEMPLATE, 
                                                            new Object[] { DBSCHEMA_TYPE_DB2 } );
      }

      vSchemaName = null;
      vSchemaList = XMLUtils.getChildNodeList(pModelDoc, vXPATHSchemaDefinition);
      if (vSchemaList.size() > 0)
      {
         curSchemaNode = (Node) vSchemaList.get(0);
         vSchemaName   = curSchemaNode.getAttributes().getNamedItem("name").getNodeValue(); //$NON-NLS-1$
      } // end of if (vSchemaList.size() > 0)
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("Schema name = " + vSchemaName); //$NON-NLS-1$
      }

      return(vSchemaName);
   } // end of getDBSchemaName()
   
} // end of class DBSupport
