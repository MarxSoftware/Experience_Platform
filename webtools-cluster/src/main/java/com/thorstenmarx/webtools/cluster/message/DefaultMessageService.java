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
package com.thorstenmarx.webtools.cluster.message;

import com.google.gson.Gson;
import com.thorstenmarx.webtools.api.cluster.Message;
import com.thorstenmarx.webtools.api.cluster.services.MessageService;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public class DefaultMessageService extends ReceiverAdapter implements MessageService  {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageService.class);
	
	private final JChannel channel;

	private final List<MessageListener> messageListeners = new CopyOnWriteArrayList<>();
	
	private final Gson gson;
	
	public DefaultMessageService(final JChannel channel) {
		this.channel = channel;
		this.channel.setReceiver(this);
		
		gson = new Gson();
	}

	@Override
	public void receive(org.jgroups.Message msg) {
		
		// ignore self sended messages
		if (channel.getAddress().equals(msg.getSrc())) {
			return;
		}
		
		final String body = msg.getObject();
		
		final Message<Address> message = gson.fromJson(body, Message.class);
		message.setSender(msg.getSrc());
		
		messageListeners.forEach((listener) -> listener.handle(message));
	}
	

	@Override
	public void publish(final Message message) throws IOException {
		final Address receiver = ((Message<Address>)message).getSender();
		final String body = gson.toJson(message);
		
		try {
			channel.send(receiver, body);
		} catch (Exception ex) {
			LOGGER.error("", ex);
			throw new IOException(ex);
		}
	}

	@Override
	public void registerMessageListener(final MessageListener listener) {
		messageListeners.add(listener);
	}

	@Override
	public void unregisterMessageListener(final MessageListener listener) {
		messageListeners.remove(listener);
	}
	
	
}
