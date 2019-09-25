package com.thorstenmarx.webtools.actions.dsl.rhino;

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
import com.thorstenmarx.webtools.actions.dsl.*;
import com.thorstenmarx.webtools.api.actions.Conditional;
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.actions.dsl.rules.CampaignRule;
import com.thorstenmarx.webtools.actions.dsl.rules.CategoryRule;
import com.thorstenmarx.webtools.actions.dsl.rules.EventRule;
import com.thorstenmarx.webtools.actions.dsl.rules.FirstVisitRule;
import com.thorstenmarx.webtools.actions.dsl.rules.KeyValueRule;
import com.thorstenmarx.webtools.actions.dsl.rules.PageViewRule;
import com.thorstenmarx.webtools.actions.dsl.rules.ReferrerRule;
import com.thorstenmarx.webtools.actions.dsl.rules.ScoreRule;
import com.thorstenmarx.webtools.scripting.RhinoScripting;
import com.thorstenmarx.webtools.scripting.rhino.function.AnnotatedScriptableObject;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.script.ScriptException;
import net.engio.mbassy.bus.MBassador;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public class RhinoDSL {

	private static final Logger log = LoggerFactory.getLogger(RhinoDSL.class);

	private final ModuleManager moduleManager;
	private final MBassador eventBus;

	private final RhinoScripting engine;

	public RhinoDSL(final ModuleManager moduleManager, final MBassador eventBus) {
		this.moduleManager = moduleManager;
		this.eventBus = eventBus;
		engine = new RhinoScripting("/com/thorstenmarx/webtools/actions/dsl/rhino/modules");
	}

//	private void initExtensions(final Scriptable context, final ConcurrentMap<String, Supplier<Conditional>> rules) {
//		moduleManager.extensions(SegmentationRuleExtension.class).forEach(sr -> {
//			rules.put(sr.getKey(), sr.getRule());
//			context.put(sr.getKey(), sr.getKey());
//		});
//	}

	public EventAction buildAction(final String event, final String content) throws ScriptException {
		ConcurrentMap<String, Supplier<Conditional>> rules = createRules();

		final EventAction eventAction = new EventAction(event, eventBus);
		engine.eval(content, (Scriptable context) -> {
			context.put(PageViewRule.RULE, context, PageViewRule.RULE);
			context.put(ScoreRule.RULE, context, ScoreRule.RULE);
			context.put(EventRule.RULE, context, EventRule.RULE);
			context.put(KeyValueRule.RULE, context, KeyValueRule.RULE);
			context.put(FirstVisitRule.RULE, context, FirstVisitRule.RULE);
			context.put(CampaignRule.RULE, context, CampaignRule.RULE);
			context.put(ReferrerRule.RULE, context, ReferrerRule.RULE);
			context.put(CategoryRule.RULE, context, CategoryRule.RULE);
			new Functions(eventAction, rules, eventBus).addToScope(context);
			new TimeFunctions().addToScope(context);

//			if (moduleManager != null) {
//				initExtensions(context, rules);
//			}
		});

		return eventAction;
	}

	public DSLSegment build(final String content) throws ScriptException {

		ConcurrentMap<String, Supplier<Conditional>> rules = createRules();

//		if (moduleManager != null) {
//			initExtensions(engine, rules);
//		}
		final DSLSegment segment = new DSLSegment();
		engine.eval(content, (Scriptable context) -> {
			context.put(PageViewRule.RULE, context, PageViewRule.RULE);
			context.put(ScoreRule.RULE, context, ScoreRule.RULE);
			context.put(EventRule.RULE, context, EventRule.RULE);
			context.put(KeyValueRule.RULE, context, KeyValueRule.RULE);
			context.put(FirstVisitRule.RULE, context, FirstVisitRule.RULE);
			context.put(CampaignRule.RULE, context, CampaignRule.RULE);
			context.put(ReferrerRule.RULE, context, ReferrerRule.RULE);
			context.put(CategoryRule.RULE, context, CategoryRule.RULE);
			new Functions(segment, rules, eventBus).addToScope(context);
			new TimeFunctions().addToScope(context);
		});

		return segment;
	}

	protected ConcurrentMap<String, Supplier<Conditional>> createRules() {
		ConcurrentMap<String, Supplier<Conditional>> rules = new ConcurrentHashMap<>();
		rules.put(PageViewRule.RULE, () -> new PageViewRule());
		rules.put(ScoreRule.RULE, () -> new ScoreRule());
		rules.put(EventRule.RULE, () -> new EventRule());
		rules.put(KeyValueRule.RULE, () -> new KeyValueRule());
		rules.put(FirstVisitRule.RULE, () -> new FirstVisitRule());
		rules.put(CampaignRule.RULE, () -> new CampaignRule());
		rules.put(ReferrerRule.RULE, () -> new ReferrerRule());
		rules.put(CategoryRule.RULE, () -> new CategoryRule());
		return rules;
	}

	public static class TimeFunctions extends AnnotatedScriptableObject {

		@Expose
		public long minutes(final int minutes) {
			return TimeUnit.MINUTES.toMillis(minutes);
		}

		@Expose
		public long hours(final int hours) {
			return TimeUnit.HOURS.toMillis(hours);
		}

		@Expose
		public long days(final int days) {
			return TimeUnit.DAYS.toMillis(days);
		}

		@Expose
		public long weeks(final int weeks) {
			return weeks * TimeUnit.DAYS.toMillis(7);
		}

		@Expose
		public long month(final int months) {
			return months * TimeUnit.HOURS.toMillis(31);
		}

		@Expose
		public long years(final int years) {
			return years * TimeUnit.DAYS.toMillis(365);
		}
	}

	public static class Functions extends AnnotatedScriptableObject {

		final DSLSegment segment;
		final ConcurrentMap<String, Supplier<Conditional>> rules;
		final MBassador eventBus;
		final EventAction eventAction;

		public Functions(final DSLSegment segment, final ConcurrentMap<String, Supplier<Conditional>> rules, final MBassador eventBus) {
			this.eventAction = null;
			this.segment = segment;
			this.rules = rules;
			this.eventBus = eventBus;
		}

		public Functions(final EventAction eventAction, final ConcurrentMap<String, Supplier<Conditional>> rules, final MBassador eventBus) {
			this.segment = null;
			this.rules = rules;
			this.eventBus = eventBus;
			this.eventAction = eventAction;
		}

		@Expose
		public DSLSegment segment() {
			return segment;
		}

		@Expose
		public EventAction eventAction() {
			return eventAction;
		}

		@Expose
		public Conditional rule(final String name) {
			if (rules.containsKey(name)) {
				return rules.get(name).get();
			}
			return null;
		}

		@Expose
		public static Conditional and(Context cx, Object[] conditionals, org.mozilla.javascript.Function ctorObj, boolean inNewExpr) {
			return new AND(toConditionals(conditionals));
		}

		@Expose
		public static Conditional or(Context cx, Object[] conditionals, org.mozilla.javascript.Function ctorObj, boolean inNewExpr) {
			return new OR(toConditionals(conditionals));
		}

		@Expose
		public static Conditional not(Context cx, Object[] conditionals, org.mozilla.javascript.Function ctorObj, boolean inNewExpr) {
			return new NOT(toConditionals(conditionals));
		}

		private static Conditional[] toConditionals(final Object[] args) {
			List<Conditional> conditionals = Stream.of(args).map((Object t) -> (NativeJavaObject) t).map((jno) -> (Conditional) jno.unwrap()).collect(Collectors.toList());
			Conditional[] con = {};
			return conditionals.toArray(con);
		}
	}

	public static void main(final String... args) throws ScriptException {

		StringBuilder sb = new StringBuilder();
		sb.append("var arule = rule(PAGEVIEW).page('page');");
		sb.append("var or1 = or(arule, arule, arule);");
		sb.append("minutes(5);");
		sb.append("segment().and(or1, or1);");
		DSLSegment seg = new RhinoDSL(null, null).build(sb.toString());
		System.out.println(seg.toString());
	}
}
