package de.marx_software.webtools.api.entities;

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
import de.marx_software.webtools.api.entities.criteria.Criteria;
import java.util.List;

/**
 *
 * @author marx
 * @since 1.11.0
 */
public interface Store<T> {

	Criteria criteria();

	void delete(final T entity);

	T get(final String id);

	Result<T> list(final int offset, final int limit);

	String save(final T entity);
	
	/**
	 * Adds a list of entities in a single transaction
	 * @param entities
	 * @return 
	 */
	public List<String> save(final List<T> entities);
	
	void clear ();
	
	int size ();
}
