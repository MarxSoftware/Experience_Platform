package com.thorstenmarx.webtools.tracking.useragent;

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
import com.thorstenmarx.webtools.api.analytics.Fields;

/**
 *
 * @author marx
 */
public class UserAgentFilter  {

	
	public static void filter(final JSONObject object) {
		if (object.getJSONObject("data").containsKey(Fields.UserAgent.value())) {
			String userAgentString = object.getJSONObject("data").getString(Fields.UserAgent.value());
			eu.bitwalker.useragentutils.UserAgent ua = eu.bitwalker.useragentutils.UserAgent.parseUserAgentString(userAgentString);
			if (ua != null) {
				object.getJSONObject("data").put("browser.name", ua.getBrowser().name());
				object.getJSONObject("data").put("browser.group", ua.getBrowser().getGroup().name());
				object.getJSONObject("data").put("browser.version", ua.getBrowserVersion().getVersion());
				object.getJSONObject("data").put("os.name", ua.getOperatingSystem().name());
				object.getJSONObject("data").put("os.group", ua.getOperatingSystem().getGroup().name());
				object.getJSONObject("data").put("os.type", ua.getOperatingSystem().getDeviceType().name());
			}
		}
	}

}
