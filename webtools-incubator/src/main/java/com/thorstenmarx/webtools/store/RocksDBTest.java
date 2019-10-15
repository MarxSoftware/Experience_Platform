/*
 * Copyright (C) 2019 WP DigitalExperience
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
package com.thorstenmarx.webtools.store;

import org.iq80.leveldb.impl.Iq80DBFactory;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

/**
 *
 * @author marx
 */
public class RocksDBTest {

	public static void main(String... args) {
		RocksDB.loadLibrary();

		// the Options class contains a set of configurable DB options
		// that determines the behaviour of the database.
		try (final Options options = new Options().setCreateIfMissing(true)) {

			// a factory method that returns a RocksDB instance
			try (final RocksDB db = RocksDB.open(options, "target/rocksdb-" + System.currentTimeMillis())) {
				long before = System.currentTimeMillis();
				for (int i = 0; i < 100000; i++) {
					db.put(Iq80DBFactory.bytes("name"), Iq80DBFactory.bytes("thorsten " + 1));
				}
				long after = System.currentTimeMillis();
				System.out.format("took: %dms", (after - before));
			}
			
		} catch (RocksDBException e) {
			e.printStackTrace();
		}
	}
}
