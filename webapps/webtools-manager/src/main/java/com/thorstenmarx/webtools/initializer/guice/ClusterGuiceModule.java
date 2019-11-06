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
import com.thorstenmarx.webtools.api.CoreModuleContext;
import com.thorstenmarx.webtools.api.cluster.Cluster;
import com.thorstenmarx.webtools.api.execution.Executor;
import com.thorstenmarx.webtools.api.extensions.system.SystemClusterExtension;
import com.thorstenmarx.webtools.base.Configuration;
import com.thorstenmarx.webtools.initializer.Infrastructure;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.engio.mbassy.bus.MBassador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterGuiceModule extends AbstractModule {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClusterGuiceModule.class);

    public ClusterGuiceModule() {
    }

	@Provides
	@Singleton
	protected CoreModuleContext coreModuleContext (final Configuration configuration, final Executor executor, final MBassador mbassador, final Cluster cluster) {
		final CoreModuleContext coreModuleContext = new CoreModuleContext(new File("./webtools_data/core_modules_data"), mbassador, executor, cluster);
		
		
		Map<String, Object> analytics = configuration.getMap("analytics", Collections.EMPTY_MAP);
		if  (analytics.containsKey("shards")) {
			coreModuleContext.put("analyticsdb.shard.count", analytics.get("shards"));
		} else {
			coreModuleContext.put("analyticsdb.shard.count", 3);
		}
		
		
		return coreModuleContext;
	}
   
	
	@Provides
	@Singleton
	private Cluster cluster (@Infrastructure ModuleManager moduleManager) {
		final List<SystemClusterExtension> extensions = moduleManager.extensions(SystemClusterExtension.class);


		return extensions.get(0).getCluster();
	}

   

    @Override
    protected void configure() {
    }


}
