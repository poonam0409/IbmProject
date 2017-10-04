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
// Module Name : com.ibm.is.sappack.gen.tools.sap
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap;


import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ibm.iis.sappack.gen.tools.sap.rmconf.RMConfiguration.SupportedDB;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.ImporterOptionsBase;


public class PreferencePageRMSettings extends org.eclipse.jface.preference.PreferencePage implements IWorkbenchPreferencePage {
   static class DBSetting {
      private SupportedDB _DB;
      private int         _IdMaxLen;
      
      public DBSetting(SupportedDB db, int maxLen) {
         _DB       = db;
         _IdMaxLen = maxLen;
      }
      
      public SupportedDB getDB() {
         return(_DB);
      }
      
      public static DBSetting getDB(String dbName) {
         DBSetting retDBImpSetting = null;
         
         if (dbName != null) {
            if (dbName.equals(Messages.TargetDBPage_It_DB2)) {
               retDBImpSetting = TRG_DB_DB2;
            }
            else if (dbName.equals(Messages.TargetDBPage_It_DB2_CW)) {
                    retDBImpSetting = TRG_DB_DB2CW;
            }
            else if (dbName.equals(Messages.TargetDBPage_It_ORA)) {
                    retDBImpSetting = TRG_DB_ORACLE;
            }
            else if (dbName.equals(Messages.TargetDBPage_It_CUST)) {
                    retDBImpSetting = TRG_DB_CUSTOM;
            }
         } // end of if (dbName != null)
         
         return(retDBImpSetting);
      }
      
      public int getMaxLength() {
         return(_IdMaxLen);
      }
      
      public boolean isCustom() {
         return(_DB == SupportedDB.Custom);
      }
      
      public void setMaxLength(int maxLen) {
         if (isCustom()                                   &&
             maxLen >= Constants.DB_IDENTIFIER_MIN_LENGTH && 
             maxLen <= Constants.DB_IDENTIFIER_MAX_LENGTH) {
            _IdMaxLen = maxLen;
         }
      }
      
      public String toString() {
         String retString;

         if (_DB == SupportedDB.Custom) {
            retString = TRG_DB_CUSTOM_NAME;
         }
         else {
            retString = _DB.toString();
         }
            
         return(retString);
      }
      
   } // end of class DBImportSetting
   
   
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
	private static final String PREF_PAGE_RM_TARGET_DB  = "PREF_PG_RM_TARGET_DB";            //$NON-NLS-1$
   private static final String PREF_PAGE_RM_ID_MAX_LEN = "PREF_PG_RM_ID_MAX_LEN";           //$NON-NLS-1$
   private static final String TRG_DB_CUSTOM_NAME      = Messages.PreferencePageRMSettings_0;
   private static final DBSetting TRG_DB_DB2           = new DBSetting(SupportedDB.DB2, Constants.DB_IDENTIFIER_MAX_LENGTH);
   private static final DBSetting TRG_DB_DB2CW         = new DBSetting(SupportedDB.DB2forCW, Constants.DB_IDENTIFIER_DB2_FOR_CW_LENGTH);
   private static final DBSetting TRG_DB_ORACLE        = new DBSetting(SupportedDB.Oracle, Constants.DB_IDENTIFIER_ORACLE_LENGTH);
   private static final DBSetting TRG_DB_CUSTOM        = new DBSetting(SupportedDB.Custom, Constants.DB_IDENTIFIER_MAX_LENGTH);
   private static final DBSetting TARGET_DB_ARR[]      = { TRG_DB_DB2, TRG_DB_ORACLE, TRG_DB_CUSTOM } ; 
   private static final DBSetting TARGET_DB_ARR_CW[]   = { TRG_DB_DB2, TRG_DB_DB2CW, TRG_DB_ORACLE, TRG_DB_CUSTOM } ; 


   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
	private Combo _TargetDBCombo;
   private Text  _MaxDBIdentifierLengthText  = null;

   
	static String copyright() { 
	   return com.ibm.is.sappack.gen.tools.sap.Copyright.IBM_COPYRIGHT_SHORT; 
	}	
	
   public PreferencePageRMSettings() {
      // TODO Auto-generated constructor stub
   }

	public PreferencePageRMSettings(String title) {
		super(title);
	}

