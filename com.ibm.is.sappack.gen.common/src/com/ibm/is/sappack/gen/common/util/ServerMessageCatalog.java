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
// Module Name : com.ibm.is.sappack.gen.common.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.ibm.is.sappack.gen.common.Constants;


public final class ServerMessageCatalog {
	// -------------------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------------------
   private static final String MSG_NOT_FOUND_TEMPLATE = "###__{0}__### not found";
   private static final String MSG_LAYOUT_TEMPLATE    = "({0}) {1}";
   public  static final String MESSAGE_FILE           = "ServerMessages";
   
   
	// -------------------------------------------------------------------------------------
	// Member Variables
	// -------------------------------------------------------------------------------------
   private        ResourceBundle        _MessageBundle;
   
   private static Locale                _DefaultLocale; 
   private static String                _MessageBundlePath; 
   private static ServerMessageCatalog  _DefaultCatalog; 
   
   
   static {
      String msgFilePath;
      String curClassName;
      
      // the the 'path' of the message file
      curClassName        = ServerMessageCatalog.class.getName();
      msgFilePath         = curClassName.substring(0, curClassName.lastIndexOf('.')+1);
      _MessageBundlePath  = msgFilePath.replaceAll("[ .]", "/");
       
      // and load the (localized) message bundle
      _DefaultLocale  = Locale.getDefault();
      _DefaultCatalog = new ServerMessageCatalog(_DefaultLocale);
   } // end of static
   
   
	static String copyright() {
		return com.ibm.is.sappack.gen.common.util.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private ServerMessageCatalog(Locale locale) {
      _MessageBundle = ResourceBundle.getBundle(_MessageBundlePath + MESSAGE_FILE, locale);
	}
	
	public static ServerMessageCatalog createMessageCatalog() {
	   
      return(createMessageCatalog(Locale.getDefault()));
   } // end of createMessageCatalog()

	
   public static ServerMessageCatalog createMessageCatalog(String locale) {
      if (locale == null) {
         throw new IllegalArgumentException("Locale string must be set.");
      }
      
      return(createMessageCatalog(new Locale(locale.toString())));
   }

   
   public static ServerMessageCatalog createMessageCatalog(Locale locale) {
      
      if (locale == null) {
         System.out.println("Message locale has not been specified: Using default locale '" + _DefaultLocale + "' instead.");
         locale = _DefaultLocale;
      }
      
      return(new ServerMessageCatalog(locale));
   } // end of createMessageCatalog()

   
   public static ServerMessageCatalog getDefaultCatalog() {
      
      return(_DefaultCatalog);
   } // end of getDefaultCatalog()
   
   
   public static Locale getDefaultLocale() {
      
      return(_DefaultLocale);
   } // end of getDefaultLocale()
   
   
   public String getMessage(String msgKey) {
      
      return(getMessage(msgKey, null));
   } // end of getMessage()
   
	
   private String getMessage(String msgKey, Object msgParamArr[], boolean setKeyPrefix) {
      String retMsgText;
      String tmpMessageText;
      
      // load the (message) string
      tmpMessageText = loadStringFromCatalog(msgKey);
      
      if (tmpMessageText == null) {
         retMsgText = MessageFormat.format(MSG_NOT_FOUND_TEMPLATE, new Object[]  { msgKey });
      }
      else {
         if (msgParamArr != null && msgParamArr.length > 0) {
            
            tmpMessageText = MessageFormat.format(tmpMessageText, msgParamArr);
         } // end of if (msgParamArr != null && msgParamArr.length > 0) {
         
         if (setKeyPrefix) {
            retMsgText = MessageFormat.format(MSG_LAYOUT_TEMPLATE, 
                                              new Object[] { msgKey, tmpMessageText });
         }
         else {
            retMsgText = tmpMessageText;
         }
      } // end of (else) if (msgKey == null)
      
      return(retMsgText);
   } // end of getMessage()
   
   
   public String getMessage(String msgKey, Object msgParamArr[]) {
      
      return(getMessage(msgKey, msgParamArr, true));
   } // end of getMessage()
   
   
   public String getText(String textKey) {
      return(getText(textKey, Constants.NO_PARAMS));
   } // end of getMessageWOKey()
   
   
   public String getText(String textKey, Object textParamArr[]) {
      
      return(getMessage(textKey, textParamArr, false));
   } // end of getMessageWOKey()
   
   
   private String loadStringFromCatalog(String key) {
      String retString;
      
      if (key == null) {
         retString = null;
      }
      else
      {
         try {
            retString = _MessageBundle.getString(key);
         }
         catch(MissingResourceException pMissingResourceExcpt) {
            retString = null;
         }
      } // end of (else) if (key != null)
      
      return(retString);
   } // end of loadStringFromCatalog()

} // end of class ServerMessageCatalog
