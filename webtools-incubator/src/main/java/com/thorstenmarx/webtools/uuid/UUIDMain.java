package com.thorstenmarx.webtools.uuid;

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

import com.fasterxml.uuid.Generators;
import java.util.UUID;

/**
 *
 * @author marx
 */
public class UUIDMain {
	
	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			final UUID uuid1 = Generators.timeBasedGenerator().generate();
			final UUID uuid2 = Generators.timeBasedGenerator().generate();
			
			System.out.println(uuid1.compareTo(uuid2) == -1);
		}
	}
}
