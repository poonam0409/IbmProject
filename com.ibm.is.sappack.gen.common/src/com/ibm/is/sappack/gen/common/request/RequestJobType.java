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
// Module Name : com.ibm.is.sappack.gen.common.request
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.common.request;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.FileStageData;
import com.ibm.is.sappack.gen.common.NetezzaStageData;
import com.ibm.is.sappack.gen.common.ODBCStageData;
import com.ibm.is.sappack.gen.common.PersistenceData;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.XMLUtils;


public abstract class RequestJobType {
	// -------------------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------------------
	public  static final int    TYPE_ABAP_EXTRACT_ID       = 1;
	        static final String TYPE_ABAP_EXTRACT          = "ABAPExtract";      //$NON-NLS-1$
	public  static final int    TYPE_IDOC_EXTRACT_ID       = 2;
	        static final String TYPE_IDOC_EXTRACT          = "IDocExtract";      //$NON-NLS-1$
	public  static final int    TYPE_IDOC_LOAD_ID          = 3;
	        static final String TYPE_IDOC_LOAD             = "IDocLoad";         //$NON-NLS-1$
	public  static final int    TYPE_MOVEMENT_ID           = 4;
	        static final String TYPE_MOVEMENT              = "Movement";         //$NON-NLS-1$
	public  static final int    TYPE_MIH_LOAD_ID           = 5;
	        static final String TYPE_MIH_LOAD              = "MIHLoad";          //$NON-NLS-1$
	public  static final int    TYPE_IDOC_MIH_LOAD_ID      = 6;
	        static final String TYPE_IDOC_MIH_LOAD         = "IDocMIHLoad";      //$NON-NLS-1$
	public  static final int    TYPE_MIH_CODETABLE_LOAD_ID = 7;
	        static final String TYPE_MIH_CODETABLE_LOAD    = "MIHCodeTableLoad"; //$NON-NLS-1$
	public  static final int    TYPE_TRANSCODING_ID 	    = 8;
	        static final String TYPE_TRANSCODING    	    = "Transcoding"; //$NON-NLS-1$  

	public  static final String XML_TAG_JOB_TYPE           = "JobType"; //$NON-NLS-1$
	        static final String XML_ATTRIB_TYPE            = "type"; //$NON-NLS-1$
	        static final String XML_TAG_PHYSICAL_MODEL     = "PhysicalModel"; //$NON-NLS-1$
	        static final String XML_ATTRIB_PHY_MODEL_ID    = "id"; //$NON-NLS-1$
	        static final String XML_TAG_SOURCE             = "Source"; //$NON-NLS-1$
	        static final String XML_TAG_TARGET             = "Target"; //$NON-NLS-1$
	        static final String XML_TAG_JOB_NAME           = "DSJobname"; //$NON-NLS-1$
	        static final String XML_ATTRIB_JOB_NAME        = "name"; //$NON-NLS-1$
	        static final String XML_ATTRIB_JOB_OVERWRITE   = "overwrite"; //$NON-NLS-1$
	        static final String XML_TAG_SUPPORTED_TYPES    = "SupportedTypes"; //$NON-NLS-1$

	private static final String XML_TAG_COLUMN_DERIVATIONS = "ColumnDerivations"; //$NON-NLS-1$
	private static final String XML_TAG_COLUMN_DERIVATION  = "ColumnDerivation";  //$NON-NLS-1$
	private static final String XML_ATTRIB_COL_DERIV_NAME  = "name";              //$NON-NLS-1$
	private static final String XML_ATTRIB_COL_DERIV_TYPE  = "type";              //$NON-NLS-1$

	private static final String XML_TAG_DERIVATION_EXCEPTIONS = "DerivationExceptions"; //$NON-NLS-1$

	// -------------------------------------------------------------------------------------
	// Member Variables
	// -------------------------------------------------------------------------------------
	private PersistenceData         _PersistenceDataSrc;
	private PersistenceData         _PersistenceDataTrg;
	private SAPSystemData           _SAPSystemData;
	private String                  _JobName;
	private boolean                 _DoOverwriteExistingJob;
	private Map                     _ColumnDerivationMap;
	private Map                     _DerivationExceptionsMap;
	private SupportedColumnTypesMap _SupportedColumnTypesMap;
	private SupportedTableTypesMap  _SupportedTableTypesMap;
	

	
	static String copyright() {
		return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT;
	}


