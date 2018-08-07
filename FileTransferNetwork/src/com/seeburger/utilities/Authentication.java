package com.seeburger.utilities;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Authentication
{
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	private static boolean login;

	public static boolean clientSideAuthentication(DataInputStream dataInputStream, DataOutputStream dataOutputStream)
			throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		try
		{
			int hello = dataInputStream.readInt();
			if (hello == ServerClientCommunicationMessages.HELLO)
			{
				System.out.println(dataInputStream.readUTF());
				int choice = Integer.parseInt(reader.readLine());
				switch (choice)
				{
				case 1:
					dataOutputStream.writeInt(ServerClientCommunicationMessages.LOGIN_PLAIN);
					login = true;
					break;

				case 2:
					dataOutputStream.writeInt(ServerClientCommunicationMessages.REGISTER_PLAIN);
					break;
				}
				String saltAndIterations = dataInputStream.readUTF();
				String saltedMasterPassword = generateHash(saltAndIterations);
				dataOutputStream.writeUTF(saltedMasterPassword);
				int responseToSaltFromServer = dataInputStream.readInt();
				switch (responseToSaltFromServer)
				{
				case ServerClientCommunicationMessages.STATUS_OK:
					if (login)
					{
						// TODO login
						System.out.println("login complete");
					} else
					{
						// TODO register
						System.out.println("registration complete");
					}
					return true;

				case ServerClientCommunicationMessages.STATUS_WRONG_INFO:
					System.out.println("wrong info");
					break;
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;

	}

	public static boolean serverSideAuthentication(DataInputStream dataInputStream, DataOutputStream dataOutputStream)
			throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		try
		{
			dataOutputStream.writeInt(ServerClientCommunicationMessages.HELLO);
			dataOutputStream.writeUTF("1: Login\n2: Register");
			switch (dataInputStream.readInt())
			{
			case ServerClientCommunicationMessages.REGISTER_PLAIN:
				return checkMasterPasswordHash(dataInputStream, dataOutputStream);

			case ServerClientCommunicationMessages.LOGIN_PLAIN:
				return checkMasterPasswordHash(dataInputStream, dataOutputStream);
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	private static boolean checkMasterPasswordHash(DataInputStream dataInputStream, DataOutputStream dataOutputStream)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
	{
		// Generates a random salt string
		String randomSaltString = UUID.randomUUID().toString().replaceAll("-", "");
		randomSaltString.substring(0, 4);
		// Generates a random integer (iterations to hash)
		int randomNum = ThreadLocalRandom.current().nextInt(2, 50);
		dataOutputStream.writeUTF(ServerClientCommunicationMessages.STATUS_OK_SEND_SALT_AND_ITERATIONS + "<"
				+ randomSaltString + "><" + randomNum + ">");
		String hashedMasterPass = dataInputStream.readUTF();

		if (masterPasswordCheck(hashedMasterPass, randomSaltString, randomNum))
		{
			System.out.println("SERVER SIDE HASH CHECK - OK");
			dataOutputStream.writeInt(ServerClientCommunicationMessages.STATUS_OK);
			return true;
		} else
		{
			System.out.println("HASH CHECK ERROR");
			return false;
		}
	}

	private static boolean masterPasswordCheck(String clientHash, String salt, int iterations)
			throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		String hashedString = Password.generateHashedMasterPass(salt, iterations);
		if (clientHash.equals(hashedString))
		{
			return true;
		}
		return false;
	}

	private static String generateHash(String saltAndIterations)
			throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		String[] split = saltAndIterations.split("\\<");
		String salt = split[1].substring(0, split[1].length() - 1);
		int iterations = Integer.parseInt(split[2].substring(0, split[2].length() - 1));
		String saltedMasterPassword = Password.generateHashedMasterPass(salt, iterations);
		return saltedMasterPassword;
	}
}
