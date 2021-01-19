/*
 * Copyright (C) 2020 WP DigitalExperience
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.marx_software.webtools.integration;

/**
 *
 * @author marx
 */
public enum Segments {
	
	BIG_SPENDER("Big Spender", 1, "src/test/resources/audiences/ecom/big_spender.json"),
	NO_BIG_SPENDER("No Big Spender", 2, "src/test/resources/audiences/ecom/not_big_spender.json"),
	NO_ORDER("No Order", 3, "src/test/resources/audiences/ecom/order_none.json"),
	AT_LEAST_ONE_ORDER("At least on order", 4, "src/test/resources/audiences/ecom/order_at_least_one.json"),
	NO_COUPON("No Coupon", 5, "src/test/resources/audiences/ecom/coupon_none.json"),
	AT_LEAST_ONE_COUPON("At least one coupon", 6, "src/test/resources/audiences/ecom/coupon_at_least_one.json")
	;
	
	private String filename;
	private String name;
	private Integer wpid;

	private Segments(String name, Integer wpid, String filename) {
		this.filename = filename;
		this.name = name;
		this.wpid = wpid;
	}

	public String getFilename() {
		return filename;
	}

	public String getName() {
		return name;
	}

	public Integer getWpid() {
		return wpid;
	}
	
	
	
}
