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


public final class DSProject 
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   private static final String  PROJECT_NAMESPACE_TEMPLATE  = "{0}:{1}";    //$NON-NLS-1$
   private static final String  QUALIFIED_PROJ_NAME_WO_PORT = "{0}/{1}";    //$NON-NLS-1$
   private static final String  QUALIFIED_PROJ_NAME_W_PORT  = "{0}:{1}/{2}";    //$NON-NLS-1$

   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private String  _Id;
   private String  _HostName;
   private String  _Name;
   private Integer _DSServerPort;
   
   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.util.Copyright.IBM_COPYRIGHT_SHORT;
   }

   
   /**
    * Constructor
    */
   public DSProject(String id, String name, String hostName)
   {
      _Id           = id;
      _Name         = name;
      _DSServerPort = null;
      
      if (hostName != null) {
         _HostName = hostName.toUpperCase();
      }
      else {
         _HostName = hostName;
      }
   } // end of DSProject()
   
   
   public DSProject(String id, String name, String hostName, Integer srvrRPCPort)
   {
      this(id, name, hostName);
      _DSServerPort = srvrRPCPort;
   } // end of DSProject()
   
   
   public Integer getDSServerPort()
   {
      return(_DSServerPort);
   } // end of getDSServerPort()
   
   
   public String getId()
   {
      return(_Id);
   } // end of getId()
   
   
   public String getHostName()
   {
      return(_HostName);
   } // end of getHostName()

   
   public String getName()
   {
      return(_Name);
   } // end of getName()

   
   public String getNameSpace()
   {
      return(MessageFormat.format(PROJECT_NAMESPACE_TEMPLATE, new Object[] { _HostName, _Name } ));
   } // end of getNameSpace()

   
   public String getQualifiedName()
   {
      String qualifiedName;
      
      if (_DSServerPort == null)
      {
         qualifiedName = MessageFormat.format(QUALIFIED_PROJ_NAME_WO_PORT, 
                                              new Object[] { _HostName, _Name } );
      }
      else
      {
         qualifiedName = MessageFormat.format(QUALIFIED_PROJ_NAME_W_PORT, 
                        new Object[] { _HostName, String.valueOf(_DSServerPort.intValue()), _Name } );
      }
      
      return(qualifiedName);
   } // end of getQualifiedName()
   
   
   public String toString()
   {
      return(String.valueOf(_Name) + "::" + _Id + "(" + _HostName + "):" + _DSServerPort);
   }
   
} // end of class DSProject
