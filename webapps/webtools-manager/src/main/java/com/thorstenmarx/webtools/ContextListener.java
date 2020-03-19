package com.thorstenmarx.webtools;

/*-
 * #%L
 * webtools-manager
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
import com.google.inject.Injector;
import com.thorstenmarx.webtools.base.Configuration;
import com.thorstenmarx.webtools.initializer.Activation;
import com.thorstenmarx.webtools.initializer.ClusterActivation;
import com.thorstenmarx.webtools.initializer.LocalActivation;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author marx
 */
public class ContextListener implements ServletContextListener {

	private static final Logger LOGGER = LogManager.getLogger(ContextListener.class);

	public final static InjectorProvider INJECTOR_PROVIDER = new InjectorProvider();

	public final static StateProvider STATE = new StateProvider();

	private Activation activation;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Configuration config = Configuration.getInstance(new File("webtools_data"));

		Configuration.Config<String> modeConfig = config.getConfig("mode", String.class);
		final String mode = modeConfig.get("local");

		if ("cluster".equals(mode)) {
			activation = new ClusterActivation();
		} else {
			activation = new LocalActivation();
		}
		activation.initialize();

		
		try {
			Path path = Paths.get("experience-platform.pid");
			byte[] strToBytes = String.valueOf(ProcessHandle.current().pid()).getBytes(Charsets.UTF_8);
			Files.write(path, strToBytes);
		} catch (IOException ex) {
			LOGGER.error("", ex);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		activation.destroy();
	}

	public static class InjectorProvider {

		Injector injector;

		public Injector injector() {
			return injector;
		}

		public void injector(Injector injector) {
			this.injector = injector;
		}
	}

	public static class StateProvider {

		boolean shuttingDown = false;

		public boolean shuttingDown() {
			return shuttingDown;
		}

		public void shuttingDown(final boolean shuttingDown) {
			this.shuttingDown = shuttingDown;
		}
	}
}
