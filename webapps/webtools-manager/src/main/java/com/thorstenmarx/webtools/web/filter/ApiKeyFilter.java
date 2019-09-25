package com.thorstenmarx.webtools.web.filter;

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
import com.google.common.base.Strings;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.Fields;
import com.thorstenmarx.webtools.api.configuration.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ApiKeyFilter implements Filter {

	private final static String PARAMETER_APIKEY = "apikey";

	@Override
	public void destroy() {

	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		
		Optional<String> apikeyOptional = ContextListener.INJECTOR_PROVIDER.injector().getInstance(Configuration.class).getString(Fields.ApiKey.value());

		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String apikeyParameterValue;
		// try getting apikey from header
		apikeyParameterValue = request.getHeader(PARAMETER_APIKEY);
		// fallback to query parameter
		if (Strings.isNullOrEmpty(apikeyParameterValue)) {
			apikeyParameterValue = request.getParameter(PARAMETER_APIKEY);
		}

		if (!Strings.isNullOrEmpty(apikeyParameterValue) && apikeyOptional.isPresent() && apikeyOptional.get().equals(apikeyParameterValue)) {
			chain.doFilter(req, res);
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
}
