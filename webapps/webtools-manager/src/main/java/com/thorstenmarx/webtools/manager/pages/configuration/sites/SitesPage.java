package com.thorstenmarx.webtools.manager.pages.configuration.sites;

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
import com.thorstenmarx.webtools.api.model.Site;
import com.thorstenmarx.webtools.manager.pages.BasePage;
import com.thorstenmarx.webtools.manager.services.SiteService;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

public class SitesPage extends BasePage {

	private static final long serialVersionUID = -2053979078406890879L;

	@Inject
	transient private SiteService sites;
	
	public SitesPage() {
		super();
		
		initGui();
	}
	

	private void initGui() {
		addSitesmodule();
		addCreateNewSiteLink();
	}

	private void addSitesmodule() {
		ListView<Site> sitesView = new ListView<Site>("sites", createModelForSites()) {
			private static final long serialVersionUID = 9101744072914090143L;
			@Override
			protected void populateItem(final ListItem<Site> item) {
				item.add(new Label("id", new PropertyModel<>(item.getModel(), "id")));
				item.add(new Label("name", new PropertyModel<>(item.getModel(), "name")));

				Link<BasePage> editSiteLink = new Link<BasePage>("editSiteLink") {
					private static final long serialVersionUID = -4331619903296515985L;
					@Override
					public void onClick() {
						setResponsePage(new AddEditSitePage(item.getModelObject().getId()));
					}
				};

				item.add(editSiteLink);
				item.add(new RemoveSiteLink("removeSiteLink", item.getModelObject()));
			}
		};

		sitesView.setVisible(!sitesView.getList().isEmpty());
		add(sitesView);

		Label noSitesLabel = new Label("noSitesLabel", "There are no sites in the database. Maybe you can add one?");
		noSitesLabel.setVisible(!sitesView.isVisible());
		add(noSitesLabel);

	}

	private LoadableDetachableModel<List<Site>> createModelForSites() {

		return new LoadableDetachableModel<List<Site>>() {
			@Override
			protected List<Site> load() {
				return new ArrayList<>(sites.all());
			}

		};
	}

	private void addCreateNewSiteLink() {
		add(new Link<BasePage>("addSitePageLink") {

			@Override
			public void onClick() {
				setResponsePage(new AddEditSitePage());
			}
		});
	}
	
	@Override
	public String getTitle () {
		return getString("pages.sites.title");
	}

}
