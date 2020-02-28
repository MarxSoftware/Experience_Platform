package com.thorstenmarx.webtools.api.model;

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
import com.thorstenmarx.webtools.api.entities.annotations.Entity;
import com.thorstenmarx.webtools.api.entities.annotations.Field;
import java.io.Serializable;
import java.util.Objects;


/**
 *
 * @author marx
 */
@Entity(type = "site")
public class Site implements Serializable	{

	private static final long serialVersionUID = 453480889100256259L;

	@Field(name = "id", key = true)
	private String id;
	private String name;
	@Field(name = "apikey")
	private String apikey;
	
	public Site () {
		
	}
	public Site (final String id, final String name) {
		this.id = id;
		this.name = name;
	}

	public String getApikey() {
		return apikey;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}

		
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 47 * hash + Objects.hashCode(this.id);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Site other = (Site) obj;
		if (!Objects.equals(this.id, other.id)) {
			return false;
		}
		return true;
	}


	
}
