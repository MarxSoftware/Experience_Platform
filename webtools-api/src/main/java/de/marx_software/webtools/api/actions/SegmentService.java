package de.marx_software.webtools.api.actions;

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
import de.marx_software.webtools.api.actions.model.Segment;
import de.marx_software.webtools.api.entities.criteria.Criteria;
import java.util.Collection;
import java.util.EventObject;

/**
 *
 * @author marx
 */
public interface SegmentService {

	void add(Segment segment) throws InvalidSegmentException;

	void addEventListener(final ChangedEventListener listener);

	Collection<Segment> all();

	Segment get(String id);
	
	Criteria<Segment> criteria ();

	void remove(String id);

	void removeEventListener(final ChangedEventListener listener);

	public static interface ChangedEventListener {

		public void changed(ChangedEvent event);
	}

	public static class ChangedEvent extends EventObject {

		private static final long serialVersionUID = -8144811252269149761L;

		public enum Type {
			Update, Delete
		}

		private Type type;
		final Segment segment;

		public ChangedEvent(final Object source, final Type type, final Segment segment) {
			super(source);
			this.type = type;
			this.segment = segment;
		}

		public Segment segment() {
			return segment;
		}

		public Type type() {
			return type;
		}
	}

}
