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
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener.jobrunner
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener.jobrunner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.impl.DSSAPConnectionImpl;
import com.ibm.is.sappack.dsstages.idoc.listener.IDocServerImpl;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerMessages;

/**
 * IDocCounterImpl
 * 
 * Implementation of an IDocCounter to count
 * and persist the number of incoming IDocs
 * from SAP.
 * 
 * The number of IDocs is store in a file 
 * named '$IDOCTYPE.BATCH_COUNT'. 
 * For example DEBMAS06.BATCH_COUNT for the IDoc
 * type DEBMAS06.
 * 
 * DataStage jobs are triggered when the IDocCounter
 * has reached a specified number. When the DataStage jobs
 * have been executed, the IDocCounter is set back to zero.
 */
public class IDocCounterImpl implements IDocCounter {

	/* logger */
	private Logger logger = null;
	
	/* class name for logging purposes */
	static final String CLASSNAME = IDocServerImpl.class.getName();
	
	/* BATCH_COUNT file extension */
	private static final String BATCH_COUNT = ".BATCH_COUNT";
	
	/* the counter file */
	private File counterFile = null;
	
	/* IDoc type name */
	private String idocType = "";
	
	/* counter initial value */
	private int initialValue = 0;
	
	/**
	 * IDocCounterImpl
	 */
	public IDocCounterImpl() {
		/* initialize the logger */
		this.logger = StageLogger.getLogger();
	}
	
	
	@Override
	/**
	 * initialize the IDocCounter. 
	 * Create the file '$IDOCTYPE.BATCH_COUNT'
	 * within the IDocType folder and initialize
	 * the counter with zero if file does not 
	 * exist yet. If the file already exists, leave
	 * it as it is.
	 * 
	 * @param idocTypesDir
	 * @param idocType
	 * 
	 */
	public synchronized void init(String idocTypesDir, String idocType) {
		
		final String METHODNAME = "init()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		
		this.idocType = idocType;
		
		/* check if the $idocType.BATCH_COUNT file already exists */
		StringBuffer path = new StringBuffer();
		path.append(idocTypesDir).append(DSSAPConnectionImpl.convertIDocTypeNameToFileName(idocType)).append(BATCH_COUNT);
		String filePath = path.toString();
		this.counterFile = new File(filePath);
		if(!counterFile.exists()) {
			/* file does not exist yet - create it and initialize IDoc counter value with 0 */
			this.setCounterFileValue(this.initialValue);
		}
		
		this.logger.exiting(CLASSNAME, METHODNAME);
		
	}
	
	@Override
	/**
	 * increment
	 * 
	 * open the counter file and increment 
	 * the counter value with the given number
	 * 
	 * @param number
	 */
	public synchronized void increment(int number){

		final String METHODNAME = "increment()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		
		/* get the current counter value */
		int value = this.getCounterFileValue();
		
		/* update counter file value */
		value = value + number;
		this.setCounterFileValue(value);
	
		this.logger.exiting(CLASSNAME, METHODNAME);
		
	}

	@Override
	public synchronized void decrement(int number) {
		
		final String METHODNAME = "decrement()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		
		/* get current counter value */
		int value = this.getCounterFileValue();
		
		/* decrement counter value */
		value = value - number;
		
		/* value should not be negative */
		if(value < this.initialValue) {
			/* set new counter value */
			this.setCounterFileValue(this.initialValue);
		} else {
			/* set new counter value */
			this.setCounterFileValue(value);
		}
		
		this.logger.exiting(CLASSNAME, METHODNAME);
	}
	

	@Override
	public synchronized void reset(){

		final String METHODNAME = "reset()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		
		/* reset counter to initial value */
		this.setCounterFileValue(this.initialValue);
		
		this.logger.exiting(CLASSNAME, METHODNAME);
		
	}
	
	@Override
	/**
	 * getValue
	 * 
	 * read the counter value from the counter file
	 * 
	 * @return
	 */
	public int getValue() {
		
		final String METHODNAME = "getValue()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		
		/* get counter value */
		int value = this.getCounterFileValue();
		
		this.logger.log(Level.FINER, "IDoc counter for IDoc type {0} = {1}", new Object[]{this.idocType, value});
		this.logger.exiting(CLASSNAME, METHODNAME);
		
		return value;
	}

	/**
	 * setCounterFileValue
	 * 
	 * set the value of the counter file
	 * 
	 * @param value
	 */
	private void setCounterFileValue(int value) {
		
		final String METHODNAME = "setCounterFileValue(String value)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(this.counterFile));
			/* write a single line */
			writer.write(String.valueOf(value));
			this.logger.log(Level.INFO, IDocServerMessages.IDocCounterValue, new Object[]{this.idocType, value});
			writer.close();
			this.logger.exiting(CLASSNAME, METHODNAME);
		} catch (IOException e) {
			this.logger.log(Level.WARNING, IDocServerMessages.IDocCounterFileWriteError, this.counterFile.getAbsolutePath());
			this.logger.log(Level.WARNING, e.getMessage());
		} finally {
			if (writer != null) {
				try {
	            writer.close();
            }
            catch (IOException e) {
            	this.logger.log(Level.FINER, e.getMessage());
            }
			}
		}
	}
	
	/**
	 * getCounterFileValue
	 * 
	 * get the value of the counter file
	 * 
	 * @return
	 */
	private int getCounterFileValue() {
		final String METHODNAME = "getCounterFileValue()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(this.counterFile));
			/* read only the first line */
			String content = reader.readLine();
			this.logger.log(Level.FINER, "IDoc counter value for IDoc type {0} is {1}", new Object[]{content, this.idocType});
			reader.close();
			/* convert String to integer */
			int value = Integer.parseInt(content);
			this.logger.exiting(CLASSNAME, METHODNAME);
			return value;
		} catch (FileNotFoundException e) {
			this.logger.log(Level.WARNING, IDocServerMessages.IDocCounterFileNotFound, this.counterFile.getAbsolutePath());
			this.logger.log(Level.WARNING, e.getMessage());
			/* try to recreate the counter file */
			this.reset();
			/* return -1 if something goes wrong */
			return -1;
		} catch (IOException e) {
			this.logger.log(Level.WARNING, IDocServerMessages.IDocCounterFileReadError, this.counterFile.getAbsolutePath());
			this.logger.log(Level.WARNING, e.getMessage());
			/* try to recreate the counter file */
			this.reset();
			/* return -1 if something goes wrong */
			return -1;
		} catch (NumberFormatException e) {
			/* invalid counter file content - reset the counter */
			this.logger.log(Level.WARNING, "IDoc counter file {0} is corrupt. Resetting counter for IDoc type {1}", new Object[]{this.counterFile.getAbsolutePath(), this.idocType});
			/* recreate counter file */
			this.reset();
			this.logger.exiting(CLASSNAME, METHODNAME);
			/* return -1 if something goes wrong */
			return -1;
		} finally {
			if (reader != null) {
				try {
	            reader.close();
            }
            catch (IOException e) {
            	this.logger.log(Level.FINER, e.getMessage());
            }
			}
		}
	}
	
	
	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.Copyright.IBM_COPYRIGHT_SHORT;
	}


	
}
