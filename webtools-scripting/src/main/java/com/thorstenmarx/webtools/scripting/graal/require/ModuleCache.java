package com.thorstenmarx.webtools.scripting.graal.require;

/*-
 * #%L
 * webtools-scripting
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

public class ModuleCache {
  private Map<String, Module> modules = new HashMap<>();

  public Module get(String fullPath) {
    return modules.get(fullPath);
  }

  public void put(String fullPath, Module module) {
    modules.put(fullPath, module);
  }
}
