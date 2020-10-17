package com.thorstenmarx.webtools.tracking.location;

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

import com.thorstenmarx.webtools.api.location.LocationProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.maxmind.db.CHMCache;
import com.maxmind.db.Reader;
import com.thorstenmarx.webtools.api.location.Location;
import com.thorstenmarx.webtools.base.Configuration;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author marx
 */
public class MaxmindLocationProvider implements LocationProvider {

	private static final Logger LOGGER = LogManager.getLogger(MaxmindLocationProvider.class);

	private Reader current;

	private final Configuration configuration;

	public MaxmindLocationProvider(final Configuration configuration) {
		this.configuration = configuration;
		File database = new File(configuration.baseDir(), "data/GeoLite2-City.mmdb");
		LOGGER.debug("loading maxmind geolite database : " + database.exists());
		if (database.exists()) {
			try {
				current = new Reader(database, new CHMCache());
			} catch (IOException ex) {
				LOGGER.error("error loading maxmind database", ex);
			}
		} else {
			LOGGER.warn("GeoLite database not found at " + database.getAbsolutePath());
		}

	}

	@Override
	public boolean isAvailable() {
		return current != null;
	}

	@Override
	public Location getLocation(final String ip) {
		if (!isAvailable()) {
			return LocationProvider.UNKNOWN;
		}
		try {
			InetAddress inet = InetAddress.getByName(ip);
			JsonNode node = current.get(inet);
			if (node != null) {
				String country = node.get("country").get("names").get("en").asText();
				String countryIso = node.get("country").get("iso_code").asText();
				String city = node.get("city").get("names").get("en").asText();
				String postalcode = node.get("postal").get("code").asText();

				return new Location(city, country, postalcode).setCountryIso(countryIso);
			}

		} catch (UnknownHostException ex) {
			LOGGER.error("", ex);
		} catch (IOException ex) {
			LOGGER.error("", ex);
		}
		return null;
	}

	@Override
	public void close() {
		try {
			current.close();
		} catch (IOException ex) {
			LOGGER.error("", ex);
		}
	}
}
