package com.thorstenmarx.webtools.manager.wicket.dashboard.widgets;

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

import com.thorstenmarx.webtools.api.Lookup;
import com.thorstenmarx.webtools.api.TimeWindow;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.events.DashboardSelectionChangedEvent;
import com.thorstenmarx.webtools.api.model.Site;
import com.thorstenmarx.webtools.api.ui.Widget;
import com.thorstenmarx.webtools.reports.DefaultReport;
import java.util.Map;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 *
 * @author marx
 */
public class DefaultWidget extends Widget {

	private static final long serialVersionUID = -4054192800119763291L;

	Model<String> visitModel = Model.of("0");
	Model<String> userModel = Model.of("0");
	
	Label visitLabel;
	Label userLabel;
	
	public DefaultWidget(String id, String title, int size) {
		super(id, title, size);

		visitLabel = new Label("visitCount", visitModel);
		visitLabel.setOutputMarkupId(true);
		add(visitLabel);
		userLabel = new Label("userCount", userModel);
		userLabel.setOutputMarkupId(true);
		add(userLabel);
				
		init(null);
		
	}

	private void init(DashboardSelectionChangedEvent event) {
		
		String site = null;
		if (event != null && event.site() != null) {
			site = event.site().getId();
		}
		
		AnalyticsDB db = Lookup.getDefault().lookup(AnalyticsDB.class);
		DefaultReport report = new DefaultReport(db);
		long start = System.currentTimeMillis() - new TimeWindow(TimeWindow.UNIT.MINUTE, 5).millis();
		Map<String, Object> status = report.status(site, null, start, System.currentTimeMillis());
		
		visitModel.setObject(String.valueOf(status.get("visitCount")));
		userModel.setObject(String.valueOf(status.get("userCount")));
		
		if (event == null){
			add(visitLabel);
			add(userLabel);
		} else {
			event.target().add(visitLabel);
			event.target().add(userLabel);
		}

	}
	@Override
	public void update (DashboardSelectionChangedEvent event) {
		
		Site site = event.site();
		init(event);
	}
}
