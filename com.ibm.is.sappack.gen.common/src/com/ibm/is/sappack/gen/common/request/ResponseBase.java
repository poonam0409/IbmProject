//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.common.request
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.common.request;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.ServerMessageCatalog;
import com.ibm.is.sappack.gen.common.util.XMLUtils;


public abstract class ResponseBase {
   // -------------------------------------------------------------------------------------
   //                                       Subclass
   // -------------------------------------------------------------------------------------
   private static class MessageData {
      private  String  _ClassName;
      private  String  _Id;
      private  String  _ParamArr[];
      private  String  _Text;
      private  String  _Type;
      
      public MessageData(String id, String paramArr[], int typeInt) {
         this(id, paramArr, null, typeInt);
      }
      
      public MessageData(String text, int typeInt) {
         this(null, null, text, typeInt);
      }
      
      private MessageData(String id, String paramArr[], String text, int typeInt) {
         _Type      = getTypeAsString(typeInt);
         _ClassName = null;
         _Id        = id;
         _ParamArr  = paramArr;
         _Text      = text;
      }
      
      public String getClassName() {
         return(_ClassName);
      }
      
      public String getId() {
         return(_Id);
      }
      
      public String[] getParams() {
         return(_ParamArr);
      }
      
      public String getText() {
         return(_Text);
      }
      
      public String getType() {
         return(_Type);
      }
      
      public static int getTypeAsInt(String type) {
         
         int retTypeInt;
         
         if (type == null) {
            throw new IllegalArgumentException("Message type must not be null.");
         }
         
         if (type.equals(MESSAGE_TYPE_ERROR_VAL))
            retTypeInt = MESSAGE_TYPE_ERROR;
         else
         if (type.equals(MESSAGE_TYPE_INFO_VAL))
            retTypeInt = MESSAGE_TYPE_INFO;
         else
         if (type.equals(MESSAGE_TYPE_WARNING_VAL))
            retTypeInt = MESSAGE_TYPE_WARNING;
         else {
            throw new IllegalArgumentException("Message type is not valid.");
         }
            
         return(retTypeInt);
      }
      
      public static String getTypeAsString(int typeInt) {
         
         String retTypeString;
         
         switch(typeInt) {
         case MESSAGE_TYPE_ERROR:
              retTypeString = MESSAGE_TYPE_ERROR_VAL;
              break;
         case MESSAGE_TYPE_INFO:
              retTypeString = MESSAGE_TYPE_INFO_VAL;
              break;
         case MESSAGE_TYPE_WARNING:
              retTypeString= MESSAGE_TYPE_WARNING_VAL;
              break;
         default:
              throw new IllegalArgumentException("Message type is not valid.");
      }
            
         return(retTypeString);
      }
      
      public void setClassName(String className) {
         _ClassName = className;
      }
      
      public String toString() {
         StringBuffer buf = new StringBuffer();
         
         buf.append("Id=");
         buf.append(_Id);
         buf.append(" - ");
         buf.append("Params=");
         if (_ParamArr == null) {
            buf.append("-");
         }
         else {
            buf.append("(");
            for(int idx = 0; idx < _ParamArr.length; idx ++) {
               if (idx > 0) {
                  buf.append("//-//");
               }
               buf.append(_ParamArr[idx]);
            }
            buf.append(")");
         }
         buf.append(" - ");
         buf.append("Text=");
         buf.append(_Text);
         buf.append(" - ");
         buf.append("Type=");
         buf.append(_Type);
         
         return(buf.toString());
      }
   } // end of class MessageData

   
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   private   static final String  XML_TAG_SERVER_RESULT        = "ServerResult";          //$NON-NLS-1$
   private   static final String  XML_ATTRIB_DSSERVER_VERSION  = "dsServerVersion";       //$NON-NLS-1$
   private   static final String  XML_ATTRIB_MODEL_VERSION     = "supportedModelVersion"; //$NON-NLS-1$
   private   static final String  XML_ATTRIB_REQUEST_TYPE      = "requestType";           //$NON-NLS-1$
   private   static final String  XML_TAG_REQUEST_RESULT       = "RequestResult";         //$NON-NLS-1$
   private   static final String  XML_TAG_MESSAGE_LIST         = "MessageList";           //$NON-NLS-1$
   private   static final String  XML_TAG_MESSAGE              = "Message";               //$NON-NLS-1$
   private   static final String  XML_ATTRIB_ID                = "id";                    //$NON-NLS-1$
   private   static final String  XML_ATTRIB_TYPE              = "type";                  //$NON-NLS-1$
   private   static final String  XML_ATTRIB_TEXT              = "text";                  //$NON-NLS-1$
   private   static final String  XML_TAG_MSG_PARAM_LIST       = "MsgParamList";          //$NON-NLS-1$
   private   static final String  XML_TAG_MSG_PARAM            = "MsgParam";              //$NON-NLS-1$
   private   static final String  XML_ATTRIB_CLASS             = "class";                 //$NON-NLS-1$
   private   static final String  XML_TAG_DETAILED_INFO        = "DetailedInfo";          //$NON-NLS-1$

