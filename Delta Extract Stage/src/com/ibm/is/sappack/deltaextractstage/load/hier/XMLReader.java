package com.ibm.is.sappack.deltaextractstage.load.hier;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.ibm.is.sappack.deltaextractstage.commons.DsSapExtractorParam;
import com.sap.conn.jco.JCoTable;

public class XMLReader extends DefaultHandler {

	private final String SEGNAM = "SEGNAM";
	private final String DATA = "DATA";
	private final String SEQUELFLAG = "SEQUELFLAG";
	private final String LENGTH = "LENGTH";
	private final String LANGU = "LANGU";
	private final String ITEM = "item";
	//
	private boolean slength = false;
	private boolean segnam = false;
	private boolean sequalflag = false;
	private boolean cdata = false;
	private boolean langu = false;
	private JCoTable tab;
	DsSapExtractorParam param = null;
	long entrytime;
	private StringBuffer strbuf = null;
	
	public XMLReader(JCoTable tab, DsSapExtractorParam param)
	{
		this.tab = tab;
		this.param = param;
		strbuf = new StringBuffer();
	}
	
	@Override
	public void startDocument() throws SAXException {
		entrytime = System.currentTimeMillis();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		if(qName.equals(SEGNAM))
		{
			segnam = true;
			strbuf.setLength(0);
		}
		
		if(qName.equals(SEQUELFLAG))
		{
			sequalflag = true;
			strbuf.setLength(0);
		}
		
		if(qName.equals(LENGTH))
		{
			slength = true;
			strbuf.setLength(0);
		}
		
		if(qName.equals(DATA))
		{
			cdata = true;
			strbuf.setLength(0);
		}
		
		if(qName.equals(LANGU))
		{
			langu = true;
			strbuf.setLength(0);
		}
		
		if(qName.equals(ITEM))
		{
			tab.appendRow();
			strbuf.setLength(0);
		}
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		if(segnam)
		{
		tab.setValue(SEGNAM, strbuf.toString());
		segnam = false;
		}
		
		if(sequalflag)
		{
		tab.setValue(SEQUELFLAG, strbuf.toString());
		sequalflag = false;
		}
		
		if(slength)
		{
		tab.setValue(LENGTH, strbuf.toString());
		slength= false;
		}
		
		if(cdata)
		{
		tab.setValue(DATA,strbuf.toString());
		cdata = false;
		}
		
		if(langu)
		{
		tab.setValue(LANGU, strbuf.toString());
		langu= false;
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		strbuf.append(String.valueOf(ch,start,length));	
	}
	
	@Override
	public void endDocument() throws SAXException {
		param.setTab(tab);
	}
}
//End of class MyLoginxmlHandler.Java
