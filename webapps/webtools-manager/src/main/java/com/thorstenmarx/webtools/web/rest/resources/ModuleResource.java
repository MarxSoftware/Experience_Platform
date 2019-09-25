package com.thorstenmarx.webtools.web.rest.resources;

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
import com.alibaba.fastjson.JSONArray;
import com.thorstenmarx.modules.api.ManagerConfiguration;
import com.thorstenmarx.modules.api.Module;
import com.thorstenmarx.modules.api.ModuleDescription;
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.api.extensions.RestResourceExtension;
import com.thorstenmarx.webtools.initializer.MultiModuleManager;
import java.io.BufferedReader;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Call Extensions.
 *
 * module/<moduleid>/<module resources>
 *
 * @author thmarx
 */
@Path("module")
public class ModuleResource {

	private static final Logger LOGGER = LogManager.getLogger(ModuleResource.class);

	public ModuleResource() {
	}

	@Path("{moduleName}")
	public RestResourceExtension module(
			@PathParam("moduleName") final String moduleName) {

		String errorMessage = "extension not found";
		try {
			MultiModuleManager modules = ContextListener.INJECTOR_PROVIDER.injector().getInstance(MultiModuleManager.class);
			Module module = modules.module(moduleName);
			if (module != null) {
				List<RestResourceExtension> resourceExtensions = module.extensions(RestResourceExtension.class);
				if (resourceExtensions.size() > 0) {
					return resourceExtensions.get(0);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
			errorMessage = "error while executing module";
		}

		throw new WebApplicationException(errorMessage, Response.Status.NOT_FOUND);
	}

	@GET
	@Path("installed")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public String installed() {

		String errorMessage = "extension not found";
		JSONArray installedModules = new JSONArray();
		boolean error = false;
		try {
			MultiModuleManager modules = ContextListener.INJECTOR_PROVIDER.injector().getInstance(MultiModuleManager.class);
			for (ManagerConfiguration.ModuleConfig mc : modules.configuration().getModules().values()) {
				ModuleDescription modDesc = modules.description(mc.getId());
				JSONObject module = new JSONObject();
				module.put("id", mc.getId());
				module.put("active", mc.isActive());
				module.put("name", modDesc.getName());
				module.put("version", modDesc.getVersion());

				installedModules.add(module);
			}
		} catch (Exception e) {
			LOGGER.error(e);
			error = true;
			errorMessage = "error collecting module informations";
		}

		JSONObject jsonRepsonse = new JSONObject();
		jsonRepsonse.put("error", error);
		if (error) {
			jsonRepsonse.put("message", errorMessage);
		}
		jsonRepsonse.put("modules", installedModules);

		return jsonRepsonse.toJSONString();
	}

	private String getRequestBody(HttpServletRequest request) {
		StringBuilder jb = new StringBuilder();
		String line;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				jb.append(line);
			}
		} catch (Exception e) {
			/*report an error*/ }

		return jb.toString();
	}
}
