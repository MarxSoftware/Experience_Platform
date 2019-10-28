/*
 * Copyright (C) 2019 WP DigitalExperience
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
package com.thorstenmarx.webtools.cluster;

import com.thorstenmarx.webtools.api.cluster.Message;
import com.thorstenmarx.webtools.api.cluster.services.MessageService;

/**
 *
 * @author marx
 */
public class DefaultTopic implements MessageService.MessageListener {

	final MessageService messageService;
	final String topicName;
	final MessageService.MessageListener listener;

	protected DefaultTopic(final MessageService messageService, final String topicName, final MessageService.MessageListener listener) {
		this.messageService = messageService;
		this.topicName = topicName;
		this.listener = listener;
		messageService.registerMessageListener(this);
	}

	@Override
	public void handle(final Message message) {
		if (message.getType().equals(topicName)) {
			listener.handle(message);
		}
	}

	public void close()  {
		messageService.unregisterMessageListener(this);
	}
	
	
}
