package com.thorstenmarx.webtools.manager.rest.endpoints;

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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.impl.eventsource.EventSources;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;

/**
 *
 * @author marx
 */
@Path("/event")
public class EventEndpoint {

	private final EventSources eventSources;

	public EventEndpoint() {
		eventSources = ContextListener.INJECTOR_PROVIDER.injector().getInstance(EventSources.class);
	}

	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public String broadcastMessage() {

		JSONObject result = new JSONObject();

		JSONArray events = new JSONArray();
		eventSources.list().forEach(events::add);
		result.put("events", events);

		return result.toJSONString();
	}

	@GET
	@Path("/send/{event}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String broadcastMessage(@PathParam("event") final String event, final @QueryParam("message") String message) {

		if (Strings.isNullOrEmpty(event)) {
			throw new WebApplicationException("Event muste not be null or empty", Response.Status.BAD_REQUEST);
		}
		if (!eventSources.contains(event)) {
			throw new WebApplicationException("EventSource not found for event: " + event, Response.Status.NOT_FOUND);
		}

		eventSources.get(event).send(message);

		return "Message '" + message + "' has been broadcast.";
	}

	@GET
	@Path("/register/{event}")
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput register(@PathParam("event") final String event) {

		if (Strings.isNullOrEmpty(event)) {
			throw new WebApplicationException("Event muste not be null or empty", Response.Status.BAD_REQUEST);
		}
		if (!eventSources.contains(event)) {
			throw new WebApplicationException("EventSource not found for event: " + event, Response.Status.NOT_FOUND);
		}

		final EventOutput eventOutput = new EventOutput();
		eventSources.get(event).register(eventOutput);
		return eventOutput;
	}

}
