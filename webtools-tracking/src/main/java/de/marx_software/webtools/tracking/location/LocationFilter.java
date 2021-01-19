package de.marx_software.webtools.tracking.location;

/*-
 * #%L
 * webtools-tracking
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

import com.alibaba.fastjson.JSONObject;
import de.marx_software.webtools.api.analytics.Fields;
import de.marx_software.webtools.api.analytics.Filter;
import de.marx_software.webtools.api.location.Location;
import de.marx_software.webtools.api.location.LocationProvider;

/**
 *
 * @author marx
 */
public class LocationFilter implements Filter {

	private final LocationProvider provider;

	public LocationFilter(final LocationProvider provider) {
		this.provider = provider;
	}

	@Override
	public void filter(final JSONObject object) {
		JSONObject meta = object.getJSONObject("meta");
		if (meta.containsKey(Fields.IP.value())) {
			String ip = meta.getString(Fields.IP.value());
			Location location = provider.getLocation(ip);
			if (location != null) {
				object.getJSONObject("data").put(Fields.Location_City.value(), location.getCity());
				object.getJSONObject("data").put(Fields.Location_Country.value(), location.getCountry());
				object.getJSONObject("data").put(Fields.Location_Country_Iso.value(), location.getCountryIso());
			}
		}
	}
}
