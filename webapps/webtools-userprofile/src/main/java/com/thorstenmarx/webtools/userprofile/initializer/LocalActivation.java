package com.thorstenmarx.webtools.userprofile.initializer;

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
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.thorstenmarx.webtools.userprofile.ContextListener;
import com.thorstenmarx.webtools.userprofile.initializer.guice.LocalGuiceModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Initializer for local installation
 *
 * @author thmarx
 */
public class LocalActivation implements Activation {
	
	private static final Logger LOGGER = LogManager.getLogger(LocalActivation.class);
	
	@Override
	public void initialize () {
		Injector injector = Guice.createInjector(new LocalGuiceModule());
		ContextListener.INJECTOR_PROVIDER.injector(injector);
	}
	
	@Override
	public void destroy () {
		ContextListener.STATE.shuttingDown(true);
	}
}
