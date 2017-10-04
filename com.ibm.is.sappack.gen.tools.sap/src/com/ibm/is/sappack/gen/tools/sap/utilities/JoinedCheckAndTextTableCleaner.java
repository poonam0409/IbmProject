//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.utilities
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.utilities;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.ibm.db.models.logical.Attribute;
import com.ibm.db.models.logical.Entity;
import com.ibm.db.models.logical.PrimaryKey;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;

public class JoinedCheckAndTextTableCleaner {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private LdmAccessor ldmAccessor;

	public JoinedCheckAndTextTableCleaner(LdmAccessor ldmAccessor) {
		this.ldmAccessor = ldmAccessor;
	}

	private class IDocAttribute {

		private Attribute attribute;

		private Entity relatedJoinedCheckAndTextTable = null;

		private String relatedJoinedCheckAndTextTableColumnName = null;

		public IDocAttribute(Attribute attribute) {
			this.attribute = attribute;
			String relatedJoinedCheckAndTextTableName = ldmAccessor.getAnnotationValue(this.attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_RELATED_CHECKTABLE);
			if (relatedJoinedCheckAndTextTableName != null && relatedJoinedCheckAndTextTableName.trim().length() > 0) {
            relatedJoinedCheckAndTextTableName = MessageFormat.format(com.ibm.is.sappack.gen.tools.sap.constants.Constants.CW_JLT_NAME_TEMPLATE,
                                                                      new Object[] { relatedJoinedCheckAndTextTableName.trim() });
			   
				this.relatedJoinedCheckAndTextTable = ldmAccessor.findEntity(relatedJoinedCheckAndTextTableName);
				if (this.relatedJoinedCheckAndTextTable == null) {
					String msg = Messages.JoinedCheckAndTextTableCleaner_0;
					msg = MessageFormat.format(msg, relatedJoinedCheckAndTextTableName, attribute.getEntity().getName(), attribute.getName());
					throw new RuntimeException(msg);
				}
				this.relatedJoinedCheckAndTextTableColumnName = ldmAccessor.getAnnotationValue(this.attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_RELATED_CHECKTABLE_COLUMN);
			}
		}

		public String getEntityName() {
			return this.attribute.getEntity().getName();
		}

		public void removeAnnotation() {
			ldmAccessor.deleteAnnotation(this.attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_RELATED_CHECKTABLE_COLUMN);
			ldmAccessor.deleteAnnotation(this.attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_RELATED_CHECKTABLE);
		}

		public boolean pointsToJoinedCheckAndTextTable() {
			return (this.relatedJoinedCheckAndTextTable != null);
		}

		public String getRelatedJoinedCheckAndTextTableColumnName() {
			return this.relatedJoinedCheckAndTextTableColumnName;
		}

		public String getRelatedJltName() {
			return this.relatedJoinedCheckAndTextTable.getName();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof IDocAttribute) {
				IDocAttribute other = (IDocAttribute) o;
				return this.attribute.getName().equals(other.attribute.getName());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return this.attribute.getName().hashCode();
		}
	}

	private class JoinedCheckAndTextTable {

		private Entity joinedCheckAndTextTable;
		private HashMap<String, IDocAttribute> attributesPointingToThisJlt;

		public JoinedCheckAndTextTable(Entity joinedCheckAndTextTable) {
			this.joinedCheckAndTextTable = joinedCheckAndTextTable;
			this.attributesPointingToThisJlt = new HashMap<String, IDocAttribute>();
		}

		public void addAttributePointingToThisJlt(IDocAttribute attribute) {
			if (attribute.relatedJoinedCheckAndTextTable == joinedCheckAndTextTable) {
				this.attributesPointingToThisJlt.put(attribute.getRelatedJoinedCheckAndTextTableColumnName(), attribute);
			}
		}

		public String getName() {
			return this.joinedCheckAndTextTable.getName();
		}

