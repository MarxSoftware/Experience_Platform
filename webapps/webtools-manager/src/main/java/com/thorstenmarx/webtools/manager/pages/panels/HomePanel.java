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
import com.thorstenmarx.webtools.manager.pages.*;
import com.thorstenmarx.webtools.manager.wicket.components.bootstrap.datetimepicker.BootstrapDatetimepicker;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

public class HomePanel extends Panel {

	private BootstrapDatetimepicker datetimePicker = null;

	/**
	 *
	 */
	public HomePanel(final String id) {
		super(id);
		add(new Label("message", "Hello World!"));

		AjaxLink printButton = new AjaxLink("printButton") {
			@Override
			public void onClick(AjaxRequestTarget art) {
				System.out.println("1");

			}

		};
		add(printButton);

		Form form = new Form("form") {
			@Override
			protected void onSubmit() {
				System.out.println(1);
				System.out.println(datetimePicker.getDateTimeValue().getValue());
				System.out.println(datetimePicker.getValue());
			}
		};
		add(form);
		this.datetimePicker = new BootstrapDatetimepicker("panel");
		form.add(datetimePicker);

		Button submitButton = new Button("button1") {

			@Override
			public void onSubmit() {
				System.out.println(2);
				System.out.println(datetimePicker.getDateTimeValue().getValue());
				System.out.println(datetimePicker.getValue());
			}
		};
		form.add(submitButton);

		add(new BookmarkablePageLink<>("loginLink", LoginPage.class));
		add(new BookmarkablePageLink<>("adminLink", AdminPage.class));
		add(new BookmarkablePageLink<>("dashboardLink", DashboardPage.class));
	}

	
}
