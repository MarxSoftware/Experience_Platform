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
import com.thorstenmarx.webtools.scripting.graal.require.RequireFunction;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import org.graalvm.polyglot.Context;

/**
 *
 * @author marx
 */
public class GraalScripting implements Scripting<ScriptContext> {

	private ScriptEngine scriptEngine;

	private final String modulePackage;

	public GraalScripting(final String modulePackage) {
		this.scriptEngine = new ScriptEngineManager().getEngineByName("graal.js");
		this.modulePackage = modulePackage;
	}

	private void initContext(final ScriptContext context) throws ScriptException {

		context.getBindings(ScriptContext.ENGINE_SCOPE).put("require", new RequireFunction() {
			@Override
			public Object require(String module) throws ScriptException {
				try {
					System.out.println("loading module: " + (modulePackage + module));
					URL moduleUrl = Scripting.class.getClassLoader().getResource(modulePackage + module);

					final String script = new String(Files.readAllBytes(Paths.get(moduleUrl.toURI())));

					scriptEngine.eval("var exports = {}", context);
					final Object exports = context.getBindings(ScriptContext.ENGINE_SCOPE).get("exports");
					scriptEngine.eval(script, context);

					return exports;
				} catch (URISyntaxException | FileNotFoundException ex) {
					throw new ScriptException(ex);
				} catch (IOException ex) {
					throw new ScriptException(ex);
				}
			}
		});
	}

	@Override
	public void eval(final String script) {
		eval(script, c -> {
		});
	}

	@Override
	public void eval(final String script, final Consumer<ScriptContext> contextInitializer) {
		try {

			ScriptContext context = new SimpleScriptContext();
			final Bindings engineBindings = scriptEngine.createBindings();
			engineBindings.put("polyglot.js.allowAllAccess", true);
			context.setBindings(engineBindings, ScriptContext.ENGINE_SCOPE);
			final Bindings globalBindings = scriptEngine.createBindings();
			globalBindings.put("polyglot.js.allowAllAccess", true);
			context.setBindings(globalBindings, ScriptContext.GLOBAL_SCOPE);
			

			contextInitializer.accept(context);

			initContext(context);

			scriptEngine.eval(script, context);
		} catch (ScriptException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
}
