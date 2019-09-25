package com.thorstenmarx.webtools.manager.rest.endpoints;

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

import com.thorstenmarx.modules.api.Module;
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.api.extensions.ManagerRestResourceExtension;
import com.thorstenmarx.webtools.api.extensions.RestResourceExtension;
import com.thorstenmarx.webtools.initializer.MultiModuleManager;
import java.util.List;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author marx
 */
@Path("module")
public class ManagerModuleEndpoint {

	private static final Logger LOGGER = LogManager.getLogger(ManagerModuleEndpoint.class);

	@Path("{moduleName}")
	public ManagerRestResourceExtension module(
			@PathParam("moduleName") final String moduleName) {

		String errorMessage = "extension not found";
		try {
			MultiModuleManager modules = ContextListener.INJECTOR_PROVIDER.injector().getInstance(MultiModuleManager.class);
			Module module = modules.module(moduleName);
			if (module != null) {
				List<ManagerRestResourceExtension> resourceExtensions = module.extensions(ManagerRestResourceExtension.class);
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

}
