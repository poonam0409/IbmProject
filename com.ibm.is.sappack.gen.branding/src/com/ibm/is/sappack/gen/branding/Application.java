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
// Module Name : com.ibm.is.sappack.gen.branding
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.branding;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class Application implements IApplication {

	static String copyright()
	{ return com.ibm.is.sappack.gen.branding.Copyright.IBM_COPYRIGHT_SHORT; }

	public Application() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();
		Object result = null;

		try {
			Platform.endSplash();

			if (false == PlatformUI.isWorkbenchRunning()
					&& result != IApplication.EXIT_OK) {
				PlatformUI.createAndRunWorkbench(display,
						new ApplicationWorkbenchAdvisor());
			}

			result = IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		// TODO Auto-generated method stub

	}

}
