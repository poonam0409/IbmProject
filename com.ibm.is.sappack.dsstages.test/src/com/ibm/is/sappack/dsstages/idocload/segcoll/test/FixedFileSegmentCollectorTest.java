package com.ibm.is.sappack.dsstages.idocload.segcoll.test;

import java.io.IOException;

import com.ibm.is.sappack.dsstages.common.DSEnvironment;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.RuntimeConfiguration;
import com.ibm.is.sappack.dsstages.idocload.SegmentCollector;
import com.ibm.is.sappack.dsstages.idocload.segcollimpl.FixedFilesSegmentCollector;

public class FixedFileSegmentCollectorTest extends SegmentCollectorTest {
	
	@Override
	protected SegmentCollector createSegmentCollector(DSEnvironment env, IDocType idocType) throws IOException {
		return new FixedFilesSegmentCollector(env, null, idocType, new ListSegmentValidationHandler(), RuntimeConfiguration.getRuntimeConfiguration().getFixedFilesSegmentCollectionMode_FileNumber());
	}
}
