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


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ibm.is.sappack.gen.common.DBSupport.DataBaseType;



public final class DSTypeDBMMapping {
   // -------------------------------------------------------------------------------------
   //                            S u b   c l a s s e s
   // -------------------------------------------------------------------------------------
   public static class DSDataType {
      String _TypeName;
//      String _DBMType;
      String _TypeCode;
      
      public DSDataType(String typeName, String typeCode) {
         _TypeName   = typeName;
         _TypeCode = typeCode;
      }

      public String getTypeName() {
         return(_TypeName);
      }
      
      public String getTypeCode() {
         return(_TypeCode);
      }
      
      public String toString() {
         return(_TypeName + ":" + _TypeCode);
      }
   } // end of class DSDataType()      
      
   
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   
   // ******************** Physical Data Model Data Types **************************
   // ................................ DB2 ......................................................                        
   private static final String DBM_DB2_TYPE_BIGINT                    = "BIGINT";
   private static final String DBM_DB2_TYPE_BLOB                      = "BLOB";
   private static final String DBM_DB2_TYPE_CHAR                      = "CHAR";
   private static final String DBM_DB2_TYPE_CHAR_FOR_BIT_DATA         = "CHAR FOR BIT DATA";
   private static final String DBM_DB2_TYPE_CLOB                      = "CLOB";
   private static final String DBM_DB2_TYPE_DATALINK                  = "DATALINK";
   private static final String DBM_DB2_TYPE_DATE                      = "DATE";
   private static final String DBM_DB2_TYPE_DBCLOB                    = "DBCLOB";
   private static final String DBM_DB2_TYPE_DECFLOAT                  = "DECFLOAT";
   private static final String DBM_DB2_TYPE_DECIMAL                   = "DECIMAL";
   private static final String DBM_DB2_TYPE_DOUBLE                    = "DOUBLE";
   private static final String DBM_DB2_TYPE_FLOAT                     = "FLOAT";
   private static final String DBM_DB2_TYPE_GRAPHIC                   = "GRAPHIC";
   private static final String DBM_DB2_TYPE_INTEGER                   = "INTEGER";
   private static final String DBM_DB2_TYPE_LONG_VARCHAR              = "LONG VARCHAR";
   private static final String DBM_DB2_TYPE_LONG_VARCHAR_FOR_BIT_DATA = "LONG VARCHAR FOR BIT DATA";
   private static final String DBM_DB2_TYPE_LONG_VARGRAPHIC           = "LONG VARGRAPHIC";
   private static final String DBM_DB2_TYPE_SMALLINT                  = "SMALLINT";
   private static final String DBM_DB2_TYPE_TIME                      = "TIME";
   private static final String DBM_DB2_TYPE_TIMESTAMP                 = "TIMESTAMP";
   private static final String DBM_DB2_TYPE_VARCHAR                   = "VARCHAR";
   private static final String DBM_DB2_TYPE_VARCHAR_FOR_BIT_DATA      = "VARCHAR FOR BIT DATA";
   private static final String DBM_DB2_TYPE_VARGRAPHIC                = "VARGRAPHIC";
   private static final String DBM_DB2_TYPE_XML                       = "XML";

   // .............................. NETEZZA ....................................................                        
	private static final String DBM_NZZA_TYPE_BOOLEAN                  = "BOOLEAN";
	private static final String DBM_NZZA_TYPE_BIGINT                   = "BIGINT";
	private static final String DBM_NZZA_TYPE_DATE                     = "DATE";
	private static final String DBM_NZZA_TYPE_DOUBLE                   = "DOUBLE";
	private static final String DBM_NZZA_TYPE_FLOAT                    = "FLOAT";
	private static final String DBM_NZZA_TYPE_INTEGER                  = "INTEGER";
	private static final String DBM_NZZA_TYPE_INTERVAL                 = "INTERVAL";
	private static final String DBM_NZZA_TYPE_NCHAR                    = "NCHAR";
	private static final String DBM_NZZA_TYPE_NUMERIC                  = "NUMERIC";
	private static final String DBM_NZZA_TYPE_NVARCHAR                 = "NVARCHAR";
	private static final String DBM_NZZA_TYPE_SMALLINT                 = "SMALLINT";
	private static final String DBM_NZZA_TYPE_TIME                     = "TIME";
	private static final String DBM_NZZA_TYPE_VARCHAR                  = "VARCHAR";
	
