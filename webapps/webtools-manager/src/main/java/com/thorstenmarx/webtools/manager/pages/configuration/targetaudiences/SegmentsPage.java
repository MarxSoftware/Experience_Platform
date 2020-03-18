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
import com.thorstenmarx.webtools.api.analytics.Fields;
import com.thorstenmarx.webtools.api.entities.criteria.Criteria;
import com.thorstenmarx.webtools.api.entities.criteria.Restrictions;
import com.thorstenmarx.webtools.api.model.Site;
import com.thorstenmarx.webtools.manager.pages.BasePage;
import com.thorstenmarx.webtools.manager.wicket.components.sitefilter.SiteFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

public class SegmentsPage extends BasePage implements Consumer<SiteFilter.SelectionChangedEvent> {

	private static final long serialVersionUID = -2053979078406890879L;

	@Inject
	transient private SegmentService service;
	
	private SiteFilter siteFilter;
	private SegmentsPanel segmentsPanel;
	
	public SegmentsPage() {
		super();
		
		initGui();
	}

	private void initGui() {
		
		siteFilter = new SiteFilter("sitefilter", this);
		segmentsPanel = new SegmentsPanel("segments");
		segmentsPanel.setOutputMarkupId(true);
		add(siteFilter);
		add(segmentsPanel);

		addCreateNewSegmentLink();
	}

	@Override
	public void accept(SiteFilter.SelectionChangedEvent event) {
		
		segmentsPanel.setSelectedSite(event.site());
		event.target().add(segmentsPanel);
	}


	private LoadableDetachableModel<List<Segment>> createModelForSegments() {

		return new LoadableDetachableModel<List<Segment>>() {
			private static final long serialVersionUID = 5275935387613157437L;
			@Override
			protected List<Segment> load() {
				if (siteFilter.getSelectedSite() != null) {
					Criteria<Segment> add = service.criteria().add(Restrictions.EQ.eq(Fields.Site.value(), siteFilter.getSelectedSite().getId()));
					return new ArrayList<>(add.query());
				} else {
					return new ArrayList<>(service.all());
				}
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
