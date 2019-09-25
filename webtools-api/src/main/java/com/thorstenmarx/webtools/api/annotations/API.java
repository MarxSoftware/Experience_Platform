package com.thorstenmarx.webtools.api.annotations;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*-
 * #%L
 * webtools-api
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

/**
 *
 * @author marx
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(value={CONSTRUCTOR,FIELD,LOCAL_VARIABLE,METHOD,PACKAGE,PARAMETER,TYPE})
public @interface API {

	/**
	 * The current state of the api.
	 * 
	 * @return the current state;
	 */
	Status status();
	/**
	 * The version of the last status change.
	 * @return a version string
	 */
	String since ();
	/**
	 * The version the deprecated api will be removed
	 * @return a version string
	 */
	String toRemove() default "";
	
	enum Status {
		/**
		 * Experimental API
		 * could be changed anytime
		 */
		Experimental,
		/**
		 * Deprecated API
		 * will be removed in the next major version
		 */
		Deprecated,
		/**
		 * Staple API
		 * will not be changed in any incomatibly way
		 */
		Stable
	}
}
