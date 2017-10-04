package com.ibm.is.sappack.gen.help;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

import com.ibm.iis.sappack.gen.common.ui.wizards.CreateFilesInDataDesignProjectWizard;
import com.ibm.is.sappack.gen.common.ui.Activator;

public class ExampleTableListWizard extends CreateFilesInDataDesignProjectWizard {

	static String copyright()
	   { return com.ibm.is.sappack.gen.help.Copyright.IBM_COPYRIGHT_SHORT; }

	Map<String, byte[]> file;
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
	}

	public static byte[] replaceString(byte[] xmlutf8Contents, String oldString, String newString) {
		try {
			String s = new String(xmlutf8Contents, "UTF-8"); //$NON-NLS-1$
			s = s.replaceAll(oldString, newString);
			return s.getBytes("UTF-8"); //$NON-NLS-1$
		} catch(Exception exc) {
			exc.printStackTrace();
			Activator.logException(exc);
			return xmlutf8Contents;
		}
	}
	
	@SuppressWarnings("nls")
	@Override
	protected Map<String, byte[]> getFilesToBeCreated(IProject p) {
		if (file == null) {
			file = new HashMap<String, byte[]>();
			String[][] exampleFiles = new String[][] {
					{"ExampleTableList_BusinessPartner.rmtl", "ExampleTableList_BusinessPartner.rmtl" }, 
					{"ExampleTableList_Customer.rmtl", "ExampleTableList_Customer.rmtl"},
					{"ExampleTableList_Inventory.rmtl", "ExampleTableList_Inventory.rmtl"},
					{"ExampleTableList_Material.rmtl", "ExampleTableList_Material.rmtl"},
			};
			for (String[] ex : exampleFiles) {
				String id = "EXAMPLEID" + System.currentTimeMillis() + Math.abs(ex[0].hashCode() ); //$NON-NLS-1$
				byte[] b = replaceString(getContents(this.getClass().getClassLoader().getResourceAsStream(ex[1])), "#IDPLACEHOLDER#", id);  //$NON-NLS-1$
				file.put(ex[0], b);				
			}
		}
		return file;
	}

}
