package com.thorstenmarx.webtools.manager.utils;

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
import com.google.common.base.Strings;
import java.io.Serializable;



/**
 *
 * @author marx
 */
public class LinkBean {

	public static final String DEFAULT_IMAGE = "images/manager_config_button.png";
	
	private Class link;
	private String label;
	private String image = null;

	LinkBean(final String label) {
		this.label = label;
	}
	
	public LinkBean(final Class link, final String label) {
		this.link = link;
		this.label = label;
	}

	public LinkBean(final Class link, final String label, final String image) {
		this.link = link;
		this.label = label;
		this.image = image;
	}
	
	public String getLabel() {
		return this.label;
	}

	public Class getLink() {
		return this.link;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public void setLink(final Class link) {
		this.link = link;
	}

	public String getImage() {
		if (Strings.isNullOrEmpty(image)) {
			return DEFAULT_IMAGE;
		}
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
