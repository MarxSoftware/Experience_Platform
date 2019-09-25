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
import com.thorstenmarx.webtools.scripting.rhino.function.AnnotatedScriptableObject;
import java.util.function.Supplier;
import javax.script.Bindings;
import javax.script.ScriptContext;
import org.junit.jupiter.api.Disabled;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.junit.jupiter.api.Test;

/**
 *
 * @author marx
 */
public class RhinoScriptingTest {

	@Test
	public void testSomeMethod() {
		String script = "java.lang.System.out.println(name);"
				+ "java.lang.System.out.println(getName());";

		RhinoScripting scripting = new RhinoScripting("com/thorstenmarx/webtools/scripting/modules/rhino");

		scripting.eval(script, (context) -> {
//			Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
			context.put("name", context, "thorsten");
			new GetName().addToScope(context);
//			context.put("getName", context, );
		});

	}

	@Test
	public void test_require() {
		String script = "var mod1 = require('test'); mod1.println('Hello Thorsten');";

		RhinoScripting scripting = new RhinoScripting("/com/thorstenmarx/webtools/scripting/modules/rhino");

		scripting.eval(script, (context) -> {

		});

	}

	@Disabled
	@Test
	public void test_promise() {
//		RhinoScripting scripting = new RhinoScripting("com/thorstenmarx/webtools/scripting/modules/rhino");
//		scripting.eval("new Promise(function (resolve, reject) { resolve();}).then(function (){print('resolved');});");
//		scripting.eval("var mod1 = require('test'); mod1.println('Das ist ein Test');"
//				+ "new Promise({ get : function () {return 'Thorsten Marx';}).then({accept : function (result) {mod1.println(result)}});");
	}

	@Test
	public void test_varargs() {
		String script = "java.lang.System.out.println(vargstest('thorsten', 'Imke', 'Lara'));";

		RhinoScripting scripting = new RhinoScripting("com/thorstenmarx/webtools/scripting/modules/rhino");

		scripting.eval(script, (context) -> {
			new GetName().addToScope(context);
		});

	}

	private static class GetName extends AnnotatedScriptableObject {

		@Expose
		public String getName() {
			return "Thorsten Marx";
		}

		@Expose
		public static String vargstest(Context cx, Object[] args, Function ctorObj, boolean inNewExpr) {
			return "name: " + args.length;
		}

	}
}
