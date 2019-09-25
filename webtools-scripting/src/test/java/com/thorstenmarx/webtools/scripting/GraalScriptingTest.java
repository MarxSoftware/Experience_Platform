package com.thorstenmarx.webtools.scripting;

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
import java.util.function.Supplier;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import org.assertj.core.api.Assertions;
import org.graalvm.polyglot.PolyglotException;
import org.junit.jupiter.api.Test;

/**
 *
 * @author marx
 */
public class GraalScriptingTest {

	private GraalScripting scripting = new GraalScripting("com/thorstenmarx/webtools/scripting/modules/graal/");

	@Test
	public void testSomeMethod() {
		String script = "print(getName());";
//		String script = "print(name);";

		scripting.eval(script, (context) -> {
			Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("getName", (Supplier<String>) () -> "thorsten");
//			bindings.put("name", "thorsten");
		});

	}

	@Test
	public void test_context_separation() {
		Assertions.assertThatThrownBy(() -> {
			scripting.eval("getName('thorsten');");
		}).isInstanceOf(RuntimeException.class);
	}

	@Test
	public void test_promise() {
		scripting.eval("new Promise(function (resolve, reject) { resolve();}).then(function (){print('resolved');});");
	}

	@Test()
	public void test_require() {
		StringBuilder sb = new StringBuilder();
		sb.append("var test = require('test.js');\r\n");
		sb.append("var demo = require('demo.js');\r\n");
		sb.append("test.println('Thorsten');\r\n");
		sb.append("demo.println('Thorsten');");

		scripting.eval(sb.toString());
	}

	@Test
	public void test_require_seperation() {

		StringBuilder sb = new StringBuilder();
		sb.append("var test = require('test.js');\r\n");
		sb.append("var demo = require('demo.js');\r\n");
		sb.append("test.println('Thorsten');\r\n");
		sb.append("demo.println('Thorsten');");
 
		scripting.eval(sb.toString());

		Assertions.assertThatThrownBy(() -> {
			scripting.eval("demo.println('Thorsten');");
		}).isInstanceOfAny(PolyglotException.class, RuntimeException.class, ScriptException.class);

	}
}
