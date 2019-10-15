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
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class ClusterMassTest {

	JGroupsCluster serviceA;
	JGroupsCluster serviceB;
	JGroupsCluster serviceC;

	AtomicInteger counta = new AtomicInteger(0);
	AtomicInteger countb = new AtomicInteger(0);
	AtomicInteger countc = new AtomicInteger(0);

	@BeforeMethod
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

	@AfterMethod
	public void tearDownClass() throws Exception {
		serviceA.close();
		Thread.sleep(3000);
		serviceB.close();
		Thread.sleep(3000);
		serviceC.close();
		Thread.sleep(3000);
	}

	@DataProvider(name = "message_count")
	public static Object[][] message_count() {
		return new Object[][]{{10000}};
	}

	@Test(dataProvider = "message_count")
	public void test_mass_publish(final int count) throws Exception {

		for (int i = 0; i < count; i++) {
			serviceA.getMessageService().publish(new Message().setType("event").setPayload("{name:'test'}"));
		}

		Thread.sleep(2000);

		Assertions.assertThat(counta.intValue()).isEqualTo(count);
		Assertions.assertThat(countb.intValue()).isEqualTo(count);
		Assertions.assertThat(countc.intValue()).isEqualTo(count);
	}

}
