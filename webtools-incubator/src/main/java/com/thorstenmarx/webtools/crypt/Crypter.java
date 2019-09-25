package com.thorstenmarx.webtools.crypt;

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

import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author marx
 */
public class Crypter {

	public String encryptionKey;

	public static void main(String args[]) throws Exception {
		Crypter t = new Crypter();
		String encrypt = t.encrypt("mypassword");
		System.out.println("decrypted value:" + t.decrypt(t.encryptionKey, encrypt));
	}

	public String encrypt(String value) throws Exception {
		// Get the KeyGenerator
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(256);
		// Generate the secret key specs.
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		String key = Base64.getEncoder().encodeToString(raw);
		this.encryptionKey = key;
		System.out.println("------------------Key------------------");
		System.out.println(key);
		System.out.println("--------------End of Key---------------");
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		String encrypt = Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes()));
		System.out.println("encrypted string:" + encrypt);
		return encrypt;

	}

	public String decrypt(String key, String encrypted) throws Exception {
		Key k = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.DECRYPT_MODE, k);
		byte[] decodedValue = Base64.getDecoder().decode(encrypted);
		byte[] decValue = c.doFinal(decodedValue);
		String decryptedValue = new String(decValue);
		return decryptedValue;
	}
}
