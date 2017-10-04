package com.ibm.is.sappack.cw.app.services;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.UserTransaction;

import com.ibm.is.sappack.cw.app.data.JPAResourceFactory;
import com.ibm.websphere.webmsg.publisher.Publisher;

public abstract class AbstractCancelableThread extends Thread {

	protected final Logger logger;
	protected boolean cancelled = false;
	protected final HttpSession session;
	protected final String sessionId;
	protected final Publisher publisher;
	protected final EntityManager manager;
	protected final UserTransaction jpaTransaction;
	
	public AbstractCancelableThread(HttpSession session, Publisher publisher) {
		this.logger = CwApp.getLogger();
		this.session = session;
		this.sessionId = session.getId();
		this.publisher = publisher;
		this.manager = JPAResourceFactory.getEntityManager();
		this.jpaTransaction = JPAResourceFactory.getUserTransaction();
	}
	
	public synchronized void cancel() {
		this.cancelled = true;
	}
}
