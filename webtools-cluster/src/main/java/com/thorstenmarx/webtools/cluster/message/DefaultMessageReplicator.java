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
import com.thorstenmarx.webtools.api.execution.Executor;
import com.thorstenmarx.webtools.cluster.Topic;
import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 * @param <T>
 */
public class DefaultMessageReplicator<T extends Serializable> implements Topic.Receiver<DefaultMessageReplicator.ReplicationMessage> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageReplicator.class);
	
	private final String name;
	private final Set<String> members;
	private final Gson gson;
	private final ConcurrentMap<String, ReplicationMessage> messages;
	private final Topic<ReplicationMessage> topic;
	private final Class<T> type;
	private final Topic.Handler<T> handler;

	public DefaultMessageReplicator(final String name, final Set<String> members, final Topic<ReplicationMessage> topic, final Executor executor, final Topic.Handler<T> handler, final Class<T> type) {
		this.messages = new ConcurrentHashMap<>();
		this.name = name;
		this.members = members;
		this.topic = topic;
		this.gson = new Gson();
		this.type = type;
		this.handler = handler;
		topic.setListener(this::receive);
		
		executor.scheduleFixedDelay(this::updateMembers, 1, 1, TimeUnit.MINUTES);
	}
	
	public void updateMembers () {
		messages.values().forEach((message) -> {
			try {
				topic.publish(message);
			} catch (IOException ex) {
				LOGGER.error("", ex);
			}
		});
	}
	
	@Override
	public void receive(final ReplicationMessage message) {
		if (name.equals(message.target)) {
			if (message.commited) { // commit message from other member
				messages.remove(message.uuid);
			} else {
				try {
					// handle message
					handler.handle(gson.fromJson(message.message, type));
					
					// send commit message
					final ReplicationMessage commited_message = new ReplicationMessage();
					commited_message.uuid = message.uuid;
					commited_message.commited = true;
					commited_message.source = name;
					commited_message.target = message.source;
					
					topic.publish(commited_message);
				} catch (IOException ex) {
					LOGGER.error("", ex);
				}
			}
		}
	}
	
	public void replicate (final T message) {
		members.stream().map((m) -> {
			ReplicationMessage replication_message = new ReplicationMessage();
			replication_message.uuid = UUID.randomUUID().toString();
			replication_message.message = gson.toJson(message);
			replication_message.source = name;
			replication_message.target = m;
			return replication_message;
		}).forEach((m) -> {
			messages.put(m.uuid, m);
		});
	}
	
	public static class ReplicationMessage implements Serializable{
		public String uuid;
		public String source;
		public String target;
		public String message;
		public boolean commited = false;
	}
}
