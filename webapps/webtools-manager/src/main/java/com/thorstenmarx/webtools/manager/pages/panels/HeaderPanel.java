package com.thorstenmarx.webtools.manager.pages.panels;

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
import com.thorstenmarx.webtools.manager.utils.AppLinkBean;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.initializer.MultiModuleManager;
import com.thorstenmarx.webtools.manager.pages.BasePage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author marx
 */
public class HeaderPanel extends Panel implements GenericPageBuilder {

	private static final long serialVersionUID = 1424360512405804706L;

	@Inject
	transient private MultiModuleManager moduleManager;

	transient final BasePage page;
	
	public HeaderPanel(final String id, final BasePage page) {
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
		add(new BookmarkablePageLink("configurationLink", ConfigurationPage.class));
		add(new BookmarkablePageLink<>("dashboardLink", DashboardPage.class));
		add(new ExternalLink("helpLink", "https://thorstenmarx.com/wiki/doku.php?id=webtools:intro"));
		add(new Label("title", "webTools"));
	}

	@Override
	public WebPage getPage(Panel panel) {
		return new GenericConfigPage(panel);
	}
}
