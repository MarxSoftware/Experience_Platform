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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FilesystemFolder extends AbstractFolder {
  private File root;
  private String encoding = "UTF-8";

  private FilesystemFolder(File root, Folder parent, String path, String encoding) {
    super(parent, path);
    this.root = root;
    this.encoding = encoding;
  }

  @Override
  public String getFile(String name) {
    File file = new File(root, name);

    try {
      try (FileInputStream stream = new FileInputStream(file)) {
        return IOUtils.toString(stream, encoding);
      }
    } catch (FileNotFoundException ex) {
      return null;
    } catch (IOException ex) {
      return null;
    }
  }

  @Override
  public Folder getFolder(String name) {
    File folder = new File(root, name);
    if (!folder.exists()) {
      return null;
    }

    return new FilesystemFolder(folder, this, getPath() + name + File.separator, encoding);
  }

  public static FilesystemFolder create(File root, String encoding) {
    File absolute = root.getAbsoluteFile();
    return new FilesystemFolder(absolute, null, absolute.getPath() + File.separator, encoding);
  }
}
