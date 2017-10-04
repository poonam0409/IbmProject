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

import com.ibm.is.sappack.gen.common.util.ServerMessageCatalog;


public abstract class BaseException extends Exception {
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   protected static final long serialVersionUID = -1000000011L;


   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private   String  _MsgId;
   private   String  _MsgParamArr[];

	
   static String copyright() { 
      return com.ibm.is.sappack.gen.common.Copyright.IBM_COPYRIGHT_SHORT; 
   }
	
	
   public BaseException(String msgId, String msgParamArr[]) {
      this(msgId, msgParamArr, null);
   } // end of BaseException()
	
	
   public BaseException(String msgId, String msgParamArr[], Throwable cause) {
      super(cause);
      _MsgId       = msgId;
      _MsgParamArr = msgParamArr;
   } // end of BaseException()
   
   
   public BaseException(String message, Throwable cause) {
      super(message, cause);
   }
   
   
   public BaseException(String message) {
      super(message);
   }
   
   
   public String getLocalizedMessage() {
      String retMessage;
         
      if (_MsgId == null) {
         retMessage = super.getMessage();
      }
      else {
         retMessage = ServerMessageCatalog.getDefaultCatalog().getText(_MsgId, _MsgParamArr);
      }
         
      return(retMessage);
   } // end of getMessageText()
	
	
   public String getMessageId() {
      return(_MsgId);
   } // end of getMessageId()

   
   public String[] getMessageParams() {
      return(_MsgParamArr);
   } // end of getMessageParams()

   
   protected void setMessageId(String msgId) {
      setMessageId(msgId, null);
   } // end of setMessageId()


   protected void setMessageId(String msgId, String msgParamArr[]) {
      _MsgId       = msgId;
      _MsgParamArr = msgParamArr;
   } // end of setMessageId()

} // end of class BaseException
