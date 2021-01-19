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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author thmarx
 */
public final class Lookup {
	
	private final static Lookup INSTANCE = new Lookup();
	
	public static Lookup getDefault () {
		return INSTANCE;
	}
	
	private final ConcurrentMap<Class, Object> lookups = new ConcurrentHashMap<>();
	
	public Lookup () {
	}
	
	public <T> void register (Class<T> clazz, T object) {
		lookups.put(clazz, object);
	}
	
	
	public <T> T lookup (Class<T> clazz) {
		
		Object value = lookups.get(clazz);
		if (value != null && clazz.isAssignableFrom(value.getClass())) {
			return clazz.cast(value);
		}
		
		return null;
	}
}
