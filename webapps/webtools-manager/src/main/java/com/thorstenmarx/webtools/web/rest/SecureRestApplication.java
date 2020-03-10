package com.thorstenmarx.webtools.web.rest;

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
import com.thorstenmarx.webtools.web.hosting.HostingPackageJerseyFilter;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * The SecureRestApplication is secured by an apikey authentication.
 * 
 * @author thmarx
 */
@ApplicationPath("/")
public class SecureRestApplication extends ResourceConfig {

	
	
	public SecureRestApplication () {
		packages("com.thorstenmarx.webtools.web.rest.resources.secured");
		register(HostingPackageJerseyFilter.class);
	}
	
	
}
