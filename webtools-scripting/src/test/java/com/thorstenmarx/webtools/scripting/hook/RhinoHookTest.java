package com.thorstenmarx.webtools.scripting.hook;

/*-
 * #%L
 * webtools-scripting
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

import com.thorstenmarx.webtools.scripting.*;
import com.thorstenmarx.webtools.scripting.rhino.function.AnnotatedScriptableObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.script.Bindings;
import javax.script.ScriptContext;
import org.junit.jupiter.api.Test;


/**
 *
 * @author marx
 */
public class RhinoHookTest {
	
	@Test
	public void regsiter_and_call_hook() {
		String script = "Hooks.register('print', function (args) { println('Hello ' + args.name);});";
		
		RhinoScripting scripting = new RhinoScripting("com/thorstenmarx/webtools/scripting/modules/rhino");
		
		HookSystem hooks = new HookSystem();
		scripting.eval(script, (context) -> {
			new PrintLn().addToScope(context);
			context.put("Hooks", context, hooks);
		});
		
		script = "Hooks.call('print', {'name' : 'Thorsten'});";
		scripting.eval(script, (context) -> {
			new PrintLn().addToScope(context);
			context.put("Hooks", context, hooks);
		});
		
		script = "Hooks.register('print', function (args) { println('Ola ' + args.name);});";
		scripting.eval(script, (context) -> {
			new PrintLn().addToScope(context);
			context.put("Hooks", context, hooks);
		});
		script = "Hooks.call('print', {'name' : getName()});";
		scripting.eval(script, (context) -> {
			new PrintLn().addToScope(context);
			new GetName().addToScope(context);
			context.put("Hooks", context, hooks);
		});
	}
	@Test
	public void hook_priority() {
		String hook1 = "Hooks.register('print', function (args) { println('Hello (second) ' + args.name);}, 10);";
		String hook2 = "Hooks.register('print', function (args) { println('Hello (first) ' + args.name);}, 9);";
		
		RhinoScripting scripting = new RhinoScripting("com/thorstenmarx/webtools/scripting/modules/rhino");
		
		HookSystem hooks = new HookSystem();
		scripting.eval(hook1, (context) -> {
			new PrintLn().addToScope(context);
			context.put("Hooks", context, hooks);
		});
		scripting.eval(hook2, (context) -> {
			new PrintLn().addToScope(context);
			context.put("Hooks", context, hooks);
		});
		
		String script = "Hooks.call('print', {'name' : 'Thorsten'});";
		scripting.eval(script, (context) -> {
			new PrintLn().addToScope(context);
			new GetName().addToScope(context);
			context.put("Hooks", context, hooks);
		});
	}
	
	public class HookSystem {
		private static final int DEFAULT_PRIORITY = 10;
		
		private ConcurrentMap<String, List<HookHolder>> hooks = new ConcurrentHashMap<>();
		
		public void register (final String name, final Hook hook) {
			register(name, hook, DEFAULT_PRIORITY);
		}
		public void register (final String name, final Hook hook, final int priority) {
			hooks.computeIfAbsent(name, k -> new CopyOnWriteArrayList<>()).add(new HookHolder(hook, priority));
		}
		
		public void call (final String name, Map<String, Object> arguments) {
			List<HookHolder> matchingHooks = hooks.entrySet().stream().filter(map -> map.getKey().equals(name)).flatMap(entry -> entry.getValue().stream()).collect(Collectors.toList());
			
			matchingHooks.stream().sorted(Comparator.comparing(HookHolder::byPriority)).forEach(hook -> hook.hook.execute(arguments));
		}
		
		private class HookHolder {
			public final Hook hook;
			public final int priority;

			public HookHolder(Hook hook, int priority) {
				this.hook = hook;
				this.priority = priority;
			}
			
			public int byPriority () {
				return priority;
			}
		}
	}
	
	public interface Hook {
		public void execute (Map<String, Object> arguments);
	} 
	
	private static class PrintLn extends AnnotatedScriptableObject {

		@AnnotatedScriptableObject.Expose
		public void println(final String text) {
			System.out.println(text);
		}

	}
	private static class GetName extends AnnotatedScriptableObject {

		@AnnotatedScriptableObject.Expose
		public String getName() {
			return "Thorsten Marx";
		}

	}
}
