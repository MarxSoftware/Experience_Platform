package com.thorstenmarx.webtools.initializer.guice;

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
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.thorstenmarx.modules.ModuleAPIClassLoader;
import com.thorstenmarx.modules.ModuleManagerImpl;
import com.thorstenmarx.modules.api.DefaultServiceRegistry;
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.modules.api.ServiceRegistry;
import com.thorstenmarx.webtools.api.CoreModuleContext;
import com.thorstenmarx.webtools.api.execution.Executor;
import com.thorstenmarx.webtools.base.Configuration;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.engio.mbassy.bus.MBassador;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.thorstenmarx.webtools.initializer.annotations.Core;

/**
 *
 * @author marx
 */
public class SystemGuiceModule extends AbstractModule {

	private Logger LOGGER = LogManager.getLogger(SystemGuiceModule.class);

	public SystemGuiceModule() {
	}
	
	@Provides
	@Singleton
	protected ServiceRegistry serviceRegistry () {
		return new DefaultServiceRegistry();
	}

	@Provides
	@Singleton
	protected CoreModuleContext coreModuleContext(final Configuration configuration, final Executor executor, final MBassador mbassador) {
		final CoreModuleContext coreModuleContext = new CoreModuleContext(new File("./webtools_modules/system/modules_data"), executor);

		Map<String, Object> analytics = configuration.getMap("analytics", Collections.EMPTY_MAP);
		if (analytics.containsKey("shards")) {
			coreModuleContext.put("analyticsdb.shard.count", analytics.get("shards"));
		} else {
			coreModuleContext.put("analyticsdb.shard.count", 3);
		}

		return coreModuleContext;
	}

	@Provides
	@Singleton
	@Core
	protected ModuleManager coreModuleManager(final CoreModuleContext context, final ServiceRegistry serviceRegistry) {
		try {
			List<String> apiPackages = new ArrayList<>();
			apiPackages.add("com.thorstenmarx.webtools.api");
			apiPackages.add("com.thorstenmarx.webtools.hosting");
			apiPackages.add("com.thorstenmarx.webtools.collection");
			apiPackages.add("com.thorstenmarx.webtools.streams");
			apiPackages.add("com.thorstenmarx.webtools.scripting");
			apiPackages.add("net.engio.mbassy");
			apiPackages.add("org.apache.wicket");
			apiPackages.add("de.agilecoders.wicket");
			apiPackages.add("com.googlecode.wickedcharts");
			apiPackages.add("com.alibaba.fastjson");
			apiPackages.add("org.slf4j");
			apiPackages.add("org.apache.logging.log4j");
			apiPackages.add("javax.ws.rs");
			apiPackages.add("javax.inject");
			apiPackages.add("java.internal.reflect");
			apiPackages.add("jdk.internal.reflect");
			apiPackages.add("com.google.gson");
			apiPackages.add("org.xml");
			apiPackages.add("org.w3c");
			ModuleAPIClassLoader apiClassLoader = new ModuleAPIClassLoader((URLClassLoader) getClass().getClassLoader(), apiPackages);
			
			
			ModuleManager coreModuleManager = ModuleManagerImpl.builder()
					.setPath(new File("webtools_modules/system"))
					.setContext(context)
					.setClassLoader(apiClassLoader)
					.setServiceRegistry(serviceRegistry)
					.build();

			// if available activate cluster first
			if (coreModuleManager.module("core-module-jgroups-cluster") != null) {
				coreModuleManager.activateModule("core-module-jgroups-cluster");
			}

			// autoactivate core modules
			coreModuleManager.activateModule("core-module-entities");
			coreModuleManager.activateModule("core-module-analytics-storage-lucene");
			coreModuleManager.activateModule("core-module-cachelayer");
			coreModuleManager.activateModule("core-module-configuration");
			coreModuleManager.activateModule("core-module-actionsystem");


			return coreModuleManager;
		} catch (Throwable e) {
			LOGGER.error("error loading core modules", e);
			System.exit(25);
		}
		throw new IllegalStateException("should never be reached");
	}

	@Override
	protected void configure() {
	}

}
