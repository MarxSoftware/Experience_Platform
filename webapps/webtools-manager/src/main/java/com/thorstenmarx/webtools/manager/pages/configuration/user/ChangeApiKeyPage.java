package com.thorstenmarx.webtools.manager.pages.configuration.user;

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
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.Fields;
import com.thorstenmarx.webtools.api.configuration.Configuration;
import com.thorstenmarx.webtools.manager.pages.BasePage;
import com.thorstenmarx.webtools.manager.utils.Helper;
import java.io.IOException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.feedback.ExactLevelFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 *
 * @author marx
 */
public class ChangeApiKeyPage extends BasePage {

	private static final Logger LOGGER = LogManager.getLogger(ChangeApiKeyPage.class);
	private static final long serialVersionUID = -4325916820203608307L;

	private FeedbackPanel errorFeedBack = null;
	private FeedbackPanel successFeedBack = null;

	public ChangeApiKeyPage() {
		super();

		init();
	}

	private void displaySuccess() {
		successFeedBack.add(new AttributeModifier("class", "alert alert-success"));
		errorFeedBack.add(new AttributeModifier("class", ""));
	}

	private void displayError() {
		errorFeedBack.add(new AttributeModifier("class", "alert alert-danger"));
		successFeedBack.add(new AttributeModifier("class", ""));
	}

	private void clearMessage() {
		errorFeedBack.add(new AttributeModifier("class", ""));
		successFeedBack.add(new AttributeModifier("class", ""));
	}

	private void init() {

		errorFeedBack = new FeedbackPanel("feedbackMessage", new ExactLevelFeedbackMessageFilter(FeedbackMessage.ERROR));
		errorFeedBack.setOutputMarkupId(true);
		successFeedBack = new FeedbackPanel("succesMessage", new ExactLevelFeedbackMessageFilter(FeedbackMessage.SUCCESS));
		successFeedBack.setOutputMarkupId(true);
		add(errorFeedBack);
		add(successFeedBack);

		final IModel model = new LoadableDetachableModel() {
			private static final long serialVersionUID = 7474274077691068779L;

			@Override
			protected Object load() {
				Configuration config = ContextListener.INJECTOR_PROVIDER.injector().getInstance(Configuration.class);
				Optional<String> apikey = config.getString(Fields.ApiKey.value());
				
				return apikey.orElse("");
			}
		};

		clearMessage();
		final Label apikeyLabel = new Label(Fields.ApiKey.value(), model);
		apikeyLabel.setOutputMarkupId(true);
		AjaxLink generateLink = new AjaxLink("apikeyLink") {
			private static final long serialVersionUID = -7978723352517770644L;

			@Override
			public void onClick(AjaxRequestTarget target) {

				try {

					Configuration config = ContextListener.INJECTOR_PROVIDER.injector().getInstance(Configuration.class);
					final String apikey = Helper.randomString();
					config.set(Fields.ApiKey.value(), apikey);

					successFeedBack.success(getString("generate.success"));

					displaySuccess();

					target.add(apikeyLabel);
					target.add(successFeedBack);
//					target.addChildren(getPage(), FeedbackPanel.class);
				} catch (Exception ex) {
					LOGGER.error(ex);
					error(getString("generate.error"));
					displayError();
					target.add(errorFeedBack);
				}
			}
		};

		add(generateLink);
		add(apikeyLabel);
	}
	
	@Override
	public String getTitle () {
		return getString("pages.apikey.title");
	}
}
