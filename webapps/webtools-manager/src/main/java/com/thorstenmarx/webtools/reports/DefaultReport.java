package com.thorstenmarx.webtools.reports;

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
import com.google.common.base.Strings;
import com.google.common.util.concurrent.AtomicLongMap;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.analytics.Fields;
import com.thorstenmarx.webtools.api.analytics.query.Aggregator;
import com.thorstenmarx.webtools.api.analytics.query.Query;
import com.thorstenmarx.webtools.api.analytics.query.ShardDocument;
import com.thorstenmarx.webtools.tracking.Constants;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The UserService provides divered methods to work with the analytics db.
 *
 * @author marx
 */
public class DefaultReport {
	
	private static final Logger LOGGER = LogManager.getLogger(DefaultReport.class);

	final AnalyticsDB db;
	
	public DefaultReport (final AnalyticsDB db) {
		this.db = db;
	}
	
	/**
	 * returns the status of a given site and page.
	 * 
	 * @param site the id of the side.
	 * @param page the id of the page.
	 * @param start
	 * @param end
	 * @return 
	 */
	public Map<String, Object> status (final String site, final String page, final long start, final long end) {
		Map<String, Object> status = new HashMap<>();
		
		Query.Builder qb = Query.builder()
				.start(start)
				.end(end)
				.term(Fields.Event.value(), Constants.Event.PageView.value());
		
		if (!Strings.isNullOrEmpty(site)) {
			qb.term(Fields.Site.value(), site);
		}
		if (!Strings.isNullOrEmpty(page)) {
			qb.term(Fields.Page.value(), page);
		}
		
		qb.term(Fields.IsCrawler.value(), String.valueOf(Boolean.FALSE));
		
		Query q = qb.build();
		Future<Integer> userCountFuture = db.query(q, new Aggregator<Integer>() {
			@Override
			public Integer call() throws Exception {
				return documents.stream().collect(Collectors.toCollection(() -> new TreeSet<ShardDocument>((p1, p2) -> p1.document.getString(Fields.UserId.value()).compareTo(p2.document.getString(Fields.UserId.value()))))).size();
			}
		});
		Future<Map<String, Object>> requestFuture = db.query(q, new Aggregator<Map<String, Object>>() {
			@Override
			public Map<String, Object> call() throws Exception {
				
				Map<String, Object> requestResult = new HashMap<>();
				
				documents.stream().collect(Collectors.toCollection(() -> new TreeSet<ShardDocument>((p1, p2) -> p1.document.getString(Fields.RequestId.value()).compareTo(p2.document.getString(Fields.RequestId.value()))))).size();
				AtomicLongMap<String> visitRequestCount = AtomicLongMap.create();
				
				documents.stream().map((doc) -> doc.document.getString(Fields.VisitId.value())).forEach((visit) -> {
					visitRequestCount.getAndAdd(visit, 1l);
				});
				
				long visitCount = visitRequestCount.size();
				final AtomicLong visitsWithSingleRequest = new AtomicLong(0);
				visitRequestCount.asMap().entrySet().stream().filter((entry) -> (entry.getValue() <= 1)).forEach((_item) -> {
					visitsWithSingleRequest.getAndIncrement();
				});
				
				
				long requestCount = visitRequestCount.sum();
				float bounceRate = ((float) 100 / (float) visitCount) * (float) visitsWithSingleRequest.get();
				float requestsPerVisit = (float) requestCount / (float)visitRequestCount.size();
				
				requestResult.put("bounceRate", bounceRate);
				requestResult.put("requestsPerVisit", requestsPerVisit);
				requestResult.put("requestCount", requestCount);
				
				return requestResult;
			}
		});
		Future<Integer> visitCountFuture = db.query(q, new Aggregator<Integer>() {
			@Override
			public Integer call() throws Exception {
				return documents.stream().collect(Collectors.toCollection(() -> new TreeSet<ShardDocument>((p1, p2) -> p1.document.getString(Fields.VisitId.value()).compareTo(p2.document.getString(Fields.VisitId.value()))))).size();
			}
		});
		
		try {
			status.put("userCount", userCountFuture.get());
			status.put("visitCount", visitCountFuture.get());
			
			Map<String, Object> requestMap = requestFuture.get();
			status.putAll(requestMap);
			
		} catch (InterruptedException | ExecutionException ex) {
			LOGGER.error("", ex);
		}
		
		return status;
	}
	
	
	/**
	 * returns the bounce rate for a site and page
	 * 
	 * @param site the id of the side.
	 * @param page the id of the page.
	 * @param start
	 * @param end
	 * @return 
	 */
	public JSONObject statistic (final String site, final String page, final long start, final long end) {
		
		JSONObject result = new JSONObject();
		
		Query.Builder qb = Query.builder()
//				.start(now - MINUTES_FIVE)
				.start(start)
				.end(end)
				.term(Fields.Event.value(), Constants.Event.PageView.value());
		
		if (!Strings.isNullOrEmpty(site)) {
			qb.term(Fields.Site.value(), site);
		}
		if (!Strings.isNullOrEmpty(page)) {
			qb.term(Fields.Page.value(), page);
		}
		
		Query q = qb.build();
		Future<Map<String, Float>> bounceRate = db.query(q, new Aggregator<Map<String, Float>>() {
			@Override
			public Map<String, Float> call() throws Exception {
				Map<String, Float> result = new HashMap<>();
				
				AtomicLongMap<String> visitRequestCount = AtomicLongMap.create();
				
				for (ShardDocument doc : documents) {
					String visit = doc.document.getString(Fields.VisitId.value());
					visitRequestCount.getAndAdd(visit, 1l);
				}
				
				long visitCount = visitRequestCount.size();
				long visitsWithSingleRequest = 0;
				for (Map.Entry<String, Long> entry : visitRequestCount.asMap().entrySet()) {
					if (entry.getValue() <= 1) {
						visitsWithSingleRequest++;
					}
				}
				
				
				float bounceRate = ((float) 100 / (float) visitCount) * (float) visitsWithSingleRequest;
				float requestsPerVisit = (float) visitRequestCount.sum() / (float)visitRequestCount.size();
				
				result.put("bounceRate", bounceRate);
				result.put("requestsPerVisit", requestsPerVisit);
				
				return result;
			}
		});
		
		try {
			result.putAll(bounceRate.get());
		} catch (InterruptedException | ExecutionException ex) {
			LOGGER.error("", ex);
		}
		return result;
	}
}
