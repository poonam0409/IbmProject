//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.server.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.util;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.DBSupport;
import com.ibm.is.sappack.gen.common.DBSupport.DataBaseType;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.StopWatch;
import com.ibm.is.sappack.gen.common.request.SupportedTableTypesMap;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.common.util.XMLUtils;


public class TableData implements Comparable<TableData> 
{
   // -------------------------------------------------------------------------------------
   //                                       Subclasses
   // -------------------------------------------------------------------------------------
   
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   private static final String  EMPTY_STRING_ARR[]               = new String[0];
   public  static final String  KEY_COLUMN_SEPARATOR             = ",";                                   //$NON-NLS-1$
   
   private static final String  XPATH_STAGE_VAR_DEFINITION       = "StageVariable";                       //$NON-NLS-1$
   private static final String  XPATH_TABLE_DEFINITION_TEMPLATE  = "xmi:XMI/{0}";                         //$NON-NLS-1$
   private static final String  XPATH_COLUMNS_NULLABLES_DEFINITION_TEMPLATE = "xmi:XMI/{0}/columns[1]/@nullable"; //$NON-NLS-1$
   
   private static final String  XPATH_COLUMNS_DEFINITION         = "columns";                             //$NON-NLS-1$
   private static final String  XPATH_CONTAINED_TYPE_DEFINITION  = "containedType";                       //$NON-NLS-1$
   private static final String  XPATH_CONSTRAINTS_DEFINITION     = "constraints";                         //$NON-NLS-1$
   public  static final String  XPATH_EANNOTATIONS_DEFINITION    = "eAnnotations/details";                //$NON-NLS-1$
   public  static final String  TABLE_NAME_TEMPLATE              = "%TBL_NAME%";                          //$NON-NLS-1$
   public  static final String  MSG_TYPE_TEMPLATE                = "%MSG_TYPE%";                          //$NON-NLS-1$
   public  static final String  OBJECT_NAME_TEMPLATE             = "%OBJECTNAME%";                        //$NON-NLS-1$
   public  static final String  PK_CONSTRAINT_SUFFIX             = "PrimaryKey";
   public  static final String  FK_CONSTRAINT_SUFFIX             = "ForeignKey";

