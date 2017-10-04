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


public final class NextCounter 
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   private static final int     DEFAULT_START_VALUE  = 0;
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private int _CurValue;
   
   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.server.common.util.Copyright.IBM_COPYRIGHT_SHORT; 
   }      

   
   /**
    * Constructor
    */
   public NextCounter()
   {
      this(DEFAULT_START_VALUE);
   } // end of NextCounter()
   
   
   /**
    * Constructor
    */
   public NextCounter(int startValue)
   {
      _CurValue = startValue;
   } // end of NextCounter()
   
   
   public int getCurrentValue()
   {
      return(_CurValue);
   } // end of getCurrentValue()

   
   public Integer getNextValue()
   {
      _CurValue ++;
      
      return(new Integer(_CurValue));
   } // end of getNextValue()

   
   public String toString()
   {
      return(Integer.toString(_CurValue));
   } // end of toString()
   
} // end of class NextCounter
