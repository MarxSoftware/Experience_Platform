package com.thorstenmarx.webtools.userprofile.web.servlets;

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
import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.resolver.ClasspathResolver;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.thorstenmarx.webtools.api.location.Location;
import com.thorstenmarx.webtools.api.location.LocationProvider;
import com.thorstenmarx.webtools.base.Configuration;
import com.thorstenmarx.webtools.tracking.location.MaxmindLocationProvider;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
 *
 * @author marx
 */
@WebServlet(asyncSupported = true)
public class UserProfileServlet extends HttpServlet {

	private static final Logger LOGGER = LogManager.getLogger(UserProfileServlet.class);

	private static final long serialVersionUID = -3779953422930833665L;

	final UserAgentParser parser;
	final LocationProvider locationProvider;

	MustacheFactory mustacheFactory;
	Mustache mustache;

	public UserProfileServlet() {
		try {
			this.parser = new UserAgentService().loadParser();

			Configuration config = Configuration.getInstance(new File("./webtools_data"));
			this.locationProvider = new MaxmindLocationProvider(config);

			ClasspathResolver resolver = new ClasspathResolver("/com/thorstenmarx/webtools/web/templates/");
			this.mustacheFactory = new DefaultMustacheFactory(resolver);
			mustache = mustacheFactory.compile("webtools-userprofile.mustache");
		} catch (IOException | ParseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Content-Type", "text/javascript; charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");

		final AsyncContext asyncContext = request.startAsync(request, response);

		System.out.println(request.getServerName());

		asyncContext.start(() -> {
			try (OutputStreamWriter writer = new OutputStreamWriter(asyncContext.getResponse().getOutputStream(), Charsets.UTF_8)) {

				JSONObject userProfile = new JSONObject();

				processUserAgent(request, userProfile);
				processIP(request, userProfile);

				Map<String, Object> scope = new HashMap<>();
				scope.put("userprofile", userProfile.toJSONString());
				mustache.execute(writer, scope);
			} catch (IOException ex) {
				LOGGER.error("", ex);
			}

			asyncContext.complete();
		});
	}

	private void processUserAgent(final HttpServletRequest request, final JSONObject result) {
		String userAgent = request.getHeader("User-Agent");

		if (!Strings.isNullOrEmpty(userAgent)) {
			Capabilities browserCapabilities = parser.parse(userAgent);
			if (browserCapabilities != null) {
				result.put("device_type", browserCapabilities.getDeviceType());
				result.put("browser", browserCapabilities.getBrowser());
				result.put("platform", browserCapabilities.getPlatform());
			}
		}
	}

	private void processIP(final HttpServletRequest request, final JSONObject result) {
		final String clientIP = getClientIpAddress(request);
		final Location location = locationProvider.getLocation(clientIP);
		if (location != null) {
			JSONObject geo = new JSONObject();
			geo.put("country", location.getCountry());
			geo.put("city", location.getCity());
			geo.put("postalcode", location.getPostalcode());
			geo.put("iso", location.getCountryIso());

			result.put("geo", geo);
		}
	}

	private static final String[] HEADERS_TO_TRY = {
		"X-Forwarded-For",
		"Proxy-Client-IP",
		"WL-Proxy-Client-IP",
		"HTTP_X_FORWARDED_FOR",
		"HTTP_X_FORWARDED",
		"HTTP_X_CLUSTER_CLIENT_IP",
		"HTTP_CLIENT_IP",
		"HTTP_FORWARDED_FOR",
		"HTTP_FORWARDED",
		"HTTP_VIA",
		"REMOTE_ADDR"};

	private String getClientIpAddress(HttpServletRequest request) {
		for (String header : HEADERS_TO_TRY) {
			String ip = request.getHeader(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}

		return request.getRemoteAddr();
	}
}
