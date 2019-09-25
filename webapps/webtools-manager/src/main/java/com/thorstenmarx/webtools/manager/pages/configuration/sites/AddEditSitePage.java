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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

public class AddEditSitePage extends BasePage {

	@Inject
	transient private SiteService sites;

	private boolean edit = false;
	
	public AddEditSitePage() {
		super();
		this.edit = false;
		setDefaultModel(new Model<>(new Site()));
		initGui();
	}
	
	public AddEditSitePage(final String siteid) {
		super();
		this.edit = true;
		
		Site site = sites.get(siteid);
		setDefaultModel(new Model<>(site));
		initGui();
	}
	
	
	private void initGui() {

		Form<Site> addLocationForm = new Form<>("addSiteForm",
				new CompoundPropertyModel<Site>((IModel<Site>) getDefaultModel()));
		add(addLocationForm);

		Label nameLabel = new Label("nameLabel", new StringResourceModel("siteName", this, null));
		addLocationForm.add(nameLabel);
		Label keyLabel = new Label("idLabel", new StringResourceModel("siteId", this, null));
		addLocationForm.add(keyLabel);

		addLocationForm.add(createLabelFieldWithValidation("name", "siteName"));
		
		TextField<String> idField = createLabelField("id", "siteId");
		idField.setEnabled(false);
		addLocationForm.add(idField);
		

		Button submitButton = new Button("submitButton") {
			@Override
			public void onSubmit() {
				Site site = getSiteFromPageModel();

				sites.add(site);
				getSession().info(new StringResourceModel("siteUpdated", this, null).getString());

				setResponsePage(SitesPage.class);
			}
		};
		addLocationForm.add(submitButton);
	}

	private RequiredTextField<String> createLabelFieldWithValidation(String id, String property) {
		RequiredTextField<String> nameField = new RequiredTextField<>(id);
		nameField.setLabel(new StringResourceModel(property, this, null));

		return nameField;
	}
	private TextField<String> createLabelField(String id, String property) {
		TextField<String> nameField = new TextField<>(id);
		nameField.setLabel(new StringResourceModel(property, this, null));

		return nameField;
	}

	@SuppressWarnings("unchecked")
	private Site getSiteFromPageModel() {
		return (Site) getDefaultModel().getObject();
	}
}
