/*
 * Copyright (C) 2019 Thorsten Marx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.thorstenmarx.webtools.tracking;

/*-
 * #%L
 * webtools-tracking
 * %%
 * Copyright (C) 2016 - 2019 Thorsten Marx
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

import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.thorstenmarx.webtools.api.analytics.Fields;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author marx
 */
public class EventUtil2 extends EventUtil {
	
	public EventUtil2 (final CrawlerUtil crawlerUtil) {
		super(crawlerUtil);
	}
	
	public Map<String, Map<String, Object>> getEventData (final Multimap<String, String> parameters, final HttpServletRequest request) {
		// base event data to track
		Map<String, Object> data = getEventDataInternal(request, parameters);
		Map<String, Object> meta = getEventMetaInternal(request);

		Constants.Event eventType = getEventType(parameters);

		// add scores
		if (Constants.Event.Score.equals(eventType)) {
			data.put(Fields.Score.value(), getScores(parameters));
		}
		
		// add other parameters to event
		request.getParameterMap().entrySet().stream().filter((entry) -> !ignoreParameter(entry.getKey())).forEach((entry) -> {
			if (entry.getValue().length == 1) {
				data.put(entry.getKey(), entry.getValue()[0]);
			} else if (entry.getValue().length > 1) {
				data.put(entry.getKey(), entry.getValue());
			}
		});
		
		addCustomAttributes(parameters, data);

		Map<String, Map<String, Object>> event = new HashMap<>();
		event.put("data", data);
		event.put("meta", meta);
		return event;
	}
	
	private void addCustomAttributes (final Multimap<String, String> parameters, final Map<String, Object> event) {
		parameters.keySet().stream().filter((name) -> (name.startsWith(CUSTOM_ATTRIBUTES_PREFIX))).forEach((name) -> {
			Collection<String> values = parameters.get(name);
			if (values.size() == 1) {
				event.put(name, values.iterator().next());
			} else if (values.size() > 1) {
				event.put(name, values.toArray());
			}
		});
	}
	
	private List<String> getScores(final Multimap<String, String> parameters) {
		List<String> scores = new ArrayList<>();

		parameters.keySet().stream().filter((name) -> (name.startsWith(SCORE_ATTRIBUTES_PREFIX) && !name.endsWith(SCORE_ATTRIBUTES_PREFIX))).forEach((name) -> {
			final Collection<String> value = parameters.get(name);
			if (value.size() == 1){
				final String scoreName = name.substring(name.indexOf('_') + 1);
				scores.add(scoreName + ":" + value.iterator().next());
			}			
		});

		return scores;
	}

	private Map<String, Object> getEventDataInternal(final HttpServletRequest request, final Multimap<String, String> parameters) {

		Map<String, Object> event = new HashMap<>();

		final String ip = getClientIpAddr(request);
		final String userid = getStringParameter(parameters, Constants.Param.USER_ID.value(), "");
		final String requestId = getStringParameter(parameters, Constants.Param.REQUEST_ID.value(), "");

		Constants.Event eventType = getEventType(parameters);

		event.put(Fields._UUID.value(), UUID.randomUUID().toString());
		event.put(Fields.UserId.value(), userid);
		event.put(Fields.RequestId.value(), requestId);
		event.put(Fields.VisitId.value(), getStringParameter(parameters, Constants.Param.VISIT_ID.value(), ""));
		event.put(Fields.IP.value(), ip.hashCode());
		event.put(Fields.Site.value(), getStringParameter(parameters, Constants.Param.SITE.value(), ""));
		event.put(Fields.Page.value(), getStringParameter(parameters, Constants.Param.PAGE.value(), ""));
		event.put(Fields.Type.value(), getStringParameter(parameters, Constants.Param.TYPE.value(), ""));
		event.put(Fields.Referrer.value(), getStringParameter(parameters, Constants.Param.REFERRER.value(), ""));
		
		
		ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

		event.put(Fields._TimeStamp.value(), System.currentTimeMillis());
		event.put(Fields.YEAR.value(), utc.format(Constants.FORMATTER_YEAR));
		event.put(Fields.YEAR_MONTH.value(), utc.format(Constants.FORMATTER_YEAR_MONTH));
		event.put(Fields.YEAR_WEEK.value(), utc.format(Constants.FORMATTER_YEAR_WEEK));
		event.put(Fields.YEAR_MONTH_DAY.value(), utc.format(Constants.FORMATTER_YEAR_MONTH_DAY));
		
		event.put(Fields.UserAgent.value(), request.getHeader(HEADER_USERAGENT));
		event.put(Fields.Referrer.combine("header"), request.getHeader(HEADER_REFERER));
		event.put(Fields.Event.value(), eventType.value());
		event.put(Fields.IsCrawler.value(), isCrawler(request, ip));

		return event;
	}
	
	public boolean isCrawler (final HttpServletRequest request, final String ip) {
		return crawlerUtil.isCrawler(request.getHeader(HEADER_USERAGENT), ip);
	}
	public boolean isCrawler (final HttpServletRequest request) {
		final String ip = getClientIpAddr(request);
		return isCrawler(request, ip);
	}
	
	private Map<String, Object> getEventMetaInternal(final HttpServletRequest request) {

		Map<String, Object> meta = new HashMap<>();

		final String ip = getClientIpAddr(request);
		meta.put(Fields.IP.value(), ip);
		

		return meta;
	}

	public Constants.Event getEventType(final Multimap<String, String> parameters) {
		final String request_eventtype = getStringParameter(parameters, Constants.Param.EVENT.value(), Constants.Event.PageView.value());
		return Constants.Event.forValue(request_eventtype);
	}

	private String getStringParameter(final Multimap<String, String> parameters, final String param, final String defaultValue) {
		Collection<String> req_id = parameters.get(param);
		return req_id.size() > 0 ? req_id.iterator().next() : defaultValue;
	}
	
	private String getUserId2(final HttpServletRequest request) {
		return request.getParameter(Constants.Param.USER_ID.value());
	}

	private String getRequestId2(final Multimap<String, String> parameters) {
		Collection<String> req_id = parameters.get(Constants.Param.REQUEST_ID.value());
		return req_id.size() > 0 ? req_id.iterator().next() : "";
	}

	private String getVisitId2(final HttpServletRequest request) {
		return request.getParameter(Constants.Param.VISIT_ID.value());
	}
}
