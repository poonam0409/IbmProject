package com.ibm.iis.sappack.gen.common.ui.util;

public class Pair<A, B> {
	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}
	
	private A first;
	private B second;
	
	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}
	
	public A getFirst() {
		return this.first;
	}
	
	public B getSecond() {
		return this.second;
	}

}
