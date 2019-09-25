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
package com.thorstenmarx.webtools.cluster.actionsystem;

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
import com.thorstenmarx.webtools.actions.SegmentCalculator;
import com.thorstenmarx.webtools.actions.dsl.graal.GraalDSL;
import com.thorstenmarx.webtools.api.actions.SegmentService;
import com.thorstenmarx.webtools.api.actions.model.AdvancedSegment;
import com.thorstenmarx.webtools.api.actions.model.Segment;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.cluster.Cluster;
import com.thorstenmarx.webtools.api.cluster.services.LockService;
import com.thorstenmarx.webtools.api.datalayer.SegmentData;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public class ClusterActionSystem {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusterActionSystem.class);

	private final SegmentService segmentService;
	private final Cluster cluster;
	private final AnalyticsDB db;
	private final GraalDSL dslRunner;

	private ExecutorService executorService;
	private Future<?> segmentCoordinator;

	public ClusterActionSystem(final Cluster cluster, final SegmentService segmentService, final AnalyticsDB db) {
		this.segmentService = segmentService;
		this.cluster = cluster;
		this.db = db;
		this.dslRunner = new GraalDSL(null, null);
	}

	public GraalDSL getDSLRunner() {
		return dslRunner;
	}

	public void start() {
		if (executorService == null) {
			executorService = Executors.newSingleThreadExecutor();
			executorService.submit(() -> {
				while (!Thread.currentThread().isInterrupted()) {
					try {
						Collection<Segment> segments = segmentService.all();
						segments.forEach((segment) -> {
							LockService.Lock lock = cluster.getLockService().getLock("segment_" + segment.getId());
							try {
								if (lock.tryLock()) {
									Runnable r = new SegmentCoordinatorRunnable((AdvancedSegment) segment);
									cluster.getExecutorService().submit(r);
								}
							} finally {
								lock.unlock();
							}

						});
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return;
					}
				}
			});
		}
	}

	public void stop() {
		if (executorService != null) {
			segmentCoordinator.cancel(true);
			executorService.shutdown();
		}
	}

	public static class SegmentCoordinatorRunnable implements Runnable {

		private transient Cluster cluster;
		private transient AnalyticsDB db;
		private transient GraalDSL dslRunner;

		private AdvancedSegment segment;

		public SegmentCoordinatorRunnable() {

		}

		public SegmentCoordinatorRunnable(final AdvancedSegment segment) {
			this.segment = segment;
		}

		public void setDb(AnalyticsDB db) {
			this.db = db;
		}

		public void setDslRunner(GraalDSL dslRunner) {
			this.dslRunner = dslRunner;
		}

		public void setCluster(Cluster cluster) {
			this.cluster = cluster;
		}

		@Override
		public void run() {
			SegmentCalculator segmentCalculator = new SegmentCalculator(db, dslRunner);
			SegmentCalculator.Result result = segmentCalculator.calculate(segment);
			result.users.forEach(this::updateDataLayer);
		}

		private void updateDataLayer(final String user) {
			LockService.Lock lock = cluster.getLockService().getLock("segment_" + segment.getId());
			try {
				if (lock.tryLock(1, TimeUnit.SECONDS)) {
					try {
						final SegmentData segmentData = new SegmentData();
						long validTo = System.currentTimeMillis() + segment.startTimeWindow().millis();
						segmentData.addSegment(segment.getName(), segment.getExternalId(), validTo);
						cluster.getDataLayer().add(user, SegmentData.KEY, segmentData);
					} finally {
						lock.unlock();
					}
				}
			} catch (InterruptedException ex) {
				LOGGER.error("", ex);
			}

		}
	}
}
