package com.ibm.is.sappack.cw.app.services;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import javax.ws.rs.core.StreamingOutput;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.bdr.IPathElement;
import com.ibm.is.sappack.cw.app.data.bdr.ParentPathElement;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.UserRegistryObject;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTable;
import com.ibm.websphere.security.CustomRegistryException;
import com.ibm.websphere.security.EntryNotFoundException;
import com.ibm.websphere.webmsg.publisher.Publisher;
import com.ibm.websphere.webmsg.publisher.jndijms.JmsPublisherServlet;

public class Util {
	
	private static final String CLASS_NAME = Util.class.getName();
	
	private static final String OBJECT_TYPE_USER = "user";
	private static final String OBJECT_TYPE_GROUP = "group";

	public static String generateRdmUrl(String rdmHost, String rdmPort) {
		return Constants.RDM_URL_PREFIX + rdmHost + ":" + rdmPort;
	}

	public static String generateRdmTypeName(ReferenceTable table) {
		String typeName = null;
		switch (table.getTableType()) {
		case CHECK_TABLE:
			typeName = table.getName();
			break;
		case DOMAIN_TABLE:
			typeName = Constants.DOMAIN_TABLE_RDM_TYPE;
			break;
		}
		return typeName;
	}
	
	public static String generateTargetRdmSetName(ReferenceTable table) {
		return table.getLegacyId() + "_" + table.getName();
	}

	public static String generateSourceRdmSetName(ReferenceTable table, String legacyId) {
		return legacyId + "_" + table.getName();
	}

	// Common method used in several REST service classes
	// TODO: No idea why this is better than just returning a string
	public static StreamingOutput output(final String output) {
		return new StreamingOutput() {
			@Override
        public void write(OutputStream outputStream) throws IOException {
				PrintStream printer = new PrintStream(outputStream, true, "UTF-8");
				printer.print(output);
			}
		};
	}
	
	// Logs an exception during batch processing (no exception is being sent to the client)
	public static void handleBatchException(Exception e) {
		if (e instanceof CwAppException) {
			Logger logger = CwApp.getLogger();
			logger.severe("An internal error has occurred.");
			logger.severe("Status: " + ((CwAppException) e).getResponse().getStatus());
			if (((CwAppException) e).getResponse() != null && ((CwAppException) e).getResponse().getEntity() != null) {
				logger.severe("Message: " + ((CwAppException) e).getResponse().getEntity().toString());
			}
		}
		e.printStackTrace();
	}
	
	// Logs a JPA exception and tries to rollback the transaction during batch processing (no exception is being sent to the client)
	public static void handleBatchException(UserTransaction transaction, Exception e) {
		handleBatchException(e);
		rollbackJpaTransaction(transaction);
	}
	
	// Logs an SQL exception, tries to rollback the transaction during batch processing (no exception is being sent to the client)
	public static void handleBatchException(Connection connection, Exception e) {
		handleBatchException(e);
		rollbackSqlConnection(connection);
	}
	
