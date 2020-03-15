package com.thorstenmarx.webtools.manager.pages;

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
import com.thorstenmarx.webtools.api.events.DashboardSelectionChangedEvent;
import com.thorstenmarx.webtools.api.model.Site;
import com.thorstenmarx.webtools.api.message.Message;
import com.thorstenmarx.webtools.api.message.MessageStream;
import com.thorstenmarx.webtools.manager.services.SiteService;
import com.thorstenmarx.webtools.manager.wicket.MessagePanel;
import com.thorstenmarx.webtools.manager.wicket.dashboard.Dashboard;
import com.thorstenmarx.webtools.manager.wicket.dashboard.widgets.DefaultWidget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class DashboardPage extends BasePage {

    private static final long serialVersionUID = -1097997000715535529L;

    private static final Logger LOGGER = LogManager.getLogger(DashboardPage.class);

    private static final JavaScriptResourceReference D3_JS = new JavaScriptResourceReference(Dashboard.class,
            "js/d3.min.js");
    private static final JavaScriptResourceReference DIMPLE_JS = new JavaScriptResourceReference(Dashboard.class,
            "js/dimple.v2.3.0.min.js");

    private final Dashboard dashboard;
    private Site selectedSite;

    @Inject
    private transient SiteService siteService;
    @Inject
    private transient MessageStream messageStream;

    /**
     * Dashboard entry page.
     */
    public DashboardPage() {
        super();

        this.dashboard = new Dashboard("dashboard");
//		dashboard.addWidget(new OverviewWidget("topChart", "Requests", 12));
        dashboard.addWidget(new DefaultWidget("dw1", "Default Widget", 4));

        add(dashboard);

        init();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptReferenceHeaderItem.forReference(D3_JS));
    }

    private void init() {

        initMessages();

        Collection<Site> sitesTemp = siteService.all();

        List<Site> sites = new ArrayList<>(sitesTemp);

        IModel<List<? extends Site>> siteChoices = new AbstractReadOnlyModel<List<? extends Site>>() {
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
                DashboardSelectionChangedEvent event = new DashboardSelectionChangedEvent(target);
                event.site(selectedSite);
                dashboard.update(event);
            }
        };
        form.add(siteSubmit);
    }

    public Site getSelectedSite() {
        return selectedSite;
    }

    public void setSelectedSite(Site selectedSite) {
        this.selectedSite = selectedSite;
    }

    private void initMessages() {
        IModel<List<Message>> messagesModel = new LoadableDetachableModel<List<Message>>() {
            @Override
            protected List<Message> load() {
                return messageStream.list(MessageStream.Destination.DASHBOARD);
            }
        };
        ListView<Message> repeating = new ListView<Message>("messages", messagesModel) {
            private static final long serialVersionUID = 4949588177564901031L;

            @Override
            protected void populateItem(ListItem<Message> item) {
                MessagePanel messagePanel = new MessagePanel("message", item.getModelObject());
                item.add(messagePanel);
            }

        };
        add(repeating);
    }

	@Override
	public String getTitle() {
		return getString("pages.dashboard.title");
	}

}
