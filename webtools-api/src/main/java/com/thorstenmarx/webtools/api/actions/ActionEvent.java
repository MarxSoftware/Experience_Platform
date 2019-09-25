package com.thorstenmarx.webtools.api.actions;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author thmarx
 */
public class ActionEvent {

	private Map<String, Object> data;

	private String userid;

	private String event;

	public ActionEvent(final Builder builder) {
		this.data = builder.data;
		this.userid = builder.userid;
		this.event = builder.event;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public String getUserid() {
		return userid;
	}

	public String getEvent() {
		return event;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 37 * hash + Objects.hashCode(this.data);
		hash = 37 * hash + Objects.hashCode(this.userid);
		hash = 37 * hash + Objects.hashCode(this.event);
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
		final ActionEvent other = (ActionEvent) obj;
		if (!Objects.equals(this.userid, other.userid)) {
			return false;
		}
		if (!Objects.equals(this.event, other.event)) {
			return false;
		}
		if (!Objects.equals(this.data, other.data)) {
			return false;
		}
		return true;
	}
	
	

	public static ActionEvent.Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Map<String, Object> data = new HashMap<>();;

		private String userid;

		private String event;

		public Builder() {
		}

		public Builder data(final String name, final Object object) {
			this.data.put(name, object);
			return this;
		}

		public Builder setUserid(String userid) {
			this.userid = userid;
			return this;
		}

		public Builder setEvent(String event) {
			this.event = event;
			return this;
		}
		
		public ActionEvent build () {
			return new ActionEvent(this);
		}
		
	}
}