	public PreferencePageRMSettings(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	protected Control createContents(final Composite parent) {
      /* only required once */
      Image infoImage = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage();
      
		Composite mainComposite = parent;
		Group dsClientComposite = new Group(mainComposite, SWT.NONE);
		dsClientComposite.setText(Messages.PreferencePageRMSettings_1);
		GridLayout dsClientLayout = new GridLayout();
		dsClientLayout.numColumns = 3;
		dsClientComposite.setLayout(dsClientLayout);
      GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		dsClientComposite.setLayoutData(gridData);

		Label dsClientLabel = new Label(dsClientComposite, SWT.NONE);
		dsClientLabel.setText(Messages.PreferencePageRMSettings_2);

		this._TargetDBCombo = new Combo(dsClientComposite, SWT.BORDER | SWT.READ_ONLY);
		DBSetting targetDBArr[] = getUsedTargetDBArray();
	
		   
		for(int idx = 0; idx < targetDBArr.length; idx ++) {
			this._TargetDBCombo.add(targetDBArr[idx].toString());
		}
		this._TargetDBCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this._TargetDBCombo.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            DBSetting selectedEntry = DBSetting.getDB(_TargetDBCombo.getText());
            _MaxDBIdentifierLengthText.setEnabled(selectedEntry.isCustom());
            
            String maxIdLenAsString;
            maxIdLenAsString = String.valueOf(selectedEntry.getMaxLength());
            _MaxDBIdentifierLengthText.setText(maxIdLenAsString);
            
            selectedEntry.setMaxLength(getInputDBIdLength());
         }

      });
      
