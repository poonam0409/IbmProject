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
// Module Name : com.ibm.is.sappack.gen.server.jobgenerator.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.jobgenerator.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ASCLModel.MainObject;
import DataStageX.DSJobDef;
import DataStageX.DSLocalContainerDef;

import com.ibm.is.sappack.gen.server.jobgenerator.layout.BaseLayout;
import com.ibm.is.sappack.gen.server.jobgenerator.layout.DefaultContainerLayout;
import com.ibm.is.sappack.gen.server.jobgenerator.layout.DefaultJobLayout;
import com.ibm.is.sappack.gen.server.util.GraphJobNode;


public final class JobStructureData 
{
   // -------------------------------------------------------------------------------------
   //                                       Sub Classes
   // -------------------------------------------------------------------------------------
   public static class LayoutData
   {
      // -------------------------------------------------------------------------------------
      //                                 Member Variables
      // -------------------------------------------------------------------------------------
      private List        _SourceNodeList;
      private BaseLayout  _JobContainerLayout;
      
      
      static String copyright()
      { 
         return com.ibm.is.sappack.gen.server.jobgenerator.util.Copyright.IBM_COPYRIGHT_SHORT; 
      }
      
      public LayoutData()
      {
         this(null);
      } // end of LayoutData()
      
      public LayoutData(BaseLayout containerLayout)
      {
         // create member instances
         _SourceNodeList = new ArrayList();
         
         if (containerLayout == null)
         {
            _JobContainerLayout = new DefaultJobLayout();
         }
         else
         {
            _JobContainerLayout = containerLayout;
         }
      } // end of LayoutData()
      
      public GraphJobNode addSourceNode(GraphJobNode sourceNode)
      {
         _SourceNodeList.add(sourceNode);
         
         return(sourceNode);
      } // end of addSourceNode()
      
      public BaseLayout getContainerLayout()
      {
         return(_JobContainerLayout);
      } // end of getContainerLayout()
      
      public List getSourceNodesList()
      {
         return(_SourceNodeList);
      } // end of getSourceNodesList()
      
   } // end of class LayoutData
   
   
   
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private Map       _LayoutDataMap;
   private DSJobDef  _JobDef;

   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.server.jobgenerator.util.Copyright.IBM_COPYRIGHT_SHORT; 
   }

   
   /**
    * Constructor
    */
   public JobStructureData(DSJobDef jobDef)
   {
      if(jobDef == null)
      {
         throw new IllegalArgumentException("DSJobDef must be specified.");
      }
      
      // create the needed maps
      _LayoutDataMap = new HashMap();
      _JobDef        = jobDef;
      
      // create a new DesignViewData instance for the specified JobDef
      _LayoutDataMap.put(_JobDef, new LayoutData());
   } // end of JobStructureData()


   public LayoutData getLayoutData()
   {
      return((LayoutData) _LayoutDataMap.get(_JobDef));
   } // end of getLayoutData()

   
   public LayoutData getLayoutData(MainObject containerDef)
   {
      return((LayoutData) _LayoutDataMap.get(containerDef));
   } // end of getLayoutData()

   public DSJobDef getJobDef()
   {
      return(_JobDef);
   } // end of getJobDef()
   
   
   public List getParentContainerDefs()
   {
      List      containerList;
      Iterator  mapIter;
      Map.Entry mapEntry;
      
      // get all container definitions
      containerList = new ArrayList();
      mapIter       = _LayoutDataMap.entrySet().iterator();
      while(mapIter.hasNext())
      {
         mapEntry = (Map.Entry) mapIter.next();
         containerList.add(mapEntry.getKey());
      }
      
      return(containerList);
   } // end of getParentContainerDefs()

   
   /**
    * This method sets a layout for a container definition. If 'layout' is null 
    * the DefaultContainerLayout is used.
    * 
    * @param containerDef  container definition
    * @param layout        container layout or null
    */
   public void setContainerLayout(DSLocalContainerDef containerDef, BaseLayout layout)
   {
      if (containerDef == null)
      {
         throw new IllegalArgumentException("ContainerDef must not be null.");
      }
      
      if (layout == null)
      {
         _LayoutDataMap.put(containerDef, new LayoutData(new DefaultContainerLayout()));
      }
      else
      {
         _LayoutDataMap.put(containerDef, new LayoutData(layout));
      }
   } // end of setContainerLayout()

   
   /**
    * This method sets a new layout for the Job Container.
    * 
    * @param layout        container layout to be set
    */
   public void setJobLayout(BaseLayout layout)
   {
      if (layout == null)
      {
         throw new IllegalArgumentException("Layout must not be null.");
      }
      
      _LayoutDataMap.put(_JobDef, new LayoutData(layout));
   } // end of setJobLayout()
   
} // end of class JobStructureData
