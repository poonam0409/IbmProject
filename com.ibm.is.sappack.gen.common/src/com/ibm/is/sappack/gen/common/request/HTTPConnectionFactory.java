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
// Module Name : com.ibm.is.sappack.gen.common.request
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.request;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;


public class HTTPConnectionFactory {

	private static final String SEP                          = File.separator;
	private static final String IS_CLIENT_PLUGINS_FOLDER     = "ASBNode" + SEP + "eclipse" + SEP + "plugins";  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static final String IS_100_SSL_CLIENT_FOLDER     = IS_CLIENT_PLUGINS_FOLDER    + SEP + "com.ibm.iis.client";  //$NON-NLS-1$
	private static final String IS_912_SSL_CLIENT_FOLDER     = IS_CLIENT_PLUGINS_FOLDER    + SEP + "com.ibm.isf.client";  //$NON-NLS-1$ //$NON-NLS-2$
	private static final String IS_912_SSL_CLIENT_PROPS_FILE = IS_912_SSL_CLIENT_FOLDER    + SEP + "ssl.client.props";    //$NON-NLS-1$

	private static final String IS_SSL_UTILS_JAR             = "ASBNode/eclipse/plugins/com.ibm.isf.client/ISF_util.jar"; //$NON-NLS-1$
	private static final String IS_WS_ADMIN_CLIENT_JAR       = "ASBNode/apps/etc/com.ibm.ws.admin.client_7.0.0.jar";         //$NON-NLS-1$

	private Properties     sslClientProps;
	private URLClassLoader classLoader;
	private RequestBase    request;


