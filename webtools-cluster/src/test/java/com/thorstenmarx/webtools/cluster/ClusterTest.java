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
import com.thorstenmarx.webtools.api.datalayer.Data;
import com.thorstenmarx.webtools.api.cluster.services.LockService;
import com.thorstenmarx.webtools.test.MockAnalyticsDB;
import com.thorstenmarx.webtools.test.MockEntities;
import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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

	@BeforeClass
	public void setUpClass() throws Exception {
		FileUtils.deleteDirectory(new File("c:\\entwicklung\\temp\\raft"));
		serviceA = new JGroupsCluster("A");
		serviceB = new JGroupsCluster("B");
		serviceC = new JGroupsCluster("C");

		serviceA.getMessageService().registerMessageListener((m) -> {
			counta.incrementAndGet();
		});
		serviceA.getMessageService().registerMessageListener((m) -> {
			countb.incrementAndGet();
		});
		serviceA.getMessageService().registerMessageListener((m) -> {
			countc.incrementAndGet();
		});

		serviceA.start(new File("etc/a"), false, 3000, new File("target/messages-a-" + System.currentTimeMillis()));
		serviceB.start(new File("etc/b"), false, 3000, new File("target/messages-b-" + System.currentTimeMillis()));
		serviceC.start(new File("etc/c"), false, 3000, new File("target/messages-c-" + System.currentTimeMillis()));
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
	public void test_messageservice_publish() throws Exception {

		serviceA.getMessageService().publish(new Message().setType("event").setPayload("{name:'test'}"));
		serviceB.getMessageService().publish(new Message().setType("event").setPayload("{name:'test'}"));
		serviceC.getMessageService().publish(new Message().setType("event").setPayload("{name:'test'}"));

		Thread.sleep(2000);

		Assertions.assertThat(counta.intValue()).isEqualTo(3);
		Assertions.assertThat(countb.intValue()).isEqualTo(3);
		Assertions.assertThat(countc.intValue()).isEqualTo(3);
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

	@Test(enabled = false)
	public void test_datalayer_single() throws Exception {
		serviceA.getDataLayer().add("uid", "name", new MyData("uid_name", "thats my name"));

		Thread.sleep(2000);

		Assertions.assertThat(serviceC.getDataLayer().exists("uid", "name")).isTrue();

		Optional<List<MyData>> myDataList = serviceC.getDataLayer().list("uid", "name", MyData.class);
		Assertions.assertThat(myDataList).isNotNull().isPresent();
		Assertions.assertThat(myDataList.get().size()).isEqualTo(1);
	}

	@Test(enabled = false)
	public void test_datalayer_multi() throws Exception {
		serviceA.getDataLayer().add("uid1", "name", new MyData("uid_name", "thats my name"));
		serviceA.getDataLayer().add("uid1", "name", new MyData("uid_name", "thats other data"));

		Thread.sleep(2000);

		Assertions.assertThat(serviceC.getDataLayer().exists("uid1", "name")).isTrue();

		Optional<List<MyData>> myDataList = serviceC.getDataLayer().list("uid1", "name", MyData.class);
		Assertions.assertThat(myDataList).isNotNull().isPresent();
		Assertions.assertThat(myDataList.get().size()).isEqualTo(2);
	}

	@Test(enabled = false)
	public void test_datalayer_each() throws Exception {
		serviceA.getDataLayer().add("uid1", "name1", new MyData("uid_name", "thats my name"));
		serviceA.getDataLayer().add("uid2", "name1", new MyData("uid_name", "thats other data"));
		serviceA.getDataLayer().add("uid3", "name2", new MyData("uid_name", "thats other data"));

		Thread.sleep(2000);

		Set<String> users = new HashSet<>();
		serviceC.getDataLayer().each((uid, data) -> {
			users.add(uid);
		}, "name1", MyData.class);

		Assertions.assertThat(users).hasSize(2);
		Assertions.assertThat(users).containsExactlyInAnyOrder("uid1", "uid2");
	}

	@Test(enabled = false)
	public void test_datalayer_clear() throws Exception {
		serviceA.getDataLayer().add("uid1", "name1", new MyData("uid_name", "thats my name"));
		serviceA.getDataLayer().add("uid2", "name1", new MyData("uid_name", "thats other data"));
		serviceA.getDataLayer().add("uid3", "name2", new MyData("uid_name", "thats other data"));

		Thread.sleep(2000);

		serviceA.getDataLayer().clear("name1");

		Thread.sleep(2000);

		Set<String> users = new HashSet<>();
		serviceC.getDataLayer().each((uid, data) -> {
			users.add(uid);
		}, "name1", MyData.class);

		Assertions.assertThat(users).isEmpty();
	}

	@Test(enabled = false)
	public void test_executor() throws InterruptedException, ExecutionException {
		Future<?> future = serviceA.getExecutorService().submit(MyCallable.get("eins"));
		System.out.println(future.get());
		future = serviceA.getExecutorService().submit(MyCallable.get("zwei"));
		System.out.println(future.get());
		future = serviceA.getExecutorService().submit(MyCallable.get("drei"));
		System.out.println(future.get());

	}

	public static class MyCallable implements Callable<String>, Serializable {

		public static MyCallable get(final String message) {
			return new MyCallable(message);
		}

		final String message;

		public MyCallable(final String message) {
			this.message = message;
		}

		@Override
		public String call() throws Exception {
			return message;
		}

	}

	public static class MyData implements Data, Serializable {

		public final String key;
		public final String data;

		public MyData(final String key, final String data) {
			this.key = key;
			this.data = data;
		}

		@Override
		public String getKey() {
			return key;
		}

	}

}
