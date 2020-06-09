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

import com.thorstenmarx.webtools.api.execution.Executor;
import java.io.File;

/**
 *
 * @author thmarx
 */
public class CoreModuleContext extends ModuleContext {
	
	
	private final File dataPath;
	
	private final Executor executor;
	
	public CoreModuleContext (final File dataPath, final Executor executor) {
		super();
		this.dataPath = dataPath;
		this.executor = executor;
	}
	
	public File getDataPath() {
		return dataPath;
	}
	
	public Executor getExecutor() {
		return executor;
	}
}
