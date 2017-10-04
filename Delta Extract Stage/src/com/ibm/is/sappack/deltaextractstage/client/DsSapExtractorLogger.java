// IBM Confidential                                                            
//---------------------------------------------------------------------------  
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2015                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.deltaextractstage.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.ascential.e2.daapi.util.CC_Message;
import com.ascential.e2.daapi.util.CC_MessageLogger;
import com.ibm.is.sappack.deltaextractstage.commons.DsSapExtractorParam;
import com.ibm.is.sappack.deltaextractstage.commons.LogMessageUtil;

public class DsSapExtractorLogger extends com.ibm.is.cc.javastage.api.Logger{

	static private Logger mLogger = null;
	static private PrintStream mLogPs = null;
	static final private String _baseResourceName = "_DsSapDeltaExtractor" ;
    static public ResourceBundle mResourceBundle = null;
    static public ResourceBundle mResourceBundle_ids=null;
    static private String logfile=null;
    static private LogRecord record = null;
    static private boolean initLog = false;
    static private MessageFormat m_msgFormatter = null;
    static private boolean multiEventLog=true;
    
    /**
     * Initialize logger
     */
    static public void initLogger() {
    	if(initLog!=true){
    	String deltaExtTrace= System.getenv("DS_DELTAEXTRACT_TRACE");
   	 if("1".equals(deltaExtTrace))
   	 {
   		 String timeStamp = new SimpleDateFormat("_yyyyMMdd_hhmm").format( new Date() );
   		 logfile=(System.getenv("DS_DELTAEXTRACT_TRACE_DIR")!= null)? System.getenv("DS_DELTAEXTRACT_TRACE_DIR")+"DsSapExtractor_NodeId_"+DsSapExtractorParam.dataStageNodeId
+timeStamp+".log" : null;
   	 }
   	 	loadResourceBundle();
    	loadLoggerFile();
    	m_msgFormatter=new MessageFormat("", Locale.getDefault());
    	initLog=true;
    }}

    /**
     * Load Resource Bundle
     */
    private static void loadResourceBundle() {
        try {
        	if(null == mResourceBundle){
            try{
            	mResourceBundle_ids = ResourceBundle.getBundle("com/ibm/is/sappack/deltaextractstage/resources/" + _baseResourceName+"_ids");
				mResourceBundle = ResourceBundle.getBundle("com/ibm/is/sappack/deltaextractstage/resources/" + _baseResourceName, Locale.getDefault(), Thread.currentThread().getContextClassLoader());
            }catch(MissingResourceException mREx){
            	mResourceBundle = ResourceBundle.getBundle("com/ibm/is/sappack/deltaextractstage/resources/" + _baseResourceName, Locale.US, Thread.currentThread().getContextClassLoader());
                System.out.println("Resource bundle not found for locale: "+ Locale.getDefault() + ". This may cause job logs to be printed in default English language");
            	}
            }
        }catch (Exception ex) {
            throw new RuntimeException("Exception during Logger initialization: " + ex.getMessage());
        }
    }
    
    /**
     * Load Logger File
     */
    private static void loadLoggerFile()
    {
    	 try {
             if (mLogger == null && mLogPs == null) {
            	 if (logfile != null) {
                     if (logfile.equals("")) {
                         mLogger = Logger.getLogger("com.ibm.is.sappack.deltaextractstage.client");
                     } else {
                         mLogPs = new PrintStream(new FileOutputStream(logfile));
                     }
                 } else {
                     mLogger = Logger.getLogger("com.ibm.is.sappack.deltaextractstage.client");
                 }
             }
         } catch (FileNotFoundException ex) {
             throw new RuntimeException("Exception during Logger initialization: " + ex.getMessage());
         }
    }
    
    /**
     * public method to write in a log file
     */    
    public static void writeToLogFile(Level level, String messageId, Object[] args) {
    	String result=getMessageStringForLoggerFile(level, messageId, args);
    	
    	if(null != mLogger)
    	{
    		record = new LogRecord(level, messageId);
            record.setResourceBundle(mResourceBundle);
            record.setParameters(args);
    		mLogger.log(record);
    	}
    	else if (null !=  mLogPs)
    	{
    		mLogPs.println(new java.util.Date() + " " + result);
    	}
    }

