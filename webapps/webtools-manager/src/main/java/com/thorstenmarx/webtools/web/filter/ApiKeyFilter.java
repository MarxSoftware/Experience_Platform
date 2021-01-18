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
import com.thorstenmarx.webtools.api.model.Site;
import com.thorstenmarx.webtools.manager.services.SiteService;
import com.thorstenmarx.webtools.web.hosting.Hosting;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ApiKeyFilter implements Filter {

	public final static String PARAMETER_APIKEY = "apikey";
	public final static String PARAMETER_SITE = "site";

	public static final ThreadLocal<Hosting> HOSTING = new ThreadLocal<>();
	
	@Override
	public void destroy() {

	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		final  HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;

		final Hosting hosing = new Hosting(isValidMasterKeyAccess(request));

		ApiKeyFilter.HOSTING.set(hosing);
		try {
			if (isValidMasterKeyAccess(request)
					|| (isValidSiteKeyAccess(request) && isValidSiteRequest(request))) {
				
				chain.doFilter(req, res);
			} else {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} finally {
			ApiKeyFilter.HOSTING.remove();
		}
	}

	private boolean isValidSiteKeyAccess (final HttpServletRequest request) {
		SiteService siteService = ContextListener.INJECTOR_PROVIDER.injector().getInstance(SiteService.class);
		
		final String apikeyParameterValue = getParameter(PARAMETER_APIKEY, request);
		final String site = getParameter(PARAMETER_SITE, request);
				
		if (!Strings.isNullOrEmpty(apikeyParameterValue) 
				&& !Strings.isNullOrEmpty(site)){
			final Site theSite = siteService.get(site);
			
			return theSite != null ? apikeyParameterValue.equals(theSite.getApikey()) : false;
		}
		
		return false;
	}
	
	public static String getParameter (final String name, final HttpServletRequest request) {
		String value;
		value = request.getHeader(name);
		if (Strings.isNullOrEmpty(value)) {
			value = request.getParameter(name);
		}
		
		return value;
	}
	
	private boolean isValidSiteRequest (final HttpServletRequest request) {
		final String site_parameter = request.getParameter(PARAMETER_SITE);
		final String site_header = request.getHeader(PARAMETER_SITE);
		
		if (!Strings.isNullOrEmpty(site_header) && !Strings.isNullOrEmpty(site_parameter)){
			return site_header.equals(site_parameter);
		}
		
		return true;
	}
	
	private boolean isValidMasterKeyAccess(final HttpServletRequest request) {
		Optional<String> apikeyOptional = ContextListener.INJECTOR_PROVIDER.injector().getInstance(Configuration.class).getString(Fields.ApiKey.value());

		String apikeyParameterValue;
		// try getting apikey from header
		apikeyParameterValue = request.getHeader(PARAMETER_APIKEY);
		// fallback to query parameter
		if (Strings.isNullOrEmpty(apikeyParameterValue)) {
			apikeyParameterValue = request.getParameter(PARAMETER_APIKEY);
		}

		return !Strings.isNullOrEmpty(apikeyParameterValue) && apikeyOptional.isPresent() && apikeyOptional.get().equals(apikeyParameterValue);

	}
}
