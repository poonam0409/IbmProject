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


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.net.ssl.SSLHandshakeException;

import org.w3c.dom.Node;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.RetryRequestException;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.XMLUtils;


/**
 * This class is responsible for converting the HTTP request to the RequestBase
 * and ResponseBase objects. All decoding / encoding is done in here.
 * 
 */
public class ServerRequestUtil {

	private static final String ZIPENTRY                    = "request";         //$NON-NLS-1$
	private static final String ENCODED_VALUE_SEPERATOR     = " ";

	/** HTTP parameter separator */
	public  static final String PARAM_SEPERATOR             = "&";               //$NON-NLS-1$
	/** HTTP Key/value separator */
	public  static final String KEY_VALUE_SEPERATOR         = "=";               //$NON-NLS-1$

	public static final int HTTP_RESPONSE_SC_OK             = 200;
	public static final int HTTP_RESPONSE_SC_FOUND          = 302;
	public static final int HTTP_RESPONSE_SC_NOT_AUTHORIZED = 401;
	public static final int HTTP_RESPONSE_SC_NOT_FOUND      = 404;
	public static final int HTTP_RESPONSE_SC_SERVER_ERROR   = 500;

	protected static final String SERVLET_CONNECT_PREFIX_HTTP        = "http";   //$NON-NLS-1$
	protected static final String SERVLET_CONNECT_PREFIX_HTTPS       = "https";  //$NON-NLS-1$
	public    static final String SERVLET_URL_TEMPLATE_PRODUCT       = SERVLET_CONNECT_PREFIX_HTTP  + Constants.SERVLET_URL_TEMPLATE_PRODUCT;
	public    static final String HTTPS_SERVLET_URL_TEMPLATE_PRODUCT = SERVLET_CONNECT_PREFIX_HTTPS + Constants.SERVLET_URL_TEMPLATE_PRODUCT;

	public    static final String DOMAIN_PORT_SEPARATOR              = ":";                                     //$NON-NLS-1$
	public    static final String DOMAIN_PORT_TEMPLATE               = "{0}" + DOMAIN_PORT_SEPARATOR + "{1}";   //$NON-NLS-1$ //$NON-NLS-2$

	private static final boolean            useIISServerConnectionLibs;
	

	static String copyright() {
		return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT;
	}


	static {
		useIISServerConnectionLibs = isClassAvailable("com.ibm.iis.isf.security.HttpConnectionManager"); //$NON-NLS-1$
	} // end of static


	public static String decode(String dataToDecode) throws JobGeneratorException {
		if (dataToDecode == null) {
			return null;
		}
		try {
			// 1. parse data and put together the bytes
			char[] c = dataToDecode.toCharArray();

			final int SIZE = 100000;
			byte[] tempBytes = new byte[0];
			int bytesRead = 0;

			int i = 0;
			while (i<c.length) {				
				byte b = parseByte( new String(c, i, 2) );
				if (bytesRead >= tempBytes.length) {
					byte[] newBytes = new byte[tempBytes.length + SIZE];
					System.arraycopy(tempBytes, 0, newBytes, 0, tempBytes.length);
					tempBytes = newBytes;
				}
				tempBytes[bytesRead] = b;
				bytesRead++;
				i += 2;
			} // end of if (dataToDecode != null)
			byte[] zipBytes = new byte[bytesRead];
			System.arraycopy(tempBytes, 0, zipBytes, 0, bytesRead );

			// 2. unzip the bytes
			ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
			ZipInputStream zis = new ZipInputStream(bais);

			ZipEntry ze = zis.getNextEntry();

			// osuhre: using a BufferedInputStream makes this a lot faster
			byte[] utf8Bytes = readInputStream(new BufferedInputStream(  zis ), dataToDecode.length());

			zis.closeEntry();
			zis.close();

			String result = new String(utf8Bytes, Constants.STRING_ENCODING);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			TraceLogger.trace(TraceLogger.LEVEL_INFO, "Exception ocurred while decoding request", e);
			throw new JobGeneratorException("104800E", Constants.NO_PARAMS, e);
		}
	} // end of decode()


