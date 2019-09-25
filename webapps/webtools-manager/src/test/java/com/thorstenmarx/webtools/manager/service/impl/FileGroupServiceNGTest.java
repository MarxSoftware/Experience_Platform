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

import com.thorstenmarx.webtools.manager.model.Group;
import com.thorstenmarx.webtools.manager.services.impl.FileGroupService;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import static org.assertj.core.api.Assertions.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class FileGroupServiceNGTest {
	
	FileGroupService groupService;
	
	private String gid;
	
	@BeforeClass
	public void beforeClass () throws IOException, NoSuchAlgorithmException {
		String path = "target/data-" + System.nanoTime();
		
		new File(path).mkdirs();
		
		groupService = new FileGroupService(path);
	}
	
	@Test
	public void testAdd() throws Exception {
		Group g = new Group();
		g.name("group 1");
		groupService.add(g);
		g = new Group();
		g.name("group 2");
		groupService.add(g);
		
		gid = g.id();
	}

	@Test(dependsOnMethods = {"testAdd"})
	public void testGet() throws Exception {
		Group g = groupService.get(gid);
		
		assertThat(g).isNotNull();
		assertThat(g.name()).isEqualTo("group 2");
	}

	@Test(dependsOnMethods = {"testAdd"})
	public void testAll() throws Exception {
		Collection<Group> groups = groupService.all();
		
		assertThat(groups.size()).isEqualTo(2);
	}
	
}
