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
// Module Name : com.ibm.is.sappack.gen.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common;



public final class StopWatch
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public static final int  JOB_PARAM_TYPE_STRING     = 0;
   public static final int  JOB_PARAM_TYPE_ENCYRYPTED = 1;
   public static final int  JOB_PARAM_TYPE_INTEGER    = 2;
   public static final int  JOB_PARAM_TYPE_FLOAT      = 3;
   public static final int  JOB_PARAM_TYPE_PATHNAME   = 4;
   public static final int  JOB_PARAM_TYPE_LIST       = 5;
   public static final int  JOB_PARAM_TYPE_DATE       = 6;
   public static final int  JOB_PARAM_TYPE_TIME       = 7;
   public static final int  JOB_PARAM_TYPE_PARAM_SET  = 8;
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private long    _StartTime    = -1;
   private double  _lastRunTime  = 0.0d;

   static String copyright()
   { return com.ibm.is.sappack.gen.common.Copyright.IBM_COPYRIGHT_SHORT; }
   
   public StopWatch()
   {
      _StartTime   = -1;
      _lastRunTime = -1;
   } // end of StopWatch()
   
   public StopWatch(boolean startWatch)
   {
      this();
      
      if (startWatch)
      {
         start();
      }
   } // end of StopWatch()
   
   private double getCurrentTime()
   {
      long   tmpLong;
      double retDouble;
      
      tmpLong   = (System.currentTimeMillis() - _StartTime) / 100;
      retDouble = ((double) tmpLong) / 10.0d;
      
      return(retDouble);
   }
   
   public double getLastRuntime()
   {
      return(_lastRunTime);
   }
   
   public void restart()
   {
      _StartTime   = -1;
      _lastRunTime = -1;
      start();
   }
   
   public void start()
   {
      if (_StartTime < 0)
      {
         _StartTime = System.currentTimeMillis();
      }
   }
   
   public double stop()
   {
      _lastRunTime = getCurrentTime();
      _StartTime   = -1;
      
      return(_lastRunTime);
   }
   
   public String toString()
   {
      double timeToReturn;
      
      // if watch is running ...
      if (_StartTime > 0)
      {
         timeToReturn = getCurrentTime();
      }
      else
      {
         // not running any more ==> get last runtime
         timeToReturn = _lastRunTime;
      }
         
      return(valueOf(timeToReturn));
   }
   
   public String valueOf(double runTime)
   {
      return(runTime + "s"); //$NON-NLS-1$
   }
} // end of class StopWatch
