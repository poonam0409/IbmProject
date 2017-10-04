//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2013                                              
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


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;



public final class ObjectParamMap
{
   // -------------------------------------------------------------------------------------
   // Entry Subclass
   // -------------------------------------------------------------------------------------
   public static class Value
   {
      // -------------------------------------------------------------------------------------
      // Member Variables
      // -------------------------------------------------------------------------------------
      private String   _Value;
      private int      _UsageType;

      
      public Value(String parValue, int parUsageType)
      {
         if (parValue == null)
         {
//            throw new IllegalArgumentException("Link Parameter value must not be null.");
         }
         
         switch(parUsageType)
         {
            case DataStageObjectFactory.OBJECT_TYPE_DEFAULT:
            case DataStageObjectFactory.OBJECT_TYPE_INPUT:
            case DataStageObjectFactory.OBJECT_TYPE_OUTPUT:
                 break;
                 
            default:
                 throw new IllegalArgumentException("Link Usage Type '" + parUsageType + "' is not valid.");
         } // end of switch(parType)
         
         _Value     = parValue;
         _UsageType = parUsageType;
      } // end of Value()
      
      public int getUsageType()
      {
         return(_UsageType);
      }
      
      public String getValue()
      {
         return(_Value);
      }
      
      public String toString()
      {
         return(_Value + " (" +_UsageType +")");
      } // end of toString()
   } // end of Value
   
   
   // -------------------------------------------------------------------------------------
   // Constants
   // -------------------------------------------------------------------------------------
 
   
   // -------------------------------------------------------------------------------------
   // Member Variables
   // -------------------------------------------------------------------------------------
   private Map<String, ObjectParamMap.Value>   _ParamMap;
   private int                                 _DefaultUsage;
   
   
   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.server.util.Copyright.IBM_COPYRIGHT_SHORT; 
   }
   
      
   public ObjectParamMap(int parDefaultUsageType)
   {
      setDefaultUsageType(parDefaultUsageType);

      _ParamMap     = new HashMap<String, ObjectParamMap.Value>();
      _DefaultUsage = parDefaultUsageType;
   } // end of ObjectParamMap()

   
   private ObjectParamMap(int parDefaultUsageType, Map<String, ObjectParamMap.Value> parObjectParamMap)
   {
      _ParamMap     = parObjectParamMap;
      _DefaultUsage = parDefaultUsageType;
   } // end of ObjectParamMap()

   
   public ObjectParamMap(Map parMap, int parUsageType)
   {
      Iterator   mapIter;
      Map.Entry  mapEntry;
      
      _ParamMap = new HashMap<String, ObjectParamMap.Value>();
      
      setDefaultUsageType(parUsageType);
      
      // take over the key/value pairs of an existing map
      if (parMap != null)
      {
         mapIter = parMap.entrySet().iterator();
         while(mapIter.hasNext())
         {
            mapEntry = (Map.Entry) mapIter.next();
            
            _ParamMap.put((String) mapEntry.getKey(), 
                          new Value((String) mapEntry.getValue(), parUsageType));
         } // end of while(mapIter.hasNext())
      } // end of if (parMap != null)
      
   } // end of ObjectParamMap()
   
   
   public static ObjectParamMap concat(ObjectParamMap parMap1, ObjectParamMap parMap2)
   {
      Map<String, ObjectParamMap.Value>                  tmpHashMap;
      Iterator<Map.Entry<String, ObjectParamMap.Value>>  mapIter;
      Map.Entry<String, ObjectParamMap.Value>            mapEntry;
      int                                                tmpDefUsage;
      
      // check if map 1 has been specified ...
      if (parMap1 == null)
      {
         throw new IllegalArgumentException("ObjectParamMap 1 must be specified.");
      }
      
      tmpHashMap  = new HashMap<String, ObjectParamMap.Value>(parMap1._ParamMap);
      tmpDefUsage = parMap1._DefaultUsage;
      
      // add values from Map2 to the new map
      if (parMap2 != null)
      {
         tmpDefUsage = parMap2._DefaultUsage;
         
         mapIter = parMap2._ParamMap.entrySet().iterator();
         while(mapIter.hasNext())
         {
            mapEntry = mapIter.next();
            
            tmpHashMap.put(mapEntry.getKey(), mapEntry.getValue());
         } // end of while(mapIter.hasNext())
      } // end of if (parMap2 != null)

      return(new ObjectParamMap(tmpDefUsage, tmpHashMap));
   } // end of concat()
   
   
   public Value get(String parKey)
   {
      return((Value) _ParamMap.get(parKey));
   } // end of get()
   
   
   public Iterator iterator()
   {
      return(_ParamMap.entrySet().iterator());
   } // end of toString()

   
   public void put(String parKey, String parValue)
   {
      _ParamMap.put(parKey, new Value(parValue, _DefaultUsage));
   } // end of put()

   
   public void put(String parKey, String parValue, int parType)
   {
      _ParamMap.put(parKey, new Value(parValue, parType));
   } // end of put()

   
   private void setDefaultUsageType(int parDefaultUsageType)
   {
      // create a dummy 'value' to check the passed usage type
      new Value("", parDefaultUsageType);
      
      _DefaultUsage = parDefaultUsageType;
   } // end of setDefaultUsageType()
   
   
   public int size()
   {
      return(_ParamMap.size());
   } // end of size()
   
   
   public String toString()
   {
      return(_ParamMap.toString());
   } // end of toString()
   
} // end of class ObjectParamMap
