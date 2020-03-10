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
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.api.CoreModuleContext;
import com.thorstenmarx.webtools.api.ModuleContext;
import com.thorstenmarx.webtools.api.actions.SegmentService;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.configuration.Registry;
import com.thorstenmarx.webtools.api.entities.Entities;
import com.thorstenmarx.webtools.api.execution.Executor;
import com.thorstenmarx.webtools.api.extensions.EventSourceProvidingExtension;
import com.thorstenmarx.webtools.api.extensions.core.CoreActionSystemExtension;
import com.thorstenmarx.webtools.api.message.MessageStream;
import com.thorstenmarx.webtools.base.Configuration;
import com.thorstenmarx.webtools.impl.eventsource.EventSources;
import com.thorstenmarx.webtools.impl.execution.DefaultExecutor;
import com.thorstenmarx.webtools.impl.message.LocalMessageStream;
import com.thorstenmarx.webtools.manager.services.SiteService;
import com.thorstenmarx.webtools.manager.services.UserService;
import com.thorstenmarx.webtools.manager.services.impl.DBSiteService;
import com.thorstenmarx.webtools.manager.services.impl.FileUserService;
import com.thorstenmarx.webtools.tracking.referrer.ReferrerFilter;
import com.thorstenmarx.webtools.tracking.useragent.UserAgentFilter;
import com.thorstenmarx.webtools.tracking.CrawlerUtil;
import com.thorstenmarx.webtools.api.location.LocationProvider;
import com.thorstenmarx.webtools.initializer.annotations.Common;
import com.thorstenmarx.webtools.initializer.MultiModuleManager;
import com.thorstenmarx.webtools.tracking.location.LocationFilter;
import com.thorstenmarx.webtools.web.utils.MaxmindLocationProvider;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.engio.mbassy.bus.MBassador;

/**
 *
 * @author marx
 */
public class BaseGuiceModule extends AbstractModule {

	public BaseGuiceModule() {
	}

	protected static void initAnalyticsFilters(final AnalyticsDB db, final LocationProvider locationProvider) throws IOException {
		db.addFilter(UserAgentFilter.getInstance()::filter);

		LocationFilter locationFilter = new LocationFilter(locationProvider);
		db.addFilter(locationFilter);

		db.addFilter(ReferrerFilter::filter);

	}

	@Provides
	@Singleton
	protected Configuration configuration() {
		return Configuration.getInstance(new File("webtools_data"));
	}

	@Provides
	@Singleton
	protected CrawlerUtil crawlerUtil() {
		return new CrawlerUtil();
	}

	@Provides
	@Singleton
	protected EventSources eventSources(MBassador mBassador, ModuleManager moduleManager) {
		EventSources eventSources = new EventSources();
		moduleManager.extensions(EventSourceProvidingExtension.class).stream().map((EventSourceProvidingExtension ext) -> ext.getSupportedEvents()).flatMap(Arrays::stream).forEach(eventSources::addNew);
		mBassador.subscribe(eventSources);
		return eventSources;
	}

	@Provides
	@Singleton
	protected ModuleManager moduleManager(final AnalyticsDB analyticsDB, final SegmentService segmentService, final MBassador mBassador, final Entities entities, final Registry registry, final Injector injector) {
		List<String> apiPackages = new ArrayList<>();
		apiPackages.add("com.thorstenmarx.webtools.api");
		apiPackages.add("com.thorstenmarx.webtools.hosting");
		apiPackages.add("com.thorstenmarx.webtools.collection");
		apiPackages.add("com.thorstenmarx.webtools.streams");
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
		ModuleAPIClassLoader apiClassLoader = new ModuleAPIClassLoader((URLClassLoader) getClass().getClassLoader(), apiPackages);
		return ModuleManagerImpl.create(new File("webtools_modules/extensions"), new ModuleContext(analyticsDB, segmentService, mBassador, entities, registry), apiClassLoader, injector::injectMembers);
	}
	
	@Provides
    @Singleton
    private MultiModuleManager multiModuleManager(@Common ModuleManager coreModuleManager, ModuleManager moduleManager) {
        return MultiModuleManager.create(coreModuleManager, moduleManager);
    }

	@Provides
	@Singleton
	protected MBassador mBassador() {
		if (ContextListener.STATE.shuttingDown()) {
			return null;
		}
		return new MBassador();
	}

	@Provides
	@Singleton
	protected LocationProvider locationProvider(final Configuration config) {
		if (ContextListener.STATE.shuttingDown()) {
			return null;
		}
		LocationProvider locationProvider = new MaxmindLocationProvider(config);
		return locationProvider;
	}

	@Provides
	@Singleton
	protected UserService userService() {
		return new FileUserService("webtools_data/conf/");
	}

	@Provides
	@Singleton
	protected SegmentService segmentService(final @Common ModuleManager moduleManager) {
		final List<CoreActionSystemExtension> extensions = moduleManager.extensions(CoreActionSystemExtension.class);
		return extensions.get(0).getSegmentService();
	}

	@Provides
	@Singleton
	protected SiteService siteService(final Entities entities) {
		return new DBSiteService(entities);
	}

	@Provides
	@Singleton
	protected com.thorstenmarx.webtools.api.configuration.Configuration getSystemConfiguration(final Registry registry) {
		return registry.getConfiguration("system");
	}

	@Provides
	@Singleton
	protected Executor executor() {
		return new DefaultExecutor();
	}

	@Provides
	@Singleton
	protected MessageStream messageStream() {
		return new LocalMessageStream();
	}

	@Override
	protected void configure() {
	}

}
