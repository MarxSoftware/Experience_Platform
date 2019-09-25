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
package com.thorstenmarx.webtools.actions.segmentation.mocks;

/*-
 * #%L
 * webtools-actions
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

import com.google.common.collect.Multimap;
import com.thorstenmarx.webtools.api.datalayer.Data;
import com.thorstenmarx.webtools.api.datalayer.DataLayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 *
 * @author marx
 */
public class MockDataLayer implements DataLayer {

	private Map<String, Map<String, List<Object>>> values = new HashMap<>();
	
	public MockDataLayer () {
		
	}
	
	@Override
	public <T extends Data> Optional<T> get(String uid, String key, Class<T> clazz) {
		throw new UnsupportedOperationException();
	}
	
	public void remove (String uid, String key) {
		if (values.containsKey(key)) {
			values.get(key).remove(uid);
		}
	}

	@Override
	public boolean add(String uid, String key, Data value) {
		if (values.containsKey(key) && values.get(key).containsKey(uid)) {
			values.get(key).get(uid).add(value);
		} else if (values.containsKey(key)) {
			values.get(key).put(uid, new ArrayList<>());
			values.get(key).get(uid).add(value);
		} else {
			Map<String, List<Object>> subValues = new HashMap<>();
			subValues.put(uid, new ArrayList<>());
			subValues.get(uid).add(value);
			values.put(key, subValues);
		}
		return true;
	}

	@Override
	public boolean exists(String uid, String key) {
		
		if (values.containsKey(key)) {
			return values.get(key).containsKey(uid);
		}
		
		
		return false;
	}

	@Override
	public <T extends Data> void each(BiConsumer<String, T> consumer, String key, Class<T> clazz) {
		if (values.containsKey(key)) {
			values.get(key).values().forEach((value) -> {
				consumer.accept(key, (T)value);
			});
		}
	}

	@Override
	public <T extends Data> Optional<List<T>> list(String uid, String key, Class<T> clazz) {
		if (values.containsKey(key) && values.get(key).containsKey(uid)) {
			List<Object> get = values.get(key).get(uid);
			List<T> result = get.stream().map((o) -> (T)o).collect(Collectors.toList());
			return Optional.of(result);
		}
		
		return Optional.empty();
	}

	@Override
	public void clear(String key) {
		values.remove(key);
	}
	
}
