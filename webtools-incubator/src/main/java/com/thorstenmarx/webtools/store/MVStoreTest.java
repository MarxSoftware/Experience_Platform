package com.thorstenmarx.webtools.store;

/*-
 * #%L
 * webtools-incubator
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
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.xerial.snappy.Snappy;

/**
 *
 * @author marx
 */
public class MVStoreTest {

	private long timestamp = System.currentTimeMillis();

	public MVStoreTest() {

	}

	public void work() throws IOException {
		createdb(false);
		createdb(true);
	}

	public void createdb(final boolean compression) throws IOException {

		System.out.println("create db: " + compression);

		final String storeName = "target/mv_store-" + (compression ? "compressed" : "unpompressed") + "-" + timestamp;

		MVStore store = MVStore.open(storeName);

		Set<String> values = new HashSet<>();
		try {
			MVMap<String, byte[]> map = store.openMap("test");
			long before = System.currentTimeMillis();
			for (int i = 0; i < 100000; i++) {
				if (compression) {
					map.put("name " + i, Snappy.compress("thorsten" + i));
					map.put("name " + i, Snappy.compress("thorsten   marx" + i));
				} else {
					map.put("name " + i, ("thorsten" + i).getBytes());
					map.put("name " + i, ("thorsten   marx" + i).getBytes());
				}

			}
			long after = System.currentTimeMillis();
			System.out.println("write took: " + (after - before) + "ms");

			before = System.currentTimeMillis();
			for (int i = 0; i < 100000; i++) {
				if (compression) {
					final String value = Snappy.uncompressString(map.get("name " + i));
					values.add(value);
				} else {
					final String value = new String(map.get("name " + i));
					values.add(value);
				}

			}
			after = System.currentTimeMillis();
			System.out.println("");
			System.out.println("read took: " + (after - before) + "ms");
		} finally {
			store.close();
		}
	}

	public static void main(String[] args) throws IOException {
		MVStoreTest mvstore = new MVStoreTest();
		mvstore.work();
	}
}