	public RequestJobType(String parJobName) {
		if (parJobName == null || parJobName.length() == 0) {
			throw new IllegalArgumentException("Jobname was not specified.");
		}

		_ColumnDerivationMap     = new HashMap();
		_DerivationExceptionsMap = new HashMap();
		_DoOverwriteExistingJob  = false;
		_JobName                 = parJobName;
		_SAPSystemData           = null;
		_SupportedColumnTypesMap = new SupportedColumnTypesMap();
		_SupportedTableTypesMap  = new SupportedTableTypesMap();

		// set supported table and column types
		setSupportedTypes(_SupportedColumnTypesMap, _SupportedTableTypesMap);
	} // end of RequestJobType()


	static Map xmlToMap(Node mapNode) {
		Map result = null;
		if (mapNode  != null) {
			result = new HashMap();
			NodeList tmpChildNodeList     = mapNode.getChildNodes();
			for (int vNodeIdx = 0; vNodeIdx < tmpChildNodeList.getLength(); vNodeIdx++) {
				Node curNode = tmpChildNodeList.item(vNodeIdx);
				String key = XMLUtils.getNodeAttributeValue(curNode, XML_ATTRIB_COL_DERIV_TYPE);
				String value = XMLUtils.getNodeAttributeValue(curNode, XML_ATTRIB_COL_DERIV_NAME);

				result.put(key, value);
			}
		} // end of if (childNode != null)
		return result;
	}
	
