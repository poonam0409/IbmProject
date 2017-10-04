//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.jobgen
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.jobgen;


import com.ibm.iis.sappack.gen.tools.jobgenerator.api.RapidGeneratorSAPSettings;


public class RapidGeneratorSettingsImpl implements RapidGeneratorSAPSettings {
	private boolean saveABAPPrograms;
	private boolean uploadABAPPrograms;
	private boolean useCTS;
	private String ctsPackage;
	private String ctsRequest;
	private String abapProgramPrefix;
	private String abapUploadConnection;
	private String idocLoadMessageType;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}

	public boolean isUploadABAPPrograms() {
		return uploadABAPPrograms;
	}

	public void setUploadABAPPrograms(boolean uploadABAPPrograms) {
		this.uploadABAPPrograms = uploadABAPPrograms;
	}

	public String getIDocLoadMessageType() {
		return idocLoadMessageType;
	}

	public void setIDocLoadMessageType(String idocLoadMessageType) {
		this.idocLoadMessageType = idocLoadMessageType;
	}

	public String getABAPUploadSAPConnection() {
		return abapUploadConnection;
	}

	public void setABAPUploadSAPConnection(String abapUploadConnection) {
		this.abapUploadConnection = abapUploadConnection;
	}

	public String getABAPProgramPrefix() {
		return abapProgramPrefix;
	}

	public void setABAPProgramPrefix(String abapProgramPrefix) {
		this.abapProgramPrefix = abapProgramPrefix;
	}

	public boolean isSaveABAPPrograms() {
		return saveABAPPrograms;
	}

	public void setSaveABAPPrograms(boolean saveABAPPrograms) {
		this.saveABAPPrograms = saveABAPPrograms;
	}

	public boolean isUseCTS() {
		return useCTS;
	}

	public void setUseCTS(boolean useCTS) {
		this.useCTS = useCTS;
	}

	public String getCTSPackage() {
		return ctsPackage;
	}

	public void setCTSPackage(String ctsPackage) {
		this.ctsPackage = ctsPackage;
	}

	public String getCTSRequest() {
		return ctsRequest;
	}

	public void setCTSRequest(String ctsRequest) {
		this.ctsRequest = ctsRequest;
	}

}
