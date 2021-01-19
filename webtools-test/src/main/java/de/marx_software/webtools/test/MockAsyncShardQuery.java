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
package de.marx_software.webtools.test;

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

import de.marx_software.webtools.api.analytics.Searchable;
import de.marx_software.webtools.api.analytics.query.LimitProvider;
import de.marx_software.webtools.api.analytics.query.Query;
import de.marx_software.webtools.api.analytics.query.ShardDocument;
import de.marx_software.webtools.api.analytics.query.ShardedQuery;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 *
 * @author marx
 */
public class MockAsyncShardQuery<R, S, Q extends LimitProvider> extends RecursiveTask<R> {

	private static final long serialVersionUID = 726397759360339054L;

	transient protected final MockAnalyticsDB db;
	transient protected final Q query;

	transient protected final ShardedQuery<R, S, Q> shardedQuery;

	/**
	 * The asynchrone query.
	 *
	 * @param db
	 * @param shardedQuery
	 */
	public MockAsyncShardQuery(final MockAnalyticsDB db, final ShardedQuery<R, S, Q> shardedQuery) {
		this.db = db;
		this.query = shardedQuery.query();
		this.shardedQuery = shardedQuery;
	}

	@Override
	protected R compute() {

		// select shards
		List<Searchable> targetShards = new ArrayList<>();
		targetShards.add(new Searchable() {
			@Override
			public List<ShardDocument> search(Query query) throws IOException {
				return db.documents;
			}

			@Override
			public boolean hasData(long from, long to) {
				return !db.documents.isEmpty();
			}

			@Override
			public int size() {
				return db.documents.size();
			}
		});

		List<RecursiveTask<S>> shardTasks = new ArrayList<>();
		targetShards.stream().map(target -> shardedQuery.getSubTask(query, target)).map(task -> {
			task.fork();
			return task;
		}).forEach((task) -> {
			shardTasks.add(task);
		});
		List<S> subResults = new ArrayList<>();
		shardTasks.stream().forEach(task -> subResults.add(task.join()));

		return shardedQuery.merge(subResults);
	}
}
