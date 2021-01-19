package de.marx_software.webtools.web.rest.resources.secured;

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
import com.alibaba.fastjson.JSONObject;
import de.marx_software.webtools.api.extensions.RestUserInformationExtension;
import de.marx_software.webtools.ContextListener;
import de.marx_software.webtools.initializer.MultiModuleManager;
import java.util.List;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("userinformation")
public class UserInformationResource {

	private static final Logger LOGGER = LogManager.getLogger(UserInformationResource.class);

	final MultiModuleManager moduleManager;

	public UserInformationResource() {
		this.moduleManager = ContextListener.INJECTOR_PROVIDER.injector().getInstance(MultiModuleManager.class);
	}

	@GET
	@Path("/user")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public String segments(final @QueryParam("user") String user, final @QueryParam("site") String site, final @HeaderParam("site") String headerSite) {

		JSONObject result = new JSONObject();
		try {
			JSONObject userObj = userInformation(user, headerSite != null ? headerSite : site);

			result.put("user", userObj);
			result.put("status", "ok");
		} catch (Exception e) {
			LOGGER.error("", e);
			result.put("status", "error");
			result.put("message", e.getMessage());
		}

		return result.toJSONString();
	}

	private JSONObject userInformation(final String userid, final String site) {
		JSONObject userObj = new JSONObject();


		List<RestUserInformationExtension> extensions = moduleManager.extensions(RestUserInformationExtension.class);
		extensions.forEach((ruie) -> {
			if (ruie.hasUserInformation(userid, site)) {
				userObj.put(ruie.getName(), ruie.getUserInformation(userid, site));
			}
		});

		return userObj;
	}
}
