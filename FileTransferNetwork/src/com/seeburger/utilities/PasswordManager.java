package com.seeburger.utilities;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordManager
{
	public static String generateHashedMasterPass(String salt, int iterations)
			throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		byte[] saltedBytes = salt.getBytes();
		KeySpec spec = new PBEKeySpec(ServerClientCommunicationMessages.MASTER_PASSWORD.toCharArray(), saltedBytes,
				iterations, 128);
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] hash = f.generateSecret(spec).getEncoded();
		System.out.println("Master pass: " + ServerClientCommunicationMessages.MASTER_PASSWORD);
		System.out.println("Salt: " + salt);
		System.out.println("Hashed pass: " + new String(hash));
		return new String(hash);
	}

	public static String generateHashedPass(String userPass, String salt, int iterations)
			throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		byte[] saltedBytes = salt.getBytes();
		KeySpec spec = new PBEKeySpec(userPass.toCharArray(), saltedBytes, iterations, 128);
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] hash = f.generateSecret(spec).getEncoded();
		System.out.println("User pass: " + userPass);
		System.out.println("Salt: " + salt);
		System.out.println("Hashed pass: " + new String(hash));
		return new String(hash);
	}

	public static String generateRandomSalt(int lengthOfSalt)
	{
		String randomSaltString = UUID.randomUUID().toString().replaceAll("-", "");
		randomSaltString = randomSaltString.substring(0, lengthOfSalt);
		return randomSaltString;
	}

	public static int generateRandomNumberOfIterations(int topRange)
	{
		return ThreadLocalRandom.current().nextInt(2, topRange);
	}
}