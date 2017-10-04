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


import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertyInfo;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertyInfoCollection;
import com.ibm.iis.sappack.gen.common.ui.validators.ValidatorBase;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class TableList extends ConfigurationBase implements ITableList {
	public static final String TABLE_LIST_FILE_EXTENSION = "rmtl"; //$NON-NLS-1$


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public TableList(IResource res) throws IOException, CoreException {
		super(res, TABLE_LIST_FILE_EXTENSION);
	}
	
	public Collection<String> getTables() {
		return new HashSet<String>(TablesPage.getTables(this));
	}
	
	public String getBusinessObjectName() {
		return TablesPage.getBusinessObjectName(this);
	}

	@Override
	protected PropertyInfoCollection createPropertyInfoCollection() {
		PropertyInfo pi = new PropertyInfo(TablesPage.KEY_TABLES, Messages.TableList_0, Messages.TableList_1);
		pi.setLocation(TablesPage.PAGE_NAME);

		PropertyInfoCollection result = new PropertyInfoCollection();
		result.addPropertyInfo(pi);
		return result;
	}

	@Override
	public ValidatorBase createValidator() {
		return new TableListValidator();
	}

}