   public   static final  int     MESSAGE_TYPE_INFO            = 1;
   public   static final  String  MESSAGE_TYPE_INFO_VAL        = "I";
   public   static final  int     MESSAGE_TYPE_WARNING         = 2;
   public   static final  String  MESSAGE_TYPE_WARNING_VAL     = "W";
   public   static final  int     MESSAGE_TYPE_ERROR           = 4;
   public   static final  String  MESSAGE_TYPE_ERROR_VAL       = "E";
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private   Class             _RequestClass;
   private   boolean           _DoesContainErrors;
   private   boolean           _ExceptionAdded;
   private   Throwable         _Exception;
   private   String            _DetailedInfo;
   private   String            _DSServerVersion;
   private   String            _ModelVersion;
   private   List<MessageData> _MessageList;

	
	static String copyright() { 
	   return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; 
	}	
	
	
	public ResponseBase(Class requestClass) {
	   _Exception         = null;
	   _RequestClass      = requestClass;
	   _MessageList       = new ArrayList<MessageData>();
	   _DoesContainErrors = false;
	   _ExceptionAdded    = false;
	   _ModelVersion      = Constants.MODEL_VERSION;	   
	}

   public ResponseBase(Node xmlNode) {

      _DoesContainErrors = false;
      _ExceptionAdded    = false;
      
      _DSServerVersion = XMLUtils.getNodeAttributeValue(xmlNode, XML_ATTRIB_DSSERVER_VERSION);
      _ModelVersion    = XMLUtils.getNodeAttributeValue(xmlNode, XML_ATTRIB_MODEL_VERSION);
      
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      // get all (result) message
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      _MessageList = getMessageListFromXML(XML_TAG_MESSAGE_LIST, xmlNode);
      
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      // get detailed information
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      buildDetailedInfoFromXML(xmlNode);
   }


   private void addExceptionMessage(Throwable excpt, boolean handleCause) {
      
      if (!_ExceptionAdded && excpt != null) {
         MessageData msgData;
         String      msgId;
         
         if (excpt instanceof BaseException) {
            BaseException tmpBaseExcpt = (BaseException) excpt;
         
            msgId = tmpBaseExcpt.getMessageId();
            if (msgId != null) {
               msgData = new MessageData(msgId, tmpBaseExcpt.getMessageParams(), MESSAGE_TYPE_ERROR);
            }
            else {
               msgData = new MessageData(tmpBaseExcpt.getMessage(), MESSAGE_TYPE_ERROR);
            }
         }
         else {
            msgData = new MessageData(excpt.getMessage(), MESSAGE_TYPE_ERROR);
         }

         // add Exception class ...
         msgData.setClassName(excpt.getClass().getName());
         
         // and finally add message to the list
         _MessageList.add(msgData);
         _DoesContainErrors = true;
         
         // add 'ExceptionCause' message if cause is available
         if (handleCause && excpt.getCause() != null) {
//            addExceptionMessage(excpt.getCause(), false);
         }
         
         // prevent adding twice exception to message list
         // _ExceptionAdded = true;
      } // end of if (!_ExceptionAdded && excpt != null)
   }
   
   
   public  void addMessage(String msgId, String msgParam, int typeInt) {
      addMessage(msgId, new String[] { msgParam } , typeInt);
   }
   
   
   public  void addMessage(String msgId, String msgParamsArr[], int typeInt) {
      _MessageList.add(new MessageData(msgId, msgParamsArr, typeInt));
      
      if (typeInt == MESSAGE_TYPE_ERROR) {
         _DoesContainErrors = true;
      }
   }
   
   
   public  void addMessage(String msgText, int typeInt) {
      _MessageList.add(new MessageData(msgText, typeInt));
      
      if (typeInt == MESSAGE_TYPE_ERROR) {
         _DoesContainErrors = true;
      }
   }
   
   
   private void buildDetailedInfoFromXML(Node xmlNode) {
      Node         detailedInfoNode;
      
      _DetailedInfo    = null;
      detailedInfoNode = XMLUtils.getChildNode(xmlNode, XML_TAG_DETAILED_INFO);
      if (detailedInfoNode != null) {
         // get the exception class and exception message
         _DetailedInfo = XMLUtils.getNodeTextValue(detailedInfoNode);
      } // end of if (detailedInfoNode != null)
   }
   
