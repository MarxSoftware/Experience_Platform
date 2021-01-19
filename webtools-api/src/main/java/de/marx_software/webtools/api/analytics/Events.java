package de.marx_software.webtools.api.analytics;

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

/**
 * 
 * All known events.
 *
 * @author marx
 */
public enum Events {

	PageView("pageview"),
	CartItemAdd("ecommerce_cart_item_add"),
	CartItemRemove("ecommerce_cart_item_remove"),
	Order("ecommerce_order"),
	;

	final String value;

	Events(final String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	public static boolean isField(final String name) {
		for (Events field : values()) {
			if (field.value.equals(name)) {
				return true;
			}
		}
		return false;
	}
}
