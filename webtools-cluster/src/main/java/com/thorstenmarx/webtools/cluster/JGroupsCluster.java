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
import com.thorstenmarx.webtools.cluster.lock.JGroupsLockService;
import com.thorstenmarx.webtools.cluster.message.RAFTMessageService;
import com.thorstenmarx.webtools.api.cluster.Cluster;
import com.thorstenmarx.webtools.api.cluster.Node;
import com.thorstenmarx.webtools.api.cluster.NodeRole;
import com.thorstenmarx.webtools.cluster.datalayer.ClusterDataLayer;
import com.thorstenmarx.webtools.api.datalayer.DataLayer;
import com.thorstenmarx.webtools.api.cluster.services.LockService;
import com.thorstenmarx.webtools.api.cluster.services.MessageService;
import com.thorstenmarx.webtools.cluster.message.DefaultMessageService;
import com.thorstenmarx.webtools.cluster.message.MessageStateMachine;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.jmx.JmxConfigurator;
import org.jgroups.protocols.raft.RAFT;
import org.jgroups.protocols.raft.Role;
import org.jgroups.util.Util;
import com.thorstenmarx.webtools.api.cluster.NodeRoleChangeListener;
import java.util.ArrayList;
import org.jgroups.Address;

/**
 * Demos {@link MessageStateMachine}
 *
 * @author Bela Ban
 * @since 0.1
 */
public class JGroupsCluster extends ReceiverAdapter implements RAFT.RoleChange, Cluster {

	public static final String CLUSTER_NAME = "WEBTOOLS";
	public static final String CLUSTER_NAME_RAFT = CLUSTER_NAME + "_raft";

	protected JChannel raftChannel;
	
	public final String name;

	private final RAFTMessageService raftMessageService;
	private MessageService messageService;
	private LockService lockService;
	private JChannel clusterChannel;
	
	private Role currentRole;
	
	private List<NodeRoleChangeListener> roleChangeListeners = new CopyOnWriteArrayList<>();

	public JGroupsCluster(final String name) {
		this.name = name;
		raftMessageService = new RAFTMessageService();
	}
	
	@Override
	public void registerRoleChangeListener (final NodeRoleChangeListener roleChangeListener) {
		roleChangeListeners.add(roleChangeListener);
	}
	@Override
	public void unregisterRoleChangeListener (final NodeRoleChangeListener roleChangeListener) {
		roleChangeListeners.remove(roleChangeListener);
	}
	
	
	@Override
	public MessageService getMessageService() {
		return messageService;
	}

	@Override
	public LockService getLockService() {
		return lockService;
	}


	public void close() {
		try {
			raftMessageService.close();
			RAFT raft = raftChannel.getProtocolStack().findProtocol(RAFT.class);
			raft.log().close();
			Util.close(raftChannel);
			Util.close(clusterChannel);
		
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	public void send (final String message) throws Exception {
		clusterChannel.send(new Message(null, message));
	}

	public void start(final File configPath, final boolean follower, final long timeout, final File dataPath) throws Exception {
		raftChannel = new JChannel(new FileInputStream(new File(configPath, "jgroups_raft.xml"))).name(name);
		raftChannel.setReceiver(this);
		raftMessageService.start(raftChannel, name, follower, timeout, new File(dataPath, "messages"));
		raftMessageService.addRoleChangeListener(this);
		
		clusterChannel = new JChannel(new FileInputStream(new File(configPath, "jgroups_cluster.xml")));
		clusterChannel.connect(CLUSTER_NAME + "_cluster");
		clusterChannel.setReceiver((message) -> {
			System.out.println(message.getObject().toString());
		});
		lockService = new JGroupsLockService(clusterChannel);
		
		messageService = new DefaultMessageService(clusterChannel);
		
		try {

			JmxConfigurator.unregisterChannel(raftChannel, Util.getMBeanServer(), CLUSTER_NAME_RAFT);
			raftChannel.connect(CLUSTER_NAME_RAFT);
			Util.registerChannel(raftChannel, CLUSTER_NAME_RAFT);
		} finally {

		}
	}

	@Override
	public void viewAccepted(View view) {
		System.out.println(name + ": -- view change: " + view);
	}

	@Override
	public void roleChanged(final Role role) {
		System.out.println(name + ": -- changed role to " + role);
		if (Role.Leader.equals(role)){
			System.out.println("new leader starts actionsystem coordination");
		} else if (Role.Leader.equals(currentRole)) {
			System.out.println("old leader starts actionsystem coordination");
		}
		currentRole = role;
		
		NodeRole nodeRole = getRole();
		
		roleChangeListeners.forEach((l) -> l.roleChanged(nodeRole));
	}
	
	@Override
	public NodeRole getRole () {
		switch (this.currentRole) {
			case Candidate:
				return NodeRole.Candidate;
			case Follower: 
				return NodeRole.Follower;
			case Leader:
				return NodeRole.Leader;
			default:
				return NodeRole.UNDEFINED;
		}
		
	}

	@Override
	public MessageService getRAFTMessageService() {
		return raftMessageService;
	}

	@Override
	public List<Node<?>> getNodes() {
		final Address self = clusterChannel.getAddress();
		final List<Node<?>> nodes = new ArrayList<>();
		clusterChannel.getView().getMembers().forEach((a) -> {
			Node<Address> node = new Node<>();
			node.setAddress(a);
			node.setSelf(self.equals(a));
			node.setCoordinator(clusterChannel.getAddress().equals(clusterChannel.getView().getCoord()));
		});
		
		return nodes;
	}
}
