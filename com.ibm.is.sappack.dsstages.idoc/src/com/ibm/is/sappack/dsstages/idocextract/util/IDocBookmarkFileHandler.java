//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.idocextract.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocextract.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.CCFResource;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.Utilities;
import com.ibm.is.sappack.dsstages.idocextract.jcconnector.IDocExtractAdapter;

/**
 * IDocBookmarkFileHandler class which wraps the call to the underlying IDocBookmark class in convenience functions.
 * 
 * @see com.ibm.is.sappack.dsstages.idocextract.util.IDocBookmark
 */
public class IDocBookmarkFileHandler {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocextract.util.Copyright.IBM_COPYRIGHT_SHORT;
	}

	static private String CLASSNAME = IDocExtractAdapter.class.getName();
	private Logger logger = null;
	
	private IDocBookmark bookmark = null;
	private File[] idocFilesToProcess = null;
	private int nextIdocToProcess = -1;
	
	/**
	 * Default constructor
	 */
	public IDocBookmarkFileHandler(String jobName, String idocType, String idocExtractDir) {
		this.logger = StageLogger.getLogger();
		final String METHODNAME = "<init>"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);

		this.bookmark = new IDocBookmark();
		this.bookmark.initialize(jobName, idocType, idocExtractDir);
		
		this.logger.log(Level.FINE, "Extract IDOC files from directory {0}, bookmark file used: {1}", //$NON-NLS-1$
				new Object[] {idocExtractDir, this.bookmark.getFileName()}); 

		this.idocFilesToProcess = getListOfIDocs(idocExtractDir, idocType);

		if (this.idocFilesToProcess.length == 0){
			this.logger.log(Level.INFO, "No IDoc files that need processing where found in directory {0}," + //$NON-NLS-1$
					" bookmark file used: {1}", new Object[] {idocExtractDir, this.bookmark.getFileName()});  //$NON-NLS-1$
		}
		
		this.logger.exiting(CLASSNAME, METHODNAME);
	}
	
	/**
	 * get a List of IDocs files that has to be
	 * processed by the worker specified by the
	 * given node number
	 * 
	 * @param currentNodeNumber
	 * @param numberOfNodes
	 * @return
	 */
	public List<File> getIDocFilesForNode(int currentNodeNumber, int numberOfNodes) {
		
		final String METHODNAME = "getIDocFilesForNode(int currentNodeNumber, int numberOfNodes)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME, new Object[]{currentNodeNumber, numberOfNodes});
		
		List<File> idocFilesForThisNode = new ArrayList<File>();
		
		while (this.hasNextIdocFile(currentNodeNumber, numberOfNodes)){
			File idocFile = this.getNextIdocFile(currentNodeNumber, numberOfNodes);
			if (idocFile != null){
				/* add this IDoc file to the list of IDocFiles that have to be 
				 * processed by the worker node with node number $currentNodeNumber
				 */
				idocFilesForThisNode.add(idocFile);
			} else {
				// should never happen
				this.logger.log(Level.SEVERE, "CC_IDOC_IncorrectProgramLogic"); //$NON-NLS-1$
				throw new RuntimeException(CCFResource.getCCFMessage("CC_IDOC_IncorrectProgramLogic")); //$NON-NLS-1$
			}
		}

		this.logger.log(Level.INFO, "CC_IDOC_EXTRACT_NumberOfFiles", idocFilesForThisNode.size()); 
		
		this.logger.exiting(CLASSNAME, METHODNAME);
		return idocFilesForThisNode;
	}
	
	
	
	/**
	 * Checks if there is another IDoc file to be processed by the current node. 
	 * @param currentNodeNumber number of this worker node
	 * @param numberOfNodes overall number of worker nodes
	 * @return <code>true</code> if this node has another the initial entry was created 
	 * successfully or an entry already exists, <code>false</code> if writing the initial entry failed
	 */
	private boolean hasNextIdocFile(int currentNodeNumber, int numberOfNodes){
		final String METHODNAME = "getNextIDOCFile(int currentNodeNumber, int numberOfNodes)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME, new Object[]{currentNodeNumber, numberOfNodes});

		int i;
		boolean hasNext = false;

		if (numberOfNodes > 0 && this.idocFilesToProcess != null) {
			if (nextIdocToProcess == -1) {
				// initialize nextIDocToProcess with the number of the current worker node
				// when it extracts its first IDoc file 
				i = currentNodeNumber;
			}else{ 
				// nextIdocToProcess indicates the index of the next IDoc file in array
				// that must be processed by this worker node
				i = nextIdocToProcess;
			}
			return(i < idocFilesToProcess.length);				
		}

		this.logger.exiting(CLASSNAME, METHODNAME, hasNext);
		return hasNext;
	}
	
	/**
	 * Returns the next IDoc files that should be processed by this worker node.
	 * A simple mechanism is used to assign IDoc files to worker nodes. 
	 *    <index of IDoc File> MODULO <number of current worker node>
	 * As a result every ith IDoc file is processed by worker node i    
	 * 
	 * @param currentNodeNumber number of this worker node
	 * @param numberOfNodes overall number of worker nodes
	 * @return the next IDoc files that should be processed by this worker node
	 */
	private File getNextIdocFile(int currentNodeNumber, int numberOfNodes){
		final String METHODNAME = "getNextIDOCFile(int currentNodeNumber, int numberOfNodes)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME, new Object[]{currentNodeNumber, numberOfNodes});

		File fileToReturn = null;
		
		if (idocFilesToProcess != null){
			if (numberOfNodes > 0) {
				if (this.nextIdocToProcess == -1) 
					this.nextIdocToProcess = currentNodeNumber;
				
				if ( this.nextIdocToProcess < idocFilesToProcess.length){
					fileToReturn = this.idocFilesToProcess[this.nextIdocToProcess];
					this.nextIdocToProcess += numberOfNodes;
				}
			}
		}
		
		this.logger.exiting(CLASSNAME, METHODNAME, fileToReturn);
		return fileToReturn;
	}
	
	
	/**
	 * Extract a list of IDoc files that should be processed in this job run according to the 
	 * to the bookmark file. 
	 * @param idocPathname path where to find the IDoc files
	 * @param idoctype name of the IDoc processed by this job
	 * @return list of IDoc files that should be processed
	 */
	private File[] getListOfIDocs(String idocPathname, String idoctype) {
		final String METHODNAME = "getListOfIDocs"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME, new Object[]{idocPathname, idoctype});

		File[] fileList = null;

		if (this.bookmark.isInitialized()) {

			this.logger.log(Level.FINEST, "IDOC files to be processed: "); //$NON-NLS-1$

			// retrieve the bookmark for the last successful export run
			Long lastTimestamp = this.bookmark.getPrevious();
			if (lastTimestamp != null) {
				Long currentTimestamp = null;

				// retrieve previous timestamp
				currentTimestamp = this.bookmark.getCurrent();

				if (currentTimestamp != null) {
					File idocDir = new File(idocPathname);

					if (idocDir.exists() && idocDir.isDirectory()) {
						String filter = Utilities.idocType2FileName(idoctype);
						this.logger.log(Level.FINE, "IDoc type name: {0}, file filter: {1}", new Object[]{idoctype, filter}); //$NON-NLS-1$
						FileFilter idocFileFilter = new FileFilter(filter, lastTimestamp, currentTimestamp);
						fileList = idocDir.listFiles(idocFileFilter);
						this.logger.log(Level.FINE, "Number of files in directory : {0}", fileList.length); //$NON-NLS-1$
						
					}else
						this.logger.log(Level.FINE, "IDoc files directory is invalid."); //$NON-NLS-1$
				}else
					this.logger.log(Level.FINE, "Retrieving timestamp from bookmark file failed"); //$NON-NLS-1$
			}
		}

		if (fileList != null && fileList.length != 0) {
			java.util.Arrays.sort(fileList);
		}else
			  this.logger.log(Level.FINE, "No IDoc files in IDoc files directory."); //$NON-NLS-1$

		this.logger.exiting(CLASSNAME, METHODNAME, fileList);
		return fileList;
	}

	class FileFilter implements java.io.FileFilter {

		String idoctype = ""; //$NON-NLS-1$
		Long lastTimestamp;
		Long currentTimestamp;
		
		public FileFilter(String idoctype, Long lastTimestamp, Long currentTimestamp) {
			this.idoctype = idoctype;
			this.lastTimestamp = lastTimestamp;
			this.currentTimestamp = currentTimestamp;
			
		}

		/* (non-Javadoc)
       * @see java.io.FileFilter#accept(java.io.File)
       */

		@Override
      public boolean accept(File pathname) {
      	if (pathname.getName().indexOf(this.idoctype) > -1 
					&& pathname.getName().endsWith(".txt")) { //$NON-NLS-1$

				long mtime = pathname.lastModified();
				if (mtime > this.lastTimestamp.longValue() && mtime <= this.currentTimestamp.longValue()) {
					return true;
				}

      	}
			return false;
      }
		

	}
	
	
}
