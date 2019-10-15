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
package com.thorstenmarx.webtools.cluster;

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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class JGroupsTest {

	@Test(enabled = true)
	public void simple() throws Exception {

		try (org.jgroups.JChannel channel = new JChannel("udp.xml")) {
			channel.setReceiver(new ReceiverAdapter() {
				@Override
				public void receive(Message msg) {
					System.out.println("received msg from " + msg.getSrc() + ": " + msg.getObject());
				}
			});
			channel.connect("MyCluster");
			channel.send(new Message(null, "hello world"));

			Thread.sleep(2000);
		}
	}

	@Test(enabled = false)
	public void multisocket_error() throws IOException {
		InetSocketAddress isa = new InetSocketAddress("239.255.0.113", 1234);

		MulticastSocket mcs = new MulticastSocket(isa);

	}

	@Test(enabled = true)
	public void multisocket_ok() throws IOException {

		InetAddress ia = InetAddress.getByName("239.255.0.113");

		MulticastSocket mcs = new MulticastSocket(1234);
		mcs.joinGroup(ia);

	}

}
