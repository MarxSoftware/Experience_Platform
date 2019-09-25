package com.thorstenmarx.webtools.jgroups;

/*-
 * #%L
 * webtools-incubator
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
import org.jgroups.blocks.atomic.Counter;

/**
 * @author Bela Ban
 * @since 0.2
 */
public class CounterImpl implements Counter {

	protected final String name;
	protected final CounterService counter_service; // to delegate all commands to

	public CounterImpl(String name, CounterService counter_service) {
		this.name = name;
		this.counter_service = counter_service;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long get() {
		try {
			return counter_service.allowDirtyReads() ? counter_service._get(name) : counter_service.get(name);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void set(long new_value) {
		try {
			counter_service.set(name, new_value);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public boolean compareAndSet(long expect, long update) {
		try {
			return counter_service.compareAndSet(name, expect, update);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public long incrementAndGet() {
		try {
			return counter_service.incrementAndGet(name);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public long decrementAndGet() {
		try {
			return counter_service.decrementAndGet(name);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public long addAndGet(long delta) {
		try {
			return counter_service.addAndGet(name, delta);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String toString() {
		return String.valueOf(counter_service._get(name));
	}
}
