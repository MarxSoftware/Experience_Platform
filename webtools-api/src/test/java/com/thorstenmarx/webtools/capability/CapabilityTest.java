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
package com.thorstenmarx.webtools.capability;

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

import com.thorstenmarx.webtools.capabilitiy.Capability;
import com.thorstenmarx.webtools.capabilitiy.CapabilityAware;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class CapabilityTest {

	@Test
	public void simple_test () {
		Person p = new Person();
		Assertions.assertThat(p.hasCapability(Name.class)).isFalse();
		Name name = new Name();
		name.name = "a persons name";
		p.addCapability(name, Name.class);
		Assertions.assertThat(p.hasCapability(Name.class)).isTrue();
		
		name = p.getCapability(Name.class);
		Assertions.assertThat(name.name).isEqualTo("a persons name");
	}

	
	public static class Person extends CapabilityAware {
		
	}
	
	public static class Name implements Capability {
		public String name;
	}
	
}
