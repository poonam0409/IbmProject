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
// IBM Information Server R/3 Pack IDoc Load 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.idocload.online.test
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload.online.test;

import java.util.HashMap;
import java.util.Map;

import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.DSEnvironment;
import com.ibm.is.sappack.dsstages.common.DSSAPConnection;
import com.ibm.is.sappack.dsstages.common.IDocMetadataFactory;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.SapSystem;
import com.ibm.is.sappack.dsstages.common.ccf.IDocConnection;
import com.ibm.is.sappack.dsstages.common.impl.RfcDestinationDataProvider;
import com.ibm.is.sappack.dsstages.common.test.DSSAPConnectionsDirSetup;
import com.ibm.is.sappack.dsstages.idocload.IDocSender;
import com.ibm.is.sappack.dsstages.idocload.SegmentCollector;
import com.ibm.is.sappack.dsstages.idocload.sender.IDocSenderImpl;
import com.sap.conn.jco.JCoDestination;


/**
 * OnlineIDocSenderTest
 * 
 * Test case class for the IDocSenderImpl
 *
 */
@SuppressWarnings("nls")
public class OnlineIDocSenderTest extends DSSAPConnectionsDirSetup {
	
	static String copyright()
	{ return com.ibm.is.sappack.dsstages.idocload.online.test.Copyright.IBM_COPYRIGHT_SHORT; }
	
	/* IDocSender */
	private IDocSender idocSender;
	/* DSSAPConnection */
	private IDocConnection connection;
	/* Stage properties */
	private Map<String, String> stageProps;
	private DSEnvironment dsEnvironment;
	IDocType idocType;
	
	IDocTestDataGenerator generator;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		System.out.println("Logging to "+System.getProperty("java.io.tmpdir"));
		
		
		/* create stage properties */
		this.stageProps = new HashMap<String, String>();
		/* IDoc Type */
		stageProps.put(Constants.IDOCSTAGE_STAGE_PROP_IDOCTYP, "DEBMAS06");
		/* Message Type */
		stageProps.put(Constants.IDOCSTAGE_STAGE_PROP_MESTYP, "DEBMAS");
		/* Number of IDocs per transaction - package size */
		stageProps.put(Constants.IDOCSTAGE_STAGE_PROP_IDOCSPERTRANS, "1500");
		
		/* SAP logon defaults */
		stageProps.put(Constants.IDOCSTAGE_STAGE_PROP_DEFSAPLOGON, "1");
		stageProps.put(Constants.IDOCSTAGE_STAGE_PROP_USERNAME, "osuhre");
		stageProps.put(Constants.IDOCSTAGE_STAGE_PROP_PASSWORD, "saptest");
		stageProps.put(Constants.IDOCSTAGE_STAGE_PROP_CLIENT, "800");
		stageProps.put(Constants.IDOCSTAGE_STAGE_PROP_LANGUAGE, "EN");
		
		/* create SAP connection */
		final DSSAPConnection conn = IDocMetadataFactory.createDSSAPConnection();
		conn.initialize("BOCASAPERP5");
				
		SapSystem sapSys = conn.getSapSystem();
		sapSys.setClientId(800);
		sapSys.setUserName("osuhre");
		sapSys.setPassword("saptest");
		sapSys.setClientId(800);
		sapSys.setLanguage("EN");
		final JCoDestination jcoDest = RfcDestinationDataProvider.getDestination(sapSys);
		this.idocType = IDocMetadataFactory.createIDocType(jcoDest, sapSys.getName(), "DEBMAS06", null, null);
		this.connection = new IDocConnection() {

			@Override
			public void connect() {
			}

			@Override
			public void disconnect() {
			}

			@Override
			public DSSAPConnection getDSSAPConnection() {
				return conn;
			}

			@Override
			public IDocType getIDocType() {
				return idocType;
			}

			@Override
			public JCoDestination getJCODestination() {
				return jcoDest;
			}
			
		};
		
		this.generator = new IDocTestDataGenerator(conn, this.idocType);
		
