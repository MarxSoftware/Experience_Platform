
package com.thorstenmarx.webtools.configuration;

/*-
 * #%L
 * webtools-configuration
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

import com.thorstenmarx.webtools.api.configuration.Configuration;
import com.thorstenmarx.webtools.api.configuration.Registry;
import com.thorstenmarx.webtools.configuration.store.MariaDbDB;
import javax.sql.DataSource;

/**
 *
 * @author marx
 */
public class RemoteRegistryImpl implements Registry {
	
	private final DataSource dataSource;
	
	private final MariaDbDB db;
	
	public RemoteRegistryImpl (final DataSource dataSource) {
		this.dataSource = dataSource;
		db = new MariaDbDB(dataSource);
	}
	
	@Override
	public Configuration getConfiguration (final String namespace) {
		return new ConfigurationImpl(namespace, db);
	}
}
