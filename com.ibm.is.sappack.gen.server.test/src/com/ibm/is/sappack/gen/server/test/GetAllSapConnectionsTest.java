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
// Module Name : com.ibm.is.sappack.gen.common.request
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.test;


import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.request.GetAllSapConnectionsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllSapConnectionsResponse;
import com.ibm.is.sappack.gen.common.request.ServerRequestUtil;

public class GetAllSapConnectionsTest {

	/**
	 * @param args
	 * @throws JobGeneratorException
	 */
	public static void main(String[] args) throws JobGeneratorException {

		GetAllSapConnectionsRequest req = new GetAllSapConnectionsRequest();
		req.setDomainServerName("bl3aed5b");
		req.setISUsername("isadmin");
		req.setISPassword("inf0server");
		req.setDomainServerPort(9080);
		req.setDSProjectName("ANALYZERPROJECT");

		GetAllSapConnectionsResponse resp = (GetAllSapConnectionsResponse) ServerRequestUtil.send(req);

		Map connectionsMap = resp.getConnectionsMap();
		Set keySet = connectionsMap.keySet();
		Iterator it = keySet.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			System.out.println(key);
		}
      System.out.println("result contains errors = " + resp.get1stMessage()); 
      String msgArr[] = resp.getMessages();
      for(int idx = 0; idx < msgArr.length; idx ++)
      {
         System.out.println(" msg = " + msgArr[idx]);
      }
      if (resp.containsErrors())
      {
         System.out.println("details = " + resp.getDetailedInfo());
      }

	}

}
