package com.thorstenmarx.webtools.manager.pages.configuration.extensions;

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
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.thorstenmarx.modules.api.ManagerConfiguration;
import com.thorstenmarx.modules.api.ModuleDescription;
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.manager.pages.BasePage;
import com.thorstenmarx.webtools.manager.wicket.components.ConfirmationLink;
import com.thorstenmarx.webtools.ContextListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;

public class ExtensionsPage extends BasePage {

	private static final long serialVersionUID = 5256750544476030004L;

	@Inject
	transient private ModuleManager modules;

	public ExtensionsPage() {
		super();
		initGui();
	}
	

	private void initGui() {
		addSitesmodule();
	}

	private void addSitesmodule() {
		ListView<ManagerConfiguration.ModuleConfig> sitesView = new ListView<ManagerConfiguration.ModuleConfig>("extensions", createModelForSites()) {
			private static final long serialVersionUID = 9101744072914090143L;

			@Override
			protected void populateItem(final ListItem<ManagerConfiguration.ModuleConfig> item) {
				String name = "undefined";
				String version = "undefined";
				try {
					ModuleDescription description = modules.description(item.getModelObject().getId());
					name = description.getName();
					version = description.getVersion();
				} catch (IOException ex) {
					Logger.getLogger(ExtensionsPage.class.getName()).log(Level.SEVERE, null, ex);
				}
				item.add(new Label("id", Model.of(item.getModelObject().getId())));
				item.add(new Label("name", Model.of(name)));
				item.add(new Label("version", Model.of(version)));
				item.add(new Label("active", Model.of(item.getModelObject().isActive())));
				ConfirmationLink<Void> actionLink = null;
				if (item.getModelObject().isActive()) {
					actionLink = new DeactivateExtensionLink("actionLink", item.getModelObject().getId());
					actionLink.add(new Label("label", getString("deactivate")));
					actionLink.add(AttributeAppender.append("class", "btn btn-warning"));
				} else {
					actionLink = new ActivateExtensionLink("actionLink", item.getModelObject().getId());
					actionLink.add(new Label("label", getString("activate")));
					actionLink.add(AttributeAppender.append("class", "btn btn-primary"));
				}
				item.add(actionLink);
			}
		};

		sitesView.setVisible(!sitesView.getList().isEmpty());
		add(sitesView);

		Label noSitesLabel = new Label("noExtensionsLabel", "There are no extensions installed. Maybe you can add one?");
		noSitesLabel.setVisible(!sitesView.isVisible());
		add(noSitesLabel);

		final FileUploadForm simpleUploadForm = new FileUploadForm("simpleUpload");
		add(simpleUploadForm);
	}

	private LoadableDetachableModel<List<ManagerConfiguration.ModuleConfig>> createModelForSites() {
		return new LoadableDetachableModel<List<ManagerConfiguration.ModuleConfig>>() {
			private static final long serialVersionUID = 5275935387613157437L;

			@Override
			protected List<ManagerConfiguration.ModuleConfig> load() {
				return new ArrayList<>(modules.configuration().getModules().values());
			}

		};
	}
	
	@Override
	public String getTitle () {
		return getString("pages.extensions.title");
	}

	/**
	 * Form for uploads.
	 */
	private class FileUploadForm extends Form<Void> {

		FileUploadField fileUploadField;

		/**
		 * Construct.
		 *
		 * @param name Component name
		 */
		public FileUploadForm(String name) {
			super(name);

			// set this form to multipart mode (always needed for uploads!)
			setMultiPart(true);

			// Add one file input field
			add(fileUploadField = new FileUploadField("fileInput"));

			setMaxSize(Bytes.megabytes(50));
			setFileMaxSize(Bytes.megabytes(50));
		}

		/**
		 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		protected void onSubmit() {
			final List<FileUpload> uploads = fileUploadField.getFileUploads();
			if (uploads != null) {
				for (FileUpload upload : uploads) {
					// Create a new file
					File newFile = new File(Files.createTempDir(), upload.getClientFileName());
					
					try {
						// Save to new file
						newFile.createNewFile();
						upload.writeTo(newFile);
						ModuleManager mm = ContextListener.INJECTOR_PROVIDER.injector().getInstance(ModuleManager.class);
						mm.installModule(newFile.toURI());
					} catch (Exception e) {
						throw new IllegalStateException("Unable to write file", e);
					}
				}
			}
		}
	}
}
