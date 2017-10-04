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
// Module Name : com.ibm.is.sappack.gen.common.request
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.request;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.util.XMLUtils;



public final class JobGeneratorRequestResult extends ResponseBase {
	// -------------------------------------------------------------------------------------
	//                                       Constants
	// -------------------------------------------------------------------------------------
   private  static final String  XML_TAG_GENERATOR_RESULT     = "GeneratorResult";
   private  static final String  XML_ATTRIB_CNT_LOAD_JOBS     = "loadJobs";
   private  static final String  XML_ATTRIB_CNT_EXTRACT_JOBS  = "extractJobs";
   private  static final String  XML_ATTRIB_CNT_LOG_TBL_JOBS  = "extractLogigalTables";
   private  static final String  XML_ATTRIB_CNT_MIH_LOAD_JOBS = "mihLoadJobs";
   private  static final String  XML_ATTRIB_CNT_MVMNT_JOBS    = "movementJobs";
   private  static final String  XML_TAG_RUNTIME              = "JobRuntime";
   private  static final String  XML_ATTRIB_TIME              = "time";
   private  static final String  XML_TAG_SUCCESSFUL_JOB_LIST  = "SuccessfullJobList";
   private  static final String  XML_TAG_FAILED_JOB_LIST      = "FailedJobList";
   private  static final String  XML_TAG_JOB                  = "Job";

   
	// -------------------------------------------------------------------------------------
	//                                 Member Variables
	// -------------------------------------------------------------------------------------
	private byte[]  _ZipFileArray;
	private String  _NIDocLoadJobs;
	private String  _NIDocExtractJobs;
	private String  _NLogicalTablesExtractJobs;
	private String  _NMIHLoadJobs;
	private String  _NMovementJobs;
	private String  _JobRuntime;
	private List    _FailedJobsList;
	private List    _SuccessfulJobsList;

	
	static String copyright() { 
	   return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; 
   }	
	
   public JobGeneratorRequestResult(Node xmlNode, byte abapCodeAsArr[]) {
      super(xmlNode);
      
      Node genResultNode;
      
      // get response parameter from DOM
      genResultNode = XMLUtils.getChildNode(xmlNode, XML_TAG_GENERATOR_RESULT);
      if (genResultNode != null) {
         // load the attributes
         _NIDocLoadJobs             = XMLUtils.getNodeAttributeValue(genResultNode, XML_ATTRIB_CNT_LOAD_JOBS);
         _NIDocExtractJobs          = XMLUtils.getNodeAttributeValue(genResultNode, XML_ATTRIB_CNT_EXTRACT_JOBS);
         _NLogicalTablesExtractJobs = XMLUtils.getNodeAttributeValue(genResultNode, XML_ATTRIB_CNT_LOG_TBL_JOBS);
         _NMIHLoadJobs              = XMLUtils.getNodeAttributeValue(genResultNode, XML_ATTRIB_CNT_MIH_LOAD_JOBS);
         _NMovementJobs             = XMLUtils.getNodeAttributeValue(genResultNode, XML_ATTRIB_CNT_MVMNT_JOBS);
         _JobRuntime                = XMLUtils.getNodeAttributeValue(genResultNode, XML_ATTRIB_TIME);
         
         // ... and get the 'Successful Jobs' list ...
         _SuccessfulJobsList = createJobListFromXML(XML_TAG_SUCCESSFUL_JOB_LIST, genResultNode);
         
         // ... and the 'Failed Jobs' list ...
         _FailedJobsList = createJobListFromXML(XML_TAG_FAILED_JOB_LIST, genResultNode);
         
         // store passed ABAP code (as byte arr)
         _ZipFileArray = abapCodeAsArr;
      } // end of if (genResultNode != null)
   }

   public JobGeneratorRequestResult(RequestBase requestType) {
      super(requestType.getClass());
   }

