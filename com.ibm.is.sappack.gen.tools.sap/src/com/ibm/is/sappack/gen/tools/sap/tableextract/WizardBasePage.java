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
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.wizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.tools.sap.tableextract;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;


public abstract class WizardBasePage extends WizardPage { // implements IJobGeneratorWizardPage {

   // -------------------------------------------------------------------------------------
   // Constants
   // -------------------------------------------------------------------------------------

   // -------------------------------------------------------------------------------------
   // Member Variables
   // -------------------------------------------------------------------------------------
   private Control _FocusDlgField = null;
   
   
   // -------------------------------------------------------------------------------------
   // Abstract methods
   // -------------------------------------------------------------------------------------
   protected abstract Composite createPageControls(final Composite parentComposite);
   

   
   static String copyright() {
      return Copyright.IBM_COPYRIGHT_SHORT;
   }
	

   protected WizardBasePage(String pageName) {
      super(pageName);
   }


   protected WizardBasePage(String pageName, String pageTitle, String pageDescription) {
      super(pageName);
      
      // set page title and page description if available
      if (pageTitle != null) {
         this.setTitle(pageTitle);
      }
      if (pageDescription != null) {
         this.setDescription(pageDescription);
      }
   } // end of WizardBasePage()

   
   public void createControl(final Composite parent) {
      Composite pageComposite;
      
      initializeDialogUnits(parent);
      
      ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
      scrollComposite.setExpandVertical(true);
      scrollComposite.setExpandHorizontal(true);

      // create the page controls
      pageComposite = createPageControls(scrollComposite);
      setControl(scrollComposite);
      scrollComposite.setContent(pageComposite);

      // last not least set the composite minimum size when the scroll bars are to be shown
      pageComposite.setSize(pageComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      scrollComposite.setMinSize(pageComposite.getSize());
   } // end of createControl()


   private void postSetFocusOnDialogField(final Control dialogFieldCtrl) {
      Display curDisplay = getShell().getDisplay();
      
      if (curDisplay != null) {
         curDisplay.asyncExec(new Runnable() {
            
            public void run() {
               dialogFieldCtrl.setFocus();

               // if the focused field is a table ...
               if (dialogFieldCtrl instanceof Table) {
                  Table tbl = (Table) dialogFieldCtrl;
                  
                  // ==> select first row 
                  if (tbl.getItemCount() > 0) {
                     tbl.setSelection(0);
                  }
               } // end of if (dialogFieldCtrl instanceof Table)
            } // end of run()
         });
      }
   } // end of postSetFocusOnDialogField()


   public void setInitialFocusToDlgFld(Control dialogField) {
      _FocusDlgField = dialogField;
   } // end of setInitialFocusToDlgFld()
   
   
   @Override
   public void setVisible(boolean visible) {
      
      super.setVisible(visible);
      
      if (_FocusDlgField != null && visible) {
         postSetFocusOnDialogField(_FocusDlgField);
         
         // do it only the first time
//         _FocusDlgField = null;
      }
   }

} // end of class WizardBasePage
