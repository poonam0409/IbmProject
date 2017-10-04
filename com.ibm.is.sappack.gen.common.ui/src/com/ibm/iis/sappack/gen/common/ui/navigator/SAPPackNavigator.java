package com.ibm.iis.sappack.gen.common.ui.navigator;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonNavigator;

public class SAPPackNavigator extends CommonNavigator {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public SAPPackNavigator() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {

			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				Display disp = Display.getCurrent();
				if (disp == null) {
					disp = Display.getDefault();
				}
				if (disp != null) {
					disp.syncExec(new Runnable() {

						@Override
						public void run() {
							getCommonViewer().refresh();
						}

					});
				}
			}
		});
	}
}
