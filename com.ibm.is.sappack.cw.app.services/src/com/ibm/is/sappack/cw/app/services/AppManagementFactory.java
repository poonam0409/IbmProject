package com.ibm.is.sappack.cw.app.services;

import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementProxy;

public class AppManagementFactory {

	private static AppManagement appMgmt = null;

	public static synchronized AppManagement getAppManagement() {
		try {
			if (appMgmt == null) {
				appMgmt = AppManagementProxy.getJMXProxyForServer();
			}
		} catch (Exception e) {
			Util.throwInternalErrorToClient(e);
		}
		return appMgmt;
	}
}
