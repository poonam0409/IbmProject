package com.ibm.iis.sappack.gen.tools.jobgenerator.api;

import com.ibm.iis.sappack.gen.tools.sap.api.IProgressCallback;

public interface RapidGenerator {

	void generateJobs( //
			String rapidGeneratorConfiguration, //
			String sourceModel, //
			String targetModel, //
			RapidGeneratorSAPSettings settings, //
			String iisConnectionName, //
			String iisProject, //
			String iisFolder, //
			String jobNamePrefix, //
			boolean overwriteJobs, //
			IProgressCallback callback //
	) throws RapidGeneratorException;

}
