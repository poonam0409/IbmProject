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
// Module Name : com.ibm.is.sappack.gen.tools.sap.provider
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.provider;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.sap.conn.jco.ext.DestinationDataProvider;

public class SapSystemsListProvider extends LabelProvider implements IStructuredContentProvider, IBaseLabelProvider {

	private static HashSet<SapSystem> sapSystems;

	public static final String SAP_SYSTEM_NAME = "sap.system.name"; //$NON-NLS-1$
	
	public static final String SAP_IS_MSG_SRV = "sap.is_message_server"; //$NON-NLS-1$

	private static final String SAP_SYSTEMS = "sap_systems"; //$NON-NLS-1$

	private static final String SAP_SYSTEM = "sap_system"; //$NON-NLS-1$
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.sap.provider.Copyright.IBM_COPYRIGHT_SHORT; }

	public SapSystemsListProvider() {
		read();
	}

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement) {
		HashSet<SapSystem> hashSet = (HashSet<SapSystem>) inputElement;
		return hashSet.toArray();
	}
	
	public SapSystem getSapSystemByName(String sapSystemName) {
		Iterator<SapSystem> iterator = sapSystems.iterator();
		while(iterator.hasNext()) {
			SapSystem sapSystem = (SapSystem) iterator.next();
			if (sapSystem.getName().equals(sapSystemName)) {
				return sapSystem;
			}
		}
		return null;
	}
	
	public void dispose() {
		// Nothing to be done here
	}

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// Nothing to be done here
	}

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		return ((SapSystem) element).getName();
	}

	public Collection<SapSystem> getSapSystems() {
		return sapSystems;
	}

	public void remove(SapSystem sapSystem) {
		sapSystems.remove(sapSystem);
	}

	public void add(SapSystem sapSystem) {
		if (sapSystems.contains(sapSystem)) {
			sapSystems.remove(sapSystem);
		}
		sapSystems.add(sapSystem);
	}

	public int getSize() {
		return sapSystems.size();
	}

	public void save() {
		try {
			XMLMemento memento = XMLMemento.createWriteRoot(SAP_SYSTEMS);
			Iterator<SapSystem> iterator = sapSystems.iterator();
			while (iterator.hasNext()) {
				SapSystem sapSystem = iterator.next();
				XMLMemento childMemento = (XMLMemento) memento.createChild(SAP_SYSTEM);

				if (sapSystem.isMessageServerSystem()) {
					childMemento.putBoolean(SapSystemsListProvider.SAP_IS_MSG_SRV, true);
					childMemento.putString(DestinationDataProvider.JCO_MSHOST, sapSystem.getHost());
					childMemento.putString(DestinationDataProvider.JCO_R3NAME, sapSystem.getSystemId());
					childMemento.putString(DestinationDataProvider.JCO_GROUP, sapSystem.getGroupName());
					childMemento.putString(DestinationDataProvider.JCO_CLIENT, sapSystem.getClientId() + Constants.EMPTY_STRING);					
				} else {
					childMemento.putBoolean(SapSystemsListProvider.SAP_IS_MSG_SRV, false);
					childMemento.putString(DestinationDataProvider.JCO_ASHOST, sapSystem.getHost());
					childMemento.putString(DestinationDataProvider.JCO_SYSNR, sapSystem.getSystemNumber() + Constants.EMPTY_STRING);
					childMemento.putString(DestinationDataProvider.JCO_CLIENT, sapSystem.getClientId() + Constants.EMPTY_STRING);
				}
				
				// Don't store the password!
				// childMemento.putString(DestinationDataProvider.JCO_PASSWD,
				// sapSystem.getPassword());
				
				childMemento.putString(DestinationDataProvider.JCO_SAPROUTER, sapSystem.getRouterString());
				childMemento.putString(DestinationDataProvider.JCO_USER, sapSystem.getUsername());
            childMemento.putString(DestinationDataProvider.JCO_LANG, sapSystem.getUserLanguage());
				childMemento.putString(SapSystemsListProvider.SAP_SYSTEM_NAME, sapSystem.getName());
			}
			FileWriter fileWriter = new FileWriter(Activator.getDefault().getStateLocation().append(SAP_SYSTEMS).toFile());
			memento.save(fileWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void read() {
		if (sapSystems != null) {
			return;
		}
		
		sapSystems = new HashSet<SapSystem>();
		
		try {
			File file = Activator.getDefault().getStateLocation().append(SAP_SYSTEMS).toFile();
			if (!file.exists()) {
				return;
			}
			FileReader fileReader = new FileReader(file);

			XMLMemento memento = XMLMemento.createReadRoot(fileReader);
			IMemento[] childMementos = memento.getChildren(SAP_SYSTEM);
			for (int i = 0; i < childMementos.length; i++) {
				IMemento childMemento = childMementos[i];
				SapSystem sapSystem = new SapSystem(childMemento.getString(SAP_SYSTEM_NAME));
				
				boolean isMessageServer = (null == childMemento.getBoolean(SapSystemsListProvider.SAP_IS_MSG_SRV)) ? false : childMemento.getBoolean(SapSystemsListProvider.SAP_IS_MSG_SRV).booleanValue();
				
				if (isMessageServer) {
					sapSystem.setMessageServerSystem(true);
					sapSystem.setGroupName(childMemento.getString(DestinationDataProvider.JCO_GROUP));
					sapSystem.setSystemId(childMemento.getString(DestinationDataProvider.JCO_R3NAME));
					sapSystem.setHost(childMemento.getString(DestinationDataProvider.JCO_MSHOST));
					sapSystem.setClientId(childMemento.getInteger(DestinationDataProvider.JCO_CLIENT).intValue());
				} else {
					sapSystem.setMessageServerSystem(false);
					sapSystem.setClientId(childMemento.getInteger(DestinationDataProvider.JCO_CLIENT).intValue());
					sapSystem.setHost(childMemento.getString(DestinationDataProvider.JCO_ASHOST));
					sapSystem.setSystemNumber(childMemento.getString(DestinationDataProvider.JCO_SYSNR));
				}

				// Don't load the password!
				// sapSystem.setPassword(childMemento.getString(DestinationDataProvider.JCO_PASSWD));
				sapSystem.setRouterString(childMemento.getString(DestinationDataProvider.JCO_SAPROUTER));
				sapSystem.setUsername(childMemento.getString(DestinationDataProvider.JCO_USER));
            sapSystem.setUserLanguage(childMemento.getString(DestinationDataProvider.JCO_LANG));
				sapSystems.add(sapSystem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
