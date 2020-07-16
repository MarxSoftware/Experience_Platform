package com.thorstenmarx.webtools.manager;

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
import com.google.inject.Injector;
import com.thorstenmarx.webtools.manager.pages.DashboardPage;
import com.thorstenmarx.webtools.manager.pages.LoginPage;
import com.thorstenmarx.webtools.manager.pages.error.InternalErrorPage;
import com.thorstenmarx.webtools.manager.wicket.session.MMAuthenticationSession;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.manager.pages.HomePage;
import com.thorstenmarx.webtools.manager.pages.configuration.ConfigurationPage;
import com.thorstenmarx.webtools.manager.pages.configuration.extensions.ExtensionsPage;
import com.thorstenmarx.webtools.manager.pages.configuration.targetaudiences.SegmentsPage;
import com.thorstenmarx.webtools.manager.pages.configuration.sites.AddEditSitePage;
import com.thorstenmarx.webtools.manager.pages.configuration.sites.SitesPage;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.settings.ExceptionSettings;

/**
 *
 * @author marx
 */
public class ManagerApplication extends AuthenticatedWebApplication {

	private Injector injector;

	@Override
	public Class getHomePage() {
		return DashboardPage.class;
	}

	@Override
	protected void init() {
		super.init();
		
		getCspSettings().blocking().disabled();

		injector = ContextListener.INJECTOR_PROVIDER.injector();
		getMarkupSettings().setStripWicketTags(true);
		getComponentInstantiationListeners().add(new GuiceComponentInjector(this, injector));
		getRequestCycleListeners().add(new MMRequestCycleListener());

		getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);
		getExceptionSettings().setUnexpectedExceptionDisplay(ExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);
		
		if (get().getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT) {
			getExceptionSettings().setUnexpectedExceptionDisplay(ExceptionSettings.SHOW_EXCEPTION_PAGE);
		}

		SecurePackageResourceGuard guard = new SecurePackageResourceGuard();
		guard.addPattern("+**.otf");
		guard.addPattern("+**.eot");
		guard.addPattern("+**.svg");
		guard.addPattern("+**.ttf");
		guard.addPattern("+**.woff");
		guard.addPattern("+**.woff2");
		guard.addPattern("+**.png");
		get().getResourceSettings().setPackageResourceGuard(guard);

		mountPage("login", LoginPage.class);
		mountPage("dashboard", DashboardPage.class);
		mountPage("home", HomePage.class);
		mountPage("configuration", ConfigurationPage.class);

		mountPage("configuration/sites", SitesPage.class);
		mountPage("configuration/sites/edit", AddEditSitePage.class);

		mountPage("configuration/segments", SegmentsPage.class);

		mountPage("configuration/extensions", ExtensionsPage.class);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
		return MMAuthenticationSession.class;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return LoginPage.class;
	}

	static class MMRequestCycleListener implements IRequestCycleListener {

		@Override
		public void onBeginRequest(RequestCycle cycle) {
			if (Session.exists()) {
				org.apache.wicket.injection.Injector.get().inject(Session.get());
			}
		}
	}
}
