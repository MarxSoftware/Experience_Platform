package com.thorstenmarx.webtools.base;

/*-
 * #%L
 * webtools-base
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;


/**
 *
 * @author marx
 */
public class ConfigurationTest {
	
	
	Map<String, Object> load;
	
	@BeforeClass
	public void before () throws FileNotFoundException {
		Yaml yaml = new Yaml();
		load = yaml.load(new FileReader(new File("src/test/resources/configuration.yml")));
	}
	
	@Test
	public void testYaml()  {
		System.out.println(load);
	}	
	@Test
	public void testMariadb()  {
		Map<String, Object> mariadb = (Map<String, Object>) load.get("mariadb");
		Assertions.assertThat(mariadb).isNotNull().isNotEmpty();
		Assertions.assertThat(mariadb).containsKey("url");
		Assertions.assertThat(mariadb.get("url")).isEqualTo("jdbc:mysql://localhost:3306/webtools");
		Assertions.assertThat(mariadb).containsKey("username");
		Assertions.assertThat(mariadb.get("username")).isEqualTo("root");
		Assertions.assertThat(mariadb).containsKey("password");
		Assertions.assertThat(mariadb.get("password")).isEqualTo("");	
	}	
	@Test
	public void testElastic()  {
		Map<String, Object> elastic = (Map<String, Object>) load.get("elastic");
		
		Assertions.assertThat(elastic.get("url")).isInstanceOf(List.class);
		
	}	
	@Test
	public void testNode()  {
		Map<String, Object> elastic = (Map<String, Object>) load.get("node");
		
		Assertions.assertThat(elastic.get("members")).isInstanceOf(List.class);
		
	}
}
