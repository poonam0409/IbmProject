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

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.TextCustomPanel;
import com.ibm.is.sappack.gen.jco.panel.internal.Messages;

/**
 *
 */
public class JCoConsoleClass extends TextCustomPanel implements IPanelConstants {
	
	private final ILogger log = IMLogger.getLogger(com.ibm.is.sappack.gen.jco.panel.JCoConsoleClass.class);
	
	static String copyright() {
		return com.ibm.is.sappack.gen.jco.panel.Copyright.IBM_COPYRIGHT_SHORT;
	}

    /**
    *
    */
   public JCoConsoleClass() {
       super(Messages.PanelName); //NON-NLS-1
   }

    /**
     * @see com.ibm.cic.agent.core.api.TextCustomPanel#perform()
     */
    @Override
    public void perform() {
        // TODO
    }

}
