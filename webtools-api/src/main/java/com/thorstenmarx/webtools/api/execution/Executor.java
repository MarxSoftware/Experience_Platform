package com.thorstenmarx.webtools.api.execution;

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

import com.thorstenmarx.webtools.api.annotations.API;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The Executor is for executing parallel tasks.
 *
 * @author marx
 * 
 * @since 2.1.0
 */
@API(since = "2.1.0", status = API.Status.Stable)
public interface Executor {
	CompletableFuture<Void> execute(final Runnable task);

	/**
	 * @see ScheduledExecutorService#schedule(java.lang.Runnable, long, java.util.concurrent.TimeUnit)
	 * @param task The Taks
	 * @param delay The delay
	 * @param timeUnit The timeUnit
	 * @return
	 */
	ScheduledFuture<?> schedule(final Runnable task, final long delay, final TimeUnit timeUnit);

	/**
	 * @see ScheduledExecutorService#scheduleWithFixedDelay(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
	 * @param task The task
	 * @param initialDelay Initial Delay
	 * @param delay delay
	 * @param timeUnit TimeUnit
	 * @return
	 */
	ScheduledFuture<?> scheduleFixedDelay(final Runnable task, final long initialDelay, final long delay, final TimeUnit timeUnit);

	/**
	 * @see ScheduledExecutorService#scheduleAtFixedRate(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
	 * @param task The task
	 * @param initialDelay Initial delay
	 * @param period Period
	 * @param timeUnit Timeunit
	 * @return
	 */
	ScheduledFuture<?> scheduleFixedRate(final Runnable task, final long initialDelay, final long period, final TimeUnit timeUnit);
}
