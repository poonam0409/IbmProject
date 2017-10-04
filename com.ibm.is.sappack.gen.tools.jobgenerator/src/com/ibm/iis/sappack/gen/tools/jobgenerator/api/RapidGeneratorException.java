package com.ibm.iis.sappack.gen.tools.jobgenerator.api;

public class RapidGeneratorException extends Exception {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	private static final long serialVersionUID = -292038297416535798L;

	public RapidGeneratorException() {
		super();
	}

	public RapidGeneratorException(String message, Throwable cause) {
		super(message, cause);
	}

	public RapidGeneratorException(String message) {
		super(message);
	}

	public RapidGeneratorException(Throwable cause) {
		super(cause);
	}

}