   private List createJobListFromXML(String jobTypeXMLTagName, Node parentNode) {
      List     retJobList;
      NodeList jobNodes;
      Node     jobNode;
      Node     jobListNode;
      int      idx;

      retJobList = new ArrayList();
      
      jobListNode = XMLUtils.getChildNode(parentNode, jobTypeXMLTagName);
      if (jobListNode != null) {
         jobNodes = jobListNode.getChildNodes();
         
         for (idx = 0; idx < jobNodes.getLength(); idx++) {
            
            jobNode = jobNodes.item(idx);
            retJobList.add(XMLUtils.getNodeTextValue(jobNode));
         }
      } // end of if (jobListNode != null)
      
      return(retJobList);
   }
   
   protected String getTraceString() {
      StringBuffer traceBuffer = new StringBuffer();
      Iterator     jobListIter;
      
      traceBuffer.append("ZIP Array size = ");
      if (_ZipFileArray == null) {
         traceBuffer.append("null");
      }
      else {
         traceBuffer.append(_ZipFileArray.length);
      }
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append("IDoc Extract Job cnt = ");
      traceBuffer.append(_NIDocExtractJobs);
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append("IDoc Load Job cnt = ");
      traceBuffer.append(_NIDocLoadJobs);
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append("Extract Logigal Tables cnt = ");
      traceBuffer.append(_NLogicalTablesExtractJobs);
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append("MIH Load Job cnt = ");
      traceBuffer.append(_NMIHLoadJobs);
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append("Movement Job cnt = ");
      traceBuffer.append(_NMovementJobs);
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append("Job Runtime = ");
      traceBuffer.append(_JobRuntime);
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append("Successful Jobs:");
      traceBuffer.append(Constants.NEWLINE);
      if (_SuccessfulJobsList == null) {
         traceBuffer.append("   -");
         traceBuffer.append(Constants.NEWLINE);
      }
      else {
         jobListIter = _SuccessfulJobsList.iterator();
         while (jobListIter.hasNext()) {
            traceBuffer.append("  ");
            traceBuffer.append(jobListIter.next().toString());
            traceBuffer.append(Constants.NEWLINE);
         }
      }
      traceBuffer.append("Failed Jobs:");
      traceBuffer.append(Constants.NEWLINE);
      if (_FailedJobsList == null) {
         traceBuffer.append("   -");
         traceBuffer.append(Constants.NEWLINE);
      }
      else {
         jobListIter = _FailedJobsList.iterator();
         while (jobListIter.hasNext()) {
            traceBuffer.append("  ");
            traceBuffer.append(jobListIter.next().toString());
            traceBuffer.append(Constants.NEWLINE);
         }
      }
      
      return(traceBuffer.toString());
   } // end of getTraceString()
   
   public void setByteArray(byte[] b) {
      this._ZipFileArray = b;
   }

	public void setIDocLoadJobNumber(String docLoadJobs) {
		_NIDocLoadJobs = docLoadJobs;
	}

	public void setIDocExtractJobNumber(String docExtractJobs) {
		_NIDocExtractJobs = docExtractJobs;
	}

	public void setLogicalTablesExtractJobNumber(String logicalTablesExtractJobs) {
		_NLogicalTablesExtractJobs = logicalTablesExtractJobs;
	}

	public void setMIHLoadJobNumber(String loadJobs) {
		_NMIHLoadJobs = loadJobs;
	}

	public void setMovementJobNumber(String movementJobs) {
		_NMovementJobs = movementJobs;
	}

	public void setJobRuntime(String jobRuntime) {
		_JobRuntime = jobRuntime;
	}

	public void setFailedJobsList(List failedJobsList) {
		_FailedJobsList = failedJobsList;
	}
	
	public void setSuccessfulJobsList(List successfulJobsList) {
		_SuccessfulJobsList = successfulJobsList;
	}

	/*
	public InputStream getABAPCodeZipStream() {
		return (_InputStream);
	}
	*/

	public byte[] getABAPCodeBytes() {
		return _ZipFileArray;
	}

	public List getFailedJobsList() {
	   if (_FailedJobsList == null) {
	      _FailedJobsList = new ArrayList();
	   }
	   
		return (_FailedJobsList);
	}
	
	public List getSuccessfulJobsList() {
      if (_SuccessfulJobsList == null) {
         _SuccessfulJobsList = new ArrayList();
      }
      
		return (_SuccessfulJobsList);
	}

