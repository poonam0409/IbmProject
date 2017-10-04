//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.server.common.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.common.util;


public class DSAccessJobLockedException extends DSAccessException 
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   protected static final long serialVersionUID = DSAccessException.serialVersionUID + 5;

   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------


	static String copyright()	{ 
	   return com.ibm.is.sappack.gen.server.common.util.Copyright.IBM_COPYRIGHT_SHORT; 
	}   

	
   public DSAccessJobLockedException(String jobName) {
      this(jobName, null);
   } // end of DSAccessJobLockedException()
   
   
   public DSAccessJobLockedException(String jobName, Throwable cause) {
      super("101400E", new String[] { jobName }, cause);
   } // end of DSAccessJobLockedException()
	
} // end of class DSAccessJobLockedException
