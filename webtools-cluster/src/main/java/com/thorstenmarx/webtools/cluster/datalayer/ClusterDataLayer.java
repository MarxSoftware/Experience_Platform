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
package com.thorstenmarx.webtools.cluster.datalayer;

/*-
 * #%L
 * webtools-cluster
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
import com.google.gson.Gson;
import com.thorstenmarx.webtools.api.datalayer.Data;
import com.thorstenmarx.webtools.api.datalayer.DataLayer;
import com.thorstenmarx.webtools.cluster.persistence.LevelDBMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.jgroups.JChannel;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap;

/**
 *
 * Cluster implemtentation of the DataLayer
 *
 * @author marx
 */
public class ClusterDataLayer implements DataLayer {

	private final ReplicatedHashMap<String, String> replicatedMap;

	private final LevelDBMap leveldbMap;

	private final Index index;

	private final Gson gson = new Gson();

	public ClusterDataLayer(final JChannel channel, final File dataPath) throws IOException {
		leveldbMap = new LevelDBMap();
		leveldbMap.open(dataPath, true);
		index = new Index();
		replicatedMap = new ReplicatedHashMap<>(leveldbMap, channel);
		replicatedMap.addNotifier(new ReplicatedHashMap.Notification<String, String>() {
			@Override
			public void entrySet(final String key, final String value) {
				String[] splitted_key = split(key);
				index.addByUserKey(splitted_key[0], splitted_key[1], key);
			}

			@Override
			public void entryRemoved(final String key) {
				String[] splitted_key = split(key);
				index.removeByUserKey(splitted_key[0], splitted_key[1]);
			}

			@Override
			public void viewChange(final View view, final List mbrs_joined, final List mbrs_left) {
			}

			@Override
			public void contentsSet(final Map new_entries) {
				new_entries.keySet().forEach((key) -> {
					String[] splitted_key = split((String) key);
					index.addByUserKey(splitted_key[0], splitted_key[1], (String) key);
				});
			}

			@Override
			public void contentsCleared() {
				index.clear();
			}
		});
	}

	public void close() throws IOException {
		leveldbMap.close();
	}

	public static String uuid(final String uid, final String key) {
		return String.format("%s/%s/%s", uid, key, UUID.randomUUID().toString());
	}

	public static String[] split(final String key) {
		return key.split("/");
	}

	@Override
	public <T extends Data> Optional<T> get(String uid, String key, Class<T> clazz) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public boolean add(String uid, String key, Data value) {

		final String uuid = uuid(uid, key);
		replicatedMap.put(uuid, gson.toJson(value));
//		index.addByUserKey(uid, key, uuid);
		return true;
	}

	@Override
	public boolean exists(String uid, String key) {
		return index.containsByUserKey(uid, key);
	}

	@Override
	public void remove(String uid, String key) {
		Set<String> keys = index.findByUserKey(uid, key);
		keys.forEach((uuid) -> {
			replicatedMap.remove(uuid);
		});
//		index.removeByUserKey(uid, key);
	}

	@Override
	public void clear(final String key) {
		Set<String> findByKey = index.findByKey(key);
		findByKey.forEach((uuid) -> {
			replicatedMap.remove(uuid);
		});
	}

	@Override
	public <T extends Data> void each(BiConsumer<String, T> consumer, String key, Class<T> clazz) {
		Set<String> uuids = index.findByKey(key);
		if (uuids.isEmpty()) {
			return;
		}
		uuids.forEach((uuid) -> {
			final String uid = split(uuid)[0];
			final String data = replicatedMap.get(uuid);
			consumer.accept(uid, gson.fromJson(data, clazz));
		});
	}

	@Override
	public <T extends Data> Optional<List<T>> list(String uid, String key, Class<T> clazz) {
		if (!index.containsByUserKey(uid, key)) {
			return Optional.empty();
		}
		List<T> result = new ArrayList<>();
		Set<String> uuids = index.findByUserKey(uid, key);
		uuids.forEach((uuid) -> {
			final String value = replicatedMap.get(uuid);
			result.add(gson.fromJson(value, clazz));
		});

		return Optional.of(result);
	}

}
