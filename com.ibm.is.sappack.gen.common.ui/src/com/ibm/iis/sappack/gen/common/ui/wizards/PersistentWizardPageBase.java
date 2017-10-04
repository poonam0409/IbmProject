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
// Module Name : com.ibm.iis.sappack.gen.common.ui.wizards
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.wizards;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.ibm.is.sappack.gen.common.ui.Activator;


public abstract class PersistentWizardPageBase extends WizardPage implements INextActionWizardPage {
	private Map<String, Widget> propToWidgetMap;
   private Control             focusDlgField        = null;

	
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	protected PersistentWizardPageBase(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		this.propToWidgetMap = new HashMap<String, Widget>();
	}

	protected void configureTextForProperty(Text t, String prop) {
		this.propToWidgetMap.put(prop, t);
	}

	protected void configureComboForProperty(Combo c, String prop) {
		this.propToWidgetMap.put(prop, c);
	}

	
	protected void configureCheckBoxForProperty(Button b, String prop) {
		this.propToWidgetMap.put(prop, b);
	}
	
	@Override
	final public void createControl(Composite parent) {
		Composite pageComposite;

		initializeDialogUnits(parent);

		ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		scrollComposite.setExpandVertical(true);
		scrollComposite.setExpandHorizontal(true);
		scrollComposite.setSize(600, 500);

//		scrollComposite.setLayout(new GridLayout(1, false));
//		scrollComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// create the page controls
		pageComposite = createControlImpl(scrollComposite);
		load();
		setControl(scrollComposite);
		scrollComposite.setContent(pageComposite);

		// last not least set the composite minimum size when the scroll bars are to be shown
		pageComposite.setSize(pageComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrollComposite.setMinSize(pageComposite.getSize());
		
		// post create control action
		updateControlsAfterCreation();
	}


	protected void updateControlsAfterCreation() {
		// overwrite this method in subclasses if needed
	}


	
	protected abstract Composite createControlImpl(Composite parent);

	protected void load() {
		IDialogSettings dialogSettings = this.getDialogSettings();
		for (Map.Entry<String, Widget> entry : propToWidgetMap.entrySet()) {
			String prop = entry.getKey();
			Widget w = entry.getValue();
			if (w instanceof Text) {
				String value = dialogSettings.get(prop);
				Text t = (Text) w;
				if (value != null) {
					t.setText(value);
					t.selectAll();
				}
			} else if (w instanceof Button) {
				boolean value = dialogSettings.getBoolean(prop);
				Button b = (Button) w;
				b.setSelection(value);
			} else if (w instanceof Combo) {
				String value = dialogSettings.get(prop);
				Combo c = (Combo) w;
				if (value != null) {
					c.setText(value);
				}
			}
		}
	}
	
	protected void save() {
		IDialogSettings dialogSettings = this.getDialogSettings();
		for (Map.Entry<String, Widget> entry : propToWidgetMap.entrySet()) {
			String prop = entry.getKey();
			Widget w = entry.getValue();
			if (w instanceof Text) {
				Text t = (Text) w;
				dialogSettings.put(prop, t.getText());
			} else if (w instanceof Button) {
				Button b = (Button) w;
				dialogSettings.put(prop, b.getSelection());
			} else if (w instanceof Combo) {
				Combo c = (Combo) w;
				dialogSettings.put(prop, c.getText());
			}
		}
	}

	@Override
	final public boolean nextPressed() {
		boolean result = nextPressedImpl();
		if (!result) {
			return false;
		}
		save();
		return true;
	}


	public abstract boolean nextPressedImpl();


	protected void handleException(Throwable t) {
		String errMsg;

		if (t.getMessage() == null) {
			errMsg = t.getClass().getName();
		}
		else {
//			errMsg = t.getMessage() + " (" + t.getClass().getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			errMsg = t.getMessage();
		}
//		setErrorMessage(t.getClass().getName() + ": " + t.getMessage());
		setErrorMessage(errMsg);
		Activator.logException(t);
	}

	
   public void setInitialFocusToDlgFld(Control dialogField) {
      focusDlgField = dialogField;
   } // end of setInitialFocusToDlgFld()


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


   @Override
   public void setVisible(boolean visible) {
      
      super.setVisible(visible);
      
      if (focusDlgField != null && visible) {
         postSetFocusOnDialogField(focusDlgField);
         
         // do it only the first time
//         _FocusDlgField = null;
      }
   }
   
}
