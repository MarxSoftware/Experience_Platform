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
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.api.CoreModuleContext;
import com.thorstenmarx.webtools.api.execution.Executor;
import com.thorstenmarx.webtools.base.Configuration;
import com.thorstenmarx.webtools.initializer.annotations.Infrastructure;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.engio.mbassy.bus.MBassador;

/**
 *
 * @author marx
 */
public class SystemGuiceModule extends AbstractModule {

	public SystemGuiceModule() {
	}

	@Provides
	@Singleton
	@Infrastructure
	protected CoreModuleContext infrastructureModuleContext (final Configuration configuration, final Executor executor, final MBassador mbassador) {
		final CoreModuleContext coreModuleContext = new CoreModuleContext(new File("./webtools_data/infrastructure/data"), mbassador, executor);
		
		// config
		Map<String, Object> node = configuration.getMap("node", Collections.EMPTY_MAP);
		if  (node.containsKey("name")) {
			final File configDir = new File("./webtools_data/conf");
			coreModuleContext.put("node.name", node.get("name"));
			coreModuleContext.put("node.config", configDir);
		}
		
		
		return coreModuleContext;
	}
	
	@Provides
	@Singleton
	@Infrastructure
	protected ModuleManager infrastructureModuleManager(final Injector injector, @Infrastructure final CoreModuleContext context) {
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
		apiPackages.add("javax.ws.rs");
		apiPackages.add("javax.inject");
		apiPackages.add("java.internal.reflect");
		apiPackages.add("jdk.internal.reflect");
		apiPackages.add("com.google.gson");
		ModuleAPIClassLoader apiClassLoader = new ModuleAPIClassLoader((URLClassLoader) getClass().getClassLoader(), apiPackages);
		ModuleManager coreModuleManager = ModuleManagerImpl.create(new File("webtools_modules/system"), context, apiClassLoader, injector::injectMembers);		
		
		// autoactivate core modules
		coreModuleManager.configuration().getModules().keySet().forEach((module) -> {
			try {
				coreModuleManager.activateModule(module);
			} catch (IOException ex) {
				throw new IllegalStateException(ex);
			}
		});
		
		return coreModuleManager;
	}

	@Override
	protected void configure() {
	}

}