   // .............................. ORACLE ........................................................                        
   private static final String DBM_ORA_TYPE_BOOLEAN                   = "BOOLEAN";
   private static final String DBM_ORA_TYPE_CLOB                      = "CLOB";
   private static final String DBM_ORA_TYPE_BFILE                     = "BFILE";
   private static final String DBM_ORA_TYPE_DATE                      = "DATE";
   private static final String DBM_ORA_TYPE_BLOB                      = "BLOB";
   private static final String DBM_ORA_TYPE_CHAR                      = "CHAR";
   private static final String DBM_ORA_TYPE_INT                       = "INT";
   private static final String DBM_ORA_TYPE_INTEGER                   = "INTEGER";
   private static final String DBM_ORA_TYPE_LONG_VARCHAR              = "LONG RAW";
   private static final String DBM_ORA_TYPE_NCHAR                     = "NCHAR";
   private static final String DBM_ORA_TYPE_NUMBER                    = "NUMBER";
   private static final String DBM_ORA_TYPE_NUMERIC                   = "NUMERIC";
   private static final String DBM_ORA_TYPE_NVARCHAR_2                = "NVARCHAR2";
   private static final String DBM_ORA_TYPE_RAW                       = "RAW";
   private static final String DBM_ORA_TYPE_REAL                      = "REAL";
   private static final String DBM_ORA_TYPE_TIMESTAMP                 = "TIMESTAMP";
   private static final String DBM_ORA_TYPE_VARCHAR_2                 = "VARCHAR2";
   private static final String DBM_ORA_TYPE_XML                       = "XML";

   // ******************** SAP Data Types **************************
   private static final String SAP_DATA_TYPE_CLIENT                   = "CLNT";
   private static final String SAP_DATA_TYPE_CHAR                     = "CHAR";
   private static final String SAP_DATA_TYPE_VARC                     = "VARC";
   private static final String SAP_DATA_TYPE_ACCP                     = "ACCP";
   private static final String SAP_DATA_TYPE_CUKY                     = "CUKY";
   private static final String SAP_DATA_TYPE_DATS                     = "DATS";
   private static final String SAP_DATA_TYPE_DEC                      = "DEC";
   private static final String SAP_DATA_TYPE_FLTP                     = "FLTP";
   private static final String SAP_DATA_TYPE_INT1                     = "INT1";
   private static final String SAP_DATA_TYPE_INT2                     = "INT2";
   private static final String SAP_DATA_TYPE_INT4                     = "INT4";
   private static final String SAP_DATA_TYPE_LANG                     = "LANG";
   private static final String SAP_DATA_TYPE_NUMC                     = "NUMC";
   private static final String SAP_DATA_TYPE_PREC                     = "PREC";
   private static final String SAP_DATA_TYPE_RAW                      = "RAW";
   private static final String SAP_DATA_TYPE_RSTR                     = "RSTR";
   private static final String SAP_DATA_TYPE_SSTR                     = "SSTR";
   private static final String SAP_DATA_TYPE_STRG                     = "STRG";
   private static final String SAP_DATA_TYPE_TIMS                     = "TIMS";
   private static final String SAP_DATA_TYPE_UNIT                     = "UNIT";
   private static final String SAP_DATA_TYPE_LRAW                     = "LRAW";