   public boolean containsErrors() {
      return(_DoesContainErrors);
   }
   
   public String getDetailedInfo() {
      return(_DetailedInfo);
   }
   
   public String getDSServerVersion() {
      return(_DSServerVersion);
   }

   private String getExceptionStackTrace() {
      StringBuffer  xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      if (_Exception != null) {
         StringWriter strWriter;
         PrintWriter  prtWriter;
         
         // write stack trace into string ...
         strWriter = new StringWriter();
         prtWriter = new PrintWriter(strWriter);
         
         _Exception.printStackTrace(prtWriter);
         
         xmlBuf.append(strWriter.toString());
      } // end of if (_Exception != null)
      
      return (xmlBuf.toString());
   }
   
   public String get1stMessage() {
      String tmpMsgArr[];
      String retMessage;
      
      retMessage = null;
      tmpMsgArr  = getMessages();
      if (tmpMsgArr.length > 0) {
         retMessage = tmpMsgArr[0];
      }
      
      return(retMessage);
   }
   
   
   public String[] getMessages() {
      return(getMessages(-1));
   }
   
   
   public String[] getMessages(Locale locale) {
      return(getMessages(-1, locale));
   }
   
   
   public String[] getMessages(int msgType) {
      return(getMessages(msgType, null));
   }
   
