package com.thorstenmarx.webtools.manager.utils;

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
import java.util.List;

/**
 *
 * @author marx
 */
public class ListUtils {

	/**
	 * Returns a range of a list based on traditional offset/limit criteria.
	 *
	 * <p>
	 * Example:
	 * <pre>
	 *   ListUtil.subList(Arrays.asList(1, 2, 3, 4, 5), 3, 5) => [4,5]
	 * </pre></p>
	 *
	 * <p>
	 * In case the offset is higher than the list length the returned sublist is empty (no
	 * exception). In case the list has fewer items than limit (with optional offset applied) then
	 * the remaining items are returned (if any).</p>
	 *
	 * <p>
	 * Impl notes: returns a {@link List#subList} in all cases to have a consistent return
	 * value.</p>
	 *
	 * @param list The input list.
	 * @param offset 0 for now offset, >=1 for an offset.
	 * @param limit -1 for no limit, >=0 for how many items to return at most, 0 is allowed.
	 */
	public static <T> List<T> subList(List<T> list, int offset, int limit) {
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must be >=0 but was " + offset + "!");
		}
		if (limit < -1) {
			throw new IllegalArgumentException("Limit must be >=-1 but was " + limit + "!");
		}

		if (offset > 0) {
			if (offset >= list.size()) {
				return list.subList(0, 0); //return empty.
			}
			if (limit > -1) {
				//apply offset and limit
				return list.subList(offset, Math.min(offset + limit, list.size()));
			} else {
				//apply just offset
				return list.subList(offset, list.size());
			}
		} else if (limit > -1) {
			//apply just limit
			return list.subList(0, Math.min(limit, list.size()));
		} else {
			return list.subList(0, list.size());
		}
	}
}