   // ******************** DataStage Type Codes **************************
//   private static final String DS_TYPE_CODE_INT8                      = "INT8";
   private static final String DS_TYPE_CODE_INT16                     = "INT16";
   private static final String DS_TYPE_CODE_INT32                     = "INT32";
   private static final String DS_TYPE_CODE_INT64                     = "INT64";
   private static final String DS_TYPE_CODE_SFLOAT                    = "SFLOAT";
   private static final String DS_TYPE_CODE_DFLOAT                    = "DFLOAT";
//   private static final String DS_TYPE_CODE_QFLOAT                    = "QFLOAT";
   private static final String DS_TYPE_CODE_DECIMAL                   = "DECIMAL";
   private static final String DS_TYPE_CODE_STRING                    = "STRING";
   private static final String DS_TYPE_CODE_BINARY                    = "BINARY";
//   private static final String DS_TYPE_CODE_BOOLEAN                   = "BOOLEAN";
   private static final String DS_TYPE_CODE_DATE                      = "DATE";
   private static final String DS_TYPE_CODE_TIME                      = "TIME";
   private static final String DS_TYPE_CODE_DATETIME                  = "DATETIME";
//   private static final String DS_TYPE_CODE_DURATION                  = "DURATION";
//   private static final String DS_TYPE_CODE_CHOICE                    = "CHOICE";
//   private static final String DS_TYPE_CODE_ORDERED_GROUP             = "ORDERED_GROUP";
//   private static final String DS_TYPE_CODE_UNORDERED_GROUP           = "UNORDERED_GROUP";
//   private static final String DS_TYPE_CODE_GUID                      = "GUID";
   private static final String DS_TYPE_CODE_UNKNOWN                   = "UNKNOWN";
 
// dataTypes = new String[] { "VARCHAR", "INTEGER", "DECIMAL", "DATE", "DOUBLE",                //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//                "TIME", "FLOAT", "BINARY", "VARGRAPHIC", "CHAR", "SMALLINT",      //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
//                "LONGVARCHAR", "WCHAR", "WVARCHAR", "WLONGVARCHAR", "NUMERIC",    //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//                "REAL", "BIT", "TINYINT", "BIGINT", "VARBINARY", "LONGVARBINARY", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//                "TIMESTAMP", "GUID" };                                            //$NON-NLS-1$ //$NON-NLS-2$
 
   
   // ******************** DataStage (ODBC) Types **************************
   private static final DSDataType DS_TYPE_BIGINT                          = new DSDataType("BigInt", DS_TYPE_CODE_INT64);
   private static final DSDataType DS_TYPE_BINARY                          = new DSDataType("Binary", DS_TYPE_CODE_BINARY);
//   private static final String DS_TYPE_BIT                             = new DSType("Bit", DS_TYPE_CODE_INT16);
   private static final DSDataType DS_TYPE_DATE                            = new DSDataType("Date", DS_TYPE_CODE_DATE);
   private static final DSDataType DS_TYPE_DECIMAL                         = new DSDataType("Decimal", DS_TYPE_CODE_DECIMAL);
   private static final DSDataType DS_TYPE_DOUBLE                          = new DSDataType("Double", DS_TYPE_CODE_DFLOAT);
   private static final DSDataType DS_TYPE_FLOAT                           = new DSDataType("Float", DS_TYPE_CODE_SFLOAT);
   private static final DSDataType DS_TYPE_CHAR                            = new DSDataType("Char", DS_TYPE_CODE_STRING);
//   private static final DSType DS_TYPE_GUID                            = new DSType("GUId", DS_TYPE_CODE_STRING);
   private static final DSDataType DS_TYPE_INTEGER                         = new DSDataType("Integer", DS_TYPE_CODE_INT32);
   private static final DSDataType DS_TYPE_LONGVARBINARY                   = new DSDataType("LongVarBinary", DS_TYPE_CODE_BINARY);
   private static final DSDataType DS_TYPE_LONGVARCHAR                     = new DSDataType("LongVarChar", DS_TYPE_CODE_STRING);
   private static final DSDataType DS_TYPE_NUMERIC                         = new DSDataType("Numeric", DS_TYPE_CODE_DECIMAL);
   private static final DSDataType DS_TYPE_REAL                            = new DSDataType("Real", DS_TYPE_CODE_SFLOAT);
   private static final DSDataType DS_TYPE_SMALLINT                        = new DSDataType("SmallInt", DS_TYPE_CODE_INT16);
   private static final DSDataType DS_TYPE_TIME                            = new DSDataType("Time", DS_TYPE_CODE_TIME);
   private static final DSDataType DS_TYPE_TIMESTAMP                       = new DSDataType("Timestamp", DS_TYPE_CODE_DATETIME);
//   private static final DSType DS_TYPE_TINYINT                         = new DSType("TinyInt", DS_TYPE_CODE_INT16);
   private static final DSDataType DS_TYPE_VARBINARY                       = new DSDataType("VarBinary", DS_TYPE_CODE_BINARY);
   private static final DSDataType DS_TYPE_VARCHAR                         = new DSDataType("VarChar", DS_TYPE_CODE_STRING);
   private static final DSDataType DS_TYPE_WCHAR                           = new DSDataType("WChar", DS_TYPE_CODE_STRING);
   private static final DSDataType DS_TYPE_WLONGVARCHAR                    = new DSDataType("WLongVarChar", DS_TYPE_CODE_STRING);
   private static final DSDataType DS_TYPE_WVARCHAR                        = new DSDataType("WVarChar", DS_TYPE_CODE_STRING);
   private static final DSDataType DS_TYPE_UNKNWON                         = new DSDataType("Unknown", DS_TYPE_CODE_UNKNOWN);
   
   

   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private static Map<String,DSDataType>     _DBMMappingUnknown;
   private static Map<String, Set<String>>   _DBMMappingUnknownInverted;
   private static Map<String,DSDataType>     _DBMMappingDB2;
   private static Map<String, Set<String>>   _DBMMappingDB2Inverted;
   private static Map<String,DSDataType>     _DBMMappingNzza;
   private static Map<String, Set<String>>   _DBMMappingNzzaInverted;
   private static Map<String,DSDataType>     _DBMMappingOra;
   private static Map<String, Set<String>>   _DBMMappingOraInverted;
   