   public String[] getMessages(int msgTypeFilter, Locale locale) {

      MessageData          curMessage;
      ServerMessageCatalog curMsgCatalog;
      String               msgText;
      List<String>         retMsgList;
      Iterator             msgDataIter;
      int                  msgTypeAsInt;

      // check passed locale and load Message Catalog
      if (locale == null) {
         locale = Locale.getDefault();
      }
      curMsgCatalog = ServerMessageCatalog.createMessageCatalog(locale);

      retMsgList  = new ArrayList<String>();
      msgDataIter = _MessageList.iterator();
      while(msgDataIter.hasNext()) {
         curMessage = (MessageData) msgDataIter.next();

         msgTypeAsInt = MessageData.getTypeAsInt(curMessage.getType());

         // all messages types (-1) or a specific message type  
         if (msgTypeFilter <  0 ||
             msgTypeFilter == msgTypeAsInt) {
            if (curMessage.getId() == null || curMessage.getId().length() == 0)
            {
               msgText = curMessage.getText();
            }
            else {
               msgText = curMsgCatalog.getMessage(curMessage.getId(), curMessage.getParams());
            }

            retMsgList.add(msgText);
         } // end of if (msgTypeFilter <  ... == msgTypeAsInt)
      } // end of while(msgDataIter.hasNext())

      return((String[]) retMsgList.toArray(new String[0]));
   }

   
   public String getMessageListAsXML() {
      MessageData  curMessage;
      String       msgParamArr[];
      StringBuffer xmlBuf;
      Iterator     msgDataIter;
      int          arrIdx;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append("<" + XML_TAG_MESSAGE_LIST + ">");                  //$NON-NLS-1$ $NON-NLS-2$
      
      // process all messages existing in the message list
      msgDataIter = _MessageList.iterator();
      while(msgDataIter.hasNext()) {
         curMessage = (MessageData) msgDataIter.next();
         
         xmlBuf.append("<");                                            //$NON-NLS-1$
         xmlBuf.append(XML_TAG_MESSAGE);
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_ID, curMessage.getId()));
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_TEXT, curMessage.getText()));
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_TYPE, curMessage.getType()));
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_CLASS, curMessage.getClassName()));
         xmlBuf.append(">");                                            //$NON-NLS-1$
         
         msgParamArr = curMessage.getParams();
         if (msgParamArr == null) {
            xmlBuf.append("<");                                         //$NON-NLS-1$
            xmlBuf.append(XML_TAG_MSG_PARAM_LIST);
            xmlBuf.append("/>");                                        //$NON-NLS-1$
         }
         else {
            xmlBuf.append("<");                                         //$NON-NLS-1$
            xmlBuf.append(XML_TAG_MSG_PARAM_LIST);
            xmlBuf.append(">");                                         //$NON-NLS-1$
            
            for (arrIdx = 0; arrIdx < msgParamArr.length; arrIdx ++ ) {
               xmlBuf.append("<" + XML_TAG_MSG_PARAM + ">");            //$NON-NLS-1$ $NON-NLS-2$
               xmlBuf.append(XMLUtils.replaceXMLCharacters(msgParamArr[arrIdx]));
               xmlBuf.append("</" + XML_TAG_MSG_PARAM + ">");           //$NON-NLS-1$ $NON-NLS-2$
            }
            
            xmlBuf.append("</");                                         //$NON-NLS-1$
            xmlBuf.append(XML_TAG_MSG_PARAM_LIST);
            xmlBuf.append(">");                                        //$NON-NLS-1$
         } // end of if (msgParamArr == null) {
         
         xmlBuf.append("</");                      //$NON-NLS-1$
         xmlBuf.append(XML_TAG_MESSAGE);
         xmlBuf.append(">");                       //$NON-NLS-1$
      } // end of while(msgDataIter.hasNext())
      
      xmlBuf.append("</" + XML_TAG_MESSAGE_LIST + ">");                 //$NON-NLS-1$ $NON-NLS-2$

      return (xmlBuf.toString());
   }
   
   private List<MessageData> getMessageListFromXML(String msgXMLTagName, Node parentNode) {
      MessageData       msgDataObj;
      List<MessageData> retMessageList;
      String            msgClassName;
      String            msgId;
      String            msgParamArr[];
      String            msgText;
      String            msgType;
      Node              listNode;
      Node              messageNode;
      Node              paramNode;
      NodeList          messageNodeList;
      NodeList          paramNodeList;
      int               msgIdx;
      int               parIdx;
      int               msgTypeInt;

      retMessageList = new ArrayList<MessageData>();
      
      listNode = XMLUtils.getChildNode(parentNode, msgXMLTagName);
      if (listNode != null) {
         // process all the 'Message' nodes
         messageNodeList = listNode.getChildNodes();
         for(msgIdx = 0; msgIdx < messageNodeList.getLength(); msgIdx ++) {
            
            messageNode = messageNodeList.item(msgIdx);
            
            // get the attributes
            msgClassName = XMLUtils.getNodeAttributeValue(messageNode, XML_ATTRIB_CLASS);
            msgId        = XMLUtils.getNodeAttributeValue(messageNode, XML_ATTRIB_ID);
            msgType      = XMLUtils.getNodeAttributeValue(messageNode, XML_ATTRIB_TYPE);
            msgTypeInt   = MessageData.getTypeAsInt(msgType);
            
            // message id or message text ?????
            if (msgId == null || msgId.length() == 0) {
               // --> message text
               msgText    = XMLUtils.getNodeAttributeValue(messageNode, XML_ATTRIB_TEXT);
               msgDataObj = new MessageData(msgText, msgTypeInt);
            }
            else {
               // --> message id --> get the message parameters
               paramNode = XMLUtils.getChildNode(messageNode, XML_TAG_MSG_PARAM_LIST);
               if (paramNode != null) {
                  paramNodeList = paramNode.getChildNodes();
                  msgParamArr = new String[paramNodeList.getLength()];
                  for (parIdx = 0; parIdx < msgParamArr.length; parIdx++) {
                     paramNode           = paramNodeList.item(parIdx);
                     msgParamArr[parIdx] = XMLUtils.getNodeTextValue(paramNode);
                  }
               }
               else {
                  msgParamArr = null;
               } // end of (else) if (paramNode != null)
               
               msgDataObj = new MessageData(msgId, msgParamArr, msgTypeInt);
            }
            
            // set 'Request Successful' to false if an error message occurs
            if (msgType.equals(MESSAGE_TYPE_ERROR_VAL)) {
               _DoesContainErrors = true;
            }

            msgDataObj.setClassName(msgClassName);
            retMessageList.add(msgDataObj);
         } // end of for(msgIdy = 0; msgIdx < messageNodeList.getLength(); msgIdx ++)
      } // end of if if (listNode != null) != null)
      
      return(retMessageList);
   }

   public String getSupportedModelVersion() {
      return(_ModelVersion);
   }

   public void setDetailedInfo(String detailedInfo) {
      _DetailedInfo = detailedInfo;
   }
   
   public void setDSServerVersion(String svrVersion) {
      _DSServerVersion = svrVersion;
   }
   
   public void setException(Throwable exception) {
      _Exception = exception;
      addExceptionMessage(_Exception, true);
   }

   public String toString() {
      
      String retString;
      
      retString = "contains Errors = " + String.valueOf(_DoesContainErrors) + " - " + get1stMessage(); 
      
      return(retString);
   }
   
   public String toXML() {
      StringBuffer xmlBuf;
      String       childXML;
      String       detailedInfo;

      // get the child's XML ...
      childXML = getXML();
      
      // build result XML ...
      xmlBuf = new StringBuffer(XMLUtils.XML_HEADER);

      xmlBuf.append("<");                                               //$NON-NLS-1$
      xmlBuf.append(XML_TAG_SERVER_RESULT);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_REQUEST_TYPE, 
                                                    String.valueOf(_RequestClass.getName())));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_DSSERVER_VERSION, _DSServerVersion)); 
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_MODEL_VERSION, _ModelVersion)); 
      xmlBuf.append(">");                                               //$NON-NLS-1$
      
      
      // - - - - - - - - - - - - - - - - add Exception Message - - - - - - - - - - - - - - - - -
