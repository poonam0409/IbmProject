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
// Module Name : com.ibm.is.sappack.gen.common.ui.preferences
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.jco.panel;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.is.sappack.gen.jco.panel.internal.Messages;

/**
 * @author dsh
 *
 */
public class JCoArtifactChooserWidget extends Composite {
	
	private final ILogger log = IMLogger.getLogger(com.ibm.is.sappack.gen.jco.panel.JCoArtifactChooserWidget.class);

	private FormToolkit formToolkit = null;   //  @jve:decl-index=0:visual-constraint=""
	private Label jcoJARLocationLabel = null;
	private Label jcoNativeDirectoryLocationLabel = null;
	private Text jcoJARLocationText = null;
	private Text jcoNativeDirectoryLocationText = null;
	private Button jcoJARLocationButton = null;
	private Button jcoNativeDirectoryLocationButton = null;
	private boolean jcoJARLocationIsValid = false;
	private boolean jcoNativeDirectoryIsValid = false;
	private Composite parent = null;
	private Composite parentWidget = null;
	
	private String selectedDirectory;  //  @jve:decl-index=0:
	private String selectedJavaArchive;

	static String copyright() {
		return com.ibm.is.sappack.gen.jco.panel.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	/**
	 * @return the jcoJARLocationIsValid
	 */
	public boolean isJcoJARLocationIsValid() {
		return jcoJARLocationIsValid;
	}

	/**
	 * @return the jcoNativeDirectoryIsValid
	 */
	public boolean isJcoNativeDirectoryIsValid() {
		return jcoNativeDirectoryIsValid;
	}

	public String getSelectedDirectory() {
		return selectedDirectory;
	}

	public void setSelectedDirectory(String selectedDirectory) {
		this.selectedDirectory = selectedDirectory;
	}

	public String getSelectedJavaArchive() {
		return selectedJavaArchive;
	}

	public void setSelectedJavaArchive(String selectedJavaArchive) {
		this.selectedJavaArchive = selectedJavaArchive;
	}

	/**
	 * @param parent
	 * @param style
	 */
	public JCoArtifactChooserWidget(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
		initialize();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param parent
	 * @param toolkit
	 * @param style
	 */
	public JCoArtifactChooserWidget(Composite parentWidget, Composite parent, FormToolkit toolkit, int style) {
		super(parent, style);
		this.formToolkit = toolkit;
		this.parentWidget = parentWidget;
		this.parent = parent;
		initialize();
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        GridData gridData1 = new GridData();
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        this.setSize(new Point(527, 61));
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        this.setLayout(gridLayout);
        jcoJARLocationLabel = getFormToolkit().createLabel(this, Messages.JCoArtifactChooserWidget_0); //$NON-NLS-1$
        jcoJARLocationText = getFormToolkit().createText(this, null, SWT.SINGLE | SWT.BORDER);
        jcoJARLocationText.setLayoutData(gridData);
        jcoJARLocationButton = getFormToolkit().createButton(this, Messages.JCoArtifactChooserWidget_1, SWT.PUSH); //$NON-NLS-1$
        jcoNativeDirectoryLocationLabel = getFormToolkit().createLabel(this, Messages.JCoArtifactChooserWidget_2); //$NON-NLS-1$
        jcoNativeDirectoryLocationText = getFormToolkit().createText(this, null, SWT.SINGLE | SWT.BORDER);
        jcoNativeDirectoryLocationText.setLayoutData(gridData1);
        jcoNativeDirectoryLocationButton = getFormToolkit().createButton(this, Messages.JCoArtifactChooserWidget_3, SWT.PUSH); //$NON-NLS-1$
        
        //text listeners
        jcoJARLocationText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
            	if (null != JCoArtifactChooserWidget.this.jcoJARLocationText.getText() && JCoArtifactChooserWidget.this.jcoJARLocationText.getText().trim().length() > 0) {
	                JCoArtifactChooserWidget.this.selectedJavaArchive = JCoArtifactChooserWidget.this.jcoJARLocationText.getText();
	                JCoArtifactChooserWidget.this.validate();
            	}
            }
          });
        