	/*
	public String getFailedJobsListAsString() {
		StringBuffer tmpBuffer = new StringBuffer();
		Iterator listIter = _FailedJobsList.iterator();
		boolean isFirst = true;
		while (listIter.hasNext()) {
			if (isFirst) {
				isFirst = false;
			} else {
				tmpBuffer.append(ServerRequestConverter.PARAM_SEPERATOR);
			}

			tmpBuffer.append((String) listIter.next());
			tmpBuffer.append(Constants.NEWLINE);
		}
		return tmpBuffer.toString();
	}
	*/

	public String getIDocExtractJobNumber() {
		return (_NIDocExtractJobs);
	}

	public String getIDocLoadJobNumber() {
		return (_NIDocLoadJobs);
	}

	public String getLogicalTablesExtractJobNumber() {
		return (_NLogicalTablesExtractJobs);
	}

	public String getJobRuntime() {
		return (_JobRuntime);
	}

	public String getMIHLoadJobNumber() {
		return (_NMIHLoadJobs);
	}

	public String getMovementJobNumber() {
		return (_NMovementJobs);
	}

   protected String getXML() {
      StringBuffer xmlBuf;
      Iterator     listIter;

      // build result XML ...
      xmlBuf = new StringBuffer();

      xmlBuf.append("<");
      xmlBuf.append(XML_TAG_GENERATOR_RESULT);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_CNT_EXTRACT_JOBS, _NIDocExtractJobs));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_CNT_LOAD_JOBS, _NIDocLoadJobs));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_CNT_LOG_TBL_JOBS, _NLogicalTablesExtractJobs));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_CNT_MIH_LOAD_JOBS, _NMIHLoadJobs));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_CNT_MVMNT_JOBS, _NMovementJobs));
      xmlBuf.append(">");
      
      // - - - - - - - - - - - - - - - - - - - - Job Runtime - - - - - - - - - - - - - - - - - - - -
      xmlBuf.append("<");
      xmlBuf.append(XML_TAG_RUNTIME);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_TIME, _JobRuntime));
      xmlBuf.append("/>");
      
      // - - - - - - - - - - - - - - - - - - List Of Successful Jobs - - - - - - - - - - - - - - - -
      if (_SuccessfulJobsList == null) {
         xmlBuf.append("<");
         xmlBuf.append(XML_TAG_SUCCESSFUL_JOB_LIST);
         xmlBuf.append("/>");
      }
      else {
         xmlBuf.append("<");
         xmlBuf.append(XML_TAG_SUCCESSFUL_JOB_LIST);
         xmlBuf.append(">");
         listIter = _SuccessfulJobsList.iterator();
         while(listIter.hasNext()) {
            xmlBuf.append("<" + XML_TAG_JOB + ">");
            xmlBuf.append((String) listIter.next());
            xmlBuf.append("</" + XML_TAG_JOB + ">");
         }
         xmlBuf.append("</");
         xmlBuf.append(XML_TAG_SUCCESSFUL_JOB_LIST);
         xmlBuf.append(">");
      }
      
      // - - - - - - - - - - - - - - - - - - List Of Failed Jobs - - - - - - - - - - - - - - - -
      if (_FailedJobsList == null) {
         xmlBuf.append("<");
         xmlBuf.append(XML_TAG_FAILED_JOB_LIST);
         xmlBuf.append("/>");
      }
      else {
         xmlBuf.append("<");
         xmlBuf.append(XML_TAG_FAILED_JOB_LIST);
         xmlBuf.append(">");
         listIter = _FailedJobsList.iterator();
         while(listIter.hasNext()) {
            xmlBuf.append("<" + XML_TAG_JOB + ">");
            xmlBuf.append((String) listIter.next());
            xmlBuf.append("</" + XML_TAG_JOB + ">");
         }
         xmlBuf.append("</");
         xmlBuf.append(XML_TAG_FAILED_JOB_LIST);
         xmlBuf.append(">");
      }
      
      xmlBuf.append("</");
      xmlBuf.append(XML_TAG_GENERATOR_RESULT);
      xmlBuf.append(">");

      return (xmlBuf.toString());
   } // end of getXML()
   
} // end of class JobGeneratorRequestResult
