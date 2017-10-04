package com.ibm.iis.sappack.gen.tools.jobgenerator.api;

import com.ibm.iis.sappack.gen.tools.jobgenerator.jobgen.RapidGeneratorImpl;
import com.ibm.iis.sappack.gen.tools.jobgenerator.jobgen.RapidGeneratorSettingsImpl;

public class RapidGeneratorFactory {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public static RapidGenerator createRapidGenerator() {
		return new RapidGeneratorImpl();
	}
	
	public static RapidGeneratorSAPSettings createRapidGeneratorSettings() {
		return new RapidGeneratorSettingsImpl();
	}
}
