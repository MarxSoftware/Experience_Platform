/**
 * WebTools-Platform
 * Copyright (C) 2016-2018  ThorstenMarx (kontakt@thorstenmarx.com)
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
package com.thorstenmarx.webtools.datalayer;

/*-
 * #%L
 * webtools-datalayer
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
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.thorstenmarx.webtools.api.datalayer.SegmentData;
import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.SqlScriptSource;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Version;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class MariaDBDataLayerTest {

	private HikariDataSource ds;

	MariaDBDataLayer datalayer;

	String id;
	EmbeddedMysql mysqld;

	@BeforeClass(groups = "integration")
	public void before() {
		
		MysqldConfig config = MysqldConfig.aMysqldConfig(Version.v5_7_latest).withPort(2315).build();
		
		mysqld = EmbeddedMysql.anEmbeddedMysql(config)
				.addSchema("webtools", new SqlScriptSource() {
					@Override
					public String read() throws IOException {
						File file = new File("../distribution/saas/webtools.sql");
						return Files.asCharSource(file, Charsets.UTF_8).read();
					}
				})
				.start();
		
		ds = new HikariDataSource();
		ds.setJdbcUrl("jdbc:mysql://localhost:2315/webtools");
		ds.setUsername("root");
		ds.setPassword("");
		
		datalayer = new MariaDBDataLayer(ds);
	}

	@AfterClass(groups = "integration")
	public void after() {
		ds.close();
		
		mysqld.stop();
	}
	
	@BeforeMethod(groups = "integration")
	public void clear () {
		datalayer.clear();
	}

	@Test(groups = "integration")
	public void testAdd() {

		SegmentData data = new SegmentData();
		data.addSegment("eins", 1);
		data.addSegment("zwei", 2);

		datalayer.add("testAdd", "segments", data, SegmentData.class);
		data = datalayer.get("testAdd", "segments", SegmentData.class).get();
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getSegments()).isNotNull().isNotEmpty().containsExactlyInAnyOrder("eins", "zwei");
	}

	@Test(
			expectedExceptions = UnsupportedOperationException.class,
			groups = "integration"
	)
	public void testUnmodifiable() {

		SegmentData data = new SegmentData();
		data.getSegments().add("eins");
	}

	@Test(groups = "integration")
	public void testUpdate() {

		SegmentData data = new SegmentData();
		
		data.addSegment("eins", 1);
		
		int size = datalayer.size();
		datalayer.add("testUpdate", "segments", data, SegmentData.class);
		data = datalayer.get("testUpdate", "segments", SegmentData.class).get();
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getSegments()).isNotNull().isNotEmpty().containsExactlyInAnyOrder("eins");
		Assertions.assertThat(datalayer.size()).isNotNull().isEqualTo(size + 1);

		data.addSegment("zwei", 2);
		datalayer.add("testUpdate", "segments", data, SegmentData.class);
		data = datalayer.get("testUpdate", "segments", SegmentData.class).get();
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getSegments()).isNotNull().isNotEmpty().containsExactlyInAnyOrder("eins", "zwei");
		Assertions.assertThat(datalayer.size()).isNotNull().isEqualTo(size + 1);
	}

	@Test(groups = "integration")
	public void testExpire() {

		SegmentData data = new SegmentData();
		data.addSegment("eins", 1, System.currentTimeMillis() - 1000);

		datalayer.add("testExpire", "segments", data, SegmentData.class);
		data = datalayer.get("testExpire", "segments", SegmentData.class).get();
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getSegments()).isNotNull().isEmpty();
	}

	@Test(groups = "integration")
	public void testMore() {

		SegmentData data = new SegmentData();
		data.addSegment("eins", 1);
		data.addSegment("zwei", 2);

		final long before = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			datalayer.add("testMore" + i, "segments", data, SegmentData.class);
		}
		final long after = System.currentTimeMillis();

		System.out.println("took: " + (after - before) + " ms");

	}

	
	@Test(groups = "integration")
	public void testUpdateMulitpleTimes() {

		SegmentData data = new SegmentData();
		
		data.addSegment("eins", 1);
		
		int size = datalayer.size();
		datalayer.add("testUpdateMulitpleTimes", "segments", data, SegmentData.class);
		
		data.addSegment("zwei", 2);
		for (int i = 0; i < 1000; i++) {
			datalayer.add("testUpdateMulitpleTimes", "segments", data, SegmentData.class);
		}
	}
	
}
