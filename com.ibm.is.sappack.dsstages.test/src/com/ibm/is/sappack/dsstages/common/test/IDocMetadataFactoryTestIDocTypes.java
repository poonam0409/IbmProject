package com.ibm.is.sappack.dsstages.common.test;

import java.util.List;

import com.ibm.is.sappack.dsstages.common.DSSAPConnection;
import com.ibm.is.sappack.dsstages.common.IDocMetadataFactory;
import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.IDocTypeConfiguration;
import com.ibm.is.sappack.dsstages.common.Utilities;
import com.ibm.is.sappack.dsstages.common.impl.IDocTypeImpl;
import com.ibm.is.sappack.dsstages.common.util.IDocMetadataFileHandler;
import com.sap.conn.jco.JCoDestination;

@SuppressWarnings("nls")
public class IDocMetadataFactoryTestIDocTypes extends DSSAPConnectionsDirSetup {

	public void testCreateIDocType() throws Exception {
		DSSAPConnection conn = IDocMetadataFactory.createDSSAPConnection();
		conn.initialize("BOCASAPERP5");
		String[][] idocTypeNames = { { "DEBMAS06", "700" }, { "MATMAS05", "700" } };

		JCoDestination jcoDest = conn.createJCODestinationWithConnectionDefaults();
		int foundIDocs = 0;
		List<IDocTypeConfiguration> idocConfigs = conn.getIDocTypeConfigurations();
		for (IDocTypeConfiguration idocConfig : idocConfigs) {
			String idocTypeName = idocConfig.getIDocTypeName();
			String release = idocConfig.getRelease();
			for (int i = 0; i < idocTypeNames.length; i++) {
				if (idocTypeNames[i][0].equals(idocTypeName)) {
					foundIDocs++;
					TestLog.log("Found idoc typeName: " + idocTypeName);
					assertEquals(idocTypeNames[i][1], release);
					IDocType idocType =
					      IDocMetadataFactory.createIDocType(jcoDest, conn.getSapSystem().getName(), idocTypeName, null, release);
					TestLog.log("Retrieved IDoc type: " + idocType.getIDocTypeName());
					if (idocType.getIDocTypeName().equals("DEBMAS06")) {
						String[] existingSegmentTypes =
						      new String[] { "E1KNB1H", "E1KNA1M", "E1KNA11", "E1KNVKM", "E1KNVKH" };
						for (int j = 0; j < existingSegmentTypes.length; j++) {
							IDocSegment seg = Utilities.findIDocSegment(idocType, existingSegmentTypes[j]);
							TestLog.log("Found segment type " + existingSegmentTypes[j] + ": " + seg);
							assertNotNull(seg);
						}

						assertNull(Utilities.findIDocSegment(idocType, "BLABLAABL"));

					}				
					
					IDocMetadataFileHandler mdfh = new IDocMetadataFileHandler();
					mdfh.initialize(conn.getSapSystem().getName(), idocTypeName, null, release);
					TestLog.log("Writing metadata to file");
					mdfh.writeMetadataToFile((IDocTypeImpl) idocType);
					TestLog.log("Reading metadata from file again");
					IDocType idocType2 = mdfh.readMetadataFromFile();
					assert( idocType != idocType2 );
					assertEquals(idocType.getIDocTypeName(), idocType2.getIDocTypeName());
					
					break;
				}
			}
		}
	//	assertEquals(idocTypeNames.length, foundIDocs);

		TestLog.log("testCreateIDoctype() finished");
		this.tearDownDSSAPConnectionsDir = false;
	}

}
