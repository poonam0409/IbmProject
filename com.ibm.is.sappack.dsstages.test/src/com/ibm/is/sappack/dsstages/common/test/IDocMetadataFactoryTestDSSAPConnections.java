package com.ibm.is.sappack.dsstages.common.test;

import com.ibm.is.sappack.dsstages.common.DSSAPConnection;
import com.ibm.is.sappack.dsstages.common.IDocMetadataFactory;

@SuppressWarnings("nls")
public class IDocMetadataFactoryTestDSSAPConnections extends DSSAPConnectionsDirSetup {

	private void performTest(String connName) throws Exception {
		DSSAPConnection conn = IDocMetadataFactory.createDSSAPConnection();
		conn.initialize(connName);
		conn.createJCODestinationWithConnectionDefaults().ping();
		TestLog.log("SAP System " + connName + " pinged successfully");
		TestLog.log("connection test finished");
	}

	public void testCreateDSSAPConnection() throws Exception {
		performTest("BOCASAPERP5");
		performTest("SATURN2");
		this.tearDownDSSAPConnectionsDir = true;
	}

}
