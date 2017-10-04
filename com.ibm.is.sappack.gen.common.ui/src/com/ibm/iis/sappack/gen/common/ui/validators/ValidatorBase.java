//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.common.ui.validators
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.iis.sappack.gen.common.ui.validators;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;

import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertyInfo;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertyInfoCollection;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;


public abstract class ValidatorBase extends AbstractValidator {
	public  static final String MARKER_ATTR_MODEL_PROPERTY = "MARKER_ATTR_MODEL_PROPERTY"; //$NON-NLS-1$

	private IResource              currentResource;
	private ValidationResult       currentValidationResult;
	private PropertyInfoCollection currentPropertyInfoCollection;
	private ConfigurationBase      currentConfiguration;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public ValidatorBase() {
		super();
	}

	protected abstract String getMarkerID();

	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		ValidationResult res = new ValidationResult();

		if (!(resource instanceof IFile)) {
			return res;
		}
		try {
			ConfigurationBase conf = createConfiguration(resource);
			return validate(conf, monitor);
		} catch (Exception exc) {
			exc.printStackTrace();
			Activator.logException(exc);
		}
		return res;
	}

	protected abstract ConfigurationBase createConfiguration(IResource resource) throws IOException, CoreException;

	protected void markPropertyAsRequired(String requiredProperty) {
		markPropertiesAsRequired(new String[] { requiredProperty });
	}

	protected void markPropertiesAsRequired(String[] requiredProps) {
		Map<String, String> fileProperties = this.currentConfiguration.getProperties();
		for (String property : requiredProps) {
			String val = fileProperties.get(property);
			if (val == null || val.trim().isEmpty()) {
				String readableName = getReadablePropertyName(property);
				if (readableName == null) {
					readableName = property;
				}
				String msg = MessageFormat.format(Messages.ValidatorBase_0, readableName);
				addErrorMessage(msg, property);
			}
		}
	}

	protected String getReadablePropertyName(String propName) {
		String retReadableName = null;
		PropertyInfo pi = this.currentPropertyInfoCollection.getPropertyInfo(propName);
		if (pi != null) {
			retReadableName = pi.getReadableName();
		}

		return(retReadableName);
	}

	protected void checkPropertiesForIllegalChars(String prop2Check, String illegalCharsList) {
		PropertyInfo pi = this.currentPropertyInfoCollection.getPropertyInfo(prop2Check);
		if (pi != null) {
			Map<String, String> fileProperties = this.currentConfiguration.getProperties();
			String propValue = fileProperties.get(prop2Check);

			int     idx              = 0;
			boolean invalidCharFound = false;
			while (idx < illegalCharsList.length() && !invalidCharFound) {
				char char2Check = illegalCharsList.charAt(idx);
				if (propValue.indexOf(char2Check) > -1) {
					String msg = MessageFormat.format(Messages.ValidatorBase_1, String.valueOf(char2Check), propValue);
					addErrorMessage(msg, prop2Check);

					invalidCharFound = true;
				}
				idx ++;
			}
		}
	}
	
	protected final static int SEVERITY_ERROR = IMarker.SEVERITY_ERROR; 
	protected final static int SEVERITY_WARNING = IMarker.SEVERITY_WARNING; 
	
	protected void addErrorMessage(String msg, String prop) {
		this.addMessage(msg, prop, IMarker.SEVERITY_ERROR);
	}

	protected void addWarningMessage(String msg, String prop) {
		this.addMessage(msg, prop, IMarker.SEVERITY_WARNING);
	}

	
	protected void addMessage(String msg, String prop, int severity) {
		ValidatorMessage vm = ValidatorMessage.create(msg, this.currentResource);
		vm.setAttribute(IMarker.SEVERITY, severity);
		PropertyInfo pi = this.currentPropertyInfoCollection.getPropertyInfo(prop);
		if (pi != null) {
			vm.setAttribute(IMarker.LOCATION, pi.getLocation());
		}
		vm.setAttribute(ValidatorBase.MARKER_ATTR_MODEL_PROPERTY, prop);
		vm.setType(getMarkerID());
		this.currentValidationResult.add(vm);
	}

	/**
	 * Call this function directly from the editor.
	 */
	public ValidationResult validate(ConfigurationBase conf, IProgressMonitor monitor) throws IOException, CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		this.currentConfiguration = conf;
		ValidationResult res = new ValidationResult();
		this.currentValidationResult = res;
		this.currentResource = conf.getResource();
		PropertyInfoCollection coll = conf.getPropertyCollection();
		this.currentPropertyInfoCollection = coll;

		try {
			validateImpl(conf, monitor);
		} catch (Exception exc) {
			// catch all exceptions to make validation more robust
			Activator.logException(exc);
		}
		return res;
	}

	protected abstract void validateImpl(ConfigurationBase configuration, IProgressMonitor monitor);
}
