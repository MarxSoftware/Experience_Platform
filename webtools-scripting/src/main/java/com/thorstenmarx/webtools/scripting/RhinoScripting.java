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
import com.thorstenmarx.webtools.scripting.rhino.Promise;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.ModuleScript;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;

/**
 *
 * @author marx
 */
public class RhinoScripting implements Scripting<Scriptable> {

	private final String modulePackage;
	
	public RhinoScripting(final String modulePackage) {
		this.modulePackage = modulePackage;
	}

	private void initContext(final ScriptContext context) throws ScriptException {
		}

	@Override
	public void eval(final String script) {
		eval(script, c -> {
		});
	}

	@Override
	public void eval(final String script, final Consumer<Scriptable> contextInitializer) {
		
		Context context = Context.enter();
		try {
			context.setLanguageVersion(Context.VERSION_ES6);
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			Scriptable scope = context.initStandardObjects();

			ImporterTopLevel top = new ImporterTopLevel(context);
//			ScriptableObject.defineClass(top, Promise.class);
			
			contextInitializer.accept(top);
			
			RequireBuilder require = new RequireBuilder();
			require.setSandboxed(false);
			require.setModuleScriptProvider(new ModuleScriptProvider() {
				@Override
				public ModuleScript getModuleScript(final Context cx, final String moduleId, final URI moduleUri, final URI baseUri, final Scriptable paths) throws Exception {

					final String text = new Scanner(RhinoScripting.class.getResourceAsStream(modulePackage + "/" + moduleId  + ".js"), "UTF-8").useDelimiter("\\A").next();
					
					URI moduleURI = RhinoScripting.class.getResource(modulePackage + "/" + moduleId  + ".js").toURI();
					URI baseURI = RhinoScripting.class.getResource(modulePackage + "/").toURI();
					
					ModuleScript script = new ModuleScript(new Script() {
						@Override
						public Object exec(Context cx, Scriptable scope) {
							return cx.evaluateString(scope, text, "<cmd>", 1, null);
						}
					}, moduleURI, baseURI);
					return script;
				}
			});
			Require required = require.createRequire(context, scope);
			
			required.install(top);
			
			// Now evaluate the string we've colected.
			Object result = context.evaluateString(top, script, "<cmd>", 1, null);

			// Convert the result to a string and print it.
//			System.err.println(Context.toString(result));
		} finally {
			// Exit from the context.
			Context.exit();
		}
	}
}
