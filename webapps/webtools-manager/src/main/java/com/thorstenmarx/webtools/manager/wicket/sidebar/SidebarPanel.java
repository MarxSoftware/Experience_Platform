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
import com.google.inject.Inject;
import com.thorstenmarx.webtools.api.ui.GenericPageBuilder;
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.manager.pages.DashboardPage;
import com.thorstenmarx.webtools.manager.pages.configuration.ConfigurationPage;
import com.thorstenmarx.webtools.manager.pages.configuration.GenericConfigPage;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.api.extensions.ui.SidebarContribution;
import com.thorstenmarx.webtools.api.ui.sidebar.Material;
import com.thorstenmarx.webtools.initializer.MultiModuleManager;
import com.thorstenmarx.webtools.manager.pages.BasePage;
import com.thorstenmarx.webtools.manager.pages.configuration.extensions.ExtensionsPage;
import com.thorstenmarx.webtools.manager.pages.configuration.sites.SitesPage;
import com.thorstenmarx.webtools.manager.pages.configuration.targetaudiences.SegmentsPage;
import com.thorstenmarx.webtools.manager.pages.configuration.user.ChangeApiKeyPage;
import com.thorstenmarx.webtools.manager.pages.configuration.user.ChangePasswordPage;
import java.io.IOException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author marx
 */
public class SidebarPanel extends Panel implements GenericPageBuilder {

	private static final long serialVersionUID = 1424360512405804706L;

	@Inject
	transient private MultiModuleManager moduleManager;

	transient final BasePage page;

	public SidebarPanel(final String id, final BasePage page) {
		super(id);
		this.page = page;

		ContextListener.INJECTOR_PROVIDER.injector().injectMembers(this);

		init();

	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		ContextListener.INJECTOR_PROVIDER.injector().injectMembers(this);
	}

	private void init() {
		SidebarRegistration registration = new SidebarRegistration(SidebarPanel.this, moduleManager);
		registration.addSection("main", getString("sidebar.section.main.title"), 1);
		registration.addMenuItem("main", DashboardPage.class, "dashboard", getString("sidebar.menu.item.dashboard.title"), Material.Icons.dashboard);
		
		registration.addMenu("main", "personalization", getString("sidebar.menu.personalization.title"), Material.Icons.group);
		registration.addMenuItem("main", "personalization", SegmentsPage.class, "audiences", getString("sidebar.audiences.title"));
		
		registration.addMenu("main", "settings", getString("sidebar.menu.settings.title"), Material.Icons.settings);
		registration.addMenuItem("main", "settings", ConfigurationPage.class, "overview", getString("sidebar.overview.title"));
		registration.addMenuItem("main", "settings", ChangeApiKeyPage.class, "apikey", getString("sidebar.apikey.title"));
		registration.addMenuItem("main", "settings", ExtensionsPage.class, "extensions", getString("sidebar.extensions.title"));
		registration.addMenuItem("main", "settings", ChangePasswordPage.class, "password", getString("sidebar.password.title"));
		registration.addMenuItem("main", "settings", SitesPage.class, "sites", getString("sidebar.sites.title"));

		// load modules
		moduleManager.extensions(SidebarContribution.class).forEach(it -> { it.contribute(registration);});
		
		add(new ListView<SidebarSection>("sections", registration.build(page)) {
			@Override
			protected void populateItem(ListItem<SidebarSection> item) {
				item.add(item.getModelObject().createFragement());
			}
		});
	}

	@Override
	public WebPage getPage(Panel panel) {
		return new GenericConfigPage(panel);
	}
}
