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
import com.alibaba.fastjson.JSONArray;
import com.thorstenmarx.modules.api.ManagerConfiguration;
import com.thorstenmarx.modules.api.Module;
import com.thorstenmarx.modules.api.ModuleDescription;
import com.thorstenmarx.modules.api.ModuleManager;
import de.marx_software.webtools.ContextListener;
import de.marx_software.webtools.api.extensions.SecureRestResourceExtension;
import de.marx_software.webtools.initializer.MultiModuleManager;
import java.io.BufferedReader;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Call Extensions.
 *
 * module/<moduleid>/<module resources>
 *
 * @author thmarx
 */
@Path("manage/module")
public class ModuleManagmentResource {

	private static final Logger LOGGER = LogManager.getLogger(ModuleManagmentResource.class);

	public ModuleManagmentResource() {
	}

	@Path("activate/{moduleName}")
	public Response module(
			@PathParam("moduleName") final String moduleName) {

		String errorMessage = "extension not found";
		try {
			ModuleManager modules = ContextListener.INJECTOR_PROVIDER.injector().getInstance(ModuleManager.class);
			
			modules.activateModule(moduleName);
			
			return Response.ok().build();
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
			ModuleManager modules = ContextListener.INJECTOR_PROVIDER.injector().getInstance(ModuleManager.class);
			for (ManagerConfiguration.ModuleConfig mc : modules.configuration().getModules().values()) {
				ModuleDescription modDesc = modules.description(mc.getId());
				if (modDesc == null) {
					continue;
				}
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
			errorMessage = "error collecting module information";
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