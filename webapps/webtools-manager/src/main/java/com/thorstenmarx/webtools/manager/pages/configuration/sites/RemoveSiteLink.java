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
import com.thorstenmarx.webtools.manager.services.SiteService;
import com.thorstenmarx.webtools.manager.wicket.components.ConfirmationLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 * @author marx
 */
public class RemoveSiteLink extends ConfirmationLink<Void> {
	
	@Inject
	transient SiteService sites;
	private final Site site;
	

	public RemoveSiteLink(String componentId, Site site) {
		super(componentId, "Delete site?");
		this.site = site;
	}

	

	@Override
	public void onClick(AjaxRequestTarget art) {
		
		sites.remove(this.site.getId());
		getSession().info(new StringResourceModel("siteRemoved", Model.of(site.getName())));
        setResponsePage(SitesPage.class);
	}

}
