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
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.helper.model
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.helper.model;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;

import com.ibm.db.models.logical.AtomicDomain;
import com.ibm.db.models.logical.Entity;
import com.ibm.db.models.logical.LogicalDataModelFactory;
import com.ibm.db.models.logical.Package;


public class DdmAccessor extends ModelAccessor {
   private Package _AtomicDomainPackage;
   
   
	static String copyright() { 
	   return Copyright.IBM_COPYRIGHT_SHORT;
	}

   public DdmAccessor(IFile modelFile, String atomicDomainPkgName) throws IOException {
      super(modelFile);
      _AtomicDomainPackage = super.getPackage();
      
      setAtomicDomainPackage(atomicDomainPkgName);
   }


	public DdmAccessor(IFile modelFile) throws IOException {
	   this(modelFile, null);
	}


   public Package getPackage() {
      return _AtomicDomainPackage;
   }

	public AtomicDomain getAtomicDomain(String dataType, String baseType) {

		// Check whether the domain needed is already existing
		AtomicDomain domain = this.findAtomicDomain(dataType);
		if (domain != null) {
			return domain;
		}

		// If no corresponding domain exists, a new one will be created
		return this.createAtomicDomain(dataType, baseType);
	}

	@SuppressWarnings("unchecked")
	private AtomicDomain findAtomicDomain(String domainName) {
		List<AtomicDomain> domains = this.rootPackage.getDomainsRecursively();
		if (domains == null || domains.isEmpty()) {
			return null;
		}

		Iterator<AtomicDomain> iterator = domains.iterator();
		while (iterator.hasNext()) {
			AtomicDomain atomicDomain = iterator.next();
			if (atomicDomain.getName().equalsIgnoreCase(domainName)) {
				return atomicDomain;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private final AtomicDomain createAtomicDomain(String dataType, String baseType) {
		AtomicDomain atomicDomain = LogicalDataModelFactory.eINSTANCE.createAtomicDomain();
		atomicDomain.setName(dataType);
		atomicDomain.setBaseType(baseType);
		
      _AtomicDomainPackage.getContents().add(atomicDomain);

		return atomicDomain;
	}

	// public AtomicDomain getAtomicDomain(Package targetPackage, String
	// dataType, String baseType) {
	//
	// // Check whether the domain needed is already existing
	// AtomicDomain domain = this.findAtomicDomain(targetPackage, dataType);
	// if (domain != null) {
	// return domain;
	// }
	//
	// // If no corresponding domain exists, a new one will be created
	// return this.createAtomicDomain(targetPackage, dataType, baseType);
	// }
	//
	// @SuppressWarnings("unchecked")
	// public AtomicDomain findAtomicDomain(Package targetPackage, String
	// domainName) {
	// List<AtomicDomain> domains = targetPackage.getDomainsRecursively();
	// if (domains == null || domains.isEmpty()) {
	// return null;
	// }
	//
	// Iterator<AtomicDomain> iterator = domains.iterator();
	// while (iterator.hasNext()) {
	// AtomicDomain atomicDomain = iterator.next();
	// if (atomicDomain.getName().equalsIgnoreCase(domainName)) {
	// return atomicDomain;
	// }
	// }
	// return null;
	// }
	//
	// @SuppressWarnings("unchecked")
	// public final AtomicDomain createAtomicDomain(Package targetPackage,
	// String dataType, String baseType) {
	// AtomicDomain atomicDomain =
	// LogicalDataModelFactory.eINSTANCE.createAtomicDomain();
	// atomicDomain.setName(dataType);
	// atomicDomain.setBaseType(baseType);
	// targetPackage.getContents().add(atomicDomain);
	//
	// return atomicDomain;
	// }


   @SuppressWarnings("unchecked")
   private Package findPackage(String packageName) {
      Package rootPkg = super.getPackage();
      Package retPkg;
      Object  curModelObj;
      
      retPkg = null;
      Iterator<Entity> iterator = rootPkg.getChildren().iterator();
      while (iterator.hasNext() && retPkg == null) {
         curModelObj = iterator.next();
         if (curModelObj instanceof Package) {
            if (((Package)curModelObj).getName().equals(packageName)) {
               retPkg = (Package) curModelObj;
            }
         }
      }
      
      return(retPkg);
   } // end of findPackage()


	
   @SuppressWarnings("unchecked")
   public void setAtomicDomainPackage(String packageName) {
      
      if (packageName != null && packageName.length() > 0) {
         
         // search for existence of the package
         _AtomicDomainPackage =  findPackage(packageName);
         
         // if not found ==> create it
         if (_AtomicDomainPackage == null) {
            _AtomicDomainPackage = LogicalDataModelFactory.eINSTANCE.createPackage();
            _AtomicDomainPackage.setName(packageName);
            
            this.rootPackage.getChildren().add(_AtomicDomainPackage);
         }
      }
      else {
         _AtomicDomainPackage = super.getPackage();
      }
   }

}
