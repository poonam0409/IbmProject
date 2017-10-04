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

import java.io.IOException;

import com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.IDocCounter;
import com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.IDocCounterImpl;

import junit.framework.TestCase;

/**
 * IDocCounterTest
 *
 */
public class IDocCounterTest extends TestCase {

	/**
	 * Test method for {@link com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.IDocCounterImpl#IDocCounterImpl()}.
	 */
	public void testIDocCounterImpl() {
		
		IDocCounter counter = new IDocCounterImpl();
	}

	/**
	 * Test method for {@link com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.IDocCounterImpl#init(java.lang.String, java.lang.String)}.
	 * @throws IOException 
	 */
	public void testInit() throws IOException {
		
		IDocCounter counter = new IDocCounterImpl();
		counter.init("C:\\IBM\\InformationServer\\Server\\DSSAPConnections\\BOCASAPIDES5\\IDocTypes\\", "DEBMAS06");
		/* value should be 0 or > 0 */
		int value = counter.getValue();
		assertTrue(value >=0);
	}

	/**
	 * Test method for {@link com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.IDocCounterImpl#increment(int)}.
	 * @throws IOException 
	 */
	public void testIncrement() throws IOException {
		
		IDocCounter counter = new IDocCounterImpl();
		counter.init("C:\\IBM\\InformationServer\\Server\\DSSAPConnections\\BOCASAPIDES5\\IDocTypes\\", "DEBMAS06");
		/* value should be 0 or > 0 */
		int value = counter.getValue();
		
		/* increment 5 */
		counter.increment(5);
		
		/* increment 3 */
		counter.increment(3);
		
		/* value should be old value + 8 */
		int newValue = counter.getValue();
		
		assertEquals(newValue, value+8);
		
	}

	/**
	 * Test method for {@link com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.IDocCounterImpl#reset()}.
	 * @throws IOException 
	 */
	public void testReset() throws IOException {
	
		IDocCounter counter = new IDocCounterImpl();
		counter.init("C:\\IBM\\InformationServer\\Server\\DSSAPConnections\\BOCASAPIDES5\\IDocTypes\\", "DEBMAS06");
		counter.reset();
		int value = counter.getValue();
		/* value should be 0 */
		assertEquals(value, 0);
	}

	/**
	 * Test method for {@link com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.IDocCounterImpl#getValue()}.
	 * @throws IOException 
	 */
	public void testGetValue() throws IOException {
		IDocCounter counter = new IDocCounterImpl();
		counter.init("C:\\IBM\\InformationServer\\Server\\DSSAPConnections\\BOCASAPIDES5\\IDocTypes\\", "DEBMAS06");
		int value = counter.getValue();
		assertTrue(value >= 0);
	}
	
	/**
	 * Test method for {@link com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.IDocCounterImpl#decrement()}.
	 * @throws IOException 
	 */
	public void testDecrement() throws IOException {
		IDocCounter counter = new IDocCounterImpl();
		counter.init("C:\\IBM\\InformationServer\\Server\\DSSAPConnections\\BOCASAPIDES5\\IDocTypes\\", "DEBMAS06");
		
		counter.increment(10);
		
		/* value should be 0 or > 0 */
		int value = counter.getValue();
		
		/* decrement 5 */
		counter.decrement(5);
		
		/* decrement 3 */
		counter.decrement(1);
		
		/* value should be old value - 6 */
		int newValue = counter.getValue();
		
		assertEquals(newValue, value-6);
		
		/* reset counter */
		counter.reset();
		
		/* decrement again to test negative values */
		counter.decrement(10);
		
		newValue = counter.getValue();
		
		assertTrue(newValue >=0);
	}

}
