package com.ibm.is.sappack.cw.app.data.rdm;

import java.util.Collection;

@SuppressWarnings("rawtypes")
public interface ITable {
	public String getName();

	public String getSapName();

	public Collection getColumns();

	public TableStatus getTableStatus();

	public void setTableStatus(TableStatus tableStatus);

	public int getRowCount();

	public void setRowCount(int rowCount);

	public Collection getNonMandtColumns();
	
	public String getLegacyId();
	
	public ReferenceTableType getTableType();
}
