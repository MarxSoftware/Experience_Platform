package com.thorstenmarx.webtools.tracking;

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

import java.time.format.DateTimeFormatter;

/**
 *
 * @author marx
 */
public class Constants {


	public static final DateTimeFormatter FORMATTER_YEAR = DateTimeFormatter.ofPattern("yyyy");
	public static final DateTimeFormatter FORMATTER_YEAR_MONTH = DateTimeFormatter.ofPattern("yyyy-MM");
	public static final DateTimeFormatter FORMATTER_YEAR_MONTH_DAY = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static final DateTimeFormatter FORMATTER_YEAR_WEEK = DateTimeFormatter.ofPattern("yyyy-ww");

	public static enum Param {

		REQUEST_ID("reqid"),
		USER_ID("uid"),
		VISIT_ID("vid"),
		IP("ip"),
		TIMESTAMP("_t"),
		OFFSET("offset"),
		USERAGENT("ua"),
		EVENT("event"),
		SCORE("score"),
		SITE("site"),
		PAGE("page"),
		REFERRER("referrer"),
//		SOURCE("_source"),
		;

		final String value;

		Param(final String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}

		public static boolean is(final String name) {
			for (Param param : values()) {
				if (param.value.equals(name)) {
					return true;
				}
			}
			return false;
		}
	}
	
	public static class Event {

		public static final Event Score = new Event("score");
		public static final Event Click = new Event("click");
		public static final Event PageView = new Event("pageview");
		public static final Event PageLeave = new Event("pageleave");
		public static final Event VisitEnd = new Event("visitend");
		public static final Event AdView = new Event("adview");
		public static final Event AdClick = new Event("adclick");

		final String value;
		
		static Event[] values = new Event[7];
		static {
			values[0] = Score;
			values[1] = Click;
			values[2] = PageView;
			values[3] = PageLeave;
			values[4] = VisitEnd;
			values[5] = AdView;
			values[6] = AdClick;
		}

		private Event(final String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}

		public boolean is(final String event) {
			return value.equals(event);
		}

		public static Event forValue(final String value) {
			for (Event e : values) {
				if (e.value.equals(value)) {
					return e;
				}
			}
			// return new custom event
			return new Event(value);
		}
	}
}
