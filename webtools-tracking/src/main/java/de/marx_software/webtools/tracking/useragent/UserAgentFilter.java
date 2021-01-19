package de.marx_software.webtools.tracking.useragent;

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
import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;
import de.marx_software.webtools.api.analytics.Fields;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marx
 */
public class UserAgentFilter {

	private static UserAgentFilter INSTANCE = null;

	public static UserAgentFilter getInstance() throws IOException {
		if (INSTANCE == null) {
			INSTANCE = new UserAgentFilter();
		}

		return INSTANCE;
	}

	final UserAgentParser parser;

	private UserAgentFilter() throws IOException {
		try {
			parser = new UserAgentService().loadParser();
		} catch (ParseException ex) {
			throw new IOException(ex);
		}
	}

	public void filter(final JSONObject object) {
		if (object.getJSONObject("data").containsKey(Fields.UserAgent.value())) {
			String userAgentString = object.getJSONObject("data").getString(Fields.UserAgent.value());
			
			final Capabilities capabilities = parser.parse(userAgentString);
			
			if (capabilities != null){
				object.getJSONObject("data").put("browser.name", capabilities.getBrowser());
				object.getJSONObject("data").put("browser.version", capabilities.getBrowserMajorVersion());
				object.getJSONObject("data").put("browser.type", capabilities.getBrowserType());
				object.getJSONObject("data").put("os.name", capabilities.getPlatform());
				object.getJSONObject("data").put("os.version", capabilities.getPlatformVersion());
				object.getJSONObject("data").put("device.type", capabilities.getDeviceType());
			}
		}
	}
}
