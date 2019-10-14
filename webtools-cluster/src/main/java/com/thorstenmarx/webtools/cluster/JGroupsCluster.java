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
import com.thorstenmarx.webtools.api.actions.SegmentService;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.cluster.Cluster;
import com.thorstenmarx.webtools.cluster.datalayer.ClusterDataLayer;
import com.thorstenmarx.webtools.api.datalayer.DataLayer;
import com.thorstenmarx.webtools.api.cluster.services.LockService;
import com.thorstenmarx.webtools.api.cluster.services.MessageService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.executor.ExecutionService;
import org.jgroups.jmx.JmxConfigurator;
import org.jgroups.protocols.raft.RAFT;
import org.jgroups.protocols.raft.Role;
import org.jgroups.util.Util;

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

	private final ReplicatedMessageService messageService;
	private LockService lockService;
	private ClusterDataLayer dataLayer;
	private JChannel clusterChannel;

	private ExecutorService executorService;
	
	private ExecutorService service;
	private Future<?> segmentExecutionRunner;
	
	
	private Role currentRole;

	public JGroupsCluster(final String name) {
		this.name = name;
		messageService = new ReplicatedMessageService();
	}

	@Override
	public ExecutorService getExecutorService () {
		return executorService;
	}
	
	@Override
	public MessageService getMessageService() {
		return messageService;
	}

	@Override
	public LockService getLockService() {
		return lockService;
	}

	@Override
	public DataLayer getDataLayer() {
		return dataLayer;
	}

	public void close() {
		try {
			messageService.close();
//			dataLayer.close();
			Util.close(raftChannel);
			Util.close(clusterChannel);
			
			segmentExecutionRunner.cancel(true);
			executorService.shutdownNow();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	public void start(final File configPath, final boolean follower, final long timeout, final File dataPath) throws Exception {
		raftChannel = new JChannel(new FileInputStream(new File(configPath, "jgroups_raft.xml"))).name(name);
		raftChannel.setReceiver(this);
		messageService.start(raftChannel, name, follower, timeout, new File(dataPath, "messages"));
		messageService.addRoleChangeListener(this);
		
		clusterChannel = new JChannel(new FileInputStream(new File(configPath, "jgroups_cluster.xml")));
//		clusterChannel.connect(CLUSTER_NAME + "_cluster");
//		lockService = new JGroupsLockService(clusterChannel);
//		dataLayer = new ClusterDataLayer(clusterChannel, new File(dataPath, "datalayer"));
		
//		executorService = new ExecutionService(clusterChannel);
		
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
	}
}
