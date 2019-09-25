package com.thorstenmarx.webtools.scripting.mustache;

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
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.TemplateFunction;
import com.thorstenmarx.webtools.scripting.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.script.Bindings;
import javax.script.ScriptContext;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

/**
 *
 * @author marx
 */
public class MustacheTest {

	@Test
	public void testSomeMethod() {
		String script = "print( renderTemplate('Hallo {{name}}', {'name': 'thorsten'}) );";

		GraalScripting scripting = new GraalScripting("com/thorstenmarx/webtools/scripting/modules/nashorn");

		scripting.eval(script, (context) -> {
			Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("getTemplate", (Supplier<String>) () -> "Hallo {{name}}");
			bindings.put("name", "Thorsten");
			bindings.put("renderTemplate", (BiFunction<String, Map, String>) (template, scope) -> {
				MustacheFactory mf = new DefaultMustacheFactory();
				Mustache mustache = mf.compile(new StringReader(template), "example");
				StringWriter writer = new StringWriter();
				
				scope.put("callme", new TemplateFunction() {
					@Override
					public String apply(String t) {
						return "callme " + t;
					}
				});
				mustache.execute(writer, scope);
				return writer.toString();
			});
		});
		System.out.println("");
		scripting.eval("print (renderTemplate('{{#callme}}Thorsten{{/callme}}', {}));", (context) -> {
			Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("getTemplate", (Supplier<String>) () -> "Hallo {{name}}");
			bindings.put("name", "Thorsten");
			bindings.put("renderTemplate", (BiFunction<String, Map, String>) (template, scope) -> {
				MustacheFactory mf = new DefaultMustacheFactory();
				Mustache mustache = mf.compile(new StringReader(template), "example");
				StringWriter writer = new StringWriter();
				
				scope.put("callme", (TemplateFunction) (String t) -> "callme " + t);
				mustache.execute(writer, scope);
				return writer.toString();
			});
		});

	}
}
