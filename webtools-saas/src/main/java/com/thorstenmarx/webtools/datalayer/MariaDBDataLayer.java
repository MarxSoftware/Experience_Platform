package com.thorstenmarx.webtools.datalayer;

/*-
 * #%L
 * webtools-datalayer
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
import com.google.gson.Gson;
import com.thorstenmarx.webtools.api.datalayer.DataLayer;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public class MariaDBDataLayer implements DataLayer {

	private static final Logger LOGGER = LoggerFactory.getLogger(MariaDBDataLayer.class);

	private final DataSource datasource;

	private final Gson gson;

	public MariaDBDataLayer(final DataSource datasource) {
		this.datasource = datasource;
		this.gson = new Gson();
	}

	public int size() {
		try (Connection connection = datasource.getConnection()) {

			String statement = "SELECT count(db_uid) as entityCount FROM datalayer";

			try (PreparedStatement ps = connection.prepareStatement(statement);
					ResultSet rs = ps.executeQuery()) {

				if (rs.next()) {
					return rs.getInt("entityCount");
				}
			}

		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return 0;
	}

	@Override
	public <T> Optional<T> get(final String uid, final String key, Class<T> clazz) {
		try (Connection connection = datasource.getConnection()) {

			String statement = "SELECT * FROM datalayer WHERE db_uid = ? AND db_key = ?";

			try (PreparedStatement ps = connection.prepareStatement(statement)) {
				ps.setString(1, uid);
				ps.setString(2, key);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {

						try (Reader reader = rs.getClob("db_value").getCharacterStream()) {
							return Optional.ofNullable(gson.fromJson(reader, clazz));
						}
					}
				}
			}
			return Optional.empty();
		} catch (SQLException | IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public boolean exists(final String uid, final String key) {
		try (Connection connection = datasource.getConnection()) {

			String statement = "SELECT db_uid, db_key FROM datalayer WHERE db_uid = ? AND db_key = ?";

			try (PreparedStatement ps = connection.prepareStatement(statement)) {
				ps.setString(1, uid);
				ps.setString(2, key);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						return true;

					}
				}
			}
			return false;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public boolean add(final String uid, final String key, final Object value, final Class clazz) {

		remove(uid, key);
		String statement = "INSERT INTO datalayer (db_uid, db_key, db_value, db_lastmodified, db_version) VALUES(?, ?, ?, ?, 1)";
		try (Connection connection = datasource.getConnection();
				PreparedStatement ps = connection.prepareStatement(statement)) {

			final String content = gson.toJson(value);

			ps.setString(1, uid);
			ps.setString(2, key);
			ps.setString(3, content);
			ps.setDate(4, new Date(System.currentTimeMillis()));

			ps.execute();

			connection.commit();

			return true;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Delete an entity and all attributes;
	 *
	 * @param uid
	 * @param key
	 */
	public void remove(final String uid, final String key) {
		try (Connection connection = datasource.getConnection();
				PreparedStatement st = connection.prepareStatement("DELETE FROM datalayer WHERE db_uid = ? AND db_key = ?")) {

			st.setString(1, uid);
			st.setString(2, key);
			st.execute();

			connection.commit();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);

		}
	}

	
	protected void clear() {
		try (Connection connection = datasource.getConnection();
				Statement st = connection.createStatement()) {
			st.execute("DELETE FROM datalayer");

			connection.commit();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);

		}
	}
}
