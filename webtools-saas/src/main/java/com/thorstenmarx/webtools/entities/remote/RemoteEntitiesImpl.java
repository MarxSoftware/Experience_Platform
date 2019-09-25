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
import com.thorstenmarx.webtools.entities.*;
import com.thorstenmarx.webtools.api.entities.Entities;
import com.thorstenmarx.webtools.api.entities.Serializer;
import com.thorstenmarx.webtools.api.entities.Store;
import com.thorstenmarx.webtools.entities.store.MariaDB;
import javax.sql.DataSource;


/**
 *
 * @author marx
 */
public class RemoteEntitiesImpl implements Entities {

	final String userKey;
	final MariaDB db;
	
	public RemoteEntitiesImpl(final DataSource ds, final DB db, final String userKey) {
		this.db = new MariaDB(ds, db);
		this.userKey = userKey;
	}

	public void open () {
		this.db.open();
	}
	public void close () {
		this.db.close();
	}

	@Override
	public <T> RemoteStoreImpl<T> store(final Class<T> clazz) {
		if (!clazz.isAnnotationPresent(com.thorstenmarx.webtools.api.entities.annotations.Entity.class)) {
			throw new IllegalArgumentException("Entity annotation not present!");
		}
		return new RemoteStoreImpl<>(db, clazz, new GsonSerializer<>(clazz), userKey);
	}

	@Override
	public <T> Store<T> store(final Class<T> clazz, final Serializer<T> serializer) {
		if (!clazz.isAnnotationPresent(com.thorstenmarx.webtools.api.entities.annotations.Entity.class)) {
			throw new IllegalArgumentException("Entity annotation not present!");
		}
		return new RemoteStoreImpl<>(db, clazz, serializer, userKey);
	}

}
