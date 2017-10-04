package com.ibm.is.sappack.gen.tools.sap.utilities;

import java.util.Map;

public interface IResult {

	public abstract int getCurrentRowPosition();

	public abstract int getNumberOfRows();

	public abstract boolean nextRow();

	public abstract String getValue(String columnName);

	public abstract Map<String, String> getRow();

}