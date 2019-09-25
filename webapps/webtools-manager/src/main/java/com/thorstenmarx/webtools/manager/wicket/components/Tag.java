package com.thorstenmarx.webtools.manager.wicket.components;

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

import com.thorstenmarx.webtools.function.SerializableBiConsumer;
import com.thorstenmarx.webtools.function.SerializableFunction;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author marx
 */
public class Tag extends Panel {
	
	final SerializableBiConsumer<Tag, AjaxRequestTarget> remover;
	final SerializableFunction<String, String> displayName;
	
	public Tag(final String id, final IModel<String> model, final SerializableBiConsumer<Tag, AjaxRequestTarget> remover, final SerializableFunction<String, String> displayName) {
		super(id, model);
		this.remover = remover;
		this.displayName = displayName;
		
		init();
	}
	
	private void init () {
		Label label = new Label("label", Model.of(displayName.apply(getDefaultModelObjectAsString())));
		add(label);
		AjaxLink<Void> action = new AjaxLink<Void>("action") {
			private static final long serialVersionUID = -1964967067512351526L;
			@Override
			public void onClick(AjaxRequestTarget target) {
				remover.accept(Tag.this, target);
			}
		};
		add(action);
	}
	
	
}
