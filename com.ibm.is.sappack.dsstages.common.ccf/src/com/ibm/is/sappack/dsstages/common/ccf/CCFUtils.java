//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.common.ccf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common.ccf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ascential.e2.common.CC_Exception;
import com.ascential.e2.daapi.util.CC_Calendar;
import com.ascential.e2.propertyset.CC_ErrorList;
import com.ascential.e2.propertyset.CC_Property;
import com.ascential.e2.propertyset.CC_PropertyError;
import com.ascential.e2.propertyset.CC_PropertyNameList;
import com.ascential.e2.propertyset.CC_PropertySet;
import com.ibm.is.sappack.dsstages.common.StageLogger;

public class CCFUtils {

	

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.ccf.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private static final String CLASSNAME = CCFUtils.class.getName();
	private static CC_Calendar calendar = CC_Calendar.createInstance();

	public static void throwCC_Exception(Throwable e) {
		String msg = e.getMessage();
		if (msg == null) {
			msg = e.getClass().getName();
		}
		// TODO check "fatal code argument"
		throw new CC_Exception(msg, CC_Exception.CC_X_DAAPI_INTERNAL_ERROR);
	}

	public static void handleException(Throwable t) {
		Logger logger = StageLogger.getLogger();
		logger.log(Level.SEVERE, "CC_IDOC_CommonUnexpectedException", t); //$NON-NLS-1$
		throwCC_Exception(t);
	}

	public static Map<String, String> createPropertyMap(CC_Property prop, CC_ErrorList errList,
	      String[] requiredProperties) {
		Logger logger = StageLogger.getLogger();
		final String METHODNAME = "createMapForProperty(CC_Property)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);
		if (prop == null) {
			logger.log(Level.FINE, "CC Property is null"); //$NON-NLS-1$
			logger.exiting(CLASSNAME, METHODNAME);
			return new HashMap<String, String>();
		}
		if (requiredProperties == null) {
			requiredProperties = new String[0];
		}
		List<String> reqProps = Arrays.asList(requiredProperties);
		boolean[] foundRequiredProperty = new boolean[requiredProperties.length];
		Arrays.fill(foundRequiredProperty, false);
		logger.log(Level.FINEST, "Creating map for property {0}", prop.getName()); //$NON-NLS-1$
		Map<String, String> map = new HashMap<String, String>();
		for (CC_Property p = prop.getFirstChild(); p != null; p = prop.getNextChild()) {
			String name = p.getName();
			int ix = reqProps.indexOf(name);
			if (ix >= 0) {
				foundRequiredProperty[ix] = true;
			}
			String path = p.getPathName();
			logger.log(Level.FINE, "   Property name: {0}, path: {1}", new Object[] { name, path }); //$NON-NLS-1$
			if (p.getCount() == 1) {
				String value = p.getValueAsString(0);
				map.put(name, value);
			}
			else {
				// ignore multi-values properties
				logger.log(Level.FINE, "   Propery is multi-valued, ignoring it"); //$NON-NLS-1$
			}
		}
		boolean allReqPropsFound = true;
		CC_PropertyNameList pnl = CC_PropertyNameList.createInstance();
		for (int i = 0; i < reqProps.size(); i++) {
			if (!foundRequiredProperty[i]) {
				String p = reqProps.get(i);
				pnl.addPropertyName(p);
				allReqPropsFound = false;
				logger.log(Level.SEVERE, "CC_IDOC_PropertyNotFound", new Object[] { p });
			}
		}
		if (!allReqPropsFound) {
			CC_PropertyError propErr = CC_PropertyError.createInstance();
			propErr.addPropertyNames(pnl);
			propErr.setErrorType(CC_PropertyError.CC_ERROR_MISSINGVALUE);
			propErr.setSeverity(CC_PropertyError.CC_SEVERITY_FATAL);
			errList.addError(propErr);
		}
		logger.exiting(CLASSNAME, METHODNAME);
		return map;
	}

