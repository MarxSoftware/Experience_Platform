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
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class SimpleITCase {

	private static final String API_KEY = "f8f8s8v1ih8lhjotbocaccuo2f";
	private static final String SITE = "5dc5ac7e-b86c-4601-89ed-81596aeb7f56";

	protected String loadContent(final String file) throws IOException {
		return new String(Files.readAllBytes(Paths.get(file)));
	}

	public static final MediaType JSON
			= MediaType.get("application/json; charset=utf-8");

	public final String USER_ID = UUID.randomUUID().toString();
	public final String VID_ID = UUID.randomUUID().toString();

	public String REQ_ID() {
		return UUID.randomUUID().toString();
	}

	@Test
	public void create_segment() throws IOException {
		OkHttpClient client = new OkHttpClient();

		JSONObject audience = new JSONObject();
		audience.put("name", "Big Spender");
		audience.put("externalId", 1);
		audience.put("site", SITE);
		audience.put("content", loadContent("src/test/resources/audiences/big_spender.json"));
		audience.put("active", true);

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

		OkHttpClient client = new OkHttpClient();
		
		for (int i = 0; i < 5; i++) {
			Request request = new Request.Builder()
				.url("http://localhost:9191/tracking/pixel?"
						+ "event=ecommerce_order"
						+ "&page=#order"
						+ "&uid=" + USER_ID + i
						+ "&vid=" + VID_ID
						+ "&reqid=" + REQ_ID()
						+ "&c_order_id=1"
						+ "&c_cart_id=1"
						+ "&c_order_items=1"
						+ "&c_order_total=10"
				)
				.build();
		try (Response response = client.newCall(request).execute()) {
			System.out.println(response.body().string());
		}
		}

		Request request = new Request.Builder()
				.url("http://localhost:9191/tracking/pixel?"
						+ "event=ecommerce_order"
						+ "&page=#order"
						+ "&uid=" + USER_ID
						+ "&vid=" + VID_ID
						+ "&reqid=" + REQ_ID()
						+ "&c_order_id=1"
						+ "&c_cart_id=1"
						+ "&c_order_items=1"
						+ "&c_order_total=100"
				)
				.build();
		try (Response response = client.newCall(request).execute()) {
			System.out.println(response.body().string());
		}
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
			System.out.println(response.body().string());
		}
	}
}
