/*
 * Copyright (C) 2019 Thorsten Marx
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
package com.thorstenmarx.webtools.api.cluster;

/*-
 * #%L
 * webtools-api
 * %%
 * Copyright (C) 2016 - 2019 Thorsten Marx
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
import com.thorstenmarx.webtools.api.annotations.API;
import com.thorstenmarx.webtools.api.cluster.services.LockService;
import com.thorstenmarx.webtools.api.cluster.services.MessageReplicator;
import com.thorstenmarx.webtools.api.cluster.services.MessageService;
import com.thorstenmarx.webtools.api.cluster.services.Topic;
import com.thorstenmarx.webtools.api.execution.Executor;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author marx
 */
@API(since = "3.1.0", status = API.Status.Experimental)
public interface Cluster {
	
	void connect () throws Exception;
	
	void close () throws Exception;

	List<Node<?>> getNodes();

	LockService getLockService();

	/**
	 * The MessageService published Messages to all cluster members.
	 * The member that sends the meswsage will not call the local message listener.
	 * 
	 * @return 
	 */
	MessageService getMessageService();

	MessageService getRAFTMessageService();

	<T extends Serializable> Topic<T> createTopic(final String name, final Topic.Receiver<T> listener, final Class<T> type);

	<T extends Serializable> MessageReplicator<T> createReplicator(final String topicName, final Executor executor, final MessageReplicator.Handler<T> handler, final Class<T> type);

	void registerRoleChangeListener(final NodeRoleChangeListener roleChangeListener);

	void unregisterRoleChangeListener(final NodeRoleChangeListener roleChangeListener);

	NodeRole getRole();
}
