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

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;

/**
 *
 * @author marx
 */
public class SidebarSection extends SidebarFragment {

	private final int order;
	private final String id;
	private final String title;
	private final List<SidebarFragment> fragments;
	private final MarkupContainer markupProvider;
	
	public SidebarSection (final MarkupContainer markupProvider, final String id, final String title, final List<SidebarFragment> fragments, final int order) {
		this.markupProvider = markupProvider;
		this.id = id;
		this.title = title;
		this.fragments = fragments;
		this.order = order;
	}
	public SidebarSection (final MarkupContainer markupProvider, final String id, final String title, final int order) {
		this.markupProvider = markupProvider;
		this.id = id;
		this.title = title;
		this.fragments = new ArrayList<>();
		this.order = order;
	}

	public void add (final SidebarFragment fragment) {
		fragments.add(fragment);
	}
	
	public int order () {
		return order;
	}
	
	@Override
	public Fragment createFragement() {
		Fragment fragment = new Fragment("content", "sidebarSection", markupProvider);
		
		fragment.add(new Label("title", title));
		
		fragment.add(new ListView<SidebarFragment>("links", this.fragments) {
			@Override
			protected void populateItem(ListItem<SidebarFragment> item) {
				item.add(item.getModelObject().createFragement());
			}
		});
		
		return fragment;
	}
}
