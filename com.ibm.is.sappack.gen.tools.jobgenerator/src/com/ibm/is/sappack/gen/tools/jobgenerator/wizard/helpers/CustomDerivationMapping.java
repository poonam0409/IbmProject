//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers;


import java.util.Set;


public class CustomDerivationMapping {

	private String      dsDataType;
	private String      derivation;
   private Set<String> dbmTypeSet;
   
	
	static String copyright() { 
	   return Copyright.IBM_COPYRIGHT_SHORT; 
	}	

	
	public CustomDerivationMapping(String dsDataType, Set<String> dbmTypeSet, 
	                               String derivation) {
	   this.dsDataType = dsDataType;
		this.dbmTypeSet = dbmTypeSet;
		this.derivation = derivation;
	}

   public void setDSDataType(String dataType) {
      this.dsDataType = dataType;
   }

	public void setDBMTypes(Set<String> typeSet) {
		this.dbmTypeSet = typeSet;
	}

	public void setDerivation(String derivation) {
		this.derivation = derivation;
	}

	public String getDBMTypes() {
      StringBuffer dbmSetAsString = new StringBuffer();
      boolean      hasMore;
      
      hasMore = false;
      for(String dbmType: dbmTypeSet) {
         
         if (hasMore) {
            dbmSetAsString.append(","); //$NON-NLS-1$
         }
         else {
            hasMore = true;
         }
         dbmSetAsString.append(dbmType);
      }
	   
		return dbmSetAsString.toString();
	}

   public String getDSDataType() {
      return this.dsDataType;
   }

	public String getDerivation() {
		return this.derivation;
	}

	public String toString() {
	   
		return "DS data type:" + this.dsDataType + "\tderivation:" + this.derivation + "\tDBM types:" + getDBMTypes();  //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
	}
	
	public boolean isDerviationEmpty() {
		return (this.derivation == null) || this.derivation.trim().equals(""); //$NON-NLS-1$
	}
	
	public boolean isDataTypeEmpty() {
		return (this.dsDataType == null) || this.dsDataType.trim().equals(""); //$NON-NLS-1$
	}
	
	public boolean isDataTypeEqual(CustomDerivationMapping otherMapping) {
		return this.dsDataType.trim().equalsIgnoreCase(otherMapping.dsDataType.trim());
	}

}
