/**
 * WebTools-Platform
 * Copyright (C) 2016-2018  ThorstenMarx (kontakt@thorstenmarx.com)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.manager.model;

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
import com.thorstenmarx.webtools.manager.utils.Helper;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.*;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class UserNGTest {
	@Test
	public void testFromString() {
		String userString = "id:paul:hashedpwd:group1,group2";
		User u = User.fromString(userString);
		
		assertThat(u).isNotNull();
		assertThat(u.id()).isEqualTo("id");
		assertThat(u.username()).isEqualTo("paul");
		assertThat(u.password()).isEqualTo("hashedpwd");
		assertThat(u.groups()).containsAll(Arrays.asList("group1", "group2"));
	}
	@Test
	public void testFromStringNoGroups() {
		String userString = "id:paul:hashedpwd:";
		User u = User.fromString(userString);
		
		assertThat(u).isNotNull();
		assertThat(u.id()).isEqualTo("id");
		assertThat(u.username()).isEqualTo("paul");
		assertThat(u.password()).isEqualTo("hashedpwd");
	}

	@Test
	public void testToString() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String userString = "id:paul:" + Helper.hash("password") + ":group1,group2";
		User u = new User();
		u.username("paul");
		u.id("id");
		u.password(Helper.hash("password"));
		
		u.group("group1");
		u.group("group2");
		
		assertThat(u.toString()).isEqualTo(userString);
	}

	
}
