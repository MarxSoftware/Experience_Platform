package de.marx_software.webtools.web.rest.resources.secured;

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
import de.marx_software.webtools.ContextListener;
import de.marx_software.webtools.api.analytics.AnalyticsDB;
import de.marx_software.webtools.tracking.CrawlerUtil;
import de.marx_software.webtools.tracking.EventUtil;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BeanParam;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The REST-Resource to track user events.
 *
 * @author thmarx
 */
@Path("track")
@Deprecated(since = "2.5.0")
public class TrackResource {

	private static final Logger LOGGER = LogManager.getLogger(TrackResource.class);

	final AnalyticsDB analyticsDB;
	private final EventUtil eventUtil;

	public TrackResource() {
		this.analyticsDB = ContextListener.INJECTOR_PROVIDER.injector().getInstance(AnalyticsDB.class);
		this.eventUtil = new EventUtil(ContextListener.INJECTOR_PROVIDER.injector().getInstance(CrawlerUtil.class));
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public String track(final @BeanParam TrackParamBean trackParams) {

		JSONObject result = new JSONObject();
		try {
			Map<String, Map<String, Object>> event = eventUtil.getEventData(trackParams.getRequest());

			ContextListener.INJECTOR_PROVIDER.injector().getInstance(AnalyticsDB.class).track(event);

			result.put("status", "ok");
		} catch (Exception e) {
			LOGGER.error("", e);
			result.put("status", "fail");
		}

		return result.toJSONString();
	}

	public static class TrackParamBean {

		@Context
		UriInfo uriInfo;

		@Context
		HttpServletRequest httpRequest;

		@QueryParam("site")
		private String site;

		@QueryParam("page")
		private String page;

		@QueryParam("event")
		private String event;

		public String site() {

			return site;
		}

		public String page() {
			return page;
		}

		public String event() {
			return event;
		}
		
		public HttpServletRequest getRequest () {
			return httpRequest;
		}
	}
}
