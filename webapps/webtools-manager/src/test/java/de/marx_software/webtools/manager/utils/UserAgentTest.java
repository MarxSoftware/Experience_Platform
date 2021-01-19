package de.marx_software.webtools.manager.utils;

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

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class UserAgentTest {
	@Test
	public void testUserAgent () {
		final String userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:51.0) Gecko/20100101 Firefox/51.0";
		final UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
		
		Assertions.assertThat(userAgent.getBrowser()).isEqualTo(Browser.FIREFOX51);
		Assertions.assertThat(userAgent.getOperatingSystem().getGroup()).isEqualTo(OperatingSystem.WINDOWS);
		Assertions.assertThat(userAgent.getOperatingSystem().getDeviceType()).isEqualTo(DeviceType.COMPUTER);
		
		System.out.println(userAgent.getBrowser().getName());
		System.out.println(userAgent.getBrowser().getGroup().getName());
		
		final UserAgent mobileFireFox = UserAgent.parseUserAgentString("Mozilla/5.0 (Android; Mobile; rv:26.0) Gecko/26.0 Firefox/26.0");
		System.out.println("mobile");
		System.out.println(mobileFireFox.getBrowser().getName());
		System.out.println(mobileFireFox.getBrowser().getGroup().getName());
		System.out.println(mobileFireFox.getOperatingSystem().getDeviceType().getName());
		System.out.println(mobileFireFox.getOperatingSystem().getManufacturer().getName());
		
		final UserAgent iphone = UserAgent.parseUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 6_1_4 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10B350 Safari/8536.25");
		System.out.println("iphone");
		System.out.println("os name: " + iphone.getOperatingSystem().name());
		System.out.println("os group: " + iphone.getOperatingSystem().getGroup().name());
		System.out.println("os group devicetype: " + iphone.getOperatingSystem().getGroup().getDeviceType().name());
		System.out.println("os group group: " + iphone.getOperatingSystem().getGroup().getGroup().name());
		System.out.println("os devicetype: " + iphone.getOperatingSystem().getDeviceType().name());
		System.out.println("os manufactor: " + iphone.getOperatingSystem().getManufacturer().getName());
		
		
	}
}
