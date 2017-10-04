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
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.idoctypes
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.idoctypes;


public class MessageType {

	private String name;
	private String desription;
	private String idoctype;


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public MessageType(String name, String descrp, String idoctype){
		
		this.name = name;
		this.desription = descrp;
		this.idoctype = idoctype;
		
		
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getDescription(){
		return this.desription;
	}
	
	public String forIDoctype(){
		return this.idoctype;
	}
	
}
