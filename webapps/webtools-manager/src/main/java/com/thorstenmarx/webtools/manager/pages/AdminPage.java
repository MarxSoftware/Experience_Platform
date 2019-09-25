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
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

@AuthorizeInstantiation("ADMIN")
public class AdminPage extends WebPage {

	private static final JavaScriptResourceReference BOOTSTRAP_JS = new JavaScriptResourceReference(AdminPage.class,
			"libs/bootstrap-3.3.7/js/bootstrap.min.js");
	private static final CssResourceReference BOOTSTRAP_CSS = new CssResourceReference(AdminPage.class,
			"libs/bootstrap-3.3.7/css/bootstrap.min.css");
//	private static final CssResourceReference BOOTSTRAP_THEME = new CssResourceReference(AdminPage.class,
//			"libs/bootstrap-3.3.7/css/bootstrap-theme.min.css");
	private static final CssResourceReference BOOTSTRAP_THEME = new CssResourceReference(AdminPage.class,
			"libs/theme/superhero-3.3.7/bootstrap.min.css");
	private static final CssResourceReference STYLE_FONT_AWESOME = new CssResourceReference(AdminPage.class,
			"libs/font-awesome-4.6.3/css/font-awesome.min.css");

	private static final CssResourceReference CSS_STYLES = new CssResourceReference(AdminPage.class,
			"css/styles.css");

	public AdminPage() {

		add(new BookmarkablePageLink<>("logoutLink", LoginPage.class));

		List<Class> listData = new ArrayList<>();
		listData.add(HomePage.class);
		listData.add(LogoutPage.class);

		add(new ListView<Class>("links", listData) {
			@Override
			public void populateItem(final ListItem<Class> item) {
				final Class clazz = item.getModelObject();
				BookmarkablePageLink<String> link = new BookmarkablePageLink("id", clazz);
				link.add(new Label("label", clazz.getName()));
				item.add(link);
			}
		});
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		response.render(JavaScriptReferenceHeaderItem.forReference(getApplication().getJavaScriptLibrarySettings()
				.getJQueryReference()));
//		response.render(JavaScriptReferenceHeaderItem.forReference(BOOTSTRAP_JS));
//		response.render(CssReferenceHeaderItem.forReference(BOOTSTRAP_CSS));
//		response.render(CssReferenceHeaderItem.forReference(BOOTSTRAP_THEME));

		response.render(CssReferenceHeaderItem.forReference(STYLE_FONT_AWESOME));

		response.render(CssReferenceHeaderItem.forReference(CSS_STYLES));
	}
}