    public void releaseResource() {
        if (mLogPs != null) {
            mLogPs.close();
        }
    }
    
    
    /**
     * Logger method to print DataStage log
     * @param id(presents in _DsSapDeltaExtractor_ids_en_IN.properties file)
     * @param Object[]
     */
    public static void information(String id, Object[] objParams) {
    	if(DsSapExtractorParam.dataStageNodeId==0)
    	{
    	String result=getMessageStringForLogger(objParams);
    	if(multiEventLog)
    	{
	    	CC_Message msg = LogMessageUtil.createConnectorMessage(id, result);
	        CC_MessageLogger.information(msg); 
	        CC_Message.destroyInstance(msg);
    	}else
    		DsSapExtractorLogger.information(result);
    	}
    }
    
    public static void fatal(String id, Object[] objParams) {
    	String result=getMessageStringForLogger(objParams);
    	if(multiEventLog)
    	{
    	CC_Message msg = LogMessageUtil.createConnectorMessage(id, result);
        CC_MessageLogger.fatal(msg); 
        CC_Message.destroyInstance(msg);
    	}else
    		DsSapExtractorLogger.fatal(result);
    }
    
    public static void debug(String id, Object[] objParams) {
    	if(DsSapExtractorParam.dataStageNodeId==0)
    	{
    	String result=getMessageStringForLogger(objParams);
    	if(multiEventLog)
    	{
	    	CC_Message msg = LogMessageUtil.createConnectorMessage(id, result);
	        CC_MessageLogger.debug(msg); 
	        CC_Message.destroyInstance(msg);
    	}else
    		DsSapExtractorLogger.debug(result);
    	}
    }
    
    public static void warning(String id, Object[] objParams) {
    	if(DsSapExtractorParam.dataStageNodeId==0)
    	{
    	String result=getMessageStringForLogger(objParams);
    	if(multiEventLog)
    	{
	    	CC_Message msg = LogMessageUtil.createConnectorMessage(id, result);
	        CC_MessageLogger.warning(msg); 
	        CC_Message.destroyInstance(msg);
    	}else
    		DsSapExtractorLogger.warning(result);
    	}
    }
    
    private static String getMessageStringForLoggerFile(Level level, String messageId, Object[] objParams)
    {
    	if(!initLog)
    	{
    		initLogger();
    		initLog=true;
    	}
        String result ="";
        result = level+ " : ";
        if(mResourceBundle.containsKey(messageId))
        result+= mResourceBundle.getString(messageId)+" ";
        result+=getMessageStringForLogger(objParams);
        return result;
    }
    
    public static String getMessageStringForLogger(Object[] objParams)
    {
    	if(!initLog)
    	{
    		initLogger();
    		initLog=true;
    	}
    	String result="";
    	String objParam="";
    	if (objParams == null || objParams.length==0) {
            //result += "";
        } else {
        	try{
        	for (int i = 0; i < objParams.length; i++) {
        		if(mResourceBundle.containsKey(objParams[i].toString()))
        			objParam=mResourceBundle.getString(objParams[i].toString());
            	else
            		objParam=objParams[i].toString();
        		result +=objParam+" ";
            }
        }catch(Exception e)
        {
        	return result;	
        }}
    	return result;
    }
    
    /**
     * Check Null value for input String and 
     * print it in DataStage log if null and return the same value
     * @param String paramName
     * @param String paramValue
     */
//    public static String checkNullString(String paramName, String paramValue)
//	{
//	if(paramValue==null)
//		DsSapExtractorLogger.warning(new Object[]{"DeltaExtract_78",paramName});
//	return paramValue;
//	}
    
    public static String getFormattedString(String template, Object[] args)
    {
    	if(!initLog)
    	{
    		initLogger();
    		initLog=true;
    	}
    	System.out.println(mResourceBundle.getString(template));
    	m_msgFormatter.applyPattern(
    		   mResourceBundle.getString(template));
       return m_msgFormatter.format(args);
    }
    
    public static String getMessageID(String template)
    {
    	try{
    		if(mResourceBundle_ids.containsKey(template))
    			return mResourceBundle_ids.getString(template).trim();
    		return null;
    	}catch(Exception ex){
    	return null;
    	}
    	
    }

}

