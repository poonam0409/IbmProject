package com.ibm.is.sappack.cw.app.data.bdr;

import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;

public class PathElement implements IPathElement {

	protected Integer id;
	protected String name;
	protected String type;

	protected Process process;

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void setProcess(Process process) {
		this.process = process;
	}

	@Override
	public Process getProcess() {
		return this.process;
	}

	@Override
	public PathElement clone() {
		PathElement clone = new PathElement();

		clone.setId(this.id);
		clone.setType(this.type);
		clone.setName(this.name);
		clone.setProcess(this.process);

		return clone;
	}

	@Override
	public boolean sameAs(IPathElement elem) {
		if (this.id == elem.getId() && this.name == elem.getName() && this.type == elem.getType()) {
			return true;
		}

		return false;
	}
}
