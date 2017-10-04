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
// Module Name : com.ibm.is.sappack.gen.server.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;


/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>DS Stage Type Enum</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.ibm.investigate.flowgraph.FlowgraphPackage#getDSStageTypeEnum()
 * @model
 */
public final class DSStageTypeEnum extends AbstractEnumerator {
	
	static String copyright() { 
	   return com.ibm.is.sappack.gen.server.common.Copyright.IBM_COPYRIGHT_SHORT; 
	}


	/**
	 * The '<em><b>Container View</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONTAINER_VIEW_LITERAL
	 * @model name="ContainerView"
	 * @ordered
	 */
	public static final int CONTAINER_VIEW_STAGE = 0;

	/**
	 * The '<em><b>ODBC Connector PX</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ODBC_CONNECTOR_PX_LITERAL
	 * @model name="ODBCConnectorPX"
	 * @ordered
	 */
	public static final int ODBC_CONNECTOR_PX_STAGE = 1;

	/**
	 * The '<em><b>Px Oracle</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_ORACLE_LITERAL
	 * @model name="PxOracle"
	 * @ordered
	 */
	public static final int PX_ORACLE_STAGE = 2;

	/**
	 * The '<em><b>DB2 Connector PX</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DB2_CONNECTOR_PX_LITERAL
	 * @model name="DB2ConnectorPX"
	 * @ordered
	 */
	public static final int DB2_CONNECTOR_PX_STAGE = 3;

	/**
	 * The '<em><b>Px Copy</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_COPY_LITERAL
	 * @model name="PxCopy"
	 * @ordered
	 */
	public static final int PX_COPY_STAGE = 4;

	/**
	 * The '<em><b>Container Stage</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONTAINER_STAGE_LITERAL
	 * @model name="ContainerStage"
	 * @ordered
	 */
	public static final int CONTAINER_STAGE = 5;

	/**
	 * The '<em><b>CTransformer Stage</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CTRANSFORMER_STAGE_LITERAL
	 * @model name="CTransformerStage"
	 * @ordered
	 */
	public static final int CTRANSFORMER_STAGE = 6;

	/**
	 * The '<em><b>Px Aggregator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_AGGREGATOR_LITERAL
	 * @model name="PxAggregator"
	 * @ordered
	 */
	public static final int PX_AGGREGATOR_STAGE = 7;

	/**
	 * The '<em><b>Px External Filter</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_EXTERNAL_FILTER_LITERAL
	 * @model name="PxExternalFilter"
	 * @ordered
	 */
	public static final int PX_EXTERNAL_FILTER_STAGE = 8;

	/**
	 * The '<em><b>Px Filter</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_FILTER_LITERAL
	 * @model name="PxFilter"
	 * @ordered
	 */
	public static final int PX_FILTER_STAGE = 9;

	/**
	 * The '<em><b>Px Funnel</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_FUNNEL_LITERAL
	 * @model name="PxFunnel"
	 * @ordered
	 */
	public static final int PX_FUNNEL_STAGE = 10;

	/**
	 * The '<em><b>Px Generic</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_GENERIC_LITERAL
	 * @model name="PxGeneric"
	 * @ordered
	 */
	public static final int PX_GENERIC_STAGE = 11;

	/**
	 * The '<em><b>Px Join</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_JOIN_LITERAL
	 * @model name="PxJoin"
	 * @ordered
	 */
	public static final int PX_JOIN_STAGE = 12;

	/**
	 * The '<em><b>Px Lookup</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_LOOKUP_LITERAL
	 * @model name="PxLookup"
	 * @ordered
	 */
	public static final int PX_LOOKUP_STAGE = 13;

	/**
	 * The '<em><b>Px Merge</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_MERGE_LITERAL
	 * @model name="PxMerge"
	 * @ordered
	 */
	public static final int PX_MERGE_STAGE = 14;

	/**
	 * The '<em><b>Px Modify</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_MODIFY_LITERAL
	 * @model name="PxModify"
	 * @ordered
	 */
	public static final int PX_MODIFY_STAGE = 15;

