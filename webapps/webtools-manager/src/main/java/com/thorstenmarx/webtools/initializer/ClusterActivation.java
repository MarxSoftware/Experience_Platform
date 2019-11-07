package com.thorstenmarx.webtools.initializer;

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
import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.util.Modules;
import com.google.inject.Module;
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.Fields;
import com.thorstenmarx.webtools.api.Lookup;
import com.thorstenmarx.webtools.api.actions.ActionSystem;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.cluster.Cluster;
import com.thorstenmarx.webtools.api.configuration.Configuration;
import com.thorstenmarx.webtools.api.execution.Executor;
import com.thorstenmarx.webtools.impl.execution.DefaultExecutor;
import com.thorstenmarx.webtools.manager.model.User;
import com.thorstenmarx.webtools.manager.services.UserService;
import com.thorstenmarx.webtools.manager.utils.Helper;
import com.thorstenmarx.webtools.api.location.LocationProvider;
import com.thorstenmarx.webtools.initializer.annotations.Common;
import com.thorstenmarx.webtools.initializer.annotations.Infrastructure;
import com.thorstenmarx.webtools.initializer.guice.BaseGuiceModule;
import com.thorstenmarx.webtools.initializer.guice.ClusterGuiceModule;
import com.thorstenmarx.webtools.initializer.guice.CommonGuiceModule;
import com.thorstenmarx.webtools.initializer.guice.SystemGuiceModule;
import com.thorstenmarx.webtools.initializer.guice.LocalGuiceModule;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Initializer for local installation
 *
 * @author thmarx
 */
public class ClusterActivation implements Activation {
	
	private static final Logger LOGGER = LogManager.getLogger(ClusterActivation.class);
	
	private Gson gson = new Gson();
	
	@Override
	public void initialize () {
		
		Module tempModule = Modules.override(new BaseGuiceModule()).with(new LocalGuiceModule());
		tempModule = Modules.combine(tempModule, new CommonGuiceModule());
		Module configModule = Modules.override(tempModule).with(new ClusterGuiceModule());
		Injector injector = Guice.createInjector(configModule, new SystemGuiceModule());
		
		ContextListener.INJECTOR_PROVIDER.injector(injector);
		
		EventBus eventBus = new EventBus();
		Lookup.getDefault().register(EventBus.class, eventBus);

		Lookup.getDefault().register(AnalyticsDB.class, injector.getInstance(AnalyticsDB.class));
		Lookup.getDefault().register(ActionSystem.class, injector.getInstance(ActionSystem.class));
		

		UserService users = injector.getInstance(UserService.class);
		// no users
		if (users.all().isEmpty()) {
			User u = new User();
			u.group("admin");
			final String password = Helper.randomString();
			u.username("admin");
			u.password(password);
			users.add(u);
			StringBuilder sb = new StringBuilder();
			sb.append("===== GENERATED USER =====\r\n");
			sb.append("===== username: admin =====\r\n");
			sb.append("===== password: ").append(password).append(" =====\r\n");
			sb.append("==========================\r\n");
			LOGGER.error(sb.toString());
		}
		Configuration config = injector.getInstance(Configuration.class);
		Optional<String> apikeyOptional = config.getString(Fields.ApiKey.value());
		if ( !apikeyOptional.isPresent() ) {
			final String apikey = Helper.randomString();
			config.set(Fields.ApiKey.value(), apikey);
		}
		
		// init module manager by get the instance from guice
		ContextListener.INJECTOR_PROVIDER.injector().getInstance(ModuleManager.class);
		
		// Connect the cluster
		try {
			Cluster cluster = ContextListener.INJECTOR_PROVIDER.injector().getInstance(Cluster.class);
			if (cluster != null) {
				cluster.connect();
			} else {
				throw new IllegalStateException("no cluster implementation evailable");
			}
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	@Override
	public void destroy () {
		ContextListener.STATE.shuttingDown(true);

		try {
			Cluster cluster = ContextListener.INJECTOR_PROVIDER.injector().getInstance(Cluster.class);
			if (cluster != null) {
				cluster.close();
			}
		} catch (Exception ex) {
			LOGGER.error("", ex);
		}

		// close modulemanager
		ModuleManager moduleManager = ContextListener.INJECTOR_PROVIDER.injector().getInstance(ModuleManager.class);
		try {
			moduleManager.close();
		} catch (Exception ex) {
			LOGGER.error("", ex);
		}
		moduleManager = ContextListener.INJECTOR_PROVIDER.injector().getInstance(Key.get(ModuleManager.class, Common.class));
		try {
			
			moduleManager.close();
		} catch (Exception ex) {
			LOGGER.error("", ex);
		}
		moduleManager = ContextListener.INJECTOR_PROVIDER.injector().getInstance(Key.get(ModuleManager.class, Infrastructure.class));
		try {
			
			moduleManager.close();
		} catch (Exception ex) {
			LOGGER.error("", ex);
		}
		
		try {
			DefaultExecutor executor = (DefaultExecutor) ContextListener.INJECTOR_PROVIDER.injector().getInstance(Executor.class);
			executor.shutdown();
		} catch (Exception ex) {
			LOGGER.error("", ex);
		}
		
		LocationProvider locationProvider = (LocationProvider) ContextListener.INJECTOR_PROVIDER.injector().getInstance(LocationProvider.class);
		locationProvider.close();
	}
}
