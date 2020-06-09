package com.thorstenmarx.webtools.api.metrics;

/*-
 * #%L
 * webtools-api
 * %%
 * Copyright (C) 2016 - 2020 WP DigitalExperience
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

import java.util.Map;

/**
 *
 * @author marx
 */
public interface MetricsService {
	/**
	 * 
	 * @param name Name of the kpi
	 * @param site The site for the kpi
	 * @param start The start value
	 * @param end The end value
	 * @return 
	 */
	Number getKpi (final String name, final String site, final long start, final long end);
}