        jcoNativeDirectoryLocationText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
            	if (null != JCoArtifactChooserWidget.this.jcoNativeDirectoryLocationText.getText() && JCoArtifactChooserWidget.this.jcoNativeDirectoryLocationText.getText().trim().length() > 0) {
	                JCoArtifactChooserWidget.this.selectedDirectory = JCoArtifactChooserWidget.this.jcoNativeDirectoryLocationText.getText();
	                JCoArtifactChooserWidget.this.validate();
            	}
            }
          });
        
        //button listeners
        this.jcoJARLocationButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                FileDialog fileDialog = new FileDialog(JCoArtifactChooserWidget.this.getShell(), SWT.SINGLE);

                fileDialog.setFilterPath(null);
                
                fileDialog.setFilterExtensions(new String[]{"*.jar"}); //$NON-NLS-1$
                fileDialog.setFilterNames(new String[]{Messages.JCoArtifactChooserWidget_5}); //$NON-NLS-1$
                
                String firstFile = fileDialog.open();

                if(firstFile != null) {
                  JCoArtifactChooserWidget.this.selectedJavaArchive = fileDialog.getFilterPath() + File.separator + fileDialog.getFileName();
                  JCoArtifactChooserWidget.this.jcoJARLocationText.setText(fileDialog.getFilterPath() + File.separator + fileDialog.getFileName());
                  JCoArtifactChooserWidget.this.validate();
                }
              }
            });
        
        this.jcoNativeDirectoryLocationButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                DirectoryDialog directoryDialog = new DirectoryDialog(JCoArtifactChooserWidget.this.getShell());
                
                directoryDialog.setFilterPath(JCoArtifactChooserWidget.this.selectedDirectory);
                directoryDialog.setMessage(Messages.JCoArtifactChooserWidget_6); //$NON-NLS-1$
                
                String dir = directoryDialog.open();
                if(dir != null) {
                	JCoArtifactChooserWidget.this.selectedDirectory = dir;
                	JCoArtifactChooserWidget.this.jcoNativeDirectoryLocationText.setText(dir);
                	JCoArtifactChooserWidget.this.validate();
                }
              }
            });
	}
	
	public boolean validate() {
		boolean returnValue = false;
		
		if (null != this.selectedDirectory) {
			File selectedDirectoryFile = new File(this.selectedDirectory);
			returnValue = selectedDirectoryFile.isDirectory();
			this.jcoNativeDirectoryIsValid = returnValue;
			
			if (this.parentWidget instanceof JCoWidgetClass) {
				((JCoWidgetClass) this.parentWidget).setJcoNativeDirectoryIsValid(returnValue);
				((JCoWidgetClass) this.parentWidget).setSelectedDirectory(this.selectedDirectory);
			}
			
			if (this.parentWidget instanceof JCoWidgetClass && null != ((JCoWidgetClass) this.parentWidget).getValidator()) {
				((JCoWidgetClass) this.parentWidget).getValidator().setJcoNativeDirectoryLocation(this.selectedDirectory);
				((JCoWidgetClass) this.parentWidget).getValidator().setJcoNativeDirectoryIsValid(returnValue);
			}
		}
		if (null != this.selectedJavaArchive) {
			File selectedJavaArchiveFile = new File(this.selectedJavaArchive);
			returnValue = selectedJavaArchiveFile.exists() && selectedJavaArchiveFile.isFile();
			this.jcoJARLocationIsValid = returnValue;
			
			if (this.parentWidget instanceof JCoWidgetClass) {
				((JCoWidgetClass) this.parentWidget).setJcoJARLocationIsValid(returnValue);
				((JCoWidgetClass) this.parentWidget).setSelectedJavaArchive(this.selectedJavaArchive);
			}
			
			if (this.parentWidget instanceof JCoWidgetClass && null != ((JCoWidgetClass) this.parentWidget).getValidator()) {
				((JCoWidgetClass) this.parentWidget).getValidator().setJcoJavaArchiveLocation(this.selectedJavaArchive);
				((JCoWidgetClass) this.parentWidget).getValidator().setJcoJARLocationIsValid(returnValue);
			}
		}
		
		return returnValue;
	}

	/**
	 * This method initializes formToolkit	
	 * 	
	 * @return org.eclipse.ui.forms.widgets.FormToolkit	
	 */
	private FormToolkit getFormToolkit() {
		if (formToolkit == null) {
			formToolkit = new FormToolkit(Display.getCurrent());
		}
		return formToolkit;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
