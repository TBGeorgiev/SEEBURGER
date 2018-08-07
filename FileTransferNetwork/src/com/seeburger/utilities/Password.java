package com.seeburger.utilities;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Password
{
	public static String generateHashedMasterPass(String salt, int iterations)
			throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		byte[] saltedBytes = salt.getBytes();
		KeySpec spec = new PBEKeySpec(ServerClientCommunicationMessages.MASTER_PASSWORD.toCharArray(), saltedBytes,
				iterations, 128);
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] hash = f.generateSecret(spec).getEncoded();

		System.out.println(ServerClientCommunicationMessages.MASTER_PASSWORD);
		System.out.println(new String(saltedBytes));
		System.out.println(new String(hash));

		return new String(hash);
	}
}