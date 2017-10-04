package com.ibm.is.sappack.dsstages.idocload.segcoll.test;

import java.io.IOException;

import com.ibm.is.sappack.dsstages.common.DSEnvironment;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.RuntimeConfiguration;
import com.ibm.is.sappack.dsstages.idocload.SegmentCollector;
import com.ibm.is.sappack.dsstages.idocload.segcollimpl.VariableFilesSegmentCollector;

public class VariableFileSegmentCollectorTest extends SegmentCollectorTest {

	@Override
	protected SegmentCollector createSegmentCollector(DSEnvironment env, IDocType idocType) throws IOException {
		return new VariableFilesSegmentCollector(env, null, idocType, new ListSegmentValidationHandler(), RuntimeConfiguration.getRuntimeConfiguration().getVariableFilesSegmentCollectionMode_MaximumNumberOfIDocsPerFile());
	}

}
