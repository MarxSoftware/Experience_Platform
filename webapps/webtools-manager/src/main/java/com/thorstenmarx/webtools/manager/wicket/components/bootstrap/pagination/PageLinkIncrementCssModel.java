package com.thorstenmarx.webtools.manager.wicket.components.bootstrap.pagination;

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
import java.io.Serializable;

import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.model.IModel;

/**
 * @author thmarx
 */
public class PageLinkIncrementCssModel implements IModel<String>, Serializable {

	protected final IPageable pageable;

	private final long pageNumber;

	public PageLinkIncrementCssModel(IPageable pageable, long pageNumber) {
		this.pageable = pageable;
		this.pageNumber = pageNumber;
	}

	@Override
	public String getObject() {
		return isEnabled() ? "" : "disabled";
	}

	@Override
	public void setObject(String object) {
	}

	@Override
	public void detach() {
	}

	public boolean isEnabled() {
		if (pageNumber < 0) {
			return !isFirst();
		} else {
			return !isLast();
		}
	}

	public boolean isFirst() {
		return pageable.getCurrentPage() <= 0;
	}

	public boolean isLast() {
		return pageable.getCurrentPage() >= (pageable.getPageCount() - 1);
	}
}
