//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.idocseglist
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.idocseglist;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertyInfo;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertyInfoCollection;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.validators.ValidatorBase;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class IDocSegmentList extends ConfigurationBase {
	public static final String IDOC_SEGMENT_LIST_FILE_EXTENSION = "rmil"; //$NON-NLS-1$

  	
	static String copyright() { 
 	   return Copyright.IBM_COPYRIGHT_SHORT; 
 	}	


  	public IDocSegmentList(IResource resource) throws IOException, CoreException {
		super(resource, IDOC_SEGMENT_LIST_FILE_EXTENSION);
	}

	@Override
	protected PropertyInfoCollection createPropertyInfoCollection() {
		String empty = ""; //$NON-NLS-1$
		List<PropertyInfo> pis = new ArrayList<PropertyInfo>(Arrays.asList(new PropertyInfo[] {
				//
				new PropertyInfo(IDocSegmentPage.KEY_IDOCRELEASE, Messages.IDocSegmentList_0, Messages.IDocSegmentList_1, empty), 
				new PropertyInfo(IDocSegmentPage.PROP_IDOC_BASIC_OR_EXTENSION_TYPE, empty, empty, 0),
				new PropertyInfo(IDocSegmentPage.KEY_IDOCTYPENAME, Messages.IDocSegmentList_2, Messages.IDocSegmentList_3, empty),
				new PropertyInfo(IDocSegmentPage.KEY_IDOCSEGMENTS, Messages.IDocSegmentList_4, Messages.IDocSegmentList_5, empty),
		//
				}));
		setLocation(pis, IDocSegmentPage.PAGE_NAME);
		PropertyInfoCollection result = new PropertyInfoCollection();
		for (PropertyInfo pi : pis) {
			result.addPropertyInfo(pi);
		}
		return result;
	}


	public String getIDocType() {
		return getValueAsNullIfEmpty(this.get(IDocSegmentPage.KEY_IDOCTYPENAME));
	}

	public List<String> getIDocSegments() {
		String s = this.get(IDocSegmentPage.KEY_IDOCSEGMENTS);
		return Utils.getTableListFromTextField(s);
	}

	public String getIDocRelease() {
		return this.get(IDocSegmentPage.KEY_IDOCRELEASE);
	}

	public boolean isExtensionIDocType() {
		int ix = this.getInt(IDocSegmentPage.PROP_IDOC_BASIC_OR_EXTENSION_TYPE);
		return ix != 0;
	}

	
	@Override
	public ValidatorBase createValidator() {
		return new IDocSegmentListValidator();
	}

}