	static String copyright() {
		return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public HTTPConnectionFactory(RequestBase request) throws JobGeneratorException {
		this.sslClientProps = null;
		this.classLoader    = null;
		this.request        = request;

		if (request == null) {
			throw new IllegalArgumentException("'request' must not be empty.");
		}
		String iisClientLocation = request.getIISClientLocation();

		if (this.request.getUseHTTPS()) {
			// check if the IS client installation folder exists ... 
			File iisClientLocationFolder = new File(iisClientLocation);
			if (!iisClientLocationFolder.exists()) {
				throw new JobGeneratorException("142000E", new String[] { iisClientLocation } );
			}

			// initialize SSL client Properties and classloader
			initSSLClientProps(iisClientLocation);
			initClassLoader(iisClientLocation);
		}
	}

	private File getSSLClientPropsFile(String iisClientLocation) throws JobGeneratorException {
		String propsFilePath;
		File   sslClientFile;

		// IS v9.1.2 or later
		propsFilePath = iisClientLocation + SEP + IS_912_SSL_CLIENT_PROPS_FILE;

		sslClientFile = new File(propsFilePath);
		if (!sslClientFile.exists()) {
			// SSL client properties files does not exist. Is it really a IS 9.1.2 installtion ? 
			// Possible cause: IS Picasso installation --> check for existence of the IS Picasso SSL properties file
			File clientIS100Folder = new File(iisClientLocation + SEP + IS_100_SSL_CLIENT_FOLDER);
			if (clientIS100Folder.exists()) {
				// ==> User should configure Picasso plugins in IDA 
				throw new JobGeneratorException("141000E", new String[] { } );
			}

			throw new JobGeneratorException("143500E", new String[] { propsFilePath } );
		}

		return(sslClientFile);
	}

	private void initSSLClientProps(String iisClientLocation) throws JobGeneratorException {
		File sslClientPropsFile = getSSLClientPropsFile(iisClientLocation);

		sslClientProps = new Properties();
		try {
			FileInputStream fis = new FileInputStream(sslClientPropsFile);
			this.sslClientProps.load(fis);
		
			fis.close();
		}
		catch(IOException ioExcpt) {
			throw new JobGeneratorException(ioExcpt.getMessage(), ioExcpt);
		}
	}

	private void initClassLoader(String iisClientLocation) throws JobGeneratorException {
		String jarFiles[] = new String[] {IS_SSL_UTILS_JAR, 
			                               IS_WS_ADMIN_CLIENT_JAR };

		iisClientLocation = iisClientLocation.replaceAll("\\\\", "/");
		URL urls[]        = new URL[jarFiles.length];

		try {
			for (int i = 0; i < jarFiles.length; i++) {
				String s = "file:///" + iisClientLocation + "/" + jarFiles[i];
				urls[i] = new URL(s);
			}
		}
		catch(MalformedURLException malFormedURLExcpt) {
			throw new JobGeneratorException(malFormedURLExcpt.getMessage(), malFormedURLExcpt);
		}

		this.classLoader = new URLClassLoader(urls);
	}

	private String getTrustStore() {
		if (sslClientProps == null) {
			return null;
		}
				
		String tsValue = sslClientProps.getProperty("com.ibm.ssl.trustStore"); //$NON-NLS-1$
		if (tsValue.startsWith("${")) { //$NON-NLS-1$
			String token = tsValue.substring(2, tsValue.indexOf("}"));          //$NON-NLS-1$
			String tokenV = sslClientProps.getProperty(token);
			String str = tsValue.substring(tsValue.indexOf("}") + 1);           //$NON-NLS-1$
			tsValue = tokenV + str;
		}
		return tsValue;
	}

	private String getTrustStoreType() {
		if (sslClientProps == null) {
			return null;
		} else {
			String tsType = sslClientProps.getProperty("com.ibm.ssl.trustStoreType");
			return tsType;
		}
	}


	private X509TrustManager createTrustManager() {
		try {
			Class cl = this.classLoader.loadClass("com.ibm.is.net.ssl.ISX509TrustManager"); //$NON-NLS-1$
			Object o = cl.newInstance();
			X509TrustManager result = (X509TrustManager) o;
			File f = new File(getTrustStore());
			String t = getTrustStoreType();
			Method loadCertificates = cl.getMethod("loadCertificates", new Class[] { File.class, String.class }); //$NON-NLS-1$

			loadCertificates.invoke(result, new Object[] { f, t });
			return result;
		} catch (Exception exc) {
			exc.printStackTrace();
			throw new RuntimeException(exc);
		}
	}

	public HttpURLConnection createConnection(String newServletURLString) throws NoSuchAlgorithmException, KeyManagementException, IOException {
		boolean useSSL = this.request.getUseHTTPS();

		if (this.sslClientProps == null && useSSL) {
			useSSL = false;
			TraceLogger.trace(TraceLogger.LEVEL_FINE, "An unexpected error occurred while initializing HTTPS connection. Switching to HTTP."); //$NON-NLS-1$
		}

		String tmpURLRequestString;
		if (newServletURLString == null) {
			// open the connection to the server
			// create servlet URL using passed connection information
			String urlTemplate      = ServerRequestUtil.SERVLET_URL_TEMPLATE_PRODUCT;
			String domainServerName = this.request.getDomainServerName();
			int    port             = this.request.getDomainServerPort();
			
			if (useSSL) {
				urlTemplate      = ServerRequestUtil.HTTPS_SERVLET_URL_TEMPLATE_PRODUCT;
				domainServerName = ServerRequestUtil.getFullDomainName(domainServerName);
				port             = this.request.getHTTPSPort();
			}

			tmpURLRequestString = MessageFormat.format(urlTemplate, new Object[] { domainServerName, String.valueOf(port) });
		}
		else {
			tmpURLRequestString = newServletURLString;
		}
		
		URL servletURL = new URL(tmpURLRequestString);
		HttpURLConnection vServerConnection = (HttpURLConnection) servletURL.openConnection();

		if (useSSL) {
			X509TrustManager tm = createTrustManager();

			SSLContext sc = SSLContext.getInstance("SSL"); //$NON-NLS-1$
			sc.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory myfactory = sc.getSocketFactory();

			((HttpsURLConnection) vServerConnection).setSSLSocketFactory(myfactory);
		}
		vServerConnection.setRequestMethod("POST"); //$NON-NLS-1$
		vServerConnection.setDoOutput(true);
		vServerConnection.setUseCaches(false);

		return(vServerConnection);
	}

}
