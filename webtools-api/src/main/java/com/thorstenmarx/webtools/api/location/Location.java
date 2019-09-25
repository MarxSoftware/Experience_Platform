package com.thorstenmarx.webtools.api.location;

import com.thorstenmarx.webtools.api.annotations.API;

/*-
 * #%L
 * webtools-analytics
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
@API(since = "2.3.0", status = API.Status.Stable)
public class Location {

	private final String city;
	private final String country;
	private final String postalcode;
	private String countryIso;
	
	public Location (final String city, final String country, final String postalcode) {
		this.city = city;
		this.country = country;
		this.postalcode = postalcode;
	}

	public String getCountryIso() {
		return countryIso;
	}

	public Location setCountryIso(String countryIso) {
		this.countryIso = countryIso;
		return this;
	}
	
	

	public String getPostalcode() {
		return postalcode;
	}

	
	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}
	
	
}
