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
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.resolver.ClasspathResolver;
import com.google.common.base.Charsets;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
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
 * @author marx
 */
@WebServlet(asyncSupported = true)
public class JSConfigurationServlet extends HttpServlet {
	
	private static final Logger LOGGER = LogManager.getLogger(JSConfigurationServlet.class);
	
	private static final long serialVersionUID = -3779953422930833665L;

	MustacheFactory mustacheFactory;
	Mustache mustache;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config); //To change body of generated methods, choose Tools | Templates.
				
		ClasspathResolver resolver = new ClasspathResolver("/com/thorstenmarx/webtools/web/templates/");
		this.mustacheFactory = new DefaultMustacheFactory(resolver);
		mustache = mustacheFactory.compile("configuration.mustache");
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Content-Type", "text/javascript; charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		

		final AsyncContext asyncContext = request.startAsync(request, response);

		asyncContext.start(() -> {
			try (OutputStreamWriter writer = new OutputStreamWriter(asyncContext.getResponse().getOutputStream(), Charsets.UTF_8)) {
				Map<String, Object> scope = new HashMap<>();
				scope.put("name", "Thorsten");
				mustache.execute(writer, scope);
			} catch (IOException ex) {
				LOGGER.error("", ex);
			}

			asyncContext.complete();
		});
	}
	
	
}