	/**
	 * The '<em><b>Netezza Connector PX</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #NETEZZA_CONNECTOR_PX_LITERAL
	 * @model name="NetezzaConnectorPX"
	 * @ordered
	 */
	public static final int NETEZZA_CONNECTOR_PX_STAGE = 16;

	/**
	 * The '<em><b>Px Remove Duplicates</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_REMOVE_DUPLICATES_LITERAL
	 * @model name="PxRemoveDuplicates"
	 * @ordered
	 */
	public static final int PX_REMOVE_DUPLICATES_STAGE = 17;

	/**
	 * The '<em><b>Px Sort</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_SORT_LITERAL
	 * @model name="PxSort"
	 * @ordered
	 */
	public static final int PX_SORT_STAGE = 18;

	/**
	 * The '<em><b>Px Surrogate Key Generator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_SURROGATE_KEY_GENERATOR_LITERAL
	 * @model name="PxSurrogateKeyGenerator"
	 * @ordered
	 */
	public static final int PX_SURROGATE_KEY_GENERATOR_STAGE = 19;

	/**
	 * The '<em><b>Px Switch</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_SWITCH_LITERAL
	 * @model name="PxSwitch"
	 * @ordered
	 */
	public static final int PX_SWITCH_STAGE = 20;

   /**
    * The '<em><b>Px Column Generator</b></em>' literal value.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #PX_COLUMN_GENERATOR_LITERAL
    * @model name="PxColumnGenerator"
    * @ordered
    */
   public static final int PX_COLUMN_GENERATOR_STAGE = 21;

	/**
	 * The '<em><b>Px Sequential File</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_SEQUENTIAL_FILE_LITERAL
	 * @model name="PxSequentialFile"
	 * @ordered
	 */
	public static final int PX_SEQUENTIAL_FILE_STAGE = 23;

	/**
	 * The '<em><b>Px DB2</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_DB2_LITERAL
	 * @model name="PxDB2"
	 * @ordered
	 */
	public static final int PX_DB2_STAGE = 24;

	/**
	 * The '<em><b>Px Sybase</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PX_SYBASE_LITERAL
	 * @model name="PxSybase"
	 * @ordered
	 */
	public static final int PX_SYBASE_STAGE = 25;

	/**
	 * The '<em><b>MQ Series PX</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MQ_SERIES_PX_LITERAL
	 * @model name="MQSeriesPX"
	 * @ordered
	 */
	public static final int MQ_SERIES_PX_STAGE = 26;

	/**
	 * The '<em><b>ABAP EXTRACT PX</b></em>' literal value.
	 * FIXME The value is not generated, but manually inserted.
	 * Include it into the model as soon as possible!
	 */
	public static final int ABAP_EXTRACT_PX_STAGE = 30;
	
	/**
	 * The '<em><b>SAP IDOC EXTRACT PX</b></em>' literal value for WISD stages.
	 * FIXME The value is not generated, but manually inserted.
	 * Include it into the model as soon as possible!
	 */
	public static final int SAP_IDOC_EXTRACT_PX_STAGE = 31;

	/**
	 * The '<em><b>SAP IDOC LOAD PX</b></em>' literal value for WISD stages.
	 * FIXME The value is not generated, but manually inserted.
	 * Include it into the model as soon as possible!
	 */
   public static final int SAP_IDOC_LOAD_PX_STAGE = 32;

   /**
    * The '<em><b>SAP IDOC EXTRACT PX CONNECTOR</b></em>' literal value for WISD stages.
    * FIXME The value is not generated, but manually inserted.
    * Include it into the model as soon as possible!
    */
   public static final int SAP_IDOC_EXTRACT_CONNECTOR_PX_STAGE = 33;

   /**
    * The '<em><b>SAP IDOC LOAD PX CONNECTOR</b></em>' literal value for WISD stages.
    * FIXME The value is not generated, but manually inserted.
    * Include it into the model as soon as possible!
    */
   public static final int SAP_IDOC_LOAD_CONNECTOR_PX_STAGE = 34;