   private static Map<String,DSDataType>    _DBMMappingSAP;
   private static Map<String, Set<String>>  _DBMMappingSAPInverted;
   private static Set<String>               _AllDSTypeNames;

   
   static {
      _DBMMappingUnknown         = new HashMap<String,DSDataType>();
      _DBMMappingUnknownInverted = new HashMap<String, Set<String>>();
      _DBMMappingDB2             = new HashMap<String,DSDataType>();
      _DBMMappingDB2Inverted     = new HashMap<String, Set<String>>();
      _DBMMappingNzza            = new HashMap<String,DSDataType>();
      _DBMMappingNzzaInverted    = new HashMap<String, Set<String>>();
      _DBMMappingOra             = new HashMap<String,DSDataType>();
      _DBMMappingOraInverted     = new HashMap<String, Set<String>>();
      _DBMMappingSAP             = new HashMap<String,DSDataType>();
      _DBMMappingSAPInverted     = new HashMap<String, Set<String>>();
      _AllDSTypeNames            = new HashSet<String>();
      
      // setup DBM (mapping) map for UNKNOWN2
      addDbmAndDSTypeToMaps(_DBMMappingUnknown, _DBMMappingUnknownInverted, null, DS_TYPE_UNKNWON);
      
      // setup DBM (mapping) map for DB2
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_BIGINT, DS_TYPE_BIGINT);
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_BIGINT, DS_TYPE_BIGINT);
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_BLOB, DS_TYPE_LONGVARBINARY);
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_CHAR, DS_TYPE_CHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_CHAR_FOR_BIT_DATA, DS_TYPE_BINARY); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_CLOB,DS_TYPE_LONGVARCHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_DATALINK, DS_TYPE_VARCHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_DATE,DS_TYPE_DATE); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_DBCLOB, DS_TYPE_WLONGVARCHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_DECFLOAT, DS_TYPE_FLOAT); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_DECIMAL, DS_TYPE_DECIMAL); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_DOUBLE, DS_TYPE_DOUBLE); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_FLOAT, DS_TYPE_FLOAT); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_GRAPHIC, DS_TYPE_WCHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_INTEGER, DS_TYPE_INTEGER); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_LONG_VARCHAR, DS_TYPE_VARCHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_LONG_VARCHAR_FOR_BIT_DATA, DS_TYPE_VARBINARY); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_LONG_VARGRAPHIC, DS_TYPE_WVARCHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_SMALLINT, DS_TYPE_SMALLINT); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_TIME, DS_TYPE_TIME); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_TIMESTAMP, DS_TYPE_TIMESTAMP); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_VARCHAR, DS_TYPE_VARCHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_VARCHAR_FOR_BIT_DATA, DS_TYPE_VARBINARY); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_VARGRAPHIC, DS_TYPE_VARCHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, DBM_DB2_TYPE_XML, DS_TYPE_LONGVARCHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingDB2, _DBMMappingDB2Inverted, null, DS_TYPE_UNKNWON); 

      // setup DBM (mapping) map for Netezza
      addDbmAndDSTypeToMaps(_DBMMappingNzza, _DBMMappingNzzaInverted, DBM_NZZA_TYPE_BOOLEAN,  DS_TYPE_CHAR);
      addDbmAndDSTypeToMaps(_DBMMappingNzza, _DBMMappingNzzaInverted, DBM_NZZA_TYPE_BIGINT,   DS_TYPE_BIGINT);
      addDbmAndDSTypeToMaps(_DBMMappingNzza, _DBMMappingNzzaInverted, DBM_NZZA_TYPE_DATE,     DS_TYPE_DATE);
      addDbmAndDSTypeToMaps(_DBMMappingNzza, _DBMMappingNzzaInverted, DBM_NZZA_TYPE_DOUBLE,   DS_TYPE_DOUBLE);
      addDbmAndDSTypeToMaps(_DBMMappingNzza, _DBMMappingNzzaInverted, DBM_NZZA_TYPE_FLOAT,    DS_TYPE_FLOAT);
      addDbmAndDSTypeToMaps(_DBMMappingNzza, _DBMMappingNzzaInverted, DBM_NZZA_TYPE_INTEGER,  DS_TYPE_INTEGER);
      addDbmAndDSTypeToMaps(_DBMMappingNzza, _DBMMappingNzzaInverted, DBM_NZZA_TYPE_INTERVAL, DS_TYPE_UNKNWON);
      addDbmAndDSTypeToMaps(_DBMMappingNzza, _DBMMappingNzzaInverted, DBM_NZZA_TYPE_NCHAR,    DS_TYPE_WCHAR);
      addDbmAndDSTypeToMaps(_DBMMappingNzza, _DBMMappingNzzaInverted, DBM_NZZA_TYPE_NUMERIC,  DS_TYPE_NUMERIC);
      addDbmAndDSTypeToMaps(_DBMMappingNzza, _DBMMappingNzzaInverted, DBM_NZZA_TYPE_NVARCHAR, DS_TYPE_VARCHAR);
      addDbmAndDSTypeToMaps(_DBMMappingNzza, _DBMMappingNzzaInverted, DBM_NZZA_TYPE_SMALLINT, DS_TYPE_SMALLINT);
      addDbmAndDSTypeToMaps(_DBMMappingNzza, _DBMMappingNzzaInverted, DBM_NZZA_TYPE_TIME,     DS_TYPE_TIME);
      addDbmAndDSTypeToMaps(_DBMMappingNzza, _DBMMappingNzzaInverted, DBM_NZZA_TYPE_VARCHAR,  DS_TYPE_VARCHAR);

      // setup DBM (mapping) map for Oracle
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_BOOLEAN, DS_TYPE_CHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_CLOB, DS_TYPE_LONGVARCHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_BFILE, DS_TYPE_VARCHAR); 
      // defect 114402 addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_DATE, DS_TYPE_DATE); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_DATE, DS_TYPE_TIMESTAMP); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_BLOB, DS_TYPE_LONGVARBINARY);
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_CHAR, DS_TYPE_CHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_INT, DS_TYPE_DOUBLE); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_INTEGER, DS_TYPE_INTEGER); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_LONG_VARCHAR, DS_TYPE_VARCHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_NCHAR, DS_TYPE_WCHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_NUMBER, DS_TYPE_DOUBLE); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_NUMERIC, DS_TYPE_NUMERIC); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_NVARCHAR_2, DS_TYPE_VARCHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_RAW, DS_TYPE_BINARY); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_REAL, DS_TYPE_REAL); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_VARCHAR_2, DS_TYPE_VARCHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_TIMESTAMP, DS_TYPE_TIMESTAMP); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, DBM_ORA_TYPE_XML, DS_TYPE_LONGVARCHAR); 
      addDbmAndDSTypeToMaps(_DBMMappingOra, _DBMMappingOraInverted, null, DS_TYPE_UNKNWON); 

      // setup DBM (mapping) map for SAP
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_CHAR, DS_TYPE_VARCHAR);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_CLIENT, DS_TYPE_VARCHAR);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_VARC, DS_TYPE_VARCHAR);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_ACCP, DS_TYPE_VARCHAR);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_CUKY, DS_TYPE_VARCHAR);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_DATS, DS_TYPE_VARCHAR);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_DEC,  DS_TYPE_DECIMAL);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_FLTP, DS_TYPE_FLOAT);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_INT1, DS_TYPE_SMALLINT);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_INT2, DS_TYPE_INTEGER);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_INT4, DS_TYPE_BIGINT);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_LANG, DS_TYPE_VARCHAR);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_NUMC, DS_TYPE_VARCHAR);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_PREC, DS_TYPE_INTEGER);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_RAW,  DS_TYPE_BINARY);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_RSTR, DS_TYPE_VARCHAR);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_SSTR, DS_TYPE_VARCHAR);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_STRG, DS_TYPE_VARCHAR);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_TIMS, DS_TYPE_VARCHAR);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_UNIT, DS_TYPE_VARCHAR);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, SAP_DATA_TYPE_LRAW, DS_TYPE_BINARY);
      addDbmAndDSTypeToMaps(_DBMMappingSAP, _DBMMappingSAPInverted, null, DS_TYPE_UNKNWON); 
   } // end of static


   static String copyright() { 
      return com.ibm.is.sappack.gen.common.Copyright.IBM_COPYRIGHT_SHORT; 
   }


   private static void addDbmAndDSTypeToMaps(Map<String,DSDataType> mappingMap, Map<String, Set<String>> invertedMappingMap, 
                                             String dbmType, DSDataType dsDataType) {
      Set<String> dbmList;

      // first add to mapping (map)
      mappingMap.put(dbmType, dsDataType);

      // then check if DSType name exists in the inverted mapping (map)
      dbmList = invertedMappingMap.get(dsDataType.getTypeName());
      if (dbmList == null) {
         dbmList = new HashSet<String>();
         invertedMappingMap.put(dsDataType.getTypeName(), dbmList);
      }
      
      // add the DBM type to the DBM set
      dbmList.add(dbmType);
      _AllDSTypeNames.add(dsDataType.getTypeName());
   } // end of addDbmAndTypeToMaps()


