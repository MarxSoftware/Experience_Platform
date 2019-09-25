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
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.CharStreams;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.ContextListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author thmarx
 */
@WebServlet(name = "RemoteTracking", value = "/track", asyncSupported = true)
public class TrackServlet extends HttpServlet {

	private static final Logger LOGGER = LogManager.getLogger(TrackServlet.class);

	private static byte[] image = null;

	public TrackServlet() {
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
	}
	

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		final AsyncContext asyncContext = request.startAsync(request, response);

		asyncContext.start(() -> {
			try {
				
				final String body = CharStreams.toString(request.getReader());
				
				
				Map<String, Map<String, Object>> parameters = new HashMap<>();
				final JSONObject jsonParameters = JSONObject.parseObject(body);
				parameters.put("data", jsonParameters.getJSONObject("data").getInnerMap());
				parameters.put("meta", jsonParameters.getJSONObject("meta").getInnerMap());
				
				ContextListener.INJECTOR_PROVIDER.injector().getInstance(AnalyticsDB.class).track(parameters);
				
			} catch (IOException ex) {
				LOGGER.error(ex);
			} finally {
				
				asyncContext.complete();
			}
		});
	}


}
