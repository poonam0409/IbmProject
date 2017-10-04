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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.ibm.is.sappack.gen.common.Constants;



/**
 * This class provides base methods for tracing and logging. 
 *
 * @since 1.0.1
 * @version 1.0.1
**/
public class TraceLoggerImpl
{
   // ======================================================================
   //                             constants
   // ======================================================================
   protected static final String         COPYRIGHT           = "\n\n(C) Copyright IBM Corporation 2009, 2013.\n\n";
   private   static final String         PRODUCT_STRING      = "SAPPack Service";
   private   static final TraceEventType LEVEL_ENTRY         = new TraceEventType(Level.FINEST);
   private   static final TraceEventType LEVEL_EXIT          = LEVEL_ENTRY;

   /** Trace level: info */
   public    static final TraceEventType LEVEL_INFO          = new TraceEventType(Level.INFO);
   /** Trace level: fine */
   public    static final TraceEventType LEVEL_FINE          = new TraceEventType(Level.FINE);
   /** Trace level: finer */
   public    static final TraceEventType LEVEL_FINER         = new TraceEventType(Level.FINER);
   /** Trace level: finest */
   public    static final TraceEventType LEVEL_FINEST        = new TraceEventType(Level.FINEST);
   /** Trace level: event */
   public    static final TraceEventType EVENT               = new TraceEventType(Level.FINE);
      
   // ======================================================================
   //                         member variables
   // ======================================================================
   private Logger   _Logger;
   private String   _className;
   private boolean  _IsInitialized;
   private ArrayList _filters;

   static String copyright()
   { return com.ibm.is.sappack.gen.common.trace.Copyright.IBM_COPYRIGHT_SHORT; }
   
