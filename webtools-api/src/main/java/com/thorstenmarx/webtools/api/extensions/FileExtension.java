package com.thorstenmarx.webtools.api.extensions;

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
import com.thorstenmarx.webtools.api.ModuleContext;
import com.thorstenmarx.webtools.api.annotations.API;

/**
 *
 * @author marx
 * @since 1.14.0
 */
@API(since = "2.1.0", status = API.Status.Stable)
public abstract class FileExtension extends BaseExtension<ModuleContext> {

	public abstract File getFile (final String file);	
	
	public static class File {
		private final String name;
		private final byte[] content;
		private final String contentType;

		public File(final String name, final byte[] content, final String contentType) {
			this.name = name;
			this.content = content;
			this.contentType = contentType;
		}

		public String getName() {
			return name;
		}

		public byte[] getContent() {
			return content;
		}

		public String getContentType() {
			return contentType;
		}
		
		
		
	}
}
