package com.thorstenmarx.webtools.api.analytics.query;

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

/**
 *
 * @author thmarx
 */
public class Query implements LimitProvider {
	
	public enum OCCUR {
		AND, OR;
	}
	public enum TYPE {
		RAW,DOCUMENT;
	}
	
	private final long start;
	private final long end;

	private final Map<String, String> terms;
	private final Map<String, String[]> multivalueTerms;
	
	private final TYPE type;
	
	private Query (Builder builder) {
		this.start = builder.start;
		this.end = builder.end;
		this.terms = builder.terms;
		this.multivalueTerms = builder.multivalueTerms;
		this.type = builder.type;
	}
	
	@Override
	public long start () {
		return start;
	}

	/**
	 *
	 * @return
	 */
	public long end () {
		return end;
	}
	
	public TYPE Type () {
		return type;
	}
	
	public Map<String, String> terms () {
		return terms;
	}
	public Map<String, String[]> multivalueTerms () {
		return multivalueTerms;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private long start;
		private long end;
		
		private Map<String, String> terms = new HashMap<>();
		private Map<String, String[]> multivalueTerms = new HashMap<>();
		
		private TYPE type;
		
		private Builder() {}
		
		
		public Builder type (final TYPE type) {
			this.type = type;
			return this;
		}
		/**
		 * The term (key, value) must match.
		 * 
		 * @param key
		 * @param value
		 * @return 
		 */
		public Builder term (final String key, final String value) {
			this.terms.put(key, value);
			return this;
		}
		/**
		 * The term (key, values[]) matchs, if one of the values matchs.
		 * Terms are connected with or.
		 * 
		 * @param key
		 * @param values
		 * @return 
		 */
		public Builder multivalueTerm (final String key, final String[] values) {
			multivalueTerms.put(key, values);
			return this;
		}
		
		public Builder start (final long start) {
			this.start = start;
			return this;
		}
		public Builder end (final long end) {
			this.end = end;
			return this;
		}
		
		public Query build () {
			return new Query(this);
		}
	}
}
