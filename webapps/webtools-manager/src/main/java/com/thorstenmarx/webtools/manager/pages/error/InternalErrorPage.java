package com.thorstenmarx.webtools.manager.pages.error;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InternalErrorPage extends BasePage {

    private static final long serialVersionUID = -1097997000715535529L;

    private static final Logger LOGGER = LogManager.getLogger(InternalErrorPage.class);

    /**
     * Dashboard entry page.
     */
    public InternalErrorPage() {
        super();
    }

	@Override
	public String getTitle() {
		return getString("pages.error.internal.title");
	}

}
