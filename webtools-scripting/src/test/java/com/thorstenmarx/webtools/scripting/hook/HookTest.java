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
import org.graalvm.polyglot.proxy.ProxyObject;
import org.junit.jupiter.api.Test;

/**
 *
 * @author marx
 */
public class HookTest {

	@Test
	public void regsiter_and_call_hook() {
		String script = "Hooks.register('print', function (args) { print('Hello ' + args.name);});";

		GraalScripting scripting = new GraalScripting("com/thorstenmarx/webtools/scripting/modules/graal/");

		HookSystem hooks = new HookSystem();
		scripting.eval(script, (context) -> {
			Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("getName", (Supplier<String>) () -> "thorsten");
			bindings.put("Hooks", hooks);
		});

		script = "Hooks.call('print', {'name' : 'Thorsten'});";
		scripting.eval(script, (context) -> {
			Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("Hooks", hooks);
		});
		
		script = "Hooks.register('print', function (args) { print('Ola ' + args.name);});";
		scripting.eval(script, (context) -> {
			Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("Hooks", hooks);
		});
		script = "Hooks.call('print', {'name' : getName()});";
		scripting.eval(script, (context) -> {
			Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("getName", (Supplier<String>) () -> "thorsten");
			bindings.put("Hooks", hooks);
		});
	}

	@Test
	public void hook_priority() {
		String hook1 = "Hooks.register('print', function (args) { print('Hello (second) ' + args.name);}, 10);";
		String hook2 = "Hooks.register('print', function (args) { print('Hello (first) ' + args.name);}, 9);";

		GraalScripting scripting = new GraalScripting("com/thorstenmarx/webtools/scripting/modules/graal/");

		HookSystem hooks = new HookSystem();
		scripting.eval(hook1, (context) -> {
			Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("Hooks", hooks);
		});
		scripting.eval(hook2, (context) -> {
			Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("Hooks", hooks);
		});

		String script = "Hooks.call('print', {'name' : 'Thorsten'});";
		scripting.eval(script, (context) -> {
			Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("Hooks", hooks);
		});
	}

	public class HookSystem {

		private static final int DEFAULT_PRIORITY = 10;

		private ConcurrentMap<String, List<HookHolder>> hooks = new ConcurrentHashMap<>();

		public void register(final String name, final Hook hook) {
			register(name, hook, DEFAULT_PRIORITY);
		}

		public void register(final String name, final Hook hook, final int priority) {
			hooks.computeIfAbsent(name, k -> new CopyOnWriteArrayList<>()).add(new HookHolder(hook, priority));
		}

		public void call(final String name, Map<String, Object> arguments) {
			final Map<String, Object> tempArguments = new HashMap<>(arguments);
			
			List<HookHolder> matchingHooks = hooks.entrySet().stream().filter(map -> map.getKey().equals(name)).flatMap(entry -> entry.getValue().stream()).collect(Collectors.toList());

			matchingHooks.stream().sorted(Comparator.comparing(HookHolder::byPriority)).forEach(hook
					-> hook.hook.execute(new MapProxyObject(arguments))
			);
		}

		private class HookHolder {

			public final Hook hook;
			public final int priority;

			public HookHolder(Hook hook, int priority) {
				this.hook = hook;
				this.priority = priority;
			}

			public int byPriority() {
				return priority;
			}
		}
	}

	public interface Hook {

		public void execute(Map<String, Object> arguments);
	}
}