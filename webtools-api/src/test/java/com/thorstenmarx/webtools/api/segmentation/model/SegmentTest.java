package com.thorstenmarx.webtools.api.segmentation.model;

/*-
 * #%L
 * webtools-api
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
import com.alibaba.fastjson.JSONObject;
import com.thorstenmarx.webtools.api.TimeWindow;
import com.thorstenmarx.webtools.api.actions.model.Segment;
import com.thorstenmarx.webtools.api.actions.model.rules.EventRule;
import com.thorstenmarx.webtools.api.actions.model.rules.PageViewRule;
import com.thorstenmarx.webtools.api.actions.model.rules.ScoreRule;
import static org.assertj.core.api.Assertions.*;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class SegmentTest {
	
	/**
	 * Test of rules method, of class Segment.
	 */
	@Test
	public void testSimple() {
	
		Segment segment = new Segment();
		segment.setId("1");
		segment.setName("Ein name");
		
		JSONObject result = segment.toJson();
		
		Segment testling = Segment.fromJson(result);
		
		assertThat(testling).isNotNull();
		assertThat(testling.getName()).isEqualTo("Ein name");
		assertThat(testling.getId()).isEqualTo("1");
	}
	
	@Test 
	public void testScoreRule () {
		Segment segment = new Segment();
		segment.setId("1");
		segment.setName("Ein name");
		segment.start(new TimeWindow(TimeWindow.UNIT.MINUTE, 1));
		
		ScoreRule rule = new ScoreRule();
		rule.id("id");
		rule.name("name");
		
		rule.score(50);
		segment.addRule(rule);
		
		JSONObject result = segment.toJson();
		
		Segment testling = Segment.fromJson(result);
		
		assertThat(testling.rules().isEmpty()).isFalse();
		assertThat(testling.rules()).hasSize(1);
		assertThat(testling.startTimeWindow().getCount()).isEqualTo(1);
		assertThat(testling.startTimeWindow().getUnit()).isEqualTo(TimeWindow.UNIT.MINUTE);
		ScoreRule scoreRule = (ScoreRule) testling.rules().iterator().next();
		assertThat(scoreRule.id()).isEqualTo("id");
		assertThat(scoreRule.name()).isEqualTo("name");
		assertThat(scoreRule.score()).isEqualTo(50);
	}
	
	@Test 
	public void testEventRule () {
		Segment segment = new Segment();
		segment.setId("1");
		segment.setName("Ein name");
		segment.start(new TimeWindow(TimeWindow.UNIT.MINUTE, 1));
		
		EventRule rule = new EventRule();
		rule.id("id");
		rule.site("site");
		rule.event("event");
		segment.addRule(rule);
		
		JSONObject result = segment.toJson();
		
		Segment testling = Segment.fromJson(result);
		
		assertThat(testling.rules().isEmpty()).isFalse();
		assertThat(testling.rules()).hasSize(1);
		assertThat(testling.startTimeWindow().getCount()).isEqualTo(1);
		assertThat(testling.startTimeWindow().getUnit()).isEqualTo(TimeWindow.UNIT.MINUTE);
		EventRule scoreRule = (EventRule) testling.rules().iterator().next();
		assertThat(scoreRule.id()).isEqualTo("id");
		assertThat(scoreRule.event()).isEqualTo("event");
		assertThat(scoreRule.site()).isEqualTo("site");

	}
	
	@Test 
	public void testPageviewRule () {
		Segment segment = new Segment();
		segment.setId("1");
		segment.setName("Ein name");
		segment.start(new TimeWindow(TimeWindow.UNIT.MINUTE, 1));
		
		PageViewRule rule = new PageViewRule();
		rule.id("id");
		rule.site("site");
		rule.page("page");
		rule.count(10);
		segment.addRule(rule);
		
		JSONObject result = segment.toJson();
		
		Segment testling = Segment.fromJson(result);
		
		assertThat(testling.rules().isEmpty()).isFalse();
		assertThat(testling.rules()).hasSize(1);
		assertThat(testling.startTimeWindow().getCount()).isEqualTo(1);
		assertThat(testling.startTimeWindow().getUnit()).isEqualTo(TimeWindow.UNIT.MINUTE);
		PageViewRule scoreRule = (PageViewRule) testling.rules().iterator().next();
		assertThat(scoreRule.id()).isEqualTo("id");
		assertThat(scoreRule.page()).isEqualTo("page");
		assertThat(scoreRule.site()).isEqualTo("site");
		assertThat(scoreRule.count()).isEqualTo(10);
	}
}
