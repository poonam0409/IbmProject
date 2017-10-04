package com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.validator;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertiesConstants;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.validators.ValidatorBase;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.ABAPExtractSettingsPage;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.DBPage;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.DBPageProperties;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.MoveTranscodeSettingsPage;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.RGConfiguration;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.RGConfiguration.CONFIGURATION_TYPE;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.FileStageData;
import com.ibm.is.sappack.gen.common.ODBCStageData;
import com.ibm.is.sappack.gen.common.PersistenceData;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;

public class RGConfValidator extends ValidatorBase {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	private static final String MARKERID = Activator.PLUGIN_ID + ".rgcfgmarker"; //$NON-NLS-1$

	public RGConfValidator() {
		super();
	}

	private void validateODBCPage(RGConfiguration rgconf) {
		if (rgconf.getConfigurationType() == CONFIGURATION_TYPE.SAP_EXTRACT) {
			validatePersistenceData(rgconf, rgconf.getTargetPersistenceData(), false);
		} else if (rgconf.getConfigurationType() == CONFIGURATION_TYPE.SAP_LOAD) {
			validatePersistenceData(rgconf, rgconf.getSourcePersistenceData(), true);
		} else if (rgconf.getConfigurationType() == CONFIGURATION_TYPE.MOVE) {
			validatePersistenceData(rgconf, rgconf.getSourcePersistenceData(), true);
			validatePersistenceData(rgconf, rgconf.getTargetPersistenceData(), false);
		}
	}
	
	private void validateSAPPage(RGConfiguration rgconf) {
		if (rgconf.getConfigurationType() == CONFIGURATION_TYPE.SAP_EXTRACT) {
			validateSAPSystemData(rgconf);
		} else if (rgconf.getConfigurationType() == CONFIGURATION_TYPE.SAP_LOAD) {
			validateSAPSystemData(rgconf);
		}
	}
	
	private void validateSAPSystemData(RGConfiguration rgconf) {
		String dsSAPConnection = rgconf.getProperties().get(PropertiesConstants.PROP_SAP_CONN_NAME);
		if (dsSAPConnection != null && !dsSAPConnection.trim().isEmpty() && !Utils.isJobParameter(dsSAPConnection)) {
			dsSAPConnection = dsSAPConnection.trim();
			if (!dsSAPConnection.equals(dsSAPConnection.toUpperCase())) {
				this.addErrorMessage(Messages.RGConfValidator_3, PropertiesConstants.PROP_SAP_CONN_NAME); 
			}
		}
		
		String pw = rgconf.getProperties().get(PropertiesConstants.PROP_SAP_PASSWORD);
		if (pw != null && !pw.trim().isEmpty() && !Utils.isJobParameter(pw)) {
			this.addWarningMessage(Messages.RGConfValidator_0, PropertiesConstants.PROP_SAP_PASSWORD); 
		}
		
		// some fields are not allowed to contain Job parameters
		if (rgconf.getGenerateRFCDestinationNames()) {
			markPropertyWhenHaveJobParam(rgconf, ABAPExtractSettingsPage.PROP_DYNAMIC_RFC_PREFIX);
		}
		if (rgconf.getUseBackground()) {
			markPropertyWhenHaveJobParam(rgconf, ABAPExtractSettingsPage.PROP_BG_VARIANT_PREFIX);
			markPropertyWhenHaveJobParam(rgconf, ABAPExtractSettingsPage.PROP_STARTUP_DELAY);
		}
	}

	private void markPropertyWhenHaveJobParam(RGConfiguration rgconf, String propName) {
		String noJobParamValue = rgconf.getProperties().get(propName);
		if (noJobParamValue != null) {
			noJobParamValue = noJobParamValue.trim();
			if (!noJobParamValue.isEmpty() && StringUtils.isJobParamVariable(noJobParamValue)) {
				String readablePropName = getReadablePropertyName(propName);
				if (readablePropName == null) {
					readablePropName = propName;
				}
				String errMsg = MessageFormat.format(Messages.RGConfValidator_5, readablePropName );
				this.addErrorMessage(errMsg, propName); 
			}
		}
	}
	

