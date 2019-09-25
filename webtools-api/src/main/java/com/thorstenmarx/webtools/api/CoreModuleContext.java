package com.thorstenmarx.webtools.api;

/*-
 * #%L
 * webtools-api
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

import com.thorstenmarx.modules.api.Context;
import com.thorstenmarx.webtools.api.cluster.Cluster;
import com.thorstenmarx.webtools.api.execution.Executor;
import java.io.File;
import net.engio.mbassy.bus.MBassador;

/**
 *
 * @author thmarx
 */
public class CoreModuleContext extends Context {
	
	
	private final File dataPath;
	private final MBassador eventBus;
	private final Executor executor;
	
	private final Cluster cluster;
	
	public CoreModuleContext (final File dataPath, final MBassador eventBus, final Executor executor) {
		this(dataPath, eventBus, executor, null);
	}
	public CoreModuleContext (final File dataPath, final MBassador eventBus, final Executor executor, final Cluster cluster) {
		this.dataPath = dataPath;
		this.eventBus = eventBus;
		this.executor = executor;
		this.cluster = cluster;
	}
	
	public boolean isCluster () {
		return cluster != null;
	}
	
	public Cluster getCluster () {
		return cluster;
	}

	public Executor getExecutor() {
		return executor;
	}
	
	public MBassador getEventBus() {
		return eventBus;
	}
	
	public File getDataPath() {
		return dataPath;
	}
}
