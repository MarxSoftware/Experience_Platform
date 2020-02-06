package com.thorstenmarx.webtools.api.analytics;

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

/**
 *
 * @author marx
 */
public enum Fields {

	RequestId("requestid"),
	UserId("userid"),
	VisitId("visitid"),
	IP("ip"),
	YEAR("year"),
	YEAR_MONTH("year_month"),
	YEAR_MONTH_DAY("year_month_day"),
	YEAR_WEEK("year_week"),
	UserAgent("useragent"),
	IsCrawler("iscrawler"),
	Referrer("referrer"),
	Event("event"),
	Score("score"),
	Site("site"),
	Page("page"),
	Type("type"),
	Utm("utm"),
	VISIBLE("visible"),
	
//	Not_Tracked("_not_tracked"),
	
	Location_City("location.city"),
	Location_Country("location.country"),
	Location_Country_Iso("location.country.iso"),
	Location_Postalcode("location.postalcode"),
	SOURCE("_source"),
	
	_TimeStamp("_timestamp"),
	TIMESTAMP_SORT("_timestamp_sort"),
	_UUID("_uuid"),
	VERSION("_version"),
	;

		final String value;

	Fields(final String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	public static boolean isField(final String name) {
		for (Fields field : values()) {
			if (field.value.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public String combine (final String name) {
		return value + "." + name;
	}
}
