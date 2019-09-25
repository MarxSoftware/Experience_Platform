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

import com.google.common.base.Strings;
import com.thorstenmarx.webtools.manager.pages.configuration.GenericConfigPage;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author marx
 */
public class SidebarMenuItem extends SidebarFragment {

	final Class<? extends WebPage> target;
	final Panel targetPanel;
	final String id;
	final String title;
	final String icon;
	final boolean active;
	final MarkupContainer markupProvider;

	public SidebarMenuItem(final Class<? extends WebPage> target, final String id, final String title, final boolean active, final MarkupContainer markupProvider) {
		this(target, id, title, null, active, markupProvider);
	}

	public SidebarMenuItem(final Class<? extends WebPage> target, final String id, final String title, final String icon, final boolean active, final MarkupContainer markupProvider) {
		this.target = target;
		this.id = id;
		this.title = title;
		this.icon = icon;
		this.active = active;
		this.markupProvider = markupProvider;
		targetPanel = null;
	}

	public SidebarMenuItem(final Panel panel, final String id, final String title, final boolean active, final MarkupContainer markupProvider) {
		this(panel, id, title, null, active, markupProvider);
	}

	public SidebarMenuItem(final Panel panel, final String id, final String title, final String icon, final boolean active, final MarkupContainer markupProvider) {
		this.targetPanel = panel;
		this.id = id;
		this.title = title;
		this.icon = icon;
		this.active = active;
		this.markupProvider = markupProvider;
		target = null;
	}

	@Override
	public Fragment createFragement() {
		final Fragment fragment = new Fragment("content", "sidebarMenuItem", markupProvider);

		WebMarkupContainer list = new WebMarkupContainer("list");

		Label wicketTitle = new Label("title", Model.of(title));

		Link link = new Link("target") {
			@Override
			public void onClick() {
				if (targetPanel != null) {
					PageParameters pageParameters = new PageParameters();
					pageParameters.add("sidebar.menu.item", id);
					setResponsePage(new GenericConfigPage(targetPanel, pageParameters));
				} else {
					setResponsePage(target);
				}
			}

		};

		if (!Strings.isNullOrEmpty(icon)) {
			Label wicketIcon = new Label("icon", Model.of(icon));
			link.add(wicketIcon);
		} else {
			Label wicketIcon = new Label("icon", Model.of(""));
			link.add(wicketIcon);
		}
		link.add(wicketTitle);
		if (active) {
			list.add(new AttributeAppender("class", "active"));
		}
		list.add(link);
		fragment.add(list);

		return fragment;
	}
}
