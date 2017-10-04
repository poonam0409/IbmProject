package com.ibm.iis.sappack.gen.common.ui.util;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

import com.ibm.is.sappack.gen.common.ui.Activator;

public class ImageProvider {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	static ImageRegistry registry; 
	
	static {
		registry = new ImageRegistry();
		initializeImageRegistry(registry);
	}
	
	static void initializeImageRegistry(ImageRegistry registry) {
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		String[][] images = new String[][] { //
				// new icons
				{ "icons/SAP_rapid_generator_config_16.png", ImageProvider.IMAGE_RGCONF_16 }, //$NON-NLS-1$
				{ "icons/SAP_rapid_generator_config_45.png", ImageProvider.IMAGE_RGCONF_45 }, //$NON-NLS-1$
				{ "icons/SAP_rapid_modeler_config_16.png", ImageProvider.IMAGE_RMCONF_16 }, //$NON-NLS-1$
				{ "icons/SAP_rapid_modeler_config_45.png", ImageProvider.IMAGE_RMCONF_45 }, //$NON-NLS-1$
	//			{ "icons/SAP_Rapid_Modeler_16.png", ImageProvider.IMAGE_SOC_16 }, //$NON-NLS-1$
	//			{ "icons/SAP_Rapid_Modeler_45.png", ImageProvider.IMAGE_SOC_45 }, //$NON-NLS-1$
				{ "icons/SAP_abap_table_list_16.png", ImageProvider.IMAGE_ABAP_TABLES_16 }, //$NON-NLS-1$
				{ "icons/SAP_abap_table_list_45.png", ImageProvider.IMAGE_ABAP_TABLES_45 }, //$NON-NLS-1$
				//{ "icons/table.gif", ImageProvider.IMAGE_TABLES_16 }, //$NON-NLS-1$
				{ "icons/SAP_Idoc _segment_list_16.png", ImageProvider.IMAGE_IDOCS_16 }, //$NON-NLS-1$
				{ "icons/SAP_idoc_segment_list_45.png", ImageProvider.IMAGE_IDOCS_45 }, //$NON-NLS-1$
				{ "icons/SAP_IS_connections_folder_16.png", ImageProvider.IMAGE_IIS_CONNECTION_FOLDER }, //$NON-NLS-1$
				{ "icons/SAP_IS_connection_16.png", ImageProvider.IMAGE_IIS_CONNECTION_16 }, //$NON-NLS-1$
				{ "icons/SAP_IS_connection_45.png", ImageProvider.IMAGE_IIS_CONNECTION_45 }, //$NON-NLS-1$

				{ "icons/SAP_cwdb_connections_folder_16.png", ImageProvider.IMAGE_CWDB_CONNECTION_FOLDER }, //$NON-NLS-1$
				{ "icons/SAP_cwdb_connection_16.png", ImageProvider.IMAGE_CWDB_CONNECTION_16 }, //$NON-NLS-1$
				{ "icons/SAP_cwdb_connection_45.png", ImageProvider.IMAGE_CWDB_CONNECTION_45 }, //$NON-NLS-1$
				{ "icons/SAP_connections_folder_16.png", ImageProvider.IMAGE_SAP_CONNECTIONS_FOLDER }, //$NON-NLS-1$

				{ "icons/SAP_Rapid_Modeler_16.png", IMAGE_RAPID_MODELER_16}, //$NON-NLS-1$
				// old
				//	{ "icons/iDoc_column_new.png", ImageProvider.IMAGE_TECHFIELDSPAGE }, //$NON-NLS-1$
				{ "icons/SAP_ADMIN_16.png", ImageProvider.IMAGE_TABLE_IMPORT_OPTIONS }, //$NON-NLS-1$
				//{ "icons/SAP_Host_new.png", ImageProvider.IMAGE_SAP_SYSTEM_ICON }, //$NON-NLS-1$
				{ "icons/data_connection_SAP_new.png", ImageProvider.IMAGE_SAP_CONNECTION_ICON }, //$NON-NLS-1$
				{ "icons/SAP_IDOC_Segment_Type_16.png", ImageProvider.IMAGE_IDOC_SEGMENT_ICON }, //$NON-NLS-1$
				{ "icons/SAP_IDOC_TYPE_16.png", ImageProvider.IMAGE_IDOC_TYPE_ICON } //$NON-NLS-1$
		};

		for (String[] img : images) {
			ImageDescriptor imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(img[0]), null));
			registry.put(img[1], imgDesc);

		}
		String dataToolsCoreUIPluginID = "com.ibm.datatools.core.ui"; //$NON-NLS-1$
		registry.put(ImageProvider.IMAGE_DBM_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(dataToolsCoreUIPluginID, "icons/physicalModel.gif")); //$NON-NLS-1$
		registry.put(ImageProvider.IMAGE_DB_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(dataToolsCoreUIPluginID, "icons/sql.gif")); //$NON-NLS-1$
		registry.put(ImageProvider.IMAGE_DATADESIGN_PROJECT, AbstractUIPlugin.imageDescriptorFromPlugin(dataToolsCoreUIPluginID, "icons/dataDesignProject.gif")); //$NON-NLS-1$
		registry.put(ImageProvider.IMAGE_MODELS_FOLDER, AbstractUIPlugin.imageDescriptorFromPlugin(dataToolsCoreUIPluginID, "icons/modelsFolder.gif")); //$NON-NLS-1$

		registry.put(ImageProvider.IMAGE_LDM_ICON, AbstractUIPlugin.imageDescriptorFromPlugin("com.ibm.datatools.project.ui.ldm.enablement", "icons/logical_model.gif")); //$NON-NLS-1$ //$NON-NLS-2$

