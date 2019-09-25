package com.thorstenmarx.webtools.manager.pages.configuration.targetaudiences;

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

import com.google.inject.Inject;
import com.thorstenmarx.webtools.api.actions.SegmentService;
import com.thorstenmarx.webtools.api.actions.model.Segment;
import com.thorstenmarx.webtools.manager.wicket.components.ConfirmationLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 * @author marx
 */
public class RemoveSegmentLink extends ConfirmationLink<Void> {
	
	@Inject
	transient SegmentService service;
	private final Segment segment;
	

	public RemoveSegmentLink(String componentId, final Segment segment) {
		super(componentId, "Delete segment?");
		this.segment = segment;
	}

	

	@Override
	public void onClick(AjaxRequestTarget art) {
		
		service.remove(this.segment.getId());
		getSession().info(new StringResourceModel("segmentRemoved", Model.of(segment.getName())));
        setResponsePage(SegmentsPage.class);
	}

}
