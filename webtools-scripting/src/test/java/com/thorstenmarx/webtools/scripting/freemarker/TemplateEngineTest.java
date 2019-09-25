package com.thorstenmarx.webtools.scripting.freemarker;

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
import com.thorstenmarx.webtools.scripting.freemarker.context.Context;
import com.thorstenmarx.webtools.scripting.freemarker.context.SimpleContext;
import com.thorstenmarx.webtools.scripting.freemarker.methods.RepeatDirective;
import com.thorstenmarx.webtools.scripting.freemarker.methods.UpperDirective;
import java.io.IOException;

import freemarker.cache.StringTemplateLoader;
import java.util.List;
import java.util.function.Function;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Created by IntelliJ IDEA. User: marx Date: 13.06.12 Time: 11:49 To change
 * this template use File | Settings | File Templates.
 */
public class TemplateEngineTest {

	@Test
	public void testRender() throws IOException {
		StringTemplateLoader loader = new StringTemplateLoader();

		loader.putTemplate("test1", "Hi ${name?if_exists}, How are you?");
		loader.putTemplate("greetTemplate", "[#macro greet]Hello[/#macro]");
		loader.putTemplate("myTemplate",
				"[#include \"greetTemplate\" ][@greet/] World!");

		Context context = new SimpleContext();
		context.put("name", "Joe");

		TemplateEngine manager = TemplateEngine.builder()
				.templateLoader(loader).build();

		assertThat(manager.render("test1", context)).isEqualTo("Hi Joe, How are you?");

		assertThat(manager.render("myTemplate", context)).isEqualTo("Hello World!");
	}

	@Test
	public void testMethod() throws IOException {
		StringTemplateLoader loader = new StringTemplateLoader();

		loader.putTemplate("test1",
				"${indexOf(\"met\", x)}");

		Context context = new SimpleContext();
		context.put("x", "something");
		context.put("preview", true);

		TemplateEngine manager = TemplateEngine.builder()
				.templateLoader(loader)
				.addMethod("indexOf", new Function<List, Number>() {
					@Override
					public Number apply(List args) {
						if (args.size() != 2) {
							throw new RuntimeException("Wrong arguments");
						}
						return ((String) args.get(1)).indexOf(((String) args.get(0)));
					}

				}, Number.class)
				.build();

		assertThat(manager.render("test1", context)).isEqualTo("2");
	}

	@Test
	public void testMethod2() throws IOException {
		StringTemplateLoader loader = new StringTemplateLoader();

		loader.putTemplate("test1",
				"${indexOf(\"met\", x)}");

		Context context = new SimpleContext();
		context.put("x", "something");
		context.put("preview", true);
		context.put("indexOf", new TemplateEngine.MethodWrapper<Number>(new Function<List, Number>() {
			@Override
			public Number apply(List args) {
				if (args.size() != 2) {
					throw new RuntimeException("Wrong arguments");
				}
				return ((String) args.get(1)).indexOf(((String) args.get(0)));
			}

		}));

		TemplateEngine manager = TemplateEngine.builder()
				.templateLoader(loader)
				.build();

		assertThat(manager.render("test1", context)).isEqualTo("2");
	}

	@Test
	public void testUpperDirective() throws IOException {
		StringTemplateLoader loader = new StringTemplateLoader();

		loader.putTemplate("test1",
				"[@upper]${x}[/@upper]");

		String totest = "something";

		Context context = new SimpleContext();
		context.put("x", totest);

		TemplateEngine manager = TemplateEngine.builder()
				.templateLoader(loader)
				.addDirective("upper", new UpperDirective())
				.build();

		assertThat(manager.render("test1", context)).isEqualTo(totest.toUpperCase());
	}

	@Test
	public void testRepeatDirective() throws IOException {
		StringTemplateLoader loader = new StringTemplateLoader();

		loader.putTemplate("test1",
				"[@repeat count=3]${x}[/@repeat]");

		String totest = "something";

		Context context = new SimpleContext();
		context.put("x", totest);
		context.put("preview", true);

		TemplateEngine manager = TemplateEngine.builder()
				.templateLoader(loader)
				.addDirective("repeat", new RepeatDirective())
				.build();

		assertThat(manager.render("test1", context)).isEqualTo(totest + totest + totest);
	}
}