//		registry.put(IMAGE_VIEW_REFRESH, AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui", "icons/full/elcl16/refresh_nav.gif")); //$NON-NLS-1$ //$NON-NLS-2$
		registry.put(IMAGE_VIEW_REFRESH, AbstractUIPlugin.imageDescriptorFromPlugin("com.ibm.datatools.routines", "icons/refresh.gif")); //$NON-NLS-1$ //$NON-NLS-2$
		registry.put(IMAGE_DEPVIEW_DOWN, AbstractUIPlugin.imageDescriptorFromPlugin("com.ibm.datatools.core.ui", "icons/downarrow.gif")); //$NON-NLS-1$ //$NON-NLS-2$
//		registry.put(IMAGE_DEPVIEW_DOWN, PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_BACK));
		registry.put(IMAGE_DEPVIEW_UP, AbstractUIPlugin.imageDescriptorFromPlugin("com.ibm.datatools.core.ui", "icons/uparrow.gif")); //$NON-NLS-1$ //$NON-NLS-2$
//		registry.put(IMAGE_DEPVIEW_UP, PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_FORWARD));
	}

	
	public static Image getImage(String regKey) {
		Image img = registry.get(regKey);
		if (img == null) {
			img = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
		return img;
	}

	public static ImageDescriptor getImageDescriptor(String regKey) {
		ImageDescriptor desc = registry.getDescriptor(regKey);
		if (desc == null) {
			desc = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT);
		}
		return desc;
	}

	public static final String IMAGE_RGCONF_16 = "IMAGE_RGCONF_16"; //$NON-NLS-1$
	public static final String IMAGE_RGCONF_45 = "IMAGE_RGCONF_45"; //$NON-NLS-1$
	public static final String IMAGE_RMCONF_16 = "IMAGE_RMCONF_16"; //$NON-NLS-1$
	public static final String IMAGE_RMCONF_45 = "IMAGE_RMCONF_45"; //$NON-NLS-1$
