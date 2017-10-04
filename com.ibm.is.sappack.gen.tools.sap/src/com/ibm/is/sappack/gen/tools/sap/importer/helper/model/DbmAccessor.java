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
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.datatools.modelbase.sql.schema.SQLObject;
import org.eclipse.datatools.modelbase.sql.schema.Schema;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import com.ibm.datatools.core.DataToolsPlugin;
import com.ibm.db.models.db2.luw.LUWDatabase;


public class DbmAccessor {

	private LUWDatabase database;


	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	public DbmAccessor(IFile dbmFile) throws IOException {
		Resource modelResource = loadModel(dbmFile);
		this.database = (LUWDatabase) modelResource.getContents().get(0);
	}

	private final Resource loadModel(IFile modelFile) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		String file = modelFile.getLocation().toOSString();
		Resource resource = resourceSet.createResource(URI.createFileURI(file));
		resource.load(null);
		return resource;
	}

	@SuppressWarnings("unchecked")
	public List<Schema> getSchemas() {
		return this.database.getSchemas();
	}

	public static String getAnnotationValue(SQLObject object, String annotationName) {
		Map<String, String> annotationMap = getAnnotations(object);
		return annotationMap.get(annotationName);
	}

	public static Map<String, String> getAnnotations(SQLObject object) {
		EAnnotation annotation = object.getEAnnotation(DataToolsPlugin.ANNOTATION_UDP);
		if (annotation == null) {
			return new HashMap<String, String>();
		}
		return annotation.getDetails().map();
	}

}
