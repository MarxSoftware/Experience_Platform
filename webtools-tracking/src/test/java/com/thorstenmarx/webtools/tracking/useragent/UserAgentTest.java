/*
 * Copyright (C) 2019 WP DigitalExperience
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
package com.thorstenmarx.webtools.tracking.useragent;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;

/**
 *
 * @author thmar
 */
public class UserAgentTest {

	public static void main(final String... args) throws Exception {
		final UserAgentParser parser = new UserAgentService().loadParser();

		final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:71.0) Gecko/20100101 Firefox/71.0";
		final Capabilities capabilities = parser.parse(userAgent);
		
		System.out.println(capabilities.getBrowser());
		System.out.println(capabilities.getBrowserType());
		System.out.println(capabilities.getDeviceType());
	}
}
