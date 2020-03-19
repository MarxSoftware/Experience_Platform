package com.thorstenmarx.webtools.manager.pages;

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
import com.thorstenmarx.webtools.api.events.DashboardSelectionChangedEvent;
import com.thorstenmarx.webtools.api.message.Message;
import com.thorstenmarx.webtools.api.message.MessageStream;
import com.thorstenmarx.webtools.manager.services.SiteService;
import com.thorstenmarx.webtools.manager.wicket.MessagePanel;
import com.thorstenmarx.webtools.manager.wicket.components.sitefilter.SiteFilter;
import com.thorstenmarx.webtools.manager.wicket.dashboard.Dashboard;
import com.thorstenmarx.webtools.manager.wicket.dashboard.widgets.DefaultWidget;
import java.util.List;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class DashboardPage extends BasePage implements Consumer<SiteFilter.SelectionChangedEvent> {

	private static final long serialVersionUID = -1097997000715535529L;

	private static final Logger LOGGER = LogManager.getLogger(DashboardPage.class);

	private static final JavaScriptResourceReference D3_JS = new JavaScriptResourceReference(Dashboard.class,
			"js/d3.min.js");
	private static final JavaScriptResourceReference DIMPLE_JS = new JavaScriptResourceReference(Dashboard.class,
			"js/dimple.v2.3.0.min.js");

	private final Dashboard dashboard;
	private final SiteFilter siteFilter;

	@Inject
	private transient SiteService siteService;
	@Inject
	private transient MessageStream messageStream;

	/**
	 * Dashboard entry page.
	 */
	public DashboardPage() {
		super();

		this.dashboard = new Dashboard("dashboard");
//		dashboard.addWidget(new OverviewWidget("topChart", "Requests", 12));
		dashboard.addWidget(new DefaultWidget("dw1", "Default Widget", 4));

		add(dashboard);

		this.siteFilter = new SiteFilter("siteFilter", this);
		add(siteFilter);

		init();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		response.render(JavaScriptReferenceHeaderItem.forReference(D3_JS));
	}

	private void init() {

		initMessages();

	}

	private void initMessages() {
		IModel<List<Message>> messagesModel = new LoadableDetachableModel<List<Message>>() {
			@Override
			protected List<Message> load() {
				return messageStream.list(MessageStream.Destination.DASHBOARD);
			}
		};
		ListView<Message> repeating = new ListView<Message>("messages", messagesModel) {
			private static final long serialVersionUID = 4949588177564901031L;

			@Override
			protected void populateItem(ListItem<Message> item) {
				MessagePanel messagePanel = new MessagePanel("message", item.getModelObject());
				item.add(messagePanel);
			}

		};
		add(repeating);
	}

	@Override
	public String getTitle() {
		return getString("pages.dashboard.title");
	}

	@Override
	public void accept(SiteFilter.SelectionChangedEvent t) {
		DashboardSelectionChangedEvent event = new DashboardSelectionChangedEvent(t.target());
		event.site(t.site());
		dashboard.update(event);
	}

}
