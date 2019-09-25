package com.thorstenmarx.webtools.scripting.graal.require;

/*-
 * #%L
 * webtools-scripting
 * %%
 * Copyright (C) 2016 - 2019 Thorsten Marx
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
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;


public class Require {
  // This overload registers the require function globally in the engine scope
  public static Module enable(final ScriptEngine engine, final Folder folder) throws ScriptException {
    Bindings global = engine.getBindings(ScriptContext.ENGINE_SCOPE);
    return enable(engine, folder, global);
  }

  // This overload registers the require function in a specific Binding. It is useful when re-using the
  // same script engine across multiple threads (each thread should have his own global scope defined
  // through the binding that is passed as an argument).
  public static Module enable(final ScriptEngine engine, final Folder folder, final Bindings bindings)
      throws ScriptException {
    Bindings module = engine.createBindings();
    Bindings exports = engine.createBindings();

    Module created =
        new Module(engine, folder, new ModuleCache(), "<main>", module, exports, null, null);
    created.setLoaded();

    bindings.put("require", created);
    bindings.put("module", module);
    bindings.put("exports", exports);

    return created;
  }
}
