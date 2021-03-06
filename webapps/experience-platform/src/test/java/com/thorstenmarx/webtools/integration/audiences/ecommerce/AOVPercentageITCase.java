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
package com.thorstenmarx.webtools.integration.audiences.ecommerce;

/*-
 * #%L
 * experience-platform
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
import com.thorstenmarx.webtools.integration.Segments;
import com.thorstenmarx.webtools.integration.audiences.AbstractAudiencesTest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class AOVPercentageITCase extends AbstractAudiencesTest {

	@Test
	public void create_segment() throws IOException {
//		AUDIENCES.clear();
		
		AUDIENCES.create(Segments.BIG_SPENDER, true);
	}

	@Test(dependsOnMethods = "create_segment")
	public void track_order() throws IOException {

		for (int i = 0; i < 5; i++) {
			Map<String, String> parameters = new HashMap<>();
			parameters.put("c_order_id", "1");
			parameters.put("c_cart_id", "1");
			parameters.put("c_order_items", "1");
			parameters.put("c_order_total", "10");
			TRACKING.track(() -> USER_ID + 1, () -> VID_ID + 1, () -> REQ_ID(), "ecommerce_order", "#order", parameters);
		}

		Map<String, String> parameters = new HashMap<>();
		parameters.put("c_order_id", "1");
		parameters.put("c_cart_id", "1");
		parameters.put("c_order_items", "1");
		parameters.put("c_order_total", "10");
		TRACKING.track(() -> REQ_ID(), "ecommerce_order", "#order", parameters);
	}

	@Test(dependsOnMethods = "track_order")
	public void get_segments() throws IOException, InterruptedException {
		
		Thread.sleep(5000l);
		
		List<Integer> segments = AUDIENCES.getSegments(USER_ID);

		Assertions.assertThat(segments).containsExactly(Segments.BIG_SPENDER.getWpid());
	}
}
