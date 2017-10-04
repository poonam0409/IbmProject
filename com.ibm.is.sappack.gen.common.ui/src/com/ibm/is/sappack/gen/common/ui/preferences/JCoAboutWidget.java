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
package com.ibm.is.sappack.gen.common.ui.preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.sap.conn.jco.JCo;

public class JCoAboutWidget extends Composite {

	static String copyright() {
		return com.ibm.is.sappack.gen.common.ui.preferences.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private Group jcoLibraryGroup = null;
	private Label versionLabel = null;
	private Text versionText = null;
	private Label pathLabel = null;
	private Text pathText = null;
	private Group jcoMiddlewareGroup = null;
	private Label typeMiddlewareLabel = null;
	private Text typeMiddlewareText = null;
	private Label versionMiddlewareLabel = null;
	private Text versionMiddlewareText = null;
	private Label pathMiddlewareLabel = null;
	private Text pathMiddlewareText = null;
	private Group sapPackGroup = null;
	private Label versionSapPackLabel = null;
	private Text versionSapPackText = null;
	private Group javaRuntimeGroup = null;
	private Text javaRuntimeTextArea = null;
	private Button clipboardButton = null;

	/**
	 * @param parent
	 * @param style
	 */
	public JCoAboutWidget(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment = GridData.END;
		gridData11.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		createSapPackGroup();
		Label filler3 = new Label(this, SWT.NONE);
		filler3.setVisible(false);
		createJcoLibraryGroup();
		this.setLayout(gridLayout1);
		Label filler2 = new Label(this, SWT.NONE);
		filler2.setVisible(false);
		createJcoMiddlewareGroup();
		Label filler1 = new Label(this, SWT.NONE);
		filler1.setVisible(false);
		createJavaRuntimeGroup();
		setSize(new Point(300, 376));
		Label filler = new Label(this, SWT.NONE);
		filler.setVisible(false);

		final Clipboard cb = new Clipboard(Display.getCurrent());

		clipboardButton = new Button(this, SWT.NONE);
		clipboardButton.setText(Messages.JCoAboutWidget_0);
		clipboardButton.setToolTipText(Messages.JCoAboutWidget_1);
		clipboardButton.setLayoutData(gridData11);
		clipboardButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				StringBuffer clipboardText = new StringBuffer();

				clipboardText.append(Messages.JCoAboutWidget_2);
				clipboardText.append(versionSapPackText.getText());
				clipboardText.append(Messages.JCoAboutWidget_3);
				clipboardText.append(versionText.getText());
				clipboardText.append(Messages.JCoAboutWidget_4);
				clipboardText.append(pathText.getText());
				clipboardText.append(Messages.JCoAboutWidget_5);
				clipboardText.append(typeMiddlewareText.getText());
				clipboardText.append(Messages.JCoAboutWidget_6);
				clipboardText.append(versionMiddlewareText.getText());
				clipboardText.append(Messages.JCoAboutWidget_7);
				clipboardText.append(pathMiddlewareText.getText());
				clipboardText.append(Messages.JCoAboutWidget_8);
				clipboardText.append(javaRuntimeTextArea.getText());

				TextTransfer textTransfer = TextTransfer.getInstance();
				cb.setContents(new Object[] { clipboardText.toString() }, new Transfer[] { textTransfer });
				MessageDialog.openInformation(null, Messages.JCoAboutWidget_9,
				      Messages.JCoAboutWidget_10);
			}
		});
	}

	/**
	 * This method initializes jcoLibraryGroup
	 * 
	 */
	private void createJcoLibraryGroup() {
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = false;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		jcoLibraryGroup = new Group(this, SWT.NONE);
		jcoLibraryGroup.setLayoutData(gridData);
		jcoLibraryGroup.setLayout(gridLayout);
		jcoLibraryGroup.setText(Messages.JCoAboutWidget_11);
		versionLabel = new Label(jcoLibraryGroup, SWT.NONE);
		versionLabel.setText(Messages.JCoAboutWidget_12);
		versionText = new Text(jcoLibraryGroup, SWT.BORDER);
		versionText.setLayoutData(gridData1);
		versionText.setEditable(false);
		pathLabel = new Label(jcoLibraryGroup, SWT.NONE);
		pathLabel.setText(Messages.JCoAboutWidget_13);
		pathText = new Text(jcoLibraryGroup, SWT.BORDER);
		pathText.setLayoutData(gridData1);
		pathText.setEditable(false);

		if (!Activator.locateJCoJAR().equals("")) { //$NON-NLS-1$
			versionText.setText(JCo.getVersion());
			pathText.setText(Activator.locateJCoJAR());
		}
	}

	/**
	 * This method initializes jcoMiddlewareGroup
	 * 
	 */
	private void createJcoMiddlewareGroup() {
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.FILL;
		gridData5.grabExcessHorizontalSpace = true;
		gridData5.verticalAlignment = GridData.CENTER;
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.FILL;
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.verticalAlignment = GridData.CENTER;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.verticalAlignment = GridData.CENTER;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.grabExcessVerticalSpace = false;
		gridData2.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 2;
		jcoMiddlewareGroup = new Group(this, SWT.NONE);
		jcoMiddlewareGroup.setText(Messages.JCoAboutWidget_15);
		jcoMiddlewareGroup.setLayout(gridLayout2);
		jcoMiddlewareGroup.setLayoutData(gridData2);
		typeMiddlewareLabel = new Label(jcoMiddlewareGroup, SWT.NONE);
		typeMiddlewareLabel.setText(Messages.JCoAboutWidget_16);
		typeMiddlewareText = new Text(jcoMiddlewareGroup, SWT.BORDER);
		typeMiddlewareText.setLayoutData(gridData3);
		typeMiddlewareText.setEditable(false);
		versionMiddlewareLabel = new Label(jcoMiddlewareGroup, SWT.NONE);
		versionMiddlewareLabel.setText(Messages.JCoAboutWidget_17);
		versionMiddlewareText = new Text(jcoMiddlewareGroup, SWT.BORDER);
		versionMiddlewareText.setLayoutData(gridData4);
		versionMiddlewareText.setEditable(false);
		pathMiddlewareLabel = new Label(jcoMiddlewareGroup, SWT.NONE);
		pathMiddlewareLabel.setText(Messages.JCoAboutWidget_18);
		pathMiddlewareText = new Text(jcoMiddlewareGroup, SWT.BORDER);
		pathMiddlewareText.setLayoutData(gridData5);
		pathMiddlewareText.setEditable(false);

		if (Activator.locateJCoJAR() != "") { //$NON-NLS-1$
			typeMiddlewareText.setText(JCo.getMiddlewareProperty(Activator.PROPERTY_JCO_MIDDLEWARE_NAME));
			versionMiddlewareText.setText(JCo
			      .getMiddlewareProperty(Activator.PROPERTY_JCO_MIDDLEWARE_NATIVE_LAYER_VERSION));
			pathMiddlewareText.setText(JCo.getMiddlewareProperty(Activator.PROPERTY_JCO_MIDDLEWARE_NATIVE_LAYER_PATH));
		}
	}

	/**
	 * This method initializes sapPackGroup
	 * 
	 */
	private void createSapPackGroup() {
		GridData gridData7 = new GridData();
		gridData7.horizontalAlignment = GridData.FILL;
		gridData7.grabExcessHorizontalSpace = true;
		gridData7.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout3 = new GridLayout();
		gridLayout3.numColumns = 2;
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.FILL;
		gridData6.grabExcessHorizontalSpace = true;
		gridData6.verticalAlignment = GridData.CENTER;
		sapPackGroup = new Group(this, SWT.NONE);
		sapPackGroup.setText(Messages.JCoAboutWidget_20);
		sapPackGroup.setLayoutData(gridData6);
		sapPackGroup.setLayout(gridLayout3);
		versionSapPackLabel = new Label(sapPackGroup, SWT.NONE);
		versionSapPackLabel.setText(Messages.JCoAboutWidget_21);
		versionSapPackText = new Text(sapPackGroup, SWT.BORDER);
		versionSapPackText.setEnabled(true);
		versionSapPackText.setEditable(false);
		versionSapPackText.setLayoutData(gridData7);
		versionSapPackText.setText(com.ibm.is.sappack.gen.common.Constants.CLIENT_SERVER_VERSION);
	}

	/**
	 * This method initializes javaRuntimeGroup
	 * 
	 */
	private void createJavaRuntimeGroup() {
		StringBuffer message = new StringBuffer();

		message.append("java.runtime.name: "); //$NON-NLS-1$
		message.append(System.getProperty("java.runtime.name") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		message.append("java.runtime.version: "); //$NON-NLS-1$
		message.append(System.getProperty("java.runtime.version") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		message.append("java.specification.name: "); //$NON-NLS-1$
		message.append(System.getProperty("java.specification.name") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		message.append("java.specification.vendor: "); //$NON-NLS-1$
		message.append(System.getProperty("java.specification.vendor") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		message.append("java.specification.version: "); //$NON-NLS-1$
		message.append(System.getProperty("java.specification.version") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		message.append("java.vendor: "); //$NON-NLS-1$
		message.append(System.getProperty("java.vendor") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		message.append("java.vendor.url: "); //$NON-NLS-1$
		message.append(System.getProperty("java.vendor.url") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		message.append("java.version: "); //$NON-NLS-1$
		message.append(System.getProperty("java.version") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		message.append("java.vm.info: "); //$NON-NLS-1$
		message.append(System.getProperty("java.vm.info") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		message.append("java.vm.name: "); //$NON-NLS-1$
		message.append(System.getProperty("java.vm.name") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		message.append("java.vm.specification.name: "); //$NON-NLS-1$
		message.append(System.getProperty("java.vm.specification.name") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		message.append("java.vm.specification.vendor: "); //$NON-NLS-1$
		message.append(System.getProperty("java.vm.specification.vendor") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		message.append("java.vm.specification.version: "); //$NON-NLS-1$
		message.append(System.getProperty("java.vm.specification.version") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		message.append("java.vm.vendor: "); //$NON-NLS-1$
		message.append(System.getProperty("java.vm.vendor") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		message.append("java.vm.version: "); //$NON-NLS-1$
		message.append(System.getProperty("java.vm.version")); //$NON-NLS-1$

		GridData gridData10 = new GridData();
		gridData10.horizontalAlignment = GridData.FILL;
		gridData10.grabExcessHorizontalSpace = true;
		gridData10.grabExcessVerticalSpace = true;
		gridData10.verticalAlignment = GridData.FILL;
		GridData gridData9 = new GridData();
		gridData9.horizontalAlignment = GridData.FILL;
		gridData9.grabExcessHorizontalSpace = true;
		gridData9.verticalAlignment = GridData.CENTER;
		GridData gridData8 = new GridData();
		gridData8.horizontalAlignment = GridData.FILL;
		gridData8.grabExcessHorizontalSpace = true;
		gridData8.grabExcessVerticalSpace = true;
		gridData8.verticalAlignment = GridData.FILL;
		GridLayout gridLayout4 = new GridLayout();
		gridLayout4.numColumns = 1;
		javaRuntimeGroup = new Group(this, SWT.NONE);
		javaRuntimeGroup.setText(Messages.JCoAboutWidget_66);
		javaRuntimeGroup.setLayout(gridLayout4);
		javaRuntimeGroup.setLayoutData(gridData8);
		javaRuntimeTextArea = new Text(javaRuntimeGroup, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		javaRuntimeTextArea.setEditable(false);
		javaRuntimeTextArea.setLayoutData(gridData10);
		javaRuntimeTextArea.setText(message.toString());
	}

} // @jve:decl-index=0:visual-constraint="10,10"
