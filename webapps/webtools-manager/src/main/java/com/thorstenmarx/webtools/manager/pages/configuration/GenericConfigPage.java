package com.thorstenmarx.webtools.manager.pages.configuration;

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
import com.thorstenmarx.webtools.manager.pages.BasePage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class GenericConfigPage extends BasePage {

	private static final long serialVersionUID = 3334189645311272329L;

	public GenericConfigPage(final Panel panel) {
		this(panel, new PageParameters());
	}
	public GenericConfigPage(final Panel panel, final PageParameters params) {
		super(params);

		add(panel);
	}

}
