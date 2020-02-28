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
import com.thorstenmarx.webtools.manager.wicket.session.MMAuthenticationSession;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.WebPage;

public class LogoutPage extends WebPage {

	public LogoutPage() {
		((MMAuthenticationSession)getSession()).signOut();
		getSession().invalidate();
		getSession().invalidateNow();
		getSession().getPageManager().clear();
		getSession().clear();
//		redirectToInterceptPage(getApplication().getHomePage());	
		setResponsePage(Application.get().getHomePage());
	}
}
