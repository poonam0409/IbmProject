package com.ibm.is.sappack.cw.app.ui.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class RedirectFilter implements Filter {

	public RedirectFilter() {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		
		if (isValidSession(req)) {
			chain.doFilter(request, response);
		} else {
			req.getRequestDispatcher("/public/login.jsp").forward(request, response);
			
		}
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
	}

	private boolean isValidSession(HttpServletRequest httpServletRequest) {	
		return (httpServletRequest.getRequestedSessionId() != null) && httpServletRequest.isRequestedSessionIdValid();
	}
}
