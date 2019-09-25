package com.thorstenmarx.webtools.api.datalayer;

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

import com.thorstenmarx.webtools.stream.ImmutableCollectionCollectors;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 *
 * @author marx
 */
public class SegmentData implements Data, Expirable {
	
	public static final String KEY = "segments";

	public Set<Segment> segments;

	public SegmentData() {
		segments = new HashSet<>();
	}

	@Override
	public boolean isExpired() {
		return getSegments().isEmpty();
	}
	
	/**
	 * Returns a copy of valid segments.
	 *
	 * @return
	 */
	public Set<String> getSegments() {
		final long current = System.currentTimeMillis();
		return segments.stream().filter((s) -> s.validTo >= current).map((s) -> s.name).collect(ImmutableCollectionCollectors.toImmutableSet());
	}

	public void addSegment(final String name, final long wpid, final long validTo) {
		segments.add(new Segment(name, wpid, validTo));
	}
	public void addSegment(final String name, final long wpid) {
		segments.add(new Segment(name, wpid, Long.MAX_VALUE));
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 47 * hash + Objects.hashCode(this.segments);
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
		final SegmentData other = (SegmentData) obj;
		if (!Objects.deepEquals(this.segments, other.segments)) {
			return false;
		}
		return true;
	}

	@Override
	public String getKey() {
		return KEY;
	}
	
	

	public static class Segment {

		public String name;
		public long wpid;
		public long validTo;

		public Segment() {
		}

		public Segment(final String name, final long wpid, final long validTo) {
			this.name = name;
			this.wpid = wpid;
			this.validTo = validTo;
		}

		@Override
		public int hashCode() {
			int hash = 5;
			hash = 61 * hash + Objects.hashCode(this.name);
			hash = 61 * hash + (int) (this.wpid ^ (this.wpid >>> 32));
			hash = 61 * hash + (int) (this.validTo ^ (this.validTo >>> 32));
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
			final Segment other = (Segment) obj;
			if (this.validTo != other.validTo) {
				return false;
			}
			if (this.wpid != other.wpid) {
				return false;
			}
			if (!Objects.equals(this.name, other.name)) {
				return false;
			}
			return true;
		}

		
	}
}
