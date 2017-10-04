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
// Module Name : com.ibm.iis.sappack.gen.common.ui.editors
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.editors;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.ibm.iis.sappack.gen.common.ui.util.FileHelper;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.validators.ValidatorBase;
import com.ibm.is.sappack.gen.common.ui.RMRGMode;


public abstract class ConfigurationBase extends ModelStoreMap {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	protected IResource resource;
//	protected ModelStoreMap map;
	PropertyInfoCollection piCollection;

	protected ConfigurationBase(IResource resource, String fileExtension) throws IOException, CoreException {
		this.resource = resource;
		FileHelper.load( (IFile) resource, fileExtension, this);
		this.piCollection = createPropertyInfoCollection();
	}
		
	/**
	 * Use this constructor for a configuration based by a ModelStoreMap 
	 * (typically from within the editor).
	 * 
	 * The resource is only needed for displaying the name.
	 */
	/*
	protected ConfigurationBase(ModelStoreMap map, IResource resource) {
		this.map = map;
		this.resource = resource;
	}
	*/

	public IResource getResource() {
		return this.resource;
	}
	
	public Map<String, String> getProperties() {
		return this;
	}
	
	public String getID() {
		return this.get(PropertiesConstants.KEY_ID);
	}
	
	public RMRGMode getMode() {
		String m = this.get(PropertiesConstants.PROP_KEY_MODE);
		if (m == null) {
			return null;
		}

		return Utils.getMode(m);
	}
	
	protected void setLocation(List<PropertyInfo> pis, String location) {
		for (PropertyInfo pi : pis) {
			pi.setLocation(location);
		}
	}
	
	public final PropertyInfoCollection getPropertyCollection() {
		return this.piCollection;
	}
	
	protected abstract PropertyInfoCollection createPropertyInfoCollection(); 

	public abstract ValidatorBase createValidator();

	@Override
	public String get(Object key) {
		String s = super.get(key);
		if (s != null) {
			return s;
		}
		PropertyInfo pi = this.piCollection.getPropertyInfo((String) key);
		if (pi == null) {
			return null;
		}
		Object o = pi.getDefault();
		if (o != null) {
			return o.toString();
		}
		return null;
	}
	
	protected String getValueAsNullIfEmpty(String s) {
		if (s == null) {
			return null;
		}
		if (s.trim().isEmpty()) {
			return null;
		}
		return s;
	}
	
}
