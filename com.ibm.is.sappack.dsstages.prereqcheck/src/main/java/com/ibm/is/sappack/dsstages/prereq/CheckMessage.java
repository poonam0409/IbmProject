//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2012, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.prereq
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.prereq;


public class CheckMessage {
	static String copyright() {
		return com.ibm.is.sappack.dsstages.prereq.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public  enum TYPE {
		ERROR, PASSED, WARNING
	};

	private TYPE type;
	private String testDescription;
	private String resultDescription;
	private String recommendation;


	public CheckMessage(TYPE type, String testDescription, String resultDescription, String recommendation) {
		super();
		this.type = type;
		this.testDescription = testDescription;
		this.resultDescription = resultDescription;
		this.recommendation = recommendation;
	}

	public TYPE getType() {
		return type;
	}

	public String getTestDescription() {
		return testDescription;
	}

	public String getResultDescription() {
		return resultDescription;
	}

	public String getRecommendation() {
		return recommendation;
	}
	
	public static void main(String ...argd)
	{
	TYPE t=	TYPE.ERROR;
	TYPE.valueOf("ERROR");
	TYPE [] tarray =TYPE.values();
	for(TYPE arr: tarray)
		System.out.println(arr);
		
	System.out.println(TYPE.valueOf("ERROR"));
	}

}
