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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.is.sappack.gen.jco.panel.internal.Messages;

/**
 * @author dsh
 *
 */
public class JCoInformationWidget extends Composite {
	
	private final ILogger log = IMLogger.getLogger(com.ibm.is.sappack.gen.jco.panel.JCoInformationWidget.class);

	private FormToolkit formToolkit = null;   //  @jve:decl-index=0:visual-constraint=""
	private Table jcoInfoTable = null;
	private Text javaRuntimeConfigText = null;
	private Button clipboardButton = null;
	private TableViewer tableViewer = null;
	private String jcoJavaArchiveLocation;
	private String jcoNativeDirectoryLocation;
	private Label javaRuntimeConfigLabel = null;
	
	private Composite parentWidget = null;
	
	static String copyright() {
		return com.ibm.is.sappack.gen.jco.panel.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/**
	 * @param parent
	 * @param style
	 */
	public JCoInformationWidget(Composite parent, int style) {
		super(parent, style);
		initialize();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param parent
	 * @param toolkit
	 * @param javaArchiveLocation
	 * @param nativeDirectoryLocation
	 * @param style
	 */
	public JCoInformationWidget(Composite parentWidget, Composite parent, FormToolkit toolkit, String javaArchiveLocation, String nativeDirectoryLocation, int style) {
		super(parent, style);
		this.parentWidget = parentWidget;
		this.formToolkit = toolkit;
		this.jcoJavaArchiveLocation = javaArchiveLocation;
		this.jcoNativeDirectoryLocation = nativeDirectoryLocation;
		initialize();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the tableViewer
	 */
	protected TableViewer getTableViewer() {
		return tableViewer;
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        GridData gridData3 = new GridData();
        gridData3.horizontalAlignment = GridData.FILL;
        gridData3.grabExcessHorizontalSpace = true;
        gridData3.verticalAlignment = GridData.CENTER;
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL;
        GridData gridData2 = new GridData();
        gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = GridData.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.verticalAlignment = GridData.CENTER;
        jcoInfoTable = getFormToolkit().createTable(this, SWT.NONE);
        jcoInfoTable.setLinesVisible(true);
        jcoInfoTable.setLayoutData(gridData1);
        jcoInfoTable.setHeaderVisible(true);
        tableViewer = new TableViewer(jcoInfoTable);
        this.createColumns(this, tableViewer);
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(JCoModelProvider.INSTANCE.getJCoSettingsPair());
        this.setLayout(new GridLayout());
        this.setSize(new Point(346, 251));
        javaRuntimeConfigLabel = getFormToolkit().createLabel(this, Messages.JCoInformationWidget_0); //$NON-NLS-1$
        javaRuntimeConfigLabel.setLayoutData(gridData3);
		javaRuntimeConfigText = getFormToolkit().createText(this, null, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP | SWT.READ_ONLY);
		javaRuntimeConfigText.setEditable(false);
		javaRuntimeConfigText.setLayoutData(gridData);
		javaRuntimeConfigText.setText(this.getJavaRuntimeConfiguration());
		
		final Clipboard cb = new Clipboard(Display.getCurrent());
		
        clipboardButton = getFormToolkit().createButton(this, Messages.JCoInformationWidget_1, SWT.PUSH); //$NON-NLS-1$
        clipboardButton.setLayoutData(gridData2);
        
		clipboardButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				StringBuffer clipboardText = new StringBuffer();

				clipboardText.append(Messages.JCoInformationWidget_2); //$NON-NLS-1$
				clipboardText.append(((JCoWidgetClass) JCoInformationWidget.this.parentWidget).getValidator().getJcoVersion());
				clipboardText.append(Messages.JCoInformationWidget_3); //$NON-NLS-1$
				clipboardText.append(((JCoWidgetClass) JCoInformationWidget.this.parentWidget).getValidator().getJcoJavaArchiveLocation());
				clipboardText.append(Messages.JCoInformationWidget_4); //$NON-NLS-1$
				clipboardText.append(((JCoWidgetClass) JCoInformationWidget.this.parentWidget).getValidator().getJcoMiddlewareName());
				clipboardText.append(Messages.JCoInformationWidget_5); //$NON-NLS-1$
				clipboardText.append(((JCoWidgetClass) JCoInformationWidget.this.parentWidget).getValidator().getJcoMiddlewareNativeLayerVersion());
				clipboardText.append(Messages.JCoInformationWidget_6); //$NON-NLS-1$
				clipboardText.append(((JCoWidgetClass) JCoInformationWidget.this.parentWidget).getValidator().getJcoMiddlewareNativeLayerPath());
				clipboardText.append(Messages.JCoInformationWidget_7); //$NON-NLS-1$
				clipboardText.append(JCoInformationWidget.this.getJavaRuntimeConfiguration());

				TextTransfer textTransfer = TextTransfer.getInstance();
				cb.setContents(new Object[] { clipboardText.toString() }, new Transfer[] { textTransfer });
				MessageDialog.openInformation(null, Messages.JCoInformationWidget_8, Messages.JCoInformationWidget_9); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
	}
	
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { Messages.JCoInformationWidget_10, Messages.JCoInformationWidget_11 }; //$NON-NLS-1$ //$NON-NLS-2$
		int[] bounds = { 150, 200 };

		// First column is for the JCo setting name
		TableViewerColumn col = createTableViewerColumn(viewer, titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				JCoSettingsPair p = (JCoSettingsPair) element;
				return p.getJcoSettingsName();
			}
		});

		// Second column is for the JCo setting value
		col = createTableViewerColumn(viewer, titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				JCoSettingsPair p = (JCoSettingsPair) element;
				return p.getJcoSettingsValue();
			}
		});
	}
	
	private TableViewerColumn createTableViewerColumn(final TableViewer viewer, String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
				SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}
	
	private String getJavaRuntimeConfiguration() {
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
		message.append(System.getProperty("java.vm.version") + "\n"); //$NON-NLS-1$
//		message.append("java.library.path: "); //$NON-NLS-1$
//		message.append(System.getProperty("java.library.path")); //$NON-NLS-1$
		
		return message.toString();
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
