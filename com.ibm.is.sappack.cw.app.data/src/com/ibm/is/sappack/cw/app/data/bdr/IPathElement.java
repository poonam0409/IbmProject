package com.ibm.is.sappack.cw.app.data.bdr;

import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;

public interface IPathElement extends Cloneable {

	Integer getId();
	void setId(Integer id);
	String getName();
	void setName(String name);
	String getType();
	void setType(String type);
	
	Process getProcess();
	void setProcess(Process process);

	IPathElement clone();
	boolean sameAs(IPathElement pathElem);
}
