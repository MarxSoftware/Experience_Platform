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
import com.thorstenmarx.webtools.manager.model.Group;
import com.thorstenmarx.webtools.manager.model.User;
import com.thorstenmarx.webtools.manager.services.GroupService;
import java.io.File;
import java.io.IOException;
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
public class FileGroupService implements GroupService {

	private static final Logger LOGGER = LogManager.getLogger(FileGroupService.class);
	
	public static final String FILENAME = "groups.realm";
	
	String path;

	Map<String, Group> groupByID = new HashMap<>();

	final static Splitter userSplitter = Splitter.on(":").trimResults();

	public FileGroupService(final String path) {
		this.path = path;
		if (!path.endsWith("/")) {
			this.path += "/";
		}

		try {
			loadGroups();
		} catch (IOException ex) {
			LOGGER.error("", ex);
			throw new RuntimeException(ex);
		}
	}

	/**
	 * users are stored:
	 * <id>:<name>
	 */
	private void loadGroups() throws IOException {
		File users = new File(path + FILENAME);
		if (users.exists()) {
			List<String> lines = Files.readLines(users, Charsets.UTF_8);

			for (String line : lines) {
				if (!line.startsWith("#")) {
					Group g = Group.fromString(line);
					add(g);
				}
			}
		}
	}

	/**
	 * groups are stored:
	 * <id>:<name>
	 */
	private void saveGroups() throws IOException {
		File groupFile = new File(path + FILENAME);
		if (groupFile.exists()) {
			groupFile.createNewFile();
		}
		StringBuilder sb = new StringBuilder();
		groupByID.values().stream().forEach((group) -> {
			sb.append(group.toString()).append("\r\n");
		});
		
		Files.write(sb.toString(), groupFile, Charsets.UTF_8);
	}

	@Override
	public void add(Group group) {
		if (group.id() == null) {
			group.id(UUID.randomUUID().toString());
		}
		groupByID.put(group.id(), group);
		try {
			saveGroups();
		} catch (IOException ex) {
			LOGGER.error("", ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Group get(String id) {
		return groupByID.get(id);
	}

	@Override
	public void delete(Group group) {
		groupByID.remove(group.id());
		try {
			saveGroups();
		} catch (IOException ex) {
			LOGGER.error("", ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Collection<Group> all() {
		return groupByID.values();
	}
	
	
}
