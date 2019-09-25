/*
 * Copyright (C) 2019 Thorsten Marx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.thorstenmarx.webtools.cluster;

/*-
 * #%L
 * webtools-cluster
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

import com.thorstenmarx.webtools.api.cluster.services.LockService;
import java.util.concurrent.TimeUnit;
import org.jgroups.JChannel;

/**
 *
 * @author marx
 */
public class JGroupsLockService implements LockService {

	private final org.jgroups.blocks.locking.LockService lockService;
	
	public JGroupsLockService (final JChannel channel) {
		this.lockService = new org.jgroups.blocks.locking.LockService(channel);
	}
	
	@Override
	public Lock getLock(String name) {
		return new JGroupsLock(lockService.getLock(name));
	}

	@Override
	public void unlockAll() {
		lockService.unlockAll();
	}

	@Override
	public void unlockForce(final String name) {
		lockService.unlockForce(name);
	}
	
	
	
	public static class JGroupsLock implements Lock {

		final java.util.concurrent.locks.Lock lock;
		
		public JGroupsLock (final java.util.concurrent.locks.Lock lock) {
			this.lock = lock;
		}
		
		@Override
		public void lock() {
			lock.lock();
		}
		
		@Override
		public void unlock() {
			lock.unlock();
		}

		@Override
		public boolean tryLock() {
			return lock.tryLock();
		}
		
		@Override
		public boolean tryLock (final long time, final TimeUnit unit) throws InterruptedException {
			return lock.tryLock(time, unit);
		}
		
	}
}
