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
import com.thorstenmarx.webtools.manager.pages.BasePage;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class SegmentsPage extends BasePage {

	private static final long serialVersionUID = -2053979078406890879L;

	@Inject
	transient private SegmentService service;
	
	public SegmentsPage() {
		super();
		
		initGui();
	}

	private void initGui() {
		addSegmentsmodule();
		addCreateNewSegmentLink();
	}

	private void addSegmentsmodule() {
		ListView<Segment> segmentsView = new ListView<Segment>("segments", createModelForSegments()) {
			private static final long serialVersionUID = 9101744072914090143L;
			@Override
			protected void populateItem(final ListItem<Segment> item) {
				item.add(new Label("name", new PropertyModel<>(item.getModel(), "name")));
				item.add(new Label("id", new PropertyModel<>(item.getModel(), "id")));
				
				final Segment segment = item.getModelObject();
				item.add(new Label("externalId", new PropertyModel<>(item.getModel(), "externalId")));
				
				Label active = new Label("active");
				active.add(new AttributeModifier("data-toggle", "tooltip"));
				active.add(new AttributeModifier(" data-placement", "top"));
				if (item.getModelObject().isActive()) {
					active.add(new AttributeModifier("class", "fa fa-play"));					
					active.add(new AttributeModifier("title", getString("tooltip.active")));
					active.add(new AttributeModifier("style", "color: red;"));
				} else {
					active.add(new AttributeModifier("class", "fa fa-pause"));
					active.add(new AttributeModifier("title", getString("tooltip.inactive")));
					active.add(new AttributeModifier("style", "color: green;"));
				}
				item.add(active);

				Link<BasePage> editSegmentLink = new Link<BasePage>("editSegmentLink") {
					private static final long serialVersionUID = -4331619903296515985L;
					@Override
					public void onClick() {
						final Segment segment = item.getModelObject();
						setResponsePage(new AddEditAdvancedSegmentPage(item.getModel()));
					}
				};

				item.add(editSegmentLink);
				item.add(new RemoveSegmentLink("removeSegmentLink", item.getModelObject()));
			}
		};

		segmentsView.setVisible(!segmentsView.getList().isEmpty());
		add(segmentsView);

		Label noSegmentsLabel = new Label("noSegmentsLabel", "There are no segments in the database. Maybe you can add one?");
		noSegmentsLabel.setVisible(!segmentsView.isVisible());
		add(noSegmentsLabel);

	}

	private LoadableDetachableModel<List<Segment>> createModelForSegments() {

		return new LoadableDetachableModel<List<Segment>>() {
			private static final long serialVersionUID = 5275935387613157437L;
			@Override
			protected List<Segment> load() {
				return new ArrayList<>(service.all());
			}

		};
	}

	private void addCreateNewSegmentLink() {
		add(new Link<BasePage>("addAdvancedSegmentPageLink") {
			private static final long serialVersionUID = -4331619903296515985L;

			@Override
			public void onClick() {
				setResponsePage(new AddEditAdvancedSegmentPage());
			}
		});
	}
	
	@Override
	public String getTitle () {
		return getString("pages.segments.title");
	}
}