//      targetDBCombo.addModifyListener(new ModifyListener() {
//         public void modifyText(ModifyEvent arg0) {
//         }
//      });
      
      // add dummy label to fill the row
      new Label(dsClientComposite, SWT.NONE).setText(""); //$NON-NLS-1$
   
      // create the Grid Data for the text field ...
      GridData textGridData = new GridData();
      textGridData.horizontalAlignment = GridData.FILL; // GridData.FILL_BOTH;

      // ... the label and the input (text) field
      Label textLabel = new Label(dsClientComposite, SWT.NONE);
      textLabel.setText(Messages.PreferencePageRMSettings_3);
      _MaxDBIdentifierLengthText = new Text(dsClientComposite, SWT.BORDER);
      _MaxDBIdentifierLengthText.setLayoutData(textGridData);
      
      final ControlDecoration maxDBIdentifierLengthDecoration = new ControlDecoration(this._MaxDBIdentifierLengthText, SWT.RIGHT | SWT.TOP);
      maxDBIdentifierLengthDecoration.hide();
      maxDBIdentifierLengthDecoration.setImage(infoImage);
      maxDBIdentifierLengthDecoration.setDescriptionText(Messages.PreferencePageRMSettings_4);
      maxDBIdentifierLengthDecoration.setShowHover(true);
      
      this._MaxDBIdentifierLengthText.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent arg0) {
            updateEnablement();
         }
      });
      this._MaxDBIdentifierLengthText.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
               maxDBIdentifierLengthDecoration.show();
            }

            public void focusLost(FocusEvent e) {
               maxDBIdentifierLengthDecoration.hide();
            }
        });
      
		
		restorePreferences();
		
		// add dummy composite to grab all the space
		Composite comp = new Composite(mainComposite, SWT.NONE); 
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));
		
		return mainComposite;
	}

	@Override
	public void init(IWorkbench workbench) {
	   ; // nothing to do
	}

	@Override
	protected void performApply() {
		storePreferences();
		super.performApply();
	}

	@Override
	public boolean performOk() {
		storePreferences();
		return super.performOk();
	}

	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}
	
	
	static ImporterOptionsBase globalOptions;
	
	public static void setImporterOptions(ImporterOptionsBase options) {
		globalOptions = options;
	}
	
	public static int getMaxDBIdentifierLength() {
		if (globalOptions != null) {
			return globalOptions.getDatabaseEntityMaxLength();
		}
		
	   int maxLen;
	   
	   maxLen = Activator.getDefault().getPreferenceStore().getInt(PREF_PAGE_RM_ID_MAX_LEN);
	   
	   if (maxLen < Constants.DB_IDENTIFIER_MIN_LENGTH) {
	      // default value is assumed
	      maxLen = Constants.DB_IDENTIFIER_MAX_LENGTH;
	   }
	   
		return(maxLen);
	}

	
   public static SupportedDB getTargetDBType() {
	   if (globalOptions != null) {
		   return globalOptions.getTargetDatabase();
	   }
	   
      DBSetting   targetDBArr[];
      SupportedDB retDBType;
      String      targetDB;
      int         idx;
      
      targetDBArr = getUsedTargetDBArray();
      targetDB    = Activator.getDefault().getPreferenceStore().getString(PREF_PAGE_RM_TARGET_DB);
      idx         = searchForTargetDB(targetDB, targetDBArr);
      
      if (idx < 0) {
       idx = 0;  
      }
      retDBType = targetDBArr[idx].getDB();  
      
      return(retDBType);
   }

   
   private int getInputDBIdLength() {
      int maxLen;
      
      try {
         maxLen = Integer.parseInt(this._MaxDBIdentifierLengthText.getText());
      }
      catch(NumberFormatException pNumberFormatExcpt) {
         maxLen = -1;
      }
      
      if (maxLen < Constants.DB_IDENTIFIER_MIN_LENGTH) {
         maxLen = Constants.DB_IDENTIFIER_MAX_LENGTH;
      }
         
      return maxLen;
   }


   private static DBSetting[] getUsedTargetDBArray() {
      DBSetting retTargetDBArr[];
      
      if (ModeManager.isCWEnabled()) {
         retTargetDBArr = TARGET_DB_ARR_CW; 
      }
      else {
         retTargetDBArr = TARGET_DB_ARR; 
      }
      
      return(retTargetDBArr);
   }


   private void restorePreferences() {
      IPreferenceStore ps = getPreferenceStore();
      
      if (ps != null) {
         int     selectIdx;
         int     maxDBIdLen;
         boolean isTargetDBCustom;
         
         String targetDB = ps.getString(PREF_PAGE_RM_TARGET_DB);
         if (targetDB == null) {
            targetDB  = TARGET_DB_ARR[0].toString();
            selectIdx = 0;
         }
         else {
            // search for the current target name in the name array
            selectIdx = searchForTargetDB(targetDB, getUsedTargetDBArray());
            
            if (selectIdx < 0) {
               // not found --> use 1st element in array
               targetDB  = TARGET_DB_ARR[0].toString();
               selectIdx = 0;
            }
         } // end of if (targetDB == null)
         DBSetting dbImpSetting =  DBSetting.getDB(targetDB);
         
         this._TargetDBCombo.setText(dbImpSetting.toString());
         this._TargetDBCombo.select(selectIdx);

         isTargetDBCustom = dbImpSetting.isCustom();
         if (isTargetDBCustom) {
            maxDBIdLen = ps.getInt(PREF_PAGE_RM_ID_MAX_LEN);
            if (maxDBIdLen < Constants.DB_IDENTIFIER_MIN_LENGTH) {
               maxDBIdLen = Constants.DB_IDENTIFIER_MAX_LENGTH;
            }
         }
         else {
            maxDBIdLen = dbImpSetting.getMaxLength(); 
         }
         dbImpSetting.setMaxLength(maxDBIdLen);
         _MaxDBIdentifierLengthText.setEnabled(isTargetDBCustom);            
         _MaxDBIdentifierLengthText.setText(String.valueOf(maxDBIdLen));
      } // end of if (ps != null)
   }

   
   private static int searchForTargetDB(String targetDBName, DBSetting targetDBArr[]) {
      int foundIdx;
      int srchIdx;
      
      // search for the current target name in the name array
      foundIdx = -1;
      srchIdx  = 0;
      while(srchIdx < targetDBArr.length && foundIdx < 0) {
         if (targetDBArr[srchIdx].toString().equals(targetDBName)) {
            foundIdx = srchIdx;
         }
         srchIdx ++;
      }
      
      return(foundIdx);
   }
   
   private void storePreferences() {
      IPreferenceStore ps = getPreferenceStore();
      
      if (ps != null) {
         ps.setValue(PREF_PAGE_RM_TARGET_DB, this._TargetDBCombo.getText());
         ps.setValue(PREF_PAGE_RM_ID_MAX_LEN, getInputDBIdLength());
      } // end of if (ps != null)
   } 
	
   private void updateEnablement() {
      String  maxLenAsString;
      String  errMsg;
      int     curLen;

      errMsg         = null;
      maxLenAsString = _MaxDBIdentifierLengthText.getText();
            
      try {
         curLen = Integer.parseInt(maxLenAsString);
         if (curLen > Constants.DB_IDENTIFIER_MAX_LENGTH || curLen < Constants.DB_IDENTIFIER_MIN_LENGTH) {
            errMsg = Messages.PreferencePageRMSettings_5;
         }
      }
      catch (NumberFormatException e) {
         errMsg = Messages.PreferencePageRMSettings_6;
      }
      
      setErrorMessage(errMsg);
      setValid(errMsg == null);
   }
   
} // end of class PreferencePageRMSettings