//      addExceptionMessage(_Exception, true);
      
      // - - - - - - - - - - - - - - - - - - - - Message - - - - - - - - - - - - - - - - - - - -
      xmlBuf.append(getMessageListAsXML());
      
      // - - - - - - - - - - - - - - -  - - - Detailed Information  - - - - - - - - - - - - - - - - - - - -
      if (_Exception == null) {
         detailedInfo = _DetailedInfo;
      }
      else {
         detailedInfo = getExceptionStackTrace(); 
      }
      if (detailedInfo == null) {
         xmlBuf.append("<" + XML_TAG_DETAILED_INFO + "/>");                 //$NON-NLS-1$ $NON-NLS-2$
      }
      else {
      	xmlBuf.append(XMLUtils.createCDATAElement(XML_TAG_DETAILED_INFO, detailedInfo));
      }
         
      // - - - - - - - - - - - - - - - - - - - - Request Result - - - - - - - - - - - - - - - - - - - -
      xmlBuf.append("<" + XML_TAG_REQUEST_RESULT + ">");                //$NON-NLS-1$ $NON-NLS-2$
      if (childXML != null) {
         xmlBuf.append(childXML);
      }
      xmlBuf.append("</" + XML_TAG_REQUEST_RESULT + ">");               //$NON-NLS-1$ $NON-NLS-2$
      
      xmlBuf.append("</");                                              //$NON-NLS-1$
      xmlBuf.append(XML_TAG_SERVER_RESULT);
      xmlBuf.append(">");                                               //$NON-NLS-1$

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Result as XML = " + xmlBuf.toString()); //$NON-NLS-1$
      }

      return (xmlBuf.toString());
   } // end of toXML()

   
   public static void traceResponse(ResponseBase response) {
      
      Iterator msgListIter;

      if (TraceLogger.isTraceEnabled()) {
         StringBuffer traceBuffer = new StringBuffer();
         traceBuffer.append(Constants.NEWLINE);
         traceBuffer.append("----------------------- SAPPacks Job Generator Result (Start) -----------------------");
         traceBuffer.append(Constants.NEWLINE);
         traceBuffer.append("Request Type = ");
         traceBuffer.append(response._RequestClass.getName());
         traceBuffer.append(Constants.NEWLINE);
         if (response._DSServerVersion != null) {
            traceBuffer.append("DSServer version = ");
            traceBuffer.append(response._DSServerVersion);
            traceBuffer.append(Constants.NEWLINE);
         }
         traceBuffer.append("Supported model version = ");
         traceBuffer.append(response._ModelVersion);
         traceBuffer.append(Constants.NEWLINE);
         traceBuffer.append("Messages:");
         traceBuffer.append(Constants.NEWLINE);
         msgListIter = response._MessageList.iterator();
         while(msgListIter.hasNext()) {
            traceBuffer.append("  Msg: ");
            traceBuffer.append(msgListIter.next());
            traceBuffer.append(Constants.NEWLINE);
         }
         traceBuffer.append("Contains Errors = ");
         traceBuffer.append(response._DoesContainErrors);
         traceBuffer.append(Constants.NEWLINE);
         traceBuffer.append(response.getTraceString());
         traceBuffer.append("----------------------- SAPPacks Job Generator Result (End) -------------------------");
         traceBuffer.append(Constants.NEWLINE);

         TraceLogger.trace(TraceLogger.LEVEL_FINER, traceBuffer.toString());
      } // end of if (TraceLogger.isTraceEnabled())
   } // end of traceRequest()

   
   // -------------------------------------------------------------------------------------
   //                                  Abstract Methods
   // -------------------------------------------------------------------------------------
   abstract protected String getTraceString();
   abstract protected String getXML();

   
} // end of class responseBase
