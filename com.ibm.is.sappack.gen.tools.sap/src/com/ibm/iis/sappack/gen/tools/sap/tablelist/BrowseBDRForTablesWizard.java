//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.tablelist
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.tablelist;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.Wizard;

import com.ibm.iis.sappack.gen.common.ui.connections.CWDBConnection;
import com.ibm.iis.sappack.gen.common.ui.wizards.SelectCWDBConnectionWizardPage;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public class BrowseBDRForTablesWizard extends Wizard {
//	SelectConnectionProfileForCWDBWizardPage connectionProfilePage;
	private SelectCWDBConnectionWizardPage selectConnectionPage;
	private SelectCWDBTablesWizardPage tablesSelectionPage;

	private List<String> selectedTables;
	private String rloutDescription;
	private String selectedBusinessObjectName; 


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public BrowseBDRForTablesWizard() {
		super();
		this.setNeedsProgressMonitor(true);
		this.setDialogSettings(Activator.getDefault().getDialogSettings() );
		this.setWindowTitle(Messages.BrowseBDRForTablesWizard_0);
		this.selectConnectionPage = new SelectCWDBConnectionWizardPage("selectcwdbconnectionpage",               //$NON-NLS-1$
			                                                            Messages.BrowseBDRForTablesWizard_1,
			                                                            Messages.BrowseBDRForTablesWizard_2,
			                                                            null) { 

			@Override
			public boolean nextPressedImpl() {
				boolean b = super.nextPressedImpl();
				if (!b) {
					return false;
				}
				final Map<String, Map<String, Map<String, List<String>>>> leg2rlout2BO2TablesMap = new HashMap<String, Map<String,Map<String,List<String>>>>();
				
				CWDBConnection cwdbConn = getCWDBConnection();
				/*
				boolean isConnected = cwdbConn.ensureIsConnected();
				if (!isConnected) {
					setErrorMessage(MessageFormat.format(Messages.BrowseBDRForTablesWizard_3, cwdbConn.getName()));
					return false;
				}
				*/
				Connection conn = cwdbConn.getJDBCConnection();
				/*
				if (conn == null) {
					setErrorMessage(Messages.BrowseBDRForTablesWizard_4);
					return false;
				}
				*/
				ResultSet rs = null;
				try {
					String sqlRlouts = "SELECT DISTINCT T.CW_RLOUT, T.CW_LOB, T.CW_LEGACY_ID, T.SAP_TABNAME, S.DESCRIPTION "+ //  //$NON-NLS-1$
	                   " FROM AUX.SAP_DATATABLES_CONFIG T, AUX.LEGACY_SYSTEM S "+ //$NON-NLS-1$
	                   " WHERE S.CW_LEGACY_ID = T.CW_LEGACY_ID"; //$NON-NLS-1$

					PreparedStatement prep = conn.prepareStatement(sqlRlouts);
					rs = prep.executeQuery();
					while (rs.next()) {
						String boName = rs.getString(2);
						String tableName = rs.getString(4);
						String rlout = rs.getString(1);
						
						String legacyShortName = rs.getString(3);  // use legacy ID as short name
						String legacyDescription = rs.getString(5);
						String legacySystemName = legacyShortName;
						if (legacyDescription != null) {
							legacySystemName += " - " + legacyDescription; //$NON-NLS-1$
						}
						
						Map<String, Map<String, List<String>>> rlout2BO2TablesMap = leg2rlout2BO2TablesMap.get(legacySystemName);
						if (rlout2BO2TablesMap == null) {
							rlout2BO2TablesMap = new HashMap<String, Map<String,List<String>>>();
							leg2rlout2BO2TablesMap.put(legacySystemName, rlout2BO2TablesMap);
						}
						Map<String,List<String>> bo2TablesMap = rlout2BO2TablesMap.get(rlout);
						if (bo2TablesMap == null) {
							bo2TablesMap = new HashMap<String, List<String>>();
							rlout2BO2TablesMap.put(rlout, bo2TablesMap);
						}
						List<String> tables = bo2TablesMap.get(boName);
						if (tables == null) {
							tables = new ArrayList<String>();
							bo2TablesMap.put(boName, tables);
						}
						tables.add(tableName);
					}
				} catch (Exception exc) {
					setErrorMessage(Messages.BrowseBDRForTablesWizard_6);
					Activator.logException(exc);
					return false;
				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException e) {
							Activator.logException(e);
						}
					}
				}
				// don't close JBDC connection because it is managed through IDA

			//	tablesSelectionPage.setBO2TableListMap(bo2TablesMap);
				tablesSelectionPage.setLegacyID2Rlout2BO2TablesMap(leg2rlout2BO2TablesMap);
//				tablesSelectionPage.setRlout2BO2TablesMap(rlout2BO2TablesMap);
				return true;
			}

		};
		addPage(this.selectConnectionPage);
		this.tablesSelectionPage = new SelectCWDBTablesWizardPage();
		addPage(this.tablesSelectionPage);
	}

	public String getSelectedBusinessObjectName() {
		return this.selectedBusinessObjectName;
	}
	
	public List<String> getSelectedTables() {
		return this.selectedTables;
	}
	
	public String getRloutDescription() {
		return this.rloutDescription;
	}

	@Override
	public boolean performFinish() {
		this.selectedBusinessObjectName = this.tablesSelectionPage.getSelectedBusinessObject();
		
		this.selectedTables = this.tablesSelectionPage.getSelectedTables();
		String tableListString = SelectCWDBTablesWizardPage.getTableListAsString(selectedTables, ", "); //$NON-NLS-1$
		
		String rlout = this.tablesSelectionPage.getSelectedRlout();
		String date = DateFormat.getDateInstance().format(new Date());
		this.rloutDescription = MessageFormat.format(Messages.BrowseBDRForTablesWizard_5, date, rlout, this.tablesSelectionPage.getSelectedBusinessObject(), tableListString);
		
		return true;
	}

}
