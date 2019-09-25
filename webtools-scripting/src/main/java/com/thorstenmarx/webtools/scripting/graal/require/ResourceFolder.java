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
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class ResourceFolder extends AbstractFolder {
  private ClassLoader loader;
  private String resourcePath;
  private String encoding;

  @Override
  public String getFile(String name) {
    InputStream stream = loader.getResourceAsStream(resourcePath + "/" + name);
    if (stream == null) {
      return null;
    }

    try {
      return IOUtils.toString(stream, encoding);
    } catch (IOException ex) {
      return null;
    }
  }

  @Override
  public Folder getFolder(String name) {
    return new ResourceFolder(
        loader, resourcePath + "/" + name, this, getPath() + name + "/", encoding);
  }

  private ResourceFolder(
      ClassLoader loader, String resourcePath, Folder parent, String displayPath, String encoding) {
    super(parent, displayPath);
    this.loader = loader;
    this.resourcePath = resourcePath;
    this.encoding = encoding;
  }

  public static ResourceFolder create(ClassLoader loader, String path, String encoding) {
    return new ResourceFolder(loader, path, null, "/", encoding);
  }
}
