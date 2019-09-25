package com.thorstenmarx.webtools.api.datalayer;

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
import com.thorstenmarx.webtools.api.annotations.API;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 *
 * @author marx
 * @since 1.11.0
 */
@API(since = "2.0.0", status = API.Status.Stable)
public interface DataLayer {
	
	@Deprecated
	@API(since = "3.1.0", status=API.Status.Deprecated, toRemove = "4.0.0")
	public <T extends Data> Optional<T> get(final String uid, final String key, Class<T> clazz);
	
	@API(since = "3.1.0", status=API.Status.Experimental)
	public <T extends Data> Optional<List<T>> list (final String uid, final String key, Class<T> clazz);
	
	boolean add(final String uid, final String key, final Data value);
	
	public boolean exists(final String uid, final String key);
	
	public void remove(final String uid, final String key);
	
	@API(since = "3.1.0", status=API.Status.Experimental)
	public void clear (final String key);
	
	<T extends Data> void each (BiConsumer<String, T> consumer, String key, Class<T> clazz);
	
}
