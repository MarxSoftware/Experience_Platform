package com.thorstenmarx.webtools.stream;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 *
 * @author marx
 */
public class ImmutableCollectionCollectors {
	public static <t, A extends List<t>> Collector<t, A, List<t>> toImmutableList(Supplier<A> collectionFactory) {
		return Collector.of(collectionFactory, List::add, (left, right) -> {
			left.addAll(right);
			return left;
		}, Collections::unmodifiableList);
	}

	public static <t> Collector<t, List<t>, List<t>> toImmutableList() {
		return toImmutableList(ArrayList::new);
	}

	public static <t, A extends Set<t>> Collector<t, A, Set<t>> toImmutableSet(Supplier<A> collectionFactory) {
		return Collector.of(collectionFactory, Set::add, (left, right) -> {
			left.addAll(right);
			return left;
		}, Collections::unmodifiableSet);
	}

	public static <t> Collector<t, Set<t>, Set<t>> toImmutableSet() {
		return toImmutableSet(HashSet::new);
	}
}
