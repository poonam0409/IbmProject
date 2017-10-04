package com.ibm.is.sappack.deltaextractstage.commons;

import com.ascential.e2.daapi.util.CC_Message;
import com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger;

public class LogMessageUtil
{
  private static final String CC_UNST_PREFIX = "java:";
  private static final String CC_UNST_CATEGORY = "CONN-";
  private static final String CC_UNST_COMPONENT = "UNST-";
  private static final String DEFAULT_MESSAGE_ID = "UNKNOWN_ID";
  
  private static String generateMessageID(String messageID)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(CC_UNST_PREFIX);
    localStringBuilder.append(CC_UNST_CATEGORY);
    localStringBuilder.append(CC_UNST_COMPONENT);
    if (messageID == null)
      localStringBuilder.append(DEFAULT_MESSAGE_ID);
    else
      localStringBuilder.append(messageID);
    return localStringBuilder.toString();
  }

  public static CC_Message createConnectorMessage(String messageID, Object[] paramArrayOfObject)
  {
    String str = DsSapExtractorLogger.getMessageStringForLogger(paramArrayOfObject);
    return createConnectorMessage(messageID, str);
  }

  public static CC_Message createConnectorMessage(String messageID, String message)
  {
    String str = DsSapExtractorLogger.getMessageID(messageID);
    CC_Message cc_Message = CC_Message.createInstance(null, generateMessageID(str).getBytes());
    cc_Message.addArgument(message);
    return cc_Message;
  }
}