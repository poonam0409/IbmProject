//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2013, 2014                                             
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.common.ui.util;
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.connections.SAPConnectionRepository;
import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.DBSupport;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.common.ui.RMRGMode;


public class Utils {
	public  static final String NL                 = "\n"; //$NON-NLS-1$
	public  static final char   DELIM              = ',';
	private static final String HELP_PLUGIN_ID_PFX = "com.ibm.is.sappack.gen.help."; //$NON-NLS-1$
	public  static final Color  COLOR_GRAY         = new Color(null, 211, 211, 211);


	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static List<String> getTableListFromTextField(String tableText) {
		List<String> result = new ArrayList<String>();
		if (tableText == null) {
			return result;
		}

		String s = tableText;
		StringTokenizer tok = new StringTokenizer(s, DELIM + "\n\t\r "); //$NON-NLS-1$
		while (tok.hasMoreTokens()) {
			String t = tok.nextToken();
			result.add(t.trim());
		}
		return result;
	}

	public static String getTableTextFieldFromList(Collection<String> tables) {
		StringBuffer buf = new StringBuffer();
		boolean first = true;
		for (String t : tables) {
			if (!first) {
				buf.append(DELIM + NL);
			}
			buf.append(t.trim());
			first = false;
		}
		return buf.toString();
	}

	public static boolean isJobParameter(String s) {
		String[] params = getJobParameters(s);
		return params.length > 0;
	}

	public static String[] getJobParameters(String s) {
		/*
		 * if (s.length() > 2 && s.startsWith(PARAM_SIGN) &&
		 * s.endsWith(PARAM_SIGN)) {
		 * return new String[]{ s.substring(1, s.length() - 1) };
		 * }
		 * return new String[0];
		 */
		List<String> result = new ArrayList<String>();
		boolean withinParam = false;
		String lastToken = null;
		StringTokenizer tok = new StringTokenizer(s, Constants.JOB_PARAM_SEPARATOR, true);
		while (tok.hasMoreTokens()) {
			String t = tok.nextToken();
			if (t.equals(Constants.JOB_PARAM_SEPARATOR)) {
				withinParam = !withinParam;
				if (!withinParam) {
					result.add(lastToken);
					lastToken = null;
				}
			} else {
				lastToken = t;
			}
		}
		return result.toArray(new String[0]);
	}

	/**
	 * returns an array of length two consisting of the parameter set and
	 * parameter name if the string denotes a parameter
	 * in a parameter set.
	 * Returns null otherwise.
	 */
	public static String[] splitJobParameter(String paramString) {
		int ix = paramString.indexOf('.');
		if (ix == -1) {
			return null;
		}
		String[] result = new String[2];
		result[0] = paramString.substring(0, ix);
		result[1] = paramString.substring(ix + 1);
		return result;
	}

	public static void addStringToListProperty(ConfigurationBase map, String property,
		String separator, String newValue) {
		String s = map.get(property);
		boolean found = false;
		if (s == null) {
			s = newValue;
		} else {
			StringTokenizer tok = new StringTokenizer(s, separator);
			while (tok.hasMoreTokens()) {
				String next = tok.nextToken();
				if (next.equals(newValue)) {
					found = true;
					break;
				}
			}
			if (!found) {
				s += separator + newValue;
			}
		}
		if (!found) {
			map.put(property, s);
		}
	}

	public static void removeStringFromListProperty(ConfigurationBase map, String property,
		String separator, String newValue) {
		String s = map.get(property);
		if (s == null) {
			return;
		}
		StringBuffer newProp = new StringBuffer();
		StringTokenizer tok = new StringTokenizer(s, separator);
		while (tok.hasMoreTokens()) {
			String next = tok.nextToken();
			if (!next.equals(newValue)) {
				if (newProp.length() > 0) {
					newProp.append(separator);
				}
				newProp.append(next);
			}
		}
		map.put(property, newProp.toString());
	}

