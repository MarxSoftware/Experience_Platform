package com.thorstenmarx.webtools.cluster.messageservice;

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
import com.google.gson.Gson;
import org.jgroups.JChannel;
import org.jgroups.protocols.raft.InternalCommand;
import org.jgroups.protocols.raft.RAFT;
import org.jgroups.protocols.raft.StateMachine;
import org.jgroups.raft.RaftHandle;
import org.jgroups.util.Bits;
import org.jgroups.util.ByteArrayDataInputStream;
import org.jgroups.util.ByteArrayDataOutputStream;
import org.jgroups.util.Util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

/**
 * A key-value store replicating its contents with RAFT via consensus
 *
 * @author Bela Ban
 * @since 0.1
 */
public class MessageStateMachine implements StateMachine {

	protected JChannel ch;
	protected RaftHandle raft;
	protected long repl_timeout = 20000; // timeout (ms) to wait for a majority to ack a write
	protected final List<Notification> listeners = new ArrayList<>();
	// Hashmap for the contents. Doesn't need to be reentrant, as updates will be applied sequentially
//	protected final Map<K, V> map = new HashMap<>();

	protected final ChronicleQueue queue;

	protected static final byte PUT = 1;

	protected Gson gson = new Gson();

	public MessageStateMachine(final JChannel ch, final File dataPath) {
		this.ch = ch;
		this.raft = new RaftHandle(this.ch, this);

		this.queue = SingleChronicleQueueBuilder.binary(dataPath).build();
	}

	public void close() {
		this.queue.close();
	}

	public void addRoleChangeListener(RAFT.RoleChange listener) {
		raft.addRoleListener(listener);
	}

	public void addNotificationListener(final Notification n) {
		if (n != null) {
			listeners.add(n);
		}
	}

	public void removeNotificationListener(final Notification n) {
		listeners.remove(n);
	}

	public void removeRoleChangeListener(RAFT.RoleChange listener) {
		raft.removeRoleListener(listener);
	}

	public MessageStateMachine timeout(long timeout) {
		this.repl_timeout = timeout;
		return this;
	}

	public int lastApplied() {
		return raft.lastApplied();
	}

	public int commitIndex() {
		return raft.commitIndex();
	}

	public JChannel channel() {
		return ch;
	}

	public void snapshot() throws Exception {
		if (raft != null) {
			raft.snapshot();
		}
	}

	public int logSize() {
		return raft != null ? raft.logSizeInBytes() : 0;
	}

	public String raftId() {
		return raft.raftId();
	}

	public MessageStateMachine raftId(String id) {
		raft.raftId(id);
		return this;
	}

	public void dumpLog() {
		raft.logEntries((entry, index) -> {
			StringBuilder sb = new StringBuilder().append(index).append(" (").append(entry.term()).append("): ");
			if (entry.command() == null) {
				sb.append("<marker record>");
				System.out.println(sb);
				return;
			}
			if (entry.internal()) {
				try {
					InternalCommand cmd = Util.streamableFromByteBuffer(InternalCommand.class,
							entry.command(), entry.offset(), entry.length());
					sb.append("[internal] ").append(cmd).append("\n");
				} catch (Exception ex) {
					sb.append("[failure reading internal cmd] ").append(ex).append("\n");
				}
				System.out.println(sb);
				return;
			}
			ByteArrayDataInputStream in = new ByteArrayDataInputStream(entry.command(), entry.offset(), entry.length());
			try {
				byte type = in.readByte();
				switch (type) {
					case PUT:
						Message key = Util.objectFromStream(in);
						sb.append("put(").append(key).append(")");
						break;
					default:
						sb.append("type " + type + " is unknown");
				}
			} catch (Throwable t) {
				sb.append(t);
			}
			System.out.println(sb);
		});
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (other.getClass() != getClass()) {
			return false;
		}
		return queue.equals(((MessageStateMachine) other).queue);
	}

	@Override
	public int hashCode() {
		return queue.hashCode();
	}

	/**
	 * Adds a key value pair to the state machine.The data is not added
	 * directly, but sent to the RAFT leader and only added to the hashmap after
	 * the change has been committed (by majority decision).The actual change
	 * will be applied with callback {@link #apply(byte[], int, int)}.
	 *
	 * @param message
	 * @throws java.lang.Exception
	 */
	public void append(final Message message) throws Exception {
		invoke(PUT, message);
	}

	///////////////////////////////////////// StateMachine callbacks /////////////////////////////////////
	@Override
	public byte[] apply(byte[] data, int offset, int length) throws Exception {
		ByteArrayDataInputStream in = new ByteArrayDataInputStream(data, offset, length);
		byte command = in.readByte();
		switch (command) {
			case PUT:
				Message message = Util.objectFromStream(in);
				notifyPut(message);
				return null;
			default:
				throw new IllegalArgumentException("command " + command + " is unknown");
		}
	}

	@Override
	public void readContentFrom(DataInput in) throws Exception {
		int size = Bits.readInt(in);
		for (int i = 0; i < size; i++) {
			Message message = Util.objectFromStream(in);
			final ExcerptAppender appender = queue.acquireAppender();
			appender.writeText(gson.toJson(message));
		}
	}

	@Override
	public void writeContentTo(DataOutput out) throws Exception {

		ExcerptTailer tailer = queue.createTailer();
		String content = null;
		while ((content = tailer.readText()) != null) {
			Message message = gson.fromJson(content, Message.class);
			Util.objectToStream(message, out);
		}
	}

	///////////////////////////////////// End of StateMachine callbacks ///////////////////////////////////
	public String toString() {
		return queue.toString();
	}

	protected void notifyPut(final Message message) {
		for (Notification n : listeners) {
			try {
				n.put(message);
			} catch (Throwable t) {
			}
		}
	}

	protected void invoke(byte command, final Message message) throws Exception {
		ByteArrayDataOutputStream out = new ByteArrayDataOutputStream(256);
		try {
			out.writeByte(command);
			Util.objectToStream(message, out);
		} catch (Exception ex) {
			throw new Exception("serialization failure (key=" + message + ")", ex);
		}

		byte[] buf = out.buffer();
		byte[] rsp = raft.set(buf, 0, out.position(), repl_timeout, TimeUnit.MILLISECONDS);
	}

	public interface Notification {

		void put(Message message);
	}
}
