/*
 * Copyright (C) 2019 Thorsten Marx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.thorstenmarx.webtools.initializer;

/*-
 * #%L
 * webtools-manager
 * %%
 * Copyright (C) 2016 - 2019 Thorsten Marx
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

import com.thorstenmarx.modules.api.ExtensionPoint;
import com.thorstenmarx.modules.api.ManagerConfiguration;
import com.thorstenmarx.modules.api.Module;
import com.thorstenmarx.modules.api.ModuleDescription;
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.api.extensions.ManagerConfigExtension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author marx
 */
public class MultiModuleManager {
	
	public static MultiModuleManager create (final ModuleManager ... managers) {
		return new MultiModuleManager(Arrays.asList(managers));
	}
	
	private final List<ModuleManager> moduleManagers;

	public MultiModuleManager () {
		this(Collections.EMPTY_LIST);
	}
	
	private MultiModuleManager(final List<ModuleManager> moduleManagers) {
		this.moduleManagers = moduleManagers;
	}

	public Module module(String moduleName) {
		for (final ModuleManager moduleManager : moduleManagers) {
			final Module module = moduleManager.module(moduleName);
			if (module != null) {
				return module;
			}
		}
		return null;
	}

	public <T  extends ExtensionPoint> List<T> extensions(final Class<T> extensionClass) {
		List<T> extensions = new ArrayList<>();
		
		moduleManagers.stream().map((mm) -> mm.extensions(extensionClass)).forEach(extensions::addAll);
		
		return extensions;
	}

	public ManagerConfiguration configuration() {
		ConcurrentMap<String, ManagerConfiguration.ModuleConfig> moduleConfigs = new ConcurrentHashMap<>();
		moduleManagers.stream().forEach((mm) -> {moduleConfigs.putAll(mm.configuration().getModules());});
		
		return new ManagerConfiguration(){
			@Override
			public ConcurrentMap<String, ManagerConfiguration.ModuleConfig> getModules() {
				return moduleConfigs;
			}
			
		};
	}

	public ModuleDescription description(String id) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
