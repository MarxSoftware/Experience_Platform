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
import com.alibaba.fastjson.JSONObject;
import com.thorstenmarx.webtools.integration.Audiences;
import com.thorstenmarx.webtools.integration.Tracking;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class AOVPercentageITCase {

	private final String API_KEY = "f8f8s8v1ih8lhjotbocaccuo2f";
	private final String SITE = "5dc5ac7e-b86c-4601-89ed-81596aeb7f56";

	public final String USER_ID = UUID.randomUUID().toString();
	public final String VID_ID = UUID.randomUUID().toString();

	private final Audiences AUDIENCES = new Audiences(SITE);
	private final Tracking TRACKING = new Tracking(USER_ID, VID_ID, SITE);

	public static final MediaType JSON
			= MediaType.get("application/json; charset=utf-8");

	public String REQ_ID() {
		return UUID.randomUUID().toString();
	}

	@Test
	public void create_segment() throws IOException {
		OkHttpClient client = new OkHttpClient();

		JSONObject audience = AUDIENCES.create("Big Spender", 1, true, "src/test/resources/audiences/big_spender.json");

		RequestBody body = RequestBody.create(audience.toJSONString(), JSON);
		Request request = new Request.Builder()
				.url("http://localhost:9191/rest/audience")
				.post(body)
				.addHeader("site", SITE)
				.addHeader("apikey", API_KEY)
				.build();
		try (Response response = client.newCall(request).execute()) {
			System.out.println(response.body().string());
		}
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
	public void get_segments() throws IOException {

		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
				.addHeader("site", SITE)
				.addHeader("apikey", API_KEY)
				.url("http://localhost:9191/rest/userinformation/user?"
						+ "user=" + USER_ID
				)
				.build();
		try (Response response = client.newCall(request).execute()) {
			JSONObject userInfo = (JSONObject) JSONObject.parseObject(response.body().string());

			Assertions.assertThat(userInfo).isNotNull();
			Assertions.assertThat(userInfo.containsKey("user"));
			Assertions.assertThat(userInfo.getJSONObject("user").containsKey("actionSystem"));
			Assertions.assertThat(userInfo.getJSONObject("user").getJSONObject("actionSystem").containsKey("segments"));
			Assertions.assertThat(userInfo.getJSONObject("user").getJSONObject("actionSystem").getJSONArray("segments")).hasSize(1);
		}
	}
}
