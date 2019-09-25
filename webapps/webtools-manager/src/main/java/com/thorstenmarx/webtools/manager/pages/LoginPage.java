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
import com.thorstenmarx.webtools.manager.pages.panels.SignInPanel;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class LoginPage extends BaseCssPage {

	private static final JavaScriptResourceReference LOGIN_JS = new JavaScriptResourceReference(BasePage.class,
			"js/login.js");
	private static final CssResourceReference LOGIN_CSS = new CssResourceReference(BasePage.class,
			"css/login.css");
	private static final CssResourceReference STYLE_CSS = new CssResourceReference(BasePage.class,
			"css/style.css");

	private static final JavaScriptResourceReference OCANVAS_JS = new JavaScriptResourceReference(BasePage.class,
			"libs/ocanvas-2.9.1.min.js");

	public LoginPage() {

		add(new SignInPanel("signInPanel", false));

	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response); //To change body of generated methods, choose Tools | Templates.

		response.render(JavaScriptReferenceHeaderItem.forReference(getApplication().getJavaScriptLibrarySettings().getJQueryReference()));
		response.render(JavaScriptReferenceHeaderItem.forReference(OCANVAS_JS));
		response.render(JavaScriptReferenceHeaderItem.forReference(LOGIN_JS));
		response.render(CssReferenceHeaderItem.forReference(LOGIN_CSS));
		response.render(CssReferenceHeaderItem.forReference(STYLE_CSS));
	}

}
