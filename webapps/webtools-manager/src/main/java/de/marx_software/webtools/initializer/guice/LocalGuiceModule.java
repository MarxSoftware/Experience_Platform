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
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.thorstenmarx.modules.api.ModuleManager;
import de.marx_software.webtools.api.analytics.AnalyticsDB;
import de.marx_software.webtools.ContextListener;
import de.marx_software.webtools.api.cache.CacheLayer;
import de.marx_software.webtools.api.configuration.Registry;
import de.marx_software.webtools.api.entities.Entities;
import de.marx_software.webtools.api.extensions.core.CoreAnalyticsDbExtension;
import de.marx_software.webtools.api.extensions.core.CoreCacheLayerExtension;
import de.marx_software.webtools.api.extensions.core.CoreEntitiesExtension;
import de.marx_software.webtools.api.extensions.core.CoreRegistryExtension;
import de.marx_software.webtools.api.location.LocationProvider;
import java.io.IOException;
import java.util.List;
import de.marx_software.webtools.initializer.annotations.Core;

public class LocalGuiceModule extends AbstractModule {

    public LocalGuiceModule() {
    }

    @Provides
    @Singleton
    private AnalyticsDB analyticsDB(final LocationProvider locationProvider, @Core ModuleManager moduleManager) throws IOException {
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
    private Entities entities(@Core ModuleManager moduleManager) {
        final List<CoreEntitiesExtension> extensions = moduleManager.extensions(CoreEntitiesExtension.class);

		return extensions.get(0).getEntities();
    }

    @Provides
    @Singleton
    private Registry registry(@Core ModuleManager moduleManager) {
		final List<CoreRegistryExtension> extensions = moduleManager.extensions(CoreRegistryExtension.class);

		return extensions.get(0).getRegistry();
    }
	
	@Provides
	@Singleton
	protected CacheLayer cachelayer(final @Core ModuleManager moduleManager) {
		
		final List<CoreCacheLayerExtension> extensions = moduleManager.extensions(CoreCacheLayerExtension.class);

		return extensions.get(0).getCacheLayer();
	}

    @Override
    protected void configure() {
		
		
    }

}
