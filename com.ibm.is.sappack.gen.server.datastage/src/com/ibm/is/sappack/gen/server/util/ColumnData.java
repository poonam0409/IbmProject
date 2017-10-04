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
// Module Name : com.ibm.is.sappack.gen.server.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.util;


import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.request.SupportedColumnTypesMap;
import com.ibm.is.sappack.gen.common.util.XMLUtils;


public class ColumnData extends FlowVarData implements Cloneable
{
	// -------------------------------------------------------------------------------------
	//                                       Constants
	// -------------------------------------------------------------------------------------
	private static final int UNICODE_SIZE_MULTIPLIER    =   2;
	private static final int INTEGER_INIT_VALUE         =  -2;


	// -------------------------------------------------------------------------------------
	//                                 Member Variables
	// -------------------------------------------------------------------------------------
	private TableData _TableData;
	private Integer   _DataObjectSourceType;
	private Integer   _DisplayLength;
	private boolean   _IsNullable;
	private boolean   _IsKey;
	private String    _TransformerSrcMapping;
	private String    _SAPRelatedCheckTable;
	private String    _SAPRelatedCTColumn;
	private String	   _SAPRelatedTT;
	private String	   _SAPRelatedTTColumn;
	private String	   _SAPRelatedTTJoinCondition;
	private String 	_SAPRelatedCheckTableType;

	private String	   _SAPDomain;
	private String	   _SAPRelatedDTT;
	private String	   _SAPRelatedDTTColumn;
	private String    _SAPRelatedDTTJoinCondition;
	private String    _SAPDataType;

	private int       _SAPDataTypeDecimals;
	private Integer   _SAPDataTypeLength;
	private int       _Scale;


	static String copyright()
	{ 
		return com.ibm.is.sappack.gen.server.util.Copyright.IBM_COPYRIGHT_SHORT; 
	}   

	public ColumnData(TableData table, String pName, String pDesc)  
	{
		this(table, pName, pDesc, -1);
	}


	public ColumnData(TableData table, String pName, int pLength)  
	{
		this(table, pName, null, pLength);
	}


	public ColumnData(TableData table, String pName, String pDesc, int pLength)  
	{
		super(pName, pDesc, pLength);

		_TableData                 = table;
		_DataObjectSourceType      = null;
		_IsKey                     = false;
		_IsNullable                = Constants.DEFAULT_COLUMN_NULLABLE;
		_TransformerSrcMapping     = null;
		_Scale                     = 0;

		_DisplayLength             = null;
		_SAPDataType               = null;
		_SAPDataTypeDecimals       = INTEGER_INIT_VALUE;
		_SAPDataTypeLength         = null;
		_SAPRelatedCheckTable      = null;
		_SAPRelatedCTColumn        = null;

		_SAPRelatedTT              = null;
		_SAPRelatedCTColumn        = null;
		_SAPRelatedTTJoinCondition = null;
	} // end of ColumnData


	public Object clone()
	{
		Object clonedObject;

		try
		{
			clonedObject = super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			e.printStackTrace();
			clonedObject = null;
		}

		return(clonedObject);
	} // end of clone()


	public Integer getDataObjectSourceType()
	{
		return(_DataObjectSourceType);
	} // end of getDataObjectSourceType


	public Integer getDisplayLength(boolean isHostSystemUnicode)
	{
		Integer curLength;
		Integer retLength;

		// determine the display length
		retLength = null;

		if (_DisplayLength == null)
		{
			curLength = _SAPDataTypeLength;

			if (curLength == null) 
			{
				curLength = getLength();
			}
		}
		else
		{
			curLength = _DisplayLength;
		}

		if (curLength != null)
		{
			int newLength;

			newLength = curLength.intValue();

			if (isHostSystemUnicode) 
			{
				newLength = newLength * UNICODE_SIZE_MULTIPLIER;
			}

			retLength =  new Integer(newLength);
		} // end of if (curLength != null)

			return(retLength);
	}


	public Integer getDisplayLength()
	{
		boolean isUnicode = isUnicode();

		return(getDisplayLength(isUnicode));
	}


	public String getRelatedCheckTable()
	{
		return(_SAPRelatedCheckTable);
	}


	public String getRelatedCheckTableColumn()
	{
		return(_SAPRelatedCTColumn);
	}


	public String getSAPDataType()
	{
		return(_SAPDataType);
	}


	public int getScale()
	{
		return(_Scale);
	}


	public String getTransformerSrcMapping()
	{
		return(_TransformerSrcMapping);
	}


	public String getDomain() {
		return _SAPDomain;
	}
	
	public String getDomainTranslationTable() {
		return _SAPRelatedDTT;
	}
	
	public String getRelatedTranslationTable() {
		return _SAPRelatedTT;
	}


	public String getRelatedTranslationTableColumn() {
		return _SAPRelatedTTColumn;
	}

	public String getRelatedCheckTableType() {
		return _SAPRelatedCheckTableType;
	}

	public String getRelatedTTJoinCondition() {
		return _SAPRelatedTTJoinCondition;
	}
	
