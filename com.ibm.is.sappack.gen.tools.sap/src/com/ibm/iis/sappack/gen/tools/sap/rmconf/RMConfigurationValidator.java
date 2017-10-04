//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.rmconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.rmconf;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.validators.ValidatorBase;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap.SapDataType;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalField;


public class RMConfigurationValidator extends ValidatorBase {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public static final String MARKERID = Activator.PLUGIN_ID + ".rmconfmarker"; //$NON-NLS-1$
	
	@Override
	protected String getMarkerID() {
		return MARKERID;
	}

	@Override
	protected ConfigurationBase createConfiguration(IResource resource) throws IOException, CoreException {
		return new RMConfiguration(resource);
	}

	@Override
	protected void validateImpl(ConfigurationBase configuration, IProgressMonitor monitor) {
		RMConfiguration rmConf = (RMConfiguration) configuration;
		
		// first check the DB object length
		String resultMsg = rmConf.checkDBObjectLength(); 
		if (resultMsg != null) {
			this.addErrorMessage(resultMsg, TargetDBPage.PROP_TARGETDB_MAXLENGTH); 
		}
		else {
			// check Technical Fields only if they are to be created
			if (rmConf.getTableImportOptions().isCreateTechnicalFields()) {

				List<TechnicalField> fieldNames = rmConf.getTechnicalFields();
				// check if duplicate fields exist
				Set<String> seenFieldNames = new HashSet<String>();
				int lineNo = 0;
				for (TechnicalField tf : fieldNames) {
					lineNo ++;
					String n = tf.getFieldName();

					// field name must be entered
					if (n.length() == 0) {
						String message = MessageFormat.format(Messages.TechnicalFieldsWizardPage_17, Integer.toString(lineNo));
						this.addErrorMessage(message, TechnicalFieldsPage.SETTING_NAME); 
//						break;
					}
					else {
						// field name must be unique
						if (seenFieldNames.contains(n)) {
							String message = Messages.TechnicalFieldsWizardPage_14;
							message = MessageFormat.format(message, n);
							this.addErrorMessage(message, TechnicalFieldsPage.SETTING_NUMBER_OF_TECH_FIELDS); 
							break;
						}
						seenFieldNames.add(n);
					}

					// character fields must not a have a length of 0
					SapDataType sapDt = tf.getDataType();
					if (sapDt.getDataTypeName().startsWith("VAR") && sapDt.getLength() <= 0) {   //$NON-NLS-1$
						String message = Messages.TechnicalFieldsWizardPage_16;
						message = MessageFormat.format(message, n);
						this.addErrorMessage(message, TechnicalFieldsPage.SETTING_NUMBER_OF_TECH_FIELDS);
//						break;
					}
				} // end of for (TechnicalField tf : fieldNames)
			} // end of if (rmConf.getTableImportOptions().isCreateTechnicalFields())
		} // end of (else) if (resultMsg != null)
	}

}
