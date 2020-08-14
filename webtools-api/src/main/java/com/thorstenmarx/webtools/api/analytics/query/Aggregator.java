package com.thorstenmarx.webtools.api.analytics.query;

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
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author thmarx
 * @param <R> The returntype of the callable.
 */
public abstract class Aggregator<R> implements Callable<R> {
	
	protected List<ShardDocument> documents;

	protected boolean error = false;
	protected String errorMessage = null;
	
	public void documents (final List<ShardDocument> documents) {
		this.documents = documents;
	}
	
	public void error (final boolean error) {
		this.error = error;
	}
	
	public void errorMessage (final String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
