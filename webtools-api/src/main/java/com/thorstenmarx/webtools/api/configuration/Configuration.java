package com.thorstenmarx.webtools.api.configuration;

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

/**
 *
 * @author marx
 * 
 * @since 1.12.0
 */
@API(since = "2.0.0", status = API.Status.Stable)
public interface Configuration {

	Optional<Boolean> getBoolean(final String key);

	Optional<Double> getDouble(final String key);

	Optional<Float> getFloat(final String key);

	Optional<Integer> getInt(final String key);

	<T> Optional<List<T>> getList(final String key, final Class<T> type);

	Optional<Short> getShort(final String key);

	Optional<String> getString(final String key);

	boolean set(final String key, final List<?> value);

	boolean set(final String key, final Boolean value);

	boolean set(final String key, final String value);

	boolean set(final String key, final Integer value);

	boolean set(final String key, final Float value);

	boolean set(final String key, final Double value);

	boolean set(final String key, final Short value);
	
}
