package com.thorstenmarx.webtools.actions.segmentation.dsl;

/*-
 * #%L
 * webtools-actions
 * %%
 * Copyright (C) 2016 - 2018 Thorsten Marx
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
import com.thorstenmarx.webtools.actions.segmentation.mocks.MockedExecutor;
import com.thorstenmarx.webtools.actions.segmentation.mocks.MockDataLayer;
import com.thorstenmarx.webtools.actions.segmentation.*;
import com.alibaba.fastjson.JSONObject;
import com.thorstenmarx.webtools.actions.ActionSystem;
import com.thorstenmarx.webtools.actions.TestHelper;
import com.thorstenmarx.webtools.actions.segmentation.mocks.MockAnalyticsDB;
import com.thorstenmarx.webtools.api.TimeWindow;
import com.thorstenmarx.webtools.api.datalayer.SegmentData;
import com.thorstenmarx.webtools.api.actions.SegmentService;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.analytics.Fields;
import com.thorstenmarx.webtools.base.Configuration;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.Set;
import java.util.UUID;
import net.engio.mbassy.bus.MBassador;

/**
 *
 * @author thmarx
 */
public class CampaignRuleTest extends AbstractTest {

	AnalyticsDB analytics;
	ActionSystem actionSystem;
	SegmentService service;
	MockedExecutor executor;
	MockDataLayer datalayer;
	
	private String twitter_id;
	private String facebook_id;

	@BeforeClass
	public void setUpClass() {
		long timestamp = System.currentTimeMillis();

		Configuration config = Configuration.empty();
		config.put("data", "dir", "target/CampaignRuleTest-" + timestamp);

		MBassador mbassador = new MBassador();
		executor = new MockedExecutor();
		
		analytics = new MockAnalyticsDB();
		

		service = new EntitiesSegmentService(entities());

		twitter_id = createSegment(service, "Twitter Test", new TimeWindow(TimeWindow.UNIT.YEAR, 1), "segment().and(rule(CAMPAIGN).source('twitter').medium('tweet').campaign('test'))");
		
		facebook_id = createSegment(service, "Facebook demo", new TimeWindow(TimeWindow.UNIT.YEAR, 1), "segment().and(rule(CAMPAIGN).source('facebook').medium('post').campaign('demo'))");

		System.out.println("service: " + service.all());
		
		datalayer = new MockDataLayer();
		
		actionSystem = new ActionSystem(analytics, service, config, null, mbassador, datalayer, executor);
		actionSystem.start();
	}

	@AfterClass()
	public void tearDownClass() throws InterruptedException, Exception {
		actionSystem.close();
	}

	@BeforeMethod
	public void setUp() {
	}

	@AfterMethod
	public void tearDown() {
	}

	/**
	 * Test of open method, of class AnalyticsDb.
	 *
	 * @throws java.lang.Exception
	 */
	@Test(invocationCount = 1, enabled = true)
	public void test_campaign_rule() throws Exception {

		System.out.println("test_campaign_rule");
		
		final String USER_ID = "user " + UUID.randomUUID().toString();

		
		analytics.track(TestHelper.event(TestHelper.event_data(USER_ID), new JSONObject()));
		
		await(datalayer, USER_ID, 1);
		
		List<SegmentData> data = datalayer.list(USER_ID, SegmentData.KEY, SegmentData.class).get();
		assertThat(data).isNotEmpty();

		Set<String> segments = data.get(0).getSegments();

		assertThat(segments).isNotNull();
		assertThat(segments).containsExactly(twitter_id);
		
		JSONObject event = TestHelper.event_data(USER_ID);
		event.put(Fields.VisitId.value(), UUID.randomUUID().toString());
		event.put(Fields.Referrer.combine("header"), "https://heise.de/?utm_source=facebook&utm_medium=post&utm_campaign=demo");
		
		analytics.track(TestHelper.event(event, new JSONObject()));
						
		datalayer.remove(USER_ID, SegmentData.KEY);

		await(datalayer, USER_ID, 2);
				
		data = datalayer.list(USER_ID, SegmentData.KEY, SegmentData.class).get();
		assertThat(data).isNotEmpty();
		
		
		segments = getRawSegments(data);
		assertThat(segments).isNotNull();
		assertThat(segments).containsExactlyInAnyOrder(twitter_id, facebook_id);
	}
}