   /**
    * This private constructor creates a logger instance and initializes the 
    * member variables.
    *  
    * @param pProduct    product string
    * @param pId         trace id (package to be traced)
    * @param pClassName  trace class name
   **/
   private TraceLoggerImpl(String pProduct, String pId, String pClassName)
   {
     // The parent logger of this logger is com.ibm.ws.logging.WsLogger, its parent is java.util.logging.LogManager$RootLogger
     _Logger = Logger.getLogger(pId);
     
     // To avoid superfluous 'com.ibm.is.sappack.util.TraceLog' in the trace:
     // [9/12/04 14:33:56:810 CEST] 0000000a TraceJSAS  1 com.ibm.is.sappack.TraceLogger com.ibm.is.sappack.util.TraceLogger.newTraceLogger(TraceLogger.java:123)
//     _className = pClassName;
     _className = " ";
     
     // initialize the list of filters
     _filters = new ArrayList();
     
     try
     {
//        trace(LEVEL_FINE, "Starting trace for " + pClassName, null);
        trace(LEVEL_FINE, "Starting trace for '" + pId 
                                                 + "' - server version = " + Constants.CLIENT_SERVER_VERSION, 
              null);
     }
     catch(Exception pExcpt)
     {
        System.err.println("Error when starting trace for " + pClassName + ": " + pExcpt.getMessage());
     } // end of catch(Exception pExcpt)
   } // end of TraceLoggerImpl() 
   
   
   /**
    * This factory method creates a new TraceLogger instance.
    * 
    * @param pTraceCompId  trace component Id id, for example "com.ibm.is.js.TraceJSAS"
    *    
    * @return new TraceLoggerImpl instance
   **/
   public static TraceLoggerImpl newTraceLogger(String pTraceCompId)
   {
      return(newTraceLogger(TraceLoggerImpl.class, pTraceCompId));
   } // end of newTraceLogger()

   
   /**
    * This factory method creates a new TraceLogger instance.
    * 
    * @param pComponent    main class of the component that is tracing.
    * @param pTraceCompId  trace component Id id, for example "com.ibm.is.js.TraceJSAS"
    *    
    * @return new TraceLoggerImpl instance
   **/
   public static TraceLoggerImpl newTraceLogger(Class pComponent, String pTraceCompId)
   {
      String  vCompId;

      if (pComponent == TraceLoggerImpl.class)
      {
         vCompId = pTraceCompId;
      }
      else
      {
         vCompId = pComponent.getName();
      } // end of (else) if (component == TraceLoggerImpl.class)

      // The parent logger of this logger is com.ibm.ws.logging.WsLogger, its parent is java.util.logging.LogManager$RootLogger
      return(new TraceLoggerImpl(PRODUCT_STRING, vCompId, pComponent.getName()));
   } // end of newTraceLogger()

   
   /**
    * This method initializes the local tracing into a certain file. The 
    * parameter 'pUseParentLogger' specifies if the parent logger is 
    * additionally used to the trace file handler.
    * 
    * @param pTraceFileName  trace file name
    * @param pUseParentLogger  use the parent logger additionally: true or false
    * 
    * @throws IllegalStateException if already initialized
   **/
   public synchronized final void initialize(String pTraceFileName, 
                                             boolean pUseParentLogger)
          throws IllegalStateException
   {
      FileHandler vFileHandler;
      
      if (_IsInitialized)
      {
         throw new IllegalStateException("Tracing already initialized.");
      } // end of if (_IsInitialized)

      // go on if there has a filename specified
      if (pTraceFileName == null)
      {
         System.err.println("Error setting up trace FileHandler: Tracefile is null.");
      }
      else
      {
         try
         {
            // set up the trace file handler ...
            vFileHandler = new FileHandler(pTraceFileName);
            vFileHandler.setFormatter(new SimpleFormatter());
            _Logger.addHandler(vFileHandler);

            // ... and enable the tracing
            _IsInitialized = true;
            setTracing(true);
            
            // prevent the usage of the parent logger
            _Logger.setUseParentHandlers(pUseParentLogger);
         } // end of try
         catch(IOException pIOExcpt)
         {
            System.err.println("Error setting up trace FileHandler: " + pIOExcpt);
         } // end of catch(IOException pIOExcpt)
      } // end of (else) if (pTraceFileName == null) 
   } // end of initialize()

   
   /**
    * This method deinitializes the local tracing.
    * 
    * @throws IllegalStateException if not initialized
   **/
   public synchronized final void deInitialize()
          throws IllegalStateException
   {
      Handler vFileHandlerArr[];
      int     vArrIdx;
      
      if (!_IsInitialized)
      {
         throw new IllegalStateException("Tracing already deinitialized.");
      }

      // remove all trace file handlers ...
      vFileHandlerArr = _Logger.getHandlers();
      if (vFileHandlerArr != null) 
      {
         for (vArrIdx = 0; vArrIdx < vFileHandlerArr.length; vArrIdx ++) 
         {
          _Logger.removeHandler(vFileHandlerArr[vArrIdx]);
         } // end of for (vArrIdx = 0; vArrIdx < vFileHandlerArr.length; vArrIdx ++)
      } // end of if (vFileHandlerArr != null)
        
       // .. and disable the tracing
      setTracing(false);
      _IsInitialized = false;
   } // end of deInitialize()

   
   /**
    * This method returns the calling function in a stack trace of an 
    * exception as a string.
    *
    * @param pThrowable   throwable of which to extract the stack trace.
    * @param pMethodName  name of the method that created the exception.
    * 
    * @return The calling method as formatted by <tt>printStackTrace</tt>
   **/
   private String getMethodNameFromStack(Throwable pThrowable, String pMethodName)
   {
      StringBuffer        vMethodNameBuffer;
      StackTraceElement[] vStackArr;
      StackTraceElement   vStackTraceElement;
      int                 vStackElementIdx;
      boolean             vNameFound; 

      // get stack trace ...
      vStackArr         = pThrowable.getStackTrace();
      vMethodNameBuffer = new StringBuffer();
      vNameFound        = false;
      
      // walk through the passed stack elements
      vStackElementIdx = vStackArr.length - 1;
      while(vStackElementIdx >= 0 && !vNameFound)
      {
         // check if the current stack element contains the specified method name 
         if (pMethodName.equals(vStackArr[vStackElementIdx].getMethodName()))
         {
            // is this the top of the stack ???
            if (vStackElementIdx + 1 < vStackArr.length)
            {
               vStackTraceElement = vStackArr[vStackElementIdx + 1];
               
               vMethodNameBuffer.append(vStackTraceElement.getClassName());
               vMethodNameBuffer.append(".");
               vMethodNameBuffer.append(vStackTraceElement.getMethodName());

               // if a filename is specified ..
               if (vStackTraceElement.getFileName() != null)
               {
                  // ==> add the filename to the buffer
                  vMethodNameBuffer.append("(");
                  vMethodNameBuffer.append(vStackTraceElement.getFileName());

                  // if a line number is specified ..
                  if (vStackTraceElement.getLineNumber() > 0)
                  {
                     // ==> add the line number to the buffer
                     vMethodNameBuffer.append(":");
                     vMethodNameBuffer.append(vStackTraceElement.getLineNumber());
                  } // end of if (vStackTraceElement.getLineNumber() > 0)

                  vMethodNameBuffer.append(")");
               } // end of if (vStackTraceElement.getFileName() != null)
            } // end of if (vStackElementIdx + 1 < vStackArr.length)
           
            // ok, method name has been found
            vNameFound = true;
         } // end of if (pMethodName.equals(vStackArr[vStackElementIdx].getMethodName()))
         
         vStackElementIdx --;    // next stack element
      } // end of while(vStackElementIdx >= 0)
      
      return(vMethodNameBuffer.toString());
   } // end of getMethodNameFromStack()
    
    
   /**
    * This method determines if a log entry will be processed.
    *  
    * @param pTraceEventType  type of the trace entry
    * 
    * @return true if the logger is enabled otherwise false
   **/
   public final boolean isLogging(TraceEventType pTraceType)
   {
      // default trace type is: FINE
      if (pTraceType == null)
      {
         pTraceType = TraceLoggerImpl.LEVEL_FINE;
      } // end of if (pTraceType == null)

      return(_Logger.isLoggable(pTraceType.getLevel()));
   } // end of isLogging()


