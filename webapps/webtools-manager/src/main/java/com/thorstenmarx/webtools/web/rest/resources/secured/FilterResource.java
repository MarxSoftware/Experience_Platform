package com.thorstenmarx.webtools.web.rest.resources.secured;

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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.api.TimeWindow;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.analytics.Fields;
import com.thorstenmarx.webtools.api.analytics.query.Aggregator;
import com.thorstenmarx.webtools.api.analytics.query.Query;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import jakarta.ws.rs.BeanParam;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("filter")
public class FilterResource {

	public static final String FILTER_PARAMETER_PREFIX = "f_";
	public static final int FILTER_PARAMETER_PREFIX_LENGTH = FILTER_PARAMETER_PREFIX.length();
	
	private static final Logger LOGGER = LogManager.getLogger(FilterResource.class);

	private AnalyticsDB db;
	
	public FilterResource () {
		this.db = ContextListener.INJECTOR_PROVIDER.injector().getInstance(AnalyticsDB.class);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public String filter(final @BeanParam FilterParamBean filter) {
		
		JSONObject result = new JSONObject();
		try {
			JSONArray array = new JSONArray();
			
			Query.Builder queryBuilder = Query.builder()
					.start(filter.start())
					.end(filter.end());
			
			if (filter.event() != null){
				queryBuilder.term(Fields.Event.value(), filter.event());
			}
			if (filter.site() != null){
				queryBuilder.term(Fields.Site.value(), filter.site());
			}
			if (filter.page() != null){
				queryBuilder.term(Fields.Page.value(), filter.page());
			}
			
			filter.filterParameters().entries().forEach((entry) -> {
				queryBuilder.term(entry.getKey(), String.valueOf(entry.getValue()));
			});
			
			Query query = queryBuilder.build();
			
			
			Future<JSONObject> jsonResult = db.query(query, new Aggregator<JSONObject>() {
				@Override
				public JSONObject call() throws Exception {
					JSONObject jsonResult = new JSONObject();
					jsonResult.put("documents", documents);
					return jsonResult;
				}
			});
			
			result.put("result", jsonResult.get());
		
			result.put("status", "ok");
		} catch (Exception e) {
			LOGGER.error("", e);
			result.put("status", "fail");
		}
		
		return result.toJSONString();
	}
	
	public static class FilterParamBean {
		@Context 
		UriInfo uriInfo;
		
		@QueryParam("site")
		private String site;
		
		@QueryParam("page")
		private String page;
		
		@QueryParam("event")
		private String event;
		
		@QueryParam("user")
		private String user;
		
		@QueryParam("start")
		private Long start = System.currentTimeMillis() - new TimeWindow(TimeWindow.UNIT.MINUTE, 5).millis();
		
		@QueryParam("end")
		private Long end = System.currentTimeMillis();

		public Multimap<String, Object> filterParameters;
		
		public Multimap<String, Object> filterParameters () {
			if (filterParameters == null) {
				MultivaluedMap<String, String> parameterMap = uriInfo.getQueryParameters();
				Multimap<String, String> params = ArrayListMultimap.create();
				
				for (Map.Entry<String, List<String>> entry : parameterMap.entrySet()) {
					if (entry.getKey().startsWith(FILTER_PARAMETER_PREFIX)) {
						String filterName = entry.getKey().substring(FILTER_PARAMETER_PREFIX_LENGTH);
						params.putAll(filterName, entry.getValue());
					}
				}
				this.filterParameters = ImmutableListMultimap.copyOf(params);
			}
			
			return filterParameters;
		}

		public String site() {
			return site;
		}

		public String page() {
			return page;
		}
		
		public String user () {
			return user;
		}

		public String event() {
			return event;
		}

		public Long start() {
			return start;
		}

		public Long end() {
			return end;
		}
		
		
	}
}
