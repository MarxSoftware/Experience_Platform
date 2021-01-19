package de.marx_software.webtools.tracking.referrer;

/*-
 * #%L
 * webtools-incubator
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
public class Referrer {

	public static final Referrer UNKNOWN = new Referrer(Medium.UNKNOWN, "Unknown");
	public static final Referrer INTERNAL = new Referrer(Medium.INTERNAL, "Internal");
	
	public final Medium medium;
	public final String source;
	public Utm utm;

	public static Referrer unknown () {
		return new Referrer(Medium.UNKNOWN, "Unknown");
	}
	
	public Referrer(Medium medium, String source) {
		this(medium, source, null);
	}
	public Referrer(Medium medium, String source, Utm utm) {
		this.medium = medium;
		this.source = source;
		this.utm = utm;
	}

	public Utm getUtm() {
		return utm;
	}

	public void setUtm(Utm utm) {
		this.utm = utm;
	}
	
	

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (!(other instanceof Referrer)) {
			return false;
		}

		Referrer r = (Referrer) other;
		return ((this.medium != null && this.medium.equals(r.medium)) || this.medium == r.medium)
				&& ((this.source != null && this.source.equals(r.source)) || (this.source == null ? r.source == null : this.source.equals(r.source)));
	}

	@Override
	public int hashCode() {
		int h = medium == null ? 0 : medium.hashCode();
		h += source == null ? 0 : source.hashCode();
		return h;
	}

	@Override
	public String toString() {
		return String.format("{medium: %s, source: %s}",
				medium, source);
	}
}
