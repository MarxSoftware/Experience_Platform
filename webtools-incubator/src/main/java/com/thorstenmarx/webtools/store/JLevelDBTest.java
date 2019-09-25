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
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import org.iq80.leveldb.*;
import static org.iq80.leveldb.impl.Iq80DBFactory.*;

/**
 *
 * @author marx
 */
public class JLevelDBTest {

	public JLevelDBTest() {

	}

	public void work() throws IOException  {
		createDB(CompressionType.NONE);
		createDB(CompressionType.SNAPPY);
	}

	private void createDB(final CompressionType commpressionType) throws IOException {
		System.out.println("create db: " + commpressionType.name());
		Options options = new Options();
		options.createIfMissing(true);
		options.compressionType(commpressionType);
		DB db = factory.open(new File("target/jleveldb_" + commpressionType.name() + "-" + System.currentTimeMillis()), options);

		Set<String> values = new HashSet<>();
		try {
			long before = System.currentTimeMillis();
			for (int i = 0; i < 100000; i++) {
				db.put(bytes("name " + i), bytes("thorsten" + i));

				db.put(bytes("name " + i), bytes("thorsten   marx" + i));
			}
			long after = System.currentTimeMillis();
			System.out.println("writet took: " + (after - before) + "ms");

			before = System.currentTimeMillis();
			for (int i = 0; i < 100000; i++) {
				final String value = asString(db.get(bytes("name " + i)));
				values.add(value);
			}
			after = System.currentTimeMillis();
			System.out.println("");
			System.out.println("read took: " + (after - before) + "ms");
		} finally {
			db.close();
		}
	}

	public static void main(String[] args) throws IOException {
		JLevelDBTest leveldb = new JLevelDBTest();
		leveldb.work();
	}
}
