package com.ibm.is.sappack.cw.tools.sap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

import com.ibm.iis.sappack.gen.common.ui.editors.PropertiesConstants;
import com.ibm.iis.sappack.gen.common.ui.util.FileHelper;
import com.ibm.iis.sappack.gen.common.ui.wizards.CreateFilesInDataDesignProjectWizard;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;

public class PrepareCWProjectWizard extends CreateFilesInDataDesignProjectWizard {

	String currentProject;
	
	public PrepareCWProjectWizard() {
		super();
		this.setWindowTitle(Messages.PrepareCWProjectWizard_0);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	public void addPages() {
		super.addPages();
		this.selectDataDesignProjectPage.setDescription(Messages.PrepareCWProjectWizard_1);
		this.selectDataDesignProjectPage.setTreatExistingFilesAsWarnings(true);
		
	}

	@Override
	protected Map<String, byte[]> getFilesToBeCreated(IProject p) {
		String[] files = new String[] { // 
				"CW_ALG0.rmcfg", // //$NON-NLS-1$
				"CW_ALG1.rmcfg", // //$NON-NLS-1$
				"CW_EXTRACT_INTO_STG.rgcfg", // //$NON-NLS-1$
				"CW_EXTRACT_NONREF_INTO_PLD.rgcfg", // //$NON-NLS-1$
				"CW_MOVE_ALG1_TO_PLD.rgcfg", // //$NON-NLS-1$
				"CW_LOAD_FROM_PLD_IDOC.rgcfg", // //$NON-NLS-1$
				"CW_PLD.rmcfg", // //$NON-NLS-1$
				"CW_PLD_IDOC.rmcfg", // //$NON-NLS-1$
				"CW_STG.rmcfg", // //$NON-NLS-1$
				"CW_TRANSCODE_ALG0_TO_ALG1.rgcfg", // //$NON-NLS-1$
		};
								
		Map<String, byte[]> result = new HashMap<String, byte[]>();
		for (String f : files) {
			byte[] b;
			try {
				b = Utilities.readInputStream(this.getClass().getClassLoader().getResourceAsStream("com/ibm/is/sappack/cw/tools/sap/cwfiles/" + f)); //$NON-NLS-1$
				Properties props = new Properties();
				props.loadFromXML(new ByteArrayInputStream(b));
				String newID = FileHelper.createID(Integer.toString((p.getName() + f).hashCode()));
				props.setProperty(PropertiesConstants.KEY_ID, newID);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				props.storeToXML(baos, null, FileHelper.FILE_ENCODING);
				b = baos.toByteArray();
			} catch (IOException e) {
				Activator.logException(e);
				continue;
			}
			result.put(f, b);
		}
		return result;
	}

}
