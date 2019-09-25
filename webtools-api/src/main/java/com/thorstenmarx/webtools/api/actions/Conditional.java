package com.thorstenmarx.webtools.api.actions;

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
import com.thorstenmarx.webtools.api.analytics.query.ShardDocument;

/**
 *
 * @author marx
 */
public interface Conditional {
	/**
	 * checks if a single user matchs the conditional.
	 * 
	 * @param userid
	 * @return 
	 */
	public boolean matchs (final String userid);
	/**
	 * run match.
	 */
	public void match ();
	/**
	 * checks if the conditional is valid configured.
	 * @return 
	 */
	public boolean valid ();
	/**
	 * Handel the sharddoc.
	 * 
	 * @param doc 
	 */
	public void handle (final ShardDocument doc);

	/**
	 * 
	 * @param event
	 * @return 
	 */
	public boolean affected(JSONObject event);
}
