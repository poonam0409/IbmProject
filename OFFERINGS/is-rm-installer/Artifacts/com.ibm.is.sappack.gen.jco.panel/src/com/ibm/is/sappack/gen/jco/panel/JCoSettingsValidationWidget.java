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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.is.sappack.gen.jco.panel.internal.Messages;
import com.ibm.is.sappack.gen.jco.panel.jni.DLLVersion;

/**
 * @author dsh
 *
 */
public class JCoSettingsValidationWidget extends Composite {
	
	private final ILogger log = IMLogger.getLogger(com.ibm.is.sappack.gen.jco.panel.JCoSettingsValidationWidget.class);
	
	private static final String JCO_NATIVE_ARTIFACT_NAME = "sapjco3.dll"; //$NON-NLS-1$

	private FormToolkit formToolkit = null;   //  @jve:decl-index=0:visual-constraint=""
	private Button msvcp60CheckBox = null;
	private Button msvcm80CheckBox = null;
	private Button msvcr80CheckBox = null;
	private Button jcoJARCheckBox = null;
	private Button jcoNativeDirCheckBox = null;
	private Button jcoExecTestCheckBox = null;
	private Button settingsValidationButton = null;
	
	private String jcoJavaArchiveLocation;
	private String jcoNativeDirectoryLocation;
	private boolean validationResult = false;
	private boolean jcoJARLocationIsValid = false;
	private boolean jcoNativeDirectoryIsValid = false;
	private boolean msvcp60DLLExists = false;
	private boolean msvcm80DLLExists = false;
	private boolean msvcr80DLLExists = false;
	
	private boolean jcoInvokationSucceeded = false;
    String jcoVersion;
    String jcoMiddlewareName;
    String jcoMiddlewareNativeLayerVersion;
    String jcoMiddlewareNativeLayerPath;
    
    private Composite parentWidget;
    private Class jcoClass = null;
    