	public String getRelatedDTTJoinCondition() {
		return _SAPRelatedDTTJoinCondition;
	}
	
	public String getRelatedDTTColumn() {
		return _SAPRelatedDTTColumn;
	}

	public TableData getTableData() {
		return _TableData;
	}


	public boolean isKeyColumn()
	{
		return(_IsKey);
	}


	public boolean isNullable()
	{
		return(_IsNullable);
	}


	public boolean isSAPColumn()
	{
		// inactivated since an error occurs when using SAP Data Types for job generation
		// (when converting VARCHARS to DECIMAL)
		//      return(_SAPDataType != null);
		return(false);      
	}


	public void setAnnotations(Node pColumnNode)
	{
		List         annotationsList;
		NamedNodeMap curAnnotationNodeMap;
		String       annotationKey;
		String       annotationValue;
		int          annotationListIdx;

		try
		{
			// read column annotations ...
			annotationsList = XMLUtils.getChildNodeList(pColumnNode, TableData.XPATH_EANNOTATIONS_DEFINITION);

			// ... then check and store required annotations
			for (annotationListIdx = 0; annotationListIdx < annotationsList.size(); 
			annotationListIdx ++) 
			{
				curAnnotationNodeMap = ((Node) annotationsList.get(annotationListIdx)).getAttributes();

				annotationKey   = curAnnotationNodeMap.getNamedItem("key").getNodeValue();
				if (curAnnotationNodeMap.getNamedItem("value") == null)
				{
					// key found but not a value
					annotationKey   = "";  
					annotationValue = null;
				}
				else
				{
					annotationValue = curAnnotationNodeMap.getNamedItem("value").getNodeValue();
				} // end of (else) if (curAnnotationNodeMap.getNamedItem("value") == null)

				// - DATA OBJECT SOURCE -
				if (annotationKey.equals(Constants.ANNOT_DATA_OBJECT_SOURCE))
				{
					if (annotationValue.length() > 0)
					{
						if (annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_IDOC))
							_DataObjectSourceType = SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_TYPE;
						else if (annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE))
							_DataObjectSourceType = SupportedColumnTypesMap.COLUMN_TYPE_ENUM_LOGICAL_TABLE;
						else if(annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_JOINED_CHECK_AND_TEXT_TABLE))
							_DataObjectSourceType = SupportedColumnTypesMap.COLUMN_TYPE_ENUM_JOINED_CHECK_AND_TEXT_TABLE;
						else if(annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_CHECK_TABLE))
							_DataObjectSourceType = SupportedColumnTypesMap.COLUMN_TYPE_ENUM_REFERENCE_CHECK_TABLE;
						else if(annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_NON_REFERENCE_CHECK_TABLE))
							_DataObjectSourceType = SupportedColumnTypesMap.COLUMN_TYPE_ENUM_NON_REFERENCE_CHECK_TABLE;
						else if(annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_TEXT_TABLE))
							_DataObjectSourceType = SupportedColumnTypesMap.COLUMN_TYPE_ENUM_REFERENCE_TEXT_TABLE;
						else if(annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_LOAD_STATUS))
							_DataObjectSourceType = SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_LOAD_STATUS;
						else if(annotationValue.equalsIgnoreCase(Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD))
							_DataObjectSourceType = SupportedColumnTypesMap.COLUMN_TYPE_ENUM_CW_TECH_FIELD;
					}
				}
				else // - Column Derivation Expression -
				if(annotationKey.equals(Constants.ANNOT_COLUMN_DERIVATION_EXPRESSION))
				{
					setDerivation(annotationValue);
				}
				else // - Transformer Source mapping -
					if(annotationKey.equals(Constants.ANNOT_TRANSFORMER_SOURCE_MAPPING))
					{
						_TransformerSrcMapping = annotationValue;
					}
					else // - Column Is Unicode -
						if(annotationKey.equals(Constants.ANNOT_COLUMN_IS_UNICODE))
						{
							setIsUnicode(Boolean.valueOf(annotationValue).booleanValue());
						}
						else // - SAP Data Type -
							if(annotationKey.equals(Constants.ANNOT_DATATYPE_DATATYPE))
							{
								_SAPDataType = annotationValue;
							}
							else // - SAP Data Type Length -
								if(annotationKey.equals(Constants.ANNOT_DATATYPE_LENGTH))
								{
									_SAPDataTypeLength = convertLength(Integer.parseInt(annotationValue));
								}
								else // - SAP Data Type Decimals -
									if(annotationKey.equals(Constants.ANNOT_DATATYPE_DECIMALS))
									{
										_SAPDataTypeDecimals = Integer.parseInt(annotationValue);
									}
									else // - SAP Display Length -
										if(annotationKey.equals(Constants.ANNOT_DATATYPE_DISPLAY_LENGTH))
										{
											_DisplayLength = convertLength(Integer.parseInt(annotationValue));
										}
										else // - SAP Related CheckTable -
											if(annotationKey.equals(Constants.ANNOT_RELATED_CHECKTABLE))
											{
												_SAPRelatedCheckTable = annotationValue;
											}
											else // - SAP Related CheckTable Column -
												if(annotationKey.equals(Constants.ANNOT_RELATED_CHECKTABLE_COLUMN))
												{
													_SAPRelatedCTColumn = annotationValue;
												}
												else // - SAP Related Translation Table -
													if(annotationKey.equals(Constants.ANNOT_RELATED_TT))
													{
														_SAPRelatedTT = annotationValue;
													}
													else // - SAP Related Translation Table Column -
														if(annotationKey.equals(Constants.ANNOT_RELATED_TT_COLUMN))
														{
															_SAPRelatedTTColumn = annotationValue;
														}
														else // - SAP Related Translation Table Join Condition -
															if(annotationKey.equals(Constants.ANNOT_RELATED_TT_JOIN))
															{
																_SAPRelatedTTJoinCondition = annotationValue;
															}
														else // - SAP Related Domain -
															if(annotationKey.equals(Constants.ANNOT_DATATYPE_DOMAIN))
															{
																_SAPDomain = annotationValue;
															}
														else // - SAP Related Domain translation table -
															if(annotationKey.equals(Constants.ANNOT_DOMAIN_TRANSLATION_TABLE))
															{
																_SAPRelatedDTT = annotationValue;
															}
														else // - SAP Related Domain translation table join condition -
															if(annotationKey.equals(Constants.ANNOT_DOMAIN_TRANSLATION_TABLE_JOIN_CONDITION))
															{
																_SAPRelatedDTTJoinCondition = annotationValue;
															}
														else // - SAP Related Domain translation table column -
															if(annotationKey.equals(Constants.ANNOT_DOMAIN_TRANSLATION_TABLE_COLUMN))
															{
																_SAPRelatedDTTColumn = annotationValue;
															}
														else // - SAP Related check table type - ReferenceCheckTable or NonReferenceCheckTable 
															if(annotationKey.equals(Constants.ANNOT_SAPPACK_CHECK_TBL_DATA_OBJECT_SOURCE))
															{
																_SAPRelatedCheckTableType = annotationValue;
															}
				
			} // end of for (annotationListIdx = 0; ... annotationListIdx ++)
		} // end of try
		catch(Exception pExcpt)
		{
			/*
         if (TraceLogger.isTraceEnabled)
         {
            TraceLogger.traceException(pExcpt);
         }
			 */         
			pExcpt.printStackTrace();
		}

		// check the consistency of the column data
		// inactivated since an error occurs when using SAP Data Types for job generation
		// (when converting VARCHARS to DECIMAL)
		//      validate();
	} // end of setAnnotations()


	public void setDataObjectSourceType(Integer dataObjectSourceType)
	{
		// validate the passed type ...
		SupportedColumnTypesMap.validateType(dataObjectSourceType);

		_DataObjectSourceType = dataObjectSourceType;
	} // end of setDataObjectSourceType()


	public void setIsKeyColumn(boolean isKey)
	{
		_IsKey = isKey;

		// key columns must not be 'nullable'
		if (_IsKey)
		{
			_IsNullable = false;
		}
	}


	public void setScale(int scale)
	{
		_Scale = scale;
	}


	public void setTransformerSrcMapping(String srcMapping)
	{
		_TransformerSrcMapping = srcMapping;
	}


	public void setIsNullable(boolean isNullable)
	{
		// 'nullable' of key columns cannot be changed
		if (!_IsKey)
		{
			_IsNullable = isNullable;
		}
	}


	public String toString()
	{
		return(getName() + " - "+ getType());
	}


	protected void validate()
	{
		if (_DataObjectSourceType != null)
		{
			if (_DataObjectSourceType.equals(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_TYPE)                    ||
					_DataObjectSourceType.equals(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_LOGICAL_TABLE)                ||
					_DataObjectSourceType.equals(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_JOINED_CHECK_AND_TEXT_TABLE) || 
					_DataObjectSourceType.equals(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_REFERENCE_CHECK_TABLE) ||
					_DataObjectSourceType.equals(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_NON_REFERENCE_CHECK_TABLE) ||
					_DataObjectSourceType.equals(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_REFERENCE_TEXT_TABLE)			)
			{
				// check some (SAP) attributes to be exist
				if (_SAPDataType         == null               || 
						_SAPDataTypeDecimals == INTEGER_INIT_VALUE || 
						_SAPDataTypeLength   == null               || 
						_DisplayLength       == null)
				{
					throw new IllegalArgumentException("SAP Column '" + getName() + "' found but additional SAP data is missing.");
				} // end of if (_SAPDataType == null || ... || _DisplayLength       == null) 

				// update member variables
				_Scale = _SAPDataTypeDecimals;
				setType(_SAPDataType);
				if (_SAPDataType.startsWith("INT") || 
						_SAPDataType.equals("PREC")) 
				{
					setLength(_SAPDataTypeLength);
				}
			} // end of if (_DataObjectSourceType == SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_TYPE || ...
		} // end of if (_DataObjectSourceType != null)
	} // end of validate()

} // end of class ColumnData
