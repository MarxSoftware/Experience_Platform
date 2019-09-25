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
package com.thorstenmarx.webtools.cluster;

/*-
 * #%L
 * webtools-cluster
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
import com.thorstenmarx.webtools.api.cluster.Message;
import com.thorstenmarx.webtools.api.cluster.services.MessageService;
import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.JChannel;
import org.jgroups.protocols.raft.ELECTION;
import org.jgroups.protocols.raft.RAFT;

/**
 * Demos {@link MessageStateMachine}
 *
 * @author Bela Ban
 * @since 0.1
 */
public class ReplicatedMessageService implements MessageService {

	protected JChannel ch;
	protected MessageStateMachine rsm;

	private final List<MessageListener> messageListeners = new CopyOnWriteArrayList<>();

	public ReplicatedMessageService() {
	}

	@Override
	public void publish(final Message message) {
		try {
			rsm.append(message);
		} catch (Exception ex) {
			Logger.getLogger(ReplicatedMessageService.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void close() {
		rsm.close();
	}
	
	public void addRoleChangeListener (final RAFT.RoleChange listener) {
		rsm.addRoleChangeListener(listener);
	}

	@Override
	public void registerMessageListener(final MessageListener listener) {
		messageListeners.add(listener);
	}

	@Override
	public void unregisterMessageListener(final MessageListener listener) {
		messageListeners.remove(listener);
	}

	protected void start(final JChannel channel, String name, boolean follower, long timeout, final File dataPath) throws Exception {
		ch = channel;
		rsm = new MessageStateMachine(ch, dataPath).raftId(name).timeout(timeout);
		if (follower) {
			disableElections(ch);
		}
		rsm.addNotificationListener((final Message message) -> {
			messageListeners.forEach((listener) -> listener.handle(message));
		});
	}

	protected static void disableElections(JChannel ch) {
		ELECTION election = ch.getProtocolStack().findProtocol(ELECTION.class);
		if (election != null) {
			election.noElections(true);
		}
	}
}
