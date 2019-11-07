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
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.api.actions.ActionSystem;
import com.thorstenmarx.webtools.api.cache.CacheLayer;
import com.thorstenmarx.webtools.api.configuration.Registry;
import com.thorstenmarx.webtools.api.datalayer.DataLayer;
import com.thorstenmarx.webtools.api.entities.Entities;
import com.thorstenmarx.webtools.api.execution.Executor;
import com.thorstenmarx.webtools.api.extensions.core.CoreActionSystemExtension;
import com.thorstenmarx.webtools.api.extensions.core.CoreAnalyticsDbExtension;
import com.thorstenmarx.webtools.api.extensions.core.CoreCacheLayerExtension;
import com.thorstenmarx.webtools.api.extensions.core.CoreDataLayerExtension;
import com.thorstenmarx.webtools.api.extensions.core.CoreEntitiesExtension;
import com.thorstenmarx.webtools.api.extensions.core.CoreRegistryExtension;
import com.thorstenmarx.webtools.api.location.LocationProvider;
import com.thorstenmarx.webtools.initializer.annotations.Common;
import java.io.IOException;
import java.util.List;
import net.engio.mbassy.bus.MBassador;

public class LocalGuiceModule extends AbstractModule {

    public LocalGuiceModule() {
    }

    @Provides
    @Singleton
    private AnalyticsDB analyticsDB(final LocationProvider locationProvider, @Common ModuleManager moduleManager) throws IOException {
        if (ContextListener.STATE.shuttingDown()) {
            return null;
        }

        final List<CoreAnalyticsDbExtension> extensions = moduleManager.extensions(CoreAnalyticsDbExtension.class);

		AnalyticsDB db = extensions.get(0).getAnalyticsDb();

        BaseGuiceModule.initAnalyticsFilters(db, locationProvider);

        return db;
    }

    @Provides
    @Singleton
    private Entities entities(@Common ModuleManager moduleManager) {
        final List<CoreEntitiesExtension> extensions = moduleManager.extensions(CoreEntitiesExtension.class);

		return extensions.get(0).getEntities();
    }

    @Provides
    @Singleton
    private Registry registry(@Common ModuleManager moduleManager) {
		final List<CoreRegistryExtension> extensions = moduleManager.extensions(CoreRegistryExtension.class);

		return extensions.get(0).getRegistry();
    }

    @Provides
    @Singleton
    private DataLayer datalayer(@Common ModuleManager moduleManager) {
        final List<CoreDataLayerExtension> extensions = moduleManager.extensions(CoreDataLayerExtension.class);

		return extensions.get(0).getDataLayer();
    }

	@Provides
    @Singleton
    private ActionSystem actionSystem(final @Common ModuleManager moduleManager, final DataLayer datalayer, final Executor executor, final AnalyticsDB analyticsDB, final MBassador mBassador) {
        final List<CoreActionSystemExtension> extensions = moduleManager.extensions(CoreActionSystemExtension.class);

		return extensions.get(0).getActionSystem();
    }
	
	@Provides
	@Singleton
	protected CacheLayer cachelayer(final @Common ModuleManager moduleManager) {
		
		final List<CoreCacheLayerExtension> extensions = moduleManager.extensions(CoreCacheLayerExtension.class);

		return extensions.get(0).getCacheLayer();
	}

    @Override
    protected void configure() {
		
		
    }

}
