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
package com.thorstenmarx.webtools.jgroups.message.demo;

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
public class ReplicatedStateMachineDemo extends ReceiverAdapter implements RAFT.RoleChange {

	protected JChannel ch;
	protected MessageStateMachine rsm;

	protected void start(String props, String name, boolean follower, long timeout) throws Exception {
		ch = new JChannel(props).name(name);
		rsm = new MessageStateMachine(ch).raftId(name).timeout(timeout);
		if (follower) {
			disableElections(ch);
		}
		ch.setReceiver(this);

		try {
			rsm.addRoleChangeListener(this);
			rsm.addNotificationListener(new MessageStateMachine.Notification() {
				@Override
				public void put(final Message message) {
					System.out.printf("-- put -> %s\n", message);
				}

			});
			ch.connect("rsm");
			Util.registerChannel(rsm.channel(), "rsm");
			loop();
			JmxConfigurator.unregisterChannel(rsm.channel(), Util.getMBeanServer(), "rsm");
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

	protected void loop() {
		boolean looping = true;
		while (looping) {
			int input = Util.keyPress("[1] add [2] get [3] remove [4] show all [5] dump log [6] snapshot [7] put N [x] exit\n"
					+ "first-applied=" + firstApplied()
					+ ", last-applied=" + rsm.lastApplied()
					+ ", commit-index=" + rsm.commitIndex()
					+ ", log size=" + Util.printBytes(logSize()) + "\n");
			switch (input) {
				case '1':
					put(new Message().setPayload("{name: 'thorsten'}").setType("event"));
					break;
				case '4':
					System.out.println(rsm + "\n");
					break;
				case '5':
					dumpLog();
					break;
				case '6':
					try {
						rsm.snapshot();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case 'x':
					looping = false;
					break;
			}
		}
	}

	protected void put(final Message message) {
		
		try {
			rsm.append(message);
		} catch (Throwable t) {
			System.err.println("failed setting " + message + ": " + t);
		}
	}


	protected static String read(String name) {
		try {
			return Util.readStringFromStdin(name + ": ");
		} catch (Exception e) {
			return null;
		}
	}

	protected int firstApplied() {
		RAFT raft = rsm.channel().getProtocolStack().findProtocol(RAFT.class);
		return raft.log().firstAppended();
	}

	protected int logSize() {
		return rsm.logSize();
	}

	protected void dumpLog() {
		System.out.println("\nindex (term): command\n---------------------");
		rsm.dumpLog();
		System.out.println("");
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

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-props")) {
				props = args[++i];
				continue;
			}
			if (args[i].equals("-name")) {
				name = args[++i];
				continue;
			}
			if (args[i].equals("-follower")) {
				follower = true;
				continue;
			}
			if (args[i].equals("-timeout")) {
				timeout = Long.parseLong(args[++i]);
				continue;
			}
			System.out.println("ReplicatedStateMachine [-props <config>] [-name <name>] [-follower] [-timeout timeout]");
			return;
		}
		new ReplicatedStateMachineDemo().start(props, name, follower, timeout);
	}

}
