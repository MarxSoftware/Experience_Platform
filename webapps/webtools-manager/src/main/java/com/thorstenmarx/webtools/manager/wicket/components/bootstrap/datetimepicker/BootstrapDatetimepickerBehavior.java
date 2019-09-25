package com.thorstenmarx.webtools.manager.wicket.components.bootstrap.datetimepicker;

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
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 *
 * @author thmarx
 */
public class BootstrapDatetimepickerBehavior extends Behavior {

	private static final JavaScriptResourceReference JS = new JavaScriptResourceReference(BootstrapDatetimepickerBehavior.class,
			"libs/bootstrap-datetimepicker-4.17.37/js/bootstrap-datetimepicker.min.js");
	private static final CssResourceReference CSS = new CssResourceReference(BootstrapDatetimepickerBehavior.class,
			"libs/bootstrap-datetimepicker-4.17.37/css/bootstrap-datetimepicker.min.css");

	private static final JavaScriptResourceReference MOMENT_JS = new JavaScriptResourceReference(BootstrapDatetimepickerBehavior.class,
			"libs/moment-with-locales.min.js");
	
	protected Component source;

	protected boolean onLoad = true;

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);

		
		response.render(JavaScriptReferenceHeaderItem.forReference(component.getApplication().getJavaScriptLibrarySettings()
				.getJQueryReference()));
		
		response.render(JavaScriptReferenceHeaderItem.forReference(MOMENT_JS));
		response.render(JavaScriptReferenceHeaderItem.forReference(JS));
		response.render(CssReferenceHeaderItem.forReference(CSS));
		
		if (onLoad) {
			response.render(OnLoadHeaderItem.forScript(toJavascript()));
		} else {
			response.render(OnDomReadyHeaderItem.forScript(toJavascript()));
		}
	}

	@Override
	public void bind(Component component) {
		super.bind(component); //To change body of generated methods, choose Tools | Templates.
		this.source = component;
	}

	/**
	 * Get string to add the prototip to the page
	 *
	 * @return the String
	 */
	protected String toJavascript() {
		StringBuilder script = new StringBuilder();
		
//		script.append("$('#").append(source.getMarkupId()).append("').datetimepicker({pick12HourFormat: false});");
		script.append("$('#").append(source.getMarkupId()).append("').datetimepicker();");
		
		return script.toString();
	}

	/**
	 * Is the javascript set to load 'onload' if false then it will be 'ondomready'
	 *
	 * @return
	 */
	public boolean isOnLoad() {
		return onLoad;
	}

	/**
	 * Is the javascript set to load 'onload' if false then it will be 'ondomready'
	 *
	 * @param onLoad
	 */
	public void setOnLoad(boolean onLoad) {
		this.onLoad = onLoad;
	}
}
