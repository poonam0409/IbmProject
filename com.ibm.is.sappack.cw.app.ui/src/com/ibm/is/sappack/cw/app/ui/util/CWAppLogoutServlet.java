package com.ibm.is.sappack.cw.app.ui.util;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CWAppLogoutServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public CWAppLogoutServlet() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logout(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logout(request, response);
	}

	private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setHeader("Cache-Control", "no-cache, no-store");
		response.setHeader("Pragma", "no-cache");

		// delete all cookies, so session is destroyed for sure
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				cookie.setValue("token= ; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT");
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}

		// destroy session
		request.getSession(false).invalidate();
		response.sendRedirect(request.getContextPath() + "/index.jsp");
	}
}
