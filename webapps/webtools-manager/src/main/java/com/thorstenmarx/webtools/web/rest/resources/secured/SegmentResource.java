package com.thorstenmarx.webtools.web.rest.resources.secured;

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
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.api.actions.ActionSystem;
import com.thorstenmarx.webtools.api.actions.SegmentService;
import com.thorstenmarx.webtools.api.actions.model.Segment;
import com.thorstenmarx.webtools.api.actions.model.rules.EventRule;
import com.thorstenmarx.webtools.api.actions.model.rules.PageViewRule;
import com.thorstenmarx.webtools.api.actions.model.rules.ScoreRule;
import java.util.List;
import java.util.Collections;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author marx
 */
@Path("segments")
public class SegmentResource {

	private static final Logger LOGGER = LogManager.getLogger(SegmentResource.class);

	final ActionSystem actionSystem;
	final SegmentService segmentService;
	public SegmentResource () {
		this.actionSystem = ContextListener.INJECTOR_PROVIDER.injector().getInstance(ActionSystem.class);
		this.segmentService= ContextListener.INJECTOR_PROVIDER.injector().getInstance(SegmentService.class);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public String list() {
		JSONObject result = new JSONObject();
		try {
			JSONArray array = new JSONArray();
			
			segmentService.all().forEach((segment) -> {
				array.add(segment.toJson());
			});
			
			result.put("segments", array);
			result.put("status", "ok");
			
			
		} catch (Exception e) {
			LOGGER.error("", e);
			result.put("status", "fail");
		}
		return result.toJSONString();
	}
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String single(@PathParam("id") final String id) {
		Segment segment = segmentService.get(id);
		if (segment != null){
			return segment.toJson().toJSONString();
		}
		return "{}";
	}
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String delete(@PathParam("id") final String id) {
		Segment segment = segmentService.get(id);
		if (segment != null){
			segmentService.remove(id);
		}
		return "{}";
	}
	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String update(@PathParam("id") final String id, final String content) {
		Segment segment = Segment.fromJson(JSONObject.parseObject(content));
		if (segment != null) {
			if (segment.startTimeWindow() == null) {
				throw new WebApplicationException("timewindow can not be null", Response.Status.BAD_REQUEST);
			} else if (segment.getId() == null || segment.getName() == null) {
				throw new WebApplicationException("id and name can not be null", Response.Status.BAD_REQUEST);
			}
			
			segmentService.add(segment);
		}
		
		return "{status : 'ok'}";
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String add(final String content) {
		Segment segment = Segment.fromJson(JSONObject.parseObject(content));
		if (segment != null){
			segmentService.add(segment);
		}
		return "{status : 'ok'}";
	}
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public String segments() {
		
		JSONObject result = new JSONObject();
		try {
			JSONArray array = new JSONArray();
			
			segmentService.all().forEach((segment) -> {
				JSONObject segmentObj = segmentToJson(segment);
				
				array.add(segmentObj);
			});
			
			result.put("segments", array);
			result.put("status", "ok");
		} catch (Exception e) {
			LOGGER.error("", e);
			result.put("status", "fail");
		}
		
		return result.toJSONString();
	}

	private JSONObject segmentToJson(Segment segment) {
		JSONObject segmentObj = new JSONObject();
		segmentObj.put("name", segment.getName());
		segmentObj.put("id", segment.getId());
		JSONArray rules = new JSONArray();
		segment.rules().forEach(rule -> {
			JSONObject jsonRule = new JSONObject();
			jsonRule.put("id" , rule.id());
			if (rule instanceof ScoreRule) {
				jsonRule.put("name", ((ScoreRule)rule).name());
				jsonRule.put("type", "score");
			} else if (rule instanceof PageViewRule) {
				jsonRule.put("type", "pagEView");
			} else if (rule instanceof EventRule) {
				jsonRule.put("event", ((EventRule)rule).event());
				jsonRule.put("type", "event");
			}
			rules.add(jsonRule);
		});
		segmentObj.put("rules", rules);
		return segmentObj;
	}
}
