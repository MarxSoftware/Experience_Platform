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
import com.thorstenmarx.webtools.api.TimeWindow;
import com.thorstenmarx.webtools.api.actions.InvalidSegmentException;
import com.thorstenmarx.webtools.api.actions.model.Segment;
import com.thorstenmarx.webtools.manager.pages.BasePage;
import java.util.Arrays;
import org.apache.wicket.Session;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddEditAdvancedSegmentPage extends BasePage {

	private static final Logger LOGGER = LoggerFactory.getLogger(AddEditAdvancedSegmentPage.class);
	
	@Inject
	transient private SegmentService service;

	private boolean edit = false;

	private WebMarkupContainer rulesContainer;

	TimeWindow.UNIT unit;
	long unitCount;

	boolean active = false;

	public AddEditAdvancedSegmentPage() {
		super();
		this.edit = false;
		setDefaultModel(new Model<>(new Segment()));
		initGui();
	}

	public AddEditAdvancedSegmentPage(final IModel<Segment> model) {
		super();
		this.edit = true;

		unit = model.getObject().startTimeWindow().getUnit();
		unitCount = model.getObject().startTimeWindow().getCount();
		active = model.getObject().isActive();
		setDefaultModel(model);
		initGui();
	}

	private void initGui() {

		Form<Segment> addSegmentForm = new Form<>("addSegmentForm",
				new CompoundPropertyModel<Segment>((IModel<Segment>) getDefaultModel()));
		add(addSegmentForm);

		Label nameLabel = new Label("nameLabel", new StringResourceModel("segmentName", this, null));
		addSegmentForm.add(nameLabel);

		Label wpidLabel = new Label("externalidLabel", Model.of(((Segment) getDefaultModel().getObject()).getExternalId()));
		addSegmentForm.add(wpidLabel);

		addSegmentForm.add(createLabelFieldWithValidation("name", "segmentName"));

		TextField<String> idField = new HiddenField<>("id");
		idField.setLabel(new StringResourceModel("segmentId", this, null));
		addSegmentForm.add(idField);

		Label activeLabel = new Label("activeLabel", new StringResourceModel("segmentActive", this, null));
		addSegmentForm.add(activeLabel);
		CheckBox activeCheckbox = new CheckBox("segmentActive", new PropertyModel<>(this, "active"));
		addSegmentForm.add(activeCheckbox);

		ChoiceRenderer<TimeWindow.UNIT> intervalRenderer = new ChoiceRenderer<>("name");
		final DropDownChoice<TimeWindow.UNIT> timeRangeDropdown = new DropDownChoice<>("timerange-interval", new PropertyModel<>(this, "unit"), Arrays.asList(TimeWindow.UNIT.values()), intervalRenderer);
		timeRangeDropdown.setRequired(true);
		addSegmentForm.add(timeRangeDropdown);
		final TextField timerangeField = new TextField("timerange-count", new PropertyModel<>(this, "unitCount"));
		timerangeField.setRequired(true);
		addSegmentForm.add(timerangeField);

		Button submitButton = new Button("submitButton") {
			private static final long serialVersionUID = 9123164874596936371L;

			@Override
			public void onSubmit() {
				Segment segment = getSegmentFromPageModel();

				try {
					service.add(segment);
					getSession().info(new StringResourceModel("segmentUpdated", this, null).getString());
				} catch (InvalidSegmentException ex) {
					LOGGER.error("error saving segment", ex);
					getSession().info(getString("dsltextarea.error"));
				}

				setResponsePage(new SegmentsPage());
			}
		};
		addSegmentForm.add(submitButton);

		Button cancelButton = new Button("cancelButton") {
			@Override
			public void onSubmit() {
				setResponsePage(new SegmentsPage());
			}
		};
		cancelButton.setDefaultFormProcessing(false);
		addSegmentForm.add(cancelButton);

		final TextArea dslTextArea = new TextArea("dsltextarea", new PropertyModel<>(getDefaultModel(), "content"));
//		dslTextArea.add(new DSLScriptValidator(getString("dsltextarea.error")));
		addSegmentForm.add(dslTextArea);

		Session.get().getFeedbackMessages().clear();
		addSegmentForm.add(new FeedbackPanel("feedback"));

	}

	private RequiredTextField<String> createLabelFieldWithValidation(String id, String property) {
		RequiredTextField<String> nameField = new RequiredTextField<>(id);
		nameField.setLabel(new StringResourceModel(property, this, null));

		return nameField;
	}

	@SuppressWarnings("unchecked")
	private Segment getSegmentFromPageModel() {
		Segment segment = (Segment) getDefaultModel().getObject();
		segment.start(new TimeWindow(unit, unitCount));
		segment.setActive(active);
		return segment;
	}

	public TimeWindow.UNIT getUnit() {
		return unit;
	}

	public void setUnit(TimeWindow.UNIT unit) {
		this.unit = unit;
	}

	public long getUnitCount() {
		return unitCount;
	}

	public void setUnitCount(long unitCount) {
		this.unitCount = unitCount;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forUrl("https://cdnjs.cloudflare.com/ajax/libs/ace/1.4.7/ace.js"));
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String getTitle() {
		return getString("pages.segment.advanced.edit.title");
	}

}
