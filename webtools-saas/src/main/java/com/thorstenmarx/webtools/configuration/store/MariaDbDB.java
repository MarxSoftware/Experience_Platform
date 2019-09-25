
package com.thorstenmarx.webtools.configuration.store;

/*-
 * #%L
 * webtools-configuration
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public class MariaDbDB implements DB {

	private static final Logger LOGGER = LoggerFactory.getLogger(MariaDbDB.class);

	final DataSource ds;
	public MariaDbDB(final DataSource dataSource) {
		this.ds = dataSource;
	}

	private void init() {
		try (Connection connection = ds.getConnection();
				Statement st = connection.createStatement();) {

			st.execute("CREATE TABLE IF NOT EXISTS configuration (db_namespace VARCHAR(255), db_key VARCHAR(255), db_content TEXT, PRIMARY KEY(db_namespace, db_key))");
			connection.commit();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void clear(final String namespace) {
		try (Connection connection = ds.getConnection();
				PreparedStatement st = connection.prepareStatement("DELETE FROM configuration WHERE db_namespace = ?")) {

			st.setString(1, namespace);
			st.execute();

			connection.commit();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String get(final String namespace, final String key) {
		try (Connection connection = ds.getConnection()) {

			String statement = "SELECT * FROM configuration WHERE db_namespace = ? AND db_key = ?";

			try (PreparedStatement ps = connection.prepareStatement(statement)) {
				ps.setString(1, namespace);
				ps.setString(2, key);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						return rs.getString("db_content");
					}
				}

			}
			return null;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public int count(final String namespace) {
		try (Connection connection = ds.getConnection()) {

			String statement = "SELECT count(db_key) as count FROM configuration WHERE db_namespace = ?";

			try (PreparedStatement ps = connection.prepareStatement(statement)) {
				ps.setString(1, namespace);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						return rs.getInt("count");
					}
				}

			}
			return 0;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public boolean add(final String namespace, final String key, final String content) {

		try (Connection connection = ds.getConnection()) {

			String statement = "INSERT INTO configuration (db_namespace, db_key, db_content) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE db_content=?";

			try (PreparedStatement ps = connection.prepareStatement(statement)) {

				ps.setString(1, namespace);
				ps.setString(2, key);
				ps.setString(3, content);
				ps.setString(4, content);
				ps.execute();

				connection.commit();

				return true;
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Delete an entity and all attributes;
	 *
	 * @param namespace
	 */
	@Override
	public void delete(final String namespace) {
		try (Connection connection = ds.getConnection();
				PreparedStatement st = connection.prepareStatement("DELETE FROM configuration WHERE db_namespace = ?")) {

			st.setString(1, namespace);
			st.execute();

			connection.commit();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
}
