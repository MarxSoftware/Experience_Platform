package de.marx_software.webtools.api.analytics.query;

/*-
 * #%L
 * webtools-api
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
import com.alibaba.fastjson.JSONObject;
import java.util.Objects;

/**
 *
 * @author marx
 */
public class ShardDocument {
	
	public final String shard;
	
	public final JSONObject document;

	public ShardDocument(final String shard, final JSONObject document) {
		this.shard = shard;
		this.document = document;
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 29 * hash + Objects.hashCode(this.shard);
		hash = 29 * hash + Objects.hashCode(this.document);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ShardDocument other = (ShardDocument) obj;
		if (!Objects.equals(this.shard, other.shard)) {
			return false;
		}
		if (!Objects.equals(this.document, other.document)) {
			return false;
		}
		return true;
	}

	
}
