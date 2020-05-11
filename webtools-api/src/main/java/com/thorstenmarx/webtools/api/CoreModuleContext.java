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
import com.thorstenmarx.webtools.api.execution.Executor;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.engio.mbassy.bus.MBassador;

/**
 *
 * @author thmarx
 */
public class CoreModuleContext extends Context {
	
	
	private final File dataPath;
	private final MBassador eventBus;
	private final Executor executor;
	
	
	protected final Map<String, Object> parameters = new HashMap<>();
	
	
	public CoreModuleContext (final File dataPath, final MBassador eventBus, final Executor executor) {
		this.dataPath = dataPath;
		this.eventBus = eventBus;
		this.executor = executor;
	}
	
	public void put (final String name, final Object value) {
		this.parameters.put(name, value);
	}
	public <T> T get(final String name, final Class<T> type, final T defaultValue) {
		if (!parameters.containsKey(name)) {
			return defaultValue;
		} 
		Object value = parameters.get(name);
		if (!type.isInstance(value)){
			return defaultValue;
		}
		
		return type.cast(value);
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
