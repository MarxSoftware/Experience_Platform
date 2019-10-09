package com.thorstenmarx.webtools.test;

/*-
 * #%L
 * webtools-manager
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
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.thorstenmarx.webtools.api.cache.CacheLayer;
import com.thorstenmarx.webtools.api.cache.Expirable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author marx
 */
public class MockCacheLayer implements CacheLayer {

	private Cache<String, Expirable> cache;

	private KeyLookup keyLookup;

	public MockCacheLayer() {
		keyLookup = new KeyLookup();
		cache = Caffeine.newBuilder().expireAfter(new Expiry<String, Expirable>() {
			@Override
			public long expireAfterCreate(String key, Expirable value, long currentTime) {
				return value.getCacheTime();
			}

			@Override
			public long expireAfterUpdate(String key, Expirable value, long currentTime, long currentDuration) {
				return currentDuration;
			}

			@Override
			public long expireAfterRead(String key, Expirable value, long currentTime, long currentDuration) {
				return currentDuration;
			}
		}).removalListener((String identifier, Expirable value, RemovalCause cause) -> {
			final String uuid = getUUIDFromIdentifier(identifier);
			final String key = getKeyFromIdentifier(identifier);
			keyLookup.remove(key, uuid);
		}).build();

	}

	private String getKeyFromIdentifier(final String identifier) {
		return identifier.split("###")[1];
	}

	private String getUUIDFromIdentifier(final String identifier) {
		return identifier.split("###")[0];
	}

	private String identifier(final String key) {
		return String.format("%s###%s", UUID.randomUUID().toString(), key);
	}

	@Override
	public <T extends Serializable> void add(final String key, final T value, final Class<T> clazz, final long duration, final TimeUnit unit) {
		if (valueExists(key, value, clazz)) {
			return;
		}
		Expirable cache_value = new Expirable(value, unit.toNanos(duration));
		final String uuid = identifier(key);
		keyLookup.add(key, uuid);
		cache.put(uuid, cache_value);
	}
	
	private <T extends Serializable> boolean valueExists (final String key, final T value, final Class<T> clazz) {
		List<T>  values = list(key, clazz);
		
		return values.contains(value);
	}

	@Override
	public <T extends Serializable> List<T> list(final String key, final Class<T> clazz) {

		List<T> result = new ArrayList<>();
		keyLookup.getUUIDs(key).forEach((identifier) -> {
			Expirable ifPresent = cache.getIfPresent(identifier);
			if (ifPresent != null && clazz.isInstance(ifPresent.getValue())) {
				result.add(clazz.cast(ifPresent.getValue()));
			}
		});

		return result;
	}

	@Override
	public boolean exists(final String key) {
		final AtomicBoolean result = new AtomicBoolean(false);

		keyLookup.getUUIDs(key).forEach((identifer) -> {
			Expirable value = cache.getIfPresent(identifer);
			if (value != null) {
				result.set(true);
			}
		});

		return result.get();
	}

	@Override
	public void invalidate(String key) {
		keyLookup.getUUIDs(key).forEach((identifer) -> {
			cache.invalidate(identifer);
		});
	}

	private class KeyLookup {

		private Multimap<String, String> key_uuid_mapping = Multimaps.synchronizedMultimap(ArrayListMultimap.create());

		ReadWriteLock lock = new ReentrantReadWriteLock();
		Lock writeLock = lock.writeLock();

		public KeyLookup() {

		}

		public void add(final String key, final String uuid) {
			key_uuid_mapping.put(key, uuid);
		}

		
		
		public void remove(final String key, final String uuid) {
			try {
				writeLock.lock();

				if (key_uuid_mapping.containsKey(key)) {
					key_uuid_mapping.remove(key, uuid);
					if (key_uuid_mapping.get(key).isEmpty()) {
						_removeAll(key);
					}
				}

			} finally {
				writeLock.unlock();
			}
		}

		public void removeAll(final String key) {
			try {
				writeLock.lock();

				key_uuid_mapping.removeAll(key);
			} finally {
				writeLock.unlock();
			}
		}

		public void _removeAll(final String key) {
			key_uuid_mapping.removeAll(key);
		}

		public Collection<String> getUUIDs(final String key) {
			if (key_uuid_mapping.containsKey(key)) {
				return key_uuid_mapping.get(key);
			}
			return Collections.EMPTY_LIST;
		}
	}
}
