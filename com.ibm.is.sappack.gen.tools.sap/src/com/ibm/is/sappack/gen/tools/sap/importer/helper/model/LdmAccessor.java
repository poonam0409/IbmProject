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
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.helper.model
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.helper.model;


import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.datatools.modelbase.sql.schema.SQLObject;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EcoreFactory;

import com.ibm.datatools.core.DataToolsPlugin;
import com.ibm.db.models.logical.AlternateKey;
import com.ibm.db.models.logical.Attribute;
import com.ibm.db.models.logical.CardinalityType;
import com.ibm.db.models.logical.Entity;
import com.ibm.db.models.logical.ForeignKey;
import com.ibm.db.models.logical.Key;
import com.ibm.db.models.logical.LogicalDataModelFactory;
import com.ibm.db.models.logical.Package;
import com.ibm.db.models.logical.PrimaryKey;
import com.ibm.db.models.logical.Relationship;
import com.ibm.db.models.logical.RelationshipEnd;
import com.ibm.db.models.logical.RelationshipType;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.ida.IdaDataType;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.mapping.IDataTypeMapper;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.mapping.LogicalTableDataTypeMapper;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap.SapDataElement;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap.SapDataType;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.AbstractField;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TableField;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalField;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.summary.SummaryCollector;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Segment;
import com.ibm.is.sappack.gen.tools.sap.utilities.CardinalityMapper;
import com.ibm.is.sappack.gen.tools.sap.utilities.DefaultNameConverter;


public class LdmAccessor extends ModelAccessor {
	private static final String VERBNAME                    = "Verb";        //$NON-NLS-1$
	private static final String RELATIONSHIP_CHILD_POSTFIX  = "_child";      //$NON-NLS-1$
	private static final String RELATIONSHIP_PARENT_POSTFIX = "_parent";     //$NON-NLS-1$
	private static final String NAME_SEPERATOR              = com.ibm.is.sappack.gen.common.Constants.LDM_ENTITY_NAME_SEPARATOR;
	private static final String PRIMARY_KEY                 = "primary key"; //$NON-NLS-1$
   public  static final String FOREIGN_KEY                 = "foreign key"; //$NON-NLS-1$
   public  static final String PACKAGE_HIERARCHY_SEPARATOR = "\\";          //$NON-NLS-1$
   public  static final String SAP_SYSTEM_TABLE_1          = "SYST";        //$NON-NLS-1$
   public  static final String SAP_SYSTEM_TABLE_2          = "SY";          //$NON-NLS-1$
   public  static final String COLUMN_LIST_SEPARATOR       = ",";           //$NON-NLS-1$
   

	private SapSystem        sapSystem;
	private IDataTypeMapper  dataTypeMapper;
	private DdmAccessor      ddmAccessor;
	private long             runId;
   private boolean          isV7Mode;
	private SummaryCollector summaryCollector;
	private LdmNameConverter nameConverter;
	private IDataTypeMapper  tableDataTypeMapper = new LogicalTableDataTypeMapper();

	
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.helper.model.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public LdmAccessor(DdmAccessor ddmAccessor, SapSystem sapSystem, IDataTypeMapper dataTypeMapper,
	                   LdmNameConverter converter) throws IOException {
		super(ddmAccessor);

		this.summaryCollector = new SummaryCollector(this.getModelFile().getFullPath().toString());
		this.sapSystem        = sapSystem;
		this.dataTypeMapper   = dataTypeMapper;
		this.ddmAccessor      = ddmAccessor;
		this.runId            = System.currentTimeMillis();
		this.nameConverter    = converter;
		this.isV7Mode         = false;
		
		if (this.nameConverter == null) {
		   this.nameConverter = DefaultNameConverter.getConverter();
		}
	}

	public LdmAccessor(IFile logicalDataModelFile, LdmNameConverter converter) throws IOException {
		super(logicalDataModelFile);
		
      this.nameConverter = converter;
      
      if (this.nameConverter == null) {
         this.nameConverter = DefaultNameConverter.getConverter();
      }
	}

