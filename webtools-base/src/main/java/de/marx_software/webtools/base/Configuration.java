package de.marx_software.webtools.base;

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
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author thmarx
 */
public class Configuration {

//	public enum Field {
//		DB_DIR("db.dir"),
//		ELASTIC_URL("elastic.url"),
//		ELASTIC_CLUSTER("elastic.cluster"),
//		ELASTIC_USERNAME("elastic.username"),
//		ELASTIC_PASSWORD("elastic.password"),
//		ELASTIC_INDEX("elastic.index"),
//		MODE("mode"),
//		APIKEY("apikey");
//
//		private final String value;
//
//		Field(final String value) {
//			this.value = value;
//		}
//
//		public String value() {
//			return value;
//		}
//	}

	public enum Stage {

		Production("production"),
		Development("development"),
		Test("test");

		private final String value;

		private Stage(final String value) {
			this.value = value;
		}

		public static Stage forStage(final String stage) {
			for (final Stage s : values()) {
				if (s.value.equals(stage)) {
					return s;
				}
			}
			throw new IllegalArgumentException("unknown stage: " + stage);
		}
	}

	private static final EnumMap<Stage, Configuration> configurations = new EnumMap<>(Stage.class);

	private Map<String, Object> configuration;

	private File baseDir;

	private Configuration() {
	}

	public synchronized static Configuration empty() {
		return new Configuration();
	}

	public synchronized static Configuration getInstance(final File baseDir) {
		Configuration instance = new Configuration();
		instance.baseDir = baseDir;
		try {
			Yaml yaml = new Yaml();
			instance.configuration = yaml.load(new FileReader(new File(baseDir, "conf/configuration.yml")));
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
		}

		return instance;
	}

	public void put(final String parent, final String key, final Object value) {
		if (configuration == null) {
			configuration = new HashMap<>();
		}
		if (!configuration.containsKey(parent)) {
			configuration.put(parent, new HashMap<>());
		}
		((Map<String, Object>)configuration.get(parent)).put(key, value);
	}
	
	public Map<String, Object> getMap(final String key, final Map<String, Object> defaultValues) {
		return (Map<String, Object>) configuration.getOrDefault(key, defaultValues);
	}
	public Optional get(final String key) {
		return Optional.ofNullable(configuration.get(key));
	}
	public <T> Config<T> getConfig(final String key, final Class clazz) {
		Optional<T> optional = Optional.ofNullable((T) configuration.get(key));
		return new Config<>(optional);
	}

	public File baseDir() {
		return baseDir;
	}

	public static class Config<T> {

		final Optional<T> optional;

		public Config(Optional<T> optional) {
			this.optional = optional;
		}
		
		public T get(final T defaultValue) {
			
			if (optional.isPresent()) {
				return optional.get();
			}
			
			return defaultValue;
		}
	}
}
