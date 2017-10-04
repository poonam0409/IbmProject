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



public abstract class FlowVarData 
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   
	
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private String   _Derivation;
   private String   _Description;
   private String   _Name;
   private String   _Type;
   private Integer  _Length;
   private boolean  _IsUnicode;

   static String copyright()
   { return com.ibm.is.sappack.gen.server.util.Copyright.IBM_COPYRIGHT_SHORT; }
   
   public FlowVarData(String pName, String pDesc)  
   {
      this(pName, pDesc, -1);
   }

   
   public FlowVarData(String pName, String pDesc, int pLength)  
   {
      _Derivation   = null;; 
      _Description  = pDesc;
      _Name         = pName;
      _Type         = null;
      _IsUnicode    = false;
      
      setLength(pLength);
   }
   
   protected Integer checkLength(Integer length2check)
   {
      Integer retLength;
      
      if (length2check != null && length2check.intValue() < 0)
      {
         retLength = null;
//         throw new IllegalArgumentException("length must not be less then 0.");
      }
      else
      {
         retLength = length2check;
      }
      
      return(retLength);
   }
   
   
   protected Integer convertLength(int length)
   {
      Integer retLength;
      
      retLength = checkLength(new Integer(length));
      
      return(retLength);
   }
   
   
   public String getDerivation()
   {
      if (_Derivation == null)
      {
         _Derivation = _Name;
      }
      
      return(_Derivation);
   }
   
   
   public String getDescription()
   {
      return(_Description);
   }
   
   
   public Integer getLength()
   {
      return(_Length);
   }
   
   
   public String getName()
   {
      return(_Name);
   }
   
   
   public String getType()
   {
      return(_Type);
   }
   
   
   public boolean isUnicode()
   {
      return(_IsUnicode);
   }
   
   
   public void setDerivation(String derivation)
   {
      _Derivation = derivation;
   }

   
   public void setIsUnicode(boolean isUnicode)
   {
      _IsUnicode = isUnicode;
   }
   
   
   public void setLength(int length)
   {
      _Length = convertLength(length);
   }
   
   
   public void setLength(Integer length)
   {
      _Length = checkLength(length);
   }
   
   
   public void setName(String name)
   {
      _Name = name;
   }
   
   
   public void setType(String type)
   {
      _Type = type;
   }
   
   
   public String toString()
   {
      return(_Name);
   }

   
   // -------------------------------------------------------------------------------------
   //                                 Abstract Methods
   // -------------------------------------------------------------------------------------
   protected abstract void validate();
   public    abstract int  getScale();
   
} // end of class FlowVarData
