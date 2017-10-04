//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2015                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.deltaextractstage.utils;

import java.util.logging.Level;

import com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger;

public class DsSapExtractorUtility {


    /**
     * throws the custom generated error messages to the DS.
     *
     * @param exObj
     * @param extractorLogger
     */
    public static void throwExtractorException(Object exObj, DsSapExtractorLogger extractorLogger) {
    	
    	   DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Error Occured: ..."+exObj});
           throw new RuntimeException(exObj.toString());
           
    }

    /**
     * throws the custom generated error messages to the DS.
     *
     * @param exObj
     */
    public static void throwExtractorException(Object exObj) {
    	DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Error Occured: ..."+exObj.toString()});
        throw new RuntimeException(exObj.toString());
    }
}
