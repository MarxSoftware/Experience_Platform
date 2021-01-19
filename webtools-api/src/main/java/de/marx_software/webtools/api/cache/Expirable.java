package de.marx_software.webtools.api.cache;

/*-
 * #%L
 * webtools-api
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

import java.io.Serializable;

/**
 *
 * @author marx
 */
public class Expirable implements Serializable {
	
	private Serializable value;
	private long cache_time;
	private long added;
	
	public Expirable () {
		this(null, 0);
	}

	public Expirable(final Serializable value, final long cache_time) {
		this.value = value;
		this.cache_time = cache_time;
		
		this.added = System.currentTimeMillis();
	}

	public boolean isExpired () {
		long duration = System.currentTimeMillis() - added;
		
		return duration > cache_time;
	}
	
	public long getAdded() {
		return added;
	}

	public Serializable getValue() {
		return value;
	}

	public long getCacheTime() {
		return cache_time;
	}	
}
