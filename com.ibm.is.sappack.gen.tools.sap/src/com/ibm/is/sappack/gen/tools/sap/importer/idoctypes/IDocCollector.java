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
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.idoctypes
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.idoctypes;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.jco.RfcDestinationDataProvider;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.ImporterOptionsBase.CHECKTABLEOPTIONS;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.TableImporterOptions;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.IDocField;
import com.ibm.is.sappack.gen.tools.sap.model.IDocSegmentTableSet;
import com.ibm.is.sappack.gen.tools.sap.model.IDocTableSet;
import com.ibm.is.sappack.gen.tools.sap.model.SapTable;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoStructure;


public class IDocCollector implements IRunnableWithProgress {

	private List<String> idocSegments;
	private IDocType idocType;
	private List<String> blacklist;
	private TableImporterOptions.CHECKTABLEOPTIONS checkTablesOption;
	private boolean textTablesOption;
	private IDocTableSet idocTableSet;
	private SapSystem sapSystem;
	private JCoDestination destination;
	private JCoFunction function_DD_TABL_GET;
	private JCoFunction function_TABLE_GET_TEXTTABLE;
	private IFile logicalDataModelFile;


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/**
	 * IDocCollector
	 * 
	 * @param sapSystem
	 * @param idocSegments
	 * @param idocType
	 * @param logicalDataModelFile
	 */
	public IDocCollector(SapSystem sapSystem, List<String> idocSegments, IDocType idocType, IFile logicalDataModelFile) {

		this.idocSegments = idocSegments;
		this.idocType = idocType;
		this.blacklist = new ArrayList<String>();
		this.sapSystem = sapSystem;
		this.textTablesOption = false;
		this.checkTablesOption = CHECKTABLEOPTIONS.NO_CHECKTABLES;
		this.idocTableSet = new IDocTableSet(this.idocType);
		this.logicalDataModelFile = logicalDataModelFile;
	}

	/**
	 * init
	 * 
	 * @throws JCoException
	 */
	private void init(IProgressMonitor progressMonitor) throws JCoException {
		
		// only necessary if we need to extract check or text tables
		if(this.checkTablesOption == CHECKTABLEOPTIONS.NO_CHECKTABLES) {
			return;
		}
		
		if (this.destination == null) {

			progressMonitor.beginTask(Messages.SapLogicalTableCollector_4, 3);
			progressMonitor.subTask(Messages.SapLogicalTableCollector_4);
			this.destination = RfcDestinationDataProvider.getDestination(this.sapSystem);
			JCoRepository repository = null;

			repository = this.destination.getRepository();
			progressMonitor.worked(1);

			this.function_DD_TABL_GET = repository.getFunction(Constants.JCO_FUNCTION_DD_TABL_GET);
			progressMonitor.worked(1);

			if (!progressMonitor.isCanceled()) {
				this.function_TABLE_GET_TEXTTABLE = repository.getFunction(Constants.JCO_FUNCTION_TABLE_GET_TEXTTABLE);
				progressMonitor.worked(1);
			}

		}

	}

	/**
	 * getIDocTableSet
	 * 
	 * @return
	 */
	public IDocTableSet getIDocTableSet() {
		return this.idocTableSet;
	}

