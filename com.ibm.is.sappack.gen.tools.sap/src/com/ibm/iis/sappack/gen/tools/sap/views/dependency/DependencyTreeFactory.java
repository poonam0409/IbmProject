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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.views.dependency
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.views.dependency;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.Document;

import com.ibm.db.models.logical.Entity;
import com.ibm.db.models.logical.Package;
import com.ibm.iis.sappack.gen.common.ui.util.FileHelper;
import com.ibm.iis.sappack.gen.tools.sap.idocseglist.IDocSegmentList;
import com.ibm.iis.sappack.gen.tools.sap.importer.MetaDataImporter;
import com.ibm.iis.sappack.gen.tools.sap.rmconf.RMConfiguration;
import com.ibm.iis.sappack.gen.tools.sap.tablelist.TableList;
import com.ibm.is.sappack.gen.common.DBSupport;
import com.ibm.is.sappack.gen.common.DBSupport.DBInstance;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;
import com.ibm.is.sappack.gen.tools.sap.utilities.LogicalTableNameConverter;


public class DependencyTreeFactory {
	private IContainer container;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public DependencyTreeFactory(IContainer container) throws CoreException, IOException {
		this.container = container;
		rmConfFiles = new HashMap<String, IFile>();
		socFiles = new HashMap<String, IFile>();
		ldmFiles = new HashMap<String, IFile>();
		dbmFiles = new HashMap<String, List<IFile>>();
		this.importRunNodes = new HashMap<String, ImportRun>();
		//	this.sapSystems = new HashMap<String, SAPConnectionStub>();
		readAllFiles();
		//	readAllSAPConnections();
	}

	Map<String, IFile> rmConfFiles;
	Map<String, IFile> socFiles;
	Map<String, IFile> ldmFiles;
	Map<String, List<IFile>> dbmFiles;
	Map<String, ImportRun> importRunNodes;

	//	Map<String, SAPConnectionStub> sapSystems;

	public static class ImportRun {
		String socID;
		String rmconfID;
		String ldmID;
		String sapSystem;
		Date timeOfRun;
	}

	public static class SAPConnectionStub {
		String name;

