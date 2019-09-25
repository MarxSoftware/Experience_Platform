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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;

import freemarker.cache.TemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.SimpleDate;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Thorsten Marx (thmarx@gmx.net)
 */
public class TemplateEngine {

	private Configuration config = null;

	public static class Builder {

		private TemplateLoader templateLoader = null;
		private final Map<String, TemplateMethodModelEx> methods = new HashMap<>();
		private Map<String, TemplateDirectiveModel> directives;

		public Builder() {
			this.directives = new HashMap<>();
		}

		public Builder templateLoader(TemplateLoader templateLoader) {
			this.templateLoader = templateLoader;
			return this;
		}

		public <R> Builder addMethod(final String name, final Function<List, R> function, final Class<R> clazz) {
			this.methods.put(name, new MethodWrapper(function));
			return this;
		}
		public Builder addMethod(final String name, final TemplateMethodModelEx templateMethod) {
			this.methods.put(name, templateMethod);
			return this;
		}

		public Builder addDirective(final String name, final TemplateDirectiveModel directive) {
			this.directives.put(name, directive);
			return this;
		}

		public TemplateEngine build() {
			Configuration config = new Configuration(new Version(2, 3, 28));
			config.setTemplateLoader(templateLoader);
			config.setObjectWrapper(new DefaultObjectWrapperBuilder(new Version(2, 3, 28)).build());
			config.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);

			directives.entrySet().forEach((directive) -> {
				config.setSharedVariable(directive.getKey(), directive.getValue());
			});
			methods.entrySet().forEach((method) -> {
				config.setSharedVariable(method.getKey(), method.getValue());
			});

			return new TemplateEngine(config);
		}

	}

	public static Builder builder() {
		return new Builder();
	}

	private TemplateEngine(Configuration config) {
		this.config = config;
	}

	public Configuration getConfig() {
		return config;
	}

	public void render(String template, Context context,
			Writer writer) throws IOException {
		try {
			if (context == null) {
				context = new Context();
			}
			Template temp = config.getTemplate(template);
			temp.process(context, writer);

		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public String render(String template, Context context)
			throws IOException {
		try {

			StringWriter sw = new StringWriter();
			BufferedWriter bw = new BufferedWriter(sw);
			render(template, context, bw);
			bw.flush();
			return sw.toString();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	private static class DirectiveWrapper implements TemplateDirectiveModel {

		@Override
		public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
		
	}

	public static class MethodWrapper<R> implements TemplateMethodModelEx {

		final Function<List, R> function;

		public MethodWrapper(final Function<List, R> function) {
			this.function = function;
		}

		@Override
		public Object exec(List list) throws TemplateModelException {
			List args = (List) list.stream().map(object -> {
				if (object instanceof SimpleScalar) {
					return ((SimpleScalar) object).getAsString();
				} else if (object instanceof SimpleNumber) {
					return ((SimpleNumber) object).getAsNumber();
				} else if (object instanceof SimpleDate) {
					return ((SimpleDate) object).getAsDate();
				}
				return object;
			}).collect(Collectors.toList());
			return function.apply(args);
		}

	}
}
