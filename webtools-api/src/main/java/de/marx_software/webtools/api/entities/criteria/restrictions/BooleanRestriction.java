package de.marx_software.webtools.api.entities.criteria.restrictions;

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

import de.marx_software.webtools.api.entities.criteria.Restriction;

/**
 *
 * @author marx
 * @since 1.11.0
 */
public class BooleanRestriction implements Restriction {
	
	public final Mode mode;
	public final String name;
	public final Boolean value;
	
	public BooleanRestriction (final Mode mode, final String name, final Boolean value) {
		this.mode = mode;
		this.name = name;
		this.value = value;
	}
}
