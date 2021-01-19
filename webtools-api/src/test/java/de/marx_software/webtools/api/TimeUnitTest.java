package de.marx_software.webtools.api;

/*-
 * #%L
 * webtools-api
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
import static org.assertj.core.api.Assertions.*;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class TimeUnitTest {
	
	@Test
	public void hours () {
		assertThat(new TimeWindow(TimeWindow.UNIT.HOUR, 1).millis()).isEqualTo((1000 * 60 * 60));
		assertThat(new TimeWindow(TimeWindow.UNIT.HOUR, 2).millis()).isEqualTo((1000 * 60 * 60 * 2));
	}
	@Test
	public void day () {
		assertThat(new TimeWindow(TimeWindow.UNIT.DAY, 1).millis()).isEqualTo((1000 * 60 * 60 * 24));
		assertThat(new TimeWindow(TimeWindow.UNIT.DAY, 2).millis()).isEqualTo((1000 * 60 * 60 * 24 * 2));
	}
	@Test
	public void week () {
		assertThat(new TimeWindow(TimeWindow.UNIT.WEEK, 1).millis()).isEqualTo((1000 * 60 * 60 * 24 * 7));
		assertThat(new TimeWindow(TimeWindow.UNIT.WEEK, 2).millis()).isEqualTo((1000 * 60 * 60 * 24 * 7 * 2));
	}
	@Test
	public void month () {
		assertThat(new TimeWindow(TimeWindow.UNIT.MONTH, 1).millis()).isEqualTo((1000l * 60l * 60l * 24l * 30l));
		assertThat(new TimeWindow(TimeWindow.UNIT.MONTH, 2).millis()).isEqualTo((1000l * 60l * 60l * 24l * 30l * 2l));
	}
	@Test
	public void year () {
		assertThat(new TimeWindow(TimeWindow.UNIT.YEAR, 1).millis()).isEqualTo((1000l * 60l * 60l * 24l * 365l));
		assertThat(new TimeWindow(TimeWindow.UNIT.YEAR, 2).millis()).isEqualTo((1000l * 60l * 60l * 24l * 365l * 2l));
	}
}
