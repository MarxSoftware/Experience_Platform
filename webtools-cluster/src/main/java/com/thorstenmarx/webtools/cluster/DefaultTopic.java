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

import com.google.gson.Gson;
import com.thorstenmarx.webtools.api.cluster.Message;
import com.thorstenmarx.webtools.api.cluster.services.MessageService;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author marx
 * @param <T>
 */
public class DefaultTopic<T extends Serializable> implements MessageService.MessageListener {

	final MessageService messageService;
	final String topicName;
	final TopicListener<T> listener;
	final Class<T> type;
	final Gson gson;
	
	protected DefaultTopic(final MessageService messageService, final String topicName, final TopicListener<T> listener, final Class<T> type) {
		this.gson = new Gson();
		this.messageService = messageService;
		this.topicName = topicName;
		this.listener = listener;
		this.type = type;
		messageService.registerMessageListener(this);
	}

	public void publish (final T message) throws IOException {
		Message raw_message = new Message().setType(topicName).setPayload(gson.toJson(message));
		messageService.publish(raw_message);
	}
	
	@Override
	public void handle(final Message message) {
		if (message.getType().equals(topicName)) {
			listener.handle(gson.fromJson(message.getPayload(), type));
		}
	}

	public void close()  {
		messageService.unregisterMessageListener(this);
	}
	
	public static interface TopicListener<T extends Serializable> {
		public void handle (T message);
	}
}
