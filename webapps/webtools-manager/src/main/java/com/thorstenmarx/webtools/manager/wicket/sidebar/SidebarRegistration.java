package com.thorstenmarx.webtools.manager.wicket.sidebar;

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

import com.thorstenmarx.webtools.api.ui.sidebar.SidebarBuilding;
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.api.ui.sidebar.Material;
import com.thorstenmarx.webtools.initializer.MultiModuleManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.string.StringValue;

/**
 * For icons the material icons are used: https://material.io/icons/
 *
 * @author marx
 */
public class SidebarRegistration implements SidebarBuilding {
	
	Map<String, Section> sections;
	
	final SidebarPanel sidebar;
	final MultiModuleManager moduleManager;
	final int DEFAULT_ORDER = 5;
	
	public SidebarRegistration (final SidebarPanel sidebar, final MultiModuleManager moduleManager) {
		this.sidebar = sidebar;
		this.moduleManager = moduleManager;
		
		sections = new HashMap<>();
	}
	
	@Override
	public void addSection (final String id, final String title, final int order) {
		sections.put(id, Section.create(id, title, order));
	}
	@Override
	public void addSection (final String id, final String title) {
		sections.put(id, Section.create(id, title, DEFAULT_ORDER));
	}
	
	@Override
	public void addMenu (final String section, final String id, final String title, final Material.Icons icon) {
		if (sectionExists(section)) {
			sections.get(section).menu.put(id, Menu.create(id, title, icon));
		}
	}

	private boolean sectionExists(final String section) {
		return sections.containsKey(section);
	}
	
	public void addMenuItem (final String section, final Class<? extends WebPage> page, final String id, final String title, final Material.Icons icon) {
		if (sectionExists(section)) {
			sections.get(section).items.add(MenuItem.create(page, id, title, icon));
		}
	}
	public void addMenuItem (final String section, final String menu, final Class<? extends WebPage> page, final String id, final String title, final Material.Icons icon) {
		if (menuExists(section, menu)) {
			sections.get(section).menu.get(menu).items.add(MenuItem.create(page, id, title, icon));
		}
	}
	public void addMenuItem (final String section, final String menu, final Class<? extends WebPage> page, final String id, final String title) {
		if (menuExists(section, menu)) {
			sections.get(section).menu.get(menu).items.add(MenuItem.create(page, id, title, Material.Icons.NONE));
		}
	}
	@Override
	public void addMenuItem (final String section, final Panel panel, final String id, final String title, final Material.Icons icon) {
		if (sectionExists(section)) {
			sections.get(section).items.add(MenuItem.create(panel, id, title, icon));
		}
	}
	@Override
	public void addMenuItem (final String section, final String menu, final Panel panel, final String id, final String title, final Material.Icons icon) {
		if (menuExists(section, menu)) {
			sections.get(section).menu.get(menu).items.add(MenuItem.create(panel, id, title, icon));
		}
	}
	@Override
	public void addMenuItem (final String section, final String menu, final Panel panel, final String id, final String title) {
		if (menuExists(section, menu)) {
			sections.get(section).menu.get(menu).items.add(MenuItem.create(panel, id, title, Material.Icons.NONE));
		}
	}

	private boolean menuExists(final String section, final String menu) {
		return sectionExists(section) && sections.get(section).menu.containsKey(menu);
	}
	
	public List<SidebarSection> build (final WebPage currentPage) {
		
		List<SidebarSection> sectionList = new ArrayList<>();
		
		sections.values().stream().sorted((Section t, Section t1) -> Integer.compare(t.order, t1.order)).forEach(section -> {
			SidebarSection sidebarSection = new SidebarSection(sidebar, section.id, section.title, section.order);
			
			// add menu items
			section.items.stream().map(item -> new SidebarMenuItem(item.page, item.id, item.title, item.icon.getValue(), isActiveMenuItem(item, currentPage), sidebar)).forEach(sidebarSection::add);
			// add sub menu
			section.menu.values().stream().forEach(menu -> {
				SidebarMenu sidebarMenu = new SidebarMenu(menu.id, menu.title, menu.icon.getValue(), sidebar);
				menu.items.stream().filter(it -> it.page != null).map(menuItem -> new SidebarMenuItem(menuItem.page, menuItem.id, menuItem.title, isActiveMenuItem(menuItem, currentPage), sidebar)).forEach(sidebarMenu::add);
				menu.items.stream().filter(it -> it.panel != null).map(menuItem -> new SidebarMenuItem(menuItem.panel, menuItem.id, menuItem.title, isActiveMenuItem(menuItem, currentPage), sidebar)).forEach(sidebarMenu::add);
				sidebarSection.add(sidebarMenu);
			});
			
			sectionList.add(sidebarSection);
		});
		
		return sectionList;
	}

	private static boolean isActiveMenuItem(final MenuItem menuItem, final WebPage currentPage) {
		final StringValue currentMenuItem = currentPage.getPageParameters().get("sidebar.menu.item");
		if (!currentMenuItem.isNull() && !currentMenuItem.isEmpty()) {
			return menuItem.id.equals(currentMenuItem.toString());
		}
	
		return currentPage.getClass().equals(menuItem.page);
	}
	
	public static class Section {
		final int order;
		final String title;
		final String id;
		final Map<String, Menu> menu;
		final List<MenuItem> items;

		static Section create (final String id, final String title, final int order) {
			return new Section(id, title, order);
		}
		
		private Section(final String id, final String title, final int order) {
			this.title = title;
			this.id = id;
			this.order = order;
			this.menu = new HashMap<>();
			this.items = new ArrayList<>();
		}
	}
	
	public static class Menu {
		final int order = 1;
		final String id;
		final String title;
		final Material.Icons icon;
		final List<MenuItem> items;


		static Menu create (final String id, final String title, final Material.Icons icon) {
			return new Menu(id, title, icon);
		}
		
		private Menu(String id, String title, Material.Icons icon) {
			this.id = id;
			this.title = title;
			this.icon = icon;
			this.items = new ArrayList<>();
		}
	}
	
	public static class MenuItem {
		final int order = 1;
		final Class<? extends WebPage> page;
		final Panel panel;
		final String id;
		final String title;
		final Material.Icons icon;

		public static MenuItem create (final Class<? extends WebPage> page, final String id, final String title, final Material.Icons icon) {
			return new MenuItem (page, id, title, icon);
		}
		
		private MenuItem(Class<? extends WebPage> page, String id, String title, Material.Icons icon) {
			this.page = page;
			this.id = id;
			this.title = title;
			this.icon = icon;
			panel = null;
		}
		public static MenuItem create (final Panel panel, final String id, final String title, final Material.Icons icon) {
			return new MenuItem (panel, id, title, icon);
		}
		
		private MenuItem(final Panel panel, String id, String title, Material.Icons icon) {
			this.panel = panel;
			this.id = id;
			this.title = title;
			this.icon = icon;
			this.page = null;
		}
	}
}