	static String decodeKey(String keyToDecode) {
		String          result;
		StringTokenizer decodeBufTokenizer;
		String          curValue;
		byte            byteArr[];
		int             byteCount;

		result    = null;
		byteCount = 0;
		if (keyToDecode == null) {
			byteArr = new byte[0];
		}
		else {
			byteArr            = new byte[keyToDecode.length() /2 ];
			decodeBufTokenizer = new StringTokenizer(keyToDecode, ENCODED_VALUE_SEPERATOR);

			while (decodeBufTokenizer.hasMoreTokens()) {
				curValue           = decodeBufTokenizer.nextToken();
				byteArr[byteCount] = parseByte(curValue);
				byteCount ++;
			} // end of while(decodeBufTokenizer.hasMoreTokens())
		} // end of if (dataToDecode != null)

		try {
			result = new String(byteArr, 0, byteCount, Constants.CODE_PAGE_UTF_8);
		}
		catch (UnsupportedEncodingException unsupportEncodingExcpt) {
			unsupportEncodingExcpt.printStackTrace();
			result = new String(byteArr, 0, byteCount);
		}

		return (result);
	} // end of decodeKey()


	public static String getFullDomainName(String hostNameOrIPAddress) {
		String fullQualifiedHostName;

		try {
			fullQualifiedHostName = InetAddress.getByName(hostNameOrIPAddress).getCanonicalHostName(); 
		}
		catch(UnknownHostException unknownHostExcpt) {
			unknownHostExcpt.printStackTrace();
			fullQualifiedHostName = hostNameOrIPAddress;
		}

		return(fullQualifiedHostName);
	} // end of getFullDomainName()


	private static int getHttpCode(Map<String, List<String>> headerFields) {
		String          httpResultField;
		StringTokenizer tokenizer;
		List<String>    responseList;
		int             retHTTPCode;

		// get the response list (--> key = 'null') ...
		responseList = headerFields.get(null);

		// ... and get first element (HTTP result) 
		httpResultField = responseList.get(0);

		// result layout is "HTTP/1.1 200 OK"
		// we need the numeric value
		retHTTPCode = -1;
		tokenizer   = new StringTokenizer(httpResultField, " ");
		if (tokenizer.countTokens() > 1) {
			tokenizer.nextToken();  // jump over protocol version
			retHTTPCode = Integer.parseInt(tokenizer.nextToken());
		}

		return(retHTTPCode);
	} // end of getHttpCode()


	public static boolean isIISServerConnectionLibsUsed() {
		return(useIISServerConnectionLibs); 
	} // end of isIISServerConnectionLibsUsed()


	public static ResponseBase loadResponseFromHTTPResult(RequestBase request, String resultXML, 
	                                                      InputStream serverInputStream ) {
		ResponseBase retResponse = null;
		Node         rootNode;
		// System.out.println(resultXML);

		// parse the passed result XML ...
		rootNode = null;
		try {
			rootNode = XMLUtils.getRootElementFromXML(resultXML) ;

			// go on if there is no ResponseBase instance ... 
			if (retResponse == null) {
				if (request instanceof JobGeneratorRequest) {
					byte[] abapCodeArr = readInputStream(serverInputStream);
					retResponse = new JobGeneratorRequestResult(rootNode, abapCodeArr);

				}
				else if (request instanceof GetAllProjectsRequest) {
					retResponse = new GetAllProjectsResponse(rootNode);
				}
				else if (request instanceof GetAllSapConnectionsRequest) {
					retResponse = new GetAllSapConnectionsResponse(rootNode);
				}
				else if (request instanceof GetAllFoldersRequest) {
					retResponse = new GetAllFoldersResponse(rootNode);
				}
				else if (request instanceof GetAllParameterSetsRequest) {
					retResponse = new GetAllParameterSetsResponse(rootNode);
				}
				else if (request instanceof ValidateDataRequest) {
					retResponse = new ValidateDataResponse(rootNode);
				}
			} // end of if (retResponse == null) {
		} // end of try
		catch(Exception excpt) {
			excpt.printStackTrace();

			retResponse = new ErrorResult(request.getClass());
			retResponse.addMessage(excpt.getMessage(), ResponseBase.MESSAGE_TYPE_ERROR);
			retResponse.setException(excpt);
		}

		return(retResponse);  
	} // end of loadResponseFromHTTPResult()