	/**
	 * collectTables
	 * 
	 * @param progressMonitor
	 * @throws IDocTypeExtractException
	 * @throws JCoException
	 * @throws IOException
	 */
	private void collectTables(IProgressMonitor progressMonitor) throws IDocTypeExtractException, JCoException,
			IOException {

		progressMonitor.setTaskName(Messages.IDocCollector_2);
		if (!this.idocType.exists()) {
			String name = this.idocType.getName();
			if (name == null) {
				name = ""; //$NON-NLS-1$
			}
			String msg = MessageFormat.format(Messages.IDocCollector_3, name);
			throw new IDocTypeExtractException(msg);
		}
		/* determine number of steps */
		int steps = 0;
		for (int i = 0; i < this.idocSegments.size(); i++) {
			String segmentName = this.idocSegments.get(i);
			Segment segment = this.idocType.getSegment(segmentName);
			if (segment == null) {
				String msg = MessageFormat.format(Messages.MetadataImportWizard_5, new Object[]{ segmentName, this.idocType.getName() });
				throw new IDocTypeExtractException(msg);
			}
			steps ++;
			if (this.checkTablesOption != CHECKTABLEOPTIONS.NO_CHECKTABLES) {
				if (segment != null) {
					steps = steps + segment.getFields().size();
				}
			}
			
		}
		progressMonitor.beginTask("", steps); //$NON-NLS-1$

		/*
		 * iterate over all idoc segments and look for check tables and text
		 * tables
		 */
		Iterator<String> iterator = this.idocSegments.iterator();

		while (iterator.hasNext()) {

			/* check if cancel button was pressed */
			if (progressMonitor.isCanceled()) {
				return;
			}

			String segmentName = iterator.next();
			Segment segment = this.idocType.getSegment(segmentName);
			if (segment == null) {
				IDocSegmentTableSet nonExistingIdocSegmentTableSet = new IDocSegmentTableSet(null);
				SapTable t = new SapTable(segmentName, Messages.IDocSegmentListWizardPage_3);
				t.setExistsOnSAPSystem(false);
				nonExistingIdocSegmentTableSet.add(t);
				this.idocTableSet.add(nonExistingIdocSegmentTableSet);
				continue;
			}

			// check if this segment is already contained in the logical data
			// model
			String msg = Messages.IDocCollector_0;
			msg = MessageFormat.format(msg, segmentName);
			progressMonitor.subTask(msg);
		//	this.checkForExistingSegments(segment);

			IDocSegmentTableSet idocSegmentTableSet = new IDocSegmentTableSet(segment);
			this.idocTableSet.add(idocSegmentTableSet);

			/* consider check tables only if necessary */
			if (this.checkTablesOption != CHECKTABLEOPTIONS.NO_CHECKTABLES) {

				Iterator<IDocField> fields = segment.getFields().iterator();
				while (fields.hasNext()) {

					/* check if cancel button was pressed */
					if (progressMonitor.isCanceled()) {
						return;
					}

					IDocField field = fields.next();

					progressMonitor.subTask(MessageFormat.format(Messages.IDocCollector_1, segmentName, field
							.getFieldName()));

					String checkTableName = field.getCheckTable();
					SapTable checkTable = null;

					/* check if this table is blacklisted */
					if (!Utilities.containsIgnoreCase(this.blacklist, checkTableName)) {
						if (checkTableName != null) {
							checkTable = new SapTable(checkTableName, null);
							checkTable.setCheckTable(true);
							/*
							if ( this.checkTablesOption == CHECKTABLEOPTIONS.JOINED_CHECK_AND_TEXT_TABLES) {
								checkTable.setDescription(Messages.IDocCollector_3);
							} else if (this.checkTablesOption == CHECKTABLEOPTIONS.TRANSCODING_TABLES) {
								checkTable.setDescription(Messages.IDocCollector_4);
							} else {
							checkTable.setDescription(this. getTableDescription(checkTable, progressMonitor));
							  }
							*/
							this.addTableInfo(checkTable, progressMonitor);
							
							idocSegmentTableSet.add(checkTable);
						}

						/* consider text tables only if necessary */
						if (this.textTablesOption && checkTable != null) {
							SapTable textTable = this.getTextTable(checkTable);
							if (textTable != null) {
								textTable.setTextTable();
								//textTable.setDescription(this.getTableDescription(textTable, progressMonitor));
								this.addTableInfo(textTable, progressMonitor);
								idocSegmentTableSet.add(textTable);
							}

						}

					}
					progressMonitor.worked(1);
				}
				
			//	this.checkForExistingTables(idocSegmentTableSet.getSapTableSet());
			}
			progressMonitor.worked(1);
		}
		progressMonitor.done();
	}

