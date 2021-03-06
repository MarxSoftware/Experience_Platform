package com.thorstenmarx.webtools.manager.rest.endpoints;

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
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.api.TimeWindow;
import com.thorstenmarx.webtools.api.actions.SegmentService;
import com.thorstenmarx.webtools.api.actions.model.Segment;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.reports.OverviewReport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

/**
 *
 * @author marx
 */
@Path("/dashboard")
public class DashBoardEndpoint {

	private final AnalyticsDB analyticsDb;
	transient SegmentService segmentService;

	public DashBoardEndpoint() {
		analyticsDb = ContextListener.INJECTOR_PROVIDER.injector().getInstance(AnalyticsDB.class);
		segmentService = ContextListener.INJECTOR_PROVIDER.injector().getInstance(SegmentService.class);
	}

	@GET
	@Path("overview")
	public void overview(@Suspended AsyncResponse asyncResponse, @QueryParam("site") final String site) {
		CompletableFuture<JSONObject> future = CompletableFuture.supplyAsync(() -> {
			JSONObject result = new JSONObject();

			OverviewReport report = new OverviewReport(analyticsDb);
			long start = System.currentTimeMillis() - new TimeWindow(TimeWindow.UNIT.WEEK, 1).millis();
			Map<String, Object> status = report.status(site, null, start, System.currentTimeMillis());

			TreeMap<String, Long> requestValues = new TreeMap<>((Map<String, Long>) status.get("requestsPerDay"));
			TreeMap<String, Long> visitValues = new TreeMap<>((Map<String, Long>) status.get("visitsPerDay"));

			List<Map<String, Object>> points = new ArrayList<>();
			for (Map.Entry<String, Long> entry : requestValues.entrySet()) {
				Map<String, Object> point = new HashMap<>();
				point.put("type", "Request");
				point.put("Count", entry.getValue());
				point.put("Day", entry.getKey());

				points.add(point);
			}
			for (Map.Entry<String, Long> entry : visitValues.entrySet()) {
				Map<String, Object> point = new HashMap<>();
				point.put("type", "Visit");
				point.put("Count", entry.getValue());
				point.put("Day", entry.getKey());

				points.add(point);
			}

			result.put("visits", visitValues);
			result.put("requests", requestValues);
			result.put("points", points);

			return result;
		});
		future.thenApply(
				result -> asyncResponse.resume(result.toJSONString()))
				.exceptionally(
						e -> asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build()));

	}

	static class SegmentCounter {

		public final Segment segment;
		public final AtomicLong count = new AtomicLong(0);

		SegmentCounter(final Segment segment) {
			this.segment = segment;
		}

		SegmentCounter count(final long count) {
			this.count.set(count);
			return this;
		}
	}
}
