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
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener.handler
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener.handler;


/**
 * IDocFile
 */
public interface IDocFile {

	static final String IBM_COPYRIGHT_SHORT = Copyright.IBM_COPYRIGHT_SHORT;
	
	/**
	 * getIDocNumber
	 * 
	 * get the IDoc number in original
	 * format with leading zeros
	 * 
	 * @return
	 */
	public String getIDocNumber();
	
	/**
	 * getIDocType
	 * 
	 * get the IDocType of this IDocFile
	 * 
	 * @return
	 */
	public String getIDocType();
	
	/**
	 * getSenderPartnerNumber
	 * 
	 * get the sender partner number of this IDocFile
	 * 
	 * @return
	 */
	public String getSenderPartnerNumber();
	
	/**
	 * getReceiverPartnerNumber
	 * 
	 * get the receiver partner number of this IDocFile
	 * 
	 * @return
	 */
	public String getReceiverPartnerNumber();
	
	/**
	 * commit
	 */
	public void commit();
	
	/**
	 * rollback
	 */
	public void rollback();
	
}
