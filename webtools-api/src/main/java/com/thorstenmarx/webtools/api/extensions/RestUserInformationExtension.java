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

import com.alibaba.fastjson.JSONObject;
import com.thorstenmarx.modules.api.BaseExtension;
import com.thorstenmarx.webtools.api.ModuleContext;

/**
 * ExtensionPoint to append custom userinformations to the userinformation rest
 * call.
 *
 * @author marx
 */
public abstract class RestUserInformationExtension extends BaseExtension<ModuleContext> {

    /**
     * Returns the user information.
     *
     * @param userid THe useid.
     * @return A REST Resource
     */
    public abstract JSONObject getUserInformation(final String userid, final String site);

    /**
     * Get the Key for the user information.
     *
     * @return the unique key for this information.
     */
    public abstract String getName();

    /**
     * Check if there are user information available.
     *
     * @param userid the user id.
     * @return Returns true if the extension has information about the user.
     */
    public abstract boolean hasUserInformation(final String userid);
}
