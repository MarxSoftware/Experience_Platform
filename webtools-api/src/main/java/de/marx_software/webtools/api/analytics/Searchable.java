package de.marx_software.webtools.api.analytics;

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
import de.marx_software.webtools.api.analytics.query.Query;
import de.marx_software.webtools.api.analytics.query.ShardDocument;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author marx
 */
public interface Searchable {
	/**
	 * 
	 * @param query
	 * @return
	 * @throws IOException 
	 */
	public List<ShardDocument> search(final Query query) throws IOException;
	
	/**
	 * 
	 * @param from
	 * @param to
	 * @return 
	 */
	public boolean hasData(long from, long to);
	
	/**
	 * return the aacutal size of the searchable
	 * @return 
	 */
	public int size ();
}