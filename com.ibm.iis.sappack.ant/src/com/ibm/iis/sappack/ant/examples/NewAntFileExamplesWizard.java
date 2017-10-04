package com.ibm.iis.sappack.ant.examples;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

import com.ibm.iis.sappack.gen.common.ui.wizards.CreateFilesInDataDesignProjectWizard;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;

public class NewAntFileExamplesWizard extends CreateFilesInDataDesignProjectWizard {

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@SuppressWarnings("nls")
	@Override
	protected Map<String, byte[]> getFilesToBeCreated(IProject p) {
		InputStream is = this.getClass().getResourceAsStream("rmrgBuildFileExample.xml"); //$NON-NLS-1$
		if (is == null) {
			throw new RuntimeException("An unexpected error occured while loading Ant example file");
		}
		byte[] b;
		try {
			b = Utilities.readInputStream(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Map<String, byte[]> result = new HashMap<String, byte[]>();
		result.put("rmrgBuildFileExample.xml", b); //$NON-NLS-1$
		return result;
	}

}
