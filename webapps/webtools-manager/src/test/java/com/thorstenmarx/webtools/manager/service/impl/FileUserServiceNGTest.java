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
package com.thorstenmarx.webtools.manager.service.impl;

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
import com.thorstenmarx.webtools.manager.model.User;
import com.thorstenmarx.webtools.manager.services.impl.FileUserService;
import com.thorstenmarx.webtools.manager.utils.Helper;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import static org.assertj.core.api.Assertions.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class FileUserServiceNGTest {
	
	FileUserService userService;
	final String username = "paul";
	final String password = "paule";
	String hashed;
	
	@BeforeClass
	public void beforeClass () throws IOException, NoSuchAlgorithmException {
		String path = "target/data-" + System.nanoTime();
		
		new File(path).mkdirs();
		
		userService = new FileUserService(path);
		hashed = Helper.hash(password);
	}
	
	@Test
	public void testAdd() throws Exception {
		User u = new User();
		u.username(username);
		u.password(password);
		u.group("group1");
		u.group("group2");
		
		userService.add(u);
		
		u = new User();
		u.username("random");
		u.password("random");
		
		userService.add(u);
	}

	@Test(dependsOnMethods = {"testAdd"})
	public void testGet() throws Exception {
		User u = userService.get(username);
		assertThat(u).isNotNull();
		assertThat(u.username()).isEqualTo(username);
	}

	@Test(dependsOnMethods = {"testAdd"})
	public void testLogin() throws Exception {
		User u = userService.login(username, password);
		assertThat(u).isNotNull();
		assertThat(u.username()).isEqualTo(username);
	}
	
	@Test(dependsOnMethods = {"testAdd"}, expectedExceptions = {RuntimeException.class})
	public void testUserName_used() throws Exception {
		User u = new User();
		u.username(username);
		u.password(password);
		u.group("group1");
		u.group("group2");
		
		userService.add(u);
	}
	@Test(dependsOnMethods = {"testUserName_used"})
	public void init() throws Exception {
		User u = new User();
		u.username("admin");
		u.password("Nj5LqQmpeI3S");
		u.group("admin");
		
		userService.add(u);
	}
}
