package com.thorstenmarx.webtools.api;

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
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author marx
 */
public class TimeWindow implements Serializable {

	private static final long serialVersionUID = -4445988686501355836L;

	
	public enum UNIT {
		MINUTE(1000 * 60), 
		HOUR(MINUTE.millis * 60l), 
		DAY( (long)HOUR.millis * 24l  ), 
		WEEK( (long)DAY.millis * 7l ), 
		MONTH(DAY.millis * 30l), 
		YEAR((long)DAY.millis * 365l);
		// YEARE = 31536000000l ; WEEK = 604800000l : DAY = 86400000l

		long millis;

		
		UNIT(final long millis) {
			this.millis = millis;
		}
	}

	static long MINUTE = 1000l * 60l;
	
	private UNIT unit;
	private long count;

	public TimeWindow() {
		
	}
	
	public TimeWindow(final UNIT unit, final long count) {
		this.unit = unit;
		this.count = count;
	}

	public long millis() {
		return unit.millis * count;
	}

	public UNIT getUnit() {
		return unit;
	}

	public long getCount() {
		return count;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.unit);
		hash = 97 * hash + (int) (this.count ^ (this.count >>> 32));
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
		final TimeWindow other = (TimeWindow) obj;
		if (this.count != other.count) {
			return false;
		}
		return this.unit == other.unit;
	}

	@Override
	public String toString() {
		return "TimeUnit{" + "unit=" + unit + ", count=" + count + '}';
	}
	
	
}
