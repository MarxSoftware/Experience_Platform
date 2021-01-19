package de.marx_software.webtools.initializer.guice;

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
import com.thorstenmarx.modules.api.ServiceRegistry;
import de.marx_software.webtools.ContextListener;
import de.marx_software.webtools.api.ModuleContext;
import de.marx_software.webtools.api.actions.SegmentService;
import de.marx_software.webtools.api.analytics.AnalyticsDB;
import de.marx_software.webtools.api.configuration.Registry;
import de.marx_software.webtools.api.entities.Entities;
import de.marx_software.webtools.api.execution.Executor;
import de.marx_software.webtools.api.extensions.EventSourceProvidingExtension;
import de.marx_software.webtools.api.extensions.core.CoreSegmentationExtension;
import de.marx_software.webtools.api.message.MessageStream;
import de.marx_software.webtools.base.Configuration;
import de.marx_software.webtools.impl.eventsource.EventSources;
import de.marx_software.webtools.impl.execution.DefaultExecutor;
import de.marx_software.webtools.impl.message.LocalMessageStream;
import de.marx_software.webtools.manager.services.SiteService;
import de.marx_software.webtools.manager.services.impl.DBSiteService;
import de.marx_software.webtools.tracking.referrer.ReferrerFilter;
import de.marx_software.webtools.tracking.useragent.UserAgentFilter;
import de.marx_software.webtools.tracking.CrawlerUtil;
import de.marx_software.webtools.api.location.LocationProvider;
import de.marx_software.webtools.initializer.MultiModuleManager;
import de.marx_software.webtools.tracking.location.LocationFilter;
import de.marx_software.webtools.tracking.location.MaxmindLocationProvider;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.engio.mbassy.bus.MBassador;
import de.marx_software.webtools.initializer.annotations.Core;

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
	protected ModuleManager moduleManager(final ServiceRegistry serviceRegistry, final Injector injector) {
		List<String> apiPackages = new ArrayList<>();
		apiPackages.add("de.marx_software.webtools.api");
		apiPackages.add("de.marx_software.webtools.hosting");
		apiPackages.add("de.marx_software.webtools.collection");
		apiPackages.add("de.marx_software.webtools.streams");
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
		ModuleAPIClassLoader apiClassLoader = new ModuleAPIClassLoader((URLClassLoader) getClass().getClassLoader(), apiPackages);
		return ModuleManagerImpl.builder()
				.setPath(new File("webtools_modules/extensions"))
				.setContext(new ModuleContext())
				.setClassLoader(apiClassLoader)
				.setInjector(injector::injectMembers)
				.setServiceRegistry(serviceRegistry).build();
	}
	
	@Provides
    @Singleton
    private MultiModuleManager multiModuleManager(@Core ModuleManager coreModuleManager, ModuleManager moduleManager) {
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
	protected SegmentService segmentService(final @Core ModuleManager moduleManager) {
		final List<CoreSegmentationExtension> extensions = moduleManager.extensions(CoreSegmentationExtension.class);
		return extensions.get(0).getSegmentService();
	}

	@Provides
	@Singleton
	protected SiteService siteService(final Entities entities) {
		return new DBSiteService(entities);
	}

	@Provides
	@Singleton
	protected de.marx_software.webtools.api.configuration.Configuration getSystemConfiguration(final Registry registry) {
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