   /**
    * This method traces the entry into a method.
   **/
/*   
   public void entry()
   {
      entry((Object[]) null);
   } // end of entry()
*/
   
     
   /**
    * This method traces the entry into a method with a parameter.
    * 
    * @param pTraceParam   parameter to be traced
   **/
/*   
   public void entry(Object pTraceParam)
   {
      entry(new Object[] { pTraceParam });
   } // end of entry()
*/
    
   /**
    * This method traces the entry into a method with an array of parameters.
    * 
    * @param pParamArr array of parameters to be traced
   **/
   public void entry(Object pParamArr[])
   {
      // tracing allowed for entry tracing level only
      if (isLogging(TraceLoggerImpl.LEVEL_ENTRY) && filtersAllowLogging())
      {
         if (pParamArr == null)
         {
            _Logger.entering(_className, getMethodNameFromStack(new Throwable(), "entry"));
         }
         else
         {
            _Logger.entering(_className, getMethodNameFromStack(new Throwable(), "entry"), pParamArr);
         } // end of (else) if (pParamArr == null)
      } // end of if (isLogging(TraceLogger.LEVEL_ENTRY))
   } // end of entry()

   
   /**
    * This method traces the exit from a method.
   **/
/*   
   public void exit()
   {
      exit((Object[]) null);
   } // end of exit()
*/
   
     
   /**
    * This method traces the exit from a method with a parameter.
    * 
    * @param pTraceParam   parameter to be traced
   **/
   public void exit(Object pTraceParam)
   {
      // tracing allowed for exit tracing level only
      if (isLogging(TraceLoggerImpl.LEVEL_EXIT) && filtersAllowLogging())
      {
         if (pTraceParam == null)
         {
            _Logger.exiting(_className, getMethodNameFromStack(new Throwable(), "exit"));
         }
         else
         {
            _Logger.exiting(_className, getMethodNameFromStack(new Throwable(), "exit"), pTraceParam);
         } // end of (else) if (pTraceParam == null)
      } // end of if (isLogging(TraceLogger.LEVEL_EXIT))
   } // end of exit()

    
   /**
    * This method returns if the TraceLogger has been initialized.
    * 
    *  @return true if it is initialized otherwise false
   **/
   public synchronized boolean isInitialized()
   {
      return(_IsInitialized);
   } // end of isInitialized()

   
   /**
    * This method enables/disables the tracing.
    * 
    * @param pDoTrace  true to enable the trace, false to disable it
   **/
   public void setTracing(final boolean pDoTrace)
   {
      // requires LoggingPermission("control")
      AccessController.doPrivileged(new PrivilegedAction()
      {
         public Object run()
         {
           _Logger.setLevel(pDoTrace ? Level.ALL : Level.OFF);
           return(null);
         } // end of public Object run()
      });
   } // end of setTracing()
    
   
   /**
    * This method traces a text message without any parameters.
    * 
    * @param pEventType  trace event type
    * @param pText       text to be traced
   **/
/*   
   public void trace(TraceEventType pEventType, String pText)
   {
      trace(pEventType, pText, null);
   } // end of trace()
*/
   
