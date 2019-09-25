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
package com.thorstenmarx.webtools.actions.segmentation.mocks;

/*-
 * #%L
 * webtools-actions
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

/**
 *
 * @author marx
 */
import com.alibaba.fastjson.JSONObject;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.analytics.Filter;
import com.thorstenmarx.webtools.api.analytics.query.Aggregator;
import com.thorstenmarx.webtools.api.analytics.query.LimitProvider;
import com.thorstenmarx.webtools.api.analytics.query.Query;
import com.thorstenmarx.webtools.api.analytics.query.ShardDocument;
import com.thorstenmarx.webtools.api.analytics.query.ShardedQuery;
import com.thorstenmarx.webtools.tracking.referrer.ReferrerFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

/**
 *
 * @author marx
 */
public class MockAnalyticsDB implements AnalyticsDB {

	protected final List<ShardDocument> documents = new ArrayList<>();

	@Override
	public <R, S, Q extends LimitProvider> R queryAsync(ShardedQuery<R, S, Q> sq) {
		return ForkJoinPool.commonPool().invoke(new MockAsyncShardQuery<>(this, sq));
	}

	@Override
	public <T> CompletableFuture<T> query(Query query, Aggregator<T> aggregator) {
		try {
			aggregator.documents(documents);

			return CompletableFuture.supplyAsync(() -> {
				try {
					return aggregator.call();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			});
//			return pool.submit(aggregator);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void track(Map<String, Map<String, Object>> event) {
		final JSONObject jsonEvent = new JSONObject();
		jsonEvent.putAll(event);
		ReferrerFilter.filter(jsonEvent);
		
		documents.add(new ShardDocument("mock", jsonEvent.getJSONObject("data")));
		
	}

	@Override
	public void addFilter(Filter filter) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<Filter> getFilters() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean hasFilters() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
