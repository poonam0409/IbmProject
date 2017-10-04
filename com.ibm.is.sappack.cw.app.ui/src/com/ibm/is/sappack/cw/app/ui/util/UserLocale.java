package com.ibm.is.sappack.cw.app.ui.util;

import javax.servlet.http.HttpServletRequest;

public class UserLocale {
	
	private static final String LANG_DELIMITER = "-";
	
	public static String print(HttpServletRequest request) {
		return request.getLocale().getLanguage() + LANG_DELIMITER + request.getLocale().getCountry().toLowerCase();
	}
}
