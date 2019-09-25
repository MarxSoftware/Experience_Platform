package com.thorstenmarx.webtools.manager.services.impl;

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
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.thorstenmarx.webtools.manager.model.User;
import com.thorstenmarx.webtools.manager.services.UserService;
import com.thorstenmarx.webtools.manager.utils.Helper;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author marx
 */
public class FileUserService implements UserService {
	
	private static final Logger LOGGER = LogManager.getLogger(FileUserService.class);

	public static final String FILENAME = "users.realm";
	
	String path;

	Map<String, User> userByName = new HashMap<>();

	final static Splitter userSplitter = Splitter.on(":").trimResults();
	final static Splitter groupSplitter = Splitter.on(",").trimResults();

	public FileUserService(final String path) {
		this.path = path;
		if (!path.endsWith("/")) {
			this.path += "/";
		}

		try {
			loadUsers();
		} catch (IOException ex) {
			LOGGER.error("", ex);
			throw new RuntimeException(ex);
		}
	}

	/**
	 * users are stored:
	 * <id>:<username>:<password.hash>:group,group
	 */
	private void loadUsers() throws IOException {
		File users = new File(path + FILENAME);
		if (users.exists()) {
			List<String> lines = Files.readLines(users, Charsets.UTF_8);

			for (String line : lines) {
				if (!line.startsWith("#")) {
					User user = User.fromString(line);
					userByName.put(user.username(), user);
				}
			}
		}
	}

	/**
	 * users are stored:
	 * <id>:<username>:<password.hash>:group,group
	 */
	private void saveUsers() throws IOException {
		File usersFile = new File(path + FILENAME);
		if (usersFile.exists()) {
			usersFile.createNewFile();
		}
		StringBuilder sb = new StringBuilder();
		userByName.values().stream().forEach((user) -> {
			sb.append(user.toString()).append("\r\n");
		});
		
		Files.write(sb.toString(), usersFile, Charsets.UTF_8);
	}

	@Override
	public void add(User user) {
		if (user.id() == null) {
			
			if (userByName.containsKey(user.username())) {
				throw new RuntimeException("username already exists.");
			}
			
			user.id(UUID.randomUUID().toString());
		}
		
		try {
			
			final String hashedPassword = Helper.hash(user.password());
			user.password(hashedPassword);
			userByName.put(user.username(), user);
			
			saveUsers();
		} catch (IOException | NoSuchAlgorithmException ex) {
			LOGGER.error("", ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public User get(String username) {
		return userByName.get(username);
	}
	

	@Override
	public User login(final String username, final String password) {
		try {
			final String hashedPassword = Helper.hash(password);
			if (userByName.containsKey(username)) {
				if (userByName.get(username).password().equals(hashedPassword)) {
					return userByName.get(username);
				}
			}
			return null;
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
			LOGGER.error("", ex);
		}
		return null;
	}

	@Override
	public void delete(User user) {
		userByName.remove(user.username());
		try {
			saveUsers();
		} catch (IOException ex) {
			LOGGER.error("", ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Collection<User> all() {
		return userByName.values();
	}
	
	
}
