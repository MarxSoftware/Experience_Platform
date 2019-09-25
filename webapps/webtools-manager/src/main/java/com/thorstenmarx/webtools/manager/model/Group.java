package com.thorstenmarx.webtools.manager.model;

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
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 * @author marx
 */
public class Group extends IDProvider {

	private String name;
	private final SortedSet<String> users = new ConcurrentSkipListSet<>();

	final static Splitter groupSplitter = Splitter.on(":").trimResults();
	final static Joiner groupJoiner = Joiner.on(":");

	public Group() {
	}

	public String name() {
		return name;
	}

	public Group name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * String format: 
	 * <id>:<name>
	 *
	 * @param userString
	 * @return
	 */
	public static Group fromString(final String userString) {
		Group user = new Group();

		List<String> userParts = groupSplitter.splitToList(userString);

		user.id(userParts.get(0));
		user.name(userParts.get(1));
		

		return user;
	}

	@Override
	public String toString() {
		//<id>:<username>:<password.hash>:group,group
		StringBuilder sb = new StringBuilder();

		sb.append(groupJoiner.join(id(), name()));

		return sb.toString();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + Objects.hashCode(this.id);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Group other = (Group) obj;
		if (!Objects.equals(this.id, other.id)) {
			return false;
		}
		return true;
	}

}
