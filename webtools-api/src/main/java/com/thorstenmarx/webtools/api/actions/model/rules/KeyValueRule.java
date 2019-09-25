package com.thorstenmarx.webtools.api.actions.model.rules;

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
import com.thorstenmarx.webtools.api.actions.model.Rule;
import com.thorstenmarx.webtools.collection.CounterMapMap;
import java.util.Arrays;
import java.util.Map;


/**
 * Eine einfache Key-Value Rule die generisch verwendet werden aknn
 *
 * Ein User muss ein Feld (key) mit dem Wert (value) aufweisen.
 *
 * @author thmarx
 */
public class KeyValueRule extends Rule<KeyValueRule> {

	private String key;
	private String[] values;

	public KeyValueRule() {

	}

	public String key() {
		return key;
	}

	public KeyValueRule key(final String key) {
		this.key = key;
		return this;
	}

	public String[] values() {
		return values;
	}

	public KeyValueRule values(final String[] values) {
		this.values = values;
		return this;
	}

	@Override
	public void extendQuery(final Query query) {
		query.multivalueTerms().put(key, values);
	}

	@Override
	public String toString() {
		return "KeyValueRule{" + key + "=" + values + "}";
	}

	@Override
	protected void extendJson(final JSONObject rule) {
		rule.put("type", "keyvalue");
		rule.put("key", values);
		rule.put("value", values);
	}

	public static Rule fromJson(final JSONObject jsonRule) {
		KeyValueRule rule = new KeyValueRule();
		Rule.extendBasic(jsonRule, rule);

		rule.key(jsonRule.getString("key"));
		rule.values(jsonRule.getJSONArray("value").toArray(new String[0]));

		return rule;
	}

	@Override
	public void handle(final ShardDocument doc, final CounterMapMap<String, String> results) {
		final String docValue = doc.document.getString(key);

		if (Arrays.stream(values).anyMatch(docValue::equals)) {
			final String userid = doc.document.getString("userid");
			results.add(userid, docValue, 1);
		}
	}

	@Override
	public boolean match(Map<String, Integer> testValues) {
		for (String value : values) {
			if (testValues.containsKey(value) && testValues.get(value) >= 1) {
				// Anzahl der nötigen Events ist erreicht
				return true;
			}
		}

		return false;
	}
}
