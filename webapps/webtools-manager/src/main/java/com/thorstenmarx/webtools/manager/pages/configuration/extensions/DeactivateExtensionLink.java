package com.thorstenmarx.webtools.manager.pages.configuration.extensions;

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
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.manager.wicket.components.ConfirmationLink;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 * @author marx
 */
public class DeactivateExtensionLink extends ConfirmationLink<Void> {

	private static final long serialVersionUID = -3312219365937304839L;
	
	@Inject
	transient ModuleManager moduleManager;
	private final String id;
	

	public DeactivateExtensionLink(String componentId, final String id) {
		super(componentId, "Deactivate extension?");
		this.id = id;
	}

	

	@Override
	public void onClick(AjaxRequestTarget art) {
		try {
			moduleManager.deactivateModule(id);
			
			getSession().getPageManager().clear();
			getSession().clear();
			getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().clear();
			
			getSession().info(new StringResourceModel("extensionDeactivated", Model.of(id)));
			setResponsePage(ExtensionsPage.class);
		} catch (IOException ex) {
			Logger.getLogger(DeactivateExtensionLink.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
