package com.thorstenmarx.webtools.web.rest.resources.secured.targetaudience;

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
import com.thorstenmarx.webtools.api.actions.ActionSystem;
import com.thorstenmarx.webtools.api.actions.SegmentService;
import com.thorstenmarx.webtools.api.actions.model.AdvancedSegment;
import com.thorstenmarx.webtools.api.actions.model.Segment;
import com.thorstenmarx.webtools.api.entities.criteria.Restrictions;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author thmar
 */
@Path("audience")
public class AudienceResource {
	
	private static final Logger LOGGER = LogManager.getLogger(AudienceResource.class);
	
	private final SegmentService segmentService;
	private final ActionSystem actionSystem;
	public AudienceResource () {
		this.segmentService = ContextListener.INJECTOR_PROVIDER.injector().getInstance(SegmentService.class);
		this.actionSystem = ContextListener.INJECTOR_PROVIDER.injector().getInstance(ActionSystem.class);
	}
	
	@GET
	public String get () {
		
		JSONObject result = new JSONObject();
		JSONArray segments = new JSONArray();
		segmentService.all().stream().filter(AdvancedSegment.class::isInstance).map(AdvancedSegment.class::cast).forEach((segment) -> {
			JSONObject segmentObj = new JSONObject();
			segmentObj.put("id", segment.getId());
			segmentObj.put("external_id", segment.getExternalId());
			segmentObj.put("name", segment.getName());
			segmentObj.put("content", segment.getContent());
			segmentObj.put("active", segment.isActive());
			segmentObj.put("site", segment.getSite());
			segmentObj.put("time.count", segment.getTimeWindow().getCount());
			segmentObj.put("time.unit", segment.getTimeWindow().getUnit());
			
			segments.add(segmentObj);
		});
		result.put("segments", segments);
		
		return result.toJSONString();
	}
	
	@DELETE
	public String delete (@QueryParam("wpid") final long wpid) {
		List<Segment> queryResult = segmentService.criteria().add(Restrictions.EQ.eq("externalId", wpid)).query();
		
		LOGGER.info("found: " + queryResult.size() + " audiences to delete");
		queryResult.stream().map(segment -> segment.getId()).forEach(segmentService::remove);
		
		JSONObject result = new JSONObject();
		result.put("status", "ok");
		return result.toJSONString();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String create (final Audience audience) {
		// check existens
		List<?> queryResult = segmentService.criteria().add(Restrictions.EQ.eq("externalId", audience.getExternalId())).query();
		if (!queryResult.isEmpty()) {
			JSONObject result = new JSONObject();
			result.put("status", "error");
			result.put("message", "Audience already exists.");
			return update(audience);
		}
		ValidationResult validation = internal_validate(audience.getDsl());
		if (!validation.valid) {
			JSONObject result = new JSONObject();
			result.put("status", "error");
			result.put("message", validation.message);
			
			return result.toJSONString();
		}
		
		AdvancedSegment segment = new AdvancedSegment();
		segment.setName(audience.getName());
		segment.setActive(audience.isActive());
		segment.setContent(audience.getDsl());
		segment.setExternalId(audience.getExternalId());
		segment.setSite(audience.getSite());
		if (audience.getPeriod() != null) {
			segment.setTimeWindow(audience.getPeriod().toTimeWindow());
		}
		
		segmentService.add(segment);
		
		JSONObject result = new JSONObject();
		result.put("status", "ok");
		result.put("audience_id", segment.getId());
		return result.toJSONString();
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String update (final Audience audience) {
		// check existens
		List<AdvancedSegment> queryResult = segmentService.criteria()
				.add(Restrictions.EQ.eq("externalId", audience.getExternalId()))
				.add(Restrictions.EQ.eq("site", audience.getSite()))
				.query().stream().map(AdvancedSegment.class::cast).collect(Collectors.toList());
		if (queryResult.isEmpty()) {
			JSONObject result = new JSONObject();
			result.put("status", "error");
			result.put("message", "Audience not exists.");
			return result.toJSONString();
		} else if (queryResult.size() > 1) {
			JSONObject result = new JSONObject();
			result.put("status", "error");
			result.put("message", "Multiple audiences found.");
			return result.toJSONString();
		}
		ValidationResult validation = internal_validate(audience.getDsl());
		if (!validation.valid) {
			JSONObject result = new JSONObject();
			result.put("status", "error");
			result.put("message", validation.message);

			return result.toJSONString();
		}

		AdvancedSegment segment = queryResult.get(0);
		segment.setName(audience.getName());
		segment.setActive(audience.isActive());
		segment.setContent(audience.getDsl());
		segment.setExternalId(audience.getExternalId());
		segment.setSite(audience.getSite());
		if (audience.getPeriod() != null) {
			segment.setTimeWindow(audience.getPeriod().toTimeWindow());
		}

		segmentService.add(segment);

		JSONObject result = new JSONObject();
		result.put("status", "ok");
		result.put("audience_id", segment.getId());
		return result.toJSONString();
	}
	
	@POST
	@Path("/validate")
	public String validate (final String content) {
		
		JSONObject result = new JSONObject();
		
		JSONObject validation = new JSONObject();
		
		ValidationResult validationResult = internal_validate(content);
		if (validationResult.valid) {
			validation.put("valid", true);
		} else {
			validation.put("valid", false);
			validation.put("message", validationResult.message);
		}
		
		result.put("validation", validation);
		result.put("status", "ok");
		
		return result.toJSONString();
	}
	
	private ValidationResult internal_validate (final String dsl) {
		if (Strings.isNullOrEmpty(dsl)) {
			return new ValidationResult(false, "Content should not be null!");
		}
		try {
			return new ValidationResult(actionSystem.validate(dsl));
		} catch (Exception ex) {
			return new ValidationResult(false, ex.getMessage());
		}
	}
}
	
