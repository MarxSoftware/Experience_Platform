package com.thorstenmarx.webtools.web.rest.resources.secured;

/*-
 * #%L
 * webtools-manager
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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thorstenmarx.webtools.api.extensions.RestUserInformationExtension;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.api.datalayer.DataLayer;
import com.thorstenmarx.webtools.api.datalayer.SegmentData;
import com.thorstenmarx.webtools.initializer.MultiModuleManager;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("userinformation")
public class UserInformationResource {

	private static final Logger LOGGER = LogManager.getLogger(UserInformationResource.class);

	final MultiModuleManager moduleManager;
	final DataLayer datalayer;

	public UserInformationResource() {
		this.moduleManager = ContextListener.INJECTOR_PROVIDER.injector().getInstance(MultiModuleManager.class);
		this.datalayer = ContextListener.INJECTOR_PROVIDER.injector().getInstance(DataLayer.class);
	}

	@GET
	@Path("/user")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public String segments(final @QueryParam("user") String user) {

		JSONObject result = new JSONObject();
		try {
			JSONObject userObj = userInformation(user);

			result.put("user", userObj);
			result.put("status", "ok");
		} catch (Exception e) {
			LOGGER.error("", e);
			result.put("status", "fail");
		}

		return result.toJSONString();
	}

	private JSONObject userInformation(final String userid) {
		JSONObject userObj = new JSONObject();

//		JSONArray segments = new JSONArray();
//		Optional<List<SegmentData>> segmentListOptional = datalayer.list(userid, SegmentData.KEY, SegmentData.class);
//		if (segmentListOptional.isPresent()) {
//			segmentListOptional.get().forEach(sd -> {
//				segments.addAll(sd.segments);
//			});
//		}
//		userObj.put("segments", segments);

		List<RestUserInformationExtension> extensions = moduleManager.extensions(RestUserInformationExtension.class);
		extensions.forEach((ruie) -> {
			if (ruie.hasUserInformation(userid)) {
				userObj.put(ruie.getName(), ruie.getUserInformation(userid));
			}
		});

		return userObj;
	}
}
