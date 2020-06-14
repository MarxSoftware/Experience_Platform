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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import static com.thorstenmarx.webtools.integration.audiences.AbstractAudiencesTest.JSON;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.assertj.core.api.Assertions;

/**
 *
 * @author marx
 */
public class Audiences {

	final String site;
	final String apikey;

	public Audiences(final String site, final String apikey) {
		this.site = site;
		this.apikey = apikey;
	}

	public void clear() throws IOException {
		for (Segments segment : Segments.values()) {
			OkHttpClient client = new OkHttpClient();

			HttpUrl.Builder httpBuilder = HttpUrl.parse("http://localhost:9191/rest/audience").newBuilder();
			httpBuilder.addQueryParameter("wpid", String.valueOf(segment.getWpid()));
			httpBuilder.addQueryParameter("site", site);

			Request request = new Request.Builder()
					.addHeader("site", site)
					.addHeader("apikey", apikey)
					.delete()
					.url(httpBuilder.build())
					.build();
			try (Response response = client.newCall(request).execute()) {

			}
		}
	}

	public JSONObject create(final String name, final int externalId, final boolean active, final String content) throws IOException {
		JSONObject audience = new JSONObject();
		audience.put("name", name);
		audience.put("externalId", externalId);
		audience.put("site", site);
		audience.put("content", loadContent(content));
		audience.put("active", active);

		return audience;
	}

	public void create(Segments segment, final boolean active) throws IOException {

		OkHttpClient client = new OkHttpClient();

		JSONObject audience = new JSONObject();
		audience.put("name", segment.getName());
		audience.put("externalId", segment.getWpid());
		audience.put("site", site);
		audience.put("content", loadContent(segment.getFilename()));
		audience.put("active", active);

		RequestBody body = RequestBody.create(audience.toJSONString(), JSON);
		Request request = new Request.Builder()
				.url("http://localhost:9191/rest/audience")
				.post(body)
				.addHeader("site", site)
				.addHeader("apikey", apikey)
				.build();
		try (Response response = client.newCall(request).execute()) {
			System.out.println(response.body().string());
		}
	}

	public List<Integer> getSegments(final String userid) throws IOException {

		List<Integer> segments = new ArrayList<>();

		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
				.addHeader("site", site)
				.addHeader("apikey", apikey)
				.url("http://localhost:9191/rest/userinformation/user?"
						+ "user=" + userid
				)
				.build();
		try (Response response = client.newCall(request).execute()) {
			JSONObject userInfo = (JSONObject) JSONObject.parseObject(response.body().string());

			userInfo.getJSONObject("user").getJSONObject("actionSystem").getJSONArray("segments").forEach((object) -> {
				segments.add(((JSONObject) object).getInteger("wpid"));
			});
		}

		return segments;
	}

	private String loadContent(final String file) throws IOException {
		return new String(Files.readAllBytes(Paths.get(file)));
	}
}
