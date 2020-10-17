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
package com.thorstenmarx;

/*-
 * #%L
 * webtools-userprofile
 * %%
 * Copyright (C) 2020 WP DigitalExperience
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

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class DateTest {

	@Test
	public void test_date() {
		final Date date = new Date();
		System.out.println(date.getTime());

		Calendar cal = Calendar.getInstance().getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(1602249065375l);
		System.out.println(cal.getTime());

		ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(1602249065375l),
				ZoneId.of("UTC"));
		System.out.println(zdt.toString());
		
		ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
	}
}
