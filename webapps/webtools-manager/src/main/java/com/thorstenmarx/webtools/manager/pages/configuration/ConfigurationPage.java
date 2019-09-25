package com.thorstenmarx.webtools.manager.pages.configuration;

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
import com.google.inject.Inject;
import com.thorstenmarx.webtools.api.extensions.ManagerConfigExtension;
import com.thorstenmarx.webtools.api.ui.GenericPageBuilder;
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.initializer.MultiModuleManager;
import com.thorstenmarx.webtools.manager.ManagerApplication;
import com.thorstenmarx.webtools.manager.pages.configuration.targetaudiences.SegmentsPage;

import com.thorstenmarx.webtools.manager.pages.configuration.extensions.ExtensionsPage;
import com.thorstenmarx.webtools.manager.pages.configuration.sites.SitesPage;
import com.thorstenmarx.webtools.manager.pages.configuration.user.ChangeApiKeyPage;
import com.thorstenmarx.webtools.manager.pages.configuration.user.ChangePasswordPage;
import com.thorstenmarx.webtools.manager.utils.AppLinkBean;
import com.thorstenmarx.webtools.manager.utils.LinkBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.SharedResourceReference;

public class ConfigurationPage extends BasePage implements GenericPageBuilder {

	private static final long serialVersionUID = -1097997000715535529L;

	@Inject
	transient MultiModuleManager moduleManager;

	public ConfigurationPage() {
		super();
		getSession().getPageManager().clear();
		getSession().clear();

		init();

	}

	private void init() {

		IModel<List<LinkBean>> linkModel = new LoadableDetachableModel<List<LinkBean>>() {
			private static final long serialVersionUID = 5275935387613157437L;

			@Override
			protected List<LinkBean> load() {

				List<ManagerConfigExtension> extensions = moduleManager.extensions(ManagerConfigExtension.class);

				List<LinkBean> links = new ArrayList<>();
				links.add(new LinkBean(SitesPage.class, getString("menu.sites.title"), "images/manager_config_sites.png"));
				links.add(new LinkBean(ExtensionsPage.class, getString("menu.extensions.title"), "images/manager_config_extensions.png"));
				links.add(new LinkBean(ChangePasswordPage.class, getString("menu.change_password.title"), "images/manager_config_password.png"));
				links.add(new LinkBean(ChangeApiKeyPage.class, getString("menu.apikey.title"), "images/manager_config_apikey.png"));
				extensions.forEach((ext) -> {
					AppLinkBean appLink = new AppLinkBean(ext, ext.getTitle());
					links.add(appLink);
				});

				Collections.sort(links, (LinkBean link1, LinkBean link2) -> link1.getLabel().compareTo(link2.getLabel()));

				return links;
			}
		};

		final GenericPageBuilder pageBuilder = this;

		ListView<LinkBean> repeating = new ListView<LinkBean>("configLinks", linkModel) {
			private static final long serialVersionUID = 4949588177564901031L;

			@Override
			protected void populateItem(ListItem<LinkBean> item) {
				LinkBean link = item.getModelObject();
				if (link instanceof AppLinkBean) {
					Link pageLink = new Link("link") {
						private static final long serialVersionUID = -4331619903296515985L;

						@Override
						public void onClick() {
							setResponsePage(((AppLinkBean<ManagerConfigExtension>) item.getModelObject()).getExtension().getPage(pageBuilder));
						}

					};
					pageLink.add(new Label("label", link.getLabel()));
					pageLink.add(((AppLinkBean<ManagerConfigExtension>) item.getModelObject()).getExtension().getImage());
					pageLink.add(new AttributeAppender("onclick", new Model("if (event.stopPropagation) { " + "event.stopPropagation();" + "} else { " + "event.cancelBubble = true;" + "}"), ";"));
					item.add(pageLink);
				} else if (link.getLink() != null) {
					item.add(new BookmarkablePageLink("link", link.getLink())
							.add(new Label("label", link.getLabel()))
							.add(new Image("image", new SharedResourceReference(ManagerApplication.class, link.getImage()))
									.add(new AttributeModifier("title", Model.of(link.getLabel())))
							));
				}
			}

		};
		add(repeating);
	}

	@Override
	public WebPage getPage(Panel panel) {
		return new GenericConfigPage(panel);
	}
	
	public String getTitle () {
		return getString("pages.configuration.title");
	}

}
