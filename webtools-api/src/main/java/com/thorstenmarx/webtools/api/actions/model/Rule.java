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
import com.alibaba.fastjson.JSONObject;
import com.thorstenmarx.webtools.api.analytics.query.Query;
import com.thorstenmarx.webtools.api.analytics.query.ShardDocument;
import com.thorstenmarx.webtools.collection.CounterMapMap;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;


/**
 * Die abstrakte Basisregel.
 *
 * @author thmarx
 * @param <T>
 */

public abstract class Rule<T extends Rule> implements Serializable {

	private static final long serialVersionUID = 3809377610139081248L;

	
	protected String id;

	public String id() {
		return id;
	}

	public void id(String id) {
		this.id = id;
	}
	
	public void extendQuery (final Query query) {	
	}
	
	/**
	 * Handles the ShardDocument in the compute part of the SegmentationWorkerThread
	 * @param doc
	 * @param results 
	 */
	abstract public void handle (final ShardDocument doc, final CounterMapMap<String, String> results);
	
	abstract public boolean match (final Map<String, Integer> values);

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.id);
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
		final Rule<?> other = (Rule<?>) obj;
		if (!Objects.equals(this.id, other.id)) {
			return false;
		}
		return true;
	}

	public JSONObject toJson () {
		JSONObject ruleObject = new JSONObject();
		
		ruleObject.put("id", id());
		
		extendJson(ruleObject);
		
		return ruleObject;
	}
	
	abstract protected void extendJson (JSONObject rule);
	
	/**
	 * sets all basic information.
	 * @param jsonRule
	 * @param rule 
	 */
	protected static void extendBasic (final JSONObject jsonRule, final Rule rule) {
		rule.id(jsonRule.getString("id"));
		
		
	}
}
