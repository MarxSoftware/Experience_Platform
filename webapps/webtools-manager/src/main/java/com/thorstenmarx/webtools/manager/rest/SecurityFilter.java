package com.thorstenmarx.webtools.manager.rest;

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

import com.thorstenmarx.webtools.manager.wicket.session.MMAuthenticationSession;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class SecurityFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response,
			final FilterChain chain) throws IOException, ServletException {

		final String method = ((HttpServletRequest) request).getMethod();
		if ("options".equalsIgnoreCase(method)) {
			((HttpServletResponse) response).setStatus(HttpServletResponse.SC_OK);
			return;
		}
		
		MMAuthenticationSession wicketSession = (MMAuthenticationSession) ((HttpServletRequest)request).getSession().getAttribute("wicket:ManagerApplication:session");
		if (wicketSession == null || !wicketSession.isSignedIn()) {
			((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} else {	
			chain.doFilter(request, response);
		}
	}
}
