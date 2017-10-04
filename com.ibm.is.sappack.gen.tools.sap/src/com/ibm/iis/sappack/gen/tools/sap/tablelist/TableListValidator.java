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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.tablelist
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.tablelist;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.validators.ValidatorBase;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public class TableListValidator extends ValidatorBase {
	private static final String TABLELISTMARKERID = Activator.PLUGIN_ID + ".tablelistmarker"; //$NON-NLS-1$

	private Pattern validTableNamePattern;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

  	
	public TableListValidator() {
		super();
		validTableNamePattern = Pattern.compile("(\\p{Alnum}|/|_)+"); //$NON-NLS-1$
	}

	@Override
	protected void validateImpl(ConfigurationBase configuration, IProgressMonitor monitor) {
		TableList tl = (TableList) configuration;
		markPropertyAsRequired(TablesPage.KEY_TABLES);
		Collection<String> tables = tl.getTables();
		if (tables != null) {
			for (String t : tables) {
				Matcher m = validTableNamePattern.matcher(t);
				if (!m.matches()) {
					String msg = MessageFormat.format(Messages.TableListValidator_0, t);
					addWarningMessage(msg, TablesPage.KEY_TABLES);
				}
			}
		} else {
			addErrorMessage(Messages.TableListValidator_1, TablesPage.KEY_TABLES);
		}
	}

	@Override
	protected String getMarkerID() {
		return TABLELISTMARKERID;
	}

	@Override
	protected ConfigurationBase createConfiguration(IResource resource) throws IOException, CoreException {
		return new TableList(resource);
	}

	
	public static void main(String[] args) {
		try {
			Pattern validTableNamePattern = Pattern.compile("(\\p{Alnum}|/|_)+"); //$NON-NLS-1$
			String t = "AA/a_a#a"; //$NON-NLS-1$
			Matcher m = validTableNamePattern.matcher(t);
			System.out.println("Matches: " + m.matches()); //$NON-NLS-1$
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
