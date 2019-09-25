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
import java.util.Map;

/**
 *
 * @author thmarx
 */
public class PageViewRule extends Rule<PageViewRule> {

	private String page;
	private String site;
	private int count;

	public PageViewRule() {

	}

	public String site() {
		return site;
	}

	public PageViewRule site(String site) {
		this.site = site;
		return this;
	}

	public String page() {
		return page;
	}

	public PageViewRule page(String page) {
		this.page = page;
		return this;
	}

	public int count() {
		return count;
	}

	public PageViewRule count(int count) {
		this.count = count;
		return this;
	}

	@Override
	public void extendQuery(Query query) {
		query.terms().put("site", site);
		query.terms().put("page", page);
	}

	@Override
	public String toString() {
		return "PageView{" + "site=" + site + ", page=" + page + ", count=" + count + '}';
	}

	@Override
	protected void extendJson(JSONObject rule) {
		rule.put("type", "pageview");
		rule.put("page", page);
		rule.put("site", site);
		rule.put("count", count);
	}

	public static Rule fromJson(final JSONObject jsonRule) {
		PageViewRule rule = new PageViewRule();
		Rule.extendBasic(jsonRule, rule);

		rule.page(jsonRule.getString("page"));
		rule.site(jsonRule.getString("site"));
		rule.count(jsonRule.getIntValue("count"));

		return rule;
	}

	@Override
	public void handle(ShardDocument doc, CounterMapMap<String, String> results) {
		if (!doc.document.containsKey("site")) {
			return;
		}
		final String docSite = doc.document.getString("site");
		final String docPage = doc.document.getString("page");
		if (page().equals(docPage) && site().equals(docSite)) {
			final String userid = doc.document.getString("userid");

			results.add(userid, docSite + "_" + docPage, 1);
		}
	}

	@Override
	public boolean match(Map<String, Integer> values) {
		final String key = site + "_" + page;
		if (values.containsKey(key) && values.get(key) >= count) {
			// Anzahl der nötigen PageViews ist erreicht
			return true;
		}
		return false;
	}
}
