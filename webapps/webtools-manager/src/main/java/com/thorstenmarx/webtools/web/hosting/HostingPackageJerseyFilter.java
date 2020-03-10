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
package com.thorstenmarx.webtools.web.hosting;

/*-
 * #%L
 * webtools-manager
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
import com.thorstenmarx.modules.api.Module;
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.hosting.extensions.HostingPackageValidatorExtension;
import com.thorstenmarx.webtools.web.filter.ApiKeyFilter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author marx
 */
@Provider
public class HostingPackageJerseyFilter implements ContainerRequestFilter {

	@Override
	public void filter(final ContainerRequestContext ctx) throws IOException {
		ModuleManager moduleManager = ContextListener.INJECTOR_PROVIDER.injector().getInstance(ModuleManager.class);

		Module hostingModule = moduleManager.module("module-hosting");
		final Optional<String> site = getSite(ctx);
		if (hostingModule != null && site.isPresent()) {
			List<HostingPackageValidatorExtension> extensions = hostingModule.extensions(HostingPackageValidatorExtension.class);

			for (final HostingPackageValidatorExtension validator : extensions) {
				if (!validator.validate(site.get(), ctx.getUriInfo().getPath())) {
					ctx.abortWith(Response.status(Response.Status.FORBIDDEN)
							.entity("Cannot access")
							.build());
				}
			}
		}

		System.out.println(ctx.getUriInfo().getPath());
	}

	private Optional<String> getSite(final ContainerRequestContext context) {
		if (context.getUriInfo().getQueryParameters().containsKey(ApiKeyFilter.PARAMETER_SITE)) {
			return Optional.of(context.getUriInfo().getQueryParameters().getFirst(ApiKeyFilter.PARAMETER_SITE));
		}
		return Optional.empty();
	}
}