		this.idocSender = new IDocSenderImpl();
		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	void validate(SegmentCollector coll) {
		/*
		boolean success = true;
		List<SegmentValidationResult> valResults = coll.validateSegments();
		for (SegmentValidationResult r : valResults) {
			TestLog.log( "Validation warning: " + r.getType() + ": " + r.getMessage() );
			success = false;
		}
		if (!success) {
			throw new RuntimeException("Segment validation failed!!");
		}
		*/
	}
	
	/**
	 * testInitializeDEBMAS06
	 * @throws Exception
	 */
	public void testInitializeDEBMAS06() throws Exception {
		
		/* initialized IDocSender */
		this.idocSender.initialize(this.connection, stageProps, dsEnvironment);	
	}
	
	
	/**
	 * testSendSingleIDocDEBMAS06
	 * 
	 * send one IDoc of type DEBMAS06 to SAP
	 * 
	 * @throws Exception
	 */
	public void testSendSingleIDocDEBMAS06() throws Exception {
		/* get SegmentCollector */
		
		
		SegmentCollector segmentCollector = generator.createSingleDEBMAS06();
		validate(segmentCollector);
		
		/* initialized IDocSender */
		this.idocSender.initialize(this.connection, stageProps, dsEnvironment);	
		
		/* send IDocs */
		this.idocSender.sendIDocs(segmentCollector);
	}
	
	
	
	
	/**
	 * testSendSingleIDocDEBMAS06WithCR
	 * 
	 * send one IDoc of type DEBMAS06 with control record to SAP
	 * 
	 * @throws Exception
	 */
	public void testSendSingleIDocDEBMAS06WithCR() throws Exception {
		
		/* get SegmentCollector */
		SegmentCollector segmentCollector = generator.createSingleDEBMAS06WithCR();
		validate(segmentCollector);

		
		
		/* initialized IDocSender */
		this.idocSender.initialize(this.connection, stageProps, dsEnvironment);	
		
		/* send IDocs */
		this.idocSender.sendIDocs(segmentCollector);
	}
	
	/**
	 * testSendDEBMAS06TenTimes
	 * 
	 * send an DEBMAS06 ten times to SAP
	 * 
	 * @throws Exception
	 */
	public void testSendDEBMAS06TenTimes() throws Exception {
		
		/* get SegmentCollector */
		SegmentCollector segmentCollector = generator.createSingleDEBMAS06WithCR();
		validate(segmentCollector);

		/* initialized IDocSender */
		this.idocSender.initialize(this.connection, stageProps, dsEnvironment);	
		
		/* send IDoc ten times to SAP */
		for(int i=0; i<10; i++) {
			/* send IDocs */
			this.idocSender.sendIDocs(segmentCollector);
		}
	}
	
	
	
	/**
	 * testSendMultiIDocDEBMAS06
	 * 
	 * send two IDocs of type DEBMAS06 to SAP
	 * 
	 * @throws Exception
	 */
	public void testSendMultiIDocDEBMAS06() throws Exception {
		
		/* get SegmentCollector */
		SegmentCollector segmentCollector = generator.createMultiDEBMAS06();
		validate(segmentCollector);

		/* initialized IDocSender */
		this.idocSender.initialize(this.connection, stageProps, dsEnvironment);	
		
		/* send IDocs */
		this.idocSender.sendIDocs(segmentCollector);
	}
	
	
//	/**
//	 * testSend1000IDocDEBMAS06
//	 * 
//	 * send 1000 IDocs of type DEBMAS06 to SAP
//	 * 
//	 * @throws Exception
//	 */
//	public void testSend1000IDocDEBMAS06() throws Exception {
//		
//		/* get SegmentCollector */
//		SegmentCollector segmentCollector = IDocTestDataGenerator.create1000DEBMAS06();
//		
//		/* initialized IDocSender */
//		this.idocSender.initialize(this.connection, stageProps);	
//		
//		/* send IDocs */
//		this.idocSender.sendIDocs(segmentCollector);
//	}
//	

	
}
