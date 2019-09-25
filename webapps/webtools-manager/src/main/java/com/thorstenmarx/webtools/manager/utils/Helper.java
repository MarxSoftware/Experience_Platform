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
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.bouncycastle.jcajce.provider.digest.SHA3;

/**
 *
 * @author marx
 */
public abstract class Helper {

	private static final SecureRandom RANDOM = new SecureRandom();

	public static String hash(String value) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		SHA3.DigestSHA3 md = new SHA3.DigestSHA3(512);
		md.update(value.getBytes("UTF-8"));
		byte[] digest = md.digest();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < digest.length; i++) {
			sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
		}

		

		return sb.toString();

	}

	public static String randomString() {
		return new BigInteger(130, RANDOM).toString(32);
	}
}
