package de.marx_software.webtools.collection;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author marx
 */
public class MapMap<E, K, V> implements Map<E, Map<K, V>> {

	Map<E, Map<K, V>> maps = new ConcurrentHashMap<>();

	public void add(E entry, K key, V value) {
		synchronized(maps) {
			if (!maps.containsKey(entry)) {
				maps.put(entry, new HashMap<>());
			}
		}

		maps.get(entry).put(key, value);
	}

	@Override
	public int size() {
		return maps.size();
	}

	@Override
	public boolean isEmpty() {
		return maps.isEmpty();
	}

	@Override
	public boolean containsKey(Object o) {
		return maps.containsKey(o);
	}

	@Override
	public boolean containsValue(Object o) {
		return maps.containsValue(o);
	}

	@Override
	public Map<K, V> get(Object o) {
		return maps.get(o);
	}

	@Override
	public Map<K, V> put(E k, Map<K, V> v) {
		return maps.put(k, v);
	}

	@Override
	public Map<K, V> remove(Object o) {
		return maps.remove(o);
	}

	@Override
	public void putAll(Map<? extends E, ? extends Map<K, V>> map) {
		maps.putAll(map);
	}

	@Override
	public void clear() {
		maps.clear();
	}

	@Override
	public Set<E> keySet() {
		return maps.keySet();
	}

	@Override
	public Collection<Map<K, V>> values() {
		return maps.values();
	}

	@Override
	public Set<Entry<E, Map<K, V>>> entrySet() {
		return maps.entrySet();
	}

}
