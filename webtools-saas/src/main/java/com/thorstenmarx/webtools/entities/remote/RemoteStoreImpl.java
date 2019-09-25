package com.thorstenmarx.webtools.entities.remote;

/*-
 * #%L
 * webtools-entities
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

import com.mongodb.DB;
import com.thorstenmarx.webtools.api.entities.Store;
import com.thorstenmarx.webtools.api.entities.Result;
import com.thorstenmarx.webtools.api.entities.Serializer;
import com.thorstenmarx.webtools.entities.annotations.AnnotationHelper;
import com.thorstenmarx.webtools.api.entities.annotations.Entity;
import com.thorstenmarx.webtools.api.entities.criteria.Criteria;
import com.thorstenmarx.webtools.api.model.Pair;
import com.thorstenmarx.webtools.entities.criteria.LuceneCriteria;
import com.thorstenmarx.webtools.entities.store.DBAttribute;
import com.thorstenmarx.webtools.entities.store.DBEntity;
import com.thorstenmarx.webtools.entities.store.MariaDB;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.sql.DataSource;

/**
 *
 * @author marx
 * @param <T>
 */
public class RemoteStoreImpl<T> implements Store<T> {

	private final MariaDB db;
	private final Class<T> typeClass;
	private final String type;

	final AnnotationHelper<T> annotationHelper;

	final Serializer<T> serializer;
	
	final String userKey;

	protected RemoteStoreImpl(final MariaDB db,
			final Class<T> typeClass, final Serializer<T> serializer,
			final String userKey) {
		
		this.typeClass = typeClass;
		this.serializer = serializer;
		this.userKey = userKey;
		type = typeClass.getAnnotation(Entity.class).type();
		
		this.db = db;
		
		annotationHelper = new AnnotationHelper<>(typeClass);
	}
	
	@Override
	public Criteria criteria() {
		return new LuceneCriteria(type, typeClass, db, annotationHelper, serializer);
	}

	@Override
	public int size() {
		return db.count(type);
	}

	@Override
	public void clear() {
		db.clear(type);
	}

	@Override
	public void delete(final T entity) {
		db.delete(annotationHelper.getId(entity));
	}

	@Override
	public T get(final String id) {
		DBEntity entity = db.get(id);
		final T instance = fromJSON(entity);
		return instance;
	}

	private T fromJSON(final DBEntity entity) {
		final T right = serializer.deserialize(entity.version(), entity.content()).right;
		annotationHelper.setId(right, entity.id());
		return right;
	}

	@Override
	public Result<T> list(final int offset, final int limit) {
		Result<DBEntity> entities = db.list(type, offset, limit);
		Result<T> resultList = new Result<>(entities.totalSize(), entities.offset(), entities.limit());

		entities.stream().map(this::fromJSON).forEach(resultList::add);

		return resultList;
	}

