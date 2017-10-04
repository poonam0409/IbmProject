package com.ibm.is.sappack.cw.app.services;

import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.ibm.is.sappack.cw.app.data.JPAResourceFactory;

public abstract class ImportAbstractProvider {
	
	protected static Logger logger;
	protected static EntityManager manager;
	
	// Controls whether the provider will persist the objects it creates.
	// This is necessary for handling temporary, unpersisted objects
	// for import types that only import certain item types.
	protected boolean transientMode;
	
	protected ImportAbstractProvider() {
		logger = CwApp.getLogger();
		manager = JPAResourceFactory.getEntityManager();
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone not allowed.");
	}
	
	protected abstract void updateTransientModeEnabled();
	
}
