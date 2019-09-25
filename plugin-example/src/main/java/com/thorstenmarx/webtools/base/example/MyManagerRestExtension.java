package com.thorstenmarx.webtools.base.example;

/*-
 * #%L
 * plugin-example
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

import com.thorstenmarx.modules.api.annotation.Extension;
import com.thorstenmarx.webtools.api.extensions.ManagerRestResourceExtension;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author marx
 */
@Extension(ManagerRestResourceExtension.class)
public class MyManagerRestExtension extends ManagerRestResourceExtension {

	@Override
	public void init() {
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String name() {
		return "my rest extension for the manager";
	}
}
