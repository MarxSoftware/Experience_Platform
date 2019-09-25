package com.thorstenmarx.webtools.api.actions.model;

/*-
 * #%L
 * webtools-api
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
import com.thorstenmarx.webtools.api.TimeWindow;
import com.thorstenmarx.webtools.api.analytics.query.LimitProvider;
import com.thorstenmarx.webtools.api.actions.model.rules.EventRule;
import com.thorstenmarx.webtools.api.actions.model.rules.PageViewRule;
import com.thorstenmarx.webtools.api.actions.model.rules.ScoreRule;
import com.thorstenmarx.webtools.api.annotations.API;
import com.thorstenmarx.webtools.api.entities.annotations.Entity;
import com.thorstenmarx.webtools.api.entities.annotations.Field;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Ein Segment entspricht in etwa eine Zielgruppe.
 *
 * Ein User wird einem Segment zugeordnet, sobald er alle Regeln eines RuleSets erfüllt.
 *
 * @author thmarx
 */

@Entity(type = "segment")
@Deprecated(since = "3.1.0")
@API(status = API.Status.Deprecated, since = "2.0.0", toRemove = "4.0.0")
public class Segment implements LimitProvider, Serializable {

	protected TimeWindow timeWindow;
	
	/**
	 * Die ID des Segmentes.
	 */
	@Field(name = "id", key = true)
	private String id;
	/**
	 * Der Name des Segmentes.
	 */
	private String name;

	private HashSet<Rule> rules = new HashSet<>();

	private String content;
	
	private boolean active;
	
	public Segment() {
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	 
	
	public Set<Rule> rules() {
		return rules;
	}

	public void removeRule(final String id) {
		for (Rule r : rules) {
			if (r.id().equals(id)) {
				rules.remove(r);
				break;
			}
		}
	}

	public Segment addRule(Rule rule) {
		if (rule.id() == null) {
			rule.id(UUID.randomUUID().toString());
		}
		rules.add(rule);
		return this;
	}

	@Override
	public long start() {
		long now = System.currentTimeMillis();
		return now - timeWindow.millis();
	}

	public Segment start(TimeWindow timeunit) {
		this.timeWindow = timeunit;
		return this;
	}
	public TimeWindow startTimeWindow() {
		return this.timeWindow;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public long end() {
		// we use the current date
		return System.currentTimeMillis();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 61 * hash + Objects.hashCode(this.id);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Segment other = (Segment) obj;
		if (!Objects.equals(this.id, other.id)) {
			return false;
		}
		return true;
	}

	public TimeWindow getTimeWindow() {
		return timeWindow;
	}

	public Segment setTimeWindow(TimeWindow timeWindow) {
		this.timeWindow = timeWindow;
		return this;
	}

	public String getId() {
		return id;
	}

	public Segment setId(String id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Segment setName(String name) {
		this.name = name;
		return this;
	}

	public HashSet<Rule> getRules() {
		return rules;
	}

	public Segment setRules(HashSet<Rule> rules) {
		this.rules = rules;
		return this;
	}

	public String getContent() {
		return content;
	}

	public Segment setContent(String content) {
		this.content = content;
		return this;
	}
	
	
	
	public JSONObject toJson () {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("id", getId());
		jsonObject.put("name", getName());
		
		if (timeWindow != null) {
			JSONObject timeunit = new JSONObject();
			timeunit.put("name", timeWindow.getUnit().name());
			timeunit.put("count", timeWindow.getCount());
			jsonObject.put("timewindow", timeunit);
		}
		
		JSONArray rulesArray = new JSONArray();
		rules.forEach((rule) -> {
			rulesArray.add(rule.toJson());
		});
		jsonObject.put("rules", rulesArray);
		
		return jsonObject;
	}
	
	public static Segment fromJson (final JSONObject jsonSegment) {
		Segment segment = new Segment();
		
		segment.setId(jsonSegment.getString("id"));
		segment.setName(jsonSegment.getString("name"));
		
		if (jsonSegment.containsKey("timewindow")) {
			JSONObject jsonTimeUnit = jsonSegment.getJSONObject("timewindow");
			TimeWindow timeunit = new TimeWindow(TimeWindow.UNIT.valueOf(jsonTimeUnit.getString("name")), jsonTimeUnit.getIntValue("count"));
			segment.timeWindow = timeunit;
		}
		
		if (jsonSegment.containsKey("rules")) {
			jsonSegment.getJSONArray("rules").stream().map(JSONObject.class::cast).forEach(jsonRule -> {
				Rule rule = null;
				if (jsonRule.getString("type").equals("score")) {
					rule = ScoreRule.fromJson(jsonRule);
				} else if (jsonRule.getString("type").equals("event")) {
					rule = EventRule.fromJson(jsonRule);
				} else if (jsonRule.getString("type").equals("pageview")) {
					rule = PageViewRule.fromJson(jsonRule);
				}
				if (rule != null) {
					segment.addRule(rule);
				}
			});
		}
		
		return segment;
	}

	@Override
	public String toString() {
		return "[Segment " + name + " / " + id + " ]";
	}

}
