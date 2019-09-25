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
package com.thorstenmarx.webtools.cluster.persistence;

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
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.iq80.leveldb.*;
import java.io.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import static org.iq80.leveldb.impl.Iq80DBFactory.*;
import org.iq80.leveldb.impl.Iq80DBFactory;

/**
 *
 * @author marx
 */
public class LevelDBMap implements ConcurrentMap<String, String> {

	private DB db;
	private Gson gson = new Gson();

	public LevelDBMap() {

	}

	boolean deleteDirectory(final File directoryToBeDeleted) {
		if (!directoryToBeDeleted.exists()) {
			return true;
		}
		File[] allContents = directoryToBeDeleted.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}
		return directoryToBeDeleted.delete();
	}

	public void open(final File path, final boolean clean) throws IOException {
		if (clean) {
			deleteDirectory(path);
		}
		Options options = new Options();
		options.createIfMissing(true);
		options.errorIfExists(true);
		DBFactory factory = new Iq80DBFactory();

		db = factory.open(path, options);
	}

	public void close() throws IOException {
		db.close();
	}

	@Override
	public String putIfAbsent(String key, String value) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean remove(Object key, Object value) {
		db.delete(bytes((String) key));
		return true;
	}

	@Override
	public boolean replace(String key, String oldValue, String newValue) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String replace(String key, String value) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isEmpty() {
		try (DBIterator iterator = db.iterator()) {
			return iterator.hasNext();
		}
	}

	@Override
	public boolean containsKey(Object key) {
		return get(key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String get(Object key) {
		String stringkey = (String) key;
		return asString(db.get(bytes(stringkey)));
	}

	@Override
	public String put(String key, String value) {
		db.put(bytes(key), bytes(value));

		return value;
	}

	@Override
	public String remove(Object key) {
		String value = asString(db.get(bytes((String) key)));
		db.delete(bytes((String) key));
		return value;
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		m.forEach(this::put);
	}

	@Override
	public void clear() {
		try (DBIterator iterator = db.iterator()) {
			for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
				db.delete(iterator.peekNext().getKey());
			}
		}
	}

	@Override
	public Set<String> keySet() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Collection<String> values() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Set<Entry<String, String>> entrySet() {
		Set<Entry<String, String>> entries = new HashSet<>();
		try (DBIterator iterator = db.iterator()) {
			for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
				final String key = asString(iterator.peekNext().getKey());
				final String value = asString(iterator.peekNext().getValue());
				entries.add(new HashMap.SimpleEntry<>(key, value));
			}
		}
		
		return entries;
	}

}
