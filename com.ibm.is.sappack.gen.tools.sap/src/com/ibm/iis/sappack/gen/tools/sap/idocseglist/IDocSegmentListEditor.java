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

import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.editors.EditorPageBase;
import com.ibm.iis.sappack.gen.common.ui.editors.MultiPageEditorBase;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;


public class IDocSegmentListEditor extends MultiPageEditorBase {
	static String copyright() { 
 	   return Copyright.IBM_COPYRIGHT_SHORT; 
 	}	


	public IDocSegmentListEditor() {
		super(false);
		this.editorImage = ImageProvider.getImage(ImageProvider.IMAGE_IDOCS_16);
	}

	@Override
	protected ConfigurationBase createConfiguration(IResource resource) throws IOException, CoreException {
		return new IDocSegmentList(resource);
	}

	@Override
	protected EditorPageBase[] createPageList() {
		return new EditorPageBase[] { new IDocSegmentCollectionGeneralPage(), new IDocSegmentPage() };
	}

}
