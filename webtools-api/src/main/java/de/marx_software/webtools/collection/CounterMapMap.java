package de.marx_software.webtools.collection;

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
import java.util.HashMap;

/**
 *
 * @author marx
 */
public class CounterMapMap<E, K> extends MapMap<E, K, Integer> {

	@Override
	public void add (E entry, K key, Integer value) {
		if (!maps.containsKey(entry)) {
			maps.put(entry, new HashMap<>());
		}
		
		int oldValue = maps.get(entry).containsKey(key) ? maps.get(entry).get(key) : 0;
		
		maps.get(entry).put(key, oldValue + value);
	}
	
}
