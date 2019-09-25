package com.thorstenmarx.webtools.actions;

/*-
 * #%L
 * webtools-actions
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
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.thorstenmarx.modules.api.ModuleManager;
import com.thorstenmarx.webtools.actions.dsl.EventAction;
import com.thorstenmarx.webtools.actions.dsl.graal.GraalDSL;
import com.thorstenmarx.webtools.actions.dsl.rhino.RhinoDSL;
import com.thorstenmarx.webtools.api.Clone;
import com.thorstenmarx.webtools.api.actions.Action;
import com.thorstenmarx.webtools.api.actions.ActionException;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.actions.SegmentService;
import com.thorstenmarx.webtools.api.actions.model.AdvancedSegment;
import com.thorstenmarx.webtools.api.actions.model.Segment;
import com.thorstenmarx.webtools.api.analytics.TrackedEvent;
import com.thorstenmarx.webtools.api.datalayer.DataLayer;
import com.thorstenmarx.webtools.api.datalayer.Expirable;
import com.thorstenmarx.webtools.api.datalayer.SegmentData;
import com.thorstenmarx.webtools.api.execution.Executor;
import com.thorstenmarx.webtools.base.Configuration;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.script.ScriptException;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public class ActionSystem implements SegmentService.ChangedEventListener {

	private static final Logger log = LoggerFactory.getLogger(ActionSystem.class);

	public static final String ACTIONS_DIR = "actions";

	Set<Segment> segments = Sets.newConcurrentHashSet();

	Cache<String, Set<String>> segmentCache = Caffeine.newBuilder()
			.expireAfterWrite(5, TimeUnit.MINUTES)
			.maximumSize(Integer.MAX_VALUE)
			.build();

	SegmentationWorkerThread segmentationWorker;

	private final AnalyticsDB analyticsDb;
	private final Configuration configuration;

	private final SegmentService segmentService;
	private final ModuleManager moduleManager;

	private final Map<String, EventAction> actions = new ConcurrentHashMap<>();
	private final MBassador mBassador;
	private ActionWorkerThread actionWorker;
	private final DataLayer datalayer;

	private final GraalDSL dslRunner;
	
	private final ScheduledFuture<?> cleanupScheduler;

	public ActionSystem(final AnalyticsDB analyticsDb, final SegmentService segmentService, final Configuration configuration, final ModuleManager moduleManager, final MBassador mBassador, final DataLayer datalayer, final Executor executor) {
		this.moduleManager = moduleManager;
		this.mBassador = mBassador;
		this.analyticsDb = analyticsDb;
		this.configuration = configuration;
		this.segmentService = segmentService;
		this.datalayer = datalayer;
		segments.addAll(segmentService.all());
		segmentService.addEventListener(this);
		this.dslRunner = new GraalDSL(moduleManager, mBassador);

		cleanupScheduler = executor.scheduleFixedDelay(() -> {
			datalayer.each((uid, data) -> {
				if (((Expirable) data).isExpired()) {
					datalayer.remove(uid, SegmentData.KEY);
				}
			}, SegmentData.KEY, SegmentData.class
			);
		}, 1, 10, TimeUnit.MINUTES);
	}

	public void addAction(final String id, final String event, final String dsl) throws ActionException {
		try {
			actions.put(id, dslRunner.buildAction(event, dsl));
		} catch (ScriptException ex) {
			log.error("", ex);
			throw new ActionException(ex.getMessage());
		}
	}

	public void removeAction(final String id) {
		actions.remove(id);
	}

	public List<Action> actions() {
		return Lists.newArrayList(actions.values());
	}

	/**
	 *
	 * @param segment
	 */
	public void addSegment(final Segment segment) {
		this.segments.add(segment);
	}

	/**
	 *
	 * @param segments
	 */
	public void addSegments(final Collection<? extends Segment> segments) {
		this.segments.addAll(segments);
	}

	public void clearSegments() {
		this.segments.clear();
	}

	/**
	 *
	 * @return
	 */
	public Set<Segment> getSegments() {
		return new HashSet<>(Clone.clone(segments));
	}

	public void start() {
		segmentationWorker = new SegmentationWorkerThread(1, analyticsDb, this, moduleManager, this.datalayer);
		segmentationWorker.start();

		actionWorker = new ActionWorkerThread(0, analyticsDb, this, moduleManager);
		actionWorker.start();

		this.mBassador.subscribe(this);
	}

	@Handler
	public void afterTracking(final TrackedEvent event) {

		getSegments().stream().forEach((segment) -> {
			try {
				final String dslContent;
				if (segment instanceof AdvancedSegment) {
					dslContent = ((AdvancedSegment) segment).getDsl();
				} else {
					throw new IllegalStateException("unkown segment definition");
				}

				com.thorstenmarx.webtools.actions.dsl.DSLSegment query = dslRunner.build(dslContent);

				if (query.affected(event.getEvent())) {
					segmentationWorker.forceSegmenteGeneration(segment);
				}

			} catch (ScriptException ex) {
				log.error("error create segement query " + segment, ex);
			}

		});
	}

	public void close() {
		
		cleanupScheduler.cancel(true);
		
		segmentationWorker.shutdown();
//		segmentationWorker.interrupt();

		actionWorker.shutdown();
//		actionWorker.interrupt();

		segmentService.removeEventListener(this);
	}

	@Override
	public void changed(SegmentService.ChangedEvent event) {
		if (SegmentService.ChangedEvent.Type.Update.equals(event.type())) {
			segments.remove(event.segment());
			segments.add(event.segment());
		} else if (SegmentService.ChangedEvent.Type.Delete.equals(event.type())) {
			segments.remove(event.segment());
		}
	}
}
