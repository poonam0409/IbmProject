package com.ibm.is.sappack.cw.app.services.bdr;

import java.util.ArrayList;

public class MpxProject {
	
	private ArrayList<MpxRecord> recordList;
	
	public void setRecordList(ArrayList<MpxRecord> recordList) {
		this.recordList = recordList;
	}

	public ArrayList<MpxRecord> getRecordList() {
		if(recordList == null) {
			recordList = new ArrayList<MpxRecord>();
		}
		return recordList;
	}
	
}
