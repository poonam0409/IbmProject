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
// Module Name : com.ibm.is.sappack.gen.server.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.server.util;


import DataStageX.DSLink;


public final class GraphJobLink 
{
   // -------------------------------------------------------------------------------------
   //                                    Constants
   // -------------------------------------------------------------------------------------
   public static final int LINK_DIRECTION_UP      = 0;
   public static final int LINK_DIRECTION_DOWN    = 1;
   public static final int LINK_DIRECTION_LEFT    = 2;
   public static final int LINK_DIRECTION_RIGHT   = 3;

   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private DSLink        _OutputLink;
   private GraphJobNode  _TargetGraphNode;
   private int           _Direction;
   
   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.server.util.Copyright.IBM_COPYRIGHT_SHORT; 
   }
   
   
   public GraphJobLink(DSLink outputLink, GraphJobNode targetGraphNode)
   {
      this(outputLink, targetGraphNode, LINK_DIRECTION_RIGHT);
   } // end of GraphJobLink()
   
   
   public GraphJobLink(DSLink outputLink, GraphJobNode targetGraphNode, int direction)
   {
      _OutputLink      = outputLink;
      _TargetGraphNode = targetGraphNode;
      
      switch(direction)
      {
         case LINK_DIRECTION_DOWN:
         case LINK_DIRECTION_LEFT:
         case LINK_DIRECTION_UP:
              _Direction = direction;
              break;
              
         default:
              _Direction = LINK_DIRECTION_RIGHT;
      }
   } // end of GraphJobLink()
   
   
   public int getDirection()
   {
      return(_Direction);
   }
   
   
   public DSLink getDSOutputLink()
   {
      return(_OutputLink);
   }
   
   
   public GraphJobNode getTargetGraphNode()
   {
      return(_TargetGraphNode);
   }
   
   public String toString()
   {
      return("Link = " + _OutputLink.getName() + " - TargetNode: " + _TargetGraphNode);
   }
   
} // end of class GraphJobLink
