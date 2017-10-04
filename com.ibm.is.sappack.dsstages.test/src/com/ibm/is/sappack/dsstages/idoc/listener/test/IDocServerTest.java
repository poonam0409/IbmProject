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
// IBM Information Server R/3 Pack IDoc Listener
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import com.ibm.is.sappack.dsstages.idoc.listener.IDocServer;
import com.ibm.is.sappack.dsstages.idoc.listener.IDocServerImpl;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerException;

public class IDocServerTest extends TestCase {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.listener.test.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/* IDoc Listener arguments */
	private String[] args = null;

	/**
	 * setUp
	 */
	protected void setUp() throws Exception {
		super.setUp();

		this.args = new String[34];

		/* read IDoc Listener properties from DSSAPConnections.config file */
		FileInputStream fstream = new FileInputStream("DSSAPConnections.config"); //$NON-NLS-1$
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		int line = 0;
		/* read configuration file line by line */
		while ((strLine = br.readLine()) != null) {
			/* add line to arguments */
			args[line] = strLine;
			line++;
		}
		//Close the input stream
		in.close();

	}

	/**
	 * tearDown
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}


	/**
	 * Test method for
	 * {@link com.ibm.is.sappack.dsstages.idoc.listener.IDocServerImpl#start()}.
	 * @throws IDocServerException 
	 * @throws InterruptedException 
	 */
	public void testIDocServer() throws IDocServerException, InterruptedException {

		IDocServer idocServer = new IDocServerImpl();
		/* init IDocServer */
		idocServer.initialize(args);
		
		/* start server */
		idocServer.start();
		
		while(true) {
			Thread.sleep(1000);
		}
	
//		
//		/* status should be running */
//		assertEquals(true, status.isRunning());
//		
//		/* lastException should be null */
//		assertNull(status.getLastException());
//		
//		/* stop server */
//		idocServer.stop();
//		
//		/* status should be stopped */
//		assertEquals(false, status.isRunning());
//		
//		/* lastException should be null */
//		assertNull(status.getLastException());
		
	}



}
