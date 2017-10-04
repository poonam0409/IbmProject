package com.ibm.is.sappack.dsstages.idocload.segcoll.test;

import java.io.IOException;

import com.ibm.is.sappack.dsstages.common.DSEnvironment;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.idocload.SegmentCollector;
import com.ibm.is.sappack.dsstages.idocload.segcollimpl.InMemorySegmentCollector;

public class InMemSegmentCollectorTest extends SegmentCollectorTest {

	public SegmentCollector createSegmentCollector(DSEnvironment env, IDocType idocType) throws IOException {
		SegmentCollector coll = null;
		coll = new InMemorySegmentCollector(env, null, idocType, new ListSegmentValidationHandler());
		return coll;
	}

}
