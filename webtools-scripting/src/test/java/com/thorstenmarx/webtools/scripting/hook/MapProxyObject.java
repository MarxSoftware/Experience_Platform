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
package com.thorstenmarx.webtools.scripting.hook;

/*-
 * #%L
 * webtools-scripting
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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

/**
 *
 * @author marx
 */
public class MapProxyObject implements ProxyObject, Map<String, Object> { 
    private final Map<String, Object> values;

    public MapProxyObject(Map<String, Object> map) {
        this.values = map;
    }

    public void putMember(String key, Value value) {
        values.put(key, value.isHostObject() ? value.asHostObject() : value);
    }

    public boolean hasMember(String key) {
        return values.containsKey(key);
    }

    public Object getMemberKeys() {
        return values.keySet().toArray();
    }

    public Object getMember(String key) {
        Object v = values.get(key);
        if (v instanceof Map) {
            return new MapProxyObject((Map<String, Object>)v);
        } else {
            return v;
        }
    }

    public Map<String, Object> getMap() {
        return values;
    }

	@Override
	public int size() {
		return values.size();
	}

	@Override
	public boolean isEmpty() {
		return values.isEmpty();
	}

	@Override
	public boolean containsKey(Object arg0) {
		return values.containsKey(arg0);
	}

	@Override
	public boolean containsValue(Object arg0) {
		return values.containsValue(arg0);
	}

	@Override
	public Object get(Object arg0) {
		return values.get(arg0);
	}

	@Override
	public Object put(String arg0, Object arg1) {
		return values.put(arg0, arg1);
	}

	@Override
	public Object remove(Object arg0) {
		return values.remove(arg0);
	}

	@Override
	public void putAll(Map arg0) {
		values.putAll(arg0);
	}

	@Override
	public void clear() {
		values.clear();
	}

	@Override
	public Set keySet() {
		return values.keySet();
	}

	@Override
	public Collection values() {
		return values.values();
	}

	@Override
	public Set entrySet() {
		return values.entrySet();
	}
}
