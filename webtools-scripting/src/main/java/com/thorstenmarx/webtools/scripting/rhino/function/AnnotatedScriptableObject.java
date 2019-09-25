package com.thorstenmarx.webtools.scripting.rhino.function;

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
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

@SuppressWarnings("serial")
public class AnnotatedScriptableObject extends ScriptableObject {

	@Target(METHOD)
	@Retention(RUNTIME)
	public @interface Expose {
	}

	public void addToScope(Scriptable scope) {
		for (Method method : this.getClass().getMethods()) {
			if (method.isAnnotationPresent(Expose.class)) {
				FunctionObject function = new FunctionObject(method.getName(), method, this);
				scope.put(function.getFunctionName(), scope, function);
			}
		}
	}

	@Override
	public String getClassName() {
		return getClass().getName();
	}
}
