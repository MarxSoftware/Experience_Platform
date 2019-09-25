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

import com.thorstenmarx.webtools.manager.pages.BasePage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.Model;

/**
 *
 * @author marx
 */
public class SidebarMenu extends SidebarFragment {
	
	final String id; 
	final String title; 
	final String icon; 
	final MarkupContainer markupProvider;
	final List<SidebarMenuItem> menuItems;
	final String collapsId;
	
	public SidebarMenu(final String id, final String title, final String icon, final MarkupContainer markupProvider, final List<SidebarMenuItem> menuItems) {
		this.id = id;
		this.title = title;
		this.icon = icon;
		this.markupProvider = markupProvider;
		this.menuItems = menuItems;
		
		collapsId = UUID.randomUUID().toString();
	}
	public SidebarMenu(final String id, final String title, final String icon, final MarkupContainer markupProvider) {
		this.id = id;
		this.title = title;
		this.icon = icon;
		this.markupProvider = markupProvider;
		this.menuItems = new ArrayList<>();
		
		collapsId = UUID.randomUUID().toString();
	}
	
	public void add (final SidebarMenuItem item) {
		menuItems.add(item);
	}

	@Override
	public Fragment createFragement() {
		final Fragment fragment = new Fragment("content", "sidebarMenu", markupProvider);
		
		WebMarkupContainer list = new WebMarkupContainer("list");
		
		
		Label wicketIcon = new Label("icon", Model.of(icon));
		Label wicketTitle = new Label("title", Model.of(title));
		
		WebMarkupContainer link = new WebMarkupContainer("target");
		link.add(wicketIcon);
		link.add(wicketTitle);
		link.add(new AttributeModifier("href", "#" + collapsId));
		link.add(new AttributeModifier("aria-expanded", hasActiveMenuItem()));
		list.add(link);
		
		WebMarkupContainer items = new WebMarkupContainer("items");
		items.add(new AttributeModifier("id", collapsId));
		if (hasActiveMenuItem()) {
			items.add(new AttributeAppender("class", " show"));			
		} else {
			items.add(new AttributeAppender("class", " collapse"));
		}
		list.add(items);
		items.add(new ListView<SidebarMenuItem>("links", this.menuItems) {
			@Override
			protected void populateItem(ListItem<SidebarMenuItem> item) {
				item.add(item.getModelObject().createFragement());
			}
		});
		
		fragment.add(list);
		
		return fragment;
	}

	private boolean hasActiveMenuItem() {
		return menuItems.stream().anyMatch((menuItem) -> (menuItem.active));
	}
	
}
