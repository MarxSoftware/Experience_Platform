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

/*-
 * #%L
 * webtools-cluster
 * %%
 * Copyright (C) 2016 - 2019 WP DigitalExperience
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
public class DefaultTopic<T extends Serializable> implements MessageService.MessageListener, Topic<T> {

	final MessageService messageService;
	final String topicName;
	Topic.Receiver<T> listener;
	final Class<T> type;
	final Gson gson;
	
	protected DefaultTopic(final MessageService messageService, final String topicName, final Class<T> type) {
		this.gson = new Gson();
		this.messageService = messageService;
		this.topicName = topicName;
		this.type = type;
		messageService.registerMessageListener(this);
	}
	protected DefaultTopic(final MessageService messageService, final String topicName, final Topic.Receiver<T> listener, final Class<T> type) {
		this.gson = new Gson();
		this.messageService = messageService;
		this.topicName = topicName;
		this.listener = listener;
		this.type = type;
		messageService.registerMessageListener(this);
	}

	@Override
	public void publish (final T message) throws IOException {
		Message raw_message = new Message().setType(topicName).setPayload(gson.toJson(message));
		messageService.publish(raw_message);
	}
	
	@Override
	public void handle(final Message message) {
		if (message.getType().equals(topicName)) {
			listener.receive(gson.fromJson(message.getPayload(), type));
		}
	}

	@Override
	public void close()  {
		messageService.unregisterMessageListener(this);
	}

	@Override
	public void setListener(final Receiver<T> listener) {
		this.listener = listener;
	}
}
