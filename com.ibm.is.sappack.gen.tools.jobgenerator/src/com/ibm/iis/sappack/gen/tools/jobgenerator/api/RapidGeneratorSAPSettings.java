package com.ibm.iis.sappack.gen.tools.jobgenerator.api;

public interface RapidGeneratorSAPSettings {

	void setABAPProgramPrefix(String prefix);

	void setSaveABAPPrograms(boolean saveABAPPrograms);

	void setUploadABAPPrograms(boolean uploadABAPPrograms);

	void setABAPUploadSAPConnection(String abapUploadConnection);

	void setUseCTS(boolean useCTS);

	void setCTSPackage(String ctsPackage);

	void setCTSRequest(String ctsRequest);

	void setIDocLoadMessageType(String idocMessageType);
}
