package com.ibm.is.sappack.cw.app.ui.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CWAppConfigurationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String showBdrUiInitParam = "SHOW_BDR_UI";

	private static final String fullUiHtmlTemplate = "cwapp/main/templates/TabMenuLauncher_Full_UI.html";
	private static final String uiWithoutBdrHtmlTemplate = "cwapp/main/templates/TabMenuLauncher_Without_BDR.html";
	private static final String defaultTemplate = fullUiHtmlTemplate;

	private boolean showBdrUi;

	public CWAppConfigurationServlet() {
		super();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.showBdrUi = Boolean.parseBoolean(config.getInitParameter(showBdrUiInitParam));
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uiTemplate = defaultTemplate;
		DataInputStream dataInput = null;
		String line;
		StringBuffer sBuffer = new StringBuffer();
		BufferedReader reader = null;

		if (!this.showBdrUi) {
			uiTemplate = uiWithoutBdrHtmlTemplate;
		}
		
		try {
			dataInput = new DataInputStream(this.getClass().getClassLoader().getResourceAsStream(uiTemplate));
			reader = new BufferedReader(new InputStreamReader(dataInput, "UTF-8"));

			while ((line = reader.readLine()) != null) {
				sBuffer.append(line);
			}

			response.getWriter().write(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (dataInput != null) {
						dataInput.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
