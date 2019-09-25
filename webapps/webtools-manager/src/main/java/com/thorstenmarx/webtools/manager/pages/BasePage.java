package com.thorstenmarx.webtools.manager.pages;

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
import com.thorstenmarx.webtools.ContextListener;
//import com.thorstenmarx.webtools.manager.pages.panels.HeaderPanel;
import com.thorstenmarx.webtools.manager.wicket.sidebar.SidebarPanel;
import java.io.IOException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 *
 * @author marx
 */
@AuthorizeInstantiation("admin")
abstract public class BasePage extends BaseCssPage {

	private static final CssResourceReference CSS_STYLES = new CssResourceReference(BasePage.class,
			"css/styles.css");
	private static final JavaScriptResourceReference DASBOARD_JS = new JavaScriptResourceReference(BasePage.class,
			"js/dashboard.js");

	public static final String CONTENT_ID = "contentComponent";

//	private HeaderPanel headerPanel;

	public BasePage(final PageParameters parameters) {
		super(parameters);
		init();
	}
	
	public BasePage() {
		super();
		init();
	}
	
	private void init () {
		
		//		this.headerPanel = new HeaderPanel("headerPanel", this);
//		add(headerPanel);
		
		add(new SidebarPanel("sidebarPanel", this));
		add(new Label("title", Model.of(getTitle())));
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		ContextListener.INJECTOR_PROVIDER.injector().injectMembers(this);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		response.render(JavaScriptReferenceHeaderItem.forReference(getApplication().getJavaScriptLibrarySettings()
				.getJQueryReference()));
		response.render(JavaScriptReferenceHeaderItem.forReference(DASBOARD_JS));

		response.render(CssReferenceHeaderItem.forReference(CSS_STYLES));
	}
	
	public String getTitle () {
		return getString("pages.base.title");
	}
}
