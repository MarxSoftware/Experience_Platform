package de.marx_software.webtools.reports;

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
import com.google.common.base.Strings;
import de.marx_software.webtools.api.analytics.AnalyticsDB;
import de.marx_software.webtools.api.analytics.Fields;
import de.marx_software.webtools.api.analytics.query.Aggregator;
import de.marx_software.webtools.api.analytics.query.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The EventReport.
 *
 * @author marx
 */
public class EventReport {
	
	private static final Logger LOGGER = LogManager.getLogger(EventReport.class);

	final AnalyticsDB db;
	
	public EventReport (final AnalyticsDB db) {
		this.db = db;
	}
	
	/**
	 * returns the bounce rate for a site and page
	 * 
	 * @param site the id of the side.
	 * @param event the event
	 * @param start
	 * @param end
	 * @return 
	 */
	public JSONObject statistic (final String site, final String event, final long start, final long end) {
		
		JSONObject result = new JSONObject();
		
		Query.Builder qb = Query.builder()
				.start(start)
				.end(end);
		
		if (!Strings.isNullOrEmpty(site)) {
			qb.term(Fields.Site.value(), site);
		}
		if (!Strings.isNullOrEmpty(event)) {
			qb.term(Fields.Event.value(), event);
		}
		
		Query q = qb.build();
		Future<List<Map<String, Object>>> bounceRate = db.query(q, new Aggregator<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> call() throws Exception {
				List<Map<String, Object>> events = new ArrayList<>();
				
				documents.stream().forEach((document) -> {
					// TODO: implement event report
				});
				
				return events;
			}
		});
		
		try {
			JSONArray events = new JSONArray();
			for (Map<String, Object> eventObj : bounceRate.get()) {
				events.add(new JSONObject(eventObj));
			}
			result.put("events", events);
		} catch (InterruptedException | ExecutionException ex) {
			LOGGER.error("", ex);
		}
		return result;
	}
	
}
