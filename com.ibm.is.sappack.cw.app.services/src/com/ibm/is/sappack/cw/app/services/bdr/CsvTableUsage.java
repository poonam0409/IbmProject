package com.ibm.is.sappack.cw.app.services.bdr;

import java.util.ArrayList;

import com.ibm.is.sappack.cw.app.data.bdr.jpa.TableUsage;

public class CsvTableUsage {
	
	private TableUsage tableUsage;
	private ArrayList<String[]> content;

	public CsvTableUsage(TableUsage tableUsage) {
		this.setTableUsage(tableUsage);
	}

	public void setTableUsage(TableUsage tableUsage) {
		this.tableUsage = tableUsage;
	}

	public TableUsage getTableUsage() {
		return tableUsage;
	}

	public void setContent(ArrayList<String[]> content) {
		this.content = content;
	}

	public ArrayList<String[]> getContent() {
		if(content == null) {
			content = new ArrayList<String[]>(); 
		}
		return content;
	}
	
}