		// Checks, whether all Columns of the primary key are referenced
		public boolean isCheckTableFullyReferenced() {
			PrimaryKey primaryKey = this.joinedCheckAndTextTable.getPrimaryKey();
			if (primaryKey == null || primaryKey.getAttributes().isEmpty()) {
				return true;
			}

			List<Attribute> primaryKeyAttributes = primaryKey.getAttributes();
			Iterator<Attribute> primaryKeyAttributeIterator = primaryKeyAttributes.iterator();
			while (primaryKeyAttributeIterator.hasNext()) {
				Attribute primaraKeyAttribute = primaryKeyAttributeIterator.next();
				if (primaraKeyAttribute.getName().equalsIgnoreCase("MANDT")) { //$NON-NLS-1$
					continue;
				}

				if (!attributesPointingToThisJlt.containsKey(primaraKeyAttribute.getName())) {

					Iterator<IDocAttribute> idocAttributeIterator = attributesPointingToThisJlt.values().iterator();
					while (idocAttributeIterator.hasNext()) {
						IDocAttribute idocAttribute = idocAttributeIterator.next();
						idocAttribute.removeAnnotation();
					}

					return false;
				}
			}
			return true;
		}

		// public void cleansePrimaryKeyForTest() {
		// PrimaryKey primaryKey = this.joinedCheckAndTextTable.getPrimaryKey();
		// if (primaryKey == null || primaryKey.getAttributes().isEmpty()) {
		// return;
		// }
		//
		// List primaryKeyAttributes = primaryKey.getAttributes();
		// Iterator<Attribute> primaryKeyAttributeIterator =
		// primaryKeyAttributes.iterator();
		//
		// List<Attribute> primaryKeyAttributesToKeep = new
		// ArrayList<Attribute>();
		//
		// while (primaryKeyAttributeIterator.hasNext()) {
		// Attribute primaKeyAttribute = primaryKeyAttributeIterator.next();
		// if (primaKeyAttribute.getName().equalsIgnoreCase("MANDT")) {
		// primaryKeyAttributesToKeep.add(primaKeyAttribute);
		// continue;
		// }
		//
		// if
		// (attributesPointingToThisJlt.containsKey(primaKeyAttribute.getName()))
		// {
		// primaryKeyAttributesToKeep.add(primaKeyAttribute);
		// }
		// }
		//
		// this.joinedCheckAndTextTable.getPrimaryKey().getAttributes().clear();
		// this.joinedCheckAndTextTable.getPrimaryKey().getAttributes().addAll(primaryKeyAttributesToKeep);
		// }

		public void delete() {
			ldmAccessor.deleteEntity(joinedCheckAndTextTable);
//			System.out.println("JLT \"" + joinedCheckAndTextTable.getName() + " was deleted");
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof JoinedCheckAndTextTable) {
				JoinedCheckAndTextTable other = (JoinedCheckAndTextTable) o;
				return this.joinedCheckAndTextTable.getName().equals(other.joinedCheckAndTextTable.getName());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return this.joinedCheckAndTextTable.getName().hashCode();
		}

	}

