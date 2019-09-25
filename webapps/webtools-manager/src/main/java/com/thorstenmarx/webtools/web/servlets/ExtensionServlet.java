package com.thorstenmarx.webtools.web.servlets;

/*-
 * #%L
 * webtools-manager
 * %%
 * Copyright (C) 2016 - 2018 Thorsten Marx
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import com.thorstenmarx.modules.api.Module;
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.api.extensions.ServletCommandProcessorExtension;
import com.thorstenmarx.webtools.initializer.MultiModuleManager;
import java.io.IOException;
import java.util.List;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author marx
 */
public class ExtensionServlet extends HttpServlet {

	private static final Logger LOGGER = LogManager.getLogger(ExtensionServlet.class);
	private static final long serialVersionUID = 5271966649035125800L;

	public ExtensionServlet() {
	}

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");

		final AsyncContext asyncContext = request.startAsync(request, response);

		asyncContext.start(() -> {
			final String command = request.getParameter("command");
			final String extension = request.getParameter("extension");

			MultiModuleManager modules = ContextListener.INJECTOR_PROVIDER.injector().getInstance(MultiModuleManager.class);
			Module module = modules.module(extension);
			if (module != null) {
				List<ServletCommandProcessorExtension> resourceExtensions = module.extensions(ServletCommandProcessorExtension.class);
				if (resourceExtensions.size() > 0) {
					resourceExtensions.get(0).process(command, request, response);
				}
			}

			asyncContext.complete();
		});

	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}
