package com.thorstenmarx.webtools.tracking;

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
import com.google.common.collect.Multimap;
import com.thorstenmarx.webtools.api.analytics.Fields;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author marx
 */
public class EventUtil {
	
	public static final String CUSTOM_ATTRIBUTES_PREFIX = "c_";
	public static final String SCORE_ATTRIBUTES_PREFIX = "score_";
	public static final int CUSTOM_ATTRIBUTES_PREFIX_LENGTH = CUSTOM_ATTRIBUTES_PREFIX.length();

	protected static final String HEADER_USERAGENT = "User-Agent";
	protected static final String HEADER_REFERER = "referer";

	protected static final String[] HEADERS_TO_TRY = {
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
		"REMOTE_ADDR",
		"X-Real-IP"};

	protected final CrawlerUtil crawlerUtil;
	
	protected static final List<String> RESERVERD_PARAMETERS = new ArrayList<>();
	static {
		for (Constants.Param param : Constants.Param.values()) {
			RESERVERD_PARAMETERS.add(param.value());
		}
	}
	
	public EventUtil (final CrawlerUtil crawlerUtil) {
		this.crawlerUtil = crawlerUtil;
	}
	
	
	
	/**
	 * creates a map of event data.
	 * 
	 * @param request
	 * @return 
	 */
	public Map<String, Map<String, Object>> getEventData(final HttpServletRequest request) {
		
		
		// base event data to track
		Map<String, Object> data = getEventDataInternal(request);
		Map<String, Object> meta = getEventMetaInternal(request);

		Constants.Event eventType = getEventType(request);

		// add scores
		if (Constants.Event.Score.equals(eventType)) {
			data.put(Fields.Score.value(), getScores(request));
		}
		
		// add other parameters to event
		request.getParameterMap().entrySet().stream().filter((entry) -> !ignoreParameter(entry.getKey())).forEach((entry) -> {
			if (entry.getValue().length == 1) {
				data.put(entry.getKey(), entry.getValue()[0]);
			} else if (entry.getValue().length > 1) {
				data.put(entry.getKey(), entry.getValue());
			}
		});
		
		addCustomAttributes(request, data);

		Map<String, Map<String, Object>> event = new HashMap<>();
		event.put("data", data);
		event.put("meta", meta);
		return event;
	}
	
	private void addCustomAttributes (final HttpServletRequest request, final Map<String, Object> event) {
		request.getParameterMap().keySet().stream().filter((name) -> (name.startsWith(CUSTOM_ATTRIBUTES_PREFIX))).forEach((name) -> {
			String[] values = request.getParameterValues(name);
			if (values.length == 1) {
				event.put(name, values[0]);
			} else if (values.length > 1) {
				event.put(name, values);
			}
		});
	}

	protected boolean ignoreParameter(final String name) {

		return Fields.isField(name)
				|| RESERVERD_PARAMETERS.contains(name)
				|| name.startsWith(SCORE_ATTRIBUTES_PREFIX)
				|| name.startsWith(CUSTOM_ATTRIBUTES_PREFIX);
	}

	private List<String> getScores(final HttpServletRequest request) {
		List<String> scores = new ArrayList<>();

		request.getParameterMap().keySet().stream().filter((name) -> (name.startsWith(SCORE_ATTRIBUTES_PREFIX) && !name.endsWith(SCORE_ATTRIBUTES_PREFIX))).forEach((name) -> {
			final String value = request.getParameter(name);
			final String scoreName = name.substring(name.indexOf('_') + 1);
			scores.add(scoreName + ":" + value);
		});

		return scores;
	}

	private Map<String, Object> getEventDataInternal(final HttpServletRequest request) {

		Map<String, Object> event = new HashMap<>();

		final String ip = getClientIpAddr(request);
		final String userid = getUserId(request);
		final String requestId = getRequestId(request);

		Constants.Event eventType = getEventType(request);

		event.put(Fields.UserId.value(), userid);
		event.put(Fields.RequestId.value(), requestId);
		event.put(Fields.VisitId.value(), getVisitId(request));
		event.put(Fields.IP.value(), ip.hashCode());
		event.put(Fields.Site.value(), getParameter(request, Constants.Param.SITE.value()));
		event.put(Fields.Page.value(), getParameter(request, Constants.Param.PAGE.value()));
		event.put(Fields.Referrer.value(), getParameter(request, Constants.Param.REFERRER.value()));
		
		
		// js offset is negativ
//		final String offset = getParameter(request, Constants.Param.OFFSET.value());
		ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
//		utc = utc.plusMinutes(-1 * Integer.parseInt(offset.trim()));

//		event.put(Constants.Field.TimeStamp.value(), utc.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		event.put(Fields._TimeStamp.value(), System.currentTimeMillis());
		event.put(Fields.YEAR.value(), utc.format(Constants.FORMATTER_YEAR));
		event.put(Fields.YEAR_MONTH.value(), utc.format(Constants.FORMATTER_YEAR_MONTH));
		event.put(Fields.YEAR_WEEK.value(), utc.format(Constants.FORMATTER_YEAR_WEEK));
		event.put(Fields.YEAR_MONTH_DAY.value(), utc.format(Constants.FORMATTER_YEAR_MONTH_DAY));
		
		event.put(Fields.UserAgent.value(), request.getHeader(HEADER_USERAGENT));
		event.put(Fields.Referrer.combine("header"), request.getHeader(HEADER_REFERER));
		event.put(Fields.Event.value(), eventType.value());
		event.put(Fields.IsCrawler.value(), crawlerUtil.isCrawler(request.getHeader(HEADER_USERAGENT), ip));

		return event;
	}
	private Map<String, Object> getEventMetaInternal(final HttpServletRequest request) {

		Map<String, Object> meta = new HashMap<>();

		final String ip = getClientIpAddr(request);
		meta.put(Fields.IP.value(), ip);
		

		return meta;
	}

	private Constants.Event getEventType(final HttpServletRequest request) {
		Constants.Event eventType = Constants.Event.PageView;
		if (request.getParameterMap().containsKey(Constants.Param.EVENT.value())) {
			String eventTemp = request.getParameter(Constants.Param.EVENT.value()).toLowerCase();
			eventType = Constants.Event.forValue(eventTemp);
		}
		return eventType;
	}

	private String getParameter(final HttpServletRequest request, final String param) {
		return request.getParameter(param);
	}
	
	private String getUserId(final HttpServletRequest request) {
		return request.getParameter(Constants.Param.USER_ID.value());
	}

	private String getRequestId(final HttpServletRequest request) {
		return request.getParameter(Constants.Param.REQUEST_ID.value());
	}

	private String getVisitId(final HttpServletRequest request) {
		return request.getParameter(Constants.Param.VISIT_ID.value());
	}

	protected String getClientIpAddr(HttpServletRequest request) {
		for (String header : HEADERS_TO_TRY) {
			String ip = request.getHeader(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return request.getRemoteAddr();
	}
}
