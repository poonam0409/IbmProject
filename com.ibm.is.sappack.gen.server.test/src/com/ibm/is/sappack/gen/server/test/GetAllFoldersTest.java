package com.ibm.is.sappack.gen.server.test;

import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.request.GetAllFoldersRequest;
import com.ibm.is.sappack.gen.common.request.GetAllFoldersResponse;
import com.ibm.is.sappack.gen.common.request.ServerRequestUtil;

public class GetAllFoldersTest {

	/**
	 * @param args
	 * @throws JobGeneratorException 
	 */
	public static void main(String[] args) throws JobGeneratorException {
		GetAllFoldersRequest req = new GetAllFoldersRequest();
		req.setDomainServerName("localhost");
		req.setISUsername("isadmin");
		req.setISPassword("inf0server");
		req.setDomainServerPort(9080);
		req.setDSProjectName("TEST");
		
		GetAllFoldersResponse resp = (GetAllFoldersResponse) ServerRequestUtil.send(req);
		
		System.out.println(resp.getFolders());


	}

}
