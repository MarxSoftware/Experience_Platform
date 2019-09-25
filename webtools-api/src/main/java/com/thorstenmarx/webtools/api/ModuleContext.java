package com.thorstenmarx.webtools.api;

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

import com.thorstenmarx.modules.api.Context;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.actions.SegmentService;
import com.thorstenmarx.webtools.api.configuration.Registry;
import com.thorstenmarx.webtools.api.entities.Entities;
import net.engio.mbassy.bus.MBassador;

/**
 *
 * @author thmarx
 */
public class ModuleContext extends Context {
	
	protected final AnalyticsDB analyticsDB;
	protected final SegmentService segmentService;
	protected final MBassador messageBus;
	protected final Entities entities;
	protected final Registry registry;
	
	public ModuleContext (final AnalyticsDB analyticsDB, final SegmentService segmentService, final MBassador messageBus, final Entities entities, final Registry registry) {
		this.analyticsDB = analyticsDB;
		this.segmentService = segmentService;
		this.messageBus = messageBus;
		this.entities = entities;
		this.registry = registry;
	}

	public AnalyticsDB getAnalyticsDB() {
		return analyticsDB;
	}

	public SegmentService getSegmentService() {
		return segmentService;
	}

	public MBassador getMessageBus() {
		return messageBus;
	}

	public Entities getEntities() {
		return entities;
	}

	public Registry getRegistry() {
		return registry;
	}

	

}
