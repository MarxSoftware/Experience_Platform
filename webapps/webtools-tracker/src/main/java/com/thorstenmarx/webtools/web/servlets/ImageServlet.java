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
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.CharStreams;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.base.Configuration;
import com.thorstenmarx.webtools.tracking.CrawlerUtil;
import com.thorstenmarx.webtools.tracking.EventUtil2;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import javax.imageio.ImageIO;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author thmarx
 */
@WebServlet(name = "TrackingPixel", value = "/pixel", asyncSupported = true)
public class ImageServlet extends HttpServlet {

	private static final Logger LOGGER = LogManager.getLogger(ImageServlet.class);

	private static byte[] image = null;

	private static final String RESPONSE = "";
	private static final byte[] RESPONSE_BYTES = RESPONSE.getBytes(Charsets.UTF_8);

	private final EventUtil2 eventUtil;
	private Configuration configuration;

	public ImageServlet() {
		this.eventUtil = new EventUtil2(ContextListener.INJECTOR_PROVIDER.injector().getInstance(CrawlerUtil.class));
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		configuration = ContextListener.INJECTOR_PROVIDER.injector().getInstance(Configuration.class);

		try {
			ImageServlet.image = BufferedImageToByte(getImage());
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Content-Type", "text/plain");
		response.setHeader("Content-Length", String.valueOf(RESPONSE_BYTES.length));

		response.getOutputStream().write(RESPONSE_BYTES);
		if (isCrawler(request)) {
			return;
		}

		final AsyncContext asyncContext = request.startAsync(request, response);

		asyncContext.start(() -> {
			try {

				final String body = CharStreams.toString(request.getReader());
				final Multimap<String, String> parameters = this.splitQuery(body);

				Map<String, Map<String, Object>> event = eventUtil.getEventData(parameters, (HttpServletRequest) asyncContext.getRequest());

				// TODO: to njot track into anayltics db, generate post instead
//				ContextListener.INJECTOR_PROVIDER.injector().getInstance(AnalyticsDB.class).track(event);
				JSONObject jsonEvent = new JSONObject();
				jsonEvent.put("data", event.get("data"));
				jsonEvent.put("meta", event.get("meta"));

				HttpResponse httpResponse = Unirest.post((String) configuration.getMap("tracking", Collections.EMPTY_MAP).getOrDefault("url", "http://localhost:8082/track")).body(jsonEvent.toJSONString())
						.header("site", getParameter("site", request))
						.asEmpty();
			} catch (IOException ex) {
				LOGGER.error("error sending data to storage", ex);
			} finally {
				asyncContext.complete();
			}
		});
	}
	
	private String getParameter (final String name, final HttpServletRequest request) {
		String value;
		value = request.getHeader(name);
		if (Strings.isNullOrEmpty(value)) {
			value = request.getParameter(name);
		}
		
		return value;
	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		response.setHeader("Content-Type", "image/gif");
		response.setHeader("Content-Length", String.valueOf(image.length));
		response.setHeader("Content-Disposition", "inline; filename=\"1x1.gif\"");

		response.getOutputStream().write(image);
		
		if (isCrawler(request)) {
			return;
		}

		final AsyncContext asyncContext = request.startAsync(request, response);

		asyncContext.start(() -> {
			try {
				Map<String, Map<String, Object>> event = eventUtil.getEventData((HttpServletRequest) asyncContext.getRequest());

				// TODO: to njot track into anayltics db, generate post instead
//				ContextListener.INJECTOR_PROVIDER.injector().getInstance(AnalyticsDB.class).track(event);
				JSONObject jsonEvent = new JSONObject();
				jsonEvent.put("data", event.get("data"));
				jsonEvent.put("meta", event.get("meta"));

				HttpResponse httpResponse = Unirest.post((String) configuration.getMap("tracking", Collections.EMPTY_MAP).getOrDefault("url", "http://localhost:8082/track"))
						.header("site", getParameter("site", request))
						.body(jsonEvent.toJSONString()).asEmpty();
			} finally {
				asyncContext.complete();
			}
		});
	}
	
	private boolean isCrawler (final HttpServletRequest request) {
		return eventUtil.isCrawler(request);
	}

	private BufferedImage getImage() throws IOException {
		URL url = this.getClass().getResource("1x1.gif");
		return ImageIO.read(url);
	}

	private static byte[] BufferedImageToByte(BufferedImage bild) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bild, "gif", byteArrayOutputStream);
			byte[] imageData = byteArrayOutputStream.toByteArray();
			return imageData;
		} catch (IOException ex) {
			LOGGER.error("", ex);
		}

		return null;
	}

	public Multimap<String, String> splitQuery(final String queryParameters) {
	
		Multimap<String, String> resultMap = ArrayListMultimap.create();
		
		Arrays.stream(queryParameters.split("&"))
				.map(this::splitQueryParameter)
				.forEach(entry -> resultMap.put(entry.getKey(), entry.getValue()));
				//.collect(Collectors.groupingBy(SimpleImmutableEntry::getKey, LinkedHashMap::new, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
				
		return resultMap;
	}

	public SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
		final int idx = it.indexOf("=");
		final String key = idx > 0 ? it.substring(0, idx) : it;
		final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
		return new SimpleImmutableEntry<>(key, value);
	}
}
