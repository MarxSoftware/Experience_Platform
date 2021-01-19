package de.marx_software.webtools.web.servlets;

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
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.thorstenmarx.modules.api.Module;
import com.thorstenmarx.modules.api.ModuleManager;
import de.marx_software.webtools.ContextListener;
import de.marx_software.webtools.api.extensions.FileExtension;
import de.marx_software.webtools.initializer.MultiModuleManager;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.regex.Pattern;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * Beispiel Aufruf: http://localhost/extension/file/<module_id>/<file_path>
 *
 * @author marx
 */
@WebServlet(asyncSupported = true)
public class ExtensionFileServlet extends HttpServlet {

	private static final Logger LOGGER = LogManager.getLogger(ExtensionFileServlet.class);

	private static final long serialVersionUID = -3779953422930833665L;

	private static final Pattern PATH_PATTERN = Pattern.compile("/");

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Cache-Control", "no-cache");

		final ModuleFileDescription description = getModuleFileDescription(request);
		if (description.module == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		final AsyncContext asyncContext = request.startAsync(request, response);

		asyncContext.start(() -> {
			try (OutputStreamWriter writer = new OutputStreamWriter(asyncContext.getResponse().getOutputStream(), Charsets.UTF_8)) {

				MultiModuleManager moduleManager = ContextListener.INJECTOR_PROVIDER.injector().getInstance(MultiModuleManager.class);
				Module module = moduleManager.module(description.module);
				if (module != null) {
					module.extensions(FileExtension.class).forEach(ext -> {
						FileExtension.File file = ext.getFile(description.file);
						if (file != null) {
							try {
								response.setHeader("Content-Type", file.getContentType());
								writer.write(new String(file.getContent()));
							} catch (IOException ex) {
								LOGGER.error("", ex);
							}
						} else {
							try {
								response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
							} catch (IOException ex) {
								LOGGER.error("", ex);
							}
						}
					});
				} else {
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "Module not found");
				}
			} catch (IOException ex) {
				LOGGER.error("", ex);
			}

			asyncContext.complete();
		});
	}

	private ModuleFileDescription getModuleFileDescription(final HttpServletRequest request) {

		final String filename = request.getPathInfo().substring(1);
		final String[] pathParts = PATH_PATTERN.split(filename);
		if (pathParts.length < 2) {
			return ModuleFileDescription.EMPTY;
		}
		final String moduleName = pathParts[0];
		final String moduleFile = String.join("/", Arrays.copyOfRange(pathParts, 1, pathParts.length));

		return new ModuleFileDescription(moduleName, moduleFile);
	}

	private static class ModuleFileDescription {

		public static final ModuleFileDescription EMPTY = new ModuleFileDescription(null, null);

		public final String file;
		public final String module;

		public ModuleFileDescription(final String module, final String file) {
			this.file = file;
			this.module = module;
		}
	}
}
