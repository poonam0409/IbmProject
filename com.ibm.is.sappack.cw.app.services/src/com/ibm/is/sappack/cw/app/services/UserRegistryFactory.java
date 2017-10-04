package com.ibm.is.sappack.cw.app.services;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.websphere.security.UserRegistry;

public class UserRegistryFactory {

	private static UserRegistry registry = null;
	
	public static synchronized UserRegistry getUserRegistry() {
		try {
			if (registry == null) {
				InitialContext context = new InitialContext();
				registry = (UserRegistry) context.lookup(Resources.USERREGISTRY_CONTEXT_ID);
			}
		} catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		return registry;
	}
}
