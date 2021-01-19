package de.marx_software.webtools.api.entities.criteria;

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

import de.marx_software.webtools.api.entities.criteria.restrictions.BooleanRestriction;
import de.marx_software.webtools.api.entities.criteria.restrictions.DoubleRestriction;
import de.marx_software.webtools.api.entities.criteria.restrictions.FloatRestriction;
import de.marx_software.webtools.api.entities.criteria.restrictions.IntegerRestriction;
import de.marx_software.webtools.api.entities.criteria.restrictions.LongRestriction;
import de.marx_software.webtools.api.entities.criteria.restrictions.StringRestriction;

/**
 *
 * @author marx
 */
public final class Restrictions {

	private Restrictions() {
	}

	public final static class EQ {

		public static Restriction eq(final String name, final String value) {
			return new StringRestriction(name, value);
		}

		public static Restriction eq(final String name, final Integer value) {
			return new IntegerRestriction(Restriction.Mode.EQ, name, value);
		}

		public static Restriction eq(final String name, final Long value) {
			return new LongRestriction(Restriction.Mode.EQ, name, value);
		}

		public static Restriction eq(final String name, final Double value) {
			return new DoubleRestriction(Restriction.Mode.EQ, name, value);
		}

		public static Restriction eq(final String name, final Float value) {
			return new FloatRestriction(Restriction.Mode.EQ, name, value);
		}

		public static Restriction eq(final String name, final Boolean value) {
			return new BooleanRestriction(Restriction.Mode.EQ, name, value);
		}
	}

	public final static class GT {

		public static Restriction gt(final String name, final Integer value) {
			return new IntegerRestriction(Restriction.Mode.GT, name, value);
		}
		public static Restriction gt(final String name, final Long value) {
			return new LongRestriction(Restriction.Mode.GT, name, value);
		}
		public static Restriction gt(final String name, final Double value) {
			return new DoubleRestriction(Restriction.Mode.GT, name, value);
		}
		public static Restriction gt(final String name, final Float value) {
			return new FloatRestriction(Restriction.Mode.GT, name, value);
		}
	}

	public final static class GTE {

		public static Restriction gte(final String name, final Integer value) {
			return new IntegerRestriction(Restriction.Mode.GTE, name, value);
		}
		public static Restriction gte(final String name, final Long value) {
			return new LongRestriction(Restriction.Mode.GTE, name, value);
		}
		public static Restriction gte(final String name, final Double value) {
			return new DoubleRestriction(Restriction.Mode.GTE, name, value);
		}
		public static Restriction gte(final String name, final Float value) {
			return new FloatRestriction(Restriction.Mode.GTE, name, value);
		}
	}

	public final static class LT {

		public static Restriction lt(final String name, final Integer value) {
			return new IntegerRestriction(Restriction.Mode.LT, name, value);
		}
		public static Restriction lt(final String name, final Long value) {
			return new LongRestriction(Restriction.Mode.LT, name, value);
		}
		public static Restriction lt(final String name, final Double value) {
			return new DoubleRestriction(Restriction.Mode.LT, name, value);
		}
		public static Restriction lt(final String name, final Float value) {
			return new FloatRestriction(Restriction.Mode.LT, name, value);
		}
	}

	public final static class LTE {

		public static Restriction lte(final String name, final Integer value) {
			return new IntegerRestriction(Restriction.Mode.LTE, name, value);
		}
		public static Restriction lte(final String name, final Long value) {
			return new LongRestriction(Restriction.Mode.LTE, name, value);
		}
		public static Restriction lte(final String name, final Double value) {
			return new DoubleRestriction(Restriction.Mode.LTE, name, value);
		}
		public static Restriction lte(final String name, final Float value) {
			return new FloatRestriction(Restriction.Mode.LTE, name, value);
		}
	}
}
