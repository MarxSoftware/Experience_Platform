package de.marx_software.webtools.api;

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
import de.marx_software.webtools.api.analytics.AnalyticsDB;
import de.marx_software.webtools.api.actions.SegmentService;
import de.marx_software.webtools.api.configuration.Registry;
import de.marx_software.webtools.api.entities.Entities;
import java.util.HashMap;
import java.util.Map;
import net.engio.mbassy.bus.MBassador;

/**
 *
 * @author thmarx
 */
public class ModuleContext extends Context {
	
	protected final Map<String, Object> parameters = new HashMap<>();
	
	public ModuleContext () {
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
}
