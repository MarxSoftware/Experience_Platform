package com.thorstenmarx.webtools.websockets;

/*-
 * #%L
 * webtools-incubator
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

import io.undertow.Undertow;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.resource;
import static io.undertow.Handlers.websocket;

/**
 *
 * @author marx
 */
public class Server {

	public static void main(final String[] args) {
		Undertow server = Undertow.builder()
				.addHttpListener(8080, "localhost")
				.setHandler(path()
						.addPrefixPath("/myapp", websocket(new WebSocketConnectionCallback() {

							@Override
							public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
								channel.getReceiveSetter().set(new AbstractReceiveListener() {

									@Override
									protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
										WebSockets.sendText(message.getData(), channel, null);
									}
								});
								channel.resumeReceives();
							}
						}))
						.addPrefixPath("/", resource(new ClassPathResourceManager(Server.class.getClassLoader(), Server.class.getPackage())).addWelcomeFiles("index.html")))
				.build();
		server.start();
	}

}
