/**
 * WebTools-Platform
 * Copyright (C) 2016-2018  ThorstenMarx (kontakt@thorstenmarx.com)
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
package com.thorstenmarx.webtools.manager.pages.panels.sidebar;

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

import com.thorstenmarx.webtools.manager.wicket.sidebar.SidebarRegistration;
import com.thorstenmarx.webtools.api.ui.sidebar.Material;
import org.testng.annotations.Test;


/**
 *
 * @author marx
 */
public class SidebarRegistrationTest {
	

	@Test
	public void test_sidebar_section() {
		SidebarRegistration sidebarRegister = new SidebarRegistration(null,null);
		sidebarRegister.addSection("main", "Main");
		
		sidebarRegister.addMenu("main", "settings", "Configuration", Material.Icons.settings);
	}
	
}
