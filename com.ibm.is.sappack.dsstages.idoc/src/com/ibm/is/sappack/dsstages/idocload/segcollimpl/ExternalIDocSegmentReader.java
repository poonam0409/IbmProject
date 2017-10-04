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
// Module Name : com.ibm.is.sappack.dsstages.idocload.segcollimpl
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload.segcollimpl;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * An ExternalIDocSegmentReader reads IDoc data from a file previously written
 * by an ExternalIDocSegmentWriter. As soon as the object is created,
 * it points to the first IDoc in the file. A call to getNextIDocData()
 * retrieves the current IDoc and moves the pointer to the next one.
 *
 */
public class ExternalIDocSegmentReader {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.segcollimpl.Copyright.IBM_COPYRIGHT_SHORT;
	}

	DataInputStream is;

	public ExternalIDocSegmentReader(File f) throws IOException {
		this.is = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
	}

	/**
	 * Returns the current IDoc data and points to the next.
	 * Returns an Object[] r of length 4 where the entries are:
	 *  r[0]: IDoc number (java.lang.String)
	 *  r[1]: segment number (java.lang.String)
	 *  r[2]: parent segment numbet (java.lang.String)
	 *  r[3]: segment type name (java.lang.String)
	 *  r[4]: segment data (char[])
	 */
	public Object[] getNextIDocData() throws IOException {
		Object[] result = new Object[5];
		try {
			result[0] = this.is.readUTF();
		} catch (EOFException eofExc) {
			// first item was not there, that's OK, stream is finished
			return null;
		}
		result[1] = this.is.readUTF();
		result[2] = this.is.readUTF();
		result[3] = this.is.readUTF();
		result[4] = this.is.readUTF().toCharArray();
		return result;
	}

	public void close() throws IOException {
		this.is.close();
	}
}
