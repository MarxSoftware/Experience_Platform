package com.thorstenmarx.webtools.api.cache;

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

import com.thorstenmarx.webtools.api.annotations.API;
import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author marx
 */
@API(since = "3.1.0", status = API.Status.Experimental)
public interface CacheLayer {
	
	<T extends Serializable> void add (String key, T value, long duration, TimeUnit unit);
	
	<T extends Serializable> Optional<T> get (String key, Class<T> clazz);
	
	boolean exists (String key);
	
	default String key (final String key, final String...parts) {
		return key + ((parts.length > 0) ? ("_" + String.join("_", parts)) : "");
	}
}
