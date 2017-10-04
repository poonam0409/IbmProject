package com.ibm.iis.sappack.gen.common.ui.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class CachingTreeContentProvider implements ITreeContentProvider {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	Map<Object, List<Object>> childChache;
	Map<Object, List<Object>> elementsChache;
	Map<Object, Object> parentCache;
	Map<Object, Boolean> hasChildrenCache;

	ITreeContentProvider delegate;

	public CachingTreeContentProvider(ITreeContentProvider delegate) {
		this.delegate = delegate;
		this.childChache = new HashMap<Object, List<Object>>();
		this.elementsChache = new HashMap<Object, List<Object>>();
		this.parentCache = new HashMap<Object, Object>();
		this.hasChildrenCache = new HashMap<Object, Boolean>();
	}

	public Object[] getElements(Object inputElement) {
		List<Object> elements = elementsChache.get(inputElement);
		if (elements == null) {
			Object[] os = delegate.getElements(inputElement);
			elements = Arrays.asList(os);
			elementsChache.put(inputElement, elements);
		}
		return elements.toArray();
	}

	public Object[] getChildren(Object parentElement) {
		List<Object> elements = childChache.get(parentElement);
		if (elements == null) {
			Object[] os = delegate.getChildren(parentElement);
			elements = Arrays.asList(os);
			childChache.put(parentElement, elements);
		}
		return elements.toArray();
	}

	public void dispose() {
		delegate.dispose();
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		delegate.inputChanged(viewer, oldInput, newInput);
	}

	public Object getParent(Object element) {
		Object parent = parentCache.get(element);
		if (parent == null) {
			parent = delegate.getParent(element);
			parentCache.put(element, parent);
		}
		return parent;
	}

	public boolean hasChildren(Object element) {
		Boolean b = this.hasChildrenCache.get(element);
		if (b == null) {
			b = delegate.hasChildren(element);
			hasChildrenCache.put(element, b);
		}
		return b;
	}
	
	public void refresh() {
		this.childChache.clear();
		this.elementsChache.clear();
		this.hasChildrenCache.clear();
		this.parentCache.clear();
	}

}
