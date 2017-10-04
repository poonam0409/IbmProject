package com.ibm.is.sappack.gen.help;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

import com.ibm.iis.sappack.gen.common.ui.wizards.CreateFilesInDataDesignProjectWizard;

public class ConfigRMRGExampleFilesWizard extends CreateFilesInDataDesignProjectWizard {

	Map<String, byte[]> files;

	String[] exampleFiles = new String[] {
			//
			"ExampleRapidGeneratorConfiguration_MoveTranscode.rgcfg", // //$NON-NLS-1$
			"ExampleRapidGeneratorConfiguration_SAPExtract.rgcfg", // //$NON-NLS-1$
			"ExampleRapidGeneratorConfiguration_SAPLoad.rgcfg", // //$NON-NLS-1$
			"ExampleRapidModelerConfiguration.rmcfg", // //$NON-NLS-1$
			"ExampleABAPTableList.rmtl", // //$NON-NLS-1$
			"ExampleIDocSegmentList.rmil", // //$NON-NLS-1$
	//
	};

	public ConfigRMRGExampleFilesWizard() {
		files = new HashMap<String, byte[]>();
		for (String s : exampleFiles) {
			byte[] b = getContents(this.getClass().getClassLoader().getResourceAsStream("exampleConfigFiles/" + s)); //$NON-NLS-1$
			files.put(s, b);
		}
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	protected Map<String, byte[]> getFilesToBeCreated(IProject p) {
		return files;
	}

}
