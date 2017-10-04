package com.ibm.is.sappack.dsstages.idocextract.test;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import com.ibm.is.sappack.dsstages.common.test.TestLog;
import com.ibm.is.sappack.dsstages.idocextract.util.IDocFileHandler;

@SuppressWarnings("nls")
public class IDocFileHandlerTest extends TestCase {

	File file = null;
	IDocFileHandler idocFileHandler = null;

	public IDocFileHandlerTest(String name) {
		super(name);
		this.file = new File("C:\\temp\\idocextract_test\\DEBMAS06_BOCASAPIDES5_0000000001185799.txt");
		this.idocFileHandler = new IDocFileHandler(file);
	}

	protected void setUp() throws Exception {
		super.setUp();
			
			TestLog.log("Parse IDoc file: " + this.file.getName());
			TestLog.log("Init IDoc file handler ...");
			
			assertTrue(this.idocFileHandler.initialize());
			
			TestLog.log("IDoc file handler initialized");
						

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testGetSegmentData() {
		TestLog.log("Extract segment data ...");

		String segName = "E2KNA1M005";
		printSegmentData(segName);

		TestLog.log(" ");
		segName = "E2KNA11002";
		printSegmentData(segName);

		TestLog.log(" ");
		segName = "E2KNVVM007";
		printSegmentData(segName);

		TestLog.log(" ");
		segName = "E2KNVIM";
		printSegmentData(segName);

		TestLog.log(" ");
		segName = "E2KNB1M006";
		printSegmentData(segName);

		TestLog.log(" ");
		segName = "CONTROL_RECORD";
		printSegmentData(segName);
		
	}
	private void printSegmentData(String segName){
		TestLog.log("Extract all segments of type " + segName + " : ");

		List<String> segData = this.idocFileHandler.getSegmentData(segName);
		Iterator<String> segIterator = segData.iterator();
		while (segIterator.hasNext()){
			String data = segIterator.next();
			TestLog.log("length: "+ data.length() +" Segment name: " + segName + " Segment data: " + data);
		}

		TestLog.log("segment data extracted");
		
	}


	
}