	protected static ResponseBase processServerResult(RequestBase clientRequest, Map<String, List<String>> headerFields,
	                                                  InputStream resultInpStream) throws JobGeneratorException, RetryRequestException {
		
		ResponseBase  retResponse;
		int           httpResultCode;

		TraceLogger.entry();

		retResponse = null;
		try {
			// check the HTTP result code first ...
			httpResultCode = getHttpCode(headerFields);

			if (httpResultCode == HTTP_RESPONSE_SC_OK)
			{
				retResponse = loadResponseFromHTTPResult(clientRequest,	
				                                         decode((headerFields.get(Constants.REQUEST_RESULT).get(0))),
				                                         resultInpStream);
			}
			else {
				String errMsgId;

				switch(httpResultCode) {
					case HTTP_RESPONSE_SC_NOT_FOUND:
						  errMsgId = "103300E";
						  break;

					case HTTP_RESPONSE_SC_NOT_AUTHORIZED:
					     errMsgId = "108400E";
					     break;

					case HTTP_RESPONSE_SC_FOUND:
						  // get the new URL location from header
						  List<String> tmpLocationList = headerFields.get("Location");
						  String urlLocation = tmpLocationList.get(0); 
						  throw new RetryRequestException(urlLocation);
//					     errMsgId = "103400E";
//					     break;

					case HTTP_RESPONSE_SC_SERVER_ERROR:
					     errMsgId = "105100E";
					     break;

					default:
					    errMsgId = "103500E";
				}

				throw new JobGeneratorException(errMsgId, new String[] { String.valueOf(httpResultCode) } );
			} // end of (else) vServerConnection.getHeaderFields()
			
		} 
		catch (JobGeneratorException pJobGenExcpt) {
			throw pJobGenExcpt;
		}
		catch (RetryRequestException pRetryRequestExcpt) {
			throw pRetryRequestExcpt;
		}
		catch (Exception pIOExcpt) {
			pIOExcpt.printStackTrace();
			throw new JobGeneratorException(pIOExcpt.toString());
		} 
		finally {
			;
		} // end of finally

		TraceLogger.exit();

		return(retResponse);
	} // end of processServerResult()


	public static byte[] readInputStream(InputStream is) throws IOException {
		return readInputStream(is, 2056);
	}


	public static byte[] readInputStream(InputStream is, int initialBufferSize) throws IOException {
		byte[] result = new byte[0];

		if (is != null) {
			byte[] tmp = new byte[initialBufferSize];
			int i;
			while ((i = is.read(tmp)) != -1) {
				byte[] old = result;
				result = new byte[old.length + i];
				System.arraycopy(old, 0, result, 0, old.length);
				System.arraycopy(tmp, 0, result, old.length, i);
			}
		} // end of if (is != null)

		return result;
	}


	public static ResponseBase send(RequestBase clientRequest) throws JobGeneratorException {
		
		ResponseBase              vResponse          = null;
		Map<String,String>        vParamsMap;

		TraceLogger.entry();
		vParamsMap = new HashMap<String,String>();
		vParamsMap.putAll(clientRequest.getAdditionalParametersMap());

		// and add JobGenerator XML to that new map ...
		vParamsMap.put(RequestBase.PARAM_JOB_CONFIG, clientRequest.buildXMLString());

		// open the connection to the server
		// create servlet URL using passed connection information
		if (useIISServerConnectionLibs) {
			vResponse = IISConnectionUtils.sendServerRequest(clientRequest, vParamsMap);
		}
		else {
			vResponse = sendPostRequest(clientRequest, vParamsMap);
				
		} // end of (else) if (useIISServerConnectionLibs)
		TraceLogger.exit();

		return(vResponse);
	} // end of send()


