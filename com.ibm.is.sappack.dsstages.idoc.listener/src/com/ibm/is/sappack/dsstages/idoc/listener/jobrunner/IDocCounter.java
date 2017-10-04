//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener.jobrunner
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener.jobrunner;


/**
 * IDocCounter
 * 
 * Interface to count and persist the
 * number of incoming IDocs from SAP 
 */
public interface IDocCounter {

	static final String IBM_COPYRIGHT_SHORT = Copyright.IBM_COPYRIGHT_SHORT;
	
	/**
	 * initialize the counter
	 * 
	 * @param idocTypeDir
	 * @param idocType
	 */
	public void init(String idocTypeDir, String idocType);
	
	/**
	 * increment the counter
	 * by the given number
	 * 
	 * @param number
	 */
	public void increment(int number);
	
	/**
	 * decrement the counter
	 * by the given number
	 * 
	 * @param number
	 */
	public void decrement(int number);
	
	/**
	 * reset the counter to its
	 * initial value
	 * 
	 */
	public void reset();
	
	
	/**
	 * get the counter value
	 * @return
	 * 
	 */
	public int getValue();
}
