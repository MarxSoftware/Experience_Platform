package de.marx_software.webtools.impl.execution;

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

import de.marx_software.webtools.api.execution.Executor;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marx
 */
public class DefaultExecutor implements Executor {

	ScheduledThreadPoolExecutor scheduler;
	ExecutorService executor;
	
	public DefaultExecutor () {
		this(1, 1);
	}
	
	public DefaultExecutor (final int schedulerThreads, int executorThreads) {
		scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(schedulerThreads);
		scheduler.setRemoveOnCancelPolicy(true);
		executor = Executors.newFixedThreadPool(executorThreads);
	}
	
	@Override
	public CompletableFuture<Void> execute (final Runnable task) {
		return CompletableFuture.runAsync(task, executor);
//		return executor.submit(task);
	}
	
	/**
	 * @see ScheduledExecutorService#schedule(java.lang.Runnable, long, java.util.concurrent.TimeUnit) 
	 * @param task The Taks
	 * @param delay The delay
	 * @param timeUnit The timeUnit
	 * @return 
	 */
	@Override
	public ScheduledFuture<?> schedule (final Runnable task, final long delay, final TimeUnit timeUnit) {
		return scheduler.schedule(task, delay, timeUnit);
	}
	/**
	 * @see ScheduledExecutorService#scheduleAtFixedRate(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit) 
	 * @param task The task
	 * @param initialDelay Initial delay
	 * @param period Period
	 * @param timeUnit Timeunit
	 * @return 
	 */
	@Override
	public ScheduledFuture<?> scheduleFixedRate (final Runnable task, final long initialDelay, final long period, final TimeUnit timeUnit) {
		return scheduler.scheduleAtFixedRate(task, initialDelay, period, timeUnit);
	}
        /**
	 * @see ScheduledExecutorService#scheduleWithFixedDelay(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit) 
	 * @param task The task
	 * @param initialDelay Initial Delay
	 * @param delay delay
	 * @param timeUnit TimeUnit
	 * @return 
	 */
	@Override
	public ScheduledFuture<?> scheduleFixedDelay (final Runnable task, final long initialDelay, final long delay, final TimeUnit timeUnit) {
		return scheduler.scheduleWithFixedDelay(task, initialDelay, delay, timeUnit);
	}
	
	public void shutdown () {
		scheduler.shutdownNow();
		executor.shutdownNow();
	}
}
