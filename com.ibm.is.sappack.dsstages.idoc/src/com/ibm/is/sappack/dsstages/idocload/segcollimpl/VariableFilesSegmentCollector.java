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
// Module Name : com.ibm.is.sappack.dsstages.idocload.segcollimpl
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload.segcollimpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.ibm.is.sappack.dsstages.common.DSEnvironment;
import com.ibm.is.sappack.dsstages.common.DSSAPConnection;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.idocload.SegmentValidationHandler;

public class VariableFilesSegmentCollector extends FileSegmentCollectorBase {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.segcollimpl.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private static final String CLASSNAME = VariableFilesSegmentCollector.class.getName();

	int maximumIDocsPerFile;
	Map<String, Entry> idocNum2EntryMap;
	int currentNodeNumber = 0;

	public VariableFilesSegmentCollector(DSEnvironment env, DSSAPConnection dsSAPConnection, IDocType idocType, SegmentValidationHandler handler, int maximumIDocsPerFile) throws IOException {
		super(env, dsSAPConnection, idocType, handler);
		final String METHODNAME = "<init>"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.idocNum2EntryMap = new HashMap<String, Entry>();
		this.currentNodeNumber = env.getCurrentNodeNumber();
		addNewEntry(this.currentNodeNumber);
		this.maximumIDocsPerFile = maximumIDocsPerFile;
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	protected Entry findEntry(String idocNumber) throws IOException {
		final String METHODNAME = "findEntry"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		Entry existingEntry = this.idocNum2EntryMap.get(idocNumber);
		if (existingEntry != null) {
			if (this.logger.isLoggable(Level.FINEST)) {
				this.logger.log(Level.FINEST, "Found entry {0} for IDoc number {1}", new Object[] { existingEntry.file.getAbsolutePath(), idocNumber }); //$NON-NLS-1$
			}
			return existingEntry;
		}
		// find first bucket that can still take idocs
		for (Entry e : this.entries) {
			if (e.numberOfIDocs < maximumIDocsPerFile) {
				this.idocNum2EntryMap.put(idocNumber, e);
				e.numberOfIDocs++;
				if (this.logger.isLoggable(Level.FINEST)) {
					this.logger.log(Level.FINEST, "Found entry {0} for IDoc number {1}", new Object[] { e.file.getAbsolutePath(), idocNumber }); //$NON-NLS-1$
				}
				return e;
			}
		}
		// all buckets are full, add new one
		Entry e = addNewEntry(this.currentNodeNumber);
		e.numberOfIDocs++;
		this.idocNum2EntryMap.put(idocNumber, e);
		if (this.logger.isLoggable(Level.FINEST)) {
			this.logger.log(Level.FINEST, "Adding new entry {0} for IDoc number {1}", new Object[] { e.file.getAbsolutePath(), idocNumber }); //$NON-NLS-1$
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
		return e;
	}

}
