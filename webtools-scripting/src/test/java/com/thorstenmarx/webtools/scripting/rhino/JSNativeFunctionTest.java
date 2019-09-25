package com.thorstenmarx.webtools.scripting.rhino;

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
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.junit.jupiter.api.Test;

/**
 * Unit test for JS Native function
 * 
 * @author Ibrahim Chaehoi
 */
public class JSNativeFunctionTest {

	private final String testScript = "var FRUITS = 'apple orange banana';\n"
			+ "var COLORS = 'red green yellow';\n"
			+ "var RESERVED_WORDS = FRUITS + ' ' + COLORS;\n"
			+" (function(){\n"
			+ "var words = RESERVED_WORDS.split(' ');\n"
			+ "return words.length})()";
	
	
	@Test()
	@Disabled
	public void testWithStdRhinoScriptWithGlobalNativeRhinoObject() throws ScriptException{
		
		Context context = Context.enter();
		context.setOptimizationLevel(-1);
		ScriptableObject scope = context.initStandardObjects();
		Object result = null;
		try {
			Context ctx = Context.getCurrentContext();
			result = ctx.evaluateString(scope, testScript, "test", 0,
					null);
		} finally {
			if (Context.getCurrentContext() != null) {
				Context.exit();
			}
		}
		
		Assertions.assertThat(((Number)result).intValue()).isEqualTo(6);
	}
}
