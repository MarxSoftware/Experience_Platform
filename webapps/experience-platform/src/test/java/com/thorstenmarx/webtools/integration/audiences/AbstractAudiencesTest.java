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
package de.marx_software.webtools.integration.audiences;

import de.marx_software.webtools.integration.Audiences;
import de.marx_software.webtools.integration.Tracking;
import java.util.UUID;
import okhttp3.MediaType;

/**
 *
 * @author marx
 */
public abstract class AbstractAudiencesTest {

	protected final String API_KEY = "f8f8s8v1ih8lhjotbocaccuo2f";
	protected final String SITE = "2bce5f5e-be17-442d-ada1-a7ed6e849047";

	protected final String USER_ID = UUID.randomUUID().toString();
	protected final String VID_ID = UUID.randomUUID().toString();

	protected final Audiences AUDIENCES = new Audiences(SITE, API_KEY);
	protected final Tracking TRACKING = new Tracking(USER_ID, VID_ID, SITE);

	public static final MediaType JSON
			= MediaType.get("application/json; charset=utf-8");

	protected String REQ_ID() {
		return UUID.randomUUID().toString();
	}
}
