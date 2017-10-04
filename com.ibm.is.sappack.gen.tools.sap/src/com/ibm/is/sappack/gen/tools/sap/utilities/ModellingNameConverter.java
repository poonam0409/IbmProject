package com.ibm.is.sappack.gen.tools.sap.utilities;

import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmNameConverter;

public class ModellingNameConverter implements LdmNameConverter {

	@Override
	public String convertEntityName(String entityName) {
		return entityName;
	}

	@Override
	public String convertAttributeName(String attributeName) {
		return attributeName;
	}

	@Override
	public String convertRelationShipName(String relationShipName) {
		return relationShipName;
	}

	@Override
	public String convertRelationShipName(String relationShipName, int suffixLen) {
		return relationShipName;
	}

	@Override
	public String convertAtomicDomainName(String domain) {
		return domain;
	}


}