	public static List<String> getListProperty(ConfigurationBase map, String property,
		String separator) {
		String s = map.get(property);
		List<String> result = new ArrayList<String>();
		if (s != null) {
			StringTokenizer tok = new StringTokenizer(s, separator);
			while (tok.hasMoreTokens()) {
				String next = tok.nextToken();
				result.add(next);
			}
		}
		return result;

	}

	public static int getComboIndex(ConfigurationBase map, String property, String[] possibleValues) {
		int val = map.getInt(property);
		return val;
		/*
		 * if (val == null) {
		 * return -1;
		 * }
		 * for (int i=0; i<possibleValues.length; i++) {
		 * if (val.equals(possibleValues[i])) {
		 * return i;
		 * }
		 * }
		 * return -1;
		 */
	}

	public static RMRGMode getMode(String modeID) {
		for (RMRGMode mode : ModeManager.getInstalledModes()) {
			if (mode.getID().equals(modeID)) {
				return mode;
			}
		}
		return null;
	}

	public static String getText(Text t) {
		String s = t.getText();
		if (s == null) {
			return null;
		}
		s = s.trim();
		if (s.length() == 0) {
			return null;
		}
		return s;
	}

	public static int getIntValue(Text t, int defaultValue) {
		String s = Utils.getText(t);
		if (s == null) {
			return defaultValue;
		}
		int result;
		try {
			result = Integer.parseInt(s);
		} catch (NumberFormatException exc) {
			result = defaultValue;
		}
		return result;
	}

	public static String getText(Combo c) {
		String s = c.getText();
		if (s == null) {
			return null;
		}
		s = s.trim();
		if (s.length() == 0) {
			return null;
		}
		return s;
	}

	public static void setText(Text t, String s) {
		if (s == null) {
			s = ""; //$NON-NLS-1$
		}
		t.setText(s);
	}

	public static boolean isValidConnectionName(String name, String[] msg) {
		if (name == null || name.length() == 0) {
			msg[0] = Messages.Utils_0;
			return false;
		}
		if (name.contains(SAPConnectionRepository.IIS_SAP_CONN_SEP)) {
			msg[0] = MessageFormat.format(Messages.Utils_1, SAPConnectionRepository.IIS_SAP_CONN_SEP);
			return false;
		}
		return true;
	}

	public static void showUnexpectedException(Shell shell, Throwable t) {
		Activator.logException(t);
		String errMsg = getExceptionMessage(t);
		String message = MessageFormat.format(Messages.Utils_2, errMsg);
		MessageDialog.openError(shell, Messages.Utils_3, message);
	}

	public static String getExceptionMessage(Throwable t) {
		String errMsg = t.getLocalizedMessage();
		if (errMsg == null) {
			errMsg = t.getMessage();
			if (errMsg == null) {
				errMsg = t.getClass().getName();
			}
		}
		return errMsg;
	}

	public static String[] getDomainAndProjectFromFullProjectName(String fullProjectName) {
		int ix = fullProjectName.indexOf('/');
		if (ix == -1) {
			return null;
		}

		String[] result = new String[2];
		result[0] = fullProjectName.substring(0, ix);
		result[1] = fullProjectName.substring(ix + 1);
		return result;
	}

	public static String getWSRelativePath(IFile f) {
		return f.getFullPath().toString();
	}


	public static String getHelpID(String id) {
		return HELP_PLUGIN_ID_PFX + id;
	}

	public static void saveZip(byte[] content, String filename) throws IOException {
		FileOutputStream fileOutputStream = null;

		try {
			fileOutputStream = new FileOutputStream(filename);
			fileOutputStream.write(content);
			fileOutputStream.flush();
		} 
		finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				}
				catch (IOException e) {
					// Nothing to be done here
				}
			}
		}
	}

	public static DBSupport.DBInstance getDBInstance(IFile file) {
		DBSupport.DBInstance dbInst = null;
		try {
			dbInst = DBSupport.createDBInstance(new File(file.getLocation().toOSString()));
		}
		catch (JobGeneratorException e) {
			Activator.logException(e);
			return null;
		}
		return dbInst;
	}

}
