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

import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 *
 * @author marx
 */
public class BaseCssPage extends WebPage {

	private static final JavaScriptResourceReference BOOTSTRAP_JS = new JavaScriptResourceReference(BasePage.class,
			"libs/bootstrap-4.0.0/js/bootstrap.min.js");
	private static final CssResourceReference BOOTSTRAP_CSS = new CssResourceReference(BasePage.class,
			"libs/bootstrap-4.0.0/css/bootstrap.min.css");
	private static final JavaScriptResourceReference BOOTSTRAP_MATERIAL_JS = new JavaScriptResourceReference(BasePage.class,
			"libs/bootstrap-material-design-4.1.1/js/bootstrap-material-design.min.js");
	private static final CssResourceReference BOOTSTRAP_MATERIAL_CSS = new CssResourceReference(BasePage.class,
			"libs/bootstrap-material-design-4.1.1/css/bootstrap-material-design.min.css");
//	private static final CssResourceReference STYLE_FONT_AWESOME = new CssResourceReference(BasePage.class,
//			"libs/font-awesome-5.0.1/css/font-awesome.min.css");
	private static final JavaScriptResourceReference FONTAWESOME_ALL_JS = new JavaScriptResourceReference(BasePage.class,
			"libs/font-awesome/js/fontawesome-all.min.js");
	private static final JavaScriptResourceReference FONTAWESOME_4SHIMS_JS = new JavaScriptResourceReference(BasePage.class,
			"libs/font-awesome/js/fa-v4-shims.min.js");
	
	public static final String RESORUCES_POPPER_JS = "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.6/umd/popper.min.js";
	public static final String RESORUCES_POPPER_JS_MAP = "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.6/umd/popper.min.js.map";	


	public BaseCssPage	() {
		super();
	}
	public BaseCssPage	(final PageParameters parameters) {
		super(parameters);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		response.render(JavaScriptReferenceHeaderItem.forReference(getApplication().getJavaScriptLibrarySettings()
				.getJQueryReference()));
		response.render(JavaScriptReferenceHeaderItem.forUrl(RESORUCES_POPPER_JS));
//		response.render(JavaScriptReferenceHeaderItem.forReference(BOOTSTRAP_JS));
//		response.render(CssReferenceHeaderItem.forReference(BOOTSTRAP_CSS));
		response.render(JavaScriptReferenceHeaderItem.forReference(BOOTSTRAP_MATERIAL_JS));
		response.render(CssReferenceHeaderItem.forReference(BOOTSTRAP_MATERIAL_CSS));
		response.render(CssReferenceHeaderItem.forUrl("https://fonts.googleapis.com/icon?family=Material+Icons"));

		response.render(JavaScriptReferenceHeaderItem.forReference(FONTAWESOME_ALL_JS));
		response.render(JavaScriptReferenceHeaderItem.forReference(FONTAWESOME_4SHIMS_JS));
		
		

	}
	
}
