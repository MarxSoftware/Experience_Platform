package com.thorstenmarx.webtools.tracking.referrer;

/*-
 * #%L
 * webtools-incubator
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

import java.io.IOException;
import java.net.URISyntaxException;
import static org.assertj.core.api.Assertions.*;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class ReferrerParserNGTest {

	public ReferrerParserNGTest() {
	}

	@Test
	public void testSearchEngine() throws URISyntaxException, IOException {

		ReferrerParser parser = new ReferrerParser();

		Referrer referer = parser.parse("https://google.de/?q=adfasdf", "test.de");
		assertThat(referer).isNotNull();
		assertThat(referer.medium).isEqualTo(Medium.SEARCH);
		assertThat(referer.source).isEqualTo("Google");
	}

	
}
