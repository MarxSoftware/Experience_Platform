package com.thorstenmarx.webtools.manager.pages.configuration.targetaudiences;

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

import com.thorstenmarx.webtools.api.actions.model.Rule;
import com.thorstenmarx.webtools.api.actions.model.rules.KeyValueRule;
import com.thorstenmarx.webtools.api.analytics.Fields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.Component;
import org.apache.wicket.util.string.Strings;

/**
 *
 * @author marx
 */
public final class RuleLabelRenderer {
	
	private RuleLabelRenderer () {}
	
	public static String render (final Rule rule, final Component component) {
		StringBuilder sb = new StringBuilder();
		if (KeyValueRule.class.isInstance(rule)) {
			KeyValueRule kvr = (KeyValueRule)rule;
			sb.append("UserAgentRule: ").append(component.getString("keyvalue.label." + kvr.key())).append(" = ").append(valueRenderer(kvr));
		} else {
			sb.append(rule.toString());
		}
		
		return sb.toString();
	}
	
	private static String valueRenderer (final KeyValueRule rule) {
		String[] resultValues = rule.values();
		if (Fields.Location_Country_Iso.value().equals(rule.key())) {
			List values = new ArrayList<>();
			for (String value : rule.values()) {
				Locale locale = new Locale("", value);
				values.add(locale.getDisplayCountry(Locale.ENGLISH));
			}
			resultValues = (String[]) values.toArray(new String[values.size()]);
		}
		return Strings.join(" OR ", resultValues);
	}
}