	private static ResponseBase sendPostRequest(RequestBase clientRequest, Map<String,String> reqParamsMap) throws JobGeneratorException {
		
		ResponseBase              retResponse        = null;
		HttpURLConnection         vServerConnection;
		InputStream               vResultInputStream = null;
		OutputStream              vSendOutputStream  = null;
		BufferedWriter            vURLWriter         = null;
		OutputStreamWriter        osw                = null;
		String                    vParamBuffer;
		String                    vRetryRC302Location;
		Map<String, List<String>> headerFields;

		TraceLogger.entry();

		vParamBuffer = encodeParams(reqParamsMap);
		try {
			// open the connection to the server
			// create servlet URL using passed connection information
			// encode the parameters and build the parameter (string)

			vRetryRC302Location = null;
			do {
				HTTPConnectionFactory factory = new HTTPConnectionFactory(clientRequest);
				
				vServerConnection   = factory.createConnection(vRetryRC302Location);

				// enable Basic Authorization
				String userpass = MessageFormat.format(DOMAIN_PORT_TEMPLATE,
			                                          new Object[] { clientRequest.getISUsername(), clientRequest.getISPassword()} );
				String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
				vServerConnection.setRequestProperty("Authorization", basicAuth);

				vSendOutputStream = vServerConnection.getOutputStream();
				osw               = new OutputStreamWriter(vSendOutputStream);
				vURLWriter        = new BufferedWriter(osw);

				// and then 'POST' the buffer into the output stream
				vURLWriter.write(vParamBuffer);
				vURLWriter.flush();
				vSendOutputStream.flush();
				headerFields       = vServerConnection.getHeaderFields();
				vResultInputStream = vServerConnection.getInputStream();

				try {
					// process the server result
					retResponse         = processServerResult(clientRequest, headerFields, vResultInputStream);
					vRetryRC302Location = null;
				}
				catch(RetryRequestException pRetryReqExcpt) {
					vRetryRC302Location = pRetryReqExcpt.getURLLocation();
					if (vRetryRC302Location.startsWith(SERVLET_CONNECT_PREFIX_HTTPS)) {
						clientRequest.setHTTPSPort(Constants.JOB_GEN_HTTPS_IS_SERVER_PORT);  // HTTPS --> enable SSL on the client request 
					}
					else {
						clientRequest.setHTTPSPort(0);  // HTTP --> disable SSL on the client request
					}
				}
			}
			while(vRetryRC302Location != null);
		} // end of try 
		catch(SSLHandshakeException sslExc) {
			String msg = sslExc.toString();
			sslExc.printStackTrace();

			if (clientRequest.getUseHTTPS()) {
				if (!useIISServerConnectionLibs) {
					throw new JobGeneratorException("141000E", new String[] { } );
				}
				else {
					throw new JobGeneratorException("140000E", new String[] { msg } );
				}
			}

			throw new JobGeneratorException(msg);
		} 
		catch (JobGeneratorException pJobGenExcpt) {
			throw pJobGenExcpt;
		}
		catch (IOException pIOExcpt) {
			pIOExcpt.printStackTrace();
			throw new JobGeneratorException(pIOExcpt.getLocalizedMessage());
		}
		catch (Exception unexpectedExcpt) {
			unexpectedExcpt.printStackTrace();
			throw new JobGeneratorException(unexpectedExcpt.toString());
		} 
		finally {
			try {
				if (vResultInputStream != null) {
					vResultInputStream.close();
				}
				if (vURLWriter != null) {
					vURLWriter.close();
				}
			} 
			catch (IOException ioe) {
				ioe.printStackTrace();
			} 
			finally {
				try {
					if (osw != null) {
						osw.close();
					}
				} 
				catch (IOException ioe) {
					ioe.printStackTrace();
				} 
				finally {
					try {
						if (vSendOutputStream != null) {
							vSendOutputStream.close();
						}
					} 
					catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		} // end of finally

		TraceLogger.exit();

		return(retResponse);
	} // end of sendPostRequest()





	static String encodeKey(String keyToEncode) {
		StringBuffer encodeBuffer;
		String       result;
		int          arrIdx;
		byte         dataAsByteArr[];
		String       curByteAsString;

		result = null;
		if (keyToEncode != null) {
			encodeBuffer = new StringBuffer();

			try {
				dataAsByteArr = keyToEncode.getBytes(Constants.CODE_PAGE_UTF_8);
			}
			catch (UnsupportedEncodingException unsupportEncodingExcpt) {
				unsupportEncodingExcpt.printStackTrace();
				dataAsByteArr = keyToEncode.getBytes();
			}

			for (arrIdx = 0; arrIdx < dataAsByteArr.length; arrIdx++) {
				if (arrIdx > 0) {
					encodeBuffer.append(ENCODED_VALUE_SEPERATOR);
				}

				curByteAsString = byteToString(dataAsByteArr[arrIdx]); 
				encodeBuffer.append(curByteAsString);
			} // end of for(arrIdx = 0; arrIdx < dataAsByteArr.length; arrIdx ++)

			result = encodeBuffer.toString();
		} // end of if (keyToEncode != null)

		return (result);
	} // end of encodeKey()


	static String byteToString(byte b) {
		int i = (int) b;
		if (i<0) {
			i = -i + 127;			
		}
		String s = Integer.toHexString(i);
		if (i < 16) {
			s = "0" + s;
		}
		return s;
	} // end of byteToString()


	static byte parseByte(String s) {
		int i = Integer.parseInt(s, 16);

		if (i > 127) {
			i = 127 - i;
		}
		return (byte) i;
	} // end of parseByte()



	public static String encode(String dataToEncode) throws JobGeneratorException {
		if (dataToEncode == null) {
			return null;
		}

		String result = null;
		try {
			// 1. convert string to UTF-8 bytes
			byte[] utf8bytes = dataToEncode.getBytes(Constants.STRING_ENCODING);

			// 2. zip the UTF-8 bytes
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream( baos) );
			ZipEntry ze = new ZipEntry(ZIPENTRY);
			zos.putNextEntry(ze);
			zos.write(utf8bytes);
			zos.close();

			// 3. build a string with textual representation of zipped bytes
			byte[] dataAsByteArr = baos.toByteArray();

			StringBuffer encodeBuffer = new StringBuffer();
			String intValue = null;
			for (int arrIdx = 0; arrIdx < dataAsByteArr.length; arrIdx++) {
				intValue = byteToString(dataAsByteArr[arrIdx]); 
				encodeBuffer.append(intValue);
			} // end of for(arrIdx = 0; arrIdx < dataAsByteArr.length; arrIdx
			// ++)

			result = encodeBuffer.toString();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			TraceLogger.trace(TraceLogger.LEVEL_INFO, "Exception ocurred while encoding request", e);
			throw new JobGeneratorException("104700E", Constants.NO_PARAMS, e);
		}
	} // end of encode()



	private static String encodeParams(Map pParamsMap) throws JobGeneratorException {

		String vParamName;
		String vParamValue;
		String vEncodedName;
		String vEncodedValue;
		StringBuffer vParamBuffer;
		Iterator vMapIter;
		Map.Entry vMapEntry;
		boolean hasMoreParams;

		// 'encode' all parameters in the parameter map and write it back
		vParamBuffer = new StringBuffer();
		vParamName = "";
		hasMoreParams = false;
		vMapIter = pParamsMap.entrySet().iterator();
		while (vMapIter.hasNext()) {
			vMapEntry = (Map.Entry) vMapIter.next();

			vParamName    = (String) vMapEntry.getKey();
			vEncodedName  = encodeKey(vParamName);
			vParamValue   = (String) vMapEntry.getValue();
			vEncodedValue = encode(vParamValue);

			if (hasMoreParams) {
				vParamBuffer.append(PARAM_SEPERATOR);
			} else {
				hasMoreParams = true;
			}

			// store the (encoded) result in the parameter buffer
			vParamBuffer.append(vEncodedName);
			vParamBuffer.append(KEY_VALUE_SEPERATOR);
			vParamBuffer.append(vEncodedValue);
		} // end of while (vMapIter.hasNext())

		return (vParamBuffer.toString());
	} // end of encodeParams()


	public static RequestBase createRequestFromHTTPParameterMap(Map<String,String> httpRequestMap) throws JobGeneratorException {

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		// 'decode' all parameters in the HTTP POST parameter map and
		// store it in a new map
		Map<String,String> vNewParamMap = new HashMap<String,String>();
		Iterator it = httpRequestMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String encodedVal = (String) entry.getValue();
			String decodedVal = ServerRequestUtil.decode(encodedVal);
			String decodedKey = decodeKey(entry.getKey().toString());

			vNewParamMap.put(decodedKey, decodedVal);
		}

		RequestBase result = RequestBase.create(vNewParamMap);
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}

		return result;
	}


	private static boolean isClassAvailable(String className) {
		boolean retClassCouldBeloaded;

      retClassCouldBeloaded = false;
		try {
	      Class.forName(className);
	      retClassCouldBeloaded = true;
		} // end of try
      catch(ClassNotFoundException pClassNotFoundExcpt) {
      } // end of catch(ClassNotFoundException pClassNotFoundExcpt)
		catch(Exception pExcpt) {
			TraceLogger.traceException(pExcpt);
			pExcpt.printStackTrace();
		} // end of catch(Exception pExcpt)

      return(retClassCouldBeloaded);
	} // end of isClassAvailable()

} // end of class ServerRequestUtil
