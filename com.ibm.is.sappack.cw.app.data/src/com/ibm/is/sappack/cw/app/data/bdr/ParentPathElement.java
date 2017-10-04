package com.ibm.is.sappack.cw.app.data.bdr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParentPathElement extends PathElement {

	private final List<IPathElement> children;

	public ParentPathElement() {
		this.children = new ArrayList<IPathElement>();
	}

	public void addChild(IPathElement child) {
		this.children.add(child.clone());
	}

	public List<IPathElement> getChildren() {
		return this.children;
	}

	@Override
	public ParentPathElement clone() {
		ParentPathElement clone = new ParentPathElement();

		clone.setId(this.id);
		clone.setType(this.type);
		clone.setName(this.name);
		clone.setProcess(this.process);

		Iterator<IPathElement> childrenIterator = this.children.iterator();

		while (childrenIterator.hasNext()) {
			clone.addChild(childrenIterator.next());
		}

		return clone;
	}
}
