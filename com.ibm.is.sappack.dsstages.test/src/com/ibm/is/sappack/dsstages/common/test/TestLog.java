package com.ibm.is.sappack.dsstages.common.test;

import java.util.Date;

@SuppressWarnings("nls")
public class TestLog {

	public static void log(String s) {
		System.out.println((new Date()).toString() + ": " + s); 
	}
}
