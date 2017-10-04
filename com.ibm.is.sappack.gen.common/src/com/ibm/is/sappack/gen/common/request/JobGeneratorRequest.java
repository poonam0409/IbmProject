//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2012                                              
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


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.common.util.XMLUtils;


public final class JobGeneratorRequest extends RequestBase {
	public  static final String XML_TAG_JOB_CONFIG                = "JobConfig";
   public  static final String XML_ATTRIB_CREATE_V7_STAGE        = "createV7Stage";
	public  static final String XML_TAG_DS_PROJECT                = "DSProject";
	public  static final String XML_ATTRIB_PROJECT_NAME           = "name";
	public  static final String XML_ATTRIB_DSHOST_NAME            = "dshost";
   public  static final String XML_ATTRIB_DSSRVR_RPC_PORT        = "dsSrvrRPCPort";
   public  static final String XML_TAG_DS_PROJECT_TARGET_FOLDER  = "TargetFolder";
   public  static final String XML_ATTRIB_TRG_FOLDER_FOLDER      = "folder";
	public  static final String XML_ATTRIB_PROJECT_CONT_ON_ERR    = "continueOnError";
	public  static final String XML_TAG_JOB_PARAMS                = "JobParams";
	public  static final String XML_TAG_JOB_TYPES                 = "JobTypes";
   private static final String XML_TAG_JOB_DESC                  = "DSJobDescription";
   private static final String UNIX_NEWLINE                      = "\n";
	// private static final String XML_TAG_JOB_CREATION_REQUEST = "JobCreationRequest";

   private String                    _DSJobTargetFolder;
	private String                    _DSProjectName;
	private String                    _DSHostName;
   private String                    _JobLongDescription;
   private Integer                   _DSServerRPCPort;
	private List<RequestJobType>      _JobTypeList;
	private Map<String, JobParamData> _JobParamsMap;
	private boolean                   _DoContinueOnError;
   private boolean                   _DoCreateV7Stage;



	static String copyright() {
		return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT;
	}


	public JobGeneratorRequest() {
		super();
		_JobParamsMap       = new HashMap<String, JobParamData>();
		_JobTypeList        = new ArrayList<RequestJobType>();
		_DoContinueOnError  = false;
		_DSJobTargetFolder  = null;
		_DSProjectName      = null;
		_DSServerRPCPort    = null;
      _JobLongDescription = null;
      _DoCreateV7Stage    = false;
	}

	public void initConfiguration(Element configNode) throws JobGeneratorException {
		TraceLogger.entry();
		super.initConfiguration(configNode);

		NodeList nl = configNode.getElementsByTagName(XML_TAG_JOB_REQUEST_SETTINGS);
		if (nl.getLength() != 1) {
			throw new JobGeneratorException("110100E", Constants.NO_PARAMS);
		}
		configNode = (Element) nl.item(0);

		// get the nodes to be processed ...
		NodeList vConfigNodes = configNode.getChildNodes();

		for (int vNodeListIdx = 0; vNodeListIdx < vConfigNodes.getLength(); vNodeListIdx++) {
			Node curNode = vConfigNodes.item(vNodeListIdx);
			if (curNode.getNodeName().equals(JobGeneratorRequest.XML_TAG_JOB_CONFIG)) {
            _DoCreateV7Stage = Boolean.valueOf(getNodeAttributeValue(curNode, 
                                                                     XML_ATTRIB_CREATE_V7_STAGE)).booleanValue();

				processJobConfigNodes(curNode.getChildNodes());
			}
			else if (curNode.getNodeName().equals(JobGeneratorRequest.XML_TAG_JOB_TYPES)) {
				processJobTypeNodes(curNode.getChildNodes());
			} // end of if (curNode.getNodeName().equals(JobGeneratorRequest.XML_TAG_JOB_CONFIG))
		} // end of for(vNodeListIdx = 0; vNodeListIdx < vConfigNodes.getLength(); vNodeListIdx ++)
		TraceLogger.exit();
	}

	private void processJobTypeNodes(NodeList parJobTypeNodes) {
		RequestJobType jobType;
		Node typeNode;
		int vNodeListdx;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("JobType node count = " + parJobTypeNodes.getLength());
		}

