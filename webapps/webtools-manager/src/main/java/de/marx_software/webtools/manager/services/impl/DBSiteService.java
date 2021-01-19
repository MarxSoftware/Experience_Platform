package de.marx_software.webtools.manager.services.impl;

/*-
 * #%L
 * webtools-manager
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

import de.marx_software.webtools.api.entities.Entities;
import de.marx_software.webtools.api.entities.Store;
import de.marx_software.webtools.api.model.Site;
import de.marx_software.webtools.manager.services.SiteService;
import java.util.Collection;

/**
 *
 * @author marx
 */
public class DBSiteService implements SiteService {

	private final Store<Site> store;
	
	public DBSiteService (final Entities entities) {
		store = entities.store(Site.class);
	}
	
	@Override
	public void add(Site site) {
		store.save(site);
	}

	@Override
	public void remove(String id) {
		store.delete(store.get(id));
	}

	@Override
	public Site get(String id) {
		return store.get(id);
	}

	@Override
	public Collection<Site> all() {
		return store.list(0, Integer.MAX_VALUE);
	}
	
}