		public SAPConnectionStub(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	boolean downward = true;

	void readAllFiles() throws CoreException, IOException {
		traverseAllFiles(this.container);
	}

	String getRMConfID(IFile f) throws IOException, CoreException {
		return FileHelper.getID(f, RMConfiguration.RMCONF_FILE_EXTENSION);
	}

	//	String getSOCID(IFile f) throws IOException, CoreException {
	//		return FileHelper.getID(f, SAPObjectCollection.SOC_FILE_EXTENSION);
	//	}

	String getTableListID(IFile f) throws IOException, CoreException {
		return FileHelper.getID(f, TableList.TABLE_LIST_FILE_EXTENSION);
	}

	String getIDocSegmentListID(IFile f) throws IOException, CoreException {
		return FileHelper.getID(f, IDocSegmentList.IDOC_SEGMENT_LIST_FILE_EXTENSION);
	}

	Map<IFile, String> ldmInfos = new HashMap<IFile, String>();

	String getLDMInfo(IFile f) throws IOException {
		String s = ldmInfos.get(f);
		if (s != null) {
			return s;
		}

		String id = getLDMID(f);
		if (id != null) {
			this.ldmInfos.put(f, id);
		}
		return id;
	}

	public static String getLDMID(IFile f) {
		try {
			LdmAccessor originalLDMAccessor = new LdmAccessor(f, LogicalTableNameConverter.createLogicalTableNameConverter());

			List<Entity> entities = originalLDMAccessor.getAllEntities();
			for (Entity e : entities) {
				String ldmID = LdmAccessor.getAnnotationValue(e, MetaDataImporter.ANNOT_LDM_ID);
				if (ldmID != null) {
					return ldmID;
				}
			}
		} catch (Exception exc) {
			Activator.logException(exc);
		}
		return null;

	}

	public static String getDBMID(IFile f) {
		try {
			DocumentBuilderFactory vDocBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder vDocBuilder = vDocBuilderFactory.newDocumentBuilder();
			Document vModelDocument = vDocBuilder.parse(f.getContents());
			DBInstance dbinst = DBSupport.createDBInstance(vModelDocument);
			String ldmID = dbinst.getLDMID();
			return ldmID;
		} catch (Exception e) {
			Activator.logException(e);
		}
		return null;
	}

	private String addImportRuns(IFile logicalDataModel) throws IOException {
		String ldmID = getLDMInfo(logicalDataModel);
		if (ldmID != null) {
			LdmAccessor originalLDMAccessor = new LdmAccessor(logicalDataModel, LogicalTableNameConverter.createLogicalTableNameConverter());
			Package rootPackage = originalLDMAccessor.getRootPackage();
			Map<String, String> annotations = LdmAccessor.getAnnotations(rootPackage);
			for (Map.Entry<String, String> entry : annotations.entrySet()) {
				String key = entry.getKey();
				if (key.startsWith(MetaDataImporter.ANNOT_IMPORT_RUN_ID)) {
					String runid = key.substring(MetaDataImporter.ANNOT_IMPORT_RUN_ID.length());
					String value = entry.getValue();
					StringTokenizer tok = new StringTokenizer(value, MetaDataImporter.RUN_ID_SEP);
					String socID = tok.nextToken();
					String rmConfID = tok.nextToken();
					String sapSystem = tok.nextToken();
					ImportRun run = new ImportRun();
					run.socID = socID;
					run.rmconfID = rmConfID;
					run.ldmID = ldmID;
					run.timeOfRun = new Date(Long.parseLong(runid));
					run.sapSystem = sapSystem;
					//					Node n = this.graph.addNode(run);
					this.importRunNodes.put(runid, run);
				}
			}
		}
		return ldmID;
	}

	private void traverseAllFiles(IContainer cont) throws CoreException, IOException {
		if (cont instanceof IProject && !((IProject) cont).isOpen()) {
			return;
		}
		IResource[] resources = cont.members();
		for (IResource res : resources) {
			if (res instanceof IContainer) {
				traverseAllFiles((IContainer) res);
			} else if (res instanceof IFile) {
				boolean b = true;
				if (b) {
					IFile f = (IFile) res;
					String s = f.getName();
					if (s.endsWith(RMConfiguration.RMCONF_FILE_EXTENSION)) {
						String rmConfID = getRMConfID(f);
						if (rmConfID != null) {
							this.rmConfFiles.put(rmConfID, f);
						}
					} else if (s.endsWith(TableList.TABLE_LIST_FILE_EXTENSION)) {
						String socID = getTableListID(f);
						if (socID != null) {
							this.socFiles.put(socID, f);
						}
					} else if (s.endsWith(IDocSegmentList.IDOC_SEGMENT_LIST_FILE_EXTENSION)) {
						String socID = getIDocSegmentListID(f);
						if (socID != null) {
							this.socFiles.put(socID, f);
						}
					} else if (s.endsWith("ldm")) { //$NON-NLS-1$
						String ldmID = addImportRuns(f);
						if (ldmID != null) {
							this.ldmFiles.put(ldmID, f);
						}
					} else if (s.endsWith("dbm")) { //$NON-NLS-1$
						String dbmID = getDBMID(f);
						if (dbmID != null) {
							List<IFile> l = this.dbmFiles.get(dbmID);
							if (l == null) {
								l = new ArrayList<IFile>();
								this.dbmFiles.put(dbmID, l);
							}
							l.add(f);
						}
					}
				}
			}
		}
	}

	private void addLDM(TreeNode parent, IFile ldmFile) {
		TreeNode tn = new TreeNode(parent, ldmFile);
		if (downward) {
			addDBMsForLDM(tn, ldmFile);
		}
	}

	private void addDBMsForLDM(TreeNode parent, IFile ldmFile) {
		for (Map.Entry<String, IFile> e : this.ldmFiles.entrySet()) {
			if (e.getValue().equals(ldmFile)) {
				String s = e.getKey();
				List<IFile> dbms = this.dbmFiles.get(s);
				if (dbms != null) {
					for (IFile dbm : dbms) {
						new TreeNode(parent, dbm);
					}
				}
			}
		}
	}

	private void addSOC(TreeNode parent, IFile ldmFile) {
		new TreeNode(parent, ldmFile);
	}

	private void addRMConf(TreeNode parent, IFile ldmFile) {
		new TreeNode(parent, ldmFile);
	}

	private void addSAP(TreeNode parent, SAPConnectionStub sap) {
		new TreeNode(parent, sap);
	}

	private TreeNode addImportRun(TreeNode parent, ImportRun ir) {
		boolean showLDM = true;
		boolean showSOC = true;
		boolean showRMCONF = true;
		boolean showSAP = true;
		if (parent != null) {
			Object o = parent.getInfo();
			if (o instanceof IFile) {
				String name = ((IFile) o).getName();
				if (name.endsWith("ldm")) { //$NON-NLS-1$
					showLDM = false;
				}
				if (name.endsWith(TableList.TABLE_LIST_FILE_EXTENSION) || name.endsWith(IDocSegmentList.IDOC_SEGMENT_LIST_FILE_EXTENSION)) {
					showSOC = false;
				}
				if (name.endsWith(RMConfiguration.RMCONF_FILE_EXTENSION)) {
					showRMCONF = false;
				}
			} else if (o instanceof SAPConnectionStub) {
				showSAP = false;
			}
		}

		TreeNode rn = new TreeNode(parent, ir);
		if (showSOC) {
			IFile soc = this.socFiles.get(ir.socID);
			if (soc != null) {
				addSOC(rn, soc);
			}
		}
		if (showRMCONF) {
			IFile rmConf = this.rmConfFiles.get(ir.rmconfID);
			if (rmConf != null) {
				addRMConf(rn, rmConf);
			}
		}

		if (showSAP) {
			SAPConnectionStub sap = new SAPConnectionStub(ir.sapSystem);
			if (sap != null) {
				addSAP(rn, sap);
			}
		}

		if (showLDM) {
			IFile ldm = this.ldmFiles.get(ir.ldmID);
			if (ldm != null) {
				addLDM(rn, ldm);
			}
		}

		return rn;
	}

	void addLDMUpward(TreeNode ldmNode) throws IOException {
		IFile f = (IFile) ldmNode.getInfo();
		String ldmID = getLDMInfo(f);
		for (Map.Entry<String, ImportRun> e : this.importRunNodes.entrySet()) {
			ImportRun ir = e.getValue();
			if (ir.ldmID.equals(ldmID)) {
				addImportRun(ldmNode, ir);
			}
		}

	}

	public TreeNode createDependencyTree(Object startObject, boolean downward) throws CoreException, IOException {
		this.downward = downward;
		if (startObject instanceof IFile) {
			IFile f = (IFile) startObject;
			TreeNode root = new TreeNode(null);
			root.setInfo(f);
			if (f.getName().endsWith("ldm")) { //$NON-NLS-1$
				if (downward) {
					addDBMsForLDM(root, f);
				} else {
					addLDMUpward(root);
				}
			} else if (f.getName().endsWith(RMConfiguration.RMCONF_FILE_EXTENSION)) {
				String rmconfID = getRMConfID(f);
				if (downward) {
					for (Map.Entry<String, ImportRun> e : this.importRunNodes.entrySet()) {
						ImportRun ir = e.getValue();
						if (ir.rmconfID.equals(rmconfID)) {
							addImportRun(root, ir);
						}
					}
				} else {
					// do nothing
				}
			} else if (f.getName().endsWith(TableList.TABLE_LIST_FILE_EXTENSION)) {
				String socID = getTableListID(f);
				if (downward) {
					for (Map.Entry<String, ImportRun> e : this.importRunNodes.entrySet()) {
						ImportRun ir = e.getValue();
						if (ir.socID.equals(socID)) {
							addImportRun(root, ir);
						}
					}
				} else {
					// do nothing
				}
			} else if (f.getName().endsWith(IDocSegmentList.IDOC_SEGMENT_LIST_FILE_EXTENSION)) {
				String socID = getIDocSegmentListID(f);
				if (downward) {
					for (Map.Entry<String, ImportRun> e : this.importRunNodes.entrySet()) {
						ImportRun ir = e.getValue();
						if (ir.socID.equals(socID)) {
							addImportRun(root, ir);
						}
					}
				} else {
					// do nothing
				}
			} else if (f.getName().endsWith("dbm")) { //$NON-NLS-1$
				String dbmID = getDBMID(f);
				if (downward) {
					// do nothing
				} else {
					IFile ldmFile = this.ldmFiles.get(dbmID);
					if (ldmFile != null) {
						TreeNode ldmNode = new TreeNode(root, ldmFile);
						addLDMUpward(ldmNode);
					}
				}
			} else {
				return null;
			}

			return root;
		} else if (startObject instanceof ImportRun) {
			ImportRun ir = (ImportRun) startObject;
			if (downward) {
				return addImportRun(null, ir);
			} else {
				return addImportRun(null, ir);
			}
		} else if (startObject instanceof SAPConnectionStub) {
			SAPConnectionStub sapConn = (SAPConnectionStub) startObject;
			TreeNode root = new TreeNode(null, sapConn);
			if (downward) {
				for (Map.Entry<String, ImportRun> e : this.importRunNodes.entrySet()) {
					ImportRun ir = e.getValue();
					if (ir.sapSystem.equals(sapConn.getName())) {
						addImportRun(root, ir);
					}
				}
			} else {
				// do nothing
			}
			return root;
		}
		return null;
	}
}
