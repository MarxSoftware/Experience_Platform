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
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.api.actions.InvalidSegmentException;
import com.thorstenmarx.webtools.api.actions.SegmentService;
import com.thorstenmarx.webtools.api.actions.model.Segment;
import com.thorstenmarx.webtools.api.actions.model.Segment;
import com.thorstenmarx.webtools.api.entities.criteria.Restrictions;
import com.thorstenmarx.webtools.hosting.extensions.HostingPackageValidatorExtension;
import com.thorstenmarx.webtools.web.hosting.HostingPackageEvaluator;
import java.util.List;
import java.util.logging.Level;
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
import javax.ws.rs.core.Response;
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
	private final ModuleManager moduleManager;

	private final HostingPackageEvaluator hostingPackageEvaluator;

	public AudienceResource() {
		this.segmentService = ContextListener.INJECTOR_PROVIDER.injector().getInstance(SegmentService.class);
		this.moduleManager = ContextListener.INJECTOR_PROVIDER.injector().getInstance(ModuleManager.class);

		this.hostingPackageEvaluator = new HostingPackageEvaluator(moduleManager);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public String get(@QueryParam("wpid") final long wpid, @QueryParam("site") final String site) {
		JSONObject result = new JSONObject();

		segmentService.criteria()
				.add(Restrictions.EQ.eq("site", site))
				.add(Restrictions.EQ.eq("externalId", wpid))
				.query().stream().forEach((segment) -> {
			JSONObject segmentObj = toJson(segment);

			result.put("segment", segmentObj);
		});

		return result.toJSONString();
	}

	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public String list(@QueryParam("site") final String site) {

		JSONObject result = new JSONObject();
		JSONArray segments = new JSONArray();

		segmentService.criteria()
				.add(Restrictions.EQ.eq("site", site))
				.query().stream().map(this::toJson).forEach(segments::add);

		result.put("segments", segments);

		return result.toJSONString();
	}

	private JSONObject toJson(final Segment segment) {
		JSONObject segmentObj = new JSONObject();
		segmentObj.put("id", segment.getId());
		segmentObj.put("external_id", segment.getExternalId());
		segmentObj.put("name", segment.getName());
		segmentObj.put("content", segment.getContent());
		segmentObj.put("active", segment.isActive());
		segmentObj.put("site", segment.getSite());
		segmentObj.put("attributes", segment.getAttributes());
		segmentObj.put("time.count", segment.getTimeWindow().getCount());
		segmentObj.put("time.unit", segment.getTimeWindow().getUnit());

		return segmentObj;
	}

	@DELETE
	public String delete(@QueryParam("wpid") final long wpid, @QueryParam("site") final String site) {
		List<Segment> queryResult = segmentService.criteria()
				.add(Restrictions.EQ.eq("externalId", wpid))
				.add(Restrictions.EQ.eq("site", site))
				.query();

		LOGGER.info("found: " + queryResult.size() + " audiences to delete");
		queryResult.stream().map(segment -> segment.getId()).forEach(segmentService::remove);

		JSONObject result = new JSONObject();
		result.put("status", "ok");
		return result.toJSONString();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(final Audience audience) {
		// check existens
		List<?> queryResult = segmentService.criteria()
				.add(Restrictions.EQ.eq("externalId", audience.getExternalId()))
				.add(Restrictions.EQ.eq("site", audience.getSite()))
				.query();
		if (!queryResult.isEmpty()) {
			return update(audience);
		}

		// validate the allowed amount of segmentes allowed for this
		final String site = audience.getSite();
		if (Strings.isNullOrEmpty(site)) {
			JSONObject result = new JSONObject();
			result.put("status", "error");
			result.put("message", "Site parameter is not set.");
			return Response.status(Response.Status.FORBIDDEN.getStatusCode(), "Site parameter is not set.")
					.entity(result.toJSONString()).build();
		}
		if (!hostingPackageEvaluator.is_action_allowed(site, HostingPackageValidatorExtension.Action.CREATE_SEGMENTE)) {
			JSONObject result = new JSONObject();
			result.put("status", "error");
			result.put("message", "Your maximum number of segments is reached.");
			return Response.status(Response.Status.FORBIDDEN.getStatusCode(), "Your maximum number of segments is reached.")
					.entity(result.toJSONString()).build();
		}

		Segment segment = new Segment();
		addAttributes(segment, audience);

		try {
			segmentService.add(segment);

			JSONObject result = new JSONObject();
			result.put("status", "ok");
			result.put("audience_id", segment.getId());
			return Response.status(Response.Status.OK).entity(result.toJSONString()).build();
		} catch (InvalidSegmentException ex) {
			JSONObject result = new JSONObject();
			result.put("status", "error");
			result.put("message", ex.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Erro saving the segment.")
					.entity(result.toJSONString()).build();
		}

	}

	private void addAttributes(Segment segment, final Audience audience) {
		segment.setName(audience.getName());
		segment.setActive(audience.isActive());
		segment.setContent(audience.getContent());
		segment.setExternalId(audience.getExternalId());
		segment.setSite(audience.getSite());
		segment.setAttributes(audience.getAttributes());
		if (audience.getPeriod() != null) {
			segment.setTimeWindow(audience.getPeriod().toTimeWindow());
		}
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(final Audience audience) {
		// check existens
		List<Segment> queryResult = segmentService.criteria()
				.add(Restrictions.EQ.eq("externalId", audience.getExternalId()))
				.add(Restrictions.EQ.eq("site", audience.getSite()))
				.query().stream().collect(Collectors.toList());
		if (queryResult.isEmpty()) {
			JSONObject result = new JSONObject();
			result.put("status", "error");
			result.put("message", "Audience not exists.");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Audience not exists.").entity(result.toJSONString()).build();
		} else if (queryResult.size() > 1) {
			JSONObject result = new JSONObject();
			result.put("status", "error");
			result.put("message", "Multiple audiences found.");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Multiple audiences found.").entity(result.toJSONString()).build();
		}

		Segment segment = queryResult.get(0);
		addAttributes(segment, audience);

		try {
			segmentService.add(segment);
			
			JSONObject result = new JSONObject();
			result.put("status", "ok");
			result.put("audience_id", segment.getId());
			return Response.status(Response.Status.OK).entity(result.toJSONString()).build();
		} catch (InvalidSegmentException ex) {
			JSONObject result = new JSONObject();
			result.put("status", "error");
			result.put("message", ex.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Erro updating the segment.").entity(result.toJSONString()).build();
		}
	}

	
}
