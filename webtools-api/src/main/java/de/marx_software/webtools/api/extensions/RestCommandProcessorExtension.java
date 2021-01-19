package de.marx_software.webtools.api.extensions;

/*-
 * #%L
 * webtools-api
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
import com.thorstenmarx.modules.api.BaseExtension;
import de.marx_software.webtools.api.ModuleContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * ExtensionPoint to create a rest command processor.
 *
 * @author marx
 */
public abstract class RestCommandProcessorExtension extends BaseExtension<ModuleContext> {
	/**
	 * 
	 * 
	 * @param command The command.
	 * @param request The http request.
	 * @return A REST Resource
	 */
	public abstract JSONObject post (final String command, final HttpServletRequest request);
	public abstract JSONObject get (final String command, final HttpServletRequest request);
}
