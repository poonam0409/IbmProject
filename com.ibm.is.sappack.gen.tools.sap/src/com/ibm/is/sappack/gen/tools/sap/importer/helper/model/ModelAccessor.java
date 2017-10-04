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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;

import com.ibm.db.models.logical.Package;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public abstract class ModelAccessor {

	private IFile modelFile;
	protected Resource modelResource;
	protected Package rootPackage;


	static String copyright() { 
		return com.ibm.is.sappack.gen.tools.sap.importer.helper.model.Copyright.IBM_COPYRIGHT_SHORT; 
	}

	public ModelAccessor(IFile modelFile) throws IOException {
		this.modelFile = modelFile;
		this.modelResource = loadModel();
		this.rootPackage = (Package) this.modelResource.getContents().get(0);
	}
	
	protected ModelAccessor(ModelAccessor modelAccessor) throws IOException {
		this.modelFile = modelAccessor.modelFile;
		this.modelResource = modelAccessor.modelResource;
		this.rootPackage = modelAccessor.rootPackage;
	}
	
	public IFile getModelFile() {
		return this.modelFile;
	}
	
	public Package getPackage() {
		return this.rootPackage;
	}

	private final Resource loadModel() throws IOException {
		// ResourceSet resourceSet =
		// DataToolsPlugin.getDefault().getResourceSet();
		ResourceSet resourceSet = new ResourceSetImpl();
		String file = this.modelFile.getLocation().toOSString();
		Resource resource = resourceSet.createResource(URI.createFileURI(file));
		resource.load(null);
		return resource;
	}

	public void saveModel() {
		this.save();
	}

	@SuppressWarnings("unchecked")
	private void save() {
		try {
			Map<String, Comparable> options = new HashMap<String, Comparable>();
			options.put(XMLResource.OPTION_DECLARE_XML, Boolean.TRUE);
			options.put(XMLResource.OPTION_ENCODING, "UTF-8"); //$NON-NLS-1$
			this.modelResource.save(options);
			this.modelFile.refreshLocal(IResource.DEPTH_ZERO, null);
			this.modelFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (Exception e) {
			e.printStackTrace();
			Activator.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
