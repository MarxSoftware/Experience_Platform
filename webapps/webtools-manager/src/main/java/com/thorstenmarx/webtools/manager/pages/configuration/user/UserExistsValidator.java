package com.thorstenmarx.webtools.manager.pages.configuration.user;

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
import com.thorstenmarx.webtools.manager.model.User;
import com.thorstenmarx.webtools.manager.services.UserService;
import com.thorstenmarx.webtools.manager.utils.Helper;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

/**
 *
 * @author marx
 */
public class UserExistsValidator implements IValidator<String> {

	private static final Logger LOGGER = LogManager.getLogger(UserExistsValidator.class);

	private static final long serialVersionUID = -9159660209499379760L;

	UserService userService;

	final private String username;

	public static UserExistsValidator of(final String username, final UserService userSerivce) {
		return new UserExistsValidator(username, userSerivce);
	}

	private UserExistsValidator(final String username, final UserService userSerivce) {
		super();
		this.username = username;
		this.userService = userSerivce;
	}

	@Override
	public void validate(IValidatable<String> validatable) {

		String password = validatable.getValue();
		User user = userService.login(username, password);
		if (user == null) {
			ValidationError error = new ValidationError(this);
			validatable.error(error);
		}

	}
}
