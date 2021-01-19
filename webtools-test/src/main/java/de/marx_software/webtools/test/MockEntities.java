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
package de.marx_software.webtools.test;

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
import de.marx_software.webtools.api.actions.model.Segment;
import de.marx_software.webtools.api.entities.Entities;
import de.marx_software.webtools.api.entities.Result;
import de.marx_software.webtools.api.entities.Serializer;
import de.marx_software.webtools.api.entities.Store;
import de.marx_software.webtools.api.entities.criteria.Criteria;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author marx
 */
public class MockEntities implements Entities {

	@Override
	public <T> Store<T> store(Class<T> clazz) {
		return new MockStore<>();
	}

	@Override
	public <T> Store<T> store(Class<T> clazz, Serializer<T> serializer) {
		return new MockStore<>();
	}

	public static class MockStore<T> implements Store<T> {

		public Map<String, T> entities = new HashMap<>();

		@Override
		public Criteria criteria() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void delete(T entity) {
			entities.remove(entity);
		}

		@Override
		public T get(String id) {
			return entities.get(id);
		}

		@Override
		public Result<T> list(final int offset, final int limit) {
			return new MockResult<>(List.copyOf(entities.values()), offset, limit);
		}

		@Override
		public String save(T entity) {
			String uid = UUID.randomUUID().toString();
			((Segment)entity).setId(uid);
			entities.put(uid, entity);
			return uid;
		}

		@Override
		public List<String> save(List<T> entities) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void clear() {
			entities.clear();
		}

		@Override
		public int size() {
			return entities.size();
		}

	}
	
	public static class MockResult<T> extends ArrayList<T> implements Result<T>  {

		private final int offset;
		private final int limit;
		
		protected MockResult (final List<T> result, final int offset, final int limit) {
			super();
			addAll(result);
			this.offset = offset;
			this.limit = limit;
		}
		
		@Override
		public int totalSize() {
			return size();
		}

		@Override
		public int offset() {
			return offset;
		}

		@Override
		public int limit() {
			return limit;
		}
		
	}
}
