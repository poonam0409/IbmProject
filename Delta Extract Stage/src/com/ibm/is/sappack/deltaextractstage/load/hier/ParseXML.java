package com.ibm.is.sappack.deltaextractstage.load.hier;

//Start of class ParseXMLResponseIKM
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

import com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger;
import com.ibm.is.sappack.deltaextractstage.commons.DsSapExtractorParam;
import com.sap.conn.jco.JCoTable;

public class ParseXML {
	JCoTable tab;
	DsSapExtractorParam param = null;

	public ParseXML(DsSapExtractorParam param)
	{
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"++ ParseXML ++"});
		this.param = param;
	}


	public boolean getdatafromXML(InputStream sfInputstream, JCoTable tab)
	{
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"getdatafromXML ..."});
		boolean status = false;
		SAXParserFactory factory=SAXParserFactory.newInstance();
		try {
			SAXParser parser=factory.newSAXParser();
			XMLReader xmlHandler= new XMLReader(tab,param);
			try {
				DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"try ..."});
				parser.parse(sfInputstream, xmlHandler);
				status= true;
				DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"try2 ..."});
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return status;
	}

	public boolean insertdataOracle()
	{
		return false;
	}
}
//End of class ParseXMLResponseIKM
