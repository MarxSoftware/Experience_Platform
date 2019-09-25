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
package com.thorstenmarx.webtools.jgroups.message;

/*-
 * #%L
 * webtools-incubator
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

import com.thorstenmarx.webtools.jgroups.message.Message;
import com.thorstenmarx.webtools.jgroups.message.MessageStateMachine;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.jmx.JmxConfigurator;
import org.jgroups.protocols.raft.ELECTION;
import org.jgroups.protocols.raft.RAFT;
import org.jgroups.protocols.raft.Role;
import org.jgroups.util.Util;

/**
 * Demos {@link MessageStateMachine}
 *
 * @author Bela Ban
 * @since 0.1
 */
public class ReplicatedMessageService extends ReceiverAdapter implements RAFT.RoleChange, MessageService {

	protected JChannel ch;
	protected MessageStateMachine rsm;
	
	private List<MessageListener> messageListeners = new CopyOnWriteArrayList<>();
	
	public ReplicatedMessageService () {
		
	}
	
	@Override
	public void publish (final Message message) {
		
	}
	
	@Override
	public void registerMessageListener (final MessageListener listener) {
		messageListeners.add(listener);
	}
	@Override
	public void unregisterMessageListener (final MessageListener listener) {
		messageListeners.remove(listener);
	}

	protected void start(String props, String name, boolean follower, long timeout) throws Exception {
		ch = new JChannel(props).name(name);
		rsm = new MessageStateMachine(ch).raftId(name).timeout(timeout);
		if (follower) {
			disableElections(ch);
		}
		ch.setReceiver(this);

		try {
			rsm.addRoleChangeListener(this);
			rsm.addNotificationListener((final Message message) -> {
				messageListeners.forEach((listener) -> listener.handle(message));
			});
			JmxConfigurator.unregisterChannel(rsm.channel(), Util.getMBeanServer(), "rsm");
			
			ch.connect("rsm");
			Util.registerChannel(rsm.channel(), "rsm");
		} finally {
			Util.close(ch);
		}
	}

	protected static void disableElections(JChannel ch) {
		ELECTION election = ch.getProtocolStack().findProtocol(ELECTION.class);
		if (election != null) {
			election.noElections(true);
		}
	}

	@Override
	public void viewAccepted(View view) {
		System.out.println("-- view change: " + view);
	}

	@Override
	public void roleChanged(Role role) {
		System.out.println("-- changed role to " + role);
	}

	public static void main(String[] args) throws Exception {
		String props = "raft_state_1.xml";
		String name = "A";
		boolean follower = false;
		long timeout = 3000;
		
		new ReplicatedMessageService().start(props, name, follower, timeout);
	}

}
