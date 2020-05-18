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
package com.thorstenmarx.webtools.api;

/*-
 * #%L
 * webtools-api
 * %%
 * Copyright (C) 2016 - 2020 WP DigitalExperience
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

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class VersionNGTest {
	
	 @Test
    public void newInstance_withTwoDotRelease_isParsedCorrectly() {
        final Version version = new Version("1.26.6");
        Assertions.assertThat(version.numbers).containsExactly(new int[]{1, 26, 6});
    }

    @Test
    public void newInstance_withTwoDotReleaseAndPreReleaseName_isParsedCorrectly() {
        final Version version = new Version("1.26.6-DEBUG");
        Assertions.assertThat(version.numbers).containsExactly(new int[]{1, 26, 6});
    }

    @Test
    public void compareTo_withEarlierVersion_isGreaterThan() {
        Assertions.assertThat(new Version("2.0.0").compareTo(new Version("1.0.0"))).isEqualTo(1);
    }

    @Test
    public void compareTo_withSameVersion_isEqual() {
        Assertions.assertThat(new Version("2.0.0").compareTo(new Version("2.0.0"))).isEqualTo(0);
    }

    @Test
    public void compareTo_withLaterVersion_isLessThan() {
        Assertions.assertThat(new Version("1.0.0").compareTo(new Version("2.0.0"))).isEqualTo(-1);
    }

    @Test
    public void compareTo_withMorePreciseSameVersion_isFalse() {
        Assertions.assertThat(new Version("1").compareTo(new Version("1.0.0"))).isEqualTo(0);
    }

    @Test
    public void compareTo_withMorePreciseEarlierVersion_isFalse() {
        Assertions.assertThat(new Version("2").compareTo(new Version("1.0.0"))).isEqualTo(1);
    }

    @Test
    public void compareTo_withMorePreciseLaterVersion_isLessThan() {
        Assertions.assertThat(new Version("1").compareTo(new Version("1.0.1"))).isEqualTo(-1);
    }
	
}