   /**
    * The internal '<em><b>Local Container Def</b></em>' literal value.
    * 'Internal use only'
    * @see #LOCAL_CONTAINER_DEF_LITERAL
    * @ordered
    */
   public static final int LOCAL_CONTAINER_DEF = 35;


   
	/**
	 * The '<em><b>Container View</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Container View</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONTAINER_VIEW
	 * @ordered
	 */
	public static final DSStageTypeEnum CONTAINER_VIEW_LITERAL = new DSStageTypeEnum(CONTAINER_VIEW_STAGE, "ContainerView"); //$NON-NLS-1$

	/**
	 * The '<em><b>ODBC Connector PX</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>ODBC Connector PX</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ODBC_CONNECTOR_PX
	 * @ordered
	 */
	public static final DSStageTypeEnum ODBC_CONNECTOR_PX_LITERAL = new DSStageTypeEnum(ODBC_CONNECTOR_PX_STAGE, "ODBCConnectorPX"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px Oracle</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px Oracle</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_ORACLE
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_ORACLE_LITERAL = new DSStageTypeEnum(PX_ORACLE_STAGE, "PxOracle"); //$NON-NLS-1$

	/**
	 * The '<em><b>DB2 Connector PX</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>DB2 Connector PX</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DB2_CONNECTOR_PX
	 * @ordered
	 */
	public static final DSStageTypeEnum DB2_CONNECTOR_PX_LITERAL = new DSStageTypeEnum(DB2_CONNECTOR_PX_STAGE, "DB2ConnectorPX"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px Copy</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px Copy</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_COPY
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_COPY_LITERAL = new DSStageTypeEnum(PX_COPY_STAGE, "PxCopy"); //$NON-NLS-1$

	/**
	 * The '<em><b>Container Stage</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Container Stage</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONTAINER_STAGE
	 * @ordered
	 */
	public static final DSStageTypeEnum CONTAINER_STAGE_LITERAL = new DSStageTypeEnum(CONTAINER_STAGE, "ContainerStage"); //$NON-NLS-1$

	/**
	 * The '<em><b>CTransformer Stage</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>CTransformer Stage</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CTRANSFORMER_STAGE
	 * @ordered
	 */
	public static final DSStageTypeEnum CTRANSFORMER_STAGE_LITERAL = new DSStageTypeEnum(CTRANSFORMER_STAGE, "CTransformerStage"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px Aggregator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px Aggregator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_AGGREGATOR
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_AGGREGATOR_LITERAL = new DSStageTypeEnum(PX_AGGREGATOR_STAGE, "PxAggregator"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px External Filter</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px External Filter</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_EXTERNAL_FILTER
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_EXTERNAL_FILTER_LITERAL = new DSStageTypeEnum(PX_EXTERNAL_FILTER_STAGE, "PxExternalFilter"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px Filter</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px Filter</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_FILTER
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_FILTER_LITERAL = new DSStageTypeEnum(PX_FILTER_STAGE, "PxFilter"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px Funnel</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px Funnel</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_FUNNEL
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_FUNNEL_LITERAL = new DSStageTypeEnum(PX_FUNNEL_STAGE, "PxFunnel"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px Generic</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px Generic</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_GENERIC
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_GENERIC_LITERAL = new DSStageTypeEnum(PX_GENERIC_STAGE, "PxGeneric"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px Join</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px Join</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_JOIN
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_JOIN_LITERAL = new DSStageTypeEnum(PX_JOIN_STAGE, "PxJoin"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px Lookup</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px Lookup</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_LOOKUP
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_LOOKUP_LITERAL = new DSStageTypeEnum(PX_LOOKUP_STAGE, "PxLookup"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px Merge</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px Merge</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_MERGE
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_MERGE_LITERAL = new DSStageTypeEnum(PX_MERGE_STAGE, "PxMerge"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px Modify</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px Modify</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_MODIFY
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_MODIFY_LITERAL = new DSStageTypeEnum(PX_MODIFY_STAGE, "PxModify"); //$NON-NLS-1$

	/**
	 * The '<em><b>Netezza Connector PX</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Netezza Connector PX</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #NETEZZA_CONNECTOR_PX_STAGE
	 * @ordered
	 */
	public static final DSStageTypeEnum NETEZZA_CONNECTOR_PX_LITERAL = new DSStageTypeEnum(NETEZZA_CONNECTOR_PX_STAGE, "NetezzaConnectorPX"); //$NON-NLS-1$
	
	/**
	 * The '<em><b>Px Remove Duplicates</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px Remove Duplicates</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_REMOVE_DUPLICATES_STAGE
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_REMOVE_DUPLICATES_LITERAL = new DSStageTypeEnum(PX_REMOVE_DUPLICATES_STAGE, "PxRemDup"); //$NON-NLS-1$
//	public static final DSStageTypeEnum PX_REMOVE_DUPLICATES_LITERAL = new DSStageTypeEnum(PX_REMOVE_DUPLICATES_STAGE, "PxRemoveDuplicates"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px Sort</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px Sort</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_SORT_STAGE
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_SORT_LITERAL = new DSStageTypeEnum(PX_SORT_STAGE, "PxSort"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px Surrogate Key Generator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px Surrogate Key Generator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_SURROGATE_KEY_GENERATOR_STAGE
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_SURROGATE_KEY_GENERATOR_LITERAL = new DSStageTypeEnum(PX_SURROGATE_KEY_GENERATOR_STAGE, "PxSurrogateKeyGenerator"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px Switch</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px Switch</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_SWITCH
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_SWITCH_LITERAL = new DSStageTypeEnum(PX_SWITCH_STAGE, "PxSwitch"); //$NON-NLS-1$

   /**
    * The '<em><b>Px Column Generator</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>Px Column Generator</b></em>' literal object isn't clear,
    * there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * @see #PX_COLUMN_GENERATOR
    * @ordered
    */
   public static final DSStageTypeEnum PX_COLUMN_GENERATOR_LITERAL = new DSStageTypeEnum(PX_COLUMN_GENERATOR_STAGE, "PxColumnGenerator"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px Sequential File</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px Sequential File</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_SEQUENTIAL_FILE
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_SEQUENTIAL_FILE_LITERAL = new DSStageTypeEnum(PX_SEQUENTIAL_FILE_STAGE, "PxSequentialFile"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px DB2</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px DB2</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_DB2
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_DB2_LITERAL = new DSStageTypeEnum(PX_DB2_STAGE, "PxDB2"); //$NON-NLS-1$

	/**
	 * The '<em><b>Px Sybase</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Px Sybase</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PX_SYBASE
	 * @ordered
	 */
	public static final DSStageTypeEnum PX_SYBASE_LITERAL = new DSStageTypeEnum(PX_SYBASE_STAGE, "PxSybase"); //$NON-NLS-1$

	/**
	 * The '<em><b>MQ Series PX</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>MQ Series PX</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #MQ_SERIES_PX
	 * @ordered
	 */
	public static final DSStageTypeEnum MQ_SERIES_PX_LITERAL = new DSStageTypeEnum(MQ_SERIES_PX_STAGE, "MQSeriesPX"); //$NON-NLS-1$

	/**
	 * The '<em><b>ABAP EXTRACT PX</b></em>' literal object.
	 * FIXME The object is not generated, but manually inserted.
	 * Include it into the model as soon as possible!
	 */
	public static final DSStageTypeEnum ABAP_EXTRACT_PX_LITERAL = new DSStageTypeEnum(ABAP_EXTRACT_PX_STAGE, "ABAP_EXT_for_R3_PX"); //$NON-NLS-1$
	
	/**
	 * The '<em><b>SAP IDOC Extract PX</b></em>' literal object.
	 * FIXME The object is not generated, but manually inserted.
	 * Include it into the model as soon as possible!
	 */
	public static final DSStageTypeEnum SAP_IDOC_EXTRACT_PX_LITERAL = new DSStageTypeEnum(SAP_IDOC_EXTRACT_PX_STAGE, "IDOC_EXT_for_R3_PX"); //$NON-NLS-1$

   /**
    * The '<em><b>SAP IDOC Load PX</b></em>' literal object.
    * FIXME The object is not generated, but manually inserted.
    * Include it into the model as soon as possible!
    */
   public static final DSStageTypeEnum SAP_IDOC_LOAD_PX_LITERAL = new DSStageTypeEnum(SAP_IDOC_LOAD_PX_STAGE, "IDOC_LOAD_for_R3_PX"); //$NON-NLS-1$
   
   /**
    * The '<em><b>SAP IDOC Extract PX Connector</b></em>' literal object.
    * FIXME The object is not generated, but manually inserted.
    * Include it into the model as soon as possible!
    */
   public static final DSStageTypeEnum SAP_IDOC_EXTRACT_CONNECTOR_PX_LITERAL = new DSStageTypeEnum(SAP_IDOC_EXTRACT_CONNECTOR_PX_STAGE, "SAPIDocExtractConnectorPX"); //$NON-NLS-1$

   /**
    * The '<em><b>SAP IDOC Load PX Connector</b></em>' literal object.
    * FIXME The object is not generated, but manually inserted.
    * Include it into the model as soon as possible!
    */
   public static final DSStageTypeEnum SAP_IDOC_LOAD_CONNECTOR_PX_LITERAL = new DSStageTypeEnum(SAP_IDOC_LOAD_CONNECTOR_PX_STAGE, "SAPIDocLoadConnectorPX"); //$NON-NLS-1$
   
      
   /**
    * The '<em><b>Local Container Def</b></em>' literal object.
    * <!-- begin-user-doc -->
    * <p>
    * It's for internal use only.
    * </p>
    * @see #LOCAL_CONTAINER_DEF
    * @ordered
    */
   public static final DSStageTypeEnum LOCAL_CONTAINER_DEF_LITERAL = new DSStageTypeEnum(LOCAL_CONTAINER_DEF, "LocalContainerDef"); //$NON-NLS-1$

	
	/**
	 * An array of all the '<em><b>DS Stage Type Enum</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	private static final DSStageTypeEnum[] VALUES_ARRAY =
		new DSStageTypeEnum[] {
			CONTAINER_VIEW_LITERAL,
			ODBC_CONNECTOR_PX_LITERAL,
			PX_ORACLE_LITERAL,
			DB2_CONNECTOR_PX_LITERAL,
			PX_COPY_LITERAL,
			CONTAINER_STAGE_LITERAL,
			CTRANSFORMER_STAGE_LITERAL,
			PX_AGGREGATOR_LITERAL,
			PX_EXTERNAL_FILTER_LITERAL,
			PX_FILTER_LITERAL,
			PX_FUNNEL_LITERAL,
			PX_GENERIC_LITERAL,
			PX_JOIN_LITERAL,
			PX_LOOKUP_LITERAL,
			PX_MERGE_LITERAL,
			PX_MODIFY_LITERAL,
			NETEZZA_CONNECTOR_PX_LITERAL, 
			PX_REMOVE_DUPLICATES_LITERAL,
			PX_SORT_LITERAL,
			PX_SURROGATE_KEY_GENERATOR_LITERAL,
			PX_SWITCH_LITERAL,
			PX_COLUMN_GENERATOR_LITERAL,
			PX_SEQUENTIAL_FILE_LITERAL,
			PX_DB2_LITERAL,
			PX_SYBASE_LITERAL,
			MQ_SERIES_PX_LITERAL,
			ABAP_EXTRACT_PX_LITERAL,
			SAP_IDOC_EXTRACT_PX_LITERAL,
         SAP_IDOC_LOAD_PX_LITERAL,
         SAP_IDOC_EXTRACT_CONNECTOR_PX_LITERAL,
         SAP_IDOC_LOAD_CONNECTOR_PX_LITERAL,
         LOCAL_CONTAINER_DEF_LITERAL
		};

	/**
	 * A public read-only list of all the '<em><b>DS Stage Type Enum</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));


	/**
	 * Returns the '<em><b>DS Stage Type Enum</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static DSStageTypeEnum get(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			DSStageTypeEnum result = VALUES_ARRAY[i];
			if (result.toString().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>DS Stage Type Enum</b></em>' literal with the specified value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public static DSStageTypeEnum get(int value) {
		switch(value) {
			case CONTAINER_VIEW_STAGE:       return CONTAINER_VIEW_LITERAL;
			case ODBC_CONNECTOR_PX_STAGE:    return ODBC_CONNECTOR_PX_LITERAL;
			case PX_ORACLE_STAGE:            return PX_ORACLE_LITERAL;
			case DB2_CONNECTOR_PX_STAGE:     return DB2_CONNECTOR_PX_LITERAL;
			case PX_COPY_STAGE:              return PX_COPY_LITERAL;
			case CONTAINER_STAGE:            return CONTAINER_STAGE_LITERAL;
			case CTRANSFORMER_STAGE:         return CTRANSFORMER_STAGE_LITERAL;
			case PX_AGGREGATOR_STAGE:        return PX_AGGREGATOR_LITERAL;
			case PX_EXTERNAL_FILTER_STAGE:   return PX_EXTERNAL_FILTER_LITERAL;
			case PX_FILTER_STAGE:            return PX_FILTER_LITERAL;
			case PX_FUNNEL_STAGE:            return PX_FUNNEL_LITERAL;
			case PX_GENERIC_STAGE:           return PX_GENERIC_LITERAL;
			case PX_JOIN_STAGE:              return PX_JOIN_LITERAL;
			case PX_LOOKUP_STAGE:            return PX_LOOKUP_LITERAL;
			case PX_MERGE_STAGE:             return PX_MERGE_LITERAL;
			case PX_MODIFY_STAGE:            return PX_MODIFY_LITERAL;
			case NETEZZA_CONNECTOR_PX_STAGE: return NETEZZA_CONNECTOR_PX_LITERAL;
			case PX_REMOVE_DUPLICATES_STAGE: return PX_REMOVE_DUPLICATES_LITERAL;
			case PX_SORT_STAGE:              return PX_SORT_LITERAL;
			case PX_SURROGATE_KEY_GENERATOR_STAGE: return PX_SURROGATE_KEY_GENERATOR_LITERAL;
			case PX_SWITCH_STAGE:            return PX_SWITCH_LITERAL;
			case PX_COLUMN_GENERATOR_STAGE: return PX_COLUMN_GENERATOR_LITERAL;
			case PX_SEQUENTIAL_FILE_STAGE:  return PX_SEQUENTIAL_FILE_LITERAL;
			case PX_DB2_STAGE:              return PX_DB2_LITERAL;
			case PX_SYBASE_STAGE:           return PX_SYBASE_LITERAL;
			case MQ_SERIES_PX_STAGE:        return MQ_SERIES_PX_LITERAL;
			case ABAP_EXTRACT_PX_STAGE:     return ABAP_EXTRACT_PX_LITERAL;
         case SAP_IDOC_EXTRACT_PX_STAGE: return SAP_IDOC_EXTRACT_PX_LITERAL;
         case SAP_IDOC_LOAD_PX_STAGE:    return SAP_IDOC_LOAD_PX_LITERAL;
         case SAP_IDOC_EXTRACT_CONNECTOR_PX_STAGE: return SAP_IDOC_EXTRACT_CONNECTOR_PX_LITERAL;
         case SAP_IDOC_LOAD_CONNECTOR_PX_STAGE: return SAP_IDOC_LOAD_CONNECTOR_PX_LITERAL;
         case LOCAL_CONTAINER_DEF:       return LOCAL_CONTAINER_DEF_LITERAL;
		}
		return null;	
	}

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	private DSStageTypeEnum(int value, String name) {
		super(value, name);
	}

} // end of class DSStageTypeEnum
