//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.api
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.api;


/**
 * The Rapid Modeler API class.
 *
 */
public interface RapidModeler {

	/**
	 * Import SAP metadata into a logical data model.
	 * All paths are relative to the Eclipse workspace starting with a "/". For instance "/Project1/Configuration.rmcfg".
	 * 
	 * @param sapSystemName The name of the SAP system. 
	 * @param sapUser optional: the SAP user 
	 * @param sapPassword optional: the SAP password 
	 * @param sapObjectCollectionFile The file name of the SAP collection used for the import. 
	 * @param rapidModelerConfigurationFile The import configuration file name
	 * @param ldmFile The logical data model file used as the target for import. Will be created if it doesn't exist. Note that the project however, must exist.
	 * @param background Flag indicating if the metadata import should run in background. If true, an Eclipse background task will be created which can be monitored through the "Progress View" view.
	 * @param callback The progress callback for this task. May be null if not required.
	 * 
	 * @throws RapidModelerException
	 */
	public void importMetadata(//
			String sapSystemName, //
			String sapUser, //
			String sapPassword, //
			String sapObjectCollectionFile, //
			String rapidModelerConfigurationFile, //
			String ldmFile, //
			String checkTableLDMFile, //
			boolean background, //
			IProgressCallback callback) throws RapidModelerException;

}