		for (vNodeListdx = 0; vNodeListdx < parJobTypeNodes.getLength(); vNodeListdx++) {
			typeNode = parJobTypeNodes.item(vNodeListdx);

			// create appropriate JobType instances
			jobType = RequestJobType.createJobType(typeNode);

			if (TraceLogger.isTraceEnabled() && jobType != null) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, jobType.getJobTypeAsString());
			}

			if (jobType != null) {
				// add JobType to map
				_JobTypeList.add(jobType);
			} // end of if (jobType != null)
		} // end of for(vNodeListdx = 0; vNodeListdx < parJobTypeNodes.getLength(); vNodeListdx ++)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of processJobTypeNodes(curNode.getChildNodes())

	private void processJobConfigNodes(NodeList parConfigNodes) {
		Node    vConfigNode;
		Node    vTargetFolderNode; 
		String  vTmpIntString;
		int     vNodeListdx;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("config node count = " + parConfigNodes.getLength());
		}

		for (vNodeListdx = 0; vNodeListdx < parConfigNodes.getLength(); vNodeListdx++) {
			vConfigNode = parConfigNodes.item(vNodeListdx);
			if (vConfigNode.getNodeName().equals(JobGeneratorRequest.XML_TAG_DS_PROJECT)) {
				// ---------------------------------- DataStage Project Name --------------------------------
				_DSProjectName = getNodeAttributeValue(vConfigNode, JobGeneratorRequest.XML_ATTRIB_PROJECT_NAME);
				
            // ----------------------------- DataStage DS Host name and RPC port ------------------------
				_DSHostName = getNodeAttributeValue(vConfigNode, XML_ATTRIB_DSHOST_NAME);
				vTmpIntString = getNodeAttributeValue(vConfigNode, XML_ATTRIB_DSSRVR_RPC_PORT);
				if (vTmpIntString != null) {
	            _DSServerRPCPort = Integer.valueOf(vTmpIntString);
				}

				// ------------------------------ DataStage Job Subfolder Name ----------------------------
		      vTargetFolderNode = XMLUtils.getChildNode(vConfigNode, XML_TAG_DS_PROJECT_TARGET_FOLDER);
		      if (vTargetFolderNode != null) {
	            // ------------------------------ DataStage Job Target folder Name ----------------------------
	            _DSJobTargetFolder = getNodeAttributeValue(vTargetFolderNode, XML_ATTRIB_TRG_FOLDER_FOLDER);
		      } // end of if (vTargetFolderNode != null)

				// ------------------------------ Continue Job Generation On Error ----------------------------
				_DoContinueOnError = Boolean.valueOf(getNodeAttributeValue(vConfigNode, 
				                                     JobGeneratorRequest.XML_ATTRIB_PROJECT_CONT_ON_ERR)).booleanValue();
				
		      // +++++++++++++++++++++++++++++++++++++++++++++++++++
		      // set 
		      // +++++++++++++++++++++++++++++++++++++++++++++++++++
			}
			else if (vConfigNode.getNodeName().equals(JobGeneratorRequest.XML_TAG_JOB_PARAMS)) {
				processJobParamNodes(vConfigNode.getChildNodes());
			}
         else if (vConfigNode.getNodeName().equals(JobGeneratorRequest.XML_TAG_JOB_DESC)) {
            // --------------------------------- DataStage Job Description -------------------------------
            _JobLongDescription = XMLUtils.getNodeTextValue(vConfigNode);
         }
		} // end of for(vNodeListdx = 0; vNodeListdx < parConfigNodes.getLength(); vNodeListdx ++)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}

	} // end of processJobConfigNodes(curNode.getChildNodes())

	private void processJobParamNodes(NodeList parParamNodes) {
		Node curJobParamNode;
		int vNodeListdx;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("JobParam node count = " + parParamNodes.getLength());
		}

		for (vNodeListdx = 0; vNodeListdx < parParamNodes.getLength(); vNodeListdx++) {
			curJobParamNode = parParamNodes.item(vNodeListdx);

         JobParamData jpd = new JobParamData(curJobParamNode);

			TraceLogger.trace(TraceLogger.LEVEL_FINER, "Found job parameter " + jpd);
			this.addJobParameter(jpd);

		} // end of for(vNodeListdx = 0; vNodeListdx < parParamNodes.getLength(); vNodeListdx ++)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of processJobParamNodes(curNode.getChildNodes())

	public void addJobParameter(JobParamData parJobParam) {
		if (parJobParam != null) {
			_JobParamsMap.put(parJobParam.getName(), parJobParam);
		}
	} // end of addJobParameter()

	public void addJobType(RequestJobType parJobType) throws IllegalArgumentException {
		if (parJobType != null) {
			parJobType.validate();

			_JobTypeList.add(parJobType);
		}
	} // end of addJobType()

	public void addModel(String parId, String parModelXML) {
		if (parId != null && parModelXML != null) {
			getAdditionalParametersMap().put(parId, parModelXML);
		}
	} // end of addModel()

	public void addModel(String parId, File parModelFile) throws IOException {
		addModel(parId, readFile(parModelFile));
	} // end of addModel()

   /**
    * This method checks if the passed variable name is declared as a Job Parameter (leading and trailing '#'s).
    * <p>
    * If it is a variable, it must exist in the Job Parameter list otherwise an IllegalArgumentException is thrown. If
    * it's not a variable it will be enclosed in "" and returned.
    * 
    * @param parVarName
    *           variable to be checked
    * 
    * @return parameter name or enclosed variable name
    * 
    * @throws JobGeneratorException
    *            if the Job Parameter is not found
    **/
   public String checkForConstantOrJobParam(String parVarName) throws JobGeneratorException {
      JobParamData jobParamData;
      String retValue;
      Iterator jobParamIter;
      boolean doesJobParamExist;

      retValue = parVarName;
      if (parVarName != null && parVarName.length() > 1) {
         if (parVarName.charAt(0) == '#' && parVarName.charAt(parVarName.length() - 1) == '#') {
            // Job Parameter ==> remove first and last '#' characters
            retValue = parVarName.substring(1, parVarName.length() - 1);

            // check if the Job Parameter can be resolved
            doesJobParamExist = false;
            jobParamIter = this.getJobParamsList().iterator();
            while (jobParamIter.hasNext() && !doesJobParamExist) {
               jobParamData = (JobParamData) jobParamIter.next();

               if (jobParamData.getName().equalsIgnoreCase(retValue)) {
                  doesJobParamExist = true;
               }
            }

            if (!doesJobParamExist) {
               throw new IllegalArgumentException("Job Parameter '" + retValue + "' could not be resolved.");
            }
         }
         else {
            // NOT a Job Parameter ==> enclose value in ""
            retValue = "\"" + parVarName + "\"";
         } // end of (else) if (requestParam.charAt(0) == '#' && ... length()-1) == '#')
      }

      return (retValue);
   } // end of checkForConstantOrJobParam()

   
   private String getJobDescriptionXML() {
      StringBuffer xmlBuf;

      xmlBuf = new StringBuffer();

      if (_JobLongDescription != null && _JobLongDescription.length() > 0) {
      	xmlBuf.append(XMLUtils.createCDATAElement(XML_TAG_JOB_DESC, _JobLongDescription));
      }

      return (xmlBuf.toString());
   }


	private String getJobParamsAsFlatXML() {
		StringBuffer vXMLBuffer;
		Map.Entry vMapEntry;
		Iterator vEntrySetIter;

		// process all map entries
		vXMLBuffer = new StringBuffer();
		vEntrySetIter = _JobParamsMap.entrySet().iterator();
		while (vEntrySetIter.hasNext()) {
			vMapEntry = (Map.Entry) vEntrySetIter.next();

			// add value to string buffer
			vXMLBuffer.append(((JobParamData) vMapEntry.getValue()).toXML());
		} // end of while(vEntrySetIter.hasNext())

		return (vXMLBuffer.toString());
	} // end of getJobParamsAsFlatXML()


	private String getJobTypesAsFlatXML() {
		RequestJobType curJobType;
		StringBuffer vXMLBuffer;
		Iterator vListIter;

		// process all map entries
		vXMLBuffer = new StringBuffer();
		vListIter = _JobTypeList.iterator();
		while (vListIter.hasNext()) {
			curJobType = (RequestJobType) vListIter.next();

			// add value to string buffer
			vXMLBuffer.append(curJobType.toXML());
		} // end of while(vListIter.hasNext())

		return (vXMLBuffer.toString());
	} // end of getJobTypesAsFlatXML()


	public static String readFile(File pFileToRead) throws IOException {
		byte[] byteBufArr;
		StringBuffer vFileContentBuf;

		vFileContentBuf = new StringBuffer();
		try {
		   byteBufArr = ServerRequestUtil.readInputStream(new FileInputStream(pFileToRead));
		   
		   vFileContentBuf.append(new String(byteBufArr, Constants.STRING_ENCODING));
		} // end of try
		catch (IOException pIOExcpt) {
			System.err.println("Error reading file '" + pFileToRead.getName() + "': " + pIOExcpt);
		}

		return (vFileContentBuf.toString());
	} // end of readFile()

	
	public String getDSHostName() {
		return _DSHostName;
	}

	public boolean doContinueOnError() {
		return (_DoContinueOnError);
	} // end of doContinueOnError() {

   public boolean doCreateV7Stage() {
      return (_DoCreateV7Stage);
   } // end of doCreateV7Stage() {

	public String getDSProjectName() {
		return (_DSProjectName);
	} // end of getDSProjectName()

   public Integer getDSServerRPCPort() {
      return _DSServerRPCPort;
   }

   public String getDSTargetFolderName() {
      return (_DSJobTargetFolder);
   } // end of getDSTargetFolderName()

   public String getJobDescription() {
      
      String retDesc;
      
      // IMPORTANT: ensure that server platform 'line separator' is used
      //            --> replace all 'NEWLINE's by a Unix LF
      //            --> and then vice versa (all LFs need to be 'platform NEWLINE')
      
      // first replace all CRLF by LF
      retDesc = StringUtils.replaceString(_JobLongDescription, Constants.NEWLINE, UNIX_NEWLINE);
      
      // and then vice versa
      retDesc = StringUtils.replaceString(retDesc, UNIX_NEWLINE, Constants.NEWLINE);
      
      return (retDesc);
   } // end of getJobDescription()

	public Map getJobParams() {
		return (_JobParamsMap);
	} // end of getJobParams()

	public List<JobParamData> getJobParamsList() {
		List<JobParamData> paramList = new ArrayList<JobParamData>();

		paramList.addAll(_JobParamsMap.values());
		return paramList;
	}

   public List getJobTypes() {
      return (_JobTypeList);
   } // end of getJobTypes()

   public void setCreateV7Stage(boolean createV7Stage) {
      _DoCreateV7Stage = createV7Stage;
   } // end of setCreateV7Stage() {

   public void setDoContinueOnError(boolean continueOnError) {
      _DoContinueOnError = continueOnError;
   } // end of setdoContinueOnError() {
   
   public void setDSHostName(String dsHostName) {
      _DSHostName = dsHostName;
   }

	public void setDSProjectName(String pProjectName) {
		_DSProjectName = pProjectName;
	} // end of setDSProjectName()

   public void setDSServerRPCPort(Integer dsSrvrRPCPort) {
      _DSServerRPCPort = dsSrvrRPCPort;
   }

   public void setDSTargetFolderName(String pTargetFolderName) {
      _DSJobTargetFolder = pTargetFolderName;
   } // end of setDSTargetFolderName()

   
   public void setJobDescription(String parJobDesc) {
      _JobLongDescription = parJobDesc;
   }
   
   
   protected String toXML() {
		StringBuffer xmlBuf = new StringBuffer();

		// - - - - - - - - - - - - - - - - - - - - JobConfig - - - - - - - - - - - - - - - - - - - -

		xmlBuf.append("<");
		xmlBuf.append(XML_TAG_JOB_CONFIG);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_CREATE_V7_STAGE, String.valueOf(_DoCreateV7Stage)));
		xmlBuf.append(">");

		// - - - - - - - - - - - - - - - - - - - (DS) Project - - - - - - - - - - - - - - - - - - -
		xmlBuf.append("<");
		xmlBuf.append(XML_TAG_DS_PROJECT);
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_PROJECT_NAME, _DSProjectName));
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_DSHOST_NAME, _DSHostName));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_DSSRVR_RPC_PORT, _DSServerRPCPort));
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_PROJECT_CONT_ON_ERR, String.valueOf(_DoContinueOnError)));
      xmlBuf.append(">");
      xmlBuf.append("<");
      xmlBuf.append(XML_TAG_DS_PROJECT_TARGET_FOLDER);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_TRG_FOLDER_FOLDER, _DSJobTargetFolder));
		xmlBuf.append(" />");
      xmlBuf.append("</");
      xmlBuf.append(XML_TAG_DS_PROJECT);
      xmlBuf.append(">");

		// - - - - - - - - - - - - - - - - - - - - JobParams - - - - - - - - - - - - - - - - - - - -
		xmlBuf.append("<");
		xmlBuf.append(XML_TAG_JOB_PARAMS);
		xmlBuf.append(">");
		xmlBuf.append(getJobParamsAsFlatXML());
		xmlBuf.append("</");
		xmlBuf.append(XML_TAG_JOB_PARAMS);
		xmlBuf.append(">");
		
      // - - - - - - - - - - - - - - - - - Job (Long) Description - - - - - - - - - - - - - - - -
      xmlBuf.append(getJobDescriptionXML());
      
		xmlBuf.append("</");
		xmlBuf.append(XML_TAG_JOB_CONFIG);
		xmlBuf.append(">");

		// - - - - - - - - - - - - - - - - - - - - JobTypes - - - - - - - - - - - - - - - - - - - -
		xmlBuf.append("<");
		xmlBuf.append(XML_TAG_JOB_TYPES);
		xmlBuf.append(">");
		xmlBuf.append(getJobTypesAsFlatXML());
		xmlBuf.append("</");
		xmlBuf.append(XML_TAG_JOB_TYPES);
		xmlBuf.append(">");

		return (xmlBuf.toString());

	} // end of toXML()

   protected String getTraceString() {
      StringBuffer traceBuffer = new StringBuffer();
      Iterator     jobTypeIter;

      traceBuffer.append("DS Host = ");
      traceBuffer.append(_DSHostName);
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append("DS Server RPC Port = ");
      if (_DSServerRPCPort == null) {
         traceBuffer.append("-");
      }
      else {
         traceBuffer.append(_DSServerRPCPort);
      }
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append("Project Name = ");
      traceBuffer.append(_DSProjectName);
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append("Target Folder Name = ");
      traceBuffer.append(_DSJobTargetFolder);
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append("Continue On errors = ");
      traceBuffer.append(_DoContinueOnError);
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append("Job Params List = ");
      traceBuffer.append(_JobParamsMap);
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append("(External) Job Description = ");
      traceBuffer.append(_JobLongDescription);
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append("Job Types = ");
      jobTypeIter = _JobTypeList.iterator();
      if (jobTypeIter.hasNext()) {
         while (jobTypeIter.hasNext()) {
            traceBuffer.append("Job Type: ");
            traceBuffer.append(((RequestJobType) jobTypeIter.next()).toString());
            traceBuffer.append(Constants.NEWLINE);
         }
      }
      else {
         traceBuffer.append(Constants.NEWLINE);
      }
      traceBuffer.append("Create V7 SAP Stage = ");
      traceBuffer.append(_DoCreateV7Stage);
      traceBuffer.append(Constants.NEWLINE);
      
      return(traceBuffer.toString());
   } // end of getTraceString()

   
   public void validate() throws JobGeneratorException, IllegalArgumentException {
      super.validate();
      if (_DSProjectName == null) {
         throw new IllegalArgumentException("Project name not found");
      }
      if (_JobTypeList.isEmpty()) {
         throw new IllegalArgumentException("No job type found");
      }

   }

} // end of class JobGeneratorRequest
