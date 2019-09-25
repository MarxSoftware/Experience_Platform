package com.thorstenmarx.webtools.api.actions.model;

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
 * @author thmarx
 */
@Entity(type = "segment")
public class AdvancedSegment extends Segment implements Serializable {
	
	private String dsl;
	
	@Field(name = "externalId")
	private long externalId;
	
	public AdvancedSegment () {
		super();
	}
	public AdvancedSegment (final Segment segment) {
		super();
		start(segment.startTimeWindow());
		setId(segment.getId());
		setName(segment.getName());
	}


	public long getExternalId() {
		return externalId;
	}

	public void setExternalId(long externalId) {
		this.externalId = externalId;
	}

	public String getDsl() {
		return dsl;
	}

	public void setDsl(String dsl) {
		this.dsl = dsl;
	}
	
	
	
	/**
	 *
	 * @return
	 */
	@Override
	public long end() {
		// we use the current date
		return System.currentTimeMillis();
	}
	@Override
	public long start() {
		long now = System.currentTimeMillis();
		return now - timeWindow.millis();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.dsl);
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
		final AdvancedSegment other = (AdvancedSegment) obj;
		if (!Objects.equals(this.getId(), other.getId())) {
			return false;
		}
		return true;
	}
	
	
	
}