	@Override
	public List<String> save(final List<T> entities) {
		if (entities == null || entities.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		if (!typeClass.isAssignableFrom(entities.get(0).getClass()) || !entities.get(0).getClass().isAnnotationPresent(Entity.class)) {
			throw new IllegalArgumentException("Entity annotation not present");
		}
		try {
			List<DBEntity> dbEntities = new ArrayList<>();
			for (final T entity : entities) {

				String id = annotationHelper.getId(entity);
				boolean update = false;
				if (id == null || id.equals("")) {
					id = UUID.randomUUID().toString();
					annotationHelper.setId(entity, id);
				} else {
					update = true;
				}

				Pair<String, String> content = serializer.serialize(entity);
				com.thorstenmarx.webtools.entities.store.DBEntity storeEntity = new com.thorstenmarx.webtools.entities.store.DBEntity(type, content.left);
				storeEntity.setUpdate(update);
				storeEntity.id(id);
				storeEntity.content(content.right);

				
				for (final Field field : entity.getClass().getDeclaredFields()) {
					if (field.isAnnotationPresent(com.thorstenmarx.webtools.api.entities.annotations.Field.class)) {
						com.thorstenmarx.webtools.api.entities.annotations.Field annotation = field.getAnnotation(com.thorstenmarx.webtools.api.entities.annotations.Field.class);

						boolean accessible = field.isAccessible();
						field.setAccessible(true);

						try {
							DBAttribute attribute = annotationHelper.fieldToAttribute(field, annotation, entity);
							if (attribute != null) {
								storeEntity.addAttribute(attribute);
							} else if (field.get(entity) != null) {
								Object value = field.get(entity);
								if (value instanceof Collection) {
									addAttributes(annotationHelper.getFieldName(field, annotation), (Collection) value, storeEntity);
								} else {
									addAttributes(annotationHelper.getFieldName(field, annotation), value, storeEntity);
								}
							}
						} finally {
							field.setAccessible(accessible);
						}
					}
				}
				dbEntities.add(storeEntity);
			}

			db.batch(dbEntities);
			Function<? super DBEntity, ? extends String> fnctn = (DBEntity t) -> t.id();

			return dbEntities.stream().map(fnctn).collect(Collectors.toList());
		} catch (IllegalAccessException illae) {
			throw new RuntimeException(illae);
		}
	}

	@Override
	public String save(final T entity) {
		if (!typeClass.isAssignableFrom(entity.getClass()) || !entity.getClass().isAnnotationPresent(Entity.class)) {
			throw new IllegalArgumentException("Entity annotation not present");
		}
		try {

			String id = annotationHelper.getId(entity);
			boolean update = false;
			if (id == null || id.equals("")) {
				id = UUID.randomUUID().toString();
				annotationHelper.setId(entity, id);
			} else {
				update = true;
			}

			Pair<String, String> content = serializer.serialize(entity);
			com.thorstenmarx.webtools.entities.store.DBEntity storeEntity = new com.thorstenmarx.webtools.entities.store.DBEntity(type, content.left);
			storeEntity.setUpdate(update);
			storeEntity.id(id);
			storeEntity.content(content.right);

			for (final Field field : entity.getClass().getDeclaredFields()) {
				if (field.isAnnotationPresent(com.thorstenmarx.webtools.api.entities.annotations.Field.class)) {
					com.thorstenmarx.webtools.api.entities.annotations.Field annotation = field.getAnnotation(com.thorstenmarx.webtools.api.entities.annotations.Field.class);

					boolean accessible = field.isAccessible();
					field.setAccessible(true);

					try {
						DBAttribute attribute = annotationHelper.fieldToAttribute(field, annotation, entity);
						if (attribute != null) {
							storeEntity.addAttribute(attribute);
						} else if (field.get(entity) != null) {
							Object value = field.get(entity);
							if (value instanceof Collection) {
								addAttributes(annotationHelper.getFieldName(field, annotation), (Collection) value, storeEntity);
							} else {
								addAttributes(annotationHelper.getFieldName(field, annotation), value, storeEntity);
							}
						}
					} finally {
						field.setAccessible(accessible);
					}
				}
			}
			db.add(storeEntity);

			return storeEntity.id();
		} catch (IllegalAccessException illae) {
			throw new RuntimeException(illae);
		}
	}

	private void addAttributes(final String namePrefix, final Collection entityCollection, final DBEntity storeEntity) throws IllegalArgumentException, IllegalAccessException {
		for (Object value : entityCollection) {
			addAttributes(namePrefix, value, storeEntity);
		}
	}

	private void addAttributes(final String namePrefix, final Object entity, final DBEntity storeEntity) throws IllegalArgumentException, IllegalAccessException {
		if (!entity.getClass().isAnnotationPresent(Entity.class)) {
			return;
		}
		for (final Field field : entity.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(com.thorstenmarx.webtools.api.entities.annotations.Field.class)) {
				com.thorstenmarx.webtools.api.entities.annotations.Field annotation = field.getAnnotation(com.thorstenmarx.webtools.api.entities.annotations.Field.class);
				boolean accessible = field.isAccessible();
				field.setAccessible(true);

				try {
					field.setAccessible(true);

					DBAttribute attribute = annotationHelper.fieldToAttribute(field, annotation, entity, namePrefix);
					if (attribute != null) {
						storeEntity.addAttribute(attribute);
					} else {
						Object value = field.get(entity);
						if (value instanceof Collection) {
							addAttributes(annotationHelper.getFieldName(field, annotation), (Collection) value, storeEntity);
						} else {
							addAttributes(annotationHelper.getFieldName(field, annotation), value, storeEntity);
						}
					}
				} finally {
					field.setAccessible(accessible);
				}
			}
		}
	}

}
