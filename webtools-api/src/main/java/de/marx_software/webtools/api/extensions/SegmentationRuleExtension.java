package de.marx_software.webtools.api.extensions;

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
import de.marx_software.webtools.api.ModuleContext;
import de.marx_software.webtools.api.actions.Conditional;
import java.util.function.Supplier;
/**
 *
 * @author marx
 */
public abstract class SegmentationRuleExtension extends BaseExtension<ModuleContext> {
	
	/**
	 * 
	 * @return conditional 
	 */
	public abstract Supplier<Conditional> getRule ();
	
	/**
	 * the key is a unique identifier to address the rule in den dsl script.
	 * e.g.
	 * rule(THE_KEY).rule_method().rule_method()
	 * @return 
	 */
	public abstract String getKey ();
}