	/**
	 * getSubPackages
	 * 
	 * collects a list of sub packages of the given package
	 * 
	 * @param pkg
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void getSubPackages(Package pkg, List<Package> subPackages) {

		subPackages.add(pkg);

		if (pkg.getChildren().size() > 0) {

			Iterator<Package> iterator = pkg.getChildren().iterator();
			while (iterator.hasNext()) {
				Package currentPkg = iterator.next();
				/* add child packages of current package */
				getSubPackages(currentPkg, subPackages);
			}
		}
	}

	/**
	 * getPackages
	 * 
	 * collect all packages recursively
	 * 
	 * @return
	 */
	public List<Package> getPackages() {

		/* recursively search for sub packages */
		List<Package> packages = new ArrayList<Package>();
		this.getSubPackages(this.rootPackage, packages);
		return packages;
	}

	public Package getPackage(String packageName) {
		Package resultPackage = null;

		if (this.rootPackage.getName().equals(packageName)) {
			return this.rootPackage;
		}

		Iterator<Package> iterator = this.getPackages().iterator();
		while ((resultPackage == null) && iterator.hasNext()) {
			Object child = iterator.next();
			if (child instanceof Package) {
				Package childPackage = (Package) child;
				if (childPackage.getName().equals(packageName)) {
					resultPackage = childPackage;
					break;
				}
			}
		}

		if (resultPackage == null) {
			resultPackage = LogicalDataModelFactory.eINSTANCE.createPackage();
			this.rootPackage.getChildren().add(resultPackage);
			resultPackage.setName(packageName);
		}

		return resultPackage;
	}

	public Package getPackageFor(Package parent, String packageName) {
		Package resultPackage = null;

		if (parent.getName().equals(packageName)) {
			resultPackage = parent;
		}
		else {
	      resultPackage = getPackageByName(packageName, parent.getChildren());
		}

		if (resultPackage == null) {
			resultPackage = LogicalDataModelFactory.eINSTANCE.createPackage();
			parent.getChildren().add(resultPackage);
			resultPackage.setName(packageName);
		}

		return resultPackage;
	}

	
	public Package getPackageFromPathBelowRoot(String packagePath, boolean addAsNewIfNotFound) {
		StringTokenizer tok = new StringTokenizer(packagePath, PACKAGE_HIERARCHY_SEPARATOR);
		Package currentPackage = this.getRootPackage();
		while (tok.hasMoreTokens()) {
			String curName = tok.nextToken();
			Package matchingChild = null;
			for (Object kidPackageO : currentPackage.getChildren()) {
				Package kidPackage = (Package) kidPackageO;
				if (kidPackage.getName().equals(curName)) {
					matchingChild = kidPackage;
					break;
				}
			}
			if (matchingChild == null) {
				if (!addAsNewIfNotFound) {
					return currentPackage;
				}
				matchingChild = LogicalDataModelFactory.eINSTANCE.createPackage();
				matchingChild.setName(curName);
				currentPackage.getChildren().add(matchingChild);
			}
			currentPackage = matchingChild;			
		}
		return currentPackage;
	}

	
   @SuppressWarnings("unchecked")
   @Deprecated
   public Package getPackageFromPath(String packagePath, boolean addAsNewIfNotFound) {
      Package         resultPackage;
      Package         rootPackage;
      Package         newPackage;
      
      resultPackage = null;
      if (packagePath != null && packagePath.length() > 0) {
         rootPackage = this.getRootPackage();
         
         if (rootPackage.getName().equals(packagePath.substring(PACKAGE_HIERARCHY_SEPARATOR.length())) ||
             packagePath.length() == PACKAGE_HIERARCHY_SEPARATOR.length()) {
            resultPackage = rootPackage;
         }
         else {
            EList tmpRootList = new BasicEList<Package>();
            tmpRootList.add(rootPackage); 
            resultPackage = getPackageFromPath(packagePath, tmpRootList);
         }

         // no package found but the package is to be added
         if (resultPackage == null && addAsNewIfNotFound) {
            StringTokenizer pathTokenizer;
            String          curPkgName;

            rootPackage = getRootPackage();
            pathTokenizer = new StringTokenizer(packagePath, PACKAGE_HIERARCHY_SEPARATOR);
            if (pathTokenizer.hasMoreElements()) {
               curPkgName = pathTokenizer.nextToken();
               
               if (!rootPackage.getName().equals(curPkgName)) {
            	   Activator.getLogger().log(Level.WARNING, MessageFormat.format(Messages.LdmAccessor_1, rootPackage.getName(), curPkgName));
                  packagePath = "";   //prevent running next tokenizer //$NON-NLS-1$
               }
            }
            else {
               // error there is no root package name specified
            	Activator.getLogger().log(Level.WARNING, Messages.LdmAccessor_3);
               packagePath = "";   //prevent running next tokenizer //$NON-NLS-1$
            }
            
            // the root segment must be the same name
            pathTokenizer = new StringTokenizer(packagePath, PACKAGE_HIERARCHY_SEPARATOR);
            while (pathTokenizer.hasMoreElements()) {
               curPkgName = pathTokenizer.nextToken();
               newPackage = LogicalDataModelFactory.eINSTANCE.createPackage();
               newPackage.setName(curPkgName);
               
               rootPackage.getChildren().add(newPackage);
               rootPackage = newPackage;
               
               if (!pathTokenizer.hasMoreElements()) {
                  resultPackage = newPackage;
               }
            } // end of if (pathTokenizer.hasMoreElements())
         } // end of if (resultPackage == null && addAsNewIfNotFound)
      } // end of if (packagePath != null && packagePath.length() > 0)

      return resultPackage;
   }
   
   
   @SuppressWarnings("unchecked")
   private Package getPackageByName(String packageName, EList packageList) {
      Package           resultPackage;
      Package           curPackage;
      Iterator<Package> pkgListIter;
      Object            child;
      
      resultPackage = null;
      pkgListIter   = packageList.iterator();
      while(pkgListIter.hasNext() && resultPackage == null) {
         child = pkgListIter.next();
         
         if (child instanceof Package) {
            curPackage = (Package) child;
            
            if (curPackage.getName().equals(packageName)) {
               resultPackage = curPackage;
            }
         } // end of if (child instanceof Package)
      } // end of while(pkgListIter.hasNext() && resultPackage == null)

      return(resultPackage);
   }
      

   private Package getPackageFromPath(String packagePath, EList packageList) {
      Package         resultPackage;
      Package         curPackage;
      StringTokenizer pathTokenizer;
      String          curPkgName;
      
      resultPackage = null;
      if (packagePath != null && packagePath.length() > 0) {
         
         // get next subpackage name
         pathTokenizer = new StringTokenizer(packagePath, PACKAGE_HIERARCHY_SEPARATOR);
         
         if (pathTokenizer.hasMoreElements()) {
            curPkgName = pathTokenizer.nextToken();
            curPackage = getPackageByName(curPkgName, packageList);
            
            // if current package name could be found in hierarchy ...
            if (curPackage != null) {
               // ==> go on 
               packagePath = packagePath.substring(curPkgName.length() + PACKAGE_HIERARCHY_SEPARATOR.length());
               
               // check if if we are 'end-of-path'
               if (packagePath.length() == 0) {
                  // we got it
                  resultPackage = curPackage;
               }
               else {
                  resultPackage = getPackageFromPath(packagePath, curPackage.getChildren());
               }
            }
         } // end of if (pathTokenizer.hasMoreElements())
      } // end of if (packagePath != null && packagePath.length() > 0)
      
      return(resultPackage);
   }
   
   public String getPackagePath(Package selectedPackage) {
      Package       rootPackage;
      Package       curPackage;
      StringBuffer  retSerialBuf;
      List<String>  tmpList;

      // first get root package
      tmpList     = new ArrayList<String>();
      curPackage  = selectedPackage;
      rootPackage = selectedPackage;
      while(curPackage != null) {
         rootPackage = curPackage;
         tmpList.add(curPackage.getName());
         
         curPackage = rootPackage.getParent();
      }
      
      // build the 'hierarchical' package name from list
      retSerialBuf = new StringBuffer(PACKAGE_HIERARCHY_SEPARATOR);
      for(int listIdx = tmpList.size()-1; listIdx >= 0 ; listIdx --) {
         retSerialBuf.append(tmpList.get(listIdx));
         
         if (listIdx > 0) {
            retSerialBuf.append(PACKAGE_HIERARCHY_SEPARATOR);
         }
      }
      
      return(retSerialBuf.toString());
   }


	public Entity findEntity(String tableName) {
		return findEntity(this.rootPackage, tableName);
	}

	@SuppressWarnings("unchecked")
	public Entity findEntity(Package targetPackage, String tableName) {
		// osuhre: behaviour change: always search from the root package
		//   table name must be unique across packages
		targetPackage = this.rootPackage;
	   tableName = nameConverter.convertEntityName(tableName);
	   
		Iterator<Entity> iterator = targetPackage.getEntitiesRecursively().iterator();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			if (entity.getName().equalsIgnoreCase(tableName)) {
				return entity;
			}
			
			// CW fix: temporary fix so CW can find this table
			// -----------------------------------------------
			String tmpCWJLTName = MessageFormat.format(Constants.CW_JLT_NAME_TEMPLATE,
                                                    new Object[] { tableName });
			if (entity.getName().equalsIgnoreCase(tmpCWJLTName)) {
            return entity;
         }
         // -----------------------------------------------
		}
		
		return null;
	}


   public Entity findEntity(Package targetPackage, Segment idocSegment) {
      String tableName = idocSegment.getTableName();

      return findEntity(this.rootPackage, tableName);
   }


	public void deleteEntity(Entity entity) {
		if (entity != null) {
			Package parentPackage = entity.getPackage();
			if (parentPackage == null) {
				// This happens, if the entity was already deleted...
				return;
			}
			parentPackage.getContents().remove(entity);
		}
		this.summaryCollector.addJltDeleteMessage(entity.getName());
	}

	public void deleteAnnotation(SQLObject object, String annotationName) {
		EAnnotation annotation = object.getEAnnotation(DataToolsPlugin.ANNOTATION_UDP);
		if (annotation == null) {
			return;
		}

		annotation.getDetails().removeKey(annotationName);
	}

	public Entity createEntity(Package targetPackage, String tableName, String cleanedTableName, String tableDescription) {
	   
		if (!cleanedTableName.equals(tableName)) {
			this.summaryCollector.addTableRenameMessage(tableName, cleanedTableName);
			tableName = cleanedTableName;
		}
		
		return createEntity(targetPackage, tableName, tableDescription);
	}

   public Entity createEntity(Package targetPackage, Segment segment) {
      String segmentName      = segment.getTableName();
      String cleanedTableName = nameConverter.convertEntityName(segment.getTableName());

      return(createEntity(targetPackage, segmentName, cleanedTableName, segment.getDescription()));
    }

	public Entity createEntity(Package targetPackage, String tableName, String tableDescription) {

      tableName = nameConverter.convertEntityName(tableName);
      
		this.summaryCollector.addTable(tableName);

		Entity entity = LogicalDataModelFactory.eINSTANCE.createEntity();
		entity.setPackage(targetPackage);
		entity.setName(tableName);
		entity.setLabel(tableDescription);

		addModelAnnotations(entity);
      addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_PACKS_V7_MODE, String.valueOf(this.isV7Mode));

		return entity;
	}

	public Attribute addColumnMetadataToTable(Entity entity, AbstractField field) {
		// DataType dataType = new DataType(this, sapDataType, sapBaseDataType,
		// length, decimals);

		// Check whether this attribute already exists
		Attribute attribute = findAttribute(entity, field.getFieldName());
		if (attribute != null) {
			return null;
		}

		// If not, create a new one
		// attribute = createAttribute(columnName, columnDescription,
		// columnLabel, dataType);
		attribute = createAttribute(field);
		if (field.isKey()) {
			attribute.setRequired(field.isKey());
			addPrimaryKeyToAttribute(entity, attribute);
		} else {
			attribute.setRequired(!field.isNullable());
		}
		entity.getAttributes().add(attribute);

		String cleanedFieldName = this.nameConverter.convertAttributeName(field.getFieldName()); 
		if (! cleanedFieldName.equals(field.getFieldName())) {
			this.summaryCollector.addColumnRenameMessage(field, cleanedFieldName);
		}

		return attribute;
	}

	private Attribute createAttribute(AbstractField field) {

		// get the converted attribute name
		String attributeName = nameConverter.convertAttributeName(field.getFieldName());

		Attribute attribute = LogicalDataModelFactory.eINSTANCE.createAttribute();
		attribute.setName(attributeName);
		attribute.setDescription(field.getFieldDescription());
		attribute.setLabel(field.getFieldLabel());

		// 43006, osuhre: technical fields should not be changed according to
		// the type settings
		IDataTypeMapper mapper = this.dataTypeMapper;
		if (field instanceof TechnicalField) {
			mapper = new LogicalTableDataTypeMapper();
		}

		SapDataType sapDataType = field.getDataType();
		
		// different handling for "Active Extension" or "Packs Base" 
		IdaDataType idaDataType;
		if (ModeManager.isCWEnabled()) {
			// * * * *  A C T I V E   E X T E N S I O N  * * * *
			// Use the mapper as defined in the wizard (could be VARCHAR mapper for IDOC fields)
			idaDataType = mapper.map(sapDataType);

			/*
         // Only set the modified length (*=length-factor) for IDocfields
         if (mapper instanceof VarcharDataTypeMapper) {
            idaDataType.setVarCharLengthFactor(field) Length(field.getVarCharLength());
         }
			 */
		}
		else {
			// SNELKE, 113620: For IDocs and option "VARCHAR only" 
			// --> Table fields will still be treated as table fields and not be converted to VARCHAR
			// * * * *  P A C K S   B A S E  * * * *
			if (field instanceof TableField) {
				// Always use the tableDataType Mapper 
				idaDataType = this.tableDataTypeMapper.map(sapDataType);
			} else {
				// Use the mapper as defined in the wizard (could be VARCHAR mapper for IDOC fields)
				idaDataType = mapper.map(sapDataType);

				/*
	         // Only set the modified length (*=length-factor) for IDocfields
	         if (mapper instanceof VarcharDataTypeMapper) {
	            idaDataType.setLength(field.getVarCharLength());
	         }
				 */
			}
			// End of 133620 fix
		} // end of (else) if (ExtensionPreferencePage.isExtensionActive())


		if (sapDataType.isCharToVarcharExtension()) {
			summaryCollector.addCharToVarcharExtensionMessage(field.getFieldName());
		}



		if (sapDataType instanceof SapDataElement) {
			// We're dealing with a Data Element so we need to create a domain
			SapDataElement sapDataElement = (SapDataElement) sapDataType;
			String newDataTypeName = nameConverter.convertAtomicDomainName(sapDataElement.getDataElementName());
			//         String newDataTypeName = sapDataElement.getDataElementName();
			attribute.setDataType(newDataTypeName);
			this.ddmAccessor.getAtomicDomain(newDataTypeName, idaDataType.getIdaDataTypeDefinition());

			addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATATYPE_DATA_ELEMENT, 
					((SapDataElement) sapDataType).getDataElementName());
			try {
				// osuhre, 47017: attributes must not be derived. Derived means
				// that the value is
				// always a combination of other attributes which in our case is
				// never true.
				// It has nothing to do with data elements.
				attribute.setDerived(false);
			} catch (Exception e) {
				// Avoid errors for older versions of IDA where this option was
				// not available
			}
		} else { // Not a data element, so creating a normal attribute (no
			// domain) is sufficent
			try {
				attribute.setDerived(false);
				attribute.setDataType(idaDataType.getIdaDataTypeDefinition());
			} catch (Exception e) {
				// Avoid errors for older versions of IDA where this option was
				// not available
			}
		}

		// Add the data type annotations, but only if this is an attribute that
		// came from SAP, computed fields
		// (technical fields) are ignored
		if (sapDataType.getDataTypeName() != null) {
			addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATATYPE_DATATYPE, sapDataType.getDataTypeName());
			addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATATYPE_LENGTH, String.valueOf(sapDataType.getLength()));
			addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATATYPE_DECIMALS, String.valueOf(sapDataType.getDecimals()));
			if ((sapDataType instanceof SapDataElement) && ((SapDataElement) sapDataType).hasDomain()) {
				addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATATYPE_DOMAIN, ((SapDataElement) sapDataType).getDomainName());
			}
		}

		// If the field points to a checktable, we add an Annotation
		if (field.hasCheckTable()) {
			addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_RELATED_CHECKTABLE, field.getCheckTable());
		}

		addModelAnnotations(attribute);
		addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATATYPE_DISPLAY_LENGTH, String.valueOf(field.getDataType().getABAPDisplayLength()));
		return attribute;
	}

	/**
	 * Finds and returns an attribute on a specified key
	 * 
	 * @param key
	 *            : The key on which the attribute will be searched for
	 * @param attributeName
	 *            : The name of the attribute
	 * @return: Returns the attribute if it was found on the key or "null" if
	 *          the attribute wasn't found
	 */
	@SuppressWarnings("unchecked")
	private Attribute findAttribute(Key key, String attributeName) {
		List<Attribute> attributes = key.getAttributes();
		if (attributes == null || attributes.isEmpty()) {
			return null;
		}

		Iterator<Attribute> iterator = attributes.iterator();
		while (iterator.hasNext()) {
			Attribute attribute = iterator.next();
			if (attribute.getName().equalsIgnoreCase(attributeName)) {
				return attribute;
			}
		}
		return null;
	}

	/**
	 * Finds and return an attribute on the given entity
	 * 
	 * @param entity
	 *            : The entity to find the attribute on
	 * @param attributeName
	 *            : The name of the attribute to find
	 * @return: Returns the specified attribute if found or "null" if the
	 *          attribute wasn't found
	 */
	@SuppressWarnings("unchecked")
	public Attribute findAttribute(Entity entity, String attributeName) {
	   // get the converted attribute name
	   attributeName = nameConverter.convertAttributeName(attributeName);

		List<Attribute> attributes = entity.getAttributes();
		if (attributes != null) {
	      Iterator<Attribute> iterator = attributes.iterator();
	      
	      while (iterator.hasNext()) {
	         Attribute attribute = iterator.next();
	         
	         if (attribute.getName().equalsIgnoreCase(attributeName)) {
	            return attribute;
	         }
	      }
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public void addPrimaryKeyToAttribute(Entity entity, Attribute attribute) {
		PrimaryKey primaryKey = entity.getPrimaryKey();
		if (primaryKey == null) {
			primaryKey = LogicalDataModelFactory.eINSTANCE.createPrimaryKey();
			
			// create the primary key name: PK_ + entity name + hashcode(entity name)
			String entityName  = entity.getName();
			String pkName      = MessageFormat.format(com.ibm.is.sappack.gen.common.Constants.PRIMARY_KEY_NAME_TEMPLATE,
	                                                new Object[] { entityName } );
			StringBuffer pkSuffixBuf = new StringBuffer();
			pkSuffixBuf.append(pkName);
			pkSuffixBuf.append(NAME_SEPERATOR);
			pkSuffixBuf.append(Math.abs(pkName.hashCode()));

			pkName = nameConverter.convertEntityName(pkSuffixBuf.toString());

			primaryKey.setName(pkName);
			primaryKey.setEntity(entity);
		}

		attribute.setRequired(true);
		entity.getKeys().add(primaryKey);
		primaryKey.getAttributes().add(attribute);
	}

   public Relationship createRelationship(Package targetPackage, String tableName, String foreignKeyName, 
                                          String checkTableName, String checkTableFieldName, 
                                          String fieldName, String foreignKeyTableName,
                                          boolean enforce, String altKeyName) {
      
      // Find or create a new relationship attached to the table specified
      // in "TABNAME". For the name of the relationship
      // "TABNAME"_"FIELDNAME"_"CHECKTABLE" will be used, e.g.
      // BUT000_AUGRP_TB037
      String relationshipName = tableName + NAME_SEPERATOR + foreignKeyName + NAME_SEPERATOR + checkTableName;

      // CW fix: for CW remove prefix in the table names
      // -----------------------------------------------
      relationshipName = StringUtils.replaceString(relationshipName, Constants.CW_JLT_PREFIX, ""); //$NON-NLS-1$ 
      // -----------------------------------------------
      
      return(createRelationship(relationshipName, targetPackage, tableName, foreignKeyName, 
                                checkTableName, checkTableFieldName, fieldName, foreignKeyTableName,
                                enforce, altKeyName));
   }

	public Relationship createRelationship(String relationshipName, Package targetPackage, 
	                                       String tableName, String foreignKeyName, 
	                                       String checkTableName, String checkTableFieldName, 
	                                       String fieldName, String foreignKeyTableName,
	                                       boolean enforce, String altKeyName) {

	   // we can handle converted names only ...
      relationshipName    = nameConverter.convertEntityName(relationshipName);
	   tableName           = nameConverter.convertEntityName(tableName);
      checkTableName      = nameConverter.convertEntityName(checkTableName);
      foreignKeyTableName = nameConverter.convertEntityName(foreignKeyTableName);
      foreignKeyName      = nameConverter.convertAttributeName(foreignKeyName);
      checkTableFieldName = nameConverter.convertAttributeName(checkTableFieldName);
      fieldName           = nameConverter.convertAttributeName(fieldName);
	   
		Entity table = findEntity(targetPackage, tableName);
		if (table == null) {
			return(null);
		}

		Entity checkTable = findEntity(targetPackage, checkTableName);
		if (checkTable == null) {
         return(null);
		}

		// Find the foreign key for this relation on the child table (if the
		// foreign key does not yet exist, it will be generated). For the
		// name of the foreign key the value of the "FIELDNAME" column is
		// used
		ForeignKey foreignKey = getForeignKey(table, foreignKeyName);

      if (foreignKeyTableName.equals(SAP_SYSTEM_TABLE_1) ||
          foreignKeyTableName.equals(SAP_SYSTEM_TABLE_2)) {
		   
		   // foreign key table is the SYST or SY table ==> use check table field name as foreign key name 
		   fieldName = checkTableFieldName;
		}
		
      // add the attribute with the name specified in column "FORKEY" to
      // the foreign key
      addAttributeToKey(checkTable, foreignKey, fieldName);
//      addAttributeToKey(table, foreignKey, foreignKeyName);

      return(createRelationship(relationshipName, table, checkTable, 
                                foreignKey, foreignKeyName, enforce, altKeyName));
	}


   private Relationship createRelationship(String relationshipName, Entity childTable, Entity parentTable, 
                                           ForeignKey foreignKey, String fieldName, boolean enforce, 
                                           String altKeyName) {

      Relationship relationship = getRelationship(childTable, parentTable, relationshipName);

      Key keyToSet = null;
      
      if (altKeyName != null) {
         keyToSet = findAlternateKey(parentTable, altKeyName);
      } // end of if (altKeyName != null)
      
      if (keyToSet == null) {
         // Use the lookup table's primary key for the parent end of the
         // relationship
         keyToSet = parentTable.getPrimaryKey();
      }
      relationship.getParentEnd().setKey(keyToSet);
      
      // Use the foreign key of the table for the child end of the
      // relationship
      relationship.getChildEnd().setKey(foreignKey);
      relationship.setEnforced(enforce);
      
      return(relationship);
   }


   public void createCardinality(Package targetPackage, String tableName, String checkTableName, 
                                 String fieldName, String cardinality, String cardinalityLeft) {
      
      String relationshipName = tableName + NAME_SEPERATOR + fieldName + NAME_SEPERATOR + checkTableName;
      
      // CW fix: for CW remove prefix in the table names
      // -----------------------------------------------
      relationshipName = StringUtils.replaceString(relationshipName, Constants.CW_JLT_PREFIX, ""); //$NON-NLS-1$ 
      // -----------------------------------------------
      
      createCardinality(relationshipName, targetPackage, tableName, checkTableName, fieldName, cardinality, cardinalityLeft);
   }

   
	public void createCardinality(String relationshipName, Package targetPackage, String tableName, 
	                              String checkTableName, String fieldName, String cardinality, String cardinalityLeft) {

		// TODO: Threat special cases
		if (checkTableName.equalsIgnoreCase("*")) { //$NON-NLS-1$
			String msg = ("Special case (3): Cardinality could not be set, checktable is invalid: \"" //$NON-NLS-1$
					+ checkTableName + "\""); //$NON-NLS-1$
			Activator.getLogger().fine(msg);
			return;
		}

		Entity table = findEntity(targetPackage, tableName);
		
		Relationship relationship = findRelationship(table, relationshipName);

		if (relationship == null) {
			// TODO: Treat special cases
			String msg = "Special case (4): No relationship defined for table:\"" //$NON-NLS-1$
					+ tableName + "\" checktable: \""//$NON-NLS-1$
					+ checkTableName + "\" field: \"" + fieldName + "\"";//$NON-NLS-1$ //$NON-NLS-2$
			Activator.getLogger().fine(msg);
			return;
		}

		// Set the cardinality for the table end (i.e. the parent end)
		RelationshipEnd tableEnd = relationship.getParentEnd();
		int tableCardinality = CardinalityMapper.mapCardinality(cardinalityLeft);
		tableEnd.setCardinality(CardinalityType.get(tableCardinality));

		// Set the cardinality for the check table end (i.e. the child end)
		RelationshipEnd checkTableEnd = relationship.getChildEnd();
		int checkTableCardinality = CardinalityMapper.mapCardinality(cardinality);
		checkTableEnd.setCardinality(CardinalityType.get(checkTableCardinality));
	}

   /**
    * Find an alternate key for given key name
    * 
    * @param entity
    *            : entity where the key is located
    * @param alternateKeyName
    *            : The name of the alternate key
    *            
    * @return: Returns the specified alternate key or null if it could not be found
    */
   @SuppressWarnings("unchecked")
   public AlternateKey findAlternateKey(Entity entity, String alternateKeyName) {

      AlternateKey alternateKey = null;
      
      // first get the entity of the passed table
      if (entity != null) {
         alternateKeyName = nameConverter.convertEntityName(alternateKeyName);
         
         // search the alternate key with passed name
         // (iterate over all existing keys)
         List<AlternateKey> keyList = entity.getAlternateKeys();
         if (keyList != null && !alternateKeyName.isEmpty()) {
            
            Iterator<AlternateKey> iterator = keyList.iterator();
            while (iterator.hasNext() && alternateKey == null) {
               AlternateKey tmpKey = iterator.next();
               if (tmpKey.getName().equalsIgnoreCase(alternateKeyName)) {
                  alternateKey = tmpKey;
               }
            } // end of while (iterator.hasNext() && altKey == null)
         } // end of if (alternateKeys != null && !alternateKeyName.isEmpty())
      } // end of if (entity != null)
      
      return(alternateKey);
   }

   /**
    * Find or creates a new alternate key for given table name
    * 
    * @param tableName 
    *            : name of the entity to find the key on
    *            
    * @param alternateKeyName
    *            : The name of the alternate key
    *            
    * @return: Returns the specified alternate key on the given entity or null
    */
   @SuppressWarnings("unchecked")
   public AlternateKey getAlternateKey(String tableName, String alternateKeyName) {

      AlternateKey alternateKey = null;
      
      // first get the entity of the passed table
      Entity tableEntity = findEntity(tableName);
      if (tableEntity != null) {
         // search the alternate key with passed name
         alternateKey = findAlternateKey(tableEntity, alternateKeyName);
         
         // If not found, create a new Alternate key
         if (alternateKey == null) {
            alternateKeyName = nameConverter.convertEntityName(alternateKeyName);
            
            alternateKey = LogicalDataModelFactory.eINSTANCE.createAlternateKey();
            alternateKey.setName(alternateKeyName);
            tableEntity.getAlternateKeys().add(alternateKey);
            tableEntity.getKeys().add(alternateKey);
         }
      } // end of if (tableEntity != null)
      
      return(alternateKey);
   }

	/**
	 * Find or creates a new returns a foreign key on a given entity
	 * 
	 * @param entity
	 *            : The entity to find the key on
	 * @param foreignKeyName
	 *            : The name of the foreign key
	 * @return: Returns the specified foreign key on the given entity
	 */
	@SuppressWarnings("unchecked")
	public ForeignKey getForeignKey(Entity entity, String foreignKeyName) {

		ForeignKey foreignKey = findForeignKey(entity, foreignKeyName);
		
		if (foreignKey == null) {
		   // If not found, create a new foreign key
		   foreignKey = LogicalDataModelFactory.eINSTANCE.createForeignKey();
		   foreignKey.setName(foreignKeyName);
		   entity.getForeignKeys().add(foreignKey);
		   entity.getKeys().add(foreignKey);
      }
		
		return foreignKey;
	}

	public ForeignKey findForeignKey(Entity entity, String foreignKeyName) {
		// Iterate over all existing foreign key an try to find the searched on
		List<ForeignKey> foreignKeys = entity.getForeignKeys();
		if (foreignKeys != null && !foreignKeyName.isEmpty()) {
			Iterator<ForeignKey> iterator = foreignKeys.iterator();
			while (iterator.hasNext()) {
				ForeignKey foreignKey = iterator.next();
				if (foreignKey.getName().equalsIgnoreCase(foreignKeyName)) {
					return foreignKey;
				}
			}

		}
		return null;
	}

	/**
	 * Add an attribute to a key
	 * 
	 * @param checkTable
	 *            : The 'checkTable' entity the attribute belongs to
	 * @param key
	 *            : The key the attribute will be added to
	 * @param attributeName
	 *            : The name of the attribute
	 */
	@SuppressWarnings("unchecked")
//   public void addAttributeToKey(Entity table, Entity checkTable, Key key, String attributeName) {
   public void addAttributeToKey(Entity checkTable, Key key, String attributeName) {

      // Check whether the key already contains the specified attribute, if
      // yes, nothing must be done
      Attribute attribute = findAttribute(key, attributeName);
      if (attribute != null) {
         return;
      }

      // Otherwise we need to find the attribute on the entity
      Entity entity = key.getEntity();
//      if (entity != table) {
//         String msg = Messages.LdmAccessor_1;
//         msg = MessageFormat.format(msg, attributeName, table);
//         // "Attribute " + attributeName + " was not found in entity " + table)
//         Activator.getLogger().log(Level.SEVERE, msg);
//         throw new RuntimeException(msg);
//      }
      
      attribute = findAttribute(entity, attributeName);
      if (attribute == null) {
         attribute = findAttribute(checkTable, attributeName);
         if (attribute == null) {
            String msg = MessageFormat.format(Messages.LdmAccessor_0, new Object[] { attributeName, entity.getName() });
            Activator.getLogger().log(Level.SEVERE, msg);
            throw new RuntimeException(msg);
            // TODO: Throw an appropriate exception here
         }
      }

      // Add the specified attribute to the key
      key.getAttributes().add(attribute);
   }

	/**
	 * Returns the relationship between a child and parent entity by either
	 * retrieving the already existing relationship or creating a new one
	 * 
	 * @param child
	 *            : The child entity for the relationship
	 * @param parent
	 *            : The parent entity for the relationship
	 * @param relationshipName
	 *            : The name of the relationship
	 * @return: The specified relationship
	 */
	@SuppressWarnings("unchecked")
	private Relationship getRelationship(Entity child, Entity parent, String relationshipName) {
	   
      relationshipName = StringUtils.replaceString(relationshipName, Constants.CW_JLT_PREFIX, ""); //$NON-NLS-1$ 
	   relationshipName = nameConverter.convertRelationShipName(relationshipName, 
	                                                            RELATIONSHIP_PARENT_POSTFIX.length());

		// Search for the relationship on the child entity an return it
		Relationship relationship = findRelationship(child, relationshipName);
		if (relationship != null) {
			return relationship;
		}

		// If no relationship was found, a new one will be created
		relationship = LogicalDataModelFactory.eINSTANCE.createRelationship();

		// The table (not the lookup table!) is the owning entity
		relationship.setOwningEntity(child);

		// parent.getRelationships().add(relationship);
		relationship.setName(relationshipName);
		relationship.setExistenceOptional(false);
		relationship.setRelationshipType(RelationshipType.get(RelationshipType.NON_IDENTIFYING));

		RelationshipEnd childRelationShipEnd = LogicalDataModelFactory.eINSTANCE.createRelationshipEnd();
		childRelationShipEnd.setEntity(child);
		childRelationShipEnd.setVerbPhrase(VERBNAME);
		childRelationShipEnd.setName(relationshipName + RELATIONSHIP_CHILD_POSTFIX);
		childRelationShipEnd.setCardinality(CardinalityType.get(CardinalityType.ZERO_TO_MANY));

		RelationshipEnd parentRelationShipEnd = LogicalDataModelFactory.eINSTANCE.createRelationshipEnd();
		parentRelationShipEnd.setEntity(parent);
		parentRelationShipEnd.setVerbPhrase(VERBNAME);
		parentRelationShipEnd.setName(relationshipName + RELATIONSHIP_PARENT_POSTFIX);
		parentRelationShipEnd.setCardinality(CardinalityType.get(CardinalityType.ONE));
		
		relationship.getRelationshipEnds().add(parentRelationShipEnd);
		relationship.getRelationshipEnds().add(childRelationShipEnd);

		return relationship;
	}

	@SuppressWarnings("unchecked")
	private Relationship findRelationship(Entity entity, String relationshipName) {
		List<Relationship> relationships = entity.getRelationships();
		
		if (relationships != null) {
	      Iterator<Relationship> iterator = relationships.iterator();
	      
	      while (iterator.hasNext()) {
	         Relationship relationship = iterator.next();
	         
	         if (relationship.getName().equalsIgnoreCase(relationshipName)) {
	            return relationship;
	         }
	      }
		}

		return null;
	}

	public void addAnnotation(SQLObject object, String key, String value) {
		// don't create certain annotations in modeling mode
		if (ModeManager.isModellingEnabled()) {
			if (ModeManager.getModellingAnnotationsToBeFiltered().contains(key)) {
				return;
			}
		}
		
		EAnnotation annotation = object.getEAnnotation(DataToolsPlugin.ANNOTATION_UDP);
		if (annotation == null) {
			annotation = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation.setSource(DataToolsPlugin.ANNOTATION_UDP);
		}
		annotation.getDetails().put(key, value);
		object.getEAnnotations().add(annotation);
	}

	// @SuppressWarnings("unchecked")
	public void addModelAnnotations(SQLObject object) {
		// Add the SAP System information:
		addAnnotation(object, com.ibm.is.sappack.gen.common.Constants.ANNOT_SAP_SYSTEM_NAME, this.sapSystem.getName());
		addAnnotation(object, com.ibm.is.sappack.gen.common.Constants.ANNOT_SAP_SYSTEM_HOST, this.sapSystem.getHost());
		addAnnotation(object, com.ibm.is.sappack.gen.common.Constants.ANNOT_SAP_SYSTEM_NUMBER, this.sapSystem.getSystemNumber() + Constants.EMPTY_STRING);
		addAnnotation(object, com.ibm.is.sappack.gen.common.Constants.ANNOT_SAP_SYSTEM_IS_UNICODE, this.sapSystem.isUnicode().toString());
		addAnnotation(object, com.ibm.is.sappack.gen.common.Constants.ANNOT_SAP_CLIENT_ID, this.sapSystem.getClientId() + Constants.EMPTY_STRING);
		addAnnotation(object, com.ibm.is.sappack.gen.common.Constants.ANNOT_SAP_USERNAME, this.sapSystem.getUsername());
      addAnnotation(object, com.ibm.is.sappack.gen.common.Constants.ANNOT_SAP_USERLANGUAGE, this.sapSystem.getUserLanguage());
		addAnnotation(object, com.ibm.is.sappack.gen.common.Constants.ANNOT_OS_USERNAME, System.getProperty(com.ibm.is.sappack.gen.common.Constants.ANNOT_PROPERTY_USER_NAME));

		Date now = new Date();
		DateFormat dateFormat = DateFormat.getDateInstance();
		addAnnotation(object, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATE_CREATED, dateFormat.format(now));

		DateFormat timeFormat = DateFormat.getTimeInstance();
		addAnnotation(object, com.ibm.is.sappack.gen.common.Constants.ANNOT_TIME_CREATED, timeFormat.format(now));

		addAnnotation(object, com.ibm.is.sappack.gen.common.Constants.ANNOT_RUN_ID, this.runId + Constants.EMPTY_STRING);

		// // Adding the Metapack annotations
		// List<Entity> entities = this.targetPackage.getEntitiesRecursively();
		// if (entities == null || entities.isEmpty()) {
		// return;
		// }
		// Iterator<Entity> entityIterator = entities.iterator();
		// while (entityIterator.hasNext()) {
		// Entity entity = entityIterator.next();
		// // On all entities add an annotation about whether relations were
		// // extracted or not
		// addAnnotation(entity,
		// com.ibm.is.metapack.common.Constants.ANNOT_EXTRACT_REALTIONS,
		// Boolean.toString(extractRelations));
		// addAnnotation(entity,
		// com.ibm.is.metapack.common.Constants.ANNOT_DATA_OBJECT_SOURCE,
		// com.ibm.is.metapack.common.Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE);
		//			
		// addAnnotation(entity,
		// com.ibm.is.metapack.common.Constants.ANNOT_SAP_SYSTEM_NAME,
		// sapSystem.getName());
		// addAnnotation(entity,
		// com.ibm.is.metapack.common.Constants.ANNOT_SAP_SYSTEM_HOST,
		// sapSystem.getHost());
		// addAnnotation(entity,
		// com.ibm.is.metapack.common.Constants.ANNOT_SAP_SYSTEM_NUMBER,
		// Integer.toString(sapSystem.getSystemNumber()));
		// addAnnotation(entity,
		// com.ibm.is.metapack.common.Constants.ANNOT_SAP_CLIENT_ID,
		// Integer.toString(sapSystem.getClientId()));
		// addAnnotation(entity,
		// com.ibm.is.metapack.common.Constants.ANNOT_SAP_USERNAME,
		// sapSystem.getUsername());
		// addAnnotation(entity,
		// com.ibm.is.metapack.common.Constants.ANNOT_OS_USERNAME,
		// System.getProperty(com.ibm.is.metapack.common.Constants.ANNOT_PROPERTY_USER_NAME));
		//
		// // On all attributes add the "metapack" annotation
		// List<Attribute> attributes = entity.getAttributes();
		// if (attributes == null || attributes.isEmpty()) {
		// continue;
		// }
		// Iterator<Attribute> attributeIterator = attributes.iterator();
		// while (attributeIterator.hasNext()) {
		// Attribute attribute = attributeIterator.next();
		// addAnnotation(attribute,
		// com.ibm.is.metapack.common.Constants.ANNOT_DATA_OBJECT_SOURCE,
		// com.ibm.is.metapack.common.Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE);
		// }
		// }
	}

	// @SuppressWarnings("unchecked")
	// public void addModelAnnotations(SapSystem sapSystem, boolean
	// extractRelations) {
	// // Add the SAP System information:
	// addAnnotation(this.rootPackage,
	// com.ibm.is.metapack.common.Constants.ANNOT_SAP_SYSTEM_NAME,
	// sapSystem.getName());
	// addAnnotation(this.rootPackage,
	// com.ibm.is.metapack.common.Constants.ANNOT_SAP_SYSTEM_HOST,
	// sapSystem.getHost());
	// addAnnotation(this.rootPackage,
	// com.ibm.is.metapack.common.Constants.ANNOT_SAP_SYSTEM_NUMBER,
	// sapSystem.getSystemNumber() + Constants.EMPTY_STRING);
	// addAnnotation(this.rootPackage,
	// com.ibm.is.metapack.common.Constants.ANNOT_SAP_CLIENT_ID,
	// sapSystem.getClientId() + Constants.EMPTY_STRING);
	// addAnnotation(this.rootPackage,
	// com.ibm.is.metapack.common.Constants.ANNOT_SAP_USERNAME,
	// sapSystem.getUsername());
	// addAnnotation(this.rootPackage,
	// com.ibm.is.metapack.common.Constants.ANNOT_OS_USERNAME,
	// System.getProperty(com.ibm.is.metapack.common.Constants.ANNOT_PROPERTY_USER_NAME));
	//
	// Date now = new Date();
	// DateFormat dateFormat = DateFormat.getDateInstance();
	// addAnnotation(this.rootPackage,
	// com.ibm.is.metapack.common.Constants.ANNOT_DATE_CREATED,
	// dateFormat.format(now));
	//
	// DateFormat timeFormat = DateFormat.getTimeInstance();
	// addAnnotation(this.rootPackage,
	// com.ibm.is.metapack.common.Constants.ANNOT_TIME_CREATED,
	// timeFormat.format(now));
	//
	// // Adding the Metapack annotations
	// List<Entity> entities = this.targetPackage.getEntitiesRecursively();
	// if (entities == null || entities.isEmpty()) {
	// return;
	// }
	// Iterator<Entity> entityIterator = entities.iterator();
	// while (entityIterator.hasNext()) {
	// Entity entity = entityIterator.next();
	// // On all entities add an annotation about whether relations were
	// // extracted or not
	// addAnnotation(entity,
	// com.ibm.is.metapack.common.Constants.ANNOT_EXTRACT_REALTIONS,
	// Boolean.toString(extractRelations));
	// addAnnotation(entity,
	// com.ibm.is.metapack.common.Constants.ANNOT_DATA_OBJECT_SOURCE,
	// com.ibm.is.metapack.common.Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE);
	//			
	// addAnnotation(entity,
	// com.ibm.is.metapack.common.Constants.ANNOT_SAP_SYSTEM_NAME,
	// sapSystem.getName());
	// addAnnotation(entity,
	// com.ibm.is.metapack.common.Constants.ANNOT_SAP_SYSTEM_HOST,
	// sapSystem.getHost());
	// addAnnotation(entity,
	// com.ibm.is.metapack.common.Constants.ANNOT_SAP_SYSTEM_NUMBER,
	// Integer.toString(sapSystem.getSystemNumber()));
	// addAnnotation(entity,
	// com.ibm.is.metapack.common.Constants.ANNOT_SAP_CLIENT_ID,
	// Integer.toString(sapSystem.getClientId()));
	// addAnnotation(entity,
	// com.ibm.is.metapack.common.Constants.ANNOT_SAP_USERNAME,
	// sapSystem.getUsername());
	// addAnnotation(entity,
	// com.ibm.is.metapack.common.Constants.ANNOT_OS_USERNAME,
	// System.getProperty(com.ibm.is.metapack.common.Constants.ANNOT_PROPERTY_USER_NAME));
	//
	// // On all attributes add the "metapack" annotation
	// List<Attribute> attributes = entity.getAttributes();
	// if (attributes == null || attributes.isEmpty()) {
	// continue;
	// }
	// Iterator<Attribute> attributeIterator = attributes.iterator();
	// while (attributeIterator.hasNext()) {
	// Attribute attribute = attributeIterator.next();
	// addAnnotation(attribute,
	// com.ibm.is.metapack.common.Constants.ANNOT_DATA_OBJECT_SOURCE,
	// com.ibm.is.metapack.common.Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE);
	// }
	// }
	// }

	public Package getRootPackage() {
		return this.rootPackage;
	}

   public String getRootPackagePath() {
      return(PACKAGE_HIERARCHY_SEPARATOR + this.rootPackage.getName());
   }

	public SummaryCollector getSummaryCollector() {
		return this.summaryCollector;
	}

	public static Map<String, String> getAnnotations(SQLObject object) {
		EAnnotation annotation = object.getEAnnotation(DataToolsPlugin.ANNOTATION_UDP);
		if (annotation == null) {
			return new HashMap<String, String>();
		}
		return annotation.getDetails().map();
	}

	public long getRunID() {
		return this.runId;
	}

	public List<Entity> getAllEntities() {
		// recursive search from the root returns all entities !! 
		return (this.rootPackage.getEntitiesRecursively());
//		List<Entity> result = new ArrayList<Entity>();
//		for (Package p : getPackages()) {
//			List<?> entities = p.getEntitiesRecursively();
//			for (Object o : entities) {
//				if (o instanceof Entity) {
//					result.add((Entity) o);
//				}
//			}
//		}
//		return result;
	}

	public static String getAnnotationValue(SQLObject object, String annotationName) {
		Map<String, String> annotationMap = getAnnotations(object);
		return annotationMap.get(annotationName);
	}

	public List<Attribute> findAttributesByAnnotationValue(Entity entity, String annotationName, String annotationValue) {
		List<Attribute> foundAttributes = new ArrayList<Attribute>();

		List<Attribute> attributes = entity.getAttributes();
		Iterator<Attribute> attributeIterator = attributes.iterator();
		while (attributeIterator.hasNext()) {
			Attribute currentAttribute = attributeIterator.next();
			String currentAttributeAnnotationValue = getAnnotationValue(currentAttribute, annotationName);
			if (annotationValue.trim().equalsIgnoreCase(currentAttributeAnnotationValue)) {
				foundAttributes.add(currentAttribute);
			}
		}
		return foundAttributes;
	}

	public LdmNameConverter getNameConverter() {
	   return(nameConverter);
	}

	public void setV7Mode(boolean isV7Mode) {
	   this.isV7Mode = isV7Mode;
	}

}
