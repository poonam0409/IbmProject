package com.ibm.is.sappack.cw.app.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Security {
	
	private static final String GETTER_PREFIX = "get";
	private static final String SETTER_PREFIX = "set";
	
	public static void setHeadersResponseNoCache(HttpServletResponse response){
		response.setHeader("Cache-Control", "no-cache"); 
		response.setHeader("Pragma", "no-cache"); 
		response.setDateHeader ("Expires", -1); 
	}
	
	public static boolean checkRefererURL(HttpServletRequest request) {
		// compare webApp context root with referer context root to prevent CSRF
		String contextRoot = request.getRequestURL().toString();
		String refererRoot = request.getHeader("referer");
		String[] contextUrl = contextRoot.split("/");
		String[] refererUrl = null;
		boolean validReferer = false;

		if (refererRoot != null) {
			refererUrl = refererRoot.split("/");
		}

		if (refererUrl != null && refererUrl.length > 1) {
			if (contextUrl[2].equals(refererUrl[2])) {
				validReferer = true;
			}
		}

		return validReferer;
	}
	
	public static void replaceHarmfulChars(Object object) {
		try {
			Class<?> cls = Class.forName(object.getClass().getName());
			Method methodArray[] = cls.getDeclaredMethods();
			
			for (int i = 0; i < methodArray.length; i++) {
				Method m = methodArray[i];
				
				if (m.getReturnType().equals(String.class) && m.getName().startsWith(GETTER_PREFIX)) {
					String methodName = m.getName();
					String baseName = methodName.substring(GETTER_PREFIX.length());
					String returnValue = (String)m.invoke(object, new Object[0]);
					
					if (!(returnValue == null || returnValue.isEmpty())) {
						for (int j = 0; j < methodArray.length; j++) {
							Method n = methodArray[j];
							
							if (n.getName().startsWith(SETTER_PREFIX) && n.getName().contains(baseName)) {
								n.invoke(object, replaceHarmfulCharsInValue(returnValue));
							}
						}
					}
				}
			}
		} catch (ClassNotFoundException e) {
			Util.throwInternalErrorToClient(e);
		} catch (IllegalArgumentException e) {
			Util.throwInternalErrorToClient(e);
		} catch (IllegalAccessException e) {
			Util.throwInternalErrorToClient(e);
		} catch (InvocationTargetException e) {
			Util.throwInternalErrorToClient(e);
		}
	}
	
	private static String replaceHarmfulCharsInValue(String value) {
		if (value == null) {
			return null;
		}
		
		StringBuffer result = new StringBuffer(value.length());

		for (int i = 0; i < value.length(); ++i) {
			switch (value.charAt(i)) {
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '"':
				result.append("&quot;");
				break;
			case '\'':
				result.append("&#39;");
				break;
			case '%':
				result.append("&#37;");
				break;
			case ';':
				result.append("&#59;");
				break;
			case '(':
				result.append("&#40;");
				break;
			case ')':
				result.append("&#41;");
				break;
			case '&':
				result.append("&amp;");
				break;
			case '+':
				result.append("&#43;");
				break;
			default:
				result.append(value.charAt(i));
				break;
			}
		}
		return result.toString();
	}
}
