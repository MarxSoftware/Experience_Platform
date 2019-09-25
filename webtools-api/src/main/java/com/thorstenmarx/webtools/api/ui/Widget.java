package com.thorstenmarx.webtools.api.ui;

/*-
 * #%L
 * webtools-api
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
import com.thorstenmarx.webtools.api.events.DashboardSelectionChangedEvent;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * @author marx
 */
public abstract class Widget extends Panel {

	private Label title;
	
	public Widget(final String id, final String title, final int size) {
		super(id);
		setOutputMarkupId(true);
				
		this.title = new Label("title", Model.of(title));
		add(this.title);
		add(new AttributeModifier("class", "col-md-" + size));	
	}
	
	/**
	 *
	 * @param title
	 */
	public void setTitle (final String title) {
		this.title.setDefaultModel(Model.of(title));
	}

	/**
	 *
	 * @param panel
	 */
	public void setContent (final Panel panel) {
		replace(panel);
	}
	
	/**
	 *
	 * @param event
	 */
	public void update (DashboardSelectionChangedEvent event) {
		// do nothing
	}
}
