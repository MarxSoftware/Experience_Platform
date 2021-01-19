/*
 * Copyright (C) 2019 Thorsten Marx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.marx_software.webtools.capabilitiy;

/*-
 * #%L
 * webtools-api
 * %%
 * Copyright (C) 2016 - 2019 Thorsten Marx
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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author marx
 */
public abstract class CapabilityAware {
	private final Map<Class<? extends Capability>, Capability> capabilities = new HashMap<>();
	
	public boolean hasCapability (final Class<? extends Capability> capability) {
		return capabilities.containsKey(capability);
	}
	
	public void addCapability (final Capability capability, final Class<? extends Capability> capapilityClass) {
		capabilities.put(capapilityClass, capability);
	}
	public <T extends Capability> T getCapability (final Class<T> capapilityClass) {
		Capability capability = capabilities.get(capapilityClass);
		if (!capapilityClass.isInstance(capability)) {
			throw new IllegalArgumentException("capability not of type " + capapilityClass);
		}
		return capapilityClass.cast(capability);
	}
}
