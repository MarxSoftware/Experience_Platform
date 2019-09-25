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
public class ReplicatedStateMachineDemo2 extends ReplicatedStateMachineDemo {

	public static void main(String[] args) throws Exception {
		String props = "raft_state_2.xml";
		String name = "B";
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
		new ReplicatedStateMachineDemo2().start(props, name, follower, timeout);
	}

}
