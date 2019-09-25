package com.thorstenmarx.webtools.api.analytics;

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
import com.thorstenmarx.webtools.api.analytics.query.Aggregator;
import com.thorstenmarx.webtools.api.analytics.query.LimitProvider;
import com.thorstenmarx.webtools.api.analytics.query.Query;
import com.thorstenmarx.webtools.api.analytics.query.ShardedQuery;
import com.thorstenmarx.webtools.api.annotations.API;
import com.thorstenmarx.webtools.api.datalayer.DataLayer;
import io.reactivex.Completable;
import java.util.List;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 *
 * @author marx
 * @since 1.0.0
 */
@API(since = "1.0.0", status = API.Status.Stable)
public interface AnalyticsDB {

    /**
     * Run a query.
     *
     * @param <T> The type of the query.
     * @param query The query object.
     * @param aggregator The result aggregator.
     * @return
     */
    <T> CompletableFuture<T> query(final Query query, final Aggregator<T> aggregator);

    /**
     * Run a async query per shard.
     *
     * @param <R> The result type.
     * @param <S> The subresult type.
     * @param <Q> The query type.
     * @param query the query.
     * @return
     */
    <R, S, Q extends LimitProvider> R queryAsync(ShardedQuery<R, S, Q> query);

    /**
     * Track event in map.
     *
     * @param event the event to track.
     */
    void track(Map<String, Map<String, Object>> event);
	
	public void addFilter (final Filter filter);
	public List<Filter> getFilters ();
	public boolean hasFilters ();
}
