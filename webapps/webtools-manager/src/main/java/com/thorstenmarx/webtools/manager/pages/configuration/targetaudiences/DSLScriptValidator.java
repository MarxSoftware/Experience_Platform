package com.thorstenmarx.webtools.manager.pages.configuration.targetaudiences;

/*-
 * #%L
 * webtools-manager
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
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.ContextListener;
import com.thorstenmarx.webtools.actions.dsl.DSLSegment;
import com.thorstenmarx.webtools.actions.dsl.rhino.RhinoDSL;
import javax.script.ScriptException;
import net.engio.mbassy.bus.MBassador;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

/**
 *
 * @author thmarx
 */
public class DSLScriptValidator implements IValidator<String> {

	private static final Logger LOGGER = LogManager.getLogger(DSLScriptValidator.class);
	private RhinoDSL dslRunner;
	final String message;

	public DSLScriptValidator(final String message) {
		this.message = message;
		dslRunner = new RhinoDSL(
				ContextListener.INJECTOR_PROVIDER.injector().getInstance(ModuleManager.class),
				ContextListener.INJECTOR_PROVIDER.injector().getInstance(MBassador.class)
		);
	}

	/**
	 *
	 * @param iv
	 */
	@Override
	public void validate(IValidatable<String> iv) {
		try {
			final DSLSegment segment = dslRunner.build(iv.getValue());
		} catch (ScriptException ex) {
			LOGGER.error("", ex);
			ValidationError error = new ValidationError();
			error.setMessage(message);
			iv.error(error);
		}
	}

}
