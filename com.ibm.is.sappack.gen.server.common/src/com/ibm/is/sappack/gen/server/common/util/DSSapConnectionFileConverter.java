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
// Module Name : com.ibm.is.sappack.gen.server.common.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.common.util;


import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.XMLUtils;


/**
 * DSSapConnectionFileConverter
 * 
 * Utility class to convert the content of the DSSAPConnections.config file into XML
 * 
 */
public class DSSapConnectionFileConverter {

	private static final String XMLTAG_DSSAPCONNECTIONS    = "DSSapConnections"; //$NON-NLS-1$
	public  static final String XMLTAG_NO_DSSAPCONNECTIONS = "<DSSapConnections/>"; //$NON-NLS-1$
	private static final String XMLTAG_DSSAPCONNECTION     = "DSSapConnection"; //$NON-NLS-1$
	private static final String BEGIN                      = "<BEGIN>"; //$NON-NLS-1$
	private static final String END                        = "<END>"; //$NON-NLS-1$
	private static final String EQUALS                     = "="; //$NON-NLS-1$
	private static final String DSPASSWD                   = "DSPASSWORD"; //$NON-NLS-1$
	private static final String SAPPASSWD                  = "DEFAULTPASSWORD"; //$NON-NLS-1$
	private static final String PASSWDFILE                 = "PASSWORDFILE"; //$NON-NLS-1$

	
	static String copyright() {
		return com.ibm.is.sappack.gen.server.common.util.Copyright.IBM_COPYRIGHT_SHORT;
	}


	/**
	 * convertToXML
	 * 
	 * converts the content of the connections file into XML
	 * 
	 * @param filecontent
	 * @return
	 * @throws Exception
	 */
	public static String convertToXML(String filecontent) throws Exception {

		// input
		StringBuffer mainContent = new StringBuffer();

		// output
		StringBuffer xmlContent = new StringBuffer();
		int begin;
		int end;
		
		if (filecontent.length() > 1) {
			begin = filecontent.indexOf(BEGIN) + BEGIN.length();
			end   = filecontent.lastIndexOf(END);
			mainContent.append(filecontent.substring(begin, end));
		} // end of if (filecontent.length() > 1) 
		
		/*
		 * get content between the first <BEGIN> tag and the last <END> tag
		 */

		// consider that no connections might be defined so far
		if (mainContent.length() > 1 ) { // contains \n if empty
			// <DSSapConnections>
			xmlContent.append("<");
			xmlContent.append(XMLTAG_DSSAPCONNECTIONS);
			xmlContent.append(">");
			
			/* read each connection */
			while (mainContent.indexOf(BEGIN) > -1) {

				/* create an own Node for this connection */
				// open <DSSapConnection> node
				xmlContent.append("<");
				xmlContent.append(XMLTAG_DSSAPCONNECTION);
				xmlContent.append(" ");

				begin = mainContent.indexOf(BEGIN);
				end   = mainContent.indexOf(END) + END.length();

				// extract content for current connection
				StringBuffer connectionContent = new StringBuffer();

				// remove <BEGIN> and <END> from connection content
				connectionContent.append((mainContent.substring(begin, end)).replaceAll(BEGIN, 
																												Constants.EMPTY_STRING).replaceAll(END, 
																																							  Constants.EMPTY_STRING));

				// split string into lines
				String[] lines = connectionContent.toString().split("\n");
				/*
				 * create xml attribute for each key value pair except
				 * the passwords for SAP and DataStage
				 */
				for (int i = 0; i < lines.length; i++) {
					String line = lines[i];
					// line should contain '='
					if (line.indexOf(EQUALS) > -1) {
						int index = line.indexOf(EQUALS);
						String key = line.substring(0, index);
						String value = line.substring(index + EQUALS.length(), line.length());

						/* do not store passwords */
						if (!key.equals(DSPASSWD) && !key.equals(SAPPASSWD) && !key.equals(PASSWDFILE)) {
					      xmlContent.append(XMLUtils.createAttribPairString(key, value));
						}

					}
				}

				// close <DSSapConnection> node
				xmlContent.append("/>");

				// remove connection substring from main content
				mainContent.delete(begin, end);
			} // end of while (mainContent.indexOf(BEGIN) > -1)

			// </DSSapConnections>
			xmlContent.append("</");
			xmlContent.append(XMLTAG_DSSAPCONNECTIONS);
			xmlContent.append(">");

			// validate generated xml
			validateXML(xmlContent.toString());

			if(TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINER, "Converted DSSapConnections file into XML: "+ xmlContent.toString());
			}
		} 
		else {
			// no SAP connection defined on this DataStage project
			if(TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINE, "DSSapConnections file is empty - looks like no SAP R/3 connections have been defined");
			}

			// <DSSapConnections/>
			xmlContent = new StringBuffer();
			xmlContent.append(XMLTAG_NO_DSSAPCONNECTIONS);
		}

		return xmlContent.toString();
	}


	/**
	 * validateXML
	 * 
	 * validate the generated XML
	 * 
	 * @param xml
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static void validateXML(String xml) throws ParserConfigurationException, SAXException, IOException {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		db = dbf.newDocumentBuilder();
		InputSource inputSource = new InputSource();
		inputSource.setCharacterStream(new StringReader(xml));
		db.parse(inputSource);
	}
	
}