	public static void logPropertySet(CC_PropertySet propSet) {
		Logger logger = StageLogger.getLogger();
		if (propSet != null) {
			logger.log(Level.FINE, "Property Set: {0}", propSet.serialize(null)); //$NON-NLS-1$
		}
		else {
			logger.log(Level.FINE, "Property Set is null"); //$NON-NLS-1$			
		}
	}

	/**
	 * This function converts year, month and day date components of the given calendar into a char array according to
	 * the internal DATE format of SAP.
	 * 
	 * @param calendar
	 *           - the calendar object providing year, month and day
	 * @param arraySize
	 *           - the size of the char array into which the formatted date shall be copied
	 * 
	 * @return a char array containing a properly formatted date entry
	 */
	public static char[] formatDateForSAP(CC_Calendar calendar, int arraySize) {
		Logger logger = StageLogger.getLogger();
		char[] result = new char[arraySize];
		StringBuffer sb = new StringBuffer();

		// first retrieve the date components from the calendar
		int year = calendar.getYear(); // four digit value
		int month = calendar.getMonth(); // one- or two digit value
		int day = calendar.getDay(); // one- or two digit value

		// fill the StringBuffer so that the result matches the
		// date format expected by SAP (internal format: YYYYMMDD)
		if (year <10) {
			sb.append("000"); //$NON-NLS-1$
		} else if (year < 100) {
			sb.append("00"); //$NON-NLS-1$
		} else if (year < 1000) {
			sb.append("0"); //$NON-NLS-1$
		}
		sb.append(String.valueOf(year));

		// if the month is single digit value we need to precede
		// it with a '0' to make it a double digit value
		if (month < 10) {
			sb.append("0"); //$NON-NLS-1$
		}

		sb.append(String.valueOf(month));

		// digit handling for day is the same as for month
		if (day < 10) {
			sb.append("0"); //$NON-NLS-1$
		}

		sb.append(String.valueOf(day));

		logger.log(Level.FINEST, "Converting date string ''{0}'', target array size: {1}", new Object[]{sb, arraySize});  //$NON-NLS-1$
		
		// finally, copy the StringBuffer into the char array
		sb.getChars(0, arraySize, result, 0);
		logger.log(Level.FINEST, "Conversion succesfull"); //$NON-NLS-1$
		return result;
	}

	/**
	 * This function converts a time char array in format "hh:mm:ss" into a char array according to the internal TIME
	 * format of SAP.
	 * 
	 * @param time
	 *           - the char array representing a time value
	 * @param arraySize
	 *           - the size of the char array into which the formatted date shall be copied
	 * 
	 * @return a char array containing a properly formatted time entry
	 */
	public static char[] formatTimeForSAP(char[] time, int arraySize) {
		char[] result = new char[arraySize];
		int j = 0;

		// loop over the indexes of the target char array
		for (int i = 0; i < arraySize; i++) {
			
			// if the source char array index contains a colon, we skip it
			// (meaning that we don't write it to the target char array)
			if (time[j] == ':') {
				j++;
			}

			result[i] = time[j];
			j++;
		}

		return result;
	}
	
