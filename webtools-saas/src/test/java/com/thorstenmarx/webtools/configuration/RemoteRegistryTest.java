package com.thorstenmarx.webtools.configuration;

/*-
 * #%L
 * webtools-configuration
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
import com.thorstenmarx.webtools.api.configuration.Configuration;
import com.thorstenmarx.webtools.api.configuration.Registry;
import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.ScriptResolver;
import com.wix.mysql.SqlScriptSource;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Version;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.assertj.core.api.Assertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class RemoteRegistryTest {

	Registry registry;
	AtomicInteger counter = new AtomicInteger();
	private HikariDataSource ds;
	private EmbeddedMysql mysqld;

	@BeforeClass(groups = "integration")
	public void setup() {

		MysqldConfig config = MysqldConfig.aMysqldConfig(Version.v5_7_latest).withPort(2215).build();
		
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
		ds.setJdbcUrl("jdbc:mysql://localhost:2215/webtools");
		ds.setUsername("root");
		ds.setPassword("");

		registry = new RemoteRegistryImpl(ds);
	}

	@AfterClass(groups = "integration")
	public void down() {
		ds.close();
		mysqld.stop();
	}

	@Test(groups = "integration")
	public void testString() {
		Configuration config = registry.getConfiguration("test" + counter.getAndIncrement());
		Configuration config2 = registry.getConfiguration("test" + counter.getAndIncrement());

		Assertions.assertThat(config.set("name", "thorsten")).isTrue();

		Assertions.assertThat(config.getString("name")).isPresent();
		Assertions.assertThat(config.getString("name").get()).isEqualTo("thorsten");

		Assertions.assertThat(config2.getString("name")).isNotPresent();
	}

	@Test(groups = "integration")
	public void testBoolean() {
		Configuration config = registry.getConfiguration("test" + counter.getAndIncrement());
		Configuration config2 = registry.getConfiguration("test" + counter.getAndIncrement());

		Assertions.assertThat(config.set("name", Boolean.TRUE)).isTrue();

		Assertions.assertThat(config.getBoolean("name")).isPresent();
		Assertions.assertThat(config.getBoolean("name").get()).isTrue();

		Assertions.assertThat(config2.getString("name")).isNotPresent();
	}

	@Test(groups = "integration")
	public void testList() {
		Configuration config = registry.getConfiguration("test" + counter.getAndIncrement());
		Configuration config2 = registry.getConfiguration("test" + counter.getAndIncrement());

		List<String> items = new ArrayList<>();
		items.add("eins");
		items.add("zwei");

		Assertions.assertThat(config.set("items", items)).isTrue();

		Assertions.assertThat(config.getList("items", String.class)).isPresent();
		Assertions.assertThat(config.getList("items", String.class).get()).isNotEmpty();
		Assertions.assertThat(config.getList("items", String.class).get()).containsExactlyElementsOf(items);

		Assertions.assertThat(config2.getString("name")).isNotPresent();
	}
}
