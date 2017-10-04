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
// Module Name : com.ibm.is.sappack.gen.common.request
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.request;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.common.util.XMLUtils;


public abstract class RequestBase {

	private static final String XML_TAG_JOB_GEN_REQ             = "JobGeneratorRequest";
	private static final String XML_ATTRIB_REQUEST_TYPE         = "requestType";
	private static final String XML_TAG_GENERAL_SETTINGS        = "JobGeneratorGeneralSettings";
	public  static final String XML_TAG_JOB_REQUEST_SETTINGS    = "JobRequestSettings";
	public  static final String XML_ATTRIB_VERSION              = "version";
   public  static final String XML_ATTRIB_LOCALE               = "locale";
	public  static final String XML_TAG_IS_DOMAIN               = "ISDomain";
	public  static final String XML_ATTRIB_IS_DOMAIN_NAME       = "name";
	public  static final String XML_ATTRIB_IS_DOMAIN_HTTP_PORT  = "port";
	public  static final String XML_ATTRIB_IS_DOMAIN_HTTPS_PORT = "httpsPort";
	public  static final String XML_ATTRIB_IS_DOMAIN_USER       = "user";
	public  static final String XML_ATTRIB_IS_DOMAIN_PASSWORD   = "pw";
	public  static final String PARAM_JOB_CONFIG                = "JOB_CONFIG";

	
	static String copyright() { 
	   return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; 
	}
	
	
	public static RequestBase create(Map httpParams) throws JobGeneratorException {
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		String configXML = null;
		Map<String,String> additionalParams = new HashMap<String,String>();
		Iterator it = httpParams.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if (entry.getKey().equals(PARAM_JOB_CONFIG)) {
				configXML = (String) entry.getValue();
			}
			else {
				additionalParams.put((String) entry.getKey(), (String) entry.getValue());
			}
		}

		if (configXML == null) {
         throw new JobGeneratorException("102000E", Constants.NO_PARAMS);
		}

//    see traceRequest(...) below 				
//		TraceLogger.trace(TraceLogger.LEVEL_FINE, "Request: " + configXML);

      Element root;
		try {
		   root = XMLUtils.getRootElementFromXML(configXML);
		} catch (Exception e) {
			throw new JobGeneratorException("101900E", Constants.NO_PARAMS, e);
		}

      String clientVersion = XMLUtils.getNodeAttributeValue(root, XML_ATTRIB_VERSION);
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "client version = " + clientVersion);
		}
		StringUtils.checkClientServerVersion(clientVersion, Constants.CLIENT_SERVER_VERSION);

		String requestType = root.getAttribute(XML_ATTRIB_REQUEST_TYPE);

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINER, "RequestType = " + requestType);
      }
      
		RequestBase result = null;
		Class cl;
		try {
			cl = Class.forName(requestType);
			result = (RequestBase) cl.newInstance();
		} catch (Exception e) {
			throw new JobGeneratorException("102300E", new String[] { requestType }, e);
		}

		if (result == null) {
			throw new JobGeneratorException("102400E", Constants.NO_PARAMS );
		}
		result.getAdditionalParametersMap().putAll(additionalParams);
		result.initConfiguration(root);
		
		traceRequest(result);

		result.validate();

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}

		return result;
	}

	// -------------------------------------------------------------------------------------
	//                                 Member Variables
	// -------------------------------------------------------------------------------------
	private   String             _ISUsername;
	private   String             _ISPassword;
	private   String             _ISDomainName;
	private   int                _ISDomainPort;
	private   boolean            _UseHTTPS;
	private   int                _HTTPSPort;
	private   String             _IISClientLocation;
   private   String             _Locale;
	private   Map<String,String> _AdditionalHttpParameters = new HashMap<String,String>();

	
	public RequestBase() {
	   _Locale = Locale.getDefault().toString();
	}

	public void setDomainServerName(String serverName) {
		_ISDomainName = serverName;
	}

	public void setDomainServerPort(int port) {
		this._ISDomainPort = port;
	}
	
	public void setHTTPSPort(int httpsPort) {
		this._HTTPSPort = httpsPort;
		if (httpsPort > 0) {
			this._UseHTTPS  = true;
		}
		else {
			this._UseHTTPS  = false;
		}
	}
	
	public int getHTTPSPort() {
		return this._HTTPSPort;
	}

