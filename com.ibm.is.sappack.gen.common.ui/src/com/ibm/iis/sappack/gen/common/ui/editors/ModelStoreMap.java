//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.common.ui.editors
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.editors;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Set;


class ModelStoreMap extends Observable implements Map<String, String> {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	private HashMap<String, String> map = new HashMap<String, String>();

	public ModelStoreMap() {
		super();
	}

	private void notifyForAllKeys(Set<String> keys) {
		for (String k : keys) {
			notifyObservers(k);
		}
	}

	private Set<String> createCopyOfKeySet(Map<? extends String, ? extends String> m) {
		Set<String> result = new HashSet<String>();
		result.addAll(m.keySet());
		return result;
	}

	@Override
	public void clear() {
		Set<String> keysCopy = createCopyOfKeySet(this.map);
		this.setChanged();
		this.map.clear();
		notifyForAllKeys(keysCopy);
		this.clearChanged();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.map.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return this.map.entrySet();
	}

	@Override
	public String get(Object key) {
		String s = this.map.get(key);
		return s;
	}

	@Override
	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return this.map.keySet();
	}

	@Override
	public String put(String key, String value) {
		String o = this.map.put(key, value);
		setChanged();
		notifyObservers(key);
		clearChanged();
		return o;
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> map) {
		Set<String> copyOfKeySet = createCopyOfKeySet(map);
		setChanged();
		this.map.putAll(map);
		notifyForAllKeys(copyOfKeySet);
		clearChanged();
	}

	@Override
	public String remove(Object key) {
		String o = this.map.remove(key);
		setChanged();
		notifyObservers(key);
		clearChanged();
		return o;
	}

	@Override
	public int size() {
		return this.map.size();
	}

	@Override
	public Collection<String> values() {
		return this.map.values();
	}

	public void put(String key, boolean value) {
		put(key, String.valueOf(value));
	}

	public boolean getBoolean(String key) {
		String s = get(key);
		if (s == null) {
			return false;
		}
		boolean result = Boolean.parseBoolean(s);
		return result;
	}

	public void put(String key, int value) {
		put(key, String.valueOf(value));
	}

	public int getInt(String key) {
		String s = get(key);
		if (s == null) {
			return Integer.MIN_VALUE;
		}
		int result = Integer.parseInt(s);
		return result;
	}

}
