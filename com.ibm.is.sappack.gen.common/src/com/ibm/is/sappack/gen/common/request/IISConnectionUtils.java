//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
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


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.ibm.iis.isf.security.HttpConnectionManager;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.RetryRequestException;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;


public class IISConnectionUtils {

	static String copyright() {
		return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT;
	}


	private static void assignedParam2ToRequest(HttpPost pPostRequest, Map<String,String> pParamsMap)
	        throws JobGeneratorException, UnsupportedEncodingException {
		String                             vParamName;
		String                             vParamValue;
		String                             vEncodedName;
		String                             vEncodedValue;
		List<NameValuePair>                reqParamList;
		Iterator<Map.Entry<String,String>> vMapIter;
		Map.Entry<String,String>           vMapEntry;

		// 'encode' all parameters in the parameter map and write it back
		reqParamList = new ArrayList<NameValuePair>();
		vParamName    = "";
		vMapIter      = pParamsMap.entrySet().iterator();
		while (vMapIter.hasNext()) {
			vMapEntry = vMapIter.next();

			vParamName    = (String) vMapEntry.getKey();
			vEncodedName  = ServerRequestUtil.encodeKey(vParamName);
			vParamValue   = (String) vMapEntry.getValue();
			vEncodedValue = ServerRequestUtil.encode(vParamValue);

			reqParamList.add(new BasicNameValuePair(vEncodedName, vEncodedValue));
		} // end of while (vMapIter.hasNext())
		
		pPostRequest.setEntity(new UrlEncodedFormEntity(reqParamList, Constants.STRING_ENCODING));
	}


	public static Map<String, List<String>> getHeaderFields(HttpResponse pHTTPResonse) {
		Map<String, List<String>> retHeaderFields;
		List<String>              valueList;

		retHeaderFields = new HashMap<String, List<String>>();

		if (pHTTPResonse != null) {
			// fist of all store the status line ...
			valueList = new ArrayList<String>();
			valueList.add(pHTTPResonse.getStatusLine().toString()); // HTTP/1.1 200 OK
			retHeaderFields.put(null, valueList);

			for(Header header : pHTTPResonse.getAllHeaders()) {
				HeaderElement hdrElemArr[] = header.getElements();

				// create MAP entry
				valueList = new ArrayList<String>();
				retHeaderFields.put(header.getName(), valueList);

				if (hdrElemArr.length > 0) {
					// store all header elements
					for(HeaderElement element : hdrElemArr) {
						valueList.add(element.getName());
					}
				}
				else {
					// no header elements ==> store eader value as the only element
					valueList.add(header.getValue());
				}
			}
		} // end of if (pHTTPResonse != null)

		return(retHeaderFields);
	}
	
	protected static ResponseBase sendServerRequest(RequestBase clientRequest, Map<String,String> reqParamsMap)
	          throws JobGeneratorException {
		ResponseBase              retResponse;
		DefaultHttpClient         vHTTPClient;
		HttpEntity                vHTTPEntity;
		HttpPost                  vHTTPReq;
		HttpHost                  vHTTPHost;
		HttpResponse              vHTTPResponse;
		InputStream               vResultInputStream;
		String                    domainServerName;
		String                    tmpRequestString;
		Map<String, List<String>> headerFields;  
		int                       port;

		TraceLogger.entry();

		// open the connection to the server
		// create servlet URL using passed connection information
		domainServerName = ServerRequestUtil.getFullDomainName(clientRequest.getDomainServerName());
		port             = clientRequest.getHTTPSPort();
		vHTTPHost        = new HttpHost(domainServerName, port, ServerRequestUtil.SERVLET_CONNECT_PREFIX_HTTPS);
		tmpRequestString = MessageFormat.format(ServerRequestUtil.HTTPS_SERVLET_URL_TEMPLATE_PRODUCT,
		                                        new Object[] { domainServerName, String.valueOf(port) });

		vResultInputStream = null;
		retResponse        = null;
		vHTTPReq           = new HttpPost(tmpRequestString);
		vHTTPResponse      = null;
		try {
			vHTTPClient = HttpConnectionManager.getHttpClient();
//			vHTTPClient.getCredentialsProvider().setCredentials(new AuthScope(clientRequest.getDomainServerName(), port, AuthScope.ANY_REALM, "basic"),
//          new UsernamePasswordCredentials(clientRequest.getISUsername(), clientRequest.getISPassword()));			
			vHTTPClient.getCredentialsProvider().setCredentials(new AuthScope(domainServerName, port, AuthScope.ANY_REALM, "basic"),
                    new UsernamePasswordCredentials(clientRequest.getISUsername(), clientRequest.getISPassword()));			
			assignedParam2ToRequest(vHTTPReq, reqParamsMap);

			// Clear any previous refused certificates
			HttpConnectionManager.clearLastRefusedChain();

			// send the HTTPS request
			vHTTPResponse = vHTTPClient.execute(vHTTPHost, vHTTPReq);

			// process the server result
			headerFields       = getHeaderFields(vHTTPResponse);
			vHTTPEntity        = vHTTPResponse.getEntity();
			vResultInputStream = vHTTPEntity.getContent();
			retResponse        = ServerRequestUtil.processServerResult(clientRequest, headerFields, vResultInputStream);
		} // end of try
		catch (SSLPeerUnverifiedException sslPeerUnverifiedExcpt) {
			X509Certificate chain[];

			// get the refused certicate chain
			try {
				chain = HttpConnectionManager.getLastRefusedChain();
			}
			catch(IOException ioExcpt) {
				chain = null;
			}

			if (chain != null) {
	/*				
				// An unknown SSL certificate was received
				// Prompt the user and accept the certificate or not based on his answer
				// For example, in the case of a command line tool, do this:
				boolean hasAcceptedCertificates = HttpConnectionManager.promptAndAcceptCertificate(chain); 

				if (hasAcceptedCertificates) {
					// User accepted the certificate, try the request again
					vHTTPResponse = httpClient.execute(httpHost, httpReq);
				}
				else {
					// User refused the certificate.
					;
				} // end of (else) if (hasAcceptedCertificates)
	*/				
				throw new JobGeneratorException("140000E", new String[] { sslPeerUnverifiedExcpt.toString() } );
			}
			TraceLogger.traceException(sslPeerUnverifiedExcpt);
			throw new JobGeneratorException(sslPeerUnverifiedExcpt.toString());
		} // end of catch (SSLPeerUnverifiedException sslPeerUnverifiedExcpt) 
		catch(IOException ioExcpt) {
			TraceLogger.traceException(ioExcpt);
			throw new JobGeneratorException(ioExcpt.toString());
		} // end of catch(IOException ioExcpt)
		catch(RetryRequestException retryExcpt) {
			TraceLogger.traceException(retryExcpt);
			throw new JobGeneratorException(retryExcpt.toString());
		} // end of catch(IOException ioExcpt)
		finally {
			try {
				if (vResultInputStream != null) {
					vResultInputStream.close();
				}
			} 
			catch (IOException ioe) {
				ioe.printStackTrace();
			} 
			vHTTPReq.releaseConnection();
		} // end of finally 

		TraceLogger.exit();

		return(retResponse);
	} // end of sendServerRequest()

} // end of class IISConnectionUtils
