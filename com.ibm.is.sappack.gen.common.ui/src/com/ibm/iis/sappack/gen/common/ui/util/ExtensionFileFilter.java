package com.ibm.iis.sappack.gen.common.ui.util;

import org.eclipse.core.resources.IFile;


public class ExtensionFileFilter implements IFileFilter {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

  	String fileExtension;

	public ExtensionFileFilter(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	@Override
	public boolean accept(IFile file) {
		return file.getName().endsWith(fileExtension);
	}

}