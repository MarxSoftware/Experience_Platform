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
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.base.Configuration;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.actions.ActionSystem;
import com.thorstenmarx.webtools.api.actions.SegmentService;
import com.thorstenmarx.webtools.api.cache.CacheLayer;
import com.thorstenmarx.webtools.api.configuration.Registry;
import com.thorstenmarx.webtools.api.datalayer.DataLayer;
import com.thorstenmarx.webtools.api.entities.Entities;
import com.thorstenmarx.webtools.api.execution.Executor;
import com.thorstenmarx.webtools.api.extensions.core.CoreAnalyticsDbExtension;
import com.thorstenmarx.webtools.api.extensions.core.CoreDataLayerExtension;
import com.thorstenmarx.webtools.api.extensions.core.CoreEntitiesExtension;
import com.thorstenmarx.webtools.api.extensions.core.CoreRegistryExtension;
import com.thorstenmarx.webtools.api.location.LocationProvider;
import com.thorstenmarx.webtools.initializer.CoreModuleManager;
import com.thorstenmarx.webtools.initializer.guice.local.LocalCacheLayer;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.engio.mbassy.bus.MBassador;

public class LocalGuiceModule extends AbstractGuiceModule {

    public LocalGuiceModule() {
    }

    @Provides
    @Singleton
    private AnalyticsDB analyticsDB(final LocationProvider locationProvider, @CoreModuleManager ModuleManager moduleManager) {
        if (ContextListener.STATE.shuttingDown()) {
            return null;
        }

        final List<CoreAnalyticsDbExtension> extensions = moduleManager.extensions(CoreAnalyticsDbExtension.class);

		AnalyticsDB db = extensions.get(0).getAnalyticsDb();

        initAnalyticsFilters(db, locationProvider);

        return db;
    }

    @Provides
    @Singleton
    private Entities entities(@CoreModuleManager ModuleManager moduleManager) {
        final List<CoreEntitiesExtension> extensions = moduleManager.extensions(CoreEntitiesExtension.class);

		return extensions.get(0).getEntities();
    }

    @Provides
    @Singleton
    private Registry registry(@CoreModuleManager ModuleManager moduleManager) {
		final List<CoreRegistryExtension> extensions = moduleManager.extensions(CoreRegistryExtension.class);

		return extensions.get(0).getRegistry();
    }

    @Provides
    @Singleton
    private DataLayer datalayer(@CoreModuleManager ModuleManager moduleManager) {
        final List<CoreDataLayerExtension> extensions = moduleManager.extensions(CoreDataLayerExtension.class);

		return extensions.get(0).getDataLayer();
    }
	
	@Provides
	@Singleton
	protected ActionSystem actionSystem(@Nullable final AnalyticsDB db, final SegmentService segments, final Configuration config, final ModuleManager moduleManager, final MBassador mbassador, final DataLayer datalayer, final Executor executor) throws FileNotFoundException {
		if (ContextListener.STATE.shuttingDown()) {
			return null;
		}
		ActionSystem actionSystem = new ActionSystem(db, segments, config, moduleManager, mbassador, datalayer, executor);
		return actionSystem;
	}
	
	@Provides
	@Singleton
	protected CacheLayer cachelayer() {
		
		CacheLayer cachelayer = new LocalCacheLayer();
		
		return cachelayer;
	}

    @Override
    protected void configure() {
    }

}
