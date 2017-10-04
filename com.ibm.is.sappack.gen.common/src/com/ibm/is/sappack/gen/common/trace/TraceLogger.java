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
// Module Name : com.ibm.is.sappack.gen.common.trace
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.trace;




/**
 * This class provides methods for tracing. 
 *
 * @since 1.0.0
 * @version 1.0.0
**/
public final class TraceLogger
{
   // ======================================================================
   //                             constants
   // ======================================================================
   private static final String  TRACE_COMPONENT_ID  = "com.ibm.is.SAPPacks";
   
   /** Trace level: info */
   public    static final TraceEventType LEVEL_INFO          = TraceLoggerImpl.LEVEL_INFO;
   /** Trace level: fine */
   public    static final TraceEventType LEVEL_FINE          = TraceLoggerImpl.LEVEL_FINE;
   /** Trace level: finer */
   public    static final TraceEventType LEVEL_FINER         = TraceLoggerImpl.LEVEL_FINER;
   /** Trace level: finest */
   public    static final TraceEventType LEVEL_FINEST        = TraceLoggerImpl.LEVEL_FINEST;
   /** Trace level: event */
   public    static final TraceEventType EVENT               = TraceLoggerImpl.LEVEL_FINE;

   
   // ======================================================================
   //                         member variables
   // ======================================================================
   /** flag if trace is enabled: true or false */ 
   private  static   boolean          isTraceEnabled;
   /** TraceLogger implementation */ 
   private static   TraceLoggerImpl  _TraceLogger;

   
   // ======================================================================
   //                          static block
   // ======================================================================
   static
   {
      _TraceLogger = TraceLoggerImpl.newTraceLogger(TRACE_COMPONENT_ID);
      refreshCachedSettings();
   } // end of static

   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.trace.Copyright.IBM_COPYRIGHT_SHORT; 
   }   

   
   /**
    * This method initializes the local tracing into a certain file. The 
    * parameter 'pUseParentLogger' specifies if the parent logger is 
    * additonally used to the trace file handler.
    * 
    * @param pTraceFileName  trace file name
    * @param pUseParentLogger  use the parent logger additionally: true or false
    * 
    * @throws IllegalStateException if already initialized
   **/
   public static final void initialize(String pTraceFileName, boolean pUseParentLogger)
          throws IllegalStateException
   {
      // delegate the method call
      _TraceLogger.initialize(pTraceFileName, pUseParentLogger);
      isTraceEnabled = true;
   } // end of initialize()

   
   /**
    * This method de-initializes the local tracing.
    * 
    * @throws IllegalStateException if not initialized
   **/
   public static final void deInitialize()
          throws IllegalStateException
   {
      // delegate the method call
      _TraceLogger.deInitialize();
      isTraceEnabled = false;
   } // end of deInitialize()

   
   /**
    * This method determines if a log entry will be processed.
    *  
    * @param pTraceEventType  type of the trace entry
    * 
    * @return true if the logger is enabled otherwise false
   **/
   public static final boolean isLogging(TraceEventType pTraceType)
   {
      // delegate the method call
      return(_TraceLogger.isLogging(pTraceType));
     } // end of isLogging()


   /**
    * This method traces the entry into a method.
   **/
   public static void entry()
   {
      // delegate the method call
      entry((Object[]) null);
   } // end of entry()

     
   /**
    * This method traces the entry into a method with a parameter.
    * 
    * @param pTraceParam   parameter to be traced
   **/
   public static void entry(Object pTraceParam)
   {
      if (isTraceEnabled)
      {
         // delegate the method call
         entry(new Object[] { pTraceParam });
      } // end of if (isTraceEnabled)
   } // end of entry()

    
   /**
    * This method traces the entry into a method with an array of parameters.
    * 
    * @param pParamArr array of parameters to be traced
   **/
   public static void entry(Object pParamArr[])
   {
      if (isTraceEnabled)
      {
         // delegate the method call
         _TraceLogger.entry(pParamArr);
      } // end of if (isTraceEnabled)
   } // end of entry()

   
   /**
    * This method traces the exit from a method.
   **/
   public static void exit()
   {
      // delegate the method call
      exit((Object[]) null);
   } // end of exit()

     
   /**
    * This method traces the exit from a method with a parameter.
    * 
    * @param pTraceParam   parameter to be traced
   **/
   public static void exit(Object pTraceParam)
   {
      // delegate the method call
      _TraceLogger.exit(pTraceParam);
   } // end of exit()

    
   /**
    * This method returns if the TraceLogger has been initialized.
    * 
    *  @return true if it is initialized otherwise false
   **/
   public static boolean isInitialized()
   {
      return(_TraceLogger.isInitialized());
   } // end of isInitialized()

   
   /**
    * This method refreshes the cached trace settings, for example, 
    * 'is trace enabled', 'trace level' etc. .
   **/
   public static synchronized void refreshCachedSettings()
   {
      isTraceEnabled = _TraceLogger.isLogging(null);
   } // end of refreshCachedSettings()

   
   /**
    * This method enables/disables the tracing.
    * 
    * @param pDoTrace  true to enable the trace, false to disable it
   **/
   public static void setTracing(final boolean pDoTrace)
   {
      // delegate the method call
      _TraceLogger.setTracing(pDoTrace);
      isTraceEnabled = pDoTrace;
   } // end of setLogging()
    
   
   /**
    * This method traces a text message without any parameters.
    * 
    * @param pEventType  trace event type
    * @param pText       text to be traced
   **/
   public static void trace(TraceEventType pEventType, String pText)
   {
      // delegate the method call
      trace(pEventType, pText, null);
   } // end of trace()

   
   /**
    * This method traces a text message with one parameter.
    * 
    * @param pEventType  trace event type
    * @param pText       text to be traced
    * @param pParam      parameter
   **/
   public static void trace(TraceEventType pEventType, String pText, Object pParam)
   {
      if (isTraceEnabled)
      {
         // delegate the method call
         trace(pEventType, pText, new Object[] { pParam } );
      } // end of if (isTraceEnabled)
   } // end of trace()

    
   /**
    * This method traces a text message with two parameters.
    * 
    * @param pEventType  trace event type
    * @param pText       text to be traced
    * @param pParam1     parameter 1
    * @param pParam2     parameter 2
   **/
   public static void trace(TraceEventType pEventType, String pText, Object pParam1, Object pParam2)
   {
      if (isTraceEnabled)
      {
         // delegate the method call
         trace(pEventType, pText, new Object[] { pParam1, pParam2 } );
      } // end of if (isTraceEnabled)
   } // end of trace()


   
    /**
     * This method traces a text message with an array of parameters.
     * 
     * @param pEventType  trace event type
     * @param pText       text to be traced
     * @param pParamArr   array of parameters to be traced
    **/
    public static void trace(TraceEventType pEventType, String pText, Object[] pParamArr)
    {
       if (isTraceEnabled)
       {
          // delegate the method call
          _TraceLogger.trace(pEventType, pText, pParamArr);
       } // end of if (isTraceEnabled)
    } // end of trace()

    
    /**
     * This method traces an exception and its stacktrace.
     * 
     * @param pExcpt  exception to be traced
    **/
    public static void traceException(Throwable pExcpt)
    {
       if (isTraceEnabled)
       {
          // delegate the method call
          _TraceLogger.traceException(pExcpt);
       } // end of if (isTraceEnabled)
    } // end of traceException()
    
    public static void addFilter(TraceStringFilter f) {
   	 _TraceLogger.addFilter(f);
    }
    
    public static boolean isTraceEnabled() {
   	 return isTraceEnabled;
    }
     
} // end of class TraceLogger
