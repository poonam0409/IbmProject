//	---------------------------------------------------------------------------  
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
// Module Name : com.ibm.is.sappack.gen.tools.sap.dialog
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.dialog;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.util.DSFolder;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;


public class FolderTreeDialog extends Dialog {

	private List<DSFolder>  folderList;
   private Tree            folderTree;
   private String          selectedFolderName;
   private String          prevFolderName;

   private static Image    folderImage;
	

   static {
      ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
      folderImage                 = imageRegistry.get(Constants.ICON_ID_FOLDER);
   } // end of static


	static String copyright() { 
	   return com.ibm.is.sappack.gen.tools.sap.dialog.Copyright.IBM_COPYRIGHT_SHORT; 
	}


	public FolderTreeDialog(Shell parentShell, List<DSFolder> folderList, String prevFolderName) {
		super(parentShell);
		this.folderList     = folderList;
		this.prevFolderName = prevFolderName;
	} // end of FolderTreeDialog


   private void buildTreeFromList(Composite dlgComposite) {
      TreeItem             newTreeItem;
      TreeItem             selectedTreeItem;
      GridData             treeGridData;
      Map<String, Object>  treeMap;
      Object               parentTreeObj;
      
      this.folderTree = new Tree(dlgComposite, SWT.SINGLE| SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
      treeGridData    = new GridData(SWT.FILL, SWT.FILL, true, true);
      treeGridData.heightHint = 300;
      treeGridData.widthHint  = 400;
      this.folderTree.setLayoutData(treeGridData);
      treeMap          = new HashMap<String, Object>();
      selectedTreeItem = null;
      
      for (DSFolder curFolder: folderList) {
         // check if there is already a parent 
         parentTreeObj = treeMap.get(curFolder.getParentId());

         if (parentTreeObj == null) {
            parentTreeObj = this.folderTree;
         }

         if (parentTreeObj instanceof Tree) {
            newTreeItem = new TreeItem((Tree) parentTreeObj, 0);
         }
         else {
            newTreeItem = new TreeItem((TreeItem) parentTreeObj, 0);
         }
         
         newTreeItem.setText(curFolder.getName());
         newTreeItem.setData(curFolder);
         newTreeItem.setImage(folderImage);
            
         treeMap.put(curFolder.getId(), newTreeItem);
         
         // select item if matches with previous selected
         if (this.prevFolderName != null) {
            if (this.prevFolderName.equalsIgnoreCase(curFolder.getDirectoryPath())) {
               this.folderTree.select(newTreeItem);
               selectedTreeItem = newTreeItem;
            }
         } // end of if (this.prevFolderName != null)
      } // end of for (DSFolder curFolder: folderList)

      // if the selected folder has children --> expand the the folder (only this)
      if (selectedTreeItem != null && selectedTreeItem.getItemCount() > 0) {
         selectedTreeItem.setExpanded(true);
      }
   } // end of buildTreeFromList()


   protected void configureShell(Shell shell) {
      super.configureShell(shell);
      shell.setText(Messages.FolderTreeDialog_title);
   }


   protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		
		buildTreeFromList(composite);
//      composite.setSize(folderTree.computeSize(SWT.DEFAULT, SWT.DEFAULT, false));

		applyDialogFont(composite);

		return(composite);
	} // end of createDialogArea()


	protected void buttonPressed(int buttonId) {
      DSFolder curFolder;
      TreeItem selectedItemsArr[];
      
		if (buttonId == IDialogConstants.OK_ID) {
	      selectedItemsArr = this.folderTree.getSelection();
	      if (selectedItemsArr.length > 0) {
	         curFolder               = (DSFolder) selectedItemsArr[0].getData();
	         this.selectedFolderName = curFolder.getDirectoryPath();
	      }
		} 
		else {
			this.selectedFolderName = null;
		}
		
      super.buttonPressed(buttonId);
	} // end of buttonPressed()


	public String getSelectedFolder() {
	   return(this.selectedFolderName);
	} // end of getSelectedFolder()


	public static final String getSelectedFolderName(Shell shell, List<DSFolder> folderList, String prevFolderName) {
      FolderTreeDialog dialog;
	   String folderName = null;
	   
	   if (folderList != null) {
         dialog = new FolderTreeDialog(shell, folderList, prevFolderName);
         dialog.setBlockOnOpen(true);
         
         if (dialog.open() == Window.OK) {
            folderName = dialog.getSelectedFolder();
         }
	   }
		
		return(folderName);
	} // end of getSelectedFolderName()


//   protected boolean isResizable() {
//      return(true);
//   }
	
} // end of class FolderTreeDialog
