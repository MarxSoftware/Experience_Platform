package com.thorstenmarx.webtools.base.example;

/*-
 * #%L
 * plugin-example
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
import com.thorstenmarx.modules.api.annotation.Extension;
import com.thorstenmarx.webtools.api.extensions.ui.SidebarContribution;
import com.thorstenmarx.webtools.api.ui.sidebar.Material;
import com.thorstenmarx.webtools.api.ui.sidebar.SidebarBuilding;
import javax.inject.Inject;

/**
 *
 * @author marx
 */
@Extension(SidebarContribution.class)
public class MySidebarContribution extends SidebarContribution {

	public static final String CONTENT_ID = "contentComponent";

	@Override
	public void init() {
	}

	@Override
	public void contribute(final SidebarBuilding sidebarBuilding) {
		sidebarBuilding.addSection("plugin-example", "My PLugin");
		sidebarBuilding.addMenu("plugin-example", "demo-menu", "MyConfig", Material.Icons.alarm);
		sidebarBuilding.addMenuItem("plugin-example", "demo-menu", new ConfigPanel(CONTENT_ID), "demo-config", "Config");
	}
	
}
