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

import com.google.common.base.Strings;
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.modules.api.Module;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.hosting.extensions.HostingReportExtension;
import com.thorstenmarx.webtools.web.filter.ApiKeyFilter;
import java.io.IOException;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

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

/**
 *
 * @author marx
 */
public class HostingCountingServletFilter implements Filter {

	private String counterName;
	private ModuleManager moduleManager;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		Filter.super.init(filterConfig); //To change body of generated methods, choose Tools | Templates.
		
		counterName = filterConfig.getInitParameter("counter_name");
		
		moduleManager = ContextListener.INJECTOR_PROVIDER.injector().getInstance(ModuleManager.class);
	}

	
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		track(req);
		chain.doFilter(req, res);
	}
	
	private void track (final ServletRequest req) {
		
		final HttpServletRequest request = (HttpServletRequest) req;
		final String site = ApiKeyFilter.getParameter(ApiKeyFilter.PARAMETER_SITE, request);
		Module hostingModule = moduleManager.module("module-hosting");
		if (hostingModule != null && !Strings.isNullOrEmpty(site)) {
			List<HostingReportExtension> extensions = hostingModule.extensions(HostingReportExtension.class);
			if (!extensions.isEmpty()) {
				extensions.get(0).incrementCounter(site, counterName, 1);
			}
		}
		
		
	}
	
	
}