//	public void setUseHTTPS(boolean useHTTPS) {
//		this._UseHTTPS = useHTTPS;
//	}
//	
	public boolean getUseHTTPS() {
		return this._UseHTTPS;
	}

	public void setIISClientLocation(String iisClientLocation) {
		this._IISClientLocation = iisClientLocation;
	}
	
	public String getIISClientLocation() {
		return this._IISClientLocation;
	}

   protected static String getNodeAttributeValue(Node parNode, String parAttribName) {
      Node curAttrib;
      String retAttribValue;

      retAttribValue = null;
      curAttrib = parNode.getAttributes().getNamedItem(parAttribName);

      if (curAttrib != null) {
         retAttribValue = curAttrib.getNodeValue();
      } // end of if (curAttrib !== null)

      return (retAttribValue);
   } // end of getNodeAttributeValue()
   
   
	/**
	 * Should be overwritten by subclasses. Overwritten implementation must call
	 * this first via super.initConifguration()
	 */
	public void initConfiguration(Element configNode) throws JobGeneratorException {
	   
      setLocale(XMLUtils.getNodeAttributeValue(configNode, XML_ATTRIB_LOCALE));

      NodeList nl = configNode.getElementsByTagName(XML_TAG_IS_DOMAIN);
		if (nl.getLength() != 1) {
         throw new JobGeneratorException("102500E", new String[] { XML_TAG_IS_DOMAIN  } );
		}
		Element serverElem = (Element) nl.item(0);
		_ISDomainName = serverElem.getAttribute(XML_ATTRIB_IS_DOMAIN_NAME);

		String serverPortString = XMLUtils.getNodeAttributeValue(serverElem, XML_ATTRIB_IS_DOMAIN_HTTP_PORT);
		if (serverPortString != null) {
			this._ISDomainPort = Integer.valueOf(serverPortString).intValue();
		}

		serverPortString = XMLUtils.getNodeAttributeValue(serverElem, XML_ATTRIB_IS_DOMAIN_HTTPS_PORT);
		if (serverPortString != null) {
			this._HTTPSPort = Integer.valueOf(serverPortString).intValue();
		}

		_ISUsername = serverElem.getAttribute(XML_ATTRIB_IS_DOMAIN_USER);
		_ISPassword = serverElem.getAttribute(XML_ATTRIB_IS_DOMAIN_PASSWORD);
	}

	
	/**
	 * validate if this request object is configured correctly. Subclasses may
	 * overwrite this method and call super.validate() first!! Validation
	 * failures are indicated if the JobGeneratorException is thrown.
	 * 
	 * @throws JobGeneratorException
	 */
	public void validate() throws JobGeneratorException, IllegalArgumentException {
		if (_ISDomainName == null) {
			throw new JobGeneratorException("102700E", Constants.NO_PARAMS);
		}

		// server and port must be valid to continue
		if (_ISDomainName.length() == 0) {
			throw new IllegalArgumentException("IS Domain was not specified.");
		}
		if (_ISDomainPort < 1) {
			throw new IllegalArgumentException("IS Domain port number is not valid (must be > 0).");
		}
		if (this._ISUsername == null) {
			throw new IllegalArgumentException("Domain User name is not set");
		}
		if (this._ISPassword == null) {
			throw new IllegalArgumentException("Domain Password is not set");
		}
	}

   public Map<String,String> getAdditionalParametersMap() {
      return this._AdditionalHttpParameters;
   }

   public String getDomainServerName() {
      return (_ISDomainName);
   } // end of getDomainServerName()

   public int getDomainServerPort() {
      return (_ISDomainPort);
   } // end of getDomainServerPort()

	public String getISPassword() {
		return (_ISPassword);
	} // end of getISPassword()

   public String getISUsername() {
      return (_ISUsername);
   } // end of setISUsername()

   public String getLocale() {
      return (_Locale);
   } // end of getLocale()

	public void setISPassword(String pPassword) {
		_ISPassword = pPassword;
	} // end of setISPassword()

	public void setISUsername(String pUserName) {
		_ISUsername = pUserName;
	} // end of setISUsername()

   public void setLocale(String pLocale) {
      _Locale = pLocale;
      
      if (_Locale == null || _Locale.length() == 0)
      {
         _Locale = Locale.getDefault().toString();
      }
   } // end of setLocale()

   public void setLocale(Locale pLocale) {
      if (pLocale == null)
      {
         pLocale = Locale.getDefault();
      }
      setLocale(pLocale.toString());
   } // end of setLocale()
   
   
	protected abstract String toXML();
   protected abstract String getTraceString();
   

	protected String buildXMLString() {
		StringBuffer xmlBuf;

		xmlBuf = new StringBuffer(XMLUtils.XML_HEADER);

		xmlBuf.append("<");
		xmlBuf.append(XML_TAG_JOB_GEN_REQ);
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_VERSION, Constants.CLIENT_SERVER_VERSION));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_LOCALE, _Locale));
		String jobRequestType = this.getClass().getName();
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_REQUEST_TYPE, jobRequestType));
		xmlBuf.append(">");

		// - - - - - - - - - - - - - - - - - - - - Server - - - - - - - - - - - - - - - - - - - -
		xmlBuf.append("<" + XML_TAG_GENERAL_SETTINGS + ">");
		xmlBuf.append("<");
		xmlBuf.append(XML_TAG_IS_DOMAIN);
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_IS_DOMAIN_NAME, _ISDomainName));
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_IS_DOMAIN_HTTP_PORT, String.valueOf(_ISDomainPort)));
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_IS_DOMAIN_HTTPS_PORT, String.valueOf(_HTTPSPort)));
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_IS_DOMAIN_USER, _ISUsername));
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_IS_DOMAIN_PASSWORD, _ISPassword));
		xmlBuf.append(" />");
		xmlBuf.append("</" + XML_TAG_GENERAL_SETTINGS + ">");

		xmlBuf.append("<" + XML_TAG_JOB_REQUEST_SETTINGS + ">");
		xmlBuf.append(toXML());
		xmlBuf.append("</" + XML_TAG_JOB_REQUEST_SETTINGS + ">");

		xmlBuf.append("</");
		xmlBuf.append(XML_TAG_JOB_GEN_REQ);
		xmlBuf.append(">");

		return (xmlBuf.toString());
	}
	
	
   private static void traceRequest(RequestBase request) {
      if (TraceLogger.isTraceEnabled()) {
         StringBuffer traceBuffer = new StringBuffer();
         
         traceBuffer.append(Constants.NEWLINE);
         traceBuffer.append("----------------------- SAPPacks Job Generator Request (Start) -----------------------");
         traceBuffer.append(Constants.NEWLINE);
         traceBuffer.append("Request Type = ");
         traceBuffer.append(request.getClass().getName());
         traceBuffer.append(Constants.NEWLINE);
         traceBuffer.append("Locale = ");
         traceBuffer.append(request._Locale);
         traceBuffer.append(Constants.NEWLINE);
         traceBuffer.append("IS Domain Name = ");
         traceBuffer.append(request._ISDomainName);
         traceBuffer.append(Constants.NEWLINE);
         traceBuffer.append("IS Domain Port = ");
         traceBuffer.append(request._ISDomainPort);
         traceBuffer.append(Constants.NEWLINE);
         traceBuffer.append("IS Username = ");
         traceBuffer.append(request._ISUsername);
         traceBuffer.append(Constants.NEWLINE);
         traceBuffer.append("IS Password = ");
         if (request._ISPassword == null)
            traceBuffer.append("null");
         else
            traceBuffer.append("********");
         traceBuffer.append(Constants.NEWLINE);
         traceBuffer.append("HTTP Port = ");
         traceBuffer.append(request._ISDomainPort);
         traceBuffer.append(Constants.NEWLINE);
         traceBuffer.append("HTTPS Port = ");
         traceBuffer.append(request._HTTPSPort);
         traceBuffer.append(Constants.NEWLINE);
         traceBuffer.append(request.getTraceString());
         traceBuffer.append("----------------------- SAPPacks Job Generator Request (End) -------------------------");
         traceBuffer.append(Constants.NEWLINE);

         TraceLogger.trace(TraceLogger.LEVEL_FINER, traceBuffer.toString());
      } // end of if (TraceLogger.isTraceEnabled())
   } // end of traceRequest()
   
}