	static String copyright() {
		return com.ibm.is.sappack.gen.jco.panel.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/**
	 * @param parent
	 * @param style
	 */
	public JCoSettingsValidationWidget(Composite parent, int style) {
		super(parent, style);
		initialize();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param parent
	 * @param toolkit
	 * @param javaArchiveLocation
	 * @nativeDirectoryLocation
	 * @param style
	 */
	public JCoSettingsValidationWidget
	(
		Composite parentWidget,
		Composite parent,
		FormToolkit toolkit,
		String javaArchiveLocation,
		String nativeDirectoryLocation,
		boolean jarLocationIsValid,
		boolean nativeDirectoryIsValid,
		int style
	) {
		super(parent, style);
		this.parentWidget = parentWidget;
		this.formToolkit = toolkit;
		this.jcoJavaArchiveLocation = javaArchiveLocation;
		this.jcoNativeDirectoryLocation = nativeDirectoryLocation;
		this.jcoJARLocationIsValid = jarLocationIsValid;
		this.jcoNativeDirectoryIsValid = nativeDirectoryIsValid;
		initialize();
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        GridData gridData6 = new GridData();
        gridData6.horizontalAlignment = GridData.FILL;
        gridData6.grabExcessHorizontalSpace = true;
        gridData6.verticalAlignment = GridData.CENTER;
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
        gridData2.verticalAlignment = GridData.CENTER;
        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = GridData.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.verticalAlignment = GridData.CENTER;
        GridData gridData = new GridData();
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
        this.setLayout(new GridLayout());
        this.setSize(new Point(592, 159));
        msvcp60CheckBox = getFormToolkit().createButton(this, Messages.JCoSettingsValidationWidget_1, SWT.CHECK); //$NON-NLS-1$
        msvcp60CheckBox.setEnabled(false);
        msvcp60CheckBox.setLayoutData(gridData1);
        msvcm80CheckBox = getFormToolkit().createButton(this, Messages.JCoSettingsValidationWidget_2, SWT.CHECK); //$NON-NLS-1$
        msvcm80CheckBox.setEnabled(false);
        msvcm80CheckBox.setLayoutData(gridData2);
        msvcr80CheckBox = getFormToolkit().createButton(this, Messages.JCoSettingsValidationWidget_3, SWT.CHECK); //$NON-NLS-1$
        msvcr80CheckBox.setEnabled(false);
        msvcr80CheckBox.setLayoutData(gridData3);
        jcoJARCheckBox = getFormToolkit().createButton(this, Messages.JCoSettingsValidationWidget_4, SWT.CHECK); //$NON-NLS-1$
        jcoJARCheckBox.setEnabled(false);
        jcoJARCheckBox.setLayoutData(gridData4);
        jcoNativeDirCheckBox = getFormToolkit().createButton(this, Messages.JCoSettingsValidationWidget_5, SWT.CHECK); //$NON-NLS-1$
        jcoNativeDirCheckBox.setEnabled(false);
        jcoNativeDirCheckBox.setLayoutData(gridData5);
        jcoExecTestCheckBox = getFormToolkit().createButton(this, Messages.JCoSettingsValidationWidget_6, SWT.CHECK); //$NON-NLS-1$
        jcoExecTestCheckBox.setEnabled(false);
        jcoExecTestCheckBox.setLayoutData(gridData6);
        settingsValidationButton = getFormToolkit().createButton(this, Messages.JCoSettingsValidationWidget_7, SWT.PUSH); //$NON-NLS-1$
        settingsValidationButton.setLayoutData(gridData);
		settingsValidationButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
            	settingsValidationButton.setEnabled(false);
            	
            	BusyIndicator.showWhile(settingsValidationButton.getDisplay(), new Runnable(){
            		public void run() {
            			JCoSettingsValidationWidget.this.validationResult = JCoSettingsValidationWidget.this.validate();
            		}
            	});
            	((JCoWidgetClass) JCoSettingsValidationWidget.this.parentWidget).setValidationSucceeded(JCoSettingsValidationWidget.this.validationResult);
            	((JCoWidgetClass) JCoSettingsValidationWidget.this.parentWidget).getMyPanel().validateComplete(JCoSettingsValidationWidget.this.validationResult);
            	
            	settingsValidationButton.setEnabled(true);
            	
            	JCoSettingsValidationWidget.this.jcoJARCheckBox.setSelection(JCoSettingsValidationWidget.this.jcoJARLocationIsValid);
            	JCoSettingsValidationWidget.this.jcoNativeDirCheckBox.setSelection(JCoSettingsValidationWidget.this.jcoNativeDirectoryIsValid);
            	JCoSettingsValidationWidget.this.jcoExecTestCheckBox.setSelection(JCoSettingsValidationWidget.this.jcoInvokationSucceeded);
            	JCoSettingsValidationWidget.this.msvcm80CheckBox.setSelection(JCoSettingsValidationWidget.this.msvcm80DLLExists);
            	JCoSettingsValidationWidget.this.msvcp60CheckBox.setSelection(JCoSettingsValidationWidget.this.msvcp60DLLExists);
            	JCoSettingsValidationWidget.this.msvcr80CheckBox.setSelection(JCoSettingsValidationWidget.this.msvcr80DLLExists);
              }
            });
	}
	
	public boolean validate() {
		boolean returnValue = false;
		
		if (null != this.jcoNativeDirectoryLocation) {
			File selectedDirectoryFile = new File(this.jcoNativeDirectoryLocation);
			returnValue = selectedDirectoryFile.isDirectory() && validateNativeArtifact();
			this.jcoNativeDirectoryIsValid = selectedDirectoryFile.isDirectory();
		}
		if (null != this.jcoJavaArchiveLocation) {
			File selectedJavaArchiveFile = new File(this.jcoJavaArchiveLocation);
			returnValue = selectedJavaArchiveFile.exists() && selectedJavaArchiveFile.isFile() && this.validateJavaArchive();
			this.jcoJARLocationIsValid = selectedJavaArchiveFile.exists() && selectedJavaArchiveFile.isFile();
		}
		
		if (this.jcoJARLocationIsValid) {
			URI jcoJarUri = new File(this.jcoJavaArchiveLocation).toURI();
			URL jcoJarUrl = null;
			
			String oldJavaLibPath = System.getProperty("java.library.path"); //$NON-NLS-1$
			System.setProperty("java.library.path", this.jcoNativeDirectoryLocation + File.pathSeparator + oldJavaLibPath); //$NON-NLS-1$
			
			try {
				jcoJarUrl = jcoJarUri.toURL();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				this.jcoInvokationSucceeded = false;
			}
			
	        ClassLoader contextFinder = Thread.currentThread().getContextClassLoader();
	        ClassLoader classLoader = new URLClassLoader(new URL[]{jcoJarUrl}, contextFinder);
	        
	        if(classLoader != null && (classLoader instanceof URLClassLoader)){
	            URLClassLoader urlClassLoader = (URLClassLoader)classLoader;
	            Method addURL = null;
				try {
					addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class}); //$NON-NLS-1$
				} catch (SecurityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					this.jcoInvokationSucceeded = false;
				} catch (NoSuchMethodException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					this.jcoInvokationSucceeded = false;
				}
	            addURL.setAccessible(true);
	            try {
					addURL.invoke(urlClassLoader, new Object[]{jcoJarUrl});
					
					this.jcoClass = classLoader.loadClass("com.sap.conn.jco.JCo"); //$NON-NLS-1$
			        this.jcoVersion = (String) this.jcoClass.getMethod("getVersion", null).invoke(null, null); //$NON-NLS-1$
			        this.jcoMiddlewareName = (String) this.jcoClass.getMethod("getMiddlewareProperty", String.class).invoke(null, "jco.middleware.name"); //$NON-NLS-1$ //$NON-NLS-2$
			        this.jcoMiddlewareNativeLayerVersion = (String) this.jcoClass.getMethod("getMiddlewareProperty", String.class).invoke(null, "jco.middleware.native_layer_version"); //$NON-NLS-1$ //$NON-NLS-2$
			        this.jcoMiddlewareNativeLayerPath = (String) this.jcoClass.getMethod("getMiddlewareProperty", String.class).invoke(null, "jco.middleware.native_layer_path"); //$NON-NLS-1$ //$NON-NLS-2$
			        this.jcoInvokationSucceeded = true;
			        returnValue = this.jcoInvokationSucceeded;
			        
			        List<JCoSettingsPair> pair = new ArrayList<JCoSettingsPair>();
					pair.add(new JCoSettingsPair(Messages.JCoSettingsValidationWidget_18, this.jcoVersion)); //$NON-NLS-1$
					pair.add(new JCoSettingsPair(Messages.JCoSettingsValidationWidget_19, this.jcoJavaArchiveLocation)); //$NON-NLS-1$
					pair.add(new JCoSettingsPair(Messages.JCoSettingsValidationWidget_20, this.jcoMiddlewareName)); //$NON-NLS-1$
					pair.add(new JCoSettingsPair(Messages.JCoSettingsValidationWidget_21, this.jcoMiddlewareNativeLayerVersion)); //$NON-NLS-1$
					pair.add(new JCoSettingsPair(Messages.JCoSettingsValidationWidget_22, this.jcoMiddlewareNativeLayerPath)); //$NON-NLS-1$
					JCoModelProvider.INSTANCE.setPair(pair);
			        
			        ((JCoWidgetClass) this.parentWidget).getInfo().getTableViewer().setInput(JCoModelProvider.INSTANCE.getJCoSettingsPair());
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.jcoInvokationSucceeded = false;
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.jcoInvokationSucceeded = false;
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.jcoInvokationSucceeded = false;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.jcoInvokationSucceeded = false;
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.jcoInvokationSucceeded = false;
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.jcoInvokationSucceeded = false;
				} catch (UnsatisfiedLinkError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.jcoInvokationSucceeded = false;
				}
	        }
		}
		
		return returnValue;
	}
	
	public boolean validateNativeArtifact() {
		boolean returnValue = false;
		
		File nativeArtifact = new File(new File(this.jcoNativeDirectoryLocation), JCoSettingsValidationWidget.JCO_NATIVE_ARTIFACT_NAME);
		returnValue = nativeArtifact.exists() && nativeArtifact.isFile();
		
		String version = DLLVersion.getMsvcm80Version();
		returnValue = null != version && version.trim().length() > 0 && version.equals(DLLVersion.MSVCM80_VERSION_NEEDLE);
		this.msvcm80DLLExists = null != version && version.trim().length() > 0 && version.equals(DLLVersion.MSVCM80_VERSION_NEEDLE);
		
		version = DLLVersion.getMsvcp60Version();
		returnValue = null != version && version.trim().length() > 0 && version.equals(DLLVersion.MSVCP60_VERSION_NEEDLE);
		this.msvcp60DLLExists = null != version && version.trim().length() > 0 /* && version.equals(DLLVersion.MSVCP60_VERSION_NEEDLE) */;
		
		version = DLLVersion.getMsvcr80Version();
		returnValue = null != version && version.trim().length() > 0 && version.equals(DLLVersion.MSVCR80_VERSION_NEEDLE);
		this.msvcr80DLLExists = null != version && version.trim().length() > 0 && version.equals(DLLVersion.MSVCR80_VERSION_NEEDLE);
		
		return returnValue;
	}
	
	public boolean validateJavaArchive() {
		boolean returnValue = false;
		
		JarFile jar;
		Manifest mf;
		
		try {
			jar = new JarFile(this.jcoJavaArchiveLocation);
			mf = jar.getManifest();
			Attributes attribs = mf.getMainAttributes();
			String className = attribs.getValue("Main-Class"); //$NON-NLS-1$
			returnValue = null != className && className.trim().length() > 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	/**
	 * @return the jcoJavaArchiveLocation
	 */
	protected String getJcoJavaArchiveLocation() {
		return jcoJavaArchiveLocation;
	}

	/**
	 * @param jcoJavaArchiveLocation the jcoJavaArchiveLocation to set
	 */
	protected void setJcoJavaArchiveLocation(String jcoJavaArchiveLocation) {
		this.jcoJavaArchiveLocation = jcoJavaArchiveLocation;
	}

	/**
	 * @return the jcoNativeDirectoryLocation
	 */
	protected String getJcoNativeDirectoryLocation() {
		return jcoNativeDirectoryLocation;
	}

	/**
	 * @param jcoNativeDirectoryLocation the jcoNativeDirectoryLocation to set
	 */
	protected void setJcoNativeDirectoryLocation(String jcoNativeDirectoryLocation) {
		this.jcoNativeDirectoryLocation = jcoNativeDirectoryLocation;
	}

	/**
	 * @return the jcoJARLocationIsValid
	 */
	protected boolean isJcoJARLocationIsValid() {
		return jcoJARLocationIsValid;
	}

	/**
	 * @param jcoJARLocationIsValid the jcoJARLocationIsValid to set
	 */
	protected void setJcoJARLocationIsValid(boolean jcoJARLocationIsValid) {
		this.jcoJARLocationIsValid = jcoJARLocationIsValid;
	}

	/**
	 * @return the jcoNativeDirectoryIsValid
	 */
	protected boolean isJcoNativeDirectoryIsValid() {
		return jcoNativeDirectoryIsValid;
	}

	/**
	 * @param jcoNativeDirectoryIsValid the jcoNativeDirectoryIsValid to set
	 */
	protected void setJcoNativeDirectoryIsValid(boolean jcoNativeDirectoryIsValid) {
		this.jcoNativeDirectoryIsValid = jcoNativeDirectoryIsValid;
	}

	/**
	 * @return the jcoVersion
	 */
	public String getJcoVersion() {
		return jcoVersion;
	}

	/**
	 * @param jcoVersion the jcoVersion to set
	 */
	public void setJcoVersion(String jcoVersion) {
		this.jcoVersion = jcoVersion;
	}

	/**
	 * @return the jcoMiddlewareName
	 */
	public String getJcoMiddlewareName() {
		return jcoMiddlewareName;
	}

	/**
	 * @param jcoMiddlewareName the jcoMiddlewareName to set
	 */
	public void setJcoMiddlewareName(String jcoMiddlewareName) {
		this.jcoMiddlewareName = jcoMiddlewareName;
	}

	/**
	 * @return the jcoMiddlewareNativeLayerVersion
	 */
	public String getJcoMiddlewareNativeLayerVersion() {
		return jcoMiddlewareNativeLayerVersion;
	}

	/**
	 * @param jcoMiddlewareNativeLayerVersion the jcoMiddlewareNativeLayerVersion to set
	 */
	public void setJcoMiddlewareNativeLayerVersion(
			String jcoMiddlewareNativeLayerVersion) {
		this.jcoMiddlewareNativeLayerVersion = jcoMiddlewareNativeLayerVersion;
	}

	/**
	 * @return the jcoMiddlewareNativeLayerPath
	 */
	public String getJcoMiddlewareNativeLayerPath() {
		return jcoMiddlewareNativeLayerPath;
	}

	/**
	 * @param jcoMiddlewareNativeLayerPath the jcoMiddlewareNativeLayerPath to set
	 */
	public void setJcoMiddlewareNativeLayerPath(String jcoMiddlewareNativeLayerPath) {
		this.jcoMiddlewareNativeLayerPath = jcoMiddlewareNativeLayerPath;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