	RequestJobType(Node parJobTypeNode) {
		Node     childNode;
		String   tmpValue;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++
		// set Column Derivations
		// +++++++++++++++++++++++++++++++++++++++++++++++++++
		childNode = XMLUtils.getChildNode(parJobTypeNode, XML_TAG_COLUMN_DERIVATIONS);
		if (childNode != null) {
			_ColumnDerivationMap = xmlToMap(childNode);
		} // end of if (childNode != null)
		
		childNode = XMLUtils.getChildNode(parJobTypeNode, XML_TAG_DERIVATION_EXCEPTIONS);
		if (childNode != null) {
			_DerivationExceptionsMap = xmlToMap(childNode);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++
		// set Job Name
		// +++++++++++++++++++++++++++++++++++++++++++++++++++
		childNode = XMLUtils.getChildNode(parJobTypeNode, XML_TAG_JOB_NAME);
		if (childNode != null) {
			_JobName = XMLUtils.getNodeAttributeValue(childNode, XML_ATTRIB_JOB_NAME);
			tmpValue = XMLUtils.getNodeAttributeValue(childNode, XML_ATTRIB_JOB_OVERWRITE);
			if (tmpValue != null) {
				_DoOverwriteExistingJob = Boolean.valueOf(tmpValue).booleanValue();
			}
		} // end of if (childNode != null)

		// +++++++++++++++++++++++++++++++++++++++++++++++++++
		// set Supported Table and Column Types
		// +++++++++++++++++++++++++++++++++++++++++++++++++++
		childNode = XMLUtils.getChildNode(parJobTypeNode, XML_TAG_SUPPORTED_TYPES);
		if (childNode != null) {
			// ==> Supported Table Types and Column Types
			childNode.getChildNodes();
			_SupportedColumnTypesMap = new SupportedColumnTypesMap(childNode);
			_SupportedTableTypesMap  = new SupportedTableTypesMap(childNode);
		} // end of if (childNode != null)

	} // end of RequestJobType()


	public void addColumDerivation(String parType, String parName) {
		_ColumnDerivationMap.put(parType, parName);
	}


	void checkParamExists(Object pObjectToCheck, String pParamName) throws IllegalArgumentException {
		boolean vCheckSuccessful = true;

		// set 'check unsuccessful' if there is no object ...
		if (pObjectToCheck == null) {
			vCheckSuccessful = false;
		} // end of if (pObjectToCheck == null)

		// STRING OBJECTS ONLY: set 'check unsuccessful' if the String length is 0 ...
		if (pObjectToCheck instanceof String) {
			if (((String) pObjectToCheck).length() == 0) {
				vCheckSuccessful = false;
			} // end of if (((String)pObjectToCheck).length() == 0)
		}
		else {
			// OBJECTS ARRAYS ONLY: set 'check unsuccessful' if the array is empty ...
			if (pObjectToCheck instanceof Object[]) {
				if (((Object[]) pObjectToCheck).length == 0) {
					vCheckSuccessful = false;
				}
			}
			if (pObjectToCheck instanceof Map) {
				if (((Map) pObjectToCheck).size() == 0) {
					vCheckSuccessful = false;
				}
			}
			if (pObjectToCheck instanceof List) {
				if (((List) pObjectToCheck).size() == 0) {
					vCheckSuccessful = false;
				}
			} // end of if (pObjectToCheck instanceof Object[])
		} // end of if (pObjectToCheck instanceof String)

		// throw exception if check was not successful
		if (!vCheckSuccessful) {
			throw new IllegalArgumentException("Missing or invalid parameter: " + pParamName);
		} // end of if (!vCheckSuccessful)
	} // end of checkParamExists()


	public static RequestJobType createJobType(Node parJobTypeNode) {
		RequestJobType newJobType;
		String vType;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("JobType node = " + parJobTypeNode);
		}

		vType = XMLUtils.getNodeAttributeValue(parJobTypeNode, "type");

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINER, "type = " + vType);
		}

		// dependent on the type create appropriate JobType instances
		if (vType.equals(RequestJobType.TYPE_ABAP_EXTRACT)) {
			newJobType = new RequestJobTypeABAPExtract(parJobTypeNode);
		}
		else if (vType.equals(RequestJobType.TYPE_IDOC_LOAD)) {
			newJobType = new RequestJobTypeIDocLoad(parJobTypeNode);
		}
		else if (vType.equals(RequestJobType.TYPE_IDOC_EXTRACT)) {
			newJobType = new RequestJobTypeIDocExtract(parJobTypeNode);
		}
		else if (vType.equals(RequestJobType.TYPE_MOVEMENT)) {
			newJobType = new RequestJobTypeMovement(parJobTypeNode);
		}
		else if (vType.equals(RequestJobType.TYPE_TRANSCODING)) {
			newJobType = new RequestJobTypeTranscoding(parJobTypeNode);
		}
		
		else if (vType.equals(RequestJobType.TYPE_MIH_LOAD)) {
			newJobType = new RequestJobTypeMIHLoad(parJobTypeNode);
		}
		else if (vType.equals(RequestJobType.TYPE_IDOC_MIH_LOAD)) {
			newJobType = new RequestJobTypeIDocMIHLoad(parJobTypeNode);
		}
      else if (vType.equals(RequestJobType.TYPE_MIH_CODETABLE_LOAD)) {
         newJobType = new RequestJobTypeMIHCodeTableLoad(parJobTypeNode);
      }
		else {
			newJobType = null;

			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINE, "Warning: Unsupported JobType '" + vType + "' found. Ignored.");
			}
		}

		if (TraceLogger.isTraceEnabled()) {
			if (newJobType == null) {
				TraceLogger.exit("null");
			}
			else {
				TraceLogger.exit(newJobType.getJobTypeAsString());
			}
		}

		return (newJobType);
	} // end of createJobType()


	public boolean doProcessColumn(Integer parColDataObjSourceType) {
		boolean processColumn;
		
		// check if all column types are allowed ...
		processColumn = _SupportedColumnTypesMap.contains(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_ALL_TYPES);

		// but if not ...
		if (!processColumn) {
			// ==> perform specific column type check !!
			processColumn = _SupportedColumnTypesMap.contains(parColDataObjSourceType); 
		}

		return(processColumn);
	}


	public boolean doProcessTable(Integer parTableType) {
		boolean processTable;
		
		// check if all table types are allowed ...
		processTable = _SupportedTableTypesMap.contains(SupportedTableTypesMap.TABLE_TYPE_ENUM_ALL_TYPES);

		// but if not ...
		if (!processTable) {
			// ==> perform specific table type check !!
			processTable = _SupportedTableTypesMap.contains(parTableType); 
		}

		return(processTable);
	}


	public Map getColumnDerivationMap() {
		return (_ColumnDerivationMap);
	}
	
	public Map getDerivationExceptionsMap() {
		return _DerivationExceptionsMap;
	}
	
	public void setDerivationExceptionsMap(Map derivationExceptions) {
		this._DerivationExceptionsMap = derivationExceptions;
	}
	
	private String getDerivationExceptionsXML() {
		return mapToXML(_DerivationExceptionsMap, XML_TAG_DERIVATION_EXCEPTIONS, false);
	}


	private String getColumnDerivationsXML() {
		return mapToXML(_ColumnDerivationMap, XML_TAG_COLUMN_DERIVATIONS, true);
	}
	
	private static String mapToXML(Map m, String parentNodeName, boolean makeKeyUppercase) {
		StringBuffer buf;
		Map.Entry mapEntry;
		Iterator entryIter;

		buf = new StringBuffer();

		buf.append("<");
		buf.append(parentNodeName);
		buf.append(">");

		entryIter = m.entrySet().iterator();
		while (entryIter.hasNext()) {
			buf.append("<");
			buf.append(XML_TAG_COLUMN_DERIVATION);

			mapEntry = (Map.Entry) entryIter.next();
			String key = ((String) mapEntry.getKey());
			if (makeKeyUppercase) {
				key = key.toUpperCase();
			}
			buf.append(XMLUtils.createAttribPairString(XML_ATTRIB_COL_DERIV_TYPE, key)); 
			buf.append(XMLUtils.createAttribPairString(XML_ATTRIB_COL_DERIV_NAME, 
			                        (String) mapEntry.getValue()));
			buf.append(" />");
		}

		buf.append("</");
		buf.append(parentNodeName);
		buf.append(">");

		return (buf.toString());
	}


	public String getJobName() {
      return (_JobName);
   }


   private String getJobNameXML() {
		StringBuffer xmlBuf;

		xmlBuf = new StringBuffer();

		xmlBuf.append("<");
		xmlBuf.append(XML_TAG_JOB_NAME);

		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_JOB_NAME, _JobName));
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_JOB_OVERWRITE, String.valueOf(_DoOverwriteExistingJob)));
		xmlBuf.append(" />");

		return (xmlBuf.toString());
	}


	String getJobTypeData() {
		StringBuffer xmlBuf;

		xmlBuf = new StringBuffer();
		xmlBuf.append(getSupportedTypesXML());
		xmlBuf.append(getJobNameXML());
		xmlBuf.append(getColumnDerivationsXML());
		xmlBuf.append(getDerivationExceptionsXML());

		return (xmlBuf.toString());
	}


	public boolean doOverwriteJob() {
		return (_DoOverwriteExistingJob);
	}


	PersistenceData getPersistenceDataFromNode(Node parNode) {
		PersistenceData retPersitenceData;
		Node            curNode;
		NodeList        childNodeList;
		int             vNodeListIdx;

		childNodeList = parNode.getChildNodes();
		retPersitenceData = null;
		vNodeListIdx = 0;
		while (vNodeListIdx < childNodeList.getLength() && retPersitenceData == null) {
			curNode = childNodeList.item(vNodeListIdx);

			// what kind of persistence stage
			if (curNode.getNodeName().equals(ODBCStageData.XML_TAG_NAME_ODBC_STAGE)) {
				// ==> ODBC Stage
				retPersitenceData = new ODBCStageData(curNode);
			} // end of if (curNode.getNodeName().equals(ODBCStageData.XML_TAG_NAME))

			if (curNode.getNodeName().equals(FileStageData.XML_TAG_NAME_FILE_STAGE)) {
				// ==> File Stage
				retPersitenceData = new FileStageData(curNode);
			} // end of if (curNode.getNodeName().equals(FileStageData.XML_TAG_NAME))

			if (curNode.getNodeName().equals(NetezzaStageData.XML_TAG_NAME_NETEZZA_STAGE)) {
				// ==> Netezza Stage
				retPersitenceData = new NetezzaStageData(curNode);
			} // end of if (curNode.getNodeName().equals(NetezzaStageData.XML_TAG_NAME_NETEZZA_STAGE)) {

			vNodeListIdx++;
		} // end of while(vNodeListIdx < childNodeList.getLength() && ...

		return (retPersitenceData);
	}


	protected PersistenceData getPersistenceDataSrc() {
		return (_PersistenceDataSrc);
	}


	protected PersistenceData getPersistenceDataTrg() {
		return (_PersistenceDataTrg);
	}


	String getPhysicalModelXML(String parPhysModelId, String parSourceData) {
		StringBuffer physModelXMLBuf;

		physModelXMLBuf = new StringBuffer();

		physModelXMLBuf.append(parSourceData);
		physModelXMLBuf.append("<");
		physModelXMLBuf.append(XML_TAG_PHYSICAL_MODEL);
		physModelXMLBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_PHY_MODEL_ID, parPhysModelId));
		physModelXMLBuf.append("/>");

		return (physModelXMLBuf.toString());
	}


	String getSAPSystemXML() {
		String sapSystemXML;

		if (_SAPSystemData == null) {
			sapSystemXML = Constants.EMPTY_STRING;
		}
		else {
			sapSystemXML = _SAPSystemData.toXML();
		}

		return (sapSystemXML);
	}


	public SAPSystemData getSAPSystem() {
		return (_SAPSystemData);
	}


	String getSourceData() {
		String persistDataAsString;

		if (_PersistenceDataSrc == null) {
			persistDataAsString = Constants.EMPTY_STRING;
		}
		else {
			persistDataAsString = _PersistenceDataSrc.toXML();
		}

		return (persistDataAsString);
	}

	public SupportedColumnTypesMap getSupportedColumnTypes() {
		return (_SupportedColumnTypesMap);
	}

	public SupportedTableTypesMap getSupportedTableTypes() {
		return (_SupportedTableTypesMap);
	}

	private String getSupportedTypesXML() {
		StringBuffer xmlBuf;

		xmlBuf = new StringBuffer();

		xmlBuf.append("<");
		xmlBuf.append(XML_TAG_SUPPORTED_TYPES);
		xmlBuf.append(">");

		xmlBuf.append(_SupportedColumnTypesMap.toXML());
		xmlBuf.append(_SupportedTableTypesMap.toXML());

		xmlBuf.append("</");
		xmlBuf.append(XML_TAG_SUPPORTED_TYPES);
		xmlBuf.append(">");

		return (xmlBuf.toString());
	}

	String getTargetData() {
		String persistDataAsString;

		if (_PersistenceDataTrg == null) {
			persistDataAsString = Constants.EMPTY_STRING;
		}
		else {
			persistDataAsString = _PersistenceDataTrg.toXML();
		}

		return (persistDataAsString);
	}


	public void setColumnDerivationMap(Map columnDerivationMap) {
		_ColumnDerivationMap = columnDerivationMap;
	}


	public void setDoOverwriteJob(boolean overwrite) {
		_DoOverwriteExistingJob = overwrite;
	}


	public void setJobName(String parJobName) {
		_JobName = parJobName;
	}


	protected void setPersistenceDataSrc(PersistenceData parPersistData) {
		_PersistenceDataSrc = parPersistData;
	}


	protected void setPersistenceDataTrg(PersistenceData parPersistData) {
		_PersistenceDataTrg = parPersistData;
	}


	void setSAPSystem(Node parSAPSystemNode) {
		_SAPSystemData = new SAPSystemData(parSAPSystemNode);
	}


	public void setSAPSystem(SAPSystemData parSAPSystemData) {
		_SAPSystemData = parSAPSystemData;
	}


	public String toString() {
		StringBuffer traceStringBuf;

		traceStringBuf = new StringBuffer();
		traceStringBuf.append("JobType: ");
		traceStringBuf.append(String.valueOf(getJobTypeAsString()));
		traceStringBuf.append(" - JobName: ");
		traceStringBuf.append(_JobName);
		traceStringBuf.append(" - overwrite: ");
		traceStringBuf.append(String.valueOf(_DoOverwriteExistingJob));
		traceStringBuf.append(Constants.NEWLINE);
		traceStringBuf.append("Column Derivations: ");
		traceStringBuf.append(_ColumnDerivationMap);
		traceStringBuf.append(Constants.NEWLINE);
		traceStringBuf.append("Column Derivations Exceptions: ");
		traceStringBuf.append(_DerivationExceptionsMap);
		traceStringBuf.append(Constants.NEWLINE);
		traceStringBuf.append("SAP System: ");
		if (_SAPSystemData == null) {
			traceStringBuf.append("null");
		}
		else {
			traceStringBuf.append(_SAPSystemData.toString());
		}
		traceStringBuf.append(Constants.NEWLINE);
		traceStringBuf.append("Source Persistence: ");
		if (_PersistenceDataSrc == null) {
			traceStringBuf.append("null");
		}
		else {
			traceStringBuf.append(_PersistenceDataSrc.toString());
		}
		traceStringBuf.append(Constants.NEWLINE);
		traceStringBuf.append("Target Persistence: ");
		if (_PersistenceDataTrg == null) {
			traceStringBuf.append("null");
		}
		else {
			traceStringBuf.append(_PersistenceDataTrg.toString());
		}
		traceStringBuf.append(Constants.NEWLINE);

		traceStringBuf.append("Supported Table Types: ");
		traceStringBuf.append(_SupportedTableTypesMap);
		traceStringBuf.append(Constants.NEWLINE);
		traceStringBuf.append("Supported Column Types: ");
		traceStringBuf.append(_SupportedColumnTypesMap);
		traceStringBuf.append(Constants.NEWLINE);

		return (traceStringBuf.toString());
	}


	public String toXML() {
		String additionalAttribs;
		StringBuffer xmlBuf;

		xmlBuf = new StringBuffer();

		xmlBuf.append("<");
		xmlBuf.append(XML_TAG_JOB_TYPE);
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_TYPE, getJobTypeAsString()));
		additionalAttribs = getJobTypeAttribs();
		if (additionalAttribs != null) {
			xmlBuf.append(additionalAttribs);
		}
		xmlBuf.append(">");
		xmlBuf.append(getJobTypeData());

		xmlBuf.append("<");
		xmlBuf.append(XML_TAG_SOURCE);
		xmlBuf.append(">");
		xmlBuf.append(getSourceData());
		xmlBuf.append("</");
		xmlBuf.append(XML_TAG_SOURCE);
		xmlBuf.append(">");

		xmlBuf.append("<");
		xmlBuf.append(XML_TAG_TARGET);
		xmlBuf.append(">");
		xmlBuf.append(getTargetData());
		xmlBuf.append("</");
		xmlBuf.append(XML_TAG_TARGET);
		xmlBuf.append(">");

		xmlBuf.append("</");
		xmlBuf.append(XML_TAG_JOB_TYPE);
		xmlBuf.append(">");

		return (xmlBuf.toString());
	}


   public void setSupportedColumnTypes(SupportedColumnTypesMap parColumnTypes) {
      if (parColumnTypes != null)
      {
         _SupportedColumnTypesMap = new SupportedColumnTypesMap(parColumnTypes);
      }
    } // end of setSupportedColumnTypes()
   

	public void setSupportedTableTypes(SupportedTableTypesMap parTableTypes) {
	   if (parTableTypes != null)
	   {
	      _SupportedTableTypesMap = new SupportedTableTypesMap(parTableTypes);
	   }
	 } // end of setSupportedTableTypes()
	


	// -------------------------------------------------------------------------------------
	// Abstract Methods
	// -------------------------------------------------------------------------------------
	abstract void setSupportedTypes(SupportedColumnTypesMap parColumTypesMap, SupportedTableTypesMap parTableTypesMap);

	abstract String getJobTypeAttribs();

	abstract public int getJobType();

	abstract public String getJobTypeAsString();

	abstract public void validate() throws IllegalArgumentException;

	abstract public String getDisplayModelId();

} // end of class RequestJobType