	private void validatePersistenceData(RGConfiguration rgconf, PersistenceData p, boolean isSource) {
		DBPageProperties dbProperties = new DBPageProperties(isSource);

		if (p instanceof ODBCStageData) {
			String[] reqProps = new String[] { dbProperties.VALUE_SUBKEY_ODBC_DATASOURCE };
			markPropertiesAsRequired(reqProps);
			
			String cp = rgconf.getProperties().get(dbProperties.VALUE_SUBKEY_ODBC_CODE_PAGE_SEL);
			if ( DBPage.ODBC_CODE_PAGE_TYPE_USER_DEFINED.equals(cp)) {
				markPropertyAsRequired(dbProperties.VALUE_SUBKEY_ODBC_CODE_PAGE_NAME);
			}

			String pw = rgconf.getProperties().get(dbProperties.VALUE_SUBKEY_ODBC_PASSWORD);
			if (pw != null && !pw.trim().isEmpty() && !Utils.isJobParameter(pw)) {
				this.addWarningMessage(Messages.RGConfValidator_1, dbProperties.VALUE_SUBKEY_ODBC_PASSWORD); 
			}

			reqProps = new String[] { dbProperties.VALUE_SUBKEY_ODBC_ARRAY_SIZE, dbProperties.VALUE_SUBKEY_ODBC_RECORD_COUNT };
			markPropertiesAsRequired(reqProps);

			// AlternateSchemaName must not be empty if it is enabled
			String useAlternateSchema = rgconf.getProperties().get(dbProperties.VALUE_SUBKEY_ODBC_USE_ALT_SCHEMA);
			if (useAlternateSchema != null && Boolean.parseBoolean(useAlternateSchema)) {

				String alternateSchema = rgconf.getProperties().get(dbProperties.VALUE_SUBKEY_ODBC_ALT_SCHEMA_NAME);
				if (alternateSchema == null || alternateSchema.isEmpty()) {
					markPropertyAsRequired(dbProperties.VALUE_SUBKEY_ODBC_ALT_SCHEMA_NAME);
				}
			}
		} else if (p instanceof FileStageData) {
			String[] reqProps = new String[] { dbProperties.VALUE_SUBKEY_FILE_NAME };
			markPropertiesAsRequired(reqProps);

		}

	}

	@Override
	protected void validateImpl(ConfigurationBase configuration, IProgressMonitor monitor) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		RGConfiguration conf = (RGConfiguration) configuration;
		String[] sapConnProperties = new String[] {
				//
				PropertiesConstants.PROP_SAP_CONN_NAME, //
				PropertiesConstants.PROP_SAP_USER, //
				PropertiesConstants.PROP_SAP_PASSWORD, //
				PropertiesConstants.PROP_SAP_CLIENT, //
				PropertiesConstants.PROP_SAP_LANGUAGE
		//
		};

		if (conf.getConfigurationType() == CONFIGURATION_TYPE.SAP_EXTRACT) {
			this.markPropertiesAsRequired(sapConnProperties);
			validateSAPPage(conf);
			validateODBCPage(conf);
			if (!conf.getGenerateRFCDestinationNames()) {
				Map<String, String> rfcDescMap = new HashMap<String, String>();
				String msg = conf.getStaticRFCDestinations(rfcDescMap);
				if (msg != null) {
					this.addErrorMessage(msg, ABAPExtractSettingsPage.PROP_RFC_DEST_TEXT);
				}
				this.markPropertyAsRequired(ABAPExtractSettingsPage.PROP_RFC_DEST_TEXT);
			} else {
				this.markPropertyAsRequired(ABAPExtractSettingsPage.PROP_DYNAMIC_RFC_PREFIX);
/*
				String pfx = conf.getRFCDestinationNameGeneratorPrefix();
				if (pfx == null || pfx.trim().isEmpty()) {
					addErrorMessage("The RFC Destination name prefix must not be empty", ABAPExtractSettingsPage.PROP_DYNAMIC_RFC_PREFIX);
				}
	*/			
			}
			markPropertiesAsRequired(new String[]{ABAPExtractSettingsPage.PROP_SAP_GATEWAYHOST, ABAPExtractSettingsPage.PROP_SAP_GATEWAYSERVICE });
			if (conf.getUseBackground()) {
				markPropertiesAsRequired(new String[]{ABAPExtractSettingsPage.PROP_BG_VARIANT_PREFIX, ABAPExtractSettingsPage.PROP_STARTUP_DELAY });
			}
		} else if (conf.getConfigurationType() == CONFIGURATION_TYPE.SAP_LOAD) {
			this.markPropertiesAsRequired(sapConnProperties);
			validateSAPPage(conf);
			validateODBCPage(conf);
		} else if (conf.getConfigurationType() == CONFIGURATION_TYPE.MOVE) {
			validateODBCPage(conf);
			/* move */
			if (conf.getNumberOfFlows_Move() == -1) {
				this.addErrorMessage(Messages.RGConfValidator_2, MoveTranscodeSettingsPage.PROP_MOVE_MAX_NUMBER_FLOWS);
			}

			/// target system legacy ID must be specified if transcoding options are selected
			if (conf.isTranscodeReferenceFields() || conf.isTranscodeNonReferenceFields() || conf.isTranscodeDomainValueFields()) {
				if (conf.getTargetSystemLegacyID().equals(Constants.EMPTY_STRING)) {
					this.markPropertyAsRequired(MoveTranscodeSettingsPage.PROP_TRANSCODE_TARGET_LEGACY_ID);
				}

				if (!(conf.getSourcePersistenceData() instanceof ODBCStageData)) {
					this.addErrorMessage(Messages.RGConfValidator_4, MoveTranscodeSettingsPage.PROP_TRANSCODE_REFERENCE_FIELDS);
				}
			}
		}
	}

	@Override
	protected String getMarkerID() {
		return MARKERID;
	}

	@Override
	protected ConfigurationBase createConfiguration(IResource resource) throws IOException, CoreException {
		return new RGConfiguration(resource);
	}

}
