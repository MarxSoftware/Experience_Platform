package com.thorstenmarx.webtools.impl.message;

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

import com.thorstenmarx.webtools.api.message.Message;
import com.thorstenmarx.webtools.api.message.MessageStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author marx
 */
public class LocalMessageStream implements MessageStream {

	private final List<Message> dashBoardMessages;
	
	public LocalMessageStream () {
		dashBoardMessages = Collections.synchronizedList(new ArrayList<>());
	}
	
	@Override
	public void provideMessage(final Destination destination, final Message message) {
		if (Destination.DASHBOARD.equals(destination)) {
			dashBoardMessages.add(message);
		}
	}
	
	public List<Message> list (final Destination destination) {
		if (Destination.DASHBOARD.equals(destination)) {
			synchronized (dashBoardMessages) {
				List<Message> messages = Collections.unmodifiableList(new ArrayList<>(dashBoardMessages));
				dashBoardMessages.clear();
				
				return messages;
			}
		}
		
		return Collections.EMPTY_LIST;
	}
}
