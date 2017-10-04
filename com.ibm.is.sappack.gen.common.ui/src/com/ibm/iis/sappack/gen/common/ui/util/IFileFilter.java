package com.ibm.iis.sappack.gen.common.ui.util;

import org.eclipse.core.resources.IFile;

public interface IFileFilter {
  	static String copyright = Copyright.IBM_COPYRIGHT_SHORT; 
 

	boolean accept(IFile file);
}