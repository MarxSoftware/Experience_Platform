package com.thorstenmarx.webtools.base.example;

/*-
 * #%L
 * plugin-example
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
import com.thorstenmarx.modules.api.annotation.Extension;
import com.thorstenmarx.webtools.api.extensions.ManagerConfigExtension;
import com.thorstenmarx.webtools.api.ui.GenericPageBuilder;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.slf4j.Logger;
import javax.imageio.ImageIO;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.BufferedDynamicImageResource;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
@Extension(ManagerConfigExtension.class)
public class MyManagerConfigExtension extends ManagerConfigExtension {

	public static final String CONTENT_ID = "contentComponent";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MyManagerConfigExtension.class);
	
	private	BufferedDynamicImageResource imageResource;

	@Override
	public String getTitle() {
		return "MyConfig";
	}

	@Override
	public Image getImage() {
		Image image = new Image("image", imageResource);
		image.add(new AttributeModifier("title", Model.of(getTitle())));
		return image;
	}

	@Override
	public void init() {
		try {
			BufferedImage image = ImageIO.read(MyManagerConfigExtension.class.getResourceAsStream("images/manager_config_button_myconfig.png"));
			imageResource = new BufferedDynamicImageResource("png");
			imageResource.setImage(image);
		} catch (IOException ex) {
			LOGGER.error(null, ex);
		}
	}

	@Override
	public WebPage getPage(GenericPageBuilder pageBuilder) {
		return pageBuilder.getPage(new ConfigPanel(CONTENT_ID));
	}
	
}
