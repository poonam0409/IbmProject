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
// Module Name : com.ibm.is.sappack.dsstages.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.deltaextractstage.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Locale;

public class Utilities {
	
	private static final String CLASSNAME = Utilities.class.getName();

	public static byte[] readInputStream(InputStream is) throws IOException {
		return readInputStream(is, 2056);
	}

	public static byte[] readInputStream(InputStream is, int initialBufferSize) throws IOException {
		byte[] result = new byte[0];
		byte[] tmp = new byte[initialBufferSize];
		int i;
		while ((i = is.read(tmp)) != -1) {
			byte[] old = result;
			result = new byte[old.length + i];
			System.arraycopy(old, 0, result, 0, old.length);
			System.arraycopy(tmp, 0, result, old.length, i);
		}
		return result;
	}

	/**
	 * getPlatformEncodingForIDocFiles
	 * 
	 * get the platform dependent encoding for the IDoc
	 * files that are used by the IDoc Listener and the IDoc
	 * extract runtime.
	 * On little endian platforms the IDoc files are encoding
	 * in UTF-16LE while on big endian platforms the IDoc files
	 * are encoded in UTF-16BE
	 * 
	 * 
	 * @return
	 */
	public static String getPlatformEncodingForIDocFiles() {

		ByteOrder byteOrder = ByteOrder.nativeOrder();
		if (byteOrder.equals(ByteOrder.LITTLE_ENDIAN)) {
			return "UTF-16LE"; //$NON-NLS-1$
		}
		/* all other supported platforms are big endian */
		return "UTF-16BE"; //$NON-NLS-1$
	}

	public static String convertConfigFilePW(String pw) {
		final int mask = 0x1F;
		char[] value = pw.toCharArray();
		for (int strindex = 0; strindex < value.length; strindex++) {
			value[strindex] ^= mask;
		}

		String decryptedPW = new String(value);
		return decryptedPW;
	}

	public static String toValidFileName(String s) {
		s = s.replaceAll("/", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		s = s.replaceAll("\\\\", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		return s;
	}

	public static String toValidConnectionName(String idocTypeName) {
		return toValidFileName(idocTypeName);
	}

	public static String idocType2FileName(String idocType) {
		return idocType.replaceAll("/", "__"); //$NON-NLS-1$//$NON-NLS-2$
	}

	public static String cleanFieldName(String fieldName) {
		if (fieldName == null || fieldName.trim().length() == 0) {
			return fieldName;
		}
		String cleanedName = fieldName.replace('/', '_');
		if (cleanedName.charAt(0) == '_') {
			cleanedName = 'A' + cleanedName.substring(1);
		}
		return cleanedName;

	}

	public static String getClientEncoding() {
		Locale loc = Locale.getDefault();
		if (localesHaveSameLanguage(loc, Locale.JAPANESE)) {
			return "Windows-31J"; //$NON-NLS-1$
		}
		if (localesHaveSameLanguageAndCountry(loc, Locale.TRADITIONAL_CHINESE)) {
			return "Big5"; //$NON-NLS-1$
		}
		if (localesHaveSameLanguageAndCountry(loc, Locale.SIMPLIFIED_CHINESE)) {
			return "GBK"; //$NON-NLS-1$
		}
		if (localesHaveSameLanguage(loc, Locale.KOREAN)) {
			return "EUC-KR"; //$NON-NLS-1$
		}
	
		return "windows-1252"; //$NON-NLS-1$
	}
	
	public static boolean localesHaveSameLanguage(Locale l1, Locale l2) {
		return l1.getISO3Language().equals(l2.getISO3Language());
		
	}

	public static boolean localesHaveSameLanguageAndCountry(Locale l1, Locale l2) {
		if (!l1.getISO3Language().equals(l2.getISO3Language())) {
			return false;
		}
		return l1.getCountry().equals(l2.getCountry());
	}

	
	/**
	 * returns the encoding of the IDocListener.log file.
	 * This contains all the encodings for all Windows language installations. 
	 */
	public static String getIDocListenerLogEncoding() {
		String enc = Utilities.getClientEncoding();
		try {
			Charset.forName(enc);
		} catch (Exception exc) {
			enc = Charset.defaultCharset().name();
		}
		return enc;
	}
	
	/**
	 * Returns the encoding of the config files. The file contents are created by the client
	 * and are stored as-is on the server.
	 */
	public static String getConfigFileEncoding() {
		return getIDocListenerLogEncoding();
	}
	
}
