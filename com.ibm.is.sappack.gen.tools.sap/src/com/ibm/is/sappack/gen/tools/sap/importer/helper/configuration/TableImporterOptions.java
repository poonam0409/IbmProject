//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration;


import java.util.ArrayList;
import java.util.List;

import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalField;


public class TableImporterOptions extends ImporterOptionsBase {

	private List<String> tableBlackList = new ArrayList<String>();
	private List<String> tableWhiteList = new ArrayList<String>();

	private boolean              allowColumnsToBeNullable = false;
   private String               atomicDomainPkgName = null;
	private boolean              extractRecursively = true;
	private List<TechnicalField> explicitTechnicalFields = null;


   static String copyright() {  
      return Copyright.IBM_COPYRIGHT_SHORT; 
   }


	public boolean isExtractRecursively() {
		return extractRecursively;
	}

	public void setExtractRecursively(boolean extractRecursively) {
		this.extractRecursively = extractRecursively;
	}

	public void setAllowColumnsToBeNullable(boolean allowColumnsToBeNullable) {
		this.allowColumnsToBeNullable = allowColumnsToBeNullable;
	}

	public boolean getAllowColumnsToBeNullable() {
		return this.allowColumnsToBeNullable;
	}

   public boolean doCreateAtomicDomainPkg() {
      return(this.atomicDomainPkgName != null);
   }

   
   public String getAtomicDomainPkgName() {
      return(this.atomicDomainPkgName);
   }

   
   public void setAtomicDomainPkgName(String pkgName) {
      if (pkgName != null && pkgName.length() > 0) {
         this.atomicDomainPkgName = pkgName;
      }
      else {
         this.atomicDomainPkgName = null;
      }
   }

   

	public List<String> getTableBlackList() {
		return this.tableBlackList;
	}


	public void setTableBlackList(List<String> tableBlackList) {
		this.tableBlackList = tableBlackList;
	}


	public List<String> getTableWhiteList() {
		return this.tableWhiteList;
	}


	public void setTableWhiteList(List<String> tableWhiteList) {
		this.tableWhiteList = tableWhiteList;
	}


	public List<TechnicalField> getExplicitTechnicalFields() {
		return this.explicitTechnicalFields;
	}


	public void setExplicitTechnicalFields(List<TechnicalField> technicalFields) {
		this.explicitTechnicalFields = technicalFields;
	}

}
