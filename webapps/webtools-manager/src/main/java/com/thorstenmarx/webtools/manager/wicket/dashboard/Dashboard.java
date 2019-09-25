package com.thorstenmarx.webtools.manager.wicket.dashboard;

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
import com.thorstenmarx.webtools.api.events.DashboardSelectionChangedEvent;
import com.thorstenmarx.webtools.api.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

/**
 *
 * @author marx
 */
public class Dashboard extends Panel {

	private static final long serialVersionUID = -8250988785563548138L;
	
	RepeatingView repeating;
	
	List<Widget> widgets = new ArrayList<>();
	
	public Dashboard(String id) {
		super(id);
		
		repeating = new RepeatingView("widgets");
		add(repeating);
		
//		repeating.add(new Widget("w0", "Widget oben", 12));
		/*
		repeating.add(new Widget("w1", "Widget 1", 2));	
		repeating.add(new Widget("w2", "Widget 2", 2));
		repeating.add(new Widget("w3", "Widget 3", 4));
		repeating.add(new Widget("w4", "Widget 4", 4));
		*/	
	}
	
	public void addWidget (final Widget widget) {
		widgets.add(widget);
		repeating.add(widget);
	}
	
	public void update (final DashboardSelectionChangedEvent event ) {
		try {
			widgets.forEach((w) -> {w.update(event);});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
