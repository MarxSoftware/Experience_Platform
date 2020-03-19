package com.thorstenmarx.webtools.web.rest.resources.secured.targetaudience;

import com.thorstenmarx.webtools.api.TimeWindow;
import java.util.HashMap;
import java.util.Map;

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

/**
 *
 * @author marx
 */
public class Audience {
	
	public static final Period DEFAULT_PERIOD = new Period(TimeWindow.UNIT.YEAR.name(), 10);
	
	private String name;
	private long externalId;
	private String site;
	private String content = "";
	private boolean active = false;
	private Map<String, Object> attributes = new HashMap<>();
	
	private Period period = DEFAULT_PERIOD;

	public Audience() {
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	
	
	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public long getExternalId() {
		return externalId;
	}

	public void setExternalId(long externalId) {
		this.externalId = externalId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public static class Period {
		private String unit;
		private int count;

		
		public Period() {
		}
		
		public Period(String unit, int count) {
			super();
			this.unit = unit;
			this.count = count;
		}
		
		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}
		
		TimeWindow toTimeWindow () {
			return new TimeWindow(TimeWindow.UNIT.valueOf(unit), count);
		}
	}
}
