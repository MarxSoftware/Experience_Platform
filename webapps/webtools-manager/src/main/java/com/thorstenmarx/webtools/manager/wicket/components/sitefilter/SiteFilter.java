/*
 * Copyright (C) 2020 WP DigitalExperience
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.thorstenmarx.webtools.manager.wicket.components.sitefilter;

/*-
 * #%L
 * webtools-manager
 * %%
 * Copyright (C) 2016 - 2020 WP DigitalExperience
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
import com.thorstenmarx.webtools.api.events.DashboardSelectionChangedEvent;
import com.thorstenmarx.webtools.api.model.Site;
import com.thorstenmarx.webtools.manager.services.SiteService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author marx
 */
public class SiteFilter extends Panel {

	private Site selectedSite;

	@Inject
	private transient SiteService siteService;

	final Consumer<SelectionChangedEvent> consumer;
	
	public SiteFilter(final String id, final Consumer<SelectionChangedEvent> consumer) {
		super(id);
		this.consumer = consumer;
	}

	public Site getSelectedSite() {
		return selectedSite;
	}
	
	

	@Override
	protected void onInitialize() {
		super.onInitialize();
		Collection<Site> sitesTemp = siteService.all();

		List<Site> sites = new ArrayList<>(sitesTemp);

		IModel<List<? extends Site>> siteChoices = new IModel<List<? extends Site>>() {
			private static final long serialVersionUID = -2583290457773357445L;

			@Override
			public List<Site> getObject() {
				return sites;
			}
			
		};

		final DropDownChoice<Site> siteSelector = new DropDownChoice<>("sites",
				new PropertyModel<>(this, "selectedSite"), siteChoices, new IChoiceRenderer<Site>() {
			private static final long serialVersionUID = 2636774494996431892L;

			@Override
			public Object getDisplayValue(Site t) {
				return t.getName();
			}

			@Override
			public String getIdValue(Site t, int i) {
				return t.getId();
			}

			@Override
			public Site getObject(String string, IModel<? extends List<? extends Site>> imodel) {
				List<? extends Site> sites = imodel.getObject();
				Optional<? extends Site> siteOptional = sites.stream().filter(site -> site.getId().equals(string)).findFirst();
				return siteOptional.orElse(null);
			}

		});
		siteSelector.setNullValid(true);

		Form<?> form = new Form("form");
		add(form);
		form.add(siteSelector);
		siteSelector.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = -1107858522700306810L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (selectedSite != null) {
				}
			}
		});

		AjaxSubmitLink siteSubmit = new AjaxSubmitLink("siteSubmit", form) {
			private static final long serialVersionUID = 6169268312422258401L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				SelectionChangedEvent event = new SelectionChangedEvent(target);
				event.site(selectedSite);
				consumer.accept(event);
			}
		};
		form.add(siteSubmit);
	}

	public static class SelectionChangedEvent {

		private Site site;

		private AjaxRequestTarget target;

		public SelectionChangedEvent(AjaxRequestTarget target) {
			this.target = target;
		}

		public AjaxRequestTarget target() {
			return this.target;
		}

		public Site site() {
			return site;
		}

		public void site(Site site) {
			this.site = site;
		}
	}
}
