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
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The UserService provides divered methods to work with the analytics db.
 *
 * @author marx
 */
public class OverviewReport {

	private static final Logger LOGGER = LogManager.getLogger(OverviewReport.class);

	final AnalyticsDB db;

	public OverviewReport(final AnalyticsDB db) {
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
	public Map<String, Object> status(final String site, final String page, final long start, final long end) {
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

		Future<Map<String, Object>> requestFuture = db.query(q, new Aggregator<Map<String, Object>>() {
			@Override
			public Map<String, Object> call() throws Exception {

				Map<String, Object> requestResult = new HashMap<>();

				AtomicLongMap<String> requestsPerDay = AtomicLongMap.create();
				AtomicLongMap<String> visitsPerDay = AtomicLongMap.create();
				for (String key : initialKeys(start, end)) {
					requestsPerDay.put(key, 0);
					visitsPerDay.put(key, 0);
				}
				Set<String> visits = new HashSet<>();
				if (documents != null) {
					documents.forEach((sd) -> {
						final String day = sd.document.getString(Fields.YEAR_MONTH_DAY.value());
						final String visit = sd.document.getString(Fields.VisitId.value());
						requestsPerDay.addAndGet(day, 1l);
						if (!visits.contains(visit)) {
							visitsPerDay.addAndGet(day, 1l);
							visits.add(visit);
						}
					});
				}

//				requestResult.put("requestsPerDay", requestsPerDay.asMap());
//				requestResult.put("visitsPerDay", visitsPerDay.asMap());
				requestResult.put("requestsPerDay", Collections.unmodifiableMap(requestsPerDay.asMap()));
				requestResult.put("visitsPerDay", Collections.unmodifiableMap(visitsPerDay.asMap()));

				return requestResult;
			}
		});

		try {

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
	public JSONObject statistic(final String site, final String page, final long start, final long end) {

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
				float requestsPerVisit = (float) visitRequestCount.sum() / (float) visitRequestCount.size();

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

	private String[] initialKeys(final long start, final long end) {
		long diff = end - start;
		int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

		String[] values = new String[days];

		LocalDate now = LocalDate.now();
		values[0] = now.format(Constants.FORMATTER_YEAR_MONTH_DAY);
		for (int i = 1; i < days; i++) {
			now = now.minusDays(1);
			values[i] = now.format(Constants.FORMATTER_YEAR_MONTH_DAY);
		}

		return values;
	}

}
