package com.thorstenmarx.webtools.base.example;

/*-
 * #%L
 * plugin-example
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

import com.thorstenmarx.modules.api.annotation.Extension;
import com.thorstenmarx.webtools.api.extensions.FileExtension;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
@Extension(FileExtension.class)
public class MyFileExtension extends FileExtension {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyFileExtension.class);
	
	@Override
	public File getFile(String file) {
		try {
			if (file.equals("myjsfile.js") || file.equals("js/myjsfile.js")) {
				final String content = readContent("myjsfile.js");
				return new File(file, content.getBytes(), "application/javascript");
			} else if (file.equals("mycssfile.css") || file.equals("css/mycssfile.css")) {
				final String content = readContent("mycssfile.css");
				return new File(file, content.getBytes(), "text/css");
			}

		} catch (IOException ioe) {
			LOGGER.error("", ioe);
		}
		return null;
	}

	@Override
	public void init() {
	}

	private String readContent(String filename) throws IOException {
		try (InputStream inputStream = getClass().getResourceAsStream(filename)) {

			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			return result.toString("UTF-8");
		}
	}
}
