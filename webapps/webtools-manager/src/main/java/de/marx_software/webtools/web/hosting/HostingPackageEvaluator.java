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
package de.marx_software.webtools.web.hosting;

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

import com.thorstenmarx.modules.api.ModuleManager;
import de.marx_software.webtools.hosting.extensions.HostingPackageValidatorExtension;
import de.marx_software.webtools.web.filter.ApiKeyFilter;
import java.util.List;

/**
 *
 * @author marx
 */
public class HostingPackageEvaluator {

	private final ModuleManager moduleManager;

	public HostingPackageEvaluator(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	public boolean is_action_allowed(final String site, HostingPackageValidatorExtension.Action action) {
		return ApiKeyFilter.HOSTING.get().isApiKeyAccess || checkHostingModuleforAccess(site, action);
	}

	private boolean checkHostingModuleforAccess(final String site, HostingPackageValidatorExtension.Action action) {
		com.thorstenmarx.modules.api.Module hostingModule = moduleManager.module("module-hosting");
		if (hostingModule != null) {
			List<HostingPackageValidatorExtension> extensions = hostingModule.extensions(HostingPackageValidatorExtension.class);

			for (final HostingPackageValidatorExtension validator : extensions) {
				if (!validator.is_action_allowed(site, action)) {
					return false;
				}
			}
		}
		return true;
	}
}