	/**
	 * getTableDescription
	 * 
	 * @param table
	 * @return
	 * @throws JCoException
	 */
	private void addTableInfo(SapTable table, IProgressMonitor progressMonitor) throws JCoException {

		this.function_DD_TABL_GET.getImportParameterList().setValue(Constants.JCO_PARAMETER_TABL_NAME, table.getName());
		this.function_DD_TABL_GET.getImportParameterList().setValue(Constants.JCO_PARAMETER_WITHTEXT,
				Constants.JCO_PARAMETER_VALUE_TRUE);
		this.function_DD_TABL_GET.getImportParameterList().setValue(Constants.JCO_PARAMETER_LANGU, this.destination.getLanguage());

		this.function_DD_TABL_GET.execute(this.destination);

		JCoStructure structure_DD02V_WA_A = this.function_DD_TABL_GET.getExportParameterList().getStructure(
				Constants.JCO_RESULTSTRUCTURE_DD02_V_WA_A);
		String description = structure_DD02V_WA_A.getString(Constants.JCO_PARAMETER_DDTEXT);
		String tableName = structure_DD02V_WA_A.getString(Constants.JCO_PARAMETER_TABNAME);
		String devClass = structure_DD02V_WA_A.getString(Constants.JCO_PARAMETER_CONTFLAG);

		if (Utilities.isEmpty(tableName)) {
			table.setExistsOnSAPSystem(false);
			description = Messages.SapLogicalTableCollector_2;
		}
		
		table.setDescription(description);
		table.setDevClass(devClass);
	}


	// TODO this function has been copied from SAPLogicalTableCollector. Maybe
	// we should share the code
	private final SapTable getTextTable(final SapTable table) throws JCoException {

		this.function_TABLE_GET_TEXTTABLE.getImportParameterList().setValue(Constants.JCO_PARAMETER_CHECKTABLE,
				table.getName());

		try {
			table.setTextTablesDone();
			this.function_TABLE_GET_TEXTTABLE.execute(this.destination);
		} catch (AbapException e) {
			if (e.getKey().equalsIgnoreCase(Constants.JCO_ERROR_KEY_TABLE_NOT_FOUND)) {
				return null;
			}
			throw e;
		}
		String textTableName = this.function_TABLE_GET_TEXTTABLE.getExportParameterList().getString(
				Constants.JCO_PARAMETER_TABNAME);

		SapTable textTable = new SapTable(textTableName, null);
		table.addTextTable(textTable);

		return textTable;
	}

	@Override
	public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
		try {
			progressMonitor.beginTask("", 5); //$NON-NLS-1$
			init(new SubProgressMonitor(progressMonitor, 1));
			if (!progressMonitor.isCanceled()) {
				collectTables(new SubProgressMonitor(progressMonitor, 4));
			}
			progressMonitor.done();
		} 
		catch(JCoException jcoExcpt) {
			// possibly a AUTHORITY issue 
			Activator.getLogger().log(Level.SEVERE, Messages.LogExceptionMessage, jcoExcpt);
			throw new InvocationTargetException(jcoExcpt);
		} 
		catch(Exception e) {
			e.printStackTrace();
			Activator.getLogger().log(Level.SEVERE, Messages.LogExceptionMessage, e);
			throw new InvocationTargetException(e);
		} 
	}

	/**
	 * setBlacklist
	 * 
	 * @param blacklist
	 */
	public void setBlacklist(List<String> blacklist) {
		this.blacklist = blacklist;
	}

	/**
	 * setCheckTablesOption
	 * 
	 * @param checkTablesOption
	 */
	public void setCheckTablesOption(TableImporterOptions.CHECKTABLEOPTIONS checkTablesOption) {
		this.checkTablesOption = checkTablesOption;
	}

	/**
	 * setTextTablesOption
	 * 
	 * @param textTablesOption
	 */
	public void setTextTablesOption(boolean textTablesOption) {
		this.textTablesOption = textTablesOption;
	}

}
