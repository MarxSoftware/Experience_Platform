package com.thorstenmarx.webtools.actions.dsl.rules;

/*-
 * #%L
 * webtools-actions
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
import com.thorstenmarx.webtools.api.actions.Conditional;
import com.thorstenmarx.webtools.api.analytics.query.ShardDocument;
import com.thorstenmarx.webtools.collection.CounterMapMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author thmarx
 */
public class EventRule implements Conditional {
	
	public static final String RULE = "EVENT";

	private String event;
	private int count;

	private final CounterMapMap<String, String> results;
	
	private final Set<String> users;
	
	public EventRule() {
		results = new CounterMapMap<>();
		users = new HashSet<>();
	}

	public String event() {
		return event;
	}

	public EventRule event(final String event) {
		this.event = event;
		return this;
	}

	public int count() {
		return count;
	}

	public EventRule count(int count) {
		this.count = count;
		return this;
	}


	@Override
	public String toString() {
		return "EventRule{" + "event=" + event + ", count=" + count + '}';
	}

	public Set<String> users () {
		return users;
	}
	
	@Override
	public void match() {
		results.entrySet().forEach((entry) -> {
			final String userid = entry.getKey();
			Map<String, Integer> values = entry.getValue();
			final String key = event;
			if (values.containsKey(key) && values.get(key) >= count) {
				// Anzahl der nötigen Events ist erreicht
				users.add(userid);
			}
		});
	}

	@Override
	public boolean valid() {
		return true;
	}
	
	@Override
	public void handle(final ShardDocument doc) {
		if (!doc.document.containsKey("site")) {
			return;
		}
		final String docEvent = doc.document.getString("event");
		if (event().equals(docEvent)) {
			final String userid = doc.document.getString("userid");
			results.add(userid, docEvent, 1);
		}
	}

	@Override
	public boolean affected(JSONObject event) {
		if (!event.containsKey("site")) {
			return false;
		}
		final String docSite = event.getString("site");
		final String docEvent = event.getString("event");
		if (event().equals(docEvent)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean matchs(String userid) {
		return users.contains(userid);
	}
}
