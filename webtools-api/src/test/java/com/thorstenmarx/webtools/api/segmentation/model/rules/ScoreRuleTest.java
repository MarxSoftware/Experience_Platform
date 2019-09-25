package com.thorstenmarx.webtools.api.segmentation.model.rules;

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
import com.thorstenmarx.webtools.api.actions.model.rules.ScoreRule;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;


/**
 *
 * @author marx
 */
public class ScoreRuleTest {
	
	@Test
	public void test_score_rule_does_not_match_50() {
		ScoreRule rule = new ScoreRule();
		rule.name("score").score(100);
		
		Map<String, Integer> values = new HashMap<>();
		values.put("score", 50);
		Assertions.assertThat(rule.match(values)).isFalse();
	}
	@Test
	public void test_score_rule_does_match_100() {
		ScoreRule rule = new ScoreRule();
		rule.name("score").score(100);
		
		Map<String, Integer> values = new HashMap<>();
		values.put("score", 100);
		Assertions.assertThat(rule.match(values)).isTrue();
	}
	
	@Test
	public void test_score_rule_does_match_101() {
		ScoreRule rule = new ScoreRule();
		rule.name("score").score(100);
		
		Map<String, Integer> values = new HashMap<>();
		values.put("score", 101);
		Assertions.assertThat(rule.match(values)).isTrue();
	}
	
}
