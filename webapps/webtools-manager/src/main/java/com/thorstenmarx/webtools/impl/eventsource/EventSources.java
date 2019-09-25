package com.thorstenmarx.webtools.impl.eventsource;

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

import com.thorstenmarx.webtools.api.eventsource.RegisterEventSourceMessage;
import com.thorstenmarx.webtools.api.eventsource.UnregisterEventSourceMessage;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;

/**
 *
 * @author marx
 */
@Listener
public class EventSources {

	private final ConcurrentMap<String, EventSource> eventSources;

	public EventSources() {
		this.eventSources = new ConcurrentHashMap<>();
	}

	public void add(final String event, final EventSource source) {
		eventSources.put(event, source);
	}
	public void addNew(final String event) {
		eventSources.put(event, new EventSource(event));
	}
	
	public boolean contains (final String event) {
		return eventSources.containsKey(event);
	}
	public EventSource get (final String event) {
		return eventSources.get(event);
	}
	
	public Set<String> list () {
		return Collections.unmodifiableSet(eventSources.keySet());
	}

	public EventSource remove(final String event) {
		return eventSources.remove(event);
	}

	@Handler
	public void handle(final RegisterEventSourceMessage message) {
		add(message.getEvent(), new EventSource(message.getEvent()));
	}
	@Handler
	public void handle(final UnregisterEventSourceMessage message) {
		remove(message.getEvent());
	}
}
