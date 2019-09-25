package com.thorstenmarx.webtools.manager.wicket;

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

import com.thorstenmarx.webtools.api.message.Message;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * @author marx
 */
public class MessagePanel extends Panel {

	public MessagePanel (final String id, final Message message) {
		super(id);		
		add(new Label("content", message.getMessage()).setEscapeModelStrings(false));
		
		add(new AttributeAppender("class", Model.of(getCSSClassforType(message.getType())), " "));
	}
	
	private String getCSSClassforType (final Message.Type type) {
		switch (type) {
			case DANGER:
				return "alert alert-danger";
			case INFO:
				return "alert alert-info";
			case SUCCESS:
				return "alert alert-success";
			case WARN:
				return "alert alert-warning";
			default:
				return "alert alert-primary";
		}
	}
}