   /**
    * This method traces a text message with one parameter.
    * 
    * @param pEventType  trace event type
    * @param pText       text to be traced
    * @param pParam      parameter
   **/
/*   
   public void trace(TraceEventType pEventType, String pText, Object pParam)
   {
      trace(pEventType, pText, new Object[] { pParam } );
   } // end of trace()
*/
   
    
   /**
    * This method traces a text message with two parameters.
    * 
    * @param pEventType  trace event type
    * @param pText       text to be traced
    * @param pParam1     parameter 1
    * @param pParam2     parameter 2
   **/
/*   
   public void trace(TraceEventType pEventType, String pText, Object pParam1, Object pParam2)
   {
      trace(pEventType, pText, new Object[] { pParam1, pParam2 } );
   } // end of trace()
*/

   
    /**
     * This method traces a text message with an array of parameters.
     * 
     * @param pEventType  trace event type
     * @param pText       text to be traced
     * @param pParamArr   array of parameters to be traced
    **/
    public void trace(TraceEventType pEventType, String pText, Object[] pParamArr)
    {
       // trace allowed for required level only
       if (isLogging(pEventType) && filtersAllowLogging())
       {
          if (pParamArr == null)
          {
             _Logger.logp(pEventType.getLevel(), _className, 
                          getMethodNameFromStack(new Throwable(), "trace"), pText);
          }
          else
          {
             _Logger.logp(pEventType.getLevel(), _className, 
                          getMethodNameFromStack(new Throwable(), "trace"), pText, pParamArr);
          } // end of (else) if (pParamArr == null)
       } // end of if (isLogging(pEventType))
    } // end of trace()

    
    /**
     * This method traces an exception and its stacktrace.
     * 
     * @param pExcpt  exception to be traced
    **/
    public void traceException(Throwable pExcpt)
    {
       ByteArrayOutputStream tmpByteStream;
       PrintStream           tmpPrintStream;

       // go on if the specfied exception object is valid
       if (pExcpt != null)
       {
          // copy the stacktrace into a ByteArrayOutputStream
          tmpByteStream  = new ByteArrayOutputStream();
          tmpPrintStream = new PrintStream(tmpByteStream);
          pExcpt.printStackTrace(tmpPrintStream);
          
          // and trace it
          _Logger.logp(LEVEL_FINE, _className, 
                       getMethodNameFromStack(new Throwable(), "traceException"), pExcpt.getMessage());
          _Logger.logp(LEVEL_FINE, _className, 
                       getMethodNameFromStack(new Throwable(), "traceException"), tmpByteStream.toString());
       } // end of if (pExcpt != null)
    } // end of traceException()
    
    public void addFilter(TraceStringFilter f) {
   	 _filters.add(f);
    }
    
    private boolean filtersAllowLogging() {
   	 boolean allowLogging = false;
   	 
   	 // when there are no filters set at all (which would be the case for
   	 // the job generator server component using the trace logger)
   	 // we always return true
   	 if (_filters.size() == 0) {
   		 return true;
   	 }
   	 
   	 // if there are any filters we go through these to check whether to
   	 // allow tracing for specific components or not
   	 // we do this by checking the namespace of the current class against
   	 // the string property stored inside the filters
   	 for (int i = 0; i < _filters.size(); i++) {
   		 TraceStringFilter tsf = (TraceStringFilter) _filters.get(i);
   		 
   		 if (tsf.isTraceable(getMethodNameFromStack(new Throwable(), "trace"))) { //$NON-NLS-1$
   			 allowLogging = true;
   		 }
   	 }
   	 
   	 return allowLogging;
    }
    
    public static String[] getDefinedTraceLevels() {
   	 StringBuffer traceLevels = new StringBuffer();
   	 traceLevels.append("LEVEL_ENTRY" + ";");
   	 traceLevels.append("LEVEL_EXIT" + ";");
   	 traceLevels.append("LEVEL_INFO" + ";");
   	 traceLevels.append("LEVEL_FINE" + ";");
   	 traceLevels.append("LEVEL_FINER" + ";");
   	 traceLevels.append("LEVEL_FINEST" + ";");
   	 traceLevels.append("EVENT");
   	 
   	 return traceLevels.toString().split(";");
    }
    
    public static TraceEventType getTraceLevelByName(String traceLevelName) {
   	 if (traceLevelName.equalsIgnoreCase("LEVEL_ENTRY")) {
   		 return LEVEL_ENTRY;
   	 }
   	 else if (traceLevelName.equalsIgnoreCase("LEVEL_EXIT")) {
   		 return LEVEL_EXIT;
   	 }
   	 else if (traceLevelName.equalsIgnoreCase("LEVEL_INFO")) {
   		 return LEVEL_INFO;
   	 }
   	 else if (traceLevelName.equalsIgnoreCase("LEVEL_FINE")) {
   		 return LEVEL_FINE;
   	 }
   	 else if (traceLevelName.equalsIgnoreCase("LEVEL_FINER")) {
   		 return LEVEL_FINER;
   	 }
   	 else if (traceLevelName.equalsIgnoreCase("LEVEL_FINEST")) {
   		 return LEVEL_FINEST;
   	 }
   	 else if (traceLevelName.equalsIgnoreCase("EVENT")) {
   		 return EVENT;
   	 }
   	 
   	 // fallback if none of the above matches (which should never be the case)
   	 return LEVEL_FINER;
    }
     
} // end of class TraceLoggerImpl
