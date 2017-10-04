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


import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.ibm.is.sappack.gen.common.Constants;


public class CustomDerivationsContentProvider implements IStructuredContentProvider {
	public  static final String DEFAULT_DERIVATION = Constants.DERIVATION_REPLACEMENT;

   
//	public static final String COLUMN_DATA_TYPE = "Data Type";
//	public static final String COLUMN_DERIVATION = "Derivation";
	private List<CustomDerivationMapping> customDerivations;

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.Copyright.IBM_COPYRIGHT_SHORT;
	}

	// public String[] getColumns() {
	// return new String[] { COLUMN_DATA_TYPE, COLUMN_DERIVATION };
	// }

	public CustomDerivationsContentProvider(
			List<CustomDerivationMapping> customDerivations) {
		this.customDerivations = customDerivations;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return ((CustomDerivationMapping[]) this.customDerivations
				.toArray(new CustomDerivationMapping[this.customDerivations.size()]));
	}

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public HashMap<String, String> getCustomDerivationMap() {
		HashMap<String, String> mappings = new HashMap<String, String>(this.customDerivations.size());
		
		for (CustomDerivationMapping mapping : this.customDerivations) {
		   String curDerivation = mapping.getDerivation();
		   
		   // save modified (non-default) mappings only
			if (curDerivation != null && 
			    !curDerivation.equals(Constants.DERIVATION_REPLACEMENT)) {
				mappings.put(mapping.getDSDataType(), curDerivation); 
//				mappings.put(mapping.getDSDataType(), curDerivation.replaceAll("\\" + Constants.DERIVATION_REPLACEMENT, "{0}"));  //$NON-NLS-1$//$NON-NLS-2$
			}
		}

		return mappings;
	}

	public void addMapping(CustomDerivationMapping newMapping) {
		this.customDerivations.add(newMapping);
	}

	public void removeMapping(CustomDerivationMapping mapping) {
		this.customDerivations.remove(mapping);
	}

	public List<CustomDerivationMapping> getCustomDerivations() {
		return this.customDerivations;
	}
	
	public void clearMappings() {
		this.customDerivations.clear();
	}
}