	public void deleteUnrelatedJoinedCheckAndTextTables() {
		HashMap<String, JoinedCheckAndTextTable> allJlt = new HashMap<String, JoinedCheckAndTextTable>();
		HashSet<JoinedCheckAndTextTable> jltToKeep = new HashSet<JoinedCheckAndTextTable>();

		List entities = ldmAccessor.getAllEntities();
		if (entities == null || entities.isEmpty()) {
			return;
		}
//		System.out.println("Entities to be checked:" + entities.size());

		Iterator<Entity> entityIterator = entities.iterator();
		while (entityIterator.hasNext()) {
			Entity entity = entityIterator.next();

			Iterator<Attribute> attributeIterator = entity.getAttributes().iterator();
			while (attributeIterator.hasNext()) {
				Attribute attribute = attributeIterator.next();

				IDocAttribute idocField = new IDocAttribute(attribute);

				JoinedCheckAndTextTable joinedCheckAndTextTable = null;
				if (idocField.pointsToJoinedCheckAndTextTable()) {

					joinedCheckAndTextTable = allJlt.get(idocField.getRelatedJltName() + "|" + idocField.getEntityName()); //$NON-NLS-1$
					if (joinedCheckAndTextTable == null) {
						joinedCheckAndTextTable = new JoinedCheckAndTextTable(idocField.relatedJoinedCheckAndTextTable);
					}
					joinedCheckAndTextTable.addAttributePointingToThisJlt(idocField);
//					System.out.println(joinedCheckAndTextTable.getName() + "|" + idocField.getEntityName());
					allJlt.put(joinedCheckAndTextTable.getName() + "|" + idocField.getEntityName(), joinedCheckAndTextTable); //$NON-NLS-1$
				}
			}
		}

		Iterator<JoinedCheckAndTextTable> joinedCheckAndTextTableIterator = allJlt.values().iterator();
		while (joinedCheckAndTextTableIterator.hasNext()) {
			JoinedCheckAndTextTable joinedCheckAndTextTable = joinedCheckAndTextTableIterator.next();

			if (joinedCheckAndTextTable.isCheckTableFullyReferenced()) {
				jltToKeep.add(joinedCheckAndTextTable);

				// Only for Debugging
				Iterator<IDocAttribute> iterator = joinedCheckAndTextTable.attributesPointingToThisJlt.values().iterator();
				for (int i = 0; iterator.hasNext(); i++) {
					IDocAttribute attribute = iterator.next();
//					if (i == 0) {
//						System.out.println(attribute.getEntityName() + "->" + attribute.getRelatedJltName() + "(fully referenced)");
//					}
//					System.out.println("\t" + attribute.attribute.getName() + "->" + attribute.getRelatedJoinedCheckAndTextTableColumnName());

				}
			} else {

				// Only for Debugging!
				Iterator<IDocAttribute> iterator = joinedCheckAndTextTable.attributesPointingToThisJlt.values().iterator();
				for (int i = 0; iterator.hasNext(); i++) {
					IDocAttribute attribute = iterator.next();
//					if (i == 0) {
//						System.out.println(attribute.getEntityName() + "->" + attribute.getRelatedJltName() + "(NOT fully referenced)");
//					}
//					System.out.println("\t" + attribute.attribute.getName() + "->" + attribute.getRelatedJoinedCheckAndTextTableColumnName());

					if (i == joinedCheckAndTextTable.attributesPointingToThisJlt.values().size() - 1) {
						Iterator iterator2 = attribute.relatedJoinedCheckAndTextTable.getPrimaryKey().getAttributes().iterator();
						while (iterator2.hasNext()) {
							Attribute attribute2 = (Attribute) iterator2.next();
//							System.out.print(attribute2.getName() + " ");
						}
//						System.out.println(" ");
					}

				}

			}

			// For Testing
			// joinedCheckAndTextTable.cleansePrimaryKeyForTest();
			// jltToKeep.add(joinedCheckAndTextTable);

		}

//		System.out.println("JLT all: " + allJlt.size());
//		System.out.println("JLT to keep: " + jltToKeep.size());

		// Now we delete the JLT which are not fully referenced
		allJlt.values().removeAll(jltToKeep);
		joinedCheckAndTextTableIterator = allJlt.values().iterator();
		while (joinedCheckAndTextTableIterator.hasNext()) {
			JoinedCheckAndTextTable joinedCheckAndTextTableToBeDeleted = joinedCheckAndTextTableIterator.next();
			joinedCheckAndTextTableToBeDeleted.delete();
		}

		Iterator<JoinedCheckAndTextTable> iterator = jltToKeep.iterator();
		while (iterator.hasNext()) {
			JoinedCheckAndTextTable jlt = iterator.next();
//			System.out.println("JLT \"" + jlt.joinedCheckAndTextTable.getName() + "\" was kept");
		}

	}

