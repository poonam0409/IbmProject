package com.ibm.is.sappack.cw.app.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.BusinessObject;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.ProcessStep;
import com.ibm.is.sappack.cw.app.services.bdr.BphImportType;
import com.ibm.is.sappack.cw.app.services.bdr.threads.BphAbstractImporterThread;

public class ImportBusinessObjectProvider extends ImportAbstractProvider {
	
	private static ImportBusinessObjectProvider instanceOfBusinessObjectProvider;
	private final String CLASS_NAME = ImportBusinessObjectProvider.class.getName();
	
	private ImportBusinessObjectProvider() {
		super();
	}
	
	public static synchronized ImportBusinessObjectProvider getInstance() {
		if(instanceOfBusinessObjectProvider == null) {
			instanceOfBusinessObjectProvider = new ImportBusinessObjectProvider();
		}
		return instanceOfBusinessObjectProvider;
	}

	/**
	 * 
	 * @param parent: the parent ProcessStep
	 * @param businessObjectName: name to search for
	 * @param businessObjectShortName: will be set in case of creation
	 * @param businessObjectDescription: will be set in case of creation
	 * @param creationDate: will be set in case of creation
	 * @return: Newly created business object
	 */
	public BusinessObject provideBusinessObject(ProcessStep parent, String businessObjectName, String businessObjectShortName,
			String businessObjectDescription, Date creationDate) {
		final String METHOD_NAME = "getBusinessObject(ProcessStep processStep, String businessObjectName, String businessObjectShortName, String businessObjectDescription, Date creationDate)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// Update transientModeEnabled
		updateTransientModeEnabled();
		
		BusinessObject businessObject = null;
		
		if(BphAbstractImporterThread.businessObjectMap.isEmpty()) {
			// Get existing business objects
			TypedQuery<BusinessObject> query = manager.createNamedQuery("BusinessObject.getAll", BusinessObject.class);
			List<BusinessObject> businessObjectList = query.getResultList();
			// Put business objects into HashMap. The name will be the key.
			for(BusinessObject bo : businessObjectList) {
				BphAbstractImporterThread.businessObjectMap.put(bo.getName(), bo);
			}
		}
		// Find if businessObject already exists
		businessObject = BphAbstractImporterThread.businessObjectMap.get(businessObjectName);

		if(businessObject == null) {
			// Creating new one
			businessObject = createBusinessObject(businessObjectName, businessObjectShortName, businessObjectDescription, creationDate);
			BphAbstractImporterThread.businessObjectMap.put(businessObjectName, businessObject);
		}
		
		// Only set relation if transientModeEnabled is false. Otherwise
		// the BO will know his parent process step which is not persisted (and shouldn't be).
		// But when persisting the BO, this will lead to an error, because JPA doesn't know
		// what to do with the unmanaged parent process step.
		if(!transientMode) {
			
			if (parent != null) {
				setRelations(parent, businessObject);
			}
			
			// Persist object
			manager.joinTransaction();
			manager.persist(businessObject);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return businessObject;
	}
	
	private void setRelations(ProcessStep parent, BusinessObject businessObject) {
		final String METHOD_NAME = "setRelations(ProcessStep parent, BusinessObject businessObject)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		if(!BphAbstractImporterThread.importType.getImportType().equals(BphImportType.CSV_BOS.getImportType())) {
			// Set child
			List<BusinessObject> children = parent.getUsedBusinessObjects();
			if(children == null) {
				children = new ArrayList<BusinessObject>();
				parent.setUsedBusinessObjects(children);
			}
			
			boolean isInList = false;
			for(BusinessObject child : children) {
				if(child.getName().equals(businessObject.getName())) {
					isInList = true;
					break;
				}
			}
			if(!isInList) {
				children.add(businessObject);
			}
			
			// Set parent
			List<ProcessStep> usedInProcessSteps = businessObject.getUsedInProcessSteps();
			if(usedInProcessSteps == null) {
				usedInProcessSteps = new ArrayList<ProcessStep>();
				businessObject.setUsedInProcessSteps(usedInProcessSteps);
			}
			isInList = false;
			for(ProcessStep processStep : usedInProcessSteps) {
				if(processStep.getName().equals(parent.getName())) {
					isInList = true;
					break;
				}
			}
			if(!isInList) {
				usedInProcessSteps.add(parent);
			}
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private BusinessObject createBusinessObject(String businessObjectName, String businessObjectShortName,
			String businessObjectDescription, Date creationDate) {
		// Creates an returns a business object without persisting
		final String METHOD_NAME = "createBusinessObject(String businessObjectName, String businessObjectShortName, String businessObjectDescription, Date creationDate)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// Prevent null values
		if(businessObjectName == null) {
			businessObjectName = "";
		}
		if(businessObjectShortName == null) {
			businessObjectShortName = "";
		}
		if(businessObjectDescription == null) {
			businessObjectDescription = "";
		}
		if(creationDate == null) {
			creationDate = new Date();
		}
		
		if(!transientMode) {
			// Update counterBusinessObjects from BphCsvImporterThread
			BphAbstractImporterThread.counterBusinessObjects++;
		}
		BusinessObject newBusinessObject = new BusinessObject();
		newBusinessObject.setName(businessObjectName);
		newBusinessObject.setShortName(businessObjectShortName);
		newBusinessObject.setDescription(businessObjectDescription);
		newBusinessObject.setUpdated(creationDate);
		newBusinessObject.setType(Resources.BPH_TYPE_BUSINESSOBJECT);
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return newBusinessObject;
	}

	@Override
	protected void updateTransientModeEnabled() {
		switch(BphAbstractImporterThread.importType) {
			case CSV_BOS:
			case CSV_COMPLETE:
				transientMode = false;
				break;
			case CSV_PROCESSES:
				transientMode = true;
				break;
		}
	}
	
}
