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
package com.thorstenmarx.webtools.integration;

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
import com.thorstenmarx.webtools.integration.audiences.AbstractAudiencesTest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class MassITCase extends AbstractAudiencesTest {

	ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	AtomicInteger counter = new AtomicInteger(0);

	@BeforeClass
	public void setup() {
	}

	@AfterClass
	public void teardown() {
		executor.shutdown();
	}

	@Test
	public void track_order() throws IOException, InterruptedException {

		final int COUNT = 100000;

		long time_before = System.currentTimeMillis();
		for (int i = 0; i < COUNT; i++) {
			final int index = i;
			executor.execute(() -> {
				try {
					Map<String, String> parameters = new HashMap<>();
					parameters.put("c_order_id", "1" + index);
					parameters.put("c_cart_id", "1" + index);
					parameters.put("c_order_items", "1");
					parameters.put("c_order_total", "10");
					TRACKING.track(() -> REQ_ID(), "ecommerce_order", "#order", parameters);
					int currentCount = counter.incrementAndGet();

					if (currentCount % 1000 == 0) {
						long time_current = System.currentTimeMillis();

						long delay = (time_current - time_before) / 1000;
						System.out.println("count: " + currentCount);
						System.out.println(delay + "ms");
						System.out.println((currentCount / delay) + " elements per second");
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			});
		}

		while (counter.get() < COUNT) {
			Thread.sleep(5000l);
		}
	}
}
