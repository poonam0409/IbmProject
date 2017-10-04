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
// Module Name : com.ibm.is.sappack.gen.server.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.util;



public class StageVariable extends FlowVarData
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   
	
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------

   static String copyright()
   { return com.ibm.is.sappack.gen.server.util.Copyright.IBM_COPYRIGHT_SHORT; }
   
   public StageVariable(String name, String type, String desc, Integer length, 
                        String derivation)  
   {
      super(name, desc);
      
      setLength(length);
      setDerivation(derivation);
      setType(type);
   }
   
   
   public int getScale()
   {
      return(0);
   }

   
   protected void validate()
   {
      // nothing to do here
      ;
   }

} // end of class StageVariable
