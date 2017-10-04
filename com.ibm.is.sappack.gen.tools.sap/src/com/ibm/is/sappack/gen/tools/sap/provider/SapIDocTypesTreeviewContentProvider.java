//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2013                                              
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


import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocType;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.SapIDocTypeBrowser;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Segment;
import com.sap.conn.jco.JCoException;


public class SapIDocTypesTreeviewContentProvider implements ITreeContentProvider {

	private String lastErrorMessage = null;
	
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.provider.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public Object[] getChildren(Object parentElement) {
		this.lastErrorMessage = null;

		try {
			if (parentElement instanceof IDocType) {
				List<Segment> rootSegments;
				rootSegments = ((IDocType) parentElement).getRootSegments();
				return rootSegments.toArray(new Segment[rootSegments.size()]);
			}
			else if (parentElement instanceof Segment) {
				List<Segment> segments = ((Segment) parentElement).getChildren();
				return segments.toArray(new Segment[segments.size()]);
			}
		}
		catch (JCoException jcoExcpt) {
			jcoExcpt.printStackTrace();

 			if (jcoExcpt.getMessageNumber().equals("444")) {    //$NON-NLS-1$
 				// "(126) OBJECT_UNKNOWN: OBJECT_UNKNOWN Message 444 of class EA type E"
 				this.lastErrorMessage = Messages.IDocTypeTreeViewer_AuthMissing;
 			}
 			else {
 				this.lastErrorMessage = jcoExcpt.getMessage();
 			}
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.TitleError, this.lastErrorMessage);

			throw new RuntimeException(jcoExcpt);
		}

		return null;
	}


	public String getLastErrorMessage() {
		return(this.lastErrorMessage);
	}

	public Object getParent(Object element) {
		if (element instanceof Segment) {
			Segment parentSegment;
			parentSegment = ((Segment) element).getParentSegment();
			
			if (parentSegment == null) {
				return ((Segment) element).getIDocType();
			}
			
			return parentSegment;
		}

		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IDocType) {
			return true;
		}
		
		if (element instanceof Segment) {
			return ((Segment) element).getChildren().size() > 0;
		}
		
		return false;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IDocType[]) {
			IDocType[] sapIDocTypes = (IDocType[]) inputElement;
			
			return sapIDocTypes;
		}
		else if (inputElement instanceof Segment[]) {
			Segment[] sapIDocTypeSegments = (Segment[]) inputElement;
			
			return sapIDocTypeSegments;
		}
		
		return null;
	}
	
	public void dispose() {
		// Nothing to be done here
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// Nothing to be done here
	}

	public static IDocType[] createInitialTreeContent(SapSystem selectedSapSystem, String filter, boolean isExtendedType, String release)
	      throws JCoException {
		SapIDocTypeBrowser importer = new SapIDocTypeBrowser(selectedSapSystem, isExtendedType, release);
		List<IDocType> idocTypes = importer.listIDoctypes(filter.toUpperCase());

		IDocType[] ita = idocTypes.toArray(new IDocType[idocTypes.size()]);
		Arrays.sort(ita, new Comparator<IDocType>() {
			public int compare(IDocType idt1, IDocType idt2) {
				return idt1.getName().compareTo(idt2.getName());
			}

		});
		return ita;
	}
}
