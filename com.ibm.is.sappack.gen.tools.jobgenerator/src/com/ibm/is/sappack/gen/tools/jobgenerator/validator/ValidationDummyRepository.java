//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.validator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator.validator;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.sap.conn.jco.JCoClassMetaData;
import com.sap.conn.jco.JCoCustomRepository;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoListMetaData;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoRequest;
import com.sap.conn.jco.JCoRuntimeException;
import com.sap.conn.jco.monitor.JCoRepositoryMonitor;

/**
 * ValidationDummyRepository
 * 
 * This is just a dummy JCoCustomRepository that is used for validation purposes.
 * It does not implement any JCoRepository functionality
 */
public class ValidationDummyRepository implements JCoCustomRepository{

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.validator.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	@Override
	public void addFunctionTemplateToCache(JCoFunctionTemplate jcofunctiontemplate) {
		// nothing needs to be done here
		
	}

	@Override
	public void addRecordMetaDataToCache(JCoRecordMetaData jcorecordmetadata) {
		// nothing needs to be done here
		
	}

	@Override
	public void setDestination(JCoDestination jcodestination) throws JCoException, JCoRuntimeException {
		// nothing needs to be done here
		
	}

	@Override
	public void setQueryMode(QueryMode querymode) throws JCoRuntimeException {
		// nothing needs to be done here
		
	}

	@Override
	public void clear() {
		// nothing needs to be done here
		
	}

	@Override
	public String[] getCachedFunctionTemplateNames() {
		// nothing needs to be done here
		return null;
	}

	@Override
	public String[] getCachedRecordMetaDataNames() {
		// nothing needs to be done here
		return null;
	}

	@Override
	public JCoFunction getFunction(String s) throws JCoException {
		// nothing needs to be done here
		return null;
	}

	@Override
	public JCoListMetaData getFunctionInterface(String s) throws JCoException {
		// nothing needs to be done here
		return null;
	}

	@Override
	public JCoFunctionTemplate getFunctionTemplate(String s) throws JCoException {
		// nothing needs to be done here
		return null;
	}

	@Override
	public JCoRepositoryMonitor getMonitor() {
		// nothing needs to be done here
		return null;
	}

	@Override
	public String getName() {
		// nothing needs to be done here
		return null;
	}

	@Override
	public JCoRecordMetaData getRecordMetaData(String s) throws JCoException {
		// nothing needs to be done here
		return null;
	}

	@Override
	public JCoRequest getRequest(String s) throws JCoException {
		// nothing needs to be done here
		return null;
	}

	@Override
	public JCoRecordMetaData getStructureDefinition(String s) throws JCoException {
		// nothing needs to be done here
		return null;
	}

	@Override
	public boolean isUnicode() {
		// nothing needs to be done here
		return false;
	}

	@Override
	public void removeFunctionTemplateFromCache(String s) {
		// nothing needs to be done here
		
	}

	@Override
	public void removeRecordMetaDataFromCache(String s) {
		// nothing needs to be done here
		
	}

	@Override
	public String[] getCachedClassMetaDataNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JCoClassMetaData getClassMetaData(String className)
			throws JCoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeClassMetaDataFromCache(String className) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(Reader arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(Writer arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addClassMetaDataToCache(JCoClassMetaData arg0) {
		// TODO Auto-generated method stub
		
	}

}
