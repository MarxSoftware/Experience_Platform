/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.concurrent.ExecutionException;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import static org.asynchttpclient.Dsl.*;

/**
 *
 * @author marx
 */
public class Client {

	public static void main(String[] args) throws InterruptedException, ExecutionException {

		AsyncHttpClient client = asyncHttpClient();

		WebSocket websocket = client.prepareGet("ws://localhost:8080/myapp")
				.execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(
						new WebSocketListener() {

					private WebSocket socket;

					@Override
					public void onOpen(WebSocket websocket) {
						this.socket = websocket;
						websocket.sendTextFrame("...");
					}

					@Override
					public void onClose(WebSocket websocket, int i, String string) {
						System.out.println("close");
					}

					@Override
					public void onTextFrame(String payload, boolean finalFragment, int rsv) {
						System.out.println(payload);
						
						socket.sendTextFrame("Hallo");
					}

					@Override
					public void onError(Throwable t) {
					}

				}).build()).get();
	}

}
