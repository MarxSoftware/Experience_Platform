/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.queue;

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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 *
 * @author marx
 */
public class MySample {

	public static void main(String[] args) throws IOException {
		MVStoreQueue queue = new MVStoreQueue(Paths.get("target/mvstore-" + System.currentTimeMillis()), "testqueue");

		long before = System.currentTimeMillis();
		IntStream.range(1, 1000).forEach((i) -> {
			try {
				queue.push((i + " the data").getBytes("UTF-8"));
			} catch (IOException ex) {
				Logger.getLogger(MySample.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
		long after = System.currentTimeMillis();
		System.out.println("mvstorequeue took: " + (after - before) + "ms");
		
		while (!queue.isEmpty()) {
			String content = new String(queue.poll());
			System.out.println(content + " / " + queue.size());
		}
		
		queue.close();

	}
}
