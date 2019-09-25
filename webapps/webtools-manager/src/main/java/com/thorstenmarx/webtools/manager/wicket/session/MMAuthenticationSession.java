package com.thorstenmarx.webtools.manager.wicket.session;

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
import org.apache.wicket.IRequestListener;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;

import com.google.inject.Inject;
import com.thorstenmarx.webtools.manager.model.User;
import com.thorstenmarx.webtools.manager.services.UserService;
import com.thorstenmarx.webtools.manager.utils.Helper;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MMAuthenticationSession extends AuthenticatedWebSession implements IRequestListener {

	private static final Logger LOGGER = LogManager.getLogger(MMAuthenticationSession.class);

	public MMAuthenticationSession(Request request) {
		super(request);
	}

	@Inject
	UserService users;
	User user = null;

	private UserService users() {
		if (users == null) {
			Injector.get().inject(this);
		}

		return users;
	}

	@Override
	public void signOut() {
		super.signOut(); //To change body of generated methods, choose Tools | Templates.
	}
	
	

	@Override
	public boolean authenticate(final String username, final String password) {
		user = users().login(username, password);
		return user != null;
	}

	@Override
	public Roles getRoles() {
		if (isSignedIn() && user != null) {
			return new Roles(user.groups().toArray(new String[user.groups().size()]));
		}
		return null;
	}

    @Override
    public void onRequest() {
    }
}
