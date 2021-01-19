package de.marx_software.webtools.tracking.referrer;

/*-
 * #%L
 * webtools-tracking
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
import de.marx_software.webtools.api.analytics.Fields;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thmar
 */
public class ReferrerFilter {

	private static ReferrerParser parser;

	public static ReferrerParser getInstance() {
		if (ReferrerFilter.parser == null) {
			try {
				ReferrerFilter.parser = new ReferrerParser();
			} catch (IOException ex) {
				throw new IllegalArgumentException(ex);
			}
		}
		return ReferrerFilter.parser;
	}

	public ReferrerFilter() {

	}

	public static void filter(JSONObject object) {
		if (object.getJSONObject("data").containsKey(Fields.Referrer.combine("header"))) {
			try {
				final String refererString = object.getJSONObject("data").getString(Fields.Referrer.combine("header"));

				Referrer referer = ReferrerFilter.getInstance().parse(refererString, "");
				if (referer != null) {
					object.getJSONObject("data").put(Fields.Referrer.combine("source"), referer.source);
					object.getJSONObject("data").put(Fields.Referrer.combine("medium"), referer.medium.name());
					
					if (referer.utm != null) {
						object.getJSONObject("data").put(Fields.Utm.combine("campaign"), referer.utm.campaign);
						object.getJSONObject("data").put(Fields.Utm.combine("medium"), referer.utm.medium);
						object.getJSONObject("data").put(Fields.Utm.combine("source"), referer.utm.source);
						object.getJSONObject("data").put(Fields.Utm.combine("content"), referer.utm.content);
						object.getJSONObject("data").put(Fields.Utm.combine("term"), referer.utm.term);
					}
				}
			} catch (URISyntaxException ex) {
				Logger.getLogger(ReferrerFilter.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

}