	// Logs an exception and sends a replacement one to the client, stopping further processing.
	public static void throwInternalErrorToClient(Exception e) {
		e.printStackTrace();
		throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR);
	}
	
	// Logs a JPA exception, tries to rollback the transaction and sends a replacement exception to the client, stopping further processing.
	public static void throwInternalErrorToClient(UserTransaction transaction, Exception e) {
		e.printStackTrace();
		rollbackJpaTransaction(transaction);
		throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR);
	}
	
	// Logs an SQL exception, tries to rollback the transaction and sends a replacement exception to the client, stopping further processing.
	public static void throwInternalErrorToClient(Connection connection, Exception e) {
		e.printStackTrace();
		rollbackSqlConnection(connection);
		throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR);
	}
	
	private static void rollbackJpaTransaction(UserTransaction transaction) {
		try {
			if (transaction != null && transaction.getStatus() == Status.STATUS_ACTIVE) {
				transaction.rollback();
			}
		}
		catch (Exception rollbackException) {
			rollbackException.printStackTrace();
		}
	}
	
	private static void rollbackSqlConnection(Connection connection) {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.rollback();
			}
		}
		catch (SQLException rollbackException) {
			rollbackException.printStackTrace();
		}
	}
	
	// Logs an attempt to retrieve a non-existant item and throws an exception with the "not found" status" to the client.
	public static void throwNotFoundErrorToClient(int itemId) {
		Logger logger = CwApp.getLogger();
		logger.severe("Item not found: " + itemId);
		// TODO: this currently results in a 500 error status on the client.
		throw new CwAppException(HttpURLConnection.HTTP_NOT_FOUND);
	}
	
	// Gets the JMS publisher from the servlet context.
	public static Publisher getJmsPublisher(ServletContext servletContext) {
		Publisher publisher = (Publisher)servletContext.getAttribute(JmsPublisherServlet.PUBLISHER_SERVLET_CONTEXT_KEY);
		if (publisher == null) {
			Logger logger = CwApp.getLogger();
			logger.severe("There was an error initializing the JMS framework. Check the web server configuration.");
			throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR);
		}
		return publisher;
	}	
	
	// generic function to close an arbitrary number of jdbc objects
	// like statement, result set, connection
	public static void closeDBObjects(Wrapper ... dbObjects) {
		Logger logger = CwApp.getLogger();
		SQLException closeException = null;

		for (int i = 0; i < dbObjects.length; i++) {
			Wrapper dbObject = dbObjects[i];
			
			try {
				if (dbObject == null) {
					logger.finer("Skipping NULL object, this is probably OK");
					continue;
				}
				logger.finer("Closing DB object, type: " + dbObject.getClass().getName());
				if (dbObject instanceof ResultSet) {
					((ResultSet) dbObject).close();
				} else if (dbObject instanceof Statement) {
					((Statement) dbObject).close();
				} else if (dbObject instanceof Connection) {
					((Connection) dbObject).close();
				} else {
					logger.severe("An internal error has occurred: Attempted to close an unknown type of DB object: " + dbObject.getClass().getName());
					throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
				// Only forward the initial exception to client
				if (closeException == null) {
					closeException = e;
				}
			}
		}

		if (closeException != null) {
			throwInternalErrorToClient(closeException);
		}
	}
	
	public static List<String> getGroupsFromWASForUser(String userName) {
		List<String> groups = null;

		try {
			groups = UserRegistryFactory.getUserRegistry().getGroupsForUser(userName);
		} catch (EntryNotFoundException e) {
			Util.throwInternalErrorToClient(e);
		} catch (CustomRegistryException e) {
			Util.throwInternalErrorToClient(e);
		} catch (RemoteException e) {
			Util.throwInternalErrorToClient(e);
		}
		
		if (groups == null) {
			groups = new ArrayList<String>();
		}
		return groups;
	}
   
	public static UserRegistryObject getUserFromWAS(String userName) {
		UserRegistryObject uro = new UserRegistryObject();
		
		try {

			// retrieve user id from the WAS user registry
			String userId = UserRegistryFactory.getUserRegistry().getUniqueUserId(userName);
			uro.setSecurityName(userName);
			uro.setType(OBJECT_TYPE_USER);
			uro.setUniqueId(userId);
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(e);
		}
		
		return uro;
	}
	
	public static UserRegistryObject getGroupFromWAS(String groupName) {
		UserRegistryObject uro = new UserRegistryObject();
		
		try {

			// retrieve user id from the WAS user registry
			String userId = UserRegistryFactory.getUserRegistry().getUniqueGroupId(groupName);
			uro.setSecurityName(groupName);
			uro.setType(OBJECT_TYPE_GROUP);
			uro.setUniqueId(userId);
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(e);
		}
		
		return uro;
	}
	
	// What does this do???
	public static void iterateParentProcess(Process process, IPathElement child, List<IPathElement> paths, boolean retainProcessObject) {
		Logger logger = CwApp.getLogger();
		final String METHOD_NAME = "iterateParentProcess(Process process, IPathElement child, List<IPathElement> paths, boolean retainProcessObject)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		logger.fine("Process: " + process.getName());
		
		ParentPathElement processElem = new ParentPathElement();
		processElem.setId(process.getProcessId());
		processElem.setName(process.getName());
		processElem.setType(Resources.BPH_TYPE_PROCESS);
		
		processElem.addChild(child);
		
		if (retainProcessObject) {
			processElem.setProcess(process);
		}
		
		Process parentProcess = process.getParentProcess();
		if (parentProcess != null) {
			if (retainProcessObject) {
				boolean exists = false;
				for (Process childProcess : parentProcess.getAclChildProcesses()) {
					if (childProcess.getProcessId().equals(process.getProcessId())) {
						exists = true;
					}
				}
				if (!exists) {
					// Add the current process to the AclChild list of the parent
					parentProcess.addAclChildProcess(process);
				}
			}
			
			iterateParentProcess(parentProcess, processElem, paths, retainProcessObject);
		} else {
			// Root process
			boolean merged = false;
			for (IPathElement mainPathRootElem : paths) {
				if (mergePathElements(mainPathRootElem, processElem, null, retainProcessObject)) {
					merged = true;
				}
			}
			if (!merged) {
				paths.add(processElem.clone());
			}
		}
		
		// Logging
		for (IPathElement path : paths) {
			printPathElement(path);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
    }
	
	public static void printPathElement(IPathElement path) {
		Logger logger = CwApp.getLogger();
		logger.finer("PathElement: " + path.getName());
		if (path instanceof ParentPathElement) {
			for (IPathElement child : ((ParentPathElement)path).getChildren()) {
				printPathElement(child);
			}
		}
	}

	// What does this do???
	public static boolean mergePathElements(IPathElement mainPathElem, IPathElement processElem, ListIterator<IPathElement> mainPathParentElemIt, boolean retainProcessObject) {
		Logger logger = CwApp.getLogger();
		final String METHOD_NAME = "mergePathElements(IPathElement mainPathElem, IPathElement processElem, ListIterator<IPathElement> mainPathParentElemIt, boolean retainProcessObject)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		boolean merged = false;
		
		if (mainPathElem.sameAs(processElem)) {
			if (mainPathElem instanceof ParentPathElement && processElem instanceof ParentPathElement) {
				List<IPathElement> mainPathChildElems = ((ParentPathElement) mainPathElem).getChildren(); 
				List<IPathElement> rootProcessChildElems = ((ParentPathElement) processElem).getChildren();
				
				ListIterator<IPathElement> mainPathChildElemsIt = mainPathChildElems.listIterator();
				Iterator<IPathElement> rootProcessChildElemsIt = rootProcessChildElems.iterator();
				
				while (mainPathChildElemsIt.hasNext()) {
					IPathElement mainPathChildElem = mainPathChildElemsIt.next();

					while (rootProcessChildElemsIt.hasNext()) {
						IPathElement rootProcessChildElem = rootProcessChildElemsIt.next();
						
						merged = mergePathElements(mainPathChildElem, rootProcessChildElem, mainPathChildElemsIt, retainProcessObject);
					}
				}
			}
		}
		else {
			if (mainPathParentElemIt != null) {
				mainPathParentElemIt.add(processElem);
				merged = true;
			}
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return merged;
    }

	// The paths object can contain several paths more than just once,
	// that's why merging will be done here.
	public static void filterPaths(List<IPathElement> paths) {
		Logger logger = CwApp.getLogger();
		final String METHOD_NAME = "filterPaths(List<IPathElement> paths)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		if(paths != null) {
			Iterator<IPathElement> iterator = paths.iterator();
			while(iterator.hasNext()) {
				ParentPathElement parentPathElement = (ParentPathElement)iterator.next();
				mergePath(parentPathElement);
			}
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	public static void mergePath(ParentPathElement path) {
		Logger logger = CwApp.getLogger();
		final String METHOD_NAME = "mergePath(ParentPathElement path)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// An array which will contain the positions of items to be deleted
		ArrayList<Integer> deletePositions = new ArrayList<Integer>();
		List<IPathElement> children = path.getChildren();
		// Compare each sibling with the others to find duplicates
		for(int i = 0; i < (children.size() - 1); i++) {
			ParentPathElement elem = (ParentPathElement)children.get(i);
			for(int j = (i + 1); j < children.size(); j++) {
				ParentPathElement elemToCompareWith = (ParentPathElement)children.get(j);
				if(elem.getName().equals(elemToCompareWith.getName())) {
					// Elements with same name found. Merge this paths.
					Iterator<IPathElement> childrenToAddIt = elemToCompareWith.getChildren().iterator();
					while(childrenToAddIt.hasNext()) {
						// Add every child from elemToCompareWith to elem, so no paths are lost.
						IPathElement childToAdd = childrenToAddIt.next();
						elem.addChild(childToAdd);
					}
					// Remember which elements have to be deleted. The erase itself
					// will be done later to not touch the consistency of the loop.
					if(!deletePositions.contains(j)) {
						deletePositions.add(j);
					}
				}
			}
		}
		
		// First sort array in descending order
		Collections.sort(deletePositions, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return (o1 > o2 ? -1 : (o1.equals(o2) ? 0 : 1));
			}
		});
		
		// Deleting duplicates beginning with higher array positions
		// (this is needed because the erase process is done by the array position
		// and not by the object itself)
		Iterator<Integer> deletePosIt = deletePositions.iterator();
		while(deletePosIt.hasNext()) {
			Integer pos = deletePosIt.next();
			children.remove((int)pos);
		}
		
		// Merging done. Continue with recursion for sub levels.
		Iterator<IPathElement> iterator = children.iterator();
		while(iterator.hasNext()) {
			IPathElement pathElem = iterator.next();
			// Only do this for ParentPathElements that are basically Processes
			if(pathElem instanceof ParentPathElement) {
				if(((ParentPathElement)pathElem).getChildren() != null) {
					mergePath((ParentPathElement)pathElem);
				}
			}
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
}
