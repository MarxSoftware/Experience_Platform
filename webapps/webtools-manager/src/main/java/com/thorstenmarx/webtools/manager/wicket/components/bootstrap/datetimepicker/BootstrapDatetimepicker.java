package com.thorstenmarx.webtools.manager.wicket.components.bootstrap.datetimepicker;

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
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author thmarx
 */
public class BootstrapDatetimepicker extends Panel {

	private TextField<String> dateTimeValue;
	
	private String value = "";
	
	public BootstrapDatetimepicker(String id) {
		super(id);
		
		WebMarkupContainer datepicker = new WebMarkupContainer ("datepicker");
		datepicker.add(new BootstrapDatetimepickerBehavior());
		add(datepicker);
		
		dateTimeValue = new TextField<>("datetimevalue", new PropertyModel<String>(BootstrapDatetimepicker.this, "value"));
		datepicker.add(dateTimeValue);
	}

	/**
	 * @return the dateTimeValue
	 */
	public TextField<String> getDateTimeValue() {
		return dateTimeValue;
	}

	/**
	 * @param dateTimeValue the dateTimeValue to set
	 */
	public void setDateTimeValue(TextField<String> dateTimeValue) {
		this.dateTimeValue = dateTimeValue;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
}
