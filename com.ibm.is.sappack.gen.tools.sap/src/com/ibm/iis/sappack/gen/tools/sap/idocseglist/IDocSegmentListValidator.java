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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.validators.ValidatorBase;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public class IDocSegmentListValidator extends ValidatorBase {

	private static final String ILLEGAL_IDOC_SEGMENT_NAME_CHARS = " \t\f\"'+=|~#@!{}[]()-:;^?<>\\.";  
	private static final String ILLEGAL_IDOC_TYPE_NAME_CHARS    = ILLEGAL_IDOC_SEGMENT_NAME_CHARS + ",";  
	private static final String IDOC_SEGMENT_LIST_MARKERID      = Activator.PLUGIN_ID + ".idocsegmentlistmarker"; //$NON-NLS-1$

	public  static final char   SEARCH_WILDCARD_COMMON          = '*';
	public  static final char   SEARCH_WILDCARD_SAP             = '%';

	
  	static String copyright() { 
 	   return Copyright.IBM_COPYRIGHT_SHORT; 
 	}

	@Override
	protected String getMarkerID() {
		return IDOC_SEGMENT_LIST_MARKERID;
	}

	@Override
	protected ConfigurationBase createConfiguration(IResource resource) throws IOException, CoreException {
		return new IDocSegmentList(resource);
	}

	@Override
	protected void validateImpl(ConfigurationBase configuration, IProgressMonitor monitor) {
		markPropertyAsRequired(IDocSegmentPage.KEY_IDOCTYPENAME);
		markPropertyAsRequired(IDocSegmentPage.KEY_IDOCSEGMENTS);
		configuration.getPropertyCollection().getPropertyInfo(IDocSegmentPage.KEY_IDOCTYPENAME);

		checkPropertiesForIllegalChars(IDocSegmentPage.KEY_IDOCTYPENAME, ILLEGAL_IDOC_TYPE_NAME_CHARS);
		checkPropertiesForIllegalChars(IDocSegmentPage.KEY_IDOCSEGMENTS, ILLEGAL_IDOC_SEGMENT_NAME_CHARS);
	}

}