   public  static final int     COLUMN_POSITION_BEGIN            = 0;
   public  static final int     COLUMN_POSITION_END              = -2;

   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private String     _ABAPCodeRFC;
   private String     _ABAPCodeCPIC;
   private String     _ABAPProgramNameRFC;
   private String     _ABAPProgramNameCPIC;
   private ColumnData _ColumnArr[];
   private boolean    _ColumnNullableDefault;
   private Integer    _TableType;
   private String     _DBSchema;
   private String     _LogicalName;
   private String     _FilterConstraint;
   private String     _ModelVersion;
   private String     _ForeignKeyColArr[];
   private String     _IDocType;
   private String     _IDocBasicType;
   private boolean    _IsExtendedIDocType;
   private boolean    _IsMandatory;
   private boolean    _IsRootSegment;
   private boolean    _IsUnicodeSystem;
   private String     _Name;
   private String     _ParentSegment;
   private String     _PhysicalName;
   private String     _PrimaryKeyColArr[];
   private String     _SAPSystemHost;
   private String     _SegmentType;
   private String     _SegmentDefinition;
   private String     _SQLStatement;
   private Map        _StageVariableMap;
   private String     _SAPTableName;
   private String	    _CheckTableType;
   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.server.util.Copyright.IBM_COPYRIGHT_SHORT; 
   }   
   
   
   private TableData()
   {
   } // end of TableData()
   
   
   public TableData(Node pTableNode, boolean pColumnNullableDefault)  
   {
      String keyColName;
      
      _ForeignKeyColArr      = EMPTY_STRING_ARR;
      _PrimaryKeyColArr      = EMPTY_STRING_ARR;
      _TableType             = null;
      _IsMandatory           = false;
      _IsRootSegment         = false;
      _IsUnicodeSystem       = false;
      _ColumnNullableDefault = pColumnNullableDefault;
      _ModelVersion          = null;
      _LogicalName           = null;
      _PhysicalName          = null;
      
      if (TraceLogger.isTraceEnabled())
      {
//         TraceLogger.entry();
      }
      
      // save table name
      _Name = pTableNode.getAttributes().getNamedItem("name").getNodeValue();

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "table name = " + _Name);
      }

      // set the required table annotations ...
      setAnnotations(pTableNode);

      // read the columns and set the PKs and FKs ...
      readColumns(pTableNode);

      // for v6.5x set foreign key column variables
      // control records don't have foreign key
      if (_SegmentType != null && !_SegmentType.equals("CONTROL_RECORD")) 
      {
      	if (_IsRootSegment && _ForeignKeyColArr.length == 0) 
      	{
      		keyColName = MessageFormat.format(Constants.IDOC_FOREIGN_KEY_ROOT_TEMPLATE, 
      													 new Object[] {_IDocType});
      		_ForeignKeyColArr    = new String[1];
      		_ForeignKeyColArr[0] = StringUtils.cleanFieldName(keyColName);

            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
               						"No FK exists on root segment --> create artifical FK '" + _ForeignKeyColArr[0] + "' (for v6.5x)");
            }
      	} // end of if (_IsRootSegment)
      }

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "PK(s) = " + getPrimaryKeyColumns() + " - FK(s) = " + getForeignKeyColumns());
//         TraceLogger.exit();
      }
   } // end of TableData()
   

   public ColumnData addColumn(String parName, int parLength, int parColumnPos)
   {
      return(addColumn(parName, parLength, false, null, parColumnPos));
   } // end of addColumn()

   
   /**
    * This method is used to add a new column to the table.
    * <br>
    * If the new column is to be added at the beginning or end of the table,
    * please use the appropriate constant for 'columnPos':
    * <ul>
    * <li>COLUMN_POSITION_BEGIN</li>
    * <li>COLUMN_POSITION_END</li>
    * </ul>
    * <p>
    * <b>Note: </b>If 'columnPos' is less then 0 or 'columnPos' exceeds the current column 
    * count, the new columns is added at <b>1st</b> position (less than 0) or
    * at <b>the end</b> (column count is exceeded). 
    *
    * @param parName       column name
    * @param parLength     column length
    * @param isPrimaryKey  true if it's a primary key column otherwise false
    * @param parKeyName    name of the (foreign) key name (can be null)
    * @param parColumnPos  position where the new column is added
    * 
    * @return ColumnData
    * 
    * @throws IllegalArgumentException if a column exist with the specified name
    */
   public ColumnData addColumn(String parName, int parLength, boolean isPrimaryKey, 
                               String parKeyName, int parColumnPos)
   {
      ColumnData newCol;
      ColumnData newArr[];
      int        insertionPos;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("Column name = " + parName + " - len = " + parLength 
                         + " - isPrimaryKey = " + isPrimaryKey + " - keyName = " + parKeyName 
                         + " - columnPos = " + parColumnPos);
      }
      
      // first of all check if there already is a column having the specified name
      if (searchColumn(parName) > -1)
      {
         throw new IllegalArgumentException("Column '" + parName + "' already exists.");
      }
      
      // create the column ...
      newCol = new ColumnData(this, parName, parLength);
      
      // set primary and foreign key column variables
      if (isPrimaryKey)
      {
         newCol.setIsKeyColumn(true);
         
         // ==> PRIMARY KEY
         if (parKeyName != null)
         {
            _PrimaryKeyColArr    = new String[1];
            _PrimaryKeyColArr[0] = parKeyName;
         } // end of if (parKeyName == null)
      }
      else
      {
         // ==> FOREIGN KEY
         if (parKeyName != null)
         {
            _ForeignKeyColArr    = new String[1];
            _ForeignKeyColArr[0] = parKeyName;
         } // end of if (parKeyName == null)
      } // end of (else) if (isPrimaryKey)
      
      // add new column to column list
      newArr = new ColumnData[_ColumnArr.length + 1];
      
      // determine the position where the new columns is to be inserted
      switch(parColumnPos)
      {
         case COLUMN_POSITION_BEGIN:
              insertionPos = 0;
              break;
            
         case COLUMN_POSITION_END:
              insertionPos = _ColumnArr.length;
              break;
  
         default:
              if (parColumnPos < 0)
              {
                 // position is negative ==> set insertion position to BEGIN
                 insertionPos = 0;
                 
                 if (TraceLogger.isTraceEnabled())
                 {
                    TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Insertion position is negative (" 
                                                              + parColumnPos + "): 1st position is assumed.");
                 }
              }
              else
              {
                 if (parColumnPos > _ColumnArr.length)
                 {
                    // position is negative ==> set insertion position to BEGIN
                    insertionPos = _ColumnArr.length;
                    
                    if (TraceLogger.isTraceEnabled())
                    {
                       TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Insertion position is too big (" 
                                                                 + parColumnPos + "): Last position is assumed.");
                    }
                 }
                 else
                 {
                    insertionPos = parColumnPos;
                 } // end of (else) if (parColumnPos > _ColumnArr.length)
              } // end of (else) if (parColumnPos < 0)
      } // end of switch(parColumnPos)

      // copy the existing columns ... 
      if (insertionPos == 0)
      {
         // ==> at START
         System.arraycopy(_ColumnArr, 0, newArr, 1, _ColumnArr.length);
         insertionPos = 0;
      }
      else
      {
         if (insertionPos == _ColumnArr.length)
         {
            // ==> at END
            System.arraycopy(_ColumnArr, 0, newArr, 0, _ColumnArr.length);
         }
         else
         {
            // ==> anywhere between START and END
            System.arraycopy(_ColumnArr, 0, newArr, 0, insertionPos);
            System.arraycopy(_ColumnArr, insertionPos, newArr, insertionPos + 1, 
                             (_ColumnArr.length - insertionPos));
         } // end of (else) if (insertionPos == _ColumnArr.length)
      } // end of (else) if (insertionPos == 0)
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "insertion position: " + insertionPos);
      }
      
      // new column array is to be the active one 
      newArr[insertionPos] = newCol;
      _ColumnArr           = newArr;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("New column: " + newCol);
      }
      
      return(newCol);
   } // end of addColumn()


	public int compareTo(TableData compTableData) {
		return(getName().compareToIgnoreCase(compTableData.getName()));
	} // end of compareTo()


   private void copyMemberVars(TableData parSource, TableData parTarget)
   {
      // copy all member variables ...
      parTarget._ABAPCodeRFC           = parSource._ABAPCodeRFC;
      parTarget._ABAPCodeCPIC          = parSource._ABAPCodeCPIC;
      parTarget._ABAPProgramNameRFC    = parSource._ABAPProgramNameRFC;
      parTarget._ABAPProgramNameCPIC   = parSource._ABAPProgramNameCPIC;
      parTarget._TableType             = parSource._TableType;
      parTarget._DBSchema              = parSource._DBSchema;
      parTarget._FilterConstraint      = parSource._FilterConstraint;
      parTarget._ForeignKeyColArr      = new String[parSource._ForeignKeyColArr.length];
      System.arraycopy(parSource._ForeignKeyColArr, 0, parTarget._ForeignKeyColArr, 0, 
                       parSource._ForeignKeyColArr.length);
      parTarget._IDocType              = parSource._IDocType;
      parTarget._IDocBasicType         = parSource._IDocBasicType;
      parTarget._IsExtendedIDocType    = parSource._IsExtendedIDocType;
      parTarget._IsMandatory           = parSource._IsMandatory;
      parTarget._IsRootSegment         = parSource._IsRootSegment;
      parTarget._IsUnicodeSystem       = parSource._IsUnicodeSystem;
      parTarget._LogicalName           = parSource._LogicalName;
      parTarget._Name                  = parSource._Name;
      parTarget._ParentSegment         = parSource._ParentSegment;
      parTarget._PhysicalName          = parSource._PhysicalName;
      parTarget._PrimaryKeyColArr      = new String[parSource._PrimaryKeyColArr.length];
      System.arraycopy(parSource._PrimaryKeyColArr, 0, parTarget._PrimaryKeyColArr, 0, 
                       parSource._PrimaryKeyColArr.length);
      parTarget._SAPSystemHost         = parSource._SAPSystemHost;
      parTarget._SegmentType           = parSource._SegmentType;
      parTarget._SegmentDefinition     = parSource._SegmentDefinition;
      parTarget._SQLStatement          = parSource._SQLStatement;
      parTarget._StageVariableMap      = parSource._StageVariableMap;
      parTarget._ColumnNullableDefault = parSource._ColumnNullableDefault;
      
   } // end of copyMemberVars()

   
   public static SourceColMapping createSourceColumnMapping(TableData table)
   {
      ColumnData       colArr[];
      StageVariable    stageVar;
      FlowVarData      flowVarData;
      SourceColMapping retColMapping;
      String           mappingColumnName;
      int              colIdx;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("table = " + table);
      }
      
      retColMapping = new SourceColMapping();
      colArr        = table.getColumnData();
      
      // set default mapping for all columns
      for (colIdx = 0; colIdx < colArr.length; colIdx ++)
      {
         flowVarData       = colArr[colIdx];
         mappingColumnName = colArr[colIdx].getTransformerSrcMapping();
         
         // check if a column mapping is for a stage variable
         if (mappingColumnName == null)
         {
            // apply default mapping ==> use column name
            colArr[colIdx].setTransformerSrcMapping(colArr[colIdx].getName());
         }
         else
         {
            stageVar = (StageVariable) table.getStageVariables().get(mappingColumnName); 
            if (stageVar != null)
            {
               // ==> stage variable found ==> use this for the mapping
               flowVarData = stageVar;
            }
         } // end of (else) if (mappingColumnName == null)
         
         retColMapping.setMapping(colArr[colIdx].getName(), flowVarData);
      } // end of for (colIdx = 0; colIdx < colArr.length; colIdx ++)
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("col map size = " + retColMapping.getSize());
      }
      
      return(retColMapping);
   } // end of createSourceColumnMapping()
   
   
   
   private Map createStageVarMap(String stageVarsXML)
   {
      StageVariable          newStageVar;
      List                   stageVarList;
      Node                   curVarNode;
      Node                   tmpNodeAttribute;
      Element                rootElement;
      String                 varName;
      String                 varType;
      String                 varDerivation;
      String                 varDescription;
      Integer                varLen;
      Map                    retStageVarMap;
      int                    vNodeIdx;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("xml len = " + stageVarsXML.length());
      }
      
      retStageVarMap = new HashMap();
      try
      {
         // build and parse the XML Document ...
         rootElement = XMLUtils.getRootElementFromXML(stageVarsXML);
         
         // process each Stage Variable
         stageVarList = XMLUtils.getChildNodeList(rootElement, XPATH_STAGE_VAR_DEFINITION);
         for (vNodeIdx = 0; vNodeIdx < stageVarList.size(); vNodeIdx ++) 
         {
            // get variable name, description, type, length, derivation
            curVarNode     = (Node) stageVarList.get(vNodeIdx);
            
            varDerivation  = null;
            varDescription = null;
            varLen         = null;
            varName        = curVarNode.getAttributes().getNamedItem("name").getNodeValue();
            varType        = curVarNode.getAttributes().getNamedItem("type").getNodeValue();
            
            // get column length (if available)
            tmpNodeAttribute = curVarNode.getAttributes().getNamedItem("length");
            if (tmpNodeAttribute != null)
            {
               varLen = Integer.valueOf(tmpNodeAttribute.getNodeValue());
            }
            tmpNodeAttribute = curVarNode.getAttributes().getNamedItem("derivation");
            if (tmpNodeAttribute != null)
            {
               varDerivation = tmpNodeAttribute.getNodeValue();
            }
            tmpNodeAttribute = curVarNode.getAttributes().getNamedItem("description");
            if (tmpNodeAttribute != null)
            {
               varDescription = tmpNodeAttribute.getNodeValue();
            }
            
            // create a Stage Variable instance
            newStageVar = new StageVariable(varName, varType, varDescription, 
                                            varLen,  varDerivation);
            retStageVarMap.put(varName, newStageVar);
         } // end of for (vNodeIdx = 0; vNodeIdx < stageVarList.getLength(); vNodeIdx ++)
      } // end of try
      catch(Exception pExcpt)
      {
/*         
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.traceException(pExcpt);
         }
*/
         pExcpt.printStackTrace();
      } // end of catch(XPathException pXPATHExcpt)
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("Stage Variables count = " + retStageVarMap.size());
      }

      return(retStageVarMap);
   } // end of createStageVarMap()
   
   
   public String getABAPCodeRFC()
   {
      return(_ABAPCodeRFC);
   } // end of getABAPCode()


   public String getABAPCodeCPIC()
   {
      return(_ABAPCodeCPIC);
   } // end of getABAPCode()

   
   static String getArrayMapAsTraceString(Map pMapToTrace)
   {
      TableData    tblArr[];
      StringBuffer trcBuf;
      Iterator     mapIter;
      Map.Entry    mapEntry;
      Integer      suppTableTypeId;
      int          idx;
      
      trcBuf  = new StringBuffer();
      mapIter = pMapToTrace.entrySet().iterator();
      while(mapIter.hasNext())
      {
         mapEntry = (Map.Entry) mapIter.next();
         suppTableTypeId = (Integer) mapEntry.getKey(); 
         trcBuf.append(SupportedTableTypesMap.getType(suppTableTypeId.intValue()));
         trcBuf.append("=[");
         tblArr = (TableData[]) mapEntry.getValue();
         for(idx = 0; idx < tblArr.length; idx ++)
         {
            if (idx > 0)
            {
               trcBuf.append(',');
            }
            trcBuf.append(tblArr[idx]);
         }
         trcBuf.append("]");
         
         if (mapIter.hasNext())
         {
            trcBuf.append(' ');
         }
      } // end of while(mapIter.hasNext())
      
      return(trcBuf.toString());
   } // end of getArrayMapAsTraceString()


   public String getRFCABAPProgramName()
   {
      return(_ABAPProgramNameRFC);
   } // end of getABAPProgramName()
   

   public String getCPICABAPProgramName()
   {
      return(_ABAPProgramNameCPIC);
   } 
   
   
   public ColumnData getColumn(String pColName) 
   {
      ColumnData retCol;
      int        colIdx;
      
      retCol = null;
      if (pColName != null) {
         colIdx = searchColumn(pColName);
         if (colIdx > -1) {
            retCol = _ColumnArr[colIdx];
         }
      }
         
      return(retCol);
   } // end of getColumnData()

   
   public ColumnData[] getColumnData() 
   {
      return(_ColumnArr);
   } // end of getColumnData()

   
   private static boolean getColumnNullableDefault(Document pModelDoc, DataBaseType pDBType)
   {
      String    vBoolAsString;
      String    vXPathNullableDefintion;
      boolean   tmpBoolean;
      boolean   isNullableDefault;
      
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }
      
      // determine some XPATH Columns Nullable Definition ...
      switch(pDBType)
      {
         case Netezza:
              vXPathNullableDefintion = MessageFormat.format(XPATH_COLUMNS_NULLABLES_DEFINITION_TEMPLATE, 
                                                             new Object[] { DBSupport.DBNAME_TYPE_NETEZZA } );
              break;
              
         case Oracle:
              vXPathNullableDefintion = MessageFormat.format(XPATH_COLUMNS_NULLABLES_DEFINITION_TEMPLATE, 
                                                             new Object[] { DBSupport.DBNAME_TYPE_ORACLE } );
              break;
            
         default:
              vXPathNullableDefintion = MessageFormat.format(XPATH_COLUMNS_NULLABLES_DEFINITION_TEMPLATE, 
                                                             new Object[] { DBSupport.DBNAME_TYPE_DB2 } );
      }

      
      isNullableDefault = Constants.DEFAULT_COLUMN_NULLABLE;
      vBoolAsString     = (String) XMLUtils.evaluate(pModelDoc, vXPathNullableDefintion, 
                                                     XMLUtils.XPATH_RESULT_TYPE_STRING);
      
      if (vBoolAsString.length() > 0) {
         vBoolAsString = vBoolAsString.toLowerCase();
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "nullable value = " + vBoolAsString);
         }
         tmpBoolean = Boolean.valueOf(vBoolAsString).booleanValue();

         // default value is the inverted found value that was found
         isNullableDefault = !tmpBoolean;
      } // end of if (vBoolAsString.length() > 0)

      /*
      try
      {
         vNullableList = (NodeList) EXPRESSION_NULLABLE_DEFINITION_NODES.evaluate(pModelDoc, XPathConstants.NODESET);
         
         if (vNullableList.getLength() > 0)
         {
            vNullableNode = vNullableList.item(0);                       // only the first occurrence is important
            vBoolAsString = vNullableNode.getNodeValue().toLowerCase();
            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "node value = " + vBoolAsString);
            }
            
            tmpBoolean = Boolean.valueOf(vBoolAsString).booleanValue();

            // default value is the inverted found value that was found
            isNullableDefault = !tmpBoolean;
         } // end of if (vNullableList.getLength() > 0)
      } // end of try
      catch(XPathExpressionException pXPathExcpt)
      {
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.traceException(pXPathExcpt);
         }
      }
      */
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("Default column nullable = " + isNullableDefault);
      }
      
      return(isNullableDefault);
   } // end of getColumnNullableDefault()
   
   
   public TableData getCopyWithKeyCols(String newTableName)
   {
      TableData retTableData;
      
      retTableData = getCopyWithKeyCols();
      
      if (newTableName != null)
      {
         retTableData._Name = newTableName;
      }
      
      return(retTableData);
   } // end of getCopy()
   
   
   
   public TableData getCopyWithKeyCols()
   {
      TableData retTableData;
      List      keyColumnList;
      Iterator  listIter;  
      int       colIdx;

      retTableData = new TableData();

      // determine the number of key columns ...
      keyColumnList = new ArrayList();
      for(colIdx = 0; colIdx < _ColumnArr.length; colIdx ++)
      {
         if (_ColumnArr[colIdx].isKeyColumn())
         {
            keyColumnList.add(_ColumnArr[colIdx]);
         }
      } // end of for(colIdx = 0; colIdx < _ColumnArr.length; colIdx ++)

      // create a new column array containing the key columns only ...
      retTableData._ColumnArr = new ColumnData[keyColumnList.size()];
      
      listIter = keyColumnList.iterator();
      colIdx   = 0;
      while(listIter.hasNext())
      {
         retTableData._ColumnArr[colIdx] = (ColumnData) listIter.next();
         colIdx ++;
      } // end of while(listIter.hasNext())
      
      // copy all member variables ...
      copyMemberVars(this, retTableData);
      
      return(retTableData);
   } // end of getCopyWithKeyCols()


   public TableData getCopy(String newTableName)
   {
      TableData retTableData;
      
      retTableData = getCopy();
      
      if (newTableName != null)
      {
         retTableData._Name = newTableName;
      }
      
      return(retTableData);
   } // end of getCopy()
   
   
   public TableData getCopy()
   {
      TableData retTableData;
      int       arrIdx;
      
      retTableData = new TableData();
      
      // first copy the columns ...
      retTableData._ColumnArr             = new ColumnData[_ColumnArr.length];
      for(arrIdx = 0; arrIdx < _ColumnArr.length; arrIdx ++)
      {
         retTableData._ColumnArr[arrIdx] = (ColumnData) _ColumnArr[arrIdx].clone();
      }
      
      // and the copy all member variables ...
      copyMemberVars(this, retTableData);
      
      return(retTableData);
   } // end of getCopy()

   
   public TableData getCopyForSAPMapping()
   {
      TableData  retTableData;
      ColumnData colArr[];
      String     newColName;
      int        arrIdx;
      
      // get a copy of the current table with different table name ...
      retTableData = getCopy(getDescriptiveTableName());
      
      // and then swap two columns attributes: NAME <---> TRANSFORMER SRC MAPPING
      colArr = retTableData._ColumnArr;
      for(arrIdx = 0; arrIdx < colArr.length; arrIdx ++)
      {
         // note: we 'misuse' the original name field here
         newColName = colArr[arrIdx].getTransformerSrcMapping();
         colArr[arrIdx].setTransformerSrcMapping(colArr[arrIdx].getName());
         colArr[arrIdx].setName(newColName);
      } // end of for(arrIdx = 0; arrIdx < colArr.length; arrIdx ++)

      return(retTableData);
   } // end of getCopy()
   
   
   public String getDBSchema()
   {
      return(_DBSchema);
   } // end of getDBSchema()

   
   /**
    * This method returns the name of the logical table similar to the one ion the logical mode
    * but unshortened. It is neither the SAP table name nor the physical table name. Will be used
    * in places where a descriptive table name is needed, e.g. for stage names.
    */
   public String getDescriptiveTableName()
   {
      String retLogicalName;
      
      if (_LogicalName == null)
      {
         retLogicalName = _Name;
      }
      else
      {
         retLogicalName = _LogicalName;
      }

      return(retLogicalName);
   } // end of getLogicalName()
   

   public String getFilterConstraint() 
   {
      return(_FilterConstraint);
   }   

   
   public int getForeignKeyColumnCount()
   {
      return(_ForeignKeyColArr.length);
   } // end of getForeignKeyColumnCount()

   
   public String getForeignKeyColumns()
   {
      return(getKeyColumns(_ForeignKeyColArr));
   } // end of getForeignKeyColumns()
   
   
   public String getSAPTableName() {
	   return _SAPTableName;
   }

   public String getIDocBasicType()
   {
      if (_IDocBasicType == null) {
         // if it's not an extended type ==> same as IDoc type 
         if (!_IsExtendedIDocType) {
            _IDocBasicType = _IDocType;
         }
      }
      
      return(_IDocBasicType);
   } // end of getIDocBasicType()
   
   
   public String getIDocType()
   {
      return(_IDocType);
   } // end of getIDocType()
   
   
   public boolean getIsExtendedIDocType() 
   {
	   return(_IsExtendedIDocType);
   }
   
   public String getModelVersion() 
   {
      return(_ModelVersion);
   }   

   
   public String getName()
   {
      return(_Name);
   }
   
   /**
    * getCheckTableType
    * 
    * return the check table type
    * of a translation table. Valid types
    * are 'ReferenceCheckTable' and
    * 'NonReferenceCheckTable'
    * 
    * @return checkTableType
    */
   public String getCheckTableType() {
	   return (_CheckTableType);
   }
   
   private String getKeyColumns(String pColArr[])
   {
      StringBuffer vResultStringBuf;
      String       actColName;
      int          vIdx;
      
      vResultStringBuf = new StringBuffer();
      for(vIdx = 0; vIdx < pColArr.length; vIdx ++)
      {
         if (vIdx > 0)
         {
            vResultStringBuf.append(KEY_COLUMN_SEPARATOR);
         } // end of if (vIdx > 0)
      
         // get the actual column name
//         actColName = getSrcMappingForDerivation(pColArr[vIdx], this);
         actColName = pColArr[vIdx];

         vResultStringBuf.append(actColName);
      } // end of for(vIdx = 0; vIdx < pColArr.length; vIdx ++)

      return(vResultStringBuf.toString());
   } // end of getKeyColumns()


   public String getParentSegment()
   {
      return(_ParentSegment);
   } // end of getParentSegment()


   public String getPhysicalName()
   {
      String retPhysicalName;

      if (_PhysicalName == null)
      {
         retPhysicalName = _Name;
      }
      else
      {
         retPhysicalName = _PhysicalName;
      }

      return(retPhysicalName);
   } // end of getPhysicalName()


   public String getPrimaryKeyColumns()
   {
      return(getKeyColumns(_PrimaryKeyColArr));
   } // end of getPrimaryKeyColumns()


   public int getPrimaryKeyColumnCount()
   {
      return(_PrimaryKeyColArr.length);
   } // end of getPrimaryKeyColumnCount()


   public String getSAPSSystemHost()
   {
      return(_SAPSystemHost);
   } // end of getSAPSSystemHost()


   public String getSegmentDefinition()
   {
      return(_SegmentDefinition);
   } // end of getSegmentDefinition()


   public String getSegmentType()
   {
      return(_SegmentType);
   } // end of getSegmentType()


   public Map getStageVariables()
   {
      return(_StageVariableMap);
   } // end of getStageVariables()


   public String getSQLStatement() 
   {
		return(_SQLStatement);
	}   

   public static String getSrcMappingForDerivation(String parDerivationExpr, TableData parMapping) {
      ColumnData colArr[];
      String     retDerivSrc;
      int        colIdx;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
                           "Derivation expr = " +  parDerivationExpr + " - mapping = " + parMapping);
      }

      retDerivSrc = null;
      if (parMapping != null)
      {
         colArr     = parMapping.getColumnData();
         colIdx     = 0;
         while (colIdx < colArr.length && retDerivSrc == null) {
            
            // check if the derivation is the one we're looking for ...
            if (colArr[colIdx].getDerivation().equals(parDerivationExpr)) {
               // yes --> get the columns transformer mapping
               retDerivSrc = colArr[colIdx].getTransformerSrcMapping();
            }
            colIdx ++;
         }
      } // end of if (parMapping != null)
      
      // if not found: input = output
      if (retDerivSrc == null) {
         retDerivSrc = parDerivationExpr;
      }
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Derivation source = " +  retDerivSrc);
      }

      return(retDerivSrc);
   } // end of getSrcMappingForDerivation()
   
   
   public Integer getTableType()
   {
      return(_TableType);
   } // end of getTableType()
   
   
   public boolean isMandatory()
   {
      return(_IsMandatory);
   } // end of isMandatory()
   
   
   public boolean isRootSegment()
   {
      return(_IsRootSegment);
   } // end of isRootSegment()
   
   
   public boolean isUnicodeSystem()
   {
      return(_IsUnicodeSystem);
   } // end of isUnicodeSystem()
   
   
   private static ModelInfoBlock loadDataModel(Document pModelDoc) 
           throws IllegalArgumentException,
                  JobGeneratorException
   {
      TableData                 curTable;
      TableData                 tmpTableDataArr[];
      ModelInfoBlock            modelInfoBlk;
      Node                      curTableNode;
      String                    vSchemaName;
      String                    vXPATHTableDefinition;
      Map<Integer, TableData[]> vTableTypeMap;
      List<TableData>           vIDocAllTableList;
      List<TableData>           vIDocExtractTableList;
      List<TableData>           vIDocLoadTableList;
      List<TableData>           vLogicalTableList;
      List<TableData>           vReferenceCheckTableList;
      List<TableData>           vJLTTableList;
      List<TableData>           vTextTableList;
      List<TableData>           vNonReferenceCheckTableList;
      List<TableData>           vInternalList;
      List<TableData>           vTranslationTableList;
      List<TableData>           vDomainTranslationTableList;
      List<Node>                vTablesList;
      Integer                   vTableTypeInt;
      int                       vTableListIdx;
      DataBaseType              vDBType;
      boolean                   vColumnNullableDefault;

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }
      
      // passed model document must exist
      if (pModelDoc == null)
      {
         throw new IllegalArgumentException("No (model) Document.");
      } // end of if (pModelDoc == null)
      
      // first of all determine the DB (manufacturer) id
      vDBType = DBSupport.getDatabaseType(pModelDoc);
      
      // determine some XPATH definitions ...
      switch(vDBType)
      {
         case Netezza:
              vXPATHTableDefinition = MessageFormat.format(XPATH_TABLE_DEFINITION_TEMPLATE, 
                                                           new Object[] { DBSupport.DBNAME_TYPE_NETEZZA } );
              break;
              
         case Oracle:
              vXPATHTableDefinition = MessageFormat.format(XPATH_TABLE_DEFINITION_TEMPLATE, 
                                                           new Object[] { DBSupport.DBNAME_TYPE_ORACLE } );
              break;
            
         default:
              vXPATHTableDefinition = MessageFormat.format(XPATH_TABLE_DEFINITION_TEMPLATE, 
                                                           new Object[] { DBSupport.DBNAME_TYPE_DB2 } );
      }
      // and get the DB Schema name
      vSchemaName = DBSupport.getDBSchemaName(pModelDoc, vDBType);
      
      // create empty map and array lists
      vTableTypeMap               = new HashMap<Integer, TableData[]>();
      vJLTTableList               = new ArrayList<TableData>();
      vReferenceCheckTableList    = new ArrayList<TableData>();
      vTextTableList              = new ArrayList<TableData>();
      vIDocExtractTableList       = new ArrayList<TableData>();
      vIDocAllTableList           = new ArrayList<TableData>();
      vIDocLoadTableList          = new ArrayList<TableData>();
      vLogicalTableList           = new ArrayList<TableData>();
      vInternalList               = new ArrayList<TableData>();
      vNonReferenceCheckTableList = new ArrayList<TableData>();
      vTranslationTableList		 = new ArrayList<TableData>();
      vDomainTranslationTableList = new ArrayList<TableData>();
      
      // determine the column's 'nullable' attribute default value
      vColumnNullableDefault = getColumnNullableDefault(pModelDoc, vDBType);
      
      
      vTablesList = XMLUtils.getChildNodeList(pModelDoc, vXPATHTableDefinition);
      for (vTableListIdx = 0; vTableListIdx < vTablesList.size(); vTableListIdx ++) 
      {
         curTableNode = vTablesList.get(vTableListIdx);

         // create the a new TableData instance
         curTable = new TableData(curTableNode, vColumnNullableDefault);
         curTable.setDBSchema(vSchemaName);

         // check model version
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
                              "model version = " + curTable.getModelVersion());
         }

         // skip unknown tables
         if (curTable.getModelVersion() == null) {
            
            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.trace(TraceLogger.LEVEL_FINE, 
                                 "Missing version: Table '" +  curTable + "' ignored.");
            }
            
        	   continue;
         }
         
         // check if current table model version is supported by the server
         StringUtils.checkModelVersion(curTable.getModelVersion(), Constants.MODEL_VERSION);

         vTableTypeInt = curTable.getTableType();
         if (vTableTypeInt == null)
         {
            // unknown Data Object Source Type (not set)
            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.trace(TraceLogger.LEVEL_FINE, 
               	               "Unknown Data Object Source: Table type for table '" + curTable + "' has not been set since it is unknown.");
            }
         }
         else
         {
            switch(vTableTypeInt.intValue())
            {
               case SupportedTableTypesMap.TABLE_TYPE_INT_IDOC_TYPE_ALL:
                    vIDocAllTableList.add(curTable);
                    break;
               
               case SupportedTableTypesMap.TABLE_TYPE_INT_IDOC_EXTRACT_TYPE:
                    vIDocExtractTableList.add(curTable);
                    break;
                    
               case SupportedTableTypesMap.TABLE_TYPE_INT_IDOC_LOAD_TYPE:
                    vIDocLoadTableList.add(curTable);
                    break;
                    
               case SupportedTableTypesMap.TABLE_TYPE_INT_LOGICAL_TABLE:
                    vLogicalTableList.add(curTable);
                    break;
                    
               case SupportedTableTypesMap.TABLE_TYPE_INT_JOINED_CHECK_AND_TEXT_TABLE:
                    vJLTTableList.add(curTable);
                    break;
                    
               case SupportedTableTypesMap.TABLE_TYPE_INT_IDOC_LOAD_STATUS:
                    vInternalList.add(curTable);
                    break;
                    
               case SupportedTableTypesMap.TABLE_TYPE_INT_REFERENCE_CHECK_TABLE:
               	  vReferenceCheckTableList.add(curTable);
               	  break;
               	 
               case SupportedTableTypesMap.TABLE_TYPE_INT_NON_REFERENCE_CHECK_TABLE:
               	  vNonReferenceCheckTableList.add(curTable);
              	     break;

               case SupportedTableTypesMap.TABLE_TYPE_INT_REFERENCE_TEXT_TABLE:
               	  vTextTableList.add(curTable);
              	     break;

               case SupportedTableTypesMap.TABLE_TYPE_INT_TRANSLATION_TABLE:
                    vTranslationTableList.add(curTable);
                    break;
               	
               case SupportedTableTypesMap.TABLE_TYPE_INT_DOMAIN_TRANSLATION_TABLE:
                    vDomainTranslationTableList.add(curTable);
                    break;
               	 
               default:
                    // unknown Data Object Source Type
                    if (TraceLogger.isTraceEnabled())
                    {
                       TraceLogger.trace(TraceLogger.LEVEL_FINE, 
                                         "Unknown Data Object Source: " + curTable.getTableType().intValue());
                    }
            } // end of switch(vTableTypeInt.intValue())
         } // end of if (vTableTypeInt == null)
      } // end of for (vTableListIdx = 0; vTableListIdx < vTablesList.size(); vTableListIdx ++)

      // convert grouped table list to TableData array and put them into the map
      tmpTableDataArr = (TableData[]) vLogicalTableList.toArray(new TableData[vLogicalTableList.size()]);
		java.util.Arrays.sort(tmpTableDataArr);
      vTableTypeMap.put(SupportedTableTypesMap.TABLE_TYPE_ENUM_LOGICAL_TABLE, tmpTableDataArr);
      
      tmpTableDataArr = (TableData[]) vJLTTableList.toArray(new TableData[0]);
		java.util.Arrays.sort(tmpTableDataArr);
      vTableTypeMap.put(SupportedTableTypesMap.TABLE_TYPE_ENUM_JOINED_CHECK_AND_TEXT_TABLE, tmpTableDataArr);
      
      tmpTableDataArr = (TableData[]) vReferenceCheckTableList.toArray(new TableData[0]);
		java.util.Arrays.sort(tmpTableDataArr);
      vTableTypeMap.put(SupportedTableTypesMap.TABLE_TYPE_ENUM_REFERENCE_CHECK_TABLE, tmpTableDataArr);

      tmpTableDataArr = (TableData[]) vNonReferenceCheckTableList.toArray(new TableData[0]);
		java.util.Arrays.sort(tmpTableDataArr);
      vTableTypeMap.put(SupportedTableTypesMap.TABLE_TYPE_ENUM_NON_REFERENCE_CHECK_TABLE, tmpTableDataArr);

      tmpTableDataArr = (TableData[]) vTextTableList.toArray(new TableData[0]);
		java.util.Arrays.sort(tmpTableDataArr);
      vTableTypeMap.put(SupportedTableTypesMap.TABLE_TYPE_ENUM_REFERENCE_TEXT_TABLE, tmpTableDataArr);
      
      tmpTableDataArr = (TableData[]) vIDocExtractTableList.toArray(new TableData[vIDocExtractTableList.size()]);
      vTableTypeMap.put(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_EXTRACT, tmpTableDataArr);
      
      tmpTableDataArr = (TableData[]) vIDocLoadTableList.toArray(new TableData[vIDocLoadTableList.size()]);
      vTableTypeMap.put(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_LOAD, tmpTableDataArr);
      
      tmpTableDataArr = (TableData[]) vInternalList.toArray(new TableData[vInternalList.size()]);
      vTableTypeMap.put(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_LOAD_STATUS, tmpTableDataArr);
      
      tmpTableDataArr = (TableData[]) vIDocAllTableList.toArray(new TableData[vIDocAllTableList.size()]);
      vTableTypeMap.put(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_ALL, tmpTableDataArr);
      
      tmpTableDataArr = (TableData[]) vTranslationTableList.toArray(new TableData[vTranslationTableList.size()]);
		java.util.Arrays.sort(tmpTableDataArr);
      vTableTypeMap.put(SupportedTableTypesMap.TABLE_TYPE_ENUM_TRANSLATION_TABLE, tmpTableDataArr);
      
      tmpTableDataArr = (TableData[]) vDomainTranslationTableList.toArray(new TableData[vDomainTranslationTableList.size()]);
		java.util.Arrays.sort(tmpTableDataArr);
      vTableTypeMap.put(SupportedTableTypesMap.TABLE_TYPE_ENUM_DOMAIN_TRANSLATION_TABLE, tmpTableDataArr);
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINER, "Reference Check tables: " + vReferenceCheckTableList.size());
         TraceLogger.trace(TraceLogger.LEVEL_FINER, "Non-reference Check tables: " + vNonReferenceCheckTableList.size());
         TraceLogger.trace(TraceLogger.LEVEL_FINER, "Logical tables: " + vLogicalTableList.size());
         TraceLogger.trace(TraceLogger.LEVEL_FINER, "IDoc Extract tables: " + vIDocExtractTableList.size());
         TraceLogger.trace(TraceLogger.LEVEL_FINER, "IDoc Load tables: " + vIDocLoadTableList.size());
         TraceLogger.trace(TraceLogger.LEVEL_FINER, "IDoc All tables: " + vIDocAllTableList.size());
         TraceLogger.trace(TraceLogger.LEVEL_FINER, "Internal tables: " + vInternalList.size());
      }
      
      modelInfoBlk = new ModelInfoBlock(vTableTypeMap, vDBType);
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit(modelInfoBlk.toString());
      }
 
      return(modelInfoBlk);
   } // end of loadDataModel()


   public static ModelInfoBlock loadDataModel(String dataModel)
          throws JobGeneratorException
   {
      StopWatch            localStopWatch;
      ModelInfoBlock       vRetInfoBlk;
      ByteArrayInputStream vInputStream;

      if (TraceLogger.isTraceEnabled())
      {
         if (dataModel == null)
         {
            TraceLogger.entry("No model data was passed.");
         }
         else
         {
            TraceLogger.entry();
         }
      }

      vRetInfoBlk = null;
      if (dataModel != null)
      {
         try
         {
            localStopWatch = new StopWatch(true);
            
            // create a ByteArrayInputStream from the Physical DataModel string ...
            vInputStream = new ByteArrayInputStream(dataModel.getBytes(Constants.STRING_ENCODING));

            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "model len = " + dataModel.length());
            }

            // load the model ...
            vRetInfoBlk = loadDataModel(vInputStream);
            vInputStream.close();
            localStopWatch.stop();
            
            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "model load time = " + localStopWatch.toString());
            }
         } // end of try
         catch(IOException pIOExcpt)
         {
            pIOExcpt.printStackTrace();

            throw new JobGeneratorException("102800E", Constants.NO_PARAMS, pIOExcpt);
         } // end of catch(IOException pIOExcpt)
      } // end of if (dataModel != null)

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }

      return(vRetInfoBlk);
   } // end of loadDataModel()


   public static ModelInfoBlock loadDataModel(InputStream pDataModelStream)
          throws JobGeneratorException
   {
      ModelInfoBlock         vRetInfoBlk;
      DocumentBuilderFactory vDocBuilderFactory;
      DocumentBuilder        vDocBuilder;
      Document               vModelDocument = null;;

      if (TraceLogger.isTraceEnabled())
      {
         if (pDataModelStream == null)
         {
            TraceLogger.entry("No data model stream was passed.");
         }
         else
         {
            TraceLogger.entry();
         }
      }

      vRetInfoBlk = null;
      if (pDataModelStream != null)
      {
         try
         {
            // and parse it 
            vDocBuilderFactory = DocumentBuilderFactory.newInstance();
            vDocBuilder        = vDocBuilderFactory.newDocumentBuilder();
            vModelDocument     = vDocBuilder.parse(pDataModelStream);

            // create the tables from XML document ...
            vRetInfoBlk = loadDataModel(vModelDocument);
         } // end of try
         catch(IOException pIOExcpt)
         {
            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.traceException(pIOExcpt);
            }

            throw new JobGeneratorException("104900E", Constants.NO_PARAMS, pIOExcpt);
         } // end of catch(IOException pIOExcpt)
         catch(SAXException pSAXExcpt)
         {
            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.traceException(pSAXExcpt);
            }

            throw new JobGeneratorException("105000E", Constants.NO_PARAMS, pSAXExcpt);
         } // end of catch(SAXException pSAXExcpt)
         catch(ParserConfigurationException pParserExcpt)
         {
            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.traceException(pParserExcpt);
            }

            throw new JobGeneratorException("105000E", Constants.NO_PARAMS, pParserExcpt);
         } // end of catch(ParserConfigurationException pParserExcpt)
      } // end of if (pDataModelStream != null)

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
      
      return(vRetInfoBlk);
   } // end of loadDataModel()

   
   private void readColumns(Node pTableNode)
   {
      ColumnData                   curColData;
      List                         columnsList;
      List                         constraintsList;
      List                         typeDefsList;
      Node                         curColNode;
      Node                         curTypeDef;
      Node                         curNameNode;
      Node                         curIDNode;
      Node                         curTmpNode;
      Node                         curDescriptionNode;
      Node                         tmpNodeAttribute;
      String                       columnId;     
      String                       columnName;
      String                       columnDescription;
      String                       columnType;
      String                       keyType;
      HashMap<String, Set<String>> keyMap;
      Set<String>                  keyTypeList;
      List<String>                 pkList;
      List<String>                 fkList;
      Integer                      columnLen;
      int                          columnListIdx;
      int                          columnDecimalPart;
      int                          keyInsertIdx;
      int                          colInsertIdx;
      int                          constraintsListIdx;
      int 		                    primKeyColsCount;
      boolean                      isColumnNullable;
      
      if (TraceLogger.isTraceEnabled())
      {
//         TraceLogger.entry(_Name);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "ENTRY " + _Name);
      }

      try
      {
         // handle table (key) constraints
      	primKeyColsCount = 0;
      	pkList           = new ArrayList<String>();
      	fkList           = new ArrayList<String>();
         keyMap           = new HashMap<String,Set<String>>();
         constraintsList  = XMLUtils.getChildNodeList(pTableNode, XPATH_CONSTRAINTS_DEFINITION);
         for (constraintsListIdx = 0; constraintsListIdx < constraintsList.size(); constraintsListIdx ++)
         {
            Node  constraintsNode = (Node) constraintsList.get(constraintsListIdx);
            Node  memberItem      = constraintsNode.getAttributes().getNamedItem("members");

            // process key members (just if there are any) 
            if (memberItem != null)
            {
               String keyMembers = memberItem.getNodeValue();
               keyType           = constraintsNode.getAttributes().getNamedItem("xsi:type").getNodeValue();

               // store Primary Keys only !!!!!
               if (keyType.endsWith(PK_CONSTRAINT_SUFFIX) ||
                   keyType.endsWith(FK_CONSTRAINT_SUFFIX))
               {
                  StringTokenizer keyTokenizer = new StringTokenizer(keyMembers, " ");
                  while(keyTokenizer.hasMoreTokens())
                  {
                     String keyName = keyTokenizer.nextToken();

                     if (keyType.endsWith(PK_CONSTRAINT_SUFFIX)) {
                     	keyType = PK_CONSTRAINT_SUFFIX;

   							// count the number of pimary key columns
		               	primKeyColsCount ++;
                     }
                     else {
                     	keyType = FK_CONSTRAINT_SUFFIX;
                     }

							keyTypeList = keyMap.get(keyName);
							if (keyTypeList == null)
							{
	                     keyTypeList = new HashSet<String>();
	                     keyMap.put(keyName, keyTypeList);       // add type list
							}
							keyTypeList.add(keyType);
                  } // end of while(keyTokenizer.hasMoreTokens())
               } // end of if (keyType.endsWith(PK_CONSTRAINT_SUFFIX))
            } // end of if (memberItem != null)
         } // end of for (constraintsListIdx = 0; ... constraintsListIdx ++)

         // read columns
         columnsList = XMLUtils.getChildNodeList(pTableNode, XPATH_COLUMNS_DEFINITION);
         _ColumnArr  = new ColumnData[columnsList.size()];

         keyInsertIdx = 0;                // keys must be set at the beginning of the column array
         colInsertIdx = primKeyColsCount;
         for (columnListIdx = 0; columnListIdx < columnsList.size(); columnListIdx ++) 
         {
            // create a ColumnData instance
            curColNode = (Node) columnsList.get(columnListIdx);

            // get column name and description, etc ...
            columnName         = null;
            columnDescription  = null;
            curNameNode        = curColNode.getAttributes().getNamedItem("name");
            curDescriptionNode = curColNode.getAttributes().getNamedItem("description");
            curIDNode          = curColNode.getAttributes().getNamedItem("xmi:id");
            
            if (curNameNode != null)
            {
               columnName = curNameNode.getNodeValue();
            }
            
            if (curDescriptionNode != null)
            {
               columnDescription = curDescriptionNode.getNodeValue();
            }

            if (curIDNode == null)
            {
               columnId = "noId";
            }
            else
            {
               columnId = curIDNode.getNodeValue();
            }
            curColData = new ColumnData(this, columnName, columnDescription);

            // check if the current column is a key column
            keyTypeList = keyMap.get(columnId);
            if (keyTypeList == null)
            {
            	if (TraceLogger.isTraceEnabled()) {
            		TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Inserting non-key column {0} (name: {1}) at position {2}", new Object[]{ columnListIdx, columnName, colInsertIdx });
            	}

               // ==> column IS NOT a key column
               _ColumnArr[colInsertIdx] = curColData;
               colInsertIdx ++;
            }
            else
            {
            	if (keyTypeList.contains(PK_CONSTRAINT_SUFFIX)) {
                  // just primary keys are flagged as 'isKey'
                  curColData.setIsKeyColumn(true);
                  pkList.add(columnName);

                  _ColumnArr[keyInsertIdx] = curColData;
                  keyInsertIdx ++;

               	 if (TraceLogger.isTraceEnabled()) {
               		 TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Inserting key column {0} (name: {1}) at position {2}", new Object[]{ columnListIdx, columnName, (keyInsertIdx-1) });
               	 }
            	}
            	else {
               	if (keyTypeList.contains(FK_CONSTRAINT_SUFFIX)) {
                     fkList.add(columnName);

                     // ==> FK column is treated like a REGULAR column
                     _ColumnArr[colInsertIdx] = curColData;
                     colInsertIdx ++;

                     if (TraceLogger.isTraceEnabled()) {
                     	TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Inserting non-key column {0} (FK) (name: {1}) at position {2}", new Object[]{ columnListIdx, columnName, colInsertIdx });
                     }
                  } // end of if (keyTypeList.contains(FK_CONSTRAINT_SUFFIX))
            	} // end of (else) if (keyTypeList.contains(PK_CONSTRAINT_SUFFIX))
            } // end of (else) if (keyTypeList == null)
            
            // if available, set the 'nullable' flag
            curTmpNode  = curColNode.getAttributes().getNamedItem("nullable");
            if (curTmpNode != null)
            {
               isColumnNullable = Boolean.valueOf(curTmpNode.getNodeValue()).booleanValue();
               curColData.setIsNullable(isColumnNullable);
            }
            else
            {
               curColData.setIsNullable(_ColumnNullableDefault);
            } // end of (else) if (curTmpNode != null)

            // determine column length or precision and scale
            columnLen    = null;
            columnType   = null;
            typeDefsList = XMLUtils.getChildNodeList(curColNode, XPATH_CONTAINED_TYPE_DEFINITION);
            if (typeDefsList.size() > 0)
            {
               curTypeDef = (Node) typeDefsList.get(0);
               columnType = curTypeDef.getAttributes().getNamedItem("name").getNodeValue();

               // get column length or precision (if available)
               tmpNodeAttribute = curTypeDef.getAttributes().getNamedItem("length");
               if (tmpNodeAttribute != null)
               {
                  columnLen = Integer.valueOf(tmpNodeAttribute.getNodeValue());
               }

               // check if there has been a 'length' value ...
               if (columnLen == null)
               {
                  // No ==> DECIMALs have no 'length' but a 'precision' attribute
                  tmpNodeAttribute = curTypeDef.getAttributes().getNamedItem("precision");
                  
                  if (tmpNodeAttribute != null)
                  {
                     columnLen = Integer.valueOf(tmpNodeAttribute.getNodeValue());
                     
                     // if available, set the 'scale' value
                     tmpNodeAttribute = curTypeDef.getAttributes().getNamedItem("scale");
                     if (tmpNodeAttribute != null)
                     {
                        columnDecimalPart = Integer.parseInt(tmpNodeAttribute.getNodeValue());
                        curColData.setScale(columnDecimalPart);
                     }
                  } // end of if (tmpNodeAttribute != null)
               } // end of if (columnLen == null)
            } // end of if (typeDefsList.size() > 0)
            curColData.setType(columnType);
            curColData.setLength(columnLen);

            // read the column annotations
            curColData.setAnnotations(curColNode);
         } // end of for (columnListIdx = 0; ... columnListIdx ++)

         _PrimaryKeyColArr = (String[])pkList.toArray(new String[0]);
         _ForeignKeyColArr = (String[])fkList.toArray(new String[0]);
      } // end of try
      catch(IllegalArgumentException pIllegalArgExcpt)
      {
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.traceException(pIllegalArgExcpt);
         }

         throw new IllegalArgumentException("SAP Table '" + _Name + "': " + pIllegalArgExcpt.getMessage());
      }
      catch(Exception pExcpt)
      {
         _ColumnArr = new ColumnData[0];
         pExcpt.printStackTrace();
/*         
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.traceException(pExcpt);
         }
*/         
      }

      if (TraceLogger.isTraceEnabled())
      {
//         TraceLogger.exit("column number = " + _ColumnArr.length);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "RETURN column number = " + _ColumnArr.length);
      }
   } // end of readColumns()


   /**
    * This method is used to remove a column from the table identified by the 
    * specified column name.
    * 
    * @param pColumnName name of the column to be removed
    * 
    * @return position where the column resided before it has been removed. -1 is returned if the column does not exist.
   **/
   public int removeColumn(String pColumnName) 
   {
      ColumnData tmpColArr[];
      int        searchColIdx;

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("column name = " + pColumnName);
      }
      
      searchColIdx  = -1;
      if (pColumnName != null)
      {
         // search for the column
         searchColIdx = searchColumn(pColumnName);
         
         if (searchColIdx > -1)
         {
            // remove the column by copying the remaining columns
            tmpColArr = new ColumnData[_ColumnArr.length-1];

            System.arraycopy(_ColumnArr, 0, tmpColArr, 0, searchColIdx);
            System.arraycopy(_ColumnArr, searchColIdx+1, tmpColArr, searchColIdx, 
                             (_ColumnArr.length - searchColIdx -1));
            
            _ColumnArr = tmpColArr;
         } // end of if (searchColIdx > -1)
      } // end of (else) if (pColumnName != null)
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("new column count = " + _ColumnArr.length + " - removed at position = " + searchColIdx);
      }
      
      return(searchColIdx);
   } // end of removeColumn()
   
   
   /**
    * This private method searches for the specified column name in the column
    * array and returns the index when it's found. 
    * <p>
    * If there is no column with that name <b>-1</b> is returned.
    * 
    * @param pColumnName name of the column to be searched
    * 
    * @return index in the column array or -1 if the column name could not be found
   **/
   private int searchColumn(String pColumnName) 
   {
      int     colArrIdx;
      int     retIndex;

      retIndex  = -1;
      colArrIdx = 0;

      while(colArrIdx < _ColumnArr.length && retIndex == -1)
      {
         if (pColumnName.equals(_ColumnArr[colArrIdx].getName()))
         {
            retIndex = colArrIdx;
         }
         else
         {
            colArrIdx ++;
         } // end of if (pColumnName.equals(_ColumnArr[colArrIdx].getName()))
      } // end of while(colArrIdx < _ColumnArr.length && retIndex == -1)
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "column index = " + retIndex);
      }
      
      return(retIndex);
   } // end of searchColumn()
   
   
   private void setAnnotations(Node pTableNode)
   {
      List         annotationsList;
      NamedNodeMap curAnnotationNodeMap;
      String       annotationKey;
      String       annotationValue;
      String       tablePurpose;
      int          annotationListIdx;
      
      try
      {
         // set some defaults ...
         _StageVariableMap = new HashMap();
         
         // read table annotations ...
         annotationsList = XMLUtils.getChildNodeList(pTableNode, XPATH_EANNOTATIONS_DEFINITION);

         tablePurpose = null;
         
         // ... then check and store required annotations
         for (annotationListIdx = 0; annotationListIdx < annotationsList.size(); 
              annotationListIdx ++) 
         {
            curAnnotationNodeMap = ((Node) annotationsList.get(annotationListIdx)).getAttributes();
            
            annotationKey   = curAnnotationNodeMap.getNamedItem("key").getNodeValue();
            annotationValue = curAnnotationNodeMap.getNamedItem("value").getNodeValue();
            
            if (annotationValue != null && annotationValue.length() > 0)
            {
               // - DATA OBJECT SOURCE -
               if (annotationKey.equals(Constants.ANNOT_DATA_OBJECT_SOURCE))
               {
                  if (annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_IDOC))
                     _TableType = SupportedTableTypesMap.TABLE_TYPE_ENUM_UNKNOWN;
                  else if (annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE))
                     _TableType = SupportedTableTypesMap.TABLE_TYPE_ENUM_LOGICAL_TABLE;
                  else if (annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_TRANSLATION_TABLE))
                     _TableType = SupportedTableTypesMap.TABLE_TYPE_ENUM_TRANSLATION_TABLE;
                  else if(annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_JOINED_CHECK_AND_TEXT_TABLE))
                     _TableType = SupportedTableTypesMap.TABLE_TYPE_ENUM_JOINED_CHECK_AND_TEXT_TABLE;
                  else if(annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_CHECK_TABLE))
                      _TableType = SupportedTableTypesMap.TABLE_TYPE_ENUM_REFERENCE_CHECK_TABLE;
                  else if(annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_NON_REFERENCE_CHECK_TABLE))
                      _TableType = SupportedTableTypesMap.TABLE_TYPE_ENUM_NON_REFERENCE_CHECK_TABLE;
                  else if(annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_TEXT_TABLE))
                      _TableType = SupportedTableTypesMap.TABLE_TYPE_ENUM_REFERENCE_TEXT_TABLE;
                  else if(annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_LOAD_STATUS))
                     _TableType = SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_LOAD_STATUS;
                  else if(annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_DOMAIN_TRANSLATION_TABLE))
                	  _TableType = SupportedTableTypesMap.TABLE_TYPE_ENUM_DOMAIN_TRANSLATION_TABLE;
                  else if(annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_DOMAIN_TABLE))
                	  _TableType = SupportedTableTypesMap.TABLE_TYPE_ENUM_DOMAIN_TABLE;
               }
               else // - TABLE PURPOSE -
                  if(annotationKey.equals(Constants.ANNOT_MODEL_PURPOSE))
                  {
                     tablePurpose = annotationValue;
                  }
               else // - PARENT SEGMENT -
                  if(annotationKey.equals(Constants.ANNOT_PARENT_SEG))
                  {
                     _ParentSegment = annotationValue;
                  }
               else // - SEGMENT DEFINITION -
                  if(annotationKey.equals(Constants.ANNOT_SEGMENT_DEFINITION))
                  {
                     _SegmentDefinition = annotationValue;
                  }
               else // - SEGMENT TYPE -
                  if(annotationKey.equals(Constants.ANNOT_SEGMENT_TYPE))
                  {
                     _SegmentType = annotationValue;
                  }
               else // - IDOC TYPE -
                  if(annotationKey.equals(Constants.ANNOT_IDOC_TYPE))
                  {
                     _IDocType = annotationValue;
                  }
               else // - IS EXTENDED IDOC TYPE -
                  if(annotationKey.equals(Constants.ANNOT_IS_EXTENDED_IDOC_TYPE))
                  {
                     _IsExtendedIDocType = Boolean.valueOf(annotationValue).booleanValue();
                  }
               else // - IDOC BASIC TYPE -
                  if(annotationKey.equals(Constants.ANNOT_IDOC_BASIC_TYPE))
                  {
                     _IDocBasicType = annotationValue;
                  }
               else // - IS TABLE MANDATORY -
                  if(annotationKey.equals(Constants.ANNOT_TABLE_IS_MANDATORY))
                  {
                     _IsMandatory = Boolean.valueOf(annotationValue).booleanValue();
                  }
               else // - IS ROOT SEGMENT -
                  if(annotationKey.equals(Constants.ANNOT_IS_ROOT_SEGMENT))
                  {
                     _IsRootSegment = Boolean.valueOf(annotationValue).booleanValue();
                  }
               else // - IS UNICODE SYTEM -
                  if(annotationKey.equals(Constants.ANNOT_SAP_SYSTEM_IS_UNICODE))
                  {
                     _IsUnicodeSystem = Boolean.valueOf(annotationValue).booleanValue();
                  }
               else // - ABAP Code RFC -
                  if(annotationKey.equals(Constants.ANNOT_ABAP_CODE))
                  {
                     _ABAPCodeRFC = annotationValue;
                  }
               else // - ABAP Code CPIC -
                  if(annotationKey.equals(Constants.ANNOT_ABAP_CODE_CPIC))
                  {
                     _ABAPCodeCPIC = annotationValue;
                  }
               else // - ABAP Program Name -
                  if(annotationKey.equals(Constants.ANNOT_ABAP_PROGRAM_NAME))
                  {
                     _ABAPProgramNameRFC = annotationValue;
                  }
               else // - ABAP Program Name -
                  if(annotationKey.equals(Constants.ANNOT_ABAP_PROGRAM_NAME_CPIC))
                  {
                     _ABAPProgramNameCPIC = annotationValue;
                  }
               else // - SAP System Host -
                  if(annotationKey.equals(Constants.ANNOT_SAP_SYSTEM_HOST))
                  {
                     _SAPSystemHost = annotationValue;
                  }
               else // - Stage Variables (XML) -
                  if(annotationKey.equals(Constants.ANNOT_COLUMN_STAGE_VARIABLES))
                  {
                     _StageVariableMap = createStageVarMap(annotationValue);
                  }
               else // - Filter constraint -
                  if(annotationKey.equals(Constants.ANNOT_FILTER_CONSTRAINT))
                  {
                     _FilterConstraint = annotationValue;
                  }
               else // - Model Version -
                  if(annotationKey.equals(Constants.ANNOT_GENERATED_MODEL_VERSION))
                  {
                     _ModelVersion = annotationValue;
                  }
               else // - Logical Table Name -
                  if(annotationKey.equals(Constants.ANNOT_LOGICAL_TBL_NAME))
                  {
                     _LogicalName = annotationValue;
                  }
               else // - Physical Table Name -
                  if(annotationKey.equals(Constants.ANNOT_PHYSICAL_TBL_NAME))
                  {
                     _PhysicalName = annotationValue;
                  }
               else // - SAP Table Name -
              	   if(annotationKey.equals(Constants.ANNOT_SAP_TABLE_NAME))
               	{
              	   	_SAPTableName = annotationValue;
               	}
               else // - Check Table Type -
            	   if(annotationKey.equals(Constants.ANNOT_SAPPACK_CHECK_TBL_DATA_OBJECT_SOURCE))
                 {
                	 _CheckTableType = annotationValue;
                 }
            } // end of if (annotationValue != null && annotationValue.length() > 0)
         } // end of for (annotationListIdx = 0; ... annotationListIdx ++)

         // evaluate the table purpose ...
         if (tablePurpose != null)
         {
            if (tablePurpose.equals(Constants.MODEL_PURPOSE_IDOC_ALL))
            {
               _TableType = SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_ALL;
            }
            else
            {
               if (tablePurpose.equals(Constants.MODEL_PURPOSE_IDOC_EXTRACT))
               {
                  _TableType = SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_EXTRACT;
               }
               else
               {
                  if (tablePurpose.equals(Constants.MODEL_PURPOSE_IDOC_LOAD))
                  {
                     _TableType = SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_LOAD;
                  }
               }
            } // end of (else) if (tablePurpose.equals(Constants.MODEL_PURPOSE_IDOC_ALL))
         } // end of if (tablePurpose != null)
         
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "table type = " 
                                                      + (_TableType == null ? "UNKNOWN" : SupportedTableTypesMap.getType(_TableType.intValue())) );
         }
      } // end of try
      catch(Exception pExcpt)
      {
         _ColumnArr = new ColumnData[0];
/*
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.traceException(pExcpt);
         }
*/         
         pExcpt.printStackTrace();
      }
   } // end of setAnnotations()
   
   
   void setDBSchema(String newSchema)
   {
      _DBSchema = newSchema;
   } // end of setDBSchema()

   
   public void setFilterConstraint(String newFilterContraint) 
   {
      _FilterConstraint = newFilterContraint;
   }   


   public void setSQLStatement(String sqlStatement) 
   {
   	this._SQLStatement = sqlStatement;
   }


   public String toString()
   {
      return(_Name);
   } // end of toString()


} // end of class TableData