//	public static final String IMAGE_SOC_16 = "IMAGE_RM_16"; //$NON-NLS-1$
//	public static final String IMAGE_SOC_45 = "IMAGE_RM_45"; //$NON-NLS-1$
	public static final String IMAGE_ABAP_TABLES_16 = "IMAGE_ABAP_TABLES_16"; //$NON-NLS-1$
	public static final String IMAGE_ABAP_TABLES_45 = "IMAGE_ABAP_TABLES_45"; //$NON-NLS-1$
	public static final String IMAGE_TABLES_16 = "IMAGE_TABLES_16"; //$NON-NLS-1$
	public static final String IMAGE_IDOCS_16 = "IMAGE_IDOCS_16"; //$NON-NLS-1$
	public static final String IMAGE_IDOCS_45 = "IMAGE_IDOCS_45"; //$NON-NLS-1$
//	public static final String IMAGE_TECHFIELDSPAGE = "IMAGE_TECHFIELDSPAGE"; //$NON-NLS-1$
	public static final String IMAGE_TABLE_IMPORT_OPTIONS = "IMAGE_TABLE_IMPORT_OPTIONS"; //$NON-NLS-1$
//	public static final String IMAGE_SAP_SYSTEM_ICON = "IMAGE_SAP_SYSTEM_ICON"; //$NON-NLS-1$
	public static final String IMAGE_LDM_ICON = "IMAGE_LDM_ICON"; //$NON-NLS-1$
	public static final String IMAGE_DBM_ICON = "IMAGE_DBM_ICON"; //$NON-NLS-1$
	public static final String IMAGE_DB_ICON = "IMAGE_DB_ICON"; //$NON-NLS-1$
	public static final String IMAGE_DATADESIGN_PROJECT = "IMAGE_DATADESIGN_PROJECT"; //$NON-NLS-1$
	public static final String IMAGE_MODELS_FOLDER = "IMAGE_MODELS_FOLDER"; //$NON-NLS-1$
	public static final String IMAGE_SAP_CONNECTIONS_FOLDER = "IMAGE_SAP_CONNECTIONS_FOLDER"; //$NON-NLS-1$
	public static final String IMAGE_SAP_CONNECTION_ICON = "IMAGE_SAP_CONNECTION_ICON"; //$NON-NLS-1$
	public static final String IMAGE_IIS_CONNECTION_16 = "IMAGE_IIS_CONNECTION_16"; //$NON-NLS-1$
	public static final String IMAGE_IIS_CONNECTION_45 = "IMAGE_IIS_CONNECTION_45"; //$NON-NLS-1$
	public static final String IMAGE_IIS_CONNECTION_FOLDER = "IMAGE_IIS_CONNECTION_FOLDER"; //$NON-NLS-1$
	public static final String IMAGE_CWDB_CONNECTION_FOLDER = "IMAGE_CWDB_CONNECTION_FOLDER"; //$NON-NLS-1$
	public static final String IMAGE_CWDB_CONNECTION_16 = "IMAGE_CWDB_CONNECTION_16"; //$NON-NLS-1$
	public static final String IMAGE_CWDB_CONNECTION_45 = "IMAGE_CWDB_CONNECTION_45"; //$NON-NLS-1$
	public static final String IMAGE_RAPID_MODELER_16 = "IMAGE_RAPID_MODELER_16";  //$NON-NLS-1$
	
	public static final String IMAGE_DEPVIEW_UP = "IMAGE_DEPVIEW_UP"; //$NON-NLS-1$
	public static final String IMAGE_DEPVIEW_DOWN = "IMAGE_DEPVIEW_DOWN"; //$NON-NLS-1$

	
	public static final String IMAGE_VIEW_REFRESH = "IMAGE_VIEW_REFRESH"; //$NON-NLS-1$
	public static final String IMAGE_ABAP_ARCHIVE_16 = "IMAGE_ABAP_ARCHIVE_16"; //$NON-NLS-1$
	public static final String IMAGE_IDOC_TYPE_ICON = "IMAGE_IDOC_TYPE_ICON"; //$NON-NLS-1$
	public static final String IMAGE_IDOC_SEGMENT_ICON = "IMAGE_IDOC_SEGMENT_ICON"; //$NON-NLS-1$

}
