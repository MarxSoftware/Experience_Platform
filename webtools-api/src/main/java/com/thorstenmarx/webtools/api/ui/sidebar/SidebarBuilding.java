package com.thorstenmarx.webtools.api.ui.sidebar;

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
import com.thorstenmarx.webtools.api.annotations.API;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author marx
 * @since 2.0.0
 */
@API(since = "2.0.0", status = API.Status.Stable)
public interface SidebarBuilding {

	void addMenu(final String section, final String id, final String title, final Material.Icons icon);

	void addMenuItem(final String section, final Panel panel, final String id, final String title, final Material.Icons icon);

	void addMenuItem(final String section, final String menu, final Panel panel, final String id, final String title, final Material.Icons icon);

	void addMenuItem(final String section, final String menu, final Panel panel, final String id, final String title);

	void addSection(final String id, final String title);
	
	void addSection(final String id, final String title, int order);
	
}