//   private static Map convertToInvertedMap(Map sourceMap) {
//      Map  targetMap;
//      List dbmList;
//      
//      targetMap = new HashMap();
//      Iterator mappingIter = dsTypeMapping.entrySet().iterator();
//      while(mappingIter.hasNext()) {
//         Map.Entry mapEntry = (Map.Entry) mappingIter.next();
//
//         String dbmType                 = (String) mapEntry.getKey();
//         DSTypeDBMMapping.DSType dsType = (DSTypeDBMMapping.DSType) mapEntry.getValue();
//         CustomDerivationMapping mapping = new CustomDerivationMapping(dbmType, dsType, "$1"); //$NON-NLS-1$
//         contentProvider.addMapping(mapping);
//      }
//      
//      return(targetMap);
//   } // end of convertToInvertedMap()
   
   
   public static Map<String,DSDataType> getDBMMapping(DataBaseType databaseType) {
   	
      Map<String,DSDataType> retMap;

      switch(databaseType) {
         case Netezza:
              retMap = _DBMMappingNzza;
              break;
              
         case Oracle:
              retMap = _DBMMappingOra;
              break;
            
         case DB2:
              retMap = _DBMMappingDB2;
         	  break;
          
         default:
         case Unknown:
              retMap = _DBMMappingUnknown;
      }
      
      return(retMap);
   } // end of getDBMMapping()

   
   public static Map<String, Set<String>> getInvertedDBMMapping(DataBaseType databaseType) {
   	Map<String, Set<String>> retMap;

      switch(databaseType) {
         case Netezza:
              retMap = _DBMMappingNzzaInverted;
              break;
              
         case Oracle:
              retMap = _DBMMappingOraInverted;
              break;
            
         case DB2:
              retMap = _DBMMappingDB2Inverted;
              break;
          
         default:
         case Unknown:
              retMap = _DBMMappingUnknownInverted;
      }
      
      return(retMap);
   } // end of getInvertedDBMMapping()

   
   public static Map<String, Set<String>> getInvertedSAPMapping(int dbId) {
      return(_DBMMappingSAPInverted);
   } // end of getInvertedSAPMapping()


   public static Map<String,DSDataType> getSAPMapping() {
      return(_DBMMappingSAP);
   } // end of getSAPMapping()
   
   public static Set<String> getDataStageODBCTypeNames() {
	   return _AllDSTypeNames;
   }
   
} // end of class DSTypeDBMMapping
