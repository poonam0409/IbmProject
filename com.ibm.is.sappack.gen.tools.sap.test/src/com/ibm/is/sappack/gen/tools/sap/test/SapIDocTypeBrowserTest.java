//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2010                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the US Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM Information Server Job Generator
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.test
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.iis.sappack.gen.common.ui.connections.SapConnectionTester;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.IDocField;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocType;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocTypeMetaData;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.MessageType;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.SapIDocTypeBrowser;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Segment;
import com.sap.conn.jco.JCoException;

public class SapIDocTypeBrowserTest {

	private static String SAP = "SAP";
	private static SapSystem sapSystem = new SapSystem(SAP);
	private static SapIDocTypeBrowser browser;
	private static IDocType idoctype = null;
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.sap.test.Copyright.IBM_COPYRIGHT_SHORT; }
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/*sapSystem.setClientId(800);
		sapSystem.setUsername("nings");
		sapSystem.setPassword("sn791114");
		sapSystem.setHost("bocasaperp5.bocaraton.ibm.com");
		sapSystem.setSystemNumber(01);
		browser = new SapIDocTypeBrowser(sapSystem);
		*/
		
		sapSystem.setClientId(800);
		sapSystem.setUsername("rguberud");
		sapSystem.setPassword("cody50");
		sapSystem.setHost("bocasapides5.bocaraton.ibm.com");
		sapSystem.setSystemNumber("00");
      sapSystem.setUserLanguage("EN");
		browser = new SapIDocTypeBrowser(sapSystem, false, null);
		}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test(timeout = 1000000000)
	public void testGetIsUnicodeSapSystem() throws JCoException {
		System.out.println("Connecting...");
		SapConnectionTester.ping(sapSystem);
		assertEquals(SAP, "SAP");

	}

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testListIDoctypes() throws Exception {

		// Test listIDocTypes(String filter), expect to return a List of
		// IDocTypes:
		System.out.println("Test listIDocTypes(String filter): ");

		// Test1 (filter = ""), expect to return all the IDocTypes
		System.out.println("Test1: filter = ''");
		List<IDocType> idoctypes = browser.listIDoctypes("");
		// call selectIDoctypes(String nameFilter)
		System.out.println("Call selectIDoctypes(String nameFilter): nameFilter = ''");
		assertTrue("Return should be all IDocTypes with language = 'E'", idoctypes.size() >= 1000);
		System.out.println("There are " + idoctypes.size() + " IDocTypes:");
		Iterator<IDocType> iterator = idoctypes.iterator();
		idoctype = iterator.next();
		System.out.println("The first IDocType is " + idoctype.getName());

		// Test2 (filter = "debmas%"), expect to return the IDocTypes with
		// prefix "DEBMAS"
		System.out.println("Call selectIDocTypes(String filter): filter = 'debmas%'");
		idoctypes = browser.listIDoctypes("debmas%");
		assertTrue("Return should be the IDocTypes with prefix 'DEBMAS'", idoctypes.size() >= 6);
		iterator = idoctypes.iterator();
		while (iterator.hasNext()) {
			idoctype = iterator.next();
			assertTrue("The name of result IDocType has prefix 'DEBMAS'", idoctype.getName().startsWith("DEBMAS"));
			System.out.println(idoctype.getName());
		}

		// Test3 (filter = "%debmas%"), expect to return the IDocTypes whose
		// name contains "DEBMAS"
		System.out.println("Call selectIDocTypes(String filter): filter = '%debmas%'");
		idoctypes = browser.listIDoctypes("%debmas%");
		assertTrue("Return the IDocTypes whose name contains 'DEBMAS'", idoctypes.size() == 7);
		iterator = idoctypes.iterator();
		while (iterator.hasNext()) {
			idoctype = iterator.next();
			assertTrue("The name of result IDocType contains 'DEBMAS'", idoctype.getName().contains("DEBMAS"));
			System.out.println(idoctype.getName());
		}

		// Test4 (filter = "debmas06"), expect to return the IDocType DEBMAS06
		System.out.println("Call selectIDocTypes(String filter): filter = 'debmas06'");
		idoctypes = browser.listIDoctypes("debmas06");
		assertTrue("Return just one IDocType", idoctypes.size() == 1);
		System.out.print("Return just one IDocType: IDocTypeName = ");
		iterator = idoctypes.iterator();
		idoctype = iterator.next();
		assertEquals("The name of result IDocType is 'DEBMAS06'", "DEBMAS06", idoctype.getName());
		System.out.println(idoctype.getName());

		// Test5 (filter = "debmas"), expect to return a empty list and error
		// message "ERORR! IDoc type not found!"
		System.out.println("Call selectIDocTypes(String filter): filter = 'debmas'");
		System.out.print("Output the error message: ");
		idoctypes = browser.listIDoctypes("debmas");
		assertTrue("The list of IDocTypes should be empty", idoctypes.isEmpty());

		// Test (filter = "'"), expect an IDocTypeExtractException, see
		// testException1()

	}

	@Test(expected = JCoException.class)
	public void testException1() throws JCoException {
		List<IDocType> idoctypes = browser.listIDoctypes("'");
	}

	@Test
	public void testGetIDocTypeMetaData() throws JCoException {

		// Test getIDocTypeMetadata(IDocType idoctype), expect to return the
		// MetaData of the specified IDocType

		// Test1 (idoctype = DEBMAS06), expect to get the Segments and
		// MessageTypes of IDocType DEBMAS06
		IDocTypeMetaData metadata = browser.getIDocTypeMetaData(idoctype);
		assertTrue("Get the MetaData of IDocType 'DEBMAS06'", metadata != null);
		assertTrue("The IDocType DEBMAS06 should have 36 Segments", metadata.getSegments().size() == 36);
		System.out.println("The IDocType DEBMAS06 should have 36 Segments, return value: " + metadata.getSegments().size());
		assertTrue("The IDocType DEBMAS06 should have 2 MessageTypes", metadata.getMessageTypes().size() >= 2);
		System.out.println("The IDocType DEBMAS06 should have more than 2 MessageTypes, return value: " + metadata.getMessageTypes().size());
		// Segments test:
		System.out.println("Check the segments of IDocType DEBMAS06");
		// Test1.1 test method getRootSegments() of class IDocTypeMetaData,
		// expect to return the rootSegments
		// of IDocType DEBMAS06
		assertTrue("The IDocType DEBMAS06 should have 1 Rootsegment", metadata.getRootSegments().size() == 1);
		Segment root = metadata.getRootSegments().iterator().next();
		// test the methods of class Segment
		System.out.println("Test the rootsegment:");
		assertTrue("The SegmentType of rootsegment should be 'E1KNA1M'", root.getType().equals("E1KNA1M"));
		System.out.println("1. The SegmentType of rootsegment should be E1KNA1M, return value: " + root.getType());
		assertTrue("The SegmentDefinition of rootsegment should be 'E2KNA1M005'", root.getDefinition().equals("E2KNA1M005"));
		System.out.println("2. The SegmentDefinition of rootsegment should be E2KNA1M005, return value: " + root.getDefinition());
		assertTrue("The No. of rootsegment should be 1", root.getNr() == 1);
		System.out.println("3. The No.(ID) of rootsegment should be 1 : " + root.getNr());
		assertTrue("The rootsegment should belong to IDocType DEBMAS06", root.getIDocType().equals(idoctype));
		System.out.println("4. The rootsegment should belong to IDocType DEBMAS06, return value: " + root.getIDocType().getName());
		assertTrue("The rootsegment should have no parentsegment", root.getParentSegment() == null);
		if (root.getParentSegment() == null) {
			System.out.println("5. The rootsegment should have no parentsegment: Yes");
		} else
			System.out.println("5. The rootsegment should have no parentsegment: No");
		assertTrue("This rootsegment should have 20 childsegments", root.getChildren().size() >= 20);
		System.out.println("6. This rootsegment should have more than round 20 childsegments, return value: " + root.getChildren().size());
		assertTrue("The rootsegment should have 110 fields", root.getFields().size() >= 100);
		System.out.println("7. The rootsegment should have more than 100 fields (110), the size of fieldslist: " + root.getFields().size());
		System.out.println("Test the methods of class Field:");
		Iterator<IDocField> fields = root.getFields().iterator();
		IDocField field = fields.next();
		System.out.println(".1. Test getName(), the first Field of rootsegment should be MSGFN, return value: " + field.getFieldName());
		assertTrue("The first Field of rootsegment should be MSGFN", field.getFieldName().equalsIgnoreCase("MSGFN"));
		System.out.println(".2. Test getDatatype(), the datatype of the first Field should be CHAR, return value: " + field.getDataType().getDataTypeName());
		assertTrue("The datatype of the first Field should be CHAR", field.getDataType().getDataTypeName().equalsIgnoreCase("CHAR"));
		System.out.println(".3. Test getCheckTable(), there should be no checktable, return value: " + field.getCheckTable());
		assertTrue("The first Field of rootsegment should have no checktable", field.getCheckTable()== null);
		System.out.println(".4. Test getSegment(), the segment should be the rootsegment E1KNA1M, return value: " + field.getSegment().getType());
		assertTrue("the segment should be the rootsegment E1KNA1M", field.getSegment().equals(root));

		// Test1.2 test method getSegment(String SegmentName) of
		// class IDocTypeMetaData, expect to return the Segment whose type or
		// definition is like SegmentName
		
		// Test1.2.1 (SegmentName = "E1KNA1M"), expect to return the rootsegment
		Segment segment = metadata.getSegment("E1KNA1M");
		assertEquals("Return should be the rootsegment E1KNA1M", segment, root );
		
		// Test1.2.2 (SegmentName = "E2KNA1M005"), expect to return the rootsegment
		segment = metadata.getSegment("E2KNA1M005");
		assertEquals("Return should be the rootsegment E1KNA1M", segment, root );
		
		// Test1.2.3 (SegmentName = "E2WRF12"), expect to return segment E1WRF12
		segment = metadata.getSegment("E2WRF12");
		assertTrue("Return should be the segment No.19 E1WRF12", segment.getNr() == 19);
		assertEquals("This segment should be the child of rootsegment", segment.getParentSegment(), root);
		assertTrue("The segment should have no child", !segment.hasChild);
		
		// Test1.2.4 (SegmentName = "E1KNKKH"), expect to return segment E1KNKKH
		segment = metadata.getSegment("E1KNKKH");
		assertTrue("Return should be the segment No.19 E1WRF12", segment.getNr() == 28);
		assertTrue("This segment should be the child of segment E1KNKKM", segment.getParentSegment().getDefinition().equals("E2KNKKM001"));
		assertTrue("This segment should have a child segment E1KNKKL", (segment.getChildren().size()==1)||(segment.getChildren().iterator().next().getType().equals("E1KNKKL")));
		
		// Test1.2.5 (SegmentName = "E2KNA1M004"), expect to return null
		segment = metadata.getSegment("E2KNA1M004");
		assertTrue("There should be no segment with definition E2KNA1M004", segment == null);
		
		// MessageTypes test:
		Iterator<MessageType> iterator = metadata.getMessageTypes().iterator();
		MessageType message = iterator.next();
		assertTrue("The first MessageType ist DEBMAS", message.getName().equals("DEBMAS") || message.getDescription().trim().equalsIgnoreCase("Customer master data distribution"));
				
		// Test2(IDocType = null), expect to return a null value and a warning
		// message
		System.out.print("Output the warning message: ");
		metadata = browser.getIDocTypeMetaData(null);
		assertTrue("Return null value", metadata == null);

		// Test3(IDocType = debmas), expect IDocTypeExtractException, see
		// testException2()
	}

	@Test(expected = JCoException.class)
	public void testException2() throws JCoException {
		IDocType newIDoctype = new IDocType("debmas", false, "", null, browser);
		IDocTypeMetaData metadata = browser.getIDocTypeMetaData(newIDoctype);
	}

	@Test
	public void testIDocType() throws Exception {
		// Test test the methods of class IDocType
		System.out.println("Test IDocType: " + idoctype.getName());
		IDocType testIDoctype = new IDocType(idoctype.getName(), idoctype.isExtendedIDocType(), idoctype.getDescription(), idoctype.getRelease(), browser);
		assertTrue("A new IDocType object should be created", testIDoctype != null);

		// Test1 test getName()
		assertTrue("The Name of testIDoctype should be " + idoctype.getName(), testIDoctype.getName().equals(idoctype.getName()));

		// Test2 test getMetaData()
		assertTrue("Get the Metada for the new IDocType object", testIDoctype.getMetaData()!= null);
		
		IDocType test = new IDocType("DEBMAS", false, "test", null, browser);
		assertTrue("A new IDocType object shoud be created with name DEBMAS", test.getName().equalsIgnoreCase("DEBMAS"));
		//assertFalse("Find metadata of the new IDocType object", test.getMetaData() == null);
	}

}
