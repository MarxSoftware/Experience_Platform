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
import com.thorstenmarx.webtools.api.cluster.services.LockService;
import com.thorstenmarx.webtools.api.cluster.services.MessageService;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class ClusterTest {

	JGroupsCluster serviceA;
	JGroupsCluster serviceB;
	JGroupsCluster serviceC;

	AtomicInteger counta = new AtomicInteger(0);
	AtomicInteger countb = new AtomicInteger(0);
	AtomicInteger countc = new AtomicInteger(0);
	AtomicInteger node_a_count = new AtomicInteger(0);
	AtomicInteger node_b_count = new AtomicInteger(0);
	AtomicInteger node_c_count = new AtomicInteger(0);

	@BeforeMethod
	public void reset () {
		counta.set(0);
		countb.set(0);
		countc.set(0);
		
		node_a_count.set(0);
		node_b_count.set(0);
		node_c_count.set(0);
	}
	
	@BeforeClass
	public void setUpClass() throws Exception {
		FileUtils.deleteDirectory(new File("c:\\entwicklung\\temp\\raft"));
		serviceA = new JGroupsCluster("A");
		serviceB = new JGroupsCluster("B");
		serviceC = new JGroupsCluster("C");

		serviceA.start(new File("etc/a"), false, 3000, new File("target/messages-a-" + System.currentTimeMillis()));
		serviceB.start(new File("etc/b"), false, 3000, new File("target/messages-b-" + System.currentTimeMillis()));
		serviceC.start(new File("etc/c"), false, 3000, new File("target/messages-c-" + System.currentTimeMillis()));
		
		serviceA.getRAFTMessageService().registerMessageListener((m) -> {
			counta.incrementAndGet();
		});
		serviceB.getRAFTMessageService().registerMessageListener((m) -> {
			countb.incrementAndGet();
		});
		serviceC.getRAFTMessageService().registerMessageListener((m) -> {
			countc.incrementAndGet();
		});
		serviceA.getMessageService().registerMessageListener((m) -> {
			node_a_count.incrementAndGet();
		});
		serviceB.getMessageService().registerMessageListener((m) -> {
			node_b_count.incrementAndGet();
		});
		serviceC.getMessageService().registerMessageListener((m) -> {
			node_c_count.incrementAndGet();
		});
	}

	@AfterClass
	public void tearDownClass() throws Exception {
		serviceA.close();
		Thread.sleep(3000);
		serviceB.close();
		Thread.sleep(3000);
		serviceC.close();
	}

	@Test
	public void test_send() throws Exception {

		serviceA.send("Hallo Leute");
		Thread.sleep(2000);
	}

	@Test
	public void test_raft_messageservice_publish() throws Exception {

		serviceA.getRAFTMessageService().publish(new Message().setType("event").setPayload("{name:'test'}"));
		serviceB.getRAFTMessageService().publish(new Message().setType("event").setPayload("{name:'test'}"));
		serviceC.getRAFTMessageService().publish(new Message().setType("event").setPayload("{name:'test'}"));

		Thread.sleep(2000);

		Assertions.assertThat(counta.intValue()).isEqualTo(3);
		Assertions.assertThat(countb.intValue()).isEqualTo(3);
		Assertions.assertThat(countc.intValue()).isEqualTo(3);
	}
	
	@Test
	public void test_messageservice_publish() throws Exception {

		serviceA.getMessageService().publish(new Message().setType("event").setPayload("{name:'test'}"));
		serviceB.getMessageService().publish(new Message().setType("event").setPayload("{name:'test'}"));

		Thread.sleep(2000);

		Assertions.assertThat(node_a_count.intValue()).isEqualTo(1);
		Assertions.assertThat(node_b_count.intValue()).isEqualTo(1);
		Assertions.assertThat(node_c_count.intValue()).isEqualTo(2);
	}
	
	@Test
	public void test_messageservice_response () throws Exception {
		MessageService.MessageListener listener = (message) -> {
			Message m = new Message().setType("response").setSender(message.getSender());
			try {
				serviceC.getMessageService().publish(m);
				serviceC.getMessageService().publish(m);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		};
		
		serviceC.getMessageService().registerMessageListener(listener);
		serviceA.getMessageService().publish(new Message().setType("event").setPayload("{name:'test'}"));
		Thread.sleep(2000);
		serviceC.getMessageService().unregisterMessageListener(listener);
		
		Thread.sleep(2000);
		
		Assertions.assertThat(node_a_count.intValue()).isEqualTo(2);
		Assertions.assertThat(node_b_count.intValue()).isEqualTo(1);
		Assertions.assertThat(node_c_count.intValue()).isEqualTo(1);
	}

	@Test
	public void test_lockservice_lock() throws Exception {

		LockService.Lock lock = serviceA.getLockService().getLock("test");
		LockService.Lock lock_b = serviceB.getLockService().getLock("test");
		LockService.Lock lock_c = serviceC.getLockService().getLock("test");

		Assertions.assertThat(lock).isNotNull();

		lock.lock();
		Thread.sleep(1000);
		try {
			Assertions.assertThat(lock_b.tryLock()).isEqualTo(false);
			Assertions.assertThat(lock_c.tryLock()).isEqualTo(false);
		} finally {
			lock.unlock();
		}

		Assertions.assertThat(lock_c.tryLock()).isEqualTo(true);
	}

}
