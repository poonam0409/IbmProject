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

package com.ibm.is.sappack.cw.app.services.logging;

import java.io.File;
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

public class CWAppLogger{

	//static private Logger mLogger = null;
	static private PrintStream mLogPs = null;
	static final private String _baseResourceName = "_CWApp" ;
    static public ResourceBundle mResourceBundle = null;
    static public ResourceBundle mResourceBundle_ids=null;
    static private String logfile=null;
    static private boolean initLog = false;
    static private MessageFormat m_msgFormatter = null;
    static private String m_loginUser="";
    static private String m_sessionID="";
    /**
     * Initialize logger
     */
    static public void initLogger() {
    	if(initLog!=true){
    	String CWAppTrace= System.getenv("CW_APP_TRACE_ENABLE");
    	String loginTime = new SimpleDateFormat("yyyy/MM/dd_hh:mm").format( new Date() );
   		System.out.println("inside initLogger()");
	   	if("1".equals(CWAppTrace))
	   	{
	   		String timeStamp = new SimpleDateFormat("yyyyMMdd_hhmm").format( new Date() );
	   		logfile=(System.getenv("CW_APP_TRACE_DIR")!= null)? System.getenv("CW_APP_TRACE_DIR")+"CWApp_"+timeStamp+".log": null;
	   		File file=new File(System.getenv("CW_APP_TRACE_DIR"));
	   		file.mkdirs();
	   		System.out.println("new log file:"+logfile);
	   	}
	   	loadResourceBundle();
	    loadLoggerFile();
	    m_msgFormatter=new MessageFormat("", Locale.getDefault());
	    log(Level.FINE,"CWApp_10001",new Object[]{m_loginUser, loginTime});
	    initLog=true;
    }}

    static public void initLogger(String loginUser,String sessionID) {
    	System.out.println("String loginUser,String sessionID-"+loginUser+"--"+ sessionID);
    	m_loginUser=loginUser;
    	if(m_sessionID.equals(""))
    		m_sessionID=sessionID;
    	if(!m_sessionID.equals(sessionID)){
    	initLog=false;
    	}
    	System.out.println("m_sessionID, initLog-"+m_sessionID+"--"+initLog);
    	initLogger();
    }
    /**
     * Load Resource Bundle
     */
    private static void loadResourceBundle() {
        try {
        	if(null == mResourceBundle){
            try{
            mResourceBundle = ResourceBundle.getBundle("com/ibm/is/sappack/cw/app/services/logging/" + _baseResourceName, Locale.getDefault(), Thread.currentThread().getContextClassLoader());
            }catch(MissingResourceException mREx){
            	throw new RuntimeException("MissingResourceException during Logger initialization: " + mREx.getMessage());
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
             if (mLogPs == null) {
            	 if (logfile != null && !logfile.equals("")) {
                     mLogPs = new PrintStream(new FileOutputStream(logfile));
                 } else {
                     mLogPs = new PrintStream(new FileOutputStream("CWApp.log"));
                 }
             }
         } catch (FileNotFoundException ex) {
             throw new RuntimeException("Exception during Logger initialization: " + ex.getMessage());
         }
    }
    
    /**
     * public static method to write to a log file
     */    
    public static void log(Level level, String messageId, Object[] args) {
    	String result=getFormattedString(messageId, args);
    	
    	if (null !=  mLogPs)
    	{
    		mLogPs.println(new java.util.Date() + " : " +level+" : "+ result);
    	}
    }

    public void releaseResource() {
        if (mLogPs != null) {
            mLogPs.close();
        }
    }
   
        
    public static String getFormattedString(String template, Object[] args)
    {
    	if(!initLog)
    	{
    		initLog=true;
        	initLogger();
    	}
    	try{
    		m_msgFormatter.applyPattern(
    		   mResourceBundle.getString(template));
    	}catch(Exception ex){
    		return "";	
    	}
    	return m_msgFormatter.format(args);
    }
}