	/**
	 * Iterates over all entities and all entities' columns. For each column we
	 * check whether the column points to a joined check and text table. If it
	 * does, we try to figure out which would be the correct column on the jlt
	 * to point to (unfortunately this information is not available in the SAP
	 * data dictionary as the IDOC -> table mapping, this is not defined in the
	 * data dictionary but is done in the ABAP code filling the IDoc) so what we
	 * do is trying to find the exact name of the column of making an educated
	 * guess based on 1.) The field and column names 2.) The name of the data
	 * element used on the field 3.) The domain types of the field and the jlt's
	 * columns
	 */
	public void setJoinedCheckAndTextTableColumnAnnotations() {

		List entities = ldmAccessor.getAllEntities();
		if (entities == null || entities.isEmpty()) {
			return;
		}
//		System.out.println("Number of Entities: " + entities.size());

		Iterator<Entity> entityIterator = entities.iterator();

		// Iterate over all entities to check whether a column of this table
		// points
		// to a JLT
		while (entityIterator.hasNext()) {
			Entity currentEntity = entityIterator.next();
			List attributes = currentEntity.getAttributes();
			if (attributes == null || attributes.isEmpty()) {
				continue;
			}

			Iterator<Attribute> attributeIterator = attributes.iterator();
			while (attributeIterator.hasNext()) {
				Attribute currentAttribute = attributeIterator.next();
				String relatedCheckTableName = ldmAccessor.getAnnotationValue(currentAttribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_RELATED_CHECKTABLE);

				// No checktable related
				if (relatedCheckTableName == null || relatedCheckTableName.trim().length() == 0) {
					continue;
				}
				relatedCheckTableName = MessageFormat.format(com.ibm.is.sappack.gen.tools.sap.constants.Constants.CW_JLT_NAME_TEMPLATE,
                                                         new Object[] { relatedCheckTableName.trim() });

				Entity relatedCheckTable = ldmAccessor.findEntity(relatedCheckTableName);
				if (relatedCheckTable == null) {
//					System.out.println("CHECKTABLE \"" + relatedCheckTableName + "\" NOT FOUND!");
					continue;
				}
				Attribute checktableAttribute = ldmAccessor.findAttribute(relatedCheckTable, currentAttribute.getName());
				if (checktableAttribute == null) {

					// try using ROLLNAME (==Name of the data element)
					String rollNameAnnotationValue = ldmAccessor.getAnnotationValue(currentAttribute, Constants.ANNOT_DATATYPE_DATA_ELEMENT);
					checktableAttribute = ldmAccessor.findAttribute(relatedCheckTable, rollNameAnnotationValue);

					if (checktableAttribute == null) {

						// compare the domains
						// if there is exactly one attribute with the given
						// domain, we use this attribute
						String attributeDomainName = ldmAccessor.getAnnotationValue(currentAttribute, Constants.ANNOT_DATATYPE_DOMAIN);
						List<Attribute> candidateAttributes = ldmAccessor.findAttributesByAnnotationValue(relatedCheckTable, Constants.ANNOT_DATATYPE_DOMAIN, attributeDomainName);
						if (candidateAttributes.size() == 1) {
							checktableAttribute = candidateAttributes.get(0);
						} else {

//							System.out.println("The attribute \"" + currentAttribute.getName() + "\" of table \"" + currentEntity.getName() + "\" points to the joined check and text table \"" + relatedCheckTableName
//									+ "\" but the system could not find a corresponding colum on the joined check and text table. When generating the load job, this will result in a job that has compile errors you will have to fix yourself.");
							continue;
						}
					}
				}
				// Create the annotation
				ldmAccessor.addAnnotation(currentAttribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_RELATED_CHECKTABLE_COLUMN, checktableAttribute.getName());
			}
		}
	}

}
