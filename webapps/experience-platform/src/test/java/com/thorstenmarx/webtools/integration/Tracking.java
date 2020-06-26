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

import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * @author marx
 */
public class Tracking {

	private final String userid;
	private final String site;
	private final String visit;

	OkHttpClient client = new OkHttpClient();

	public Tracking(String userid, String visit, String site) {
		this.userid = userid;
		this.visit = visit;
		this.site = site;
	}

	public void track(Supplier<String> reqidSupplier, String event, String page, Map<String, String> queryParameters) throws IOException {
		HttpUrl.Builder httpBuilder = HttpUrl.parse("http://localhost:9191/tracking/pixel").newBuilder();
		httpBuilder.addQueryParameter("event", event);
		httpBuilder.addQueryParameter("page", page);
		httpBuilder.addQueryParameter("site", site);
		httpBuilder.addQueryParameter("uid", userid);
		httpBuilder.addQueryParameter("vid", visit);
		httpBuilder.addQueryParameter("reqid", reqidSupplier.get());
		queryParameters.forEach((key, value) -> {
			httpBuilder.addQueryParameter(key, value);
		});

		Request request = new Request.Builder()
				.url(httpBuilder.build())
				.build();
		try (Response response = client.newCall(request).execute()) {
		}
	}
	
	public void track(Supplier<String> useridSupplier, Supplier<String> visitSupplier, Supplier<String> reqidSupplier, String event, String page, Map<String, String> queryParameters) throws IOException {
		HttpUrl.Builder httpBuilder = HttpUrl.parse("http://localhost:9191/tracking/pixel").newBuilder();
		httpBuilder.addQueryParameter("event", event);
		httpBuilder.addQueryParameter("page", page);
		httpBuilder.addQueryParameter("site", site);
		httpBuilder.addQueryParameter("uid", useridSupplier.get());
		httpBuilder.addQueryParameter("vid", visit);
		httpBuilder.addQueryParameter("reqid", reqidSupplier.get());
		queryParameters.forEach((key, value) -> {
			httpBuilder.addQueryParameter(key, value);
		});

		Request request = new Request.Builder()
				.url(httpBuilder.build())
				.build();
		try (Response response = client.newCall(request).execute()) {
			System.out.println(response.body().string());
		}
	}

}