	/**
	 * This function coverts a field of SAP type DATS (date of format YYYYMMDD) 
	 * to a CC_Calendar object.  
	 * 
	 * @param value date value in format YYYYMMDD
	 * 
	 * @return calendar object 
	 */
	public static CC_Calendar formatDateForDS(String value) {
		
		// date format expected by SAP (internal format: YYYYMMDD)
		value = value.trim();
		
		if (value.length() == 8 && !value.equals("00000000")) { //$NON-NLS-1$
			int year = Integer.parseInt(value.substring(0,4));
			int month = Integer.parseInt(value.substring(4,6));
			int day = Integer.parseInt(value.substring(6,8));
			calendar.setDate(year, month, day);			
		} else {
			// invalid calendar
			calendar.setDate(1, 1, 1);
		}
		
		
		/* Do not use ICU masking for CC_Calender as it shows really bad performance!
		 * ICU mask:
		 * yyyy for 4-digit year
		 * MM   for 2-digit month (m would be minutes)
		 * dd   for 2-digit day  
		 

		if (value.length() == 8 && !value.equals("00000000")) { //$NON-NLS-1$
			calendar.fromString(value, "yyyyMMdd"); //$NON-NLS-1$			
		} else {
			// invalid calendar
			calendar.fromString("10101", "yMMdd"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		*/
		return calendar;
	}

	
	/**
	 * This function coverts a field of SAP type TIMS (date of format hhmmss) 
	 * to a CC_Calendar object.  
	 * 
	 * @param value time value 
	 * 
	 * @return calendar object 
	 */
	public static CC_Calendar formatTimeForDS(String value) {

		value = value.trim();

		if (value.length() == 6 && !value.equals("000000")) { //$NON-NLS-1$
			int hour = Integer.parseInt(value.substring(0,2));
			int minutes = Integer.parseInt(value.substring(2,4));
			int seconds = Integer.parseInt(value.substring(4,6));
			calendar.setTime(hour, minutes, seconds, 0);			
		} else {
			// invalid calendar
			calendar.setTime(0, 0, 0, 0);
		}

		/* Do not use ICU masking for CC_Calender as it shows really bad performance!
		 * ICU mask: 
		 * HH for 2-digit hours (0-23), hh would be am/pm (1-12)
		 * MM for 2-digit minutes
		 * ss for 2-digit seconds   
		 
		calendar.fromString(value, "HHmmss"); //$NON-NLS-1$
		*/
		
		return calendar;
	}

	public static CC_Calendar formatTimeStampForDS(String value) {

		value = value.trim();

		if (value.length() == 6 && !value.equals("000000")) { //$NON-NLS-1$
			int hour = Integer.parseInt(value.substring(0,2));
			int minutes = Integer.parseInt(value.substring(2,4));
			int seconds = Integer.parseInt(value.substring(4,6));
			calendar.setTimeStamp(1,1,1, hour, minutes, seconds, 0);			
		} else if (value.length() == 8 && !value.equals("00000000")) { //$NON-NLS-1$
			int year = Integer.parseInt(value.substring(0,4));
			int month = Integer.parseInt(value.substring(4,6));
			int day = Integer.parseInt(value.substring(6,8));
			calendar.setTimeStamp(year, month, day, 0, 0, 0, 0);			
		} else {
			// invalid calendar
			calendar.setTimeStamp(1, 1, 1, 0, 0, 0, 0);			
		}

		/* Do not use ICU masking for CC_Calender as it shows really bad performance!
		 * ICU mask: 
		 * HH for 2-digit hours (0-23), hh would be am/pm (1-12)
		 * MM for 2-digit minutes
		 * ss for 2-digit seconds   
		 
		calendar.fromString(value, "HHmmss"); //$NON-NLS-1$
		*/
		
		return calendar;
	}
	
	public static char[] formatTimestampForSAP(CC_Calendar calendar, int arraySize) {
		char[] result = new char[arraySize];
		StringBuffer sb = new StringBuffer();

		// first retrieve the date components from the calendar
		int hours = calendar.getHour();
		int minutes = calendar.getMinute();
		int seconds = calendar.getSecond();
				
		// fill the StringBuffer so that the result matches the
		// date format expected by SAP (internal format: YYYYMMDD)
		if (hours <10) {
			sb.append("0"); //$NON-NLS-1$
		} 
		sb.append(String.valueOf(hours));

		// if the month is single digit value we need to precede
		// it with a '0' to make it a double digit value
		if (minutes < 10) {
			sb.append("0"); //$NON-NLS-1$
		}
		sb.append(String.valueOf(minutes));

		// digit handling for day is the same as for month
		if (seconds < 10) {
			sb.append("0"); //$NON-NLS-1$
		}
		sb.append(String.valueOf(seconds));
		
		// finally, copy the StringBuffer into the char array
		sb.getChars(0, arraySize, result, 0);
		return result;	
	}

}
