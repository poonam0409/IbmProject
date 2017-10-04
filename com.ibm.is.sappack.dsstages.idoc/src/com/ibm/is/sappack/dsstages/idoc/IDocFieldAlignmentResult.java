//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2012                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.idoc
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc;

/**
 * This class holds the alignment result
 * that is produced by a IDocFieldAligner
 */
public interface IDocFieldAlignmentResult {

	/**
	 * getLength
	 * 
	 * get the updated field length
	 * of the aligned IDoc segment field.
	 * 
	 * @return the length of the aligned IDoc segment field
	 */
	public int getLength();
	
	/**
	 * getData
	 * 
	 * get the aligned IDoc segment field data.
	 * The field data might have been truncated
	 * during the alignment if the original data
	 * was to big to fit into the updated IDoc
	 * field length.
	 * 
	 * @return the aligned IDoc segment field data
	 */
	public char [] getData();
	

}
