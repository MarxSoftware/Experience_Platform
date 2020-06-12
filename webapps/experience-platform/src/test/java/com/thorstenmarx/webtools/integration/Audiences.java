/*
 * Copyright (C) 2020 WP DigitalExperience
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.thorstenmarx.webtools.integration;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author marx
 */
public class Audiences {

	final String site;

	public Audiences(final String site) {
		this.site = site;
	}

	public JSONObject create(final String name, final int externalId, final boolean active, final String content) throws IOException {
		JSONObject audience = new JSONObject();
		audience.put("name", name);
		audience.put("externalId", externalId);
		audience.put("site", site);
		audience.put("content", loadContent(content));
		audience.put("active", active);
		
		return audience;
	}

	private String loadContent(final String file) throws IOException {
		return new String(Files.readAllBytes(Paths.get(file)));
	}
}
