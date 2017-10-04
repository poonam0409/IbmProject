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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.views.navigator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.views.navigator;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import com.ibm.iis.sappack.gen.common.ui.util.IFileFilter;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public class ProjectCategory {
  	private   IProject project;
  	protected String name;
  	private   IFileFilter fileFilter;
  	protected Image img;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public ProjectCategory(IProject project, Image img, IFileFilter fileFilter, String categoryName) {
		this.project = project;
		this.name = categoryName;
		this.fileFilter = fileFilter;
		this.img = img;
	}

	public List<Object> getChildren() {
		List<Object> kids = new ArrayList<Object>();
		addChildren(project, kids);
		return kids;
	}

	private void addChildren(IContainer container, List<Object> children) {
		IResource[] res = null;
		try {
			res = container.members();
		} catch (CoreException e) {
			Activator.logException(e);
			return;
		}
		for (IResource r : res) {
			if (r instanceof IFile) {
				IFile f = (IFile) r;
				if (this.fileFilter != null) {
					if (this.fileFilter.accept(f)) {
						children.add(f);
					}
				}
			} else if (r instanceof IFolder) {
				IFolder folder = (IFolder) r;
				addChildren(folder, children);
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof ProjectCategory)) {
			return false;
		}
		ProjectCategory pc = (ProjectCategory) o;
		return pc.project.equals(this.project) && pc.name.equals(this.name);
	}

	@Override
	public int hashCode() {
		return this.project.hashCode() + this.name.hashCode();
	}

	public IProject getProject() {
		return project;
	}

	public String getName() {
		return name;
	}

}