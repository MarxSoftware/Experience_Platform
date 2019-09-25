package com.thorstenmarx.webtools.api.extensions;

/*-
 * #%L
 * webtools-api
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

import com.thorstenmarx.modules.api.BaseExtension;
import com.thorstenmarx.webtools.api.ModuleContext;
import com.thorstenmarx.webtools.api.ui.GenericPageBuilder;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;

/**
 * ExtensionPoint to create a configuration page.
 *
 * @author marx
 */
public abstract class ManagerConfigExtension extends BaseExtension<ModuleContext> {

	/**
	 * returns a configuration page. If the page contains links to other pages pageBuilder must be used.
	 * 
	 * @param pageBuilder The pagebuilder Instance to generated a webTools conform page.
	 * @return The configuration page.
	 */
	public abstract WebPage getPage (GenericPageBuilder pageBuilder);
	
	/**
	 * returns the title.
	 * @return The title of the configuration page.
	 */
	public abstract String getTitle ();
	
	public abstract Image getImage ();
	
}
